/*    */ package com.google.zxing.oned.rss.expanded.decoders;
/*    */ 
/*    */ import com.google.zxing.common.BitArray;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class AI013103decoder
/*    */   extends AI013x0xDecoder
/*    */ {
/*    */   AI013103decoder(BitArray information) {
/* 37 */     super(information);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void addWeightCode(StringBuilder buf, int weight) {
/* 42 */     buf.append("(3103)");
/*    */   }
/*    */ 
/*    */   
/*    */   protected int checkWeight(int weight) {
/* 47 */     return weight;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI013103decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */