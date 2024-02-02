/*     */ package io.undertow.attribute;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.conduits.StoredResponseStreamSinkConduit;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class StoredResponse
/*     */   implements ExchangeAttribute
/*     */ {
/*  35 */   public static final ExchangeAttribute INSTANCE = new StoredResponse();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readAttribute(HttpServerExchange exchange) {
/*  43 */     byte[] data = (byte[])exchange.getAttachment(StoredResponseStreamSinkConduit.RESPONSE);
/*  44 */     if (data == null) {
/*  45 */       return null;
/*     */     }
/*  47 */     String charset = extractCharset(exchange.getResponseHeaders());
/*  48 */     if (charset == null) {
/*  49 */       return null;
/*     */     }
/*     */     try {
/*  52 */       return new String(data, charset);
/*  53 */     } catch (UnsupportedEncodingException e) {
/*  54 */       UndertowLogger.ROOT_LOGGER.debugf(e, "Could not decode response body using charset %s", charset);
/*  55 */       return null;
/*     */     } 
/*     */   }
/*     */   private String extractCharset(HeaderMap headers) {
/*  59 */     String contentType = headers.getFirst(Headers.CONTENT_TYPE);
/*  60 */     if (contentType != null) {
/*  61 */       String value = Headers.extractQuotedValueFromHeader(contentType, "charset");
/*  62 */       if (value != null) {
/*  63 */         return value;
/*     */       }
/*     */       
/*  66 */       if (contentType.startsWith("text/")) {
/*  67 */         return StandardCharsets.ISO_8859_1.displayName();
/*     */       }
/*  69 */       return null;
/*     */     } 
/*  71 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/*  76 */     throw new ReadOnlyAttributeException("Stored Response", newValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  81 */     return "%{STORED_RESPONSE}";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements ExchangeAttributeBuilder
/*     */   {
/*     */     public String name() {
/*  88 */       return "Stored Response";
/*     */     }
/*     */ 
/*     */     
/*     */     public ExchangeAttribute build(String token) {
/*  93 */       if (token.equals("%{STORED_RESPONSE}")) {
/*  94 */         return StoredResponse.INSTANCE;
/*     */       }
/*  96 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int priority() {
/* 101 */       return 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\StoredResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */