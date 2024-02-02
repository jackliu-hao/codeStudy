package io.undertow.security.impl;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.FlexBase64;
import io.undertow.util.Headers;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class BasicAuthenticationMechanism implements AuthenticationMechanism {
   public static final AuthenticationMechanismFactory FACTORY = new Factory();
   public static final String SILENT = "silent";
   public static final String CHARSET = "charset";
   public static final String USER_AGENT_CHARSETS = "user-agent-charsets";
   private final String name;
   private final String challenge;
   private static final String BASIC_PREFIX;
   private static final String LOWERCASE_BASIC_PREFIX;
   private static final int PREFIX_LENGTH;
   private static final String COLON = ":";
   private final boolean silent;
   private final IdentityManager identityManager;
   private final Charset charset;
   private final Map<Pattern, Charset> userAgentCharsets;

   public BasicAuthenticationMechanism(String realmName) {
      this(realmName, "BASIC");
   }

   public BasicAuthenticationMechanism(String realmName, String mechanismName) {
      this(realmName, mechanismName, false);
   }

   public BasicAuthenticationMechanism(String realmName, String mechanismName, boolean silent) {
      this(realmName, mechanismName, silent, (IdentityManager)null);
   }

   public BasicAuthenticationMechanism(String realmName, String mechanismName, boolean silent, IdentityManager identityManager) {
      this(realmName, mechanismName, silent, identityManager, StandardCharsets.UTF_8, Collections.emptyMap());
   }

   public BasicAuthenticationMechanism(String realmName, String mechanismName, boolean silent, IdentityManager identityManager, Charset charset, Map<Pattern, Charset> userAgentCharsets) {
      this.challenge = BASIC_PREFIX + "realm=\"" + realmName + "\"";
      this.name = mechanismName;
      this.silent = silent;
      this.identityManager = identityManager;
      this.charset = charset;
      this.userAgentCharsets = Collections.unmodifiableMap(new LinkedHashMap(userAgentCharsets));
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      List<String> authHeaders = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
      if (authHeaders != null) {
         Iterator var4 = authHeaders.iterator();

         while(var4.hasNext()) {
            String current = (String)var4.next();
            if (current.toLowerCase(Locale.ENGLISH).startsWith(LOWERCASE_BASIC_PREFIX)) {
               String base64Challenge = current.substring(PREFIX_LENGTH);
               String plainChallenge = null;

               try {
                  ByteBuffer decode = FlexBase64.decode(base64Challenge);
                  Charset charset = this.charset;
                  if (!this.userAgentCharsets.isEmpty()) {
                     String ua = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
                     if (ua != null) {
                        Iterator var11 = this.userAgentCharsets.entrySet().iterator();

                        while(var11.hasNext()) {
                           Map.Entry<Pattern, Charset> entry = (Map.Entry)var11.next();
                           if (((Pattern)entry.getKey()).matcher(ua).find()) {
                              charset = (Charset)entry.getValue();
                              break;
                           }
                        }
                     }
                  }

                  plainChallenge = new String(decode.array(), decode.arrayOffset(), decode.limit(), charset);
                  UndertowLogger.SECURITY_LOGGER.debugf("Found basic auth header (decoded using charset %s) in %s", charset, exchange);
               } catch (IOException var20) {
                  UndertowLogger.SECURITY_LOGGER.debugf(var20, "Failed to decode basic auth header in %s", exchange);
               }

               int colonPos;
               if (plainChallenge != null && (colonPos = plainChallenge.indexOf(":")) > -1) {
                  String userName = plainChallenge.substring(0, colonPos);
                  char[] password = plainChallenge.substring(colonPos + 1).toCharArray();
                  IdentityManager idm = this.getIdentityManager(securityContext);
                  PasswordCredential credential = new PasswordCredential(password);

                  AuthenticationMechanism.AuthenticationMechanismOutcome var15;
                  try {
                     Account account = idm.verify(userName, credential);
                     AuthenticationMechanism.AuthenticationMechanismOutcome result;
                     if (account != null) {
                        securityContext.authenticationComplete(account, this.name, false);
                        result = AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
                     } else {
                        securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(userName), this.name);
                        result = AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                     }

                     var15 = result;
                  } finally {
                     clear(password);
                  }

                  return var15;
               }

               return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
         }
      }

      return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      if (this.silent) {
         String authHeader = exchange.getRequestHeaders().getFirst(Headers.AUTHORIZATION);
         if (authHeader == null) {
            return AuthenticationMechanism.ChallengeResult.NOT_SENT;
         }
      }

      exchange.getResponseHeaders().add(Headers.WWW_AUTHENTICATE, this.challenge);
      UndertowLogger.SECURITY_LOGGER.debugf("Sending basic auth challenge %s for %s", this.challenge, exchange);
      return new AuthenticationMechanism.ChallengeResult(true, 401);
   }

   private static void clear(char[] array) {
      for(int i = 0; i < array.length; ++i) {
         array[i] = 0;
      }

   }

   static {
      BASIC_PREFIX = Headers.BASIC + " ";
      LOWERCASE_BASIC_PREFIX = BASIC_PREFIX.toLowerCase(Locale.ENGLISH);
      PREFIX_LENGTH = BASIC_PREFIX.length();
   }

   public static class Factory implements AuthenticationMechanismFactory {
      /** @deprecated */
      @Deprecated
      public Factory(IdentityManager identityManager) {
      }

      public Factory() {
      }

      public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
         String realm = (String)properties.get("realm");
         String silent = (String)properties.get("silent");
         String charsetString = (String)properties.get("charset");
         Charset charset = charsetString == null ? StandardCharsets.UTF_8 : Charset.forName(charsetString);
         Map<Pattern, Charset> userAgentCharsets = new HashMap();
         String userAgentString = (String)properties.get("user-agent-charsets");
         if (userAgentString != null) {
            String[] parts = userAgentString.split(",");
            if (parts.length % 2 != 0) {
               throw UndertowMessages.MESSAGES.userAgentCharsetMustHaveEvenNumberOfItems(userAgentString);
            }

            for(int i = 0; i < parts.length; i += 2) {
               Pattern pattern = Pattern.compile(parts[i]);
               Charset c = Charset.forName(parts[i + 1]);
               userAgentCharsets.put(pattern, c);
            }
         }

         return new BasicAuthenticationMechanism(realm, mechanismName, silent != null && silent.equals("true"), identityManager, charset, userAgentCharsets);
      }
   }
}
