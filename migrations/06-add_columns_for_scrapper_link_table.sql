--liquibase formatted sql

--changeset danek0100:06
--preconditions onFail:MARK_RAN
ALTER TABLE link
    DROP COLUMN created_by,
    ADD COLUMN last_check_time TIMESTAMP WITH TIME ZONE,
    ADD COLUMN last_update_time TIMESTAMP WITH TIME ZONE;
