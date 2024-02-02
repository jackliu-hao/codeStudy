/*      */ package cn.hutool.core.date.format;
/*      */ 
/*      */ import cn.hutool.core.date.DateException;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.text.DateFormatSymbols;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
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
/*      */ public class FastDatePrinter
/*      */   extends AbstractDateBasic
/*      */   implements DatePrinter
/*      */ {
/*      */   private static final long serialVersionUID = -6305750172255764887L;
/*      */   private transient Rule[] rules;
/*      */   private transient int mMaxLengthEstimate;
/*      */   private static final int MAX_DIGITS = 10;
/*      */   
/*      */   public FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
/*   41 */     super(pattern, timeZone, locale);
/*   42 */     init();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void init() {
/*   49 */     List<Rule> rulesList = parsePattern();
/*   50 */     this.rules = rulesList.<Rule>toArray(new Rule[0]);
/*      */     
/*   52 */     int len = 0;
/*   53 */     for (int i = this.rules.length; --i >= 0;) {
/*   54 */       len += this.rules[i].estimateLength();
/*      */     }
/*      */     
/*   57 */     this.mMaxLengthEstimate = len;
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
/*      */   protected List<Rule> parsePattern() {
/*   71 */     DateFormatSymbols symbols = new DateFormatSymbols(this.locale);
/*   72 */     List<Rule> rules = new ArrayList<>();
/*      */     
/*   74 */     String[] ERAs = symbols.getEras();
/*   75 */     String[] months = symbols.getMonths();
/*   76 */     String[] shortMonths = symbols.getShortMonths();
/*   77 */     String[] weekdays = symbols.getWeekdays();
/*   78 */     String[] shortWeekdays = symbols.getShortWeekdays();
/*   79 */     String[] AmPmStrings = symbols.getAmPmStrings();
/*      */     
/*   81 */     int length = this.pattern.length();
/*   82 */     int[] indexRef = new int[1];
/*      */     
/*   84 */     for (int i = 0; i < length; i++) {
/*   85 */       Rule rule; String sub; indexRef[0] = i;
/*   86 */       String token = parseToken(this.pattern, indexRef);
/*   87 */       i = indexRef[0];
/*      */       
/*   89 */       int tokenLen = token.length();
/*   90 */       if (tokenLen == 0) {
/*      */         break;
/*      */       }
/*      */ 
/*      */       
/*   95 */       char c = token.charAt(0);
/*      */       
/*   97 */       switch (c) {
/*      */         case 'G':
/*   99 */           rule = new TextField(0, ERAs);
/*      */           break;
/*      */         case 'Y':
/*      */         case 'y':
/*  103 */           if (tokenLen == 2) {
/*  104 */             rule = TwoDigitYearField.INSTANCE;
/*      */           } else {
/*  106 */             rule = selectNumberRule(1, Math.max(tokenLen, 4));
/*      */           } 
/*  108 */           if (c == 'Y') {
/*  109 */             rule = new WeekYear((NumberRule)rule);
/*      */           }
/*      */           break;
/*      */         case 'M':
/*  113 */           if (tokenLen >= 4) {
/*  114 */             rule = new TextField(2, months); break;
/*  115 */           }  if (tokenLen == 3) {
/*  116 */             rule = new TextField(2, shortMonths); break;
/*  117 */           }  if (tokenLen == 2) {
/*  118 */             rule = TwoDigitMonthField.INSTANCE; break;
/*      */           } 
/*  120 */           rule = UnpaddedMonthField.INSTANCE;
/*      */           break;
/*      */         
/*      */         case 'd':
/*  124 */           rule = selectNumberRule(5, tokenLen);
/*      */           break;
/*      */         case 'h':
/*  127 */           rule = new TwelveHourField(selectNumberRule(10, tokenLen));
/*      */           break;
/*      */         case 'H':
/*  130 */           rule = selectNumberRule(11, tokenLen);
/*      */           break;
/*      */         case 'm':
/*  133 */           rule = selectNumberRule(12, tokenLen);
/*      */           break;
/*      */         case 's':
/*  136 */           rule = selectNumberRule(13, tokenLen);
/*      */           break;
/*      */         case 'S':
/*  139 */           rule = selectNumberRule(14, tokenLen);
/*      */           break;
/*      */         case 'E':
/*  142 */           rule = new TextField(7, (tokenLen < 4) ? shortWeekdays : weekdays);
/*      */           break;
/*      */         case 'u':
/*  145 */           rule = new DayInWeekField(selectNumberRule(7, tokenLen));
/*      */           break;
/*      */         case 'D':
/*  148 */           rule = selectNumberRule(6, tokenLen);
/*      */           break;
/*      */         case 'F':
/*  151 */           rule = selectNumberRule(8, tokenLen);
/*      */           break;
/*      */         case 'w':
/*  154 */           rule = selectNumberRule(3, tokenLen);
/*      */           break;
/*      */         case 'W':
/*  157 */           rule = selectNumberRule(4, tokenLen);
/*      */           break;
/*      */         case 'a':
/*  160 */           rule = new TextField(9, AmPmStrings);
/*      */           break;
/*      */         case 'k':
/*  163 */           rule = new TwentyFourHourField(selectNumberRule(11, tokenLen));
/*      */           break;
/*      */         case 'K':
/*  166 */           rule = selectNumberRule(10, tokenLen);
/*      */           break;
/*      */         case 'X':
/*  169 */           rule = Iso8601_Rule.getRule(tokenLen);
/*      */           break;
/*      */         case 'z':
/*  172 */           if (tokenLen >= 4) {
/*  173 */             rule = new TimeZoneNameRule(this.timeZone, this.locale, 1); break;
/*      */           } 
/*  175 */           rule = new TimeZoneNameRule(this.timeZone, this.locale, 0);
/*      */           break;
/*      */         
/*      */         case 'Z':
/*  179 */           if (tokenLen == 1) {
/*  180 */             rule = TimeZoneNumberRule.INSTANCE_NO_COLON; break;
/*  181 */           }  if (tokenLen == 2) {
/*  182 */             rule = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES; break;
/*      */           } 
/*  184 */           rule = TimeZoneNumberRule.INSTANCE_COLON;
/*      */           break;
/*      */         
/*      */         case '\'':
/*  188 */           sub = token.substring(1);
/*  189 */           if (sub.length() == 1) {
/*  190 */             rule = new CharacterLiteral(sub.charAt(0)); break;
/*      */           } 
/*  192 */           rule = new StringLiteral(sub);
/*      */           break;
/*      */         
/*      */         default:
/*  196 */           throw new IllegalArgumentException("Illegal pattern component: " + token);
/*      */       } 
/*      */       
/*  199 */       rules.add(rule);
/*      */     } 
/*      */     
/*  202 */     return rules;
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
/*      */   protected String parseToken(String pattern, int[] indexRef) {
/*  215 */     StringBuilder buf = new StringBuilder();
/*      */     
/*  217 */     int i = indexRef[0];
/*  218 */     int length = pattern.length();
/*      */     
/*  220 */     char c = pattern.charAt(i);
/*  221 */     if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
/*      */ 
/*      */       
/*  224 */       buf.append(c);
/*      */       
/*  226 */       while (i + 1 < length) {
/*  227 */         char peek = pattern.charAt(i + 1);
/*  228 */         if (peek == c) {
/*  229 */           buf.append(c);
/*  230 */           i++;
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  237 */       buf.append('\'');
/*      */       
/*  239 */       boolean inLiteral = false;
/*      */       
/*  241 */       for (; i < length; i++) {
/*  242 */         c = pattern.charAt(i);
/*      */         
/*  244 */         if (c == '\'')
/*  245 */         { if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
/*      */             
/*  247 */             i++;
/*  248 */             buf.append(c);
/*      */           } else {
/*  250 */             inLiteral = !inLiteral;
/*      */           }  }
/*  252 */         else { if (!inLiteral && ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
/*  253 */             i--;
/*      */             break;
/*      */           } 
/*  256 */           buf.append(c); }
/*      */       
/*      */       } 
/*      */     } 
/*      */     
/*  261 */     indexRef[0] = i;
/*  262 */     return buf.toString();
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
/*      */   protected NumberRule selectNumberRule(int field, int padding) {
/*  275 */     switch (padding) {
/*      */       case 1:
/*  277 */         return new UnpaddedNumberField(field);
/*      */       case 2:
/*  279 */         return new TwoDigitNumberField(field);
/*      */     } 
/*  281 */     return new PaddedNumberField(field, padding);
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
/*      */   String format(Object obj) {
/*  297 */     if (obj instanceof Date)
/*  298 */       return format((Date)obj); 
/*  299 */     if (obj instanceof Calendar)
/*  300 */       return format((Calendar)obj); 
/*  301 */     if (obj instanceof Long) {
/*  302 */       return format(((Long)obj).longValue());
/*      */     }
/*  304 */     throw new IllegalArgumentException("Unknown class: " + ((obj == null) ? "<null>" : obj.getClass().getName()));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String format(long millis) {
/*  310 */     Calendar c = Calendar.getInstance(this.timeZone, this.locale);
/*  311 */     c.setTimeInMillis(millis);
/*  312 */     return applyRulesToString(c);
/*      */   }
/*      */ 
/*      */   
/*      */   public String format(Date date) {
/*  317 */     Calendar c = Calendar.getInstance(this.timeZone, this.locale);
/*  318 */     c.setTime(date);
/*  319 */     return applyRulesToString(c);
/*      */   }
/*      */ 
/*      */   
/*      */   public String format(Calendar calendar) {
/*  324 */     return ((StringBuilder)format(calendar, new StringBuilder(this.mMaxLengthEstimate))).toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public <B extends Appendable> B format(long millis, B buf) {
/*  329 */     Calendar c = Calendar.getInstance(this.timeZone, this.locale);
/*  330 */     c.setTimeInMillis(millis);
/*  331 */     return applyRules(c, buf);
/*      */   }
/*      */ 
/*      */   
/*      */   public <B extends Appendable> B format(Date date, B buf) {
/*  336 */     Calendar c = Calendar.getInstance(this.timeZone, this.locale);
/*  337 */     c.setTime(date);
/*  338 */     return applyRules(c, buf);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <B extends Appendable> B format(Calendar calendar, B buf) {
/*  344 */     if (!calendar.getTimeZone().equals(this.timeZone)) {
/*  345 */       calendar = (Calendar)calendar.clone();
/*  346 */       calendar.setTimeZone(this.timeZone);
/*      */     } 
/*  348 */     return applyRules(calendar, buf);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String applyRulesToString(Calendar c) {
/*  358 */     return ((StringBuilder)applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
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
/*      */   private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
/*      */     try {
/*  373 */       for (Rule rule : this.rules) {
/*  374 */         rule.appendTo((Appendable)buf, calendar);
/*      */       }
/*  376 */     } catch (IOException e) {
/*  377 */       throw new DateException(e);
/*      */     } 
/*  379 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxLengthEstimate() {
/*  389 */     return this.mMaxLengthEstimate;
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
/*      */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/*  402 */     in.defaultReadObject();
/*  403 */     init();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void appendDigits(Appendable buffer, int value) throws IOException {
/*  413 */     buffer.append((char)(value / 10 + 48));
/*  414 */     buffer.append((char)(value % 10 + 48));
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
/*      */   private static void appendFullDigits(Appendable buffer, int value, int minFieldWidth) throws IOException {
/*  428 */     if (value < 10000) {
/*      */ 
/*      */       
/*  431 */       int nDigits = 4;
/*  432 */       if (value < 1000) {
/*  433 */         nDigits--;
/*  434 */         if (value < 100) {
/*  435 */           nDigits--;
/*  436 */           if (value < 10) {
/*  437 */             nDigits--;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  442 */       for (int i = minFieldWidth - nDigits; i > 0; i--) {
/*  443 */         buffer.append('0');
/*      */       }
/*      */       
/*  446 */       switch (nDigits) {
/*      */         case 4:
/*  448 */           buffer.append((char)(value / 1000 + 48));
/*  449 */           value %= 1000;
/*      */         case 3:
/*  451 */           if (value >= 100) {
/*  452 */             buffer.append((char)(value / 100 + 48));
/*  453 */             value %= 100;
/*      */           } else {
/*  455 */             buffer.append('0');
/*      */           } 
/*      */         case 2:
/*  458 */           if (value >= 10) {
/*  459 */             buffer.append((char)(value / 10 + 48));
/*  460 */             value %= 10;
/*      */           } else {
/*  462 */             buffer.append('0');
/*      */           } 
/*      */         case 1:
/*  465 */           buffer.append((char)(value + 48));
/*      */           break;
/*      */       } 
/*      */ 
/*      */     
/*      */     } else {
/*  471 */       char[] work = new char[10];
/*  472 */       int digit = 0;
/*  473 */       while (value != 0) {
/*  474 */         work[digit++] = (char)(value % 10 + 48);
/*  475 */         value /= 10;
/*      */       } 
/*      */ 
/*      */       
/*  479 */       while (digit < minFieldWidth) {
/*  480 */         buffer.append('0');
/*  481 */         minFieldWidth--;
/*      */       } 
/*      */ 
/*      */       
/*  485 */       while (--digit >= 0) {
/*  486 */         buffer.append(work[digit]);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static interface Rule
/*      */   {
/*      */     int estimateLength();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void appendTo(Appendable param1Appendable, Calendar param1Calendar) throws IOException;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static interface NumberRule
/*      */     extends Rule
/*      */   {
/*      */     void appendTo(Appendable param1Appendable, int param1Int) throws IOException;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class CharacterLiteral
/*      */     implements Rule
/*      */   {
/*      */     private final char mValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     CharacterLiteral(char value) {
/*  544 */       this.mValue = value;
/*      */     }
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  549 */       return 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  554 */       buffer.append(this.mValue);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class StringLiteral
/*      */     implements Rule
/*      */   {
/*      */     private final String mValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     StringLiteral(String value) {
/*  572 */       this.mValue = value;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  580 */       return this.mValue.length();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  588 */       buffer.append(this.mValue);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TextField
/*      */     implements Rule
/*      */   {
/*      */     private final int mField;
/*      */ 
/*      */ 
/*      */     
/*      */     private final String[] mValues;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TextField(int field, String[] values) {
/*  608 */       this.mField = field;
/*  609 */       this.mValues = values;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  617 */       int max = 0;
/*  618 */       for (int i = this.mValues.length; --i >= 0; ) {
/*  619 */         int len = this.mValues[i].length();
/*  620 */         if (len > max) {
/*  621 */           max = len;
/*      */         }
/*      */       } 
/*  624 */       return max;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  632 */       buffer.append(this.mValues[calendar.get(this.mField)]);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class UnpaddedNumberField
/*      */     implements NumberRule
/*      */   {
/*      */     private final int mField;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     UnpaddedNumberField(int field) {
/*  650 */       this.mField = field;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  658 */       return 4;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  666 */       appendTo(buffer, calendar.get(this.mField));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void appendTo(Appendable buffer, int value) throws IOException {
/*  674 */       if (value < 10) {
/*  675 */         buffer.append((char)(value + 48));
/*  676 */       } else if (value < 100) {
/*  677 */         FastDatePrinter.appendDigits(buffer, value);
/*      */       } else {
/*  679 */         FastDatePrinter.appendFullDigits(buffer, value, 1);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class UnpaddedMonthField
/*      */     implements NumberRule
/*      */   {
/*  690 */     static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();
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
/*      */     public int estimateLength() {
/*  704 */       return 2;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  712 */       appendTo(buffer, calendar.get(2) + 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void appendTo(Appendable buffer, int value) throws IOException {
/*  720 */       if (value < 10) {
/*  721 */         buffer.append((char)(value + 48));
/*      */       } else {
/*  723 */         FastDatePrinter.appendDigits(buffer, value);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class PaddedNumberField
/*      */     implements NumberRule
/*      */   {
/*      */     private final int mField;
/*      */ 
/*      */ 
/*      */     
/*      */     private final int mSize;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     PaddedNumberField(int field, int size) {
/*  744 */       if (size < 3)
/*      */       {
/*  746 */         throw new IllegalArgumentException();
/*      */       }
/*  748 */       this.mField = field;
/*  749 */       this.mSize = size;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  757 */       return this.mSize;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  765 */       appendTo(buffer, calendar.get(this.mField));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void appendTo(Appendable buffer, int value) throws IOException {
/*  773 */       FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TwoDigitNumberField
/*      */     implements NumberRule
/*      */   {
/*      */     private final int mField;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TwoDigitNumberField(int field) {
/*  791 */       this.mField = field;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  799 */       return 2;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  807 */       appendTo(buffer, calendar.get(this.mField));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void appendTo(Appendable buffer, int value) throws IOException {
/*  815 */       if (value < 100) {
/*  816 */         FastDatePrinter.appendDigits(buffer, value);
/*      */       } else {
/*  818 */         FastDatePrinter.appendFullDigits(buffer, value, 2);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TwoDigitYearField
/*      */     implements NumberRule
/*      */   {
/*  829 */     static final TwoDigitYearField INSTANCE = new TwoDigitYearField();
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
/*      */     public int estimateLength() {
/*  842 */       return 2;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  850 */       appendTo(buffer, calendar.get(1) % 100);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void appendTo(Appendable buffer, int value) throws IOException {
/*  858 */       FastDatePrinter.appendDigits(buffer, value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TwoDigitMonthField
/*      */     implements NumberRule
/*      */   {
/*  868 */     static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();
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
/*      */     public int estimateLength() {
/*  881 */       return 2;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  889 */       appendTo(buffer, calendar.get(2) + 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void appendTo(Appendable buffer, int value) throws IOException {
/*  897 */       FastDatePrinter.appendDigits(buffer, value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TwelveHourField
/*      */     implements NumberRule
/*      */   {
/*      */     private final FastDatePrinter.NumberRule mRule;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TwelveHourField(FastDatePrinter.NumberRule rule) {
/*  915 */       this.mRule = rule;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  923 */       return this.mRule.estimateLength();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  931 */       int value = calendar.get(10);
/*  932 */       if (value == 0) {
/*  933 */         value = calendar.getLeastMaximum(10) + 1;
/*      */       }
/*  935 */       this.mRule.appendTo(buffer, value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, int value) throws IOException {
/*  943 */       this.mRule.appendTo(buffer, value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TwentyFourHourField
/*      */     implements NumberRule
/*      */   {
/*      */     private final FastDatePrinter.NumberRule mRule;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TwentyFourHourField(FastDatePrinter.NumberRule rule) {
/*  961 */       this.mRule = rule;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/*  969 */       return this.mRule.estimateLength();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/*  977 */       int value = calendar.get(11);
/*  978 */       if (value == 0) {
/*  979 */         value = calendar.getMaximum(11) + 1;
/*      */       }
/*  981 */       this.mRule.appendTo(buffer, value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, int value) throws IOException {
/*  989 */       this.mRule.appendTo(buffer, value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class DayInWeekField
/*      */     implements NumberRule
/*      */   {
/*      */     private final FastDatePrinter.NumberRule mRule;
/*      */ 
/*      */     
/*      */     DayInWeekField(FastDatePrinter.NumberRule rule) {
/* 1002 */       this.mRule = rule;
/*      */     }
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/* 1007 */       return this.mRule.estimateLength();
/*      */     }
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/* 1012 */       int value = calendar.get(7);
/* 1013 */       this.mRule.appendTo(buffer, (value != 1) ? (value - 1) : 7);
/*      */     }
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, int value) throws IOException {
/* 1018 */       this.mRule.appendTo(buffer, value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class WeekYear
/*      */     implements NumberRule
/*      */   {
/*      */     private final FastDatePrinter.NumberRule mRule;
/*      */ 
/*      */     
/*      */     WeekYear(FastDatePrinter.NumberRule rule) {
/* 1031 */       this.mRule = rule;
/*      */     }
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/* 1036 */       return this.mRule.estimateLength();
/*      */     }
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/* 1041 */       this.mRule.appendTo(buffer, calendar.getWeekYear());
/*      */     }
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, int value) throws IOException {
/* 1046 */       this.mRule.appendTo(buffer, value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 1052 */   private static final ConcurrentMap<TimeZoneDisplayKey, String> C_TIME_ZONE_DISPLAY_CACHE = new ConcurrentHashMap<>(7);
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
/*      */   static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
/* 1066 */     TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
/* 1067 */     String value = C_TIME_ZONE_DISPLAY_CACHE.get(key);
/* 1068 */     if (value == null) {
/*      */       
/* 1070 */       value = tz.getDisplayName(daylight, style, locale);
/* 1071 */       String prior = C_TIME_ZONE_DISPLAY_CACHE.putIfAbsent(key, value);
/* 1072 */       if (prior != null) {
/* 1073 */         value = prior;
/*      */       }
/*      */     } 
/* 1076 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TimeZoneNameRule
/*      */     implements Rule
/*      */   {
/*      */     private final Locale mLocale;
/*      */ 
/*      */     
/*      */     private final int mStyle;
/*      */ 
/*      */     
/*      */     private final String mStandard;
/*      */ 
/*      */     
/*      */     private final String mDaylight;
/*      */ 
/*      */ 
/*      */     
/*      */     TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
/* 1098 */       this.mLocale = locale;
/* 1099 */       this.mStyle = style;
/*      */       
/* 1101 */       this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
/* 1102 */       this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/* 1113 */       return Math.max(this.mStandard.length(), this.mDaylight.length());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/* 1121 */       TimeZone zone = calendar.getTimeZone();
/* 1122 */       if (calendar.get(16) != 0) {
/* 1123 */         buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
/*      */       } else {
/* 1125 */         buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TimeZoneNumberRule
/*      */     implements Rule
/*      */   {
/* 1136 */     static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
/* 1137 */     static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
/*      */ 
/*      */ 
/*      */     
/*      */     final boolean mColon;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TimeZoneNumberRule(boolean colon) {
/* 1147 */       this.mColon = colon;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/* 1155 */       return 5;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/* 1164 */       int offset = calendar.get(15) + calendar.get(16);
/*      */       
/* 1166 */       if (offset < 0) {
/* 1167 */         buffer.append('-');
/* 1168 */         offset = -offset;
/*      */       } else {
/* 1170 */         buffer.append('+');
/*      */       } 
/*      */       
/* 1173 */       int hours = offset / 3600000;
/* 1174 */       FastDatePrinter.appendDigits(buffer, hours);
/*      */       
/* 1176 */       if (this.mColon) {
/* 1177 */         buffer.append(':');
/*      */       }
/*      */       
/* 1180 */       int minutes = offset / 60000 - 60 * hours;
/* 1181 */       FastDatePrinter.appendDigits(buffer, minutes);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class Iso8601_Rule
/*      */     implements Rule
/*      */   {
/* 1193 */     static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
/*      */     
/* 1195 */     static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
/*      */     
/* 1197 */     static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);
/*      */ 
/*      */ 
/*      */     
/*      */     final int length;
/*      */ 
/*      */ 
/*      */     
/*      */     static Iso8601_Rule getRule(int tokenLen) {
/* 1206 */       switch (tokenLen) {
/*      */         case 1:
/* 1208 */           return ISO8601_HOURS;
/*      */         case 2:
/* 1210 */           return ISO8601_HOURS_MINUTES;
/*      */         case 3:
/* 1212 */           return ISO8601_HOURS_COLON_MINUTES;
/*      */       } 
/* 1214 */       throw new IllegalArgumentException("invalid number of X");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Iso8601_Rule(int length) {
/* 1226 */       this.length = length;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int estimateLength() {
/* 1234 */       return this.length;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
/* 1242 */       int offset = calendar.get(15) + calendar.get(16);
/* 1243 */       if (offset == 0) {
/* 1244 */         buffer.append("Z");
/*      */         
/*      */         return;
/*      */       } 
/* 1248 */       if (offset < 0) {
/* 1249 */         buffer.append('-');
/* 1250 */         offset = -offset;
/*      */       } else {
/* 1252 */         buffer.append('+');
/*      */       } 
/*      */       
/* 1255 */       int hours = offset / 3600000;
/* 1256 */       FastDatePrinter.appendDigits(buffer, hours);
/*      */       
/* 1258 */       if (this.length < 5) {
/*      */         return;
/*      */       }
/*      */       
/* 1262 */       if (this.length == 6) {
/* 1263 */         buffer.append(':');
/*      */       }
/*      */       
/* 1266 */       int minutes = offset / 60000 - 60 * hours;
/* 1267 */       FastDatePrinter.appendDigits(buffer, minutes);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TimeZoneDisplayKey
/*      */   {
/*      */     private final TimeZone mTimeZone;
/*      */ 
/*      */ 
/*      */     
/*      */     private final int mStyle;
/*      */ 
/*      */ 
/*      */     
/*      */     private final Locale mLocale;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
/* 1291 */       this.mTimeZone = timeZone;
/* 1292 */       if (daylight) {
/* 1293 */         this.mStyle = style | Integer.MIN_VALUE;
/*      */       } else {
/* 1295 */         this.mStyle = style;
/*      */       } 
/* 1297 */       this.mLocale = locale;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1305 */       return (this.mStyle * 31 + this.mLocale.hashCode()) * 31 + this.mTimeZone.hashCode();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1313 */       if (this == obj) {
/* 1314 */         return true;
/*      */       }
/* 1316 */       if (obj instanceof TimeZoneDisplayKey) {
/* 1317 */         TimeZoneDisplayKey other = (TimeZoneDisplayKey)obj;
/* 1318 */         return (this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale));
/*      */       } 
/* 1320 */       return false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\FastDatePrinter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */