/*     */ package io.undertow.server.handlers.accesslog;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.Version;
/*     */ import io.undertow.attribute.AuthenticationTypeExchangeAttribute;
/*     */ import io.undertow.attribute.BytesSentAttribute;
/*     */ import io.undertow.attribute.CompositeExchangeAttribute;
/*     */ import io.undertow.attribute.ConstantExchangeAttribute;
/*     */ import io.undertow.attribute.CookieAttribute;
/*     */ import io.undertow.attribute.DateTimeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributeParser;
/*     */ import io.undertow.attribute.ExchangeAttributeWrapper;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.attribute.LocalIPAttribute;
/*     */ import io.undertow.attribute.QueryStringAttribute;
/*     */ import io.undertow.attribute.QuotingExchangeAttribute;
/*     */ import io.undertow.attribute.ReadOnlyAttributeException;
/*     */ import io.undertow.attribute.RemoteIPAttribute;
/*     */ import io.undertow.attribute.RemoteUserAttribute;
/*     */ import io.undertow.attribute.RequestHeaderAttribute;
/*     */ import io.undertow.attribute.RequestMethodAttribute;
/*     */ import io.undertow.attribute.RequestProtocolAttribute;
/*     */ import io.undertow.attribute.RequestSchemeAttribute;
/*     */ import io.undertow.attribute.RequestURLAttribute;
/*     */ import io.undertow.attribute.ResponseCodeAttribute;
/*     */ import io.undertow.attribute.ResponseHeaderAttribute;
/*     */ import io.undertow.attribute.ResponseTimeAttribute;
/*     */ import io.undertow.attribute.SecureExchangeAttribute;
/*     */ import io.undertow.attribute.SubstituteEmptyWrapper;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtendedAccessLogParser
/*     */ {
/*     */   private final ExchangeAttributeParser parser;
/*     */   
/*     */   public ExtendedAccessLogParser(ClassLoader classLoader) {
/*  75 */     this.parser = ExchangeAttributes.parser(classLoader, new ExchangeAttributeWrapper[] { QuotingExchangeAttribute.WRAPPER });
/*     */   }
/*     */   
/*     */   private static class PatternTokenizer {
/*  79 */     private StringReader sr = null;
/*  80 */     private StringBuilder buf = new StringBuilder();
/*     */     private boolean ended = false;
/*     */     private boolean subToken;
/*     */     private boolean parameter;
/*     */     
/*     */     PatternTokenizer(String str) {
/*  86 */       this.sr = new StringReader(str);
/*     */     }
/*     */     
/*     */     public boolean hasSubToken() {
/*  90 */       return this.subToken;
/*     */     }
/*     */     
/*     */     public boolean hasParameter() {
/*  94 */       return this.parameter;
/*     */     }
/*     */     
/*     */     public String getToken() throws IOException {
/*  98 */       if (this.ended) {
/*  99 */         return null;
/*     */       }
/* 101 */       String result = null;
/* 102 */       this.subToken = false;
/* 103 */       this.parameter = false;
/*     */       
/* 105 */       int c = this.sr.read();
/* 106 */       while (c != -1) {
/* 107 */         switch (c) {
/*     */           case 32:
/* 109 */             result = this.buf.toString();
/* 110 */             this.buf = new StringBuilder();
/* 111 */             this.buf.append((char)c);
/* 112 */             return result;
/*     */           case 45:
/* 114 */             result = this.buf.toString();
/* 115 */             this.buf = new StringBuilder();
/* 116 */             this.subToken = true;
/* 117 */             return result;
/*     */           case 40:
/* 119 */             result = this.buf.toString();
/* 120 */             this.buf = new StringBuilder();
/* 121 */             this.parameter = true;
/* 122 */             return result;
/*     */           case 41:
/* 124 */             result = this.buf.toString();
/* 125 */             this.buf = new StringBuilder();
/*     */             break;
/*     */           default:
/* 128 */             this.buf.append((char)c); break;
/*     */         } 
/* 130 */         c = this.sr.read();
/*     */       } 
/* 132 */       this.ended = true;
/* 133 */       if (this.buf.length() != 0) {
/* 134 */         return this.buf.toString();
/*     */       }
/* 136 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String getParameter() throws IOException {
/* 142 */       if (!this.parameter) {
/* 143 */         return null;
/*     */       }
/* 145 */       this.parameter = false;
/* 146 */       int c = this.sr.read();
/* 147 */       while (c != -1) {
/* 148 */         if (c == 41) {
/* 149 */           String result = this.buf.toString();
/* 150 */           this.buf = new StringBuilder();
/* 151 */           return result;
/*     */         } 
/* 153 */         this.buf.append((char)c);
/* 154 */         c = this.sr.read();
/*     */       } 
/* 156 */       return null;
/*     */     }
/*     */     
/*     */     public String getWhiteSpaces() throws IOException {
/* 160 */       if (isEnded())
/* 161 */         return ""; 
/* 162 */       StringBuilder whiteSpaces = new StringBuilder();
/* 163 */       if (this.buf.length() > 0) {
/* 164 */         whiteSpaces.append(this.buf);
/* 165 */         this.buf = new StringBuilder();
/*     */       } 
/* 167 */       int c = this.sr.read();
/* 168 */       while (Character.isWhitespace((char)c)) {
/* 169 */         whiteSpaces.append((char)c);
/* 170 */         c = this.sr.read();
/*     */       } 
/* 172 */       if (c == -1) {
/* 173 */         this.ended = true;
/*     */       } else {
/* 175 */         this.buf.append((char)c);
/*     */       } 
/* 177 */       return whiteSpaces.toString();
/*     */     }
/*     */     
/*     */     public boolean isEnded() {
/* 181 */       return this.ended;
/*     */     }
/*     */     
/*     */     public String getRemains() throws IOException {
/* 185 */       StringBuilder remains = new StringBuilder();
/* 186 */       for (int c = this.sr.read(); c != -1; c = this.sr.read()) {
/* 187 */         remains.append((char)c);
/*     */       }
/* 189 */       return remains.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ExchangeAttribute parse(String pattern) {
/* 195 */     List<ExchangeAttribute> list = new ArrayList<>();
/*     */     
/* 197 */     PatternTokenizer tokenizer = new PatternTokenizer(pattern);
/*     */ 
/*     */     
/*     */     try {
/* 201 */       tokenizer.getWhiteSpaces();
/*     */       
/* 203 */       if (tokenizer.isEnded()) {
/* 204 */         UndertowLogger.ROOT_LOGGER.extendedAccessLogEmptyPattern();
/* 205 */         return null;
/*     */       } 
/*     */       
/* 208 */       String token = tokenizer.getToken();
/* 209 */       while (token != null) {
/* 210 */         if (UndertowLogger.ROOT_LOGGER.isDebugEnabled()) {
/* 211 */           UndertowLogger.ROOT_LOGGER.debug("token = " + token);
/*     */         }
/* 213 */         ExchangeAttribute element = getLogElement(token, tokenizer);
/* 214 */         if (element == null) {
/*     */           break;
/*     */         }
/* 217 */         list.add(element);
/* 218 */         String whiteSpaces = tokenizer.getWhiteSpaces();
/* 219 */         if (whiteSpaces.length() > 0) {
/* 220 */           list.add(new ConstantExchangeAttribute(whiteSpaces));
/*     */         }
/* 222 */         if (tokenizer.isEnded()) {
/*     */           break;
/*     */         }
/* 225 */         token = tokenizer.getToken();
/*     */       } 
/* 227 */       if (UndertowLogger.ROOT_LOGGER.isDebugEnabled()) {
/* 228 */         UndertowLogger.ROOT_LOGGER.debug("finished decoding with element size of: " + list.size());
/*     */       }
/* 230 */       return (ExchangeAttribute)new CompositeExchangeAttribute(list.<ExchangeAttribute>toArray(new ExchangeAttribute[list.size()]));
/* 231 */     } catch (IOException e) {
/* 232 */       UndertowLogger.ROOT_LOGGER.extendedAccessLogPatternParseError(e);
/* 233 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected ExchangeAttribute getLogElement(String token, PatternTokenizer tokenizer) throws IOException {
/* 238 */     if ("date".equals(token))
/* 239 */       return (ExchangeAttribute)new DateTimeAttribute("yyyy-MM-dd", "GMT"); 
/* 240 */     if ("time".equals(token))
/* 241 */     { if (tokenizer.hasSubToken()) {
/* 242 */         String nextToken = tokenizer.getToken();
/* 243 */         if ("taken".equals(nextToken))
/*     */         {
/* 245 */           return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute((ExchangeAttribute)new ResponseTimeAttribute(TimeUnit.SECONDS), "-");
/*     */         }
/*     */       } else {
/* 248 */         return (ExchangeAttribute)new DateTimeAttribute("HH:mm:ss", "GMT");
/*     */       }  }
/* 250 */     else { if ("bytes".equals(token))
/* 251 */         return (ExchangeAttribute)new BytesSentAttribute(true); 
/* 252 */       if ("cached".equals(token))
/*     */       {
/* 254 */         return (ExchangeAttribute)new ConstantExchangeAttribute("-"); } 
/* 255 */       if ("c".equals(token)) {
/* 256 */         String nextToken = tokenizer.getToken();
/* 257 */         if ("ip".equals(nextToken))
/* 258 */           return RemoteIPAttribute.INSTANCE; 
/* 259 */         if ("dns".equals(nextToken)) {
/* 260 */           return new ExchangeAttribute()
/*     */             {
/*     */               public String readAttribute(HttpServerExchange exchange) {
/* 263 */                 InetSocketAddress peerAddress = (InetSocketAddress)exchange.getConnection().getPeerAddress(InetSocketAddress.class);
/*     */                 
/*     */                 try {
/* 266 */                   return peerAddress.getHostName();
/* 267 */                 } catch (Throwable e) {
/* 268 */                   return peerAddress.getHostString();
/*     */                 } 
/*     */               }
/*     */ 
/*     */               
/*     */               public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 274 */                 throw new ReadOnlyAttributeException();
/*     */               }
/*     */             };
/*     */         }
/* 278 */       } else if ("s".equals(token)) {
/* 279 */         String nextToken = tokenizer.getToken();
/* 280 */         if ("ip".equals(nextToken))
/* 281 */           return LocalIPAttribute.INSTANCE; 
/* 282 */         if ("dns".equals(nextToken)) {
/* 283 */           return new ExchangeAttribute()
/*     */             {
/*     */               public String readAttribute(HttpServerExchange exchange) {
/*     */                 try {
/* 287 */                   return ((InetSocketAddress)exchange.getConnection().getLocalAddress(InetSocketAddress.class)).getHostName();
/* 288 */                 } catch (Throwable e) {
/* 289 */                   return "localhost";
/*     */                 } 
/*     */               }
/*     */ 
/*     */               
/*     */               public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 295 */                 throw new ReadOnlyAttributeException();
/*     */               }
/*     */             };
/*     */         }
/*     */       } else {
/* 300 */         if ("cs".equals(token))
/* 301 */           return getClientToServerElement(tokenizer); 
/* 302 */         if ("sc".equals(token))
/* 303 */           return getServerToClientElement(tokenizer); 
/* 304 */         if ("sr".equals(token) || "rs".equals(token))
/* 305 */           return getProxyElement(tokenizer); 
/* 306 */         if ("x".equals(token))
/* 307 */           return getXParameterElement(tokenizer); 
/*     */       }  }
/* 309 */      UndertowLogger.ROOT_LOGGER.extendedAccessLogUnknownToken(token);
/* 310 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ExchangeAttribute getClientToServerElement(PatternTokenizer tokenizer) throws IOException {
/* 315 */     if (tokenizer.hasSubToken()) {
/* 316 */       String token = tokenizer.getToken();
/* 317 */       if ("method".equals(token))
/* 318 */         return RequestMethodAttribute.INSTANCE; 
/* 319 */       if ("uri".equals(token)) {
/* 320 */         if (tokenizer.hasSubToken()) {
/* 321 */           token = tokenizer.getToken();
/* 322 */           if ("stem".equals(token))
/* 323 */             return RequestURLAttribute.INSTANCE; 
/* 324 */           if ("query".equals(token)) {
/* 325 */             return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute(QueryStringAttribute.BARE_INSTANCE, "-");
/*     */           }
/*     */         } else {
/* 328 */           return new ExchangeAttribute()
/*     */             {
/*     */               public String readAttribute(HttpServerExchange exchange) {
/* 331 */                 String query = exchange.getQueryString();
/*     */                 
/* 333 */                 if (query.isEmpty()) {
/* 334 */                   return exchange.getRequestURI();
/*     */                 }
/* 336 */                 StringBuilder buf = new StringBuilder();
/* 337 */                 buf.append(exchange.getRequestURI());
/* 338 */                 buf.append('?');
/* 339 */                 buf.append(exchange.getQueryString());
/* 340 */                 return buf.toString();
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*     */               public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 346 */                 throw new ReadOnlyAttributeException();
/*     */               }
/*     */             };
/*     */         }
/*     */       
/*     */       }
/* 352 */     } else if (tokenizer.hasParameter()) {
/* 353 */       String parameter = tokenizer.getParameter();
/* 354 */       if (parameter == null) {
/* 355 */         UndertowLogger.ROOT_LOGGER.extendedAccessLogMissingClosing();
/* 356 */         return null;
/*     */       } 
/* 358 */       return (ExchangeAttribute)new QuotingExchangeAttribute((ExchangeAttribute)new RequestHeaderAttribute(new HttpString(parameter)));
/*     */     } 
/* 360 */     UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecode(tokenizer.getRemains());
/* 361 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ExchangeAttribute getServerToClientElement(PatternTokenizer tokenizer) throws IOException {
/* 366 */     if (tokenizer.hasSubToken()) {
/* 367 */       String token = tokenizer.getToken();
/* 368 */       if ("status".equals(token))
/* 369 */         return ResponseCodeAttribute.INSTANCE; 
/* 370 */       if ("comment".equals(token)) {
/* 371 */         return (ExchangeAttribute)new ConstantExchangeAttribute("?");
/*     */       }
/* 373 */     } else if (tokenizer.hasParameter()) {
/* 374 */       String parameter = tokenizer.getParameter();
/* 375 */       if (parameter == null) {
/* 376 */         UndertowLogger.ROOT_LOGGER.extendedAccessLogMissingClosing();
/* 377 */         return null;
/*     */       } 
/* 379 */       return (ExchangeAttribute)new QuotingExchangeAttribute((ExchangeAttribute)new ResponseHeaderAttribute(new HttpString(parameter)));
/*     */     } 
/* 381 */     UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecode(tokenizer.getRemains());
/* 382 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ExchangeAttribute getProxyElement(PatternTokenizer tokenizer) throws IOException {
/* 387 */     String token = null;
/* 388 */     if (tokenizer.hasSubToken()) {
/* 389 */       tokenizer.getToken();
/* 390 */       return (ExchangeAttribute)new ConstantExchangeAttribute("-");
/* 391 */     }  if (tokenizer.hasParameter()) {
/* 392 */       tokenizer.getParameter();
/* 393 */       return (ExchangeAttribute)new ConstantExchangeAttribute("-");
/*     */     } 
/* 395 */     UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecode(token);
/* 396 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ExchangeAttribute getXParameterElement(PatternTokenizer tokenizer) throws IOException {
/* 401 */     if (!tokenizer.hasSubToken()) {
/* 402 */       UndertowLogger.ROOT_LOGGER.extendedAccessLogBadXParam();
/* 403 */       return null;
/*     */     } 
/* 405 */     String token = tokenizer.getToken();
/* 406 */     if (!tokenizer.hasParameter()) {
/* 407 */       UndertowLogger.ROOT_LOGGER.extendedAccessLogBadXParam();
/* 408 */       return null;
/*     */     } 
/* 410 */     final String parameter = tokenizer.getParameter();
/* 411 */     if (parameter == null) {
/* 412 */       UndertowLogger.ROOT_LOGGER.extendedAccessLogMissingClosing();
/* 413 */       return null;
/*     */     } 
/* 415 */     if ("A".equals(token))
/* 416 */       return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute(this.parser.parse("%{sc," + parameter + "}"), "-"); 
/* 417 */     if ("C".equals(token))
/* 418 */       return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute((ExchangeAttribute)new CookieAttribute(parameter), "-"); 
/* 419 */     if ("R".equals(token))
/* 420 */       return this.parser.parse("%{r," + parameter + "}"); 
/* 421 */     if ("S".equals(token))
/* 422 */       return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute(this.parser.parse("%{s," + parameter + "}"), "-"); 
/* 423 */     if ("H".equals(token))
/* 424 */       return getServletRequestElement(parameter); 
/* 425 */     if ("P".equals(token))
/* 426 */       return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute(this.parser.parse("%{rp," + parameter + "}"), "-"); 
/* 427 */     if ("O".equals(token)) {
/* 428 */       return (ExchangeAttribute)new QuotingExchangeAttribute(new ExchangeAttribute()
/*     */           {
/*     */             public String readAttribute(HttpServerExchange exchange) {
/* 431 */               HeaderValues values = exchange.getResponseHeaders().get(parameter);
/* 432 */               if (values != null && values.size() > 0) {
/* 433 */                 StringBuilder buffer = new StringBuilder();
/* 434 */                 for (int i = 0; i < values.size(); i++) {
/* 435 */                   String string = values.get(i);
/* 436 */                   buffer.append(string);
/* 437 */                   if (i + 1 < values.size())
/* 438 */                     buffer.append(","); 
/*     */                 } 
/* 440 */                 return buffer.toString();
/*     */               } 
/* 442 */               return null;
/*     */             }
/*     */ 
/*     */             
/*     */             public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 447 */               throw new ReadOnlyAttributeException();
/*     */             }
/*     */           });
/*     */     }
/* 451 */     UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecodeXParamValue(token);
/* 452 */     return null;
/*     */   }
/*     */   
/*     */   protected ExchangeAttribute getServletRequestElement(String parameter) {
/* 456 */     if ("authType".equals(parameter))
/* 457 */       return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute(AuthenticationTypeExchangeAttribute.INSTANCE, "-"); 
/* 458 */     if ("remoteUser".equals(parameter))
/* 459 */       return (ExchangeAttribute)new SubstituteEmptyWrapper.SubstituteEmptyAttribute(RemoteUserAttribute.INSTANCE, "-"); 
/* 460 */     if ("requestedSessionId".equals(parameter))
/* 461 */       return this.parser.parse("%{REQUESTED_SESSION_ID}"); 
/* 462 */     if ("requestedSessionIdFromCookie".equals(parameter))
/* 463 */       return this.parser.parse("%{REQUESTED_SESSION_ID_FROM_COOKIE}"); 
/* 464 */     if ("requestedSessionIdValid".equals(parameter))
/* 465 */       return this.parser.parse("%{REQUESTED_SESSION_ID_VALID}"); 
/* 466 */     if ("contentLength".equals(parameter))
/* 467 */       return (ExchangeAttribute)new QuotingExchangeAttribute((ExchangeAttribute)new RequestHeaderAttribute(Headers.CONTENT_LENGTH)); 
/* 468 */     if ("characterEncoding".equals(parameter))
/* 469 */       return this.parser.parse("%{REQUEST_CHARACTER_ENCODING}"); 
/* 470 */     if ("locale".equals(parameter))
/* 471 */       return this.parser.parse("%{REQUEST_LOCALE}"); 
/* 472 */     if ("protocol".equals(parameter))
/* 473 */       return RequestProtocolAttribute.INSTANCE; 
/* 474 */     if ("scheme".equals(parameter))
/* 475 */       return RequestSchemeAttribute.INSTANCE; 
/* 476 */     if ("secure".equals(parameter)) {
/* 477 */       return SecureExchangeAttribute.INSTANCE;
/*     */     }
/* 479 */     UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecodeXParamValue(parameter);
/* 480 */     return null;
/*     */   }
/*     */   
/*     */   public static class ExtendedAccessLogHeaderGenerator
/*     */     implements LogFileHeaderGenerator {
/*     */     private final String pattern;
/*     */     
/*     */     public ExtendedAccessLogHeaderGenerator(String pattern) {
/* 488 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */     
/*     */     public String generateHeader() {
/* 493 */       StringBuilder sb = new StringBuilder();
/* 494 */       sb.append("#Fields: ");
/* 495 */       sb.append(this.pattern);
/* 496 */       sb.append(System.lineSeparator());
/* 497 */       sb.append("#Version: 2.0");
/* 498 */       sb.append(System.lineSeparator());
/* 499 */       sb.append("#Software: ");
/* 500 */       sb.append(Version.getFullVersionString());
/* 501 */       sb.append(System.lineSeparator());
/* 502 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\accesslog\ExtendedAccessLogParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */