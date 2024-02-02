/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Queue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.Marker;
/*     */ import org.slf4j.event.EventRecodingLogger;
/*     */ import org.slf4j.event.LoggingEvent;
/*     */ import org.slf4j.event.SubstituteLoggingEvent;
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
/*     */ public class SubstituteLogger
/*     */   implements Logger
/*     */ {
/*     */   private final String name;
/*     */   private volatile Logger _delegate;
/*     */   private Boolean delegateEventAware;
/*     */   private Method logMethodCache;
/*     */   private EventRecodingLogger eventRecodingLogger;
/*     */   private Queue<SubstituteLoggingEvent> eventQueue;
/*     */   private final boolean createdPostInitialization;
/*     */   
/*     */   public SubstituteLogger(String name, Queue<SubstituteLoggingEvent> eventQueue, boolean createdPostInitialization) {
/*  59 */     this.name = name;
/*  60 */     this.eventQueue = eventQueue;
/*  61 */     this.createdPostInitialization = createdPostInitialization;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  65 */     return this.name;
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  69 */     return delegate().isTraceEnabled();
/*     */   }
/*     */   
/*     */   public void trace(String msg) {
/*  73 */     delegate().trace(msg);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg) {
/*  77 */     delegate().trace(format, arg);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg1, Object arg2) {
/*  81 */     delegate().trace(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object... arguments) {
/*  85 */     delegate().trace(format, arguments);
/*     */   }
/*     */   
/*     */   public void trace(String msg, Throwable t) {
/*  89 */     delegate().trace(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled(Marker marker) {
/*  93 */     return delegate().isTraceEnabled(marker);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg) {
/*  97 */     delegate().trace(marker, msg);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg) {
/* 101 */     delegate().trace(marker, format, arg);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2) {
/* 105 */     delegate().trace(marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object... arguments) {
/* 109 */     delegate().trace(marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg, Throwable t) {
/* 113 */     delegate().trace(marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 117 */     return delegate().isDebugEnabled();
/*     */   }
/*     */   
/*     */   public void debug(String msg) {
/* 121 */     delegate().debug(msg);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg) {
/* 125 */     delegate().debug(format, arg);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg1, Object arg2) {
/* 129 */     delegate().debug(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object... arguments) {
/* 133 */     delegate().debug(format, arguments);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable t) {
/* 137 */     delegate().debug(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled(Marker marker) {
/* 141 */     return delegate().isDebugEnabled(marker);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg) {
/* 145 */     delegate().debug(marker, msg);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg) {
/* 149 */     delegate().debug(marker, format, arg);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2) {
/* 153 */     delegate().debug(marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object... arguments) {
/* 157 */     delegate().debug(marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg, Throwable t) {
/* 161 */     delegate().debug(marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 165 */     return delegate().isInfoEnabled();
/*     */   }
/*     */   
/*     */   public void info(String msg) {
/* 169 */     delegate().info(msg);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg) {
/* 173 */     delegate().info(format, arg);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg1, Object arg2) {
/* 177 */     delegate().info(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void info(String format, Object... arguments) {
/* 181 */     delegate().info(format, arguments);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable t) {
/* 185 */     delegate().info(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled(Marker marker) {
/* 189 */     return delegate().isInfoEnabled(marker);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg) {
/* 193 */     delegate().info(marker, msg);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg) {
/* 197 */     delegate().info(marker, format, arg);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2) {
/* 201 */     delegate().info(marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object... arguments) {
/* 205 */     delegate().info(marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg, Throwable t) {
/* 209 */     delegate().info(marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 213 */     return delegate().isWarnEnabled();
/*     */   }
/*     */   
/*     */   public void warn(String msg) {
/* 217 */     delegate().warn(msg);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg) {
/* 221 */     delegate().warn(format, arg);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg1, Object arg2) {
/* 225 */     delegate().warn(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object... arguments) {
/* 229 */     delegate().warn(format, arguments);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable t) {
/* 233 */     delegate().warn(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled(Marker marker) {
/* 237 */     return delegate().isWarnEnabled(marker);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg) {
/* 241 */     delegate().warn(marker, msg);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg) {
/* 245 */     delegate().warn(marker, format, arg);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2) {
/* 249 */     delegate().warn(marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object... arguments) {
/* 253 */     delegate().warn(marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg, Throwable t) {
/* 257 */     delegate().warn(marker, msg, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 261 */     return delegate().isErrorEnabled();
/*     */   }
/*     */   
/*     */   public void error(String msg) {
/* 265 */     delegate().error(msg);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg) {
/* 269 */     delegate().error(format, arg);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg1, Object arg2) {
/* 273 */     delegate().error(format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void error(String format, Object... arguments) {
/* 277 */     delegate().error(format, arguments);
/*     */   }
/*     */   
/*     */   public void error(String msg, Throwable t) {
/* 281 */     delegate().error(msg, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled(Marker marker) {
/* 285 */     return delegate().isErrorEnabled(marker);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg) {
/* 289 */     delegate().error(marker, msg);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg) {
/* 293 */     delegate().error(marker, format, arg);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2) {
/* 297 */     delegate().error(marker, format, arg1, arg2);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object... arguments) {
/* 301 */     delegate().error(marker, format, arguments);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg, Throwable t) {
/* 305 */     delegate().error(marker, msg, t);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 310 */     if (this == o)
/* 311 */       return true; 
/* 312 */     if (o == null || getClass() != o.getClass()) {
/* 313 */       return false;
/*     */     }
/* 315 */     SubstituteLogger that = (SubstituteLogger)o;
/*     */     
/* 317 */     if (!this.name.equals(that.name)) {
/* 318 */       return false;
/*     */     }
/* 320 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 325 */     return this.name.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Logger delegate() {
/* 333 */     if (this._delegate != null) {
/* 334 */       return this._delegate;
/*     */     }
/* 336 */     if (this.createdPostInitialization) {
/* 337 */       return NOPLogger.NOP_LOGGER;
/*     */     }
/* 339 */     return getEventRecordingLogger();
/*     */   }
/*     */ 
/*     */   
/*     */   private Logger getEventRecordingLogger() {
/* 344 */     if (this.eventRecodingLogger == null) {
/* 345 */       this.eventRecodingLogger = new EventRecodingLogger(this, this.eventQueue);
/*     */     }
/* 347 */     return (Logger)this.eventRecodingLogger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDelegate(Logger delegate) {
/* 355 */     this._delegate = delegate;
/*     */   }
/*     */   
/*     */   public boolean isDelegateEventAware() {
/* 359 */     if (this.delegateEventAware != null) {
/* 360 */       return this.delegateEventAware.booleanValue();
/*     */     }
/*     */     try {
/* 363 */       this.logMethodCache = this._delegate.getClass().getMethod("log", new Class[] { LoggingEvent.class });
/* 364 */       this.delegateEventAware = Boolean.TRUE;
/* 365 */     } catch (NoSuchMethodException e) {
/* 366 */       this.delegateEventAware = Boolean.FALSE;
/*     */     } 
/* 368 */     return this.delegateEventAware.booleanValue();
/*     */   }
/*     */   
/*     */   public void log(LoggingEvent event) {
/* 372 */     if (isDelegateEventAware()) {
/*     */       
/* 374 */       try { this.logMethodCache.invoke(this._delegate, new Object[] { event }); }
/* 375 */       catch (IllegalAccessException illegalAccessException) {  }
/* 376 */       catch (IllegalArgumentException illegalArgumentException) {  }
/* 377 */       catch (InvocationTargetException invocationTargetException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDelegateNull() {
/* 384 */     return (this._delegate == null);
/*     */   }
/*     */   
/*     */   public boolean isDelegateNOP() {
/* 388 */     return this._delegate instanceof NOPLogger;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\SubstituteLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */