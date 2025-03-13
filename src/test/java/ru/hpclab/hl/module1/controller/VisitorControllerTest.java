package ru.hpclab.hl.module1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.service.VisitorService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VisitorController.class)
class VisitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitorService visitorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddVisitor() throws Exception {
        Visitor visitor = new Visitor();
        visitor.setFullName("John Doe");
        visitor.setAge(30);

        when(visitorService.addVisitor(any())).thenReturn(visitor);

        mockMvc.perform(post("/visitors")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(visitor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }
}
