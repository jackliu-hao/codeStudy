package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.LinkedHashMap;
import java.util.Map;

public class HeaderTokenParser<E extends HeaderToken> {
   private static final char EQUALS = '=';
   private static final char COMMA = ',';
   private static final char QUOTE = '"';
   private static final char ESCAPE = '\\';
   private final Map<String, E> expectedTokens;

   public HeaderTokenParser(Map<String, E> expectedTokens) {
      this.expectedTokens = expectedTokens;
   }

   public Map<E, String> parseHeader(String header) {
      char[] headerChars = header.toCharArray();
      Map<E, String> response = new LinkedHashMap();
      SearchingFor searchingFor = HeaderTokenParser.SearchingFor.START_OF_NAME;
      int nameStart = 0;
      E currentToken = null;
      int valueStart = 0;
      int escapeCount = 0;
      boolean containsEscapes = false;

      for(int i = 0; i < headerChars.length; ++i) {
         String value;
         switch (searchingFor) {
            case START_OF_NAME:
               if (headerChars[i] != ',' && !Character.isWhitespace(headerChars[i])) {
                  nameStart = i;
                  searchingFor = HeaderTokenParser.SearchingFor.EQUALS_SIGN;
               }
               break;
            case EQUALS_SIGN:
               if (headerChars[i] == '=') {
                  value = String.valueOf(headerChars, nameStart, i - nameStart);
                  currentToken = (HeaderToken)this.expectedTokens.get(value);
                  if (currentToken == null) {
                     throw UndertowMessages.MESSAGES.unexpectedTokenInHeader(value);
                  }

                  searchingFor = HeaderTokenParser.SearchingFor.START_OF_VALUE;
               }
               break;
            case START_OF_VALUE:
               if (Character.isWhitespace(headerChars[i])) {
                  break;
               }

               if (headerChars[i] == '"' && currentToken.isAllowQuoted()) {
                  valueStart = i + 1;
                  searchingFor = HeaderTokenParser.SearchingFor.LAST_QUOTE;
                  break;
               }

               valueStart = i;
               searchingFor = HeaderTokenParser.SearchingFor.END_OF_VALUE;
               break;
            case LAST_QUOTE:
               if (headerChars[i] == '\\') {
                  ++escapeCount;
                  containsEscapes = true;
               } else {
                  if (headerChars[i] == '"' && escapeCount % 2 == 0) {
                     value = String.valueOf(headerChars, valueStart, i - valueStart);
                     if (containsEscapes) {
                        StringBuilder sb = new StringBuilder();
                        boolean lastEscape = false;

                        for(int j = 0; j < value.length(); ++j) {
                           char c = value.charAt(j);
                           if (c == '\\' && !lastEscape) {
                              lastEscape = true;
                           } else {
                              lastEscape = false;
                              sb.append(c);
                           }
                        }

                        value = sb.toString();
                        containsEscapes = false;
                     }

                     response.put(currentToken, value);
                     searchingFor = HeaderTokenParser.SearchingFor.START_OF_NAME;
                     escapeCount = 0;
                     continue;
                  }

                  escapeCount = 0;
               }
               break;
            case END_OF_VALUE:
               if (headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) {
                  value = String.valueOf(headerChars, valueStart, i - valueStart);
                  response.put(currentToken, value);
                  searchingFor = HeaderTokenParser.SearchingFor.START_OF_NAME;
               }
         }
      }

      if (searchingFor == HeaderTokenParser.SearchingFor.END_OF_VALUE) {
         String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
         response.put(currentToken, value);
      } else if (searchingFor != HeaderTokenParser.SearchingFor.START_OF_NAME) {
         throw UndertowMessages.MESSAGES.invalidHeader();
      }

      return response;
   }

   static enum SearchingFor {
      START_OF_NAME,
      EQUALS_SIGN,
      START_OF_VALUE,
      LAST_QUOTE,
      END_OF_VALUE;
   }
}
