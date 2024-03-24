package edu.java.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ChatDTO {
    private Long chatId;
    private LocalDateTime createdAt;
}
