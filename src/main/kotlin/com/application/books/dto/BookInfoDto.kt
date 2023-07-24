package com.application.books.dto

import org.jooq.tools.StringUtils

data class BookInfoDto (
    var bookId: Int,
    var author: String,
    var title: String
) {
    constructor(bookId: Int): this(bookId, StringUtils.EMPTY, StringUtils.EMPTY)
}