DROP TABLE IF EXISTS VINEYARD;
DROP TABLE IF EXISTS WINE;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS DRINKS;
PRAGMA foreign_keys = OFF;

--SPLIT
CREATE TABLE IF NOT EXISTS VINEYARD (
    id INTEGER PRIMARY KEY,
    name TEXT UNIQUE,
    region TEXT,
    latitude NUMERIC,
    longitude NUMERIC
);
--SPLIT
CREATE TABLE IF NOT EXISTS WINE (
    id INTEGER PRIMARY KEY,
    name TEXT UNIQUE,
    year INTEGER,
    variety TEXT,
    rating INTEGER,
    price NUMERIC,
    colour TEXT,
    vineyard INTEGER REFERENCES VINEYARD(id),
    description TEXT
);
--SPLIT
CREATE TABLE IF NOT EXISTS USER (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE,
    password TEXT,
    role TEXT,
    icon INTEGER
);
--SPLIT
CREATE TABLE IF NOT EXISTS DRINKS (
    wineid INTEGER
        REFERENCES WINE(id)
            ON DELETE CASCADE,

    userid INTEGER
        REFERENCES USER(id)
            ON DELETE CASCADE,

    favorite BOOLEAN,
    notes TEXT,
    rating INTEGER,
    PRIMARY KEY (wineid, userid)
);
--SPLIT
PRAGMA foreign_keys = ON;