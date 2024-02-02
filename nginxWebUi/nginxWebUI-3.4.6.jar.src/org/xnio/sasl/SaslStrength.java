/*    */ package org.xnio.sasl;
/*    */ 
/*    */ import org.xnio._private.Messages;
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
/*    */ public enum SaslStrength
/*    */ {
/* 33 */   LOW("low"),
/*    */ 
/*    */ 
/*    */   
/* 37 */   MEDIUM("medium"),
/*    */ 
/*    */ 
/*    */   
/* 41 */   HIGH("high");
/*    */   
/*    */   private final String toString;
/*    */ 
/*    */   
/*    */   SaslStrength(String toString) {
/* 47 */     this.toString = toString;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SaslStrength fromString(String name) {
/* 57 */     if ("low".equals(name))
/* 58 */       return LOW; 
/* 59 */     if ("medium".equals(name))
/* 60 */       return MEDIUM; 
/* 61 */     if ("high".equals(name)) {
/* 62 */       return HIGH;
/*    */     }
/* 64 */     throw Messages.msg.invalidStrength(name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return this.toString;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslStrength.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */