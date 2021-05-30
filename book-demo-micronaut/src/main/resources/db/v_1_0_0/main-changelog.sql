CREATE TABLE author(
  author_id BIGSERIAL,
  author_name VARCHAR(200) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  PRIMARY KEY(author_id)
);

CREATE TABLE book(
  book_id BIGSERIAL,
  author_id BIGINT NOT NULL,
  book_isbn VARCHAR(200) NOT NULL,
  book_name TEXT NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  PRIMARY KEY(book_id),
  FOREIGN KEY(author_id)
  REFERENCES author(author_id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT,
  CONSTRAINT unq_book_isbn UNIQUE(book_isbn)
);