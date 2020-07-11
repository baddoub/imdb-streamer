CREATE TABLE persons(
    id      VARCHAR(36)       NOT NULL PRIMARY KEY,
    primary_name   VARCHAR(100),
    birth_year     INT,
    death_year     INT,
    primary_profession VARCHAR,
    known_for_titles VARCHAR
);
CREATE INDEX persons_primary_name_idx ON persons (primary_name);
CREATE INDEX persons_primary_profession_idx ON persons (primary_profession);

CREATE TABLE titles(
    id               VARCHAR(36)        NOT NULL PRIMARY KEY,
    title_type       VARCHAR,
    primary_title    VARCHAR,
    original_title   VARCHAR,
    is_adult         INT,
    start_year       INT,
    end_year         INT,
    runtime_minutes   LONG,
    genres           VARCHAR
);
CREATE INDEX titles_type_idx ON titles (title_type);
CREATE TABLE ratings
(
    title_id VARCHAR(36) NOT NULL REFERENCES titles (id),
    average_rating  DOUBLE,
    num_votes INT
);
CREATE INDEX ratings_title_id_idx ON ratings (title_id);

CREATE TABLE episodes
(
    id      VARCHAR(36)      NOT NULL PRIMARY KEY,
    parent_id VARCHAR(36),
    season_number INT,
    episode_number INT
);
CREATE INDEX episodes_parent_id_idx ON episodes (parent_id);

CREATE TABLE principals
(
    title_id VARCHAR(36) NOT NULL REFERENCES titles (id),
    ordering INT,
    person_id VARCHAR(36) NOT NULL REFERENCES persons (id),
    category VARCHAR,
    job VARCHAR,
    characters VARCHAR
);
