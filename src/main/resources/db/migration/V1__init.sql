-- Создание таблицы Visitor
CREATE TABLE IF NOT EXISTS visitor (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    ticket_type VARCHAR(50) NOT NULL
);

-- Создание таблицы Exhibit
CREATE TABLE IF NOT EXISTS exhibit (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    era VARCHAR(255) NOT NULL,
    description TEXT
);

-- Создание таблицы Tour
CREATE TABLE IF NOT EXISTS tour (
    id SERIAL PRIMARY KEY,
    exhibit_id INT NOT NULL,
    visitor_id INT NOT NULL,
    date DATE NOT NULL,
    guide_name VARCHAR(255),
    FOREIGN KEY (exhibit_id) REFERENCES exhibit(id),
    FOREIGN KEY (visitor_id) REFERENCES visitor(id)
);
