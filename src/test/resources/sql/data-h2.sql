# Privacy
INSERT INTO `privacy`(id, created_by, created_date, modified_by, modified_date, status)
VALUES  (X'ec386a4b04cd45a7afb63635c9183ba0', 'admin', NOW(), 'admin', NOW(), 'PRIVATE'),
        (X'f01121d2b6174c21844800059c6ff461', 'admin', NOW(), 'admin', NOW(), 'PUBLIC');

# Thumbnail
INSERT INTO `thumbnail`(id, created_by, created_date, modified_by, modified_date, type, url, width, height)
VALUES  (X'7e90877fcb284ff0a7960914c3f45e59', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 1 default thumbnail', 100, 100),
        (X'f3e78681b700493c94af25cd95d53848', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', 'User 1 medium thumbnail', 200, 200),
        (X'a31aba86d50547e28ca9e71a163958f8', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 2 default thumbnail', 100, 100),

        (X'b28257049f6443cababe780f40d0e8f1', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 1 default thumbnail', 720, 450),
        (X'2c48d7cd0c0f449ab91c4c2ca083d227', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', 'Video 1 medium thumbnail', 1024, 720),
        (X'1243853b465c44179eb167f5b191c381', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 2 default thumbnail', 720, 450),
        (X'78a1b2d45bad4b74893750d4d0b6e6a5', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 3 default thumbnail', 720, 450);

# User
INSERT INTO `user`(id, created_by, created_date, modified_by, modified_date, country, date_of_birth, email, gender, password, phone_number, username, bio, published_at)
VALUES  (X'3f06af63a93c11e4979700505690773f', 'admin', NOW(), 'admin', NOW(), null, null, 'user@gmail.com', null, '00000000', null, 'user', null, NOW()),
        (X'a05990b1911040b1aa4c03951b0705de', 'admin', NOW(), 'admin', NOW(), null, null, 'user2@gmail.com', null, '00000000', null,  'user1', null, NOW());

# UserThumbnail
INSERT INTO `user_thumbnail`(user_id, thumbnail_id)
VALUES  (X'3f06af63a93c11e4979700505690773f', X'7e90877fcb284ff0a7960914c3f45e59'),
        (X'3f06af63a93c11e4979700505690773f', X'f3e78681b700493c94af25cd95d53848'),
        (X'a05990b1911040b1aa4c03951b0705de', X'a31aba86d50547e28ca9e71a163958f8');

# Follow
INSERT INTO `follow`(id, created_by, created_date, modified_by, modified_date, published_at, follower_id, user_id)
VALUES  (X'f2cf8a4802d64e04a816045521ee7b83', 'admin', NOW(), 'admin', NOW(), NOW(), X'a05990b1911040b1aa4c03951b0705de', X'3f06af63a93c11e4979700505690773f');

# Hashtag
INSERT INTO `hashtag`(id, created_by, created_date, modified_by, modified_date, tag)
VALUES  (X'c7e8a20e70164d6f9a63b4c2268a0c02', 'admin', NOW(), 'admin', NOW(), 'music'),
        (X'88cd13bd559b4f91b9e15aca57cc3024', 'admin', NOW(), 'admin', NOW(), 'sport');

# Video
INSERT INTO `video`(id, created_by, created_date, modified_by, modified_date, description, duration_sec,   age_restricted, comment_allowed, made_for_kids, location, title, published_at, video_url, user_id, privacy_id)
VALUES  (X'37b32dc2b0e045ab84691ad89a90b978', 'admin', NOW(), 'admin', NOW(), 'Video 1 description', 1000, false, true, false, 'New York, US', 'Video 1', '20240401T09:00', 'Video 1 video URL', X'3f06af63a93c11e4979700505690773f', X'ec386a4b04cd45a7afb63635c9183ba0'),
        (X'f7d9b74b750c4f4983405bcb8450ae14', 'admin', NOW(), 'admin', NOW(), 'Video 2 description', 2000, false, true, false, 'Ha Noi, Vietnam', 'Video 2', '20240402T09:00', 'Video 2 video URL', X'3f06af63a93c11e4979700505690773f', X'f01121d2b6174c21844800059c6ff461'),
        (X'e65707b4e9dc4d409a1d72667570bd6f', 'admin', NOW(), 'admin', NOW(), 'Video 3 description', 3000, false, false, true, 'Tokyo, Japan', 'Video 3', '20240403T09:00', 'Video 3 video URL', X'a05990b1911040b1aa4c03951b0705de', X'f01121d2b6174c21844800059c6ff461');

# VideoStatistic
INSERT INTO `video_statistic`(created_by, created_date, modified_by, modified_date, comment_count, dislike_count, download_count, like_count, view_count, video_id)
VALUES  ('admin', NOW(), 'admin', NOW(), 2, 0, 0, 2, 4, X'37b32dc2b0e045ab84691ad89a90b978'),
        ('admin', NOW(), 'admin', NOW(), 2, 1, 0, 1, 3, X'f7d9b74b750c4f4983405bcb8450ae14'),
        ('admin', NOW(), 'admin', NOW(), 2, 0, 0, 0, 2, X'e65707b4e9dc4d409a1d72667570bd6f');

# VideoHashtag
INSERT INTO `video_hashtag`(video_id, hashtag_id)
VALUES  (X'37b32dc2b0e045ab84691ad89a90b978', X'c7e8a20e70164d6f9a63b4c2268a0c02'),
        (X'e65707b4e9dc4d409a1d72667570bd6f', X'c7e8a20e70164d6f9a63b4c2268a0c02'),
        (X'e65707b4e9dc4d409a1d72667570bd6f', X'88cd13bd559b4f91b9e15aca57cc3024');

# VideoThumbnail
INSERT INTO `video_thumbnail`(video_id, thumbnail_id)
VALUES  (X'37b32dc2b0e045ab84691ad89a90b978', X'b28257049f6443cababe780f40d0e8f1'),
        (X'37b32dc2b0e045ab84691ad89a90b978', X'2c48d7cd0c0f449ab91c4c2ca083d227'),
        (X'f7d9b74b750c4f4983405bcb8450ae14', X'1243853b465c44179eb167f5b191c381'),
        (X'e65707b4e9dc4d409a1d72667570bd6f', X'78a1b2d45bad4b74893750d4d0b6e6a5');

# ViewHistory
INSERT INTO `view_history`(id, created_by, created_date, modified_by, modified_date, published_at, viewed_duration_sec, user_id, video_id)
VALUES  (X'12b63121ed4643a887f2d21c9fc0555c', 'admin', NOW(), 'admin', NOW(), NOW(), 50, X'3f06af63a93c11e4979700505690773f', X'37b32dc2b0e045ab84691ad89a90b978'),
        (X'bc3b65e4ec3a4e8ea900935801e0197c', 'admin', NOW(), 'admin', NOW(), NOW(), 60, X'3f06af63a93c11e4979700505690773f', X'37b32dc2b0e045ab84691ad89a90b978'),
        (X'9fec922c50dd40d49f9473d5378f2369', 'admin', NOW(), 'admin', NOW(), NOW(), 30, X'3f06af63a93c11e4979700505690773f', X'37b32dc2b0e045ab84691ad89a90b978'),
        (X'f52911f246d946fcb1db04e109f2031b', 'admin', NOW(), 'admin', NOW(), NOW(), 50, X'3f06af63a93c11e4979700505690773f', X'f7d9b74b750c4f4983405bcb8450ae14'),
        (X'ecb448c3da804b8dbc37687cf9e34709', 'admin', NOW(), 'admin', NOW(), NOW(), 50, X'3f06af63a93c11e4979700505690773f', X'f7d9b74b750c4f4983405bcb8450ae14'),
        (X'4fa7564f2c09444991b6677f586423e7', 'admin', NOW(), 'admin', NOW(), NOW(), 75, X'3f06af63a93c11e4979700505690773f', X'e65707b4e9dc4d409a1d72667570bd6f'),
        (X'd8a960e98f184781a2c9e4ef87b2640b', 'admin', NOW(), 'admin', NOW(), NOW(), 50, X'a05990b1911040b1aa4c03951b0705de', X'37b32dc2b0e045ab84691ad89a90b978'),
        (X'9e277bd6518a4ab484f8cfaddbba5dd5', 'admin', NOW(), 'admin', NOW(), NOW(), 40, X'a05990b1911040b1aa4c03951b0705de', X'f7d9b74b750c4f4983405bcb8450ae14'),
        (X'ea2e81a6d4b8488fbdb11f76d16cde8f', 'admin', NOW(), 'admin', NOW(), NOW(), 60, X'a05990b1911040b1aa4c03951b0705de', X'e65707b4e9dc4d409a1d72667570bd6f');

# VideoRating
INSERT INTO `video_rating`(id, created_by, created_date, modified_by, modified_date, published_at, rating, video_id, user_id)
VALUES  (X'56a0f368e1644cf793eb0db7a3737ed2', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', X'37b32dc2b0e045ab84691ad89a90b978', X'3f06af63a93c11e4979700505690773f'),
        (X'd7e57cc4393b41bc84ca293ae080fbf4', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', X'f7d9b74b750c4f4983405bcb8450ae14', X'3f06af63a93c11e4979700505690773f'),
        (X'ce4eec6859d14124be614176b5e16ee8', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', X'37b32dc2b0e045ab84691ad89a90b978', X'a05990b1911040b1aa4c03951b0705de'),
        (X'e2c3f28dea294758b593696f789ecea4', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', X'f7d9b74b750c4f4983405bcb8450ae14', X'a05990b1911040b1aa4c03951b0705de');

# Comment
INSERT INTO `comment`(id, created_by, created_date, modified_by, modified_date, published_at, text, updated_at, parent_id, user_id, video_id)
VALUES  (X'6c3239d65b33461a88dd1e2150fb0324', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, X'3f06af63a93c11e4979700505690773f', X'37b32dc2b0e045ab84691ad89a90b978'),
        (X'6b342e72e278482ab1e8c66a142b40ca', 'admin', NOW(), 'admin', NOW(), NOW(), 'Reply', null, X'6c3239d65b33461a88dd1e2150fb0324', X'a05990b1911040b1aa4c03951b0705de', X'37b32dc2b0e045ab84691ad89a90b978'),
        (X'0feb708a636749a48cfc33e3706cd82c', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, X'3f06af63a93c11e4979700505690773f', X'f7d9b74b750c4f4983405bcb8450ae14'),
        (X'24e257fa872d4f669eedb6031bf92d80', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, X'a05990b1911040b1aa4c03951b0705de', X'f7d9b74b750c4f4983405bcb8450ae14'),
        (X'3c9a264ed5f34d929171c450534b7746', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, X'3f06af63a93c11e4979700505690773f', X'e65707b4e9dc4d409a1d72667570bd6f'),
        (X'3a1e053952db4d70b129a2390d0e4fb4', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, X'a05990b1911040b1aa4c03951b0705de', X'e65707b4e9dc4d409a1d72667570bd6f');

# CommentRating
INSERT INTO `comment_rating`(id, created_by, created_date, modified_by, modified_date, published_at, rating, comment_id, user_id)
VALUES  (X'33921ebbae2b433aa50e06ee7f9ad889', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', X'6c3239d65b33461a88dd1e2150fb0324', X'a05990b1911040b1aa4c03951b0705de'),
        (X'85baf0bb24b14970ba24918cc45594ad', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', X'24e257fa872d4f669eedb6031bf92d80', X'3f06af63a93c11e4979700505690773f'),
        (X'def0075f6f354601bebb94b6eaeeb9fa', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', X'3a1e053952db4d70b129a2390d0e4fb4', X'3f06af63a93c11e4979700505690773f');
