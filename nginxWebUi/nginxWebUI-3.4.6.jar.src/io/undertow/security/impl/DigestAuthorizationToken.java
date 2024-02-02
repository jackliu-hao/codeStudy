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
/*    */ public enum DigestAuthorizationToken
/*    */   implements HeaderToken
/*    */ {
/* 36 */   USERNAME(Headers.USERNAME, true),
/* 37 */   REALM(Headers.REALM, true),
/* 38 */   NONCE(Headers.NONCE, true),
/* 39 */   DIGEST_URI(Headers.URI, true),
/* 40 */   RESPONSE(Headers.RESPONSE, true),
/* 41 */   ALGORITHM(Headers.ALGORITHM, true),
/* 42 */   CNONCE(Headers.CNONCE, true),
/* 43 */   OPAQUE(Headers.OPAQUE, true),
/* 44 */   MESSAGE_QOP(Headers.QOP, true),
/* 45 */   NONCE_COUNT(Headers.NONCE_COUNT, false),
/* 46 */   AUTH_PARAM(Headers.AUTH_PARAM, false);
/*    */   private static final HeaderTokenParser<DigestAuthorizationToken> TOKEN_PARSER;
/*    */   private final String name;
/*    */   private final boolean quoted;
/*    */   
/*    */   static {
/* 52 */     Map<String, DigestAuthorizationToken> expected = new LinkedHashMap<>((values()).length);
/* 53 */     for (DigestAuthorizationToken current : values()) {
/* 54 */       expected.put(current.getName(), current);
/*    */     }
/*    */     
/* 57 */     TOKEN_PARSER = new HeaderTokenParser(Collections.unmodifiableMap(expected));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   DigestAuthorizationToken(HttpString name, boolean quoted) {
/* 64 */     this.name = name.toString();
/* 65 */     this.quoted = quoted;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 70 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isAllowQuoted() {
/* 75 */     return this.quoted;
/*    */   }
/*    */   
/*    */   public static Map<DigestAuthorizationToken, String> parseHeader(String header) {
/* 79 */     return TOKEN_PARSER.parseHeader(header);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\DigestAuthorizationToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */