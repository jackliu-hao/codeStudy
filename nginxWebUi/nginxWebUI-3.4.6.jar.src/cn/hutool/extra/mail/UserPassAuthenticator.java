/*    */ package cn.hutool.extra.mail;
/*    */ 
/*    */ import javax.mail.Authenticator;
/*    */ import javax.mail.PasswordAuthentication;
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
/*    */   private final String pass;
/*    */   
/*    */   public UserPassAuthenticator(String user, String pass) {
/* 24 */     this.user = user;
/* 25 */     this.pass = pass;
/*    */   }
/*    */ 
/*    */   
/*    */   protected PasswordAuthentication getPasswordAuthentication() {
/* 30 */     return new PasswordAuthentication(this.user, this.pass);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\UserPassAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */