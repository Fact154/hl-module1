package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.repository.ExhibitRepository;
import java.util.List;

@Service
public class ExhibitService {
    private final ExhibitRepository repository;

    public ExhibitService(ExhibitRepository repository) {
        this.repository = repository;
    }

    public Exhibit addExhibit(Exhibit exhibit) {
        return repository.save(exhibit);
    }

    public Exhibit getExhibit(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Exhibit> getAllExhibits() {
        return repository.findAll();
    }

    public void deleteExhibit(Long id) {
        repository.deleteById(id);
    }
}