INSERT INTO public.comments (author_id, create_at, id, task_id, update_at, content) VALUES (1, '2024-08-07 00:29:12.222000 +00:00', 1, 1, null, 'comment 1');
INSERT INTO public.comments (author_id, create_at, id, task_id, update_at, content) VALUES (2, '2024-08-07 00:30:23.313000 +00:00', 2, 1, null, 'comment 2');

ALTER SEQUENCE comments_id_seq RESTART WITH 20