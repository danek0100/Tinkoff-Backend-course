--liquibase formatted sql

--changeset danek0100:01
--preconditions onFail:MARK_RAN
CREATE TABLE chat
(
    chat_id         BIGINT NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by      TEXT NOT NULL,
    PRIMARY KEY (chat_id)
);
