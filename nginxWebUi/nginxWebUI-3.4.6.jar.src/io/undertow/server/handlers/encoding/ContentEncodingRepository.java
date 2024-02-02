/*     */ package io.undertow.server.handlers.encoding;
/*     */ 
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.QValueParser;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class ContentEncodingRepository
/*     */ {
/*     */   public static final String IDENTITY = "identity";
/*  41 */   public static final EncodingMapping IDENTITY_ENCODING = new EncodingMapping("identity", ContentEncodingProvider.IDENTITY, 0, Predicates.truePredicate());
/*     */   
/*  43 */   private final Map<String, EncodingMapping> encodingMap = (Map<String, EncodingMapping>)new CopyOnWriteMap();
/*     */ 
/*     */   
/*     */   public AllowedContentEncodings getContentEncodings(HttpServerExchange exchange) {
/*  47 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.ACCEPT_ENCODING);
/*  48 */     if (headerValues == null || headerValues.isEmpty()) {
/*  49 */       return null;
/*     */     }
/*  51 */     List<EncodingMapping> resultingMappings = new ArrayList<>();
/*  52 */     List<List<QValueParser.QValueResult>> found = QValueParser.parse((List)headerValues);
/*  53 */     for (List<QValueParser.QValueResult> result : found) {
/*  54 */       List<EncodingMapping> available = new ArrayList<>();
/*  55 */       boolean includesIdentity = false;
/*  56 */       boolean isQValue0 = false;
/*     */       
/*  58 */       for (QValueParser.QValueResult value : result) {
/*     */         EncodingMapping encoding;
/*  60 */         if (value.getValue().equals("*")) {
/*  61 */           includesIdentity = true;
/*  62 */           encoding = IDENTITY_ENCODING;
/*     */         } else {
/*  64 */           encoding = this.encodingMap.get(value.getValue());
/*  65 */           if (encoding == null && "identity".equals(value.getValue())) {
/*  66 */             encoding = IDENTITY_ENCODING;
/*     */           }
/*     */         } 
/*  69 */         if (value.isQValueZero()) {
/*  70 */           isQValue0 = true;
/*     */         }
/*  72 */         if (encoding != null) {
/*  73 */           available.add(encoding);
/*     */         }
/*     */       } 
/*  76 */       if (isQValue0) {
/*  77 */         if (resultingMappings.isEmpty()) {
/*  78 */           if (includesIdentity) {
/*  79 */             return new AllowedContentEncodings(exchange, Collections.emptyList());
/*     */           }
/*  81 */           return null;
/*     */         }  continue;
/*     */       } 
/*  84 */       if (!available.isEmpty()) {
/*  85 */         Collections.sort(available, Collections.reverseOrder());
/*  86 */         resultingMappings.addAll(available);
/*     */       } 
/*     */     } 
/*  89 */     if (!resultingMappings.isEmpty()) {
/*  90 */       return new AllowedContentEncodings(exchange, resultingMappings);
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized ContentEncodingRepository addEncodingHandler(String encoding, ContentEncodingProvider encoder, int priority) {
/*  96 */     addEncodingHandler(encoding, encoder, priority, Predicates.truePredicate());
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized ContentEncodingRepository addEncodingHandler(String encoding, ContentEncodingProvider encoder, int priority, Predicate enabledPredicate) {
/* 101 */     this.encodingMap.put(encoding, new EncodingMapping(encoding, encoder, priority, enabledPredicate));
/* 102 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized ContentEncodingRepository removeEncodingHandler(String encoding) {
/* 106 */     this.encodingMap.remove(encoding);
/* 107 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\ContentEncodingRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */