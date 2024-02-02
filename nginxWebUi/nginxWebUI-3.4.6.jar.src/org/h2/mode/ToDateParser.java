/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.value.ValueTimestamp;
/*     */ import org.h2.value.ValueTimestampTimeZone;
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
/*     */ public final class ToDateParser
/*     */ {
/*     */   private final SessionLocal session;
/*     */   private final String unmodifiedInputStr;
/*     */   private final String unmodifiedFormatStr;
/*     */   private final ConfigParam functionName;
/*     */   private String inputStr;
/*     */   private String formatStr;
/*     */   private boolean doyValid = false;
/*     */   private boolean absoluteDayValid = false;
/*     */   private boolean hour12Valid = false;
/*     */   private boolean timeZoneHMValid = false;
/*     */   private boolean bc;
/*     */   private long absoluteDay;
/*     */   private int year;
/*     */   private int month;
/*  40 */   private int day = 1;
/*     */   
/*     */   private int dayOfYear;
/*     */   
/*     */   private int hour;
/*     */   
/*     */   private int minute;
/*     */   
/*     */   private int second;
/*     */   
/*     */   private int nanos;
/*     */   
/*     */   private int hour12;
/*     */   
/*     */   private boolean isAM = true;
/*     */   
/*     */   private TimeZoneProvider timeZone;
/*     */   
/*     */   private int timeZoneHour;
/*     */   private int timeZoneMinute;
/*     */   private int currentYear;
/*     */   private int currentMonth;
/*     */   
/*     */   private ToDateParser(SessionLocal paramSessionLocal, ConfigParam paramConfigParam, String paramString1, String paramString2) {
/*  64 */     this.session = paramSessionLocal;
/*  65 */     this.functionName = paramConfigParam;
/*  66 */     this.inputStr = paramString1.trim();
/*     */     
/*  68 */     this.unmodifiedInputStr = this.inputStr;
/*  69 */     if (paramString2 == null || paramString2.isEmpty()) {
/*     */       
/*  71 */       this.formatStr = paramConfigParam.getDefaultFormatStr();
/*     */     } else {
/*  73 */       this.formatStr = paramString2.trim();
/*     */     } 
/*     */     
/*  76 */     this.unmodifiedFormatStr = this.formatStr;
/*     */   }
/*     */ 
/*     */   
/*     */   private static ToDateParser getTimestampParser(SessionLocal paramSessionLocal, ConfigParam paramConfigParam, String paramString1, String paramString2) {
/*  81 */     ToDateParser toDateParser = new ToDateParser(paramSessionLocal, paramConfigParam, paramString1, paramString2);
/*  82 */     parse(toDateParser);
/*  83 */     return toDateParser;
/*     */   }
/*     */   private ValueTimestamp getResultingValue() {
/*     */     long l1;
/*     */     int i;
/*  88 */     if (this.absoluteDayValid) {
/*  89 */       l1 = DateTimeUtils.dateValueFromAbsoluteDay(this.absoluteDay);
/*     */     } else {
/*  91 */       i = this.year;
/*  92 */       if (i == 0) {
/*  93 */         i = getCurrentYear();
/*     */       }
/*  95 */       if (this.bc) {
/*  96 */         i = 1 - i;
/*     */       }
/*  98 */       if (this.doyValid) {
/*  99 */         l1 = DateTimeUtils.dateValueFromAbsoluteDay(
/* 100 */             DateTimeUtils.absoluteDayFromYear(i) + this.dayOfYear - 1L);
/*     */       } else {
/* 102 */         int j = this.month;
/* 103 */         if (j == 0)
/*     */         {
/* 105 */           j = getCurrentMonth();
/*     */         }
/* 107 */         l1 = DateTimeUtils.dateValue(i, j, this.day);
/*     */       } 
/*     */     } 
/*     */     
/* 111 */     if (this.hour12Valid) {
/* 112 */       i = this.hour12 % 12;
/* 113 */       if (!this.isAM) {
/* 114 */         i += 12;
/*     */       }
/*     */     } else {
/* 117 */       i = this.hour;
/*     */     } 
/* 119 */     long l2 = ((i * 60 + this.minute) * 60 + this.second) * 1000000000L + this.nanos;
/* 120 */     return ValueTimestamp.fromDateValueAndNanos(l1, l2);
/*     */   }
/*     */   private ValueTimestampTimeZone getResultingValueWithTimeZone() {
/*     */     int i;
/* 124 */     ValueTimestamp valueTimestamp = getResultingValue();
/* 125 */     long l1 = valueTimestamp.getDateValue(), l2 = valueTimestamp.getTimeNanos();
/*     */     
/* 127 */     if (this.timeZoneHMValid) {
/* 128 */       i = (this.timeZoneHour * 60 + ((this.timeZoneHour >= 0) ? this.timeZoneMinute : -this.timeZoneMinute)) * 60;
/*     */     } else {
/*     */       
/* 131 */       i = ((this.timeZone != null) ? this.timeZone : this.session.currentTimeZone()).getTimeZoneOffsetLocal(l1, l2);
/*     */     } 
/* 133 */     return ValueTimestampTimeZone.fromDateValueAndNanos(l1, valueTimestamp.getTimeNanos(), i);
/*     */   }
/*     */   
/*     */   String getInputStr() {
/* 137 */     return this.inputStr;
/*     */   }
/*     */   
/*     */   String getFormatStr() {
/* 141 */     return this.formatStr;
/*     */   }
/*     */   
/*     */   String getFunctionName() {
/* 145 */     return this.functionName.name();
/*     */   }
/*     */   
/*     */   private void queryCurrentYearAndMonth() {
/* 149 */     long l = this.session.currentTimestamp().getDateValue();
/* 150 */     this.currentYear = DateTimeUtils.yearFromDateValue(l);
/* 151 */     this.currentMonth = DateTimeUtils.monthFromDateValue(l);
/*     */   }
/*     */   
/*     */   int getCurrentYear() {
/* 155 */     if (this.currentYear == 0) {
/* 156 */       queryCurrentYearAndMonth();
/*     */     }
/* 158 */     return this.currentYear;
/*     */   }
/*     */   
/*     */   int getCurrentMonth() {
/* 162 */     if (this.currentMonth == 0) {
/* 163 */       queryCurrentYearAndMonth();
/*     */     }
/* 165 */     return this.currentMonth;
/*     */   }
/*     */   
/*     */   void setAbsoluteDay(int paramInt) {
/* 169 */     this.doyValid = false;
/* 170 */     this.absoluteDayValid = true;
/* 171 */     this.absoluteDay = paramInt;
/*     */   }
/*     */   
/*     */   void setBC(boolean paramBoolean) {
/* 175 */     this.doyValid = false;
/* 176 */     this.absoluteDayValid = false;
/* 177 */     this.bc = paramBoolean;
/*     */   }
/*     */   
/*     */   void setYear(int paramInt) {
/* 181 */     this.doyValid = false;
/* 182 */     this.absoluteDayValid = false;
/* 183 */     this.year = paramInt;
/*     */   }
/*     */   
/*     */   void setMonth(int paramInt) {
/* 187 */     this.doyValid = false;
/* 188 */     this.absoluteDayValid = false;
/* 189 */     this.month = paramInt;
/* 190 */     if (this.year == 0) {
/* 191 */       this.year = 1970;
/*     */     }
/*     */   }
/*     */   
/*     */   void setDay(int paramInt) {
/* 196 */     this.doyValid = false;
/* 197 */     this.absoluteDayValid = false;
/* 198 */     this.day = paramInt;
/* 199 */     if (this.year == 0) {
/* 200 */       this.year = 1970;
/*     */     }
/*     */   }
/*     */   
/*     */   void setDayOfYear(int paramInt) {
/* 205 */     this.doyValid = true;
/* 206 */     this.absoluteDayValid = false;
/* 207 */     this.dayOfYear = paramInt;
/*     */   }
/*     */   
/*     */   void setHour(int paramInt) {
/* 211 */     this.hour12Valid = false;
/* 212 */     this.hour = paramInt;
/*     */   }
/*     */   
/*     */   void setMinute(int paramInt) {
/* 216 */     this.minute = paramInt;
/*     */   }
/*     */   
/*     */   void setSecond(int paramInt) {
/* 220 */     this.second = paramInt;
/*     */   }
/*     */   
/*     */   void setNanos(int paramInt) {
/* 224 */     this.nanos = paramInt;
/*     */   }
/*     */   
/*     */   void setAmPm(boolean paramBoolean) {
/* 228 */     this.hour12Valid = true;
/* 229 */     this.isAM = paramBoolean;
/*     */   }
/*     */   
/*     */   void setHour12(int paramInt) {
/* 233 */     this.hour12Valid = true;
/* 234 */     this.hour12 = paramInt;
/*     */   }
/*     */   
/*     */   void setTimeZone(TimeZoneProvider paramTimeZoneProvider) {
/* 238 */     this.timeZoneHMValid = false;
/* 239 */     this.timeZone = paramTimeZoneProvider;
/*     */   }
/*     */   
/*     */   void setTimeZoneHour(int paramInt) {
/* 243 */     this.timeZoneHMValid = true;
/* 244 */     this.timeZoneHour = paramInt;
/*     */   }
/*     */   
/*     */   void setTimeZoneMinute(int paramInt) {
/* 248 */     this.timeZoneHMValid = true;
/* 249 */     this.timeZoneMinute = paramInt;
/*     */   }
/*     */   
/*     */   private boolean hasToParseData() {
/* 253 */     return !this.formatStr.isEmpty();
/*     */   }
/*     */   
/*     */   private void removeFirstChar() {
/* 257 */     if (!this.formatStr.isEmpty()) {
/* 258 */       this.formatStr = this.formatStr.substring(1);
/*     */     }
/* 260 */     if (!this.inputStr.isEmpty()) {
/* 261 */       this.inputStr = this.inputStr.substring(1);
/*     */     }
/*     */   }
/*     */   
/*     */   private static ToDateParser parse(ToDateParser paramToDateParser) {
/* 266 */     while (paramToDateParser.hasToParseData()) {
/*     */       
/* 268 */       List<ToDateTokenizer.FormatTokenEnum> list = ToDateTokenizer.FormatTokenEnum.getTokensInQuestion(paramToDateParser.getFormatStr());
/* 269 */       if (list == null) {
/* 270 */         paramToDateParser.removeFirstChar();
/*     */         continue;
/*     */       } 
/* 273 */       boolean bool = false;
/* 274 */       for (ToDateTokenizer.FormatTokenEnum formatTokenEnum : list) {
/* 275 */         if (formatTokenEnum.parseFormatStrWithToken(paramToDateParser)) {
/* 276 */           bool = true;
/*     */           break;
/*     */         } 
/*     */       } 
/* 280 */       if (!bool) {
/* 281 */         paramToDateParser.removeFirstChar();
/*     */       }
/*     */     } 
/* 284 */     return paramToDateParser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void remove(String paramString1, String paramString2) {
/* 294 */     if (paramString1 != null && this.inputStr.length() >= paramString1.length()) {
/* 295 */       this.inputStr = this.inputStr.substring(paramString1.length());
/*     */     }
/* 297 */     if (paramString2 != null && this.formatStr.length() >= paramString2.length()) {
/* 298 */       this.formatStr = this.formatStr.substring(paramString2.length());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 304 */     int i = this.inputStr.length();
/* 305 */     int j = this.unmodifiedInputStr.length();
/* 306 */     int k = j - i;
/* 307 */     int m = (i <= 0) ? i : (i - 1);
/*     */     
/* 309 */     int n = this.unmodifiedFormatStr.length();
/* 310 */     int i1 = n - this.formatStr.length();
/*     */     
/* 312 */     return String.format("\n    %s('%s', '%s')", new Object[] { this.functionName, this.unmodifiedInputStr, this.unmodifiedFormatStr
/* 313 */         }) + String.format("\n      %s^%s ,  %s^ <-- Parsing failed at this point", new Object[] {
/* 314 */           String.format("%" + (this.functionName.name().length() + k) + "s", new Object[] { "" }), (m <= 0) ? "" : 
/* 315 */           String.format("%" + m + "s", new Object[] { "" }), (i1 <= 0) ? "" : 
/* 316 */           String.format("%" + i1 + "s", new Object[] { "" })
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTimestamp toTimestamp(SessionLocal paramSessionLocal, String paramString1, String paramString2) {
/* 328 */     ToDateParser toDateParser = getTimestampParser(paramSessionLocal, ConfigParam.TO_TIMESTAMP, paramString1, paramString2);
/* 329 */     return toDateParser.getResultingValue();
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
/*     */   public static ValueTimestampTimeZone toTimestampTz(SessionLocal paramSessionLocal, String paramString1, String paramString2) {
/* 341 */     ToDateParser toDateParser = getTimestampParser(paramSessionLocal, ConfigParam.TO_TIMESTAMP_TZ, paramString1, paramString2);
/* 342 */     return toDateParser.getResultingValueWithTimeZone();
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
/*     */   public static ValueTimestamp toDate(SessionLocal paramSessionLocal, String paramString1, String paramString2) {
/* 354 */     ToDateParser toDateParser = getTimestampParser(paramSessionLocal, ConfigParam.TO_DATE, paramString1, paramString2);
/* 355 */     return toDateParser.getResultingValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private enum ConfigParam
/*     */   {
/* 362 */     TO_DATE("DD MON YYYY"),
/* 363 */     TO_TIMESTAMP("DD MON YYYY HH:MI:SS"),
/* 364 */     TO_TIMESTAMP_TZ("DD MON YYYY HH:MI:SS TZR");
/*     */     private final String defaultFormatStr;
/*     */     
/*     */     ConfigParam(String param1String1) {
/* 368 */       this.defaultFormatStr = param1String1;
/*     */     }
/*     */     String getDefaultFormatStr() {
/* 371 */       return this.defaultFormatStr;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\ToDateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */