/*     */ package com.mysql.cj.log;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class Slf4JLogger
/*     */   implements Log
/*     */ {
/*     */   private Logger log;
/*     */   
/*     */   public Slf4JLogger(String name) {
/*  39 */     this.log = LoggerFactory.getLogger(name);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  43 */     return this.log.isDebugEnabled();
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  47 */     return this.log.isErrorEnabled();
/*     */   }
/*     */   
/*     */   public boolean isFatalEnabled() {
/*  51 */     return this.log.isErrorEnabled();
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  55 */     return this.log.isInfoEnabled();
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  59 */     return this.log.isTraceEnabled();
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  63 */     return this.log.isWarnEnabled();
/*     */   }
/*     */   
/*     */   public void logDebug(Object msg) {
/*  67 */     this.log.debug(msg.toString());
/*     */   }
/*     */   
/*     */   public void logDebug(Object msg, Throwable thrown) {
/*  71 */     this.log.debug(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logError(Object msg) {
/*  75 */     this.log.error(msg.toString());
/*     */   }
/*     */   
/*     */   public void logError(Object msg, Throwable thrown) {
/*  79 */     this.log.error(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logFatal(Object msg) {
/*  83 */     this.log.error(msg.toString());
/*     */   }
/*     */   
/*     */   public void logFatal(Object msg, Throwable thrown) {
/*  87 */     this.log.error(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logInfo(Object msg) {
/*  91 */     this.log.info(msg.toString());
/*     */   }
/*     */   
/*     */   public void logInfo(Object msg, Throwable thrown) {
/*  95 */     this.log.info(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logTrace(Object msg) {
/*  99 */     this.log.trace(msg.toString());
/*     */   }
/*     */   
/*     */   public void logTrace(Object msg, Throwable thrown) {
/* 103 */     this.log.trace(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logWarn(Object msg) {
/* 107 */     this.log.warn(msg.toString());
/*     */   }
/*     */   
/*     */   public void logWarn(Object msg, Throwable thrown) {
/* 111 */     this.log.warn(msg.toString(), thrown);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\Slf4JLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */