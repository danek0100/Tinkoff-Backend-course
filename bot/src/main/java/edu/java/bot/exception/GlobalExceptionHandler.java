package edu.java.bot.exception;

import edu.java.bot.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<ApiErrorResponse>
    handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setDescription("Bad request");
        response.setCode(HttpStatus.BAD_REQUEST.toString());
        response.setExceptionName(ex.getClass().getSimpleName());
        response.setExceptionMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setDescription("Внутренняя ошибка сервера");
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        response.setExceptionName(ex.getClass().getSimpleName());
        response.setExceptionMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
