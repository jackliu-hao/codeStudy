/*     */ package com.google.zxing.datamatrix.encoder;
/*     */ 
/*     */ import com.google.zxing.Dimension;
/*     */ import java.nio.charset.Charset;
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
/*     */ final class EncoderContext
/*     */ {
/*     */   private final String msg;
/*     */   private SymbolShapeHint shape;
/*     */   private Dimension minSize;
/*     */   private Dimension maxSize;
/*     */   private final StringBuilder codewords;
/*     */   int pos;
/*     */   private int newEncoding;
/*     */   private SymbolInfo symbolInfo;
/*     */   private int skipAtEnd;
/*     */   
/*     */   EncoderContext(String msg) {
/*  37 */     byte[] msgBinary = msg.getBytes(Charset.forName("ISO-8859-1"));
/*  38 */     StringBuilder sb = new StringBuilder(msgBinary.length);
/*  39 */     for (int i = 0, c = msgBinary.length; i < c; i++) {
/*     */       char ch;
/*  41 */       if ((ch = (char)(msgBinary[i] & 0xFF)) == '?' && msg.charAt(i) != '?') {
/*  42 */         throw new IllegalArgumentException("Message contains characters outside ISO-8859-1 encoding.");
/*     */       }
/*  44 */       sb.append(ch);
/*     */     } 
/*  46 */     this.msg = sb.toString();
/*  47 */     this.shape = SymbolShapeHint.FORCE_NONE;
/*  48 */     this.codewords = new StringBuilder(msg.length());
/*  49 */     this.newEncoding = -1;
/*     */   }
/*     */   
/*     */   public void setSymbolShape(SymbolShapeHint shape) {
/*  53 */     this.shape = shape;
/*     */   }
/*     */   
/*     */   public void setSizeConstraints(Dimension minSize, Dimension maxSize) {
/*  57 */     this.minSize = minSize;
/*  58 */     this.maxSize = maxSize;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/*  62 */     return this.msg;
/*     */   }
/*     */   
/*     */   public void setSkipAtEnd(int count) {
/*  66 */     this.skipAtEnd = count;
/*     */   }
/*     */   
/*     */   public char getCurrentChar() {
/*  70 */     return this.msg.charAt(this.pos);
/*     */   }
/*     */   
/*     */   public char getCurrent() {
/*  74 */     return this.msg.charAt(this.pos);
/*     */   }
/*     */   
/*     */   public StringBuilder getCodewords() {
/*  78 */     return this.codewords;
/*     */   }
/*     */   
/*     */   public void writeCodewords(String codewords) {
/*  82 */     this.codewords.append(codewords);
/*     */   }
/*     */   
/*     */   public void writeCodeword(char codeword) {
/*  86 */     this.codewords.append(codeword);
/*     */   }
/*     */   
/*     */   public int getCodewordCount() {
/*  90 */     return this.codewords.length();
/*     */   }
/*     */   
/*     */   public int getNewEncoding() {
/*  94 */     return this.newEncoding;
/*     */   }
/*     */   
/*     */   public void signalEncoderChange(int encoding) {
/*  98 */     this.newEncoding = encoding;
/*     */   }
/*     */   
/*     */   public void resetEncoderSignal() {
/* 102 */     this.newEncoding = -1;
/*     */   }
/*     */   
/*     */   public boolean hasMoreCharacters() {
/* 106 */     return (this.pos < getTotalMessageCharCount());
/*     */   }
/*     */   
/*     */   private int getTotalMessageCharCount() {
/* 110 */     return this.msg.length() - this.skipAtEnd;
/*     */   }
/*     */   
/*     */   public int getRemainingCharacters() {
/* 114 */     return getTotalMessageCharCount() - this.pos;
/*     */   }
/*     */   
/*     */   public SymbolInfo getSymbolInfo() {
/* 118 */     return this.symbolInfo;
/*     */   }
/*     */   
/*     */   public void updateSymbolInfo() {
/* 122 */     updateSymbolInfo(getCodewordCount());
/*     */   }
/*     */   
/*     */   public void updateSymbolInfo(int len) {
/* 126 */     if (this.symbolInfo == null || len > this.symbolInfo.getDataCapacity()) {
/* 127 */       this.symbolInfo = SymbolInfo.lookup(len, this.shape, this.minSize, this.maxSize, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void resetSymbolInfo() {
/* 132 */     this.symbolInfo = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\EncoderContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */