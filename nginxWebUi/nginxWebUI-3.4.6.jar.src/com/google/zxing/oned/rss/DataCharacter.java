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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DataCharacter
/*    */ {
/*    */   private final int value;
/*    */   private final int checksumPortion;
/*    */   
/*    */   public DataCharacter(int value, int checksumPortion) {
/* 28 */     this.value = value;
/* 29 */     this.checksumPortion = checksumPortion;
/*    */   }
/*    */   
/*    */   public final int getValue() {
/* 33 */     return this.value;
/*    */   }
/*    */   
/*    */   public final int getChecksumPortion() {
/* 37 */     return this.checksumPortion;
/*    */   }
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 42 */     return this.value + "(" + this.checksumPortion + ')';
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean equals(Object o) {
/* 47 */     if (!(o instanceof DataCharacter)) {
/* 48 */       return false;
/*    */     }
/* 50 */     DataCharacter that = (DataCharacter)o;
/* 51 */     return (this.value == that.value && this.checksumPortion == that.checksumPortion);
/*    */   }
/*    */ 
/*    */   
/*    */   public final int hashCode() {
/* 56 */     return this.value ^ this.checksumPortion;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\DataCharacter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */