package com.mysql.cj.util;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.text.Normalizer;
import java.text.Normalizer.Form;

public class SaslPrep {
   public static String prepare(String str, StringType sType) {
      if (str.length() == 0) {
         return str;
      } else {
         StringBuilder sb = new StringBuilder(str.length());
         char[] var3 = str.toCharArray();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            char chr = var3[var5];
            if (isNonAsciiSpaceChar(chr)) {
               sb.append(' ');
            } else if (!isMappeableToNothing(chr)) {
               sb.append(chr);
            }
         }

         String preparedStr = normalizeKc(sb);
         boolean startsWithRAndAlCat = isBidiRAndAlCat(preparedStr.codePointAt(0));
         boolean endsWithRAndAlCat = isBidiRAndAlCat(preparedStr.codePointAt(preparedStr.length() - (Character.isLowSurrogate(preparedStr.charAt(preparedStr.length() - 1)) ? 2 : 1)));
         boolean containsRAndAlCat = startsWithRAndAlCat || endsWithRAndAlCat;
         boolean containsLCat = false;

         int ni;
         for(int i = 0; i < preparedStr.length(); i = ni) {
            char chr = preparedStr.charAt(i);
            int cp = preparedStr.codePointAt(i);
            ni = i + Character.charCount(cp);
            if (isProhibited(chr, cp)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Prohibited character at position " + i + ".");
            }

            if (!containsRAndAlCat) {
               containsRAndAlCat = isBidiRAndAlCat(cp);
            }

            if (!containsLCat) {
               containsLCat = isBidiLCat(cp);
            }

            if (containsRAndAlCat && containsLCat) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Cannot contain both RandALCat characters and LCat characters.");
            }

            if (ni >= preparedStr.length() && containsRAndAlCat && (!startsWithRAndAlCat || !endsWithRAndAlCat)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Cannot contain RandALCat characters and not start and end with RandALCat characters.");
            }

            if (sType == SaslPrep.StringType.STORED && isUnassigned(cp)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unassigned character at position " + i + ".");
            }
         }

         return preparedStr;
      }
   }

   private static boolean isNonAsciiSpaceChar(char chr) {
      return chr == 160 || chr == 5760 || chr >= 8192 && chr <= 8203 || chr == 8239 || chr == 8287 || chr == 12288;
   }

   private static boolean isMappeableToNothing(char chr) {
      return chr == 173 || chr == 847 || chr == 6150 || chr >= 6155 && chr <= 6157 || chr >= 8203 && chr <= 8205 || chr == 8288 || chr >= '︀' && chr <= '️' || chr == '\ufeff';
   }

   private static String normalizeKc(CharSequence str) {
      return Normalizer.normalize(str, Form.NFKC);
   }

   private static boolean isProhibited(char chr, int cp) {
      return isAsciiControlCharacter(chr) || isNonAsciiControlCharacter(cp) || isPrivateUseCharacter(cp) || isNonCharacterCodePoint(cp) || isSurrogateCode(chr) || isInappropriateForPlainTextCharacter(chr) || isInappropriateForCanonicalRepresentationCharacter(chr) || isChangeDisplayPropertiesOrDeprecatedCharacter(chr) || isTaggingCharacter(cp);
   }

   private static boolean isAsciiControlCharacter(char chr) {
      return chr <= 31 || chr == 127;
   }

   private static boolean isNonAsciiControlCharacter(int cp) {
      return cp >= 128 && cp <= 159 || cp == 1757 || cp == 1807 || cp == 6158 || cp == 8204 || cp == 8205 || cp == 8232 || cp == 8233 || cp >= 8288 && cp <= 8291 || cp >= 8298 && cp <= 8303 || cp == 65279 || cp >= 65529 && cp <= 65532 || cp >= 119155 && cp <= 119162;
   }

   private static boolean isPrivateUseCharacter(int cp) {
      return cp >= 57344 && cp <= 63743 || cp >= 983040 && cp <= 1048573 || cp >= 1048576 && cp <= 1114109;
   }

   private static boolean isNonCharacterCodePoint(int cp) {
      return cp >= 64976 && cp <= 65007 || cp >= 65534 && cp <= 65535 || cp >= 131070 && cp <= 131071 || cp >= 196606 && cp <= 196607 || cp >= 262142 && cp <= 262143 || cp >= 327678 && cp <= 327679 || cp >= 393214 && cp <= 393215 || cp >= 458750 && cp <= 458751 || cp >= 524286 && cp <= 524287 || cp >= 589822 && cp <= 589823 || cp >= 655358 && cp <= 655359 || cp >= 720894 && cp <= 720895 || cp >= 786430 && cp <= 786431 || cp >= 851966 && cp <= 851967 || cp >= 917502 && cp <= 917503 || cp >= 983038 && cp <= 983039 || cp >= 1048574 && cp <= 1048575 || cp >= 1114110 && cp <= 1114111;
   }

   private static boolean isSurrogateCode(char chr) {
      return chr >= '\ud800' && chr <= '\udfff';
   }

   private static boolean isInappropriateForPlainTextCharacter(char chr) {
      return chr == '\ufff9' || chr >= '\ufffa' && chr <= '�';
   }

   private static boolean isInappropriateForCanonicalRepresentationCharacter(char chr) {
      return chr >= 12272 && chr <= 12283;
   }

   private static boolean isChangeDisplayPropertiesOrDeprecatedCharacter(char chr) {
      return chr == 832 || chr == 833 || chr == 8206 || chr == 8207 || chr >= 8234 && chr <= 8238 || chr >= 8298 && chr <= 8303;
   }

   private static boolean isTaggingCharacter(int cp) {
      return cp == 917505 || cp >= 917536 && cp <= 917631;
   }

   private static boolean isBidiRAndAlCat(int cp) {
      byte dir = Character.getDirectionality(cp);
      return dir == 1 || dir == 2;
   }

   private static boolean isBidiLCat(int cp) {
      byte dir = Character.getDirectionality(cp);
      return dir == 0;
   }

   private static boolean isUnassigned(int cp) {
      return !Character.isDefined(cp);
   }

   public static enum StringType {
      STORED,
      QUERY;
   }
}
