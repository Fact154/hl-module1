DO $$
DECLARE
    v_names TEXT[] := ARRAY['John', 'Jane', 'Alex', 'Emily', 'Michael', 'Sarah', 'David', 'Laura', 'Chris', 'Anna'];
    v_surnames TEXT[] := ARRAY['Smith', 'Johnson', 'Brown', 'Williams', 'Jones', 'Garcia', 'Miller', 'Davis', 'Wilson', 'Taylor'];
    e_words TEXT[] := ARRAY['Art', 'History', 'Space', 'Culture', 'Science', 'Music', 'War', 'Technology', 'Nature', 'Mind'];
    eras TEXT[] := ARRAY['Ancient', 'Medieval', 'Renaissance', 'Modern'];
    ticket_types TEXT[] := ARRAY['FULL', 'DISCOUNT'];
    i INT;
BEGIN
    -- Заполняем ТОЛЬКО если exhibit пуст
    IF NOT EXISTS (SELECT 1 FROM exhibit LIMIT 1) THEN

        -- Посетители
        FOR i IN 1..100 LOOP
            INSERT INTO visitor (full_name, age, ticket_type)
            VALUES (
                v_names[1 + floor(random() * 10)] || ' ' || v_surnames[1 + floor(random() * 10)],
                10 + floor(random() * 71)::int,
                ticket_types[1 + floor(random() * 2)]
            );
        END LOOP;

        -- Экспонаты
        FOR i IN 1..100 LOOP
            INSERT INTO exhibit (name, era, description)
            VALUES (
                e_words[1 + floor(random() * 10)] || ' Exhibit',
                eras[1 + floor(random() * 4)],
                'This exhibit showcases ' || lower(e_words[1 + floor(random() * 10)])
            );
        END LOOP;

        -- Экскурсии
        FOR i IN 1..100 LOOP
            INSERT INTO tour (exhibit_id, visitor_id, date, guide_name)
            VALUES (
                (SELECT id FROM exhibit ORDER BY random() LIMIT 1),
                (SELECT id FROM visitor ORDER BY random() LIMIT 1),
                NOW() - (floor(random() * 365)) * INTERVAL '1 day',
                CASE WHEN random() > 0.5 THEN
                    v_names[1 + floor(random() * 10)] || ' ' || v_surnames[1 + floor(random() * 10)]
                ELSE
                    NULL
                END
            );
        END LOOP;

    END IF;
END $$;
