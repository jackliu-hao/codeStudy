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
/*    */ final class AI01320xDecoder
/*    */   extends AI013x0xDecoder
/*    */ {
/*    */   AI01320xDecoder(BitArray information) {
/* 37 */     super(information);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void addWeightCode(StringBuilder buf, int weight) {
/* 42 */     if (weight < 10000) {
/* 43 */       buf.append("(3202)"); return;
/*    */     } 
/* 45 */     buf.append("(3203)");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected int checkWeight(int weight) {
/* 51 */     if (weight < 10000) {
/* 52 */       return weight;
/*    */     }
/* 54 */     return weight - 10000;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI01320xDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */