/*     */ package org.apache.http.auth;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.security.Principal;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class NTCredentials
/*     */   implements Credentials, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -7385699315228907265L;
/*     */   private final NTUserPrincipal principal;
/*     */   private final String password;
/*     */   private final String workstation;
/*     */   
/*     */   @Deprecated
/*     */   public NTCredentials(String usernamePassword) {
/*     */     String username;
/*  68 */     Args.notNull(usernamePassword, "Username:password string");
/*     */     
/*  70 */     int atColon = usernamePassword.indexOf(':');
/*  71 */     if (atColon >= 0) {
/*  72 */       username = usernamePassword.substring(0, atColon);
/*  73 */       this.password = usernamePassword.substring(atColon + 1);
/*     */     } else {
/*  75 */       username = usernamePassword;
/*  76 */       this.password = null;
/*     */     } 
/*  78 */     int atSlash = username.indexOf('/');
/*  79 */     if (atSlash >= 0) {
/*  80 */       this.principal = new NTUserPrincipal(username.substring(0, atSlash).toUpperCase(Locale.ROOT), username.substring(atSlash + 1));
/*     */     }
/*     */     else {
/*     */       
/*  84 */       this.principal = new NTUserPrincipal(null, username.substring(atSlash + 1));
/*     */     } 
/*     */ 
/*     */     
/*  88 */     this.workstation = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NTCredentials(String userName, String password, String workstation, String domain) {
/* 106 */     Args.notNull(userName, "User name");
/* 107 */     this.principal = new NTUserPrincipal(domain, userName);
/* 108 */     this.password = password;
/* 109 */     if (workstation != null) {
/* 110 */       this.workstation = workstation.toUpperCase(Locale.ROOT);
/*     */     } else {
/* 112 */       this.workstation = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getUserPrincipal() {
/* 118 */     return this.principal;
/*     */   }
/*     */   
/*     */   public String getUserName() {
/* 122 */     return this.principal.getUsername();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 127 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDomain() {
/* 136 */     return this.principal.getDomain();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getWorkstation() {
/* 145 */     return this.workstation;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 150 */     int hash = 17;
/* 151 */     hash = LangUtils.hashCode(hash, this.principal);
/* 152 */     hash = LangUtils.hashCode(hash, this.workstation);
/* 153 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 158 */     if (this == o) {
/* 159 */       return true;
/*     */     }
/* 161 */     if (o instanceof NTCredentials) {
/* 162 */       NTCredentials that = (NTCredentials)o;
/* 163 */       if (LangUtils.equals(this.principal, that.principal) && LangUtils.equals(this.workstation, that.workstation))
/*     */       {
/* 165 */         return true;
/*     */       }
/*     */     } 
/* 168 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 173 */     StringBuilder buffer = new StringBuilder();
/* 174 */     buffer.append("[principal: ");
/* 175 */     buffer.append(this.principal);
/* 176 */     buffer.append("][workstation: ");
/* 177 */     buffer.append(this.workstation);
/* 178 */     buffer.append("]");
/* 179 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\NTCredentials.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */