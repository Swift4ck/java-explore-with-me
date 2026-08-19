package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.enums.HttpStatusEnum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        log.error("NotFound: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatusEnum.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }


    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException e) {
        log.error("BadRequest: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatusEnum.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }


    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(ConflictException e) {
        log.error("Conflict: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatusEnum.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }


    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbidden(ForbiddenException e) {
        log.error("Forbidden: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatusEnum.FORBIDDEN)
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGlobal(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatusEnum.INTERNAL_SERVER_ERROR)
                .reason("Internal server error.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParam(MissingServletRequestParameterException e) {
        log.error("Missing parameter: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatusEnum.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message("Missing required parameter: " + e.getParameterName())
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUnreadable(HttpMessageNotReadableException e) {
        log.error("Unreadable body: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatusEnum.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message("Invalid request body")
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));
        log.error("Validation error: {}", errorMessage);
        return ApiError.builder()
                .status(HttpStatusEnum.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .errors(Collections.emptyList())
                .build();
    }

}
