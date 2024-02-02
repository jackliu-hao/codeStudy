package ch.qos.logback.core.util;

import java.text.DateFormatSymbols;

class CharSequenceToRegexMapper {
   DateFormatSymbols symbols = DateFormatSymbols.getInstance();

   String toRegex(CharSequenceState css) {
      int occurrences = css.occurrences;
      char c = css.c;
      switch (css.c) {
         case '\'':
            if (occurrences == 1) {
               return "";
            }

            throw new IllegalStateException("Too many single quotes");
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'I':
         case 'J':
         case 'L':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'X':
         case 'Y':
         case '[':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'b':
         case 'c':
         case 'e':
         case 'f':
         case 'g':
         case 'i':
         case 'j':
         case 'l':
         case 'n':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         case 't':
         case 'u':
         case 'v':
         case 'x':
         default:
            if (occurrences == 1) {
               return "" + c;
            }

            return c + "{" + occurrences + "}";
         case '.':
            return "\\.";
         case 'D':
         case 'F':
         case 'H':
         case 'K':
         case 'S':
         case 'W':
         case 'd':
         case 'h':
         case 'k':
         case 'm':
         case 's':
         case 'w':
         case 'y':
            return this.number(occurrences);
         case 'E':
            if (occurrences >= 4) {
               return this.getRegexForLongDaysOfTheWeek();
            }

            return this.getRegexForShortDaysOfTheWeek();
         case 'G':
         case 'z':
            return ".*";
         case 'M':
            if (occurrences <= 2) {
               return this.number(occurrences);
            } else {
               if (occurrences == 3) {
                  return this.getRegexForShortMonths();
               }

               return this.getRegexForLongMonths();
            }
         case 'Z':
            return "(\\+|-)\\d{4}";
         case '\\':
            throw new IllegalStateException("Forward slashes are not allowed");
         case 'a':
            return this.getRegexForAmPms();
      }
   }

   private String number(int occurrences) {
      return "\\d{" + occurrences + "}";
   }

   private String getRegexForAmPms() {
      return this.symbolArrayToRegex(this.symbols.getAmPmStrings());
   }

   private String getRegexForLongDaysOfTheWeek() {
      return this.symbolArrayToRegex(this.symbols.getWeekdays());
   }

   private String getRegexForShortDaysOfTheWeek() {
      return this.symbolArrayToRegex(this.symbols.getShortWeekdays());
   }

   private String getRegexForLongMonths() {
      return this.symbolArrayToRegex(this.symbols.getMonths());
   }

   String getRegexForShortMonths() {
      return this.symbolArrayToRegex(this.symbols.getShortMonths());
   }

   private String symbolArrayToRegex(String[] symbolArray) {
      int[] minMax = findMinMaxLengthsInSymbols(symbolArray);
      return ".{" + minMax[0] + "," + minMax[1] + "}";
   }

   static int[] findMinMaxLengthsInSymbols(String[] symbols) {
      int min = Integer.MAX_VALUE;
      int max = 0;
      String[] var3 = symbols;
      int var4 = symbols.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String symbol = var3[var5];
         int len = symbol.length();
         if (len != 0) {
            min = Math.min(min, len);
            max = Math.max(max, len);
         }
      }

      return new int[]{min, max};
   }
}
