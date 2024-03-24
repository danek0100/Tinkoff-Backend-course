package edu.java.dto;

import java.time.OffsetDateTime;

public interface Comment {

    String getCommentDescription();

    OffsetDateTime getCreatedAt();

    OffsetDateTime getUpdatedAt();
}
