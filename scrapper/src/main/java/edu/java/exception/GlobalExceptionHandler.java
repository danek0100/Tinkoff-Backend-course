package edu.java.exception;

import edu.java.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setDescription("Неверный запрос");
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

    @ExceptionHandler(ChatAlreadyRegisteredException.class)
    public ResponseEntity<ApiErrorResponse> handleChatAlreadyRegisteredException(ChatAlreadyRegisteredException ex) {
        ApiErrorResponse response = new ApiErrorResponse("Повторная регистрация чата", HttpStatus.BAD_REQUEST.toString(), ex.getClass().getSimpleName(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LinkAlreadyAddedException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkAlreadyAddedException(LinkAlreadyAddedException ex) {
        ApiErrorResponse response = new ApiErrorResponse("Ссылка уже добавлена", HttpStatus.BAD_REQUEST.toString(), ex.getClass().getSimpleName(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleChatNotFoundException(ChatNotFoundException ex) {
        ApiErrorResponse response = new ApiErrorResponse("Чат не найден", HttpStatus.NOT_FOUND.toString(), ex.getClass().getSimpleName(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
