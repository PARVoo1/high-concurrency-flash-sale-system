# ⚡ High-Concurrency Flash Sale System

> A backend engineering project focused on solving the **"Million User Problem."**

The goal of this project is to build a highly scalable microservice that can handle a massive surge of concurrent requests (e.g., a 10-second flash sale) without overselling inventory, crashing the database, or suffering from race conditions.

---

## 🏗️ The Architecture

This is not a simple CRUD application. It simulates an enterprise-level environment where every millisecond matters, utilizing a stateless, event-driven architecture.

* **☕ Spring Boot 3 & Java 21:** The core application engine.
* **🔐 Stateless Security (JWT):** A custom Spring Security filter chain completely eliminates database bottlenecks during authentication. Cryptographically signed JWTs verify user identity in-memory before they ever touch the business logic.
* **🟥 Redis + Lua Scripts:** The heavy lifting. Lua scripts guarantee that rate-limiting (Token Bucket) and inventory checks are strictly atomic.
* **📨 Apache Kafka:** Instead of making the user wait for a slow database write, the API acknowledges the order and pushes it to a Kafka topic. A separate consumer asynchronously handles the DB persistence.
* **🐘 PostgreSQL:** The final, persistent source of truth for orders, inventory, and user credentials.
* **📊 Prometheus & Grafana:** Real-time observability. Watch CPU, JVM Heap, and request throughput react to traffic spikes.
* **🐳 Docker:** The entire stack (6 containers) is orchestrated via Docker Compose with a multi-stage build.

---

## 🛡️ Solving the "Bot Attack" (Rate Limiting)

One of the main challenges was preventing a single user or bot from spamming the system and draining inventory unfairly. I implemented a **Token Bucket algorithm** directly in Redis using Lua.

- Calculates tokens based on the time elapsed between requests.
- Rejects excess traffic at the Redis layer, returning a `429 Too Many Requests` status *before* the request can burden the Spring Boot server or the PostgreSQL database.

---

## 🚀 Performance Results

The system was load-tested using `oha` with **500 concurrent workers** and **10,000 total requests** hitting the secure checkout endpoint.

**The Result:**
- ⚡ **Requests/sec:** ~600+
- ✅ **Success Rate:** 100% (No server crashes or memory leaks)
- 📦 **Inventory Integrity:** Exactly 10 items sold, **0 oversold**.
- ⛔ **Rate Limiting:** ~9,300 requests correctly blocked and dropped.

---

## 💻 How to Run It

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/yourusername/high-concurrency-flash-sale-system.git](https://github.com/yourusername/high-concurrency-flash-sale-system.git)
   cd high-concurrency-flash-sale-system
2. **Ensure Docker is running.**

3. **Configure Environment Variables:**
   Create a `.env` file in the root directory and add your Base64 JWT secret:
   ```env
   JWT_KEY=your_base64_secret_key_here
4. **Spin up the cluster:**
   ```bash
   docker compose up --build -d
   
5. **View Metrics:**
   Access Grafana at [http://localhost:3000 (Login: admin / admin) and import dashboard ID 19004.]

## 🔮 Future Improvements
[ ] Distributed Caching Locks: Implementing a Distributed Lock (Redlock) for multi-node consistency.

[ ] Kafka DLQ: Adding a dead-letter queue (DLQ) in Kafka for failed order processing.
