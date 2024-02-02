/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Jdk14Logger
/*     */   implements Log, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 4784713551416303804L;
/*  44 */   protected static final Level dummyLevel = Level.FINE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient Logger logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jdk14Logger(String name) {
/*  63 */     this.logger = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     this.name = null;
/*     */     this.name = name;
/*     */     this.logger = getLogger();
/*     */   }
/*     */   protected void log(Level level, String msg, Throwable ex) {
/*  73 */     Logger logger = getLogger();
/*  74 */     if (logger.isLoggable(level)) {
/*     */       
/*  76 */       Throwable dummyException = new Throwable();
/*  77 */       StackTraceElement[] locations = dummyException.getStackTrace();
/*     */       
/*  79 */       String cname = this.name;
/*  80 */       String method = "unknown";
/*     */       
/*  82 */       if (locations != null && locations.length > 2) {
/*  83 */         StackTraceElement caller = locations[2];
/*  84 */         method = caller.getMethodName();
/*     */       } 
/*  86 */       if (ex == null) {
/*  87 */         logger.logp(level, cname, method, msg);
/*     */       } else {
/*  89 */         logger.logp(level, cname, method, msg, ex);
/*     */       } 
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
/*     */   
/*     */   public void debug(Object message) {
/* 103 */     log(Level.FINE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(Object message, Throwable exception) {
/* 114 */     log(Level.FINE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message) {
/* 124 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(Object message, Throwable exception) {
/* 135 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message) {
/* 145 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fatal(Object message, Throwable exception) {
/* 156 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/* 163 */     if (this.logger == null) {
/* 164 */       this.logger = Logger.getLogger(this.name);
/*     */     }
/* 166 */     return this.logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message) {
/* 176 */     log(Level.INFO, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(Object message, Throwable exception) {
/* 187 */     log(Level.INFO, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 194 */     return getLogger().isLoggable(Level.FINE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 201 */     return getLogger().isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFatalEnabled() {
/* 208 */     return getLogger().isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 215 */     return getLogger().isLoggable(Level.INFO);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTraceEnabled() {
/* 222 */     return getLogger().isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 229 */     return getLogger().isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Object message) {
/* 239 */     log(Level.FINEST, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trace(Object message, Throwable exception) {
/* 250 */     log(Level.FINEST, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message) {
/* 260 */     log(Level.WARNING, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void warn(Object message, Throwable exception) {
/* 271 */     log(Level.WARNING, String.valueOf(message), exception);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\Jdk14Logger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */