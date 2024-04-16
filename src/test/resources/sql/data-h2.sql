# Privacy
INSERT INTO `privacy`(id, created_by, created_date, modified_by, modified_date, status)
VALUES (X'ec386a4b04cd45a7afb63635c9183ba0', 'admin', NOW(), 'admin', NOW(), 'PRIVATE'),
       (X'f01121d2b6174c21844800059c6ff461', 'admin', NOW(), 'admin', NOW(), 'PUBLIC');

# Thumbnail
INSERT INTO `thumbnail`(id, created_by, created_date, modified_by, modified_date, type, url, width, height)
VALUES (X'7e90877fcb284ff0a7960914c3f45e59', 'admin', NOW(), 'admin', NOW(), 'DEFAULT',
        'https://ui-avatars.com/api/?name=user1&size=100&background=0D8ABC&color=fff&rouded=true&bold=true', 100, 100),
       (X'f3e78681b700493c94af25cd95d53848', 'admin', NOW(), 'admin', NOW(), 'MEDIUM',
        'https://ui-avatars.com/api/?name=user1&size=200&background=0D8ABC&color=fff&rouded=true&bold=true', 200, 200),
       (X'a31aba86d50547e28ca9e71a163958f8', 'admin', NOW(), 'admin', NOW(), 'DEFAULT',
        'https://ui-avatars.com/api/?name=user2&size=100&background=0D8ABC&color=fff&rouded=true&bold=true', 100, 100),

       (X'b28257049f6443cababe780f40d0e8f1', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', '', 720, 450),
       (X'2c48d7cd0c0f449ab91c4c2ca083d227', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', '', 1024, 720),
       (X'1243853b465c44179eb167f5b191c381', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', '', 720, 450),
       (X'78a1b2d45bad4b74893750d4d0b6e6a5', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', '', 720, 450);

# Channel
INSERT INTO `channel`(id, created_by, created_date, modified_by, modified_date, description, published_at, title)
VALUES (X'a1e6741bfb6d4fb692342a47236bcf16', 'admin', NOW(), 'admin', NOW(), null, NOW(), 'user1'),
       (X'8c936a6f7fb840078360371e6bafa18f', 'admin', NOW(), 'admin', NOW(), null, NOW(), 'user2');

# ChannelThumbnail
INSERT INTO `channel_thumbnail`(channel_id, thumbnail_id)
VALUES (X'a1e6741bfb6d4fb692342a47236bcf16', X'7e90877fcb284ff0a7960914c3f45e59'),
       (X'a1e6741bfb6d4fb692342a47236bcf16', X'f3e78681b700493c94af25cd95d53848'),
       (X'8c936a6f7fb840078360371e6bafa18f', X'a31aba86d50547e28ca9e71a163958f8');

# User
INSERT INTO `user`(id, created_by, created_date, modified_by, modified_date, country, date_of_birth, email, gender,
                   password, phone_number, channel_id)
VALUES (X'3f06af63a93c11e4979700505690773f', 'admin', NOW(), 'admin', NOW(), null, null, 'user@gmail.com', null,
        '00000000', null, X'a1e6741bfb6d4fb692342a47236bcf16'),
       (X'a05990b1911040b1aa4c03951b0705de', 'admin', NOW(), 'admin', NOW(), null, null, 'user2@gmail.com', null,
        '00000000', null, X'8c936a6f7fb840078360371e6bafa18f');

# Hashtag
INSERT INTO `hashtag`(id, created_by, created_date, modified_by, modified_date, tag)
VALUES (X'c7e8a20e70164d6f9a63b4c2268a0c02', 'admin', NOW(), 'admin', NOW(), 'music'),
       (X'88cd13bd559b4f91b9e15aca57cc3024', 'admin', NOW(), 'admin', NOW(), 'sport');

# Video
INSERT INTO `video`(id, created_by, created_date, modified_by, modified_date, description, duration_sec,
                    age_restricted, comment_allowed, made_for_kids, location, title, published_at, video_url, user_id,
                    privacy_id)
VALUES (X'37b32dc2b0e045ab84691ad89a90b978', 'admin', NOW(), 'admin', NOW(), 'Video 1 description', 1000, false, true,
        false, 'New York, US', 'Video 1', '20240401T09:00', 'Video 1 video URL',
        X'3f06af63a93c11e4979700505690773f', X'ec386a4b04cd45a7afb63635c9183ba0'),

       (X'f7d9b74b750c4f4983405bcb8450ae14', 'admin', NOW(), 'admin', NOW(), 'Video 2 description', 2000, false, true,
        false, 'Ha Noi, Vietnam', 'Video 2', '20240402T09:00', 'Video 2 video URL',
        X'3f06af63a93c11e4979700505690773f', X'f01121d2b6174c21844800059c6ff461'),

       (X'e65707b4e9dc4d409a1d72667570bd6f', 'admin', NOW(), 'admin', NOW(), 'Video 3 description', 3000, false, false,
        true, 'Tokyo, Japan', 'Video 3', '20240403T09:00', 'Video 3 video URL',
        X'a05990b1911040b1aa4c03951b0705de', X'f01121d2b6174c21844800059c6ff461');

# VideoStatistic
INSERT INTO `video_statistic`(created_by, created_date, modified_by, modified_date, comment_count, dislike_count,
                              download_count, like_count, view_count, video_id)
VALUES ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, X'37b32dc2b0e045ab84691ad89a90b978'),
       ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, X'f7d9b74b750c4f4983405bcb8450ae14'),
       ('admin', NOW(), 'admin', NOW(), 0, 0, 0, 0, 0, X'e65707b4e9dc4d409a1d72667570bd6f');

# VideoHashtag
INSERT INTO `video_hashtag`(video_id, hashtag_id)
VALUES (X'37b32dc2b0e045ab84691ad89a90b978', X'c7e8a20e70164d6f9a63b4c2268a0c02'),
       (X'e65707b4e9dc4d409a1d72667570bd6f', X'c7e8a20e70164d6f9a63b4c2268a0c02'),
       (X'e65707b4e9dc4d409a1d72667570bd6f', X'88cd13bd559b4f91b9e15aca57cc3024');

# VideoThumbnail
INSERT INTO `video_thumbnail`(video_id, thumbnail_id)
VALUES (X'37b32dc2b0e045ab84691ad89a90b978', X'b28257049f6443cababe780f40d0e8f1'),
       (X'37b32dc2b0e045ab84691ad89a90b978', X'2c48d7cd0c0f449ab91c4c2ca083d227'),
       (X'f7d9b74b750c4f4983405bcb8450ae14', X'1243853b465c44179eb167f5b191c381'),
       (X'e65707b4e9dc4d409a1d72667570bd6f', X'78a1b2d45bad4b74893750d4d0b6e6a5');
