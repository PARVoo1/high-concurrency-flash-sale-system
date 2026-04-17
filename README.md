High-Concurrency Flash Sale System
This is a backend engineering project focused on the "Million User Problem." The goal was to build a highly scalable microservice that can handle a massive surge of concurrent requests (like a 10-second flash sale) without overselling inventory, crashing the database, or suffering from race conditions.

The Architecture
I didn't want a simple CRUD app. I built this to simulate an enterprise-level environment where every millisecond matters, utilizing a stateless, event-driven architecture.

Spring Boot 3 & Java 21: The core engine.

Stateless Security (JWT): A custom Spring Security filter chain completely eliminates database bottlenecks during authentication. Cryptographically signed JWTs verify user identity in-memory before they ever touch the business logic.

Redis + Lua Scripts: This is where the heavy lifting happens. I used Lua scripts to guarantee that rate-limiting (Token Bucket) and inventory checks are strictly atomic.

Apache Kafka: Instead of making the user wait for a slow database write, the API acknowledges the order and pushes it to a Kafka topic. A separate consumer asynchronously handles the actual DB persistence.

PostgreSQL: The final, persistent source of truth for orders, inventory, and user credentials.

Prometheus & Grafana: Real-time observability. You can watch the CPU, JVM Heap, and request throughput react to traffic spikes.

Docker: The entire stack (6 containers) is orchestrated via Docker Compose with a multi-stage build for seamless deployment.

Solving the "Bot Attack" (Rate Limiting)
One of the main challenges was preventing a single user or bot from spamming the system and draining inventory unfairly. I implemented a Token Bucket algorithm directly in Redis using Lua.

It calculates tokens based on the time elapsed between requests.

It rejects excess traffic at the Redis layer, returning a 429 Too Many Requests status before the request can burden the Spring Boot server or the PostgreSQL database.

Performance Results
I load-tested the system using oha with 500 concurrent workers and 10,000 total requests hitting the secure endpoint.

The Result:

Requests/sec: ~600+

Success Rate: 100% (No server crashes or memory leaks)

Inventory Integrity: Exactly 10 items sold, 0 oversold.

Rate Limiting: ~9,300 requests correctly blocked and dropped.

How to Run It
Clone the repo.

Ensure Docker is running.

Add your base64 JWT secret to a .env file in the root directory: JWT_KEY=your_secret_here

Run: docker compose up --build -d

Access Grafana at localhost:3000 (admin/admin) and import dashboard 19004.

Future Improvements
Implementing a Distributed Lock (Redlock) for multi-node consistency.

Adding a dead-letter queue (DLQ) in Kafka for failed order processing.
