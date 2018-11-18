package io.twdps.starter.errors.errorhandling.mdc;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MdcAdapter {

  public void put(String key, String value) {
    MDC.put(key, value);
  }

  public void remove(String key) {
    MDC.remove(key);
  }

}
