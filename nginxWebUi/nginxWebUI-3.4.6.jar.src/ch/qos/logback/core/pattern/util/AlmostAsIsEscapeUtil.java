/*    */ package ch.qos.logback.core.pattern.util;
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
/*    */ public class AlmostAsIsEscapeUtil
/*    */   extends RestrictedEscapeUtil
/*    */ {
/*    */   public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
/* 42 */     super.escape("%)", buf, next, pointer);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\patter\\util\AlmostAsIsEscapeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */