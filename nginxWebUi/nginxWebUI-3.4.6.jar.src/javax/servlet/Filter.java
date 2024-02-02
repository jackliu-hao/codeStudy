package javax.servlet;

import java.io.IOException;

public interface Filter {
  default void init(FilterConfig filterConfig) throws ServletException {}
  
  void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain) throws IOException, ServletException;
  
  default void destroy() {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */