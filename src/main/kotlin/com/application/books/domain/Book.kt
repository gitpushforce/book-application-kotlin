package com.application.books.domain

import jakarta.annotation.Nonnull
import jakarta.annotation.Nullable
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class BookInsert (
    @field:Size(max = 128, message = "titleは128文字以内に入力してください。")
    @field:NotBlank(message = "titleを設定してください。")
    val title: String,

    @field:Nonnull
    @field:Min(value = 1, message = "authorIDの値には、1以上の数字を入力してください。")
    val authorId: Int
)

data class BookUpdate (
    @field:Nonnull
    @field:Min(value = 1, message = "bookIdの値には、1以上の数字を入力してください。")
    val bookId: Int,

    @field:Size(max = 128, message = "titleは128文字以内に入力してください。")
    @field:Nullable
    var title: String,

    @field:Nullable
    @field:Min(value = 1, message = "authorIDの値には、1以上の数字を入力してください。")
    var authorId: Int?
)
