CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS $$
    DECLARE
        _col text;
    BEGIN
        _col := TG_ARGV[0];
        NEW._col = NOW();
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION notify_change()
    RETURNS TRIGGER AS $$
    DECLARE
        _ch text;
        _col text;
    BEGIN
        _ch := TG_ARGV[0];
        _col := TG_ARGV[1];

      PERFORM pg_notify(
        _ch,
        NEW._col
      );

      RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;


--        json_build_object(
--          'operation', TG_OP,
--          'record', row_to_json(NEW)
--        )::text