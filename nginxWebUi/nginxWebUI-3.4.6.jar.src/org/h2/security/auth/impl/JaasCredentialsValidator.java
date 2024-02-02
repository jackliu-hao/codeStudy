/*    */ package org.h2.security.auth.impl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.security.auth.callback.Callback;
/*    */ import javax.security.auth.callback.CallbackHandler;
/*    */ import javax.security.auth.callback.NameCallback;
/*    */ import javax.security.auth.callback.PasswordCallback;
/*    */ import javax.security.auth.callback.UnsupportedCallbackException;
/*    */ import javax.security.auth.login.LoginContext;
/*    */ import org.h2.api.CredentialsValidator;
/*    */ import org.h2.security.auth.AuthenticationInfo;
/*    */ import org.h2.security.auth.ConfigProperties;
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
/*    */ public class JaasCredentialsValidator
/*    */   implements CredentialsValidator
/*    */ {
/*    */   public static final String DEFAULT_APPNAME = "h2";
/*    */   private String appName;
/*    */   
/*    */   public JaasCredentialsValidator() {
/* 39 */     this("h2");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JaasCredentialsValidator(String paramString) {
/* 47 */     this.appName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public void configure(ConfigProperties paramConfigProperties) {
/* 52 */     this.appName = paramConfigProperties.getStringValue("appName", this.appName);
/*    */   }
/*    */   
/*    */   static class AuthenticationInfoCallbackHandler
/*    */     implements CallbackHandler {
/*    */     AuthenticationInfo authenticationInfo;
/*    */     
/*    */     AuthenticationInfoCallbackHandler(AuthenticationInfo param1AuthenticationInfo) {
/* 60 */       this.authenticationInfo = param1AuthenticationInfo;
/*    */     }
/*    */ 
/*    */     
/*    */     public void handle(Callback[] param1ArrayOfCallback) throws IOException, UnsupportedCallbackException {
/* 65 */       for (byte b = 0; b < param1ArrayOfCallback.length; b++) {
/* 66 */         if (param1ArrayOfCallback[b] instanceof NameCallback) {
/* 67 */           ((NameCallback)param1ArrayOfCallback[b]).setName(this.authenticationInfo.getUserName());
/* 68 */         } else if (param1ArrayOfCallback[b] instanceof PasswordCallback) {
/* 69 */           ((PasswordCallback)param1ArrayOfCallback[b]).setPassword(this.authenticationInfo.getPassword().toCharArray());
/*    */         } 
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validateCredentials(AuthenticationInfo paramAuthenticationInfo) throws Exception {
/* 78 */     LoginContext loginContext = new LoginContext(this.appName, new AuthenticationInfoCallbackHandler(paramAuthenticationInfo));
/*    */     
/* 80 */     loginContext.login();
/* 81 */     paramAuthenticationInfo.setNestedIdentity(loginContext.getSubject());
/* 82 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\impl\JaasCredentialsValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */