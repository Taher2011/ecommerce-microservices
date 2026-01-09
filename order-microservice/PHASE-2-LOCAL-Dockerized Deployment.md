Run Order Microservice inside Docker container

PostgreSQL also containerized

Redis also containerized

Use same Spring profiles

Config injected via Docker environment variables

Use Docker image tags for env separation

====================================================================================================================================================================================================

Key points:---

.env.dev is loaded via --env-file

Postgres init SQL in db/init/ handles schema creation

AWS credentials mounted as volume

====================================================================================================================================================================================================

How to run locally:---

Clean old containers and volumes

       docker compose --env-file .env.dev -f docker-compose-local.yml down -v


Start all services

       docker compose --env-file .env.dev -f docker-compose-local.yml up -d --build


Check container status

       docker ps


Expected:

postgres-dev   Up
redis-dev      Up
order-dev      Up


Verify DB and schema

       docker exec -it postgres-dev psql -U postgres -d ecommerce_dev_db -c "\dn"
# Should show: order_dev_db


Verify AWS env inside container

docker exec -it order-dev env | grep AWS

====================================================================================================================================================================================================

Build Image (ONCE) -->Same image content, different tags
docker build -t order-service:dev .
docker tag order-service:1.0 order-service:dev
docker tag order-service:1.0 order-service:stage
====================================================================================================================================================================================================

Run Containers -->
 Dev:
 
docker compose --env-file .env.dev -f docker-compose-local.yml down -v

docker compose --env-file .env.dev -f docker-compose-local.yml up -d --build

====================================================================================================================================================================================================
     
Dump data from local DB -->
 pg_dump -U postgres -h localhost ecommerce_dev_db > backup.sql     
 pg_dump -U postgres -h localhost ecommerce_stage_db > backup.sql
====================================================================================================================================================================================================
 
Dump data into docker  DB container "postgre-dev" and "postgre-stage" container DB -->
 docker exec -i postgres-dev psql -U postgres ecommerce_dev_db < backup.sql
====================================================================================================================================================================================================
 
Stop/Cleanup -->
docker compose --project-name order-dev down -v
docker compose --project-name order-stage down -v 
====================================================================================================================================================================================================

Config Flow (Mental Model)
.env file
↓
Docker Compose
↓
Container Environment Variables
↓
Spring Boot application.yml
====================================================================================================================================================================================================
