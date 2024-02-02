package org.jboss.logging;

final class JDKLoggerProvider extends AbstractMdcLoggerProvider implements LoggerProvider {
   public Logger getLogger(String name) {
      return new JDKLogger(name);
   }
}
