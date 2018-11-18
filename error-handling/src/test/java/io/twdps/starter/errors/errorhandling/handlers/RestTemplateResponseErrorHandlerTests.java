package io.twdps.starter.errors.errorhandling.handlers;

import io.twdps.starter.errors.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


public class RestTemplateResponseErrorHandlerTests {

  private MockRestServiceServer server;
  private RestTemplate restTemplate;
  private RestTemplateBuilder builder;

  @BeforeEach
  public void contextLoads() {
    builder = new RestTemplateBuilder();
    restTemplate = this.builder
        .errorHandler(new RestTemplateResponseErrorHandler())
        .build();
    server = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void throwUnAuthorizedException() {

    assertNotNull(this.builder);
    assertNotNull(this.server);

    this.server
        .expect(ExpectedCount.once(), requestTo("/customer/33"))
        .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
    // use the Error class so we don't need another domain object
    assertThrows(UnAuthorizedException.class, () -> {
      restTemplate.getForObject("customer/33", Error.class);
      this.server.verify();
    });
  }

  @Test
  public void throwForbiddenException() {

    assertNotNull(this.builder);
    assertNotNull(this.server);

    this.server
        .expect(ExpectedCount.once(), requestTo("/customer/33"))
        .andRespond(withStatus(HttpStatus.FORBIDDEN));
    // use the Error class so we don't need another domain object
    assertThrows(ForbiddenException.class, () -> {
      restTemplate.getForObject("customer/33", Error.class);
      this.server.verify();
    });
  }


  @Test
  public void throwNotFound() {

    assertNotNull(this.builder);
    assertNotNull(this.server);

    this.server
        .expect(ExpectedCount.once(), requestTo("/customer/33"))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    // use the Error class so we don't need another domain object
    assertThrows(ResourceNotFoundException.class, () -> {
      restTemplate.getForObject("customer/33", Error.class);
      this.server.verify();
    });
  }

  @Test
  public void throwGeneric() {

    assertNotNull(this.builder);
    assertNotNull(this.server);

    this.server
        .expect(ExpectedCount.once(), requestTo("/customer/33"))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
    // use the Error class so we don't need another domain object
    assertThrows(GenericException.class, () -> {
      restTemplate.getForObject("customer/33", Error.class);
      this.server.verify();
    });
  }

  @Test
  public void throwDownstreamServiceUnavailableException() {

    assertNotNull(this.builder);
    assertNotNull(this.server);

    this.server
        .expect(ExpectedCount.once(), requestTo("/customer/33"))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    // use the Error class so we don't need another domain object
    assertThrows(DownstreamServiceUnavailableException.class, () -> {
      restTemplate.getForObject("customer/33", Error.class);
      this.server.verify();
    });
  }

}
