/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.core.Environment;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface AttemptExceptionReporter
/*    */ {
/* 36 */   public static final AttemptExceptionReporter LOG_ERROR_REPORTER = new LoggingAttemptExceptionReporter(false);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 41 */   public static final AttemptExceptionReporter LOG_WARN_REPORTER = new LoggingAttemptExceptionReporter(true);
/*    */   
/*    */   void report(TemplateException paramTemplateException, Environment paramEnvironment);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\AttemptExceptionReporter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */