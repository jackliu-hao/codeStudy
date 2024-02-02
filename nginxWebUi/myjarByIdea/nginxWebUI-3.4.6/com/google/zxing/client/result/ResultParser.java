package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class ResultParser {
   private static final ResultParser[] PARSERS = new ResultParser[]{new BookmarkDoCoMoResultParser(), new AddressBookDoCoMoResultParser(), new EmailDoCoMoResultParser(), new AddressBookAUResultParser(), new VCardResultParser(), new BizcardResultParser(), new VEventResultParser(), new EmailAddressResultParser(), new SMTPResultParser(), new TelResultParser(), new SMSMMSResultParser(), new SMSTOMMSTOResultParser(), new GeoResultParser(), new WifiResultParser(), new URLTOResultParser(), new URIResultParser(), new ISBNResultParser(), new ProductResultParser(), new ExpandedProductResultParser(), new VINResultParser()};
   private static final Pattern DIGITS = Pattern.compile("\\d+");
   private static final Pattern AMPERSAND = Pattern.compile("&");
   private static final Pattern EQUALS = Pattern.compile("=");
   private static final String BYTE_ORDER_MARK = "\ufeff";

   public abstract ParsedResult parse(Result var1);

   protected static String getMassagedText(Result result) {
      String text;
      if ((text = result.getText()).startsWith("\ufeff")) {
         text = text.substring(1);
      }

      return text;
   }

   public static ParsedResult parseResult(Result theResult) {
      ResultParser[] var1;
      int var2 = (var1 = PARSERS).length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ParsedResult result;
         if ((result = var1[var3].parse(theResult)) != null) {
            return result;
         }
      }

      return new TextParsedResult(theResult.getText(), (String)null);
   }

   protected static void maybeAppend(String value, StringBuilder result) {
      if (value != null) {
         result.append('\n');
         result.append(value);
      }

   }

   protected static void maybeAppend(String[] value, StringBuilder result) {
      if (value != null) {
         String[] var2 = value;
         int var3 = value.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            result.append('\n');
            result.append(s);
         }
      }

   }

   protected static String[] maybeWrap(String value) {
      return value == null ? null : new String[]{value};
   }

   protected static String unescapeBackslash(String escaped) {
      int backslash;
      if ((backslash = escaped.indexOf(92)) < 0) {
         return escaped;
      } else {
         int max = escaped.length();
         StringBuilder unescaped;
         (unescaped = new StringBuilder(max - 1)).append(escaped.toCharArray(), 0, backslash);
         boolean nextIsEscaped = false;

         for(int i = backslash; i < max; ++i) {
            char c = escaped.charAt(i);
            if (!nextIsEscaped && c == '\\') {
               nextIsEscaped = true;
            } else {
               unescaped.append(c);
               nextIsEscaped = false;
            }
         }

         return unescaped.toString();
      }
   }

   protected static int parseHexDigit(char c) {
      if (c >= '0' && c <= '9') {
         return c - 48;
      } else if (c >= 'a' && c <= 'f') {
         return 10 + (c - 97);
      } else {
         return c >= 'A' && c <= 'F' ? 10 + (c - 65) : -1;
      }
   }

   protected static boolean isStringOfDigits(CharSequence value, int length) {
      return value != null && length > 0 && length == value.length() && DIGITS.matcher(value).matches();
   }

   protected static boolean isSubstringOfDigits(CharSequence value, int offset, int length) {
      if (value != null && length > 0) {
         int max = offset + length;
         return value.length() >= max && DIGITS.matcher(value.subSequence(offset, max)).matches();
      } else {
         return false;
      }
   }

   static Map<String, String> parseNameValuePairs(String uri) {
      int paramStart;
      if ((paramStart = uri.indexOf(63)) < 0) {
         return null;
      } else {
         Map<String, String> result = new HashMap(3);
         String[] var3;
         int var4 = (var3 = AMPERSAND.split(uri.substring(paramStart + 1))).length;

         for(int var5 = 0; var5 < var4; ++var5) {
            appendKeyValue(var3[var5], result);
         }

         return result;
      }
   }

   private static void appendKeyValue(CharSequence keyValue, Map<String, String> result) {
      String[] keyValueTokens;
      if ((keyValueTokens = EQUALS.split(keyValue, 2)).length == 2) {
         String key = keyValueTokens[0];
         String value = keyValueTokens[1];

         try {
            value = urlDecode(value);
            result.put(key, value);
            return;
         } catch (IllegalArgumentException var5) {
         }
      }

   }

   static String urlDecode(String encoded) {
      try {
         return URLDecoder.decode(encoded, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         throw new IllegalStateException(var2);
      }
   }

   static String[] matchPrefixedField(String prefix, String rawText, char endChar, boolean trim) {
      List<String> matches = null;
      int i = 0;
      int max = rawText.length();

      while(i < max && (i = rawText.indexOf(prefix, i)) >= 0) {
         int start = i += prefix.length();
         boolean more = true;

         while(more) {
            if ((i = rawText.indexOf(endChar, i)) < 0) {
               i = rawText.length();
               more = false;
            } else if (countPrecedingBackslashes(rawText, i) % 2 != 0) {
               ++i;
            } else {
               if (matches == null) {
                  matches = new ArrayList(3);
               }

               String element = unescapeBackslash(rawText.substring(start, i));
               if (trim) {
                  element = element.trim();
               }

               if (!element.isEmpty()) {
                  matches.add(element);
               }

               ++i;
               more = false;
            }
         }
      }

      return matches != null && !matches.isEmpty() ? (String[])matches.toArray(new String[matches.size()]) : null;
   }

   private static int countPrecedingBackslashes(CharSequence s, int pos) {
      int count = 0;

      for(int i = pos - 1; i >= 0 && s.charAt(i) == '\\'; --i) {
         ++count;
      }

      return count;
   }

   static String matchSinglePrefixedField(String prefix, String rawText, char endChar, boolean trim) {
      String[] matches;
      return (matches = matchPrefixedField(prefix, rawText, endChar, trim)) == null ? null : matches[0];
   }
}
