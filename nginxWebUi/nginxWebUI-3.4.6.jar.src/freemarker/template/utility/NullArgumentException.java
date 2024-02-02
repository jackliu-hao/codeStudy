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
/*    */ 
/*    */ public class NullArgumentException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   public NullArgumentException() {
/* 30 */     super("The argument can't be null");
/*    */   }
/*    */   
/*    */   public NullArgumentException(String argumentName) {
/* 34 */     super("The \"" + argumentName + "\" argument can't be null");
/*    */   }
/*    */   
/*    */   public NullArgumentException(String argumentName, String details) {
/* 38 */     super("The \"" + argumentName + "\" argument can't be null. " + details);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void check(String argumentName, Object argumentValue) {
/* 45 */     if (argumentValue == null) {
/* 46 */       throw new NullArgumentException(argumentName);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void check(Object argumentValue) {
/* 54 */     if (argumentValue == null)
/* 55 */       throw new NullArgumentException(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\NullArgumentException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */