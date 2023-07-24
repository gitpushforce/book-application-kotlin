package com.application.books.dto

import org.jooq.tools.StringUtils

data class AuthorInfoDto (
    var authorId: Int,
    var authorName: String,
    var authorCountry: String,
    var works: List<WorkInfo>
) {
    constructor(authorId: Int): this(authorId, StringUtils.EMPTY, StringUtils.EMPTY, listOf())
}

data class WorkInfo (
    var bookId: Int,
    var title: String
)