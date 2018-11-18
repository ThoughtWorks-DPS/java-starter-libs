package io.twdps.starter.errors.errorhandling.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class Error {

  @ApiModelProperty
  private String type;
  @ApiModelProperty(position = 1)
  private String description;
  @ApiModelProperty(position = 2, allowEmptyValue = true)
  private List<ErrorDetail> details;
}
