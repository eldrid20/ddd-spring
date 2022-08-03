package com.xyz.payment.adapter.incoming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseDto> handleMissingRequestParameter(Exception exception) {
        log.error(exception.getMessage());
        var errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setErrorMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponseDto> handleGenericError(Exception exception) {
        log.error(exception.getMessage(), exception);
        var errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setErrorMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }
}
