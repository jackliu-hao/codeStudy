package javax.servlet;

import java.util.Set;

public interface ServletContainerInitializer {
  void onStartup(Set<Class<?>> paramSet, ServletContext paramServletContext) throws ServletException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletContainerInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */