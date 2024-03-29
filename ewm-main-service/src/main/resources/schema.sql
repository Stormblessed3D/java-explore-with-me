DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS categories CASCADE;

DROP TABLE IF EXISTS events CASCADE;

DROP TABLE IF EXISTS requests CASCADE;

DROP TABLE IF EXISTS compilations CASCADE;

DROP TABLE IF EXISTS compilation_event CASCADE;

DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users 
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create unique index if not exists USER_EMAIL_UINDEX on USERS (email);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

create unique index if not exists CATEGORY_NAME_UINDEX on CATEGORIES(name);

CREATE TABLE IF NOT EXISTS events 
(
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(category_id),
    state VARCHAR(255),
    created_on TIMESTAMP WITHOUT TIME ZONE,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    latitude FLOAT,
    longitude FLOAT,
    initiator_id BIGINT NOT NULL REFERENCES users(user_id),
    participant_limit BIGINT,
    paid BOOLEAN,
    request_moderation BOOLEAN
);

CREATE TABLE IF NOT EXISTS requests 
(
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    requester_id BIGINT NOT NULL REFERENCES users(user_id),
    status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS compilations 
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title VARCHAR(255),
    CONSTRAINT UQ_COMPILATION_TITLE UNIQUE (title)
);

create unique index if not exists COMPILATION_TITLE_UINDEX on COMPILATIONS(title);

CREATE TABLE IF NOT EXISTS compilation_event
(
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    compilation_id BIGINT NOT NULL REFERENCES compilations(compilation_id),
    PRIMARY KEY (event_id, compilation_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(500),
    event_id BIGINT REFERENCES events (event_id),
    author_id BIGINT REFERENCES users (user_id),
    created_on TIMESTAMP WITHOUT TIME ZONE,
    modified_on TIMESTAMP WITHOUT TIME ZONE
);    