/*    */ package cn.hutool.core.lang.caller;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CallerUtil
/*    */ {
/* 12 */   private static final Caller INSTANCE = tryCreateCaller();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Class<?> getCaller() {
/* 21 */     return INSTANCE.getCaller();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Class<?> getCallerCaller() {
/* 30 */     return INSTANCE.getCallerCaller();
/*    */   }
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
/*    */   public static Class<?> getCaller(int depth) {
/* 48 */     return INSTANCE.getCaller(depth);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isCalledBy(Class<?> clazz) {
/* 58 */     return INSTANCE.isCalledBy(clazz);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getCallerMethodName(boolean isFullName) {
/* 69 */     StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
/* 70 */     String methodName = stackTraceElement.getMethodName();
/* 71 */     if (false == isFullName) {
/* 72 */       return methodName;
/*    */     }
/*    */     
/* 75 */     return stackTraceElement.getClassName() + "." + methodName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static Caller tryCreateCaller() {
/*    */     try {
/* 86 */       Caller caller1 = new SecurityManagerCaller();
/* 87 */       if (null != caller1.getCaller() && null != caller1.getCallerCaller()) {
/* 88 */         return caller1;
/*    */       }
/* 90 */     } catch (Throwable throwable) {}
/*    */ 
/*    */ 
/*    */     
/* 94 */     Caller caller = new StackTraceCaller();
/* 95 */     return caller;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\caller\CallerUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */