package edu.java.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkDTO {
    private Long linkId;
    private String url;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastCheckTime;
    private LocalDateTime lastUpdateTime;
}
