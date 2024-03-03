package edu.java.bot.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ListLinksResponse {
    private List<LinkResponse> links;
    private Integer size;
}
