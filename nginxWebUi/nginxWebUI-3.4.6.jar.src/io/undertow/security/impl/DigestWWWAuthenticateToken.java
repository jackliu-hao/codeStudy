/*    */ package io.undertow.security.impl;
/*    */ 
/*    */ import io.undertow.util.HeaderToken;
/*    */ import io.undertow.util.HeaderTokenParser;
/*    */ import io.undertow.util.Headers;
/*    */ import io.undertow.util.HttpString;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedHashMap;
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
/*    */ public enum DigestWWWAuthenticateToken
/*    */   implements HeaderToken
/*    */ {
/* 36 */   REALM(Headers.REALM, true),
/* 37 */   DOMAIN(Headers.DOMAIN, true),
/* 38 */   NONCE(Headers.NONCE, true),
/* 39 */   OPAQUE(Headers.OPAQUE, true),
/* 40 */   STALE(Headers.STALE, false),
/* 41 */   ALGORITHM(Headers.ALGORITHM, false),
/* 42 */   MESSAGE_QOP(Headers.QOP, true),
/* 43 */   AUTH_PARAM(Headers.AUTH_PARAM, false);
/*    */   private static final HeaderTokenParser<DigestWWWAuthenticateToken> TOKEN_PARSER;
/*    */   private final String name;
/*    */   private final boolean quoted;
/*    */   
/*    */   static {
/* 49 */     Map<String, DigestWWWAuthenticateToken> expected = new LinkedHashMap<>((values()).length);
/* 50 */     for (DigestWWWAuthenticateToken current : values()) {
/* 51 */       expected.put(current.getName(), current);
/*    */     }
/*    */     
/* 54 */     TOKEN_PARSER = new HeaderTokenParser(Collections.unmodifiableMap(expected));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   DigestWWWAuthenticateToken(HttpString name, boolean quoted) {
/* 61 */     this.name = name.toString();
/* 62 */     this.quoted = quoted;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 66 */     return this.name;
/*    */   }
/*    */   
/*    */   public boolean isAllowQuoted() {
/* 70 */     return this.quoted;
/*    */   }
/*    */   
/*    */   public static Map<DigestWWWAuthenticateToken, String> parseHeader(String header) {
/* 74 */     return TOKEN_PARSER.parseHeader(header);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\DigestWWWAuthenticateToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */