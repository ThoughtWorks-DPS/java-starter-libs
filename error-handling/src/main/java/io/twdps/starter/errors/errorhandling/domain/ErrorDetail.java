package io.twdps.starter.errors.errorhandling.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ErrorDetail {

  @ApiModelProperty
  private String field;
  @ApiModelProperty(position = 1)
  private String code;
  @ApiModelProperty(position = 2)
  private String message;
}
