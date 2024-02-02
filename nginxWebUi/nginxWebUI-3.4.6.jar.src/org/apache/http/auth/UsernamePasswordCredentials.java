/*     */ package org.apache.http.auth;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.security.Principal;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.LangUtils;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class UsernamePasswordCredentials
/*     */   implements Credentials, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 243343858802739403L;
/*     */   private final BasicUserPrincipal principal;
/*     */   private final String password;
/*     */   
/*     */   @Deprecated
/*     */   public UsernamePasswordCredentials(String usernamePassword) {
/*  61 */     Args.notNull(usernamePassword, "Username:password string");
/*  62 */     int atColon = usernamePassword.indexOf(':');
/*  63 */     if (atColon >= 0) {
/*  64 */       this.principal = new BasicUserPrincipal(usernamePassword.substring(0, atColon));
/*  65 */       this.password = usernamePassword.substring(atColon + 1);
/*     */     } else {
/*  67 */       this.principal = new BasicUserPrincipal(usernamePassword);
/*  68 */       this.password = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UsernamePasswordCredentials(String userName, String password) {
/*  81 */     Args.notNull(userName, "Username");
/*  82 */     this.principal = new BasicUserPrincipal(userName);
/*  83 */     this.password = password;
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getUserPrincipal() {
/*  88 */     return this.principal;
/*     */   }
/*     */   
/*     */   public String getUserName() {
/*  92 */     return this.principal.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPassword() {
/*  97 */     return this.password;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 102 */     return this.principal.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 107 */     if (this == o) {
/* 108 */       return true;
/*     */     }
/* 110 */     if (o instanceof UsernamePasswordCredentials) {
/* 111 */       UsernamePasswordCredentials that = (UsernamePasswordCredentials)o;
/* 112 */       if (LangUtils.equals(this.principal, that.principal)) {
/* 113 */         return true;
/*     */       }
/*     */     } 
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return this.principal.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\UsernamePasswordCredentials.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */