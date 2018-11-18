package io.twdps.starter.errors.errorhandling.handlers;

import com.google.common.collect.Lists;
import io.twdps.starter.errors.errorhandling.domain.Error;
import io.twdps.starter.errors.errorhandling.domain.ErrorDetail;
import io.twdps.starter.errors.errorhandling.domain.ErrorsContext;
import io.twdps.starter.errors.exceptions.ResourceNotFoundException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultExceptionHandlerTest {

  @Mock
  private Properties errorMessageMap;

  @InjectMocks
  private DefaultExceptionHandler defaultExceptionHandler;


  @Test
  void shouldMapResourceNotFoundToErrorWithTypeAndDescriptionAndNoErrorDetails() {
    ErrorsContext expected = ErrorsContext.builder()
        .errors(createError("problem", "message", newArrayList())).build();

    ErrorsContext actual = defaultExceptionHandler.handleResourceNotFoundException(
        new ResourceNotFoundException("problem", "message"));

    assertEquals(expected, actual);
  }

  @Test
  void shouldMapGenericExceptionToInternalServerErrorWithTypeAndDescriptionAndNoErrorDetails() {
    when(errorMessageMap.getProperty("internalErrorMessage")).thenReturn("code|message");
    ErrorsContext expected = ErrorsContext.builder()
        .errors(createError("code", "message", newArrayList())).build();

    ErrorsContext actual = defaultExceptionHandler.handleGenericException();

    assertEquals(expected, actual);
  }

  @Test
  void shouldMapSystemExceptionToInternalServerErrorWithTypeAndDescriptionAndNoErrorDetails() {
    when(errorMessageMap.getProperty("internalErrorMessage")).thenReturn("code|message");
    ErrorsContext expected = ErrorsContext.builder()
        .errors(createError("code", "message", newArrayList())).build();

    ErrorsContext actual = defaultExceptionHandler.handleSystemException();

    assertEquals(expected, actual);
  }

  @Test
  void shouldMapHandlerNotFoundToResourceNotFound() {
    ErrorsContext expected = ErrorsContext.builder()
        .errors(createError("resource_not_found", "The requested resource does not exist.", newArrayList())).build();

    ErrorsContext actual = defaultExceptionHandler.handleHandlerNotFound();

    assertEquals(expected, actual);
  }

  @Test
  void shouldMapOdataClientExceptionToInternalServerErrorWithTypeAndDescriptionAndNoErrorDetails() {
    when(errorMessageMap.getProperty("internalErrorMessage")).thenReturn("code|message");
    ErrorsContext expected = ErrorsContext.builder()
        .errors(createError("code", "message", newArrayList())).build();

    ErrorsContext actual = defaultExceptionHandler.handleOdataClientException();

    assertEquals(expected, actual);
  }

  @Test
  void shouldMapConstraintViolationExceptionToErrorWithViolationTypeViolationDescriptionAndErrorDetailsAboutTheFieldThatHasValidationErrors() {
    when(errorMessageMap.getProperty("customerLengthViolation")).thenReturn("code|message");
    Set<ConstraintViolation<?>> constraintViolations = createConstraintValidation(
        "customerLengthViolation", "fieldName");

    ErrorsContext actual = defaultExceptionHandler.handleApiConstraintViolationException(
        new ConstraintViolationException(constraintViolations));

    ErrorsContext expected = ErrorsContext.builder()
        .errors(
            createError("validation_failed",
                "One or more fields specified in the request failed validation",
                Lists.newArrayList(new ErrorDetail("fieldName", "code", "message"))))
        .build();
    assertEquals(expected, actual);
  }

  private Set<ConstraintViolation<?>> createConstraintValidation(String message, String fieldName) {
    Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
    constraintViolations.add(ConstraintViolationImpl.forParameterValidation(null, null, null,
        message, null, null, null, null,
        PathImpl.createPathFromString(fieldName), null, null, null, null));
    return constraintViolations;
  }


  private List<Error> createError(String type, String message, List<ErrorDetail> errorDetails) {
    return newArrayList(new Error(type,
        message, errorDetails));
  }

}