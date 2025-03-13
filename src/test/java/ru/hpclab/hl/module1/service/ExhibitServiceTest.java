package ru.hpclab.hl.module1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.repository.ExhibitRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExhibitServiceTest {

    @Mock
    private ExhibitRepository exhibitRepository;

    @InjectMocks
    private ExhibitService exhibitService;

    @Test
    void testAddExhibit() {
        Exhibit exhibit = new Exhibit();
        exhibit.setName("Ancient Vase");

        when(exhibitRepository.save(exhibit)).thenReturn(exhibit);

        Exhibit savedExhibit = exhibitService.addExhibit(exhibit);

        assertNotNull(savedExhibit);
        assertEquals("Ancient Vase", savedExhibit.getName());
        verify(exhibitRepository, times(1)).save(exhibit);
    }

    @Test
    void testGetExhibit() {
        Exhibit exhibit = new Exhibit();
        exhibit.setId(1L);
        exhibit.setName("Golden Mask");

        when(exhibitRepository.findById(1L)).thenReturn(Optional.of(exhibit));

        Exhibit foundExhibit = exhibitService.getExhibit(1L);

        assertNotNull(foundExhibit);
        assertEquals(1L, foundExhibit.getId());
        assertEquals("Golden Mask", foundExhibit.getName());
        verify(exhibitRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllExhibits() {
        Exhibit exhibit1 = new Exhibit();
        Exhibit exhibit2 = new Exhibit();

        when(exhibitRepository.findAll()).thenReturn(List.of(exhibit1, exhibit2));

        List<Exhibit> exhibits = exhibitService.getAllExhibits();

        assertEquals(2, exhibits.size());
        verify(exhibitRepository, times(1)).findAll();
    }

    @Test
    void testDeleteExhibit() {
        doNothing().when(exhibitRepository).deleteById(1L);

        exhibitService.deleteExhibit(1L);

        verify(exhibitRepository, times(1)).deleteById(1L);
    }
}
