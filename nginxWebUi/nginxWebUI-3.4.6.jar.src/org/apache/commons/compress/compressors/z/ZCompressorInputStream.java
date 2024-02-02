/*     */ package org.apache.commons.compress.compressors.z;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteOrder;
/*     */ import org.apache.commons.compress.compressors.lzw.LZWInputStream;
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
/*     */ public class ZCompressorInputStream
/*     */   extends LZWInputStream
/*     */ {
/*     */   private static final int MAGIC_1 = 31;
/*     */   private static final int MAGIC_2 = 157;
/*     */   private static final int BLOCK_MODE_MASK = 128;
/*     */   private static final int MAX_CODE_SIZE_MASK = 31;
/*     */   private final boolean blockMode;
/*     */   private final int maxCodeSize;
/*     */   private long totalCodesRead;
/*     */   
/*     */   public ZCompressorInputStream(InputStream inputStream, int memoryLimitInKb) throws IOException {
/*  43 */     super(inputStream, ByteOrder.LITTLE_ENDIAN);
/*  44 */     int firstByte = (int)this.in.readBits(8);
/*  45 */     int secondByte = (int)this.in.readBits(8);
/*  46 */     int thirdByte = (int)this.in.readBits(8);
/*  47 */     if (firstByte != 31 || secondByte != 157 || thirdByte < 0) {
/*  48 */       throw new IOException("Input is not in .Z format");
/*     */     }
/*  50 */     this.blockMode = ((thirdByte & 0x80) != 0);
/*  51 */     this.maxCodeSize = thirdByte & 0x1F;
/*  52 */     if (this.blockMode) {
/*  53 */       setClearCode(9);
/*     */     }
/*  55 */     initializeTables(this.maxCodeSize, memoryLimitInKb);
/*  56 */     clearEntries();
/*     */   }
/*     */   
/*     */   public ZCompressorInputStream(InputStream inputStream) throws IOException {
/*  60 */     this(inputStream, -1);
/*     */   }
/*     */   
/*     */   private void clearEntries() {
/*  64 */     setTableSize(256 + (this.blockMode ? 1 : 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int readNextCode() throws IOException {
/*  75 */     int code = super.readNextCode();
/*  76 */     if (code >= 0) {
/*  77 */       this.totalCodesRead++;
/*     */     }
/*  79 */     return code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reAlignReading() throws IOException {
/*  87 */     long codeReadsToThrowAway = 8L - this.totalCodesRead % 8L;
/*  88 */     if (codeReadsToThrowAway == 8L) {
/*  89 */       codeReadsToThrowAway = 0L;
/*     */     }
/*  91 */     for (long i = 0L; i < codeReadsToThrowAway; i++) {
/*  92 */       readNextCode();
/*     */     }
/*  94 */     this.in.clearBitCache();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int addEntry(int previousCode, byte character) throws IOException {
/* 105 */     int maxTableSize = 1 << getCodeSize();
/* 106 */     int r = addEntry(previousCode, character, maxTableSize);
/* 107 */     if (getTableSize() == maxTableSize && getCodeSize() < this.maxCodeSize) {
/* 108 */       reAlignReading();
/* 109 */       incrementCodeSize();
/*     */     } 
/* 111 */     return r;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int decompressNextSymbol() throws IOException {
/* 134 */     int code = readNextCode();
/* 135 */     if (code < 0) {
/* 136 */       return -1;
/*     */     }
/* 138 */     if (this.blockMode && code == getClearCode()) {
/* 139 */       clearEntries();
/* 140 */       reAlignReading();
/* 141 */       resetCodeSize();
/* 142 */       resetPreviousCode();
/* 143 */       return 0;
/*     */     } 
/* 145 */     boolean addedUnfinishedEntry = false;
/* 146 */     if (code == getTableSize()) {
/* 147 */       addRepeatOfPreviousCode();
/* 148 */       addedUnfinishedEntry = true;
/* 149 */     } else if (code > getTableSize()) {
/* 150 */       throw new IOException(String.format("Invalid %d bit code 0x%x", new Object[] { Integer.valueOf(getCodeSize()), Integer.valueOf(code) }));
/*     */     } 
/* 152 */     return expandCodeToOutputStack(code, addedUnfinishedEntry);
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 168 */     return (length > 3 && signature[0] == 31 && signature[1] == -99);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\z\ZCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */