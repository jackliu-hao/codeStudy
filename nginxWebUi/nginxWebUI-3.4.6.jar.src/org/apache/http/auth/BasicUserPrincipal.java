/*    */ package org.apache.http.auth;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.security.Principal;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.LangUtils;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public final class BasicUserPrincipal
/*    */   implements Principal, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -2266305184969850467L;
/*    */   private final String username;
/*    */   
/*    */   public BasicUserPrincipal(String username) {
/* 51 */     Args.notNull(username, "User name");
/* 52 */     this.username = username;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 57 */     return this.username;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     int hash = 17;
/* 63 */     hash = LangUtils.hashCode(hash, this.username);
/* 64 */     return hash;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 69 */     if (this == o) {
/* 70 */       return true;
/*    */     }
/* 72 */     if (o instanceof BasicUserPrincipal) {
/* 73 */       BasicUserPrincipal that = (BasicUserPrincipal)o;
/* 74 */       if (LangUtils.equals(this.username, that.username)) {
/* 75 */         return true;
/*    */       }
/*    */     } 
/* 78 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 83 */     StringBuilder buffer = new StringBuilder();
/* 84 */     buffer.append("[principal: ");
/* 85 */     buffer.append(this.username);
/* 86 */     buffer.append("]");
/* 87 */     return buffer.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\BasicUserPrincipal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */