Run Order Microservice inside Docker container

PostgreSQL also containerized

Use same Spring profiles

Config injected via Docker environment variables

Use Docker image tags for env separation

====================================================================================================================================================================================================

Build Image (ONCE) -->Same image content, different tags
docker build -t order-service:1.0 .
docker tag order-service:1.0 order-service:dev
docker tag order-service:1.0 order-service:stage
====================================================================================================================================================================================================

Run Containers -->
 Dev:
     docker-compose --project-name order-service-dev --env-file .env.dev up -d
 Stage:
     docker-compose --project-name order-service-stage --env-file .env.stage up -d
====================================================================================================================================================================================================
     
Dump data from local DB -->
 pg_dump -U postgres -h localhost order_dev_db > backup.sql     
 pg_dump -U postgres -h localhost order_stage_db > backup.sql
====================================================================================================================================================================================================
 
Dump data into docker  DB container "postgre-dev" and "postgre-stage" container DB -->
 docker exec -i postgres-dev psql -U postgres order_dev_db < backup.sql
 docker exec -i postgres-stage psql -U postgres order_stage_db < backup.sql
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
