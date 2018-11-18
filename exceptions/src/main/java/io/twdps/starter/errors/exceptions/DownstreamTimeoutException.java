package io.twdps.starter.errors.exceptions;

public class DownstreamTimeoutException extends GenericException {
  public DownstreamTimeoutException(String code, String message) {
    super(code, message);
  }
}
