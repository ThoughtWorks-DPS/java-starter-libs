package io.twdps.starter.errors.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GenericException extends RuntimeException {

  @Getter
  private String code;
  @Getter
  private String message;
}
