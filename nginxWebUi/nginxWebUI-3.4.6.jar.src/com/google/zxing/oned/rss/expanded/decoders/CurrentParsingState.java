/*    */ package com.google.zxing.oned.rss.expanded.decoders;
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
/*    */ final class CurrentParsingState
/*    */ {
/*    */   private int position;
/*    */   private State encoding;
/*    */   
/*    */   private enum State
/*    */   {
/* 38 */     NUMERIC,
/* 39 */     ALPHA,
/* 40 */     ISO_IEC_646;
/*    */   }
/*    */   
/*    */   CurrentParsingState() {
/* 44 */     this.position = 0;
/* 45 */     this.encoding = State.NUMERIC;
/*    */   }
/*    */   
/*    */   int getPosition() {
/* 49 */     return this.position;
/*    */   }
/*    */   
/*    */   void setPosition(int position) {
/* 53 */     this.position = position;
/*    */   }
/*    */   
/*    */   void incrementPosition(int delta) {
/* 57 */     this.position += delta;
/*    */   }
/*    */   
/*    */   boolean isAlpha() {
/* 61 */     return (this.encoding == State.ALPHA);
/*    */   }
/*    */   
/*    */   boolean isNumeric() {
/* 65 */     return (this.encoding == State.NUMERIC);
/*    */   }
/*    */   
/*    */   boolean isIsoIec646() {
/* 69 */     return (this.encoding == State.ISO_IEC_646);
/*    */   }
/*    */   
/*    */   void setNumeric() {
/* 73 */     this.encoding = State.NUMERIC;
/*    */   }
/*    */   
/*    */   void setAlpha() {
/* 77 */     this.encoding = State.ALPHA;
/*    */   }
/*    */   
/*    */   void setIsoIec646() {
/* 81 */     this.encoding = State.ISO_IEC_646;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\CurrentParsingState.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */