package edu.java.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ChatLinkDTO {
    private Long chatId;
    private Long linkId;
    private LocalDateTime sharedAt;
}
