/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.ProtocolEntityReader;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ResultsetRow;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.protocol.a.result.OkPacket;
/*     */ import com.mysql.cj.protocol.a.result.ResultsetRowsCursor;
/*     */ import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
/*     */ import com.mysql.cj.protocol.a.result.ResultsetRowsStreaming;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ public class BinaryResultsetReader
/*     */   implements ProtocolEntityReader<Resultset, NativePacketPayload>
/*     */ {
/*     */   protected NativeProtocol protocol;
/*     */   
/*     */   public BinaryResultsetReader(NativeProtocol prot) {
/*  55 */     this.protocol = prot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Resultset read(int maxRows, boolean streamResults, NativePacketPayload resultPacket, ColumnDefinition metadata, ProtocolEntityFactory<Resultset, NativePacketPayload> resultSetFactory) throws IOException {
/*  62 */     Resultset rs = null;
/*     */     
/*  64 */     long columnCount = resultPacket.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/*     */     
/*  66 */     if (columnCount > 0L) {
/*     */       ResultsetRowsStreaming resultsetRowsStreaming;
/*     */ 
/*     */       
/*  70 */       ColumnDefinition cdef = this.protocol.<ColumnDefinition>read(ColumnDefinition.class, new MergingColumnDefinitionFactory(columnCount, metadata));
/*     */ 
/*     */       
/*  73 */       boolean isCursorPossible = (((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.useCursorFetch).getValue()).booleanValue() && resultSetFactory.getResultSetType() == Resultset.Type.FORWARD_ONLY && resultSetFactory.getFetchSize() > 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  80 */       if (isCursorPossible || !this.protocol.getServerSession().isEOFDeprecated()) {
/*     */         
/*  82 */         NativePacketPayload rowPacket = this.protocol.probeMessage(this.protocol.getReusablePacket());
/*  83 */         this.protocol.checkErrorMessage(rowPacket);
/*  84 */         if (rowPacket.isResultSetOKPacket() || rowPacket.isEOFPacket()) {
/*     */ 
/*     */           
/*  87 */           rowPacket = this.protocol.readMessage(this.protocol.getReusablePacket());
/*  88 */           this.protocol.readServerStatusForResultSets(rowPacket, true);
/*     */         }
/*     */         else {
/*     */           
/*  92 */           isCursorPossible = false;
/*     */         } 
/*     */       } 
/*     */       
/*  96 */       ResultsetRows rows = null;
/*     */       
/*  98 */       if (isCursorPossible && this.protocol.getServerSession().cursorExists()) {
/*  99 */         ResultsetRowsCursor resultsetRowsCursor = new ResultsetRowsCursor(this.protocol, cdef);
/*     */       }
/* 101 */       else if (!streamResults) {
/* 102 */         BinaryRowFactory brf = new BinaryRowFactory(this.protocol, cdef, resultSetFactory.getResultSetConcurrency(), false);
/*     */         
/* 104 */         ArrayList<ResultsetRow> rowList = new ArrayList<>();
/* 105 */         ResultsetRow row = this.protocol.<ResultsetRow>read(ResultsetRow.class, brf);
/* 106 */         while (row != null) {
/* 107 */           if (maxRows == -1 || rowList.size() < maxRows) {
/* 108 */             rowList.add(row);
/*     */           }
/* 110 */           row = this.protocol.<ResultsetRow>read(ResultsetRow.class, brf);
/*     */         } 
/*     */         
/* 113 */         ResultsetRowsStatic resultsetRowsStatic = new ResultsetRowsStatic(rowList, cdef);
/*     */       } else {
/*     */         
/* 116 */         resultsetRowsStreaming = new ResultsetRowsStreaming(this.protocol, cdef, true, resultSetFactory);
/* 117 */         this.protocol.setStreamingData((ResultsetRows)resultsetRowsStreaming);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 123 */       rs = (Resultset)resultSetFactory.createFromProtocolEntity((ProtocolEntity)resultsetRowsStreaming);
/*     */     }
/*     */     else {
/*     */       
/* 127 */       if (columnCount == -1L) {
/* 128 */         String charEncoding = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
/* 129 */         String fileName = resultPacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, 
/* 130 */             this.protocol.getServerSession().getCharsetSettings().doesPlatformDbCharsetMatches() ? null : charEncoding);
/* 131 */         resultPacket = this.protocol.sendFileToServer(fileName);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 139 */       OkPacket ok = this.protocol.<OkPacket>readServerStatusForResultSets(resultPacket, false);
/*     */       
/* 141 */       rs = (Resultset)resultSetFactory.createFromProtocolEntity((ProtocolEntity)ok);
/*     */     } 
/* 143 */     return rs;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\BinaryResultsetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */