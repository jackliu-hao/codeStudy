/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.ByteString;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import com.mysql.cj.x.protobuf.MysqlxResultset;
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
/*     */ public class XProtocolRow
/*     */   implements Row
/*     */ {
/*     */   private ColumnDefinition metadata;
/*     */   private MysqlxResultset.Row rowMessage;
/*     */   private boolean wasNull = false;
/*     */   
/*     */   public XProtocolRow(MysqlxResultset.Row rowMessage) {
/*  49 */     this.rowMessage = rowMessage;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row setMetadata(ColumnDefinition columnDefinition) {
/*  54 */     this.metadata = columnDefinition;
/*  55 */     return this;
/*     */   }
/*     */   
/*     */   public <T> T getValue(int columnIndex, ValueFactory<T> vf) {
/*  59 */     if (columnIndex >= (this.metadata.getFields()).length) {
/*  60 */       throw new DataReadException("Invalid column");
/*     */     }
/*  62 */     Field f = this.metadata.getFields()[columnIndex];
/*  63 */     ByteString byteString = this.rowMessage.getField(columnIndex);
/*     */ 
/*     */ 
/*     */     
/*  67 */     if (byteString.size() == 0) {
/*  68 */       T result = (T)vf.createFromNull();
/*  69 */       this.wasNull = (result == null);
/*  70 */       return result;
/*     */     } 
/*     */ 
/*     */     
/*  74 */     switch (f.getMysqlTypeId()) {
/*     */       case 16:
/*  76 */         this.wasNull = false;
/*  77 */         return XProtocolDecoder.instance.decodeBit(byteString.toByteArray(), 0, byteString.size(), vf);
/*     */       
/*     */       case 12:
/*  80 */         this.wasNull = false;
/*     */ 
/*     */         
/*  83 */         return XProtocolDecoder.instance.decodeTimestamp(byteString.toByteArray(), 0, byteString.size(), 6, vf);
/*     */       
/*     */       case 5:
/*  86 */         this.wasNull = false;
/*  87 */         return XProtocolDecoder.instance.decodeDouble(byteString.toByteArray(), 0, byteString.size(), vf);
/*     */       
/*     */       case 247:
/*  90 */         this.wasNull = false;
/*  91 */         return XProtocolDecoder.instance.decodeByteArray(byteString.toByteArray(), 0, byteString.size(), f, vf);
/*     */       
/*     */       case 4:
/*  94 */         this.wasNull = false;
/*  95 */         return XProtocolDecoder.instance.decodeFloat(byteString.toByteArray(), 0, byteString.size(), vf);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 245:
/* 102 */         this.wasNull = false;
/*     */         
/* 104 */         return XProtocolDecoder.instance.decodeByteArray(byteString.toByteArray(), 0, byteString.size(), f, vf);
/*     */ 
/*     */       
/*     */       case 8:
/* 108 */         this.wasNull = false;
/* 109 */         if (f.isUnsigned()) {
/* 110 */           return XProtocolDecoder.instance.decodeUInt8(byteString.toByteArray(), 0, byteString.size(), vf);
/*     */         }
/* 112 */         return XProtocolDecoder.instance.decodeInt8(byteString.toByteArray(), 0, byteString.size(), vf);
/*     */       
/*     */       case 246:
/* 115 */         this.wasNull = false;
/* 116 */         return XProtocolDecoder.instance.decodeDecimal(byteString.toByteArray(), 0, byteString.size(), vf);
/*     */       
/*     */       case 248:
/* 119 */         this.wasNull = false;
/* 120 */         return XProtocolDecoder.instance.decodeSet(byteString.toByteArray(), 0, byteString.size(), f, vf);
/*     */ 
/*     */       
/*     */       case 11:
/* 124 */         this.wasNull = false;
/*     */ 
/*     */         
/* 127 */         return XProtocolDecoder.instance.decodeTime(byteString.toByteArray(), 0, byteString.size(), 6, vf);
/*     */       
/*     */       case 15:
/* 130 */         this.wasNull = false;
/* 131 */         return XProtocolDecoder.instance.decodeByteArray(byteString.toByteArray(), 0, byteString.size(), f, vf);
/*     */       
/*     */       case 253:
/* 134 */         this.wasNull = false;
/* 135 */         return XProtocolDecoder.instance.decodeByteArray(byteString.toByteArray(), 0, byteString.size(), f, vf);
/*     */     } 
/*     */     
/* 138 */     throw new DataReadException("Unknown MySQL type constant: " + f.getMysqlTypeId());
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
/*     */   public boolean getNull(int columnIndex) {
/* 154 */     ByteString byteString = this.rowMessage.getField(columnIndex);
/* 155 */     this.wasNull = (byteString.size() == 0);
/* 156 */     return this.wasNull;
/*     */   }
/*     */   
/*     */   public boolean wasNull() {
/* 160 */     return this.wasNull;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XProtocolRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */