/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.servlet.api.AuthorizationManager;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.SecurityInfo;
/*     */ import io.undertow.servlet.api.SecurityRoleRef;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.api.SingleConstraintMatch;
/*     */ import io.undertow.servlet.api.TransportGuaranteeType;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultAuthorizationManager
/*     */   implements AuthorizationManager
/*     */ {
/*  42 */   public static final DefaultAuthorizationManager INSTANCE = new DefaultAuthorizationManager();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUserInRole(String role, Account account, ServletInfo servletInfo, HttpServletRequest request, Deployment deployment) {
/*  50 */     Map<String, Set<String>> principalVersusRolesMap = deployment.getDeploymentInfo().getPrincipalVersusRolesMap();
/*  51 */     Set<String> roles = principalVersusRolesMap.get(account.getPrincipal().getName());
/*     */     
/*  53 */     for (SecurityRoleRef ref : servletInfo.getSecurityRoleRefs()) {
/*  54 */       if (ref.getRole().equals(role)) {
/*  55 */         if (roles != null && roles.contains(ref.getLinkedRole())) {
/*  56 */           return true;
/*     */         }
/*  58 */         return account.getRoles().contains(ref.getLinkedRole());
/*     */       } 
/*     */     } 
/*  61 */     if (roles != null && roles.contains(role)) {
/*  62 */       return true;
/*     */     }
/*  64 */     return account.getRoles().contains(role);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canAccessResource(List<SingleConstraintMatch> constraints, Account account, ServletInfo servletInfo, HttpServletRequest request, Deployment deployment) {
/*  69 */     if (constraints == null || constraints.isEmpty()) {
/*  70 */       return true;
/*     */     }
/*  72 */     for (SingleConstraintMatch constraint : constraints) {
/*     */       
/*  74 */       boolean found = false;
/*     */       
/*  76 */       Set<String> roleSet = constraint.getRequiredRoles();
/*  77 */       if (roleSet.isEmpty() && constraint.getEmptyRoleSemantic() != SecurityInfo.EmptyRoleSemantic.DENY) {
/*     */ 
/*     */ 
/*     */         
/*  81 */         found = true;
/*  82 */       } else if (account != null) {
/*  83 */         if (roleSet.contains("**") && !deployment.getDeploymentInfo().getSecurityRoles().contains("**")) {
/*  84 */           found = true;
/*     */         } else {
/*  86 */           Set<String> roles = (Set<String>)deployment.getDeploymentInfo().getPrincipalVersusRolesMap().get(account.getPrincipal().getName());
/*     */           
/*  88 */           for (String role : roleSet) {
/*  89 */             if (roles != null && 
/*  90 */               roles.contains(role)) {
/*  91 */               found = true;
/*     */               
/*     */               break;
/*     */             } 
/*  95 */             if (account.getRoles().contains(role)) {
/*  96 */               found = true;
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 102 */       if (!found) {
/* 103 */         return false;
/*     */       }
/*     */     } 
/* 106 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TransportGuaranteeType transportGuarantee(TransportGuaranteeType currentConnectionGuarantee, TransportGuaranteeType configuredRequiredGuarentee, HttpServletRequest request) {
/* 112 */     return configuredRequiredGuarentee;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\DefaultAuthorizationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */