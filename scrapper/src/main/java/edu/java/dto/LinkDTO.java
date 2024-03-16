package edu.java.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LinkDTO {
    private Long linkId;
    private String url;
    private String description;
    private LocalDateTime createdAt;
    private String createdBy;
}
