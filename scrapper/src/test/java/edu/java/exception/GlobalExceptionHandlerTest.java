package edu.java.exception;

import edu.java.controller.ScrapperApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ScrapperApiController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

}
