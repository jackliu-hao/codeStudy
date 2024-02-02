/*     */ package org.apache.commons.compress.compressors.lz4;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.BoundedInputStream;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
/*     */ import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FramedLZ4CompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*  46 */   static final byte[] LZ4_SIGNATURE = new byte[] { 4, 34, 77, 24 };
/*     */ 
/*     */   
/*  49 */   private static final byte[] SKIPPABLE_FRAME_TRAILER = new byte[] { 42, 77, 24 };
/*     */   
/*     */   private static final byte SKIPPABLE_FRAME_PREFIX_BYTE_MASK = 80;
/*     */   
/*     */   static final int VERSION_MASK = 192;
/*     */   
/*     */   static final int SUPPORTED_VERSION = 64;
/*     */   
/*     */   static final int BLOCK_INDEPENDENCE_MASK = 32;
/*     */   
/*     */   static final int BLOCK_CHECKSUM_MASK = 16;
/*     */   static final int CONTENT_SIZE_MASK = 8;
/*     */   static final int CONTENT_CHECKSUM_MASK = 4;
/*     */   static final int BLOCK_MAX_SIZE_MASK = 112;
/*     */   static final int UNCOMPRESSED_FLAG_MASK = -2147483648;
/*  64 */   private final byte[] oneByte = new byte[1];
/*     */   
/*  66 */   private final ByteUtils.ByteSupplier supplier = this::readOneByte;
/*     */   
/*     */   private final CountingInputStream inputStream;
/*     */   
/*     */   private final boolean decompressConcatenated;
/*     */   
/*     */   private boolean expectBlockChecksum;
/*     */   
/*     */   private boolean expectBlockDependency;
/*     */   private boolean expectContentSize;
/*     */   private boolean expectContentChecksum;
/*     */   private InputStream currentBlock;
/*     */   private boolean endReached;
/*     */   private boolean inUncompressed;
/*  80 */   private final XXHash32 contentHash = new XXHash32();
/*     */ 
/*     */   
/*  83 */   private final XXHash32 blockHash = new XXHash32();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] blockDependencyBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FramedLZ4CompressorInputStream(InputStream in) throws IOException {
/*  96 */     this(in, false);
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
/*     */   public FramedLZ4CompressorInputStream(InputStream in, boolean decompressConcatenated) throws IOException {
/* 110 */     this.inputStream = new CountingInputStream(in);
/* 111 */     this.decompressConcatenated = decompressConcatenated;
/* 112 */     init(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 118 */     return (read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 125 */       if (this.currentBlock != null) {
/* 126 */         this.currentBlock.close();
/* 127 */         this.currentBlock = null;
/*     */       } 
/*     */     } finally {
/* 130 */       this.inputStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 137 */     if (len == 0) {
/* 138 */       return 0;
/*     */     }
/* 140 */     if (this.endReached) {
/* 141 */       return -1;
/*     */     }
/* 143 */     int r = readOnce(b, off, len);
/* 144 */     if (r == -1) {
/* 145 */       nextBlock();
/* 146 */       if (!this.endReached) {
/* 147 */         r = readOnce(b, off, len);
/*     */       }
/*     */     } 
/* 150 */     if (r != -1) {
/* 151 */       if (this.expectBlockDependency) {
/* 152 */         appendToBlockDependencyBuffer(b, off, r);
/*     */       }
/* 154 */       if (this.expectContentChecksum) {
/* 155 */         this.contentHash.update(b, off, r);
/*     */       }
/*     */     } 
/* 158 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 166 */     return this.inputStream.getBytesRead();
/*     */   }
/*     */   
/*     */   private void init(boolean firstFrame) throws IOException {
/* 170 */     if (readSignature(firstFrame)) {
/* 171 */       readFrameDescriptor();
/* 172 */       nextBlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean readSignature(boolean firstFrame) throws IOException {
/* 177 */     String garbageMessage = firstFrame ? "Not a LZ4 frame stream" : "LZ4 frame stream followed by garbage";
/* 178 */     byte[] b = new byte[4];
/* 179 */     int read = IOUtils.readFully((InputStream)this.inputStream, b);
/* 180 */     count(read);
/* 181 */     if (0 == read && !firstFrame) {
/*     */       
/* 183 */       this.endReached = true;
/* 184 */       return false;
/*     */     } 
/* 186 */     if (4 != read) {
/* 187 */       throw new IOException(garbageMessage);
/*     */     }
/*     */     
/* 190 */     read = skipSkippableFrame(b);
/* 191 */     if (0 == read && !firstFrame) {
/*     */       
/* 193 */       this.endReached = true;
/* 194 */       return false;
/*     */     } 
/* 196 */     if (4 != read || !matches(b, 4)) {
/* 197 */       throw new IOException(garbageMessage);
/*     */     }
/* 199 */     return true;
/*     */   }
/*     */   
/*     */   private void readFrameDescriptor() throws IOException {
/* 203 */     int flags = readOneByte();
/* 204 */     if (flags == -1) {
/* 205 */       throw new IOException("Premature end of stream while reading frame flags");
/*     */     }
/* 207 */     this.contentHash.update(flags);
/* 208 */     if ((flags & 0xC0) != 64) {
/* 209 */       throw new IOException("Unsupported version " + (flags >> 6));
/*     */     }
/* 211 */     this.expectBlockDependency = ((flags & 0x20) == 0);
/* 212 */     if (this.expectBlockDependency) {
/* 213 */       if (this.blockDependencyBuffer == null) {
/* 214 */         this.blockDependencyBuffer = new byte[65536];
/*     */       }
/*     */     } else {
/* 217 */       this.blockDependencyBuffer = null;
/*     */     } 
/* 219 */     this.expectBlockChecksum = ((flags & 0x10) != 0);
/* 220 */     this.expectContentSize = ((flags & 0x8) != 0);
/* 221 */     this.expectContentChecksum = ((flags & 0x4) != 0);
/* 222 */     int bdByte = readOneByte();
/* 223 */     if (bdByte == -1) {
/* 224 */       throw new IOException("Premature end of stream while reading frame BD byte");
/*     */     }
/* 226 */     this.contentHash.update(bdByte);
/* 227 */     if (this.expectContentSize) {
/* 228 */       byte[] contentSize = new byte[8];
/* 229 */       int skipped = IOUtils.readFully((InputStream)this.inputStream, contentSize);
/* 230 */       count(skipped);
/* 231 */       if (8 != skipped) {
/* 232 */         throw new IOException("Premature end of stream while reading content size");
/*     */       }
/* 234 */       this.contentHash.update(contentSize, 0, contentSize.length);
/*     */     } 
/* 236 */     int headerHash = readOneByte();
/* 237 */     if (headerHash == -1) {
/* 238 */       throw new IOException("Premature end of stream while reading frame header checksum");
/*     */     }
/* 240 */     int expectedHash = (int)(this.contentHash.getValue() >> 8L & 0xFFL);
/* 241 */     this.contentHash.reset();
/* 242 */     if (headerHash != expectedHash)
/* 243 */       throw new IOException("Frame header checksum mismatch"); 
/*     */   }
/*     */   
/*     */   private void nextBlock() throws IOException {
/*     */     ChecksumCalculatingInputStream checksumCalculatingInputStream;
/* 248 */     maybeFinishCurrentBlock();
/* 249 */     long len = ByteUtils.fromLittleEndian(this.supplier, 4);
/* 250 */     boolean uncompressed = ((len & 0xFFFFFFFF80000000L) != 0L);
/* 251 */     int realLen = (int)(len & 0x7FFFFFFFL);
/* 252 */     if (realLen < 0) {
/* 253 */       throw new IOException("Found illegal block with negative size");
/*     */     }
/* 255 */     if (realLen == 0) {
/* 256 */       verifyContentChecksum();
/* 257 */       if (!this.decompressConcatenated) {
/* 258 */         this.endReached = true;
/*     */       } else {
/* 260 */         init(false);
/*     */       } 
/*     */       return;
/*     */     } 
/* 264 */     BoundedInputStream boundedInputStream = new BoundedInputStream((InputStream)this.inputStream, realLen);
/* 265 */     if (this.expectBlockChecksum) {
/* 266 */       checksumCalculatingInputStream = new ChecksumCalculatingInputStream(this.blockHash, (InputStream)boundedInputStream);
/*     */     }
/* 268 */     if (uncompressed) {
/* 269 */       this.inUncompressed = true;
/* 270 */       this.currentBlock = (InputStream)checksumCalculatingInputStream;
/*     */     } else {
/* 272 */       this.inUncompressed = false;
/* 273 */       BlockLZ4CompressorInputStream s = new BlockLZ4CompressorInputStream((InputStream)checksumCalculatingInputStream);
/* 274 */       if (this.expectBlockDependency) {
/* 275 */         s.prefill(this.blockDependencyBuffer);
/*     */       }
/* 277 */       this.currentBlock = (InputStream)s;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void maybeFinishCurrentBlock() throws IOException {
/* 282 */     if (this.currentBlock != null) {
/* 283 */       this.currentBlock.close();
/* 284 */       this.currentBlock = null;
/* 285 */       if (this.expectBlockChecksum) {
/* 286 */         verifyChecksum(this.blockHash, "block");
/* 287 */         this.blockHash.reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void verifyContentChecksum() throws IOException {
/* 293 */     if (this.expectContentChecksum) {
/* 294 */       verifyChecksum(this.contentHash, "content");
/*     */     }
/* 296 */     this.contentHash.reset();
/*     */   }
/*     */   
/*     */   private void verifyChecksum(XXHash32 hash, String kind) throws IOException {
/* 300 */     byte[] checksum = new byte[4];
/* 301 */     int read = IOUtils.readFully((InputStream)this.inputStream, checksum);
/* 302 */     count(read);
/* 303 */     if (4 != read) {
/* 304 */       throw new IOException("Premature end of stream while reading " + kind + " checksum");
/*     */     }
/* 306 */     long expectedHash = hash.getValue();
/* 307 */     if (expectedHash != ByteUtils.fromLittleEndian(checksum)) {
/* 308 */       throw new IOException(kind + " checksum mismatch.");
/*     */     }
/*     */   }
/*     */   
/*     */   private int readOneByte() throws IOException {
/* 313 */     int b = this.inputStream.read();
/* 314 */     if (b != -1) {
/* 315 */       count(1);
/* 316 */       return b & 0xFF;
/*     */     } 
/* 318 */     return -1;
/*     */   }
/*     */   
/*     */   private int readOnce(byte[] b, int off, int len) throws IOException {
/* 322 */     if (this.inUncompressed) {
/* 323 */       int i = this.currentBlock.read(b, off, len);
/* 324 */       count(i);
/* 325 */       return i;
/*     */     } 
/* 327 */     BlockLZ4CompressorInputStream l = (BlockLZ4CompressorInputStream)this.currentBlock;
/* 328 */     long before = l.getBytesRead();
/* 329 */     int cnt = this.currentBlock.read(b, off, len);
/* 330 */     count(l.getBytesRead() - before);
/* 331 */     return cnt;
/*     */   }
/*     */   
/*     */   private static boolean isSkippableFrameSignature(byte[] b) {
/* 335 */     if ((b[0] & 0x50) != 80) {
/* 336 */       return false;
/*     */     }
/* 338 */     for (int i = 1; i < 4; i++) {
/* 339 */       if (b[i] != SKIPPABLE_FRAME_TRAILER[i - 1]) {
/* 340 */         return false;
/*     */       }
/*     */     } 
/* 343 */     return true;
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
/*     */   private int skipSkippableFrame(byte[] b) throws IOException {
/* 355 */     int read = 4;
/* 356 */     while (read == 4 && isSkippableFrameSignature(b)) {
/* 357 */       long len = ByteUtils.fromLittleEndian(this.supplier, 4);
/* 358 */       if (len < 0L) {
/* 359 */         throw new IOException("Found illegal skippable frame with negative size");
/*     */       }
/* 361 */       long skipped = IOUtils.skip((InputStream)this.inputStream, len);
/* 362 */       count(skipped);
/* 363 */       if (len != skipped) {
/* 364 */         throw new IOException("Premature end of stream while skipping frame");
/*     */       }
/* 366 */       read = IOUtils.readFully((InputStream)this.inputStream, b);
/* 367 */       count(read);
/*     */     } 
/* 369 */     return read;
/*     */   }
/*     */   
/*     */   private void appendToBlockDependencyBuffer(byte[] b, int off, int len) {
/* 373 */     len = Math.min(len, this.blockDependencyBuffer.length);
/* 374 */     if (len > 0) {
/* 375 */       int keep = this.blockDependencyBuffer.length - len;
/* 376 */       if (keep > 0)
/*     */       {
/* 378 */         System.arraycopy(this.blockDependencyBuffer, len, this.blockDependencyBuffer, 0, keep);
/*     */       }
/*     */       
/* 381 */       System.arraycopy(b, off, this.blockDependencyBuffer, keep, len);
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 396 */     if (length < LZ4_SIGNATURE.length) {
/* 397 */       return false;
/*     */     }
/*     */     
/* 400 */     byte[] shortenedSig = signature;
/* 401 */     if (signature.length > LZ4_SIGNATURE.length) {
/* 402 */       shortenedSig = new byte[LZ4_SIGNATURE.length];
/* 403 */       System.arraycopy(signature, 0, shortenedSig, 0, LZ4_SIGNATURE.length);
/*     */     } 
/*     */     
/* 406 */     return Arrays.equals(shortenedSig, LZ4_SIGNATURE);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz4\FramedLZ4CompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */