/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.idm.PasswordCredential;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
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
/*     */ public class BasicAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  59 */   public static final AuthenticationMechanismFactory FACTORY = new Factory();
/*     */ 
/*     */   
/*     */   public static final String SILENT = "silent";
/*     */ 
/*     */   
/*     */   public static final String CHARSET = "charset";
/*     */ 
/*     */   
/*     */   public static final String USER_AGENT_CHARSETS = "user-agent-charsets";
/*     */ 
/*     */   
/*     */   private final String name;
/*     */ 
/*     */   
/*     */   private final String challenge;
/*     */ 
/*     */   
/*  77 */   private static final String BASIC_PREFIX = Headers.BASIC + " ";
/*  78 */   private static final String LOWERCASE_BASIC_PREFIX = BASIC_PREFIX.toLowerCase(Locale.ENGLISH);
/*  79 */   private static final int PREFIX_LENGTH = BASIC_PREFIX.length();
/*     */ 
/*     */   
/*     */   private static final String COLON = ":";
/*     */ 
/*     */   
/*     */   private final boolean silent;
/*     */ 
/*     */   
/*     */   private final IdentityManager identityManager;
/*     */   
/*     */   private final Charset charset;
/*     */   
/*     */   private final Map<Pattern, Charset> userAgentCharsets;
/*     */ 
/*     */   
/*     */   public BasicAuthenticationMechanism(String realmName) {
/*  96 */     this(realmName, "BASIC");
/*     */   }
/*     */   
/*     */   public BasicAuthenticationMechanism(String realmName, String mechanismName) {
/* 100 */     this(realmName, mechanismName, false);
/*     */   }
/*     */   
/*     */   public BasicAuthenticationMechanism(String realmName, String mechanismName, boolean silent) {
/* 104 */     this(realmName, mechanismName, silent, null);
/*     */   }
/*     */   public BasicAuthenticationMechanism(String realmName, String mechanismName, boolean silent, IdentityManager identityManager) {
/* 107 */     this(realmName, mechanismName, silent, identityManager, StandardCharsets.UTF_8, Collections.emptyMap());
/*     */   }
/*     */   
/*     */   public BasicAuthenticationMechanism(String realmName, String mechanismName, boolean silent, IdentityManager identityManager, Charset charset, Map<Pattern, Charset> userAgentCharsets) {
/* 111 */     this.challenge = BASIC_PREFIX + "realm=\"" + realmName + "\"";
/* 112 */     this.name = mechanismName;
/* 113 */     this.silent = silent;
/* 114 */     this.identityManager = identityManager;
/* 115 */     this.charset = charset;
/* 116 */     this.userAgentCharsets = Collections.unmodifiableMap(new LinkedHashMap<>(userAgentCharsets));
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/* 121 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/* 130 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
/* 131 */     if (headerValues != null) {
/* 132 */       for (String current : headerValues) {
/* 133 */         if (current.toLowerCase(Locale.ENGLISH).startsWith(LOWERCASE_BASIC_PREFIX)) {
/*     */           
/* 135 */           String base64Challenge = current.substring(PREFIX_LENGTH);
/* 136 */           String plainChallenge = null;
/*     */           try {
/* 138 */             ByteBuffer decode = FlexBase64.decode(base64Challenge);
/*     */             
/* 140 */             Charset charset = this.charset;
/* 141 */             if (!this.userAgentCharsets.isEmpty()) {
/* 142 */               String ua = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
/* 143 */               if (ua != null) {
/* 144 */                 for (Map.Entry<Pattern, Charset> entry : this.userAgentCharsets.entrySet()) {
/* 145 */                   if (((Pattern)entry.getKey()).matcher(ua).find()) {
/* 146 */                     charset = entry.getValue();
/*     */                     
/*     */                     break;
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */             } 
/* 153 */             plainChallenge = new String(decode.array(), decode.arrayOffset(), decode.limit(), charset);
/* 154 */             UndertowLogger.SECURITY_LOGGER.debugf("Found basic auth header (decoded using charset %s) in %s", charset, exchange);
/* 155 */           } catch (IOException e) {
/* 156 */             UndertowLogger.SECURITY_LOGGER.debugf(e, "Failed to decode basic auth header in %s", exchange);
/*     */           } 
/*     */           int colonPos;
/* 159 */           if (plainChallenge != null && (colonPos = plainChallenge.indexOf(":")) > -1) {
/* 160 */             String userName = plainChallenge.substring(0, colonPos);
/* 161 */             char[] password = plainChallenge.substring(colonPos + 1).toCharArray();
/*     */             
/* 163 */             IdentityManager idm = getIdentityManager(securityContext);
/* 164 */             PasswordCredential credential = new PasswordCredential(password);
/*     */             try {
/*     */               AuthenticationMechanism.AuthenticationMechanismOutcome result;
/* 167 */               Account account = idm.verify(userName, (Credential)credential);
/* 168 */               if (account != null) {
/* 169 */                 securityContext.authenticationComplete(account, this.name, false);
/* 170 */                 result = AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */               } else {
/* 172 */                 securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(userName), this.name);
/* 173 */                 result = AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */               } 
/* 175 */               return result;
/*     */             } finally {
/* 177 */               clear(password);
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 183 */           return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 189 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 194 */     if (this.silent) {
/*     */ 
/*     */       
/* 197 */       String authHeader = exchange.getRequestHeaders().getFirst(Headers.AUTHORIZATION);
/* 198 */       if (authHeader == null) {
/* 199 */         return AuthenticationMechanism.ChallengeResult.NOT_SENT;
/*     */       }
/*     */     } 
/* 202 */     exchange.getResponseHeaders().add(Headers.WWW_AUTHENTICATE, this.challenge);
/* 203 */     UndertowLogger.SECURITY_LOGGER.debugf("Sending basic auth challenge %s for %s", this.challenge, exchange);
/* 204 */     return new AuthenticationMechanism.ChallengeResult(true, Integer.valueOf(401));
/*     */   }
/*     */   
/*     */   private static void clear(char[] array) {
/* 208 */     for (int i = 0; i < array.length; i++) {
/* 209 */       array[i] = Character.MIN_VALUE;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Factory
/*     */     implements AuthenticationMechanismFactory
/*     */   {
/*     */     @Deprecated
/*     */     public Factory(IdentityManager identityManager) {}
/*     */     
/*     */     public Factory() {}
/*     */     
/*     */     public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 222 */       String realm = properties.get("realm");
/* 223 */       String silent = properties.get("silent");
/* 224 */       String charsetString = properties.get("charset");
/* 225 */       Charset charset = (charsetString == null) ? StandardCharsets.UTF_8 : Charset.forName(charsetString);
/* 226 */       Map<Pattern, Charset> userAgentCharsets = new HashMap<>();
/* 227 */       String userAgentString = properties.get("user-agent-charsets");
/* 228 */       if (userAgentString != null) {
/* 229 */         String[] parts = userAgentString.split(",");
/* 230 */         if (parts.length % 2 != 0) {
/* 231 */           throw UndertowMessages.MESSAGES.userAgentCharsetMustHaveEvenNumberOfItems(userAgentString);
/*     */         }
/* 233 */         for (int i = 0; i < parts.length; i += 2) {
/* 234 */           Pattern pattern = Pattern.compile(parts[i]);
/* 235 */           Charset c = Charset.forName(parts[i + 1]);
/* 236 */           userAgentCharsets.put(pattern, c);
/*     */         } 
/*     */       } 
/*     */       
/* 240 */       return new BasicAuthenticationMechanism(realm, mechanismName, (silent != null && silent.equals("true")), identityManager, charset, userAgentCharsets);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\BasicAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */