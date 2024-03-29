BEGIN;
-- CREATE STATEMENTS FOR APP TABLES
DROP TABLE IF EXISTS WORKOUT_USER, WORKOUT_PROFILE, APP_USER, ROLES;

CREATE TABLE APP_USER (
                          id SERIAL PRIMARY KEY,
                          user_name VARCHAR(32) NOT NULL UNIQUE,
                          password VARCHAR(32) NOT NULL,
                          role SERIAL REFERENCES ROLES(id),
                          salt VARCHAR(255) NOT NULL
);

COMMIT;

CREATE TABLE ROLES (
    id SERIAL PRIMARY KEY,
    ROLE_NAME VARCHAR(15) NOT NULL UNIQUE,
)

CREATE TABLE USER_PROFILE (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    preferred_name VARCHAR(32),
    goal VARCHAR(50) NOT NULL,
    user_id INT UNIQUE,
    profile_photo VARCHAR(255)
)