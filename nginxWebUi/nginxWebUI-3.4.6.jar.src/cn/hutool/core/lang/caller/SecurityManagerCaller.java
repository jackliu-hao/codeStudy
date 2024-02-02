/*    */ package cn.hutool.core.lang.caller;
/*    */ 
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SecurityManagerCaller
/*    */   extends SecurityManager
/*    */   implements Caller, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int OFFSET = 1;
/*    */   
/*    */   public Class<?> getCaller() {
/* 19 */     Class<?>[] context = getClassContext();
/* 20 */     if (null != context && 2 < context.length) {
/* 21 */       return context[2];
/*    */     }
/* 23 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getCallerCaller() {
/* 28 */     Class<?>[] context = getClassContext();
/* 29 */     if (null != context && 3 < context.length) {
/* 30 */       return context[3];
/*    */     }
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getCaller(int depth) {
/* 37 */     Class<?>[] context = getClassContext();
/* 38 */     if (null != context && 1 + depth < context.length) {
/* 39 */       return context[1 + depth];
/*    */     }
/* 41 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCalledBy(Class<?> clazz) {
/* 46 */     Class<?>[] classes = getClassContext();
/* 47 */     if (ArrayUtil.isNotEmpty((Object[])classes)) {
/* 48 */       for (Class<?> contextClass : classes) {
/* 49 */         if (contextClass.equals(clazz)) {
/* 50 */           return true;
/*    */         }
/*    */       } 
/*    */     }
/* 54 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\caller\SecurityManagerCaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */