CREATE TABLE book(
  book_id BIGINT NOT NULL,
  author_name VARCHAR(200) NOT NULL,
  book_isbn VARCHAR(200) NOT NULL,
  book_name TEXT NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  PRIMARY KEY(book_id)
);

CREATE TABLE book_store(
    store_id BIGSERIAL,
    store_name VARCHAR(200) NOT NULL,
    store_address TEXT  NOT NULL,
    store_business_hours JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    PRIMARY KEY(store_id),
    CONSTRAINT unq_store_name UNIQUE(store_name)
);

CREATE TABLE book_store_stock(
    store_id BIGINT NOT NULL,
    book_id BIGINT  NOT NULL,
    book_stock BIGINT  NOT NULL,
    book_price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    PRIMARY KEY(store_id, book_id),
    FOREIGN KEY(book_id) REFERENCES book(book_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY(store_id) REFERENCES book_store(store_id) ON UPDATE CASCADE ON DELETE RESTRICT
);