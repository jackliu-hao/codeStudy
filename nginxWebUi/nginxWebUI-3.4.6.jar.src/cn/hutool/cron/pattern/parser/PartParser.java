/*     */ package cn.hutool.cron.pattern.parser;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.date.Month;
/*     */ import cn.hutool.core.date.Week;
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.cron.CronException;
/*     */ import cn.hutool.cron.pattern.Part;
/*     */ import cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher;
/*     */ import cn.hutool.cron.pattern.matcher.BoolArrayMatcher;
/*     */ import cn.hutool.cron.pattern.matcher.DayOfMonthMatcher;
/*     */ import cn.hutool.cron.pattern.matcher.PartMatcher;
/*     */ import cn.hutool.cron.pattern.matcher.YearValueMatcher;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class PartParser
/*     */ {
/*     */   private final Part part;
/*     */   
/*     */   public static PartParser of(Part part) {
/*  47 */     return new PartParser(part);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PartParser(Part part) {
/*  55 */     this.part = part;
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
/*     */   public PartMatcher parse(String value) {
/*  71 */     if (isMatchAllStr(value))
/*     */     {
/*  73 */       return (PartMatcher)new AlwaysTrueMatcher();
/*     */     }
/*     */     
/*  76 */     List<Integer> values = parseArray(value);
/*  77 */     if (values.size() == 0) {
/*  78 */       throw new CronException("Invalid part value: [{}]", new Object[] { value });
/*     */     }
/*     */     
/*  81 */     switch (this.part) {
/*     */       case DAY_OF_MONTH:
/*  83 */         return (PartMatcher)new DayOfMonthMatcher(values);
/*     */       case YEAR:
/*  85 */         return (PartMatcher)new YearValueMatcher(values);
/*     */     } 
/*  87 */     return (PartMatcher)new BoolArrayMatcher(values);
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
/*     */   private List<Integer> parseArray(String value) {
/* 103 */     List<Integer> values = new ArrayList<>();
/*     */     
/* 105 */     List<String> parts = StrUtil.split(value, ',');
/* 106 */     for (String part : parts) {
/* 107 */       CollUtil.addAllIfNotContains(values, parseStep(part));
/*     */     }
/* 109 */     return values;
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
/*     */   private List<Integer> parseStep(String value) {
/*     */     List<Integer> results;
/* 125 */     List<String> parts = StrUtil.split(value, '/');
/* 126 */     int size = parts.size();
/*     */ 
/*     */     
/* 129 */     if (size == 1) {
/* 130 */       results = parseRange(value, -1);
/* 131 */     } else if (size == 2) {
/* 132 */       int step = parseNumber(parts.get(1));
/* 133 */       if (step < 1) {
/* 134 */         throw new CronException("Non positive divisor for field: [{}]", new Object[] { value });
/*     */       }
/* 136 */       results = parseRange(parts.get(0), step);
/*     */     } else {
/* 138 */       throw new CronException("Invalid syntax of field: [{}]", new Object[] { value });
/*     */     } 
/* 140 */     return results;
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
/*     */   private List<Integer> parseRange(String value, int step) {
/* 158 */     List<Integer> results = new ArrayList<>();
/*     */ 
/*     */     
/* 161 */     if (value.length() <= 2) {
/*     */       
/* 163 */       int minValue = this.part.getMin();
/* 164 */       if (false == isMatchAllStr(value)) {
/* 165 */         minValue = Math.max(minValue, parseNumber(value));
/*     */       
/*     */       }
/* 168 */       else if (step < 1) {
/* 169 */         step = 1;
/*     */       } 
/*     */       
/* 172 */       if (step > 0) {
/* 173 */         int maxValue = this.part.getMax();
/* 174 */         if (minValue > maxValue) {
/* 175 */           throw new CronException("Invalid value {} > {}", new Object[] { Integer.valueOf(minValue), Integer.valueOf(maxValue) });
/*     */         }
/*     */         int i;
/* 178 */         for (i = minValue; i <= maxValue; i += step) {
/* 179 */           results.add(Integer.valueOf(i));
/*     */         }
/*     */       } else {
/*     */         
/* 183 */         results.add(Integer.valueOf(minValue));
/*     */       } 
/* 185 */       return results;
/*     */     } 
/*     */ 
/*     */     
/* 189 */     List<String> parts = StrUtil.split(value, '-');
/* 190 */     int size = parts.size();
/* 191 */     if (size == 1) {
/* 192 */       int v1 = parseNumber(value);
/* 193 */       if (step > 0) {
/* 194 */         NumberUtil.appendRange(v1, this.part.getMax(), step, results);
/*     */       } else {
/* 196 */         results.add(Integer.valueOf(v1));
/*     */       } 
/* 198 */     } else if (size == 2) {
/* 199 */       int v1 = parseNumber(parts.get(0));
/* 200 */       int v2 = parseNumber(parts.get(1));
/* 201 */       if (step < 1)
/*     */       {
/* 203 */         step = 1;
/*     */       }
/* 205 */       if (v1 < v2) {
/* 206 */         NumberUtil.appendRange(v1, v2, step, results);
/* 207 */       } else if (v1 > v2) {
/* 208 */         NumberUtil.appendRange(v1, this.part.getMax(), step, results);
/* 209 */         NumberUtil.appendRange(this.part.getMin(), v2, step, results);
/*     */       } else {
/* 211 */         NumberUtil.appendRange(v1, this.part.getMax(), step, results);
/*     */       } 
/*     */     } else {
/* 214 */       throw new CronException("Invalid syntax of field: [{}]", new Object[] { value });
/*     */     } 
/* 216 */     return results;
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
/*     */   private static boolean isMatchAllStr(String value) {
/* 228 */     return (1 == value.length() && ("*".equals(value) || "?".equals(value)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int parseNumber(String value) throws CronException {
/*     */     int i;
/*     */     try {
/* 241 */       i = Integer.parseInt(value);
/* 242 */     } catch (NumberFormatException ignore) {
/* 243 */       i = parseAlias(value);
/*     */     } 
/*     */ 
/*     */     
/* 247 */     if (i < 0) {
/* 248 */       i += this.part.getMax();
/*     */     }
/*     */ 
/*     */     
/* 252 */     if (Part.DAY_OF_WEEK.equals(this.part) && Week.SUNDAY.getIso8601Value() == i) {
/* 253 */       i = Week.SUNDAY.ordinal();
/*     */     }
/*     */     
/* 256 */     return this.part.checkValue(i);
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
/*     */   private int parseAlias(String name) throws CronException {
/* 271 */     if ("L".equalsIgnoreCase(name))
/*     */     {
/* 273 */       return this.part.getMax();
/*     */     }
/*     */     
/* 276 */     switch (this.part) {
/*     */       
/*     */       case MONTH:
/* 279 */         return Month.of(name).getValueBaseOne();
/*     */       
/*     */       case DAY_OF_WEEK:
/* 282 */         return Week.of(name).ordinal();
/*     */     } 
/*     */     
/* 285 */     throw new CronException("Invalid alias value: [{}]", new Object[] { name });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\parser\PartParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */