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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class NTUserPrincipal
/*     */   implements Principal, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6870169797924406894L;
/*     */   private final String username;
/*     */   private final String domain;
/*     */   private final String ntname;
/*     */   
/*     */   public NTUserPrincipal(String domain, String username) {
/*  56 */     Args.notNull(username, "User name");
/*  57 */     this.username = username;
/*  58 */     if (domain != null) {
/*  59 */       this.domain = domain.toUpperCase(Locale.ROOT);
/*     */     } else {
/*  61 */       this.domain = null;
/*     */     } 
/*  63 */     if (this.domain != null && !this.domain.isEmpty()) {
/*  64 */       StringBuilder buffer = new StringBuilder();
/*  65 */       buffer.append(this.domain);
/*  66 */       buffer.append('\\');
/*  67 */       buffer.append(this.username);
/*  68 */       this.ntname = buffer.toString();
/*     */     } else {
/*  70 */       this.ntname = this.username;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  76 */     return this.ntname;
/*     */   }
/*     */   
/*     */   public String getDomain() {
/*  80 */     return this.domain;
/*     */   }
/*     */   
/*     */   public String getUsername() {
/*  84 */     return this.username;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  89 */     int hash = 17;
/*  90 */     hash = LangUtils.hashCode(hash, this.username);
/*  91 */     hash = LangUtils.hashCode(hash, this.domain);
/*  92 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  97 */     if (this == o) {
/*  98 */       return true;
/*     */     }
/* 100 */     if (o instanceof NTUserPrincipal) {
/* 101 */       NTUserPrincipal that = (NTUserPrincipal)o;
/* 102 */       if (LangUtils.equals(this.username, that.username) && LangUtils.equals(this.domain, that.domain))
/*     */       {
/* 104 */         return true;
/*     */       }
/*     */     } 
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 112 */     return this.ntname;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\NTUserPrincipal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */