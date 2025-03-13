-- Создание таблицы Visitor с BIGSERIAL для id
CREATE TABLE IF NOT EXISTS visitor (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    ticket_type VARCHAR(50) NOT NULL
);

-- Создание таблицы Exhibit с BIGSERIAL для id
CREATE TABLE IF NOT EXISTS exhibit (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    era VARCHAR(255) NOT NULL,
    description TEXT
);

-- Создание таблицы Tour с BIGSERIAL для id и BIGINT для внешних ключей
CREATE TABLE IF NOT EXISTS tour (
    id BIGSERIAL PRIMARY KEY,
    exhibit_id BIGINT NOT NULL,
    visitor_id BIGINT NOT NULL,
    date DATE NOT NULL,
    guide_name VARCHAR(255),
    FOREIGN KEY (exhibit_id) REFERENCES exhibit(id),
    FOREIGN KEY (visitor_id) REFERENCES visitor(id)
);
