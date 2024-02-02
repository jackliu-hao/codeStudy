package freemarker.core;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.Constants;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

class JSONParser {
   private static final String UNCLOSED_OBJECT_MESSAGE = "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")";
   private static final String UNCLOSED_ARRAY_MESSAGE = "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")";
   private static final BigDecimal MIN_INT_AS_BIGDECIMAL = BigDecimal.valueOf(-2147483648L);
   private static final BigDecimal MAX_INT_AS_BIGDECIMAL = BigDecimal.valueOf(2147483647L);
   private static final BigDecimal MIN_LONG_AS_BIGDECIMAL = BigDecimal.valueOf(Long.MIN_VALUE);
   private static final BigDecimal MAX_LONG_AS_BIGDECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);
   private final String src;
   private final int ln;
   private int p;
   private static int MAX_QUOTATION_LENGTH = 50;

   public static TemplateModel parse(String src) throws JSONParseException {
      return (new JSONParser(src)).parse();
   }

   private JSONParser(String src) {
      this.src = src;
      this.ln = src.length();
   }

   private TemplateModel parse() throws JSONParseException {
      this.skipWS();
      TemplateModel result = this.consumeValue("Empty JSON (contains no value)", this.p);
      this.skipWS();
      if (this.p != this.ln) {
         throw this.newParseException("End-of-file was expected but found further non-whitespace characters.");
      } else {
         return result;
      }
   }

   private TemplateModel consumeValue(String eofErrorMessage, int eofBlamePosition) throws JSONParseException {
      if (this.p == this.ln) {
         throw this.newParseException(eofErrorMessage == null ? "A value was expected here, but end-of-file was reached." : eofErrorMessage, eofBlamePosition == -1 ? this.p : eofBlamePosition);
      } else {
         TemplateModel result = this.tryConsumeString();
         if (result != null) {
            return result;
         } else {
            TemplateModel result = this.tryConsumeNumber();
            if (result != null) {
               return result;
            } else {
               TemplateModel result = this.tryConsumeObject();
               if (result != null) {
                  return result;
               } else {
                  TemplateModel result = this.tryConsumeArray();
                  if (result != null) {
                     return result;
                  } else {
                     TemplateModel result = this.tryConsumeTrueFalseNull();
                     if (result != null) {
                        return result != TemplateNullModel.INSTANCE ? result : null;
                     } else if (this.p < this.ln && this.src.charAt(this.p) == '\'') {
                        throw this.newParseException("Unexpected apostrophe-quote character. JSON strings must be quoted with quotation mark.");
                     } else {
                        throw this.newParseException("Expected either the beginning of a (negative) number or the beginning of one of these: {...}, [...], \"...\", true, false, null. Found character " + StringUtil.jQuote((Object)this.src.charAt(this.p)) + " instead.");
                     }
                  }
               }
            }
         }
      }
   }

   private TemplateModel tryConsumeTrueFalseNull() throws JSONParseException {
      int startP = this.p;
      if (this.p < this.ln && isIdentifierStart(this.src.charAt(this.p))) {
         ++this.p;

         while(this.p < this.ln && isIdentifierPart(this.src.charAt(this.p))) {
            ++this.p;
         }
      }

      if (startP == this.p) {
         return null;
      } else {
         String keyword = this.src.substring(startP, this.p);
         if (keyword.equals("true")) {
            return TemplateBooleanModel.TRUE;
         } else if (keyword.equals("false")) {
            return TemplateBooleanModel.FALSE;
         } else if (keyword.equals("null")) {
            return TemplateNullModel.INSTANCE;
         } else {
            throw this.newParseException("Invalid JSON keyword: " + StringUtil.jQuote(keyword) + ". Should be one of: true, false, null. If it meant to be a string then it must be quoted.", startP);
         }
      }
   }

   private TemplateNumberModel tryConsumeNumber() throws JSONParseException {
      if (this.p >= this.ln) {
         return null;
      } else {
         char c = this.src.charAt(this.p);
         boolean negative = c == '-';
         if (!negative && !isDigit(c) && c != '.') {
            return null;
         } else {
            int startP = this.p;
            if (negative) {
               if (this.p + 1 >= this.ln) {
                  throw this.newParseException("Expected a digit after \"-\", but reached end-of-file.");
               }

               char lookAheadC = this.src.charAt(this.p + 1);
               if (!isDigit(lookAheadC) && lookAheadC != '.') {
                  return null;
               }

               ++this.p;
            }

            long longSum = 0L;
            boolean firstDigit = true;

            do {
               c = this.src.charAt(this.p);
               if (!isDigit(c)) {
                  if (c == '.' && firstDigit) {
                     throw this.newParseException("JSON doesn't allow numbers starting with \".\".");
                  }
                  break;
               }

               int digit = c - 48;
               if (longSum == 0L) {
                  if (!firstDigit) {
                     throw this.newParseException("JSON doesn't allow superfluous leading 0-s.", this.p - 1);
                  }

                  longSum = !negative ? (long)digit : (long)(-digit);
                  ++this.p;
               } else {
                  long prevLongSum = longSum;
                  longSum = longSum * 10L + (long)(!negative ? digit : -digit);
                  if (!negative && prevLongSum > longSum || negative && prevLongSum < longSum) {
                     break;
                  }

                  ++this.p;
               }

               firstDigit = false;
            } while(this.p < this.ln);

            if (this.p < this.ln && this.isBigDecimalFittingTailCharacter(c)) {
               char lastC = c;
               ++this.p;

               for(; this.p < this.ln; lastC = c) {
                  c = this.src.charAt(this.p);
                  if (this.isBigDecimalFittingTailCharacter(c)) {
                     ++this.p;
                  } else {
                     if (c != '+' && c != '-' || !this.isE(lastC)) {
                        break;
                     }

                     ++this.p;
                  }
               }

               String numStr = this.src.substring(startP, this.p);

               BigDecimal bd;
               try {
                  bd = new BigDecimal(numStr);
               } catch (NumberFormatException var11) {
                  throw new JSONParseException("Malformed number: " + numStr, this.src, startP, var11);
               }

               if (bd.compareTo(MIN_INT_AS_BIGDECIMAL) >= 0 && bd.compareTo(MAX_INT_AS_BIGDECIMAL) <= 0) {
                  if (NumberUtil.isIntegerBigDecimal(bd)) {
                     return new SimpleNumber(bd.intValue());
                  }
               } else if (bd.compareTo(MIN_LONG_AS_BIGDECIMAL) >= 0 && bd.compareTo(MAX_LONG_AS_BIGDECIMAL) <= 0 && NumberUtil.isIntegerBigDecimal(bd)) {
                  return new SimpleNumber(bd.longValue());
               }

               return new SimpleNumber(bd);
            } else {
               return new SimpleNumber((Number)(longSum <= 2147483647L && longSum >= -2147483648L ? (int)longSum : longSum));
            }
         }
      }
   }

   private TemplateScalarModel tryConsumeString() throws JSONParseException {
      int startP = this.p;
      if (!this.tryConsumeChar('"')) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         char c = false;

         while(this.p < this.ln) {
            char c = this.src.charAt(this.p);
            if (c == '"') {
               ++this.p;
               return new SimpleScalar(sb.toString());
            }

            if (c == '\\') {
               ++this.p;
               sb.append(this.consumeAfterBackslash());
            } else {
               if (c <= 31) {
                  throw this.newParseException("JSON doesn't allow unescaped control characters in string literals, but found character with code (decimal): " + c);
               }

               ++this.p;
               sb.append(c);
            }
         }

         throw this.newParseException("String literal was still unclosed when the end of the file was reached. (Look for missing or accidentally escaped closing quotation mark.)", startP);
      }
   }

   private TemplateSequenceModel tryConsumeArray() throws JSONParseException {
      int startP = this.p;
      if (!this.tryConsumeChar('[')) {
         return null;
      } else {
         this.skipWS();
         if (this.tryConsumeChar(']')) {
            return Constants.EMPTY_SEQUENCE;
         } else {
            boolean afterComma = false;
            SimpleSequence elements = new SimpleSequence(_TemplateAPI.SAFE_OBJECT_WRAPPER);

            do {
               this.skipWS();
               elements.add(this.consumeValue(afterComma ? null : "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")", afterComma ? -1 : startP));
               this.skipWS();
               afterComma = true;
            } while(this.consumeChar(',', ']', "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")", startP) == ',');

            return elements;
         }
      }
   }

   private TemplateHashModelEx2 tryConsumeObject() throws JSONParseException {
      int startP = this.p;
      if (!this.tryConsumeChar('{')) {
         return null;
      } else {
         this.skipWS();
         if (this.tryConsumeChar('}')) {
            return Constants.EMPTY_HASH_EX2;
         } else {
            boolean afterComma = false;
            Map<String, Object> map = new LinkedHashMap();

            do {
               this.skipWS();
               int keyStartP = this.p;
               Object key = this.consumeValue(afterComma ? null : "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")", afterComma ? -1 : startP);
               if (!(key instanceof TemplateScalarModel)) {
                  throw this.newParseException("Wrong key type. JSON only allows string keys inside {...}.", keyStartP);
               }

               String strKey = null;

               try {
                  strKey = ((TemplateScalarModel)key).getAsString();
               } catch (TemplateModelException var8) {
                  throw new BugException(var8);
               }

               this.skipWS();
               this.consumeChar(':');
               this.skipWS();
               map.put(strKey, this.consumeValue((String)null, -1));
               this.skipWS();
               afterComma = true;
            } while(this.consumeChar(',', '}', "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")", startP) == ',');

            return new SimpleHash(map, _TemplateAPI.SAFE_OBJECT_WRAPPER, 0);
         }
      }
   }

   private boolean isE(char c) {
      return c == 'e' || c == 'E';
   }

   private boolean isBigDecimalFittingTailCharacter(char c) {
      return c == '.' || this.isE(c) || isDigit(c);
   }

   private char consumeAfterBackslash() throws JSONParseException {
      if (this.p == this.ln) {
         throw this.newParseException("Reached the end of the file, but the escape is unclosed.");
      } else {
         char c = this.src.charAt(this.p);
         switch (c) {
            case '"':
            case '/':
            case '\\':
               ++this.p;
               return c;
            case 'b':
               ++this.p;
               return '\b';
            case 'f':
               ++this.p;
               return '\f';
            case 'n':
               ++this.p;
               return '\n';
            case 'r':
               ++this.p;
               return '\r';
            case 't':
               ++this.p;
               return '\t';
            case 'u':
               ++this.p;
               return this.consumeAfterBackslashU();
            default:
               throw this.newParseException("Unsupported escape: \\" + c);
         }
      }
   }

   private char consumeAfterBackslashU() throws JSONParseException {
      if (this.p + 3 >= this.ln) {
         throw this.newParseException("\\u must be followed by exactly 4 hexadecimal digits");
      } else {
         String hex = this.src.substring(this.p, this.p + 4);

         try {
            char r = (char)Integer.parseInt(hex, 16);
            this.p += 4;
            return r;
         } catch (NumberFormatException var3) {
            throw this.newParseException("\\u must be followed by exactly 4 hexadecimal digits, but was followed by " + StringUtil.jQuote(hex) + ".");
         }
      }
   }

   private boolean tryConsumeChar(char c) {
      if (this.p < this.ln && this.src.charAt(this.p) == c) {
         ++this.p;
         return true;
      } else {
         return false;
      }
   }

   private void consumeChar(char expected) throws JSONParseException {
      this.consumeChar(expected, '\u0000', (String)null, -1);
   }

   private char consumeChar(char expected1, char expected2, String eofErrorHint, int eofErrorP) throws JSONParseException {
      if (this.p >= this.ln) {
         throw this.newParseException(eofErrorHint == null ? "Expected " + StringUtil.jQuote((Object)expected1) + (expected2 != 0 ? " or " + StringUtil.jQuote((Object)expected2) : "") + " character, but reached end-of-file. " : eofErrorHint, eofErrorP == -1 ? this.p : eofErrorP);
      } else {
         char c = this.src.charAt(this.p);
         if (c == expected1 || expected2 != 0 && c == expected2) {
            ++this.p;
            return c;
         } else {
            throw this.newParseException("Expected " + StringUtil.jQuote((Object)expected1) + (expected2 != 0 ? " or " + StringUtil.jQuote((Object)expected2) : "") + " character, but found " + StringUtil.jQuote((Object)c) + " instead.");
         }
      }
   }

   private void skipWS() throws JSONParseException {
      while(true) {
         if (this.p < this.ln && isWS(this.src.charAt(this.p))) {
            ++this.p;
         } else if (!this.skipComment()) {
            return;
         }
      }
   }

   private boolean skipComment() throws JSONParseException {
      if (this.p + 1 < this.ln && this.src.charAt(this.p) == '/') {
         char c2 = this.src.charAt(this.p + 1);
         int closerP;
         if (c2 == '/') {
            for(closerP = this.p + 2; closerP < this.ln && !isLineBreak(this.src.charAt(closerP)); ++closerP) {
            }

            this.p = closerP;
            return true;
         }

         if (c2 == '*') {
            for(closerP = this.p + 3; closerP < this.ln && (this.src.charAt(closerP - 1) != '*' || this.src.charAt(closerP) != '/'); ++closerP) {
            }

            if (closerP >= this.ln) {
               throw this.newParseException("Unclosed comment");
            }

            this.p = closerP + 1;
            return true;
         }
      }

      return false;
   }

   private static boolean isWS(char c) {
      return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == 160 || c == '\ufeff';
   }

   private static boolean isLineBreak(char c) {
      return c == '\r' || c == '\n';
   }

   private static boolean isIdentifierStart(char c) {
      return Character.isLetter(c) || c == '_' || c == '$';
   }

   private static boolean isDigit(char c) {
      return c >= '0' && c <= '9';
   }

   private static boolean isIdentifierPart(char c) {
      return isIdentifierStart(c) || isDigit(c);
   }

   private JSONParseException newParseException(String message) {
      return this.newParseException(message, this.p);
   }

   private JSONParseException newParseException(String message, int p) {
      return new JSONParseException(message, this.src, p);
   }

   private static String createSourceCodeErrorMessage(String message, String srcCode, int position) {
      int ln = srcCode.length();
      if (position < 0) {
         position = 0;
      }

      if (position >= ln) {
         return message + "\nError location: At the end of text.";
      } else {
         int rowBegin = 0;
         int row = 1;
         char lastChar = 0;

         int i;
         char c;
         for(i = 0; i <= position; ++i) {
            c = srcCode.charAt(i);
            if (lastChar == '\n') {
               rowBegin = i;
               ++row;
            } else if (lastChar == '\r' && c != '\n') {
               rowBegin = i;
               ++row;
            }

            lastChar = c;
         }

         for(i = position; i < ln; ++i) {
            c = srcCode.charAt(i);
            if (c == '\n' || c == '\r') {
               if (c == '\n' && i > 0 && srcCode.charAt(i - 1) == '\r') {
                  --i;
               }
               break;
            }
         }

         int rowEnd = i - 1;
         if (position > rowEnd + 1) {
            position = rowEnd + 1;
         }

         int col = position - rowBegin + 1;
         if (rowBegin > rowEnd) {
            return message + "\nError location: line " + row + ", column " + col + ":\n(Can't show the line because it is empty.)";
         } else {
            String s1 = srcCode.substring(rowBegin, position);
            String s2 = srcCode.substring(position, rowEnd + 1);
            s1 = expandTabs(s1, 8);
            int ln1 = s1.length();
            s2 = expandTabs(s2, 8, ln1);
            int ln2 = s2.length();
            if (ln1 + ln2 > MAX_QUOTATION_LENGTH) {
               int newLn2 = ln2 - (ln1 + ln2 - MAX_QUOTATION_LENGTH);
               if (newLn2 < 6) {
                  newLn2 = 6;
               }

               if (newLn2 < ln2) {
                  s2 = s2.substring(0, newLn2 - 3) + "...";
                  ln2 = newLn2;
               }

               if (ln1 + ln2 > MAX_QUOTATION_LENGTH) {
                  s1 = "..." + s1.substring(ln1 + ln2 - MAX_QUOTATION_LENGTH + 3);
               }
            }

            StringBuilder res = new StringBuilder(message.length() + 80);
            res.append(message);
            res.append("\nError location: line ").append(row).append(", column ").append(col).append(":\n");
            res.append(s1).append(s2).append("\n");

            for(int x = s1.length(); x != 0; --x) {
               res.append(' ');
            }

            res.append('^');
            return res.toString();
         }
      }
   }

   private static String expandTabs(String s, int tabWidth) {
      return expandTabs(s, tabWidth, 0);
   }

   private static String expandTabs(String s, int tabWidth, int startCol) {
      int e = s.indexOf(9);
      if (e == -1) {
         return s;
      } else {
         int b = 0;
         StringBuilder buf = new StringBuilder(s.length() + Math.max(16, tabWidth * 2));

         do {
            buf.append(s, b, e);
            int col = buf.length() + startCol;

            for(int i = tabWidth * (1 + col / tabWidth) - col; i > 0; --i) {
               buf.append(' ');
            }

            b = e + 1;
            e = s.indexOf(9, b);
         } while(e != -1);

         buf.append(s, b, s.length());
         return buf.toString();
      }
   }

   static class JSONParseException extends Exception {
      public JSONParseException(String message, String src, int position) {
         super(JSONParser.createSourceCodeErrorMessage(message, src, position));
      }

      public JSONParseException(String message, String src, int position, Throwable cause) {
         super(JSONParser.createSourceCodeErrorMessage(message, src, position), cause);
      }
   }
}
