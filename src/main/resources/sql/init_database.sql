DROP TABLE IF EXISTS VINEYARD;
DROP TABLE IF EXISTS WINE;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS REVIEW;
DROP TABLE IF EXISTS CREATED_TAGS;
DROP TABLE IF EXISTS ASSIGNED_TAGS;
PRAGMA foreign_keys = OFF;

--SPLIT
CREATE TABLE IF NOT EXISTS VINEYARD (
    id INTEGER PRIMARY KEY,
    name TEXT,
    region TEXT,
    latitude NUMERIC,
    longitude NUMERIC,
    UNIQUE (name, region)
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
    description TEXT,
    favorite BOOLEAN REFERENCES REVIEW

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
CREATE TABLE IF NOT EXISTS REVIEW (
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
CREATE TABLE IF NOT EXISTS CREATED_TAGS (
    tagid INTEGER PRIMARY KEY,
    userid INTEGER
        REFERENCES USER(id)
            ON DELETE CASCADE,
    name TEXT,
    colour INTEGER
);

INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Favourite', 3);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Wish List', 4);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Breakfast', 1);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Lunch', 2);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Dinner', 0);

--SPLIT
CREATE TABLE IF NOT EXISTS ASSIGNED_TAGS (
    assignedid INTEGER PRIMARY KEY,
    wineid INTEGER
        REFERENCES WINE(id)
            ON DELETE CASCADE,
    tagid INTEGER
        REFERENCES CREATED_TAGS(tagid)
            ON DELETE CASCADE,
    userid INTEGER
        REFERENCES USER(id)
            ON DELETE CASCADE
);
--SPLIT
PRAGMA foreign_keys = ON;