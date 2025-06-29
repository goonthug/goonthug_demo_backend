-- Проверочные запросы
SELECT * FROM users;
SELECT * FROM companies;
SELECT * FROM testers;
SELECT * FROM game_demo;
SELECT * FROM game_demo_archive;
SELECT * FROM game_assignments WHERE game_id = 1 AND status = 'в работе';
SELECT * FROM game_assignments;

SELECT * FROM games;
SELECT * FROM game_assignments;

DROP TABLE users;
DELETE FROM public.users WHERE company_name IS NOT NULL OR first_name IS NOT NULL OR last_name IS NOT NULL OR username IS NOT NULL;
SELECT * FROM public.users;
SELECT * FROM public.tester;

SELECT COUNT(*) FROM public.users WHERE company_name IS NOT NULL OR first_name IS NOT NULL OR last_name IS NOT NULL OR username IS NOT NULL;

SELECT * FROM information_schema.tables
WHERE table_name = 'game_demo';

SELECT column_name
FROM information_schema.columns
WHERE table_name = 'users'
AND column_name = 'email';


ALTER TABLE users ADD COLUMN email VARCHAR(255) UNIQUE NOT NULL;

ALTER TABLE public.users DROP COLUMN company_name;
ALTER TABLE public.users DROP COLUMN first_name;
ALTER TABLE public.users DROP COLUMN last_name;
ALTER TABLE public.users DROP COLUMN username;

SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'game_demo';

SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

ALTER TABLE game_demo ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'available';
SELECT column_name FROM information_schema.columns WHERE table_name = 'game_demo';
SELECT id, title, status FROM game_demo;

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

-- Проверьте существование таблиц
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN ('users', 'companies', 'testers');

-- Проверьте колонки в таблицах
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'companies';

SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'testers';

SELECT * FROM users WHERE email IS NULL;
SELECT * FROM users WHERE email IS NULL;
INSERT INTO users (email, password, role) VALUES ('test@example.com', '$2a$10$...hashed_password...', 'TESTER');
-- Добавить колонку email, позволяющую NULL значения на начальном этапе
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255);

ALTER TABLE users ADD CONSTRAINT unique_email UNIQUE (email);
ALTER TABLE users ALTER COLUMN email SET NOT NULL;


SELECT * FROM public.users;


INSERT INTO users (company_name, first_name, last_name, password, role, username, email)
VALUES ('TestCompany', 'John', 'Doe', '$2a$10$...encoded_password...', 'TESTER', 'johndoe', 'john.doe@example.com');

BEGIN;
INSERT INTO users (company_name, first_name, last_name, password, role, username, email)
VALUES ('TestCompany', 'John', 'Doe', '$2a$10$...encoded_password...', 'TESTER', 'johndoe', 'john.doe@example.com');
COMMIT;

SELECT current_schema();

-- Обновить существующие строки с значением по умолчанию или существующим значением (настройте в зависимости от ваших данных)
UPDATE users SET email = 'default@example.com' WHERE email IS NULL;
-- ИЛИ, если есть username и его можно использовать как email:
-- UPDATE users SET email = username WHERE email IS NULL;

-- Добавить ограничение NOT NULL
ALTER TABLE users ALTER COLUMN email SET NOT NULL;

-- Добавить уникальное ограничение (если нужно)
ALTER TABLE users ADD CONSTRAINT UK_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);