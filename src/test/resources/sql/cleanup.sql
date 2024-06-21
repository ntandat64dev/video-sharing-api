SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `category`;
TRUNCATE TABLE `comment`;
TRUNCATE TABLE `comment_rating`;
TRUNCATE TABLE `fcm_message_token`;
TRUNCATE TABLE `hashtag`;
TRUNCATE TABLE `notification`;
TRUNCATE TABLE `notification_object`;
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
TRUNCATE TABLE `role`;
TRUNCATE TABLE `user_role`;

SET FOREIGN_KEY_CHECKS = 1;