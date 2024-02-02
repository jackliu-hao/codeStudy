/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.x.protobuf.MysqlxResultset;
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
/*    */ public class XProtocolRowFactory
/*    */   implements ProtocolEntityFactory<XProtocolRow, XMessage>
/*    */ {
/*    */   public XProtocolRow createFromMessage(XMessage message) {
/* 42 */     return new XProtocolRow(MysqlxResultset.Row.class.cast(message.getMessage()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XProtocolRowFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */