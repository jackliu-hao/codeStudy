/*    */ package org.h2.security.auth.impl;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import javax.naming.directory.InitialDirContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LdapCredentialsValidator
/*    */   implements CredentialsValidator
/*    */ {
/*    */   private String bindDnPattern;
/*    */   private String host;
/*    */   private int port;
/*    */   private boolean secure;
/*    */   private String url;
/*    */   
/*    */   public void configure(ConfigProperties paramConfigProperties) {
/* 41 */     this.bindDnPattern = paramConfigProperties.getStringValue("bindDnPattern");
/* 42 */     this.host = paramConfigProperties.getStringValue("host");
/* 43 */     this.secure = paramConfigProperties.getBooleanValue("secure", true);
/* 44 */     this.port = paramConfigProperties.getIntValue("port", this.secure ? 636 : 389);
/* 45 */     this.url = "ldap" + (this.secure ? "s" : "") + "://" + this.host + ":" + this.port;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validateCredentials(AuthenticationInfo paramAuthenticationInfo) throws Exception {
/* 50 */     InitialDirContext initialDirContext = null;
/*    */     try {
/* 52 */       String str = this.bindDnPattern.replace("%u", paramAuthenticationInfo.getUserName());
/* 53 */       Hashtable<Object, Object> hashtable = new Hashtable<>();
/* 54 */       hashtable.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
/* 55 */       hashtable.put("java.naming.provider.url", this.url);
/* 56 */       hashtable.put("java.naming.security.authentication", "simple");
/* 57 */       hashtable.put("java.naming.security.principal", str);
/* 58 */       hashtable.put("java.naming.security.credentials", paramAuthenticationInfo.getPassword());
/* 59 */       if (this.secure) {
/* 60 */         hashtable.put("java.naming.security.protocol", "ssl");
/*    */       }
/* 62 */       initialDirContext = new InitialDirContext(hashtable);
/* 63 */       paramAuthenticationInfo.setNestedIdentity(str);
/* 64 */       return true;
/*    */     } finally {
/* 66 */       if (initialDirContext != null)
/* 67 */         initialDirContext.close(); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\impl\LdapCredentialsValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */