-- Insert 10 users
INSERT INTO public."user" (token, is_admin)
VALUES ('token1', false),
       ('token2', false),
       ('token3', false),
       ('token4', false),
       ('token5', false),
       ('token6', false),
       ('token7', false),
       ('token8', false),
       ('token9', false),
       ('token10', false),
       ('admin-token', true);

-- Insert 10 banners
INSERT INTO public.banner (banner_id, feature_id, content, is_active, is_deleted)
VALUES (1, 1, '{"message": "Banner 1"}', true, false),
       (2, 2, '{"message": "Banner 2"}', true, false),
       (3, 3, '{"message": "Banner 3"}', true, false),
       (4, 4, '{"message": "Banner 4"}', true, false),
       (5, 5, '{"message": "Banner 5"}', true, true),
       (6, 6, '{"message": "Banner 6"}', false, false),
       (7, 7, '{"message": "Banner 7"}', false, false),
       (8, 8, '{"message": "Banner 8"}', false, false),
       (9, 9, '{"message": "Banner 9"}', false, false),
       (10, 10, '{"message": "Banner 10"}', false, true);

-- Optionally, you can add some feature-tag associations
INSERT INTO public.banner_feature_tag (feature_id, tag_id, banner_id)
VALUES (1, 1, 1),
       (1, 2, 1),
       (2, 3, 2),
       (2, 4, 2),
       (3, 5, 3),
       (3, 6, 3),
       (4, 7, 4),
       (4, 8, 4),
       (5, 9, 5),
       (5, 10, 5),
       (6, 1, 6),
       (6, 2, 6),
       (7, 3, 7),
       (7, 4, 7),
       (8, 5, 8),
       (8, 6, 8),
       (9, 7, 9),
       (9, 8, 9),
       (10, 9, 10),
       (10, 10, 10);
