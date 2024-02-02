/*    */ package freemarker.template.utility;
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
/*    */ public class UndeclaredThrowableException
/*    */   extends RuntimeException
/*    */ {
/*    */   public UndeclaredThrowableException(Throwable t) {
/* 29 */     super(t);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UndeclaredThrowableException(String message, Throwable t) {
/* 36 */     super(message, t);
/*    */   }
/*    */   
/*    */   public Throwable getUndeclaredThrowable() {
/* 40 */     return getCause();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\UndeclaredThrowableException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */