CREATE TABLE IF NOT EXISTS BOOKS_TBL (
    book_id INT NOT NULL AUTO_INCREMENT,
    author_id INT NOT NULL,
    title varchar(128) NOT NULL,
    update_date TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_date timestamp NOT NULL default CURRENT_TIMESTAMP,
    PRIMARY KEY (book_id)
);

CREATE TABLE IF NOT EXISTS AUTHOR_TBL (
    author_id INT NOT NULL AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    country varchar(32) NOT NULL,
    PRIMARY KEY (author_id)
);

INSERT INTO `bookshelf`.`BOOKS_TBL` (`book_id`, `author_id`, `title`) VALUES (1, 1, 'Hobbit');
INSERT INTO `bookshelf`.`BOOKS_TBL` (`book_id`, `author_id`, `title`) VALUES (2, 1, 'Lord of the Rings');
INSERT INTO `bookshelf`.`BOOKS_TBL` (`book_id`, `author_id`, `title`) VALUES (3, 2, 'Iliad');
INSERT INTO `bookshelf`.`BOOKS_TBL` (`book_id`, `author_id`, `title`) VALUES (4, 3, 'Ana Karenina');
INSERT INTO `bookshelf`.`BOOKS_TBL` (`book_id`, `author_id`, `title`) VALUES (5, 4, 'Harry Potter');
INSERT INTO `bookshelf`.`BOOKS_TBL` (`book_id`, `author_id`, `title`) VALUES (6, 5, '羅生門');

INSERT INTO `bookshelf`.`AUTHOR_TBL` (`author_id`, `name`, `country`) VALUES (1, 'Tolkien', 'UK');
INSERT INTO `bookshelf`.`AUTHOR_TBL` (`author_id`, `name`, `country`) VALUES (2, 'Homer', 'Greece');
INSERT INTO `bookshelf`.`AUTHOR_TBL` (`author_id`, `name`, `country`) VALUES (3, 'Tolstoi', 'Russia');
INSERT INTO `bookshelf`.`AUTHOR_TBL` (`author_id`, `name`, `country`) VALUES (4, 'Rowling', 'UK');
INSERT INTO `bookshelf`.`AUTHOR_TBL` (`author_id`, `name`, `country`) VALUES (5, '芥川龍之介', 'Japan');
