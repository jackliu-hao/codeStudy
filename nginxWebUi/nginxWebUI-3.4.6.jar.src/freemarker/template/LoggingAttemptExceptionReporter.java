/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.log.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class LoggingAttemptExceptionReporter
/*    */   implements AttemptExceptionReporter
/*    */ {
/* 30 */   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
/*    */   
/*    */   private final boolean logAsWarn;
/*    */   
/*    */   public LoggingAttemptExceptionReporter(boolean logAsWarn) {
/* 35 */     this.logAsWarn = logAsWarn;
/*    */   }
/*    */ 
/*    */   
/*    */   public void report(TemplateException te, Environment env) {
/* 40 */     String message = "Error executing FreeMarker template part in the #attempt block";
/* 41 */     if (!this.logAsWarn) {
/* 42 */       LOG.error(message, te);
/*    */     } else {
/* 44 */       LOG.warn(message, te);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\LoggingAttemptExceptionReporter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */