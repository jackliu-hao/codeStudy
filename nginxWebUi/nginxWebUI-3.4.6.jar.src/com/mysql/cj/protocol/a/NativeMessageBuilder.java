/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.MessageBuilder;
/*     */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.List;
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
/*     */ public class NativeMessageBuilder
/*     */   implements MessageBuilder<NativePacketPayload>
/*     */ {
/*     */   private boolean supportsQueryAttributes = true;
/*     */   
/*     */   public NativeMessageBuilder(boolean supportsQueryAttributes) {
/*  45 */     this.supportsQueryAttributes = supportsQueryAttributes;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload buildSqlStatement(String statement) {
/*  50 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload buildSqlStatement(String statement, List<Object> args) {
/*  55 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload buildClose() {
/*  60 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComQuery(NativePacketPayload sharedPacket, byte[] query) {
/*  64 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(query.length + 1);
/*  65 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 3L);
/*     */     
/*  67 */     if (this.supportsQueryAttributes) {
/*     */ 
/*     */       
/*  70 */       packet.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 0L);
/*  71 */       packet.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
/*     */     } 
/*     */     
/*  74 */     packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, query);
/*  75 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComQuery(NativePacketPayload sharedPacket, String query) {
/*  79 */     return buildComQuery(sharedPacket, StringUtils.getBytes(query));
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComQuery(NativePacketPayload sharedPacket, String query, String encoding) {
/*  83 */     return buildComQuery(sharedPacket, StringUtils.getBytes(query, encoding));
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComInitDb(NativePacketPayload sharedPacket, byte[] dbName) {
/*  87 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(dbName.length + 1);
/*  88 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 2L);
/*  89 */     packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, dbName);
/*  90 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComInitDb(NativePacketPayload sharedPacket, String dbName) {
/*  94 */     return buildComInitDb(sharedPacket, StringUtils.getBytes(dbName));
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComShutdown(NativePacketPayload sharedPacket) {
/*  98 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(1);
/*  99 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 8L);
/* 100 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComSetOption(NativePacketPayload sharedPacket, int val) {
/* 104 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(3);
/* 105 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 27L);
/* 106 */     packet.writeInteger(NativeConstants.IntegerDataType.INT2, val);
/* 107 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComPing(NativePacketPayload sharedPacket) {
/* 111 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(1);
/* 112 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 14L);
/* 113 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComQuit(NativePacketPayload sharedPacket) {
/* 117 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(1);
/* 118 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 1L);
/* 119 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComStmtPrepare(NativePacketPayload sharedPacket, byte[] query) {
/* 123 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(query.length + 1);
/* 124 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 22L);
/* 125 */     packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, query);
/* 126 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComStmtPrepare(NativePacketPayload sharedPacket, String queryString, String characterEncoding) {
/* 130 */     return buildComStmtPrepare(sharedPacket, StringUtils.getBytes(queryString, characterEncoding));
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComStmtClose(NativePacketPayload sharedPacket, long serverStatementId) {
/* 134 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(5);
/* 135 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 25L);
/* 136 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, serverStatementId);
/* 137 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComStmtReset(NativePacketPayload sharedPacket, long serverStatementId) {
/* 141 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(5);
/* 142 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 26L);
/* 143 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, serverStatementId);
/* 144 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComStmtFetch(NativePacketPayload sharedPacket, long serverStatementId, long numRowsToFetch) {
/* 148 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(9);
/* 149 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 28L);
/* 150 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, serverStatementId);
/* 151 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, numRowsToFetch);
/* 152 */     return packet;
/*     */   }
/*     */   
/*     */   public NativePacketPayload buildComStmtSendLongData(NativePacketPayload sharedPacket, long serverStatementId, int parameterIndex, byte[] longData) {
/* 156 */     NativePacketPayload packet = (sharedPacket != null) ? sharedPacket : new NativePacketPayload(9);
/* 157 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 24L);
/* 158 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, serverStatementId);
/* 159 */     packet.writeInteger(NativeConstants.IntegerDataType.INT2, parameterIndex);
/* 160 */     packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, longData);
/* 161 */     return packet;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeMessageBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */