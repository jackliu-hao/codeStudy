/*     */ package io.undertow.client.http;
/*     */ 
/*     */ import io.undertow.util.BadRequestException;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class HttpResponseParser
/*     */ {
/*     */   public static final HttpResponseParser INSTANCE;
/*     */   private static final int NORMAL = 0;
/*     */   private static final int WHITESPACE = 1;
/*     */   private static final int BEGIN_LINE_END = 2;
/*     */   private static final int LINE_END = 3;
/*     */   private static final int AWAIT_DATA_END = 4;
/*     */   
/*     */   static {
/*     */     try {
/* 114 */       Class<?> cls = Class.forName(HttpResponseParser.class.getName() + "$$generated", false, HttpResponseParser.class.getClassLoader());
/* 115 */       INSTANCE = (HttpResponseParser)cls.newInstance();
/* 116 */     } catch (Exception e) {
/* 117 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(ByteBuffer buffer, ResponseParseState currentState, HttpResponseBuilder builder) throws BadRequestException {
/* 127 */     if (currentState.state == 0) {
/* 128 */       handleHttpVersion(buffer, currentState, builder);
/* 129 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 133 */     if (currentState.state == 1) {
/* 134 */       handleStatusCode(buffer, currentState, builder);
/* 135 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 139 */     if (currentState.state == 2) {
/* 140 */       handleReasonPhrase(buffer, currentState, builder);
/* 141 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 145 */     if (currentState.state == 3) {
/* 146 */       handleAfterReasonPhrase(buffer, currentState, builder);
/* 147 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 151 */     while (currentState.state != 6) {
/* 152 */       if (currentState.state == 4) {
/* 153 */         handleHeader(buffer, currentState, builder);
/* 154 */         if (!buffer.hasRemaining()) {
/*     */           return;
/*     */         }
/*     */       } 
/* 158 */       if (currentState.state == 5) {
/* 159 */         handleHeaderValue(buffer, currentState, builder);
/* 160 */         if (!buffer.hasRemaining()) {
/*     */           return;
/*     */         }
/*     */       } 
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
/*     */   final void handleStatusCode(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
/* 177 */     StringBuilder stringBuilder = state.stringBuilder;
/* 178 */     while (buffer.hasRemaining()) {
/* 179 */       char next = (char)buffer.get();
/* 180 */       if (next == ' ' || next == '\t') {
/* 181 */         builder.setStatusCode(Integer.parseInt(stringBuilder.toString()));
/* 182 */         state.state = 2;
/* 183 */         state.stringBuilder.setLength(0);
/* 184 */         state.parseState = 0;
/* 185 */         state.pos = 0;
/* 186 */         state.nextHeader = null;
/*     */         return;
/*     */       } 
/* 189 */       stringBuilder.append(next);
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
/*     */   final void handleReasonPhrase(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
/* 204 */     StringBuilder stringBuilder = state.stringBuilder;
/* 205 */     while (buffer.hasRemaining()) {
/* 206 */       char next = (char)buffer.get();
/* 207 */       if (next == '\n' || next == '\r') {
/* 208 */         builder.setReasonPhrase(stringBuilder.toString());
/* 209 */         state.state = 3;
/* 210 */         state.stringBuilder.setLength(0);
/* 211 */         state.parseState = 0;
/* 212 */         state.leftOver = (byte)next;
/* 213 */         state.pos = 0;
/* 214 */         state.nextHeader = null;
/*     */         return;
/*     */       } 
/* 217 */       stringBuilder.append(next);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void handleHeaderValue(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
/* 241 */     StringBuilder stringBuilder = state.stringBuilder;
/* 242 */     if (stringBuilder == null) {
/* 243 */       stringBuilder = new StringBuilder();
/* 244 */       state.parseState = 0;
/*     */     } 
/*     */     
/* 247 */     int parseState = state.parseState;
/* 248 */     while (buffer.hasRemaining()) {
/* 249 */       HttpString nextStandardHeader; String headerValue; byte next = buffer.get();
/* 250 */       switch (parseState) {
/*     */         case 0:
/* 252 */           if (next == 13) {
/* 253 */             parseState = 2; continue;
/* 254 */           }  if (next == 10) {
/* 255 */             parseState = 3; continue;
/* 256 */           }  if (next == 32 || next == 9) {
/* 257 */             parseState = 1; continue;
/*     */           } 
/* 259 */           stringBuilder.append((char)next);
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/* 264 */           if (next == 13) {
/* 265 */             parseState = 2; continue;
/* 266 */           }  if (next == 10) {
/* 267 */             parseState = 3; continue;
/* 268 */           }  if (next == 32 || next == 9)
/*     */             continue; 
/* 270 */           if (stringBuilder.length() > 0) {
/* 271 */             stringBuilder.append(' ');
/*     */           }
/* 273 */           stringBuilder.append((char)next);
/* 274 */           parseState = 0;
/*     */ 
/*     */ 
/*     */         
/*     */         case 2:
/*     */         case 3:
/* 280 */           if (next == 10 && parseState == 2) {
/* 281 */             parseState = 3; continue;
/* 282 */           }  if (next == 9 || next == 32) {
/*     */ 
/*     */             
/* 285 */             parseState = 1;
/*     */             continue;
/*     */           } 
/* 288 */           nextStandardHeader = state.nextHeader;
/* 289 */           headerValue = stringBuilder.toString();
/*     */ 
/*     */           
/* 292 */           builder.getResponseHeaders().add(nextStandardHeader, headerValue);
/*     */           
/* 294 */           state.nextHeader = null;
/*     */           
/* 296 */           state.leftOver = next;
/* 297 */           state.stringBuilder.setLength(0);
/* 298 */           if (next == 13) {
/* 299 */             parseState = 4; continue;
/*     */           } 
/* 301 */           state.state = 4;
/* 302 */           state.parseState = 0;
/*     */           return;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 4:
/* 309 */           state.state = 6;
/*     */           return;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 315 */     state.parseState = parseState;
/*     */   }
/*     */   
/*     */   protected void handleAfterReasonPhrase(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
/* 319 */     boolean newLine = (state.leftOver == 10);
/* 320 */     while (buffer.hasRemaining()) {
/* 321 */       byte next = buffer.get();
/* 322 */       if (newLine) {
/* 323 */         if (next == 10) {
/* 324 */           state.state = 6;
/*     */           return;
/*     */         } 
/* 327 */         state.state = 4;
/* 328 */         state.leftOver = next;
/*     */         
/*     */         return;
/*     */       } 
/* 332 */       if (next == 10) {
/* 333 */         newLine = true; continue;
/* 334 */       }  if (next != 13 && next != 32 && next != 9) {
/* 335 */         state.state = 4;
/* 336 */         state.leftOver = next;
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 341 */     if (newLine) {
/* 342 */       state.leftOver = 10;
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
/*     */   protected static Map<String, HttpString> httpStrings() {
/* 354 */     Map<String, HttpString> results = new HashMap<>();
/* 355 */     Class[] classs = { Headers.class, Methods.class, Protocols.class };
/*     */     
/* 357 */     for (Class<?> c : classs) {
/* 358 */       for (Field field : c.getDeclaredFields()) {
/* 359 */         if (field.getType().equals(HttpString.class)) {
/* 360 */           field.setAccessible(true);
/* 361 */           HttpString result = null;
/*     */           try {
/* 363 */             result = (HttpString)field.get(null);
/* 364 */             results.put(result.toString(), result);
/* 365 */           } catch (IllegalAccessException e) {
/* 366 */             throw new RuntimeException(e);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 371 */     return results;
/*     */   }
/*     */   
/*     */   abstract void handleHttpVersion(ByteBuffer paramByteBuffer, ResponseParseState paramResponseParseState, HttpResponseBuilder paramHttpResponseBuilder) throws BadRequestException;
/*     */   
/*     */   abstract void handleHeader(ByteBuffer paramByteBuffer, ResponseParseState paramResponseParseState, HttpResponseBuilder paramHttpResponseBuilder) throws BadRequestException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\HttpResponseParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */