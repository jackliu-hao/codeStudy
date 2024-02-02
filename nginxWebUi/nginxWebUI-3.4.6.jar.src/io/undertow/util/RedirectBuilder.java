/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Deque;
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
/*     */ 
/*     */ public class RedirectBuilder
/*     */ {
/*  36 */   public static final String UTF_8 = StandardCharsets.UTF_8.name();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String redirect(HttpServerExchange exchange, String newRelativePath) {
/*  46 */     return redirect(exchange, newRelativePath, true);
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
/*     */   public static String redirect(HttpServerExchange exchange, String newRelativePath, boolean includeParameters) {
/*     */     try {
/*  59 */       StringBuilder uri = new StringBuilder(exchange.getRequestScheme());
/*  60 */       uri.append("://");
/*  61 */       uri.append(exchange.getHostAndPort());
/*  62 */       uri.append(encodeUrlPart(exchange.getResolvedPath()));
/*  63 */       if (exchange.getResolvedPath().endsWith("/")) {
/*  64 */         if (newRelativePath.startsWith("/")) {
/*  65 */           uri.append(encodeUrlPart(newRelativePath.substring(1)));
/*     */         } else {
/*  67 */           uri.append(encodeUrlPart(newRelativePath));
/*     */         } 
/*     */       } else {
/*  70 */         if (!newRelativePath.startsWith("/")) {
/*  71 */           uri.append('/');
/*     */         }
/*  73 */         uri.append(encodeUrlPart(newRelativePath));
/*     */       } 
/*  75 */       if (includeParameters) {
/*  76 */         if (!exchange.getPathParameters().isEmpty()) {
/*  77 */           boolean first = true;
/*  78 */           uri.append(';');
/*  79 */           for (Map.Entry<String, Deque<String>> param : (Iterable<Map.Entry<String, Deque<String>>>)exchange.getPathParameters().entrySet()) {
/*  80 */             for (String value : param.getValue()) {
/*  81 */               if (first) {
/*  82 */                 first = false;
/*     */               } else {
/*  84 */                 uri.append('&');
/*     */               } 
/*  86 */               uri.append(URLEncoder.encode(param.getKey(), UTF_8));
/*  87 */               uri.append('=');
/*  88 */               uri.append(URLEncoder.encode(value, UTF_8));
/*     */             } 
/*     */           } 
/*     */         } 
/*  92 */         if (!exchange.getQueryString().isEmpty()) {
/*  93 */           uri.append('?');
/*  94 */           uri.append(exchange.getQueryString());
/*     */         } 
/*     */       } 
/*  97 */       return uri.toString();
/*  98 */     } catch (UnsupportedEncodingException e) {
/*  99 */       throw new RuntimeException(e);
/*     */     } 
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
/*     */   private static String encodeUrlPart(String part) throws UnsupportedEncodingException {
/* 114 */     int pos = 0;
/* 115 */     for (int i = 0; i < part.length(); i++) {
/* 116 */       char c = part.charAt(i);
/* 117 */       if (c == '?')
/*     */         break; 
/* 119 */       if (c == '/') {
/* 120 */         if (pos != i) {
/* 121 */           String original = part.substring(pos, i);
/* 122 */           String encoded = URLEncoder.encode(original, UTF_8);
/* 123 */           if (!encoded.equals(original)) {
/* 124 */             return realEncode(part, pos);
/*     */           }
/*     */         } 
/* 127 */         pos = i + 1;
/* 128 */       } else if (c == ' ') {
/* 129 */         return realEncode(part, pos);
/*     */       } 
/*     */     } 
/* 132 */     return part;
/*     */   }
/*     */   
/*     */   private static String realEncode(String part, int startPos) throws UnsupportedEncodingException {
/* 136 */     StringBuilder sb = new StringBuilder();
/* 137 */     sb.append(part.substring(0, startPos));
/* 138 */     int pos = startPos;
/* 139 */     for (int i = startPos; i < part.length(); i++) {
/* 140 */       char c = part.charAt(i);
/* 141 */       if (c == '?')
/*     */         break; 
/* 143 */       if (c == '/' && 
/* 144 */         pos != i) {
/* 145 */         String str1 = part.substring(pos, i);
/* 146 */         String str2 = URLEncoder.encode(str1, UTF_8);
/* 147 */         sb.append(str2);
/* 148 */         sb.append('/');
/* 149 */         pos = i + 1;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 154 */     String original = part.substring(pos);
/* 155 */     String encoded = URLEncoder.encode(original, UTF_8);
/* 156 */     sb.append(encoded);
/* 157 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\RedirectBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */