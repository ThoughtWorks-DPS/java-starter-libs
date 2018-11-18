package io.twdps.starter.errors.exceptions;

public class SystemException extends RuntimeException {

  private String message;

  public SystemException(String message) {
    super(message);
    this.message = message;
  }
}
