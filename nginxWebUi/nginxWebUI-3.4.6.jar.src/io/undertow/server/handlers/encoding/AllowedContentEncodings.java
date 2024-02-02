/*     */ package io.undertow.server.handlers.encoding;
/*     */ 
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import java.util.List;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class AllowedContentEncodings
/*     */   implements ConduitWrapper<StreamSinkConduit>
/*     */ {
/*  39 */   public static final AttachmentKey<AllowedContentEncodings> ATTACHMENT_KEY = AttachmentKey.create(AllowedContentEncodings.class);
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */   
/*     */   private final List<EncodingMapping> encodings;
/*     */   
/*     */   public AllowedContentEncodings(HttpServerExchange exchange, List<EncodingMapping> encodings) {
/*  46 */     this.exchange = exchange;
/*  47 */     this.encodings = encodings;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCurrentContentEncoding() {
/*  54 */     for (EncodingMapping encoding : this.encodings) {
/*  55 */       if (encoding.getAllowed() == null || encoding.getAllowed().resolve(this.exchange)) {
/*  56 */         return encoding.getName();
/*     */       }
/*     */     } 
/*  59 */     return Headers.IDENTITY.toString();
/*     */   }
/*     */   
/*     */   public EncodingMapping getEncoding() {
/*  63 */     for (EncodingMapping encoding : this.encodings) {
/*  64 */       if (encoding.getAllowed() == null || encoding.getAllowed().resolve(this.exchange)) {
/*  65 */         return encoding;
/*     */       }
/*     */     } 
/*  68 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isIdentity() {
/*  72 */     return getCurrentContentEncoding().equals(Headers.IDENTITY.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNoEncodingsAllowed() {
/*  80 */     return this.encodings.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/*  85 */     if (exchange.getResponseHeaders().contains(Headers.CONTENT_ENCODING))
/*     */     {
/*  87 */       return (StreamSinkConduit)factory.create();
/*     */     }
/*     */     
/*  90 */     if (exchange.getResponseContentLength() != 0L && exchange
/*  91 */       .getStatusCode() != 204 && exchange
/*  92 */       .getStatusCode() != 304) {
/*  93 */       EncodingMapping encoding = getEncoding();
/*  94 */       if (encoding != null) {
/*  95 */         exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, encoding.getName());
/*  96 */         if (exchange.getRequestMethod().equals(Methods.HEAD))
/*     */         {
/*  98 */           return (StreamSinkConduit)factory.create();
/*     */         }
/* 100 */         return (StreamSinkConduit)encoding.getEncoding().getResponseWrapper().wrap(factory, exchange);
/*     */       } 
/*     */     } 
/*     */     
/* 104 */     return (StreamSinkConduit)factory.create();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\AllowedContentEncodings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */