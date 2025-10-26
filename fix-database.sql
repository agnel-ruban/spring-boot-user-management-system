-- Drop the existing notifications table to remove old columns
DROP TABLE IF EXISTS notifications;

-- The table will be recreated automatically by Hibernate with the correct schema
-- when the application starts with spring.jpa.hibernate.ddl-auto=update
