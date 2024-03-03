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
            .andExpect(status().isOk())
            .andExpect(content().string("Чат зарегистрирован"));
    }

    @Test
    public void deleteChat_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/tg-chat/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().string("Чат успешно удалён"));
    }

    @Test
    public void getAllLinks_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(content().string("Ссылки успешно получены"));
    }

    @Test
    public void addLink_ShouldReturnSuccessMessage() throws Exception {
        AddLinkRequest addLinkRequest = new AddLinkRequest("link");

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("Ссылка успешно добавлена"));
    }

    @Test
    public void removeLink_ShouldReturnSuccessMessage() throws Exception {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("link");

        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("Ссылка успешно убрана"));
    }

}
