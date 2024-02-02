/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log.Hierarchy;
/*     */ import org.apache.log.Logger;
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
/*     */ public class LogKitLogger
/*     */   implements Log, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3768538055836059519L;
/*  44 */   protected volatile transient Logger logger = null;
/*     */ 
/*     */   
/*  47 */   protected String name = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogKitLogger(String name) {
/*  58 */     this.name = name;
/*  59 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/*  68 */     Logger result = this.logger;
/*  69 */     if (result == null) {
/*  70 */       synchronized (this) {
/*  71 */         result = this.logger;
/*  72 */         if (result == null) {
/*  73 */           this.logger = result = Hierarchy.getDefaultHierarchy().getLoggerFor(this.name);
/*     */         }
/*     */       } 
/*     */     }
/*  77 */     return result;
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
/*  89 */     debug(message);
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
/* 100 */     debug(message, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message) {
/* 110 */     if (message != null) {
/* 111 */       getLogger().debug(String.valueOf(message));
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
/*     */   public void debug(Object message, Throwable t) {
/* 123 */     if (message != null) {
/* 124 */       getLogger().debug(String.valueOf(message), t);
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
/* 135 */     if (message != null) {
/* 136 */       getLogger().info(String.valueOf(message));
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
/* 148 */     if (message != null) {
/* 149 */       getLogger().info(String.valueOf(message), t);
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
/* 160 */     if (message != null) {
/* 161 */       getLogger().warn(String.valueOf(message));
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
/* 173 */     if (message != null) {
/* 174 */       getLogger().warn(String.valueOf(message), t);
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
/* 185 */     if (message != null) {
/* 186 */       getLogger().error(String.valueOf(message));
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
/* 198 */     if (message != null) {
/* 199 */       getLogger().error(String.valueOf(message), t);
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
/* 210 */     if (message != null) {
/* 211 */       getLogger().fatalError(String.valueOf(message));
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
/* 223 */     if (message != null) {
/* 224 */       getLogger().fatalError(String.valueOf(message), t);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 232 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 239 */     return getLogger().isErrorEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFatalEnabled() {
/* 246 */     return getLogger().isFatalErrorEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 253 */     return getLogger().isInfoEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/* 260 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 267 */     return getLogger().isWarnEnabled();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\LogKitLogger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */