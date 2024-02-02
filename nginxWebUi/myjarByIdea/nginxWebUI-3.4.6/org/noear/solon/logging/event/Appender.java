package org.noear.solon.logging.event;

public interface Appender {
   default Level getDefaultLevel() {
      return Level.TRACE;
   }

   default void start() {
   }

   default void stop() {
   }

   String getName();

   void setName(String name);

   void append(LogEvent logEvent);
}
