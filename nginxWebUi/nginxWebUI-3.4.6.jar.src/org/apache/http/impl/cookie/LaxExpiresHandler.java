/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.Calendar;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.cookie.SetCookie;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class LaxExpiresHandler
/*     */   extends AbstractCookieAttributeHandler
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*  54 */   static final TimeZone UTC = TimeZone.getTimeZone("UTC");
/*     */   private static final BitSet DELIMS;
/*     */   
/*     */   static {
/*  58 */     BitSet bitSet = new BitSet();
/*  59 */     bitSet.set(9); int b;
/*  60 */     for (b = 32; b <= 47; b++) {
/*  61 */       bitSet.set(b);
/*     */     }
/*  63 */     for (b = 59; b <= 64; b++) {
/*  64 */       bitSet.set(b);
/*     */     }
/*  66 */     for (b = 91; b <= 96; b++) {
/*  67 */       bitSet.set(b);
/*     */     }
/*  69 */     for (b = 123; b <= 126; b++) {
/*  70 */       bitSet.set(b);
/*     */     }
/*  72 */     DELIMS = bitSet;
/*     */ 
/*     */ 
/*     */     
/*  76 */     ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>(12);
/*  77 */     map.put("jan", Integer.valueOf(0));
/*  78 */     map.put("feb", Integer.valueOf(1));
/*  79 */     map.put("mar", Integer.valueOf(2));
/*  80 */     map.put("apr", Integer.valueOf(3));
/*  81 */     map.put("may", Integer.valueOf(4));
/*  82 */     map.put("jun", Integer.valueOf(5));
/*  83 */     map.put("jul", Integer.valueOf(6));
/*  84 */     map.put("aug", Integer.valueOf(7));
/*  85 */     map.put("sep", Integer.valueOf(8));
/*  86 */     map.put("oct", Integer.valueOf(9));
/*  87 */     map.put("nov", Integer.valueOf(10));
/*  88 */     map.put("dec", Integer.valueOf(11));
/*  89 */     MONTHS = map;
/*     */   }
/*     */   private static final Map<String, Integer> MONTHS;
/*  92 */   private static final Pattern TIME_PATTERN = Pattern.compile("^([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2})([^0-9].*)?$");
/*     */   
/*  94 */   private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("^([0-9]{1,2})([^0-9].*)?$");
/*     */   
/*  96 */   private static final Pattern MONTH_PATTERN = Pattern.compile("^(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(.*)?$", 2);
/*     */   
/*  98 */   private static final Pattern YEAR_PATTERN = Pattern.compile("^([0-9]{2,4})([^0-9].*)?$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/* 107 */     Args.notNull(cookie, "Cookie");
/* 108 */     ParserCursor cursor = new ParserCursor(0, value.length());
/* 109 */     StringBuilder content = new StringBuilder();
/*     */     
/* 111 */     int second = 0, minute = 0, hour = 0, day = 0, month = 0, year = 0;
/* 112 */     boolean foundTime = false, foundDayOfMonth = false, foundMonth = false, foundYear = false;
/*     */     try {
/* 114 */       while (!cursor.atEnd()) {
/* 115 */         skipDelims(value, cursor);
/* 116 */         content.setLength(0);
/* 117 */         copyContent(value, cursor, content);
/*     */         
/* 119 */         if (content.length() == 0) {
/*     */           break;
/*     */         }
/* 122 */         if (!foundTime) {
/* 123 */           Matcher matcher = TIME_PATTERN.matcher(content);
/* 124 */           if (matcher.matches()) {
/* 125 */             foundTime = true;
/* 126 */             hour = Integer.parseInt(matcher.group(1));
/* 127 */             minute = Integer.parseInt(matcher.group(2));
/* 128 */             second = Integer.parseInt(matcher.group(3));
/*     */             continue;
/*     */           } 
/*     */         } 
/* 132 */         if (!foundDayOfMonth) {
/* 133 */           Matcher matcher = DAY_OF_MONTH_PATTERN.matcher(content);
/* 134 */           if (matcher.matches()) {
/* 135 */             foundDayOfMonth = true;
/* 136 */             day = Integer.parseInt(matcher.group(1));
/*     */             continue;
/*     */           } 
/*     */         } 
/* 140 */         if (!foundMonth) {
/* 141 */           Matcher matcher = MONTH_PATTERN.matcher(content);
/* 142 */           if (matcher.matches()) {
/* 143 */             foundMonth = true;
/* 144 */             month = ((Integer)MONTHS.get(matcher.group(1).toLowerCase(Locale.ROOT))).intValue();
/*     */             continue;
/*     */           } 
/*     */         } 
/* 148 */         if (!foundYear) {
/* 149 */           Matcher matcher = YEAR_PATTERN.matcher(content);
/* 150 */           if (matcher.matches()) {
/* 151 */             foundYear = true;
/* 152 */             year = Integer.parseInt(matcher.group(1));
/*     */           }
/*     */         
/*     */         } 
/*     */       } 
/* 157 */     } catch (NumberFormatException ignore) {
/* 158 */       throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
/*     */     } 
/* 160 */     if (!foundTime || !foundDayOfMonth || !foundMonth || !foundYear) {
/* 161 */       throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
/*     */     }
/* 163 */     if (year >= 70 && year <= 99) {
/* 164 */       year = 1900 + year;
/*     */     }
/* 166 */     if (year >= 0 && year <= 69) {
/* 167 */       year = 2000 + year;
/*     */     }
/* 169 */     if (day < 1 || day > 31 || year < 1601 || hour > 23 || minute > 59 || second > 59) {
/* 170 */       throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
/*     */     }
/*     */     
/* 173 */     Calendar c = Calendar.getInstance();
/* 174 */     c.setTimeZone(UTC);
/* 175 */     c.setTimeInMillis(0L);
/* 176 */     c.set(13, second);
/* 177 */     c.set(12, minute);
/* 178 */     c.set(11, hour);
/* 179 */     c.set(5, day);
/* 180 */     c.set(2, month);
/* 181 */     c.set(1, year);
/* 182 */     cookie.setExpiryDate(c.getTime());
/*     */   }
/*     */   
/*     */   private void skipDelims(CharSequence buf, ParserCursor cursor) {
/* 186 */     int pos = cursor.getPos();
/* 187 */     int indexFrom = cursor.getPos();
/* 188 */     int indexTo = cursor.getUpperBound();
/* 189 */     for (int i = indexFrom; i < indexTo; ) {
/* 190 */       char current = buf.charAt(i);
/* 191 */       if (DELIMS.get(current)) {
/* 192 */         pos++;
/*     */         
/*     */         i++;
/*     */       } 
/*     */     } 
/* 197 */     cursor.updatePos(pos);
/*     */   }
/*     */   
/*     */   private void copyContent(CharSequence buf, ParserCursor cursor, StringBuilder dst) {
/* 201 */     int pos = cursor.getPos();
/* 202 */     int indexFrom = cursor.getPos();
/* 203 */     int indexTo = cursor.getUpperBound();
/* 204 */     for (int i = indexFrom; i < indexTo; i++) {
/* 205 */       char current = buf.charAt(i);
/* 206 */       if (DELIMS.get(current)) {
/*     */         break;
/*     */       }
/* 209 */       pos++;
/* 210 */       dst.append(current);
/*     */     } 
/* 212 */     cursor.updatePos(pos);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 217 */     return "expires";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\LaxExpiresHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */