-- Drop table if exists.
DROP TABLE IF EXISTS customers;


-- Create customer table
CREATE TABLE customers (
  id CHAR(36) PRIMARY KEY,  -- UUID stored as a 36-character string
  name VARCHAR(100),
  email VARCHAR(100),
  annual_spend DOUBLE,
  last_purchase_date DATE
);

-- Inserting data

-- Insert sample data
INSERT INTO customers (id, name, email, annual_spend, last_purchase_date)
VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Priya Sharma', 'priya@example.com', 950.50, '2023-04-12'),
('123e4567-e89b-12d3-a456-426614174000', 'Raj Patel', 'raj@example.com', 1500.00, '2024-11-10'),
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Anjali Mehta', 'anjali@example.com', 10500.75, '2025-03-01'),
('e0a953fc-d83d-4fc0-a55e-d4d40dd7fc4e', 'Vikram Rao', 'vikram@example.com', 8000.00, '2024-07-15');
