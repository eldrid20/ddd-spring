package com.xyz.order.domain.exception;

public class InvalidStateException extends RuntimeException {
  public InvalidStateException(String message) {
    super(message);
  }
}
