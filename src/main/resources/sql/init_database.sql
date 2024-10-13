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
    name TEXT NOT NULL,
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
CREATE TABLE IF NOT EXISTS REVIEW (
    wineid INTEGER
        REFERENCES WINE(id)
            ON DELETE CASCADE,

    userid INTEGER
        REFERENCES USER(id)
            ON DELETE CASCADE,

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
    colour INTEGER,
    UNIQUE (userid, name)
);

INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Favourite', 3);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Wish List', 4);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Dinner Party', 1);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Holiday Wines', 2);
INSERT INTO created_tags (userId, name, colour) VALUES (-1, 'Gift Wines', 0);

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