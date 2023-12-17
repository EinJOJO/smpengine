CREATE TABLE `spieler`
(
    `uuid`       VARCHAR(36) PRIMARY KEY NOT NULL,
    `name`       VARCHAR(16)             NOT NULL,
    `team_id`    INT                     NULL,
    `online`     BOOLEAN                 NOT NULL DEFAULT FALSE,
    `first_join` TIMESTAMP               NULL,
    `last_join`  TIMESTAMP               NULL
);

CREATE TABLE `team`
(
    `id`          int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `name`        varchar(32)     NOT NULL UNIQUE,
    `displayName` text            NOT NULL,
    `owner_uuid`  varchar(36)     NOT NULL,
    `created_at`  TIMESTAMP       NULL
);

CREATE TABLE `sessions`
(
    `id`                 int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `session_owner_uuid` varchar(36)     NOT NULL,
    `ip_address`         varchar(255)    NOT NULL,
    `login_at`           TIMESTAMP       NULL,
    `logout_at`          TIMESTAMP       NULL
);

CREATE TABLE `stats`
(
    `session_id`      int PRIMARY KEY NOT NULL,
    `player_uuid`     varchar(36)     NOT NULL,
    `blocksDestroyed` int DEFAULT 0,
    `mobKills`        int DEFAULT 0,
    `playerKills`     int DEFAULT 0,
    `deaths`          int DEFAULT 0,
    `villagerTrades`  int DEFAULT 0
);


ALTER TABLE `spieler`
    ADD FOREIGN KEY (`team_id`) REFERENCES `team` (`id`);

ALTER TABLE `team`
    ADD FOREIGN KEY (`owner_uuid`) REFERENCES `spieler` (`uuid`);

ALTER TABLE `stats`
    ADD FOREIGN KEY (`player_uuid`) REFERENCES `spieler` (`uuid`);

ALTER TABLE `sessions`
    ADD FOREIGN KEY (`id`) REFERENCES `stats` (`session_id`);

ALTER TABLE `sessions`
    ADD FOREIGN KEY (`session_owner_uuid`) REFERENCES `spieler` (`uuid`);
ALTER TABLE sessions ADD INDEX (session_owner_uuid);