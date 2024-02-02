package ch.qos.logback.classic.servlet;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import ch.qos.logback.core.util.OptionHelper;
import java.util.EventListener;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class LogbackServletContainerInitializer implements ServletContainerInitializer {
   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
      if (this.isDisabledByConfiguration(ctx)) {
         StatusViaSLF4JLoggerFactory.addInfo("Due to deployment instructions will NOT register an instance of " + LogbackServletContextListener.class + " to the current web-app", this);
      } else {
         StatusViaSLF4JLoggerFactory.addInfo("Adding an instance of  " + LogbackServletContextListener.class + " to the current web-app", this);
         LogbackServletContextListener lscl = new LogbackServletContextListener();
         ctx.addListener((EventListener)lscl);
      }
   }

   boolean isDisabledByConfiguration(ServletContext ctx) {
      String disableAttributeStr = null;
      Object disableAttribute = ctx.getInitParameter("logbackDisableServletContainerInitializer");
      if (disableAttribute instanceof String) {
         disableAttributeStr = (String)disableAttribute;
      }

      if (OptionHelper.isEmpty(disableAttributeStr)) {
         disableAttributeStr = OptionHelper.getSystemProperty("logbackDisableServletContainerInitializer");
      }

      if (OptionHelper.isEmpty(disableAttributeStr)) {
         disableAttributeStr = OptionHelper.getEnv("logbackDisableServletContainerInitializer");
      }

      return OptionHelper.isEmpty(disableAttributeStr) ? false : disableAttributeStr.equalsIgnoreCase("true");
   }
}
