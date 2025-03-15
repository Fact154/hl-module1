package ru.hpclab.hl.module1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hpclab.hl.module1.model.Tour;
import java.time.LocalDate;
import java.util.List;


public interface TourRepository extends JpaRepository<Tour, Long> {
    @Query(value = "SELECT t.exhibit_id, COUNT(t) FROM Tour t " +
            "WHERE t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY t.exhibit_id " +
            "ORDER BY COUNT(t) DESC", nativeQuery = true)
    List<Object[]> findExhibitRatingNative(@Param("startDate") LocalDate start, @Param("endDate") LocalDate end);
}
