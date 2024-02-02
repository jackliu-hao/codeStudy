package freemarker.core;

import freemarker.template.utility.StringUtil;

public final class _CoreStringUtils {
   private _CoreStringUtils() {
   }

   public static String toFTLIdentifierReferenceAfterDot(String name) {
      return backslashEscapeIdentifier(name);
   }

   public static String toFTLTopLevelIdentifierReference(String name) {
      return backslashEscapeIdentifier(name);
   }

   public static String toFTLTopLevelTragetIdentifier(String name) {
      char quotationType = 0;

      for(int i = 0; i < name.length(); ++i) {
         char c = name.charAt(i);
         if (i == 0) {
            if (StringUtil.isFTLIdentifierStart(c)) {
               continue;
            }
         } else if (StringUtil.isFTLIdentifierPart(c)) {
            continue;
         }

         if (c != '@') {
            if (quotationType != 0 && quotationType != 92 || !StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
               quotationType = 34;
               break;
            }

            quotationType = 92;
         }
      }

      switch (quotationType) {
         case 0:
            return name;
         case 34:
            return StringUtil.ftlQuote(name);
         case 92:
            return backslashEscapeIdentifier(name);
         default:
            throw new BugException();
      }
   }

   public static String backslashEscapeIdentifier(String name) {
      StringBuilder sb = null;

      for(int i = 0; i < name.length(); ++i) {
         char c = name.charAt(i);
         if (StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
            if (sb == null) {
               sb = new StringBuilder(name.length() + 8);
               sb.append(name, 0, i);
            }

            sb.append('\\');
         }

         if (sb != null) {
            sb.append(c);
         }
      }

      return sb == null ? name : sb.toString();
   }

   public static int getIdentifierNamingConvention(String name) {
      int ln = name.length();

      for(int i = 0; i < ln; ++i) {
         char c = name.charAt(i);
         if (c == '_') {
            return 11;
         }

         if (isUpperUSASCII(c)) {
            return 12;
         }
      }

      return 10;
   }

   public static String camelCaseToUnderscored(String camelCaseName) {
      int i;
      for(i = 0; i < camelCaseName.length() && Character.isLowerCase(camelCaseName.charAt(i)); ++i) {
      }

      if (i == camelCaseName.length()) {
         return camelCaseName;
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append(camelCaseName.substring(0, i));

         for(; i < camelCaseName.length(); ++i) {
            char c = camelCaseName.charAt(i);
            if (isUpperUSASCII(c)) {
               sb.append('_');
               sb.append(Character.toLowerCase(c));
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public static boolean isUpperUSASCII(char c) {
      return c >= 'A' && c <= 'Z';
   }
}
