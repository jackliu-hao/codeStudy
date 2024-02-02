/*    */ package cn.hutool.socket.nio;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum Operation
/*    */ {
/* 13 */   READ(1),
/*    */   
/* 15 */   WRITE(4),
/*    */   
/* 17 */   CONNECT(8),
/*    */   
/* 19 */   ACCEPT(16);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final int value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Operation(int value) {
/* 33 */     this.value = value;
/*    */   }
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
/*    */   public int getValue() {
/* 46 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\nio\Operation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */