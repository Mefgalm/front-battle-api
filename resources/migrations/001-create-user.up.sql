CREATE TABLE users (
    id bigserial PRIMARY KEY,
    email varchar(255) NOT NULL,
    "password" varchar(255) NOT NULL,
    password_question varchar(255) NOT NULL
);
CREATE UNIQUE INDEX users_email_index ON users USING btree (email);