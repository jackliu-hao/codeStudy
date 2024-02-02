/*    */ package org.h2.security.auth.impl;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import org.h2.api.UserToRolesMapper;
/*    */ import org.h2.security.auth.AuthenticationException;
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
/*    */ public class AssignRealmNameRole
/*    */   implements UserToRolesMapper
/*    */ {
/*    */   private String roleNameFormat;
/*    */   
/*    */   public AssignRealmNameRole() {
/* 31 */     this("@%s");
/*    */   }
/*    */   
/*    */   public AssignRealmNameRole(String paramString) {
/* 35 */     this.roleNameFormat = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public void configure(ConfigProperties paramConfigProperties) {
/* 40 */     this.roleNameFormat = paramConfigProperties.getStringValue("roleNameFormat", this.roleNameFormat);
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<String> mapUserToRoles(AuthenticationInfo paramAuthenticationInfo) throws AuthenticationException {
/* 45 */     return Arrays.asList(new String[] { String.format(this.roleNameFormat, new Object[] { paramAuthenticationInfo.getRealm() }) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\impl\AssignRealmNameRole.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */