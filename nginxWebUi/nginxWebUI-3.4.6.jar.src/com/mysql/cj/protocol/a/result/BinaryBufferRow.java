/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ValueDecoder;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.NativeUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BinaryBufferRow
/*     */   extends AbstractBufferRow
/*     */ {
/*  60 */   private int preNullBitmaskHomePosition = 0;
/*     */ 
/*     */   
/*     */   private boolean[] isNull;
/*     */ 
/*     */ 
/*     */   
/*     */   public BinaryBufferRow(NativePacketPayload buf, ColumnDefinition cd, ExceptionInterceptor exceptionInterceptor, ValueDecoder valueDecoder) {
/*  68 */     super(exceptionInterceptor);
/*     */     
/*  70 */     this.rowFromServer = buf;
/*  71 */     this.homePosition = this.rowFromServer.getPosition();
/*  72 */     this.preNullBitmaskHomePosition = this.homePosition;
/*  73 */     this.valueDecoder = valueDecoder;
/*     */     
/*  75 */     if (cd.getFields() != null) {
/*  76 */       setMetadata(cd);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBinaryEncoded() {
/*  82 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int findAndSeekToOffset(int index) {
/*  87 */     if (index == 0) {
/*  88 */       this.lastRequestedIndex = 0;
/*  89 */       this.lastRequestedPos = this.homePosition;
/*  90 */       this.rowFromServer.setPosition(this.homePosition);
/*     */       
/*  92 */       return 0;
/*     */     } 
/*     */     
/*  95 */     if (index == this.lastRequestedIndex) {
/*  96 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */       
/*  98 */       return this.lastRequestedPos;
/*     */     } 
/*     */     
/* 101 */     int startingIndex = 0;
/*     */     
/* 103 */     if (index > this.lastRequestedIndex) {
/* 104 */       if (this.lastRequestedIndex >= 0) {
/* 105 */         startingIndex = this.lastRequestedIndex;
/*     */       } else {
/*     */         
/* 108 */         startingIndex = 0;
/* 109 */         this.lastRequestedPos = this.homePosition;
/*     */       } 
/*     */       
/* 112 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */     } else {
/* 114 */       this.rowFromServer.setPosition(this.homePosition);
/*     */     } 
/*     */     
/* 117 */     for (int i = startingIndex; i < index; i++) {
/* 118 */       if (!this.isNull[i]) {
/*     */ 
/*     */ 
/*     */         
/* 122 */         int type = this.metadata.getFields()[i].getMysqlTypeId();
/*     */         
/* 124 */         if (type != 6) {
/* 125 */           int length = NativeUtils.getBinaryEncodedLength(this.metadata.getFields()[i].getMysqlTypeId());
/* 126 */           if (length == 0)
/* 127 */           { this.rowFromServer.skipBytes(NativeConstants.StringSelfDataType.STRING_LENENC); }
/* 128 */           else { if (length == -1) {
/* 129 */               throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[] { Integer.valueOf(type), Integer.valueOf(i + 1), Integer.valueOf((this.metadata.getFields()).length) }), this.exceptionInterceptor);
/*     */             }
/*     */             
/* 132 */             int curPosition = this.rowFromServer.getPosition();
/* 133 */             this.rowFromServer.setPosition(curPosition + length); }
/*     */         
/*     */         } 
/*     */       } 
/*     */     } 
/* 138 */     this.lastRequestedIndex = index;
/* 139 */     this.lastRequestedPos = this.rowFromServer.getPosition();
/*     */     
/* 141 */     return this.lastRequestedPos;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes(int index) {
/* 146 */     findAndSeekToOffset(index);
/*     */     
/* 148 */     if (getNull(index)) {
/* 149 */       return null;
/*     */     }
/*     */     
/* 152 */     int type = this.metadata.getFields()[index].getMysqlTypeId();
/*     */     
/* 154 */     switch (type) {
/*     */       case 6:
/* 156 */         return null;
/*     */       
/*     */       case 1:
/* 159 */         return this.rowFromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, 1);
/*     */     } 
/*     */     
/* 162 */     int length = NativeUtils.getBinaryEncodedLength(type);
/* 163 */     if (length == 0)
/* 164 */       return this.rowFromServer.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC); 
/* 165 */     if (length == -1) {
/* 166 */       throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[] { Integer.valueOf(type), Integer.valueOf(index + 1), Integer.valueOf((this.metadata.getFields()).length) }), this.exceptionInterceptor);
/*     */     }
/*     */     
/* 169 */     return this.rowFromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getNull(int columnIndex) {
/* 179 */     this.wasNull = this.isNull[columnIndex];
/* 180 */     return this.wasNull;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row setMetadata(ColumnDefinition f) {
/* 185 */     super.setMetadata(f);
/* 186 */     setupIsNullBitmask();
/* 187 */     return (Row)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setupIsNullBitmask() {
/* 196 */     if (this.isNull != null) {
/*     */       return;
/*     */     }
/*     */     
/* 200 */     this.rowFromServer.setPosition(this.preNullBitmaskHomePosition);
/*     */     
/* 202 */     int len = (this.metadata.getFields()).length;
/* 203 */     int nullCount = (len + 9) / 8;
/*     */     
/* 205 */     byte[] nullBitMask = this.rowFromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, nullCount);
/*     */     
/* 207 */     this.homePosition = this.rowFromServer.getPosition();
/*     */     
/* 209 */     this.isNull = new boolean[len];
/*     */     
/* 211 */     int nullMaskPos = 0;
/* 212 */     int bit = 4;
/*     */     
/* 214 */     for (int i = 0; i < len; i++) {
/*     */       
/* 216 */       this.isNull[i] = ((nullBitMask[nullMaskPos] & bit) != 0);
/*     */       
/* 218 */       if (((bit <<= 1) & 0xFF) == 0) {
/* 219 */         bit = 1;
/*     */         
/* 221 */         nullMaskPos++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getValue(int columnIndex, ValueFactory<T> vf) {
/* 231 */     findAndSeekToOffset(columnIndex);
/*     */ 
/*     */     
/* 234 */     int type = this.metadata.getFields()[columnIndex].getMysqlTypeId();
/* 235 */     int length = NativeUtils.getBinaryEncodedLength(type);
/* 236 */     if (!getNull(columnIndex)) {
/* 237 */       if (length == 0) {
/* 238 */         length = (int)this.rowFromServer.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 239 */       } else if (length == -1) {
/* 240 */         throw ExceptionFactory.createException(
/* 241 */             Messages.getString("MysqlIO.97", new Object[] { Integer.valueOf(type), Integer.valueOf(columnIndex + 1), Integer.valueOf((this.metadata.getFields()).length) }), this.exceptionInterceptor);
/*     */       } 
/*     */     }
/* 244 */     return (T)getValueFromBytes(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), length, vf);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBytes(int columnIndex, byte[] value) {
/* 250 */     byte[] backup = null;
/* 251 */     int backupLength = 0;
/*     */ 
/*     */     
/* 254 */     if (columnIndex + 1 < (this.metadata.getFields()).length) {
/* 255 */       findAndSeekToOffset(columnIndex + 1);
/* 256 */       backupLength = this.rowFromServer.getPayloadLength() - this.rowFromServer.getPosition();
/* 257 */       backup = new byte[backupLength];
/* 258 */       System.arraycopy(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), backup, 0, backupLength);
/*     */     } 
/*     */ 
/*     */     
/* 262 */     findAndSeekToOffset(columnIndex);
/* 263 */     this.rowFromServer.setPayloadLength(this.rowFromServer.getPosition());
/*     */     
/* 265 */     if (value == null) {
/* 266 */       this.metadata.getFields()[columnIndex].setMysqlTypeId(6);
/*     */     } else {
/* 268 */       int type = this.metadata.getFields()[columnIndex].getMysqlTypeId();
/*     */       
/* 270 */       int length = NativeUtils.getBinaryEncodedLength(type);
/* 271 */       if (length == 0)
/* 272 */       { this.rowFromServer.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, value); }
/* 273 */       else { if (length == -1) {
/* 274 */           throw ExceptionFactory.createException(
/* 275 */               Messages.getString("MysqlIO.97", new Object[] { Integer.valueOf(type), Integer.valueOf(columnIndex + 1), Integer.valueOf((this.metadata.getFields()).length) }), this.exceptionInterceptor);
/*     */         }
/*     */         
/* 278 */         if (length != value.length) {
/* 279 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Value length doesn't match the expected one for type " + type, this.exceptionInterceptor);
/*     */         }
/*     */ 
/*     */         
/* 283 */         this.rowFromServer.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, value); }
/*     */     
/*     */     } 
/*     */     
/* 287 */     if (backup != null)
/* 288 */       this.rowFromServer.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, backup); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\BinaryBufferRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */