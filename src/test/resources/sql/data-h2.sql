# Privacy
INSERT INTO `privacy`(id, created_by, created_date, modified_by, modified_date, status)
VALUES  ('ec386a4b', 'admin', NOW(), 'admin', NOW(), 'PRIVATE'),
        ('f01121d2', 'admin', NOW(), 'admin', NOW(), 'PUBLIC');

# Thumbnail
INSERT INTO `thumbnail`(id, created_by, created_date, modified_by, modified_date, type, url, width, height)
VALUES  ('7e90877f', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 1 default thumbnail', 100, 100),
        ('f3e78681', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', 'User 1 medium thumbnail', 200, 200),
        ('a31aba86', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 2 default thumbnail', 100, 100),

        ('b2825704', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 1 default thumbnail', 720, 450),
        ('2c48d7cd', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', 'Video 1 medium thumbnail', 1024, 720),
        ('1243853b', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 2 default thumbnail', 720, 450),
        ('78a1b2d4', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 3 default thumbnail', 720, 450);

# Role
INSERT INTO `role`(id, created_by, created_date, modified_by, modified_date, name)
VALUES ('319a7b41', 'admin', NOW(), 'admin', NOW(), 'ADMIN'),
       ('72ca9394', 'admin', NOW(), 'admin', NOW(), 'USER');

# User
# user1(username: user1, password: 11111111)
# user2(username: user2, password: 22222222)
INSERT INTO `user`(id, created_by, created_date, modified_by, modified_date, country, date_of_birth, email, gender, password, phone_number, username, bio, published_at)
VALUES  ('3f06af63', 'admin', NOW(), 'admin', NOW(), null, null, null, null, '$2a$10$713vmsvgjJiRxMhpnB3FWegmlSnY.BzKZEpWsOVs1YPQvxVp0Ef76', null, 'user1', null, NOW()),
        ('a05990b1', 'admin', NOW(), 'admin', NOW(), null, null, null, null, '$2a$10$9tcWLsdaxk50cgQiqoZNBOF/AhhSUu/imxeH8YwZRxg2i3KpVIPLG', null,  'user2', null, NOW());

# UserRole
INSERT INTO `user_role`(user_id, role_id)
VALUES ('3f06af63', '319a7b41'),
       ('a05990b1', '72ca9394');

# UserThumbnail
INSERT INTO `user_thumbnail`(user_id, thumbnail_id)
VALUES  ('3f06af63', '7e90877f'),
        ('3f06af63', 'f3e78681'),
        ('a05990b1', 'a31aba86');

# Follow
INSERT INTO `follow`(id, created_by, created_date, modified_by, modified_date, published_at, follower_id, user_id)
VALUES  ('f2cf8a48', 'admin', NOW(), 'admin', NOW(), NOW(), 'a05990b1', '3f06af63');

# Hashtag
INSERT INTO `hashtag`(id, created_by, created_date, modified_by, modified_date, tag)
VALUES  ('c7e8a20e', 'admin', NOW(), 'admin', NOW(), 'music'),
        ('88cd13bd', 'admin', NOW(), 'admin', NOW(), 'sport');

# Category
INSERT INTO `category`(id, created_by, created_date, modified_by, modified_date, category)
VALUES ('a82f3e3d', 'admin', NOW(), 'admin', NOW(), 'Autos & Vehicles'),
       ('f2fe0cb6', 'admin', NOW(), 'admin', NOW(), 'Comedy'),
       ('d073f837', 'admin', NOW(), 'admin', NOW(), 'Education'),
       ('8c1f4a20', 'admin', NOW(), 'admin', NOW(), 'Entertainment'),
       ('65e787e2', 'admin', NOW(), 'admin', NOW(), 'Film & Animation'),
       ('b18712fb', 'admin', NOW(), 'admin', NOW(), 'Gaming'),
       ('b47a3808', 'admin', NOW(), 'admin', NOW(), 'Howto & Style'),
       ('9c1e1e7d', 'admin', NOW(), 'admin', NOW(), 'Music'),
       ('e1b8b13d', 'admin', NOW(), 'admin', NOW(), 'News & Politics'),
       ('ae4c3b48', 'admin', NOW(), 'admin', NOW(), 'Nonprofits & Activism'),
       ('8fca6c8a', 'admin', NOW(), 'admin', NOW(), 'People & Blogs'),
       ('d3a616db', 'admin', NOW(), 'admin', NOW(), 'Pets & Animals'),
       ('8b63d2ed', 'admin', NOW(), 'admin', NOW(), 'Science & Technology'),
       ('c0f3f41e', 'admin', NOW(), 'admin', NOW(), 'Sports'),
       ('eb0568d3', 'admin', NOW(), 'admin', NOW(), 'Travel & Events');

# Video
INSERT INTO `video`(id, created_by, created_date, modified_by, modified_date, description, duration_sec, age_restricted, comment_allowed, made_for_kids, category_id, location, title, published_at, video_url, user_id, privacy_id)
VALUES  ('37b32dc2', 'admin', NOW(), 'admin', NOW(), 'Video 1 description', 1000, false, true, false, 'a82f3e3d' ,'New York, US', 'Video 1', '20240401T09:00', 'Video 1 video URL', '3f06af63', 'ec386a4b'),
        ('f7d9b74b', 'admin', NOW(), 'admin', NOW(), 'Video 2 description', 2000, false, true, false, 'f2fe0cb6' ,'Ha Noi, Vietnam', 'Video 2', '20240402T09:00', 'Video 2 video URL', '3f06af63', 'f01121d2'),
        ('e65707b4', 'admin', NOW(), 'admin', NOW(), 'Video 3 description', 3000, false, false, true, 'd073f837' ,'Tokyo, Japan', 'Video 3', '20240403T09:00', 'Video 3 video URL', 'a05990b1', 'f01121d2');

# VideoStatistic
INSERT INTO `video_statistic`(created_by, created_date, modified_by, modified_date, comment_count, dislike_count, download_count, like_count, view_count, video_id)
VALUES  ('admin', NOW(), 'admin', NOW(), 2, 0, 0, 2, 4, '37b32dc2'),
        ('admin', NOW(), 'admin', NOW(), 2, 1, 0, 1, 3, 'f7d9b74b'),
        ('admin', NOW(), 'admin', NOW(), 2, 0, 0, 0, 2, 'e65707b4');

# VideoHashtag
INSERT INTO `video_hashtag`(video_id, hashtag_id)
VALUES  ('37b32dc2', 'c7e8a20e'),
        ('e65707b4', 'c7e8a20e'),
        ('e65707b4', '88cd13bd');

# VideoThumbnail
INSERT INTO `video_thumbnail`(video_id, thumbnail_id)
VALUES  ('37b32dc2', 'b2825704'),
        ('37b32dc2', '2c48d7cd'),
        ('f7d9b74b', '1243853b'),
        ('e65707b4', '78a1b2d4');

# ViewHistory
INSERT INTO `view_history`(id, created_by, created_date, modified_by, modified_date, published_at, viewed_duration_sec, user_id, video_id)
VALUES  ('12b63121', 'admin', NOW(), 'admin', NOW(), NOW(), 50, '3f06af63', '37b32dc2'),
        ('bc3b65e4', 'admin', NOW(), 'admin', NOW(), NOW(), 60, '3f06af63', '37b32dc2'),
        ('9fec922c', 'admin', NOW(), 'admin', NOW(), NOW(), 30, '3f06af63', '37b32dc2'),
        ('f52911f2', 'admin', NOW(), 'admin', NOW(), NOW(), 50, '3f06af63', 'f7d9b74b'),
        ('ecb448c3', 'admin', NOW(), 'admin', NOW(), NOW(), 50, '3f06af63', 'f7d9b74b'),
        ('4fa7564f', 'admin', NOW(), 'admin', NOW(), NOW(), 75, '3f06af63', 'e65707b4'),
        ('d8a960e9', 'admin', NOW(), 'admin', NOW(), NOW(), 50, 'a05990b1', '37b32dc2'),
        ('9e277bd6', 'admin', NOW(), 'admin', NOW(), NOW(), 40, 'a05990b1', 'f7d9b74b'),
        ('ea2e81a6', 'admin', NOW(), 'admin', NOW(), NOW(), 60, 'a05990b1', 'e65707b4');

# VideoRating
INSERT INTO `video_rating`(id, created_by, created_date, modified_by, modified_date, published_at, rating, video_id, user_id)
VALUES  ('56a0f368', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '37b32dc2', '3f06af63'),
        ('d7e57cc4', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', 'f7d9b74b', '3f06af63'),
        ('ce4eec68', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '37b32dc2', 'a05990b1'),
        ('e2c3f28d', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', 'f7d9b74b', 'a05990b1');

# Comment
INSERT INTO `comment`(id, created_by, created_date, modified_by, modified_date, published_at, text, updated_at, parent_id, user_id, video_id)
VALUES  ('6c3239d6', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, '3f06af63', '37b32dc2'),
        ('6b342e72', 'admin', NOW(), 'admin', NOW(), NOW(), 'Reply', null, '6c3239d6', 'a05990b1', '37b32dc2'),
        ('0feb708a', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, '3f06af63', 'f7d9b74b'),
        ('24e257fa', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, 'a05990b1', 'f7d9b74b'),
        ('3c9a264e', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, '3f06af63', 'e65707b4'),
        ('3a1e0539', 'admin', NOW(), 'admin', NOW(), NOW(), 'Good video', null, null, 'a05990b1', 'e65707b4');

# CommentRating
INSERT INTO `comment_rating`(id, created_by, created_date, modified_by, modified_date, published_at, rating, comment_id, user_id)
VALUES  ('33921ebb', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '6c3239d6', 'a05990b1'),
        ('85baf0bb', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '24e257fa', '3f06af63'),
        ('def0075f', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', '3a1e0539', '3f06af63');
