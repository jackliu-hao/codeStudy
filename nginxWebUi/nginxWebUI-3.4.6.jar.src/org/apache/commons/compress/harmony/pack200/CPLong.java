/*    */ package org.apache.commons.compress.harmony.pack200;
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
/*    */ public class CPLong
/*    */   extends CPConstant
/*    */ {
/*    */   private final long theLong;
/*    */   
/*    */   public CPLong(long theLong) {
/* 27 */     this.theLong = theLong;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object obj) {
/* 32 */     if (this.theLong > ((CPLong)obj).theLong) {
/* 33 */       return 1;
/*    */     }
/* 35 */     if (this.theLong == ((CPLong)obj).theLong) {
/* 36 */       return 0;
/*    */     }
/* 38 */     return -1;
/*    */   }
/*    */   
/*    */   public long getLong() {
/* 42 */     return this.theLong;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     return "" + this.theLong;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */