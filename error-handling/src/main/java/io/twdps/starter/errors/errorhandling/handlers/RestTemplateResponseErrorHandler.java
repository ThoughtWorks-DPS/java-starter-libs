package io.twdps.starter.errors.errorhandling.handlers;

import io.twdps.starter.errors.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

  @Override
  public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
    return (
        httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR);
  }

  @Override
  public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
    try {
      handleError(new java.net.URI("http://notpassed.com"), null, clientHttpResponse);
    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void handleError(java.net.URI url, HttpMethod method, ClientHttpResponse httpResponse) throws IOException {

    switch (httpResponse.getRawStatusCode()) {
      case 401:
        throw new UnAuthorizedException("401",
            String.format("Service unAuthorized for url:%s", url));
      case 403:
        throw new ForbiddenException("403",String.format("Forbidden for url:%s", url));
      case 404:
        throw new ResourceNotFoundException("404",String.format("Customer Not Found for url:%s", url));
      case 503:
        throw new DownstreamServiceUnavailableException("503",String.format("downstream unavailable at url:%s", url));
      case 504:
        throw new DownstreamTimeoutException("504",String.format("downstream timeout at url:%s", url));
      default:
        //captures 500 as well
        throw new GenericException("500",String.format("server error for url:%s", url));
    }
  }
}
