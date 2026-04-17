# High-Concurrency Flash Sale System

This is a backend engineering project focused on the "Million User Problem." The goal was to build a system that can handle a massive surge of concurrent requests (like a 10-second flash sale) without overselling inventory, crashing the database, or suffering from race conditions.

## The Architecture
I didn't want a simple CRUD app. I wanted to simulate an enterprise-level environment where every millisecond matters.

[Image of High-Concurrency Microservice Architecture]

* **Spring Boot 3 & Java 21:** The core engine.
* **Redis + Lua Scripts:** This is where the heavy lifting happens. I used Lua scripts to make sure the rate-limiting (Token Bucket) and inventory checks are atomic. If it's not atomic, you get race conditions.
* **Apache Kafka:** Instead of making the user wait for a slow database write, the API acknowledges the order and pushes it to a Kafka topic. A separate consumer then handles the actual DB persistence.
* **PostgreSQL:** The final source of truth for orders.
* **Prometheus & Grafana:** Real-time observability. You can actually see the CPU and JVM Heap react to traffic spikes.
* **Docker:** The entire stack (6 containers) is orchestrated via Docker Compose with a multi-stage build.

## Solving the "Bot Attack"
One of the main challenges was preventing a single user or bot from spamming the system. I implemented a **Token Bucket algorithm** directly in Redis using Lua. 
* It calculates tokens based on the time elapsed between requests.
* It rejects excess traffic at the Redis layer, so it never even touches the Spring Boot business logic or the database.

## Performance Results
I load-tested this using `oha` with 500 concurrent workers and 10,000 total requests.

**The Result:**
* **Requests/sec:** ~600+
* **Success Rate:** 100% (No crashes)
* **Inventory Integrity:** Exactly 10 items sold, 0 oversold.
* **Rate Limiting:** ~9,300 requests correctly blocked with `429 Too Many Requests`.

## How to Run It
1. Clone the repo.
2. Ensure Docker is running.
3. Run: `docker-compose up --build`
4. Access Grafana at `localhost:3000` (admin/admin) and import dashboard `19004`.

## Future Improvements
* Adding JWT-based authentication for the checkout process.
* Implementing a Distributed Lock (Redlock) for multi-node consistency.
