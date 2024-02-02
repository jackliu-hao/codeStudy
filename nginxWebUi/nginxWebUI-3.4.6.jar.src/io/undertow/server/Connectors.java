/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.LegacyCookieSupport;
/*     */ import io.undertow.util.ParameterLimitException;
/*     */ import io.undertow.util.URLUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
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
/*     */ public class Connectors
/*     */ {
/*  54 */   private static final boolean[] ALLOWED_TOKEN_CHARACTERS = new boolean[256];
/*  55 */   private static final boolean[] ALLOWED_SCHEME_CHARACTERS = new boolean[256];
/*     */   static {
/*     */     int i;
/*  58 */     for (i = 0; i < ALLOWED_TOKEN_CHARACTERS.length; i++) {
/*  59 */       if ((i >= 48 && i <= 57) || (i >= 97 && i <= 122) || (i >= 65 && i <= 90)) {
/*     */ 
/*     */         
/*  62 */         ALLOWED_TOKEN_CHARACTERS[i] = true;
/*     */       } else {
/*  64 */         switch (i) {
/*     */           case 33:
/*     */           case 35:
/*     */           case 36:
/*     */           case 37:
/*     */           case 38:
/*     */           case 39:
/*     */           case 42:
/*     */           case 43:
/*     */           case 45:
/*     */           case 46:
/*     */           case 94:
/*     */           case 95:
/*     */           case 96:
/*     */           case 124:
/*     */           case 126:
/*  80 */             ALLOWED_TOKEN_CHARACTERS[i] = true;
/*     */             break;
/*     */           
/*     */           default:
/*  84 */             ALLOWED_TOKEN_CHARACTERS[i] = false;
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/*  89 */     for (i = 0; i < ALLOWED_SCHEME_CHARACTERS.length; i++) {
/*  90 */       if ((i >= 48 && i <= 57) || (i >= 97 && i <= 122) || (i >= 65 && i <= 90)) {
/*     */ 
/*     */         
/*  93 */         ALLOWED_SCHEME_CHARACTERS[i] = true;
/*     */       } else {
/*  95 */         switch (i) {
/*     */           case 43:
/*     */           case 45:
/*     */           case 46:
/*  99 */             ALLOWED_SCHEME_CHARACTERS[i] = true;
/*     */             break;
/*     */           
/*     */           default:
/* 103 */             ALLOWED_SCHEME_CHARACTERS[i] = false;
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void flattenCookies(HttpServerExchange exchange) {
/* 115 */     boolean enableRfc6265Validation = exchange.getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false);
/* 116 */     for (Cookie cookie : exchange.responseCookies()) {
/* 117 */       exchange.getResponseHeaders().add(Headers.SET_COOKIE, getCookieString(cookie, enableRfc6265Validation));
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
/*     */   public static void addCookie(HttpServerExchange exchange, Cookie cookie) {
/* 129 */     boolean enableRfc6265Validation = exchange.getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false);
/* 130 */     exchange.getResponseHeaders().add(Headers.SET_COOKIE, getCookieString(cookie, enableRfc6265Validation));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void ungetRequestBytes(HttpServerExchange exchange, PooledByteBuffer... buffers) {
/* 140 */     PooledByteBuffer[] newArray, existing = (PooledByteBuffer[])exchange.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/*     */     
/* 142 */     if (existing == null) {
/* 143 */       newArray = new PooledByteBuffer[buffers.length];
/* 144 */       System.arraycopy(buffers, 0, newArray, 0, buffers.length);
/*     */     } else {
/* 146 */       newArray = new PooledByteBuffer[existing.length + buffers.length];
/* 147 */       System.arraycopy(existing, 0, newArray, 0, existing.length);
/* 148 */       System.arraycopy(buffers, 0, newArray, existing.length, buffers.length);
/*     */     } 
/* 150 */     exchange.putAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA, newArray);
/* 151 */     exchange.addExchangeCompleteListener(BufferedRequestDataCleanupListener.INSTANCE);
/*     */   }
/*     */   
/*     */   private enum BufferedRequestDataCleanupListener implements ExchangeCompletionListener {
/* 155 */     INSTANCE;
/*     */ 
/*     */     
/*     */     public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 159 */       PooledByteBuffer[] bufs = (PooledByteBuffer[])exchange.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 160 */       if (bufs != null) {
/* 161 */         for (PooledByteBuffer i : bufs) {
/* 162 */           if (i != null) {
/* 163 */             i.close();
/*     */           }
/*     */         } 
/*     */       }
/* 167 */       nextListener.proceed();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void terminateRequest(HttpServerExchange exchange) {
/* 172 */     exchange.terminateRequest();
/*     */   }
/*     */   
/*     */   public static void terminateResponse(HttpServerExchange exchange) {
/* 176 */     exchange.terminateResponse();
/*     */   }
/*     */   
/*     */   public static void resetRequestChannel(HttpServerExchange exchange) {
/* 180 */     exchange.resetRequestChannel();
/*     */   }
/*     */   
/*     */   private static String getCookieString(Cookie cookie, boolean enableRfc6265Validation) {
/* 184 */     if (enableRfc6265Validation) {
/* 185 */       return addRfc6265ResponseCookieToExchange(cookie);
/*     */     }
/* 187 */     switch (LegacyCookieSupport.adjustedCookieVersion(cookie)) {
/*     */       case 0:
/* 189 */         return addVersion0ResponseCookieToExchange(cookie);
/*     */     } 
/*     */     
/* 192 */     return addVersion1ResponseCookieToExchange(cookie);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setRequestStartTime(HttpServerExchange exchange) {
/* 198 */     exchange.setRequestStartTime(System.nanoTime());
/*     */   }
/*     */   
/*     */   public static void setRequestStartTime(HttpServerExchange existing, HttpServerExchange newExchange) {
/* 202 */     newExchange.setRequestStartTime(existing.getRequestStartTime());
/*     */   }
/*     */   
/*     */   private static String addRfc6265ResponseCookieToExchange(Cookie cookie) {
/* 206 */     StringBuilder header = new StringBuilder(cookie.getName());
/* 207 */     header.append("=");
/* 208 */     if (cookie.getValue() != null) {
/* 209 */       header.append(cookie.getValue());
/*     */     }
/* 211 */     if (cookie.getPath() != null) {
/* 212 */       header.append("; Path=");
/* 213 */       header.append(cookie.getPath());
/*     */     } 
/* 215 */     if (cookie.getDomain() != null) {
/* 216 */       header.append("; Domain=");
/* 217 */       header.append(cookie.getDomain());
/*     */     } 
/* 219 */     if (cookie.isDiscard()) {
/* 220 */       header.append("; Discard");
/*     */     }
/* 222 */     if (cookie.isSecure()) {
/* 223 */       header.append("; Secure");
/*     */     }
/* 225 */     if (cookie.isHttpOnly()) {
/* 226 */       header.append("; HttpOnly");
/*     */     }
/* 228 */     if (cookie.getMaxAge() != null) {
/* 229 */       if (cookie.getMaxAge().intValue() >= 0) {
/* 230 */         header.append("; Max-Age=");
/* 231 */         header.append(cookie.getMaxAge());
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 237 */       if (cookie.getExpires() == null) {
/* 238 */         if (cookie.getMaxAge().intValue() == 0) {
/* 239 */           Date expires = new Date();
/* 240 */           expires.setTime(0L);
/* 241 */           header.append("; Expires=");
/* 242 */           header.append(DateUtils.toOldCookieDateString(expires));
/* 243 */         } else if (cookie.getMaxAge().intValue() > 0) {
/* 244 */           Date expires = new Date();
/* 245 */           expires.setTime(expires.getTime() + cookie.getMaxAge().intValue() * 1000L);
/* 246 */           header.append("; Expires=");
/* 247 */           header.append(DateUtils.toOldCookieDateString(expires));
/*     */         } 
/*     */       }
/*     */     } 
/* 251 */     if (cookie.getExpires() != null) {
/* 252 */       header.append("; Expires=");
/* 253 */       header.append(DateUtils.toDateString(cookie.getExpires()));
/*     */     } 
/* 255 */     if (cookie.getComment() != null && !cookie.getComment().isEmpty()) {
/* 256 */       header.append("; Comment=");
/* 257 */       header.append(cookie.getComment());
/*     */     } 
/* 259 */     if (cookie.isSameSite() && 
/* 260 */       cookie.getSameSiteMode() != null && !cookie.getSameSiteMode().isEmpty()) {
/* 261 */       header.append("; SameSite=");
/* 262 */       header.append(cookie.getSameSiteMode());
/*     */     } 
/*     */     
/* 265 */     return header.toString();
/*     */   }
/*     */   
/*     */   private static String addVersion0ResponseCookieToExchange(Cookie cookie) {
/* 269 */     StringBuilder header = new StringBuilder(cookie.getName());
/* 270 */     header.append("=");
/* 271 */     if (cookie.getValue() != null) {
/* 272 */       LegacyCookieSupport.maybeQuote(header, cookie.getValue());
/*     */     }
/*     */     
/* 275 */     if (cookie.getPath() != null) {
/* 276 */       header.append("; path=");
/* 277 */       LegacyCookieSupport.maybeQuote(header, cookie.getPath());
/*     */     } 
/* 279 */     if (cookie.getDomain() != null) {
/* 280 */       header.append("; domain=");
/* 281 */       LegacyCookieSupport.maybeQuote(header, cookie.getDomain());
/*     */     } 
/* 283 */     if (cookie.isSecure()) {
/* 284 */       header.append("; secure");
/*     */     }
/* 286 */     if (cookie.isHttpOnly()) {
/* 287 */       header.append("; HttpOnly");
/*     */     }
/* 289 */     if (cookie.getExpires() != null) {
/* 290 */       header.append("; Expires=");
/* 291 */       header.append(DateUtils.toOldCookieDateString(cookie.getExpires()));
/* 292 */     } else if (cookie.getMaxAge() != null) {
/* 293 */       if (cookie.getMaxAge().intValue() >= 0) {
/* 294 */         header.append("; Max-Age=");
/* 295 */         header.append(cookie.getMaxAge());
/*     */       } 
/* 297 */       if (cookie.getMaxAge().intValue() == 0) {
/* 298 */         Date expires = new Date();
/* 299 */         expires.setTime(0L);
/* 300 */         header.append("; Expires=");
/* 301 */         header.append(DateUtils.toOldCookieDateString(expires));
/* 302 */       } else if (cookie.getMaxAge().intValue() > 0) {
/* 303 */         Date expires = new Date();
/* 304 */         expires.setTime(expires.getTime() + cookie.getMaxAge().intValue() * 1000L);
/* 305 */         header.append("; Expires=");
/* 306 */         header.append(DateUtils.toOldCookieDateString(expires));
/*     */       } 
/*     */     } 
/* 309 */     if (cookie.isSameSite() && 
/* 310 */       cookie.getSameSiteMode() != null && !cookie.getSameSiteMode().isEmpty()) {
/* 311 */       header.append("; SameSite=");
/* 312 */       header.append(cookie.getSameSiteMode());
/*     */     } 
/*     */     
/* 315 */     return header.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String addVersion1ResponseCookieToExchange(Cookie cookie) {
/* 321 */     StringBuilder header = new StringBuilder(cookie.getName());
/* 322 */     header.append("=");
/* 323 */     if (cookie.getValue() != null) {
/* 324 */       LegacyCookieSupport.maybeQuote(header, cookie.getValue());
/*     */     }
/* 326 */     header.append("; Version=1");
/* 327 */     if (cookie.getPath() != null) {
/* 328 */       header.append("; Path=");
/* 329 */       LegacyCookieSupport.maybeQuote(header, cookie.getPath());
/*     */     } 
/* 331 */     if (cookie.getDomain() != null) {
/* 332 */       header.append("; Domain=");
/* 333 */       LegacyCookieSupport.maybeQuote(header, cookie.getDomain());
/*     */     } 
/* 335 */     if (cookie.isDiscard()) {
/* 336 */       header.append("; Discard");
/*     */     }
/* 338 */     if (cookie.isSecure()) {
/* 339 */       header.append("; Secure");
/*     */     }
/* 341 */     if (cookie.isHttpOnly()) {
/* 342 */       header.append("; HttpOnly");
/*     */     }
/* 344 */     if (cookie.getMaxAge() != null) {
/* 345 */       if (cookie.getMaxAge().intValue() >= 0) {
/* 346 */         header.append("; Max-Age=");
/* 347 */         header.append(cookie.getMaxAge());
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 353 */       if (cookie.getExpires() == null) {
/* 354 */         if (cookie.getMaxAge().intValue() == 0) {
/* 355 */           Date expires = new Date();
/* 356 */           expires.setTime(0L);
/* 357 */           header.append("; Expires=");
/* 358 */           header.append(DateUtils.toOldCookieDateString(expires));
/* 359 */         } else if (cookie.getMaxAge().intValue() > 0) {
/* 360 */           Date expires = new Date();
/* 361 */           expires.setTime(expires.getTime() + cookie.getMaxAge().intValue() * 1000L);
/* 362 */           header.append("; Expires=");
/* 363 */           header.append(DateUtils.toOldCookieDateString(expires));
/*     */         } 
/*     */       }
/*     */     } 
/* 367 */     if (cookie.getExpires() != null) {
/* 368 */       header.append("; Expires=");
/* 369 */       header.append(DateUtils.toDateString(cookie.getExpires()));
/*     */     } 
/* 371 */     if (cookie.getComment() != null && !cookie.getComment().isEmpty()) {
/* 372 */       header.append("; Comment=");
/* 373 */       LegacyCookieSupport.maybeQuote(header, cookie.getComment());
/*     */     } 
/* 375 */     if (cookie.isSameSite() && 
/* 376 */       cookie.getSameSiteMode() != null && !cookie.getSameSiteMode().isEmpty()) {
/* 377 */       header.append("; SameSite=");
/* 378 */       header.append(cookie.getSameSiteMode());
/*     */     } 
/*     */     
/* 381 */     return header.toString();
/*     */   }
/*     */   
/*     */   public static void executeRootHandler(HttpHandler handler, HttpServerExchange exchange) {
/*     */     try {
/* 386 */       exchange.setInCall(true);
/* 387 */       handler.handleRequest(exchange);
/* 388 */       exchange.setInCall(false);
/* 389 */       boolean resumed = exchange.isResumed();
/* 390 */       if (exchange.isDispatched()) {
/* 391 */         if (resumed) {
/* 392 */           UndertowLogger.REQUEST_LOGGER.resumedAndDispatched();
/* 393 */           exchange.setStatusCode(500);
/* 394 */           exchange.endExchange();
/*     */           return;
/*     */         } 
/* 397 */         Runnable dispatchTask = exchange.getDispatchTask();
/* 398 */         Executor executor = exchange.getDispatchExecutor();
/* 399 */         exchange.setDispatchExecutor((Executor)null);
/* 400 */         exchange.unDispatch();
/* 401 */         if (dispatchTask != null) {
/* 402 */           executor = (executor == null) ? (Executor)exchange.getConnection().getWorker() : executor;
/*     */           try {
/* 404 */             executor.execute(dispatchTask);
/* 405 */           } catch (RejectedExecutionException e) {
/* 406 */             UndertowLogger.REQUEST_LOGGER.debug("Failed to dispatch to worker", e);
/* 407 */             exchange.setStatusCode(503);
/* 408 */             exchange.endExchange();
/*     */           } 
/*     */         } 
/* 411 */       } else if (!resumed) {
/* 412 */         exchange.endExchange();
/*     */       } else {
/* 414 */         exchange.runResumeReadWrite();
/*     */       } 
/* 416 */     } catch (Throwable t) {
/* 417 */       exchange.putAttachment(DefaultResponseListener.EXCEPTION, t);
/* 418 */       exchange.setInCall(false);
/* 419 */       if (!exchange.isResponseStarted()) {
/* 420 */         exchange.setStatusCode(500);
/*     */       }
/* 422 */       if (t instanceof IOException) {
/* 423 */         UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)t);
/*     */       } else {
/* 425 */         UndertowLogger.REQUEST_LOGGER.undertowRequestFailed(t, exchange);
/*     */       } 
/* 427 */       exchange.endExchange();
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
/*     */   @Deprecated
/*     */   public static void setExchangeRequestPath(HttpServerExchange exchange, String encodedPath, String charset, boolean decode, boolean allowEncodedSlash, StringBuilder decodeBuffer) {
/*     */     try {
/* 441 */       setExchangeRequestPath(exchange, encodedPath, charset, decode, allowEncodedSlash, decodeBuffer, exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 1000));
/* 442 */     } catch (ParameterLimitException e) {
/* 443 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setExchangeRequestPath(HttpServerExchange exchange, String encodedPath, String charset, boolean decode, boolean allowEncodedSlash, StringBuilder decodeBuffer, int maxParameters) throws ParameterLimitException {
/* 454 */     boolean requiresDecode = false;
/* 455 */     StringBuilder pathBuilder = new StringBuilder();
/* 456 */     int currentPathPartIndex = 0;
/* 457 */     for (int i = 0; i < encodedPath.length(); i++) {
/* 458 */       char c = encodedPath.charAt(i);
/* 459 */       if (c == '?') {
/*     */         
/* 461 */         String str2 = encodedPath.substring(currentPathPartIndex, i);
/* 462 */         if (requiresDecode) {
/* 463 */           str1 = URLUtils.decode(str2, charset, allowEncodedSlash, false, decodeBuffer);
/*     */         } else {
/* 465 */           str1 = str2;
/*     */         } 
/* 467 */         pathBuilder.append(str1);
/* 468 */         String str1 = pathBuilder.toString();
/* 469 */         exchange.setRequestPath(str1);
/* 470 */         exchange.setRelativePath(str1);
/* 471 */         exchange.setRequestURI(encodedPath.substring(0, i));
/* 472 */         String qs = encodedPath.substring(i + 1);
/* 473 */         exchange.setQueryString(qs);
/* 474 */         URLUtils.parseQueryString(qs, exchange, charset, decode, maxParameters); return;
/*     */       } 
/* 476 */       if (c == ';') {
/*     */         
/* 478 */         String str1, str2 = encodedPath.substring(currentPathPartIndex, i);
/* 479 */         if (requiresDecode) {
/* 480 */           str1 = URLUtils.decode(str2, charset, allowEncodedSlash, false, decodeBuffer);
/*     */         } else {
/* 482 */           str1 = str2;
/*     */         } 
/* 484 */         pathBuilder.append(str1);
/* 485 */         exchange.setRequestURI(encodedPath);
/* 486 */         currentPathPartIndex = i + 1 + URLUtils.parsePathParams(encodedPath.substring(i + 1), exchange, charset, decode, maxParameters);
/* 487 */         i = currentPathPartIndex - 1;
/* 488 */       } else if (c == '%' || c == '+') {
/* 489 */         requiresDecode = decode;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 494 */     String encodedPart = encodedPath.substring(currentPathPartIndex);
/* 495 */     if (requiresDecode) {
/* 496 */       part = URLUtils.decode(encodedPart, charset, allowEncodedSlash, false, decodeBuffer);
/*     */     } else {
/* 498 */       part = encodedPart;
/*     */     } 
/* 500 */     pathBuilder.append(part);
/* 501 */     String part = pathBuilder.toString();
/* 502 */     exchange.setRequestPath(part);
/* 503 */     exchange.setRelativePath(part);
/* 504 */     exchange.setRequestURI(encodedPath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StreamSourceChannel getExistingRequestChannel(HttpServerExchange exchange) {
/* 514 */     return exchange.requestChannel;
/*     */   }
/*     */   
/*     */   public static boolean isEntityBodyAllowed(HttpServerExchange exchange) {
/* 518 */     int code = exchange.getStatusCode();
/* 519 */     return isEntityBodyAllowed(code);
/*     */   }
/*     */   
/*     */   public static boolean isEntityBodyAllowed(int code) {
/* 523 */     if (code >= 100 && code < 200) {
/* 524 */       return false;
/*     */     }
/* 526 */     if (code == 204 || code == 304) {
/* 527 */       return false;
/*     */     }
/* 529 */     return true;
/*     */   }
/*     */   
/*     */   public static void updateResponseBytesSent(HttpServerExchange exchange, long bytes) {
/* 533 */     exchange.updateBytesSent(bytes);
/*     */   }
/*     */   
/*     */   public static ConduitStreamSinkChannel getConduitSinkChannel(HttpServerExchange exchange) {
/* 537 */     return exchange.getConnection().getSinkChannel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void verifyToken(HttpString header) {
/* 545 */     int length = header.length();
/* 546 */     for (int i = 0; i < length; i++) {
/* 547 */       byte c = header.byteAt(i);
/* 548 */       if (!ALLOWED_TOKEN_CHARACTERS[c]) {
/* 549 */         throw UndertowMessages.MESSAGES.invalidToken(c);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidTokenCharacter(byte c) {
/* 558 */     return ALLOWED_TOKEN_CHARACTERS[c];
/*     */   }
/*     */   
/*     */   public static boolean isValidSchemeCharacter(byte c) {
/* 562 */     return ALLOWED_SCHEME_CHARACTERS[c];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean areRequestHeadersValid(HeaderMap headers) {
/* 571 */     HeaderValues te = headers.get(Headers.TRANSFER_ENCODING);
/* 572 */     HeaderValues cl = headers.get(Headers.CONTENT_LENGTH);
/* 573 */     if (te != null && cl != null)
/* 574 */       return false; 
/* 575 */     if (te != null && te.size() > 1)
/* 576 */       return false; 
/* 577 */     if (cl != null && cl.size() > 1) {
/* 578 */       return false;
/*     */     }
/* 580 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\Connectors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */