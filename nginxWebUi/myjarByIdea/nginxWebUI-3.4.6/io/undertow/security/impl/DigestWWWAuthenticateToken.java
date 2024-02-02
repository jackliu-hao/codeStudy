package io.undertow.security.impl;

import io.undertow.util.HeaderToken;
import io.undertow.util.HeaderTokenParser;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public enum DigestWWWAuthenticateToken implements HeaderToken {
   REALM(Headers.REALM, true),
   DOMAIN(Headers.DOMAIN, true),
   NONCE(Headers.NONCE, true),
   OPAQUE(Headers.OPAQUE, true),
   STALE(Headers.STALE, false),
   ALGORITHM(Headers.ALGORITHM, false),
   MESSAGE_QOP(Headers.QOP, true),
   AUTH_PARAM(Headers.AUTH_PARAM, false);

   private static final HeaderTokenParser<DigestWWWAuthenticateToken> TOKEN_PARSER;
   private final String name;
   private final boolean quoted;

   private DigestWWWAuthenticateToken(HttpString name, boolean quoted) {
      this.name = name.toString();
      this.quoted = quoted;
   }

   public String getName() {
      return this.name;
   }

   public boolean isAllowQuoted() {
      return this.quoted;
   }

   public static Map<DigestWWWAuthenticateToken, String> parseHeader(String header) {
      return TOKEN_PARSER.parseHeader(header);
   }

   static {
      Map<String, DigestWWWAuthenticateToken> expected = new LinkedHashMap(values().length);
      DigestWWWAuthenticateToken[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DigestWWWAuthenticateToken current = var1[var3];
         expected.put(current.getName(), current);
      }

      TOKEN_PARSER = new HeaderTokenParser(Collections.unmodifiableMap(expected));
   }
}
