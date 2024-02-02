/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertyKey;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.conf.RuntimeProperty;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.ProtocolEntityReader;
/*    */ import com.mysql.cj.protocol.ResultsetRow;
/*    */ import java.io.IOException;
/*    */ import java.util.Optional;
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
/*    */ public class ResultsetRowReader
/*    */   implements ProtocolEntityReader<ResultsetRow, NativePacketPayload>
/*    */ {
/*    */   protected NativeProtocol protocol;
/*    */   protected PropertySet propertySet;
/*    */   protected RuntimeProperty<Integer> useBufferRowSizeThreshold;
/*    */   
/*    */   public ResultsetRowReader(NativeProtocol prot) {
/* 51 */     this.protocol = prot;
/*    */     
/* 53 */     this.propertySet = this.protocol.getPropertySet();
/* 54 */     this.useBufferRowSizeThreshold = this.propertySet.getMemorySizeProperty(PropertyKey.largeRowSizeThreshold);
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
/*    */ 
/*    */   
/*    */   public ResultsetRow read(ProtocolEntityFactory<ResultsetRow, NativePacketPayload> sf) throws IOException {
/* 69 */     AbstractRowFactory rf = (AbstractRowFactory)sf;
/* 70 */     NativePacketPayload rowPacket = null;
/* 71 */     NativePacketHeader hdr = (NativePacketHeader)this.protocol.getPacketReader().readHeader();
/*    */ 
/*    */ 
/*    */     
/* 75 */     rowPacket = (NativePacketPayload)this.protocol.getPacketReader().readMessage(rf.canReuseRowPacketForBufferRow() ? Optional.<NativePacketPayload>ofNullable(this.protocol.getReusablePacket()) : Optional.empty(), hdr);
/* 76 */     this.protocol.checkErrorMessage(rowPacket);
/*    */     
/* 78 */     rowPacket.setPosition(rowPacket.getPosition() - 1);
/*    */ 
/*    */     
/* 81 */     if ((!this.protocol.getServerSession().isEOFDeprecated() && rowPacket.isEOFPacket()) || (this.protocol
/* 82 */       .getServerSession().isEOFDeprecated() && rowPacket.isResultSetOKPacket())) {
/* 83 */       this.protocol.readServerStatusForResultSets(rowPacket, true);
/* 84 */       return null;
/*    */     } 
/*    */     
/* 87 */     return (ResultsetRow)sf.createFromMessage(rowPacket);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\ResultsetRowReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */