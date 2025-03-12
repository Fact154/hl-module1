package ru.hpclab.hl.module1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.*;
import ru.hpclab.hl.module1.service.MuseumService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/museum")
public class MuseumController {
    @Autowired
    private MuseumService museumService;

    @PostMapping("/exhibit")
    public void addExhibit(@RequestBody Exhibit exhibit) {
        museumService.addExhibit(exhibit);
    }

    @PostMapping("/visitor")
    public Visitor addVisitor(@RequestBody Visitor visitor) {
        return museumService.addVisitor(visitor);
    }

    @PostMapping("/excursion")
    public void addExcursion(@RequestBody Excursion excursion) {
        museumService.addExcursion(excursion);
    }

    @GetMapping("/top-exhibits")
    public List<Map.Entry<String, Integer>> getTopExhibits(@RequestParam int month, @RequestParam int year) {
        return museumService.getTopExhibits(month, year);
    }
}