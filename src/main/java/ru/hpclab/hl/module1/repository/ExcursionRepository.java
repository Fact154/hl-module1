package ru.hpclab.hl.module1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hpclab.hl.module1.model.Excursion;

public interface ExcursionRepository extends JpaRepository<Excursion, Long> {
}