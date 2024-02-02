/*    */ package com.google.zxing.oned.rss.expanded;
/*    */ 
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
/*    */ final class ExpandedRow
/*    */ {
/*    */   private final List<ExpandedPair> pairs;
/*    */   private final int rowNumber;
/*    */   private final boolean wasReversed;
/*    */   
/*    */   ExpandedRow(List<ExpandedPair> pairs, int rowNumber, boolean wasReversed) {
/* 33 */     this.pairs = new ArrayList<>(pairs);
/* 34 */     this.rowNumber = rowNumber;
/* 35 */     this.wasReversed = wasReversed;
/*    */   }
/*    */   
/*    */   List<ExpandedPair> getPairs() {
/* 39 */     return this.pairs;
/*    */   }
/*    */   
/*    */   int getRowNumber() {
/* 43 */     return this.rowNumber;
/*    */   }
/*    */   
/*    */   boolean isReversed() {
/* 47 */     return this.wasReversed;
/*    */   }
/*    */   
/*    */   boolean isEquivalent(List<ExpandedPair> otherPairs) {
/* 51 */     return this.pairs.equals(otherPairs);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return "{ " + this.pairs + " }";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 64 */     if (!(o instanceof ExpandedRow)) {
/* 65 */       return false;
/*    */     }
/* 67 */     ExpandedRow that = (ExpandedRow)o;
/* 68 */     return (this.pairs.equals(that.getPairs()) && this.wasReversed == that.wasReversed);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 73 */     return this.pairs.hashCode() ^ Boolean.valueOf(this.wasReversed).hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\ExpandedRow.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */