/*    */ package org.h2.security.auth;
/*    */ 
/*    */ import org.h2.engine.ConnectionInfo;
/*    */ import org.h2.util.StringUtils;
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
/*    */ public class AuthenticationInfo
/*    */ {
/*    */   private ConnectionInfo connectionInfo;
/*    */   private String password;
/*    */   private String realm;
/*    */   Object nestedIdentity;
/*    */   
/*    */   public AuthenticationInfo(ConnectionInfo paramConnectionInfo) {
/* 28 */     this.connectionInfo = paramConnectionInfo;
/* 29 */     this.realm = paramConnectionInfo.getProperty("AUTHREALM", null);
/* 30 */     if (this.realm != null) {
/* 31 */       this.realm = StringUtils.toUpperEnglish(this.realm);
/*    */     }
/* 33 */     this.password = paramConnectionInfo.getProperty("AUTHZPWD", null);
/*    */   }
/*    */   
/*    */   public String getUserName() {
/* 37 */     return this.connectionInfo.getUserName();
/*    */   }
/*    */   
/*    */   public String getRealm() {
/* 41 */     return this.realm;
/*    */   }
/*    */   
/*    */   public String getPassword() {
/* 45 */     return this.password;
/*    */   }
/*    */   
/*    */   public ConnectionInfo getConnectionInfo() {
/* 49 */     return this.connectionInfo;
/*    */   }
/*    */   
/*    */   public String getFullyQualifiedName() {
/* 53 */     if (this.realm == null) {
/* 54 */       return this.connectionInfo.getUserName();
/*    */     }
/* 56 */     return this.connectionInfo.getUserName() + "@" + this.realm;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getNestedIdentity() {
/* 66 */     return this.nestedIdentity;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setNestedIdentity(Object paramObject) {
/* 77 */     this.nestedIdentity = paramObject;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clean() {
/* 84 */     this.password = null;
/* 85 */     this.nestedIdentity = null;
/* 86 */     this.connectionInfo.cleanAuthenticationInfo();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\AuthenticationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */