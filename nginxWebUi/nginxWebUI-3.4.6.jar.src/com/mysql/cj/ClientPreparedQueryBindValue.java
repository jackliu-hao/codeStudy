/*     */ package com.mysql.cj;
/*     */ 
/*     */ import java.io.InputStream;
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
/*     */ public class ClientPreparedQueryBindValue
/*     */   implements BindValue
/*     */ {
/*     */   protected boolean isNull;
/*     */   protected boolean isStream = false;
/*  41 */   protected MysqlType parameterType = MysqlType.NULL;
/*     */ 
/*     */   
/*     */   public Object value;
/*     */ 
/*     */   
/*     */   public Object origValue;
/*     */ 
/*     */   
/*     */   protected long streamLength;
/*     */ 
/*     */   
/*     */   protected boolean isSet = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientPreparedQueryBindValue clone() {
/*  58 */     return new ClientPreparedQueryBindValue(this);
/*     */   }
/*     */   
/*     */   protected ClientPreparedQueryBindValue(ClientPreparedQueryBindValue copyMe) {
/*  62 */     this.isNull = copyMe.isNull;
/*  63 */     this.isStream = copyMe.isStream;
/*  64 */     this.parameterType = copyMe.parameterType;
/*  65 */     if (copyMe.value != null && copyMe.value instanceof byte[]) {
/*  66 */       this.value = new byte[((byte[])copyMe.value).length];
/*  67 */       System.arraycopy(copyMe.value, 0, this.value, 0, ((byte[])copyMe.value).length);
/*     */     } else {
/*  69 */       this.value = copyMe.value;
/*     */     } 
/*  71 */     this.streamLength = copyMe.streamLength;
/*  72 */     this.isSet = copyMe.isSet;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  76 */     this.isNull = false;
/*  77 */     this.isStream = false;
/*  78 */     this.parameterType = MysqlType.NULL;
/*  79 */     this.value = null;
/*  80 */     this.origValue = null;
/*  81 */     this.streamLength = 0L;
/*  82 */     this.isSet = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNull() {
/*  87 */     return this.isNull;
/*     */   }
/*     */   
/*     */   public void setNull(boolean isNull) {
/*  91 */     this.isNull = isNull;
/*  92 */     if (isNull) {
/*  93 */       this.parameterType = MysqlType.NULL;
/*     */     }
/*  95 */     this.isSet = true;
/*     */   }
/*     */   
/*     */   public boolean isStream() {
/*  99 */     return this.isStream;
/*     */   }
/*     */   
/*     */   public void setIsStream(boolean isStream) {
/* 103 */     this.isStream = isStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public MysqlType getMysqlType() {
/* 108 */     return this.parameterType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMysqlType(MysqlType type) {
/* 113 */     this.parameterType = type;
/*     */   }
/*     */   
/*     */   public byte[] getByteValue() {
/* 117 */     if (this.value instanceof byte[]) {
/* 118 */       return (byte[])this.value;
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */   
/*     */   public void setByteValue(byte[] parameterValue) {
/* 124 */     this.isNull = false;
/* 125 */     this.isStream = false;
/* 126 */     this.value = parameterValue;
/* 127 */     this.streamLength = 0L;
/* 128 */     this.isSet = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOrigByteValue(byte[] origParamValue) {
/* 133 */     this.origValue = origParamValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getOrigByteValue() {
/* 138 */     return (byte[])this.origValue;
/*     */   }
/*     */   
/*     */   public InputStream getStreamValue() {
/* 142 */     if (this.value instanceof InputStream) {
/* 143 */       return (InputStream)this.value;
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */   
/*     */   public void setStreamValue(InputStream parameterStream, long streamLength) {
/* 149 */     this.value = parameterStream;
/* 150 */     this.streamLength = streamLength;
/* 151 */     this.isSet = true;
/*     */   }
/*     */   
/*     */   public long getStreamLength() {
/* 155 */     return this.streamLength;
/*     */   }
/*     */   
/*     */   public boolean isSet() {
/* 159 */     return this.isSet;
/*     */   }
/*     */   
/*     */   public ClientPreparedQueryBindValue() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ClientPreparedQueryBindValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */