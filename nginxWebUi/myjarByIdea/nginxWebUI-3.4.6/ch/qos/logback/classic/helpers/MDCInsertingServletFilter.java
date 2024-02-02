package ch.qos.logback.classic.helpers;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public class MDCInsertingServletFilter implements Filter {
   public void destroy() {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      this.insertIntoMDC(request);

      try {
         chain.doFilter(request, response);
      } finally {
         this.clearMDC();
      }

   }

   void insertIntoMDC(ServletRequest request) {
      MDC.put("req.remoteHost", request.getRemoteHost());
      if (request instanceof HttpServletRequest) {
         HttpServletRequest httpServletRequest = (HttpServletRequest)request;
         MDC.put("req.requestURI", httpServletRequest.getRequestURI());
         StringBuffer requestURL = httpServletRequest.getRequestURL();
         if (requestURL != null) {
            MDC.put("req.requestURL", requestURL.toString());
         }

         MDC.put("req.method", httpServletRequest.getMethod());
         MDC.put("req.queryString", httpServletRequest.getQueryString());
         MDC.put("req.userAgent", httpServletRequest.getHeader("User-Agent"));
         MDC.put("req.xForwardedFor", httpServletRequest.getHeader("X-Forwarded-For"));
      }

   }

   void clearMDC() {
      MDC.remove("req.remoteHost");
      MDC.remove("req.requestURI");
      MDC.remove("req.queryString");
      MDC.remove("req.requestURL");
      MDC.remove("req.method");
      MDC.remove("req.userAgent");
      MDC.remove("req.xForwardedFor");
   }

   public void init(FilterConfig arg0) throws ServletException {
   }
}
