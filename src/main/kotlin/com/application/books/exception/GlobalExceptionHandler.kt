package com.application.books.exception

import com.application.books.dto.ErrorDto
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.jooq.exception.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.lang.NumberFormatException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(methodArgumentNotValidException: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<Any> {
        val errorMessage: String = methodArgumentNotValidException.bindingResult.fieldErrors[0].defaultMessage.toString()
        val errorDetails = ErrorDto(errorMessage, request.getDescription(false))
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(exception: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<Any> {
        val errorDetails: ErrorDto = when (exception.cause) {
            is InvalidFormatException -> ErrorDto("Objectパラメターのフィルドのデータ型に誤りがあります。", request.getDescription(false))
            is JsonParseException -> ErrorDto("Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。", request.getDescription(false))
            else -> ErrorDto("Objectパラメターのフィルドに誤りがあります。", request.getDescription(false))
        }
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(exception: MethodArgumentTypeMismatchException, request: WebRequest): ResponseEntity<Any> {
        val errorDetails = if (exception.cause is NumberFormatException) {
            ErrorDto(exception.name + "パラメータにIntegerを設定してください", request.getDescription(false))
        } else {
            ErrorDto(exception.name + "パラメータに誤りがあります", request.getDescription(false))
        }
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(dataAccessException: DataAccessException, request: WebRequest): ResponseEntity<Any> {
        val errorMessage: String = dataAccessException.message!!
        val errorDetails = ErrorDto(errorMessage, request.getDescription(false))
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}