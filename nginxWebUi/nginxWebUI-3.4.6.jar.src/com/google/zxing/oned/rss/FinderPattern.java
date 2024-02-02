/*    */ package com.google.zxing.oned.rss;
/*    */ 
/*    */ import com.google.zxing.ResultPoint;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class FinderPattern
/*    */ {
/*    */   private final int value;
/*    */   private final int[] startEnd;
/*    */   private final ResultPoint[] resultPoints;
/*    */   
/*    */   public FinderPattern(int value, int[] startEnd, int start, int end, int rowNumber) {
/* 31 */     this.value = value;
/* 32 */     this.startEnd = startEnd;
/* 33 */     this.resultPoints = new ResultPoint[] { new ResultPoint(start, rowNumber), new ResultPoint(end, rowNumber) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getValue() {
/* 40 */     return this.value;
/*    */   }
/*    */   
/*    */   public int[] getStartEnd() {
/* 44 */     return this.startEnd;
/*    */   }
/*    */   
/*    */   public ResultPoint[] getResultPoints() {
/* 48 */     return this.resultPoints;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 53 */     if (!(o instanceof FinderPattern)) {
/* 54 */       return false;
/*    */     }
/* 56 */     FinderPattern that = (FinderPattern)o;
/* 57 */     return (this.value == that.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\FinderPattern.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */