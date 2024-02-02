package ch.qos.logback.classic.servlet;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import ch.qos.logback.core.spi.ContextAwareBase;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class LogbackServletContextListener implements ServletContextListener {
   ContextAwareBase contextAwareBase = new ContextAwareBase();

   public void contextInitialized(ServletContextEvent sce) {
   }

   public void contextDestroyed(ServletContextEvent sce) {
      ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
      if (iLoggerFactory instanceof LoggerContext) {
         LoggerContext loggerContext = (LoggerContext)iLoggerFactory;
         this.contextAwareBase.setContext(loggerContext);
         StatusViaSLF4JLoggerFactory.addInfo("About to stop " + loggerContext.getClass().getCanonicalName() + " [" + loggerContext.getName() + "]", this);
         loggerContext.stop();
      }

   }
}
