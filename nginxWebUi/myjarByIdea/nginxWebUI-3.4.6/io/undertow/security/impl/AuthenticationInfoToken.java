package io.undertow.security.impl;

import io.undertow.util.HeaderToken;
import io.undertow.util.HeaderTokenParser;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public enum AuthenticationInfoToken implements HeaderToken {
   NEXT_NONCE(Headers.NEXT_NONCE, true),
   MESSAGE_QOP(Headers.QOP, true),
   RESPONSE_AUTH(Headers.RESPONSE_AUTH, true),
   CNONCE(Headers.CNONCE, true),
   NONCE_COUNT(Headers.NONCE_COUNT, false);

   private static final HeaderTokenParser<AuthenticationInfoToken> TOKEN_PARSER;
   private final String name;
   private final boolean quoted;

   private AuthenticationInfoToken(HttpString name, boolean quoted) {
      this.name = name.toString();
      this.quoted = quoted;
   }

   public String getName() {
      return this.name;
   }

   public boolean isAllowQuoted() {
      return this.quoted;
   }

   public static Map<AuthenticationInfoToken, String> parseHeader(String header) {
      return TOKEN_PARSER.parseHeader(header);
   }

   static {
      Map<String, AuthenticationInfoToken> expected = new LinkedHashMap(values().length);
      AuthenticationInfoToken[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AuthenticationInfoToken current = var1[var3];
         expected.put(current.getName(), current);
      }

      TOKEN_PARSER = new HeaderTokenParser(Collections.unmodifiableMap(expected));
   }
}
