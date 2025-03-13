package ru.hpclab.hl.module1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.repository.VisitorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    @Mock
    private VisitorRepository visitorRepository;

    @InjectMocks
    private VisitorService visitorService;

    @Test
    void testAddVisitor() {
        Visitor visitor = new Visitor();
        visitor.setFullName("John Doe");
        visitor.setAge(25);

        when(visitorRepository.save(visitor)).thenReturn(visitor);

        Visitor savedVisitor = visitorService.addVisitor(visitor);

        assertNotNull(savedVisitor);
        assertEquals("John Doe", savedVisitor.getFullName());
        assertEquals(25, savedVisitor.getAge());
        verify(visitorRepository, times(1)).save(visitor);
    }

    @Test
    void testGetVisitor() {
        Visitor visitor = new Visitor();
        visitor.setId(1L);
        visitor.setFullName("Alice");

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));

        Visitor foundVisitor = visitorService.getVisitor(1L);

        assertNotNull(foundVisitor);
        assertEquals(1L, foundVisitor.getId());
        assertEquals("Alice", foundVisitor.getFullName());
        verify(visitorRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllVisitors() {
        Visitor visitor1 = new Visitor();
        Visitor visitor2 = new Visitor();

        when(visitorRepository.findAll()).thenReturn(List.of(visitor1, visitor2));

        List<Visitor> visitors = visitorService.getAllVisitors();

        assertEquals(2, visitors.size());
        verify(visitorRepository, times(1)).findAll();
    }

    @Test
    void testDeleteVisitor() {
        doNothing().when(visitorRepository).deleteById(1L);

        visitorService.deleteVisitor(1L);

        verify(visitorRepository, times(1)).deleteById(1L);
    }
}
