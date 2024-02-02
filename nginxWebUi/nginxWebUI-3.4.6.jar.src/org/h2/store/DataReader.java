/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
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
/*     */ public class DataReader
/*     */   extends Reader
/*     */ {
/*     */   private final InputStream in;
/*     */   
/*     */   public DataReader(InputStream paramInputStream) {
/*  27 */     this.in = paramInputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte readByte() throws IOException {
/*  37 */     int i = this.in.read();
/*  38 */     if (i < 0) {
/*  39 */       throw new FastEOFException();
/*     */     }
/*  41 */     return (byte)i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readVarInt() throws IOException {
/*  51 */     byte b = readByte();
/*  52 */     if (b >= 0) {
/*  53 */       return b;
/*     */     }
/*  55 */     int i = b & Byte.MAX_VALUE;
/*  56 */     b = readByte();
/*  57 */     if (b >= 0) {
/*  58 */       return i | b << 7;
/*     */     }
/*  60 */     i |= (b & Byte.MAX_VALUE) << 7;
/*  61 */     b = readByte();
/*  62 */     if (b >= 0) {
/*  63 */       return i | b << 14;
/*     */     }
/*  65 */     i |= (b & Byte.MAX_VALUE) << 14;
/*  66 */     b = readByte();
/*  67 */     if (b >= 0) {
/*  68 */       return i | b << 21;
/*     */     }
/*  70 */     return i | (b & Byte.MAX_VALUE) << 21 | readByte() << 28;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char readChar() throws IOException {
/*  79 */     int i = readByte() & 0xFF;
/*  80 */     if (i < 128)
/*  81 */       return (char)i; 
/*  82 */     if (i >= 224) {
/*  83 */       return 
/*     */         
/*  85 */         (char)(((i & 0xF) << 12) + ((readByte() & 0x3F) << 6) + (readByte() & 0x3F));
/*     */     }
/*  87 */     return 
/*  88 */       (char)(((i & 0x1F) << 6) + (readByte() & 0x3F));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException {
/*  99 */     if (paramInt2 == 0) {
/* 100 */       return 0;
/*     */     }
/* 102 */     byte b = 0;
/*     */     try {
/* 104 */       for (; b < paramInt2; b++) {
/* 105 */         paramArrayOfchar[paramInt1 + b] = readChar();
/*     */       }
/* 107 */       return paramInt2;
/* 108 */     } catch (EOFException eOFException) {
/* 109 */       if (b == 0) {
/* 110 */         return -1;
/*     */       }
/* 112 */       return b;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class FastEOFException
/*     */     extends EOFException
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized Throwable fillInStackTrace() {
/* 127 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\DataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */