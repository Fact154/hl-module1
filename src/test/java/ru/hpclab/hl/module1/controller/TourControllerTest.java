package ru.hpclab.hl.module1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.service.TourService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourService tourService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddTour() throws Exception {
        Tour tour = new Tour();
        tour.setGuideName("Alex");

        when(tourService.addTour(any())).thenReturn(tour);

        mockMvc.perform(post("/tours")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guideName").value("Alex"));
    }

    @Test
    void testGetTour() throws Exception {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setGuideName("Maria");

        when(tourService.getTour(1L)).thenReturn(tour);

        mockMvc.perform(get("/tours/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.guideName").value("Maria"));
    }

    @Test
    void testGetAllTours() throws Exception {
        Tour tour1 = new Tour();
        tour1.setGuideName("Michael");

        Tour tour2 = new Tour();
        tour2.setGuideName("Sophie");

        when(tourService.getAllTours()).thenReturn(List.of(tour1, tour2));

        mockMvc.perform(get("/tours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].guideName").value("Michael"))
                .andExpect(jsonPath("$[1].guideName").value("Sophie"));
    }

    @Test
    void testGetExhibitRating() throws Exception {
        when(tourService.getExhibitRating(2025, 3)).thenReturn(List.of(
                Map.entry(1L, 10L),
                Map.entry(2L, 5L)
        ));

        mockMvc.perform(get("/tours/rating?year=2025&month=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value(1))
                .andExpect(jsonPath("$[0].value").value(10))
                .andExpect(jsonPath("$[1].key").value(2))
                .andExpect(jsonPath("$[1].value").value(5));
    }
}
