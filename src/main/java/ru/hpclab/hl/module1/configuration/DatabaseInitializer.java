package ru.hpclab.hl.module1.configuration;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.model.Visitor.TicketType;
import ru.hpclab.hl.module1.repository.ExhibitRepository;
import ru.hpclab.hl.module1.repository.TourRepository;
import ru.hpclab.hl.module1.repository.VisitorRepository;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            VisitorRepository visitorRepo,
            ExhibitRepository exhibitRepo,
            TourRepository tourRepo) {
        return args -> {

            tourRepo.deleteAll();
            visitorRepo.deleteAll();
            exhibitRepo.deleteAll();

            List<Visitor> visitors = visitorRepo.saveAll(List.of(
                    new Visitor("Анна Смирнова", 25, TicketType.FULL),
                    new Visitor("Иван Петров", 30, TicketType.FULL),
                    new Visitor("Мария Иванова", 28, TicketType.FULL),
                    new Visitor("Дмитрий Васильев", 35, TicketType.FULL),
                    new Visitor("Елена Козлова", 40, TicketType.FULL),
                    new Visitor("Сергей Сидоров", 22, TicketType.DISCOUNT),
                    new Visitor("Ольга Михайлова", 27, TicketType.DISCOUNT),
                    new Visitor("Алексей Фёдоров", 33, TicketType.DISCOUNT),
                    new Visitor("Юлия Никитина", 29, TicketType.DISCOUNT),
                    new Visitor("Павел Орлов", 31, TicketType.DISCOUNT)
            ));

            List<Exhibit> exhibits = exhibitRepo.saveAll(List.of(
                    new Exhibit("Мона Лиза", "Ренессанс", "Знаменитая картина Леонардо да Винчи."),
                    new Exhibit("Дискобол", "Древняя Греция", "Скульптура атлета."),
                    new Exhibit("Тутанхамон", "Древний Египет", "Золотая маска фараона."),
                    new Exhibit("Звёздная ночь", "Постимпрессионизм", "Картина Ван Гога."),
                    new Exhibit("Давид", "Ренессанс", "Статуя Микеланджело."),
                    new Exhibit("Роза Мария", "Барокко", "Известное полотно Рубенса."),
                    new Exhibit("Каменный век", "Доисторическое время", "Останки мамонта."),
                    new Exhibit("Декларация независимости", "18 век", "Оригинальный документ США."),
                    new Exhibit("Жемчужина Востока", "Средневековье", "Редкий артефакт."),
                    new Exhibit("Кодекс Лестади", "Античность", "Рукописная книга.")
            ));

            LocalDate date = LocalDate.of(2025, 3, 10);

            tourRepo.saveAll(List.of(
                    new Tour(exhibits.get(0), visitors.get(0), date, "Гид Алексей"),
                    new Tour(exhibits.get(1), visitors.get(1), date, "Гид Ирина"),
                    new Tour(exhibits.get(2), visitors.get(2), date, "Гид Виктор"),
                    new Tour(exhibits.get(3), visitors.get(3), date, "Гид Ольга"),
                    new Tour(exhibits.get(4), visitors.get(4), date, "Гид Павел"),
                    new Tour(exhibits.get(5), visitors.get(5), date, "Гид Елена"),
                    new Tour(exhibits.get(5), visitors.get(6), date, "Гид Елена"),
                    new Tour(exhibits.get(6), visitors.get(7), date, "Гид Николай"),
                    new Tour(exhibits.get(6), visitors.get(8), date, "Гид Николай"),
                    new Tour(exhibits.get(6), visitors.get(9), date, "Гид Николай")
            ));
        };
    }
}
