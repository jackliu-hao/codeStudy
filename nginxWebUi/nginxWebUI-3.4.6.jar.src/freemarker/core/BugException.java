/*    */ package freemarker.core;
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
/*    */ public class BugException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final String COMMON_MESSAGE = "A bug was detected in FreeMarker; please report it with stack-trace";
/*    */   
/*    */   public BugException() {
/* 33 */     this((Throwable)null);
/*    */   }
/*    */   
/*    */   public BugException(String message) {
/* 37 */     this(message, null);
/*    */   }
/*    */   
/*    */   public BugException(Throwable cause) {
/* 41 */     super("A bug was detected in FreeMarker; please report it with stack-trace", cause);
/*    */   }
/*    */   
/*    */   public BugException(String message, Throwable cause) {
/* 45 */     super("A bug was detected in FreeMarker; please report it with stack-trace: " + message, cause);
/*    */   }
/*    */   
/*    */   public BugException(int value) {
/* 49 */     this(String.valueOf(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BugException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */