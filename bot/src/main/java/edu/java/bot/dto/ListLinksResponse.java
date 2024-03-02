package edu.java.bot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ListLinksResponse {
    private List<LinkResponse> links;
    private Integer size;
}
