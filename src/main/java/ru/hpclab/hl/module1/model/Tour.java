package ru.hpclab.hl.module1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Tour")

public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne
    private Exhibit exhibit;

    @ManyToOne
    private Visitor visitor;

    private LocalDate date;
    private String guideName;

}