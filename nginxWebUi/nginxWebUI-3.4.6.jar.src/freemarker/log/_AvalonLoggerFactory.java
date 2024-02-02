/*     */ package freemarker.log;
/*     */ 
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
/*     */ public class _AvalonLoggerFactory
/*     */   implements LoggerFactory
/*     */ {
/*     */   public Logger getLogger(String category) {
/*  31 */     return new AvalonLogger(Hierarchy.getDefaultHierarchy().getLoggerFor(category));
/*     */   }
/*     */   
/*     */   private static class AvalonLogger
/*     */     extends Logger
/*     */   {
/*     */     private final Logger logger;
/*     */     
/*     */     AvalonLogger(Logger logger) {
/*  40 */       this.logger = logger;
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message) {
/*  45 */       this.logger.debug(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void debug(String message, Throwable t) {
/*  50 */       this.logger.debug(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message) {
/*  55 */       this.logger.error(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(String message, Throwable t) {
/*  60 */       this.logger.error(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message) {
/*  65 */       this.logger.info(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void info(String message, Throwable t) {
/*  70 */       this.logger.info(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message) {
/*  75 */       this.logger.warn(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void warn(String message, Throwable t) {
/*  80 */       this.logger.warn(message, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDebugEnabled() {
/*  85 */       return this.logger.isDebugEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInfoEnabled() {
/*  90 */       return this.logger.isInfoEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWarnEnabled() {
/*  95 */       return this.logger.isWarnEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isErrorEnabled() {
/* 100 */       return this.logger.isErrorEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFatalEnabled() {
/* 105 */       return this.logger.isFatalErrorEnabled();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\log\_AvalonLoggerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */