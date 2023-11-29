INSERT INTO users (name, email)
VALUES ('Виктор Петров', 'Petrov777@gmail.com');
INSERT INTO users (name, email)
VALUES ('Ирина Боброва', 'BobrIra@mail.ru');
INSERT INTO users (name, email)
VALUES ('Сергей Иванов', 'SupremeSerg91@yandex.com');
INSERT INTO users (name, email)
VALUES ('Алена Васина', 'VasinaA163@gmail.com');

INSERT INTO item_requests (description, requester_id, created)
VALUES ('Нужно осветительное оборудование для съемки клипа', 3, '2023-08-01T00:00:00');
INSERT INTO item_requests (description, requester_id, created)
VALUES ('Возьму в аренду бетономешалку литров на 50-100', 2, '2023-08-05T00:00:00');
INSERT INTO item_requests (description, requester_id, created)
VALUES ('Прицеп для лодки Прогресс-10', 4, '2023-09-10T00:00:00');
INSERT INTO item_requests (description, requester_id, created)
VALUES ('Нужен дрон с камерой для съемки клипа', 3, '2023-10-05T00:00:00');
INSERT INTO item_requests (description, requester_id, created)
VALUES ('Ищем 3 сноуборда с ботинками на зимние каникулы', 1, '2023-11-15T00:00:00');

INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Дрель ударная Bosh', 'Мощность 7000W', true, 1, null);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Дрель аккумуляторная', 'В комплекте запасной аккумулятор и набор бит', true, 3, null);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Байдарка трёхместная Ладога', '2003г.в. в отличном состоянии, весла отсутствуют', true, 1, null);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Набор походных котелков', '3 штуки: 3, 4, 5 литров', true, 1, null);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Швейная машина', 'Профессиональная, можно использовать для плотной ткани и толстых нитей', true, 4, null);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Какая-то штука', 'Не знаю что это такое', true, 2, null);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Прожектор диодный для кино', 'Мощность 200W. В наличии до 10 штук.', true, 4, 1);

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2023-08-01T00:00:00', '2023-08-10T00:00:00', 3, 4, 'APPROVED');
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2023-06-15T00:00:00', '2023-07-02T00:00:00', 3, 3, 'APPROVED');
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2024-05-31T00:00:00', '2024-06-11T00:00:00', 3, 2, 'APPROVED');
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2024-06-15T00:00:00', '2024-06-20T00:00:00', 3, 4, 'APPROVED');
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2023-11-01T00:00:00', '2023-11-10T00:00:00', 2, 2, 'APPROVED');
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2023-12-24T00:00:00', '2023-12-25T00:00:00', 1, 2, 'WAITING');

INSERT INTO comments (text, author_id, item_id, created)
VALUES ('Брали для похода по Ладоге. Спасибо, не подвела!', 4, 3, '2023-08-10T00:00:00');
INSERT INTO comments (text, author_id, item_id, created)
VALUES ('На трехместной самое то вдвоём с грузом', 3, 3, '2023-07-02T00:00:00')