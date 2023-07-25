package com.application.books.domain

import jakarta.annotation.Nonnull
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min

data class AuthorUpdate (
    @field:Nonnull
    @field:Min(value = 1, message = "authorIDの値には、1以上の数字を入力してください。")
    val authorId: Int,

    @field:Size(max = 64, message = "nameは64文字以内に入力してください。")
    var name: String,

    @field:Size(max = 32, message = "countryは32文字以内に入力してください。")
    var country: String
)

data class AuthorInsert (
    @field:Size(max = 64, message = "nameは64文字以内に入力してください。")
    @field:NotBlank(message = "nameを設定してください。")
    val name: String,

    @field:Size(max = 32, message = "countryは32文字以内に入力してください。")
    @field:NotBlank(message = "countryを設定してください。")
    val country: String
)