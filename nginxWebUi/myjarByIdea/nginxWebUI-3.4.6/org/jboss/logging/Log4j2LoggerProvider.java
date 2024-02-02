package org.jboss.logging;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;

final class Log4j2LoggerProvider implements LoggerProvider {
   public Log4j2Logger getLogger(String name) {
      return new Log4j2Logger(name);
   }

   public void clearMdc() {
      ThreadContext.clearMap();
   }

   public Object putMdc(String key, Object value) {
      String var3;
      try {
         var3 = ThreadContext.get(key);
      } finally {
         ThreadContext.put(key, String.valueOf(value));
      }

      return var3;
   }

   public Object getMdc(String key) {
      return ThreadContext.get(key);
   }

   public void removeMdc(String key) {
      ThreadContext.remove(key);
   }

   public Map<String, Object> getMdcMap() {
      return new HashMap(ThreadContext.getImmutableContext());
   }

   public void clearNdc() {
      ThreadContext.clearStack();
   }

   public String getNdc() {
      return ThreadContext.peek();
   }

   public int getNdcDepth() {
      return ThreadContext.getDepth();
   }

   public String popNdc() {
      return ThreadContext.pop();
   }

   public String peekNdc() {
      return ThreadContext.peek();
   }

   public void pushNdc(String message) {
      ThreadContext.push(message);
   }

   public void setNdcMaxDepth(int maxDepth) {
      ThreadContext.trim(maxDepth);
   }
}
