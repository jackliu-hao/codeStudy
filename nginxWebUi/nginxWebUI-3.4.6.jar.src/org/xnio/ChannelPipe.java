/*    */ package org.xnio;
/*    */ 
/*    */ import org.xnio.channels.CloseableChannel;
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
/*    */ 
/*    */ public final class ChannelPipe<L extends CloseableChannel, R extends CloseableChannel>
/*    */ {
/*    */   private final L leftSide;
/*    */   private final R rightSide;
/*    */   
/*    */   public ChannelPipe(L leftSide, R rightSide) {
/* 40 */     this.rightSide = rightSide;
/* 41 */     this.leftSide = leftSide;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public L getLeftSide() {
/* 50 */     return this.leftSide;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public R getRightSide() {
/* 59 */     return this.rightSide;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChannelPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */