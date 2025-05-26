-- Проверочные запросы
SELECT * FROM users;
SELECT * FROM companies;
SELECT * FROM testers;
SELECT * FROM games;
SELECT * FROM game_assignments WHERE game_id = 1 AND status = 'в работе';
SELECT * FROM game_assignments;

-- Очистка данных
DELETE FROM companies;
DELETE FROM testers;
DELETE FROM users;


-- Проверки по пользователям
SELECT * FROM users WHERE username = 'company4';
SELECT * FROM users WHERE username = 'company3'; -- Исправлено: удалена лишняя точка с запятой

-- Добавление данных
INSERT INTO users (username, password, role) VALUES ('admin', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'PUBLIC'); -- Заменено на 'PUBLIC' вместо 'COMPANY'
INSERT INTO companies (user_id, company_name) VALUES (1, 'Admin Company');

-- Запрос к game_demos
SELECT
    gd.company_id,
    gd.file_path,
    gd.id,
    gd.min_tester_rating,
    gd.requires_manual_selection,
    gd.title
FROM
    game_demos gd; -- Исправлено: game_demos вместо table

-- Создание таблицы
CREATE TABLE game_assignments (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    tester_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    assigned_at TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (tester_id) REFERENCES users(id)
);

\d games
\d users


DROP TABLE game_assignments CASCADE;