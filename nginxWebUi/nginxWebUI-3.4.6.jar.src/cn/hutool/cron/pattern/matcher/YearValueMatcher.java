/*    */ package cn.hutool.cron.pattern.matcher;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedHashSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class YearValueMatcher
/*    */   implements PartMatcher
/*    */ {
/*    */   private final LinkedHashSet<Integer> valueList;
/*    */   
/*    */   public YearValueMatcher(Collection<Integer> intValueList) {
/* 17 */     this.valueList = new LinkedHashSet<>(intValueList);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean match(Integer t) {
/* 22 */     return this.valueList.contains(t);
/*    */   }
/*    */ 
/*    */   
/*    */   public int nextAfter(int value) {
/* 27 */     for (Integer year : this.valueList) {
/* 28 */       if (year.intValue() >= value) {
/* 29 */         return year.intValue();
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 34 */     return -1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\matcher\YearValueMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */