/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.h2.expression.function.ToCharFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.TimeZoneProvider;
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
/*     */ final class ToDateTokenizer
/*     */ {
/*  29 */   static final Pattern PATTERN_INLINE = Pattern.compile("(\"[^\"]*\")");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   static final Pattern PATTERN_NUMBER = Pattern.compile("^([+-]?[0-9]+)");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   static final Pattern PATTERN_FOUR_DIGITS = Pattern.compile("^([+-]?[0-9]{4})");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   static final Pattern PATTERN_TWO_TO_FOUR_DIGITS = Pattern.compile("^([+-]?[0-9]{2,4})");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   static final Pattern PATTERN_THREE_DIGITS = Pattern.compile("^([+-]?[0-9]{3})");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   static final Pattern PATTERN_TWO_DIGITS = Pattern.compile("^([+-]?[0-9]{2})");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   static final Pattern PATTERN_TWO_DIGITS_OR_LESS = Pattern.compile("^([+-]?[0-9][0-9]?)");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   static final Pattern PATTERN_ONE_DIGIT = Pattern.compile("^([+-]?[0-9])");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   static final Pattern PATTERN_FF = Pattern.compile("^(FF[0-9]?)", 2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   static final Pattern PATTERN_AM_PM = Pattern.compile("^(AM|A\\.M\\.|PM|P\\.M\\.)", 2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   static final Pattern PATTERN_BC_AD = Pattern.compile("^(BC|B\\.C\\.|AD|A\\.D\\.)", 2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   static final YearParslet PARSLET_YEAR = new YearParslet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   static final MonthParslet PARSLET_MONTH = new MonthParslet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   static final DayParslet PARSLET_DAY = new DayParslet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   static final TimeParslet PARSLET_TIME = new TimeParslet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   static final InlineParslet PARSLET_INLINE = new InlineParslet();
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
/*     */   static class YearParslet
/*     */     implements ToDateParslet
/*     */   {
/*     */     public void parse(ToDateParser param1ToDateParser, ToDateTokenizer.FormatTokenEnum param1FormatTokenEnum, String param1String) {
/*     */       int j;
/* 139 */       String str = null;
/* 140 */       int i = 0;
/* 141 */       switch (param1FormatTokenEnum) {
/*     */         case SYYYY:
/*     */         case YYYY:
/* 144 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_FOUR_DIGITS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 146 */           i = Integer.parseInt(str);
/*     */ 
/*     */           
/* 149 */           if (i == 0) {
/* 150 */             ToDateTokenizer.throwException(param1ToDateParser, "Year may not be zero");
/*     */           }
/* 152 */           param1ToDateParser.setYear((i >= 0) ? i : (i + 1));
/*     */           break;
/*     */         case YYY:
/* 155 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_THREE_DIGITS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 157 */           i = Integer.parseInt(str);
/* 158 */           if (i > 999) {
/* 159 */             ToDateTokenizer.throwException(param1ToDateParser, "Year may have only three digits with specified format");
/*     */           }
/* 161 */           i += param1ToDateParser.getCurrentYear() / 1000 * 1000;
/*     */ 
/*     */           
/* 164 */           param1ToDateParser.setYear((i >= 0) ? i : (i + 1));
/*     */           break;
/*     */         case RRRR:
/* 167 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_TO_FOUR_DIGITS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 169 */           i = Integer.parseInt(str);
/* 170 */           if (str.length() < 4) {
/* 171 */             if (i < 50) {
/* 172 */               i += 2000;
/* 173 */             } else if (i < 100) {
/* 174 */               i += 1900;
/*     */             } 
/*     */           }
/* 177 */           if (i == 0) {
/* 178 */             ToDateTokenizer.throwException(param1ToDateParser, "Year may not be zero");
/*     */           }
/* 180 */           param1ToDateParser.setYear(i);
/*     */           break;
/*     */         case RR:
/* 183 */           j = param1ToDateParser.getCurrentYear() / 100;
/* 184 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 186 */           i = Integer.parseInt(str) + j * 100;
/* 187 */           param1ToDateParser.setYear(i);
/*     */           break;
/*     */         case EE:
/* 190 */           ToDateTokenizer.throwException(param1ToDateParser, String.format("token '%s' not supported yet.", new Object[] { param1FormatTokenEnum
/* 191 */                   .name() }));
/*     */           break;
/*     */         case E:
/* 194 */           ToDateTokenizer.throwException(param1ToDateParser, String.format("token '%s' not supported yet.", new Object[] { param1FormatTokenEnum
/* 195 */                   .name() }));
/*     */           break;
/*     */         case YY:
/* 198 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 200 */           i = Integer.parseInt(str);
/* 201 */           if (i > 99) {
/* 202 */             ToDateTokenizer.throwException(param1ToDateParser, "Year may have only two digits with specified format");
/*     */           }
/* 204 */           i += param1ToDateParser.getCurrentYear() / 100 * 100;
/*     */ 
/*     */           
/* 207 */           param1ToDateParser.setYear((i >= 0) ? i : (i + 1));
/*     */           break;
/*     */         case SCC:
/*     */         case CC:
/* 211 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 213 */           i = Integer.parseInt(str) * 100;
/* 214 */           param1ToDateParser.setYear(i);
/*     */           break;
/*     */         case Y:
/* 217 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_ONE_DIGIT, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 219 */           i = Integer.parseInt(str);
/* 220 */           if (i > 9) {
/* 221 */             ToDateTokenizer.throwException(param1ToDateParser, "Year may have only two digits with specified format");
/*     */           }
/* 223 */           i += param1ToDateParser.getCurrentYear() / 10 * 10;
/*     */ 
/*     */           
/* 226 */           param1ToDateParser.setYear((i >= 0) ? i : (i + 1));
/*     */           break;
/*     */         case BC_AD:
/* 229 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_BC_AD, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 231 */           param1ToDateParser.setBC(str.toUpperCase().startsWith("B"));
/*     */           break;
/*     */         default:
/* 234 */           throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", new Object[] {
/*     */                   
/* 236 */                   getClass().getSimpleName(), param1FormatTokenEnum }));
/*     */       } 
/* 238 */       param1ToDateParser.remove(str, param1String);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class MonthParslet
/*     */     implements ToDateParslet
/*     */   {
/* 246 */     private static final String[] ROMAN_MONTH = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII" };
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void parse(ToDateParser param1ToDateParser, ToDateTokenizer.FormatTokenEnum param1FormatTokenEnum, String param1String) {
/* 252 */       String str1 = param1ToDateParser.getInputStr();
/* 253 */       String str2 = null;
/* 254 */       int i = 0;
/* 255 */       switch (param1FormatTokenEnum) {
/*     */         case MONTH:
/* 257 */           str2 = ToDateTokenizer.setByName(param1ToDateParser, 0);
/*     */           break;
/*     */         case Q:
/* 260 */           ToDateTokenizer.throwException(param1ToDateParser, String.format("token '%s' not supported yet.", new Object[] { param1FormatTokenEnum
/* 261 */                   .name() }));
/*     */           break;
/*     */         case MON:
/* 264 */           str2 = ToDateTokenizer.setByName(param1ToDateParser, 1);
/*     */           break;
/*     */         
/*     */         case MM:
/* 268 */           str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 270 */           i = Integer.parseInt(str2);
/* 271 */           param1ToDateParser.setMonth(i);
/*     */           break;
/*     */         case RM:
/* 274 */           i = 0;
/* 275 */           for (String str : ROMAN_MONTH) {
/* 276 */             i++;
/* 277 */             int j = str.length();
/* 278 */             if (str1.length() >= j && str
/* 279 */               .equalsIgnoreCase(str1.substring(0, j))) {
/* 280 */               param1ToDateParser.setMonth(i + 1);
/* 281 */               str2 = str;
/*     */               break;
/*     */             } 
/*     */           } 
/* 285 */           if (str2 == null || str2.isEmpty())
/* 286 */             ToDateTokenizer.throwException(param1ToDateParser, 
/* 287 */                 String.format("Issue happened when parsing token '%s'. Expected one of: %s", new Object[] {
/*     */                     
/* 289 */                     param1FormatTokenEnum.name(), 
/* 290 */                     Arrays.toString((Object[])ROMAN_MONTH)
/*     */                   })); 
/*     */           break;
/*     */         default:
/* 294 */           throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", new Object[] {
/*     */                   
/* 296 */                   getClass().getSimpleName(), param1FormatTokenEnum }));
/*     */       } 
/* 298 */       param1ToDateParser.remove(str2, param1String);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class DayParslet
/*     */     implements ToDateParslet
/*     */   {
/*     */     public void parse(ToDateParser param1ToDateParser, ToDateTokenizer.FormatTokenEnum param1FormatTokenEnum, String param1String) {
/* 309 */       String str = null;
/* 310 */       int i = 0;
/* 311 */       switch (param1FormatTokenEnum) {
/*     */         case DDD:
/* 313 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 315 */           i = Integer.parseInt(str);
/* 316 */           param1ToDateParser.setDayOfYear(i);
/*     */           break;
/*     */         case DD:
/* 319 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 321 */           i = Integer.parseInt(str);
/* 322 */           param1ToDateParser.setDay(i);
/*     */           break;
/*     */         case D:
/* 325 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_ONE_DIGIT, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 327 */           i = Integer.parseInt(str);
/* 328 */           param1ToDateParser.setDay(i);
/*     */           break;
/*     */         case DAY:
/* 331 */           str = ToDateTokenizer.setByName(param1ToDateParser, 2);
/*     */           break;
/*     */         case DY:
/* 334 */           str = ToDateTokenizer.setByName(param1ToDateParser, 3);
/*     */           break;
/*     */         case J:
/* 337 */           str = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 339 */           i = Integer.parseInt(str);
/* 340 */           param1ToDateParser.setAbsoluteDay(i + -2440588);
/*     */           break;
/*     */         default:
/* 343 */           throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", new Object[] {
/*     */                   
/* 345 */                   getClass().getSimpleName(), param1FormatTokenEnum }));
/*     */       } 
/* 347 */       param1ToDateParser.remove(str, param1String);
/*     */     }
/*     */   }
/*     */   
/*     */   static class TimeParslet
/*     */     implements ToDateParslet {
/*     */     public void parse(ToDateParser param1ToDateParser, ToDateTokenizer.FormatTokenEnum param1FormatTokenEnum, String param1String) {
/*     */       int j;
/*     */       String str2;
/*     */       int k;
/*     */       double d;
/*     */       int m;
/* 359 */       String str3, str1 = null;
/* 360 */       int i = 0;
/* 361 */       switch (param1FormatTokenEnum) {
/*     */         case HH24:
/* 363 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 365 */           i = Integer.parseInt(str1);
/* 366 */           param1ToDateParser.setHour(i);
/*     */           break;
/*     */         case HH12:
/*     */         case HH:
/* 370 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 372 */           i = Integer.parseInt(str1);
/* 373 */           param1ToDateParser.setHour12(i);
/*     */           break;
/*     */         case MI:
/* 376 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 378 */           i = Integer.parseInt(str1);
/* 379 */           param1ToDateParser.setMinute(i);
/*     */           break;
/*     */         case SS:
/* 382 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 384 */           i = Integer.parseInt(str1);
/* 385 */           param1ToDateParser.setSecond(i);
/*     */           break;
/*     */         case SSSSS:
/* 388 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 390 */           i = Integer.parseInt(str1);
/* 391 */           j = i % 60;
/* 392 */           i /= 60;
/* 393 */           k = i % 60;
/* 394 */           i /= 60;
/* 395 */           m = i % 24;
/* 396 */           param1ToDateParser.setHour(m);
/* 397 */           param1ToDateParser.setMinute(k);
/* 398 */           param1ToDateParser.setSecond(j);
/*     */           break;
/*     */         
/*     */         case FF:
/* 402 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, param1ToDateParser, param1FormatTokenEnum);
/*     */ 
/*     */           
/* 405 */           str2 = String.format("%-9s", new Object[] { str1 }).replace(' ', '0');
/* 406 */           str2 = str2.substring(0, 9);
/* 407 */           d = Double.parseDouble(str2);
/* 408 */           param1ToDateParser.setNanos((int)d);
/*     */           break;
/*     */         case AM_PM:
/* 411 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_AM_PM, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 413 */           if (str1.toUpperCase().startsWith("A")) {
/* 414 */             param1ToDateParser.setAmPm(true); break;
/*     */           } 
/* 416 */           param1ToDateParser.setAmPm(false);
/*     */           break;
/*     */         
/*     */         case TZH:
/* 420 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 422 */           i = Integer.parseInt(str1);
/* 423 */           param1ToDateParser.setTimeZoneHour(i);
/*     */           break;
/*     */         case TZM:
/* 426 */           str1 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, param1ToDateParser, param1FormatTokenEnum);
/*     */           
/* 428 */           i = Integer.parseInt(str1);
/* 429 */           param1ToDateParser.setTimeZoneMinute(i);
/*     */           break;
/*     */         case TZR:
/*     */         case TZD:
/* 433 */           str3 = param1ToDateParser.getInputStr();
/* 434 */           param1ToDateParser.setTimeZone(TimeZoneProvider.ofId(str3));
/* 435 */           str1 = str3;
/*     */           break;
/*     */         default:
/* 438 */           throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", new Object[] {
/*     */                   
/* 440 */                   getClass().getSimpleName(), param1FormatTokenEnum }));
/*     */       } 
/* 442 */       param1ToDateParser.remove(str1, param1String);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class InlineParslet
/*     */     implements ToDateParslet
/*     */   {
/*     */     public void parse(ToDateParser param1ToDateParser, ToDateTokenizer.FormatTokenEnum param1FormatTokenEnum, String param1String) {
/* 453 */       String str = null;
/* 454 */       switch (param1FormatTokenEnum) {
/*     */         case INLINE:
/* 456 */           str = param1String.replace("\"", "");
/*     */           break;
/*     */         default:
/* 459 */           throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", new Object[] {
/*     */                   
/* 461 */                   getClass().getSimpleName(), param1FormatTokenEnum }));
/*     */       } 
/* 463 */       param1ToDateParser.remove(str, param1String);
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
/*     */   static String matchStringOrThrow(Pattern paramPattern, ToDateParser paramToDateParser, Enum<?> paramEnum) {
/* 478 */     String str = paramToDateParser.getInputStr();
/* 479 */     Matcher matcher = paramPattern.matcher(str);
/* 480 */     if (!matcher.find()) {
/* 481 */       throwException(paramToDateParser, String.format("Issue happened when parsing token '%s'", new Object[] { paramEnum
/* 482 */               .name() }));
/*     */     }
/* 484 */     return matcher.group(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String setByName(ToDateParser paramToDateParser, int paramInt) {
/* 495 */     String str1 = null;
/* 496 */     String str2 = paramToDateParser.getInputStr();
/* 497 */     String[] arrayOfString = ToCharFunction.getDateNames(paramInt);
/* 498 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 499 */       String str = arrayOfString[b];
/* 500 */       if (str != null) {
/*     */ 
/*     */         
/* 503 */         int i = str.length();
/* 504 */         if (str.equalsIgnoreCase(str2.substring(0, i))) {
/* 505 */           switch (paramInt) {
/*     */             case 0:
/*     */             case 1:
/* 508 */               paramToDateParser.setMonth(b + 1);
/*     */               break;
/*     */             
/*     */             case 2:
/*     */             case 3:
/*     */               break;
/*     */             default:
/* 515 */               throw new IllegalArgumentException();
/*     */           } 
/* 517 */           str1 = str; break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 521 */     if (str1 == null || str1.isEmpty())
/* 522 */       throwException(paramToDateParser, String.format("Tried to parse one of '%s' but failed (may be an internal error?)", new Object[] {
/*     */               
/* 524 */               Arrays.toString((Object[])arrayOfString)
/*     */             })); 
/* 526 */     return str1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void throwException(ToDateParser paramToDateParser, String paramString) {
/* 536 */     throw DbException.get(90056, new String[] { paramToDateParser
/* 537 */           .getFunctionName(), 
/* 538 */           String.format(" %s. Details: %s", new Object[] { paramString, paramToDateParser }) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum FormatTokenEnum
/*     */   {
/* 546 */     YYYY((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 548 */     SYYYY((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 550 */     YYY((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 552 */     YY((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 554 */     SCC((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 556 */     CC((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 558 */     RRRR((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 560 */     RR((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 562 */     BC_AD((String)ToDateTokenizer.PARSLET_YEAR, ToDateTokenizer.PATTERN_BC_AD),
/*     */     
/* 564 */     MONTH((String)ToDateTokenizer.PARSLET_MONTH),
/*     */     
/* 566 */     MON((String)ToDateTokenizer.PARSLET_MONTH),
/*     */     
/* 568 */     MM((String)ToDateTokenizer.PARSLET_MONTH),
/*     */     
/* 570 */     RM((String)ToDateTokenizer.PARSLET_MONTH),
/*     */     
/* 572 */     DDD((String)ToDateTokenizer.PARSLET_DAY),
/*     */     
/* 574 */     DAY((String)ToDateTokenizer.PARSLET_DAY),
/*     */     
/* 576 */     DD((String)ToDateTokenizer.PARSLET_DAY),
/*     */     
/* 578 */     DY((String)ToDateTokenizer.PARSLET_DAY), HH24((String)ToDateTokenizer.PARSLET_TIME), HH12((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 580 */     HH((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 582 */     MI((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 584 */     SSSSS((String)ToDateTokenizer.PARSLET_TIME), SS((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 586 */     FF((String)ToDateTokenizer.PARSLET_TIME, ToDateTokenizer.PATTERN_FF),
/*     */     
/* 588 */     TZH((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 590 */     TZM((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 592 */     TZR((String)ToDateTokenizer.PARSLET_TIME),
/*     */ 
/*     */     
/* 595 */     TZD((String)ToDateTokenizer.PARSLET_TIME),
/*     */     
/* 597 */     AM_PM((String)ToDateTokenizer.PARSLET_TIME, ToDateTokenizer.PATTERN_AM_PM),
/*     */ 
/*     */ 
/*     */     
/* 601 */     EE((String)ToDateTokenizer.PARSLET_YEAR),
/*     */ 
/*     */ 
/*     */     
/* 605 */     E((String)ToDateTokenizer.PARSLET_YEAR), Y((String)ToDateTokenizer.PARSLET_YEAR),
/*     */     
/* 607 */     Q((String)ToDateTokenizer.PARSLET_MONTH),
/*     */     
/* 609 */     D((String)ToDateTokenizer.PARSLET_DAY),
/*     */ 
/*     */     
/* 612 */     J((String)ToDateTokenizer.PARSLET_DAY),
/*     */ 
/*     */ 
/*     */     
/* 616 */     INLINE((String)ToDateTokenizer.PARSLET_INLINE, ToDateTokenizer.PATTERN_INLINE);
/*     */     
/* 618 */     private static final List<FormatTokenEnum> INLINE_LIST = Collections.singletonList(INLINE);
/*     */     
/*     */     private static List<FormatTokenEnum>[] TOKENS;
/*     */     
/*     */     private final ToDateTokenizer.ToDateParslet toDateParslet;
/*     */     
/*     */     private final Pattern patternToUse;
/*     */     
/*     */     static {
/*     */     
/*     */     }
/*     */     
/*     */     FormatTokenEnum(ToDateTokenizer.ToDateParslet param1ToDateParslet, Pattern param1Pattern) {
/* 631 */       this.toDateParslet = param1ToDateParslet;
/* 632 */       this.patternToUse = param1Pattern;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     FormatTokenEnum(ToDateTokenizer.ToDateParslet param1ToDateParslet) {
/* 641 */       this.toDateParslet = param1ToDateParslet;
/* 642 */       this.patternToUse = Pattern.compile(String.format("^(%s)", new Object[] { name() }), 2);
/*     */     }
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
/*     */     static List<FormatTokenEnum> getTokensInQuestion(String param1String) {
/* 655 */       if (param1String != null && !param1String.isEmpty()) {
/* 656 */         char c = Character.toUpperCase(param1String.charAt(0));
/* 657 */         if (c >= 'A' && c <= 'Y') {
/* 658 */           List[] arrayOfList; List<FormatTokenEnum>[] arrayOfList1 = TOKENS;
/* 659 */           if (arrayOfList1 == null) {
/* 660 */             arrayOfList = (List[])initTokens();
/*     */           }
/* 662 */           return arrayOfList[c - 65];
/* 663 */         }  if (c == '"') {
/* 664 */           return INLINE_LIST;
/*     */         }
/*     */       } 
/* 667 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     private static List<FormatTokenEnum>[] initTokens() {
/* 672 */       List[] arrayOfList = new List[25];
/* 673 */       for (FormatTokenEnum formatTokenEnum : values()) {
/* 674 */         String str = formatTokenEnum.name();
/* 675 */         if (str.indexOf('_') >= 0) {
/* 676 */           for (String str1 : str.split("_")) {
/* 677 */             putToCache((List<FormatTokenEnum>[])arrayOfList, formatTokenEnum, str1);
/*     */           }
/*     */         } else {
/* 680 */           putToCache((List<FormatTokenEnum>[])arrayOfList, formatTokenEnum, str);
/*     */         } 
/*     */       } 
/* 683 */       return TOKENS = (List<FormatTokenEnum>[])arrayOfList;
/*     */     }
/*     */     
/*     */     private static void putToCache(List<FormatTokenEnum>[] param1ArrayOfList, FormatTokenEnum param1FormatTokenEnum, String param1String) {
/* 687 */       int i = Character.toUpperCase(param1String.charAt(0)) - 65;
/* 688 */       List<FormatTokenEnum> list = param1ArrayOfList[i];
/* 689 */       if (list == null) {
/* 690 */         list = new ArrayList<>(1);
/* 691 */         param1ArrayOfList[i] = list;
/*     */       } 
/* 693 */       list.add(param1FormatTokenEnum);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     boolean parseFormatStrWithToken(ToDateParser param1ToDateParser) {
/* 704 */       Matcher matcher = this.patternToUse.matcher(param1ToDateParser.getFormatStr());
/* 705 */       boolean bool = matcher.find();
/* 706 */       if (bool) {
/* 707 */         String str = matcher.group(1);
/* 708 */         this.toDateParslet.parse(param1ToDateParser, this, str);
/*     */       } 
/* 710 */       return bool;
/*     */     }
/*     */   }
/*     */   
/*     */   static interface ToDateParslet {
/*     */     void parse(ToDateParser param1ToDateParser, ToDateTokenizer.FormatTokenEnum param1FormatTokenEnum, String param1String);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\ToDateTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */