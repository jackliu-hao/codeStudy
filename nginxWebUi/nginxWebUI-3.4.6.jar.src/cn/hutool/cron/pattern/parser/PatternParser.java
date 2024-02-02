/*    */ package cn.hutool.cron.pattern.parser;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.cron.CronException;
/*    */ import cn.hutool.cron.pattern.Part;
/*    */ import cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher;
/*    */ import cn.hutool.cron.pattern.matcher.PartMatcher;
/*    */ import cn.hutool.cron.pattern.matcher.PatternMatcher;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PatternParser
/*    */ {
/* 22 */   private static final PartParser SECOND_VALUE_PARSER = PartParser.of(Part.SECOND);
/* 23 */   private static final PartParser MINUTE_VALUE_PARSER = PartParser.of(Part.MINUTE);
/* 24 */   private static final PartParser HOUR_VALUE_PARSER = PartParser.of(Part.HOUR);
/* 25 */   private static final PartParser DAY_OF_MONTH_VALUE_PARSER = PartParser.of(Part.DAY_OF_MONTH);
/* 26 */   private static final PartParser MONTH_VALUE_PARSER = PartParser.of(Part.MONTH);
/* 27 */   private static final PartParser DAY_OF_WEEK_VALUE_PARSER = PartParser.of(Part.DAY_OF_WEEK);
/* 28 */   private static final PartParser YEAR_VALUE_PARSER = PartParser.of(Part.YEAR);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<PatternMatcher> parse(String cronPattern) {
/* 37 */     return parseGroupPattern(cronPattern);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static List<PatternMatcher> parseGroupPattern(String groupPattern) {
/* 50 */     List<String> patternList = StrUtil.splitTrim(groupPattern, '|');
/* 51 */     List<PatternMatcher> patternMatchers = new ArrayList<>(patternList.size());
/* 52 */     for (String pattern : patternList) {
/* 53 */       patternMatchers.add(parseSingle(pattern));
/*    */     }
/* 55 */     return patternMatchers;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static PatternMatcher parseSingle(String pattern) {
/*    */     AlwaysTrueMatcher alwaysTrueMatcher;
/* 65 */     String[] parts = pattern.split("\\s+");
/* 66 */     Assert.checkBetween(parts.length, 5, 7, () -> new CronException("Pattern [{}] is invalid, it must be 5-7 parts!", new Object[] { pattern }));
/*    */ 
/*    */ 
/*    */     
/* 70 */     int offset = 0;
/* 71 */     if (parts.length == 6 || parts.length == 7) {
/* 72 */       offset = 1;
/*    */     }
/*    */ 
/*    */     
/* 76 */     String secondPart = (1 == offset) ? parts[0] : "0";
/*    */ 
/*    */ 
/*    */     
/* 80 */     if (parts.length == 7) {
/* 81 */       PartMatcher yearMatcher = YEAR_VALUE_PARSER.parse(parts[6]);
/*    */     } else {
/* 83 */       alwaysTrueMatcher = AlwaysTrueMatcher.INSTANCE;
/*    */     } 
/*    */     
/* 86 */     return new PatternMatcher(SECOND_VALUE_PARSER
/*    */         
/* 88 */         .parse(secondPart), MINUTE_VALUE_PARSER
/*    */         
/* 90 */         .parse(parts[offset]), HOUR_VALUE_PARSER
/*    */         
/* 92 */         .parse(parts[1 + offset]), DAY_OF_MONTH_VALUE_PARSER
/*    */         
/* 94 */         .parse(parts[2 + offset]), MONTH_VALUE_PARSER
/*    */         
/* 96 */         .parse(parts[3 + offset]), DAY_OF_WEEK_VALUE_PARSER
/*    */         
/* 98 */         .parse(parts[4 + offset]), (PartMatcher)alwaysTrueMatcher);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\parser\PatternParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */