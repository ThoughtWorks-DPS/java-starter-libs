package io.twdps.starter.errors.errorhandling.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RequestWebClientFilter {

  private static final Logger logger = LoggerFactory.getLogger(RequestWebClientFilter.class);

  public ExchangeFilterFunction xRequestFilter() {
    logger.info("xRequest Filter time");
    MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
    // capture the 'end-user header which is the only one that's not an x-* header
    headerMap.add("end-user", MDC.get("end-user"));
    //forward x-* headers
    Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
    for (String key : mdcContextMap.keySet()) {
      if (key.startsWith("x-")) {
        headerMap.add(key, mdcContextMap.get(key));
      }
    }
    return ExchangeFilterFunction.ofRequestProcessor(

        clientRequest -> {
          clientRequest.headers()
              .forEach((name, values) -> headerMap.put(name, values));

          ClientRequest newRequest = ClientRequest.from(clientRequest)
              .headers(headers -> headers.addAll(headerMap))
              .url(clientRequest.url())
              .method(clientRequest.method())
              .body(clientRequest.body())
              .build();
          return Mono.just(newRequest);
        });
  }

  public ExchangeFilterFunction loggingFilter() {
    return (clientRequest, next) -> {
      clientRequest.headers()
          .forEach((name, values) -> logger.info("header filter->name:{},value:{}", name, values.get(0)));
      return next.exchange(clientRequest);
    };
  }
}
