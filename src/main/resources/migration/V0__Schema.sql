CREATE TABLE spieler
(
    uuid       varchar(36) PRIMARY KEY,
    team_id    int,
    online     boolean,
    first_join BIGINT,
    last_join  BIGINT
);

CREATE TABLE team
(
    id          int PRIMARY KEY,
    name        varchar(32),
    displayName text,
    owner_uuid  varchar(36),
    created_at  BIGINT
);

CREATE TABLE sessions
(
    id         int PRIMARY KEY AUTO_INCREMENT,
    uuid       varchar(36),
    ip_address varchar(255),
    login_at   BIGINT,
    logout_at  BIGINT
);

CREATE TABLE stats
(
    session         int PRIMARY KEY,
    uuid            varchar(36),
    blocksDestroyed int,
    mobKills        int,
    playerKills     int,
    deaths          int,
    villagerTrades  int
);

ALTER TABLE spieler
    ADD FOREIGN KEY (team_id) REFERENCES team (id);

ALTER TABLE spieler
    ADD FOREIGN KEY (uuid) REFERENCES team (owner_uuid);

ALTER TABLE stats
    ADD FOREIGN KEY (uuid) REFERENCES spieler (uuid);

ALTER TABLE sessions
    ADD FOREIGN KEY (id) REFERENCES stats (session);

ALTER TABLE sessions
    ADD FOREIGN KEY (id) REFERENCES spieler (uuid);
