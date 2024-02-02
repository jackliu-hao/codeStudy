/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertyKey;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.Resultset;
/*    */ import com.mysql.cj.protocol.ResultsetRow;
/*    */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*    */ import com.mysql.cj.protocol.a.result.TextBufferRow;
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
/*    */ public class TextRowFactory
/*    */   extends AbstractRowFactory
/*    */   implements ProtocolEntityFactory<ResultsetRow, NativePacketPayload>
/*    */ {
/*    */   public TextRowFactory(NativeProtocol protocol, ColumnDefinition colDefinition, Resultset.Concurrency resultSetConcurrency, boolean canReuseRowPacketForBufferRow) {
/* 46 */     this.columnDefinition = colDefinition;
/* 47 */     this.resultSetConcurrency = resultSetConcurrency;
/* 48 */     this.canReuseRowPacketForBufferRow = canReuseRowPacketForBufferRow;
/* 49 */     this.useBufferRowSizeThreshold = protocol.getPropertySet().getMemorySizeProperty(PropertyKey.largeRowSizeThreshold);
/* 50 */     this.exceptionInterceptor = protocol.getExceptionInterceptor();
/* 51 */     this.valueDecoder = new MysqlTextValueDecoder();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResultsetRow createFromMessage(NativePacketPayload rowPacket) {
/* 60 */     boolean useBufferRow = (this.canReuseRowPacketForBufferRow || this.columnDefinition.hasLargeFields() || rowPacket.getPayloadLength() >= ((Integer)this.useBufferRowSizeThreshold.getValue()).intValue());
/*    */     
/* 62 */     if (this.resultSetConcurrency == Resultset.Concurrency.UPDATABLE || !useBufferRow) {
/* 63 */       byte[][] rowBytes = new byte[(this.columnDefinition.getFields()).length][];
/*    */       
/* 65 */       for (int i = 0; i < (this.columnDefinition.getFields()).length; i++) {
/* 66 */         rowBytes[i] = rowPacket.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
/*    */       }
/*    */       
/* 69 */       return (ResultsetRow)new ByteArrayRow(rowBytes, this.exceptionInterceptor);
/*    */     } 
/*    */     
/* 72 */     return (ResultsetRow)new TextBufferRow(rowPacket, this.columnDefinition, this.exceptionInterceptor, this.valueDecoder);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canReuseRowPacketForBufferRow() {
/* 77 */     return this.canReuseRowPacketForBufferRow;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\TextRowFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */