package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.ext.dom._ExtDomApi;
import freemarker.template.Version;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtil {
   private static final char[] ESCAPES = createEscapes();
   private static final char[] LT = new char[]{'&', 'l', 't', ';'};
   private static final char[] GT = new char[]{'&', 'g', 't', ';'};
   private static final char[] AMP = new char[]{'&', 'a', 'm', 'p', ';'};
   private static final char[] QUOT = new char[]{'&', 'q', 'u', 'o', 't', ';'};
   private static final char[] HTML_APOS = new char[]{'&', '#', '3', '9', ';'};
   private static final char[] XML_APOS = new char[]{'&', 'a', 'p', 'o', 's', ';'};
   private static final int NO_ESC = 0;
   private static final int ESC_HEXA = 1;
   private static final int ESC_BACKSLASH = 3;

   /** @deprecated */
   @Deprecated
   public static String HTMLEnc(String s) {
      return XMLEncNA(s);
   }

   public static String XMLEnc(String s) {
      return XMLOrHTMLEnc(s, true, true, XML_APOS);
   }

   public static void XMLEnc(String s, Writer out) throws IOException {
      XMLOrHTMLEnc(s, XML_APOS, out);
   }

   public static String XHTMLEnc(String s) {
      return XMLOrHTMLEnc(s, true, true, HTML_APOS);
   }

   public static void XHTMLEnc(String s, Writer out) throws IOException {
      XMLOrHTMLEnc(s, HTML_APOS, out);
   }

   private static String XMLOrHTMLEnc(String s, boolean escGT, boolean escQuot, char[] apos) {
      int ln = s.length();
      int firstEscIdx = -1;
      int lastEscIdx = 0;
      int plusOutLn = 0;

      int dst;
      for(int i = 0; i < ln; ++i) {
         dst = s.charAt(i);
         switch (dst) {
            case 34:
               if (!escQuot) {
                  continue;
               }

               plusOutLn += QUOT.length - 1;
               break;
            case 38:
               plusOutLn += AMP.length - 1;
               break;
            case 39:
               if (apos == null) {
                  continue;
               }

               plusOutLn += apos.length - 1;
               break;
            case 60:
               plusOutLn += LT.length - 1;
               break;
            case 62:
               if (escGT || maybeCDataEndGT(s, i)) {
                  plusOutLn += GT.length - 1;
                  break;
               }
            default:
               continue;
         }

         if (firstEscIdx == -1) {
            firstEscIdx = i;
         }

         lastEscIdx = i;
      }

      if (firstEscIdx == -1) {
         return s;
      } else {
         char[] esced = new char[ln + plusOutLn];
         if (firstEscIdx != 0) {
            s.getChars(0, firstEscIdx, esced, 0);
         }

         dst = firstEscIdx;

         for(int i = firstEscIdx; i <= lastEscIdx; ++i) {
            char c = s.charAt(i);
            switch (c) {
               case '"':
                  if (escQuot) {
                     dst = shortArrayCopy(QUOT, esced, dst);
                     continue;
                  }
                  break;
               case '&':
                  dst = shortArrayCopy(AMP, esced, dst);
                  continue;
               case '\'':
                  if (apos != null) {
                     dst = shortArrayCopy(apos, esced, dst);
                     continue;
                  }
                  break;
               case '<':
                  dst = shortArrayCopy(LT, esced, dst);
                  continue;
               case '>':
                  if (escGT || maybeCDataEndGT(s, i)) {
                     dst = shortArrayCopy(GT, esced, dst);
                     continue;
                  }
            }

            esced[dst++] = c;
         }

         if (lastEscIdx != ln - 1) {
            s.getChars(lastEscIdx + 1, ln, esced, dst);
         }

         return String.valueOf(esced);
      }
   }

   private static boolean maybeCDataEndGT(String s, int i) {
      if (i == 0) {
         return true;
      } else if (s.charAt(i - 1) != ']') {
         return false;
      } else {
         return i == 1 || s.charAt(i - 2) == ']';
      }
   }

   private static void XMLOrHTMLEnc(String s, char[] apos, Writer out) throws IOException {
      int writtenEnd = 0;
      int ln = s.length();

      for(int i = 0; i < ln; ++i) {
         char c = s.charAt(i);
         if (c == '<' || c == '>' || c == '&' || c == '"' || c == '\'') {
            int flushLn = i - writtenEnd;
            if (flushLn != 0) {
               out.write(s, writtenEnd, flushLn);
            }

            writtenEnd = i + 1;
            switch (c) {
               case '"':
                  out.write(QUOT);
                  break;
               case '&':
                  out.write(AMP);
                  break;
               case '<':
                  out.write(LT);
                  break;
               case '>':
                  out.write(GT);
                  break;
               default:
                  out.write(apos);
            }
         }
      }

      if (writtenEnd < ln) {
         out.write(s, writtenEnd, ln - writtenEnd);
      }

   }

   private static int shortArrayCopy(char[] src, char[] dst, int dstOffset) {
      int ln = src.length;

      for(int i = 0; i < ln; ++i) {
         dst[dstOffset++] = src[i];
      }

      return dstOffset;
   }

   public static String XMLEncNA(String s) {
      return XMLOrHTMLEnc(s, true, true, (char[])null);
   }

   public static String XMLEncQAttr(String s) {
      return XMLOrHTMLEnc(s, false, true, (char[])null);
   }

   public static String XMLEncNQG(String s) {
      return XMLOrHTMLEnc(s, false, false, (char[])null);
   }

   public static String RTFEnc(String s) {
      int ln = s.length();
      int firstEscIdx = -1;
      int lastEscIdx = 0;
      int plusOutLn = 0;

      int dst;
      for(int i = 0; i < ln; ++i) {
         dst = s.charAt(i);
         if (dst == 123 || dst == 125 || dst == 92) {
            if (firstEscIdx == -1) {
               firstEscIdx = i;
            }

            lastEscIdx = i;
            ++plusOutLn;
         }
      }

      if (firstEscIdx == -1) {
         return s;
      } else {
         char[] esced = new char[ln + plusOutLn];
         if (firstEscIdx != 0) {
            s.getChars(0, firstEscIdx, esced, 0);
         }

         dst = firstEscIdx;

         for(int i = firstEscIdx; i <= lastEscIdx; ++i) {
            char c = s.charAt(i);
            if (c == '{' || c == '}' || c == '\\') {
               esced[dst++] = '\\';
            }

            esced[dst++] = c;
         }

         if (lastEscIdx != ln - 1) {
            s.getChars(lastEscIdx + 1, ln, esced, dst);
         }

         return String.valueOf(esced);
      }
   }

   public static void RTFEnc(String s, Writer out) throws IOException {
      int writtenEnd = 0;
      int ln = s.length();

      for(int i = 0; i < ln; ++i) {
         char c = s.charAt(i);
         if (c == '{' || c == '}' || c == '\\') {
            int flushLn = i - writtenEnd;
            if (flushLn != 0) {
               out.write(s, writtenEnd, flushLn);
            }

            out.write(92);
            writtenEnd = i;
         }
      }

      if (writtenEnd < ln) {
         out.write(s, writtenEnd, ln - writtenEnd);
      }

   }

   public static String URLEnc(String s, String charset) throws UnsupportedEncodingException {
      return URLEnc(s, charset, false);
   }

   public static String URLPathEnc(String s, String charset) throws UnsupportedEncodingException {
      return URLEnc(s, charset, true);
   }

   private static String URLEnc(String s, String charset, boolean keepSlash) throws UnsupportedEncodingException {
      int ln = s.length();

      int i;
      for(i = 0; i < ln; ++i) {
         char c = s.charAt(i);
         if (!safeInURL(c, keepSlash)) {
            break;
         }
      }

      if (i == ln) {
         return s;
      } else {
         StringBuilder b = new StringBuilder(ln + ln / 3 + 2);
         b.append(s.substring(0, i));

         int encStart;
         int bc;
         int c1;
         for(encStart = i++; i < ln; ++i) {
            char c = s.charAt(i);
            if (!safeInURL(c, keepSlash)) {
               if (encStart == -1) {
                  encStart = i;
               }
            } else {
               if (encStart != -1) {
                  byte[] o = s.substring(encStart, i).getBytes(charset);

                  for(int j = 0; j < o.length; ++j) {
                     b.append('%');
                     bc = o[j];
                     c1 = bc & 15;
                     int c2 = bc >> 4 & 15;
                     b.append((char)(c2 < 10 ? c2 + 48 : c2 - 10 + 65));
                     b.append((char)(c1 < 10 ? c1 + 48 : c1 - 10 + 65));
                  }

                  encStart = -1;
               }

               b.append(c);
            }
         }

         if (encStart != -1) {
            byte[] o = s.substring(encStart, i).getBytes(charset);

            for(int j = 0; j < o.length; ++j) {
               b.append('%');
               byte bc = o[j];
               bc = bc & 15;
               c1 = bc >> 4 & 15;
               b.append((char)(c1 < 10 ? c1 + 48 : c1 - 10 + 65));
               b.append((char)(bc < 10 ? bc + 48 : bc - 10 + 65));
            }
         }

         return b.toString();
      }
   }

   private static boolean safeInURL(char c, boolean keepSlash) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.' || c == '!' || c == '~' || c >= '\'' && c <= '*' || keepSlash && c == '/';
   }

   private static char[] createEscapes() {
      char[] escapes = new char[93];

      for(int i = 0; i < 32; ++i) {
         escapes[i] = 1;
      }

      escapes[92] = '\\';
      escapes[39] = '\'';
      escapes[34] = '"';
      escapes[60] = 'l';
      escapes[62] = 'g';
      escapes[38] = 'a';
      escapes[8] = 'b';
      escapes[9] = 't';
      escapes[10] = 'n';
      escapes[12] = 'f';
      escapes[13] = 'r';
      return escapes;
   }

   public static String FTLStringLiteralEnc(String s, char quotation) {
      return FTLStringLiteralEnc(s, quotation, false);
   }

   public static String FTLStringLiteralEnc(String s) {
      return FTLStringLiteralEnc(s, '\u0000', false);
   }

   private static String FTLStringLiteralEnc(String s, char quotation, boolean addQuotation) {
      int ln = s.length();
      byte otherQuotation;
      if (quotation == 0) {
         otherQuotation = 0;
      } else if (quotation == '"') {
         otherQuotation = 39;
      } else {
         if (quotation != '\'') {
            throw new IllegalArgumentException("Unsupported quotation character: " + quotation);
         }

         otherQuotation = 34;
      }

      int escLn = ESCAPES.length;
      StringBuilder buf = null;

      for(int i = 0; i < ln; ++i) {
         char c = s.charAt(i);
         int escape;
         if (c != '=') {
            if (c < escLn) {
               escape = ESCAPES[c];
            } else if (c == '{' && i > 0 && isInterpolationStart(s.charAt(i - 1))) {
               escape = 123;
            } else {
               escape = 0;
            }
         } else {
            escape = i > 0 && s.charAt(i - 1) == '[' ? 61 : 0;
         }

         if (escape != 0 && escape != otherQuotation) {
            if (buf == null) {
               buf = new StringBuilder(s.length() + 4 + (addQuotation ? 2 : 0));
               if (addQuotation) {
                  buf.append(quotation);
               }

               buf.append(s.substring(0, i));
            }

            if (escape == 1) {
               buf.append("\\x00");
               int c2 = c >> 4 & 15;
               c = (char)(c & 15);
               buf.append((char)(c2 < 10 ? c2 + 48 : c2 - 10 + 65));
               buf.append((char)(c < '\n' ? c + 48 : c - 10 + 65));
            } else {
               buf.append('\\');
               buf.append((char)escape);
            }
         } else if (buf != null) {
            buf.append(c);
         }
      }

      if (buf == null) {
         return addQuotation ? quotation + s + quotation : s;
      } else {
         if (addQuotation) {
            buf.append(quotation);
         }

         return buf.toString();
      }
   }

   private static boolean isInterpolationStart(char c) {
      return c == '$' || c == '#';
   }

   public static String FTLStringLiteralDec(String s) throws ParseException {
      int idx = s.indexOf(92);
      if (idx == -1) {
         return s;
      } else {
         int lidx = s.length() - 1;
         int bidx = 0;
         StringBuilder buf = new StringBuilder(lidx);

         do {
            buf.append(s.substring(bidx, idx));
            if (idx >= lidx) {
               throw new ParseException("The last character of string literal is backslash", 0, 0);
            }

            char c = s.charAt(idx + 1);
            switch (c) {
               case '"':
                  buf.append('"');
                  bidx = idx + 2;
                  break;
               case '\'':
                  buf.append('\'');
                  bidx = idx + 2;
                  break;
               case '=':
               case '{':
                  buf.append(c);
                  bidx = idx + 2;
                  break;
               case '\\':
                  buf.append('\\');
                  bidx = idx + 2;
                  break;
               case 'a':
                  buf.append('&');
                  bidx = idx + 2;
                  break;
               case 'b':
                  buf.append('\b');
                  bidx = idx + 2;
                  break;
               case 'f':
                  buf.append('\f');
                  bidx = idx + 2;
                  break;
               case 'g':
                  buf.append('>');
                  bidx = idx + 2;
                  break;
               case 'l':
                  buf.append('<');
                  bidx = idx + 2;
                  break;
               case 'n':
                  buf.append('\n');
                  bidx = idx + 2;
                  break;
               case 'r':
                  buf.append('\r');
                  bidx = idx + 2;
                  break;
               case 't':
                  buf.append('\t');
                  bidx = idx + 2;
                  break;
               case 'x':
                  idx += 2;
                  int x = idx;
                  int y = 0;

                  for(int z = lidx > idx + 3 ? idx + 3 : lidx; idx <= z; ++idx) {
                     char b = s.charAt(idx);
                     if (b >= '0' && b <= '9') {
                        y <<= 4;
                        y += b - 48;
                     } else if (b >= 'a' && b <= 'f') {
                        y <<= 4;
                        y += b - 97 + 10;
                     } else {
                        if (b < 'A' || b > 'F') {
                           break;
                        }

                        y <<= 4;
                        y += b - 65 + 10;
                     }
                  }

                  if (x >= idx) {
                     throw new ParseException("Invalid \\x escape in a string literal", 0, 0);
                  }

                  buf.append((char)y);
                  bidx = idx;
                  break;
               default:
                  throw new ParseException("Invalid escape sequence (\\" + c + ") in a string literal", 0, 0);
            }

            idx = s.indexOf(92, bidx);
         } while(idx != -1);

         buf.append(s.substring(bidx));
         return buf.toString();
      }
   }

   public static Locale deduceLocale(String input) {
      if (input == null) {
         return null;
      } else {
         Locale locale = Locale.getDefault();
         if (input.length() > 0 && input.charAt(0) == '"') {
            input = input.substring(1, input.length() - 1);
         }

         StringTokenizer st = new StringTokenizer(input, ",_ ");
         String lang = "";
         String country = "";
         if (st.hasMoreTokens()) {
            lang = st.nextToken();
         }

         if (st.hasMoreTokens()) {
            country = st.nextToken();
         }

         if (!st.hasMoreTokens()) {
            locale = new Locale(lang, country);
         } else {
            locale = new Locale(lang, country, st.nextToken());
         }

         return locale;
      }
   }

   public static String capitalize(String s) {
      StringTokenizer st = new StringTokenizer(s, " \t\r\n", true);
      StringBuilder buf = new StringBuilder(s.length());

      while(st.hasMoreTokens()) {
         String tok = st.nextToken();
         buf.append(tok.substring(0, 1).toUpperCase());
         buf.append(tok.substring(1).toLowerCase());
      }

      return buf.toString();
   }

   public static boolean getYesNo(String s) {
      if (s.startsWith("\"")) {
         s = s.substring(1, s.length() - 1);
      }

      if (!s.equalsIgnoreCase("n") && !s.equalsIgnoreCase("no") && !s.equalsIgnoreCase("f") && !s.equalsIgnoreCase("false")) {
         if (!s.equalsIgnoreCase("y") && !s.equalsIgnoreCase("yes") && !s.equalsIgnoreCase("t") && !s.equalsIgnoreCase("true")) {
            throw new IllegalArgumentException("Illegal boolean value: " + s);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static String[] split(String s, char c) {
      int ln = s.length();
      int i = 0;

      int cnt;
      for(cnt = 1; (i = s.indexOf(c, i)) != -1; ++i) {
         ++cnt;
      }

      String[] res = new String[cnt];
      i = 0;

      int e;
      for(int b = 0; b <= ln; b = e + 1) {
         e = s.indexOf(c, b);
         if (e == -1) {
            e = ln;
         }

         res[i++] = s.substring(b, e);
      }

      return res;
   }

   public static String[] split(String s, String sep, boolean caseInsensitive) {
      int sepLn = sep.length();
      String convertedS = caseInsensitive ? s.toLowerCase() : s;
      int sLn = s.length();
      if (sepLn == 0) {
         String[] res = new String[sLn];

         for(int i = 0; i < sLn; ++i) {
            res[i] = String.valueOf(s.charAt(i));
         }

         return res;
      } else {
         String splitString = caseInsensitive ? sep.toLowerCase() : sep;
         int next = 0;

         int next;
         for(next = 1; (next = convertedS.indexOf(splitString, next)) != -1; next += sepLn) {
            ++next;
         }

         String[] res = new String[next];
         next = 0;

         int end;
         for(next = 0; next <= sLn; next = end + sepLn) {
            end = convertedS.indexOf(splitString, next);
            if (end == -1) {
               end = sLn;
            }

            res[next++] = s.substring(next, end);
         }

         return res;
      }
   }

   public static String replace(String text, String oldSub, String newSub) {
      return replace(text, oldSub, newSub, false, false);
   }

   public static String replace(String text, String oldsub, String newsub, boolean caseInsensitive, boolean firstOnly) {
      int oln = oldsub.length();
      StringBuilder buf;
      int tln;
      int i;
      if (oln == 0) {
         int nln = newsub.length();
         if (nln == 0) {
            return text;
         } else if (firstOnly) {
            return newsub + text;
         } else {
            tln = text.length();
            buf = new StringBuilder(tln + (tln + 1) * nln);
            buf.append(newsub);

            for(i = 0; i < tln; ++i) {
               buf.append(text.charAt(i));
               buf.append(newsub);
            }

            return buf.toString();
         }
      } else {
         oldsub = caseInsensitive ? oldsub.toLowerCase() : oldsub;
         String input = caseInsensitive ? text.toLowerCase() : text;
         i = input.indexOf(oldsub);
         if (i == -1) {
            return text;
         } else {
            int b = 0;
            tln = text.length();
            buf = new StringBuilder(tln + Math.max(newsub.length() - oln, 0) * 3);

            do {
               buf.append(text.substring(b, i));
               buf.append(newsub);
               b = i + oln;
               i = input.indexOf(oldsub, b);
            } while(i != -1 && !firstOnly);

            buf.append(text.substring(b));
            return buf.toString();
         }
      }
   }

   public static String chomp(String s) {
      if (s.endsWith("\r\n")) {
         return s.substring(0, s.length() - 2);
      } else {
         return !s.endsWith("\r") && !s.endsWith("\n") ? s : s.substring(0, s.length() - 1);
      }
   }

   public static String emptyToNull(String s) {
      if (s == null) {
         return null;
      } else {
         return s.length() == 0 ? null : s;
      }
   }

   public static String jQuote(Object obj) {
      return jQuote(obj != null ? obj.toString() : null);
   }

   public static String jQuote(String s) {
      if (s == null) {
         return "null";
      } else {
         int ln = s.length();
         StringBuilder b = new StringBuilder(ln + 4);
         b.append('"');

         for(int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c == '"') {
               b.append("\\\"");
            } else if (c == '\\') {
               b.append("\\\\");
            } else if (c < ' ') {
               if (c == '\n') {
                  b.append("\\n");
               } else if (c == '\r') {
                  b.append("\\r");
               } else if (c == '\f') {
                  b.append("\\f");
               } else if (c == '\b') {
                  b.append("\\b");
               } else if (c == '\t') {
                  b.append("\\t");
               } else {
                  b.append("\\u00");
                  int x = c / 16;
                  b.append(toHexDigit(x));
                  x = c & 15;
                  b.append(toHexDigit(x));
               }
            } else {
               b.append(c);
            }
         }

         b.append('"');
         return b.toString();
      }
   }

   public static String jQuoteNoXSS(Object obj) {
      return jQuoteNoXSS(obj != null ? obj.toString() : null);
   }

   public static String jQuoteNoXSS(String s) {
      if (s == null) {
         return "null";
      } else {
         int ln = s.length();
         StringBuilder b = new StringBuilder(ln + 4);
         b.append('"');

         for(int i = 0; i < ln; ++i) {
            char c = s.charAt(i);
            if (c == '"') {
               b.append("\\\"");
            } else if (c == '\\') {
               b.append("\\\\");
            } else if (c == '<') {
               b.append("\\u003C");
            } else if (c < ' ') {
               if (c == '\n') {
                  b.append("\\n");
               } else if (c == '\r') {
                  b.append("\\r");
               } else if (c == '\f') {
                  b.append("\\f");
               } else if (c == '\b') {
                  b.append("\\b");
               } else if (c == '\t') {
                  b.append("\\t");
               } else {
                  b.append("\\u00");
                  int x = c / 16;
                  b.append(toHexDigit(x));
                  x = c & 15;
                  b.append(toHexDigit(x));
               }
            } else {
               b.append(c);
            }
         }

         b.append('"');
         return b.toString();
      }
   }

   public static String ftlQuote(String s) {
      char quotation;
      if (s.indexOf(34) != -1 && s.indexOf(39) == -1) {
         quotation = '\'';
      } else {
         quotation = '"';
      }

      return FTLStringLiteralEnc(s, quotation, true);
   }

   public static boolean isFTLIdentifierStart(char c) {
      if (c < 170) {
         if ((c < 'a' || c > 'z') && (c < '@' || c > 'Z')) {
            return c == '$' || c == '_';
         } else {
            return true;
         }
      } else if (c < 'ꟸ') {
         if (c < 11631) {
            if (c < 8488) {
               if (c < 8336) {
                  if (c < 216) {
                     if (c < 186) {
                        return c == 170 || c == 181;
                     } else {
                        return c == 186 || c >= 192 && c <= 214;
                     }
                  } else if (c < 8305) {
                     return c >= 216 && c <= 246 || c >= 248 && c <= 8191;
                  } else {
                     return c == 8305 || c == 8319;
                  }
               } else if (c < 8469) {
                  if (c < 8455) {
                     return c >= 8336 && c <= 8348 || c == 8450;
                  } else {
                     return c == 8455 || c >= 8458 && c <= 8467;
                  }
               } else if (c < 8484) {
                  return c == 8469 || c >= 8473 && c <= 8477;
               } else {
                  return c == 8484 || c == 8486;
               }
            } else if (c < 11312) {
               if (c < 8517) {
                  if (c < 8495) {
                     return c == 8488 || c >= 8490 && c <= 8493;
                  } else {
                     return c >= 8495 && c <= 8505 || c >= 8508 && c <= 8511;
                  }
               } else if (c < 8579) {
                  return c >= 8517 && c <= 8521 || c == 8526;
               } else {
                  return c >= 8579 && c <= 8580 || c >= 11264 && c <= 11310;
               }
            } else if (c < 11520) {
               if (c < 11499) {
                  return c >= 11312 && c <= 11358 || c >= 11360 && c <= 11492;
               } else {
                  return c >= 11499 && c <= 11502 || c >= 11506 && c <= 11507;
               }
            } else if (c < 11565) {
               return c >= 11520 && c <= 11557 || c == 11559;
            } else {
               return c == 11565 || c >= 11568 && c <= 11623;
            }
         } else if (c < 12784) {
            if (c < 11728) {
               if (c < 11696) {
                  if (c < 11680) {
                     return c == 11631 || c >= 11648 && c <= 11670;
                  } else {
                     return c >= 11680 && c <= 11686 || c >= 11688 && c <= 11694;
                  }
               } else if (c < 11712) {
                  return c >= 11696 && c <= 11702 || c >= 11704 && c <= 11710;
               } else {
                  return c >= 11712 && c <= 11718 || c >= 11720 && c <= 11726;
               }
            } else if (c < 12337) {
               if (c < 11823) {
                  return c >= 11728 && c <= 11734 || c >= 11736 && c <= 11742;
               } else {
                  return c == 11823 || c >= 12293 && c <= 12294;
               }
            } else if (c < 12352) {
               return c >= 12337 && c <= 12341 || c >= 12347 && c <= 12348;
            } else {
               return c >= 12352 && c <= 12687 || c >= 12704 && c <= 12730;
            }
         } else if (c < 'ꙿ') {
            if (c < 'ꓐ') {
               if (c < 13312) {
                  return c >= 12784 && c <= 12799 || c >= 13056 && c <= 13183;
               } else {
                  return c >= 13312 && c <= 19893 || c >= 19968 && c <= 'ꒌ';
               }
            } else if (c < 'ꘐ') {
               return c >= 'ꓐ' && c <= 'ꓽ' || c >= 'ꔀ' && c <= 'ꘌ';
            } else {
               return c >= 'ꘐ' && c <= 'ꘫ' || c >= 'Ꙁ' && c <= 'ꙮ';
            }
         } else if (c < 'Ꞌ') {
            if (c < 'ꜗ') {
               return c >= 'ꙿ' && c <= 'ꚗ' || c >= 'ꚠ' && c <= 'ꛥ';
            } else {
               return c >= 'ꜗ' && c <= 'ꜟ' || c >= 'Ꜣ' && c <= 'ꞈ';
            }
         } else if (c < 'Ꞡ') {
            return c >= 'Ꞌ' && c <= 'ꞎ' || c >= 'Ꞑ' && c <= 'ꞓ';
         } else {
            return c >= 'Ꞡ' && c <= 'Ɦ';
         }
      } else if (c < 'ꬠ') {
         if (c < 'ꩄ') {
            if (c < 'ꣻ') {
               if (c < 'ꡀ') {
                  if (c < 'ꠇ') {
                     return c >= 'ꟸ' && c <= 'ꠁ' || c >= 'ꠃ' && c <= 'ꠅ';
                  } else {
                     return c >= 'ꠇ' && c <= 'ꠊ' || c >= 'ꠌ' && c <= 'ꠢ';
                  }
               } else if (c < '꣐') {
                  return c >= 'ꡀ' && c <= 'ꡳ' || c >= 'ꢂ' && c <= 'ꢳ';
               } else {
                  return c >= '꣐' && c <= '꣙' || c >= 'ꣲ' && c <= 'ꣷ';
               }
            } else if (c < 'ꦄ') {
               if (c < 'ꤰ') {
                  return c == 'ꣻ' || c >= '꤀' && c <= 'ꤥ';
               } else {
                  return c >= 'ꤰ' && c <= 'ꥆ' || c >= 'ꥠ' && c <= 'ꥼ';
               }
            } else if (c < 'ꨀ') {
               return c >= 'ꦄ' && c <= 'ꦲ' || c >= 'ꧏ' && c <= '꧙';
            } else {
               return c >= 'ꨀ' && c <= 'ꨨ' || c >= 'ꩀ' && c <= 'ꩂ';
            }
         } else if (c < 'ꫀ') {
            if (c < 'ꪀ') {
               if (c < 'ꩠ') {
                  return c >= 'ꩄ' && c <= 'ꩋ' || c >= '꩐' && c <= '꩙';
               } else {
                  return c >= 'ꩠ' && c <= 'ꩶ' || c == 'ꩺ';
               }
            } else if (c < 'ꪵ') {
               return c >= 'ꪀ' && c <= 'ꪯ' || c == 'ꪱ';
            } else {
               return c >= 'ꪵ' && c <= 'ꪶ' || c >= 'ꪹ' && c <= 'ꪽ';
            }
         } else if (c < 'ꫲ') {
            if (c < 'ꫛ') {
               return c == 'ꫀ' || c == 'ꫂ';
            } else {
               return c >= 'ꫛ' && c <= 'ꫝ' || c >= 'ꫠ' && c <= 'ꫪ';
            }
         } else if (c < 'ꬉ') {
            return c >= 'ꫲ' && c <= 'ꫴ' || c >= 'ꬁ' && c <= 'ꬆ';
         } else {
            return c >= 'ꬉ' && c <= 'ꬎ' || c >= 'ꬑ' && c <= 'ꬖ';
         }
      } else if (c < 'צּ') {
         if (c < 'ﬓ') {
            if (c < '가') {
               if (c < 'ꯀ') {
                  return c >= 'ꬠ' && c <= 'ꬦ' || c >= 'ꬨ' && c <= 'ꬮ';
               } else {
                  return c >= 'ꯀ' && c <= 'ꯢ' || c >= '꯰' && c <= '꯹';
               }
            } else if (c < 'ퟋ') {
               return c >= '가' && c <= '힣' || c >= 'ힰ' && c <= 'ퟆ';
            } else {
               return c >= 'ퟋ' && c <= 'ퟻ' || c >= '豈' && c <= 'ﬆ';
            }
         } else if (c < 'טּ') {
            if (c < 'ײַ') {
               return c >= 'ﬓ' && c <= 'ﬗ' || c == 'יִ';
            } else {
               return c >= 'ײַ' && c <= 'ﬨ' || c >= 'שׁ' && c <= 'זּ';
            }
         } else if (c < 'נּ') {
            return c >= 'טּ' && c <= 'לּ' || c == 'מּ';
         } else {
            return c >= 'נּ' && c <= 'סּ' || c >= 'ףּ' && c <= 'פּ';
         }
      } else if (c < 'Ａ') {
         if (c < 'ﷰ') {
            if (c < 'ﵐ') {
               return c >= 'צּ' && c <= 'ﮱ' || c >= 'ﯓ' && c <= 'ﴽ';
            } else {
               return c >= 'ﵐ' && c <= 'ﶏ' || c >= 'ﶒ' && c <= 'ﷇ';
            }
         } else if (c < 'ﹶ') {
            return c >= 'ﷰ' && c <= 'ﷻ' || c >= 'ﹰ' && c <= 'ﹴ';
         } else {
            return c >= 'ﹶ' && c <= 'ﻼ' || c >= '０' && c <= '９';
         }
      } else if (c < 'ￊ') {
         if (c < 'ｦ') {
            return c >= 'Ａ' && c <= 'Ｚ' || c >= 'ａ' && c <= 'ｚ';
         } else {
            return c >= 'ｦ' && c <= 'ﾾ' || c >= 'ￂ' && c <= 'ￇ';
         }
      } else if (c < 'ￚ') {
         return c >= 'ￊ' && c <= 'ￏ' || c >= 'ￒ' && c <= 'ￗ';
      } else {
         return c >= 'ￚ' && c <= 'ￜ';
      }
   }

   public static boolean isFTLIdentifierPart(char c) {
      return isFTLIdentifierStart(c) || c >= '0' && c <= '9';
   }

   public static boolean isBackslashEscapedFTLIdentifierCharacter(char c) {
      return c == '-' || c == '.' || c == ':' || c == '#';
   }

   public static String javaStringEnc(String s) {
      int ln = s.length();

      for(int i = 0; i < ln; ++i) {
         char c = s.charAt(i);
         if (c == '"' || c == '\\' || c < ' ') {
            StringBuilder b = new StringBuilder(ln + 4);
            b.append(s.substring(0, i));

            while(true) {
               if (c == '"') {
                  b.append("\\\"");
               } else if (c == '\\') {
                  b.append("\\\\");
               } else if (c < ' ') {
                  if (c == '\n') {
                     b.append("\\n");
                  } else if (c == '\r') {
                     b.append("\\r");
                  } else if (c == '\f') {
                     b.append("\\f");
                  } else if (c == '\b') {
                     b.append("\\b");
                  } else if (c == '\t') {
                     b.append("\\t");
                  } else {
                     b.append("\\u00");
                     int x = c / 16;
                     b.append((char)(x < 10 ? x + 48 : x - 10 + 97));
                     x = c & 15;
                     b.append((char)(x < 10 ? x + 48 : x - 10 + 97));
                  }
               } else {
                  b.append(c);
               }

               ++i;
               if (i >= ln) {
                  return b.toString();
               }

               c = s.charAt(i);
            }
         }
      }

      return s;
   }

   public static String javaScriptStringEnc(String s) {
      return jsStringEnc(s, false);
   }

   public static String jsonStringEnc(String s) {
      return jsStringEnc(s, true);
   }

   public static String jsStringEnc(String s, boolean json) {
      NullArgumentException.check("s", s);
      int ln = s.length();
      StringBuilder sb = null;

      for(int i = 0; i < ln; ++i) {
         char c = s.charAt(i);
         if ((c <= '>' || c >= 127 || c == '\\') && c != ' ' && (c < 160 || c >= 8232)) {
            int escapeType;
            if (c <= 31) {
               if (c == '\n') {
                  escapeType = 110;
               } else if (c == '\r') {
                  escapeType = 114;
               } else if (c == '\f') {
                  escapeType = 102;
               } else if (c == '\b') {
                  escapeType = 98;
               } else if (c == '\t') {
                  escapeType = 116;
               } else {
                  escapeType = 1;
               }
            } else if (c == '"') {
               escapeType = 3;
            } else if (c == '\'') {
               escapeType = json ? 0 : 3;
            } else if (c == '\\') {
               escapeType = 3;
            } else if (c != '/' || i != 0 && s.charAt(i - 1) != '<') {
               boolean dangerous;
               char prevC;
               if (c == '>') {
                  if (i == 0) {
                     dangerous = true;
                  } else {
                     prevC = s.charAt(i - 1);
                     if (prevC != ']' && prevC != '-') {
                        dangerous = false;
                     } else if (i == 1) {
                        dangerous = true;
                     } else {
                        char prevPrevC = s.charAt(i - 2);
                        dangerous = prevPrevC == prevC;
                     }
                  }

                  escapeType = dangerous ? (json ? 1 : 3) : 0;
               } else if (c == '<') {
                  if (i == ln - 1) {
                     dangerous = true;
                  } else {
                     prevC = s.charAt(i + 1);
                     dangerous = prevC == '!' || prevC == '?';
                  }

                  escapeType = dangerous ? 1 : 0;
               } else if ((c < 127 || c > 159) && c != 8232 && c != 8233) {
                  escapeType = 0;
               } else {
                  escapeType = 1;
               }
            } else {
               escapeType = 3;
            }

            if (escapeType != 0) {
               if (sb == null) {
                  sb = new StringBuilder(ln + 6);
                  sb.append(s.substring(0, i));
               }

               sb.append('\\');
               if (escapeType > 32) {
                  sb.append((char)escapeType);
                  continue;
               }

               if (escapeType == 1) {
                  if (!json && c < 256) {
                     sb.append('x');
                     sb.append(toHexDigit(c >> 4));
                     sb.append(toHexDigit(c & 15));
                     continue;
                  }

                  sb.append('u');
                  sb.append(toHexDigit(c >> 12 & 15));
                  sb.append(toHexDigit(c >> 8 & 15));
                  sb.append(toHexDigit(c >> 4 & 15));
                  sb.append(toHexDigit(c & 15));
                  continue;
               }

               sb.append(c);
               continue;
            }
         }

         if (sb != null) {
            sb.append(c);
         }
      }

      return sb == null ? s : sb.toString();
   }

   private static char toHexDigit(int d) {
      return (char)(d < 10 ? d + 48 : d - 10 + 65);
   }

   public static Map parseNameValuePairList(String s, String defaultValue) throws java.text.ParseException {
      Map map = new HashMap();
      char c = ' ';
      int ln = s.length();
      int p = 0;

      int keyStart;
      String key;
      String value;
      do {
         while(p < ln) {
            c = s.charAt(p);
            if (!Character.isWhitespace(c)) {
               break;
            }

            ++p;
         }

         if (p == ln) {
            return map;
         }

         for(keyStart = p; p < ln; ++p) {
            c = s.charAt(p);
            if (!Character.isLetterOrDigit(c) && c != '_') {
               break;
            }
         }

         if (keyStart == p) {
            throw new java.text.ParseException("Expecting letter, digit or \"_\" here, (the first character of the key) but found " + jQuote(String.valueOf(c)) + " at position " + p + ".", p);
         }

         for(key = s.substring(keyStart, p); p < ln; ++p) {
            c = s.charAt(p);
            if (!Character.isWhitespace(c)) {
               break;
            }
         }

         if (p == ln) {
            if (defaultValue == null) {
               throw new java.text.ParseException("Expecting \":\", but reached the end of the string  at position " + p + ".", p);
            }

            value = defaultValue;
         } else if (c != ':') {
            if (defaultValue == null || c != ',') {
               throw new java.text.ParseException("Expecting \":\" here, but found " + jQuote(String.valueOf(c)) + " at position " + p + ".", p);
            }

            ++p;
            value = defaultValue;
         } else {
            ++p;

            while(p < ln) {
               c = s.charAt(p);
               if (!Character.isWhitespace(c)) {
                  break;
               }

               ++p;
            }

            if (p == ln) {
               throw new java.text.ParseException("Expecting the value of the key here, but reached the end of the string  at position " + p + ".", p);
            }

            int valueStart;
            for(valueStart = p; p < ln; ++p) {
               c = s.charAt(p);
               if (!Character.isLetterOrDigit(c) && c != '_') {
                  break;
               }
            }

            if (valueStart == p) {
               throw new java.text.ParseException("Expecting letter, digit or \"_\" here, (the first character of the value) but found " + jQuote(String.valueOf(c)) + " at position " + p + ".", p);
            }

            for(value = s.substring(valueStart, p); p < ln; ++p) {
               c = s.charAt(p);
               if (!Character.isWhitespace(c)) {
                  break;
               }
            }

            if (p < ln) {
               if (c != ',') {
                  throw new java.text.ParseException("Excpecting \",\" or the end of the string here, but found " + jQuote(String.valueOf(c)) + " at position " + p + ".", p);
               }

               ++p;
            }
         }
      } while(map.put(key, value) == null);

      throw new java.text.ParseException("Dublicated key: " + jQuote(key), keyStart);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isXMLID(String name) {
      return _ExtDomApi.isXMLNameLike(name);
   }

   public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
      return _ExtDomApi.matchesName(qname, nodeName, nsURI, env);
   }

   public static String leftPad(String s, int minLength) {
      return leftPad(s, minLength, ' ');
   }

   public static String leftPad(String s, int minLength, char filling) {
      int ln = s.length();
      if (minLength <= ln) {
         return s;
      } else {
         StringBuilder res = new StringBuilder(minLength);
         int dif = minLength - ln;

         for(int i = 0; i < dif; ++i) {
            res.append(filling);
         }

         res.append(s);
         return res.toString();
      }
   }

   public static String leftPad(String s, int minLength, String filling) {
      int ln = s.length();
      if (minLength <= ln) {
         return s;
      } else {
         StringBuilder res = new StringBuilder(minLength);
         int dif = minLength - ln;
         int fln = filling.length();
         if (fln == 0) {
            throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
         } else {
            int cnt = dif / fln;

            int i;
            for(i = 0; i < cnt; ++i) {
               res.append(filling);
            }

            cnt = dif % fln;

            for(i = 0; i < cnt; ++i) {
               res.append(filling.charAt(i));
            }

            res.append(s);
            return res.toString();
         }
      }
   }

   public static String rightPad(String s, int minLength) {
      return rightPad(s, minLength, ' ');
   }

   public static String rightPad(String s, int minLength, char filling) {
      int ln = s.length();
      if (minLength <= ln) {
         return s;
      } else {
         StringBuilder res = new StringBuilder(minLength);
         res.append(s);
         int dif = minLength - ln;

         for(int i = 0; i < dif; ++i) {
            res.append(filling);
         }

         return res.toString();
      }
   }

   public static String rightPad(String s, int minLength, String filling) {
      int ln = s.length();
      if (minLength <= ln) {
         return s;
      } else {
         StringBuilder res = new StringBuilder(minLength);
         res.append(s);
         int dif = minLength - ln;
         int fln = filling.length();
         if (fln == 0) {
            throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
         } else {
            int start = ln % fln;
            int end = fln - start <= dif ? fln : start + dif;

            int cnt;
            for(cnt = start; cnt < end; ++cnt) {
               res.append(filling.charAt(cnt));
            }

            dif -= end - start;
            cnt = dif / fln;

            int i;
            for(i = 0; i < cnt; ++i) {
               res.append(filling);
            }

            cnt = dif % fln;

            for(i = 0; i < cnt; ++i) {
               res.append(filling.charAt(i));
            }

            return res.toString();
         }
      }
   }

   public static int versionStringToInt(String version) {
      return (new Version(version)).intValue();
   }

   public static String tryToString(Object object) {
      if (object == null) {
         return null;
      } else {
         try {
            return object.toString();
         } catch (Throwable var2) {
            return failedToStringSubstitute(object, var2);
         }
      }
   }

   private static String failedToStringSubstitute(Object object, Throwable e) {
      String eStr;
      try {
         eStr = e.toString();
      } catch (Throwable var4) {
         eStr = ClassUtil.getShortClassNameOfObject(e);
      }

      return "[" + ClassUtil.getShortClassNameOfObject(object) + ".toString() failed: " + eStr + "]";
   }

   public static String toUpperABC(int n) {
      return toABC(n, 'A');
   }

   public static String toLowerABC(int n) {
      return toABC(n, 'a');
   }

   private static String toABC(int n, char oneDigit) {
      if (n < 1) {
         throw new IllegalArgumentException("Can't convert 0 or negative numbers to latin-number: " + n);
      } else {
         int reached = 1;
         int weight = 1;

         while(true) {
            int nextWeight = weight * 26;
            int digitIncrease = reached + nextWeight;
            if (digitIncrease > n) {
               StringBuilder sb;
               for(sb = new StringBuilder(); weight != 0; weight /= 26) {
                  digitIncrease = (n - reached) / weight;
                  sb.append((char)(oneDigit + digitIncrease));
                  reached += digitIncrease * weight;
               }

               return sb.toString();
            }

            weight = nextWeight;
            reached = digitIncrease;
         }
      }
   }

   public static char[] trim(char[] cs) {
      if (cs.length == 0) {
         return cs;
      } else {
         int start = 0;

         int end;
         for(end = cs.length; start < end && cs[start] <= ' '; ++start) {
         }

         while(start < end && cs[end - 1] <= ' ') {
            --end;
         }

         if (start == 0 && end == cs.length) {
            return cs;
         } else if (start == end) {
            return CollectionUtils.EMPTY_CHAR_ARRAY;
         } else {
            char[] newCs = new char[end - start];
            System.arraycopy(cs, start, newCs, 0, end - start);
            return newCs;
         }
      }
   }

   public static boolean isTrimmableToEmpty(char[] text) {
      return isTrimmableToEmpty(text, 0, text.length);
   }

   public static boolean isTrimmableToEmpty(char[] text, int start) {
      return isTrimmableToEmpty(text, start, text.length);
   }

   public static boolean isTrimmableToEmpty(char[] text, int start, int end) {
      for(int i = start; i < end; ++i) {
         if (text[i] > ' ') {
            return false;
         }
      }

      return true;
   }

   public static Pattern globToRegularExpression(String glob) {
      return globToRegularExpression(glob, false);
   }

   public static Pattern globToRegularExpression(String glob, boolean caseInsensitive) {
      StringBuilder regex = new StringBuilder();
      int nextStart = 0;
      boolean escaped = false;
      int ln = glob.length();

      for(int idx = 0; idx < ln; ++idx) {
         char c = glob.charAt(idx);
         if (escaped) {
            escaped = false;
         } else if (c == '?') {
            appendLiteralGlobSection(regex, glob, nextStart, idx);
            regex.append("[^/]");
            nextStart = idx + 1;
         } else if (c != '*') {
            if (c == '\\') {
               escaped = true;
            } else if (c == '[' || c == '{') {
               throw new IllegalArgumentException("The \"" + c + "\" glob operator is currently unsupported (precede it with \\ for literal matching), in this glob: " + glob);
            }
         } else {
            appendLiteralGlobSection(regex, glob, nextStart, idx);
            if (idx + 1 < ln && glob.charAt(idx + 1) == '*') {
               if (idx != 0 && glob.charAt(idx - 1) != '/') {
                  throw new IllegalArgumentException("The \"**\" wildcard must be directly after a \"/\" or it must be at the beginning, in this glob: " + glob);
               }

               if (idx + 2 == ln) {
                  regex.append(".*");
                  ++idx;
               } else {
                  if (idx + 2 >= ln || glob.charAt(idx + 2) != '/') {
                     throw new IllegalArgumentException("The \"**\" wildcard must be followed by \"/\", or must be at tehe end, in this glob: " + glob);
                  }

                  regex.append("(.*?/)*");
                  idx += 2;
               }
            } else {
               regex.append("[^/]*");
            }

            nextStart = idx + 1;
         }
      }

      appendLiteralGlobSection(regex, glob, nextStart, glob.length());
      return Pattern.compile(regex.toString(), caseInsensitive ? 66 : 0);
   }

   private static void appendLiteralGlobSection(StringBuilder regex, String glob, int start, int end) {
      if (start != end) {
         String part = unescapeLiteralGlobSection(glob.substring(start, end));
         regex.append(Pattern.quote(part));
      }
   }

   private static String unescapeLiteralGlobSection(String s) {
      int backslashIdx = s.indexOf(92);
      if (backslashIdx == -1) {
         return s;
      } else {
         int ln = s.length();
         StringBuilder sb = new StringBuilder(ln - 1);
         int nextStart = 0;

         do {
            sb.append(s, nextStart, backslashIdx);
            nextStart = backslashIdx + 1;
         } while((backslashIdx = s.indexOf(92, nextStart + 1)) != -1);

         if (nextStart < ln) {
            sb.append(s, nextStart, ln);
         }

         return sb.toString();
      }
   }
}
