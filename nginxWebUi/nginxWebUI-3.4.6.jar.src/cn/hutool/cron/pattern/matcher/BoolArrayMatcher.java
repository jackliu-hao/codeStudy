/*    */ package cn.hutool.cron.pattern.matcher;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BoolArrayMatcher
/*    */   implements PartMatcher
/*    */ {
/*    */   private final int minValue;
/*    */   private final boolean[] bValues;
/*    */   
/*    */   public BoolArrayMatcher(List<Integer> intValueList) {
/* 29 */     Assert.isTrue(CollUtil.isNotEmpty(intValueList), "Values must be not empty!", new Object[0]);
/* 30 */     this.bValues = new boolean[((Integer)Collections.max((Collection)intValueList)).intValue() + 1];
/* 31 */     int min = Integer.MAX_VALUE;
/* 32 */     for (Integer value : intValueList) {
/* 33 */       min = Math.min(min, value.intValue());
/* 34 */       this.bValues[value.intValue()] = true;
/*    */     } 
/* 36 */     this.minValue = min;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean match(Integer value) {
/* 41 */     if (null == value || value.intValue() >= this.bValues.length) {
/* 42 */       return false;
/*    */     }
/* 44 */     return this.bValues[value.intValue()];
/*    */   }
/*    */ 
/*    */   
/*    */   public int nextAfter(int value) {
/* 49 */     if (value > this.minValue) {
/* 50 */       while (value < this.bValues.length) {
/* 51 */         if (this.bValues[value]) {
/* 52 */           return value;
/*    */         }
/* 54 */         value++;
/*    */       } 
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 61 */     return this.minValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getMinValue() {
/* 70 */     return this.minValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 75 */     return StrUtil.format("Matcher:{}", new Object[] { this.bValues });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\matcher\BoolArrayMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */