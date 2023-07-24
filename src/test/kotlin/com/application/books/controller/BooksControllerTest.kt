package com.application.books.controller

import com.application.books.domain.AuthorInsert
import com.application.books.domain.AuthorUpdate
import com.application.books.domain.BookInsert
import com.application.books.domain.BookUpdate
import com.application.books.dto.BookInfoDto
import com.application.books.dto.WorkInfo
import com.application.books.infrastructure.repository.BooksRepositoryImpl
import com.application.books.service.BooksService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.jooq.JSON.json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.streams.asSequence

@WebMvcTest(controllers = [BooksController::class])
@ExtendWith(MockitoExtension::class)
internal class BooksControllerTest {

    @Autowired
    private lateinit var mockMvc : MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var booksService: BooksService

    @MockBean
    private lateinit var repoMock: BooksRepositoryImpl

    @SpyBean
    private lateinit var serviceSpy: BooksService

    @Nested
    @DisplayName("検索：全ての書籍を取得する")
    inner class GetAll {

        @DisplayName("getAll(): /v1/books にリクエストのResponseが200," +
                " Serviceから取得したデータとURLから取得したデータが一致している")
        @Test
        fun getAll_success() {
            //given
            val book1 = BookInfoDto(bookId = 1, author = "abc", title = "title1")
            val book2 = BookInfoDto(bookId = 2, author = "def", title = "title2")
            val expected = listOf(book1, book2)

            // repositoryのMock
            given(repoMock.findAll()).willReturn(expected)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.findAll()).willCallRealMethod()

            // when
            val json = mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/books"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

            // then
            // 取得したJSONをObjectにマッピングする
            val jsonToObject: List<BookInfoDto> = mapper.readValue(json)
            assertThat(expected, `is`(Matchers.equalTo(jsonToObject)))
        }

        @DisplayName("getAll(): /v1/books/ にリクエストのResponseが200," +
                " Serviceから取得したデータとURLから取得したデータが一致している")
        @Test
        fun getAll_trailingSlash_success() {
            //given
            val book1 = BookInfoDto(bookId = 1, author = "abc", title = "title1")
            val book2 = BookInfoDto(bookId = 2, author = "def", title = "title2")
            val expected = listOf(book1, book2)

            // repositoryのMock
            given(repoMock.findAll()).willReturn(expected)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.findAll()).willCallRealMethod()

            // when
            val json = mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/books/"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

            // then
            // 取得したJSONをObjectにマッピングする
            val jsonToObject: List<BookInfoDto> = mapper.readValue(json)
            assertThat(expected, `is`(Matchers.equalTo(jsonToObject)))
        }
    }

    @Nested
    @DisplayName("検索：書籍検索")
    inner class SearchBook {
        @DisplayName("searchBook(): /v1/book/{bookId} にリクエストのResponseが200")
        @Test
        fun searchBook_success() {
            // given
            val expected = BookInfoDto(bookId = 1, author = "著者太郎", title = "タイトル")
            // repositoryのMock
            given(repoMock.findBookById(1)).willReturn(expected)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.findAll()).willCallRealMethod()

            // when
            mockMvc.get("/v1/book/1")

                // then
                .andExpect {
                status { isOk() }
                jsonPath("\$.bookId"){ value(1) }
                jsonPath("\$.author"){ value("著者太郎") }
                jsonPath("\$.title"){ value("タイトル") }
            }
        }

        @DisplayName("searchBook(): /v1/book/{bookId}/ にリクエストのResponseが200")
        @Test
        fun searchBook_trailingSlash_success() {
            // given
           // val serviceSpy: BooksService = mock()
            val expected = BookInfoDto(bookId = 1, author = "著者太郎", title = "タイトル")
            given(serviceSpy.findBookById(1)).willReturn(expected)

            // when
            mockMvc.get("/v1/book/1/")

                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("\$.bookId"){ value(1) }
                    jsonPath("\$.author"){ value("著者太郎") }
                    jsonPath("\$.title"){ value("タイトル") }
                }
        }

        @DisplayName("searchBook(): bookIdパラメータがStringの時," +
                "/v1/book/{bookId} にリクエストのResponseが4xx")
        @Test
        fun searchBook_fail_bookId() {
            // when
            mockMvc.get("/v1/book/n")

                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message"){ value("bookIdパラメータにIntegerを設定してください") }
                    jsonPath("$.details"){ value("uri=/v1/book/n") }
                }
        }
    }

    @Nested
    @DisplayName("検索：著者検索")
    inner class SearchAuthor {
        @DisplayName("searchAuthor(): /v1/author/{authorId} にリクエストのResponseが200")
        @Test
        fun searchAuthor_success() {
            // given
            val worksExpected1 = WorkInfo(bookId = 20, title = "タイトル1")
            val worksExpected2 = WorkInfo(bookId = 21, title = "タイトル2")
            val expectedWorkList = listOf(worksExpected1, worksExpected2)

            given(repoMock.findAuthorById(1)).willReturn(mapOf("name" to "著者太郎", "country" to "日本"))
            given(repoMock.findBooksByAuthorId(1)).willReturn(expectedWorkList)
            given(serviceSpy.findAuthorById(1)).willCallRealMethod()

            // when
            mockMvc.get("/v1/author/1")

                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.authorId"){ value(1) }
                    jsonPath("$.authorName"){ value("著者太郎") }
                    jsonPath("$.authorCountry"){ value("日本") }
                    jsonPath("$.works.[0].bookId"){ value(20) }
                    jsonPath("$.works.[0].title"){ value("タイトル1") }
                    jsonPath("$.works.[1].bookId"){ value(21) }
                    jsonPath("$.works.[1].title"){ value("タイトル2") }
                }
        }

        @DisplayName("searchAuthor(): /v1/author/{authorId}/ にリクエストのResponseが200")
        @Test
        fun searchAuthor_trailingSlash_success() {
            // given
            val worksExpected1 = WorkInfo(bookId = 20, title = "タイトル1")
            val worksExpected2 = WorkInfo(bookId = 21, title = "タイトル2")
            val expectedWorkList = listOf(worksExpected1, worksExpected2)

            given(repoMock.findAuthorById(1)).willReturn(mapOf("name" to "著者太郎", "country" to "日本"))
            given(repoMock.findBooksByAuthorId(1)).willReturn(expectedWorkList)
            given(serviceSpy.findAuthorById(1)).willCallRealMethod()

            // when
            mockMvc.get("/v1/author/1/")

                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.authorId"){ value(1) }
                    jsonPath("$.authorName"){ value("著者太郎") }
                    jsonPath("$.authorCountry"){ value("日本") }
                    jsonPath("$.works[0].bookId"){ value(20) }
                    jsonPath("$.works[0].title"){ value("タイトル1") }
                    jsonPath("$.works[1].bookId"){ value(21) }
                    jsonPath("$.works[1].title"){ value("タイトル2") }
                }
        }

        @DisplayName("searchAuthor(): authorIdパラメータがStringの時," +
                "/v1/author/{authorId} にリクエストのResponseが4xx")
        @Test
        fun searchAuthor_fail_authorId() {
            // when
            mockMvc.get("/v1/author/n")

                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message"){ value("authorIdパラメータにIntegerを設定してください") }
                    jsonPath("$.details"){ value("uri=/v1/author/n") }
                }
        }
    }

    @Nested
    @DisplayName("登録：著者登録")
    inner class InsertAuthor {
        @DisplayName("InsertAuthor(): /v1/create/author にリクエストのResponseが200、" +
                "Json Responseの成功メッセージ確認")
        @Test
        fun insertAuthor_success() {
            //given
            val requestModel = AuthorInsert(name = "著者太郎", country = "日本")
            // repositoryから1(成功)返却される
            given(repoMock.insertAuthor(requestModel)).willReturn(1)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.insertAuthor(requestModel)).willCallRealMethod()

            // when
            mockMvc.put("/v1/create/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("登録に成功しました。") }
                    jsonPath("$.success") { value(true) }
                }
        }

        @DisplayName("InsertAuthor(): /v1/create/author にリクエストのResponseが200、" +
                "Json Responseの失敗メッセージ確認")
        @Test
        fun insertAuthor_failInsertion() {
            //given
            val requestModel = AuthorInsert(name = "著者太郎", country = "日本")
            // repositoryから0(失敗)返却される
            given(repoMock.insertAuthor(requestModel)).willReturn(0)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.insertAuthor(requestModel)).willCallRealMethod()

            // when
            mockMvc.put("/v1/create/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("登録に失敗しました。") }
                    jsonPath("$.success") { value(false) }
                }
        }

        @DisplayName("insertAuthor(): nameパラメータが空の時," +
                "/v1/create/author にリクエストのResponseが4xx")
        @Test
        fun insertAuthor_fail_name_empty() {
            //given
            val requestModel = AuthorInsert(name = "", country = "日本")

            // when
            mockMvc.put("/v1/create/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("nameを設定してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/author") }
                }
        }

        @DisplayName("insertAuthor(): nameパラメータの文字が64字より長いの時," +
                "/v1/create/author にリクエストのResponseが4xx")
        @Test
        fun insertAuthor_fail_name_long() {
            //given
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            // ランダム65字
            val name = java.util.Random().ints(65, 0, source.length)
                .asSequence().map(source::get).joinToString("")
            val requestModel = AuthorInsert(name = name, country = "日本")

            // when
            mockMvc.put("/v1/create/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("nameは64文字以内に入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/author") }
                }
        }

        @DisplayName("insertAuthor(): countryパラメータが空の時," +
                "/v1/create/author にリクエストのResponseが4xx")
        @Test
        fun insertAuthor_fail_country_empty() {
            //given
            val requestModel = AuthorInsert(name = "abc", country = "")

            // when
            mockMvc.put("/v1/create/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("countryを設定してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/author") }
                }
        }

        @DisplayName("insertAuthor(): countryパラメータが32字より長いの時," +
                "/v1/create/author にリクエストのResponseが4xx")
        @Test
        fun insertAuthor_fail_country_long() {
            //given
            // 33字
            val country = "aaaaaaaaaabbbbbbbbbbccccccccccddd"
            val requestModel = AuthorInsert(name = "abc", country = country)

            // when
            mockMvc.put("/v1/create/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("countryは32文字以内に入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/author") }
                }
        }
    }

    @Nested
    @DisplayName("登録：書籍登録")
    inner class InsertBook {
        @DisplayName("InsertBook(): /v1/create/book にリクエストのResponseが200、" +
                "Json Responseの成功メッセージ確認")
        @Test
        fun insertBook_success() {
            //given
            val requestModel = BookInsert(title = "abc", authorId = 2)

            // repositoryに著者が存在する（Mock）
            given(repoMock.findAuthorNameById(2)).willReturn("author")
            // repositoryから1(成功)返却される
            given(repoMock.insertBook(requestModel)).willReturn(1)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.insertBook(requestModel)).willCallRealMethod()

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("登録に成功しました。") }
                    jsonPath("$.success") { value(true) }
                }
        }

        @DisplayName("InsertBook(): /v1/create/book にリクエストのResponseが200、" +
                "Json Responseの失敗メッセージ確認")
        @Test
        fun insertBook_failInsertion() {
            //given
            val requestModel = BookInsert(title = "abc", authorId = 2)

            // repositoryに著者が存在する（Mock）
            given(repoMock.findAuthorNameById(2)).willReturn("author")
            // repositoryから0(失敗)返却される
            given(repoMock.insertBook(requestModel)).willReturn(0)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.insertBook(requestModel)).willCallRealMethod()

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("登録に失敗しました。") }
                    jsonPath("$.success") { value(false) }
                }
        }

        @DisplayName("InsertBook(): 設定する著者IDがDBに存在しない時、/v1/create/book にリクエストのResponseが200が、" +
                "Json Responseの失敗メッセージ返却")
        @Test
        fun insertBook_fail_noAuthor() {
            //given
            val requestModel = BookInsert(title = "abc", authorId = 2)

            // repositoryに著者が存在する（Mock）
            given(repoMock.findAuthorNameById(2)).willReturn(null)
            // repositoryから0(失敗)返却される
            given(repoMock.insertBook(requestModel)).willReturn(0)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.insertBook(requestModel)).willCallRealMethod()

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("著者がDBに存在しないため書籍が登録できませんでした、先に著者を登録してください。") }
                    jsonPath("$.success") { value(false) }
                }
        }

        @DisplayName("insertBook(): titleパラメータが空の時," +
                "/v1/create/book にリクエストのResponseが4xx")
        @Test
        fun insertBook_fail_title_empty() {
            //given
            val requestModel = BookInsert(title = "", authorId = 1)

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("titleを設定してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/book") }
                }
        }

        @DisplayName("insertBook(): titleパラメータの文字が128字より長いの時," +
                "/v1/create/book にリクエストのResponseが4xx")
        @Test
        fun insertAuthor_fail_title_long() {
            //given
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            // 129字
            val title = java.util.Random().ints(129, 0, source.length)
                .asSequence().map(source::get).joinToString("")

            val requestModel = BookInsert(title = title, authorId = 1)

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("titleは128文字以内に入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/book") }
                }
        }

        @DisplayName("insertBook(): authorIdパラメータが空の時," +
                "/v1/create/book にリクエストのResponseが4xx")
        @Test
        fun insertBook_fail_authorId_empty() {
            //given
            val requestModel = "{\"title\":  \"abc\",\"authorId\":}"

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(requestModel))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。") }
                    jsonPath("$.details") { value("uri=/v1/create/book") }
                }
        }

        @DisplayName("insertBook(): authorIdパラメータの値が負の数の時," +
                "/v1/create/book にリクエストのResponseが4xx")
        @Test
        fun insertBook_fail_authorId_negative() {
            //given
            val requestModel = BookInsert(title = "abc", authorId = -1)

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("authorIDの値には、1以上の数字を入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/create/book") }
                }
        }
    }

    @Nested
    @DisplayName("更新：著者登録")
    inner class UpdateAuthor {
        @DisplayName("updateAuthor(): /v1/update/author にリクエストのResponseが200、" +
                "Json Responseの成功メッセージ確認")
        @Test
        fun updateAuthor_success() {
            //given
            val requestModel = AuthorUpdate(authorId = 1, name = "著者太郎", country = "日本")
            // repositoryから1(成功)返却される
            given(repoMock.updateAuthor(requestModel)).willReturn(1)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.updateAuthor(requestModel)).willCallRealMethod()

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("更新に成功しました。") }
                    jsonPath("$.success") { value(true) }
                }
        }

        @DisplayName("updateAuthor(): 更新したい著者がDBに存在しない時、/v1/update/author にリクエストのResponseが200、" +
                "Json Responseの失敗メッセージ確認")
        @Test
        fun updateAuthor_failUpdate() {
            //given
            val requestModel = AuthorUpdate(authorId = 1, name = "著者太郎", country = "日本")
            // repositoryから0(失敗)返却される
            given(repoMock.updateAuthor(requestModel)).willReturn(0)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.updateAuthor(requestModel)).willCallRealMethod()

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("更新に失敗しました。") }
                    jsonPath("$.success") { value(false) }
                }
        }

        @DisplayName("updateAuthor(): authorIdパラメータが空の時," +
                "/v1/update/author にリクエストのResponseが4xx")
        @Test
        fun updateAuthor_fail_authorId_empty() {
            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json("{\"authorId\": ,\"name\": \"abc\",\"country\":\"aaa\"}"))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。") }
                    jsonPath("$.details") { value("uri=/v1/update/author") }
                }
        }

        @DisplayName("updateAuthor(): authorIdパラメータが負数の時," +
                "/v1/update/author にリクエストのResponseが4xx")
        @Test
        fun updateAuthor_fail_authorId_negative() {
            //given
            val requestModel = AuthorUpdate(authorId = -1, name = "aaaa", country = "日本")

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("authorIDの値には、1以上の数字を入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/author") }
                }
        }

        @DisplayName("updateAuthor(): nameパラメータが空の時," +
                "/v1/update/author にリクエストのResponseが4xx")
        @Test
        fun updateAuthor_fail_name_empty() {
            //given
            val requestModel = AuthorUpdate(authorId = 1, name = "", country = "日本")

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("nameを設定してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/author") }
                }
        }

        @DisplayName("updateAuthor(): nameパラメータの文字が64字より長いの時," +
                "/v1/update/author にリクエストのResponseが4xx")
        @Test
        fun updateAuthor_fail_authorId_long() {
            //given
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            // ランダム65字
            val name = java.util.Random().ints(65, 0, source.length)
                .asSequence().map(source::get).joinToString("")
            val requestModel = AuthorUpdate(authorId = 1, name = name, country = "日本")

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("nameは64文字以内に入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/author") }
                }
        }

        @DisplayName("updateAuthor(): countryパラメータが空の時," +
                "/v1/update/author にリクエストのResponseが4xx")
        @Test
        fun insertAuthor_fail_country_empty() {
            //given
            val requestModel = AuthorUpdate(authorId = 1, name = "abc", country = "")

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("countryを設定してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/author") }
                }
        }

        @DisplayName("updateAuthor(): countryパラメータが32字より長いの時," +
                "/v1/update/author にリクエストのResponseが4xx")
        @Test
        fun updateAuthor_fail_country_long() {
            //given
            // 33字
            val country = "aaaaaaaaaabbbbbbbbbbccccccccccddd"
            val requestModel = AuthorUpdate(authorId = 1, name = "abc", country = country)

            // when
            mockMvc.post("/v1/update/author") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("countryは32文字以内に入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/author") }
                }
        }
    }

    @Nested
    @DisplayName("更新：書籍登録")
    inner class UpdateBook {

        @DisplayName(
            "UpdateBook(): /v1/update/book にリクエストのResponseが200、" +
                    "Json Responseの成功メッセージ確認"
        )
        @Test
        fun updateBook_success() {
            //given
            val requestModel = BookUpdate(bookId = 1, title = "abc", authorId = 2)

            // repositoryに著者が存在する（Mock）
            given(repoMock.findAuthorNameById(2)).willReturn("author")
            // repositoryから1(成功)返却される
            given(repoMock.updateBook(requestModel)).willReturn(1)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.updateBook(requestModel)).willCallRealMethod()

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("更新に成功しました。") }
                    jsonPath("$.success") { value(true) }
                }
        }

        @DisplayName(
            "UpdateBook(): /v1/update/book にリクエストのResponseが200、" +
                    "Json Responseの失敗メッセージ確認"
        )
        @Test
        fun updateBook_failUpdate() {
            //given
            val requestModel = BookUpdate(bookId = 1, title = "abc", authorId = 2)

            // repositoryに著者が存在する（Mock）
            given(repoMock.findAuthorNameById(2)).willReturn("author")
            // repositoryから0(失敗)返却される
            given(repoMock.updateBook(requestModel)).willReturn(0)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.updateBook(requestModel)).willCallRealMethod()

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("更新に失敗しました。") }
                    jsonPath("$.success") { value(false) }
                }
        }

        @DisplayName(
            "updateBook(): 設定する著者IDがDBに存在しない時、/v1/update/book にリクエストのResponseが200が、" +
                    "Json Responseの失敗メッセージ返却"
        )
        @Test
        fun updateBook_fail_noAuthor() {
            //given
            val requestModel = BookUpdate(bookId = 1, title = "abc", authorId = 2)

            // repositoryに著者が存在する（Mock）
            given(repoMock.findAuthorNameById(2)).willReturn(null)
            // repositoryから0(失敗)返却される
            given(repoMock.updateBook(requestModel)).willReturn(0)
            // Serviceのリアルメソッドを呼び出す
            given(booksService.updateBook(requestModel)).willCallRealMethod()

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))
                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("DBに存在しない著者で書籍の情報を更新することができません、先に著者を登録してください。") }
                    jsonPath("$.success") { value(false) }
                }
        }

        @DisplayName(
            "updateBook(): bookIdパラメータが空の時," +
                    "/v1/update/book にリクエストのResponseが4xx"
        )
        @Test
        fun updateBook_fail_bookId_empty() {
            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json("{\"bookId\": ,\"title\": \"abc\",\"authorId\":1}"))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。") }
                    jsonPath("$.details") { value("uri=/v1/update/book") }
                }
        }

        @DisplayName(
            "updateBook(): bookIdパラメータが負数の時," +
                    "/v1/update/book にリクエストのResponseが4xx"
        )
        @Test
        fun updateBook_fail_bookId_negative() {
            //given
            val requestModel = BookUpdate(bookId = -1, title = "abc", authorId = 2)

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("bookIdの値には、1以上の数字を入力してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/book") }
                }
        }

        @DisplayName(
            "updateBook(): titleパラメータが空の時," +
                    "/v1/book/author にリクエストのResponseが4xx"
        )
        @Test
        fun updateBook_fail_title_empty() {
            //given
            val requestModel = BookUpdate(bookId = 1, title = "", authorId = 1)

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("$.message") { value("titleを設定してください。") }
                    jsonPath("$.details") { value("uri=/v1/update/book") }
                }
        }

        @DisplayName(
            "updateBook(): titleパラメータの文字が128字より長いの時," +
                    "/v1/create/book にリクエストのResponseが4xx"
        )
        @Test
        fun updateAuthor_fail_title_long() {
            //given
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            // 129字
            val title = java.util.Random().ints(129, 0, source.length)
                .asSequence().map(source::get).joinToString("")

            val requestModel = BookUpdate(bookId = 1, title = title, authorId = 1)

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("\$.message") { value("titleは128文字以内に入力してください。") }
                    jsonPath("\$.details") { value("uri=/v1/update/book") }
                }
        }

        @DisplayName(
            "insertBook(): authorIdパラメータが空の時," +
                    "/v1/create/book にリクエストのResponseが4xx"
        )
        @Test
        fun insertBook_fail_authorId_empty() {
            //given
            val requestModel = "{\"title\":  \"abc\",\"authorId\":}"

            // when
            mockMvc.put("/v1/create/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(requestModel))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("\$.message") { value("Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。") }
                    jsonPath("\$.details") { value("uri=/v1/create/book") }
                }
        }

        @DisplayName(
            "updateBook(): authorIdパラメータの値が負の数の時," +
                    "/v1/create/book にリクエストのResponseが4xx"
        )
        @Test
        fun updateBook_fail_authorId_negative() {
            //given
            val requestModel = BookUpdate(bookId = 1, title = "abc", authorId = -1)

            // when
            mockMvc.post("/v1/update/book") {
                contentType = MediaType.APPLICATION_JSON
                content = (json(mapper.writeValueAsString(requestModel)))

                accept = MediaType.APPLICATION_JSON
            }
                // then
                .andExpect {
                    status { is4xxClientError() }
                    jsonPath("\$.message") { value("authorIDの値には、1以上の数字を入力してください。") }
                    jsonPath("\$.details") { value("uri=/v1/update/book") }
                }
        }
    }
}