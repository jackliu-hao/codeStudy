/*    */ package org.h2.security.auth.impl;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
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
/*    */ 
/*    */ public class StaticRolesMapper
/*    */   implements UserToRolesMapper
/*    */ {
/*    */   private Collection<String> roles;
/*    */   
/*    */   public StaticRolesMapper() {}
/*    */   
/*    */   public StaticRolesMapper(String... paramVarArgs) {
/* 35 */     this.roles = Arrays.asList(paramVarArgs);
/*    */   }
/*    */ 
/*    */   
/*    */   public void configure(ConfigProperties paramConfigProperties) {
/* 40 */     String str = paramConfigProperties.getStringValue("roles", "");
/* 41 */     if (str != null) {
/* 42 */       this.roles = new HashSet<>(Arrays.asList(str.split(",")));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<String> mapUserToRoles(AuthenticationInfo paramAuthenticationInfo) throws AuthenticationException {
/* 48 */     return this.roles;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\impl\StaticRolesMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */