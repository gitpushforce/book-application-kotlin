package com.application.books.infrastructure.repository

import com.application.books.domain.AuthorInsert
import com.application.books.domain.AuthorUpdate
import com.application.books.domain.BookInsert
import com.application.books.domain.BookUpdate
import com.application.books.dto.BookInfoDto
import com.application.books.dto.WorkInfo

interface BooksRepository {
    fun findAll(): List<BookInfoDto>
    fun findBookById(bookId: Int): BookInfoDto?
    fun findAuthorNameById(authorId: Int): String?
    fun findAuthorById(authorId: Int): Map<String, String>?
    fun findBooksByAuthorId(authorId: Int): List<WorkInfo>
    fun getBookBeforeUpdate(bookId: Int): Map<String, Any>?

    fun insertAuthor(author: AuthorInsert): Int
    fun insertBook(book: BookInsert): Int
    fun updateAuthor(author: AuthorUpdate): Int
    fun updateBook(book: BookUpdate): Int
}