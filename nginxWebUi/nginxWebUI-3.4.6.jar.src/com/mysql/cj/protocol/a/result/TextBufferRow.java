/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ValueDecoder;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.ValueFactory;
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
/*     */ public class TextBufferRow
/*     */   extends AbstractBufferRow
/*     */ {
/*     */   public TextBufferRow(NativePacketPayload buf, ColumnDefinition cd, ExceptionInterceptor exceptionInterceptor, ValueDecoder valueDecoder) {
/*  50 */     super(exceptionInterceptor);
/*     */     
/*  52 */     this.rowFromServer = buf;
/*  53 */     this.homePosition = this.rowFromServer.getPosition();
/*  54 */     this.valueDecoder = valueDecoder;
/*     */     
/*  56 */     if (cd.getFields() != null) {
/*  57 */       setMetadata(cd);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int findAndSeekToOffset(int index) {
/*  64 */     if (index == 0) {
/*  65 */       this.lastRequestedIndex = 0;
/*  66 */       this.lastRequestedPos = this.homePosition;
/*  67 */       this.rowFromServer.setPosition(this.homePosition);
/*     */       
/*  69 */       return 0;
/*     */     } 
/*     */     
/*  72 */     if (index == this.lastRequestedIndex) {
/*  73 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */       
/*  75 */       return this.lastRequestedPos;
/*     */     } 
/*     */     
/*  78 */     int startingIndex = 0;
/*     */     
/*  80 */     if (index > this.lastRequestedIndex) {
/*  81 */       if (this.lastRequestedIndex >= 0) {
/*  82 */         startingIndex = this.lastRequestedIndex;
/*     */       } else {
/*  84 */         startingIndex = 0;
/*     */       } 
/*     */       
/*  87 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */     } else {
/*  89 */       this.rowFromServer.setPosition(this.homePosition);
/*     */     } 
/*     */     
/*  92 */     for (int i = startingIndex; i < index; i++) {
/*  93 */       this.rowFromServer.skipBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
/*     */     }
/*     */     
/*  96 */     this.lastRequestedIndex = index;
/*  97 */     this.lastRequestedPos = this.rowFromServer.getPosition();
/*     */     
/*  99 */     return this.lastRequestedPos;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes(int index) {
/* 104 */     if (getNull(index)) {
/* 105 */       return null;
/*     */     }
/*     */     
/* 108 */     findAndSeekToOffset(index);
/* 109 */     return this.rowFromServer.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getNull(int columnIndex) {
/* 114 */     findAndSeekToOffset(columnIndex);
/* 115 */     this.wasNull = (this.rowFromServer.readInteger(NativeConstants.IntegerDataType.INT_LENENC) == -1L);
/* 116 */     return this.wasNull;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row setMetadata(ColumnDefinition f) {
/* 121 */     super.setMetadata(f);
/* 122 */     return (Row)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getValue(int columnIndex, ValueFactory<T> vf) {
/* 130 */     findAndSeekToOffset(columnIndex);
/* 131 */     int length = (int)this.rowFromServer.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 132 */     return (T)getValueFromBytes(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), length, vf);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\TextBufferRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */