SELECT * FROM users;
SELECT * FROM companies;
SELECT * FROM testers;

DELETE FROM companies;
DELETE FROM testers;
DELETE FROM users;

SELECT * FROM users WHERE username = 'company3';


INSERT INTO users (username, password, role) VALUES ('admin', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'COMPANY');
INSERT INTO companies (user_id, company_name) VALUES (1, 'Admin Company');