/*     */ package cn.hutool.core.date.format;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FastDateParser
/*     */   extends AbstractDateBasic
/*     */   implements DateParser
/*     */ {
/*     */   private static final long serialVersionUID = -3199383897950947498L;
/*  36 */   static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
/*     */ 
/*     */ 
/*     */   
/*     */   private final int century;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int startYear;
/*     */ 
/*     */   
/*     */   private transient List<StrategyAndWidth> patterns;
/*     */ 
/*     */   
/*  50 */   private static final Comparator<String> LONGER_FIRST_LOWERCASE = Comparator.reverseOrder();
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
/*     */   public FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
/*  64 */     this(pattern, timeZone, locale, (Date)null);
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
/*     */   public FastDateParser(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
/*  78 */     super(pattern, timeZone, locale); int centuryStartYear;
/*  79 */     Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
/*     */ 
/*     */     
/*  82 */     if (centuryStart != null) {
/*  83 */       definingCalendar.setTime(centuryStart);
/*  84 */       centuryStartYear = definingCalendar.get(1);
/*  85 */     } else if (locale.equals(JAPANESE_IMPERIAL)) {
/*  86 */       centuryStartYear = 0;
/*     */     } else {
/*     */       
/*  89 */       definingCalendar.setTime(new Date());
/*  90 */       centuryStartYear = definingCalendar.get(1) - 80;
/*     */     } 
/*  92 */     this.century = centuryStartYear / 100 * 100;
/*  93 */     this.startYear = centuryStartYear - this.century;
/*     */     
/*  95 */     init(definingCalendar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(Calendar definingCalendar) {
/* 104 */     this.patterns = new ArrayList<>();
/*     */     
/* 106 */     StrategyParser fm = new StrategyParser(definingCalendar);
/*     */     while (true) {
/* 108 */       StrategyAndWidth field = fm.getNextStrategy();
/* 109 */       if (field == null) {
/*     */         break;
/*     */       }
/* 112 */       this.patterns.add(field);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class StrategyAndWidth
/*     */   {
/*     */     final FastDateParser.Strategy strategy;
/*     */ 
/*     */     
/*     */     final int width;
/*     */ 
/*     */     
/*     */     StrategyAndWidth(FastDateParser.Strategy strategy, int width) {
/* 127 */       this.strategy = strategy;
/* 128 */       this.width = width;
/*     */     }
/*     */     
/*     */     int getMaxWidth(ListIterator<StrategyAndWidth> lt) {
/* 132 */       if (!this.strategy.isNumber() || !lt.hasNext()) {
/* 133 */         return 0;
/*     */       }
/* 135 */       FastDateParser.Strategy nextStrategy = ((StrategyAndWidth)lt.next()).strategy;
/* 136 */       lt.previous();
/* 137 */       return nextStrategy.isNumber() ? this.width : 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private class StrategyParser
/*     */   {
/*     */     private final Calendar definingCalendar;
/*     */     
/*     */     private int currentIdx;
/*     */     
/*     */     StrategyParser(Calendar definingCalendar) {
/* 149 */       this.definingCalendar = definingCalendar;
/*     */     }
/*     */     
/*     */     FastDateParser.StrategyAndWidth getNextStrategy() {
/* 153 */       if (this.currentIdx >= FastDateParser.this.pattern.length()) {
/* 154 */         return null;
/*     */       }
/*     */       
/* 157 */       char c = FastDateParser.this.pattern.charAt(this.currentIdx);
/* 158 */       if (FastDateParser.isFormatLetter(c)) {
/* 159 */         return letterPattern(c);
/*     */       }
/* 161 */       return literal();
/*     */     }
/*     */     
/*     */     private FastDateParser.StrategyAndWidth letterPattern(char c) {
/* 165 */       int begin = this.currentIdx; do {  }
/* 166 */       while (++this.currentIdx < FastDateParser.this.pattern.length() && 
/* 167 */         FastDateParser.this.pattern.charAt(this.currentIdx) == c);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 172 */       int width = this.currentIdx - begin;
/* 173 */       return new FastDateParser.StrategyAndWidth(FastDateParser.this.getStrategy(c, width, this.definingCalendar), width);
/*     */     }
/*     */     
/*     */     private FastDateParser.StrategyAndWidth literal() {
/* 177 */       boolean activeQuote = false;
/*     */       
/* 179 */       StringBuilder sb = new StringBuilder();
/* 180 */       while (this.currentIdx < FastDateParser.this.pattern.length()) {
/* 181 */         char c = FastDateParser.this.pattern.charAt(this.currentIdx);
/* 182 */         if (!activeQuote && FastDateParser.isFormatLetter(c))
/*     */           break; 
/* 184 */         if (c == '\'' && (++this.currentIdx == FastDateParser.this.pattern.length() || FastDateParser.this.pattern.charAt(this.currentIdx) != '\'')) {
/* 185 */           activeQuote = !activeQuote;
/*     */           continue;
/*     */         } 
/* 188 */         this.currentIdx++;
/* 189 */         sb.append(c);
/*     */       } 
/*     */       
/* 192 */       if (activeQuote) {
/* 193 */         throw new IllegalArgumentException("Unterminated quote");
/*     */       }
/*     */       
/* 196 */       String formatField = sb.toString();
/* 197 */       return new FastDateParser.StrategyAndWidth(new FastDateParser.CopyQuotedStrategy(formatField), formatField.length());
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isFormatLetter(char c) {
/* 202 */     return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
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
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 216 */     in.defaultReadObject();
/*     */     
/* 218 */     Calendar definingCalendar = Calendar.getInstance(this.timeZone, this.locale);
/* 219 */     init(definingCalendar);
/*     */   }
/*     */ 
/*     */   
/*     */   public Date parse(String source) throws ParseException {
/* 224 */     ParsePosition pp = new ParsePosition(0);
/* 225 */     Date date = parse(source, pp);
/* 226 */     if (date == null) {
/*     */       
/* 228 */       if (this.locale.equals(JAPANESE_IMPERIAL)) {
/* 229 */         throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\nUnparseable date: \"" + source, pp
/* 230 */             .getErrorIndex());
/*     */       }
/* 232 */       throw new ParseException("Unparseable date: " + source, pp.getErrorIndex());
/*     */     } 
/* 234 */     return date;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Date parse(String source, ParsePosition pos) {
/* 240 */     Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
/* 241 */     cal.clear();
/*     */     
/* 243 */     return parse(source, pos, cal) ? cal.getTime() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean parse(String source, ParsePosition pos, Calendar calendar) {
/* 248 */     ListIterator<StrategyAndWidth> lt = this.patterns.listIterator();
/* 249 */     while (lt.hasNext()) {
/* 250 */       StrategyAndWidth strategyAndWidth = lt.next();
/* 251 */       int maxWidth = strategyAndWidth.getMaxWidth(lt);
/* 252 */       if (false == strategyAndWidth.strategy.parse(this, calendar, source, pos, maxWidth)) {
/* 253 */         return false;
/*     */       }
/*     */     } 
/* 256 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static StringBuilder simpleQuote(StringBuilder sb, String value) {
/* 263 */     for (int i = 0; i < value.length(); i++) {
/* 264 */       char c = value.charAt(i);
/* 265 */       switch (c) {
/*     */         case '$':
/*     */         case '(':
/*     */         case ')':
/*     */         case '*':
/*     */         case '+':
/*     */         case '.':
/*     */         case '?':
/*     */         case '[':
/*     */         case '\\':
/*     */         case '^':
/*     */         case '{':
/*     */         case '|':
/* 278 */           sb.append('\\'); break;
/*     */       } 
/* 280 */       sb.append(c);
/*     */     } 
/*     */     
/* 283 */     return sb;
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
/*     */   private static Map<String, Integer> appendDisplayNames(Calendar cal, Locale locale, int field, StringBuilder regex) {
/* 296 */     Map<String, Integer> values = new HashMap<>();
/*     */     
/* 298 */     Map<String, Integer> displayNames = cal.getDisplayNames(field, 0, locale);
/* 299 */     TreeSet<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE);
/* 300 */     for (Map.Entry<String, Integer> displayName : displayNames.entrySet()) {
/* 301 */       String key = ((String)displayName.getKey()).toLowerCase(locale);
/* 302 */       if (sorted.add(key)) {
/* 303 */         values.put(key, displayName.getValue());
/*     */       }
/*     */     } 
/* 306 */     for (String symbol : sorted) {
/* 307 */       simpleQuote(regex, symbol).append('|');
/*     */     }
/* 309 */     return values;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int adjustYear(int twoDigitYear) {
/* 319 */     int trial = this.century + twoDigitYear;
/* 320 */     return (twoDigitYear >= this.startYear) ? trial : (trial + 100);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static abstract class Strategy
/*     */   {
/*     */     private Strategy() {}
/*     */ 
/*     */ 
/*     */     
/*     */     boolean isNumber() {
/* 333 */       return false;
/*     */     }
/*     */     
/*     */     abstract boolean parse(FastDateParser param1FastDateParser, Calendar param1Calendar, String param1String, ParsePosition param1ParsePosition, int param1Int);
/*     */   }
/*     */   
/*     */   private static abstract class PatternStrategy
/*     */     extends Strategy
/*     */   {
/*     */     private Pattern pattern;
/*     */     
/*     */     private PatternStrategy() {}
/*     */     
/*     */     void createPattern(StringBuilder regex) {
/* 347 */       createPattern(regex.toString());
/*     */     }
/*     */     
/*     */     void createPattern(String regex) {
/* 351 */       this.pattern = Pattern.compile(regex);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     boolean isNumber() {
/* 361 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
/* 366 */       Matcher matcher = this.pattern.matcher(source.substring(pos.getIndex()));
/* 367 */       if (!matcher.lookingAt()) {
/* 368 */         pos.setErrorIndex(pos.getIndex());
/* 369 */         return false;
/*     */       } 
/* 371 */       pos.setIndex(pos.getIndex() + matcher.end(1));
/* 372 */       setCalendar(parser, calendar, matcher.group(1));
/* 373 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     abstract void setCalendar(FastDateParser param1FastDateParser, Calendar param1Calendar, String param1String);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Strategy getStrategy(char f, int width, Calendar definingCalendar) {
/* 388 */     switch (f) {
/*     */       default:
/* 390 */         throw new IllegalArgumentException("Format '" + f + "' not supported");
/*     */       case 'D':
/* 392 */         return DAY_OF_YEAR_STRATEGY;
/*     */       case 'E':
/* 394 */         return getLocaleSpecificStrategy(7, definingCalendar);
/*     */       case 'F':
/* 396 */         return DAY_OF_WEEK_IN_MONTH_STRATEGY;
/*     */       case 'G':
/* 398 */         return getLocaleSpecificStrategy(0, definingCalendar);
/*     */       case 'H':
/* 400 */         return HOUR_OF_DAY_STRATEGY;
/*     */       case 'K':
/* 402 */         return HOUR_STRATEGY;
/*     */       case 'M':
/* 404 */         return (width >= 3) ? getLocaleSpecificStrategy(2, definingCalendar) : NUMBER_MONTH_STRATEGY;
/*     */       case 'S':
/* 406 */         return MILLISECOND_STRATEGY;
/*     */       case 'W':
/* 408 */         return WEEK_OF_MONTH_STRATEGY;
/*     */       case 'a':
/* 410 */         return getLocaleSpecificStrategy(9, definingCalendar);
/*     */       case 'd':
/* 412 */         return DAY_OF_MONTH_STRATEGY;
/*     */       case 'h':
/* 414 */         return HOUR12_STRATEGY;
/*     */       case 'k':
/* 416 */         return HOUR24_OF_DAY_STRATEGY;
/*     */       case 'm':
/* 418 */         return MINUTE_STRATEGY;
/*     */       case 's':
/* 420 */         return SECOND_STRATEGY;
/*     */       case 'u':
/* 422 */         return DAY_OF_WEEK_STRATEGY;
/*     */       case 'w':
/* 424 */         return WEEK_OF_YEAR_STRATEGY;
/*     */       case 'Y':
/*     */       case 'y':
/* 427 */         return (width > 2) ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
/*     */       case 'X':
/* 429 */         return ISO8601TimeZoneStrategy.getStrategy(width);
/*     */       case 'Z':
/* 431 */         if (width == 2)
/* 432 */           return ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;  break;
/*     */       case 'z':
/*     */         break;
/*     */     } 
/* 436 */     return getLocaleSpecificStrategy(15, definingCalendar);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 441 */   private static final ConcurrentMap<Locale, Strategy>[] CACHES = (ConcurrentMap<Locale, Strategy>[])new ConcurrentMap[17];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ConcurrentMap<Locale, Strategy> getCache(int field) {
/* 450 */     synchronized (CACHES) {
/* 451 */       if (CACHES[field] == null) {
/* 452 */         CACHES[field] = new ConcurrentHashMap<>(3);
/*     */       }
/* 454 */       return CACHES[field];
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
/*     */   private Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
/* 466 */     ConcurrentMap<Locale, Strategy> cache = getCache(field);
/* 467 */     Strategy strategy = cache.get(this.locale);
/* 468 */     if (strategy == null) {
/* 469 */       strategy = (field == 15) ? new TimeZoneStrategy(this.locale) : new CaseInsensitiveTextStrategy(field, definingCalendar, this.locale);
/* 470 */       Strategy inCache = cache.putIfAbsent(this.locale, strategy);
/* 471 */       if (inCache != null) {
/* 472 */         return inCache;
/*     */       }
/*     */     } 
/* 475 */     return strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CopyQuotedStrategy
/*     */     extends Strategy
/*     */   {
/*     */     private final String formatField;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     CopyQuotedStrategy(String formatField) {
/* 491 */       this.formatField = formatField;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isNumber() {
/* 496 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
/* 501 */       for (int idx = 0; idx < this.formatField.length(); idx++) {
/* 502 */         int sIdx = idx + pos.getIndex();
/* 503 */         if (sIdx == source.length()) {
/* 504 */           pos.setErrorIndex(sIdx);
/* 505 */           return false;
/*     */         } 
/* 507 */         if (this.formatField.charAt(idx) != source.charAt(sIdx)) {
/* 508 */           pos.setErrorIndex(sIdx);
/* 509 */           return false;
/*     */         } 
/*     */       } 
/* 512 */       pos.setIndex(this.formatField.length() + pos.getIndex());
/* 513 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CaseInsensitiveTextStrategy
/*     */     extends PatternStrategy
/*     */   {
/*     */     private final int field;
/*     */ 
/*     */     
/*     */     final Locale locale;
/*     */ 
/*     */     
/*     */     private final Map<String, Integer> lKeyValues;
/*     */ 
/*     */ 
/*     */     
/*     */     CaseInsensitiveTextStrategy(int field, Calendar definingCalendar, Locale locale) {
/* 533 */       this.field = field;
/* 534 */       this.locale = locale;
/*     */       
/* 536 */       StringBuilder regex = new StringBuilder();
/* 537 */       regex.append("((?iu)");
/* 538 */       this.lKeyValues = FastDateParser.appendDisplayNames(definingCalendar, locale, field, regex);
/* 539 */       regex.setLength(regex.length() - 1);
/* 540 */       regex.append(")");
/* 541 */       createPattern(regex);
/*     */     }
/*     */ 
/*     */     
/*     */     void setCalendar(FastDateParser parser, Calendar cal, String value) {
/* 546 */       Integer iVal = this.lKeyValues.get(value.toLowerCase(this.locale));
/* 547 */       cal.set(this.field, iVal.intValue());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class NumberStrategy
/*     */     extends Strategy
/*     */   {
/*     */     private final int field;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     NumberStrategy(int field) {
/* 563 */       this.field = field;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isNumber() {
/* 568 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
/* 573 */       int idx = pos.getIndex();
/* 574 */       int last = source.length();
/*     */       
/* 576 */       if (maxWidth == 0) {
/*     */         
/* 578 */         for (; idx < last; idx++) {
/* 579 */           char c = source.charAt(idx);
/* 580 */           if (!Character.isWhitespace(c)) {
/*     */             break;
/*     */           }
/*     */         } 
/* 584 */         pos.setIndex(idx);
/*     */       } else {
/* 586 */         int end = idx + maxWidth;
/* 587 */         if (last > end) {
/* 588 */           last = end;
/*     */         }
/*     */       } 
/*     */       
/* 592 */       for (; idx < last; idx++) {
/* 593 */         char c = source.charAt(idx);
/* 594 */         if (!Character.isDigit(c)) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 599 */       if (pos.getIndex() == idx) {
/* 600 */         pos.setErrorIndex(idx);
/* 601 */         return false;
/*     */       } 
/*     */       
/* 604 */       int value = Integer.parseInt(source.substring(pos.getIndex(), idx));
/* 605 */       pos.setIndex(idx);
/*     */       
/* 607 */       calendar.set(this.field, modify(parser, value));
/* 608 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int modify(FastDateParser parser, int iValue) {
/* 619 */       return iValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 624 */   private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1)
/*     */     {
/*     */       int modify(FastDateParser parser, int iValue) {
/* 627 */         return (iValue < 100) ? parser.adjustYear(iValue) : iValue;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   static class TimeZoneStrategy
/*     */     extends PatternStrategy
/*     */   {
/*     */     private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}";
/*     */     
/*     */     private static final String UTC_TIME_ZONE_WITH_OFFSET = "[+-]\\d{2}:\\d{2}";
/*     */     private static final String GMT_OPTION = "GMT[+-]\\d{1,2}:\\d{2}";
/*     */     private final Locale locale;
/* 640 */     private final Map<String, TzInfo> tzNames = new HashMap<>();
/*     */     private static final int ID = 0;
/*     */     
/*     */     private static class TzInfo { TimeZone zone;
/*     */       int dstOffset;
/*     */       
/*     */       TzInfo(TimeZone tz, boolean useDst) {
/* 647 */         this.zone = tz;
/* 648 */         this.dstOffset = useDst ? tz.getDSTSavings() : 0;
/*     */       } }
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
/*     */     TimeZoneStrategy(Locale locale) {
/* 663 */       this.locale = locale;
/*     */       
/* 665 */       StringBuilder sb = new StringBuilder();
/* 666 */       sb.append("((?iu)[+-]\\d{4}|[+-]\\d{2}:\\d{2}|GMT[+-]\\d{1,2}:\\d{2}");
/*     */       
/* 668 */       Set<String> sorted = new TreeSet<>(FastDateParser.LONGER_FIRST_LOWERCASE);
/*     */       
/* 670 */       String[][] zones = DateFormatSymbols.getInstance(locale).getZoneStrings();
/* 671 */       for (String[] zoneNames : zones) {
/*     */         
/* 673 */         String tzId = zoneNames[0];
/* 674 */         if (!"GMT".equalsIgnoreCase(tzId)) {
/*     */ 
/*     */           
/* 677 */           TimeZone tz = TimeZone.getTimeZone(tzId);
/*     */ 
/*     */           
/* 680 */           TzInfo standard = new TzInfo(tz, false);
/* 681 */           TzInfo tzInfo = standard;
/* 682 */           for (int i = 1; i < zoneNames.length; i++) {
/* 683 */             switch (i) {
/*     */               
/*     */               case 3:
/* 686 */                 tzInfo = new TzInfo(tz, true);
/*     */                 break;
/*     */               case 5:
/* 689 */                 tzInfo = standard;
/*     */                 break;
/*     */             } 
/* 692 */             if (zoneNames[i] != null) {
/* 693 */               String key = zoneNames[i].toLowerCase(locale);
/*     */ 
/*     */               
/* 696 */               if (sorted.add(key)) {
/* 697 */                 this.tzNames.put(key, tzInfo);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 704 */       for (String zoneName : sorted) {
/* 705 */         FastDateParser.simpleQuote(sb.append('|'), zoneName);
/*     */       }
/* 707 */       sb.append(")");
/* 708 */       createPattern(sb);
/*     */     }
/*     */ 
/*     */     
/*     */     void setCalendar(FastDateParser parser, Calendar cal, String value) {
/* 713 */       if (value.charAt(0) == '+' || value.charAt(0) == '-') {
/* 714 */         TimeZone tz = TimeZone.getTimeZone("GMT" + value);
/* 715 */         cal.setTimeZone(tz);
/* 716 */       } else if (value.regionMatches(true, 0, "GMT", 0, 3)) {
/* 717 */         TimeZone tz = TimeZone.getTimeZone(value.toUpperCase());
/* 718 */         cal.setTimeZone(tz);
/*     */       } else {
/* 720 */         TzInfo tzInfo = this.tzNames.get(value.toLowerCase(this.locale));
/* 721 */         cal.set(16, tzInfo.dstOffset);
/*     */ 
/*     */         
/* 724 */         cal.set(15, parser.getTimeZone().getRawOffset());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ISO8601TimeZoneStrategy
/*     */     extends PatternStrategy
/*     */   {
/*     */     ISO8601TimeZoneStrategy(String pattern) {
/* 738 */       createPattern(pattern);
/*     */     }
/*     */ 
/*     */     
/*     */     void setCalendar(FastDateParser parser, Calendar cal, String value) {
/* 743 */       if (Objects.equals(value, "Z")) {
/* 744 */         cal.setTimeZone(TimeZone.getTimeZone("UTC"));
/*     */       } else {
/* 746 */         cal.setTimeZone(TimeZone.getTimeZone("GMT" + value));
/*     */       } 
/*     */     }
/*     */     
/* 750 */     private static final FastDateParser.Strategy ISO_8601_1_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
/* 751 */     private static final FastDateParser.Strategy ISO_8601_2_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
/* 752 */     private static final FastDateParser.Strategy ISO_8601_3_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static FastDateParser.Strategy getStrategy(int tokenLen) {
/* 761 */       switch (tokenLen) {
/*     */         case 1:
/* 763 */           return ISO_8601_1_STRATEGY;
/*     */         case 2:
/* 765 */           return ISO_8601_2_STRATEGY;
/*     */         case 3:
/* 767 */           return ISO_8601_3_STRATEGY;
/*     */       } 
/* 769 */       throw new IllegalArgumentException("invalid number of X");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 774 */   private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2)
/*     */     {
/*     */       int modify(FastDateParser parser, int iValue) {
/* 777 */         return iValue - 1;
/*     */       }
/*     */     };
/* 780 */   private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
/* 781 */   private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
/* 782 */   private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
/* 783 */   private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
/* 784 */   private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
/* 785 */   private static final Strategy DAY_OF_WEEK_STRATEGY = new NumberStrategy(7)
/*     */     {
/*     */       int modify(FastDateParser parser, int iValue) {
/* 788 */         return (iValue != 7) ? (iValue + 1) : 1;
/*     */       }
/*     */     };
/* 791 */   private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
/* 792 */   private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
/* 793 */   private static final Strategy HOUR24_OF_DAY_STRATEGY = new NumberStrategy(11)
/*     */     {
/*     */       int modify(FastDateParser parser, int iValue) {
/* 796 */         return (iValue == 24) ? 0 : iValue;
/*     */       }
/*     */     };
/* 799 */   private static final Strategy HOUR12_STRATEGY = new NumberStrategy(10)
/*     */     {
/*     */       int modify(FastDateParser parser, int iValue) {
/* 802 */         return (iValue == 12) ? 0 : iValue;
/*     */       }
/*     */     };
/* 805 */   private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
/* 806 */   private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
/* 807 */   private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
/* 808 */   private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\FastDateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */