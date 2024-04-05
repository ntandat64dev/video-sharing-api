# Visibility
INSERT INTO videosharing_test.visibility(id, created_by, created_date, modified_by, modified_date, visibility_level)
VALUES (UNHEX(REPLACE('ec386a4b-04cd-45a7-afb6-3635c9183ba0', '-', '')), 'admin', NOW(), 'admin', NOW(), 'PRIVATE'),
       (UNHEX(REPLACE('f01121d2-b617-4c21-8448-00059c6ff461', '-', '')), 'admin', NOW(), 'admin', NOW(), 'PUBLIC');

# User
INSERT INTO videosharing_test.user(id, created_by, created_date, modified_by, modified_date, country, date_of_birth, email, gender, password, phone)
VALUES (UNHEX(REPLACE('3f06af63-a93c-11e4-9797-00505690773f', '-', '')), 'admin', NOW(), 'admin', NOW(), null, null, 'user@gmail.com', null,
        '00000000', null),

       (UNHEX(REPLACE('a05990b1-9110-40b1-aa4c-03951b0705de', '-', '')), 'admin', NOW(), 'admin', NOW(), null, null, 'user2@gmail.com', null,
        '00000000', null);

# Channel
INSERT INTO videosharing_test.channel(id, created_by, created_date, modified_by, modified_date, description, join_date, name, picture_url, user_id)
VALUES (UNHEX(REPLACE('a1e6741b-fb6d-4fb6-9234-2a47236bcf16', '-', '')), 'admin', NOW(), 'admin', NOW(), null, NOW(), 'user', '/default_avatar.png',
        UNHEX(REPLACE('3f06af63-a93c-11e4-9797-00505690773f', '-', ''))),

       (UNHEX(REPLACE('8c936a6f-7fb8-4007-8360-371e6bafa18f', '-', '')), 'admin', NOW(), 'admin', NOW(), null, NOW(), 'user2', '/default_avatar.png',
        UNHEX(REPLACE('a05990b1-9110-40b1-aa4c-03951b0705de', '-', '')));

# Hashtag
INSERT INTO videosharing_test.hashtag(id, created_by, created_date, modified_by, modified_date, tag)
VALUES (UNHEX(REPLACE('c7e8a20e-7016-4d6f-9a63-b4c2268a0c02', '-', '')), 'admin', NOW(), 'admin', NOW(), 'music'),
       (UNHEX(REPLACE('88cd13bd-559b-4f91-b9e1-5aca57cc3024', '-', '')), 'admin', NOW(), 'admin', NOW(), 'sport');

# Video
INSERT INTO videosharing_test.video(id, created_by, created_date, modified_by, modified_date, description, duration_sec, age_restricted,
                                    comment_allowed, for_kids, location, thumbnail_url, title, upload_date, video_url, user_id, visibility_id)
VALUES (UNHEX(REPLACE('37b32dc2-b0e0-45ab-8469-1ad89a90b978', '-', '')), 'admin', NOW(), 'admin', NOW(), 'Video 1 description', 1000, false, true,
        false, 'New York, US', 'Video 1 thumbnail URL', 'Video 1', '2024-04-01T09:00', 'Video 1 video URL',
        UNHEX(REPLACE('3f06af63-a93c-11e4-9797-00505690773f', '-', '')),
        UNHEX(REPLACE('ec386a4b-04cd-45a7-afb6-3635c9183ba0', '-', ''))),

       (UNHEX(REPLACE('f7d9b74b-750c-4f49-8340-5bcb8450ae14', '-', '')), 'admin', NOW(), 'admin', NOW(), 'Video 2 description', 2000, false, true,
        false, 'Ha Noi, Vietnam', 'Video 2 thumbnail URL', 'Video 2', '2024-04-02T09:00', 'Video 2 video URL',
        UNHEX(REPLACE('3f06af63-a93c-11e4-9797-00505690773f', '-', '')),
        UNHEX(REPLACE('f01121d2-b617-4c21-8448-00059c6ff461', '-', ''))),

       (UNHEX(REPLACE('e65707b4-e9dc-4d40-9a1d-72667570bd6f', '-', '')), 'admin', NOW(), 'admin', NOW(), 'Video 3 description', 3000, false, false,
        true, 'Tokyo, Japan', 'Video 3 thumbnail URL', 'Video 3', '2024-04-03T09:00', 'Video 3 video URL',
        UNHEX(REPLACE('a05990b1-9110-40b1-aa4c-03951b0705de', '-', '')),
        UNHEX(REPLACE('f01121d2-b617-4c21-8448-00059c6ff461', '-', '')));

# VideoSpec
INSERT INTO videosharing_test.video_spec(created_by, created_date, modified_by, modified_date, comment_count, dislike_count, download_count,
                                         like_count, view_count, video_id)
VALUES ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, UNHEX(REPLACE('37b32dc2-b0e0-45ab-8469-1ad89a90b978', '-', ''))),
       ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, UNHEX(REPLACE('f7d9b74b-750c-4f49-8340-5bcb8450ae14', '-', ''))),
       ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, UNHEX(REPLACE('e65707b4-e9dc-4d40-9a1d-72667570bd6f', '-', '')));

# VideoHashtag
INSERT INTO videosharing_test.video_hashtag(created_by, created_date, modified_by, modified_date, hashtag_id, video_id)
VALUES ('admin', NOW(), 'admin', NOW(), UNHEX(REPLACE('c7e8a20e-7016-4d6f-9a63-b4c2268a0c02', '-', '')),
        UNHEX(REPLACE('37b32dc2-b0e0-45ab-8469-1ad89a90b978', '-', ''))),

       ('admin', NOW(), 'admin', NOW(), UNHEX(REPLACE('c7e8a20e-7016-4d6f-9a63-b4c2268a0c02', '-', '')),
        UNHEX(REPLACE('e65707b4-e9dc-4d40-9a1d-72667570bd6f', '-', ''))),

       ('admin', NOW(), 'admin', NOW(), UNHEX(REPLACE('88cd13bd-559b-4f91-b9e1-5aca57cc3024', '-', '')),
        UNHEX(REPLACE('e65707b4-e9dc-4d40-9a1d-72667570bd6f', '-', '')));