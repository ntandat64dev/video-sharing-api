CREATE DATABASE IF NOT EXISTS videosharing;

USE videosharing;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS video;

CREATE TABLE user
(
    id            INT AUTO_INCREMENT,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(64)  NOT NULL,
    photo_url     TEXT,
    channel_name  VARCHAR(64)  NOT NULL UNIQUE,
    created_by    VARCHAR(64),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(64),
    modified_date TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE video
(
    id            INT AUTO_INCREMENT,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    url           VARCHAR(512) NOT NULL UNIQUE,
    user_id       INT,
    created_by    VARCHAR(64),
    created_date  TIMESTAMP,
    modified_by   VARCHAR(64),
    modified_date TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

SET FOREIGN_KEY_CHECKS = 1;