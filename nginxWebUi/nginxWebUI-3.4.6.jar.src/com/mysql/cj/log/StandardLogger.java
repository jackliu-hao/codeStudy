/*     */ package com.mysql.cj.log;
/*     */ 
/*     */ import com.mysql.cj.util.LogUtils;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.util.Date;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardLogger
/*     */   implements Log
/*     */ {
/*     */   private static final int FATAL = 0;
/*     */   private static final int ERROR = 1;
/*     */   private static final int WARN = 2;
/*     */   private static final int INFO = 3;
/*     */   private static final int DEBUG = 4;
/*     */   private static final int TRACE = 5;
/*     */   private boolean logLocationInfo = true;
/*     */   
/*     */   public StandardLogger(String name) {
/*  62 */     this(name, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardLogger(String name, boolean logLocationInfo) {
/*  72 */     this.logLocationInfo = logLocationInfo;
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  76 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  80 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isFatalEnabled() {
/*  84 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  88 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  92 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logDebug(Object message) {
/* 106 */     logInternal(4, message, null);
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
/* 118 */     logInternal(4, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logError(Object message) {
/* 128 */     logInternal(1, message, null);
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
/* 140 */     logInternal(1, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logFatal(Object message) {
/* 150 */     logInternal(0, message, null);
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
/* 162 */     logInternal(0, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logInfo(Object message) {
/* 172 */     logInternal(3, message, null);
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
/* 184 */     logInternal(3, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logTrace(Object message) {
/* 194 */     logInternal(5, message, null);
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
/* 206 */     logInternal(5, message, exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logWarn(Object message) {
/* 216 */     logInternal(2, message, null);
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
/* 228 */     logInternal(2, message, exception);
/*     */   }
/*     */   
/*     */   protected String logInternal(int level, Object msg, Throwable exception) {
/* 232 */     StringBuilder msgBuf = new StringBuilder();
/* 233 */     msgBuf.append((new Date()).toString());
/* 234 */     msgBuf.append(" ");
/*     */     
/* 236 */     switch (level) {
/*     */       case 0:
/* 238 */         msgBuf.append("FATAL: ");
/*     */         break;
/*     */ 
/*     */       
/*     */       case 1:
/* 243 */         msgBuf.append("ERROR: ");
/*     */         break;
/*     */ 
/*     */       
/*     */       case 2:
/* 248 */         msgBuf.append("WARN: ");
/*     */         break;
/*     */ 
/*     */       
/*     */       case 3:
/* 253 */         msgBuf.append("INFO: ");
/*     */         break;
/*     */ 
/*     */       
/*     */       case 4:
/* 258 */         msgBuf.append("DEBUG: ");
/*     */         break;
/*     */ 
/*     */       
/*     */       case 5:
/* 263 */         msgBuf.append("TRACE: ");
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 268 */     if (msg instanceof ProfilerEvent) {
/* 269 */       msgBuf.append(msg.toString());
/*     */     } else {
/*     */       
/* 272 */       if (this.logLocationInfo && level != 5) {
/* 273 */         Throwable locationException = new Throwable();
/* 274 */         msgBuf.append(LogUtils.findCallingClassAndMethod(locationException));
/* 275 */         msgBuf.append(" ");
/*     */       } 
/*     */       
/* 278 */       if (msg != null) {
/* 279 */         msgBuf.append(String.valueOf(msg));
/*     */       }
/*     */     } 
/*     */     
/* 283 */     if (exception != null) {
/* 284 */       msgBuf.append("\n");
/* 285 */       msgBuf.append("\n");
/* 286 */       msgBuf.append("EXCEPTION STACK TRACE:");
/* 287 */       msgBuf.append("\n");
/* 288 */       msgBuf.append("\n");
/* 289 */       msgBuf.append(Util.stackTraceToString(exception));
/*     */     } 
/*     */     
/* 292 */     String messageAsString = msgBuf.toString();
/*     */     
/* 294 */     System.err.println(messageAsString);
/*     */     
/* 296 */     return messageAsString;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\StandardLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */