
ALTER TABLE book ADD COLUMN book_cover TEXT;
ALTER TABLE book ADD COLUMN book_year INT;

COMMENT ON COLUMN book.book_cover IS 'URL for the cover file';
COMMENT ON COLUMN book.book_year IS 'Year of the book';

