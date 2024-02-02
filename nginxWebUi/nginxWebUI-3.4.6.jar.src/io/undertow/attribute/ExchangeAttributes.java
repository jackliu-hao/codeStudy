/*     */ package io.undertow.attribute;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ public class ExchangeAttributes
/*     */ {
/*     */   public static ExchangeAttributeParser parser(ClassLoader classLoader) {
/*  35 */     return new ExchangeAttributeParser(classLoader, Collections.emptyList());
/*     */   }
/*     */   
/*     */   public static ExchangeAttributeParser parser(ClassLoader classLoader, ExchangeAttributeWrapper... wrappers) {
/*  39 */     return new ExchangeAttributeParser(classLoader, Arrays.asList(wrappers));
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute cookie(String cookieName) {
/*  43 */     return new CookieAttribute(cookieName);
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute bytesSent(boolean dashIfZero) {
/*  47 */     return new BytesSentAttribute(dashIfZero);
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute dateTime() {
/*  51 */     return DateTimeAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute localIp() {
/*  55 */     return LocalIPAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute localPort() {
/*  59 */     return LocalPortAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute localServerName() {
/*  63 */     return LocalServerNameAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute queryString() {
/*  67 */     return QueryStringAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute relativePath() {
/*  71 */     return RelativePathAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute remoteIp() {
/*  75 */     return RemoteIPAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute remoteObfuscatedIp() {
/*  79 */     return RemoteObfuscatedIPAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute remoteUser() {
/*  83 */     return RemoteUserAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute requestHeader(HttpString header) {
/*  87 */     return new RequestHeaderAttribute(header);
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute requestList() {
/*  91 */     return RequestLineAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute requestMethod() {
/*  95 */     return RequestMethodAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute requestProtocol() {
/*  99 */     return RequestProtocolAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute requestURL() {
/* 103 */     return RequestURLAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute responseCode() {
/* 107 */     return ResponseCodeAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute responseReasonPhrase() {
/* 111 */     return ResponseReasonPhraseAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute responseHeader(HttpString header) {
/* 115 */     return new ResponseHeaderAttribute(header);
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute transportProtocol() {
/* 119 */     return TransportProtocolAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute threadName() {
/* 123 */     return ThreadNameAttribute.INSTANCE;
/*     */   }
/*     */   
/*     */   public static ExchangeAttribute constant(String value) {
/* 127 */     return new ConstantExchangeAttribute(value);
/*     */   }
/*     */   
/*     */   public static String resolve(HttpServerExchange exchange, ExchangeAttribute[] attributes) {
/* 131 */     StringBuilder result = new StringBuilder();
/* 132 */     for (int i = 0; i < attributes.length; i++) {
/* 133 */       String str = attributes[i].readAttribute(exchange);
/* 134 */       if (str != null) {
/* 135 */         result.append(str);
/*     */       }
/*     */     } 
/* 138 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExchangeAttribute authenticationType() {
/* 147 */     return AuthenticationTypeExchangeAttribute.INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ExchangeAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */