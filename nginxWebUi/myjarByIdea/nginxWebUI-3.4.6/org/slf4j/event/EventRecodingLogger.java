package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.SubstituteLogger;

public class EventRecodingLogger implements Logger {
   String name;
   SubstituteLogger logger;
   Queue<SubstituteLoggingEvent> eventQueue;
   static final boolean RECORD_ALL_EVENTS = true;

   public EventRecodingLogger(SubstituteLogger logger, Queue<SubstituteLoggingEvent> eventQueue) {
      this.logger = logger;
      this.name = logger.getName();
      this.eventQueue = eventQueue;
   }

   public String getName() {
      return this.name;
   }

   public boolean isTraceEnabled() {
      return true;
   }

   public void trace(String msg) {
      this.recordEvent_0Args(Level.TRACE, (Marker)null, msg, (Throwable)null);
   }

   public void trace(String format, Object arg) {
      this.recordEvent_1Args(Level.TRACE, (Marker)null, format, arg);
   }

   public void trace(String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.TRACE, (Marker)null, format, arg1, arg2);
   }

   public void trace(String format, Object... arguments) {
      this.recordEventArgArray(Level.TRACE, (Marker)null, format, arguments);
   }

   public void trace(String msg, Throwable t) {
      this.recordEvent_0Args(Level.TRACE, (Marker)null, msg, t);
   }

   public boolean isTraceEnabled(Marker marker) {
      return true;
   }

   public void trace(Marker marker, String msg) {
      this.recordEvent_0Args(Level.TRACE, marker, msg, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object arg) {
      this.recordEvent_1Args(Level.TRACE, marker, format, arg);
   }

   public void trace(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.TRACE, marker, format, arg1, arg2);
   }

   public void trace(Marker marker, String format, Object... argArray) {
      this.recordEventArgArray(Level.TRACE, marker, format, argArray);
   }

   public void trace(Marker marker, String msg, Throwable t) {
      this.recordEvent_0Args(Level.TRACE, marker, msg, t);
   }

   public boolean isDebugEnabled() {
      return true;
   }

   public void debug(String msg) {
      this.recordEvent_0Args(Level.DEBUG, (Marker)null, msg, (Throwable)null);
   }

   public void debug(String format, Object arg) {
      this.recordEvent_1Args(Level.DEBUG, (Marker)null, format, arg);
   }

   public void debug(String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.DEBUG, (Marker)null, format, arg1, arg2);
   }

   public void debug(String format, Object... arguments) {
      this.recordEventArgArray(Level.DEBUG, (Marker)null, format, arguments);
   }

   public void debug(String msg, Throwable t) {
      this.recordEvent_0Args(Level.DEBUG, (Marker)null, msg, t);
   }

   public boolean isDebugEnabled(Marker marker) {
      return true;
   }

   public void debug(Marker marker, String msg) {
      this.recordEvent_0Args(Level.DEBUG, marker, msg, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object arg) {
      this.recordEvent_1Args(Level.DEBUG, marker, format, arg);
   }

   public void debug(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.DEBUG, marker, format, arg1, arg2);
   }

   public void debug(Marker marker, String format, Object... arguments) {
      this.recordEventArgArray(Level.DEBUG, marker, format, arguments);
   }

   public void debug(Marker marker, String msg, Throwable t) {
      this.recordEvent_0Args(Level.DEBUG, marker, msg, t);
   }

   public boolean isInfoEnabled() {
      return true;
   }

   public void info(String msg) {
      this.recordEvent_0Args(Level.INFO, (Marker)null, msg, (Throwable)null);
   }

   public void info(String format, Object arg) {
      this.recordEvent_1Args(Level.INFO, (Marker)null, format, arg);
   }

   public void info(String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.INFO, (Marker)null, format, arg1, arg2);
   }

   public void info(String format, Object... arguments) {
      this.recordEventArgArray(Level.INFO, (Marker)null, format, arguments);
   }

   public void info(String msg, Throwable t) {
      this.recordEvent_0Args(Level.INFO, (Marker)null, msg, t);
   }

   public boolean isInfoEnabled(Marker marker) {
      return true;
   }

   public void info(Marker marker, String msg) {
      this.recordEvent_0Args(Level.INFO, marker, msg, (Throwable)null);
   }

   public void info(Marker marker, String format, Object arg) {
      this.recordEvent_1Args(Level.INFO, marker, format, arg);
   }

   public void info(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.INFO, marker, format, arg1, arg2);
   }

   public void info(Marker marker, String format, Object... arguments) {
      this.recordEventArgArray(Level.INFO, marker, format, arguments);
   }

   public void info(Marker marker, String msg, Throwable t) {
      this.recordEvent_0Args(Level.INFO, marker, msg, t);
   }

   public boolean isWarnEnabled() {
      return true;
   }

   public void warn(String msg) {
      this.recordEvent_0Args(Level.WARN, (Marker)null, msg, (Throwable)null);
   }

   public void warn(String format, Object arg) {
      this.recordEvent_1Args(Level.WARN, (Marker)null, format, arg);
   }

   public void warn(String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.WARN, (Marker)null, format, arg1, arg2);
   }

   public void warn(String format, Object... arguments) {
      this.recordEventArgArray(Level.WARN, (Marker)null, format, arguments);
   }

   public void warn(String msg, Throwable t) {
      this.recordEvent_0Args(Level.WARN, (Marker)null, msg, t);
   }

   public boolean isWarnEnabled(Marker marker) {
      return true;
   }

   public void warn(Marker marker, String msg) {
      this.recordEvent_0Args(Level.WARN, marker, msg, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object arg) {
      this.recordEvent_1Args(Level.WARN, marker, format, arg);
   }

   public void warn(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.WARN, marker, format, arg1, arg2);
   }

   public void warn(Marker marker, String format, Object... arguments) {
      this.recordEventArgArray(Level.WARN, marker, format, arguments);
   }

   public void warn(Marker marker, String msg, Throwable t) {
      this.recordEvent_0Args(Level.WARN, marker, msg, t);
   }

   public boolean isErrorEnabled() {
      return true;
   }

   public void error(String msg) {
      this.recordEvent_0Args(Level.ERROR, (Marker)null, msg, (Throwable)null);
   }

   public void error(String format, Object arg) {
      this.recordEvent_1Args(Level.ERROR, (Marker)null, format, arg);
   }

   public void error(String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.ERROR, (Marker)null, format, arg1, arg2);
   }

   public void error(String format, Object... arguments) {
      this.recordEventArgArray(Level.ERROR, (Marker)null, format, arguments);
   }

   public void error(String msg, Throwable t) {
      this.recordEvent_0Args(Level.ERROR, (Marker)null, msg, t);
   }

   public boolean isErrorEnabled(Marker marker) {
      return true;
   }

   public void error(Marker marker, String msg) {
      this.recordEvent_0Args(Level.ERROR, marker, msg, (Throwable)null);
   }

   public void error(Marker marker, String format, Object arg) {
      this.recordEvent_1Args(Level.ERROR, marker, format, arg);
   }

   public void error(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent2Args(Level.ERROR, marker, format, arg1, arg2);
   }

   public void error(Marker marker, String format, Object... arguments) {
      this.recordEventArgArray(Level.ERROR, marker, format, arguments);
   }

   public void error(Marker marker, String msg, Throwable t) {
      this.recordEvent_0Args(Level.ERROR, marker, msg, t);
   }

   private void recordEvent_0Args(Level level, Marker marker, String msg, Throwable t) {
      this.recordEvent(level, marker, msg, (Object[])null, t);
   }

   private void recordEvent_1Args(Level level, Marker marker, String msg, Object arg1) {
      this.recordEvent(level, marker, msg, new Object[]{arg1}, (Throwable)null);
   }

   private void recordEvent2Args(Level level, Marker marker, String msg, Object arg1, Object arg2) {
      if (arg2 instanceof Throwable) {
         this.recordEvent(level, marker, msg, new Object[]{arg1}, (Throwable)arg2);
      } else {
         this.recordEvent(level, marker, msg, new Object[]{arg1, arg2}, (Throwable)null);
      }

   }

   private void recordEventArgArray(Level level, Marker marker, String msg, Object[] args) {
      Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(args);
      if (throwableCandidate != null) {
         Object[] trimmedCopy = MessageFormatter.trimmedCopy(args);
         this.recordEvent(level, marker, msg, trimmedCopy, throwableCandidate);
      } else {
         this.recordEvent(level, marker, msg, args, (Throwable)null);
      }

   }

   private void recordEvent(Level level, Marker marker, String msg, Object[] args, Throwable throwable) {
      SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
      loggingEvent.setTimeStamp(System.currentTimeMillis());
      loggingEvent.setLevel(level);
      loggingEvent.setLogger(this.logger);
      loggingEvent.setLoggerName(this.name);
      loggingEvent.setMarker(marker);
      loggingEvent.setMessage(msg);
      loggingEvent.setThreadName(Thread.currentThread().getName());
      loggingEvent.setArgumentArray(args);
      loggingEvent.setThrowable(throwable);
      this.eventQueue.add(loggingEvent);
   }
}
