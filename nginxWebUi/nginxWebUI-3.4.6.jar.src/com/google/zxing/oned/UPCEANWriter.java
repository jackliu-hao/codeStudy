/*    */ package com.google.zxing.oned;
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
/*    */ 
/*    */ 
/*    */ public abstract class UPCEANWriter
/*    */   extends OneDimensionalCodeWriter
/*    */ {
/*    */   public int getDefaultMargin() {
/* 31 */     return UPCEANReader.START_END_PATTERN.length;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEANWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */