package io.twdps.starter.errors.errorhandling.handlers;

import io.twdps.starter.errors.errorhandling.domain.Error;
import io.twdps.starter.errors.errorhandling.domain.ErrorDetail;
import io.twdps.starter.errors.errorhandling.domain.ErrorsContext;
import io.twdps.starter.errors.exceptions.GenericException;
import io.twdps.starter.errors.exceptions.OdataClientException;
import io.twdps.starter.errors.exceptions.ResourceNotFoundException;
import io.twdps.starter.errors.exceptions.SystemException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static io.twdps.starter.errors.errorhandling.domain.ErrorType.VALIDATION_FAILED;

@ResponseBody
@ControllerAdvice(annotations = RestController.class)
public class DefaultExceptionHandler {

  @Autowired
  private Properties errorMessageMap;

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(value = ResourceNotFoundException.class)
  public ErrorsContext handleResourceNotFoundException(ResourceNotFoundException e) {
    Error error = createError(e.getCode(), e.getMessage(), Collections.emptyList());
    return ErrorsContext.builder()
        .errors(newArrayList(error))
        .build();
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoHandlerFoundException.class)
  public ErrorsContext handleHandlerNotFound() {
    return ErrorsContext.builder()
        .errors(newArrayList(new Error("resource_not_found",
            "The requested resource does not exist.",
            newArrayList())))
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ErrorsContext handleApiConstraintViolationException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    List<ErrorDetail> errorDetails = convert(violations);
    Error error = createError(VALIDATION_FAILED.toString(),
        VALIDATION_FAILED.getDescription(), errorDetails);
    return ErrorsContext.builder()
        .errors(newArrayList(error))
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BindException.class)
  public ErrorsContext handleApiMethodArgumentNotValidException(
      BindException e) {
    BindingResult bindingResult = e.getBindingResult();
    List<ErrorDetail> errorDetails = convertBindingResult(bindingResult);
    Error error = createError(VALIDATION_FAILED.toString(),
        VALIDATION_FAILED.getDescription(), errorDetails);
    return ErrorsContext.builder()
        .errors(newArrayList(error))
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorsContext handleApiMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    List<ErrorDetail> errorDetails = convertBindingResult(bindingResult);
    Error error = createError(VALIDATION_FAILED.toString(),
        VALIDATION_FAILED.getDescription(), errorDetails);
    return ErrorsContext.builder()
        .errors(newArrayList(error))
        .build();
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(value = GenericException.class)
  public ErrorsContext handleGenericException() {
    return generateInternalErrorsContext();
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(SystemException.class)
  public ErrorsContext handleSystemException() {
    return generateInternalErrorsContext();
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(OdataClientException.class)
  public ErrorsContext handleOdataClientException() {
    return generateInternalErrorsContext();
  }

  private Error createError(String type, String message, List<ErrorDetail> errorDetails) {
    return Error.builder()
        .type(type)
        .description(message)
        .details(errorDetails)
        .build();
  }

  private List<ErrorDetail> convertBindingResult(BindingResult bindingResult) {
    List<ErrorDetail> details = new ArrayList<>();
    for (final FieldError fieldError : bindingResult.getFieldErrors()) {
      ErrorDetail detail = createErrorDetail(fieldError.getCode(),
          fieldError.getDefaultMessage(), fieldError.getField());

      details.add(detail);
    }
    return details;
  }

  private List<ErrorDetail> convert(Set<ConstraintViolation<?>> violations) {
    List<ErrorDetail> details = new ArrayList<>(violations.size());
    for (ConstraintViolation<?> violation : violations) {
      String[] errorCodeAndMessage = getErrorCodeAndMessage(violation);

      String field = ((PathImpl) violation.getPropertyPath()).getLeafNode().asString();
      String code = errorCodeAndMessage[0];
      String message = errorCodeAndMessage[1];

      details.add(createErrorDetail(code, message, field));
    }
    return details;
  }

  private String[] getErrorCodeAndMessage(ConstraintViolation<?> violation) {
    return errorMessageMap.getProperty(violation.getMessage()).split("\\|");
  }

  private ErrorDetail createErrorDetail(String code, String message, String field) {
    return ErrorDetail.builder()
        .field(field)
        .code(code)
        .message(message)
        .build();
  }

  private ErrorsContext generateInternalErrorsContext() {
    Error error = createError(
        "SYSTEM_ERROR",
        "The server encountered an unexpected condition that prevented it from fulfilling the request.",
        Collections.emptyList());
    return ErrorsContext.builder()
        .errors(newArrayList(error))
        .build();
  }

}
