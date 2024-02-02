package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VCardResultParser extends ResultParser {
   private static final Pattern BEGIN_VCARD = Pattern.compile("BEGIN:VCARD", 2);
   private static final Pattern VCARD_LIKE_DATE = Pattern.compile("\\d{4}-?\\d{2}-?\\d{2}");
   private static final Pattern CR_LF_SPACE_TAB = Pattern.compile("\r\n[ \t]");
   private static final Pattern NEWLINE_ESCAPE = Pattern.compile("\\\\[nN]");
   private static final Pattern VCARD_ESCAPES = Pattern.compile("\\\\([,;\\\\])");
   private static final Pattern EQUALS = Pattern.compile("=");
   private static final Pattern SEMICOLON = Pattern.compile(";");
   private static final Pattern UNESCAPED_SEMICOLONS = Pattern.compile("(?<!\\\\);+");
   private static final Pattern COMMA = Pattern.compile(",");
   private static final Pattern SEMICOLON_OR_COMMA = Pattern.compile("[;,]");

   public AddressBookParsedResult parse(Result result) {
      String rawText = getMassagedText(result);
      Matcher m;
      if ((m = BEGIN_VCARD.matcher(rawText)).find() && m.start() == 0) {
         List names;
         if ((names = matchVCardPrefixedField("FN", rawText, true, false)) == null) {
            formatNames(names = matchVCardPrefixedField("N", rawText, true, false));
         }

         List nicknameString;
         String[] nicknames = (nicknameString = matchSingleVCardPrefixedField("NICKNAME", rawText, true, false)) == null ? null : COMMA.split((CharSequence)nicknameString.get(0));
         List<List<String>> phoneNumbers = matchVCardPrefixedField("TEL", rawText, true, false);
         List<List<String>> emails = matchVCardPrefixedField("EMAIL", rawText, true, false);
         List<String> note = matchSingleVCardPrefixedField("NOTE", rawText, false, false);
         List<List<String>> addresses = matchVCardPrefixedField("ADR", rawText, true, true);
         List<String> org = matchSingleVCardPrefixedField("ORG", rawText, true, true);
         List birthday;
         if ((birthday = matchSingleVCardPrefixedField("BDAY", rawText, true, false)) != null && !isLikeVCardDate((CharSequence)birthday.get(0))) {
            birthday = null;
         }

         List<String> title = matchSingleVCardPrefixedField("TITLE", rawText, true, false);
         List<List<String>> urls = matchVCardPrefixedField("URL", rawText, true, false);
         List<String> instantMessenger = matchSingleVCardPrefixedField("IMPP", rawText, true, false);
         List geoString;
         String[] var10000 = (geoString = matchSingleVCardPrefixedField("GEO", rawText, true, false)) == null ? null : SEMICOLON_OR_COMMA.split((CharSequence)geoString.get(0));
         String[] geo = var10000;
         if (var10000 != null && geo.length != 2) {
            geo = null;
         }

         return new AddressBookParsedResult(toPrimaryValues(names), nicknames, (String)null, toPrimaryValues(phoneNumbers), toTypes(phoneNumbers), toPrimaryValues(emails), toTypes(emails), toPrimaryValue(instantMessenger), toPrimaryValue(note), toPrimaryValues(addresses), toTypes(addresses), toPrimaryValue(org), toPrimaryValue(birthday), toPrimaryValue(title), toPrimaryValues(urls), geo);
      } else {
         return null;
      }
   }

   static List<List<String>> matchVCardPrefixedField(CharSequence prefix, String rawText, boolean trim, boolean parseFieldDivider) {
      List<List<String>> matches = null;
      int i = 0;
      int max = rawText.length();

      while(i < max) {
         Matcher matcher = Pattern.compile("(?:^|\n)" + prefix + "(?:;([^:]*))?:", 2).matcher(rawText);
         if (i > 0) {
            --i;
         }

         if (!matcher.find(i)) {
            break;
         }

         i = matcher.end(0);
         String metadataString = matcher.group(1);
         List<String> metadata = null;
         boolean quotedPrintable = false;
         String quotedPrintableCharset = null;
         if (metadataString != null) {
            String[] var12;
            int var13 = (var12 = SEMICOLON.split(metadataString)).length;

            for(int var14 = 0; var14 < var13; ++var14) {
               String metadatum = var12[var14];
               if (metadata == null) {
                  metadata = new ArrayList(1);
               }

               metadata.add(metadatum);
               String[] metadatumTokens;
               if ((metadatumTokens = EQUALS.split(metadatum, 2)).length > 1) {
                  String key = metadatumTokens[0];
                  String value = metadatumTokens[1];
                  if ("ENCODING".equalsIgnoreCase(key) && "QUOTED-PRINTABLE".equalsIgnoreCase(value)) {
                     quotedPrintable = true;
                  } else if ("CHARSET".equalsIgnoreCase(key)) {
                     quotedPrintableCharset = value;
                  }
               }
            }
         }

         int matchStart = i;

         while((i = rawText.indexOf(10, i)) >= 0) {
            if (i >= rawText.length() - 1 || rawText.charAt(i + 1) != ' ' && rawText.charAt(i + 1) != '\t') {
               if (!quotedPrintable || (i <= 0 || rawText.charAt(i - 1) != '=') && (i < 2 || rawText.charAt(i - 2) != '=')) {
                  break;
               }

               ++i;
            } else {
               i += 2;
            }
         }

         if (i < 0) {
            i = max;
         } else if (i > matchStart) {
            if (matches == null) {
               matches = new ArrayList(1);
            }

            if (i > 0 && rawText.charAt(i - 1) == '\r') {
               --i;
            }

            String element = rawText.substring(matchStart, i);
            if (trim) {
               element = element.trim();
            }

            if (quotedPrintable) {
               element = decodeQuotedPrintable(element, quotedPrintableCharset);
               if (parseFieldDivider) {
                  element = UNESCAPED_SEMICOLONS.matcher(element).replaceAll("\n").trim();
               }
            } else {
               if (parseFieldDivider) {
                  element = UNESCAPED_SEMICOLONS.matcher(element).replaceAll("\n").trim();
               }

               element = CR_LF_SPACE_TAB.matcher(element).replaceAll("");
               element = NEWLINE_ESCAPE.matcher(element).replaceAll("\n");
               element = VCARD_ESCAPES.matcher(element).replaceAll("$1");
            }

            if (metadata == null) {
               ArrayList match;
               (match = new ArrayList(1)).add(element);
               matches.add(match);
            } else {
               metadata.add(0, element);
               matches.add(metadata);
            }

            ++i;
         } else {
            ++i;
         }
      }

      return matches;
   }

   private static String decodeQuotedPrintable(CharSequence value, String charset) {
      int length = value.length();
      StringBuilder result = new StringBuilder(length);
      ByteArrayOutputStream fragmentBuffer = new ByteArrayOutputStream();

      for(int i = 0; i < length; ++i) {
         char c;
         switch (c = value.charAt(i)) {
            case '\n':
            case '\r':
               break;
            case '=':
               char nextChar;
               if (i < length - 2 && (nextChar = value.charAt(i + 1)) != '\r' && nextChar != '\n') {
                  char nextNextChar = value.charAt(i + 2);
                  int firstDigit = parseHexDigit(nextChar);
                  int secondDigit = parseHexDigit(nextNextChar);
                  if (firstDigit >= 0 && secondDigit >= 0) {
                     fragmentBuffer.write((firstDigit << 4) + secondDigit);
                  }

                  i += 2;
               }
               break;
            default:
               maybeAppendFragment(fragmentBuffer, charset, result);
               result.append(c);
         }
      }

      maybeAppendFragment(fragmentBuffer, charset, result);
      return result.toString();
   }

   private static void maybeAppendFragment(ByteArrayOutputStream fragmentBuffer, String charset, StringBuilder result) {
      if (fragmentBuffer.size() > 0) {
         byte[] fragmentBytes = fragmentBuffer.toByteArray();
         String fragment;
         if (charset == null) {
            fragment = new String(fragmentBytes, Charset.forName("UTF-8"));
         } else {
            try {
               fragment = new String(fragmentBytes, charset);
            } catch (UnsupportedEncodingException var5) {
               fragment = new String(fragmentBytes, Charset.forName("UTF-8"));
            }
         }

         fragmentBuffer.reset();
         result.append(fragment);
      }

   }

   static List<String> matchSingleVCardPrefixedField(CharSequence prefix, String rawText, boolean trim, boolean parseFieldDivider) {
      List values;
      return (values = matchVCardPrefixedField(prefix, rawText, trim, parseFieldDivider)) != null && !values.isEmpty() ? (List)values.get(0) : null;
   }

   private static String toPrimaryValue(List<String> list) {
      return list != null && !list.isEmpty() ? (String)list.get(0) : null;
   }

   private static String[] toPrimaryValues(Collection<List<String>> lists) {
      if (lists != null && !lists.isEmpty()) {
         List<String> result = new ArrayList(lists.size());
         Iterator var2 = lists.iterator();

         while(var2.hasNext()) {
            String value;
            if ((value = (String)((List)var2.next()).get(0)) != null && !value.isEmpty()) {
               result.add(value);
            }
         }

         return (String[])result.toArray(new String[lists.size()]);
      } else {
         return null;
      }
   }

   private static String[] toTypes(Collection<List<String>> lists) {
      if (lists != null && !lists.isEmpty()) {
         List<String> result = new ArrayList(lists.size());

         String type;
         for(Iterator var2 = lists.iterator(); var2.hasNext(); result.add(type)) {
            List<String> list = (List)var2.next();
            type = null;

            for(int i = 1; i < list.size(); ++i) {
               String metadatum;
               int equals;
               if ((equals = (metadatum = (String)list.get(i)).indexOf(61)) < 0) {
                  type = metadatum;
                  break;
               }

               if ("TYPE".equalsIgnoreCase(metadatum.substring(0, equals))) {
                  type = metadatum.substring(equals + 1);
                  break;
               }
            }
         }

         return (String[])result.toArray(new String[lists.size()]);
      } else {
         return null;
      }
   }

   private static boolean isLikeVCardDate(CharSequence value) {
      return value == null || VCARD_LIKE_DATE.matcher(value).matches();
   }

   private static void formatNames(Iterable<List<String>> names) {
      if (names != null) {
         Iterator var1 = names.iterator();

         while(var1.hasNext()) {
            List list;
            String name = (String)(list = (List)var1.next()).get(0);
            String[] components = new String[5];
            int start = 0;

            int end;
            int componentIndex;
            for(componentIndex = 0; componentIndex < 4 && (end = name.indexOf(59, start)) >= 0; start = end + 1) {
               components[componentIndex] = name.substring(start, end);
               ++componentIndex;
            }

            components[componentIndex] = name.substring(start);
            StringBuilder newName = new StringBuilder(100);
            maybeAppendComponent(components, 3, newName);
            maybeAppendComponent(components, 1, newName);
            maybeAppendComponent(components, 2, newName);
            maybeAppendComponent(components, 0, newName);
            maybeAppendComponent(components, 4, newName);
            list.set(0, newName.toString().trim());
         }
      }

   }

   private static void maybeAppendComponent(String[] components, int i, StringBuilder newName) {
      if (components[i] != null && !components[i].isEmpty()) {
         if (newName.length() > 0) {
            newName.append(' ');
         }

         newName.append(components[i]);
      }

   }
}
