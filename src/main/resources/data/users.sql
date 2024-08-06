INSERT INTO public.users (id, username, email, password) VALUES (1, 'John Doe', 'jo22hrn.doe@example.com', '$2a$10$JM0K2qciQU8xL6Po1xKFqOntLoTeVQ1o2tH4aysWiuHP2Md5.GMB2');
INSERT INTO public.users (id, username, email, password) VALUES (2, 'John 2', 'e2@example.com', '$2a$10$dqd/W2YHXYE86If6bgiA1.4HEc7fl48jFb0h86Tq0A9G0xFeGJTL2');

ALTER SEQUENCE users_id_seq RESTART WITH 10