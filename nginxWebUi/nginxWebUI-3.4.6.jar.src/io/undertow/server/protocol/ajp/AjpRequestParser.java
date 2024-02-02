/*     */ package io.undertow.server.protocol.ajp;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.impl.ExternalAuthenticationMechanism;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.BadRequestException;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.ParameterLimitException;
/*     */ import io.undertow.util.URLUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AjpRequestParser
/*     */ {
/*     */   private final String encoding;
/*     */   private final boolean doDecode;
/*     */   private final boolean allowEncodedSlash;
/*     */   private final int maxParameters;
/*     */   private final int maxHeaders;
/*     */   private StringBuilder decodeBuffer;
/*     */   private final boolean allowUnescapedCharactersInUrl;
/*     */   private final Pattern allowedRequestAttributesPattern;
/*     */   private static final HttpString[] HTTP_HEADERS;
/*     */   public static final int FORWARD_REQUEST = 2;
/*     */   public static final int CPONG = 9;
/*     */   public static final int CPING = 10;
/*     */   public static final int SHUTDOWN = 7;
/* 125 */   private static final HttpString[] HTTP_METHODS = new HttpString[28]; private static final String[] ATTRIBUTES; private static final Set<String> ATTR_SET; public static final String QUERY_STRING = "query_string"; public static final String SSL_CERT = "ssl_cert"; public static final String CONTEXT = "context"; public static final String SERVLET_PATH = "servlet_path"; public static final String REMOTE_USER = "remote_user"; public static final String AUTH_TYPE = "auth_type"; static {
/* 126 */     HTTP_METHODS[1] = Methods.OPTIONS;
/* 127 */     HTTP_METHODS[2] = Methods.GET;
/* 128 */     HTTP_METHODS[3] = Methods.HEAD;
/* 129 */     HTTP_METHODS[4] = Methods.POST;
/* 130 */     HTTP_METHODS[5] = Methods.PUT;
/* 131 */     HTTP_METHODS[6] = Methods.DELETE;
/* 132 */     HTTP_METHODS[7] = Methods.TRACE;
/* 133 */     HTTP_METHODS[8] = Methods.PROPFIND;
/* 134 */     HTTP_METHODS[9] = Methods.PROPPATCH;
/* 135 */     HTTP_METHODS[10] = Methods.MKCOL;
/* 136 */     HTTP_METHODS[11] = Methods.COPY;
/* 137 */     HTTP_METHODS[12] = Methods.MOVE;
/* 138 */     HTTP_METHODS[13] = Methods.LOCK;
/* 139 */     HTTP_METHODS[14] = Methods.UNLOCK;
/* 140 */     HTTP_METHODS[15] = Methods.ACL;
/* 141 */     HTTP_METHODS[16] = Methods.REPORT;
/* 142 */     HTTP_METHODS[17] = Methods.VERSION_CONTROL;
/* 143 */     HTTP_METHODS[18] = Methods.CHECKIN;
/* 144 */     HTTP_METHODS[19] = Methods.CHECKOUT;
/* 145 */     HTTP_METHODS[20] = Methods.UNCHECKOUT;
/* 146 */     HTTP_METHODS[21] = Methods.SEARCH;
/* 147 */     HTTP_METHODS[22] = Methods.MKWORKSPACE;
/* 148 */     HTTP_METHODS[23] = Methods.UPDATE;
/* 149 */     HTTP_METHODS[24] = Methods.LABEL;
/* 150 */     HTTP_METHODS[25] = Methods.MERGE;
/* 151 */     HTTP_METHODS[26] = Methods.BASELINE_CONTROL;
/* 152 */     HTTP_METHODS[27] = Methods.MKACTIVITY;
/*     */     
/* 154 */     HTTP_HEADERS = new HttpString[15];
/* 155 */     HTTP_HEADERS[1] = Headers.ACCEPT;
/* 156 */     HTTP_HEADERS[2] = Headers.ACCEPT_CHARSET;
/* 157 */     HTTP_HEADERS[3] = Headers.ACCEPT_ENCODING;
/* 158 */     HTTP_HEADERS[4] = Headers.ACCEPT_LANGUAGE;
/* 159 */     HTTP_HEADERS[5] = Headers.AUTHORIZATION;
/* 160 */     HTTP_HEADERS[6] = Headers.CONNECTION;
/* 161 */     HTTP_HEADERS[7] = Headers.CONTENT_TYPE;
/* 162 */     HTTP_HEADERS[8] = Headers.CONTENT_LENGTH;
/* 163 */     HTTP_HEADERS[9] = Headers.COOKIE;
/* 164 */     HTTP_HEADERS[10] = Headers.COOKIE2;
/* 165 */     HTTP_HEADERS[11] = Headers.HOST;
/* 166 */     HTTP_HEADERS[12] = Headers.PRAGMA;
/* 167 */     HTTP_HEADERS[13] = Headers.REFERER;
/* 168 */     HTTP_HEADERS[14] = Headers.USER_AGENT;
/*     */     
/* 170 */     ATTRIBUTES = new String[14];
/* 171 */     ATTRIBUTES[1] = "context";
/* 172 */     ATTRIBUTES[2] = "servlet_path";
/* 173 */     ATTRIBUTES[3] = "remote_user";
/* 174 */     ATTRIBUTES[4] = "auth_type";
/* 175 */     ATTRIBUTES[5] = "query_string";
/* 176 */     ATTRIBUTES[6] = "route";
/* 177 */     ATTRIBUTES[7] = "ssl_cert";
/* 178 */     ATTRIBUTES[8] = "ssl_cipher";
/* 179 */     ATTRIBUTES[9] = "ssl_session";
/* 180 */     ATTRIBUTES[10] = "req_attribute";
/* 181 */     ATTRIBUTES[11] = "ssl_key_size";
/* 182 */     ATTRIBUTES[12] = "secret";
/* 183 */     ATTRIBUTES[13] = "stored_method";
/* 184 */     ATTR_SET = new HashSet<>(Arrays.asList(ATTRIBUTES));
/*     */   }
/*     */   public static final String ROUTE = "route"; public static final String SSL_CIPHER = "ssl_cipher"; public static final String SSL_SESSION = "ssl_session"; public static final String REQ_ATTRIBUTE = "req_attribute"; public static final String SSL_KEY_SIZE = "ssl_key_size"; public static final String SECRET = "secret"; public static final String STORED_METHOD = "stored_method"; public static final String AJP_REMOTE_PORT = "AJP_REMOTE_PORT"; public static final int STRING_LENGTH_MASK = -2147483648;
/*     */   public AjpRequestParser(String encoding, boolean doDecode, int maxParameters, int maxHeaders, boolean allowEncodedSlash, boolean allowUnescapedCharactersInUrl) {
/* 188 */     this(encoding, doDecode, maxParameters, maxHeaders, allowEncodedSlash, allowUnescapedCharactersInUrl, null);
/*     */   }
/*     */   
/*     */   public AjpRequestParser(String encoding, boolean doDecode, int maxParameters, int maxHeaders, boolean allowEncodedSlash, boolean allowUnescapedCharactersInUrl, String allowedRequestAttributesPattern) {
/* 192 */     this.encoding = encoding;
/* 193 */     this.doDecode = doDecode;
/* 194 */     this.maxParameters = maxParameters;
/* 195 */     this.maxHeaders = maxHeaders;
/* 196 */     this.allowEncodedSlash = allowEncodedSlash;
/* 197 */     this.allowUnescapedCharactersInUrl = allowUnescapedCharactersInUrl;
/* 198 */     if (allowedRequestAttributesPattern != null && !allowedRequestAttributesPattern.isEmpty()) {
/* 199 */       this.allowedRequestAttributesPattern = Pattern.compile(allowedRequestAttributesPattern);
/*     */     } else {
/* 201 */       this.allowedRequestAttributesPattern = null;
/*     */     }  } public void parse(ByteBuffer buf, AjpRequestParseState state, HttpServerExchange exchange) throws IOException, BadRequestException { IntegerHolder integerHolder2; byte prefix; int method; StringHolder stringHolder;
/*     */     IntegerHolder integerHolder1;
/*     */     byte isSsl;
/*     */     IntegerHolder result;
/*     */     int readHeaders;
/* 207 */     if (!buf.hasRemaining()) {
/*     */       return;
/*     */     }
/* 210 */     switch (state.state) {
/*     */       case 0:
/* 212 */         integerHolder2 = parse16BitInteger(buf, state);
/* 213 */         if (!integerHolder2.readComplete) {
/*     */           return;
/*     */         }
/* 216 */         if (integerHolder2.value != 4660) {
/* 217 */           throw new BadRequestException(UndertowMessages.MESSAGES.wrongMagicNumber(integerHolder2.value));
/*     */         }
/*     */ 
/*     */       
/*     */       case 2:
/* 222 */         integerHolder2 = parse16BitInteger(buf, state);
/* 223 */         if (!integerHolder2.readComplete) {
/* 224 */           state.state = 2;
/*     */           return;
/*     */         } 
/* 227 */         state.dataSize = integerHolder2.value;
/*     */ 
/*     */       
/*     */       case 3:
/* 231 */         if (!buf.hasRemaining()) {
/* 232 */           state.state = 3;
/*     */           return;
/*     */         } 
/* 235 */         prefix = buf.get();
/* 236 */         state.prefix = prefix;
/* 237 */         if (prefix != 2) {
/* 238 */           state.state = 15;
/*     */           return;
/*     */         } 
/*     */ 
/*     */       
/*     */       case 4:
/* 244 */         if (!buf.hasRemaining()) {
/* 245 */           state.state = 4;
/*     */           return;
/*     */         } 
/* 248 */         method = buf.get();
/* 249 */         if (method > 0 && method < 28) {
/* 250 */           exchange.setRequestMethod(HTTP_METHODS[method]);
/* 251 */         } else if ((method & 0xFF) != 255) {
/* 252 */           throw new BadRequestException("Unknown method type " + method);
/*     */         } 
/*     */ 
/*     */       
/*     */       case 5:
/* 257 */         stringHolder = parseString(buf, state, StringType.OTHER);
/* 258 */         if (stringHolder.readComplete) {
/*     */           
/* 260 */           exchange.setProtocol(HttpString.tryFromString(stringHolder.value));
/*     */         } else {
/* 262 */           state.state = 5;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 6:
/* 267 */         stringHolder = parseString(buf, state, StringType.URL);
/* 268 */         if (stringHolder.readComplete) {
/* 269 */           int colon = stringHolder.value.indexOf(';');
/* 270 */           if (colon == -1) {
/* 271 */             String res = decode(stringHolder.value, stringHolder.containsUrlCharacters);
/* 272 */             if (stringHolder.containsUnencodedCharacters) {
/*     */ 
/*     */               
/* 275 */               exchange.setRequestURI(res);
/*     */             } else {
/* 277 */               exchange.setRequestURI(stringHolder.value);
/*     */             } 
/* 279 */             exchange.setRequestPath(res);
/* 280 */             exchange.setRelativePath(res);
/*     */           } else {
/* 282 */             StringBuffer resBuffer = new StringBuffer();
/* 283 */             int pathParamParsingIndex = 0;
/*     */             try {
/*     */               do {
/* 286 */                 String url = stringHolder.value.substring(pathParamParsingIndex, colon);
/* 287 */                 resBuffer.append(decode(url, stringHolder.containsUrlCharacters));
/* 288 */                 pathParamParsingIndex = colon + 1 + URLUtils.parsePathParams(stringHolder.value.substring(colon + 1), exchange, this.encoding, (this.doDecode && stringHolder.containsUrlCharacters), this.maxParameters);
/* 289 */                 colon = stringHolder.value.indexOf(';', pathParamParsingIndex + 1);
/* 290 */               } while (pathParamParsingIndex < stringHolder.value.length() && colon != -1);
/* 291 */             } catch (ParameterLimitException e) {
/* 292 */               UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest((Throwable)e);
/* 293 */               state.badRequest = true;
/*     */             } 
/* 295 */             if (pathParamParsingIndex < stringHolder.value.length()) {
/* 296 */               String url = stringHolder.value.substring(pathParamParsingIndex);
/* 297 */               resBuffer.append(decode(url, stringHolder.containsUrlCharacters));
/*     */             } 
/* 299 */             String res = resBuffer.toString();
/* 300 */             if (stringHolder.containsUnencodedCharacters) {
/* 301 */               exchange.setRequestURI(res);
/*     */             } else {
/* 303 */               exchange.setRequestURI(stringHolder.value);
/*     */             } 
/* 305 */             exchange.setRequestPath(res);
/* 306 */             exchange.setRelativePath(res);
/*     */           } 
/*     */         } else {
/* 309 */           state.state = 6;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 7:
/* 314 */         stringHolder = parseString(buf, state, StringType.OTHER);
/* 315 */         if (stringHolder.readComplete) {
/* 316 */           state.remoteAddress = stringHolder.value;
/*     */         } else {
/* 318 */           state.state = 7;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 8:
/* 323 */         stringHolder = parseString(buf, state, StringType.OTHER);
/* 324 */         if (!stringHolder.readComplete) {
/* 325 */           state.state = 8;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 9:
/* 330 */         stringHolder = parseString(buf, state, StringType.OTHER);
/* 331 */         if (stringHolder.readComplete) {
/* 332 */           state.serverAddress = stringHolder.value;
/*     */         } else {
/* 334 */           state.state = 9;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 10:
/* 339 */         integerHolder1 = parse16BitInteger(buf, state);
/* 340 */         if (integerHolder1.readComplete) {
/* 341 */           state.serverPort = integerHolder1.value;
/*     */         } else {
/* 343 */           state.state = 10;
/*     */           return;
/*     */         } 
/*     */       
/*     */       case 11:
/* 348 */         if (!buf.hasRemaining()) {
/* 349 */           state.state = 11;
/*     */           return;
/*     */         } 
/* 352 */         isSsl = buf.get();
/* 353 */         if (isSsl != 0) {
/* 354 */           exchange.setRequestScheme("https");
/*     */         } else {
/* 356 */           exchange.setRequestScheme("http");
/*     */         } 
/*     */ 
/*     */       
/*     */       case 12:
/* 361 */         result = parse16BitInteger(buf, state);
/* 362 */         if (!result.readComplete) {
/* 363 */           state.state = 12;
/*     */           return;
/*     */         } 
/* 366 */         state.numHeaders = result.value;
/* 367 */         if (state.numHeaders > this.maxHeaders) {
/* 368 */           UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest((Throwable)new BadRequestException(UndertowMessages.MESSAGES.tooManyHeaders(this.maxHeaders)));
/* 369 */           state.badRequest = true;
/*     */         } 
/*     */ 
/*     */       
/*     */       case 13:
/* 374 */         readHeaders = state.readHeaders;
/* 375 */         while (readHeaders < state.numHeaders) {
/* 376 */           if (state.currentHeader == null) {
/* 377 */             StringHolder stringHolder2 = parseString(buf, state, StringType.HEADER);
/* 378 */             if (!stringHolder2.readComplete) {
/* 379 */               state.state = 13;
/* 380 */               state.readHeaders = readHeaders;
/*     */               return;
/*     */             } 
/* 383 */             if (stringHolder2.header != null) {
/* 384 */               state.currentHeader = stringHolder2.header;
/*     */             } else {
/* 386 */               state.currentHeader = HttpString.tryFromString(stringHolder2.value);
/* 387 */               Connectors.verifyToken(state.currentHeader);
/*     */             } 
/*     */           } 
/* 390 */           StringHolder stringHolder1 = parseString(buf, state, StringType.OTHER);
/* 391 */           if (!stringHolder1.readComplete) {
/* 392 */             state.state = 13;
/* 393 */             state.readHeaders = readHeaders;
/*     */             return;
/*     */           } 
/* 396 */           if (!state.badRequest) {
/* 397 */             exchange.getRequestHeaders().add(state.currentHeader, stringHolder1.value);
/*     */           }
/* 399 */           state.currentHeader = null;
/* 400 */           readHeaders++;
/*     */         } 
/*     */       case 14:
/*     */         while (true) {
/*     */           String str;
/* 405 */           if (state.currentAttribute == null && state.currentIntegerPart == -1) {
/* 406 */             if (!buf.hasRemaining()) {
/* 407 */               state.state = 14;
/*     */               return;
/*     */             } 
/* 410 */             int val = 0xFF & buf.get();
/* 411 */             if (val == 255) {
/* 412 */               state.state = 15; return;
/*     */             } 
/* 414 */             if (val == 10) {
/*     */               
/* 416 */               state.currentIntegerPart = 1;
/*     */             } else {
/* 418 */               if (val == 0 || val >= ATTRIBUTES.length) {
/*     */                 continue;
/*     */               }
/*     */               
/* 422 */               state.currentAttribute = ATTRIBUTES[val];
/*     */             } 
/*     */           } 
/*     */           
/* 426 */           if (state.currentIntegerPart == 1) {
/* 427 */             StringHolder stringHolder1 = parseString(buf, state, StringType.OTHER);
/* 428 */             if (!stringHolder1.readComplete) {
/* 429 */               state.state = 14;
/*     */               return;
/*     */             } 
/* 432 */             state.currentAttribute = stringHolder1.value;
/* 433 */             state.currentIntegerPart = -1;
/*     */           } 
/*     */           
/* 436 */           boolean decodingAlreadyDone = false;
/* 437 */           if (state.currentAttribute.equals("ssl_key_size")) {
/* 438 */             IntegerHolder resultHolder = parse16BitInteger(buf, state);
/* 439 */             if (!resultHolder.readComplete) {
/* 440 */               state.state = 14;
/*     */               return;
/*     */             } 
/* 443 */             str = Integer.toString(resultHolder.value);
/*     */           } else {
/* 445 */             StringHolder resultHolder = parseString(buf, state, state.currentAttribute.equals("query_string") ? StringType.QUERY_STRING : StringType.OTHER);
/* 446 */             if (!resultHolder.readComplete) {
/* 447 */               state.state = 14;
/*     */               return;
/*     */             } 
/* 450 */             if (resultHolder.containsUnencodedCharacters) {
/* 451 */               str = decode(resultHolder.value, true);
/* 452 */               decodingAlreadyDone = true;
/*     */             } else {
/* 454 */               str = resultHolder.value;
/*     */             } 
/*     */           } 
/*     */           
/* 458 */           if (state.currentAttribute.equals("query_string")) {
/* 459 */             String resultAsQueryString = (str == null) ? "" : str;
/* 460 */             exchange.setQueryString(resultAsQueryString);
/*     */             try {
/* 462 */               URLUtils.parseQueryString(resultAsQueryString, exchange, this.encoding, (this.doDecode && !decodingAlreadyDone), this.maxParameters);
/* 463 */             } catch (ParameterLimitException|IllegalArgumentException e) {
/* 464 */               UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(e);
/* 465 */               state.badRequest = true;
/*     */             } 
/* 467 */           } else if (state.currentAttribute.equals("remote_user")) {
/* 468 */             exchange.putAttachment(ExternalAuthenticationMechanism.EXTERNAL_PRINCIPAL, str);
/* 469 */             exchange.putAttachment(HttpServerExchange.REMOTE_USER, str);
/* 470 */           } else if (state.currentAttribute.equals("auth_type")) {
/* 471 */             exchange.putAttachment(ExternalAuthenticationMechanism.EXTERNAL_AUTHENTICATION_TYPE, str);
/* 472 */           } else if (state.currentAttribute.equals("stored_method")) {
/* 473 */             HttpString requestMethod = new HttpString(str);
/* 474 */             Connectors.verifyToken(requestMethod);
/* 475 */             exchange.setRequestMethod(requestMethod);
/* 476 */           } else if (state.currentAttribute.equals("AJP_REMOTE_PORT")) {
/* 477 */             state.remotePort = Integer.parseInt(str);
/* 478 */           } else if (state.currentAttribute.equals("ssl_session")) {
/* 479 */             state.sslSessionId = str;
/* 480 */           } else if (state.currentAttribute.equals("ssl_cipher")) {
/* 481 */             state.sslCipher = str;
/* 482 */           } else if (state.currentAttribute.equals("ssl_cert")) {
/* 483 */             state.sslCert = str;
/* 484 */           } else if (state.currentAttribute.equals("ssl_key_size")) {
/* 485 */             state.sslKeySize = str;
/*     */           } else {
/*     */             
/* 488 */             if (state.attributes == null) {
/* 489 */               state.attributes = new TreeMap<>();
/*     */             }
/* 491 */             if (ATTR_SET.contains(state.currentAttribute)) {
/*     */               
/* 493 */               state.attributes.put(state.currentAttribute, str);
/* 494 */             } else if (this.allowedRequestAttributesPattern != null) {
/*     */               
/* 496 */               Matcher m = this.allowedRequestAttributesPattern.matcher(state.currentAttribute);
/* 497 */               if (m.matches()) {
/* 498 */                 state.attributes.put(state.currentAttribute, str);
/*     */               }
/*     */             } 
/*     */           } 
/* 502 */           state.currentAttribute = null;
/*     */         } 
/*     */     } 
/*     */     
/* 506 */     state.state = 15; }
/*     */ 
/*     */   
/*     */   private String decode(String url, boolean containsUrlCharacters) throws UnsupportedEncodingException {
/* 510 */     if (this.doDecode && containsUrlCharacters) {
/*     */       try {
/* 512 */         if (this.decodeBuffer == null) {
/* 513 */           this.decodeBuffer = new StringBuilder();
/*     */         }
/* 515 */         return URLUtils.decode(url, this.encoding, this.allowEncodedSlash, false, this.decodeBuffer);
/* 516 */       } catch (Exception e) {
/* 517 */         throw UndertowMessages.MESSAGES.failedToDecodeURL(url, this.encoding, e);
/*     */       } 
/*     */     }
/* 520 */     return url;
/*     */   }
/*     */   
/*     */   protected HttpString headers(int offset) {
/* 524 */     return HTTP_HEADERS[offset];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected IntegerHolder parse16BitInteger(ByteBuffer buf, AjpRequestParseState state) {
/* 530 */     if (!buf.hasRemaining()) {
/* 531 */       return new IntegerHolder(-1, false);
/*     */     }
/* 533 */     int number = state.currentIntegerPart;
/* 534 */     if (number == -1) {
/* 535 */       number = buf.get() & 0xFF;
/*     */     }
/* 537 */     if (buf.hasRemaining()) {
/* 538 */       byte b = buf.get();
/* 539 */       int result = ((0xFF & number) << 8) + (b & 0xFF);
/* 540 */       state.currentIntegerPart = -1;
/* 541 */       return new IntegerHolder(result, true);
/*     */     } 
/* 543 */     state.currentIntegerPart = number;
/* 544 */     return new IntegerHolder(-1, false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected StringHolder parseString(ByteBuffer buf, AjpRequestParseState state, StringType type) throws UnsupportedEncodingException, BadRequestException {
/* 549 */     boolean containsUrlCharacters = state.containsUrlCharacters;
/* 550 */     boolean containsUnencodedUrlCharacters = state.containsUnencodedUrlCharacters;
/* 551 */     if (!buf.hasRemaining()) {
/* 552 */       return new StringHolder(null, false, false, false);
/*     */     }
/* 554 */     int stringLength = state.stringLength;
/* 555 */     if (stringLength == -1) {
/* 556 */       int number = buf.get() & 0xFF;
/* 557 */       if (buf.hasRemaining()) {
/* 558 */         byte b = buf.get();
/* 559 */         stringLength = ((0xFF & number) << 8) + (b & 0xFF);
/*     */       } else {
/* 561 */         state.stringLength = number | Integer.MIN_VALUE;
/* 562 */         return new StringHolder(null, false, false, false);
/*     */       } 
/* 564 */     } else if ((stringLength & Integer.MIN_VALUE) != 0) {
/* 565 */       int number = stringLength & Integer.MAX_VALUE;
/* 566 */       stringLength = ((0xFF & number) << 8) + (buf.get() & 0xFF);
/*     */     } 
/* 568 */     if (type == StringType.HEADER && (stringLength & 0xFF00) != 0) {
/* 569 */       state.stringLength = -1;
/* 570 */       return new StringHolder(headers(stringLength & 0xFF));
/*     */     } 
/* 572 */     if (stringLength == 65535) {
/*     */       
/* 574 */       state.stringLength = -1;
/* 575 */       return new StringHolder(null, true, false, false);
/*     */     } 
/* 577 */     int length = state.getCurrentStringLength();
/* 578 */     while (length < stringLength) {
/* 579 */       if (!buf.hasRemaining()) {
/* 580 */         state.stringLength = stringLength;
/* 581 */         state.containsUrlCharacters = containsUrlCharacters;
/* 582 */         state.containsUnencodedUrlCharacters = containsUnencodedUrlCharacters;
/* 583 */         return new StringHolder(null, false, false, false);
/*     */       } 
/* 585 */       byte c = buf.get();
/* 586 */       if (type == StringType.QUERY_STRING && (c == 43 || c == 37 || c < 0)) {
/* 587 */         if (c < 0) {
/* 588 */           if (!this.allowUnescapedCharactersInUrl) {
/* 589 */             throw new BadRequestException();
/*     */           }
/* 591 */           containsUnencodedUrlCharacters = true;
/*     */         } 
/*     */         
/* 594 */         containsUrlCharacters = true;
/* 595 */       } else if (type == StringType.URL && (c == 37 || c < 0)) {
/* 596 */         if (c < 0) {
/* 597 */           if (!this.allowUnescapedCharactersInUrl) {
/* 598 */             throw new BadRequestException();
/*     */           }
/* 600 */           containsUnencodedUrlCharacters = true;
/*     */         } 
/*     */         
/* 603 */         containsUrlCharacters = true;
/*     */       } 
/* 605 */       state.addStringByte(c);
/* 606 */       length++;
/*     */     } 
/*     */     
/* 609 */     if (buf.hasRemaining()) {
/* 610 */       buf.get();
/* 611 */       String value = state.getStringAndClear();
/* 612 */       state.stringLength = -1;
/* 613 */       state.containsUrlCharacters = false;
/* 614 */       state.containsUnencodedUrlCharacters = containsUnencodedUrlCharacters;
/* 615 */       return new StringHolder(value, true, containsUrlCharacters, containsUnencodedUrlCharacters);
/*     */     } 
/* 617 */     state.stringLength = stringLength;
/* 618 */     state.containsUrlCharacters = containsUrlCharacters;
/* 619 */     state.containsUnencodedUrlCharacters = containsUnencodedUrlCharacters;
/* 620 */     return new StringHolder(null, false, false, false);
/*     */   }
/*     */   
/*     */   protected static class IntegerHolder
/*     */   {
/*     */     public final int value;
/*     */     public final boolean readComplete;
/*     */     
/*     */     private IntegerHolder(int value, boolean readComplete) {
/* 629 */       this.value = value;
/* 630 */       this.readComplete = readComplete;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static class StringHolder {
/*     */     public final String value;
/*     */     public final HttpString header;
/*     */     final boolean readComplete;
/*     */     final boolean containsUrlCharacters;
/*     */     final boolean containsUnencodedCharacters;
/*     */     
/*     */     private StringHolder(String value, boolean readComplete, boolean containsUrlCharacters, boolean containsUnencodedCharacters) {
/* 642 */       this.value = value;
/* 643 */       this.readComplete = readComplete;
/* 644 */       this.containsUrlCharacters = containsUrlCharacters;
/* 645 */       this.containsUnencodedCharacters = containsUnencodedCharacters;
/* 646 */       this.header = null;
/*     */     }
/*     */     
/*     */     private StringHolder(HttpString value) {
/* 650 */       this.value = null;
/* 651 */       this.readComplete = true;
/* 652 */       this.header = value;
/* 653 */       this.containsUrlCharacters = false;
/* 654 */       this.containsUnencodedCharacters = false;
/*     */     }
/*     */   }
/*     */   
/*     */   enum StringType {
/* 659 */     HEADER,
/* 660 */     URL,
/* 661 */     QUERY_STRING,
/* 662 */     OTHER;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpRequestParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */