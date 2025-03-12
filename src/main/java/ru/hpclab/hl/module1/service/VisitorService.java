package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.repository.VisitorRepository;
import java.util.List;

@Service
public class VisitorService {
    private final VisitorRepository repository;

    public VisitorService(VisitorRepository repository) {
        this.repository = repository;
    }

    public Visitor addVisitor(Visitor visitor) {
        return repository.save(visitor);
    }

    public Visitor getVisitor(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Visitor> getAllVisitors() {
        return repository.findAll();
    }

    public void deleteVisitor(Long id) {
        repository.deleteById(id);
    }
}
