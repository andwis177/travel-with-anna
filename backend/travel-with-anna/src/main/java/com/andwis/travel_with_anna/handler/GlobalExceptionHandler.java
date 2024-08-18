package com.andwis.travel_with_anna.handler;

import com.andwis.travel_with_anna.handler.exception.*;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.andwis.travel_with_anna.handler.ErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(AvatarNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(AVATAR_NOT_FOUND.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(AVATAR_NOT_FOUND.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException() {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(BAD_CREDENTIALS.getCode())
                                .errors(List.of(BAD_CREDENTIALS.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(ACCOUNT_DISABLED.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(ACCOUNT_DISABLED.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EmailNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(USER_NOT_FOUND.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(USER_NOT_FOUND.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(ExpiredTokenException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(EXPIRED_TOKEN.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(EXPIRED_TOKEN.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException exp) {
        return ResponseEntity
                .status(NOT_ACCEPTABLE)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(WRONG_CREDENTIALS.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(WRONG_CREDENTIALS.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidTokenException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(INVALID_TOKEN.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(INVALID_TOKEN.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(JwtParsingException.class)
    public ResponseEntity<ExceptionResponse> handleException(JwtParsingException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(JWT_PARSING_ERROR.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(JWT_PARSING_ERROR.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(ACCOUNT_LOCKED.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(ACCOUNT_LOCKED.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(MESSAGING_EXCEPTION.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(MESSAGING_EXCEPTION.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        List<String> errorsSorted = errors.stream().sorted().toList();
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(VALIDATION_ERROR.getCode())
                                .errors(errorsSorted)
                                .build()
                );
    }


    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(RoleNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(ROLE_NOT_FOUND.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(ROLE_NOT_FOUND.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(SaveAvatarException.class)
    public ResponseEntity<ExceptionResponse> handleSaveAvatarException(SaveAvatarException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(AVATAR_NOT_SAVED.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(AVATAR_NOT_SAVED.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserExistsException exp) {
        return ResponseEntity
                .status(CONFLICT)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(USER_EXISTS.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(USER_EXISTS.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UsernameNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(USER_NOT_FOUND.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(USER_NOT_FOUND.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserIdNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(USER_NOT_FOUND.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(USER_NOT_FOUND.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleException(WrongPasswordException exp) {
        return ResponseEntity
                .status(NOT_ACCEPTABLE)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(PASSWORD_NOT_MATCH.getCode())
                                .errors((exp.getMessage() == null || exp.getMessage().isEmpty())
                                        ? List.of(PASSWORD_NOT_MATCH.getMessage()) : List.of(exp.getMessage()))
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        logger.error("An unexpected error occurred. -->", exp);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errors(List.of("An unexpected error occurred. --> " + exp.getMessage()))
                                .build()
                );
    }
}
