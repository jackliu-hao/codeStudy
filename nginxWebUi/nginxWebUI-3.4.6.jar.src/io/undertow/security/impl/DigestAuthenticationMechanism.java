/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.api.NonceManager;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.DigestAlgorithm;
/*     */ import io.undertow.security.idm.DigestCredential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HexConverter;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DigestAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  65 */   public static final AuthenticationMechanismFactory FACTORY = new Factory();
/*     */   
/*     */   private static final String DEFAULT_NAME = "DIGEST";
/*  68 */   private static final String DIGEST_PREFIX = Headers.DIGEST + " ";
/*  69 */   private static final int PREFIX_LENGTH = DIGEST_PREFIX.length();
/*     */   
/*     */   private static final String OPAQUE_VALUE = "00000000000000000000000000000000";
/*     */   
/*     */   private static final byte COLON = 58;
/*     */   private final String mechanismName;
/*     */   private final IdentityManager identityManager;
/*     */   private static final Set<DigestAuthorizationToken> MANDATORY_REQUEST_TOKENS;
/*     */   
/*     */   static {
/*  79 */     Set<DigestAuthorizationToken> mandatoryTokens = EnumSet.noneOf(DigestAuthorizationToken.class);
/*  80 */     mandatoryTokens.add(DigestAuthorizationToken.USERNAME);
/*  81 */     mandatoryTokens.add(DigestAuthorizationToken.REALM);
/*  82 */     mandatoryTokens.add(DigestAuthorizationToken.NONCE);
/*  83 */     mandatoryTokens.add(DigestAuthorizationToken.DIGEST_URI);
/*  84 */     mandatoryTokens.add(DigestAuthorizationToken.RESPONSE);
/*     */     
/*  86 */     MANDATORY_REQUEST_TOKENS = Collections.unmodifiableSet(mandatoryTokens);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final List<DigestAlgorithm> supportedAlgorithms;
/*     */ 
/*     */   
/*     */   private final List<DigestQop> supportedQops;
/*     */ 
/*     */   
/*     */   private final String qopString;
/*     */   
/*     */   private final String realmName;
/*     */   
/*     */   private final String domain;
/*     */   
/*     */   private final NonceManager nonceManager;
/*     */ 
/*     */   
/*     */   public DigestAuthenticationMechanism(List<DigestAlgorithm> supportedAlgorithms, List<DigestQop> supportedQops, String realmName, String domain, NonceManager nonceManager) {
/* 107 */     this(supportedAlgorithms, supportedQops, realmName, domain, nonceManager, "DIGEST");
/*     */   }
/*     */ 
/*     */   
/*     */   public DigestAuthenticationMechanism(List<DigestAlgorithm> supportedAlgorithms, List<DigestQop> supportedQops, String realmName, String domain, NonceManager nonceManager, String mechanismName) {
/* 112 */     this(supportedAlgorithms, supportedQops, realmName, domain, nonceManager, mechanismName, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public DigestAuthenticationMechanism(List<DigestAlgorithm> supportedAlgorithms, List<DigestQop> supportedQops, String realmName, String domain, NonceManager nonceManager, String mechanismName, IdentityManager identityManager) {
/* 117 */     this.supportedAlgorithms = supportedAlgorithms;
/* 118 */     this.supportedQops = supportedQops;
/* 119 */     this.realmName = realmName;
/* 120 */     this.domain = domain;
/* 121 */     this.nonceManager = nonceManager;
/* 122 */     this.mechanismName = mechanismName;
/* 123 */     this.identityManager = identityManager;
/*     */     
/* 125 */     if (!supportedQops.isEmpty()) {
/* 126 */       StringBuilder sb = new StringBuilder();
/* 127 */       Iterator<DigestQop> it = supportedQops.iterator();
/* 128 */       sb.append(((DigestQop)it.next()).getToken());
/* 129 */       while (it.hasNext()) {
/* 130 */         sb.append(",").append(((DigestQop)it.next()).getToken());
/*     */       }
/* 132 */       this.qopString = sb.toString();
/*     */     } else {
/* 134 */       this.qopString = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public DigestAuthenticationMechanism(String realmName, String domain, String mechanismName) {
/* 139 */     this(realmName, domain, mechanismName, null);
/*     */   }
/*     */   
/*     */   public DigestAuthenticationMechanism(String realmName, String domain, String mechanismName, IdentityManager identityManager) {
/* 143 */     this(Collections.singletonList(DigestAlgorithm.MD5), Collections.singletonList(DigestQop.AUTH), realmName, domain, (NonceManager)new SimpleNonceManager(), "DIGEST", identityManager);
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/* 148 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/* 153 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
/* 154 */     if (headerValues != null) {
/* 155 */       Iterator<String> iterator = headerValues.iterator(); if (iterator.hasNext()) { String current = iterator.next();
/* 156 */         if (current.startsWith(DIGEST_PREFIX)) {
/* 157 */           String digestChallenge = current.substring(PREFIX_LENGTH);
/*     */           
/*     */           try {
/* 160 */             DigestContext context = new DigestContext();
/* 161 */             Map<DigestAuthorizationToken, String> parsedHeader = DigestAuthorizationToken.parseHeader(digestChallenge);
/* 162 */             context.setMethod(exchange.getRequestMethod().toString());
/* 163 */             context.setParsedHeader(parsedHeader);
/*     */             
/* 165 */             exchange.putAttachment(DigestContext.ATTACHMENT_KEY, context);
/*     */             
/* 167 */             UndertowLogger.SECURITY_LOGGER.debugf("Found digest header %s in %s", current, exchange);
/*     */             
/* 169 */             return handleDigestHeader(exchange, securityContext);
/* 170 */           } catch (Exception e) {
/* 171 */             UndertowLogger.SECURITY_LOGGER.authenticationFailedFor(current, exchange, e);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 177 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED; }
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 182 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */   }
/*     */   private AuthenticationMechanism.AuthenticationMechanismOutcome handleDigestHeader(HttpServerExchange exchange, SecurityContext securityContext) {
/*     */     DigestAlgorithm algorithm;
/* 186 */     DigestContext context = (DigestContext)exchange.getAttachment(DigestContext.ATTACHMENT_KEY);
/* 187 */     Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
/*     */     
/* 189 */     Set<DigestAuthorizationToken> mandatoryTokens = EnumSet.copyOf(MANDATORY_REQUEST_TOKENS);
/* 190 */     if (!this.supportedAlgorithms.contains(DigestAlgorithm.MD5))
/*     */     {
/* 192 */       mandatoryTokens.add(DigestAuthorizationToken.ALGORITHM);
/*     */     }
/* 194 */     if (!this.supportedQops.isEmpty() && !this.supportedQops.contains(DigestQop.AUTH))
/*     */     {
/* 196 */       mandatoryTokens.add(DigestAuthorizationToken.MESSAGE_QOP);
/*     */     }
/*     */     
/* 199 */     DigestQop qop = null;
/*     */     
/* 201 */     if (parsedHeader.containsKey(DigestAuthorizationToken.MESSAGE_QOP)) {
/* 202 */       qop = DigestQop.forName(parsedHeader.get(DigestAuthorizationToken.MESSAGE_QOP));
/* 203 */       if (qop == null || !this.supportedQops.contains(qop)) {
/*     */         
/* 205 */         UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.MESSAGE_QOP.getName(), parsedHeader
/* 206 */             .get(DigestAuthorizationToken.MESSAGE_QOP));
/*     */         
/* 208 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */       } 
/* 210 */       context.setQop(qop);
/* 211 */       mandatoryTokens.add(DigestAuthorizationToken.CNONCE);
/* 212 */       mandatoryTokens.add(DigestAuthorizationToken.NONCE_COUNT);
/*     */     } 
/*     */ 
/*     */     
/* 216 */     mandatoryTokens.removeAll(parsedHeader.keySet());
/* 217 */     if (mandatoryTokens.size() > 0) {
/* 218 */       for (DigestAuthorizationToken currentToken : mandatoryTokens)
/*     */       {
/*     */         
/* 221 */         UndertowLogger.REQUEST_LOGGER.missingAuthorizationToken(currentToken.getName());
/*     */       }
/*     */       
/* 224 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */ 
/*     */     
/* 228 */     if (!this.realmName.equals(parsedHeader.get(DigestAuthorizationToken.REALM))) {
/* 229 */       UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.REALM.getName(), parsedHeader
/* 230 */           .get(DigestAuthorizationToken.REALM));
/*     */       
/* 232 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */     
/* 235 */     if (parsedHeader.containsKey(DigestAuthorizationToken.DIGEST_URI)) {
/* 236 */       String uri = parsedHeader.get(DigestAuthorizationToken.DIGEST_URI);
/* 237 */       String requestURI = exchange.getRequestURI();
/* 238 */       if (!exchange.getQueryString().isEmpty()) {
/* 239 */         requestURI = requestURI + "?" + exchange.getQueryString();
/*     */       }
/* 241 */       if (!uri.equals(requestURI)) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 246 */         requestURI = exchange.getRequestURL();
/* 247 */         if (!exchange.getQueryString().isEmpty()) {
/* 248 */           requestURI = requestURI + "?" + exchange.getQueryString();
/*     */         }
/* 250 */         if (!uri.equals(requestURI)) {
/*     */           
/* 252 */           exchange.setStatusCode(400);
/* 253 */           exchange.endExchange();
/* 254 */           return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 258 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */     
/* 261 */     if (parsedHeader.containsKey(DigestAuthorizationToken.OPAQUE) && 
/* 262 */       !"00000000000000000000000000000000".equals(parsedHeader.get(DigestAuthorizationToken.OPAQUE))) {
/* 263 */       UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.OPAQUE.getName(), parsedHeader
/* 264 */           .get(DigestAuthorizationToken.OPAQUE));
/* 265 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 270 */     if (parsedHeader.containsKey(DigestAuthorizationToken.ALGORITHM)) {
/* 271 */       algorithm = DigestAlgorithm.forName(parsedHeader.get(DigestAuthorizationToken.ALGORITHM));
/* 272 */       if (algorithm == null || !this.supportedAlgorithms.contains(algorithm))
/*     */       {
/* 274 */         UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.ALGORITHM.getName(), parsedHeader
/* 275 */             .get(DigestAuthorizationToken.ALGORITHM));
/*     */         
/* 277 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 282 */       algorithm = DigestAlgorithm.MD5;
/*     */     } 
/*     */     
/*     */     try {
/* 286 */       context.setAlgorithm(algorithm);
/* 287 */     } catch (NoSuchAlgorithmException e) {
/*     */ 
/*     */ 
/*     */       
/* 291 */       UndertowLogger.REQUEST_LOGGER.exceptionProcessingRequest(e);
/* 292 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */     
/* 295 */     String userName = parsedHeader.get(DigestAuthorizationToken.USERNAME);
/* 296 */     IdentityManager identityManager = getIdentityManager(securityContext);
/*     */ 
/*     */     
/* 299 */     if (algorithm.isSession())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 306 */       throw new IllegalStateException("Not yet implemented.");
/*     */     }
/* 308 */     DigestCredential credential = new DigestCredentialImpl(context);
/* 309 */     Account account = identityManager.verify(userName, (Credential)credential);
/*     */ 
/*     */     
/* 312 */     if (account == null) {
/*     */ 
/*     */       
/* 315 */       securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(userName), this.mechanismName);
/* 316 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */ 
/*     */     
/* 320 */     if (!validateNonceUse(context, parsedHeader, exchange)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 325 */       context.markStale();
/*     */ 
/*     */       
/* 328 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 333 */     sendAuthenticationInfoHeader(exchange);
/* 334 */     securityContext.authenticationComplete(account, this.mechanismName, false);
/* 335 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean validateRequest(DigestContext context, byte[] ha1) {
/*     */     byte[] ha2, requestDigest;
/* 344 */     DigestQop qop = context.getQop();
/*     */     
/* 346 */     if (qop == null || qop.equals(DigestQop.AUTH)) {
/* 347 */       ha2 = createHA2Auth(context, context.getParsedHeader());
/*     */     } else {
/* 349 */       ha2 = createHA2AuthInt();
/*     */     } 
/*     */ 
/*     */     
/* 353 */     if (qop == null) {
/* 354 */       requestDigest = createRFC2069RequestDigest(ha1, ha2, context);
/*     */     } else {
/* 356 */       requestDigest = createRFC2617RequestDigest(ha1, ha2, context);
/*     */     } 
/*     */     
/* 359 */     byte[] providedResponse = ((String)context.getParsedHeader().get(DigestAuthorizationToken.RESPONSE)).getBytes(StandardCharsets.UTF_8);
/*     */     
/* 361 */     return MessageDigest.isEqual(requestDigest, providedResponse);
/*     */   }
/*     */   
/*     */   private boolean validateNonceUse(DigestContext context, Map<DigestAuthorizationToken, String> parsedHeader, HttpServerExchange exchange) {
/* 365 */     String suppliedNonce = parsedHeader.get(DigestAuthorizationToken.NONCE);
/* 366 */     int nonceCount = -1;
/* 367 */     if (parsedHeader.containsKey(DigestAuthorizationToken.NONCE_COUNT)) {
/* 368 */       String nonceCountHex = parsedHeader.get(DigestAuthorizationToken.NONCE_COUNT);
/*     */       
/* 370 */       nonceCount = Integer.parseInt(nonceCountHex, 16);
/*     */     } 
/*     */     
/* 373 */     context.setNonce(suppliedNonce);
/*     */     
/* 375 */     return this.nonceManager.validateNonce(suppliedNonce, nonceCount, exchange);
/*     */   }
/*     */   
/*     */   private byte[] createHA2Auth(DigestContext context, Map<DigestAuthorizationToken, String> parsedHeader) {
/* 379 */     byte[] method = context.getMethod().getBytes(StandardCharsets.UTF_8);
/* 380 */     byte[] digestUri = ((String)parsedHeader.get(DigestAuthorizationToken.DIGEST_URI)).getBytes(StandardCharsets.UTF_8);
/*     */     
/* 382 */     MessageDigest digest = context.getDigest();
/*     */     try {
/* 384 */       digest.update(method);
/* 385 */       digest.update((byte)58);
/* 386 */       digest.update(digestUri);
/*     */       
/* 388 */       return HexConverter.convertToHexBytes(digest.digest());
/*     */     } finally {
/* 390 */       digest.reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private byte[] createHA2AuthInt() {
/* 396 */     throw new IllegalStateException("Method not implemented.");
/*     */   }
/*     */   
/*     */   private byte[] createRFC2069RequestDigest(byte[] ha1, byte[] ha2, DigestContext context) {
/* 400 */     MessageDigest digest = context.getDigest();
/* 401 */     Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
/*     */     
/* 403 */     byte[] nonce = ((String)parsedHeader.get(DigestAuthorizationToken.NONCE)).getBytes(StandardCharsets.UTF_8);
/*     */     
/*     */     try {
/* 406 */       digest.update(ha1);
/* 407 */       digest.update((byte)58);
/* 408 */       digest.update(nonce);
/* 409 */       digest.update((byte)58);
/* 410 */       digest.update(ha2);
/*     */       
/* 412 */       return HexConverter.convertToHexBytes(digest.digest());
/*     */     } finally {
/* 414 */       digest.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   private byte[] createRFC2617RequestDigest(byte[] ha1, byte[] ha2, DigestContext context) {
/* 419 */     MessageDigest digest = context.getDigest();
/* 420 */     Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
/*     */     
/* 422 */     byte[] nonce = ((String)parsedHeader.get(DigestAuthorizationToken.NONCE)).getBytes(StandardCharsets.UTF_8);
/* 423 */     byte[] nonceCount = ((String)parsedHeader.get(DigestAuthorizationToken.NONCE_COUNT)).getBytes(StandardCharsets.UTF_8);
/* 424 */     byte[] cnonce = ((String)parsedHeader.get(DigestAuthorizationToken.CNONCE)).getBytes(StandardCharsets.UTF_8);
/* 425 */     byte[] qop = ((String)parsedHeader.get(DigestAuthorizationToken.MESSAGE_QOP)).getBytes(StandardCharsets.UTF_8);
/*     */     
/*     */     try {
/* 428 */       digest.update(ha1);
/* 429 */       digest.update((byte)58);
/* 430 */       digest.update(nonce);
/* 431 */       digest.update((byte)58);
/* 432 */       digest.update(nonceCount);
/* 433 */       digest.update((byte)58);
/* 434 */       digest.update(cnonce);
/* 435 */       digest.update((byte)58);
/* 436 */       digest.update(qop);
/* 437 */       digest.update((byte)58);
/* 438 */       digest.update(ha2);
/*     */       
/* 440 */       return HexConverter.convertToHexBytes(digest.digest());
/*     */     } finally {
/* 442 */       digest.reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 448 */     DigestContext context = (DigestContext)exchange.getAttachment(DigestContext.ATTACHMENT_KEY);
/* 449 */     boolean stale = (context == null) ? false : context.isStale();
/*     */     
/* 451 */     StringBuilder rb = new StringBuilder(DIGEST_PREFIX);
/* 452 */     rb.append(Headers.REALM.toString()).append("=\"").append(this.realmName).append("\",");
/* 453 */     rb.append(Headers.DOMAIN.toString()).append("=\"").append(this.domain).append("\",");
/*     */     
/* 455 */     rb.append(Headers.NONCE.toString()).append("=\"").append(this.nonceManager.nextNonce(null, exchange)).append("\",");
/*     */ 
/*     */     
/* 458 */     rb.append(Headers.OPAQUE.toString()).append("=\"00000000000000000000000000000000\"");
/* 459 */     if (stale) {
/* 460 */       rb.append(",stale=true");
/*     */     }
/* 462 */     if (this.supportedAlgorithms.size() > 0)
/*     */     {
/* 464 */       rb.append(",").append(Headers.ALGORITHM.toString()).append("=%s");
/*     */     }
/* 466 */     if (this.qopString != null) {
/* 467 */       rb.append(",").append(Headers.QOP.toString()).append("=\"").append(this.qopString).append("\"");
/*     */     }
/*     */     
/* 470 */     String theChallenge = rb.toString();
/* 471 */     HeaderMap responseHeader = exchange.getResponseHeaders();
/* 472 */     if (this.supportedAlgorithms.isEmpty()) {
/* 473 */       responseHeader.add(Headers.WWW_AUTHENTICATE, theChallenge);
/*     */     } else {
/* 475 */       for (DigestAlgorithm current : this.supportedAlgorithms) {
/* 476 */         responseHeader.add(Headers.WWW_AUTHENTICATE, String.format(theChallenge, new Object[] { current.getToken() }));
/*     */       } 
/*     */     } 
/*     */     
/* 480 */     return new AuthenticationMechanism.ChallengeResult(true, Integer.valueOf(401));
/*     */   }
/*     */   
/*     */   public void sendAuthenticationInfoHeader(HttpServerExchange exchange) {
/* 484 */     DigestContext context = (DigestContext)exchange.getAttachment(DigestContext.ATTACHMENT_KEY);
/* 485 */     DigestQop qop = context.getQop();
/* 486 */     String currentNonce = context.getNonce();
/* 487 */     String nextNonce = this.nonceManager.nextNonce(currentNonce, exchange);
/* 488 */     if (qop != null || !nextNonce.equals(currentNonce)) {
/* 489 */       StringBuilder sb = new StringBuilder();
/* 490 */       sb.append(Headers.NEXT_NONCE).append("=\"").append(nextNonce).append("\"");
/* 491 */       if (qop != null) {
/* 492 */         byte[] ha2; Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
/* 493 */         sb.append(",").append(Headers.QOP.toString()).append("=\"").append(qop.getToken()).append("\"");
/* 494 */         byte[] ha1 = context.getHa1();
/*     */ 
/*     */         
/* 497 */         if (qop == DigestQop.AUTH) {
/* 498 */           ha2 = createHA2Auth(context);
/*     */         } else {
/* 500 */           ha2 = createHA2AuthInt();
/*     */         } 
/* 502 */         String rspauth = new String(createRFC2617RequestDigest(ha1, ha2, context), StandardCharsets.UTF_8);
/* 503 */         sb.append(",").append(Headers.RESPONSE_AUTH.toString()).append("=\"").append(rspauth).append("\"");
/* 504 */         sb.append(",").append(Headers.CNONCE.toString()).append("=\"").append(parsedHeader.get(DigestAuthorizationToken.CNONCE)).append("\"");
/* 505 */         sb.append(",").append(Headers.NONCE_COUNT.toString()).append("=").append(parsedHeader.get(DigestAuthorizationToken.NONCE_COUNT));
/*     */       } 
/*     */       
/* 508 */       HeaderMap responseHeader = exchange.getResponseHeaders();
/* 509 */       responseHeader.add(Headers.AUTHENTICATION_INFO, sb.toString());
/*     */     } 
/*     */     
/* 512 */     exchange.removeAttachment(DigestContext.ATTACHMENT_KEY);
/*     */   }
/*     */   
/*     */   private byte[] createHA2Auth(DigestContext context) {
/* 516 */     byte[] digestUri = ((String)context.getParsedHeader().get(DigestAuthorizationToken.DIGEST_URI)).getBytes(StandardCharsets.UTF_8);
/*     */     
/* 518 */     MessageDigest digest = context.getDigest();
/*     */     try {
/* 520 */       digest.update((byte)58);
/* 521 */       digest.update(digestUri);
/*     */       
/* 523 */       return HexConverter.convertToHexBytes(digest.digest());
/*     */     } finally {
/* 525 */       digest.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class DigestContext
/*     */   {
/* 531 */     static final AttachmentKey<DigestContext> ATTACHMENT_KEY = AttachmentKey.create(DigestContext.class);
/*     */     
/*     */     private String method;
/*     */     private String nonce;
/*     */     private DigestQop qop;
/*     */     private byte[] ha1;
/*     */     private DigestAlgorithm algorithm;
/*     */     private MessageDigest digest;
/*     */     private boolean stale = false;
/*     */     Map<DigestAuthorizationToken, String> parsedHeader;
/*     */     
/*     */     String getMethod() {
/* 543 */       return this.method;
/*     */     }
/*     */     
/*     */     void setMethod(String method) {
/* 547 */       this.method = method;
/*     */     }
/*     */     
/*     */     boolean isStale() {
/* 551 */       return this.stale;
/*     */     }
/*     */     
/*     */     void markStale() {
/* 555 */       this.stale = true;
/*     */     }
/*     */     
/*     */     String getNonce() {
/* 559 */       return this.nonce;
/*     */     }
/*     */     
/*     */     void setNonce(String nonce) {
/* 563 */       this.nonce = nonce;
/*     */     }
/*     */     
/*     */     DigestQop getQop() {
/* 567 */       return this.qop;
/*     */     }
/*     */     
/*     */     void setQop(DigestQop qop) {
/* 571 */       this.qop = qop;
/*     */     }
/*     */     
/*     */     byte[] getHa1() {
/* 575 */       return this.ha1;
/*     */     }
/*     */     
/*     */     void setHa1(byte[] ha1) {
/* 579 */       this.ha1 = ha1;
/*     */     }
/*     */     
/*     */     DigestAlgorithm getAlgorithm() {
/* 583 */       return this.algorithm;
/*     */     }
/*     */     
/*     */     void setAlgorithm(DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
/* 587 */       this.algorithm = algorithm;
/* 588 */       this.digest = algorithm.getMessageDigest();
/*     */     }
/*     */     
/*     */     MessageDigest getDigest() {
/* 592 */       return this.digest;
/*     */     }
/*     */     
/*     */     Map<DigestAuthorizationToken, String> getParsedHeader() {
/* 596 */       return this.parsedHeader;
/*     */     }
/*     */     
/*     */     void setParsedHeader(Map<DigestAuthorizationToken, String> parsedHeader) {
/* 600 */       this.parsedHeader = parsedHeader;
/*     */     }
/*     */     
/*     */     private DigestContext() {}
/*     */   }
/*     */   
/*     */   private class DigestCredentialImpl implements DigestCredential {
/*     */     private final DigestAuthenticationMechanism.DigestContext context;
/*     */     
/*     */     private DigestCredentialImpl(DigestAuthenticationMechanism.DigestContext digestContext) {
/* 610 */       this.context = digestContext;
/*     */     }
/*     */ 
/*     */     
/*     */     public DigestAlgorithm getAlgorithm() {
/* 615 */       return this.context.getAlgorithm();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean verifyHA1(byte[] ha1) {
/* 620 */       this.context.setHa1(ha1);
/*     */       
/* 622 */       return DigestAuthenticationMechanism.this.validateRequest(this.context, ha1);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getRealm() {
/* 627 */       return DigestAuthenticationMechanism.this.realmName;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] getSessionData() {
/* 632 */       if (!this.context.getAlgorithm().isSession()) {
/* 633 */         throw UndertowMessages.MESSAGES.noSessionData();
/*     */       }
/*     */       
/* 636 */       byte[] nonce = ((String)this.context.getParsedHeader().get(DigestAuthorizationToken.NONCE)).getBytes(StandardCharsets.UTF_8);
/* 637 */       byte[] cnonce = ((String)this.context.getParsedHeader().get(DigestAuthorizationToken.CNONCE)).getBytes(StandardCharsets.UTF_8);
/*     */       
/* 639 */       byte[] response = new byte[nonce.length + cnonce.length + 1];
/* 640 */       System.arraycopy(nonce, 0, response, 0, nonce.length);
/* 641 */       response[nonce.length] = 58;
/* 642 */       System.arraycopy(cnonce, 0, response, nonce.length + 1, cnonce.length);
/*     */       
/* 644 */       return response;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Factory
/*     */     implements AuthenticationMechanismFactory
/*     */   {
/*     */     @Deprecated
/*     */     public Factory(IdentityManager identityManager) {}
/*     */     
/*     */     public Factory() {}
/*     */     
/*     */     public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 658 */       return new DigestAuthenticationMechanism(properties.get("realm"), properties.get("context_path"), mechanismName, identityManager);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\DigestAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */