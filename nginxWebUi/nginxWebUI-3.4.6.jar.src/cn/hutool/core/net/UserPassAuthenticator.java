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
/*    */ 
/*    */ public class UserPassAuthenticator
/*    */   extends Authenticator
/*    */ {
/*    */   private final String user;
/*    */   private final char[] pass;
/*    */   
/*    */   public UserPassAuthenticator(String user, char[] pass) {
/* 24 */     this.user = user;
/* 25 */     this.pass = pass;
/*    */   }
/*    */ 
/*    */   
/*    */   protected PasswordAuthentication getPasswordAuthentication() {
/* 30 */     return new PasswordAuthentication(this.user, this.pass);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\UserPassAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */