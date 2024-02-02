/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.Resultset;
/*    */ import com.mysql.cj.protocol.ResultsetRows;
/*    */ import com.mysql.cj.protocol.a.result.NativeResultset;
/*    */ import com.mysql.cj.protocol.a.result.OkPacket;
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
/*    */ public class ResultsetFactory
/*    */   implements ProtocolEntityFactory<Resultset, NativePacketPayload>
/*    */ {
/* 45 */   private Resultset.Type type = Resultset.Type.FORWARD_ONLY;
/* 46 */   private Resultset.Concurrency concurrency = Resultset.Concurrency.READ_ONLY;
/*    */   
/*    */   public ResultsetFactory(Resultset.Type type, Resultset.Concurrency concurrency) {
/* 49 */     this.type = type;
/* 50 */     this.concurrency = concurrency;
/*    */   }
/*    */   
/*    */   public Resultset.Type getResultSetType() {
/* 54 */     return this.type;
/*    */   }
/*    */   
/*    */   public Resultset.Concurrency getResultSetConcurrency() {
/* 58 */     return this.concurrency;
/*    */   }
/*    */ 
/*    */   
/*    */   public Resultset createFromProtocolEntity(ProtocolEntity protocolEntity) {
/* 63 */     if (protocolEntity instanceof OkPacket) {
/* 64 */       return (Resultset)new NativeResultset((OkPacket)protocolEntity);
/*    */     }
/* 66 */     if (protocolEntity instanceof ResultsetRows) {
/* 67 */       return (Resultset)new NativeResultset((ResultsetRows)protocolEntity);
/*    */     }
/*    */     
/* 70 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unknown ProtocolEntity class " + protocolEntity);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\ResultsetFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */