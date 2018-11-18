package io.twdps.starter.errors.errorhandling.filters;

import io.twdps.starter.errors.errorhandling.mdc.MdcAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class XRequestIdFilterTest {

  @Mock
  private MdcAdapter mdcAdapter;

  @InjectMocks
  private XRequestIdFilter xRequestIdFilter;

  @Test
  public void shouldPutRequestInMdcForLoggingPurposes() throws IOException, ServletException {
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.addHeader("x-request-id", "value");

    xRequestIdFilter.doFilter(servletRequest, new MockHttpServletResponse(), new MockFilterChain());

    InOrder inOrder = inOrder(mdcAdapter);

    inOrder.verify(mdcAdapter).put("x-request-id", "value");
    inOrder.verify(mdcAdapter).remove("x-request-id");
  }

}
