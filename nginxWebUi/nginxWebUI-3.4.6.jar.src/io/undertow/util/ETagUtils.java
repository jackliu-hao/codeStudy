/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ 
/*     */ public class ETagUtils
/*     */ {
/*     */   private static final char COMMA = ',';
/*     */   private static final char QUOTE = '"';
/*     */   private static final char W = 'W';
/*     */   private static final char SLASH = '/';
/*     */   
/*     */   public static boolean handleIfMatch(HttpServerExchange exchange, ETag etag, boolean allowWeak) {
/*  45 */     return handleIfMatch(exchange, Collections.singletonList(etag), allowWeak);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfMatch(HttpServerExchange exchange, List<ETag> etags, boolean allowWeak) {
/*  56 */     return handleIfMatch(exchange.getRequestHeaders().getFirst(Headers.IF_MATCH), etags, allowWeak);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfMatch(String ifMatch, ETag etag, boolean allowWeak) {
/*  67 */     return handleIfMatch(ifMatch, Collections.singletonList(etag), allowWeak);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfMatch(String ifMatch, List<ETag> etags, boolean allowWeak) {
/*  78 */     if (ifMatch == null) {
/*  79 */       return true;
/*     */     }
/*  81 */     if (ifMatch.equals("*")) {
/*  82 */       return true;
/*     */     }
/*  84 */     List<ETag> parts = parseETagList(ifMatch);
/*  85 */     for (ETag part : parts) {
/*  86 */       if (part.isWeak() && !allowWeak) {
/*     */         continue;
/*     */       }
/*  89 */       for (ETag tag : etags) {
/*  90 */         if (tag == null || (
/*  91 */           tag.isWeak() && !allowWeak)) {
/*     */           continue;
/*     */         }
/*  94 */         if (tag.getTag().equals(part.getTag())) {
/*  95 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 100 */     return false;
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
/*     */   public static boolean handleIfNoneMatch(HttpServerExchange exchange, ETag etag, boolean allowWeak) {
/* 112 */     return handleIfNoneMatch(exchange, Collections.singletonList(etag), allowWeak);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfNoneMatch(HttpServerExchange exchange, List<ETag> etags, boolean allowWeak) {
/* 123 */     return handleIfNoneMatch(exchange.getRequestHeaders().getFirst(Headers.IF_NONE_MATCH), etags, allowWeak);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfNoneMatch(String ifNoneMatch, ETag etag, boolean allowWeak) {
/* 134 */     return handleIfNoneMatch(ifNoneMatch, Collections.singletonList(etag), allowWeak);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfNoneMatch(String ifNoneMatch, List<ETag> etags, boolean allowWeak) {
/* 145 */     if (ifNoneMatch == null) {
/* 146 */       return true;
/*     */     }
/* 148 */     List<ETag> parts = parseETagList(ifNoneMatch);
/* 149 */     for (ETag part : parts) {
/* 150 */       if (part.getTag().equals("*")) {
/* 151 */         return false;
/*     */       }
/* 153 */       if (part.isWeak() && !allowWeak) {
/*     */         continue;
/*     */       }
/* 156 */       for (ETag tag : etags) {
/* 157 */         if (tag == null || (
/* 158 */           tag.isWeak() && !allowWeak)) {
/*     */           continue;
/*     */         }
/* 161 */         if (tag.getTag().equals(part.getTag())) {
/* 162 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 167 */     return true;
/*     */   }
/*     */   
/*     */   public static List<ETag> parseETagList(String header) {
/* 171 */     char[] headerChars = header.toCharArray();
/*     */ 
/*     */     
/* 174 */     List<ETag> response = new ArrayList<>();
/*     */     
/* 176 */     SearchingFor searchingFor = SearchingFor.START_OF_VALUE;
/* 177 */     int valueStart = 0;
/* 178 */     boolean weak = false;
/* 179 */     boolean malformed = false;
/*     */     
/* 181 */     for (int i = 0; i < headerChars.length; i++) {
/* 182 */       switch (searchingFor) {
/*     */         case START_OF_VALUE:
/* 184 */           if (headerChars[i] != ',' && !Character.isWhitespace(headerChars[i])) {
/* 185 */             if (headerChars[i] == '"') {
/* 186 */               valueStart = i + 1;
/* 187 */               searchingFor = SearchingFor.LAST_QUOTE;
/* 188 */               weak = false;
/* 189 */               malformed = false; break;
/* 190 */             }  if (headerChars[i] == 'W') {
/* 191 */               searchingFor = SearchingFor.WEAK_SLASH;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         case WEAK_SLASH:
/* 196 */           if (headerChars[i] == '"') {
/* 197 */             valueStart = i + 1;
/* 198 */             searchingFor = SearchingFor.LAST_QUOTE;
/* 199 */             weak = true;
/* 200 */             malformed = false; break;
/* 201 */           }  if (headerChars[i] != '/') {
/* 202 */             malformed = true;
/* 203 */             searchingFor = SearchingFor.END_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */         case LAST_QUOTE:
/* 207 */           if (headerChars[i] == '"') {
/* 208 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 209 */             response.add(new ETag(weak, value.trim()));
/* 210 */             searchingFor = SearchingFor.START_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */         case END_OF_VALUE:
/* 214 */           if ((headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) && 
/* 215 */             !malformed) {
/* 216 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 217 */             response.add(new ETag(weak, value.trim()));
/* 218 */             searchingFor = SearchingFor.START_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 225 */     if ((searchingFor == SearchingFor.END_OF_VALUE || searchingFor == SearchingFor.LAST_QUOTE) && 
/* 226 */       !malformed) {
/*     */       
/* 228 */       String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
/* 229 */       response.add(new ETag(weak, value.trim()));
/*     */     } 
/*     */ 
/*     */     
/* 233 */     return response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ETag getETag(HttpServerExchange exchange) {
/* 241 */     String tag = exchange.getResponseHeaders().getFirst(Headers.ETAG);
/* 242 */     if (tag == null) {
/* 243 */       return null;
/*     */     }
/* 245 */     char[] headerChars = tag.toCharArray();
/* 246 */     SearchingFor searchingFor = SearchingFor.START_OF_VALUE;
/* 247 */     int valueStart = 0;
/* 248 */     boolean weak = false;
/* 249 */     boolean malformed = false;
/* 250 */     for (int i = 0; i < headerChars.length; i++) {
/* 251 */       switch (searchingFor) {
/*     */         case START_OF_VALUE:
/* 253 */           if (headerChars[i] != ',' && !Character.isWhitespace(headerChars[i])) {
/* 254 */             if (headerChars[i] == '"') {
/* 255 */               valueStart = i + 1;
/* 256 */               searchingFor = SearchingFor.LAST_QUOTE;
/* 257 */               weak = false;
/* 258 */               malformed = false; break;
/* 259 */             }  if (headerChars[i] == 'W') {
/* 260 */               searchingFor = SearchingFor.WEAK_SLASH;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         case WEAK_SLASH:
/* 265 */           if (headerChars[i] == '"') {
/* 266 */             valueStart = i + 1;
/* 267 */             searchingFor = SearchingFor.LAST_QUOTE;
/* 268 */             weak = true;
/* 269 */             malformed = false; break;
/* 270 */           }  if (headerChars[i] != '/') {
/* 271 */             return null;
/*     */           }
/*     */           break;
/*     */         case LAST_QUOTE:
/* 275 */           if (headerChars[i] == '"') {
/* 276 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 277 */             return new ETag(weak, value.trim());
/*     */           } 
/*     */           break;
/*     */         case END_OF_VALUE:
/* 281 */           if ((headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) && 
/* 282 */             !malformed) {
/* 283 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 284 */             return new ETag(weak, value.trim());
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 290 */     if ((searchingFor == SearchingFor.END_OF_VALUE || searchingFor == SearchingFor.LAST_QUOTE) && 
/* 291 */       !malformed) {
/*     */       
/* 293 */       String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
/* 294 */       return new ETag(weak, value.trim());
/*     */     } 
/*     */ 
/*     */     
/* 298 */     return null;
/*     */   }
/*     */   
/*     */   enum SearchingFor {
/* 302 */     START_OF_VALUE, LAST_QUOTE, END_OF_VALUE, WEAK_SLASH;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ETagUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */