/*     */ package org.apache.commons.compress.compressors.lz4;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
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
/*     */ public class FramedLZ4CompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*  39 */   private static final byte[] END_MARK = new byte[4];
/*     */ 
/*     */   
/*  42 */   private final byte[] oneByte = new byte[1];
/*     */   
/*     */   private final byte[] blockData;
/*     */   
/*     */   private final OutputStream out;
/*     */   
/*     */   private final Parameters params;
/*     */   private boolean finished;
/*     */   private int currentIndex;
/*  51 */   private final XXHash32 contentHash = new XXHash32();
/*     */   
/*     */   private final XXHash32 blockHash;
/*     */   
/*     */   private final byte[] blockDependencyBuffer;
/*     */   
/*     */   private int collectedBlockDependencyBytes;
/*     */ 
/*     */   
/*     */   public enum BlockSize
/*     */   {
/*     */     private final int size;
/*     */     
/*  64 */     K64(65536, 4),
/*     */     
/*  66 */     K256(262144, 5),
/*     */     
/*  68 */     M1(1048576, 6),
/*     */     
/*  70 */     M4(4194304, 7);
/*     */     private final int index;
/*     */     
/*     */     BlockSize(int size, int index) {
/*  74 */       this.size = size;
/*  75 */       this.index = index;
/*     */     }
/*     */     int getSize() {
/*  78 */       return this.size;
/*     */     }
/*     */     int getIndex() {
/*  81 */       return this.index;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Parameters
/*     */   {
/*     */     private final FramedLZ4CompressorOutputStream.BlockSize blockSize;
/*     */     
/*     */     private final boolean withContentChecksum;
/*     */     
/*     */     private final boolean withBlockChecksum;
/*     */     
/*     */     private final boolean withBlockDependency;
/*     */     
/*     */     private final org.apache.commons.compress.compressors.lz77support.Parameters lz77params;
/*     */     
/*  99 */     public static final Parameters DEFAULT = new Parameters(FramedLZ4CompressorOutputStream.BlockSize.M4, true, false, false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Parameters(FramedLZ4CompressorOutputStream.BlockSize blockSize) {
/* 108 */       this(blockSize, true, false, false);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Parameters(FramedLZ4CompressorOutputStream.BlockSize blockSize, org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
/* 121 */       this(blockSize, true, false, false, lz77params);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Parameters(FramedLZ4CompressorOutputStream.BlockSize blockSize, boolean withContentChecksum, boolean withBlockChecksum, boolean withBlockDependency) {
/* 137 */       this(blockSize, withContentChecksum, withBlockChecksum, withBlockDependency, 
/* 138 */           BlockLZ4CompressorOutputStream.createParameterBuilder().build());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Parameters(FramedLZ4CompressorOutputStream.BlockSize blockSize, boolean withContentChecksum, boolean withBlockChecksum, boolean withBlockDependency, org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
/* 159 */       this.blockSize = blockSize;
/* 160 */       this.withContentChecksum = withContentChecksum;
/* 161 */       this.withBlockChecksum = withBlockChecksum;
/* 162 */       this.withBlockDependency = withBlockDependency;
/* 163 */       this.lz77params = lz77params;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 168 */       return "LZ4 Parameters with BlockSize " + this.blockSize + ", withContentChecksum " + this.withContentChecksum + ", withBlockChecksum " + this.withBlockChecksum + ", withBlockDependency " + this.withBlockDependency;
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
/*     */   public FramedLZ4CompressorOutputStream(OutputStream out) throws IOException {
/* 180 */     this(out, Parameters.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FramedLZ4CompressorOutputStream(OutputStream out, Parameters params) throws IOException {
/* 191 */     this.params = params;
/* 192 */     this.blockData = new byte[params.blockSize.getSize()];
/* 193 */     this.out = out;
/* 194 */     this.blockHash = params.withBlockChecksum ? new XXHash32() : null;
/* 195 */     out.write(FramedLZ4CompressorInputStream.LZ4_SIGNATURE);
/* 196 */     writeFrameDescriptor();
/* 197 */     this.blockDependencyBuffer = params.withBlockDependency ? new byte[65536] : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 204 */     this.oneByte[0] = (byte)(b & 0xFF);
/* 205 */     write(this.oneByte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] data, int off, int len) throws IOException {
/* 210 */     if (this.params.withContentChecksum) {
/* 211 */       this.contentHash.update(data, off, len);
/*     */     }
/* 213 */     int blockDataLength = this.blockData.length;
/* 214 */     if (this.currentIndex + len > blockDataLength) {
/* 215 */       flushBlock();
/* 216 */       while (len > blockDataLength) {
/* 217 */         System.arraycopy(data, off, this.blockData, 0, blockDataLength);
/* 218 */         off += blockDataLength;
/* 219 */         len -= blockDataLength;
/* 220 */         this.currentIndex = blockDataLength;
/* 221 */         flushBlock();
/*     */       } 
/*     */     } 
/* 224 */     System.arraycopy(data, off, this.blockData, this.currentIndex, len);
/* 225 */     this.currentIndex += len;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 231 */       finish();
/*     */     } finally {
/* 233 */       this.out.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 243 */     if (!this.finished) {
/* 244 */       if (this.currentIndex > 0) {
/* 245 */         flushBlock();
/*     */       }
/* 247 */       writeTrailer();
/* 248 */       this.finished = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFrameDescriptor() throws IOException {
/* 253 */     int flags = 64;
/* 254 */     if (!this.params.withBlockDependency) {
/* 255 */       flags |= 0x20;
/*     */     }
/* 257 */     if (this.params.withContentChecksum) {
/* 258 */       flags |= 0x4;
/*     */     }
/* 260 */     if (this.params.withBlockChecksum) {
/* 261 */       flags |= 0x10;
/*     */     }
/* 263 */     this.out.write(flags);
/* 264 */     this.contentHash.update(flags);
/* 265 */     int bd = this.params.blockSize.getIndex() << 4 & 0x70;
/* 266 */     this.out.write(bd);
/* 267 */     this.contentHash.update(bd);
/* 268 */     this.out.write((int)(this.contentHash.getValue() >> 8L & 0xFFL));
/* 269 */     this.contentHash.reset();
/*     */   }
/*     */   
/*     */   private void flushBlock() throws IOException {
/* 273 */     boolean withBlockDependency = this.params.withBlockDependency;
/* 274 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 275 */     try (BlockLZ4CompressorOutputStream o = new BlockLZ4CompressorOutputStream(baos, this.params.lz77params)) {
/* 276 */       if (withBlockDependency) {
/* 277 */         o.prefill(this.blockDependencyBuffer, this.blockDependencyBuffer.length - this.collectedBlockDependencyBytes, this.collectedBlockDependencyBytes);
/*     */       }
/*     */       
/* 280 */       o.write(this.blockData, 0, this.currentIndex);
/*     */     } 
/* 282 */     if (withBlockDependency) {
/* 283 */       appendToBlockDependencyBuffer(this.blockData, 0, this.currentIndex);
/*     */     }
/* 285 */     byte[] b = baos.toByteArray();
/* 286 */     if (b.length > this.currentIndex) {
/* 287 */       ByteUtils.toLittleEndian(this.out, (this.currentIndex | Integer.MIN_VALUE), 4);
/*     */       
/* 289 */       this.out.write(this.blockData, 0, this.currentIndex);
/* 290 */       if (this.params.withBlockChecksum) {
/* 291 */         this.blockHash.update(this.blockData, 0, this.currentIndex);
/*     */       }
/*     */     } else {
/* 294 */       ByteUtils.toLittleEndian(this.out, b.length, 4);
/* 295 */       this.out.write(b);
/* 296 */       if (this.params.withBlockChecksum) {
/* 297 */         this.blockHash.update(b, 0, b.length);
/*     */       }
/*     */     } 
/* 300 */     if (this.params.withBlockChecksum) {
/* 301 */       ByteUtils.toLittleEndian(this.out, this.blockHash.getValue(), 4);
/* 302 */       this.blockHash.reset();
/*     */     } 
/* 304 */     this.currentIndex = 0;
/*     */   }
/*     */   
/*     */   private void writeTrailer() throws IOException {
/* 308 */     this.out.write(END_MARK);
/* 309 */     if (this.params.withContentChecksum) {
/* 310 */       ByteUtils.toLittleEndian(this.out, this.contentHash.getValue(), 4);
/*     */     }
/*     */   }
/*     */   
/*     */   private void appendToBlockDependencyBuffer(byte[] b, int off, int len) {
/* 315 */     len = Math.min(len, this.blockDependencyBuffer.length);
/* 316 */     if (len > 0) {
/* 317 */       int keep = this.blockDependencyBuffer.length - len;
/* 318 */       if (keep > 0)
/*     */       {
/* 320 */         System.arraycopy(this.blockDependencyBuffer, len, this.blockDependencyBuffer, 0, keep);
/*     */       }
/*     */       
/* 323 */       System.arraycopy(b, off, this.blockDependencyBuffer, keep, len);
/* 324 */       this.collectedBlockDependencyBytes = Math.min(this.collectedBlockDependencyBytes + len, this.blockDependencyBuffer.length);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz4\FramedLZ4CompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */