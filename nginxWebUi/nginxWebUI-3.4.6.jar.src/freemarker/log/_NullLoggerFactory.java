/*    */ package freemarker.log;
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
/*    */ public class _NullLoggerFactory
/*    */   implements LoggerFactory
/*    */ {
/*    */   public Logger getLogger(String category) {
/* 32 */     return INSTANCE;
/*    */   }
/*    */   
/* 35 */   private static final Logger INSTANCE = new Logger()
/*    */     {
/*    */       public void debug(String message) {}
/*    */ 
/*    */ 
/*    */ 
/*    */       
/*    */       public void debug(String message, Throwable t) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void error(String message) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void error(String message, Throwable t) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void info(String message) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void info(String message, Throwable t) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void warn(String message) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void warn(String message, Throwable t) {}
/*    */ 
/*    */ 
/*    */       
/*    */       public boolean isDebugEnabled() {
/* 71 */         return false;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isInfoEnabled() {
/* 76 */         return false;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isWarnEnabled() {
/* 81 */         return false;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isErrorEnabled() {
/* 86 */         return false;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isFatalEnabled() {
/* 91 */         return false;
/*    */       }
/*    */     };
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\log\_NullLoggerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */