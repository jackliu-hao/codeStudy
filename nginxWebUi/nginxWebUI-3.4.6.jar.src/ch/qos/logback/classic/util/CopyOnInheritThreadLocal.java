/*    */ package ch.qos.logback.classic.util;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public class CopyOnInheritThreadLocal
/*    */   extends InheritableThreadLocal<HashMap<String, String>>
/*    */ {
/*    */   protected HashMap<String, String> childValue(HashMap<String, String> parentValue) {
/* 31 */     if (parentValue == null) {
/* 32 */       return null;
/*    */     }
/* 34 */     return new HashMap<String, String>(parentValue);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\CopyOnInheritThreadLocal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */