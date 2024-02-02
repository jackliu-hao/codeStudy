/*    */ package cn.hutool.cron.pattern;
/*    */ 
/*    */ import cn.hutool.core.builder.Builder;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.text.StrJoiner;
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CronPatternBuilder
/*    */   implements Builder<String>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 18 */   final String[] parts = new String[7];
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CronPatternBuilder of() {
/* 25 */     return new CronPatternBuilder();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronPatternBuilder setValues(Part part, int... values) {
/* 36 */     for (int value : values) {
/* 37 */       part.checkValue(value);
/*    */     }
/* 39 */     return set(part, ArrayUtil.join(values, ","));
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
/*    */   public CronPatternBuilder setRange(Part part, int begin, int end) {
/* 51 */     Assert.notNull(part);
/* 52 */     part.checkValue(begin);
/* 53 */     part.checkValue(end);
/* 54 */     return set(part, StrUtil.format("{}-{}", new Object[] { Integer.valueOf(begin), Integer.valueOf(end) }));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronPatternBuilder set(Part part, String value) {
/* 65 */     this.parts[part.ordinal()] = value;
/* 66 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String build() {
/* 71 */     for (int i = Part.MINUTE.ordinal(); i < Part.YEAR.ordinal(); i++) {
/*    */ 
/*    */       
/* 74 */       if (StrUtil.isBlank(this.parts[i])) {
/* 75 */         this.parts[i] = "*";
/*    */       }
/*    */     } 
/*    */     
/* 79 */     return StrJoiner.of(" ")
/* 80 */       .setNullMode(StrJoiner.NullMode.IGNORE)
/* 81 */       .append((Object[])this.parts)
/* 82 */       .toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\CronPatternBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */