/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import org.apache.avalon.framework.logger.Logger;
/*     */ import org.apache.commons.logging.Log;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AvalonLogger
/*     */   implements Log
/*     */ {
/*  55 */   private static volatile Logger defaultLogger = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final transient Logger logger;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AvalonLogger(Logger logger) {
/*  66 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AvalonLogger(String name) {
/*  76 */     if (defaultLogger == null) {
/*  77 */       throw new NullPointerException("default logger has to be specified if this constructor is used!");
/*     */     }
/*  79 */     this.logger = defaultLogger.getChildLogger(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/*  88 */     return this.logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDefaultLogger(Logger logger) {
/*  98 */     defaultLogger = logger;
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
/* 109 */     if (getLogger().isDebugEnabled()) {
/* 110 */       getLogger().debug(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message) {
/* 121 */     if (getLogger().isDebugEnabled()) {
/* 122 */       getLogger().debug(String.valueOf(message));
/*     */     }
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
/* 134 */     if (getLogger().isErrorEnabled()) {
/* 135 */       getLogger().error(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message) {
/* 146 */     if (getLogger().isErrorEnabled()) {
/* 147 */       getLogger().error(String.valueOf(message));
/*     */     }
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
/* 159 */     if (getLogger().isFatalErrorEnabled()) {
/* 160 */       getLogger().fatalError(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message) {
/* 171 */     if (getLogger().isFatalErrorEnabled()) {
/* 172 */       getLogger().fatalError(String.valueOf(message));
/*     */     }
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
/* 184 */     if (getLogger().isInfoEnabled()) {
/* 185 */       getLogger().info(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message) {
/* 196 */     if (getLogger().isInfoEnabled()) {
/* 197 */       getLogger().info(String.valueOf(message));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 206 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 214 */     return getLogger().isErrorEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFatalEnabled() {
/* 222 */     return getLogger().isFatalErrorEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 230 */     return getLogger().isInfoEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/* 238 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 246 */     return getLogger().isWarnEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Object message, Throwable t) {
/* 257 */     if (getLogger().isDebugEnabled()) {
/* 258 */       getLogger().debug(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Object message) {
/* 269 */     if (getLogger().isDebugEnabled()) {
/* 270 */       getLogger().debug(String.valueOf(message));
/*     */     }
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
/* 282 */     if (getLogger().isWarnEnabled()) {
/* 283 */       getLogger().warn(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message) {
/* 294 */     if (getLogger().isWarnEnabled())
/* 295 */       getLogger().warn(String.valueOf(message)); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\AvalonLogger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */