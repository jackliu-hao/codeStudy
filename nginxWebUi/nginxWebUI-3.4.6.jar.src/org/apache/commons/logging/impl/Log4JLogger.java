/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
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
/*     */ public class Log4JLogger
/*     */   implements Log, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 5160705895411730424L;
/*  53 */   private static final String FQCN = Log4JLogger.class.getName();
/*     */ 
/*     */   
/*  56 */   private volatile transient Logger logger = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     Level level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String name;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Priority traceLevel;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  78 */     if (!Priority.class.isAssignableFrom(Level.class))
/*     */     {
/*  80 */       throw new InstantiationError("Log4J 1.2 not available");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  89 */       Priority _traceLevel = (Priority)Level.class.getDeclaredField("TRACE").get(null);
/*  90 */     } catch (Exception ex) {
/*     */       
/*  92 */       level = Level.DEBUG;
/*     */     } 
/*  94 */     traceLevel = (Priority)level;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Log4JLogger() {
/* 100 */     this.name = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Log4JLogger(String name) {
/* 107 */     this.name = name;
/* 108 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Log4JLogger(Logger logger) {
/* 115 */     if (logger == null) {
/* 116 */       throw new IllegalArgumentException("Warning - null logger in constructor; possible log4j misconfiguration.");
/*     */     }
/*     */     
/* 119 */     this.name = logger.getName();
/* 120 */     this.logger = logger;
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
/*     */   public void trace(Object message) {
/* 132 */     getLogger().log(FQCN, traceLevel, message, null);
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
/*     */   
/*     */   public void trace(Object message, Throwable t) {
/* 145 */     getLogger().log(FQCN, traceLevel, message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message) {
/* 155 */     getLogger().log(FQCN, (Priority)Level.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message, Throwable t) {
/* 166 */     getLogger().log(FQCN, (Priority)Level.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message) {
/* 176 */     getLogger().log(FQCN, (Priority)Level.INFO, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message, Throwable t) {
/* 187 */     getLogger().log(FQCN, (Priority)Level.INFO, message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message) {
/* 197 */     getLogger().log(FQCN, (Priority)Level.WARN, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message, Throwable t) {
/* 208 */     getLogger().log(FQCN, (Priority)Level.WARN, message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message) {
/* 218 */     getLogger().log(FQCN, (Priority)Level.ERROR, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message, Throwable t) {
/* 229 */     getLogger().log(FQCN, (Priority)Level.ERROR, message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message) {
/* 239 */     getLogger().log(FQCN, (Priority)Level.FATAL, message, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message, Throwable t) {
/* 250 */     getLogger().log(FQCN, (Priority)Level.FATAL, message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/* 257 */     Logger result = this.logger;
/* 258 */     if (result == null) {
/* 259 */       synchronized (this) {
/* 260 */         result = this.logger;
/* 261 */         if (result == null) {
/* 262 */           this.logger = result = Logger.getLogger(this.name);
/*     */         }
/*     */       } 
/*     */     }
/* 266 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 273 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 280 */     return getLogger().isEnabledFor((Priority)Level.ERROR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFatalEnabled() {
/* 287 */     return getLogger().isEnabledFor((Priority)Level.FATAL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 294 */     return getLogger().isInfoEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/* 303 */     return getLogger().isEnabledFor(traceLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 310 */     return getLogger().isEnabledFor((Priority)Level.WARN);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\Log4JLogger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */