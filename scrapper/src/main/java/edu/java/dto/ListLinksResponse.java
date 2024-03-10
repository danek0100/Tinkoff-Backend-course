package edu.java.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ListLinksResponse {
    private List<LinkResponse> links;
    private Integer size;
}
