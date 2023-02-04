-- *************************************************************************************************
-- This script creates all of the database objects (tables, sequences, etc) for the database
-- *************************************************************************************************

BEGIN;

-- CREATE statements go here
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(32) NOT NULL,
    role VARCHAR(32),
    salt VARCHAR(255) NOT NULL
);

COMMIT;