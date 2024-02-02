/*      */ package org.h2.expression.function;
/*      */ 
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.DateFormatSymbols;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.DecimalFormatSymbols;
/*      */ import java.util.Arrays;
/*      */ import java.util.Currency;
/*      */ import java.util.Locale;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.TypedValueExpression;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueTimeTimeZone;
/*      */ import org.h2.value.ValueTimestamp;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ import org.h2.value.ValueVarchar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ToCharFunction
/*      */   extends FunctionN
/*      */ {
/*      */   public static final int JULIAN_EPOCH = -2440588;
/*   43 */   private static final int[] ROMAN_VALUES = new int[] { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
/*      */ 
/*      */   
/*   46 */   private static final String[] ROMAN_NUMERALS = new String[] { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MONTHS = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SHORT_MONTHS = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int WEEKDAYS = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SHORT_WEEKDAYS = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int AM_PM = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static volatile String[][] NAMES;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toChar(BigDecimal paramBigDecimal, String paramString1, String paramString2) {
/*      */     Integer integer;
/*  159 */     String str1 = (paramString1 != null) ? StringUtils.toUpperEnglish(paramString1) : null;
/*  160 */     if (str1 == null || str1.equals("TM") || str1.equals("TM9")) {
/*  161 */       String str = paramBigDecimal.toPlainString();
/*  162 */       return str.startsWith("0.") ? str.substring(1) : str;
/*  163 */     }  if (str1.equals("TME")) {
/*  164 */       int i3 = paramBigDecimal.precision() - paramBigDecimal.scale() - 1;
/*  165 */       paramBigDecimal = paramBigDecimal.movePointLeft(i3);
/*  166 */       return paramBigDecimal.toPlainString() + "E" + ((i3 < 0) ? 45 : 43) + (
/*  167 */         (Math.abs(i3) < 10) ? "0" : "") + Math.abs(i3);
/*  168 */     }  if (str1.equals("RN")) {
/*  169 */       boolean bool5 = paramString1.startsWith("r");
/*  170 */       String str = StringUtils.pad(toRomanNumeral(paramBigDecimal.intValue()), 15, " ", false);
/*  171 */       return bool5 ? str.toLowerCase() : str;
/*  172 */     }  if (str1.equals("FMRN")) {
/*  173 */       boolean bool5 = (paramString1.charAt(2) == 'r') ? true : false;
/*  174 */       String str = toRomanNumeral(paramBigDecimal.intValue());
/*  175 */       return bool5 ? str.toLowerCase() : str;
/*  176 */     }  if (str1.endsWith("X")) {
/*  177 */       return toHex(paramBigDecimal, paramString1);
/*      */     }
/*      */     
/*  180 */     String str2 = paramString1;
/*  181 */     DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
/*  182 */     char c1 = decimalFormatSymbols.getGroupingSeparator();
/*  183 */     char c2 = decimalFormatSymbols.getDecimalSeparator();
/*      */     
/*  185 */     boolean bool1 = str1.startsWith("S");
/*  186 */     if (bool1) {
/*  187 */       paramString1 = paramString1.substring(1);
/*      */     }
/*      */     
/*  190 */     boolean bool2 = str1.endsWith("S");
/*  191 */     if (bool2) {
/*  192 */       paramString1 = paramString1.substring(0, paramString1.length() - 1);
/*      */     }
/*      */     
/*  195 */     boolean bool3 = str1.endsWith("MI");
/*  196 */     if (bool3) {
/*  197 */       paramString1 = paramString1.substring(0, paramString1.length() - 2);
/*      */     }
/*      */     
/*  200 */     boolean bool4 = str1.endsWith("PR");
/*  201 */     if (bool4) {
/*  202 */       paramString1 = paramString1.substring(0, paramString1.length() - 2);
/*      */     }
/*      */     
/*  205 */     int i = str1.indexOf('V');
/*  206 */     if (i >= 0) {
/*  207 */       byte b1 = 0;
/*  208 */       for (int i3 = i + 1; i3 < paramString1.length(); i3++) {
/*  209 */         char c = paramString1.charAt(i3);
/*  210 */         if (c == '0' || c == '9') {
/*  211 */           b1++;
/*      */         }
/*      */       } 
/*  214 */       paramBigDecimal = paramBigDecimal.movePointRight(b1);
/*  215 */       paramString1 = paramString1.substring(0, i) + paramString1.substring(i + 1);
/*      */     } 
/*      */ 
/*      */     
/*  219 */     if (paramString1.endsWith("EEEE")) {
/*  220 */       integer = Integer.valueOf(paramBigDecimal.precision() - paramBigDecimal.scale() - 1);
/*  221 */       paramBigDecimal = paramBigDecimal.movePointLeft(integer.intValue());
/*  222 */       paramString1 = paramString1.substring(0, paramString1.length() - 4);
/*      */     } else {
/*  224 */       integer = null;
/*      */     } 
/*      */     
/*  227 */     byte b = 1;
/*  228 */     boolean bool = !str1.startsWith("FM") ? true : false;
/*  229 */     if (!bool) {
/*  230 */       paramString1 = paramString1.substring(2);
/*      */     }
/*      */ 
/*      */     
/*  234 */     paramString1 = paramString1.replaceAll("[Bb]", "");
/*      */ 
/*      */ 
/*      */     
/*  238 */     int j = findDecimalSeparator(paramString1);
/*  239 */     int k = calculateScale(paramString1, j);
/*  240 */     int m = paramBigDecimal.scale();
/*  241 */     if (k < m) {
/*  242 */       paramBigDecimal = paramBigDecimal.setScale(k, RoundingMode.HALF_UP);
/*  243 */     } else if (m < 0) {
/*  244 */       paramBigDecimal = paramBigDecimal.setScale(0);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  249 */     for (int n = paramString1.indexOf('0'); n >= 0 && n < j; n++) {
/*  250 */       if (paramString1.charAt(n) == '9') {
/*  251 */         paramString1 = paramString1.substring(0, n) + "0" + paramString1.substring(n + 1);
/*      */       }
/*      */     } 
/*      */     
/*  255 */     StringBuilder stringBuilder = new StringBuilder();
/*      */ 
/*      */     
/*  258 */     String str3 = ((paramBigDecimal.abs().compareTo(BigDecimal.ONE) < 0) ? zeroesAfterDecimalSeparator(paramBigDecimal) : "") + paramBigDecimal.unscaledValue().abs().toString();
/*      */ 
/*      */ 
/*      */     
/*  262 */     int i1 = j - 1;
/*  263 */     int i2 = str3.length() - paramBigDecimal.scale() - 1;
/*  264 */     for (; i1 >= 0; i1--) {
/*  265 */       char c = paramString1.charAt(i1);
/*  266 */       b++;
/*  267 */       if (c == '9' || c == '0') {
/*  268 */         if (i2 >= 0) {
/*  269 */           char c3 = str3.charAt(i2);
/*  270 */           stringBuilder.insert(0, c3);
/*  271 */           i2--;
/*  272 */         } else if (c == '0' && integer == null) {
/*  273 */           stringBuilder.insert(0, '0');
/*      */         } 
/*  275 */       } else if (c == ',') {
/*      */         
/*  277 */         if (i2 >= 0 || (i1 > 0 && paramString1.charAt(i1 - 1) == '0')) {
/*  278 */           stringBuilder.insert(0, c);
/*      */         }
/*  280 */       } else if (c == 'G' || c == 'g') {
/*      */         
/*  282 */         if (i2 >= 0 || (i1 > 0 && paramString1.charAt(i1 - 1) == '0')) {
/*  283 */           stringBuilder.insert(0, c1);
/*      */         }
/*  285 */       } else if (c == 'C' || c == 'c') {
/*  286 */         Currency currency = getCurrency();
/*  287 */         stringBuilder.insert(0, currency.getCurrencyCode());
/*  288 */         b += 6;
/*  289 */       } else if (c == 'L' || c == 'l' || c == 'U' || c == 'u') {
/*  290 */         Currency currency = getCurrency();
/*  291 */         stringBuilder.insert(0, currency.getSymbol());
/*  292 */         b += 9;
/*  293 */       } else if (c == '$') {
/*  294 */         Currency currency = getCurrency();
/*  295 */         String str = currency.getSymbol();
/*  296 */         stringBuilder.insert(0, str);
/*      */       } else {
/*  298 */         throw DbException.get(90010, str2);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  305 */     if (i2 >= 0) {
/*  306 */       return StringUtils.pad("", paramString1.length() + 1, "#", true);
/*      */     }
/*      */     
/*  309 */     if (j < paramString1.length()) {
/*      */ 
/*      */       
/*  312 */       b++;
/*  313 */       char c = paramString1.charAt(j);
/*  314 */       if (c == 'd' || c == 'D') {
/*  315 */         stringBuilder.append(c2);
/*      */       } else {
/*  317 */         stringBuilder.append(c);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  322 */       i1 = j + 1;
/*  323 */       i2 = str3.length() - paramBigDecimal.scale();
/*  324 */       for (; i1 < paramString1.length(); i1++) {
/*  325 */         char c3 = paramString1.charAt(i1);
/*  326 */         b++;
/*  327 */         if (c3 == '9' || c3 == '0') {
/*  328 */           if (i2 < str3.length()) {
/*  329 */             char c4 = str3.charAt(i2);
/*  330 */             stringBuilder.append(c4);
/*  331 */             i2++;
/*      */           }
/*  333 */           else if (c3 == '0' || bool) {
/*  334 */             stringBuilder.append('0');
/*      */           } 
/*      */         } else {
/*      */           
/*  338 */           throw DbException.get(90010, str2);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  344 */     addSign(stringBuilder, paramBigDecimal.signum(), bool1, bool2, bool3, bool4, bool);
/*      */ 
/*      */     
/*  347 */     if (integer != null) {
/*  348 */       stringBuilder.append('E');
/*  349 */       stringBuilder.append((integer.intValue() < 0) ? 45 : 43);
/*  350 */       stringBuilder.append((Math.abs(integer.intValue()) < 10) ? "0" : "");
/*  351 */       stringBuilder.append(Math.abs(integer.intValue()));
/*      */     } 
/*      */     
/*  354 */     if (bool) {
/*  355 */       if (integer != null) {
/*  356 */         stringBuilder.insert(0, ' ');
/*      */       } else {
/*  358 */         while (stringBuilder.length() < b) {
/*  359 */           stringBuilder.insert(0, ' ');
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/*  364 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */   private static Currency getCurrency() {
/*  368 */     Locale locale = Locale.getDefault();
/*  369 */     return Currency.getInstance((locale.getCountry().length() == 2) ? locale : Locale.US);
/*      */   }
/*      */   
/*      */   private static String zeroesAfterDecimalSeparator(BigDecimal paramBigDecimal) {
/*  373 */     String str = paramBigDecimal.toPlainString();
/*  374 */     int i = str.indexOf('.');
/*  375 */     if (i < 0) {
/*  376 */       return "";
/*      */     }
/*  378 */     int j = i + 1;
/*  379 */     boolean bool = true;
/*  380 */     int k = str.length();
/*  381 */     for (; j < k; j++) {
/*  382 */       if (str.charAt(j) != '0') {
/*  383 */         bool = false;
/*      */         break;
/*      */       } 
/*      */     } 
/*  387 */     char[] arrayOfChar = new char[bool ? (k - i - 1) : (j - 1 - i)];
/*  388 */     Arrays.fill(arrayOfChar, '0');
/*  389 */     return String.valueOf(arrayOfChar);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void addSign(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) {
/*  395 */     if (paramBoolean4) {
/*  396 */       if (paramInt < 0) {
/*  397 */         paramStringBuilder.insert(0, '<');
/*  398 */         paramStringBuilder.append('>');
/*  399 */       } else if (paramBoolean5) {
/*  400 */         paramStringBuilder.insert(0, ' ');
/*  401 */         paramStringBuilder.append(' ');
/*      */       } 
/*      */     } else {
/*      */       String str;
/*  405 */       if (paramInt == 0) {
/*  406 */         str = "";
/*  407 */       } else if (paramInt < 0) {
/*  408 */         str = "-";
/*      */       }
/*  410 */       else if (paramBoolean1 || paramBoolean2) {
/*  411 */         str = "+";
/*  412 */       } else if (paramBoolean5) {
/*  413 */         str = " ";
/*      */       } else {
/*  415 */         str = "";
/*      */       } 
/*      */       
/*  418 */       if (paramBoolean3 || paramBoolean2) {
/*  419 */         paramStringBuilder.append(str);
/*      */       } else {
/*  421 */         paramStringBuilder.insert(0, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static int findDecimalSeparator(String paramString) {
/*  427 */     int i = paramString.indexOf('.');
/*  428 */     if (i == -1) {
/*  429 */       i = paramString.indexOf('D');
/*  430 */       if (i == -1) {
/*  431 */         i = paramString.indexOf('d');
/*  432 */         if (i == -1) {
/*  433 */           i = paramString.length();
/*      */         }
/*      */       } 
/*      */     } 
/*  437 */     return i;
/*      */   }
/*      */   
/*      */   private static int calculateScale(String paramString, int paramInt) {
/*  441 */     byte b = 0;
/*  442 */     for (int i = paramInt; i < paramString.length(); i++) {
/*  443 */       char c = paramString.charAt(i);
/*  444 */       if (c == '0' || c == '9') {
/*  445 */         b++;
/*      */       }
/*      */     } 
/*  448 */     return b;
/*      */   }
/*      */   
/*      */   private static String toRomanNumeral(int paramInt) {
/*  452 */     StringBuilder stringBuilder = new StringBuilder();
/*  453 */     for (byte b = 0; b < ROMAN_VALUES.length; b++) {
/*  454 */       int i = ROMAN_VALUES[b];
/*  455 */       String str = ROMAN_NUMERALS[b];
/*  456 */       while (paramInt >= i) {
/*  457 */         stringBuilder.append(str);
/*  458 */         paramInt -= i;
/*      */       } 
/*      */     } 
/*  461 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private static String toHex(BigDecimal paramBigDecimal, String paramString) {
/*  466 */     boolean bool1 = !StringUtils.toUpperEnglish(paramString).startsWith("FM") ? true : false;
/*  467 */     boolean bool2 = !paramString.contains("x") ? true : false;
/*  468 */     boolean bool = paramString.startsWith("0");
/*  469 */     byte b = 0; int i;
/*  470 */     for (i = 0; i < paramString.length(); i++) {
/*  471 */       char c = paramString.charAt(i);
/*  472 */       if (c == '0' || c == 'X' || c == 'x') {
/*  473 */         b++;
/*      */       }
/*      */     } 
/*      */     
/*  477 */     i = paramBigDecimal.setScale(0, RoundingMode.HALF_UP).intValue();
/*  478 */     String str = Integer.toHexString(i);
/*  479 */     if (b < str.length()) {
/*  480 */       str = StringUtils.pad("", b + 1, "#", true);
/*      */     } else {
/*  482 */       if (bool2) {
/*  483 */         str = StringUtils.toUpperEnglish(str);
/*      */       }
/*  485 */       if (bool) {
/*  486 */         str = StringUtils.pad(str, b, "0", false);
/*      */       }
/*  488 */       if (bool1) {
/*  489 */         str = StringUtils.pad(str, paramString.length() + 1, " ", false);
/*      */       }
/*      */     } 
/*      */     
/*  493 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] getDateNames(int paramInt) {
/*  503 */     String[][] arrayOfString = NAMES;
/*  504 */     if (arrayOfString == null) {
/*  505 */       arrayOfString = new String[5][];
/*  506 */       DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance();
/*  507 */       arrayOfString[0] = dateFormatSymbols.getMonths();
/*  508 */       String[] arrayOfString1 = dateFormatSymbols.getShortMonths();
/*  509 */       for (byte b = 0; b < 12; b++) {
/*  510 */         String str = arrayOfString1[b];
/*  511 */         if (str.endsWith(".")) {
/*  512 */           arrayOfString1[b] = str.substring(0, str.length() - 1);
/*      */         }
/*      */       } 
/*  515 */       arrayOfString[1] = arrayOfString1;
/*  516 */       arrayOfString[2] = dateFormatSymbols.getWeekdays();
/*  517 */       arrayOfString[3] = dateFormatSymbols.getShortWeekdays();
/*  518 */       arrayOfString[4] = dateFormatSymbols.getAmPmStrings();
/*  519 */       NAMES = arrayOfString;
/*      */     } 
/*  521 */     return arrayOfString[paramInt];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void clearNames() {
/*  528 */     NAMES = (String[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String getTimeZone(SessionLocal paramSessionLocal, Value paramValue, boolean paramBoolean) {
/*  545 */     if (paramValue instanceof ValueTimestampTimeZone)
/*  546 */       return DateTimeUtils.timeZoneNameFromOffsetSeconds(((ValueTimestampTimeZone)paramValue)
/*  547 */           .getTimeZoneOffsetSeconds()); 
/*  548 */     if (paramValue instanceof ValueTimeTimeZone) {
/*  549 */       return DateTimeUtils.timeZoneNameFromOffsetSeconds(((ValueTimeTimeZone)paramValue)
/*  550 */           .getTimeZoneOffsetSeconds());
/*      */     }
/*  552 */     TimeZoneProvider timeZoneProvider = paramSessionLocal.currentTimeZone();
/*  553 */     if (paramBoolean) {
/*  554 */       ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP, (CastDataProvider)paramSessionLocal);
/*  555 */       return timeZoneProvider.getShortId(timeZoneProvider.getEpochSecondsFromLocal(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos()));
/*      */     } 
/*  557 */     return timeZoneProvider.getId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toCharDateTime(SessionLocal paramSessionLocal, Value paramValue, String paramString1, String paramString2) {
/*  703 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue, (CastDataProvider)paramSessionLocal);
/*  704 */     long l1 = arrayOfLong[0];
/*  705 */     long l2 = arrayOfLong[1];
/*  706 */     int i = DateTimeUtils.yearFromDateValue(l1);
/*  707 */     int j = DateTimeUtils.monthFromDateValue(l1);
/*  708 */     int k = DateTimeUtils.dayFromDateValue(l1);
/*  709 */     int m = Math.abs(i);
/*  710 */     int n = (int)(l2 / 1000000000L);
/*  711 */     int i1 = (int)(l2 - (n * 1000000000));
/*  712 */     int i2 = n / 60;
/*  713 */     n -= i2 * 60;
/*  714 */     int i3 = i2 / 60;
/*  715 */     i2 -= i3 * 60;
/*  716 */     int i4 = (i3 + 11) % 12 + 1;
/*  717 */     boolean bool1 = (i3 < 12) ? true : false;
/*  718 */     if (paramString1 == null) {
/*  719 */       paramString1 = "DD-MON-YY HH.MI.SS.FF PM";
/*      */     }
/*      */     
/*  722 */     StringBuilder stringBuilder = new StringBuilder();
/*  723 */     boolean bool2 = true;
/*      */     
/*  725 */     for (int i5 = 0, i6 = paramString1.length(); i5 < i6; ) {
/*      */       Capitalization capitalization;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  731 */       if ((capitalization = containsAt(paramString1, i5, new String[] { "A.D.", "B.C." })) != null) {
/*  732 */         String str = (i > 0) ? "A.D." : "B.C.";
/*  733 */         stringBuilder.append(capitalization.apply(str));
/*  734 */         i5 += 4; continue;
/*  735 */       }  if ((capitalization = containsAt(paramString1, i5, new String[] { "AD", "BC" })) != null) {
/*  736 */         String str = (i > 0) ? "AD" : "BC";
/*  737 */         stringBuilder.append(capitalization.apply(str));
/*  738 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  742 */       if ((capitalization = containsAt(paramString1, i5, new String[] { "A.M.", "P.M." })) != null) {
/*  743 */         String str = bool1 ? "A.M." : "P.M.";
/*  744 */         stringBuilder.append(capitalization.apply(str));
/*  745 */         i5 += 4; continue;
/*  746 */       }  if ((capitalization = containsAt(paramString1, i5, new String[] { "AM", "PM" })) != null) {
/*  747 */         String str = bool1 ? "AM" : "PM";
/*  748 */         stringBuilder.append(capitalization.apply(str));
/*  749 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  753 */       if (containsAt(paramString1, i5, new String[] { "DL" }) != null) {
/*  754 */         String str1 = getDateNames(2)[DateTimeUtils.getSundayDayOfWeek(l1)];
/*  755 */         String str2 = getDateNames(0)[j - 1];
/*  756 */         stringBuilder.append(str1).append(", ").append(str2).append(' ').append(k).append(", ");
/*  757 */         StringUtils.appendZeroPadded(stringBuilder, 4, m);
/*  758 */         i5 += 2; continue;
/*  759 */       }  if (containsAt(paramString1, i5, new String[] { "DS" }) != null) {
/*  760 */         StringUtils.appendTwoDigits(stringBuilder, j).append('/');
/*  761 */         StringUtils.appendTwoDigits(stringBuilder, k).append('/');
/*  762 */         StringUtils.appendZeroPadded(stringBuilder, 4, m);
/*  763 */         i5 += 2; continue;
/*  764 */       }  if (containsAt(paramString1, i5, new String[] { "TS" }) != null) {
/*  765 */         stringBuilder.append(i4).append(':');
/*  766 */         StringUtils.appendTwoDigits(stringBuilder, i2).append(':');
/*  767 */         StringUtils.appendTwoDigits(stringBuilder, n).append(' ').append(getDateNames(4)[bool1 ? 0 : 1]);
/*  768 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  772 */       if (containsAt(paramString1, i5, new String[] { "DDD" }) != null) {
/*  773 */         stringBuilder.append(DateTimeUtils.getDayOfYear(l1));
/*  774 */         i5 += 3; continue;
/*  775 */       }  if (containsAt(paramString1, i5, new String[] { "DD" }) != null) {
/*  776 */         StringUtils.appendTwoDigits(stringBuilder, k);
/*  777 */         i5 += 2; continue;
/*  778 */       }  if ((capitalization = containsAt(paramString1, i5, new String[] { "DY" })) != null) {
/*  779 */         String str = getDateNames(3)[DateTimeUtils.getSundayDayOfWeek(l1)];
/*  780 */         stringBuilder.append(capitalization.apply(str));
/*  781 */         i5 += 2; continue;
/*  782 */       }  if ((capitalization = containsAt(paramString1, i5, new String[] { "DAY" })) != null) {
/*  783 */         String str = getDateNames(2)[DateTimeUtils.getSundayDayOfWeek(l1)];
/*  784 */         if (bool2) {
/*  785 */           str = StringUtils.pad(str, "Wednesday".length(), " ", true);
/*      */         }
/*  787 */         stringBuilder.append(capitalization.apply(str));
/*  788 */         i5 += 3; continue;
/*  789 */       }  if (containsAt(paramString1, i5, new String[] { "D" }) != null) {
/*  790 */         stringBuilder.append(DateTimeUtils.getSundayDayOfWeek(l1));
/*  791 */         i5++; continue;
/*  792 */       }  if (containsAt(paramString1, i5, new String[] { "J" }) != null) {
/*  793 */         stringBuilder.append(DateTimeUtils.absoluteDayFromDateValue(l1) - -2440588L);
/*  794 */         i5++;
/*      */         
/*      */         continue;
/*      */       } 
/*  798 */       if (containsAt(paramString1, i5, new String[] { "HH24" }) != null) {
/*  799 */         StringUtils.appendTwoDigits(stringBuilder, i3);
/*  800 */         i5 += 4; continue;
/*  801 */       }  if (containsAt(paramString1, i5, new String[] { "HH12" }) != null) {
/*  802 */         StringUtils.appendTwoDigits(stringBuilder, i4);
/*  803 */         i5 += 4; continue;
/*  804 */       }  if (containsAt(paramString1, i5, new String[] { "HH" }) != null) {
/*  805 */         StringUtils.appendTwoDigits(stringBuilder, i4);
/*  806 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  810 */       if (containsAt(paramString1, i5, new String[] { "MI" }) != null) {
/*  811 */         StringUtils.appendTwoDigits(stringBuilder, i2);
/*  812 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  816 */       if (containsAt(paramString1, i5, new String[] { "SSSSS" }) != null) {
/*  817 */         int i7 = (int)(l2 / 1000000000L);
/*  818 */         stringBuilder.append(i7);
/*  819 */         i5 += 5; continue;
/*  820 */       }  if (containsAt(paramString1, i5, new String[] { "SS" }) != null) {
/*  821 */         StringUtils.appendTwoDigits(stringBuilder, n);
/*  822 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  826 */       if (containsAt(paramString1, i5, new String[] { "FF1", "FF2", "FF3", "FF4", "FF5", "FF6", "FF7", "FF8", "FF9" }) != null) {
/*      */         
/*  828 */         int i7 = paramString1.charAt(i5 + 2) - 48;
/*  829 */         int i8 = (int)(i1 * Math.pow(10.0D, (i7 - 9)));
/*  830 */         StringUtils.appendZeroPadded(stringBuilder, i7, i8);
/*  831 */         i5 += 3; continue;
/*  832 */       }  if (containsAt(paramString1, i5, new String[] { "FF" }) != null) {
/*  833 */         StringUtils.appendZeroPadded(stringBuilder, 9, i1);
/*  834 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  838 */       if (containsAt(paramString1, i5, new String[] { "TZR" }) != null) {
/*  839 */         stringBuilder.append(getTimeZone(paramSessionLocal, paramValue, false));
/*  840 */         i5 += 3; continue;
/*  841 */       }  if (containsAt(paramString1, i5, new String[] { "TZD" }) != null) {
/*  842 */         stringBuilder.append(getTimeZone(paramSessionLocal, paramValue, true));
/*  843 */         i5 += 3; continue;
/*  844 */       }  if (containsAt(paramString1, i5, new String[] { "TZH" }) != null) {
/*  845 */         int i7 = DateTimeFunction.extractDateTime(paramSessionLocal, paramValue, 6);
/*  846 */         stringBuilder.append((i7 < 0) ? 45 : 43);
/*  847 */         StringUtils.appendTwoDigits(stringBuilder, Math.abs(i7));
/*  848 */         i5 += 3; continue;
/*      */       } 
/*  850 */       if (containsAt(paramString1, i5, new String[] { "TZM" }) != null) {
/*  851 */         StringUtils.appendTwoDigits(stringBuilder, 
/*  852 */             Math.abs(DateTimeFunction.extractDateTime(paramSessionLocal, paramValue, 7)));
/*  853 */         i5 += 3;
/*      */         continue;
/*      */       } 
/*  856 */       if (containsAt(paramString1, i5, new String[] { "WW" }) != null) {
/*  857 */         StringUtils.appendTwoDigits(stringBuilder, (DateTimeUtils.getDayOfYear(l1) - 1) / 7 + 1);
/*  858 */         i5 += 2; continue;
/*  859 */       }  if (containsAt(paramString1, i5, new String[] { "IW" }) != null) {
/*  860 */         StringUtils.appendTwoDigits(stringBuilder, DateTimeUtils.getIsoWeekOfYear(l1));
/*  861 */         i5 += 2; continue;
/*  862 */       }  if (containsAt(paramString1, i5, new String[] { "W" }) != null) {
/*  863 */         stringBuilder.append((k - 1) / 7 + 1);
/*  864 */         i5++;
/*      */         
/*      */         continue;
/*      */       } 
/*  868 */       if (containsAt(paramString1, i5, new String[] { "Y,YYY" }) != null) {
/*  869 */         stringBuilder.append((new DecimalFormat("#,###")).format(m));
/*  870 */         i5 += 5; continue;
/*  871 */       }  if (containsAt(paramString1, i5, new String[] { "SYYYY" }) != null) {
/*      */         
/*  873 */         if (i < 0) {
/*  874 */           stringBuilder.append('-');
/*      */         }
/*  876 */         StringUtils.appendZeroPadded(stringBuilder, 4, m);
/*  877 */         i5 += 5; continue;
/*  878 */       }  if (containsAt(paramString1, i5, new String[] { "YYYY", "RRRR" }) != null) {
/*  879 */         StringUtils.appendZeroPadded(stringBuilder, 4, m);
/*  880 */         i5 += 4; continue;
/*  881 */       }  if (containsAt(paramString1, i5, new String[] { "IYYY" }) != null) {
/*  882 */         StringUtils.appendZeroPadded(stringBuilder, 4, Math.abs(DateTimeUtils.getIsoWeekYear(l1)));
/*  883 */         i5 += 4; continue;
/*  884 */       }  if (containsAt(paramString1, i5, new String[] { "YYY" }) != null) {
/*  885 */         StringUtils.appendZeroPadded(stringBuilder, 3, (m % 1000));
/*  886 */         i5 += 3; continue;
/*  887 */       }  if (containsAt(paramString1, i5, new String[] { "IYY" }) != null) {
/*  888 */         StringUtils.appendZeroPadded(stringBuilder, 3, (Math.abs(DateTimeUtils.getIsoWeekYear(l1)) % 1000));
/*  889 */         i5 += 3; continue;
/*  890 */       }  if (containsAt(paramString1, i5, new String[] { "YY", "RR" }) != null) {
/*  891 */         StringUtils.appendTwoDigits(stringBuilder, m % 100);
/*  892 */         i5 += 2; continue;
/*  893 */       }  if (containsAt(paramString1, i5, new String[] { "IY" }) != null) {
/*  894 */         StringUtils.appendTwoDigits(stringBuilder, Math.abs(DateTimeUtils.getIsoWeekYear(l1)) % 100);
/*  895 */         i5 += 2; continue;
/*  896 */       }  if (containsAt(paramString1, i5, new String[] { "Y" }) != null) {
/*  897 */         stringBuilder.append(m % 10);
/*  898 */         i5++; continue;
/*  899 */       }  if (containsAt(paramString1, i5, new String[] { "I" }) != null) {
/*  900 */         stringBuilder.append(Math.abs(DateTimeUtils.getIsoWeekYear(l1)) % 10);
/*  901 */         i5++;
/*      */         
/*      */         continue;
/*      */       } 
/*  905 */       if ((capitalization = containsAt(paramString1, i5, new String[] { "MONTH" })) != null) {
/*  906 */         String str = getDateNames(0)[j - 1];
/*  907 */         if (bool2) {
/*  908 */           str = StringUtils.pad(str, "September".length(), " ", true);
/*      */         }
/*  910 */         stringBuilder.append(capitalization.apply(str));
/*  911 */         i5 += 5; continue;
/*  912 */       }  if ((capitalization = containsAt(paramString1, i5, new String[] { "MON" })) != null) {
/*  913 */         String str = getDateNames(1)[j - 1];
/*  914 */         stringBuilder.append(capitalization.apply(str));
/*  915 */         i5 += 3; continue;
/*  916 */       }  if (containsAt(paramString1, i5, new String[] { "MM" }) != null) {
/*  917 */         StringUtils.appendTwoDigits(stringBuilder, j);
/*  918 */         i5 += 2; continue;
/*  919 */       }  if ((capitalization = containsAt(paramString1, i5, new String[] { "RM" })) != null) {
/*  920 */         stringBuilder.append(capitalization.apply(toRomanNumeral(j)));
/*  921 */         i5 += 2; continue;
/*  922 */       }  if (containsAt(paramString1, i5, new String[] { "Q" }) != null) {
/*  923 */         int i7 = 1 + (j - 1) / 3;
/*  924 */         stringBuilder.append(i7);
/*  925 */         i5++;
/*      */         
/*      */         continue;
/*      */       } 
/*  929 */       if (containsAt(paramString1, i5, new String[] { "X" }) != null) {
/*  930 */         char c = DecimalFormatSymbols.getInstance().getDecimalSeparator();
/*  931 */         stringBuilder.append(c);
/*  932 */         i5++;
/*      */         
/*      */         continue;
/*      */       } 
/*  936 */       if (containsAt(paramString1, i5, new String[] { "FM" }) != null) {
/*  937 */         bool2 = !bool2 ? true : false;
/*  938 */         i5 += 2; continue;
/*  939 */       }  if (containsAt(paramString1, i5, new String[] { "FX" }) != null) {
/*  940 */         i5 += 2;
/*      */         
/*      */         continue;
/*      */       } 
/*  944 */       if (containsAt(paramString1, i5, new String[] { "\"" }) != null) {
/*  945 */         for (; ++i5 < paramString1.length(); i5++) {
/*  946 */           char c = paramString1.charAt(i5);
/*  947 */           if (c != '"') {
/*  948 */             stringBuilder.append(c);
/*      */           } else {
/*  950 */             i5++; break;
/*      */           } 
/*      */         }  continue;
/*      */       } 
/*  954 */       if (paramString1.charAt(i5) == '-' || paramString1
/*  955 */         .charAt(i5) == '/' || paramString1
/*  956 */         .charAt(i5) == ',' || paramString1
/*  957 */         .charAt(i5) == '.' || paramString1
/*  958 */         .charAt(i5) == ';' || paramString1
/*  959 */         .charAt(i5) == ':' || paramString1
/*  960 */         .charAt(i5) == ' ') {
/*  961 */         stringBuilder.append(paramString1.charAt(i5));
/*  962 */         i5++;
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/*  967 */       throw DbException.get(90010, paramString1);
/*      */     } 
/*      */ 
/*      */     
/*  971 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Capitalization containsAt(String paramString, int paramInt, String... paramVarArgs) {
/*  990 */     for (String str : paramVarArgs) {
/*  991 */       if (paramInt + str.length() <= paramString.length()) {
/*  992 */         boolean bool = true;
/*  993 */         Boolean bool1 = null;
/*  994 */         Boolean bool2 = null;
/*  995 */         for (byte b = 0; b < str.length(); b++) {
/*  996 */           char c1 = paramString.charAt(paramInt + b);
/*  997 */           char c2 = str.charAt(b);
/*  998 */           if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
/*  999 */             bool = false; break;
/*      */           } 
/* 1001 */           if (Character.isLetter(c1)) {
/* 1002 */             if (bool1 == null) {
/* 1003 */               bool1 = Boolean.valueOf(Character.isUpperCase(c1));
/* 1004 */             } else if (bool2 == null) {
/* 1005 */               bool2 = Boolean.valueOf(Character.isUpperCase(c1));
/*      */             } 
/*      */           }
/*      */         } 
/* 1009 */         if (bool) {
/* 1010 */           return Capitalization.toCapitalization(bool1, bool2);
/*      */         }
/*      */       } 
/*      */     } 
/* 1014 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public enum Capitalization
/*      */   {
/* 1023 */     UPPERCASE,
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1028 */     LOWERCASE,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1034 */     CAPITALIZE;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static Capitalization toCapitalization(Boolean param1Boolean1, Boolean param1Boolean2) {
/* 1046 */       if (param1Boolean1 == null)
/* 1047 */         return CAPITALIZE; 
/* 1048 */       if (param1Boolean2 == null)
/* 1049 */         return param1Boolean1.booleanValue() ? UPPERCASE : LOWERCASE; 
/* 1050 */       if (param1Boolean1.booleanValue()) {
/* 1051 */         return param1Boolean2.booleanValue() ? UPPERCASE : CAPITALIZE;
/*      */       }
/* 1053 */       return LOWERCASE;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String apply(String param1String) {
/* 1064 */       if (param1String == null || param1String.isEmpty()) {
/* 1065 */         return param1String;
/*      */       }
/* 1067 */       switch (this) {
/*      */         case UPPERCASE:
/* 1069 */           return StringUtils.toUpperEnglish(param1String);
/*      */         case LOWERCASE:
/* 1071 */           return StringUtils.toLowerEnglish(param1String);
/*      */         case CAPITALIZE:
/* 1073 */           return Character.toUpperCase(param1String.charAt(0)) + (
/* 1074 */             (param1String.length() > 1) ? StringUtils.toLowerEnglish(param1String).substring(1) : "");
/*      */       } 
/* 1076 */       throw new IllegalArgumentException("Unknown capitalization strategy: " + this);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ToCharFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3) {
/* 1083 */     super((paramExpression2 == null) ? new Expression[1] : ((paramExpression3 == null) ? new Expression[2] : new Expression[3]));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/* 1089 */     switch (paramValue1.getValueType())
/*      */     { case 17:
/*      */       case 18:
/*      */       case 20:
/*      */       case 21:
/* 1094 */         paramValue1 = ValueVarchar.get(toCharDateTime(paramSessionLocal, paramValue1, (paramValue2 == null) ? null : paramValue2.getString(), (paramValue3 == null) ? null : paramValue3
/* 1095 */               .getString()), (CastDataProvider)paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1109 */         return paramValue1;case 10: case 11: case 12: case 13: case 14: case 15: paramValue1 = ValueVarchar.get(toChar(paramValue1.getBigDecimal(), (paramValue2 == null) ? null : paramValue2.getString(), (paramValue3 == null) ? null : paramValue3.getString()), (CastDataProvider)paramSessionLocal); return paramValue1; }  paramValue1 = ValueVarchar.get(paramValue1.getString(), (CastDataProvider)paramSessionLocal); return paramValue1;
/*      */   }
/*      */ 
/*      */   
/*      */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 1114 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 1115 */     this.type = TypeInfo.TYPE_VARCHAR;
/* 1116 */     if (bool) {
/* 1117 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*      */     }
/* 1119 */     return (Expression)this;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getName() {
/* 1124 */     return "TO_CHAR";
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\ToCharFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */