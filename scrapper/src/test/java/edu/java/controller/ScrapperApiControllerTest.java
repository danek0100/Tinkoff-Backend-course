package edu.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bucket.BucketManager;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.ChatLinkDTO;
import edu.java.dto.LinkDTO;
import edu.java.dto.RemoveLinkRequest;
import edu.java.service.ChatLinkService;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ScrapperApiController.class)
public class ScrapperApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private LinkService linkService;

    @MockBean
    private ChatLinkService chatLinkService;

    @MockBean
    private BucketManager bucketManager;

    @Mock
    private Bucket mockBucket;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long testChatId = 1L;
    private final Long testLinkId = 1L;
    private final String testUrl = "http://example.com";
    private final String testDescription = "Test Description";
    private LinkDTO testLink;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testLink = new LinkDTO(testLinkId, testUrl, testDescription, LocalDateTime.now(), null, null);

        ConsumptionProbe probe = Mockito.mock(ConsumptionProbe.class);
        when(probe.isConsumed()).thenReturn(true);
        when(mockBucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);

        when(bucketManager.resolveBucket(anyString())).thenReturn(mockBucket);
    }

    @Test
    public void registerChat_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(post("/tg-chat/{id}", testChatId))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteChat_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/tg-chat/{id}", testChatId))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getAllLinks_ShouldReturnListLinksResponse() throws Exception {
        when(chatLinkService.findAllLinksForChat(anyLong())).thenReturn(Arrays.asList(new ChatLinkDTO(testChatId, testLinkId, LocalDateTime.now())));
        when(linkService.findById(anyLong())).thenReturn(testLink);

        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", testChatId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.links[0].url").value(testUrl))
            .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    public void addLink_ShouldReturnLinkResponse() throws Exception {
        when(linkService.add(anyString(), anyString())).thenReturn(testLink);
        when(chatLinkService.existsChatsForLink(anyLong())).thenReturn(false);

        AddLinkRequest addLinkRequest = new AddLinkRequest();
        addLinkRequest.setLink(testUrl);
        addLinkRequest.setDescription(testDescription);

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", testChatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testLinkId))
            .andExpect(jsonPath("$.url").value(testUrl));
    }

    @Test
    public void removeLink_ShouldReturnLinkResponse() throws Exception {
        when(linkService.findByUrl(anyString())).thenReturn(testLink);

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest();
        removeLinkRequest.setLink(testUrl);

        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", testChatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testLinkId))
            .andExpect(jsonPath("$.url").value(testUrl));
    }
}
