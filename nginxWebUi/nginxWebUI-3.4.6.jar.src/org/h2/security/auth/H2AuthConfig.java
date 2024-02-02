/*    */ package org.h2.security.auth;
/*    */ 
/*    */ import java.util.ArrayList;
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
/*    */ public class H2AuthConfig
/*    */ {
/*    */   private boolean allowUserRegistration = true;
/*    */   private boolean createMissingRoles = true;
/*    */   private List<RealmConfig> realms;
/*    */   private List<UserToRolesMapperConfig> userToRolesMappers;
/*    */   
/*    */   public boolean isAllowUserRegistration() {
/* 29 */     return this.allowUserRegistration;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAllowUserRegistration(boolean paramBoolean) {
/* 36 */     this.allowUserRegistration = paramBoolean;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isCreateMissingRoles() {
/* 45 */     return this.createMissingRoles;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCreateMissingRoles(boolean paramBoolean) {
/* 54 */     this.createMissingRoles = paramBoolean;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<RealmConfig> getRealms() {
/* 63 */     if (this.realms == null) {
/* 64 */       this.realms = new ArrayList<>();
/*    */     }
/* 66 */     return this.realms;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setRealms(List<RealmConfig> paramList) {
/* 75 */     this.realms = paramList;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<UserToRolesMapperConfig> getUserToRolesMappers() {
/* 84 */     if (this.userToRolesMappers == null) {
/* 85 */       this.userToRolesMappers = new ArrayList<>();
/*    */     }
/* 87 */     return this.userToRolesMappers;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setUserToRolesMappers(List<UserToRolesMapperConfig> paramList) {
/* 96 */     this.userToRolesMappers = paramList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\H2AuthConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */