/*     */ package com.mysql.cj.log;
/*     */ 
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Jdk14Logger
/*     */   implements Log
/*     */ {
/*  39 */   private static final Level DEBUG = Level.FINE;
/*     */   
/*  41 */   private static final Level ERROR = Level.SEVERE;
/*     */   
/*  43 */   private static final Level FATAL = Level.SEVERE;
/*     */   
/*  45 */   private static final Level INFO = Level.INFO;
/*     */   
/*  47 */   private static final Level TRACE = Level.FINEST;
/*     */   
/*  49 */   private static final Level WARN = Level.WARNING;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   protected Logger jdkLogger = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jdk14Logger(String name) {
/*  63 */     this.jdkLogger = Logger.getLogger(name);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  67 */     return this.jdkLogger.isLoggable(Level.FINE);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  71 */     return this.jdkLogger.isLoggable(Level.SEVERE);
/*     */   }
/*     */   
/*     */   public boolean isFatalEnabled() {
/*  75 */     return this.jdkLogger.isLoggable(Level.SEVERE);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  79 */     return this.jdkLogger.isLoggable(Level.INFO);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  83 */     return this.jdkLogger.isLoggable(Level.FINEST);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  87 */     return this.jdkLogger.isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logDebug(Object message) {
/*  97 */     logInternal(DEBUG, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logDebug(Object message, Throwable exception) {
/* 109 */     logInternal(DEBUG, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logError(Object message) {
/* 119 */     logInternal(ERROR, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logError(Object message, Throwable exception) {
/* 131 */     logInternal(ERROR, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logFatal(Object message) {
/* 141 */     logInternal(FATAL, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logFatal(Object message, Throwable exception) {
/* 153 */     logInternal(FATAL, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logInfo(Object message) {
/* 163 */     logInternal(INFO, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logInfo(Object message, Throwable exception) {
/* 175 */     logInternal(INFO, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logTrace(Object message) {
/* 185 */     logInternal(TRACE, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logTrace(Object message, Throwable exception) {
/* 197 */     logInternal(TRACE, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logWarn(Object message) {
/* 207 */     logInternal(WARN, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logWarn(Object message, Throwable exception) {
/* 219 */     logInternal(WARN, message, exception);
/*     */   }
/*     */   
/*     */   private static final int findCallerStackDepth(StackTraceElement[] stackTrace) {
/* 223 */     int numFrames = stackTrace.length;
/*     */     
/* 225 */     for (int i = 0; i < numFrames; i++) {
/* 226 */       String callerClassName = stackTrace[i].getClassName();
/*     */       
/* 228 */       if (!callerClassName.startsWith("com.mysql.cj") && !callerClassName.startsWith("com.mysql.cj.core") && 
/* 229 */         !callerClassName.startsWith("com.mysql.cj.jdbc")) {
/* 230 */         return i;
/*     */       }
/*     */     } 
/*     */     
/* 234 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void logInternal(Level level, Object msg, Throwable exception) {
/* 242 */     if (this.jdkLogger.isLoggable(level)) {
/* 243 */       String messageAsString = null;
/* 244 */       String callerMethodName = "N/A";
/* 245 */       String callerClassName = "N/A";
/*     */ 
/*     */ 
/*     */       
/* 249 */       if (msg instanceof ProfilerEvent) {
/* 250 */         messageAsString = msg.toString();
/*     */       } else {
/* 252 */         Throwable locationException = new Throwable();
/* 253 */         StackTraceElement[] locations = locationException.getStackTrace();
/*     */         
/* 255 */         int frameIdx = findCallerStackDepth(locations);
/*     */         
/* 257 */         if (frameIdx != 0) {
/* 258 */           callerClassName = locations[frameIdx].getClassName();
/* 259 */           callerMethodName = locations[frameIdx].getMethodName();
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 264 */         messageAsString = String.valueOf(msg);
/*     */       } 
/*     */       
/* 267 */       if (exception == null) {
/* 268 */         this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString);
/*     */       } else {
/* 270 */         this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString, exception);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\Jdk14Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */