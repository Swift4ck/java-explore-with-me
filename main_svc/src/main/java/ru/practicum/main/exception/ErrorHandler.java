package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.enums.HttpStatusEnum;

import java.time.LocalDateTime;
import java.util.Collections;

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

}
