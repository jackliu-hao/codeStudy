/*     */ package io.undertow.servlet.api;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.ExceptionLog;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import org.jboss.logging.Logger;
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
/*     */ public class LoggingExceptionHandler
/*     */   implements ExceptionHandler
/*     */ {
/*  42 */   public static final LoggingExceptionHandler DEFAULT = new LoggingExceptionHandler(Collections.emptyMap());
/*     */   
/*     */   private final Map<Class<? extends Throwable>, ExceptionDetails> exceptionDetails;
/*     */   
/*     */   public LoggingExceptionHandler(Map<Class<? extends Throwable>, ExceptionDetails> exceptionDetails) {
/*  47 */     this.exceptionDetails = exceptionDetails;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean handleThrowable(HttpServerExchange exchange, ServletRequest request, ServletResponse response, Throwable t) {
/*  52 */     ExceptionDetails details = null;
/*  53 */     if (!this.exceptionDetails.isEmpty()) {
/*  54 */       Class<?> c = t.getClass();
/*  55 */       while (c != null && c != Object.class) {
/*  56 */         details = this.exceptionDetails.get(c);
/*  57 */         if (details != null) {
/*     */           break;
/*     */         }
/*  60 */         c = c.getSuperclass();
/*     */       } 
/*     */     } 
/*     */     
/*  64 */     ExceptionLog log = t.getClass().<ExceptionLog>getAnnotation(ExceptionLog.class);
/*  65 */     if (details != null) {
/*  66 */       Logger.Level level = details.level;
/*  67 */       Logger.Level stackTraceLevel = details.stackTraceLevel;
/*  68 */       String category = details.category;
/*  69 */       handleCustomLog(exchange, t, level, stackTraceLevel, category);
/*  70 */     } else if (log != null) {
/*  71 */       Logger.Level level = log.value();
/*  72 */       Logger.Level stackTraceLevel = log.stackTraceLevel();
/*  73 */       String category = log.category();
/*  74 */       handleCustomLog(exchange, t, level, stackTraceLevel, category);
/*  75 */     } else if (t instanceof java.io.IOException) {
/*     */ 
/*     */       
/*  78 */       UndertowLogger.REQUEST_IO_LOGGER.debugf(t, "Exception handling request to %s", exchange.getRequestURI());
/*     */     } else {
/*  80 */       UndertowLogger.REQUEST_LOGGER.exceptionHandlingRequest(t, exchange.getRequestURI());
/*     */     } 
/*  82 */     return false;
/*     */   }
/*     */   private void handleCustomLog(HttpServerExchange exchange, Throwable t, Logger.Level level, Logger.Level stackTraceLevel, String category) {
/*     */     Logger logger;
/*  86 */     UndertowLogger undertowLogger = UndertowLogger.REQUEST_LOGGER;
/*  87 */     if (!category.isEmpty()) {
/*  88 */       logger = Logger.getLogger(category);
/*     */     }
/*  90 */     boolean stackTrace = true;
/*  91 */     if (stackTraceLevel.ordinal() > level.ordinal() && 
/*  92 */       !logger.isEnabled(stackTraceLevel)) {
/*  93 */       stackTrace = false;
/*     */     }
/*     */     
/*  96 */     if (stackTrace) {
/*  97 */       logger.logf(level, t, "Exception handling request to %s", exchange.getRequestURI());
/*     */     } else {
/*  99 */       logger.logf(level, "Exception handling request to %s: %s", exchange.getRequestURI(), t.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ExceptionDetails
/*     */   {
/*     */     final Logger.Level level;
/*     */     
/*     */     final Logger.Level stackTraceLevel;
/*     */     
/*     */     final String category;
/*     */     
/*     */     private ExceptionDetails(Logger.Level level, Logger.Level stackTraceLevel, String category) {
/* 113 */       this.level = level;
/* 114 */       this.stackTraceLevel = stackTraceLevel;
/* 115 */       this.category = category;
/*     */     }
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 120 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static final class Builder {
/* 124 */     private final Map<Class<? extends Throwable>, LoggingExceptionHandler.ExceptionDetails> exceptionDetails = new HashMap<>();
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder add(Class<? extends Throwable> exception, String category, Logger.Level level) {
/* 129 */       this.exceptionDetails.put(exception, new LoggingExceptionHandler.ExceptionDetails(level, Logger.Level.FATAL, category));
/* 130 */       return this;
/*     */     }
/*     */     public Builder add(Class<? extends Throwable> exception, String category) {
/* 133 */       this.exceptionDetails.put(exception, new LoggingExceptionHandler.ExceptionDetails(Logger.Level.ERROR, Logger.Level.FATAL, category));
/* 134 */       return this;
/*     */     }
/*     */     public Builder add(Class<? extends Throwable> exception, String category, Logger.Level level, Logger.Level stackTraceLevel) {
/* 137 */       this.exceptionDetails.put(exception, new LoggingExceptionHandler.ExceptionDetails(level, stackTraceLevel, category));
/* 138 */       return this;
/*     */     }
/*     */     
/*     */     public LoggingExceptionHandler build() {
/* 142 */       return new LoggingExceptionHandler(this.exceptionDetails);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\LoggingExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */