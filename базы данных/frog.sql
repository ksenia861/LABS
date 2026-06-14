DROP TABLE IF EXISTS people_city CASCADE;
DROP TABLE IF EXISTS people_feeling CASCADE;
DROP TABLE IF EXISTS goal CASCADE;
DROP TABLE IF EXISTS people CASCADE;
DROP TABLE IF EXISTS feeling CASCADE;
DROP TABLE IF EXISTS city CASCADE;


CREATE TABLE city(
    city_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE people (
    people_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    has_heart BOOLEAN NOT NULL,
    goal_count INTEGER DEFAULT 0
);

CREATE TABLE goal (
    goal_id SERIAL PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    people_id INTEGER NOT NULL,

    CONSTRAINT fk_people_goal
        FOREIGN KEY (people_id)
        REFERENCES people(people_id)
        ON DELETE CASCADE
);

CREATE TABLE feeling (
    feeling_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE people_feeling(
    people_id INTEGER NOT NULL,
    feeling_id INTEGER NOT NULL,

    PRIMARY KEY (people_id, feeling_id),

    CONSTRAINT fk_people_feeling_people
        FOREIGN KEY (people_id)
        REFERENCES people(people_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_people_feeling_feeling
        FOREIGN KEY (feeling_id)
        REFERENCES feeling(feeling_id)
        ON DELETE CASCADE
);

CREATE TABLE people_city (
    people_id INTEGER NOT NULL,
    city_id INTEGER NOT NULL,

    PRIMARY KEY (people_id, city_id),

    CONSTRAINT fk_people_city_people
        FOREIGN KEY (people_id)
        REFERENCES people(people_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_people_city_city
        FOREIGN KEY (city_id)
        REFERENCES city(city_id)
        ON DELETE CASCADE
);



ALTER TABLE people
ADD COLUMN goal_count INTEGER DEFAULT 0;
CREATE OR REPLACE FUNCTION increase_goal_count()
RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE people
        SET goal_count = goal_count + 1
        WHERE people_id = NEW.people_id;
    END IF;
    IF TG_OP = 'UPDATE' THEN
        UPDATE people
        SET goal_count = goal_count - 1
        WHERE people_id = OLD.people_id;
        UPDATE people
        SET goal_count = goal_count + 1
        WHERE people_id = NEW.people_id;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
CREATE TRIGGER trg_increase_goal_count
AFTER INSERT OR UPDATE ON goal
FOR EACH ROW
EXECUTE FUNCTION increase_goal_count();



INSERT INTO city (name) VALUES
('Москва'),
('Санкт-Петербург'),
('Тверь'),
('Казань'),
('Нижний Новгород'),
('Самара'),
('Екатеринбург');



INSERT INTO people (name, has_heart) VALUES
('Анна', TRUE),
('Саша', TRUE),
('Глаша', TRUE),
('Сергей', TRUE),
('Арсений', TRUE),
('Маша', TRUE),
('Инна', FALSE);



INSERT INTO goal (description, people_id) VALUES
('Построить карьеру', 1),
('Найти смысл жизни', 2),
('Создать стартап', 3),
('Путешествовать по миру', 4),
('Заработать много денег', 5),
('Купить машину мечты', 6),
('Научиться готовить', 7),
('Прыгнуть с парашютом', 5),
('Создать семью', 3),
('Создать ИИ', 7),
('Построить самолет', 7),
('Полететь на Марс',1),
('Прокатиться на машине мечты', 3);



INSERT INTO feeling (name) VALUES
('Любовь'),
('Страх'),
('Радость'),
('Грусть'),
('Отзывчивость'),
('Верность');



INSERT INTO people_feeling(people_id, feeling_id) VALUES
(1,1),
(1,2),
(2,1),
(2,4),
(2,3),
(3,4),
(5,6),
(2,6),
(7,4),
(7,2),
(6,1),
(6,3),
(7,1),
(7,3),
(7,5),
(4,4),
(4,1);



INSERT INTO people_city(people_id, city_id) VALUES
(1,1),
(1,4),
(2,1),
(2,6),
(2,3),
(5,5),
(4,7),
(4,1),
(5,3),
(6,3),
(6,7),
(3,4),
(3,1),
(3,3),
(3,5),
(7,1),
(7,3),
(7,7),
(7,2),
(7,5);


SELECT people_id, name, goal_count
FROM people;