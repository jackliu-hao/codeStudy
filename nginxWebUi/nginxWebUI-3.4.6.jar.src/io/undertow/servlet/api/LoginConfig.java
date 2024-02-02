/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
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
/*    */ public class LoginConfig
/*    */   implements Cloneable
/*    */ {
/* 28 */   private final LinkedList<AuthMethodConfig> authMethods = new LinkedList<>();
/*    */   
/*    */   private final String realmName;
/*    */   private final String loginPage;
/*    */   private final String errorPage;
/*    */   
/*    */   public LoginConfig(String realmName, String loginPage, String errorPage) {
/* 35 */     this.realmName = realmName;
/* 36 */     this.loginPage = loginPage;
/* 37 */     this.errorPage = errorPage;
/*    */   }
/*    */   
/*    */   public LoginConfig(String realmName) {
/* 41 */     this(realmName, null, null);
/*    */   }
/*    */   
/*    */   public LoginConfig(String mechanismName, String realmName, String loginPage, String errorPage) {
/* 45 */     this.realmName = realmName;
/* 46 */     this.loginPage = loginPage;
/* 47 */     this.errorPage = errorPage;
/* 48 */     addFirstAuthMethod(mechanismName);
/*    */   }
/*    */   
/*    */   public LoginConfig(String mechanismName, String realmName) {
/* 52 */     this(mechanismName, realmName, null, null);
/*    */   }
/*    */   
/*    */   public String getRealmName() {
/* 56 */     return this.realmName;
/*    */   }
/*    */   
/*    */   public String getLoginPage() {
/* 60 */     return this.loginPage;
/*    */   }
/*    */   
/*    */   public String getErrorPage() {
/* 64 */     return this.errorPage;
/*    */   }
/*    */   
/*    */   public LoginConfig addFirstAuthMethod(AuthMethodConfig authMethodConfig) {
/* 68 */     this.authMethods.addFirst(authMethodConfig);
/* 69 */     return this;
/*    */   }
/*    */   
/*    */   public LoginConfig addLastAuthMethod(AuthMethodConfig authMethodConfig) {
/* 73 */     this.authMethods.addLast(authMethodConfig);
/* 74 */     return this;
/*    */   }
/*    */   public LoginConfig addFirstAuthMethod(String authMethodConfig) {
/* 77 */     this.authMethods.addFirst(new AuthMethodConfig(authMethodConfig));
/* 78 */     return this;
/*    */   }
/*    */   
/*    */   public LoginConfig addLastAuthMethod(String authMethodConfig) {
/* 82 */     this.authMethods.addLast(new AuthMethodConfig(authMethodConfig));
/* 83 */     return this;
/*    */   }
/*    */   
/*    */   public List<AuthMethodConfig> getAuthMethods() {
/* 87 */     return this.authMethods;
/*    */   }
/*    */ 
/*    */   
/*    */   public LoginConfig clone() {
/* 92 */     LoginConfig lc = new LoginConfig(this.realmName, this.loginPage, this.errorPage);
/* 93 */     for (AuthMethodConfig method : this.authMethods) {
/* 94 */       lc.authMethods.add(method.clone());
/*    */     }
/* 96 */     return lc;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\LoginConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */