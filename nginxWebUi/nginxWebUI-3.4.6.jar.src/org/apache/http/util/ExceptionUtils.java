/*    */ package org.apache.http.util;
/*    */ 
/*    */ import java.lang.reflect.Method;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public final class ExceptionUtils
/*    */ {
/* 43 */   private static final Method INIT_CAUSE_METHOD = getInitCauseMethod();
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
/*    */   private static Method getInitCauseMethod() {
/*    */     try {
/* 56 */       Class<?>[] paramsClasses = new Class[] { Throwable.class };
/* 57 */       return Throwable.class.getMethod("initCause", paramsClasses);
/* 58 */     } catch (NoSuchMethodException e) {
/* 59 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void initCause(Throwable throwable, Throwable cause) {
/* 70 */     if (INIT_CAUSE_METHOD != null)
/*    */       try {
/* 72 */         INIT_CAUSE_METHOD.invoke(throwable, new Object[] { cause });
/* 73 */       } catch (Exception e) {} 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\ExceptionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */