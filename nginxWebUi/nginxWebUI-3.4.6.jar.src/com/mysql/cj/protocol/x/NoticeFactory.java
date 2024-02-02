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
/*    */ public class NoticeFactory
/*    */   implements ProtocolEntityFactory<Notice, XMessage>
/*    */ {
/*    */   public Notice createFromMessage(XMessage message) {
/* 41 */     return Notice.getInstance(message);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\NoticeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */