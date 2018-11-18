package io.twdps.starter.errors.exceptions;

public class ForbiddenException extends GenericException {
  public ForbiddenException(String code, String message) {
    super(code, message);
  }
}
