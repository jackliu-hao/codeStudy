package org.h2.mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.h2.expression.function.ToCharFunction;
import org.h2.message.DbException;
import org.h2.util.TimeZoneProvider;

final class ToDateTokenizer {
   static final Pattern PATTERN_INLINE = Pattern.compile("(\"[^\"]*\")");
   static final Pattern PATTERN_NUMBER = Pattern.compile("^([+-]?[0-9]+)");
   static final Pattern PATTERN_FOUR_DIGITS = Pattern.compile("^([+-]?[0-9]{4})");
   static final Pattern PATTERN_TWO_TO_FOUR_DIGITS = Pattern.compile("^([+-]?[0-9]{2,4})");
   static final Pattern PATTERN_THREE_DIGITS = Pattern.compile("^([+-]?[0-9]{3})");
   static final Pattern PATTERN_TWO_DIGITS = Pattern.compile("^([+-]?[0-9]{2})");
   static final Pattern PATTERN_TWO_DIGITS_OR_LESS = Pattern.compile("^([+-]?[0-9][0-9]?)");
   static final Pattern PATTERN_ONE_DIGIT = Pattern.compile("^([+-]?[0-9])");
   static final Pattern PATTERN_FF = Pattern.compile("^(FF[0-9]?)", 2);
   static final Pattern PATTERN_AM_PM = Pattern.compile("^(AM|A\\.M\\.|PM|P\\.M\\.)", 2);
   static final Pattern PATTERN_BC_AD = Pattern.compile("^(BC|B\\.C\\.|AD|A\\.D\\.)", 2);
   static final YearParslet PARSLET_YEAR = new YearParslet();
   static final MonthParslet PARSLET_MONTH = new MonthParslet();
   static final DayParslet PARSLET_DAY = new DayParslet();
   static final TimeParslet PARSLET_TIME = new TimeParslet();
   static final InlineParslet PARSLET_INLINE = new InlineParslet();

   static String matchStringOrThrow(Pattern var0, ToDateParser var1, Enum<?> var2) {
      String var3 = var1.getInputStr();
      Matcher var4 = var0.matcher(var3);
      if (!var4.find()) {
         throwException(var1, String.format("Issue happened when parsing token '%s'", var2.name()));
      }

      return var4.group(1);
   }

   static String setByName(ToDateParser var0, int var1) {
      String var2 = null;
      String var3 = var0.getInputStr();
      String[] var4 = ToCharFunction.getDateNames(var1);

      label31:
      for(int var5 = 0; var5 < var4.length; ++var5) {
         String var6 = var4[var5];
         if (var6 != null) {
            int var7 = var6.length();
            if (var6.equalsIgnoreCase(var3.substring(0, var7))) {
               switch (var1) {
                  case 0:
                  case 1:
                     var0.setMonth(var5 + 1);
                  case 2:
                  case 3:
                     var2 = var6;
                     break label31;
                  default:
                     throw new IllegalArgumentException();
               }
            }
         }
      }

      if (var2 == null || var2.isEmpty()) {
         throwException(var0, String.format("Tried to parse one of '%s' but failed (may be an internal error?)", Arrays.toString(var4)));
      }

      return var2;
   }

   static void throwException(ToDateParser var0, String var1) {
      throw DbException.get(90056, var0.getFunctionName(), String.format(" %s. Details: %s", var1, var0));
   }

   private ToDateTokenizer() {
   }

   public static enum FormatTokenEnum {
      YYYY(ToDateTokenizer.PARSLET_YEAR),
      SYYYY(ToDateTokenizer.PARSLET_YEAR),
      YYY(ToDateTokenizer.PARSLET_YEAR),
      YY(ToDateTokenizer.PARSLET_YEAR),
      SCC(ToDateTokenizer.PARSLET_YEAR),
      CC(ToDateTokenizer.PARSLET_YEAR),
      RRRR(ToDateTokenizer.PARSLET_YEAR),
      RR(ToDateTokenizer.PARSLET_YEAR),
      BC_AD(ToDateTokenizer.PARSLET_YEAR, ToDateTokenizer.PATTERN_BC_AD),
      MONTH(ToDateTokenizer.PARSLET_MONTH),
      MON(ToDateTokenizer.PARSLET_MONTH),
      MM(ToDateTokenizer.PARSLET_MONTH),
      RM(ToDateTokenizer.PARSLET_MONTH),
      DDD(ToDateTokenizer.PARSLET_DAY),
      DAY(ToDateTokenizer.PARSLET_DAY),
      DD(ToDateTokenizer.PARSLET_DAY),
      DY(ToDateTokenizer.PARSLET_DAY),
      HH24(ToDateTokenizer.PARSLET_TIME),
      HH12(ToDateTokenizer.PARSLET_TIME),
      HH(ToDateTokenizer.PARSLET_TIME),
      MI(ToDateTokenizer.PARSLET_TIME),
      SSSSS(ToDateTokenizer.PARSLET_TIME),
      SS(ToDateTokenizer.PARSLET_TIME),
      FF(ToDateTokenizer.PARSLET_TIME, ToDateTokenizer.PATTERN_FF),
      TZH(ToDateTokenizer.PARSLET_TIME),
      TZM(ToDateTokenizer.PARSLET_TIME),
      TZR(ToDateTokenizer.PARSLET_TIME),
      TZD(ToDateTokenizer.PARSLET_TIME),
      AM_PM(ToDateTokenizer.PARSLET_TIME, ToDateTokenizer.PATTERN_AM_PM),
      EE(ToDateTokenizer.PARSLET_YEAR),
      E(ToDateTokenizer.PARSLET_YEAR),
      Y(ToDateTokenizer.PARSLET_YEAR),
      Q(ToDateTokenizer.PARSLET_MONTH),
      D(ToDateTokenizer.PARSLET_DAY),
      J(ToDateTokenizer.PARSLET_DAY),
      INLINE(ToDateTokenizer.PARSLET_INLINE, ToDateTokenizer.PATTERN_INLINE);

      private static final List<FormatTokenEnum> INLINE_LIST = Collections.singletonList(INLINE);
      private static List<FormatTokenEnum>[] TOKENS;
      private final ToDateParslet toDateParslet;
      private final Pattern patternToUse;

      private FormatTokenEnum(ToDateParslet var3, Pattern var4) {
         this.toDateParslet = var3;
         this.patternToUse = var4;
      }

      private FormatTokenEnum(ToDateParslet var3) {
         this.toDateParslet = var3;
         this.patternToUse = Pattern.compile(String.format("^(%s)", this.name()), 2);
      }

      static List<FormatTokenEnum> getTokensInQuestion(String var0) {
         if (var0 != null && !var0.isEmpty()) {
            char var1 = Character.toUpperCase(var0.charAt(0));
            if (var1 >= 'A' && var1 <= 'Y') {
               List[] var2 = TOKENS;
               if (var2 == null) {
                  var2 = initTokens();
               }

               return var2[var1 - 65];
            }

            if (var1 == '"') {
               return INLINE_LIST;
            }
         }

         return null;
      }

      private static List<FormatTokenEnum>[] initTokens() {
         List[] var0 = new List[25];
         FormatTokenEnum[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            FormatTokenEnum var4 = var1[var3];
            String var5 = var4.name();
            if (var5.indexOf(95) >= 0) {
               String[] var6 = var5.split("_");
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String var9 = var6[var8];
                  putToCache(var0, var4, var9);
               }
            } else {
               putToCache(var0, var4, var5);
            }
         }

         TOKENS = var0;
         return var0;
      }

      private static void putToCache(List<FormatTokenEnum>[] var0, FormatTokenEnum var1, String var2) {
         int var3 = Character.toUpperCase(var2.charAt(0)) - 65;
         Object var4 = var0[var3];
         if (var4 == null) {
            var4 = new ArrayList(1);
            var0[var3] = (List)var4;
         }

         ((List)var4).add(var1);
      }

      boolean parseFormatStrWithToken(ToDateParser var1) {
         Matcher var2 = this.patternToUse.matcher(var1.getFormatStr());
         boolean var3 = var2.find();
         if (var3) {
            String var4 = var2.group(1);
            this.toDateParslet.parse(var1, this, var4);
         }

         return var3;
      }
   }

   static class InlineParslet implements ToDateParslet {
      public void parse(ToDateParser var1, FormatTokenEnum var2, String var3) {
         String var4 = null;
         switch (var2) {
            case INLINE:
               var4 = var3.replace("\"", "");
               var1.remove(var4, var3);
               return;
            default:
               throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", this.getClass().getSimpleName(), var2));
         }
      }
   }

   static class TimeParslet implements ToDateParslet {
      public void parse(ToDateParser var1, FormatTokenEnum var2, String var3) {
         String var4 = null;
         boolean var5 = false;
         int var10;
         switch (var2) {
            case HH24:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var10 = Integer.parseInt(var4);
               var1.setHour(var10);
               break;
            case HH12:
            case HH:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var10 = Integer.parseInt(var4);
               var1.setHour12(var10);
               break;
            case MI:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var10 = Integer.parseInt(var4);
               var1.setMinute(var10);
               break;
            case SS:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var10 = Integer.parseInt(var4);
               var1.setSecond(var10);
               break;
            case SSSSS:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, var1, var2);
               var10 = Integer.parseInt(var4);
               int var11 = var10 % 60;
               var10 /= 60;
               int var12 = var10 % 60;
               var10 /= 60;
               int var8 = var10 % 24;
               var1.setHour(var8);
               var1.setMinute(var12);
               var1.setSecond(var11);
               break;
            case FF:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, var1, var2);
               String var6 = String.format("%-9s", var4).replace(' ', '0');
               var6 = var6.substring(0, 9);
               double var7 = Double.parseDouble(var6);
               var1.setNanos((int)var7);
               break;
            case AM_PM:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_AM_PM, var1, var2);
               if (var4.toUpperCase().startsWith("A")) {
                  var1.setAmPm(true);
               } else {
                  var1.setAmPm(false);
               }
               break;
            case TZH:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var10 = Integer.parseInt(var4);
               var1.setTimeZoneHour(var10);
               break;
            case TZM:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var10 = Integer.parseInt(var4);
               var1.setTimeZoneMinute(var10);
               break;
            case TZR:
            case TZD:
               String var9 = var1.getInputStr();
               var1.setTimeZone(TimeZoneProvider.ofId(var9));
               var4 = var9;
               break;
            default:
               throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", this.getClass().getSimpleName(), var2));
         }

         var1.remove(var4, var3);
      }
   }

   static class DayParslet implements ToDateParslet {
      public void parse(ToDateParser var1, FormatTokenEnum var2, String var3) {
         String var4 = null;
         boolean var5 = false;
         int var6;
         switch (var2) {
            case DDD:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, var1, var2);
               var6 = Integer.parseInt(var4);
               var1.setDayOfYear(var6);
               break;
            case DD:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var6 = Integer.parseInt(var4);
               var1.setDay(var6);
               break;
            case D:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_ONE_DIGIT, var1, var2);
               var6 = Integer.parseInt(var4);
               var1.setDay(var6);
               break;
            case DAY:
               var4 = ToDateTokenizer.setByName(var1, 2);
               break;
            case DY:
               var4 = ToDateTokenizer.setByName(var1, 3);
               break;
            case J:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, var1, var2);
               var6 = Integer.parseInt(var4);
               var1.setAbsoluteDay(var6 + -2440588);
               break;
            default:
               throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", this.getClass().getSimpleName(), var2));
         }

         var1.remove(var4, var3);
      }
   }

   static class MonthParslet implements ToDateParslet {
      private static final String[] ROMAN_MONTH = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

      public void parse(ToDateParser var1, FormatTokenEnum var2, String var3) {
         String var4 = var1.getInputStr();
         String var5 = null;
         boolean var6 = false;
         int var12;
         switch (var2) {
            case MONTH:
               var5 = ToDateTokenizer.setByName(var1, 0);
               break;
            case Q:
               ToDateTokenizer.throwException(var1, String.format("token '%s' not supported yet.", var2.name()));
               break;
            case MON:
               var5 = ToDateTokenizer.setByName(var1, 1);
               break;
            case MM:
               var5 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, var1, var2);
               var12 = Integer.parseInt(var5);
               var1.setMonth(var12);
               break;
            case RM:
               var12 = 0;
               String[] var7 = ROMAN_MONTH;
               int var8 = var7.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String var10 = var7[var9];
                  ++var12;
                  int var11 = var10.length();
                  if (var4.length() >= var11 && var10.equalsIgnoreCase(var4.substring(0, var11))) {
                     var1.setMonth(var12 + 1);
                     var5 = var10;
                     break;
                  }
               }

               if (var5 == null || var5.isEmpty()) {
                  ToDateTokenizer.throwException(var1, String.format("Issue happened when parsing token '%s'. Expected one of: %s", var2.name(), Arrays.toString(ROMAN_MONTH)));
               }
               break;
            default:
               throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", this.getClass().getSimpleName(), var2));
         }

         var1.remove(var5, var3);
      }
   }

   static class YearParslet implements ToDateParslet {
      public void parse(ToDateParser var1, FormatTokenEnum var2, String var3) {
         String var4 = null;
         boolean var5 = false;
         int var7;
         switch (var2) {
            case SYYYY:
            case YYYY:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_FOUR_DIGITS, var1, var2);
               var7 = Integer.parseInt(var4);
               if (var7 == 0) {
                  ToDateTokenizer.throwException(var1, "Year may not be zero");
               }

               var1.setYear(var7 >= 0 ? var7 : var7 + 1);
               break;
            case YYY:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_THREE_DIGITS, var1, var2);
               var7 = Integer.parseInt(var4);
               if (var7 > 999) {
                  ToDateTokenizer.throwException(var1, "Year may have only three digits with specified format");
               }

               var7 += var1.getCurrentYear() / 1000 * 1000;
               var1.setYear(var7 >= 0 ? var7 : var7 + 1);
               break;
            case RRRR:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_TO_FOUR_DIGITS, var1, var2);
               var7 = Integer.parseInt(var4);
               if (var4.length() < 4) {
                  if (var7 < 50) {
                     var7 += 2000;
                  } else if (var7 < 100) {
                     var7 += 1900;
                  }
               }

               if (var7 == 0) {
                  ToDateTokenizer.throwException(var1, "Year may not be zero");
               }

               var1.setYear(var7);
               break;
            case RR:
               int var6 = var1.getCurrentYear() / 100;
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, var1, var2);
               var7 = Integer.parseInt(var4) + var6 * 100;
               var1.setYear(var7);
               break;
            case EE:
               ToDateTokenizer.throwException(var1, String.format("token '%s' not supported yet.", var2.name()));
               break;
            case E:
               ToDateTokenizer.throwException(var1, String.format("token '%s' not supported yet.", var2.name()));
               break;
            case YY:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, var1, var2);
               var7 = Integer.parseInt(var4);
               if (var7 > 99) {
                  ToDateTokenizer.throwException(var1, "Year may have only two digits with specified format");
               }

               var7 += var1.getCurrentYear() / 100 * 100;
               var1.setYear(var7 >= 0 ? var7 : var7 + 1);
               break;
            case SCC:
            case CC:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, var1, var2);
               var7 = Integer.parseInt(var4) * 100;
               var1.setYear(var7);
               break;
            case Y:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_ONE_DIGIT, var1, var2);
               var7 = Integer.parseInt(var4);
               if (var7 > 9) {
                  ToDateTokenizer.throwException(var1, "Year may have only two digits with specified format");
               }

               var7 += var1.getCurrentYear() / 10 * 10;
               var1.setYear(var7 >= 0 ? var7 : var7 + 1);
               break;
            case BC_AD:
               var4 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_BC_AD, var1, var2);
               var1.setBC(var4.toUpperCase().startsWith("B"));
               break;
            default:
               throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", this.getClass().getSimpleName(), var2));
         }

         var1.remove(var4, var3);
      }
   }

   interface ToDateParslet {
      void parse(ToDateParser var1, FormatTokenEnum var2, String var3);
   }
}
