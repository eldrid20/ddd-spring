package com.xyz.payment.adapter.incoming.rest;

import com.xyz.payment.adapter.incoming.rest.dto.ErrorField;
import com.xyz.payment.adapter.incoming.rest.dto.ErrorFieldResponseDto;
import com.xyz.payment.adapter.incoming.rest.dto.ErrorResponseDto;
import com.xyz.payment.domain.exception.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ErrorControllerAdvice {

  @ExceptionHandler(value = PaymentNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handlePaymentNotFoundException(
      PaymentNotFoundException exception) {
    log.error(exception.getMessage());
    var errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setErrorMessage(exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorFieldResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    var errorFieldResponseDto = new ErrorFieldResponseDto();

    List<ErrorField> errorFields = new ArrayList<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errorFields.add(new ErrorField(fieldName, errorMessage));
            });
    errorFieldResponseDto.setErrorMessage("Bad Request");
    errorFieldResponseDto.setErrorFields(errorFields);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorFieldResponseDto);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<ErrorResponseDto> handleGenericError(Exception exception) {
    log.error(exception.getMessage(), exception);
    var errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setErrorMessage(exception.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
  }
}
