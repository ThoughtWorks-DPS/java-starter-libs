package io.twdps.starter.errors.errorhandling.domain;

public enum ErrorType {

  VALIDATION_FAILED("validation_failed",
      "One or more fields specified in the request failed validation");

  private final String type;

  private final String description;

  ErrorType(String type, String description) {
    this.type = type;
    this.description = description;
  }

  @Override
  public String toString() {
    return this.type;
  }

  public String getDescription() {
    return this.description;
  }
}
