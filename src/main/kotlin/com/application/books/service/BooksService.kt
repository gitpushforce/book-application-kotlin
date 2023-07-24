package com.application.books.service

import com.application.books.domain.AuthorInsert
import com.application.books.domain.AuthorUpdate
import com.application.books.domain.BookInsert
import com.application.books.domain.BookUpdate
import com.application.books.dto.AuthorInfoDto
import com.application.books.dto.BookInfoDto
import com.application.books.dto.InsUpdDto
import com.application.books.infrastructure.repository.BooksRepository
import org.springframework.stereotype.Service

@Service
class BooksService(private val repo: BooksRepository) {

    companion object {
        const val SUCCESS = 1
    }

    fun findAll(): List<BookInfoDto> = repo.findAll()

    fun findBookById(bookId: Int): BookInfoDto {
        val repoResult = repo.findBookById(bookId)
        return repoResult ?: BookInfoDto(bookId)
    }

    fun findAuthorById(authorId: Int): AuthorInfoDto {
        val authorBasicInfo = repo.findAuthorById(authorId)
        authorBasicInfo?.let {
            val books = repo.findBooksByAuthorId(authorId)
            return AuthorInfoDto(
                authorId,
                authorBasicInfo.getValue("name"),
                authorBasicInfo.getValue("country"),
                books)
        }
        return AuthorInfoDto(authorId)
    }

    fun insertAuthor(author: AuthorInsert): InsUpdDto {
        val insert = repo.insertAuthor(author)
        if (insert == SUCCESS) return InsUpdDto("登録に成功しました。", true)
        return InsUpdDto("登録に失敗しました。", false)
    }

    fun insertBook(book: BookInsert): InsUpdDto {
        val authorName = repo.findAuthorNameById(book.authorId)
        authorName?.let {
            val insert = repo.insertBook(book)
            if (insert == SUCCESS) return InsUpdDto("登録に成功しました。", true)
            return InsUpdDto("登録に失敗しました。", false)
        }
        return InsUpdDto("著者がDBに存在しないため本籍が登録できませんでした、先に著者を登録してください。", false)
    }

    fun updateAuthor(author: AuthorUpdate): InsUpdDto {
        val update = repo.updateAuthor(author)
        if (update == SUCCESS) return InsUpdDto("更新に成功しました。", true)
        return InsUpdDto("更新に失敗しました。", false)
    }

    fun updateBook(book: BookUpdate): InsUpdDto {
        val authorName = repo.findAuthorNameById(book.authorId)
        authorName?.let {
            val update = repo.updateBook(book)
            if (update == SUCCESS) return InsUpdDto("更新に成功しました。", true)
            return InsUpdDto("更新に失敗しました。", false)
        }
        return InsUpdDto("DBに存在しない著者で本籍の情報を更新することができません、先に著者を登録してください。", false)
    }
}