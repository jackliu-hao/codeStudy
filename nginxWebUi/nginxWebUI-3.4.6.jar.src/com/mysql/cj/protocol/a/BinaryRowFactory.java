/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ResultsetRow;
/*     */ import com.mysql.cj.protocol.a.result.BinaryBufferRow;
/*     */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*     */ import com.mysql.cj.result.Field;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BinaryRowFactory
/*     */   extends AbstractRowFactory
/*     */   implements ProtocolEntityFactory<ResultsetRow, NativePacketPayload>
/*     */ {
/*     */   public BinaryRowFactory(NativeProtocol protocol, ColumnDefinition columnDefinition, Resultset.Concurrency resultSetConcurrency, boolean canReuseRowPacketForBufferRow) {
/*  55 */     this.columnDefinition = columnDefinition;
/*  56 */     this.resultSetConcurrency = resultSetConcurrency;
/*  57 */     this.canReuseRowPacketForBufferRow = canReuseRowPacketForBufferRow;
/*  58 */     this.useBufferRowSizeThreshold = protocol.getPropertySet().getMemorySizeProperty(PropertyKey.largeRowSizeThreshold);
/*  59 */     this.exceptionInterceptor = protocol.getExceptionInterceptor();
/*  60 */     this.valueDecoder = new MysqlBinaryValueDecoder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultsetRow createFromMessage(NativePacketPayload rowPacket) {
/*  69 */     boolean useBufferRow = (this.canReuseRowPacketForBufferRow || this.columnDefinition.hasLargeFields() || rowPacket.getPayloadLength() >= ((Integer)this.useBufferRowSizeThreshold.getValue()).intValue());
/*     */ 
/*     */     
/*  72 */     rowPacket.setPosition(rowPacket.getPosition() + 1);
/*     */     
/*  74 */     if (this.resultSetConcurrency == Resultset.Concurrency.UPDATABLE || !useBufferRow) {
/*  75 */       return unpackBinaryResultSetRow(this.columnDefinition.getFields(), rowPacket);
/*     */     }
/*     */     
/*  78 */     return (ResultsetRow)new BinaryBufferRow(rowPacket, this.columnDefinition, this.exceptionInterceptor, this.valueDecoder);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canReuseRowPacketForBufferRow() {
/*  83 */     return this.canReuseRowPacketForBufferRow;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ResultsetRow unpackBinaryResultSetRow(Field[] fields, NativePacketPayload binaryData) {
/*  97 */     int numFields = fields.length;
/*     */     
/*  99 */     byte[][] unpackedRowBytes = new byte[numFields][];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     int nullCount = (numFields + 9) / 8;
/* 106 */     int nullMaskPos = binaryData.getPosition();
/* 107 */     binaryData.setPosition(nullMaskPos + nullCount);
/* 108 */     int bit = 4;
/*     */     
/* 110 */     byte[] buf = binaryData.getByteBuffer();
/* 111 */     for (int i = 0; i < numFields; i++) {
/* 112 */       if ((buf[nullMaskPos] & bit) != 0) {
/* 113 */         unpackedRowBytes[i] = null;
/*     */       } else {
/* 115 */         extractNativeEncodedColumn(binaryData, fields, i, unpackedRowBytes);
/*     */       } 
/*     */       
/* 118 */       if (((bit <<= 1) & 0xFF) == 0) {
/* 119 */         bit = 1;
/*     */         
/* 121 */         nullMaskPos++;
/*     */       } 
/*     */     } 
/*     */     
/* 125 */     return (ResultsetRow)new ByteArrayRow(unpackedRowBytes, this.exceptionInterceptor, new MysqlBinaryValueDecoder());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final void extractNativeEncodedColumn(NativePacketPayload binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) {
/* 141 */     int type = fields[columnIndex].getMysqlTypeId();
/*     */     
/* 143 */     int len = NativeUtils.getBinaryEncodedLength(type);
/*     */     
/* 145 */     if (type != 6)
/*     */     {
/* 147 */       if (len == 0) {
/* 148 */         unpackedRowData[columnIndex] = binaryData.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
/* 149 */       } else if (len > 0) {
/* 150 */         unpackedRowData[columnIndex] = binaryData.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, len);
/*     */       } else {
/* 152 */         throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[] { Integer.valueOf(type), Integer.valueOf(columnIndex), Integer.valueOf(fields.length) }));
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\BinaryRowFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */