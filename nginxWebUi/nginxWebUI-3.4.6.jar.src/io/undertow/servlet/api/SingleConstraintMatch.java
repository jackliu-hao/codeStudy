/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SingleConstraintMatch
/*    */ {
/*    */   private final SecurityInfo.EmptyRoleSemantic emptyRoleSemantic;
/*    */   private final Set<String> requiredRoles;
/*    */   
/*    */   public SingleConstraintMatch(SecurityInfo.EmptyRoleSemantic emptyRoleSemantic, Set<String> requiredRoles) {
/* 36 */     this.emptyRoleSemantic = emptyRoleSemantic;
/* 37 */     this.requiredRoles = requiredRoles;
/*    */   }
/*    */   
/*    */   public SecurityInfo.EmptyRoleSemantic getEmptyRoleSemantic() {
/* 41 */     return this.emptyRoleSemantic;
/*    */   }
/*    */   
/*    */   public Set<String> getRequiredRoles() {
/* 45 */     return this.requiredRoles;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     return "SingleConstraintMatch{emptyRoleSemantic=" + this.emptyRoleSemantic + ", requiredRoles=" + this.requiredRoles + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SingleConstraintMatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */