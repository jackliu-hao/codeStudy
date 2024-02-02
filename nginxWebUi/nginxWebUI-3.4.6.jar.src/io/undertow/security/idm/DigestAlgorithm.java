/*    */ package io.undertow.security.idm;
/*    */ 
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public enum DigestAlgorithm
/*    */ {
/* 33 */   MD5("MD5", "MD5", false), MD5_SESS("MD5-sess", "MD5", true);
/*    */   private final boolean session;
/*    */   private final String digestAlgorithm;
/*    */   
/*    */   static {
/* 38 */     DigestAlgorithm[] algorithms = values();
/*    */     
/* 40 */     Map<String, DigestAlgorithm> byToken = new HashMap<>(algorithms.length);
/* 41 */     for (DigestAlgorithm current : algorithms) {
/* 42 */       byToken.put(current.token, current);
/*    */     }
/*    */     
/* 45 */     BY_TOKEN = Collections.unmodifiableMap(byToken);
/*    */   }
/*    */ 
/*    */   
/*    */   private final String token;
/*    */   private static final Map<String, DigestAlgorithm> BY_TOKEN;
/*    */   
/*    */   DigestAlgorithm(String token, String digestAlgorithm, boolean session) {
/* 53 */     this.token = token;
/* 54 */     this.digestAlgorithm = digestAlgorithm;
/* 55 */     this.session = session;
/*    */   }
/*    */   
/*    */   public String getToken() {
/* 59 */     return this.token;
/*    */   }
/*    */   
/*    */   public String getAlgorithm() {
/* 63 */     return this.digestAlgorithm;
/*    */   }
/*    */   
/*    */   public boolean isSession() {
/* 67 */     return this.session;
/*    */   }
/*    */   
/*    */   public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
/* 71 */     return MessageDigest.getInstance(this.digestAlgorithm);
/*    */   }
/*    */   
/*    */   public static DigestAlgorithm forName(String name) {
/* 75 */     return BY_TOKEN.get(name);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\idm\DigestAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */