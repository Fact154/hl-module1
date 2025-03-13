//package ru.hpclab.hl.module1.configuration;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import ru.hpclab.hl.module1.repository.ExhibitRepository;
//import ru.hpclab.hl.module1.repository.VisitorRepository;
//import ru.hpclab.hl.module1.repository.TourRepository;
//import ru.hpclab.hl.module1.service.ExhibitService;
//import ru.hpclab.hl.module1.service.VisitorService;
//import ru.hpclab.hl.module1.service.TourService;
//
//@Configuration
//public class ServicesConfig {
//
//    @Bean
//    VisitorService visitorService(VisitorRepository visitorRepository) {
//        return new VisitorService(visitorRepository);
//    }
//
//    @Bean
//    ExhibitService exhibitService(ExhibitRepository exhibitRepository) {
//        return new ExhibitService(exhibitRepository);
//    }
//
//    @Bean
//    TourService tourService(TourRepository tourRepository) {
//        return new TourService(tourRepository);
//    }
//
//    @Bean
//    @ConditionalOnProperty(prefix = "statistics", name = "service", havingValue = "console2000")
//    ExhibitService statisticsService2000(ExhibitRepository exhibitRepository) {
//        return new ExhibitService(exhibitRepository);
//    }
//
//    @Bean
//    @ConditionalOnProperty(prefix = "statistics", name = "service", havingValue = "console1000")
//    ExhibitService statisticsService1000(ExhibitRepository exhibitRepository) {
//        return new ExhibitService(exhibitRepository);
//    }
//}
