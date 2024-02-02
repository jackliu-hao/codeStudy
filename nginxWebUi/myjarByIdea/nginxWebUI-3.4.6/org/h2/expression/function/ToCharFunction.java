package org.h2.expression.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;

public final class ToCharFunction extends FunctionN {
   public static final int JULIAN_EPOCH = -2440588;
   private static final int[] ROMAN_VALUES = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
   private static final String[] ROMAN_NUMERALS = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
   public static final int MONTHS = 0;
   public static final int SHORT_MONTHS = 1;
   public static final int WEEKDAYS = 2;
   public static final int SHORT_WEEKDAYS = 3;
   static final int AM_PM = 4;
   private static volatile String[][] NAMES;

   public static String toChar(BigDecimal var0, String var1, String var2) {
      String var3 = var1 != null ? StringUtils.toUpperEnglish(var1) : null;
      String var4;
      if (var3 != null && !var3.equals("TM") && !var3.equals("TM9")) {
         if (var3.equals("TME")) {
            int var27 = var0.precision() - var0.scale() - 1;
            var0 = var0.movePointLeft(var27);
            return var0.toPlainString() + "E" + (var27 < 0 ? '-' : '+') + (Math.abs(var27) < 10 ? "0" : "") + Math.abs(var27);
         } else {
            boolean var26;
            String var28;
            if (var3.equals("RN")) {
               var26 = var1.startsWith("r");
               var28 = StringUtils.pad(toRomanNumeral(var0.intValue()), 15, " ", false);
               return var26 ? var28.toLowerCase() : var28;
            } else if (var3.equals("FMRN")) {
               var26 = var1.charAt(2) == 'r';
               var28 = toRomanNumeral(var0.intValue());
               return var26 ? var28.toLowerCase() : var28;
            } else if (var3.endsWith("X")) {
               return toHex(var0, var1);
            } else {
               var4 = var1;
               DecimalFormatSymbols var5 = DecimalFormatSymbols.getInstance();
               char var6 = var5.getGroupingSeparator();
               char var7 = var5.getDecimalSeparator();
               boolean var8 = var3.startsWith("S");
               if (var8) {
                  var1 = var1.substring(1);
               }

               boolean var9 = var3.endsWith("S");
               if (var9) {
                  var1 = var1.substring(0, var1.length() - 1);
               }

               boolean var10 = var3.endsWith("MI");
               if (var10) {
                  var1 = var1.substring(0, var1.length() - 2);
               }

               boolean var11 = var3.endsWith("PR");
               if (var11) {
                  var1 = var1.substring(0, var1.length() - 2);
               }

               int var12 = var3.indexOf(86);
               int var14;
               if (var12 >= 0) {
                  int var13 = 0;

                  for(var14 = var12 + 1; var14 < var1.length(); ++var14) {
                     char var15 = var1.charAt(var14);
                     if (var15 == '0' || var15 == '9') {
                        ++var13;
                     }
                  }

                  var0 = var0.movePointRight(var13);
                  var1 = var1.substring(0, var12) + var1.substring(var12 + 1);
               }

               Integer var29;
               if (var1.endsWith("EEEE")) {
                  var29 = var0.precision() - var0.scale() - 1;
                  var0 = var0.movePointLeft(var29);
                  var1 = var1.substring(0, var1.length() - 4);
               } else {
                  var29 = null;
               }

               var14 = 1;
               boolean var30 = !var3.startsWith("FM");
               if (!var30) {
                  var1 = var1.substring(2);
               }

               var1 = var1.replaceAll("[Bb]", "");
               int var16 = findDecimalSeparator(var1);
               int var17 = calculateScale(var1, var16);
               int var18 = var0.scale();
               if (var17 < var18) {
                  var0 = var0.setScale(var17, RoundingMode.HALF_UP);
               } else if (var18 < 0) {
                  var0 = var0.setScale(0);
               }

               for(int var19 = var1.indexOf(48); var19 >= 0 && var19 < var16; ++var19) {
                  if (var1.charAt(var19) == '9') {
                     var1 = var1.substring(0, var19) + "0" + var1.substring(var19 + 1);
                  }
               }

               StringBuilder var31 = new StringBuilder();
               String var20 = (var0.abs().compareTo(BigDecimal.ONE) < 0 ? zeroesAfterDecimalSeparator(var0) : "") + var0.unscaledValue().abs().toString();
               int var21 = var16 - 1;

               int var22;
               char var23;
               char var24;
               for(var22 = var20.length() - var0.scale() - 1; var21 >= 0; --var21) {
                  var23 = var1.charAt(var21);
                  ++var14;
                  if (var23 != '9' && var23 != '0') {
                     if (var23 == ',') {
                        if (var22 >= 0 || var21 > 0 && var1.charAt(var21 - 1) == '0') {
                           var31.insert(0, var23);
                        }
                     } else if (var23 != 'G' && var23 != 'g') {
                        Currency var32;
                        if (var23 != 'C' && var23 != 'c') {
                           if (var23 != 'L' && var23 != 'l' && var23 != 'U' && var23 != 'u') {
                              if (var23 != '$') {
                                 throw DbException.get(90010, var4);
                              }

                              var32 = getCurrency();
                              String var25 = var32.getSymbol();
                              var31.insert(0, var25);
                           } else {
                              var32 = getCurrency();
                              var31.insert(0, var32.getSymbol());
                              var14 += 9;
                           }
                        } else {
                           var32 = getCurrency();
                           var31.insert(0, var32.getCurrencyCode());
                           var14 += 6;
                        }
                     } else if (var22 >= 0 || var21 > 0 && var1.charAt(var21 - 1) == '0') {
                        var31.insert(0, var6);
                     }
                  } else if (var22 >= 0) {
                     var24 = var20.charAt(var22);
                     var31.insert(0, var24);
                     --var22;
                  } else if (var23 == '0' && var29 == null) {
                     var31.insert(0, '0');
                  }
               }

               if (var22 >= 0) {
                  return StringUtils.pad("", var1.length() + 1, "#", true);
               } else {
                  if (var16 < var1.length()) {
                     ++var14;
                     var23 = var1.charAt(var16);
                     if (var23 != 'd' && var23 != 'D') {
                        var31.append(var23);
                     } else {
                        var31.append(var7);
                     }

                     var21 = var16 + 1;

                     for(var22 = var20.length() - var0.scale(); var21 < var1.length(); ++var21) {
                        var24 = var1.charAt(var21);
                        ++var14;
                        if (var24 != '9' && var24 != '0') {
                           throw DbException.get(90010, var4);
                        }

                        if (var22 < var20.length()) {
                           char var33 = var20.charAt(var22);
                           var31.append(var33);
                           ++var22;
                        } else if (var24 == '0' || var30) {
                           var31.append('0');
                        }
                     }
                  }

                  addSign(var31, var0.signum(), var8, var9, var10, var11, var30);
                  if (var29 != null) {
                     var31.append('E');
                     var31.append((char)(var29 < 0 ? '-' : '+'));
                     var31.append(Math.abs(var29) < 10 ? "0" : "");
                     var31.append(Math.abs(var29));
                  }

                  if (var30) {
                     if (var29 != null) {
                        var31.insert(0, ' ');
                     } else {
                        while(var31.length() < var14) {
                           var31.insert(0, ' ');
                        }
                     }
                  }

                  return var31.toString();
               }
            }
         }
      } else {
         var4 = var0.toPlainString();
         return var4.startsWith("0.") ? var4.substring(1) : var4;
      }
   }

   private static Currency getCurrency() {
      Locale var0 = Locale.getDefault();
      return Currency.getInstance(var0.getCountry().length() == 2 ? var0 : Locale.US);
   }

   private static String zeroesAfterDecimalSeparator(BigDecimal var0) {
      String var1 = var0.toPlainString();
      int var2 = var1.indexOf(46);
      if (var2 < 0) {
         return "";
      } else {
         int var3 = var2 + 1;
         boolean var4 = true;

         int var5;
         for(var5 = var1.length(); var3 < var5; ++var3) {
            if (var1.charAt(var3) != '0') {
               var4 = false;
               break;
            }
         }

         char[] var6 = new char[var4 ? var5 - var2 - 1 : var3 - 1 - var2];
         Arrays.fill(var6, '0');
         return String.valueOf(var6);
      }
   }

   private static void addSign(StringBuilder var0, int var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var5) {
         if (var1 < 0) {
            var0.insert(0, '<');
            var0.append('>');
         } else if (var6) {
            var0.insert(0, ' ');
            var0.append(' ');
         }
      } else {
         String var7;
         if (var1 == 0) {
            var7 = "";
         } else if (var1 < 0) {
            var7 = "-";
         } else if (!var2 && !var3) {
            if (var6) {
               var7 = " ";
            } else {
               var7 = "";
            }
         } else {
            var7 = "+";
         }

         if (!var4 && !var3) {
            var0.insert(0, var7);
         } else {
            var0.append(var7);
         }
      }

   }

   private static int findDecimalSeparator(String var0) {
      int var1 = var0.indexOf(46);
      if (var1 == -1) {
         var1 = var0.indexOf(68);
         if (var1 == -1) {
            var1 = var0.indexOf(100);
            if (var1 == -1) {
               var1 = var0.length();
            }
         }
      }

      return var1;
   }

   private static int calculateScale(String var0, int var1) {
      int var2 = 0;

      for(int var3 = var1; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         if (var4 == '0' || var4 == '9') {
            ++var2;
         }
      }

      return var2;
   }

   private static String toRomanNumeral(int var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < ROMAN_VALUES.length; ++var2) {
         int var3 = ROMAN_VALUES[var2];

         for(String var4 = ROMAN_NUMERALS[var2]; var0 >= var3; var0 -= var3) {
            var1.append(var4);
         }
      }

      return var1.toString();
   }

   private static String toHex(BigDecimal var0, String var1) {
      boolean var2 = !StringUtils.toUpperEnglish(var1).startsWith("FM");
      boolean var3 = !var1.contains("x");
      boolean var4 = var1.startsWith("0");
      int var5 = 0;

      int var6;
      for(var6 = 0; var6 < var1.length(); ++var6) {
         char var7 = var1.charAt(var6);
         if (var7 == '0' || var7 == 'X' || var7 == 'x') {
            ++var5;
         }
      }

      var6 = var0.setScale(0, RoundingMode.HALF_UP).intValue();
      String var8 = Integer.toHexString(var6);
      if (var5 < var8.length()) {
         var8 = StringUtils.pad("", var5 + 1, "#", true);
      } else {
         if (var3) {
            var8 = StringUtils.toUpperEnglish(var8);
         }

         if (var4) {
            var8 = StringUtils.pad(var8, var5, "0", false);
         }

         if (var2) {
            var8 = StringUtils.pad(var8, var1.length() + 1, " ", false);
         }
      }

      return var8;
   }

   public static String[] getDateNames(int var0) {
      String[][] var1 = NAMES;
      if (var1 == null) {
         var1 = new String[5][];
         DateFormatSymbols var2 = DateFormatSymbols.getInstance();
         var1[0] = var2.getMonths();
         String[] var3 = var2.getShortMonths();

         for(int var4 = 0; var4 < 12; ++var4) {
            String var5 = var3[var4];
            if (var5.endsWith(".")) {
               var3[var4] = var5.substring(0, var5.length() - 1);
            }
         }

         var1[1] = var3;
         var1[2] = var2.getWeekdays();
         var1[3] = var2.getShortWeekdays();
         var1[4] = var2.getAmPmStrings();
         NAMES = var1;
      }

      return var1[var0];
   }

   public static void clearNames() {
      NAMES = (String[][])null;
   }

   private static String getTimeZone(SessionLocal var0, Value var1, boolean var2) {
      if (var1 instanceof ValueTimestampTimeZone) {
         return DateTimeUtils.timeZoneNameFromOffsetSeconds(((ValueTimestampTimeZone)var1).getTimeZoneOffsetSeconds());
      } else if (var1 instanceof ValueTimeTimeZone) {
         return DateTimeUtils.timeZoneNameFromOffsetSeconds(((ValueTimeTimeZone)var1).getTimeZoneOffsetSeconds());
      } else {
         TimeZoneProvider var3 = var0.currentTimeZone();
         if (var2) {
            ValueTimestamp var4 = (ValueTimestamp)var1.convertTo(TypeInfo.TYPE_TIMESTAMP, var0);
            return var3.getShortId(var3.getEpochSecondsFromLocal(var4.getDateValue(), var4.getTimeNanos()));
         } else {
            return var3.getId();
         }
      }
   }

   public static String toCharDateTime(SessionLocal var0, Value var1, String var2, String var3) {
      long[] var4 = DateTimeUtils.dateAndTimeFromValue(var1, var0);
      long var5 = var4[0];
      long var7 = var4[1];
      int var9 = DateTimeUtils.yearFromDateValue(var5);
      int var10 = DateTimeUtils.monthFromDateValue(var5);
      int var11 = DateTimeUtils.dayFromDateValue(var5);
      int var12 = Math.abs(var9);
      int var13 = (int)(var7 / 1000000000L);
      int var14 = (int)(var7 - (long)(var13 * 1000000000));
      int var15 = var13 / 60;
      var13 -= var15 * 60;
      int var16 = var15 / 60;
      var15 -= var16 * 60;
      int var17 = (var16 + 11) % 12 + 1;
      boolean var18 = var16 < 12;
      if (var2 == null) {
         var2 = "DD-MON-YY HH.MI.SS.FF PM";
      }

      StringBuilder var19 = new StringBuilder();
      boolean var20 = true;
      int var21 = 0;
      int var22 = var2.length();

      while(true) {
         while(var21 < var22) {
            Capitalization var23;
            String var26;
            if ((var23 = containsAt(var2, var21, "A.D.", "B.C.")) != null) {
               var26 = var9 > 0 ? "A.D." : "B.C.";
               var19.append(var23.apply(var26));
               var21 += 4;
            } else if ((var23 = containsAt(var2, var21, "AD", "BC")) != null) {
               var26 = var9 > 0 ? "AD" : "BC";
               var19.append(var23.apply(var26));
               var21 += 2;
            } else if ((var23 = containsAt(var2, var21, "A.M.", "P.M.")) != null) {
               var26 = var18 ? "A.M." : "P.M.";
               var19.append(var23.apply(var26));
               var21 += 4;
            } else if ((var23 = containsAt(var2, var21, "AM", "PM")) != null) {
               var26 = var18 ? "AM" : "PM";
               var19.append(var23.apply(var26));
               var21 += 2;
            } else if (containsAt(var2, var21, "DL") != null) {
               var26 = getDateNames(2)[DateTimeUtils.getSundayDayOfWeek(var5)];
               String var27 = getDateNames(0)[var10 - 1];
               var19.append(var26).append(", ").append(var27).append(' ').append(var11).append(", ");
               StringUtils.appendZeroPadded(var19, 4, (long)var12);
               var21 += 2;
            } else if (containsAt(var2, var21, "DS") != null) {
               StringUtils.appendTwoDigits(var19, var10).append('/');
               StringUtils.appendTwoDigits(var19, var11).append('/');
               StringUtils.appendZeroPadded(var19, 4, (long)var12);
               var21 += 2;
            } else if (containsAt(var2, var21, "TS") != null) {
               var19.append(var17).append(':');
               StringUtils.appendTwoDigits(var19, var15).append(':');
               StringUtils.appendTwoDigits(var19, var13).append(' ').append(getDateNames(4)[var18 ? 0 : 1]);
               var21 += 2;
            } else if (containsAt(var2, var21, "DDD") != null) {
               var19.append(DateTimeUtils.getDayOfYear(var5));
               var21 += 3;
            } else if (containsAt(var2, var21, "DD") != null) {
               StringUtils.appendTwoDigits(var19, var11);
               var21 += 2;
            } else if ((var23 = containsAt(var2, var21, "DY")) != null) {
               var26 = getDateNames(3)[DateTimeUtils.getSundayDayOfWeek(var5)];
               var19.append(var23.apply(var26));
               var21 += 2;
            } else if ((var23 = containsAt(var2, var21, "DAY")) != null) {
               var26 = getDateNames(2)[DateTimeUtils.getSundayDayOfWeek(var5)];
               if (var20) {
                  var26 = StringUtils.pad(var26, "Wednesday".length(), " ", true);
               }

               var19.append(var23.apply(var26));
               var21 += 3;
            } else if (containsAt(var2, var21, "D") != null) {
               var19.append(DateTimeUtils.getSundayDayOfWeek(var5));
               ++var21;
            } else if (containsAt(var2, var21, "J") != null) {
               var19.append(DateTimeUtils.absoluteDayFromDateValue(var5) - -2440588L);
               ++var21;
            } else if (containsAt(var2, var21, "HH24") != null) {
               StringUtils.appendTwoDigits(var19, var16);
               var21 += 4;
            } else if (containsAt(var2, var21, "HH12") != null) {
               StringUtils.appendTwoDigits(var19, var17);
               var21 += 4;
            } else if (containsAt(var2, var21, "HH") != null) {
               StringUtils.appendTwoDigits(var19, var17);
               var21 += 2;
            } else if (containsAt(var2, var21, "MI") != null) {
               StringUtils.appendTwoDigits(var19, var15);
               var21 += 2;
            } else {
               int var24;
               if (containsAt(var2, var21, "SSSSS") != null) {
                  var24 = (int)(var7 / 1000000000L);
                  var19.append(var24);
                  var21 += 5;
               } else if (containsAt(var2, var21, "SS") != null) {
                  StringUtils.appendTwoDigits(var19, var13);
                  var21 += 2;
               } else if (containsAt(var2, var21, "FF1", "FF2", "FF3", "FF4", "FF5", "FF6", "FF7", "FF8", "FF9") != null) {
                  var24 = var2.charAt(var21 + 2) - 48;
                  int var25 = (int)((double)var14 * Math.pow(10.0, (double)(var24 - 9)));
                  StringUtils.appendZeroPadded(var19, var24, (long)var25);
                  var21 += 3;
               } else if (containsAt(var2, var21, "FF") != null) {
                  StringUtils.appendZeroPadded(var19, 9, (long)var14);
                  var21 += 2;
               } else if (containsAt(var2, var21, "TZR") != null) {
                  var19.append(getTimeZone(var0, var1, false));
                  var21 += 3;
               } else if (containsAt(var2, var21, "TZD") != null) {
                  var19.append(getTimeZone(var0, var1, true));
                  var21 += 3;
               } else if (containsAt(var2, var21, "TZH") != null) {
                  var24 = DateTimeFunction.extractDateTime(var0, var1, 6);
                  var19.append((char)(var24 < 0 ? '-' : '+'));
                  StringUtils.appendTwoDigits(var19, Math.abs(var24));
                  var21 += 3;
               } else if (containsAt(var2, var21, "TZM") != null) {
                  StringUtils.appendTwoDigits(var19, Math.abs(DateTimeFunction.extractDateTime(var0, var1, 7)));
                  var21 += 3;
               } else if (containsAt(var2, var21, "WW") != null) {
                  StringUtils.appendTwoDigits(var19, (DateTimeUtils.getDayOfYear(var5) - 1) / 7 + 1);
                  var21 += 2;
               } else if (containsAt(var2, var21, "IW") != null) {
                  StringUtils.appendTwoDigits(var19, DateTimeUtils.getIsoWeekOfYear(var5));
                  var21 += 2;
               } else if (containsAt(var2, var21, "W") != null) {
                  var19.append((var11 - 1) / 7 + 1);
                  ++var21;
               } else if (containsAt(var2, var21, "Y,YYY") != null) {
                  var19.append((new DecimalFormat("#,###")).format((long)var12));
                  var21 += 5;
               } else if (containsAt(var2, var21, "SYYYY") != null) {
                  if (var9 < 0) {
                     var19.append('-');
                  }

                  StringUtils.appendZeroPadded(var19, 4, (long)var12);
                  var21 += 5;
               } else if (containsAt(var2, var21, "YYYY", "RRRR") != null) {
                  StringUtils.appendZeroPadded(var19, 4, (long)var12);
                  var21 += 4;
               } else if (containsAt(var2, var21, "IYYY") != null) {
                  StringUtils.appendZeroPadded(var19, 4, (long)Math.abs(DateTimeUtils.getIsoWeekYear(var5)));
                  var21 += 4;
               } else if (containsAt(var2, var21, "YYY") != null) {
                  StringUtils.appendZeroPadded(var19, 3, (long)(var12 % 1000));
                  var21 += 3;
               } else if (containsAt(var2, var21, "IYY") != null) {
                  StringUtils.appendZeroPadded(var19, 3, (long)(Math.abs(DateTimeUtils.getIsoWeekYear(var5)) % 1000));
                  var21 += 3;
               } else if (containsAt(var2, var21, "YY", "RR") != null) {
                  StringUtils.appendTwoDigits(var19, var12 % 100);
                  var21 += 2;
               } else if (containsAt(var2, var21, "IY") != null) {
                  StringUtils.appendTwoDigits(var19, Math.abs(DateTimeUtils.getIsoWeekYear(var5)) % 100);
                  var21 += 2;
               } else if (containsAt(var2, var21, "Y") != null) {
                  var19.append(var12 % 10);
                  ++var21;
               } else if (containsAt(var2, var21, "I") != null) {
                  var19.append(Math.abs(DateTimeUtils.getIsoWeekYear(var5)) % 10);
                  ++var21;
               } else if ((var23 = containsAt(var2, var21, "MONTH")) != null) {
                  var26 = getDateNames(0)[var10 - 1];
                  if (var20) {
                     var26 = StringUtils.pad(var26, "September".length(), " ", true);
                  }

                  var19.append(var23.apply(var26));
                  var21 += 5;
               } else if ((var23 = containsAt(var2, var21, "MON")) != null) {
                  var26 = getDateNames(1)[var10 - 1];
                  var19.append(var23.apply(var26));
                  var21 += 3;
               } else if (containsAt(var2, var21, "MM") != null) {
                  StringUtils.appendTwoDigits(var19, var10);
                  var21 += 2;
               } else if ((var23 = containsAt(var2, var21, "RM")) != null) {
                  var19.append(var23.apply(toRomanNumeral(var10)));
                  var21 += 2;
               } else if (containsAt(var2, var21, "Q") != null) {
                  var24 = 1 + (var10 - 1) / 3;
                  var19.append(var24);
                  ++var21;
               } else if (containsAt(var2, var21, "X") != null) {
                  var24 = DecimalFormatSymbols.getInstance().getDecimalSeparator();
                  var19.append((char)var24);
                  ++var21;
               } else if (containsAt(var2, var21, "FM") != null) {
                  var20 = !var20;
                  var21 += 2;
               } else if (containsAt(var2, var21, "FX") != null) {
                  var21 += 2;
               } else if (containsAt(var2, var21, "\"") != null) {
                  ++var21;

                  while(var21 < var2.length()) {
                     var24 = var2.charAt(var21);
                     if (var24 == 34) {
                        ++var21;
                        break;
                     }

                     var19.append((char)var24);
                     ++var21;
                  }
               } else {
                  if (var2.charAt(var21) != '-' && var2.charAt(var21) != '/' && var2.charAt(var21) != ',' && var2.charAt(var21) != '.' && var2.charAt(var21) != ';' && var2.charAt(var21) != ':' && var2.charAt(var21) != ' ') {
                     throw DbException.get(90010, var2);
                  }

                  var19.append(var2.charAt(var21));
                  ++var21;
               }
            }
         }

         return var19.toString();
      }
   }

   private static Capitalization containsAt(String var0, int var1, String... var2) {
      String[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var1 + var6.length() <= var0.length()) {
            boolean var7 = true;
            Boolean var8 = null;
            Boolean var9 = null;

            for(int var10 = 0; var10 < var6.length(); ++var10) {
               char var11 = var0.charAt(var1 + var10);
               char var12 = var6.charAt(var10);
               if (var11 != var12 && Character.toUpperCase(var11) != Character.toUpperCase(var12)) {
                  var7 = false;
                  break;
               }

               if (Character.isLetter(var11)) {
                  if (var8 == null) {
                     var8 = Character.isUpperCase(var11);
                  } else if (var9 == null) {
                     var9 = Character.isUpperCase(var11);
                  }
               }
            }

            if (var7) {
               return ToCharFunction.Capitalization.toCapitalization(var8, var9);
            }
         }
      }

      return null;
   }

   public ToCharFunction(Expression var1, Expression var2, Expression var3) {
      super(var2 == null ? new Expression[]{var1} : (var3 == null ? new Expression[]{var1, var2} : new Expression[]{var1, var2, var3}));
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      switch (var2.getValueType()) {
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            var2 = ValueVarchar.get(toChar(var2.getBigDecimal(), var3 == null ? null : var3.getString(), var4 == null ? null : var4.getString()), var1);
            break;
         case 16:
         case 19:
         default:
            var2 = ValueVarchar.get(var2.getString(), var1);
            break;
         case 17:
         case 18:
         case 20:
         case 21:
            var2 = ValueVarchar.get(toCharDateTime(var1, var2, var3 == null ? null : var3.getString(), var4 == null ? null : var4.getString()), var1);
      }

      return var2;
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      this.type = TypeInfo.TYPE_VARCHAR;
      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return "TO_CHAR";
   }

   public static enum Capitalization {
      UPPERCASE,
      LOWERCASE,
      CAPITALIZE;

      static Capitalization toCapitalization(Boolean var0, Boolean var1) {
         if (var0 == null) {
            return CAPITALIZE;
         } else if (var1 == null) {
            return var0 ? UPPERCASE : LOWERCASE;
         } else if (var0) {
            return var1 ? UPPERCASE : CAPITALIZE;
         } else {
            return LOWERCASE;
         }
      }

      public String apply(String var1) {
         if (var1 != null && !var1.isEmpty()) {
            switch (this) {
               case UPPERCASE:
                  return StringUtils.toUpperEnglish(var1);
               case LOWERCASE:
                  return StringUtils.toLowerEnglish(var1);
               case CAPITALIZE:
                  return Character.toUpperCase(var1.charAt(0)) + (var1.length() > 1 ? StringUtils.toLowerEnglish(var1).substring(1) : "");
               default:
                  throw new IllegalArgumentException("Unknown capitalization strategy: " + this);
            }
         } else {
            return var1;
         }
      }
   }
}
