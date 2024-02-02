/*    */ package org.h2.security.auth.impl;
/*    */ 
/*    */ import java.util.regex.Pattern;
/*    */ import org.h2.api.CredentialsValidator;
/*    */ import org.h2.security.SHA256;
/*    */ import org.h2.security.auth.AuthenticationException;
/*    */ import org.h2.security.auth.AuthenticationInfo;
/*    */ import org.h2.security.auth.ConfigProperties;
/*    */ import org.h2.util.MathUtils;
/*    */ import org.h2.util.StringUtils;
/*    */ import org.h2.util.Utils;
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
/*    */ public class StaticUserCredentialsValidator
/*    */   implements CredentialsValidator
/*    */ {
/*    */   private Pattern userNamePattern;
/*    */   private String password;
/*    */   private byte[] salt;
/*    */   private byte[] hashWithSalt;
/*    */   
/*    */   public StaticUserCredentialsValidator() {}
/*    */   
/*    */   public StaticUserCredentialsValidator(String paramString1, String paramString2) {
/* 35 */     if (paramString1 != null) {
/* 36 */       this.userNamePattern = Pattern.compile(paramString1.toUpperCase());
/*    */     }
/* 38 */     this.salt = MathUtils.secureRandomBytes(256);
/* 39 */     this.hashWithSalt = SHA256.getHashWithSalt(paramString2.getBytes(), this.salt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validateCredentials(AuthenticationInfo paramAuthenticationInfo) throws AuthenticationException {
/* 44 */     if (this.userNamePattern != null && 
/* 45 */       !this.userNamePattern.matcher(paramAuthenticationInfo.getUserName()).matches()) {
/* 46 */       return false;
/*    */     }
/*    */     
/* 49 */     if (this.password != null) {
/* 50 */       return this.password.equals(paramAuthenticationInfo.getPassword());
/*    */     }
/* 52 */     return Utils.compareSecure(this.hashWithSalt, 
/* 53 */         SHA256.getHashWithSalt(paramAuthenticationInfo.getPassword().getBytes(), this.salt));
/*    */   }
/*    */ 
/*    */   
/*    */   public void configure(ConfigProperties paramConfigProperties) {
/* 58 */     String str1 = paramConfigProperties.getStringValue("userNamePattern", null);
/* 59 */     if (str1 != null) {
/* 60 */       this.userNamePattern = Pattern.compile(str1);
/*    */     }
/* 62 */     this.password = paramConfigProperties.getStringValue("password", this.password);
/* 63 */     String str2 = paramConfigProperties.getStringValue("salt", null);
/* 64 */     if (str2 != null) {
/* 65 */       this.salt = StringUtils.convertHexToBytes(str2);
/*    */     }
/* 67 */     String str3 = paramConfigProperties.getStringValue("hash", null);
/* 68 */     if (str3 != null)
/* 69 */       this.hashWithSalt = SHA256.getHashWithSalt(StringUtils.convertHexToBytes(str3), this.salt); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\impl\StaticUserCredentialsValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */