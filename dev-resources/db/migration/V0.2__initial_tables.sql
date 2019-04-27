-- sessions

CREATE TABLE sessions (
  session_id VARCHAR(36) NOT NULL PRIMARY KEY,
  idle_timeout BIGINT,
  absolute_timeout BIGINT,
  value BYTEA
);

CREATE INDEX sesssions_idle_timeout
    ON sessions (session_id);

CREATE INDEX sesssions_absolute_timeout
    ON sessions (absolute_timeout);

-- users

CREATE TABLE users (
    uuid uuid NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    email text NOT NULL,
    password text NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE users
    ADD CONSTRAINT users_email_unique UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT users_email_lowercase_check
    CHECK (email = lower(email));

CREATE TRIGGER users_update_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE PROCEDURE trigger_set_timestamp(updated_at);

CREATE INDEX users_updated_at
    ON users (updated_at);

CREATE INDEX users_created_at
    ON users (created_at);

-- tokens

CREATE TABLE tokens (
    uuid uuid NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    nosql jsonb NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL
);

CREATE INDEX tokens_nosql
    ON tokens (nosql);

CREATE INDEX tokens_created_at
    ON tokens (created_at);

-- emails queue

CREATE TABLE emails_queue (
    id SERIAL NOT NULL PRIMARY KEY,
    account text NOT NULL,
    priority smallint NOT NULL DEFAULT 100,
    nosql jsonb NOT NULL,
    send_after timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- number of tries

CREATE INDEX emails_queue_send_after_account
    ON emails_queue (send_after, account);

CREATE INDEX emails_queue_send_after_account_priority
    ON emails_queue (send_after, account, priority);

CREATE TRIGGER email_queue_add
    AFTER INSERT ON emails_queue
    FOR EACH ROW
    EXECUTE PROCEDURE notify_change('email_queue', id)

-- NOTIFY
-- https://stackoverflow.com/questions/12002662/how-can-i-send-email-from-postgresql-trigger
-- https://www.postgresql.org/docs/9.4/sql-notify.html