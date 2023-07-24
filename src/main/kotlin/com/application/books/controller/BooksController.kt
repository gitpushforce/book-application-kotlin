package com.application.books.controller

import com.application.books.domain.AuthorInsert
import com.application.books.domain.AuthorUpdate
import com.application.books.domain.BookInsert
import com.application.books.domain.BookUpdate
import com.application.books.dto.AuthorInfoDto
import com.application.books.dto.BookInfoDto
import com.application.books.dto.InsUpdDto
import com.application.books.service.BooksService
import jakarta.validation.Valid

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/v1")
class BooksController (private val service: BooksService){

    /* Spring boot 3から末尾スラッシュも書くことになりました　*/

    @GetMapping(value = ["/books", "/books/"], produces=["application/json;charset=UTF-8"])
    fun getAll(): List<BookInfoDto> = service.findAll()

    @GetMapping(value= ["/book/{bookId}", "/book/{bookId}/"], produces=["application/json;charset=UTF-8"])
    fun searchBook(@PathVariable bookId: Int): BookInfoDto = service.findBookById(bookId)

    @GetMapping(value=["/author/{authorId}", "/author/{authorId}/"], produces=["application/json;charset=UTF-8"])
    fun searchAuthor(@PathVariable authorId: Int): AuthorInfoDto = service.findAuthorById(authorId)

    @PutMapping(value= ["/create/author", "/create/author/"], produces=["application/json;charset=UTF-8"])
    fun insertAuthor(@Valid @RequestBody author: AuthorInsert): InsUpdDto = service.insertAuthor(author)

    @PutMapping(value= ["/create/book", "/create/book/"], produces=["application/json;charset=UTF-8"])
    fun insertBook(@Valid @RequestBody book: BookInsert): InsUpdDto = service.insertBook(book)

    @PostMapping(value= ["/update/author", "/update/author/"], produces=["application/json;charset=UTF-8"])
    fun updateAuthor(@Valid @RequestBody author: AuthorUpdate): InsUpdDto = service.updateAuthor(author)

    @PostMapping(value= ["/update/book", "/update/book/"], produces=["application/json;charset=UTF-8"])
    fun updateBook(@Valid @RequestBody book: BookUpdate): InsUpdDto = service.updateBook(book)
}