CREATE TABLE states_pp_generation
(
    id                    SERIAL PRIMARY KEY,
    state                 VARCHAR2(100),
    annual_net_generation LONG,
    percentage            DOUBLE
);
CREATE INDEX state_pp_generation_state_idx ON states_pp_generation (state);


CREATE TABLE power_plants
(
    id                    SERIAL PRIMARY KEY,
    state                 VARCHAR2(100),
    name                  VARCHAR2(100),
    longitude             DOUBLE,
    latitude              DOUBLE,
    annual_net_generation LONG
)
