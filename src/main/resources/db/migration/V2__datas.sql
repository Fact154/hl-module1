-- Очистка данных (на случай повторного запуска миграции)
TRUNCATE TABLE tour RESTART IDENTITY CASCADE;
TRUNCATE TABLE visitor RESTART IDENTITY CASCADE;
TRUNCATE TABLE exhibit RESTART IDENTITY CASCADE;

-- Заполнение таблицы visitor (10 человек, 5 с тарифом FULL)
INSERT INTO visitor (full_name, age, ticket_type) VALUES
('Иван Петров', 25, 'FULL'),
('Мария Иванова', 30, 'FULL'),
('Алексей Смирнов', 28, 'FULL'),
('Светлана Кузнецова', 35, 'FULL'),
('Дмитрий Федоров', 40, 'FULL'),
('Анна Сидорова', 22, 'DISCOUNT'),
('Петр Николаев', 27, 'DISCOUNT'),
('Виктория Орлова', 24, 'DISCOUNT'),
('Егор Тихонов', 31, 'DISCOUNT'),
('Ольга Захарова', 29, 'DISCOUNT');

-- Заполнение таблицы exhibit (10 экспонатов)
INSERT INTO exhibit (name, era, description) VALUES
('Золотая маска фараона', 'Древний Египет', 'Маска, найденная в гробнице Тутанхамона.'),
('Римский меч гладиус', 'Древний Рим', 'Оружие легионеров римской армии.'),
('Ваза из Китая', 'Династия Мин', 'Редкая китайская фарфоровая ваза.'),
('Медные монеты', 'Средневековье', 'Коллекция средневековых монет.'),
('Картина Рембрандта', 'XVII век', 'Оригинальная работа Рембрандта.'),
('Самурайский меч', 'Феодальная Япония', 'Катана легендарного самурая.'),
('Паровой двигатель', 'XIX век', 'Первая модель парового двигателя.'),
('Космический спутник', 'XX век', 'Первый искусственный спутник Земли.'),
('Греческая амфора', 'Древняя Греция', 'Керамическое изделие с мифологическими сюжетами.'),
('Бронзовая статуэтка', 'Древний Китай', 'Ценный артефакт китайского искусства.');

-- Заполнение таблицы tour (10 экскурсий)
-- Первые 10 экскурсий для посетителей с тарифом FULL (по одному на выставку)
INSERT INTO tour (exhibit_id, visitor_id, date, guide_name) VALUES
(1, 1, '2025-03-02', 'Гид Александр'),
(2, 2, '2025-03-05', 'Гид Марина'),
(3, 3, '2025-03-07', 'Гид Игорь'),
(4, 4, '2025-03-10', 'Гид Ольга'),
(5, 5, '2025-03-12', 'Гид Сергей'),
(6, 1, '2025-03-14', 'Гид Александр'),
(7, 2, '2025-03-17', 'Гид Марина'),
(8, 3, '2025-03-19', 'Гид Игорь'),
(9, 4, '2025-03-21', 'Гид Ольга'),
(10, 5, '2025-03-25', 'Гид Сергей');

-- Остальные 5 посетителей:
-- 3 посетителя пошли на одну выставку
INSERT INTO tour (exhibit_id, visitor_id, date, guide_name) VALUES
(1, 6, '2025-03-26', 'Гид Алексей'),
(1, 7, '2025-03-26', 'Гид Алексей'),
(1, 8, '2025-03-26', 'Гид Алексей');

-- 2 посетителя пошли на другую
INSERT INTO tour (exhibit_id, visitor_id, date, guide_name) VALUES
(2, 9, '2025-03-27', 'Гид Виктор'),
(2, 10, '2025-03-27', 'Гид Виктор');