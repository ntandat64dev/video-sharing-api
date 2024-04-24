SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `comment`;
TRUNCATE TABLE `comment_rating`;
TRUNCATE TABLE `hashtag`;
TRUNCATE TABLE `playlist`;
TRUNCATE TABLE `playlist_item`;
TRUNCATE TABLE `follow`;
TRUNCATE TABLE `user`;
TRUNCATE TABLE `video`;
TRUNCATE TABLE `video_hashtag`;
TRUNCATE TABLE `video_rating`;
TRUNCATE TABLE `video_statistic`;
TRUNCATE TABLE `view_history`;
TRUNCATE TABLE `user_thumbnail`;
TRUNCATE TABLE `video_thumbnail`;
TRUNCATE TABLE `thumbnail`;
TRUNCATE TABLE `privacy`;

SET FOREIGN_KEY_CHECKS = 1;