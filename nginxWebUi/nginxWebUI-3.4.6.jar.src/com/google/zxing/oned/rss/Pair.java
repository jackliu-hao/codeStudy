/*    */ package com.google.zxing.oned.rss;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class Pair
/*    */   extends DataCharacter
/*    */ {
/*    */   private final FinderPattern finderPattern;
/*    */   private int count;
/*    */   
/*    */   Pair(int value, int checksumPortion, FinderPattern finderPattern) {
/* 25 */     super(value, checksumPortion);
/* 26 */     this.finderPattern = finderPattern;
/*    */   }
/*    */   
/*    */   FinderPattern getFinderPattern() {
/* 30 */     return this.finderPattern;
/*    */   }
/*    */   
/*    */   int getCount() {
/* 34 */     return this.count;
/*    */   }
/*    */   
/*    */   void incrementCount() {
/* 38 */     this.count++;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\Pair.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */