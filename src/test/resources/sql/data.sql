# Privacy
INSERT INTO `privacy`(id, created_by, created_date, modified_by, modified_date, status)
VALUES ('ec386a4b', 'admin', NOW(), 'admin', NOW(), 'PRIVATE'),
       ('f01121d2', 'admin', NOW(), 'admin', NOW(), 'PUBLIC');

# Thumbnail
INSERT INTO `thumbnail`(id, created_by, created_date, modified_by, modified_date, type, url, width, height)
VALUES ('7e90877f', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Admin default thumbnail URL', 100, 100),
       ('f3e78681', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 1 default thumbnail URL', 100, 100),
       ('a8cfd1fb', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', 'User 1 medium thumbnail URL', 200, 200),
       ('a31aba86', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 2 default thumbnail URL', 100, 100),
       ('15de2ef4', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'User 3 default thumbnail URL', 100, 100),

       ('b2825704', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 1 default thumbnail URL', 720, 450),
       ('2c48d7cd', 'admin', NOW(), 'admin', NOW(), 'MEDIUM', 'Video 1 medium thumbnail URL', 1024, 720),
       ('1243853b', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 2 default thumbnail URL', 720, 450),
       ('78a1b2d4', 'admin', NOW(), 'admin', NOW(), 'DEFAULT', 'Video 3 default thumbnail URL', 720, 450);

# Role
INSERT INTO `role`(id, created_by, created_date, modified_by, modified_date, name)
VALUES ('319a7b41', 'admin', NOW(), 'admin', NOW(), 'ADMIN'),
       ('72ca9394', 'admin', NOW(), 'admin', NOW(), 'USER');

# User
# admin(username: admin, password: 00000000)
# user1(username: user1, password: 11111111)
# user2(username: user2, password: 22222222)
# user3(username: user3, password: 33333333)
INSERT INTO `user`(id, created_by, created_date, modified_by, modified_date, country, date_of_birth, email, gender,
                   password, phone_number, username, bio, published_at)
VALUES ('3f06af63', 'admin', NOW(), 'admin', NOW(), null, null, null, null,
        '$2a$10$aXiYw1962itW0ZHtWcEit.mUJZqUL6r9jHJy3ZeeiWuyHJ6YCzPwe', null, 'admin', null, NOW()),
       ('a05990b1', 'admin', NOW(), 'admin', NOW(), null, null, null, null,
        '$2a$10$Y4tJpNDAZbt5micTfwkx2uqU1f3EMy9/tbyz43GaNInzFXGQqDhle', null, 'user1', null, NOW()),
       ('9b79f4ba', 'admin', NOW(), 'admin', NOW(), null, null, null, null,
        '$2a$10$EfQYDzvm3xOpKXlKIyrE4uIY1NIevCE8Id5jro5K05quqeKaFhBpG', null, 'user2', null, NOW()),
       ('d540fce2', 'admin', NOW(), 'admin', NOW(), null, null, null, null,
        '$2a$10$T3nG/IZLGJUPpCf0nyrOcOivCggdil7XdTYnxzBykFt0QXJv39J2q', null, 'user3', null, NOW());

# FcmMessageToken
INSERT INTO `fcm_message_token`(id, timestamp, token, user_id, created_by, created_date, modified_by, modified_date)
VALUES ('01a14281', NOW(), '838fdc717a5480e3', 'a05990b1', 'admin', NOW(), 'admin', NOW()),
       ('783d890e', NOW(), 'b369be84eae3622c', 'a05990b1', 'admin', NOW(), 'admin', NOW());

# UserRole
INSERT INTO `user_role`(user_id, role_id)
VALUES ('3f06af63', '319a7b41'),
       ('a05990b1', '72ca9394'),
       ('9b79f4ba', '72ca9394'),
       ('d540fce2', '72ca9394');

# UserThumbnail
INSERT INTO `user_thumbnail`(user_id, thumbnail_id)
VALUES ('3f06af63', '7e90877f'),
       ('a05990b1', 'f3e78681'),
       ('a05990b1', 'a8cfd1fb'),
       ('9b79f4ba', 'a31aba86'),
       ('d540fce2', '15de2ef4');

# Follow
INSERT INTO `follow`(id, created_by, created_date, modified_by, modified_date, published_at, follower_id, user_id)
VALUES ('f2cf8a48', 'admin', NOW(), 'admin', NOW(), NOW(), 'a05990b1', '9b79f4ba'),
       ('52f031a8', 'admin', NOW(), 'admin', NOW(), NOW(), 'd540fce2', '9b79f4ba');

# Hashtag
INSERT INTO `hashtag`(id, created_by, created_date, modified_by, modified_date, tag)
VALUES ('c7e8a20e', 'admin', NOW(), 'admin', NOW(), 'music'),
       ('88cd13bd', 'admin', NOW(), 'admin', NOW(), 'sport');

# Category
INSERT INTO `category`(id, created_by, created_date, modified_by, modified_date, category)
VALUES ('a82f3e3d', 'admin', NOW(), 'admin', NOW(), 'Autos & Vehicles'),
       ('f2fe0cb6', 'admin', NOW(), 'admin', NOW(), 'Comedy'),
       ('c0f3f41e', 'admin', NOW(), 'admin', NOW(), 'Sports'),
       ('eb0568d3', 'admin', NOW(), 'admin', NOW(), 'Travel & Events');

# Video
INSERT INTO `video`(id, created_by, created_date, modified_by, modified_date, description, duration_sec, age_restricted,
                    comment_allowed, made_for_kids, category_id, location, title, published_at, video_url, user_id,
                    privacy_id)
VALUES ('37b32dc2', 'admin', NOW(), 'admin', NOW(), 'Video 1 description', 1000, false, true, false, 'a82f3e3d',
        'New York, US', 'Video 1', '2024-04-01T09:00:00', 'Video 1 URL', 'a05990b1', 'ec386a4b'),
       ('f7d9b74b', 'admin', NOW(), 'admin', NOW(), 'Video 2 description', 2000, false, true, false, 'f2fe0cb6',
        'Ha Noi, Vietnam', 'Video 2', '2024-04-02T09:00:00', 'Video 2 URL', 'a05990b1', 'f01121d2'),
       ('e65707b4', 'admin', NOW(), 'admin', NOW(), 'Video 3 description', 3000, false, false, true, 'c0f3f41e',
        'Tokyo, Japan', 'Video 3', '2024-04-03T09:00:00', 'Video 3 URL', '9b79f4ba', 'f01121d2');

# VideoStatistic
INSERT INTO `video_statistic`(created_by, created_date, modified_by, modified_date, view_count, like_count,
                              dislike_count, comment_count, download_count, video_id)
VALUES ('admin', NOW(), 'admin', NOW(), 4, 2, 0, 1, 0, '37b32dc2'),
       ('admin', NOW(), 'admin', NOW(), 3, 0, 1, 0, 0, 'f7d9b74b'),
       ('admin', NOW(), 'admin', NOW(), 2, 0, 1, 2, 0, 'e65707b4');

# VideoHashtag
INSERT INTO `video_hashtag`(video_id, hashtag_id)
VALUES ('37b32dc2', 'c7e8a20e'),
       ('e65707b4', 'c7e8a20e'),
       ('e65707b4', '88cd13bd');

# VideoThumbnail
INSERT INTO `video_thumbnail`(video_id, thumbnail_id)
VALUES ('37b32dc2', 'b2825704'),
       ('37b32dc2', '2c48d7cd'),
       ('f7d9b74b', '1243853b'),
       ('e65707b4', '78a1b2d4');

# ViewHistory
INSERT INTO `view_history`(id, created_by, created_date, modified_by, modified_date, published_at, viewed_duration_sec,
                           user_id, video_id)
VALUES ('12b63121', 'admin', NOW(), 'admin', NOW(), NOW(), 50, 'a05990b1', '37b32dc2'),
       ('bc3b65e4', 'admin', NOW(), 'admin', NOW(), NOW(), 60, 'a05990b1', '37b32dc2'),
       ('9fec922c', 'admin', NOW(), 'admin', NOW(), NOW(), 30, 'a05990b1', '37b32dc2'),
       ('f52911f2', 'admin', NOW(), 'admin', NOW(), NOW(), 50, 'a05990b1', 'f7d9b74b'),
       ('ecb448c3', 'admin', NOW(), 'admin', NOW(), NOW(), 50, 'a05990b1', 'f7d9b74b'),
       ('4fa7564f', 'admin', NOW(), 'admin', NOW(), NOW(), 75, 'a05990b1', 'e65707b4'),
       ('d8a960e9', 'admin', NOW(), 'admin', NOW(), NOW(), 50, '9b79f4ba', '37b32dc2'),
       ('9e277bd6', 'admin', NOW(), 'admin', NOW(), NOW(), 40, '9b79f4ba', 'f7d9b74b'),
       ('ea2e81a6', 'admin', NOW(), 'admin', NOW(), NOW(), 60, '9b79f4ba', 'e65707b4');

# VideoRating
INSERT INTO `video_rating`(id, created_by, created_date, modified_by, modified_date, published_at, rating, video_id,
                           user_id)
VALUES ('56a0f368', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '37b32dc2', 'a05990b1'),
       ('d7e57cc4', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', 'e65707b4', 'a05990b1'),
       ('ce4eec68', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '37b32dc2', '9b79f4ba'),
       ('e2c3f28d', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', 'f7d9b74b', '9b79f4ba');

# Comment
INSERT INTO `comment`(id, created_by, created_date, modified_by, modified_date, published_at, text, updated_at,
                      parent_id, user_id, video_id)
VALUES ('6c3239d6', 'admin', NOW(), 'admin', NOW(), NOW(), '[user1] Good video', null, null, 'a05990b1', '37b32dc2'),
       ('3a1e0539', 'admin', NOW(), 'admin', NOW(), NOW(), '[user1] Good video', null, null, 'a05990b1', 'e65707b4'),
       ('6b342e72', 'admin', NOW(), 'admin', NOW(), NOW(), '[user2] Reply', null, '3a1e0539', '9b79f4ba', 'e65707b4');

# CommentRating
INSERT INTO `comment_rating`(id, created_by, created_date, modified_by, modified_date, published_at, rating, comment_id,
                             user_id)
VALUES ('85baf0bb', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '6b342e72', 'a05990b1'),
       ('def0075f', 'admin', NOW(), 'admin', NOW(), NOW(), 'LIKE', '3a1e0539', '9b79f4ba'),
       ('def0075f', 'admin', NOW(), 'admin', NOW(), NOW(), 'DISLIKE', '6c3239d6', 'a05990b1');

# Playlist
INSERT INTO `playlist`(id, created_by, created_date, modified_by, modified_date, description, default_type,
                       published_at, title, privacy_id, user_id)
VALUES ('c31760ea', 'admin', NOW(), 'admin', NOW(), null, 1, NOW(), '', 'ec386a4b', 'a05990b1'),
       ('fae06c8a', 'admin', NOW(), 'admin', NOW(), null, 0, NOW(), '', 'ec386a4b', 'a05990b1'),
       ('d8659362', 'admin', NOW(), 'admin', NOW(), null, null, NOW(), 'My Videos', 'f01121d2', 'a05990b1'),

       ('236e2aa6', 'admin', NOW(), 'admin', NOW(), null, 0, NOW(), '', 'ec386a4b', '9b79f4ba'),
       ('d07f1bee', 'admin', NOW(), 'admin', NOW(), null, 1, NOW(), '', 'ec386a4b', '9b79f4ba');

# PlaylistItem
INSERT INTO `playlist_item`(created_by, created_date, modified_by, modified_date, priority, video_id, playlist_id)
VALUES ('admin', NOW(), 'admin', NOW(), 1, 'e65707b4', 'd8659362'),
       ('admin', NOW(), 'admin', NOW(), 0, 'f7d9b74b', 'd8659362'),
       ('admin', NOW(), 'admin', NOW(), 0, '37b32dc2', '236e2aa6');

# NotificationObject
INSERT INTO `notification_object`(id, action_type, message, object_id, object_type, published_at, created_by,
                                  created_date, modified_by, modified_date)
VALUES ('77a70703', 1, 'user2 uploaded: Video 3', 'e65707b4', 'VIDEO', NOW(), 'admin', NOW(), 'admin', NOW()),
       ('c63edb2c', 2, 'user1 has followed you', 'f2cf8a48', 'FOLLOW', NOW(), 'admin', NOW(), 'admin', NOW());

# Notification
INSERT INTO `notification`(id, is_read, is_seen, actor_id, notification_object_id, recipient_id, created_by,
                           created_date, modified_by, modified_date)
VALUES ('856c89bc', false, false, '9b79f4ba', '77a70703', 'a05990b1', 'admin', NOW(), 'admin', NOW()),
       ('652ef2c2', false, false, 'a05990b1', 'c63edb2c', '9b79f4ba', 'admin', NOW(), 'admin', NOW());