CREATE TABLE sport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE player (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    team_id BIGINT,
    sport_id BIGINT,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (sport_id) REFERENCES sport(id)
);

CREATE TABLE season (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE variant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE card_manufacturer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE card_brand (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    manufacturer_id BIGINT,
    FOREIGN KEY (manufacturer_id) REFERENCES card_manufacturer(id)
);

CREATE TABLE card_theme (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand_id BIGINT,
    FOREIGN KEY (brand_id) REFERENCES card_brand(id)
);

CREATE TABLE card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    print_run INT NOT NULL,
    serial_number INT NOT NULL,
    season_id BIGINT,
    number VARCHAR(255) NOT NULL,
    rookie_card BOOLEAN NOT NULL,
    game_used_material BOOLEAN NOT NULL,
    autograph BOOLEAN NOT NULL,
    player_id BIGINT,
    variant_id BIGINT,
    theme_id BIGINT,
    FOREIGN KEY (season_id) REFERENCES season(id),
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (variant_id) REFERENCES variant(id),
    FOREIGN KEY (theme_id) REFERENCES card_theme(id)
);
