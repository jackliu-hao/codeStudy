package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import java.util.List;

public interface ContextSelector {
   LoggerContext getLoggerContext();

   LoggerContext getLoggerContext(String var1);

   LoggerContext getDefaultLoggerContext();

   LoggerContext detachLoggerContext(String var1);

   List<String> getContextNames();
}
