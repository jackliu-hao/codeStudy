package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

class ExtendedDecimalFormatParser {
   private static final String PARAM_ROUNDING_MODE = "roundingMode";
   private static final String PARAM_MULTIPIER = "multipier";
   private static final String PARAM_MULTIPLIER = "multiplier";
   private static final String PARAM_DECIMAL_SEPARATOR = "decimalSeparator";
   private static final String PARAM_MONETARY_DECIMAL_SEPARATOR = "monetaryDecimalSeparator";
   private static final String PARAM_GROUP_SEPARATOR = "groupingSeparator";
   private static final String PARAM_EXPONENT_SEPARATOR = "exponentSeparator";
   private static final String PARAM_MINUS_SIGN = "minusSign";
   private static final String PARAM_INFINITY = "infinity";
   private static final String PARAM_NAN = "nan";
   private static final String PARAM_PERCENT = "percent";
   private static final String PARAM_PER_MILL = "perMill";
   private static final String PARAM_ZERO_DIGIT = "zeroDigit";
   private static final String PARAM_CURRENCY_CODE = "currencyCode";
   private static final String PARAM_CURRENCY_SYMBOL = "currencySymbol";
   private static final String PARAM_VALUE_RND_UP = "up";
   private static final String PARAM_VALUE_RND_DOWN = "down";
   private static final String PARAM_VALUE_RND_CEILING = "ceiling";
   private static final String PARAM_VALUE_RND_FLOOR = "floor";
   private static final String PARAM_VALUE_RND_HALF_DOWN = "halfDown";
   private static final String PARAM_VALUE_RND_HALF_EVEN = "halfEven";
   private static final String PARAM_VALUE_RND_HALF_UP = "halfUp";
   private static final String PARAM_VALUE_RND_UNNECESSARY = "unnecessary";
   private static final HashMap<String, ? extends ParameterHandler> PARAM_HANDLERS;
   private static final String SNIP_MARK = "[...]";
   private static final int MAX_QUOTATION_LENGTH = 10;
   private final String src;
   private int pos = 0;
   private final DecimalFormatSymbols symbols;
   private RoundingMode roundingMode;
   private Integer multiplier;

   static DecimalFormat parse(String formatString, Locale locale) throws java.text.ParseException {
      return (new ExtendedDecimalFormatParser(formatString, locale)).parse();
   }

   private DecimalFormat parse() throws java.text.ParseException {
      String stdPattern = this.fetchStandardPattern();
      this.skipWS();
      this.parseFormatStringExtension();

      DecimalFormat decimalFormat;
      try {
         decimalFormat = new DecimalFormat(stdPattern, this.symbols);
      } catch (IllegalArgumentException var7) {
         IllegalArgumentException e = var7;
         java.text.ParseException pe = new java.text.ParseException(var7.getMessage(), 0);
         if (var7.getCause() != null) {
            try {
               e.initCause(e.getCause());
            } catch (Exception var6) {
            }
         }

         throw pe;
      }

      if (this.roundingMode != null) {
         decimalFormat.setRoundingMode(this.roundingMode);
      }

      if (this.multiplier != null) {
         decimalFormat.setMultiplier(this.multiplier);
      }

      return decimalFormat;
   }

   private void parseFormatStringExtension() throws java.text.ParseException {
      int ln = this.src.length();
      if (this.pos != ln) {
         String currencySymbol = null;

         while(true) {
            int namePos = this.pos;
            String name = this.fetchName();
            if (name == null) {
               throw this.newExpectedSgParseException("name");
            }

            this.skipWS();
            if (!this.fetchChar('=')) {
               throw this.newExpectedSgParseException("\"=\"");
            }

            this.skipWS();
            int valuePos = this.pos;
            String value = this.fetchValue();
            if (value == null) {
               throw this.newExpectedSgParseException("value");
            }

            int paramEndPos = this.pos;
            ParameterHandler handler = (ParameterHandler)PARAM_HANDLERS.get(name);
            if (handler == null) {
               if (!name.equals("currencySymbol")) {
                  throw this.newUnknownParameterException(name, namePos);
               }

               currencySymbol = value;
            } else {
               try {
                  handler.handle(this, value);
               } catch (InvalidParameterValueException var10) {
                  throw this.newInvalidParameterValueException(name, value, valuePos, var10);
               }
            }

            this.skipWS();
            if (this.fetchChar(',')) {
               this.skipWS();
            } else {
               if (this.pos == ln) {
                  if (currencySymbol != null) {
                     this.symbols.setCurrencySymbol(currencySymbol);
                  }

                  return;
               }

               if (this.pos == paramEndPos) {
                  throw this.newExpectedSgParseException("parameter separator whitespace or comma");
               }
            }
         }
      }
   }

   private java.text.ParseException newInvalidParameterValueException(String name, String value, int valuePos, InvalidParameterValueException e) {
      return new java.text.ParseException(StringUtil.jQuote(value) + " is an invalid value for the \"" + name + "\" parameter: " + e.message, valuePos);
   }

   private java.text.ParseException newUnknownParameterException(String name, int namePos) throws java.text.ParseException {
      StringBuilder sb = new StringBuilder(128);
      sb.append("Unsupported parameter name, ").append(StringUtil.jQuote(name));
      sb.append(". The supported names are: ");
      Set<String> legalNames = PARAM_HANDLERS.keySet();
      String[] legalNameArr = (String[])legalNames.toArray(new String[legalNames.size()]);
      Arrays.sort(legalNameArr);

      for(int i = 0; i < legalNameArr.length; ++i) {
         if (i != 0) {
            sb.append(", ");
         }

         sb.append(legalNameArr[i]);
      }

      return new java.text.ParseException(sb.toString(), namePos);
   }

   private void skipWS() {
      for(int ln = this.src.length(); this.pos < ln && this.isWS(this.src.charAt(this.pos)); ++this.pos) {
      }

   }

   private boolean fetchChar(char fetchedChar) {
      if (this.pos < this.src.length() && this.src.charAt(this.pos) == fetchedChar) {
         ++this.pos;
         return true;
      } else {
         return false;
      }
   }

   private boolean isWS(char c) {
      return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == 160;
   }

   private String fetchName() throws java.text.ParseException {
      int ln = this.src.length();
      int startPos = this.pos;

      boolean firstChar;
      for(firstChar = true; this.pos < ln; ++this.pos) {
         char c = this.src.charAt(this.pos);
         if (firstChar) {
            if (!Character.isJavaIdentifierStart(c)) {
               break;
            }

            firstChar = false;
         } else if (!Character.isJavaIdentifierPart(c)) {
            break;
         }
      }

      return !firstChar ? this.src.substring(startPos, this.pos) : null;
   }

   private String fetchValue() throws java.text.ParseException {
      int ln = this.src.length();
      int startPos = this.pos;
      char openedQuot = 0;

      for(boolean needsUnescaping = false; this.pos < ln; ++this.pos) {
         char c = this.src.charAt(this.pos);
         if (c != '\'' && c != '"') {
            if (openedQuot == 0 && !Character.isJavaIdentifierPart(c)) {
               break;
            }
         } else if (openedQuot == 0) {
            if (startPos != this.pos) {
               throw new java.text.ParseException("The " + c + " character can only be used for quoting values, but it was in the middle of an non-quoted value.", this.pos);
            }

            openedQuot = c;
         } else if (c == openedQuot) {
            if (this.pos + 1 >= ln || this.src.charAt(this.pos + 1) != openedQuot) {
               String str = this.src.substring(startPos + 1, this.pos);
               ++this.pos;
               return needsUnescaping ? this.unescape(str, openedQuot) : str;
            }

            ++this.pos;
            needsUnescaping = true;
         }
      }

      if (openedQuot != 0) {
         throw new java.text.ParseException("The " + openedQuot + " quotation wasn't closed when the end of the source was reached.", this.pos);
      } else {
         return startPos == this.pos ? null : this.src.substring(startPos, this.pos);
      }
   }

   private String unescape(String s, char openedQuot) {
      return openedQuot == '\'' ? StringUtil.replace(s, "''", "'") : StringUtil.replace(s, "\"\"", "\"");
   }

   private String fetchStandardPattern() {
      int pos = this.pos;
      int ln = this.src.length();
      int semicolonCnt = 0;

      for(boolean quotedMode = false; pos < ln; ++pos) {
         char c = this.src.charAt(pos);
         if (c == ';' && !quotedMode) {
            ++semicolonCnt;
            if (semicolonCnt == 2) {
               break;
            }
         } else if (c == '\'') {
            if (quotedMode) {
               if (pos + 1 < ln && this.src.charAt(pos + 1) == '\'') {
                  ++pos;
               } else {
                  quotedMode = false;
               }
            } else {
               quotedMode = true;
            }
         }
      }

      String stdFormatStr;
      if (semicolonCnt < 2) {
         stdFormatStr = this.src;
      } else {
         int stdEndPos = pos;
         if (this.src.charAt(pos - 1) == ';') {
            stdEndPos = pos - 1;
         }

         stdFormatStr = this.src.substring(0, stdEndPos);
      }

      if (pos < ln) {
         ++pos;
      }

      this.pos = pos;
      return stdFormatStr;
   }

   private ExtendedDecimalFormatParser(String formatString, Locale locale) {
      this.src = formatString;
      this.symbols = DecimalFormatSymbols.getInstance(locale);
   }

   private java.text.ParseException newExpectedSgParseException(String expectedThing) {
      int i;
      for(i = this.src.length() - 1; i >= 0 && Character.isWhitespace(this.src.charAt(i)); --i) {
      }

      int ln = i + 1;
      String quotation;
      if (this.pos < ln) {
         int qEndPos = this.pos + 10;
         if (qEndPos >= ln) {
            quotation = this.src.substring(this.pos, ln);
         } else {
            quotation = this.src.substring(this.pos, qEndPos - "[...]".length()) + "[...]";
         }
      } else {
         quotation = null;
      }

      return new java.text.ParseException("Expected a(n) " + expectedThing + " at position " + this.pos + " (0-based), but " + (quotation == null ? "reached the end of the input." : "found: " + quotation), this.pos);
   }

   static {
      HashMap<String, ParameterHandler> m = new HashMap();
      m.put("roundingMode", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            RoundingMode parsedValue;
            if (value.equals("up")) {
               parsedValue = RoundingMode.UP;
            } else if (value.equals("down")) {
               parsedValue = RoundingMode.DOWN;
            } else if (value.equals("ceiling")) {
               parsedValue = RoundingMode.CEILING;
            } else if (value.equals("floor")) {
               parsedValue = RoundingMode.FLOOR;
            } else if (value.equals("halfDown")) {
               parsedValue = RoundingMode.HALF_DOWN;
            } else if (value.equals("halfEven")) {
               parsedValue = RoundingMode.HALF_EVEN;
            } else if (value.equals("halfUp")) {
               parsedValue = RoundingMode.HALF_UP;
            } else {
               if (!value.equals("unnecessary")) {
                  throw new InvalidParameterValueException("Should be one of: up, down, ceiling, floor, halfDown, halfEven, unnecessary");
               }

               parsedValue = RoundingMode.UNNECESSARY;
            }

            parser.roundingMode = parsedValue;
         }
      });
      ParameterHandler multiplierParamHandler = new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            try {
               parser.multiplier = Integer.valueOf(value);
            } catch (NumberFormatException var4) {
               throw new InvalidParameterValueException("Malformed integer.");
            }
         }
      };
      m.put("multiplier", multiplierParamHandler);
      m.put("multipier", multiplierParamHandler);
      m.put("decimalSeparator", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setDecimalSeparator(value.charAt(0));
            }
         }
      });
      m.put("monetaryDecimalSeparator", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setMonetaryDecimalSeparator(value.charAt(0));
            }
         }
      });
      m.put("groupingSeparator", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setGroupingSeparator(value.charAt(0));
            }
         }
      });
      m.put("exponentSeparator", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            parser.symbols.setExponentSeparator(value);
         }
      });
      m.put("minusSign", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setMinusSign(value.charAt(0));
            }
         }
      });
      m.put("infinity", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            parser.symbols.setInfinity(value);
         }
      });
      m.put("nan", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            parser.symbols.setNaN(value);
         }
      });
      m.put("percent", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setPercent(value.charAt(0));
            }
         }
      });
      m.put("perMill", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setPerMill(value.charAt(0));
            }
         }
      });
      m.put("zeroDigit", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            if (value.length() != 1) {
               throw new InvalidParameterValueException("Must contain exactly 1 character.");
            } else {
               parser.symbols.setZeroDigit(value.charAt(0));
            }
         }
      });
      m.put("currencyCode", new ParameterHandler() {
         public void handle(ExtendedDecimalFormatParser parser, String value) throws InvalidParameterValueException {
            Currency currency;
            try {
               currency = Currency.getInstance(value);
            } catch (IllegalArgumentException var5) {
               throw new InvalidParameterValueException("Not a known ISO 4217 code.");
            }

            parser.symbols.setCurrency(currency);
         }
      });
      PARAM_HANDLERS = m;
   }

   private static class InvalidParameterValueException extends Exception {
      private final String message;

      public InvalidParameterValueException(String message) {
         this.message = message;
      }
   }

   private interface ParameterHandler {
      void handle(ExtendedDecimalFormatParser var1, String var2) throws InvalidParameterValueException;
   }
}
