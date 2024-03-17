--liquibase formatted sql

--changeset danek0100:04
--preconditions onFail:MARK_RAN
ALTER TABLE chat DROP COLUMN created_by;
