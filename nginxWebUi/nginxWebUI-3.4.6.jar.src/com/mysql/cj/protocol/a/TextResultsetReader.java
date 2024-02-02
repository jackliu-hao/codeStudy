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
/*     */ public class TextResultsetReader
/*     */   implements ProtocolEntityReader<Resultset, NativePacketPayload>
/*     */ {
/*     */   protected NativeProtocol protocol;
/*     */   
/*     */   public TextResultsetReader(NativeProtocol prot) {
/*  53 */     this.protocol = prot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Resultset read(int maxRows, boolean streamResults, NativePacketPayload resultPacket, ColumnDefinition metadata, ProtocolEntityFactory<Resultset, NativePacketPayload> resultSetFactory) throws IOException {
/*  60 */     Resultset rs = null;
/*     */     
/*  62 */     long columnCount = resultPacket.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/*     */     
/*  64 */     if (columnCount > 0L) {
/*     */       ResultsetRowsStreaming resultsetRowsStreaming;
/*     */ 
/*     */       
/*  68 */       ColumnDefinition cdef = this.protocol.<ColumnDefinition>read(ColumnDefinition.class, new ColumnDefinitionFactory(columnCount, metadata));
/*     */ 
/*     */       
/*  71 */       if (!this.protocol.getServerSession().isEOFDeprecated()) {
/*  72 */         this.protocol.skipPacket();
/*     */       }
/*     */ 
/*     */       
/*  76 */       ResultsetRows rows = null;
/*     */       
/*  78 */       if (!streamResults) {
/*  79 */         TextRowFactory trf = new TextRowFactory(this.protocol, cdef, resultSetFactory.getResultSetConcurrency(), false);
/*  80 */         ArrayList<ResultsetRow> rowList = new ArrayList<>();
/*     */         
/*  82 */         ResultsetRow row = this.protocol.<ResultsetRow>read(ResultsetRow.class, trf);
/*  83 */         while (row != null) {
/*  84 */           if (maxRows == -1 || rowList.size() < maxRows) {
/*  85 */             rowList.add(row);
/*     */           }
/*  87 */           row = this.protocol.<ResultsetRow>read(ResultsetRow.class, trf);
/*     */         } 
/*     */         
/*  90 */         ResultsetRowsStatic resultsetRowsStatic = new ResultsetRowsStatic(rowList, cdef);
/*     */       } else {
/*     */         
/*  93 */         resultsetRowsStreaming = new ResultsetRowsStreaming(this.protocol, cdef, false, resultSetFactory);
/*  94 */         this.protocol.setStreamingData((ResultsetRows)resultsetRowsStreaming);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 100 */       rs = (Resultset)resultSetFactory.createFromProtocolEntity((ProtocolEntity)resultsetRowsStreaming);
/*     */     }
/*     */     else {
/*     */       
/* 104 */       if (columnCount == -1L) {
/* 105 */         String charEncoding = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
/* 106 */         String fileName = resultPacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, 
/* 107 */             this.protocol.getServerSession().getCharsetSettings().doesPlatformDbCharsetMatches() ? charEncoding : null);
/* 108 */         resultPacket = this.protocol.sendFileToServer(fileName);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 116 */       OkPacket ok = this.protocol.<OkPacket>readServerStatusForResultSets(resultPacket, false);
/*     */       
/* 118 */       rs = (Resultset)resultSetFactory.createFromProtocolEntity((ProtocolEntity)ok);
/*     */     } 
/* 120 */     return rs;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\TextResultsetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */