package io.undertow.security.impl;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.NonceManager;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.DigestAlgorithm;
import io.undertow.security.idm.DigestCredential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HexConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DigestAuthenticationMechanism implements AuthenticationMechanism {
   public static final AuthenticationMechanismFactory FACTORY = new Factory();
   private static final String DEFAULT_NAME = "DIGEST";
   private static final String DIGEST_PREFIX;
   private static final int PREFIX_LENGTH;
   private static final String OPAQUE_VALUE = "00000000000000000000000000000000";
   private static final byte COLON = 58;
   private final String mechanismName;
   private final IdentityManager identityManager;
   private static final Set<DigestAuthorizationToken> MANDATORY_REQUEST_TOKENS;
   private final List<DigestAlgorithm> supportedAlgorithms;
   private final List<DigestQop> supportedQops;
   private final String qopString;
   private final String realmName;
   private final String domain;
   private final NonceManager nonceManager;

   public DigestAuthenticationMechanism(List<DigestAlgorithm> supportedAlgorithms, List<DigestQop> supportedQops, String realmName, String domain, NonceManager nonceManager) {
      this(supportedAlgorithms, supportedQops, realmName, domain, nonceManager, "DIGEST");
   }

   public DigestAuthenticationMechanism(List<DigestAlgorithm> supportedAlgorithms, List<DigestQop> supportedQops, String realmName, String domain, NonceManager nonceManager, String mechanismName) {
      this(supportedAlgorithms, supportedQops, realmName, domain, nonceManager, mechanismName, (IdentityManager)null);
   }

   public DigestAuthenticationMechanism(List<DigestAlgorithm> supportedAlgorithms, List<DigestQop> supportedQops, String realmName, String domain, NonceManager nonceManager, String mechanismName, IdentityManager identityManager) {
      this.supportedAlgorithms = supportedAlgorithms;
      this.supportedQops = supportedQops;
      this.realmName = realmName;
      this.domain = domain;
      this.nonceManager = nonceManager;
      this.mechanismName = mechanismName;
      this.identityManager = identityManager;
      if (!supportedQops.isEmpty()) {
         StringBuilder sb = new StringBuilder();
         Iterator<DigestQop> it = supportedQops.iterator();
         sb.append(((DigestQop)it.next()).getToken());

         while(it.hasNext()) {
            sb.append(",").append(((DigestQop)it.next()).getToken());
         }

         this.qopString = sb.toString();
      } else {
         this.qopString = null;
      }

   }

   public DigestAuthenticationMechanism(String realmName, String domain, String mechanismName) {
      this(realmName, domain, mechanismName, (IdentityManager)null);
   }

   public DigestAuthenticationMechanism(String realmName, String domain, String mechanismName, IdentityManager identityManager) {
      this(Collections.singletonList(DigestAlgorithm.MD5), Collections.singletonList(DigestQop.AUTH), realmName, domain, new SimpleNonceManager(), "DIGEST", identityManager);
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      List<String> authHeaders = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
      if (authHeaders != null) {
         Iterator var4 = authHeaders.iterator();
         if (var4.hasNext()) {
            String current = (String)var4.next();
            if (current.startsWith(DIGEST_PREFIX)) {
               String digestChallenge = current.substring(PREFIX_LENGTH);

               try {
                  DigestContext context = new DigestContext();
                  Map<DigestAuthorizationToken, String> parsedHeader = DigestAuthorizationToken.parseHeader(digestChallenge);
                  context.setMethod(exchange.getRequestMethod().toString());
                  context.setParsedHeader(parsedHeader);
                  exchange.putAttachment(DigestAuthenticationMechanism.DigestContext.ATTACHMENT_KEY, context);
                  UndertowLogger.SECURITY_LOGGER.debugf("Found digest header %s in %s", current, exchange);
                  return this.handleDigestHeader(exchange, securityContext);
               } catch (Exception var9) {
                  UndertowLogger.SECURITY_LOGGER.authenticationFailedFor(current, exchange, var9);
               }
            }

            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
         }
      }

      return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   private AuthenticationMechanism.AuthenticationMechanismOutcome handleDigestHeader(HttpServerExchange exchange, SecurityContext securityContext) {
      DigestContext context = (DigestContext)exchange.getAttachment(DigestAuthenticationMechanism.DigestContext.ATTACHMENT_KEY);
      Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
      Set<DigestAuthorizationToken> mandatoryTokens = EnumSet.copyOf(MANDATORY_REQUEST_TOKENS);
      if (!this.supportedAlgorithms.contains(DigestAlgorithm.MD5)) {
         mandatoryTokens.add(DigestAuthorizationToken.ALGORITHM);
      }

      if (!this.supportedQops.isEmpty() && !this.supportedQops.contains(DigestQop.AUTH)) {
         mandatoryTokens.add(DigestAuthorizationToken.MESSAGE_QOP);
      }

      DigestQop qop = null;
      if (parsedHeader.containsKey(DigestAuthorizationToken.MESSAGE_QOP)) {
         qop = DigestQop.forName((String)parsedHeader.get(DigestAuthorizationToken.MESSAGE_QOP));
         if (qop == null || !this.supportedQops.contains(qop)) {
            UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.MESSAGE_QOP.getName(), (String)parsedHeader.get(DigestAuthorizationToken.MESSAGE_QOP));
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
         }

         context.setQop(qop);
         mandatoryTokens.add(DigestAuthorizationToken.CNONCE);
         mandatoryTokens.add(DigestAuthorizationToken.NONCE_COUNT);
      }

      mandatoryTokens.removeAll(parsedHeader.keySet());
      if (mandatoryTokens.size() > 0) {
         Iterator var14 = mandatoryTokens.iterator();

         while(var14.hasNext()) {
            DigestAuthorizationToken currentToken = (DigestAuthorizationToken)var14.next();
            UndertowLogger.REQUEST_LOGGER.missingAuthorizationToken(currentToken.getName());
         }

         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      } else if (!this.realmName.equals(parsedHeader.get(DigestAuthorizationToken.REALM))) {
         UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.REALM.getName(), (String)parsedHeader.get(DigestAuthorizationToken.REALM));
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      } else if (parsedHeader.containsKey(DigestAuthorizationToken.DIGEST_URI)) {
         String uri = (String)parsedHeader.get(DigestAuthorizationToken.DIGEST_URI);
         String userName = exchange.getRequestURI();
         if (!exchange.getQueryString().isEmpty()) {
            userName = userName + "?" + exchange.getQueryString();
         }

         if (!uri.equals(userName)) {
            userName = exchange.getRequestURL();
            if (!exchange.getQueryString().isEmpty()) {
               userName = userName + "?" + exchange.getQueryString();
            }

            if (!uri.equals(userName)) {
               exchange.setStatusCode(400);
               exchange.endExchange();
               return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
         }

         if (parsedHeader.containsKey(DigestAuthorizationToken.OPAQUE) && !"00000000000000000000000000000000".equals(parsedHeader.get(DigestAuthorizationToken.OPAQUE))) {
            UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.OPAQUE.getName(), (String)parsedHeader.get(DigestAuthorizationToken.OPAQUE));
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
         } else {
            DigestAlgorithm algorithm;
            if (parsedHeader.containsKey(DigestAuthorizationToken.ALGORITHM)) {
               algorithm = DigestAlgorithm.forName((String)parsedHeader.get(DigestAuthorizationToken.ALGORITHM));
               if (algorithm == null || !this.supportedAlgorithms.contains(algorithm)) {
                  UndertowLogger.REQUEST_LOGGER.invalidTokenReceived(DigestAuthorizationToken.ALGORITHM.getName(), (String)parsedHeader.get(DigestAuthorizationToken.ALGORITHM));
                  return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
               }
            } else {
               algorithm = DigestAlgorithm.MD5;
            }

            try {
               context.setAlgorithm(algorithm);
            } catch (NoSuchAlgorithmException var12) {
               UndertowLogger.REQUEST_LOGGER.exceptionProcessingRequest(var12);
               return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }

            userName = (String)parsedHeader.get(DigestAuthorizationToken.USERNAME);
            IdentityManager identityManager = this.getIdentityManager(securityContext);
            if (algorithm.isSession()) {
               throw new IllegalStateException("Not yet implemented.");
            } else {
               DigestCredential credential = new DigestCredentialImpl(context);
               Account account = identityManager.verify(userName, credential);
               if (account == null) {
                  securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(userName), this.mechanismName);
                  return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
               } else if (!this.validateNonceUse(context, parsedHeader, exchange)) {
                  context.markStale();
                  return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
               } else {
                  this.sendAuthenticationInfoHeader(exchange);
                  securityContext.authenticationComplete(account, this.mechanismName, false);
                  return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
               }
            }
         }
      } else {
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      }
   }

   private boolean validateRequest(DigestContext context, byte[] ha1) {
      DigestQop qop = context.getQop();
      byte[] ha2;
      if (qop != null && !qop.equals(DigestQop.AUTH)) {
         ha2 = this.createHA2AuthInt();
      } else {
         ha2 = this.createHA2Auth(context, context.getParsedHeader());
      }

      byte[] requestDigest;
      if (qop == null) {
         requestDigest = this.createRFC2069RequestDigest(ha1, ha2, context);
      } else {
         requestDigest = this.createRFC2617RequestDigest(ha1, ha2, context);
      }

      byte[] providedResponse = ((String)context.getParsedHeader().get(DigestAuthorizationToken.RESPONSE)).getBytes(StandardCharsets.UTF_8);
      return MessageDigest.isEqual(requestDigest, providedResponse);
   }

   private boolean validateNonceUse(DigestContext context, Map<DigestAuthorizationToken, String> parsedHeader, HttpServerExchange exchange) {
      String suppliedNonce = (String)parsedHeader.get(DigestAuthorizationToken.NONCE);
      int nonceCount = -1;
      if (parsedHeader.containsKey(DigestAuthorizationToken.NONCE_COUNT)) {
         String nonceCountHex = (String)parsedHeader.get(DigestAuthorizationToken.NONCE_COUNT);
         nonceCount = Integer.parseInt(nonceCountHex, 16);
      }

      context.setNonce(suppliedNonce);
      return this.nonceManager.validateNonce(suppliedNonce, nonceCount, exchange);
   }

   private byte[] createHA2Auth(DigestContext context, Map<DigestAuthorizationToken, String> parsedHeader) {
      byte[] method = context.getMethod().getBytes(StandardCharsets.UTF_8);
      byte[] digestUri = ((String)parsedHeader.get(DigestAuthorizationToken.DIGEST_URI)).getBytes(StandardCharsets.UTF_8);
      MessageDigest digest = context.getDigest();

      byte[] var6;
      try {
         digest.update(method);
         digest.update((byte)58);
         digest.update(digestUri);
         var6 = HexConverter.convertToHexBytes(digest.digest());
      } finally {
         digest.reset();
      }

      return var6;
   }

   private byte[] createHA2AuthInt() {
      throw new IllegalStateException("Method not implemented.");
   }

   private byte[] createRFC2069RequestDigest(byte[] ha1, byte[] ha2, DigestContext context) {
      MessageDigest digest = context.getDigest();
      Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
      byte[] nonce = ((String)parsedHeader.get(DigestAuthorizationToken.NONCE)).getBytes(StandardCharsets.UTF_8);

      byte[] var7;
      try {
         digest.update(ha1);
         digest.update((byte)58);
         digest.update(nonce);
         digest.update((byte)58);
         digest.update(ha2);
         var7 = HexConverter.convertToHexBytes(digest.digest());
      } finally {
         digest.reset();
      }

      return var7;
   }

   private byte[] createRFC2617RequestDigest(byte[] ha1, byte[] ha2, DigestContext context) {
      MessageDigest digest = context.getDigest();
      Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
      byte[] nonce = ((String)parsedHeader.get(DigestAuthorizationToken.NONCE)).getBytes(StandardCharsets.UTF_8);
      byte[] nonceCount = ((String)parsedHeader.get(DigestAuthorizationToken.NONCE_COUNT)).getBytes(StandardCharsets.UTF_8);
      byte[] cnonce = ((String)parsedHeader.get(DigestAuthorizationToken.CNONCE)).getBytes(StandardCharsets.UTF_8);
      byte[] qop = ((String)parsedHeader.get(DigestAuthorizationToken.MESSAGE_QOP)).getBytes(StandardCharsets.UTF_8);

      byte[] var10;
      try {
         digest.update(ha1);
         digest.update((byte)58);
         digest.update(nonce);
         digest.update((byte)58);
         digest.update(nonceCount);
         digest.update((byte)58);
         digest.update(cnonce);
         digest.update((byte)58);
         digest.update(qop);
         digest.update((byte)58);
         digest.update(ha2);
         var10 = HexConverter.convertToHexBytes(digest.digest());
      } finally {
         digest.reset();
      }

      return var10;
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      DigestContext context = (DigestContext)exchange.getAttachment(DigestAuthenticationMechanism.DigestContext.ATTACHMENT_KEY);
      boolean stale = context == null ? false : context.isStale();
      StringBuilder rb = new StringBuilder(DIGEST_PREFIX);
      rb.append(Headers.REALM.toString()).append("=\"").append(this.realmName).append("\",");
      rb.append(Headers.DOMAIN.toString()).append("=\"").append(this.domain).append("\",");
      rb.append(Headers.NONCE.toString()).append("=\"").append(this.nonceManager.nextNonce((String)null, exchange)).append("\",");
      rb.append(Headers.OPAQUE.toString()).append("=\"00000000000000000000000000000000\"");
      if (stale) {
         rb.append(",stale=true");
      }

      if (this.supportedAlgorithms.size() > 0) {
         rb.append(",").append(Headers.ALGORITHM.toString()).append("=%s");
      }

      if (this.qopString != null) {
         rb.append(",").append(Headers.QOP.toString()).append("=\"").append(this.qopString).append("\"");
      }

      String theChallenge = rb.toString();
      HeaderMap responseHeader = exchange.getResponseHeaders();
      if (this.supportedAlgorithms.isEmpty()) {
         responseHeader.add(Headers.WWW_AUTHENTICATE, theChallenge);
      } else {
         Iterator var8 = this.supportedAlgorithms.iterator();

         while(var8.hasNext()) {
            DigestAlgorithm current = (DigestAlgorithm)var8.next();
            responseHeader.add(Headers.WWW_AUTHENTICATE, String.format(theChallenge, current.getToken()));
         }
      }

      return new AuthenticationMechanism.ChallengeResult(true, 401);
   }

   public void sendAuthenticationInfoHeader(HttpServerExchange exchange) {
      DigestContext context = (DigestContext)exchange.getAttachment(DigestAuthenticationMechanism.DigestContext.ATTACHMENT_KEY);
      DigestQop qop = context.getQop();
      String currentNonce = context.getNonce();
      String nextNonce = this.nonceManager.nextNonce(currentNonce, exchange);
      if (qop != null || !nextNonce.equals(currentNonce)) {
         StringBuilder sb = new StringBuilder();
         sb.append(Headers.NEXT_NONCE).append("=\"").append(nextNonce).append("\"");
         if (qop != null) {
            Map<DigestAuthorizationToken, String> parsedHeader = context.getParsedHeader();
            sb.append(",").append(Headers.QOP.toString()).append("=\"").append(qop.getToken()).append("\"");
            byte[] ha1 = context.getHa1();
            byte[] ha2;
            if (qop == DigestQop.AUTH) {
               ha2 = this.createHA2Auth(context);
            } else {
               ha2 = this.createHA2AuthInt();
            }

            String rspauth = new String(this.createRFC2617RequestDigest(ha1, ha2, context), StandardCharsets.UTF_8);
            sb.append(",").append(Headers.RESPONSE_AUTH.toString()).append("=\"").append(rspauth).append("\"");
            sb.append(",").append(Headers.CNONCE.toString()).append("=\"").append((String)parsedHeader.get(DigestAuthorizationToken.CNONCE)).append("\"");
            sb.append(",").append(Headers.NONCE_COUNT.toString()).append("=").append((String)parsedHeader.get(DigestAuthorizationToken.NONCE_COUNT));
         }

         HeaderMap responseHeader = exchange.getResponseHeaders();
         responseHeader.add(Headers.AUTHENTICATION_INFO, sb.toString());
      }

      exchange.removeAttachment(DigestAuthenticationMechanism.DigestContext.ATTACHMENT_KEY);
   }

   private byte[] createHA2Auth(DigestContext context) {
      byte[] digestUri = ((String)context.getParsedHeader().get(DigestAuthorizationToken.DIGEST_URI)).getBytes(StandardCharsets.UTF_8);
      MessageDigest digest = context.getDigest();

      byte[] var4;
      try {
         digest.update((byte)58);
         digest.update(digestUri);
         var4 = HexConverter.convertToHexBytes(digest.digest());
      } finally {
         digest.reset();
      }

      return var4;
   }

   static {
      DIGEST_PREFIX = Headers.DIGEST + " ";
      PREFIX_LENGTH = DIGEST_PREFIX.length();
      Set<DigestAuthorizationToken> mandatoryTokens = EnumSet.noneOf(DigestAuthorizationToken.class);
      mandatoryTokens.add(DigestAuthorizationToken.USERNAME);
      mandatoryTokens.add(DigestAuthorizationToken.REALM);
      mandatoryTokens.add(DigestAuthorizationToken.NONCE);
      mandatoryTokens.add(DigestAuthorizationToken.DIGEST_URI);
      mandatoryTokens.add(DigestAuthorizationToken.RESPONSE);
      MANDATORY_REQUEST_TOKENS = Collections.unmodifiableSet(mandatoryTokens);
   }

   public static final class Factory implements AuthenticationMechanismFactory {
      /** @deprecated */
      @Deprecated
      public Factory(IdentityManager identityManager) {
      }

      public Factory() {
      }

      public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
         return new DigestAuthenticationMechanism((String)properties.get("realm"), (String)properties.get("context_path"), mechanismName, identityManager);
      }
   }

   private class DigestCredentialImpl implements DigestCredential {
      private final DigestContext context;

      private DigestCredentialImpl(DigestContext digestContext) {
         this.context = digestContext;
      }

      public DigestAlgorithm getAlgorithm() {
         return this.context.getAlgorithm();
      }

      public boolean verifyHA1(byte[] ha1) {
         this.context.setHa1(ha1);
         return DigestAuthenticationMechanism.this.validateRequest(this.context, ha1);
      }

      public String getRealm() {
         return DigestAuthenticationMechanism.this.realmName;
      }

      public byte[] getSessionData() {
         if (!this.context.getAlgorithm().isSession()) {
            throw UndertowMessages.MESSAGES.noSessionData();
         } else {
            byte[] nonce = ((String)this.context.getParsedHeader().get(DigestAuthorizationToken.NONCE)).getBytes(StandardCharsets.UTF_8);
            byte[] cnonce = ((String)this.context.getParsedHeader().get(DigestAuthorizationToken.CNONCE)).getBytes(StandardCharsets.UTF_8);
            byte[] response = new byte[nonce.length + cnonce.length + 1];
            System.arraycopy(nonce, 0, response, 0, nonce.length);
            response[nonce.length] = 58;
            System.arraycopy(cnonce, 0, response, nonce.length + 1, cnonce.length);
            return response;
         }
      }

      // $FF: synthetic method
      DigestCredentialImpl(DigestContext x1, Object x2) {
         this(x1);
      }
   }

   private static class DigestContext {
      static final AttachmentKey<DigestContext> ATTACHMENT_KEY = AttachmentKey.create(DigestContext.class);
      private String method;
      private String nonce;
      private DigestQop qop;
      private byte[] ha1;
      private DigestAlgorithm algorithm;
      private MessageDigest digest;
      private boolean stale;
      Map<DigestAuthorizationToken, String> parsedHeader;

      private DigestContext() {
         this.stale = false;
      }

      String getMethod() {
         return this.method;
      }

      void setMethod(String method) {
         this.method = method;
      }

      boolean isStale() {
         return this.stale;
      }

      void markStale() {
         this.stale = true;
      }

      String getNonce() {
         return this.nonce;
      }

      void setNonce(String nonce) {
         this.nonce = nonce;
      }

      DigestQop getQop() {
         return this.qop;
      }

      void setQop(DigestQop qop) {
         this.qop = qop;
      }

      byte[] getHa1() {
         return this.ha1;
      }

      void setHa1(byte[] ha1) {
         this.ha1 = ha1;
      }

      DigestAlgorithm getAlgorithm() {
         return this.algorithm;
      }

      void setAlgorithm(DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
         this.algorithm = algorithm;
         this.digest = algorithm.getMessageDigest();
      }

      MessageDigest getDigest() {
         return this.digest;
      }

      Map<DigestAuthorizationToken, String> getParsedHeader() {
         return this.parsedHeader;
      }

      void setParsedHeader(Map<DigestAuthorizationToken, String> parsedHeader) {
         this.parsedHeader = parsedHeader;
      }

      // $FF: synthetic method
      DigestContext(Object x0) {
         this();
      }
   }
}
