/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
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
/*    */ 
/*    */ 
/*    */ public class FetchDoneEntityFactory
/*    */   implements ProtocolEntityFactory<FetchDoneEntity, XMessage>
/*    */ {
/*    */   public FetchDoneEntity createFromMessage(XMessage message) {
/* 41 */     return new FetchDoneEntity();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\FetchDoneEntityFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */