package io.twdps.starter.errors.errorhandling.filters;

import io.twdps.starter.errors.errorhandling.domain.XRequestId;
import io.twdps.starter.errors.errorhandling.mdc.MdcAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

public class XRequestIdFilter implements Filter {
  private static final String X_REQUEST_ID_HEADER = "x-request-id";
  @Autowired
  private MdcAdapter mdcAdapter;

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    setXRequestId(request);
    mdcAdapter.put(X_REQUEST_ID_HEADER, XRequestId.getId());
    try {
      chain.doFilter(request, response);
    } finally {
      XRequestId.unsetId();
      mdcAdapter.remove(X_REQUEST_ID_HEADER);
    }
  }

  void setXRequestId(ServletRequest request) {
    if (request instanceof HttpServletRequest) {
      String requestId = ((HttpServletRequest) request).getHeader(X_REQUEST_ID_HEADER);

      if (!isNullOrEmpty(requestId)) {
        XRequestId.setId(requestId);
      }
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }
}
