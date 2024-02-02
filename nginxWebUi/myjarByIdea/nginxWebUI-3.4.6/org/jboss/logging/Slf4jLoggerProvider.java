package org.jboss.logging;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

final class Slf4jLoggerProvider extends AbstractLoggerProvider implements LoggerProvider {
   public Logger getLogger(String name) {
      org.slf4j.Logger l = LoggerFactory.getLogger(name);
      return (Logger)(l instanceof LocationAwareLogger ? new Slf4jLocationAwareLogger(name, (LocationAwareLogger)l) : new Slf4jLogger(name, l));
   }

   public void clearMdc() {
      org.slf4j.MDC.clear();
   }

   public Object putMdc(String key, Object value) {
      String var3;
      try {
         var3 = org.slf4j.MDC.get(key);
      } finally {
         if (value == null) {
            org.slf4j.MDC.remove(key);
         } else {
            org.slf4j.MDC.put(key, String.valueOf(value));
         }

      }

      return var3;
   }

   public Object getMdc(String key) {
      return org.slf4j.MDC.get(key);
   }

   public void removeMdc(String key) {
      org.slf4j.MDC.remove(key);
   }

   public Map<String, Object> getMdcMap() {
      Map copy = org.slf4j.MDC.getCopyOfContextMap();
      return (Map)(copy == null ? Collections.emptyMap() : new LinkedHashMap(copy));
   }
}
