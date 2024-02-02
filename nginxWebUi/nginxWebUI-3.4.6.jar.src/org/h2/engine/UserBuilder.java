/*    */ package org.h2.engine;
/*    */ 
/*    */ import org.h2.security.auth.AuthenticationInfo;
/*    */ import org.h2.util.MathUtils;
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
/*    */ public class UserBuilder
/*    */ {
/*    */   public static User buildUser(AuthenticationInfo paramAuthenticationInfo, Database paramDatabase, boolean paramBoolean) {
/* 26 */     User user = new User(paramDatabase, paramBoolean ? paramDatabase.allocateObjectId() : -1, paramAuthenticationInfo.getFullyQualifiedName(), false);
/*    */ 
/*    */     
/* 29 */     user.setUserPasswordHash(
/* 30 */         (paramAuthenticationInfo.getRealm() == null) ? paramAuthenticationInfo.getConnectionInfo().getUserPasswordHash() : 
/* 31 */         MathUtils.secureRandomBytes(64));
/* 32 */     user.setTemporary(!paramBoolean);
/* 33 */     return user;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\UserBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */