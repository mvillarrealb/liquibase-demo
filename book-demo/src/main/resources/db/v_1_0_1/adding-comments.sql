-- adding-comments.sql
COMMENT ON COLUMN author.author_name IS 'Author name';
COMMENT ON COLUMN author.author_id IS 'Author numeric identifier';

COMMENT ON COLUMN book.book_id IS 'Book numeric identifier';
COMMENT ON COLUMN book.book_isbn IS 'Book International Standard Book Number';
COMMENT ON COLUMN book.book_name IS 'Book name';
COMMENT ON COLUMN book.author_id IS 'Author numeric identifier(author reference)';