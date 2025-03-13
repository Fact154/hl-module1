package ru.hpclab.hl.module1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.repository.TourRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourService tourService;

    @Test
    void testAddTour() {
        Tour tour = new Tour();
        tour.setGuideName("John Doe");

        when(tourRepository.save(tour)).thenReturn(tour);

        Tour savedTour = tourService.addTour(tour);

        assertNotNull(savedTour);
        assertEquals("John Doe", savedTour.getGuideName());
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void testGetTour() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setGuideName("Alice");

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        Tour foundTour = tourService.getTour(1L);

        assertNotNull(foundTour);
        assertEquals(1L, foundTour.getId());
        assertEquals("Alice", foundTour.getGuideName());
        verify(tourRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllTours() {
        Tour tour1 = new Tour();
        Tour tour2 = new Tour();

        when(tourRepository.findAll()).thenReturn(List.of(tour1, tour2));

        List<Tour> tours = tourService.getAllTours();

        assertEquals(2, tours.size());
        verify(tourRepository, times(1)).findAll();
    }

    @Test
    void testDeleteTour() {
        doNothing().when(tourRepository).deleteById(1L);

        tourService.deleteTour(1L);

        verify(tourRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetExhibitRating() {
        Exhibit exhibit1 = new Exhibit();
        exhibit1.setId(1L);
        Exhibit exhibit2 = new Exhibit();
        exhibit2.setId(2L);

        Tour tour1 = new Tour();
        tour1.setExhibit(exhibit1);
        tour1.setDate(LocalDate.of(2025, 3, 1));

        Tour tour2 = new Tour();
        tour2.setExhibit(exhibit1);
        tour2.setDate(LocalDate.of(2025, 3, 2));

        Tour tour3 = new Tour();
        tour3.setExhibit(exhibit2);
        tour3.setDate(LocalDate.of(2025, 3, 5));

        when(tourRepository.findByDateBetween(any(), any()))
                .thenReturn(List.of(tour1, tour2, tour3));

        List<Map.Entry<Long, Long>> rating = tourService.getExhibitRating(2025, 3);

        assertEquals(2, rating.size());
        assertEquals(1L, rating.get(0).getKey());
        assertEquals(2L, rating.get(0).getValue());
        assertEquals(2L, rating.get(1).getKey());
        assertEquals(1L, rating.get(1).getValue());
    }
}
