package javax.servlet;

import java.io.IOException;

public interface FilterChain {
  void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse) throws IOException, ServletException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\FilterChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */