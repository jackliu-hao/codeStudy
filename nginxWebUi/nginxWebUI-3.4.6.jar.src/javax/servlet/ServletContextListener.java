package javax.servlet;

import java.util.EventListener;

public interface ServletContextListener extends EventListener {
  default void contextInitialized(ServletContextEvent sce) {}
  
  default void contextDestroyed(ServletContextEvent sce) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletContextListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */