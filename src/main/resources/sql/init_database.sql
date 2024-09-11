DROP TABLE IF EXISTS REGION;
--SPLIT
CREATE TABLE IF NOT EXISTS REGION (
    name TEXT PRIMARY KEY
);
--SPLIT
DROP TABLE IF EXISTS VINEYARD;
--SPLIT
CREATE TABLE IF NOT EXISTS VINEYARD (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE,
    region TEXT REFERENCES REGION(name),
    latitude NUMERIC,
    longitude NUMERIC
);
--SPLIT
DROP TABLE IF EXISTS WINE;
--SPLIT
CREATE TABLE IF NOT EXISTS WINE (
    id INTEGER PRIMARY KEY,
    name TEXT UNIQUE,
    year INTEGER,
    variety TEXT,
    rating INTEGER,
    price NUMERIC,
    colour TEXT,
    vineyard TEXT REFERENCES VINEYARD(name),
    description TEXT
);
--SPLIT
DROP TABLE IF EXISTS USER;
--SPLIT
CREATE TABLE IF NOT EXISTS USER (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE,
    password TEXT,
    role TEXT
);
--SPLIT
DROP TABLE IF EXISTS DRINKS;
--SPLIT
CREATE TABLE IF NOT EXISTS DRINKS (
    wineid INTEGER REFERENCES WINE(id),
    user TEXT REFERENCES USER(id),
    favorite BOOLEAN,
    notes TEXT,
    rating INTEGER,
    PRIMARY KEY (wineid, user)
)
