package edu.java.dto;

import java.time.OffsetDateTime;

public interface Comment {

    String getBody();

    OffsetDateTime getCreatedAt();
}
