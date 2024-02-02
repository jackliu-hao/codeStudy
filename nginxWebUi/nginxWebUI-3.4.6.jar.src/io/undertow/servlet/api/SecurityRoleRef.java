/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import io.undertow.servlet.UndertowServletMessages;
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
/*    */ public class SecurityRoleRef
/*    */ {
/*    */   private final String role;
/*    */   private final String linkedRole;
/*    */   
/*    */   public SecurityRoleRef(String role, String linkedRole) {
/* 32 */     if (role == null) {
/* 33 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("role");
/*    */     }
/* 35 */     this.role = role;
/* 36 */     this.linkedRole = linkedRole;
/*    */   }
/*    */   
/*    */   public String getRole() {
/* 40 */     return this.role;
/*    */   }
/*    */   
/*    */   public String getLinkedRole() {
/* 44 */     return this.linkedRole;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SecurityRoleRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */