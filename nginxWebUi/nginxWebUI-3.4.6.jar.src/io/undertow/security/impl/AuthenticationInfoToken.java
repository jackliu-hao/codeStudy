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
/*    */ public enum AuthenticationInfoToken
/*    */   implements HeaderToken
/*    */ {
/* 35 */   NEXT_NONCE(Headers.NEXT_NONCE, true),
/* 36 */   MESSAGE_QOP(Headers.QOP, true),
/* 37 */   RESPONSE_AUTH(Headers.RESPONSE_AUTH, true),
/* 38 */   CNONCE(Headers.CNONCE, true),
/* 39 */   NONCE_COUNT(Headers.NONCE_COUNT, false);
/*    */   private static final HeaderTokenParser<AuthenticationInfoToken> TOKEN_PARSER;
/*    */   private final String name;
/*    */   private final boolean quoted;
/*    */   
/*    */   static {
/* 45 */     Map<String, AuthenticationInfoToken> expected = new LinkedHashMap<>((values()).length);
/* 46 */     for (AuthenticationInfoToken current : values()) {
/* 47 */       expected.put(current.getName(), current);
/*    */     }
/*    */     
/* 50 */     TOKEN_PARSER = new HeaderTokenParser(Collections.unmodifiableMap(expected));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   AuthenticationInfoToken(HttpString name, boolean quoted) {
/* 57 */     this.name = name.toString();
/* 58 */     this.quoted = quoted;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 62 */     return this.name;
/*    */   }
/*    */   
/*    */   public boolean isAllowQuoted() {
/* 66 */     return this.quoted;
/*    */   }
/*    */   
/*    */   public static Map<AuthenticationInfoToken, String> parseHeader(String header) {
/* 70 */     return TOKEN_PARSER.parseHeader(header);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\AuthenticationInfoToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */