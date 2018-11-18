package io.twdps.starter.errors.errorhandling.domain;

import java.util.UUID;

public class XRequestId {
  private static final ThreadLocal<String> requestIdTL =
      ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

  public static synchronized String getId() {
    return requestIdTL.get();
  }

  public static synchronized void setId(String requestId) {
    requestIdTL.set(requestId);
  }

  public static synchronized void unsetId() {
    requestIdTL.remove();
  }
}
