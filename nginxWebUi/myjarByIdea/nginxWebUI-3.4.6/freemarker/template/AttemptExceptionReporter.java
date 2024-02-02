package freemarker.template;

import freemarker.core.Environment;

public interface AttemptExceptionReporter {
   AttemptExceptionReporter LOG_ERROR_REPORTER = new LoggingAttemptExceptionReporter(false);
   AttemptExceptionReporter LOG_WARN_REPORTER = new LoggingAttemptExceptionReporter(true);

   void report(TemplateException var1, Environment var2);
}
