package com.application.books.infrastructure.repository

import com.application.books.domain.AuthorInsert
import com.application.books.domain.AuthorUpdate
import com.application.books.domain.BookInsert
import com.application.books.domain.BookUpdate
import com.application.books.dto.BookInfoDto
import com.application.books.dto.WorkInfo
import com.application.books.infrastructure.repository.bookshelf.Tables.AUTHOR_TBL
import com.application.books.infrastructure.repository.bookshelf.Tables.BOOKS_TBL
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class BooksRepositoryImpl(private val dslContext: DSLContext): BooksRepository {
    override fun findAll(): List<BookInfoDto> {
        return this.dslContext.select()
            .from(BOOKS_TBL)
            .join(AUTHOR_TBL).on(BOOKS_TBL.AUTHOR_ID.eq(AUTHOR_TBL.AUTHOR_ID))
            .fetch().map { toModel(it) }
    }

    override fun findBookById(bookId: Int): BookInfoDto? {
        return this.dslContext.select()
            .from(BOOKS_TBL)
            .join(AUTHOR_TBL).on(BOOKS_TBL.AUTHOR_ID.eq(AUTHOR_TBL.AUTHOR_ID))
            .where(BOOKS_TBL.BOOK_ID.eq(bookId))
            .fetchOne()?.let { toModel(it) }
    }

    override fun findAuthorNameById(authorId: Int): String? {
        return this.dslContext.select()
            .from(AUTHOR_TBL)
            .where(AUTHOR_TBL.AUTHOR_ID.eq(authorId))
            .fetchOne(AUTHOR_TBL.NAME)
    }

    override fun findAuthorById(authorId: Int): Map<String, String>? {
        return this.dslContext.select()
            .from(AUTHOR_TBL)
            .where(AUTHOR_TBL.AUTHOR_ID.eq(authorId))
            .fetchOne()?.map { record ->
                return@map mapOf(
                    "name" to record.getValue(AUTHOR_TBL.NAME),
                    "country" to record.getValue(AUTHOR_TBL.COUNTRY))
            }
    }

    override fun findBooksByAuthorId(authorId: Int): List<WorkInfo> {
        return this.dslContext.select(BOOKS_TBL.BOOK_ID, BOOKS_TBL.TITLE)
            .from(BOOKS_TBL)
            .where(BOOKS_TBL.AUTHOR_ID.eq(authorId))
            .fetch().map { record ->
                WorkInfo(
                    record.getValue(BOOKS_TBL.BOOK_ID),
                    record.getValue(BOOKS_TBL.TITLE))
            }
    }

    override fun getBookBeforeUpdate(bookId: Int): Map<String, Any>? {
        return this.dslContext.select()
            .from(BOOKS_TBL)
            .where(BOOKS_TBL.BOOK_ID.eq(bookId))
            .fetchOne()?.map { record ->
                return@map mapOf(
                    "authorId" to record.getValue(BOOKS_TBL.AUTHOR_ID),
                    "title" to record.getValue(BOOKS_TBL.TITLE))
            }
    }

    override fun insertAuthor(author: AuthorInsert): Int {
        return dslContext.insertInto(AUTHOR_TBL, AUTHOR_TBL.NAME, AUTHOR_TBL.COUNTRY)
            .values(author.name, author.country).execute()
    }

    override fun insertBook(book: BookInsert): Int {
        return dslContext.insertInto(BOOKS_TBL, BOOKS_TBL.TITLE, BOOKS_TBL.AUTHOR_ID)
            .values(book.title, book.authorId).execute()
    }

    override fun updateAuthor(author: AuthorUpdate): Int {
        return dslContext.update(AUTHOR_TBL)
            .set(AUTHOR_TBL.NAME, author.name)
            .set(AUTHOR_TBL.COUNTRY, author.country)
            .where(AUTHOR_TBL.AUTHOR_ID.eq(author.authorId))
            .execute()
    }

    override fun updateBook(book: BookUpdate): Int {
        return dslContext.update(BOOKS_TBL)
            .set(BOOKS_TBL.TITLE, book.title)
            .set(BOOKS_TBL.AUTHOR_ID, book.authorId)
            .where(BOOKS_TBL.BOOK_ID.eq(book.bookId))
            .execute()
    }

    private fun toModel(record: Record) = BookInfoDto(
        record.getValue(BOOKS_TBL.BOOK_ID),
        record.getValue(AUTHOR_TBL.NAME),
        record.getValue(BOOKS_TBL.TITLE)
    )
}
