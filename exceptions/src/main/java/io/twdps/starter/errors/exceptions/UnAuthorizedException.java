package io.twdps.starter.errors.exceptions;

public class UnAuthorizedException extends GenericException {

  public UnAuthorizedException(String code, String message) {
    super(code, message);
  }
}
