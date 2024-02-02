/*     */ package org.apache.commons.compress.compressors.lz4;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
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
/*     */ public class BlockLZ4CompressorInputStream
/*     */   extends AbstractLZ77CompressorInputStream
/*     */ {
/*     */   static final int WINDOW_SIZE = 65536;
/*     */   static final int SIZE_BITS = 4;
/*     */   static final int BACK_REFERENCE_SIZE_MASK = 15;
/*     */   static final int LITERAL_SIZE_MASK = 240;
/*     */   private int nextBackReferenceSize;
/*  45 */   private State state = State.NO_BLOCK;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockLZ4CompressorInputStream(InputStream is) throws IOException {
/*  56 */     super(is, 65536);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*     */     int litLen;
/*     */     int backReferenceLen;
/*  64 */     if (len == 0) {
/*  65 */       return 0;
/*     */     }
/*  67 */     switch (this.state) {
/*     */       case EOF:
/*  69 */         return -1;
/*     */       case NO_BLOCK:
/*  71 */         readSizes();
/*     */       
/*     */       case IN_LITERAL:
/*  74 */         litLen = readLiteral(b, off, len);
/*  75 */         if (!hasMoreDataInBlock()) {
/*  76 */           this.state = State.LOOKING_FOR_BACK_REFERENCE;
/*     */         }
/*  78 */         return (litLen > 0) ? litLen : read(b, off, len);
/*     */       case LOOKING_FOR_BACK_REFERENCE:
/*  80 */         if (!initializeBackReference()) {
/*  81 */           this.state = State.EOF;
/*  82 */           return -1;
/*     */         } 
/*     */       
/*     */       case IN_BACK_REFERENCE:
/*  86 */         backReferenceLen = readBackReference(b, off, len);
/*  87 */         if (!hasMoreDataInBlock()) {
/*  88 */           this.state = State.NO_BLOCK;
/*     */         }
/*  90 */         return (backReferenceLen > 0) ? backReferenceLen : read(b, off, len);
/*     */     } 
/*  92 */     throw new IOException("Unknown stream state " + this.state);
/*     */   }
/*     */ 
/*     */   
/*     */   private void readSizes() throws IOException {
/*  97 */     int nextBlock = readOneByte();
/*  98 */     if (nextBlock == -1) {
/*  99 */       throw new IOException("Premature end of stream while looking for next block");
/*     */     }
/* 101 */     this.nextBackReferenceSize = nextBlock & 0xF;
/* 102 */     long literalSizePart = ((nextBlock & 0xF0) >> 4);
/* 103 */     if (literalSizePart == 15L) {
/* 104 */       literalSizePart += readSizeBytes();
/*     */     }
/* 106 */     if (literalSizePart < 0L) {
/* 107 */       throw new IOException("Illegal block with a negative literal size found");
/*     */     }
/* 109 */     startLiteral(literalSizePart);
/* 110 */     this.state = State.IN_LITERAL;
/*     */   }
/*     */   
/*     */   private long readSizeBytes() throws IOException {
/* 114 */     long accum = 0L;
/*     */     
/*     */     while (true) {
/* 117 */       int nextByte = readOneByte();
/* 118 */       if (nextByte == -1) {
/* 119 */         throw new IOException("Premature end of stream while parsing length");
/*     */       }
/* 121 */       accum += nextByte;
/* 122 */       if (nextByte != 255) {
/* 123 */         return accum;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean initializeBackReference() throws IOException {
/* 131 */     int backReferenceOffset = 0;
/*     */     try {
/* 133 */       backReferenceOffset = (int)ByteUtils.fromLittleEndian(this.supplier, 2);
/* 134 */     } catch (IOException ex) {
/* 135 */       if (this.nextBackReferenceSize == 0) {
/* 136 */         return false;
/*     */       }
/* 138 */       throw ex;
/*     */     } 
/* 140 */     long backReferenceSize = this.nextBackReferenceSize;
/* 141 */     if (this.nextBackReferenceSize == 15) {
/* 142 */       backReferenceSize += readSizeBytes();
/*     */     }
/*     */     
/* 145 */     if (backReferenceSize < 0L) {
/* 146 */       throw new IOException("Illegal block with a negative match length found");
/*     */     }
/*     */     try {
/* 149 */       startBackReference(backReferenceOffset, backReferenceSize + 4L);
/* 150 */     } catch (IllegalArgumentException ex) {
/* 151 */       throw new IOException("Illegal block with bad offset found", ex);
/*     */     } 
/* 153 */     this.state = State.IN_BACK_REFERENCE;
/* 154 */     return true;
/*     */   }
/*     */   
/*     */   private enum State {
/* 158 */     NO_BLOCK, IN_LITERAL, LOOKING_FOR_BACK_REFERENCE, IN_BACK_REFERENCE, EOF;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz4\BlockLZ4CompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */