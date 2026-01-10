# 🚀 Order-Service Deployment on AWS EC2 (Docker + RDS)

This README is a **step-by-step memory-safe guide** to run **order-service** on **AWS EC2** using **Docker Compose**, **Redis**, and **AWS RDS (PostgreSQL)**.


---

## 🧠 Architecture (High Level)

* EC2 (Amazon Linux)
* Docker + Docker Compose (v2 plugin)
* order-service (Spring Boot)
* Redis (Docker container)
* PostgreSQL (AWS RDS – NO container)

```
EC2
 ├── order-service (Docker)
 ├── redis (Docker)
 └── connects to → AWS RDS (Postgres)
```

---

## ✅ Prerequisites (One-time setup)

### 1️⃣ EC2 instance

* Amazon Linux
* In same **VPC** as RDS
* Security Group:

  * SSH 22 → your IP
  * App port (8081) → your IP
  * RDS port 5432 → **EC2 security group**

---

### 2️⃣ Install Docker on EC2

```bash
sudo yum update -y
sudo yum install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user
exit
```

➡️ Login again after exit

Verify:

```bash
docker --version
```

---

### 3️⃣ Install Docker Compose (v2 plugin – recommended)

```bash
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
-o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
```

Verify:

```bash
docker compose version
```

---

## 🗄️ AWS RDS (PostgreSQL) Setup

### 1️⃣ Create RDS

* Engine: PostgreSQL
* Public access: ❌ No
* VPC: Same as EC2
* Security group: allow **5432 from EC2 SG**

### 2️⃣ Create Database & Schema

Login from EC2:

```bash
sudo yum install postgresql -y

psql -h <RDS_ENDPOINT> -U postgres -d postgres
```

Inside psql:

```sql
CREATE DATABASE ecommerce_dev_db;
\c ecommerce_dev_db

CREATE SCHEMA order_dev_db;
```

---

## 📁 Project Structure on EC2

```
/home/ec2-user/ecommerce-ms/order-microservice
 ├── docker-compose-ec2.yml
 ├── .env.ec2
 ├── Dockerfile
 ├── logs/
 └── target/order-service.jar
```

---

## 🧾 docker-compose-ec2.yml (FINAL)

```yaml
version: "3.8"

services:
  redis:
    image: redis:7
    container_name: redis-${ENV}
    ports:
      - "6379:6379"

  order-service:
    image: order-service:${IMAGE_TAG}
    container_name: order-${ENV}
    ports:
      - "${APP_PORT}:${APP_PORT}"
    env_file:
      - .env.ec2
    volumes:
      - ./logs:${LOGGING_FILE_PATH}
    depends_on:
      - redis
```

---

## 🧾 .env.ec2 (IMPORTANT)

```env
# ENV
ENV=dev
SPRING_PROFILES_ACTIVE=dev

# APP
APP_PORT=8081
IMAGE_TAG=dev
LOGGING_FILE_PATH=/app/logs

# DATABASE (AWS RDS)
SPRING_DATASOURCE_URL=jdbc:postgresql://<RDS_ENDPOINT>:5432/ecommerce_dev_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA=order_dev_db

# REDIS
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379

# AWS
AWS_REGION=us-east-1
AWS_S3_BUCKET_NAME=ecommerce-ms-bucket
```

---

## 🏗️ Build Order-Service Image

Run from project root:

```bash
docker build -t order-service:dev .
```

---

## 🚀 Run Containers (Main Commands)

### 1️⃣ Stop old containers (clean state)

```bash
docker compose --env-file .env.ec2 -f docker-compose-ec2.yml down --remove-orphans
```

### 2️⃣ Start services

```bash
docker compose --env-file .env.ec2 -f docker-compose-ec2.yml up -d
```

---

## 👀 Verification

### Containers running?

```bash
docker ps
```

Expected:

```
order-dev
redis-dev
```

### Application logs

```bash
docker logs -f order-dev
```

Expected:

```
HikariPool-1 - Start completed
Tomcat started on port(s): 8081
```

---

## 🧪 Run DB Queries (RDS)

```bash
psql -h <RDS_ENDPOINT> -U postgres -d ecommerce_dev_db
```

Example:

```sql
SELECT * FROM order_dev_db.orders LIMIT 10;
```

---

## ❌ Common Mistakes (Avoid These)

* ❌ Trying to docker exec into RDS (RDS has no container)
* ❌ Using localhost/postgres as DB host
* ❌ Forgetting `--env-file .env.ec2`
* ❌ Leaving old postgres container running

---

## 🟢 Quick Checklist (Before Running)

* [ ] Docker running
* [ ] Docker Compose installed
* [ ] RDS reachable from EC2
* [ ] `.env.ec2` values correct
* [ ] order-service image built

---

## 🏁 Final Notes

* No DB container in EC2 (RDS only)
* All configs via `.env` (no hardcoding)
* Same setup works for **dev / prod** by changing ENV

---

✅ **You are now running a real production-style backend on AWS** 🚀

======================================================================================================================================================================================
CASE 1: Java / Properties change hua

(.java, application.yml, logic change)

Steps (ALWAYS this order)
./mvnw clean package -DskipTests

export DOCKER_BUILDKIT=0
export COMPOSE_DOCKER_CLI_BUILD=0   # only if EC2 build error aaye

docker compose --env-file .env.ec2 -f docker-compose-ec2.yml up -d --build


📌 --build = image fresh build
📌 export = sirf tab jab BuildKit error ho

❌ CASE 2: Sirf docker-compose change

(port, env, volume, container name)

docker compose --env-file .env.ec2 -f docker-compose-ec2.yml up -d


❌ JAR build nahi
❌ Image rebuild nahi
❌ export nahi

❌ CASE 3: Sirf restart chahiye

(app stuck, config same)

docker compose restart order-service


Fast ✅ Safe ✅

❌ CASE 4: DB / Redis env change

(RDS endpoint, password)

docker compose down
docker compose --env-file .env.ec2 -f docker-compose-ec2.yml up -d


❌ Image rebuild nahi
❌ export nahi

⚠️ EXPORT commands kab use kare?
export DOCKER_BUILDKIT=0
export COMPOSE_DOCKER_CLI_BUILD=0


✅ Use when:

EC2 me exec format error

buildx issue

❌ Don’t use when:

Docker Desktop (Windows/Mac)

CI/CD pipeline

Buildx properly installed

📌 Temporary workaround hai, permanent solution nahi

🧠 One-line memory trick

Code change → build
Config change → up
Issue → restart
