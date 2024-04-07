package edu.java.bot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LinkUpdateRequest {

    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotBlank(message = "URL cannot be blank")
    private String url;
    private String description;
    private List<Long> tgChatIds;
}
