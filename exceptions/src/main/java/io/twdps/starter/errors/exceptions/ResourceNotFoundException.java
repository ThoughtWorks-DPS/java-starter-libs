package io.twdps.starter.errors.exceptions;

public class ResourceNotFoundException extends GenericException {

  public ResourceNotFoundException(String code, String message) {
    super(code, message);
  }
}
