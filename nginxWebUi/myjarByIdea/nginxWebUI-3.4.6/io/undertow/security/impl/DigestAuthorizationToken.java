package io.undertow.security.impl;

import io.undertow.util.HeaderToken;
import io.undertow.util.HeaderTokenParser;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public enum DigestAuthorizationToken implements HeaderToken {
   USERNAME(Headers.USERNAME, true),
   REALM(Headers.REALM, true),
   NONCE(Headers.NONCE, true),
   DIGEST_URI(Headers.URI, true),
   RESPONSE(Headers.RESPONSE, true),
   ALGORITHM(Headers.ALGORITHM, true),
   CNONCE(Headers.CNONCE, true),
   OPAQUE(Headers.OPAQUE, true),
   MESSAGE_QOP(Headers.QOP, true),
   NONCE_COUNT(Headers.NONCE_COUNT, false),
   AUTH_PARAM(Headers.AUTH_PARAM, false);

   private static final HeaderTokenParser<DigestAuthorizationToken> TOKEN_PARSER;
   private final String name;
   private final boolean quoted;

   private DigestAuthorizationToken(HttpString name, boolean quoted) {
      this.name = name.toString();
      this.quoted = quoted;
   }

   public String getName() {
      return this.name;
   }

   public boolean isAllowQuoted() {
      return this.quoted;
   }

   public static Map<DigestAuthorizationToken, String> parseHeader(String header) {
      return TOKEN_PARSER.parseHeader(header);
   }

   static {
      Map<String, DigestAuthorizationToken> expected = new LinkedHashMap(values().length);
      DigestAuthorizationToken[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DigestAuthorizationToken current = var1[var3];
         expected.put(current.getName(), current);
      }

      TOKEN_PARSER = new HeaderTokenParser(Collections.unmodifiableMap(expected));
   }
}
