package io.twdps.starter.errors.errorhandling.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorsContext {

  @ApiModelProperty
  private final String requestId = XRequestId.getId();
  @ApiModelProperty
  private List<Error> errors;
}
