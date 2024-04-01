-- DROP TABLE

DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

---

CREATE TABLE IF NOT EXISTS users (
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available   BOOLEAN,
    owner_id    BIGINT       NOT NULL,
    CONSTRAINT FK_ITEMS_OWNER_ID FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL,
    booker_id  BIGINT                      NOT NULL,
    status     VARCHAR(64),
    CONSTRAINT FK_BOOKINGS_ITEM_ID FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT FK_BOOKINGS_BOOKER_ID FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      VARCHAR(255) NOT NULL,
    item_id   BIGINT        NOT NULL,
    author_id BIGINT        NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT FK_COMMENTS_ITEM_ID FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT FK_COMMENTS_AUTHOR_ID FOREIGN KEY (author_id) REFERENCES users (id)
);
