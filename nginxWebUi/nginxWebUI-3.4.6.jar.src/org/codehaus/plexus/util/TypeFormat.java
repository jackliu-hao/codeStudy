/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TypeFormat
/*     */ {
/*  39 */   private static final char[] DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexOf(CharSequence pattern, CharSequence chars, int fromIndex) {
/*  64 */     int patternLength = pattern.length();
/*  65 */     fromIndex = Math.max(0, fromIndex);
/*  66 */     if (patternLength != 0) {
/*  67 */       char firstChar = pattern.charAt(0);
/*  68 */       int last = chars.length() - patternLength;
/*  69 */       for (int i = fromIndex; i <= last; i++) {
/*  70 */         if (chars.charAt(i) == firstChar) {
/*  71 */           boolean match = true;
/*  72 */           for (int j = 1; j < patternLength; j++) {
/*  73 */             if (chars.charAt(i + j) != pattern.charAt(j)) {
/*  74 */               match = false;
/*     */               break;
/*     */             } 
/*     */           } 
/*  78 */           if (match) {
/*  79 */             return i;
/*     */           }
/*     */         } 
/*     */       } 
/*  83 */       return -1;
/*     */     } 
/*  85 */     return Math.min(0, fromIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean parseBoolean(CharSequence chars) {
/*  96 */     return (chars.length() == 4 && (chars.charAt(0) == 't' || chars.charAt(0) == 'T') && (chars.charAt(1) == 'r' || chars.charAt(1) == 'R') && (chars.charAt(2) == 'u' || chars.charAt(2) == 'U') && (chars.charAt(3) == 'e' || chars.charAt(3) == 'E'));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short parseShort(CharSequence chars) {
/* 114 */     return parseShort(chars, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short parseShort(CharSequence chars, int radix) {
/*     */     try {
/* 132 */       boolean isNegative = (chars.charAt(0) == '-');
/* 133 */       int result = 0;
/* 134 */       int limit = isNegative ? -32768 : -32767;
/* 135 */       int multmin = limit / radix;
/* 136 */       int length = chars.length();
/* 137 */       int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
/*     */       do {
/* 139 */         int digit = Character.digit(chars.charAt(i), radix);
/* 140 */         int tmp = result * radix;
/* 141 */         if (digit < 0 || result < multmin || tmp < limit + digit)
/*     */         {
/* 143 */           throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */         }
/*     */ 
/*     */         
/* 147 */         result = tmp - digit;
/* 148 */       } while (++i < length);
/*     */ 
/*     */ 
/*     */       
/* 152 */       return (short)(isNegative ? result : -result);
/* 153 */     } catch (IndexOutOfBoundsException e) {
/* 154 */       throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int parseInt(CharSequence chars) {
/* 170 */     return parseInt(chars, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int parseInt(CharSequence chars, int radix) {
/*     */     try {
/* 188 */       boolean isNegative = (chars.charAt(0) == '-');
/* 189 */       int result = 0;
/* 190 */       int limit = isNegative ? Integer.MIN_VALUE : -2147483647;
/* 191 */       int multmin = limit / radix;
/* 192 */       int length = chars.length();
/* 193 */       int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
/*     */       do {
/* 195 */         int digit = Character.digit(chars.charAt(i), radix);
/* 196 */         int tmp = result * radix;
/* 197 */         if (digit < 0 || result < multmin || tmp < limit + digit)
/*     */         {
/* 199 */           throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */         }
/*     */ 
/*     */         
/* 203 */         result = tmp - digit;
/* 204 */       } while (++i < length);
/*     */ 
/*     */ 
/*     */       
/* 208 */       return isNegative ? result : -result;
/* 209 */     } catch (IndexOutOfBoundsException e) {
/* 210 */       throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parseLong(CharSequence chars) {
/* 226 */     return parseLong(chars, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parseLong(CharSequence chars, int radix) {
/*     */     try {
/* 244 */       boolean isNegative = (chars.charAt(0) == '-');
/* 245 */       long result = 0L;
/* 246 */       long limit = isNegative ? Long.MIN_VALUE : -9223372036854775807L;
/* 247 */       long multmin = limit / radix;
/* 248 */       int length = chars.length();
/* 249 */       int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
/*     */       do {
/* 251 */         int digit = Character.digit(chars.charAt(i), radix);
/* 252 */         long tmp = result * radix;
/* 253 */         if (digit < 0 || result < multmin || tmp < limit + digit)
/*     */         {
/* 255 */           throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */         }
/*     */ 
/*     */         
/* 259 */         result = tmp - digit;
/* 260 */       } while (++i < length);
/*     */ 
/*     */ 
/*     */       
/* 264 */       return isNegative ? result : -result;
/* 265 */     } catch (IndexOutOfBoundsException e) {
/* 266 */       throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float parseFloat(CharSequence chars) {
/* 280 */     double d = parseDouble(chars);
/* 281 */     if (d >= 1.401298464324817E-45D && d <= 3.4028234663852886E38D) {
/* 282 */       return (float)d;
/*     */     }
/* 284 */     throw new NumberFormatException("Float overflow for input characters: \"" + chars.toString() + "\"");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double parseDouble(CharSequence chars) throws NumberFormatException {
/*     */     try {
/* 301 */       int length = chars.length();
/* 302 */       double result = 0.0D;
/* 303 */       int exp = 0;
/*     */       
/* 305 */       boolean isNegative = (chars.charAt(0) == '-');
/* 306 */       int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
/*     */ 
/*     */       
/* 309 */       if (chars.charAt(i) == 'N' || chars.charAt(i) == 'I') {
/* 310 */         if (chars.toString().equals("NaN"))
/* 311 */           return Double.NaN; 
/* 312 */         if (chars.subSequence(i, length).toString().equals("Infinity"))
/*     */         {
/* 314 */           return isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 320 */       boolean fraction = false;
/*     */       do {
/* 322 */         char c = chars.charAt(i);
/* 323 */         if (c == '.' && !fraction)
/* 324 */         { fraction = true; }
/* 325 */         else { if (c == 'e' || c == 'E')
/*     */             break; 
/* 327 */           if (c >= '0' && c <= '9') {
/* 328 */             result = result * 10.0D + (c - 48);
/* 329 */             if (fraction) {
/* 330 */               exp--;
/*     */             }
/*     */           } else {
/* 333 */             throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */           }  }
/*     */       
/* 336 */       } while (++i < length);
/*     */ 
/*     */ 
/*     */       
/* 340 */       result = isNegative ? -result : result;
/*     */ 
/*     */       
/* 343 */       if (i < length) {
/* 344 */         i++;
/* 345 */         boolean negE = (chars.charAt(i) == '-');
/* 346 */         i = (negE || chars.charAt(i) == '+') ? (i + 1) : i;
/* 347 */         int valE = 0;
/*     */         do {
/* 349 */           char c = chars.charAt(i);
/* 350 */           if (c >= '0' && c <= '9') {
/* 351 */             valE = valE * 10 + c - 48;
/* 352 */             if (valE > 10000000) {
/* 353 */               valE = 10000000;
/*     */             }
/*     */           } else {
/* 356 */             throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */           }
/*     */         
/*     */         }
/* 360 */         while (++i < length);
/*     */ 
/*     */ 
/*     */         
/* 364 */         exp += negE ? -valE : valE;
/*     */       } 
/*     */ 
/*     */       
/* 368 */       return multE(result, exp);
/*     */     }
/* 370 */     catch (IndexOutOfBoundsException e) {
/* 371 */       throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(boolean b, StringBuffer sb) {
/* 385 */     return b ? sb.append("true") : sb.append("false");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(short s, StringBuffer sb) {
/* 402 */     return format(s, sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(short s, int radix, StringBuffer sb) {
/* 417 */     return format(s, radix, sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(int i, StringBuffer sb) {
/* 434 */     if (i <= 0) {
/* 435 */       if (i == Integer.MIN_VALUE)
/* 436 */         return sb.append("-2147483648"); 
/* 437 */       if (i == 0) {
/* 438 */         return sb.append('0');
/*     */       }
/* 440 */       i = -i;
/* 441 */       sb.append('-');
/*     */     } 
/* 443 */     int j = 1;
/* 444 */     for (; j < 10 && i >= INT_POW_10[j]; j++);
/*     */     
/* 446 */     for (; --j >= 0; j--) {
/* 447 */       int pow10 = INT_POW_10[j];
/* 448 */       int digit = i / pow10;
/* 449 */       i -= digit * pow10;
/* 450 */       sb.append(DIGITS[digit]);
/*     */     } 
/* 452 */     return sb;
/*     */   }
/* 454 */   private static final int[] INT_POW_10 = new int[10];
/*     */   static {
/* 456 */     int j = 1;
/* 457 */     for (int i = 0; i < 10; i++) {
/* 458 */       INT_POW_10[i] = j;
/* 459 */       j *= 10;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(int i, int radix, StringBuffer sb) {
/* 475 */     if (radix == 10)
/* 476 */       return format(i, sb); 
/* 477 */     if (radix < 2 || radix > 36) {
/* 478 */       throw new IllegalArgumentException("radix: " + radix);
/*     */     }
/* 480 */     if (i < 0) {
/* 481 */       sb.append('-');
/*     */     } else {
/* 483 */       i = -i;
/*     */     } 
/* 485 */     format2(i, radix, sb);
/* 486 */     return sb;
/*     */   }
/*     */   private static void format2(int i, int radix, StringBuffer sb) {
/* 489 */     if (i <= -radix) {
/* 490 */       format2(i / radix, radix, sb);
/* 491 */       sb.append(DIGITS[-(i % radix)]);
/*     */     } else {
/* 493 */       sb.append(DIGITS[-i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(long l, StringBuffer sb) {
/* 511 */     if (l <= 0L) {
/* 512 */       if (l == Long.MIN_VALUE)
/* 513 */         return sb.append("-9223372036854775808"); 
/* 514 */       if (l == 0L) {
/* 515 */         return sb.append('0');
/*     */       }
/* 517 */       l = -l;
/* 518 */       sb.append('-');
/*     */     } 
/* 520 */     int j = 1;
/* 521 */     for (; j < 19 && l >= LONG_POW_10[j]; j++);
/*     */     
/* 523 */     for (; --j >= 0; j--) {
/* 524 */       long pow10 = LONG_POW_10[j];
/* 525 */       int digit = (int)(l / pow10);
/* 526 */       l -= digit * pow10;
/* 527 */       sb.append(DIGITS[digit]);
/*     */     } 
/* 529 */     return sb;
/*     */   }
/* 531 */   private static final long[] LONG_POW_10 = new long[19];
/*     */   static {
/* 533 */     long pow = 1L;
/* 534 */     for (int k = 0; k < 19; k++) {
/* 535 */       LONG_POW_10[k] = pow;
/* 536 */       pow *= 10L;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(long l, int radix, StringBuffer sb) {
/* 552 */     if (radix == 10)
/* 553 */       return format(l, sb); 
/* 554 */     if (radix < 2 || radix > 36) {
/* 555 */       throw new IllegalArgumentException("radix: " + radix);
/*     */     }
/* 557 */     if (l < 0L) {
/* 558 */       sb.append('-');
/*     */     } else {
/* 560 */       l = -l;
/*     */     } 
/* 562 */     format2(l, radix, sb);
/* 563 */     return sb;
/*     */   }
/*     */   private static void format2(long l, int radix, StringBuffer sb) {
/* 566 */     if (l <= -radix) {
/* 567 */       format2(l / radix, radix, sb);
/* 568 */       sb.append(DIGITS[(int)-(l % radix)]);
/*     */     } else {
/* 570 */       sb.append(DIGITS[(int)-l]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(float f, StringBuffer sb) {
/* 584 */     return format(f, 0.0F, sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(float f, float precision, StringBuffer sb) {
/*     */     boolean precisionOnLastDigit;
/* 613 */     if (precision > 0.0F) {
/* 614 */       precisionOnLastDigit = true;
/* 615 */     } else if (precision == 0.0F) {
/* 616 */       if (f != 0.0F) {
/* 617 */         precisionOnLastDigit = false;
/* 618 */         precision = Math.max(Math.abs(f * FLOAT_RELATIVE_ERROR), Float.MIN_VALUE);
/*     */       } else {
/*     */         
/* 621 */         return sb.append("0.0");
/*     */       } 
/*     */     } else {
/* 624 */       throw new IllegalArgumentException("precision: Negative values not allowed");
/*     */     } 
/*     */     
/* 627 */     return format(f, precision, precisionOnLastDigit, sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(double d, StringBuffer sb) {
/* 646 */     return format(d, 0.0D, sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(double d, int digits, StringBuffer sb) {
/* 663 */     if (digits >= 1 && digits <= 19) {
/* 664 */       double precision = Math.abs(d / DOUBLE_POW_10[digits - 1]);
/* 665 */       return format(d, precision, sb);
/*     */     } 
/* 667 */     throw new IllegalArgumentException("digits: " + digits + " is not in range [1 .. 19]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuffer format(double d, double precision, StringBuffer sb) {
/* 697 */     boolean precisionOnLastDigit = false;
/* 698 */     if (precision > 0.0D) {
/* 699 */       precisionOnLastDigit = true;
/* 700 */     } else if (precision == 0.0D) {
/* 701 */       if (d != 0.0D) {
/* 702 */         precision = Math.max(Math.abs(d * DOUBLE_RELATIVE_ERROR), Double.MIN_VALUE);
/*     */       } else {
/*     */         
/* 705 */         return sb.append("0.0");
/*     */       } 
/* 707 */     } else if (precision < 0.0D) {
/* 708 */       throw new IllegalArgumentException("precision: Negative values not allowed");
/*     */     } 
/*     */     
/* 711 */     return format(d, precision, precisionOnLastDigit, sb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static StringBuffer format(double d, double precision, boolean precisionOnLastDigit, StringBuffer sb) {
/* 730 */     if (Double.isNaN(d))
/* 731 */       return sb.append("NaN"); 
/* 732 */     if (Double.isInfinite(d)) {
/* 733 */       return (d >= 0.0D) ? sb.append("Infinity") : sb.append("-Infinity");
/*     */     }
/* 735 */     if (d < 0.0D) {
/* 736 */       d = -d;
/* 737 */       sb.append('-');
/*     */     } 
/*     */ 
/*     */     
/* 741 */     int rank = (int)Math.floor(Math.log(precision) / LOG_10);
/* 742 */     double digitValue = multE(d, -rank);
/* 743 */     if (digitValue >= 9.223372036854776E18D) {
/* 744 */       throw new IllegalArgumentException("Specified precision would result in too many digits");
/*     */     }
/*     */     
/* 747 */     int digitStart = sb.length();
/* 748 */     format(Math.round(digitValue), sb);
/* 749 */     int digitLength = sb.length() - digitStart;
/* 750 */     int dotPos = digitLength + rank;
/* 751 */     boolean useScientificNotation = false;
/*     */ 
/*     */     
/* 754 */     if (dotPos <= -LEADING_ZEROS.length || dotPos > digitLength) {
/*     */       
/* 756 */       sb.insert(digitStart + 1, '.');
/* 757 */       useScientificNotation = true;
/* 758 */     } else if (dotPos > 0) {
/*     */       
/* 760 */       sb.insert(digitStart + dotPos, '.');
/*     */     } else {
/*     */       
/* 763 */       sb.insert(digitStart, LEADING_ZEROS[-dotPos]);
/*     */     } 
/*     */ 
/*     */     
/* 767 */     if (!precisionOnLastDigit) {
/* 768 */       int newLength = sb.length();
/*     */       while (true) {
/* 770 */         newLength--;
/* 771 */         if (sb.charAt(newLength) != '0') {
/* 772 */           sb.setLength(newLength + 1); break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 776 */     if (sb.charAt(sb.length() - 1) == '.') {
/* 777 */       if (precisionOnLastDigit) {
/* 778 */         sb.setLength(sb.length() - 1);
/*     */       } else {
/* 780 */         sb.append('0');
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 785 */     if (useScientificNotation) {
/* 786 */       sb.append('E');
/* 787 */       format(dotPos - 1, sb);
/*     */     } 
/*     */     
/* 790 */     return sb;
/*     */   }
/* 792 */   private static final double LOG_10 = Math.log(10.0D);
/* 793 */   private static final float FLOAT_RELATIVE_ERROR = (float)Math.pow(2.0D, -24.0D);
/* 794 */   private static final double DOUBLE_RELATIVE_ERROR = Math.pow(2.0D, -53.0D);
/* 795 */   private static String[] LEADING_ZEROS = new String[] { "0.", "0.0", "0.00" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final double multE(double value, int E) {
/* 807 */     if (E >= 0) {
/* 808 */       if (E <= 308)
/*     */       {
/* 810 */         return value * DOUBLE_POW_10[E];
/*     */       }
/* 812 */       value *= 1.0E21D;
/* 813 */       E = Math.min(308, E - 21);
/* 814 */       return value * DOUBLE_POW_10[E];
/*     */     } 
/*     */     
/* 817 */     if (E >= -308) {
/* 818 */       return value / DOUBLE_POW_10[-E];
/*     */     }
/*     */     
/* 821 */     value /= 1.0E21D;
/* 822 */     E = Math.max(-308, E + 21);
/* 823 */     return value / DOUBLE_POW_10[-E];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 830 */   private static final double[] DOUBLE_POW_10 = new double[] { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D, 9.999999999999999E22D, 1.0E24D, 1.0E25D, 1.0E26D, 1.0E27D, 1.0E28D, 1.0E29D, 1.0E30D, 1.0E31D, 1.0E32D, 1.0E33D, 1.0E34D, 1.0E35D, 1.0E36D, 1.0E37D, 1.0E38D, 1.0E39D, 1.0E40D, 1.0E41D, 1.0E42D, 1.0E43D, 1.0E44D, 1.0E45D, 1.0E46D, 1.0E47D, 1.0E48D, 1.0E49D, 1.0E50D, 1.0E51D, 1.0E52D, 1.0E53D, 1.0E54D, 1.0E55D, 1.0E56D, 1.0E57D, 1.0E58D, 1.0E59D, 1.0E60D, 1.0E61D, 1.0E62D, 1.0E63D, 1.0E64D, 1.0E65D, 1.0E66D, 1.0E67D, 1.0E68D, 1.0E69D, 1.0E70D, 1.0E71D, 1.0E72D, 1.0E73D, 1.0E74D, 1.0E75D, 1.0E76D, 1.0E77D, 1.0E78D, 1.0E79D, 1.0E80D, 1.0E81D, 1.0E82D, 1.0E83D, 1.0E84D, 1.0E85D, 1.0E86D, 1.0E87D, 1.0E88D, 1.0E89D, 1.0E90D, 1.0E91D, 1.0E92D, 1.0E93D, 1.0E94D, 1.0E95D, 1.0E96D, 1.0E97D, 1.0E98D, 1.0E99D, 1.0E100D, 1.0E101D, 1.0E102D, 1.0E103D, 1.0E104D, 1.0E105D, 1.0E106D, 1.0E107D, 1.0E108D, 1.0E109D, 1.0E110D, 1.0E111D, 1.0E112D, 1.0E113D, 1.0E114D, 1.0E115D, 1.0E116D, 1.0E117D, 1.0E118D, 1.0E119D, 1.0E120D, 1.0E121D, 1.0E122D, 1.0E123D, 1.0E124D, 1.0E125D, 1.0E126D, 1.0E127D, 1.0E128D, 1.0E129D, 1.0E130D, 1.0E131D, 1.0E132D, 1.0E133D, 1.0E134D, 1.0E135D, 1.0E136D, 1.0E137D, 1.0E138D, 1.0E139D, 1.0E140D, 1.0E141D, 1.0E142D, 1.0E143D, 1.0E144D, 1.0E145D, 1.0E146D, 1.0E147D, 1.0E148D, 1.0E149D, 1.0E150D, 1.0E151D, 1.0E152D, 1.0E153D, 1.0E154D, 1.0E155D, 1.0E156D, 1.0E157D, 1.0E158D, 1.0E159D, 1.0E160D, 1.0E161D, 1.0E162D, 1.0E163D, 1.0E164D, 1.0E165D, 1.0E166D, 1.0E167D, 1.0E168D, 1.0E169D, 1.0E170D, 1.0E171D, 1.0E172D, 1.0E173D, 1.0E174D, 1.0E175D, 1.0E176D, 1.0E177D, 1.0E178D, 1.0E179D, 1.0E180D, 1.0E181D, 1.0E182D, 1.0E183D, 1.0E184D, 1.0E185D, 1.0E186D, 1.0E187D, 1.0E188D, 1.0E189D, 1.0E190D, 1.0E191D, 1.0E192D, 1.0E193D, 1.0E194D, 1.0E195D, 1.0E196D, 1.0E197D, 1.0E198D, 1.0E199D, 1.0E200D, 1.0E201D, 1.0E202D, 1.0E203D, 1.0E204D, 1.0E205D, 1.0E206D, 1.0E207D, 1.0E208D, 1.0E209D, 1.0E210D, 1.0E211D, 1.0E212D, 1.0E213D, 1.0E214D, 1.0E215D, 1.0E216D, 1.0E217D, 1.0E218D, 1.0E219D, 1.0E220D, 1.0E221D, 1.0E222D, 1.0E223D, 1.0E224D, 1.0E225D, 1.0E226D, 1.0E227D, 1.0E228D, 1.0E229D, 1.0E230D, 1.0E231D, 1.0E232D, 1.0E233D, 1.0E234D, 1.0E235D, 1.0E236D, 1.0E237D, 1.0E238D, 1.0E239D, 1.0E240D, 1.0E241D, 1.0E242D, 1.0E243D, 1.0E244D, 1.0E245D, 1.0E246D, 1.0E247D, 1.0E248D, 1.0E249D, 1.0E250D, 1.0E251D, 1.0E252D, 1.0E253D, 1.0E254D, 1.0E255D, 1.0E256D, 1.0E257D, 1.0E258D, 1.0E259D, 1.0E260D, 1.0E261D, 1.0E262D, 1.0E263D, 1.0E264D, 1.0E265D, 1.0E266D, 1.0E267D, 1.0E268D, 1.0E269D, 1.0E270D, 1.0E271D, 1.0E272D, 1.0E273D, 1.0E274D, 1.0E275D, 1.0E276D, 1.0E277D, 1.0E278D, 1.0E279D, 1.0E280D, 1.0E281D, 1.0E282D, 1.0E283D, 1.0E284D, 1.0E285D, 1.0E286D, 1.0E287D, 1.0E288D, 1.0E289D, 1.0E290D, 1.0E291D, 1.0E292D, 1.0E293D, 1.0E294D, 1.0E295D, 1.0E296D, 1.0E297D, 1.0E298D, 1.0E299D, 1.0E300D, 1.0E301D, 1.0E302D, 1.0E303D, 1.0E304D, 1.0E305D, 1.0E306D, 1.0E307D, 1.0E308D };
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\TypeFormat.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */