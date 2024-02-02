/*    */ package cn.hutool.core.lang.caller;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StackTraceCaller
/*    */   implements Caller, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int OFFSET = 2;
/*    */   
/*    */   public Class<?> getCaller() {
/* 18 */     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
/* 19 */     if (3 >= stackTrace.length) {
/* 20 */       return null;
/*    */     }
/* 22 */     String className = stackTrace[3].getClassName();
/*    */     try {
/* 24 */       return Class.forName(className);
/* 25 */     } catch (ClassNotFoundException e) {
/* 26 */       throw new UtilException(e, "[{}] not found!", new Object[] { className });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getCallerCaller() {
/* 32 */     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
/* 33 */     if (4 >= stackTrace.length) {
/* 34 */       return null;
/*    */     }
/* 36 */     String className = stackTrace[4].getClassName();
/*    */     try {
/* 38 */       return Class.forName(className);
/* 39 */     } catch (ClassNotFoundException e) {
/* 40 */       throw new UtilException(e, "[{}] not found!", new Object[] { className });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getCaller(int depth) {
/* 46 */     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
/* 47 */     if (2 + depth >= stackTrace.length) {
/* 48 */       return null;
/*    */     }
/* 50 */     String className = stackTrace[2 + depth].getClassName();
/*    */     try {
/* 52 */       return Class.forName(className);
/* 53 */     } catch (ClassNotFoundException e) {
/* 54 */       throw new UtilException(e, "[{}] not found!", new Object[] { className });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCalledBy(Class<?> clazz) {
/* 60 */     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
/* 61 */     for (StackTraceElement element : stackTrace) {
/* 62 */       if (element.getClassName().equals(clazz.getName())) {
/* 63 */         return true;
/*    */       }
/*    */     } 
/* 66 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\caller\StackTraceCaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */