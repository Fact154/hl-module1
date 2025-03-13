package ru.hpclab.hl.module1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.service.ExhibitService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ExhibitController.class)
class ExhibitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExhibitService exhibitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddExhibit() throws Exception {
        Exhibit exhibit = new Exhibit();
        exhibit.setName("Ancient Vase");
        exhibit.setEra("Roman");
        exhibit.setDescription("A beautifully crafted vase from ancient Rome.");

        when(exhibitService.addExhibit(any())).thenReturn(exhibit);

        mockMvc.perform(post("/exhibits")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(exhibit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ancient Vase"));
    }

    @Test
    void testGetExhibit() throws Exception {
        Exhibit exhibit = new Exhibit();
        exhibit.setId(1L);
        exhibit.setName("Golden Crown");

        when(exhibitService.getExhibit(1L)).thenReturn(exhibit);

        mockMvc.perform(get("/exhibits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Golden Crown"));
    }

    @Test
    void testGetAllExhibits() throws Exception {
        Exhibit exhibit1 = new Exhibit();
        exhibit1.setName("Statue");

        Exhibit exhibit2 = new Exhibit();
        exhibit2.setName("Shield");

        when(exhibitService.getAllExhibits()).thenReturn(List.of(exhibit1, exhibit2));

        mockMvc.perform(get("/exhibits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Statue"))
                .andExpect(jsonPath("$[1].name").value("Shield"));
    }

    @Test
    void testDeleteExhibit() throws Exception {
        doNothing().when(exhibitService).deleteExhibit(1L);

        mockMvc.perform(delete("/exhibits/1"))
                .andExpect(status().isOk());

        verify(exhibitService, times(1)).deleteExhibit(1L);
    }
}
