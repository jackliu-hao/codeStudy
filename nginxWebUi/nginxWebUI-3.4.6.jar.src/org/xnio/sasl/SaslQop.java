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
/*    */ 
/*    */ 
/*    */ public enum SaslQop
/*    */ {
/* 35 */   AUTH("auth"),
/*    */ 
/*    */ 
/*    */   
/* 39 */   AUTH_INT("auth-int"),
/*    */ 
/*    */ 
/*    */   
/* 43 */   AUTH_CONF("auth-conf");
/*    */   
/*    */   private final String s;
/*    */ 
/*    */   
/*    */   SaslQop(String s) {
/* 49 */     this.s = s;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SaslQop fromString(String name) {
/* 59 */     if ("auth".equals(name))
/* 60 */       return AUTH; 
/* 61 */     if ("auth-int".equals(name))
/* 62 */       return AUTH_INT; 
/* 63 */     if ("auth-conf".equals(name)) {
/* 64 */       return AUTH_CONF;
/*    */     }
/* 66 */     throw Messages.msg.invalidQop(name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getString() {
/* 76 */     return this.s;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return this.s;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslQop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */