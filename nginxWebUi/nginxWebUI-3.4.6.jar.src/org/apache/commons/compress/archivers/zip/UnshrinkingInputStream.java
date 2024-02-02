/*     */ package org.apache.commons.compress.archivers.zip;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class UnshrinkingInputStream
/*     */   extends LZWInputStream
/*     */ {
/*     */   private static final int MAX_CODE_SIZE = 13;
/*     */   private static final int MAX_TABLE_SIZE = 8192;
/*     */   private final boolean[] isUsed;
/*     */   
/*     */   public UnshrinkingInputStream(InputStream inputStream) throws IOException {
/*  44 */     super(inputStream, ByteOrder.LITTLE_ENDIAN);
/*  45 */     setClearCode(9);
/*  46 */     initializeTables(13);
/*  47 */     this.isUsed = new boolean[getPrefixesLength()];
/*  48 */     for (int i = 0; i < 256; i++) {
/*  49 */       this.isUsed[i] = true;
/*     */     }
/*  51 */     setTableSize(getClearCode() + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int addEntry(int previousCode, byte character) throws IOException {
/*  56 */     int tableSize = getTableSize();
/*  57 */     while (tableSize < 8192 && this.isUsed[tableSize]) {
/*  58 */       tableSize++;
/*     */     }
/*  60 */     setTableSize(tableSize);
/*  61 */     int idx = addEntry(previousCode, character, 8192);
/*  62 */     if (idx >= 0) {
/*  63 */       this.isUsed[idx] = true;
/*     */     }
/*  65 */     return idx;
/*     */   }
/*     */   
/*     */   private void partialClear() {
/*  69 */     boolean[] isParent = new boolean[8192]; int i;
/*  70 */     for (i = 0; i < this.isUsed.length; i++) {
/*  71 */       if (this.isUsed[i] && getPrefix(i) != -1) {
/*  72 */         isParent[getPrefix(i)] = true;
/*     */       }
/*     */     } 
/*  75 */     for (i = getClearCode() + 1; i < isParent.length; i++) {
/*  76 */       if (!isParent[i]) {
/*  77 */         this.isUsed[i] = false;
/*  78 */         setPrefix(i, -1);
/*     */       } 
/*     */     } 
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
/*     */   protected int decompressNextSymbol() throws IOException {
/*  97 */     int code = readNextCode();
/*  98 */     if (code < 0) {
/*  99 */       return -1;
/*     */     }
/* 101 */     if (code != getClearCode()) {
/* 102 */       boolean addedUnfinishedEntry = false;
/* 103 */       int effectiveCode = code;
/* 104 */       if (!this.isUsed[code]) {
/* 105 */         effectiveCode = addRepeatOfPreviousCode();
/* 106 */         addedUnfinishedEntry = true;
/*     */       } 
/* 108 */       return expandCodeToOutputStack(effectiveCode, addedUnfinishedEntry);
/*     */     } 
/* 110 */     int subCode = readNextCode();
/* 111 */     if (subCode < 0) {
/* 112 */       throw new IOException("Unexpected EOF;");
/*     */     }
/* 114 */     if (subCode == 1) {
/* 115 */       if (getCodeSize() >= 13) {
/* 116 */         throw new IOException("Attempt to increase code size beyond maximum");
/*     */       }
/* 118 */       incrementCodeSize();
/* 119 */     } else if (subCode == 2) {
/* 120 */       partialClear();
/* 121 */       setTableSize(getClearCode() + 1);
/*     */     } else {
/* 123 */       throw new IOException("Invalid clear code subcode " + subCode);
/*     */     } 
/* 125 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnshrinkingInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */