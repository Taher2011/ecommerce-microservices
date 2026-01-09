-- Connect to default DB created by postgres
\c ecommerce_dev_db;

-- Create schema safely
CREATE SCHEMA IF NOT EXISTS order_dev_db;

-- Permissions
GRANT ALL PRIVILEGES ON SCHEMA order_dev_db TO postgres;
