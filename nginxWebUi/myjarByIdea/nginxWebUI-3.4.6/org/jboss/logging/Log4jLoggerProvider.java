package org.jboss.logging;

import java.util.Collections;
import java.util.Map;

final class Log4jLoggerProvider implements LoggerProvider {
   public Logger getLogger(String name) {
      return new Log4jLogger("".equals(name) ? "ROOT" : name);
   }

   public void clearMdc() {
      org.apache.log4j.MDC.clear();
   }

   public Object getMdc(String key) {
      return org.apache.log4j.MDC.get(key);
   }

   public Map<String, Object> getMdcMap() {
      Map<String, Object> map = org.apache.log4j.MDC.getContext();
      return (Map)(map == null ? Collections.emptyMap() : map);
   }

   public Object putMdc(String key, Object val) {
      Object var3;
      try {
         var3 = org.apache.log4j.MDC.get(key);
      } finally {
         org.apache.log4j.MDC.put(key, val);
      }

      return var3;
   }

   public void removeMdc(String key) {
      org.apache.log4j.MDC.remove(key);
   }

   public void clearNdc() {
      org.apache.log4j.NDC.remove();
   }

   public String getNdc() {
      return org.apache.log4j.NDC.get();
   }

   public int getNdcDepth() {
      return org.apache.log4j.NDC.getDepth();
   }

   public String peekNdc() {
      return org.apache.log4j.NDC.peek();
   }

   public String popNdc() {
      return org.apache.log4j.NDC.pop();
   }

   public void pushNdc(String message) {
      org.apache.log4j.NDC.push(message);
   }

   public void setNdcMaxDepth(int maxDepth) {
      org.apache.log4j.NDC.setMaxDepth(maxDepth);
   }
}
