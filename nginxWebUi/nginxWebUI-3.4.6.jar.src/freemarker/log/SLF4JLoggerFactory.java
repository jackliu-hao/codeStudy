/*     */ package freemarker.log;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.slf4j.spi.LocationAwareLogger;
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
/*     */ @Deprecated
/*     */ public class SLF4JLoggerFactory
/*     */   implements LoggerFactory
/*     */ {
/*     */   public Logger getLogger(String category) {
/*  33 */     Logger slf4jLogger = LoggerFactory.getLogger(category);
/*  34 */     if (slf4jLogger instanceof LocationAwareLogger) {
/*  35 */       return new LocationAwareSLF4JLogger((LocationAwareLogger)slf4jLogger);
/*     */     }
/*  37 */     return new LocationUnawareSLF4JLogger(slf4jLogger);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class LocationAwareSLF4JLogger
/*     */     extends Logger
/*     */   {
/*  47 */     private static final String ADAPTER_FQCN = LocationAwareSLF4JLogger.class
/*  48 */       .getName();
/*     */     
/*     */     private final LocationAwareLogger logger;
/*     */     
/*     */     LocationAwareSLF4JLogger(LocationAwareLogger logger) {
/*  53 */       this.logger = logger;
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message) {
/*  58 */       debug(message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message, Throwable t) {
/*  63 */       this.logger.log(null, ADAPTER_FQCN, 10, message, null, t);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void info(String message) {
/*  69 */       info(message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message, Throwable t) {
/*  74 */       this.logger.log(null, ADAPTER_FQCN, 20, message, null, t);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void warn(String message) {
/*  80 */       warn(message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message, Throwable t) {
/*  85 */       this.logger.log(null, ADAPTER_FQCN, 30, message, null, t);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void error(String message) {
/*  91 */       error(message, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message, Throwable t) {
/*  96 */       this.logger.log(null, ADAPTER_FQCN, 40, message, null, t);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/* 102 */       return this.logger.isDebugEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/* 107 */       return this.logger.isInfoEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/* 112 */       return this.logger.isWarnEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 117 */       return this.logger.isErrorEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 122 */       return this.logger.isErrorEnabled();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class LocationUnawareSLF4JLogger
/*     */     extends Logger
/*     */   {
/*     */     private final Logger logger;
/*     */ 
/*     */ 
/*     */     
/*     */     LocationUnawareSLF4JLogger(Logger logger) {
/* 136 */       this.logger = logger;
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message) {
/* 141 */       this.logger.debug(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message, Throwable t) {
/* 146 */       this.logger.debug(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message) {
/* 151 */       this.logger.info(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message, Throwable t) {
/* 156 */       this.logger.info(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message) {
/* 161 */       this.logger.warn(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message, Throwable t) {
/* 166 */       this.logger.warn(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message) {
/* 171 */       this.logger.error(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message, Throwable t) {
/* 176 */       this.logger.error(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/* 181 */       return this.logger.isDebugEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/* 186 */       return this.logger.isInfoEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/* 191 */       return this.logger.isWarnEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 196 */       return this.logger.isErrorEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 201 */       return this.logger.isErrorEnabled();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\log\SLF4JLoggerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */