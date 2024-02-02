package javax.servlet.http;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class HttpFilter extends GenericFilter {
   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
      if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
         this.doFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);
      } else {
         throw new ServletException("non-HTTP request or response");
      }
   }

   protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
      chain.doFilter(req, res);
   }
}
