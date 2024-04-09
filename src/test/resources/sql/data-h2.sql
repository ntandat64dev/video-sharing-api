# Visibility
INSERT INTO `visibility`(id, created_by, created_date, modified_by, modified_date, visibility_level)
VALUES (X'ec386a4b04cd45a7afb63635c9183ba0', 'admin', NOW(), 'admin', NOW(), 'PRIVATE'),
       (X'f01121d2b6174c21844800059c6ff461', 'admin', NOW(), 'admin', NOW(), 'PUBLIC');

# User
INSERT INTO `user`(id, created_by, created_date, modified_by, modified_date, country, date_of_birth, email, gender, password, phone)
VALUES (X'3f06af63a93c11e4979700505690773f', 'admin', NOW(), 'admin', NOW(), null, null, 'user@gmail.com', null, '00000000', null),
       (X'a05990b1911040b1aa4c03951b0705de', 'admin', NOW(), 'admin', NOW(), null, null, 'user2@gmail.com', null, '00000000', null);

# Channel
INSERT INTO `channel`(id, created_by, created_date, modified_by, modified_date, description, join_date, name, picture_url, user_id)
VALUES (X'a1e6741bfb6d4fb692342a47236bcf16', 'admin', NOW(), 'admin', NOW(), null, NOW(), 'user', '/default_avatar.png',
        X'3f06af63a93c11e4979700505690773f'),
       (X'8c936a6f7fb840078360371e6bafa18f', 'admin', NOW(), 'admin', NOW(), null, NOW(), 'user2', '/default_avatar.png',
        X'a05990b1911040b1aa4c03951b0705de');

# Hashtag
INSERT INTO `hashtag`(id, created_by, created_date, modified_by, modified_date, tag)
VALUES (X'c7e8a20e70164d6f9a63b4c2268a0c02', 'admin', NOW(), 'admin', NOW(), 'music'),
       (X'88cd13bd559b4f91b9e15aca57cc3024', 'admin', NOW(), 'admin', NOW(), 'sport');

# Video
INSERT INTO `video`(id, created_by, created_date, modified_by, modified_date, description, duration_sec, age_restricted, comment_allowed, for_kids,
                    location, thumbnail_url, title, upload_date, video_url, user_id, visibility_id)
VALUES (X'37b32dc2b0e045ab84691ad89a90b978', 'admin', NOW(), 'admin', NOW(), 'Video 1 description', 1000, false, true,
        false, 'New York, US', 'Video 1 thumbnail URL', 'Video 1', '20240401T09:00', 'Video 1 video URL',
        X'3f06af63a93c11e4979700505690773f', X'ec386a4b04cd45a7afb63635c9183ba0'),

       (X'f7d9b74b750c4f4983405bcb8450ae14', 'admin', NOW(), 'admin', NOW(), 'Video 2 description', 2000, false, true,
        false, 'Ha Noi, Vietnam', 'Video 2 thumbnail URL', 'Video 2', '20240402T09:00', 'Video 2 video URL',
        X'3f06af63a93c11e4979700505690773f', X'f01121d2b6174c21844800059c6ff461'),

       (X'e65707b4e9dc4d409a1d72667570bd6f', 'admin', NOW(), 'admin', NOW(), 'Video 3 description', 3000, false, false,
        true, 'Tokyo, Japan', 'Video 3 thumbnail URL', 'Video 3', '20240403T09:00', 'Video 3 video URL',
        X'a05990b1911040b1aa4c03951b0705de', X'f01121d2b6174c21844800059c6ff461');

# VideoSpec
INSERT INTO `video_spec`(created_by, created_date, modified_by, modified_date, comment_count, dislike_count, download_count, like_count, view_count,
                         video_id)
VALUES ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, X'37b32dc2b0e045ab84691ad89a90b978'),
       ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, X'f7d9b74b750c4f4983405bcb8450ae14'),
       ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, X'e65707b4e9dc4d409a1d72667570bd6f');

# VideoHashtag
INSERT INTO `video_hashtag`(created_by, created_date, modified_by, modified_date, hashtag_id, video_id)
VALUES ('admin', NOW(), 'admin', NOW(), X'c7e8a20e70164d6f9a63b4c2268a0c02', X'37b32dc2b0e045ab84691ad89a90b978'),
       ('admin', NOW(), 'admin', NOW(), X'c7e8a20e70164d6f9a63b4c2268a0c02', X'e65707b4e9dc4d409a1d72667570bd6f'),
       ('admin', NOW(), 'admin', NOW(), X'88cd13bd559b4f91b9e15aca57cc3024', X'e65707b4e9dc4d409a1d72667570bd6f');