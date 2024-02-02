/*     */ package org.slf4j.event;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.Marker;
/*     */ import org.slf4j.helpers.MessageFormatter;
/*     */ import org.slf4j.helpers.SubstituteLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EventRecodingLogger
/*     */   implements Logger
/*     */ {
/*     */   String name;
/*     */   SubstituteLogger logger;
/*     */   Queue<SubstituteLoggingEvent> eventQueue;
/*     */   static final boolean RECORD_ALL_EVENTS = true;
/*     */   
/*     */   public EventRecodingLogger(SubstituteLogger logger, Queue<SubstituteLoggingEvent> eventQueue) {
/*  30 */     this.logger = logger;
/*  31 */     this.name = logger.getName();
/*  32 */     this.eventQueue = eventQueue;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  36 */     return this.name;
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  40 */     return true;
/*     */   }
/*     */   
/*     */   public void trace(String msg) {
/*  44 */     recordEvent_0Args(Level.TRACE, null, msg, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg) {
/*  48 */     recordEvent_1Args(Level.TRACE, null, format, arg);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg1, Object arg2) {
/*  52 */     recordEvent2Args(Level.TRACE, null, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object... arguments) {
/*  56 */     recordEventArgArray(Level.TRACE, null, format, arguments);
/*     */   }
/*     */   
/*     */   public void trace(String msg, Throwable t) {
/*  60 */     recordEvent_0Args(Level.TRACE, null, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled(Marker marker) {
/*  64 */     return true;
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg) {
/*  68 */     recordEvent_0Args(Level.TRACE, marker, msg, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg) {
/*  72 */     recordEvent_1Args(Level.TRACE, marker, format, arg);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2) {
/*  76 */     recordEvent2Args(Level.TRACE, marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object... argArray) {
/*  80 */     recordEventArgArray(Level.TRACE, marker, format, argArray);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg, Throwable t) {
/*  84 */     recordEvent_0Args(Level.TRACE, marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  88 */     return true;
/*     */   }
/*     */   
/*     */   public void debug(String msg) {
/*  92 */     recordEvent_0Args(Level.DEBUG, null, msg, null);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg) {
/*  96 */     recordEvent_1Args(Level.DEBUG, null, format, arg);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg1, Object arg2) {
/* 100 */     recordEvent2Args(Level.DEBUG, null, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object... arguments) {
/* 104 */     recordEventArgArray(Level.DEBUG, null, format, arguments);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable t) {
/* 108 */     recordEvent_0Args(Level.DEBUG, null, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled(Marker marker) {
/* 112 */     return true;
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg) {
/* 116 */     recordEvent_0Args(Level.DEBUG, marker, msg, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg) {
/* 120 */     recordEvent_1Args(Level.DEBUG, marker, format, arg);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2) {
/* 124 */     recordEvent2Args(Level.DEBUG, marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object... arguments) {
/* 128 */     recordEventArgArray(Level.DEBUG, marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg, Throwable t) {
/* 132 */     recordEvent_0Args(Level.DEBUG, marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 136 */     return true;
/*     */   }
/*     */   
/*     */   public void info(String msg) {
/* 140 */     recordEvent_0Args(Level.INFO, null, msg, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg) {
/* 144 */     recordEvent_1Args(Level.INFO, null, format, arg);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg1, Object arg2) {
/* 148 */     recordEvent2Args(Level.INFO, null, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void info(String format, Object... arguments) {
/* 152 */     recordEventArgArray(Level.INFO, null, format, arguments);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable t) {
/* 156 */     recordEvent_0Args(Level.INFO, null, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled(Marker marker) {
/* 160 */     return true;
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg) {
/* 164 */     recordEvent_0Args(Level.INFO, marker, msg, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg) {
/* 168 */     recordEvent_1Args(Level.INFO, marker, format, arg);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2) {
/* 172 */     recordEvent2Args(Level.INFO, marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object... arguments) {
/* 176 */     recordEventArgArray(Level.INFO, marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg, Throwable t) {
/* 180 */     recordEvent_0Args(Level.INFO, marker, msg, t);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 185 */     return true;
/*     */   }
/*     */   
/*     */   public void warn(String msg) {
/* 189 */     recordEvent_0Args(Level.WARN, null, msg, null);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg) {
/* 193 */     recordEvent_1Args(Level.WARN, null, format, arg);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg1, Object arg2) {
/* 197 */     recordEvent2Args(Level.WARN, null, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object... arguments) {
/* 201 */     recordEventArgArray(Level.WARN, null, format, arguments);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable t) {
/* 205 */     recordEvent_0Args(Level.WARN, null, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled(Marker marker) {
/* 209 */     return true;
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg) {
/* 213 */     recordEvent_0Args(Level.WARN, marker, msg, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg) {
/* 217 */     recordEvent_1Args(Level.WARN, marker, format, arg);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2) {
/* 221 */     recordEvent2Args(Level.WARN, marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object... arguments) {
/* 225 */     recordEventArgArray(Level.WARN, marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg, Throwable t) {
/* 229 */     recordEvent_0Args(Level.WARN, marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 233 */     return true;
/*     */   }
/*     */   
/*     */   public void error(String msg) {
/* 237 */     recordEvent_0Args(Level.ERROR, null, msg, null);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg) {
/* 241 */     recordEvent_1Args(Level.ERROR, null, format, arg);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg1, Object arg2) {
/* 245 */     recordEvent2Args(Level.ERROR, null, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void error(String format, Object... arguments) {
/* 249 */     recordEventArgArray(Level.ERROR, null, format, arguments);
/*     */   }
/*     */   
/*     */   public void error(String msg, Throwable t) {
/* 253 */     recordEvent_0Args(Level.ERROR, null, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled(Marker marker) {
/* 257 */     return true;
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg) {
/* 261 */     recordEvent_0Args(Level.ERROR, marker, msg, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg) {
/* 265 */     recordEvent_1Args(Level.ERROR, marker, format, arg);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2) {
/* 269 */     recordEvent2Args(Level.ERROR, marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object... arguments) {
/* 273 */     recordEventArgArray(Level.ERROR, marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg, Throwable t) {
/* 277 */     recordEvent_0Args(Level.ERROR, marker, msg, t);
/*     */   }
/*     */   
/*     */   private void recordEvent_0Args(Level level, Marker marker, String msg, Throwable t) {
/* 281 */     recordEvent(level, marker, msg, null, t);
/*     */   }
/*     */   
/*     */   private void recordEvent_1Args(Level level, Marker marker, String msg, Object arg1) {
/* 285 */     recordEvent(level, marker, msg, new Object[] { arg1 }, null);
/*     */   }
/*     */   
/*     */   private void recordEvent2Args(Level level, Marker marker, String msg, Object arg1, Object arg2) {
/* 289 */     if (arg2 instanceof Throwable) {
/* 290 */       recordEvent(level, marker, msg, new Object[] { arg1 }, (Throwable)arg2);
/*     */     } else {
/* 292 */       recordEvent(level, marker, msg, new Object[] { arg1, arg2 }, null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void recordEventArgArray(Level level, Marker marker, String msg, Object[] args) {
/* 297 */     Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(args);
/* 298 */     if (throwableCandidate != null) {
/* 299 */       Object[] trimmedCopy = MessageFormatter.trimmedCopy(args);
/* 300 */       recordEvent(level, marker, msg, trimmedCopy, throwableCandidate);
/*     */     } else {
/* 302 */       recordEvent(level, marker, msg, args, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void recordEvent(Level level, Marker marker, String msg, Object[] args, Throwable throwable) {
/* 309 */     SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
/* 310 */     loggingEvent.setTimeStamp(System.currentTimeMillis());
/* 311 */     loggingEvent.setLevel(level);
/* 312 */     loggingEvent.setLogger(this.logger);
/* 313 */     loggingEvent.setLoggerName(this.name);
/* 314 */     loggingEvent.setMarker(marker);
/* 315 */     loggingEvent.setMessage(msg);
/* 316 */     loggingEvent.setThreadName(Thread.currentThread().getName());
/*     */     
/* 318 */     loggingEvent.setArgumentArray(args);
/* 319 */     loggingEvent.setThrowable(throwable);
/*     */     
/* 321 */     this.eventQueue.add(loggingEvent);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\event\EventRecodingLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */