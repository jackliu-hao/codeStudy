/*    */ package org.apache.commons.compress.archivers.zip;
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
/*    */ 
/*    */ 
/*    */ public class ScatterStatistics
/*    */ {
/*    */   private final long compressionElapsed;
/*    */   private final long mergingElapsed;
/*    */   
/*    */   ScatterStatistics(long compressionElapsed, long mergingElapsed) {
/* 31 */     this.compressionElapsed = compressionElapsed;
/* 32 */     this.mergingElapsed = mergingElapsed;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getCompressionElapsed() {
/* 40 */     return this.compressionElapsed;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getMergingElapsed() {
/* 48 */     return this.mergingElapsed;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 53 */     return "compressionElapsed=" + this.compressionElapsed + "ms, mergingElapsed=" + this.mergingElapsed + "ms";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ScatterStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */