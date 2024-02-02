/*     */ package freemarker.log;
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
/*     */ public class _JULLoggerFactory
/*     */   implements LoggerFactory
/*     */ {
/*     */   public Logger getLogger(String category) {
/*  31 */     return new JULLogger(Logger.getLogger(category));
/*     */   }
/*     */   
/*     */   private static class JULLogger
/*     */     extends Logger
/*     */   {
/*     */     private final Logger logger;
/*     */     
/*     */     JULLogger(Logger logger) {
/*  40 */       this.logger = logger;
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message) {
/*  45 */       this.logger.log(Level.FINE, message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message, Throwable t) {
/*  50 */       this.logger.log(Level.FINE, message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message) {
/*  55 */       this.logger.log(Level.SEVERE, message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message, Throwable t) {
/*  60 */       this.logger.log(Level.SEVERE, message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message) {
/*  65 */       this.logger.log(Level.INFO, message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message, Throwable t) {
/*  70 */       this.logger.log(Level.INFO, message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message) {
/*  75 */       this.logger.log(Level.WARNING, message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message, Throwable t) {
/*  80 */       this.logger.log(Level.WARNING, message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/*  85 */       return this.logger.isLoggable(Level.FINE);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/*  90 */       return this.logger.isLoggable(Level.INFO);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/*  95 */       return this.logger.isLoggable(Level.WARNING);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 100 */       return this.logger.isLoggable(Level.SEVERE);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 105 */       return this.logger.isLoggable(Level.SEVERE);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\log\_JULLoggerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */