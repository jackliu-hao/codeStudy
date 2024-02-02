/*    */ package com.google.zxing.pdf417;
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
/*    */ public final class PDF417ResultMetadata
/*    */ {
/*    */   private int segmentIndex;
/*    */   private String fileId;
/*    */   private int[] optionalData;
/*    */   private boolean lastSegment;
/*    */   
/*    */   public int getSegmentIndex() {
/* 30 */     return this.segmentIndex;
/*    */   }
/*    */   
/*    */   public void setSegmentIndex(int segmentIndex) {
/* 34 */     this.segmentIndex = segmentIndex;
/*    */   }
/*    */   
/*    */   public String getFileId() {
/* 38 */     return this.fileId;
/*    */   }
/*    */   
/*    */   public void setFileId(String fileId) {
/* 42 */     this.fileId = fileId;
/*    */   }
/*    */   
/*    */   public int[] getOptionalData() {
/* 46 */     return this.optionalData;
/*    */   }
/*    */   
/*    */   public void setOptionalData(int[] optionalData) {
/* 50 */     this.optionalData = optionalData;
/*    */   }
/*    */   
/*    */   public boolean isLastSegment() {
/* 54 */     return this.lastSegment;
/*    */   }
/*    */   
/*    */   public void setLastSegment(boolean lastSegment) {
/* 58 */     this.lastSegment = lastSegment;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\PDF417ResultMetadata.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */