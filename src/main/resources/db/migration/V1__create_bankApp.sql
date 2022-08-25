-- This migration file creates tables for each model defined in our application the rows of which match the model's parameters

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS Customer
(
    customer_id        integer PRIMARY KEY ,
    name text NOT NULL,
    email      text NOT NULL
);


CREATE TABLE IF NOT EXISTS bank_account
(
    account_id         text PRIMARY KEY,
    balance integer NOT NULL,
    created_at timestamp NOT NULL,
    account_status text NOT NULL,
    customer_id   integer NOT NULL REFERENCES Customer ( customer_id) ON DELETE CASCADE,
    over_draft real ,
    interest_rate real ,
    account_type text NOT NULL
);

CREATE TABLE IF NOT EXISTS account_operation
(
    id        integer PRIMARY KEY ,
    operation_date timestamp NOT NULL,
    amount integer NOT NULL,
    operation_type   text NOT NULL,
    bank_account_id  text   NOT NULL REFERENCES bank_account (account_id) ON DELETE CASCADE,
    description   text NOT NULL
    );
