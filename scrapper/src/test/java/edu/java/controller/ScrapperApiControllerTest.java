package edu.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.RemoveLinkRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ScrapperApiController.class)
public class ScrapperApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerChat_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(post("/tg-chat/{id}", 1L))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteChat_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/tg-chat/{id}", 1L))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getAllLinks_ShouldReturnListLinksResponse() throws Exception {
        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.links").isEmpty())
            .andExpect(jsonPath("$.size").value(0));
    }


    @Test
    public void addLink_ShouldReturnLinkResponse() throws Exception {
        AddLinkRequest addLinkRequest = new AddLinkRequest();
        addLinkRequest.setLink("http://example.com");

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.url").value("http://example.com"));
    }


    @Test
    public void removeLink_ShouldReturnLinkResponse() throws Exception {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest();
        removeLinkRequest.setLink("http://example.com");

        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.url").value("http://example.com"));
    }
}
