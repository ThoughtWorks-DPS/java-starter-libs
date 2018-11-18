package io.twdps.starter.errors.exceptions;

public class DownstreamServiceUnavailableException extends GenericException {
  public DownstreamServiceUnavailableException(String code, String message) {
    super(code, message);
  }
}
