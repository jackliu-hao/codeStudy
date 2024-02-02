/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLDecoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.xnio.OptionMap;
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
/*     */ public class QueryParameterUtils
/*     */ {
/*     */   public static String buildQueryString(Map<String, Deque<String>> params) {
/*  45 */     StringBuilder sb = new StringBuilder();
/*  46 */     boolean first = true;
/*  47 */     for (Map.Entry<String, Deque<String>> entry : params.entrySet()) {
/*  48 */       if (((Deque)entry.getValue()).isEmpty()) {
/*  49 */         if (first) {
/*  50 */           first = false;
/*     */         } else {
/*  52 */           sb.append('&');
/*     */         } 
/*  54 */         sb.append(entry.getKey());
/*  55 */         sb.append('='); continue;
/*     */       } 
/*  57 */       for (String val : entry.getValue()) {
/*  58 */         if (first) {
/*  59 */           first = false;
/*     */         } else {
/*  61 */           sb.append('&');
/*     */         } 
/*  63 */         sb.append(entry.getKey());
/*  64 */         sb.append('=');
/*  65 */         sb.append(val);
/*     */       } 
/*     */     } 
/*     */     
/*  69 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Map<String, Deque<String>> parseQueryString(String newQueryString) {
/*  79 */     return parseQueryString(newQueryString, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, Deque<String>> parseQueryString(String newQueryString, String encoding) {
/*  88 */     Map<String, Deque<String>> newQueryParameters = new LinkedHashMap<>();
/*  89 */     int startPos = 0;
/*  90 */     int equalPos = -1;
/*  91 */     boolean needsDecode = false;
/*  92 */     for (int i = 0; i < newQueryString.length(); i++) {
/*  93 */       char c = newQueryString.charAt(i);
/*  94 */       if (c == '=' && equalPos == -1) {
/*  95 */         equalPos = i;
/*  96 */       } else if (c == '&') {
/*  97 */         handleQueryParameter(newQueryString, newQueryParameters, startPos, equalPos, i, encoding, needsDecode);
/*  98 */         needsDecode = false;
/*  99 */         startPos = i + 1;
/* 100 */         equalPos = -1;
/* 101 */       } else if ((c == '%' || c == '+') && encoding != null) {
/* 102 */         needsDecode = true;
/*     */       } 
/*     */     } 
/* 105 */     if (startPos != newQueryString.length()) {
/* 106 */       handleQueryParameter(newQueryString, newQueryParameters, startPos, equalPos, newQueryString.length(), encoding, needsDecode);
/*     */     }
/* 108 */     return newQueryParameters;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void handleQueryParameter(String newQueryString, Map<String, Deque<String>> newQueryParameters, int startPos, int equalPos, int i, String encoding, boolean needsDecode) {
/* 113 */     String key, value = "";
/* 114 */     if (equalPos == -1) {
/* 115 */       key = decodeParam(newQueryString, startPos, i, encoding, needsDecode);
/*     */     } else {
/* 117 */       key = decodeParam(newQueryString, startPos, equalPos, encoding, needsDecode);
/* 118 */       value = decodeParam(newQueryString, equalPos + 1, i, encoding, needsDecode);
/*     */     } 
/*     */     
/* 121 */     Deque<String> queue = newQueryParameters.get(key);
/* 122 */     if (queue == null) {
/* 123 */       newQueryParameters.put(key, queue = new ArrayDeque<>(1));
/*     */     }
/* 125 */     if (value != null) {
/* 126 */       queue.add(value);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String decodeParam(String newQueryString, int startPos, int equalPos, String encoding, boolean needsDecode) {
/*     */     String key;
/* 132 */     if (needsDecode) {
/*     */       try {
/* 134 */         key = URLDecoder.decode(newQueryString.substring(startPos, equalPos), encoding);
/* 135 */       } catch (UnsupportedEncodingException e) {
/* 136 */         key = newQueryString.substring(startPos, equalPos);
/*     */       } 
/*     */     } else {
/* 139 */       key = newQueryString.substring(startPos, equalPos);
/*     */     } 
/* 141 */     return key;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static Map<String, Deque<String>> mergeQueryParametersWithNewQueryString(Map<String, Deque<String>> queryParameters, String newQueryString) {
/* 146 */     return mergeQueryParametersWithNewQueryString(queryParameters, newQueryString, StandardCharsets.UTF_8.name());
/*     */   }
/*     */ 
/*     */   
/*     */   public static Map<String, Deque<String>> mergeQueryParametersWithNewQueryString(Map<String, Deque<String>> queryParameters, String newQueryString, String encoding) {
/* 151 */     Map<String, Deque<String>> newQueryParameters = parseQueryString(newQueryString, encoding);
/*     */     
/* 153 */     for (Map.Entry<String, Deque<String>> entry : queryParameters.entrySet()) {
/* 154 */       if (!newQueryParameters.containsKey(entry.getKey())) {
/* 155 */         newQueryParameters.put(entry.getKey(), new ArrayDeque<>(entry.getValue())); continue;
/*     */       } 
/* 157 */       ((Deque)newQueryParameters.get(entry.getKey())).addAll(entry.getValue());
/*     */     } 
/*     */     
/* 160 */     return newQueryParameters;
/*     */   }
/*     */   
/*     */   public static String getQueryParamEncoding(HttpServerExchange exchange) {
/* 164 */     String encoding = null;
/* 165 */     OptionMap undertowOptions = exchange.getConnection().getUndertowOptions();
/* 166 */     if (undertowOptions.get(UndertowOptions.DECODE_URL, true)) {
/* 167 */       encoding = (String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name());
/*     */     }
/* 169 */     return encoding;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\QueryParameterUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */