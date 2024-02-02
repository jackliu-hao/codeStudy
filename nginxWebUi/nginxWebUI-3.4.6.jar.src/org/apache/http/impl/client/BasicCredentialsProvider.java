/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class BasicCredentialsProvider
/*     */   implements CredentialsProvider
/*     */ {
/*  54 */   private final ConcurrentHashMap<AuthScope, Credentials> credMap = new ConcurrentHashMap<AuthScope, Credentials>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCredentials(AuthScope authscope, Credentials credentials) {
/*  61 */     Args.notNull(authscope, "Authentication scope");
/*  62 */     this.credMap.put(authscope, credentials);
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
/*     */   private static Credentials matchCredentials(Map<AuthScope, Credentials> map, AuthScope authscope) {
/*  77 */     Credentials creds = map.get(authscope);
/*  78 */     if (creds == null) {
/*     */ 
/*     */       
/*  81 */       int bestMatchFactor = -1;
/*  82 */       AuthScope bestMatch = null;
/*  83 */       for (AuthScope current : map.keySet()) {
/*  84 */         int factor = authscope.match(current);
/*  85 */         if (factor > bestMatchFactor) {
/*  86 */           bestMatchFactor = factor;
/*  87 */           bestMatch = current;
/*     */         } 
/*     */       } 
/*  90 */       if (bestMatch != null) {
/*  91 */         creds = map.get(bestMatch);
/*     */       }
/*     */     } 
/*  94 */     return creds;
/*     */   }
/*     */ 
/*     */   
/*     */   public Credentials getCredentials(AuthScope authscope) {
/*  99 */     Args.notNull(authscope, "Authentication scope");
/* 100 */     return matchCredentials(this.credMap, authscope);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 105 */     this.credMap.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 110 */     return this.credMap.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\BasicCredentialsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */