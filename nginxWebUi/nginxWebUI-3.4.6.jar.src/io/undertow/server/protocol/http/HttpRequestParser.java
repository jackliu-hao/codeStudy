/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.BadRequestException;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.Protocols;
/*     */ import io.undertow.util.URLUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.HashMap;
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
/*     */ public abstract class HttpRequestParser
/*     */ {
/*     */   private static final byte[] HTTP;
/*     */   public static final int HTTP_LENGTH;
/*     */   private final int maxParameters;
/*     */   private final int maxHeaders;
/*     */   private final boolean allowEncodedSlash;
/*     */   private final boolean decode;
/*     */   private final String charset;
/*     */   private final int maxCachedHeaderSize;
/*     */   private final boolean allowUnescapedCharactersInUrl;
/*     */   private static final int START = 0;
/*     */   private static final int FIRST_COLON = 1;
/*     */   private static final int FIRST_SLASH = 2;
/*     */   private static final int SECOND_SLASH = 3;
/*     */   private static final int IN_PATH = 4;
/*     */   private static final int HOST_DONE = 5;
/*     */   private static final int NORMAL = 0;
/*     */   private static final int WHITESPACE = 1;
/*     */   private static final int BEGIN_LINE_END = 2;
/*     */   private static final int LINE_END = 3;
/* 171 */   private static final boolean[] ALLOWED_TARGET_CHARACTER = new boolean[256]; private static final int AWAIT_DATA_END = 4;
/*     */   
/*     */   static {
/*     */     try {
/* 175 */       HTTP = "HTTP/1.".getBytes("ASCII");
/* 176 */       HTTP_LENGTH = HTTP.length;
/* 177 */     } catch (UnsupportedEncodingException e) {
/* 178 */       throw new RuntimeException(e);
/*     */     } 
/* 180 */     for (int i = 0; i < 256; i++) {
/* 181 */       if (i < 32 || i > 126) {
/* 182 */         ALLOWED_TARGET_CHARACTER[i] = false;
/*     */       } else {
/* 184 */         switch ((char)i) {
/*     */           case '"':
/*     */           case '#':
/*     */           case '<':
/*     */           case '>':
/*     */           case '\\':
/*     */           case '^':
/*     */           case '`':
/*     */           case '{':
/*     */           case '|':
/*     */           case '}':
/* 195 */             ALLOWED_TARGET_CHARACTER[i] = false;
/*     */             break;
/*     */           default:
/* 198 */             ALLOWED_TARGET_CHARACTER[i] = true;
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public static boolean isTargetCharacterAllowed(char c) {
/* 205 */     return ALLOWED_TARGET_CHARACTER[c];
/*     */   }
/*     */   
/*     */   public HttpRequestParser(OptionMap options) {
/* 209 */     this.maxParameters = options.get(UndertowOptions.MAX_PARAMETERS, 1000);
/* 210 */     this.maxHeaders = options.get(UndertowOptions.MAX_HEADERS, 200);
/* 211 */     this.allowEncodedSlash = options.get(UndertowOptions.ALLOW_ENCODED_SLASH, false);
/* 212 */     this.decode = options.get(UndertowOptions.DECODE_URL, true);
/* 213 */     this.charset = (String)options.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name());
/* 214 */     this.maxCachedHeaderSize = options.get(UndertowOptions.MAX_CACHED_HEADER_SIZE, 150);
/* 215 */     this.allowUnescapedCharactersInUrl = options.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false);
/*     */   }
/*     */   
/*     */   public static final HttpRequestParser instance(OptionMap options) {
/*     */     try {
/* 220 */       Class<?> cls = Class.forName(HttpRequestParser.class.getName() + "$$generated", false, HttpRequestParser.class.getClassLoader());
/*     */       
/* 222 */       Constructor<?> ctor = cls.getConstructor(new Class[] { OptionMap.class });
/* 223 */       return (HttpRequestParser)ctor.newInstance(new Object[] { options });
/* 224 */     } catch (Exception e) {
/* 225 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handle(ByteBuffer buffer, ParseState currentState, HttpServerExchange builder) throws BadRequestException {
/* 231 */     if (currentState.state == 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 236 */       int position = buffer.position();
/* 237 */       if (buffer.remaining() > 3 && buffer
/* 238 */         .get(position) == 71 && buffer
/* 239 */         .get(position + 1) == 69 && buffer
/* 240 */         .get(position + 2) == 84 && buffer
/* 241 */         .get(position + 3) == 32) {
/* 242 */         buffer.position(position + 4);
/* 243 */         builder.setRequestMethod(Methods.GET);
/* 244 */         currentState.state = 1;
/*     */       } else {
/*     */         try {
/* 247 */           handleHttpVerb(buffer, currentState, builder);
/* 248 */         } catch (IllegalArgumentException e) {
/* 249 */           throw new BadRequestException(e);
/*     */         } 
/*     */       } 
/* 252 */       handlePath(buffer, currentState, builder);
/* 253 */       boolean failed = false;
/* 254 */       if (buffer.remaining() > HTTP_LENGTH + 3) {
/* 255 */         int pos = buffer.position();
/* 256 */         for (int i = 0; i < HTTP_LENGTH; i++) {
/* 257 */           if (HTTP[i] != buffer.get(pos + i)) {
/* 258 */             failed = true;
/*     */             break;
/*     */           } 
/*     */         } 
/* 262 */         if (!failed) {
/* 263 */           byte b = buffer.get(pos + HTTP_LENGTH);
/* 264 */           byte b2 = buffer.get(pos + HTTP_LENGTH + 1);
/* 265 */           byte b3 = buffer.get(pos + HTTP_LENGTH + 2);
/* 266 */           if (b2 == 13 && b3 == 10) {
/* 267 */             if (b == 49) {
/* 268 */               builder.setProtocol(Protocols.HTTP_1_1);
/* 269 */               buffer.position(pos + HTTP_LENGTH + 3);
/* 270 */               currentState.state = 6;
/* 271 */             } else if (b == 48) {
/* 272 */               builder.setProtocol(Protocols.HTTP_1_0);
/* 273 */               buffer.position(pos + HTTP_LENGTH + 3);
/* 274 */               currentState.state = 6;
/*     */             } else {
/* 276 */               failed = true;
/*     */             } 
/*     */           } else {
/* 279 */             failed = true;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 283 */         failed = true;
/*     */       } 
/* 285 */       if (failed) {
/* 286 */         handleHttpVersion(buffer, currentState, builder);
/* 287 */         handleAfterVersion(buffer, currentState);
/*     */       } 
/*     */       
/* 290 */       while (currentState.state != 8 && buffer.hasRemaining()) {
/* 291 */         handleHeader(buffer, currentState, builder);
/* 292 */         if (currentState.state == 7) {
/* 293 */           handleHeaderValue(buffer, currentState, builder);
/*     */         }
/*     */       } 
/*     */       return;
/*     */     } 
/* 298 */     handleStateful(buffer, currentState, builder);
/*     */   }
/*     */   
/*     */   private void handleStateful(ByteBuffer buffer, ParseState currentState, HttpServerExchange builder) throws BadRequestException {
/* 302 */     if (currentState.state == 1) {
/* 303 */       handlePath(buffer, currentState, builder);
/* 304 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 309 */     if (currentState.state == 3) {
/* 310 */       handleQueryParameters(buffer, currentState, builder);
/* 311 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 316 */     if (currentState.state == 2) {
/* 317 */       handlePathParameters(buffer, currentState, builder);
/* 318 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */       
/* 322 */       if (currentState.state == 1) {
/* 323 */         handlePath(buffer, currentState, builder);
/* 324 */         if (!buffer.hasRemaining()) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 330 */     if (currentState.state == 4) {
/* 331 */       handleHttpVersion(buffer, currentState, builder);
/* 332 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 336 */     if (currentState.state == 5) {
/* 337 */       handleAfterVersion(buffer, currentState);
/* 338 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 342 */     while (currentState.state != 8) {
/* 343 */       if (currentState.state == 6) {
/* 344 */         handleHeader(buffer, currentState, builder);
/* 345 */         if (!buffer.hasRemaining()) {
/*     */           return;
/*     */         }
/*     */       } 
/* 349 */       if (currentState.state == 7) {
/* 350 */         handleHeaderValue(buffer, currentState, builder);
/* 351 */         if (!buffer.hasRemaining()) {
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
/*     */   final void handlePath(ByteBuffer buffer, ParseState state, HttpServerExchange exchange) throws BadRequestException {
/* 385 */     StringBuilder stringBuilder = state.stringBuilder;
/* 386 */     int parseState = state.parseState;
/* 387 */     int canonicalPathStart = state.pos;
/* 388 */     boolean urlDecodeRequired = state.urlDecodeRequired;
/*     */     
/* 390 */     while (buffer.hasRemaining()) {
/* 391 */       char next = (char)(buffer.get() & 0xFF);
/* 392 */       if (!this.allowUnescapedCharactersInUrl && !ALLOWED_TARGET_CHARACTER[next]) {
/* 393 */         throw new BadRequestException(UndertowMessages.MESSAGES.invalidCharacterInRequestTarget(next));
/*     */       }
/* 395 */       if (next == ' ' || next == '\t') {
/* 396 */         if (stringBuilder.length() != 0) {
/* 397 */           String path = stringBuilder.toString();
/* 398 */           parsePathComplete(state, exchange, canonicalPathStart, parseState, urlDecodeRequired, path);
/* 399 */           exchange.setQueryString("");
/* 400 */           state.state = 4; return;
/*     */         }  continue;
/*     */       } 
/* 403 */       if (next == '\r' || next == '\n')
/* 404 */         throw UndertowMessages.MESSAGES.failedToParsePath(); 
/* 405 */       if (next == '?' && (parseState == 0 || parseState == 5 || parseState == 4)) {
/* 406 */         beginQueryParameters(buffer, state, exchange, stringBuilder, parseState, canonicalPathStart, urlDecodeRequired); return;
/*     */       } 
/* 408 */       if (next == ';') {
/* 409 */         state.parseState = parseState;
/* 410 */         state.urlDecodeRequired = urlDecodeRequired;
/* 411 */         state.pos = canonicalPathStart;
/*     */         
/* 413 */         state.canonicalPath.append(stringBuilder.substring(canonicalPathStart));
/*     */         
/* 415 */         handlePathParameters(buffer, state, exchange);
/*     */ 
/*     */ 
/*     */         
/* 419 */         if (state.state == 1) {
/* 420 */           canonicalPathStart = stringBuilder.length();
/* 421 */           stringBuilder.append('/');
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 428 */       if (this.decode && (next == '%' || next > '')) {
/* 429 */         urlDecodeRequired = true;
/* 430 */       } else if (next == ':' && parseState == 0) {
/* 431 */         parseState = 1;
/* 432 */       } else if (next == '/' && parseState == 1) {
/* 433 */         parseState = 2;
/* 434 */       } else if (next == '/' && parseState == 2) {
/* 435 */         parseState = 3;
/* 436 */       } else if (next == '/' && parseState == 3) {
/* 437 */         parseState = 5;
/* 438 */         canonicalPathStart = stringBuilder.length();
/* 439 */       } else if (parseState == 1 || parseState == 2) {
/* 440 */         parseState = 4;
/* 441 */       } else if (next == '/' && parseState != 5) {
/* 442 */         parseState = 4;
/*     */       } 
/* 444 */       stringBuilder.append(next);
/*     */     } 
/*     */ 
/*     */     
/* 448 */     state.parseState = parseState;
/* 449 */     state.pos = canonicalPathStart;
/* 450 */     state.urlDecodeRequired = urlDecodeRequired;
/*     */   }
/*     */   
/*     */   private void parsePathComplete(ParseState state, HttpServerExchange exchange, int canonicalPathStart, int parseState, boolean urlDecodeRequired, String path) {
/* 454 */     if (parseState == 3) {
/* 455 */       exchange.setRequestPath("/");
/* 456 */       exchange.setRelativePath("/");
/* 457 */       exchange.setRequestURI(path, true);
/* 458 */     } else if (parseState < 5 && state.canonicalPath.length() == 0) {
/* 459 */       String decodedPath = decode(path, urlDecodeRequired, state, this.allowEncodedSlash, false);
/* 460 */       exchange.setRequestPath(decodedPath);
/* 461 */       exchange.setRelativePath(decodedPath);
/* 462 */       exchange.setRequestURI(path, false);
/*     */     } else {
/* 464 */       handleFullUrl(state, exchange, canonicalPathStart, urlDecodeRequired, path, parseState);
/*     */     } 
/* 466 */     state.stringBuilder.setLength(0);
/* 467 */     state.canonicalPath.setLength(0);
/* 468 */     state.parseState = 0;
/* 469 */     state.pos = 0;
/* 470 */     state.urlDecodeRequired = false;
/*     */   }
/*     */   
/*     */   private void beginQueryParameters(ByteBuffer buffer, ParseState state, HttpServerExchange exchange, StringBuilder stringBuilder, int parseState, int canonicalPathStart, boolean urlDecodeRequired) throws BadRequestException {
/* 474 */     String path = stringBuilder.toString();
/* 475 */     parsePathComplete(state, exchange, canonicalPathStart, parseState, urlDecodeRequired, path);
/* 476 */     state.state = 3;
/* 477 */     handleQueryParameters(buffer, state, exchange);
/*     */   }
/*     */   
/*     */   private void handleFullUrl(ParseState state, HttpServerExchange exchange, int canonicalPathStart, boolean urlDecodeRequired, String path, int parseState) {
/* 481 */     state.canonicalPath.append(path.substring(canonicalPathStart));
/* 482 */     String thePath = decode(state.canonicalPath.toString(), urlDecodeRequired, state, this.allowEncodedSlash, false);
/* 483 */     exchange.setRequestPath(thePath);
/* 484 */     exchange.setRelativePath(thePath);
/* 485 */     exchange.setRequestURI(path, (parseState == 5));
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
/*     */   final void handleQueryParameters(ByteBuffer buffer, ParseState state, HttpServerExchange exchange) throws BadRequestException {
/* 499 */     StringBuilder stringBuilder = state.stringBuilder;
/* 500 */     int queryParamPos = state.pos;
/* 501 */     int mapCount = state.mapCount;
/* 502 */     boolean urlDecodeRequired = state.urlDecodeRequired;
/* 503 */     String nextQueryParam = state.nextQueryParam;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 512 */     while (buffer.hasRemaining()) {
/* 513 */       char next = (char)(buffer.get() & 0xFF);
/* 514 */       if (!this.allowUnescapedCharactersInUrl && !ALLOWED_TARGET_CHARACTER[next]) {
/* 515 */         throw new BadRequestException(UndertowMessages.MESSAGES.invalidCharacterInRequestTarget(next));
/*     */       }
/* 517 */       if (next == ' ' || next == '\t') {
/* 518 */         String queryString = stringBuilder.toString();
/* 519 */         exchange.setQueryString(queryString);
/* 520 */         if (nextQueryParam == null) {
/* 521 */           if (queryParamPos != stringBuilder.length()) {
/* 522 */             exchange.addQueryParam(decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true), "");
/*     */           }
/*     */         } else {
/* 525 */           exchange.addQueryParam(nextQueryParam, decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true));
/*     */         } 
/* 527 */         state.state = 4;
/* 528 */         state.stringBuilder.setLength(0);
/* 529 */         state.pos = 0;
/* 530 */         state.nextQueryParam = null;
/* 531 */         state.urlDecodeRequired = false;
/* 532 */         state.mapCount = 0; return;
/*     */       } 
/* 534 */       if (next == '\r' || next == '\n') {
/* 535 */         throw UndertowMessages.MESSAGES.failedToParsePath();
/*     */       }
/* 537 */       if (this.decode && (next == '+' || next == '%' || next > '')) {
/* 538 */         urlDecodeRequired = true;
/* 539 */       } else if (next == '=' && nextQueryParam == null) {
/* 540 */         nextQueryParam = decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true);
/* 541 */         urlDecodeRequired = false;
/* 542 */         queryParamPos = stringBuilder.length() + 1;
/* 543 */       } else if (next == '&' && nextQueryParam == null) {
/* 544 */         if (++mapCount >= this.maxParameters) {
/* 545 */           throw UndertowMessages.MESSAGES.tooManyQueryParameters(this.maxParameters);
/*     */         }
/* 547 */         if (queryParamPos != stringBuilder.length()) {
/* 548 */           exchange.addQueryParam(decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true), "");
/*     */         }
/* 550 */         urlDecodeRequired = false;
/* 551 */         queryParamPos = stringBuilder.length() + 1;
/* 552 */       } else if (next == '&') {
/* 553 */         if (++mapCount >= this.maxParameters) {
/* 554 */           throw UndertowMessages.MESSAGES.tooManyQueryParameters(this.maxParameters);
/*     */         }
/* 556 */         exchange.addQueryParam(nextQueryParam, decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true));
/* 557 */         urlDecodeRequired = false;
/* 558 */         queryParamPos = stringBuilder.length() + 1;
/* 559 */         nextQueryParam = null;
/*     */       } 
/* 561 */       stringBuilder.append(next);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 566 */     state.pos = queryParamPos;
/* 567 */     state.nextQueryParam = nextQueryParam;
/* 568 */     state.urlDecodeRequired = urlDecodeRequired;
/* 569 */     state.mapCount = mapCount;
/*     */   }
/*     */   
/*     */   private String decode(String value, boolean urlDecodeRequired, ParseState state, boolean allowEncodedSlash, boolean formEncoded) {
/* 573 */     if (urlDecodeRequired) {
/* 574 */       return URLUtils.decode(value, this.charset, allowEncodedSlash, formEncoded, state.decodeBuffer);
/*     */     }
/* 576 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void handlePathParameters(ByteBuffer buffer, ParseState state, HttpServerExchange exchange) throws BadRequestException {
/* 585 */     state.state = 2;
/* 586 */     boolean urlDecodeRequired = state.urlDecodeRequired;
/* 587 */     String param = state.nextQueryParam;
/* 588 */     StringBuilder stringBuilder = state.stringBuilder;
/* 589 */     stringBuilder.append(";");
/* 590 */     int pos = stringBuilder.length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 599 */     while (buffer.hasRemaining()) {
/* 600 */       char next = (char)(buffer.get() & 0xFF);
/* 601 */       if (!this.allowUnescapedCharactersInUrl && !ALLOWED_TARGET_CHARACTER[next]) {
/* 602 */         throw new BadRequestException(UndertowMessages.MESSAGES.invalidCharacterInRequestTarget(next));
/*     */       }
/*     */       
/* 605 */       if (next == ' ' || next == '\t' || next == '?') {
/* 606 */         handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
/* 607 */         String path = stringBuilder.toString();
/*     */         
/* 609 */         parsePathComplete(state, exchange, path.length(), state.parseState, urlDecodeRequired, path);
/* 610 */         state.state = 4;
/* 611 */         state.nextQueryParam = null;
/* 612 */         if (next == '?') {
/* 613 */           state.state = 3;
/* 614 */           handleQueryParameters(buffer, state, exchange);
/*     */         } else {
/* 616 */           exchange.setQueryString("");
/*     */         }  return;
/* 618 */       }  if (next == '\r' || next == '\n')
/* 619 */         throw UndertowMessages.MESSAGES.failedToParsePath(); 
/* 620 */       if (next == '/') {
/* 621 */         handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
/* 622 */         state.pos = stringBuilder.length();
/* 623 */         state.state = 1;
/* 624 */         state.nextQueryParam = null;
/*     */         return;
/*     */       } 
/* 627 */       if (this.decode && (next == '+' || next == '%' || next > '')) {
/* 628 */         urlDecodeRequired = true;
/*     */       }
/* 630 */       if (next == '=' && param == null) {
/* 631 */         param = decode(stringBuilder.substring(pos), urlDecodeRequired, state, true, true);
/* 632 */         urlDecodeRequired = false;
/* 633 */         pos = stringBuilder.length() + 1;
/* 634 */       } else if (next == ';') {
/* 635 */         handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
/* 636 */         param = null;
/* 637 */         pos = stringBuilder.length() + 1;
/* 638 */       } else if (next == ',') {
/* 639 */         if (param == null) {
/* 640 */           throw UndertowMessages.MESSAGES.failedToParsePath();
/*     */         }
/* 642 */         handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
/* 643 */         pos = stringBuilder.length() + 1;
/*     */       } 
/*     */       
/* 646 */       stringBuilder.append(next);
/*     */     } 
/*     */ 
/*     */     
/* 650 */     state.urlDecodeRequired = urlDecodeRequired;
/* 651 */     state.pos = pos;
/* 652 */     state.urlDecodeRequired = urlDecodeRequired;
/* 653 */     state.nextQueryParam = param;
/*     */   }
/*     */   
/*     */   private void handleParsedParam(String previouslyParsedParam, String parsedParam, HttpServerExchange exchange, boolean urlDecodeRequired, ParseState state) throws BadRequestException {
/* 657 */     if (previouslyParsedParam == null) {
/* 658 */       exchange.addPathParam(decode(parsedParam, urlDecodeRequired, state, true, true), "");
/*     */     } else {
/* 660 */       exchange.addPathParam(previouslyParsedParam, decode(parsedParam, urlDecodeRequired, state, true, true));
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
/*     */   final void handleHeaderValue(ByteBuffer buffer, ParseState state, HttpServerExchange builder) throws BadRequestException {
/* 683 */     HttpString headerName = state.nextHeader;
/* 684 */     StringBuilder stringBuilder = state.stringBuilder;
/* 685 */     CacheMap<HttpString, String> headerValuesCache = state.headerValuesCache;
/* 686 */     if (headerName != null && stringBuilder.length() == 0 && headerValuesCache != null) {
/* 687 */       String existing = headerValuesCache.get(headerName);
/* 688 */       if (existing != null && 
/* 689 */         handleCachedHeader(existing, buffer, state, builder)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 695 */     handleHeaderValueCacheMiss(buffer, state, builder, headerName, headerValuesCache, stringBuilder);
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleHeaderValueCacheMiss(ByteBuffer buffer, ParseState state, HttpServerExchange builder, HttpString headerName, CacheMap<HttpString, String> headerValuesCache, StringBuilder stringBuilder) throws BadRequestException {
/* 700 */     int parseState = state.parseState;
/* 701 */     while (buffer.hasRemaining() && parseState == 0) {
/* 702 */       byte next = buffer.get();
/* 703 */       if (next == 13) {
/* 704 */         parseState = 2; continue;
/* 705 */       }  if (next == 10) {
/* 706 */         parseState = 3; continue;
/* 707 */       }  if (next == 32 || next == 9) {
/* 708 */         parseState = 1; continue;
/*     */       } 
/* 710 */       stringBuilder.append((char)(next & 0xFF));
/*     */     } 
/*     */ 
/*     */     
/* 714 */     while (buffer.hasRemaining()) {
/* 715 */       String headerValue; byte next = buffer.get();
/* 716 */       switch (parseState) {
/*     */         case 0:
/* 718 */           if (next == 13) {
/* 719 */             parseState = 2; continue;
/* 720 */           }  if (next == 10) {
/* 721 */             parseState = 3; continue;
/* 722 */           }  if (next == 32 || next == 9) {
/* 723 */             parseState = 1; continue;
/*     */           } 
/* 725 */           stringBuilder.append((char)(next & 0xFF));
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/* 730 */           if (next == 13) {
/* 731 */             parseState = 2; continue;
/* 732 */           }  if (next == 10) {
/* 733 */             parseState = 3; continue;
/* 734 */           }  if (next == 32 || next == 9)
/*     */             continue; 
/* 736 */           if (stringBuilder.length() > 0) {
/* 737 */             stringBuilder.append(' ');
/*     */           }
/* 739 */           stringBuilder.append((char)(next & 0xFF));
/* 740 */           parseState = 0;
/*     */ 
/*     */ 
/*     */         
/*     */         case 2:
/*     */         case 3:
/* 746 */           if (next == 10 && parseState == 2) {
/* 747 */             parseState = 3; continue;
/* 748 */           }  if (next == 9 || next == 32) {
/*     */ 
/*     */             
/* 751 */             parseState = 1;
/*     */             continue;
/*     */           } 
/* 754 */           headerValue = stringBuilder.toString();
/*     */ 
/*     */           
/* 757 */           if (++state.mapCount > this.maxHeaders) {
/* 758 */             throw new BadRequestException(UndertowMessages.MESSAGES.tooManyHeaders(this.maxHeaders));
/*     */           }
/*     */           
/* 761 */           builder.getRequestHeaders().add(headerName, headerValue);
/* 762 */           if (headerValuesCache != null && headerName.length() + headerValue.length() < this.maxCachedHeaderSize) {
/* 763 */             headerValuesCache.put(headerName, headerValue);
/*     */           }
/*     */           
/* 766 */           state.nextHeader = null;
/*     */           
/* 768 */           state.leftOver = next;
/* 769 */           state.stringBuilder.setLength(0);
/* 770 */           if (next == 13) {
/* 771 */             parseState = 4; continue;
/* 772 */           }  if (next == 10) {
/* 773 */             state.state = 8;
/*     */             return;
/*     */           } 
/* 776 */           state.state = 6;
/* 777 */           state.parseState = 0;
/*     */           return;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 4:
/* 784 */           state.state = 8;
/*     */           return;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 790 */     state.parseState = parseState;
/*     */   }
/*     */   
/*     */   protected boolean handleCachedHeader(String existing, ByteBuffer buffer, ParseState state, HttpServerExchange builder) throws BadRequestException {
/* 794 */     int pos = buffer.position();
/* 795 */     while (pos < buffer.limit() && buffer.get(pos) == 32) {
/* 796 */       pos++;
/*     */     }
/* 798 */     if (existing.length() + 3 + pos > buffer.limit()) {
/* 799 */       return false;
/*     */     }
/* 801 */     int i = 0;
/* 802 */     while (i < existing.length()) {
/* 803 */       byte b = buffer.get(pos + i);
/* 804 */       if (b != existing.charAt(i)) {
/* 805 */         return false;
/*     */       }
/* 807 */       i++;
/*     */     } 
/* 809 */     if (buffer.get(pos + i++) != 13) {
/* 810 */       return false;
/*     */     }
/* 812 */     if (buffer.get(pos + i++) != 10) {
/* 813 */       return false;
/*     */     }
/* 815 */     int next = buffer.get(pos + i);
/* 816 */     if (next == 9 || next == 32)
/*     */     {
/* 818 */       return false;
/*     */     }
/* 820 */     buffer.position(pos + i);
/* 821 */     if (++state.mapCount > this.maxHeaders) {
/* 822 */       throw new BadRequestException(UndertowMessages.MESSAGES.tooManyHeaders(this.maxHeaders));
/*     */     }
/*     */     
/* 825 */     builder.getRequestHeaders().add(state.nextHeader, existing);
/*     */     
/* 827 */     state.nextHeader = null;
/*     */     
/* 829 */     state.state = 6;
/* 830 */     state.parseState = 0;
/* 831 */     return true;
/*     */   }
/*     */   
/*     */   protected void handleAfterVersion(ByteBuffer buffer, ParseState state) throws BadRequestException {
/* 835 */     boolean newLine = (state.leftOver == 10);
/* 836 */     while (buffer.hasRemaining()) {
/* 837 */       byte next = buffer.get();
/* 838 */       if (newLine) {
/* 839 */         if (next == 10) {
/* 840 */           state.state = 8;
/*     */           return;
/*     */         } 
/* 843 */         state.state = 6;
/* 844 */         state.leftOver = next;
/*     */         
/*     */         return;
/*     */       } 
/* 848 */       if (next == 10) {
/* 849 */         newLine = true; continue;
/* 850 */       }  if (next != 13 && next != 32 && next != 9) {
/* 851 */         state.state = 6;
/* 852 */         state.leftOver = next;
/*     */         return;
/*     */       } 
/* 855 */       throw UndertowMessages.MESSAGES.badRequest();
/*     */     } 
/*     */ 
/*     */     
/* 859 */     if (newLine) {
/* 860 */       state.leftOver = 10;
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
/*     */   protected static Map<String, HttpString> httpStrings() {
/* 873 */     Map<String, HttpString> results = new HashMap<>();
/* 874 */     Class[] classs = { Headers.class, Methods.class, Protocols.class };
/*     */     
/* 876 */     for (Class<?> c : classs) {
/* 877 */       for (Field field : c.getDeclaredFields()) {
/* 878 */         if (field.getType().equals(HttpString.class)) {
/* 879 */           HttpString result = null;
/*     */           try {
/* 881 */             result = (HttpString)field.get(null);
/* 882 */             results.put(result.toString(), result);
/* 883 */           } catch (IllegalAccessException e) {
/* 884 */             throw new RuntimeException(e);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 889 */     return results;
/*     */   }
/*     */   
/*     */   abstract void handleHttpVerb(ByteBuffer paramByteBuffer, ParseState paramParseState, HttpServerExchange paramHttpServerExchange) throws BadRequestException;
/*     */   
/*     */   abstract void handleHttpVersion(ByteBuffer paramByteBuffer, ParseState paramParseState, HttpServerExchange paramHttpServerExchange) throws BadRequestException;
/*     */   
/*     */   abstract void handleHeader(ByteBuffer paramByteBuffer, ParseState paramParseState, HttpServerExchange paramHttpServerExchange) throws BadRequestException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpRequestParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */