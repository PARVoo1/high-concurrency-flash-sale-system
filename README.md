⚡ High-Concurrency Flash Sale System
A distributed backend event-driven architecture designed to handle the "Thundering Herd" problem.

Standard CRUD applications crash or oversell when 10,000 people click "Buy" at the exact same second. I built this system to demonstrate how to manage massive, sudden spikes in traffic without relying on slow database locks, while actively preventing scalper bots and duplicate charges.

🏗️ System Architecture
The Bouncer (Rate Limiting): Requests hit Redis first. A Fixed-Window algorithm blocks IP/User spam.

The Cache (Inventory): Valid requests trigger an atomic Redis Lua Script to check and decrement stock in memory (sub-millisecond latency).

The Queue (Async Processing): If stock is secured, an order ticket with a unique UUID is thrown into an Apache Kafka topic, and the user immediately gets a "Success" response.

The Worker (Database): A decoupled Spring Boot consumer listens to Kafka, checks for duplicate tickets (Idempotency), and safely writes the final receipt to PostgreSQL at its own pace.

🧠 Engineering Challenges Solved
1. Defeating Scalper Bots (Denial of Service)
The Problem: Bots can fire thousands of requests per second, stealing all inventory before a human can click.

The Solution: Implemented a Fixed-Window Rate Limiter in Redis. The system strictly enforces a limit (e.g., 5 requests per second per user). Excess requests are instantly rejected with an HTTP 429: Too Many Requests status before they even touch the core logic.

2. Preventing Race Conditions & Overselling
The Problem: If 100 threads read stock = 1 simultaneously, they will all subtract 1, and the database will record -99 items.

The Solution: Offloaded inventory management to Redis using Lua Scripting. Because Redis is single-threaded and executes Lua scripts atomically, it forces all simultaneous requests into a perfect single-file line. No database row-locking required.

3. Database Survival (Load Leveling)
The Problem: PostgreSQL will choke and crash if it tries to open 10,000 simultaneous connections to write orders.

The Solution: Async processing via Apache Kafka. The API responds to the user instantly after Redis confirms the stock. The actual database writes are queued in Kafka and processed sequentially, keeping the database CPU utilization stable regardless of frontend traffic.

4. Preventing Double-Charges (Idempotency)
The Problem: Kafka guarantees "at-least-once" delivery, meaning a network blip could cause it to send the same order ticket to the database twice.

The Solution: Made the Kafka consumer strictly Idempotent. The API generates a unique ticketId (UUID) upon purchase. The consumer queries PostgreSQL to check if that ticketId already exists before writing. If it does, the duplicate Kafka message is safely ignored.

🛠️ Tech Stack
Java 21 & Spring Boot 3 (REST API, Core Logic)

Redis (Rate Limiting, Atomic Inventory State)

Apache Kafka (Message Broker, Decoupling)

PostgreSQL (Persistent Data, ACID Compliance)

Docker & Docker Compose (Containerization & Orchestration)

🚀 Quick Start
You don't need to install Postgres, Redis, or Kafka locally. Everything is containerized.

1. Boot the infrastructure & application:

Bash
docker compose up -d --build
Note: On startup, the DataSeeder automatically injects "PROD-001" with 10 units of stock into both PostgreSQL and Redis.

2. Watch the logs:

Bash
docker logs -f flash-sale-app
🧪 Testing the API
Single Purchase:
Send a POST request to buy the item. The system identifies users via HTTP headers, simulating an API Gateway.

Bash
curl -X POST http://localhost:8080/inventory/PROD-001 \
     -H "X-User-Id: USER_999"
Simulate a Bot Attack (PowerShell):
Fire 100 concurrent requests to watch the Redis Rate Limiter block spam and ensure exactly 1 item is sold.

PowerShell
$jobs = @()
1..100 | ForEach-Object {
    $jobs += Start-Job -ScriptBlock { 
        Invoke-RestMethod -Uri 'http://localhost:8080/inventory/PROD-001' -Method Post -Headers @{ "X-User-Id" = "USER_999" }
    }
}
Wait-Job -Job $jobs
Check your Docker logs to see the 🚨 SPAM BLOCKED warnings and the 📉 Inventory decremented successes!

🛑 Teardown
To shut down the microservices and wipe the database volumes:

Bash
docker compose down -v
