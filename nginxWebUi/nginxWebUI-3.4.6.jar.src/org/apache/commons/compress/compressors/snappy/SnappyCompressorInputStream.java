/*     */ package org.apache.commons.compress.compressors.snappy;
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
/*     */ public class SnappyCompressorInputStream
/*     */   extends AbstractLZ77CompressorInputStream
/*     */ {
/*     */   private static final int TAG_MASK = 3;
/*     */   public static final int DEFAULT_BLOCK_SIZE = 32768;
/*     */   private final int size;
/*     */   private int uncompressedBytesRemaining;
/*  56 */   private State state = State.NO_BLOCK;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean endReached;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SnappyCompressorInputStream(InputStream is) throws IOException {
/*  69 */     this(is, 32768);
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
/*     */   public SnappyCompressorInputStream(InputStream is, int blockSize) throws IOException {
/*  85 */     super(is, blockSize);
/*  86 */     this.uncompressedBytesRemaining = this.size = (int)readSize();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*     */     int litLen;
/*     */     int backReferenceLen;
/*  94 */     if (len == 0) {
/*  95 */       return 0;
/*     */     }
/*  97 */     if (this.endReached) {
/*  98 */       return -1;
/*     */     }
/* 100 */     switch (this.state) {
/*     */       case NO_BLOCK:
/* 102 */         fill();
/* 103 */         return read(b, off, len);
/*     */       case IN_LITERAL:
/* 105 */         litLen = readLiteral(b, off, len);
/* 106 */         if (!hasMoreDataInBlock()) {
/* 107 */           this.state = State.NO_BLOCK;
/*     */         }
/* 109 */         return (litLen > 0) ? litLen : read(b, off, len);
/*     */       case IN_BACK_REFERENCE:
/* 111 */         backReferenceLen = readBackReference(b, off, len);
/* 112 */         if (!hasMoreDataInBlock()) {
/* 113 */           this.state = State.NO_BLOCK;
/*     */         }
/* 115 */         return (backReferenceLen > 0) ? backReferenceLen : read(b, off, len);
/*     */     } 
/* 117 */     throw new IOException("Unknown stream state " + this.state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fill() throws IOException {
/* 125 */     if (this.uncompressedBytesRemaining == 0) {
/* 126 */       this.endReached = true;
/*     */       
/*     */       return;
/*     */     } 
/* 130 */     int b = readOneByte();
/* 131 */     if (b == -1) {
/* 132 */       throw new IOException("Premature end of stream reading block start");
/*     */     }
/* 134 */     int length = 0;
/* 135 */     int offset = 0;
/*     */     
/* 137 */     switch (b & 0x3) {
/*     */ 
/*     */       
/*     */       case 0:
/* 141 */         length = readLiteralLength(b);
/* 142 */         if (length < 0) {
/* 143 */           throw new IOException("Illegal block with a negative literal size found");
/*     */         }
/* 145 */         this.uncompressedBytesRemaining -= length;
/* 146 */         startLiteral(length);
/* 147 */         this.state = State.IN_LITERAL;
/*     */         break;
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
/*     */       case 1:
/* 161 */         length = 4 + (b >> 2 & 0x7);
/* 162 */         if (length < 0) {
/* 163 */           throw new IOException("Illegal block with a negative match length found");
/*     */         }
/* 165 */         this.uncompressedBytesRemaining -= length;
/* 166 */         offset = (b & 0xE0) << 3;
/* 167 */         b = readOneByte();
/* 168 */         if (b == -1) {
/* 169 */           throw new IOException("Premature end of stream reading back-reference length");
/*     */         }
/* 171 */         offset |= b;
/*     */         
/*     */         try {
/* 174 */           startBackReference(offset, length);
/* 175 */         } catch (IllegalArgumentException ex) {
/* 176 */           throw new IOException("Illegal block with bad offset found", ex);
/*     */         } 
/* 178 */         this.state = State.IN_BACK_REFERENCE;
/*     */         break;
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
/*     */       case 2:
/* 191 */         length = (b >> 2) + 1;
/* 192 */         if (length < 0) {
/* 193 */           throw new IOException("Illegal block with a negative match length found");
/*     */         }
/* 195 */         this.uncompressedBytesRemaining -= length;
/*     */         
/* 197 */         offset = (int)ByteUtils.fromLittleEndian(this.supplier, 2);
/*     */         
/*     */         try {
/* 200 */           startBackReference(offset, length);
/* 201 */         } catch (IllegalArgumentException ex) {
/* 202 */           throw new IOException("Illegal block with bad offset found", ex);
/*     */         } 
/* 204 */         this.state = State.IN_BACK_REFERENCE;
/*     */         break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 3:
/* 216 */         length = (b >> 2) + 1;
/* 217 */         if (length < 0) {
/* 218 */           throw new IOException("Illegal block with a negative match length found");
/*     */         }
/* 220 */         this.uncompressedBytesRemaining -= length;
/*     */         
/* 222 */         offset = (int)ByteUtils.fromLittleEndian(this.supplier, 4) & Integer.MAX_VALUE;
/*     */         
/*     */         try {
/* 225 */           startBackReference(offset, length);
/* 226 */         } catch (IllegalArgumentException ex) {
/* 227 */           throw new IOException("Illegal block with bad offset found", ex);
/*     */         } 
/* 229 */         this.state = State.IN_BACK_REFERENCE;
/*     */         break;
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
/*     */   
/*     */   private int readLiteralLength(int b) throws IOException {
/* 249 */     switch (b >> 2)
/*     */     { case 60:
/* 251 */         length = readOneByte();
/* 252 */         if (length == -1) {
/* 253 */           throw new IOException("Premature end of stream reading literal length");
/*     */         }
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
/* 270 */         return length + 1;case 61: length = (int)ByteUtils.fromLittleEndian(this.supplier, 2); return length + 1;case 62: length = (int)ByteUtils.fromLittleEndian(this.supplier, 3); return length + 1;case 63: length = (int)ByteUtils.fromLittleEndian(this.supplier, 4); return length + 1; }  int length = b >> 2; return length + 1;
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
/*     */   private long readSize() throws IOException {
/* 287 */     int index = 0;
/* 288 */     long sz = 0L;
/* 289 */     int b = 0;
/*     */     
/*     */     while (true) {
/* 292 */       b = readOneByte();
/* 293 */       if (b == -1) {
/* 294 */         throw new IOException("Premature end of stream reading size");
/*     */       }
/* 296 */       sz |= ((b & 0x7F) << index++ * 7);
/* 297 */       if (0 == (b & 0x80)) {
/* 298 */         return sz;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 308 */     return this.size;
/*     */   }
/*     */   
/*     */   private enum State {
/* 312 */     NO_BLOCK, IN_LITERAL, IN_BACK_REFERENCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\snappy\SnappyCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */