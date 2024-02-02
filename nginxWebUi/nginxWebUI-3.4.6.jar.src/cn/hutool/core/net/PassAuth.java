/*    */ package cn.hutool.core.net;
/*    */ 
/*    */ import java.net.Authenticator;
/*    */ import java.net.PasswordAuthentication;
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
/*    */ public class PassAuth
/*    */   extends Authenticator
/*    */ {
/*    */   private final PasswordAuthentication auth;
/*    */   
/*    */   public static PassAuth of(String user, char[] pass) {
/* 22 */     return new PassAuth(user, pass);
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
/*    */   public PassAuth(String user, char[] pass) {
/* 34 */     this.auth = new PasswordAuthentication(user, pass);
/*    */   }
/*    */ 
/*    */   
/*    */   protected PasswordAuthentication getPasswordAuthentication() {
/* 39 */     return this.auth;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\PassAuth.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */