/*    */ package io.undertow.security.idm;
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
/*    */ public final class PasswordCredential
/*    */   implements Credential
/*    */ {
/*    */   private final char[] password;
/*    */   
/*    */   public PasswordCredential(char[] password) {
/* 30 */     this.password = password;
/*    */   }
/*    */   
/*    */   public char[] getPassword() {
/* 34 */     return this.password;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\idm\PasswordCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */