/*     */ package org.apache.commons.compress.compressors.snappy;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.BoundedInputStream;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FramedSnappyCompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   static final long MASK_OFFSET = 2726488792L;
/*     */   private static final int STREAM_IDENTIFIER_TYPE = 255;
/*     */   static final int COMPRESSED_CHUNK_TYPE = 0;
/*     */   private static final int UNCOMPRESSED_CHUNK_TYPE = 1;
/*     */   private static final int PADDING_CHUNK_TYPE = 254;
/*     */   private static final int MIN_UNSKIPPABLE_TYPE = 2;
/*     */   private static final int MAX_UNSKIPPABLE_TYPE = 127;
/*     */   private static final int MAX_SKIPPABLE_TYPE = 253;
/*  58 */   static final byte[] SZ_SIGNATURE = new byte[] { -1, 6, 0, 0, 115, 78, 97, 80, 112, 89 };
/*     */ 
/*     */   
/*     */   private long unreadBytes;
/*     */ 
/*     */   
/*     */   private final CountingInputStream countingStream;
/*     */ 
/*     */   
/*     */   private final PushbackInputStream inputStream;
/*     */ 
/*     */   
/*     */   private final FramedSnappyDialect dialect;
/*     */ 
/*     */   
/*     */   private SnappyCompressorInputStream currentCompressedChunk;
/*     */ 
/*     */   
/*  76 */   private final byte[] oneByte = new byte[1];
/*     */   
/*     */   private boolean endReached;
/*     */   private boolean inUncompressedChunk;
/*     */   private int uncompressedBytesRemaining;
/*  81 */   private long expectedChecksum = -1L;
/*     */   private final int blockSize;
/*  83 */   private final PureJavaCrc32C checksum = new PureJavaCrc32C();
/*     */   
/*  85 */   private final ByteUtils.ByteSupplier supplier = this::readOneByte;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FramedSnappyCompressorInputStream(InputStream in) throws IOException {
/*  95 */     this(in, FramedSnappyDialect.STANDARD);
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
/*     */   public FramedSnappyCompressorInputStream(InputStream in, FramedSnappyDialect dialect) throws IOException {
/* 108 */     this(in, 32768, dialect);
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
/*     */   public FramedSnappyCompressorInputStream(InputStream in, int blockSize, FramedSnappyDialect dialect) throws IOException {
/* 125 */     if (blockSize <= 0) {
/* 126 */       throw new IllegalArgumentException("blockSize must be bigger than 0");
/*     */     }
/* 128 */     this.countingStream = new CountingInputStream(in);
/* 129 */     this.inputStream = new PushbackInputStream((InputStream)this.countingStream, 1);
/* 130 */     this.blockSize = blockSize;
/* 131 */     this.dialect = dialect;
/* 132 */     if (dialect.hasStreamIdentifier()) {
/* 133 */       readStreamIdentifier();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 140 */     return (read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 147 */       if (this.currentCompressedChunk != null) {
/* 148 */         this.currentCompressedChunk.close();
/* 149 */         this.currentCompressedChunk = null;
/*     */       } 
/*     */     } finally {
/* 152 */       this.inputStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 159 */     if (len == 0) {
/* 160 */       return 0;
/*     */     }
/* 162 */     int read = readOnce(b, off, len);
/* 163 */     if (read == -1) {
/* 164 */       readNextBlock();
/* 165 */       if (this.endReached) {
/* 166 */         return -1;
/*     */       }
/* 168 */       read = readOnce(b, off, len);
/*     */     } 
/* 170 */     return read;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 176 */     if (this.inUncompressedChunk) {
/* 177 */       return Math.min(this.uncompressedBytesRemaining, this.inputStream
/* 178 */           .available());
/*     */     }
/* 180 */     if (this.currentCompressedChunk != null) {
/* 181 */       return this.currentCompressedChunk.available();
/*     */     }
/* 183 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 191 */     return this.countingStream.getBytesRead() - this.unreadBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int readOnce(byte[] b, int off, int len) throws IOException {
/* 202 */     int read = -1;
/* 203 */     if (this.inUncompressedChunk) {
/* 204 */       int amount = Math.min(this.uncompressedBytesRemaining, len);
/* 205 */       if (amount == 0) {
/* 206 */         return -1;
/*     */       }
/* 208 */       read = this.inputStream.read(b, off, amount);
/* 209 */       if (read != -1) {
/* 210 */         this.uncompressedBytesRemaining -= read;
/* 211 */         count(read);
/*     */       } 
/* 213 */     } else if (this.currentCompressedChunk != null) {
/* 214 */       long before = this.currentCompressedChunk.getBytesRead();
/* 215 */       read = this.currentCompressedChunk.read(b, off, len);
/* 216 */       if (read == -1) {
/* 217 */         this.currentCompressedChunk.close();
/* 218 */         this.currentCompressedChunk = null;
/*     */       } else {
/* 220 */         count(this.currentCompressedChunk.getBytesRead() - before);
/*     */       } 
/*     */     } 
/* 223 */     if (read > 0) {
/* 224 */       this.checksum.update(b, off, read);
/*     */     }
/* 226 */     return read;
/*     */   }
/*     */   
/*     */   private void readNextBlock() throws IOException {
/* 230 */     verifyLastChecksumAndReset();
/* 231 */     this.inUncompressedChunk = false;
/* 232 */     int type = readOneByte();
/* 233 */     if (type == -1)
/* 234 */     { this.endReached = true; }
/* 235 */     else if (type == 255)
/* 236 */     { this.inputStream.unread(type);
/* 237 */       this.unreadBytes++;
/* 238 */       pushedBackBytes(1L);
/* 239 */       readStreamIdentifier();
/* 240 */       readNextBlock(); }
/* 241 */     else if (type == 254 || (type > 127 && type <= 253))
/*     */     
/* 243 */     { skipBlock();
/* 244 */       readNextBlock(); }
/* 245 */     else { if (type >= 2 && type <= 127) {
/* 246 */         throw new IOException("Unskippable chunk with type " + type + " (hex " + 
/* 247 */             Integer.toHexString(type) + ") detected.");
/*     */       }
/* 249 */       if (type == 1) {
/* 250 */         this.inUncompressedChunk = true;
/* 251 */         this.uncompressedBytesRemaining = readSize() - 4;
/* 252 */         if (this.uncompressedBytesRemaining < 0) {
/* 253 */           throw new IOException("Found illegal chunk with negative size");
/*     */         }
/* 255 */         this.expectedChecksum = unmask(readCrc());
/* 256 */       } else if (type == 0) {
/* 257 */         boolean expectChecksum = this.dialect.usesChecksumWithCompressedChunks();
/* 258 */         long size = readSize() - (expectChecksum ? 4L : 0L);
/* 259 */         if (size < 0L) {
/* 260 */           throw new IOException("Found illegal chunk with negative size");
/*     */         }
/* 262 */         if (expectChecksum) {
/* 263 */           this.expectedChecksum = unmask(readCrc());
/*     */         } else {
/* 265 */           this.expectedChecksum = -1L;
/*     */         } 
/* 267 */         this.currentCompressedChunk = new SnappyCompressorInputStream((InputStream)new BoundedInputStream(this.inputStream, size), this.blockSize);
/*     */ 
/*     */         
/* 270 */         count(this.currentCompressedChunk.getBytesRead());
/*     */       } else {
/*     */         
/* 273 */         throw new IOException("Unknown chunk type " + type + " detected.");
/*     */       }  }
/*     */   
/*     */   }
/*     */   
/*     */   private long readCrc() throws IOException {
/* 279 */     byte[] b = new byte[4];
/* 280 */     int read = IOUtils.readFully(this.inputStream, b);
/* 281 */     count(read);
/* 282 */     if (read != 4) {
/* 283 */       throw new IOException("Premature end of stream");
/*     */     }
/* 285 */     return ByteUtils.fromLittleEndian(b);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static long unmask(long x) {
/* 291 */     x -= 2726488792L;
/* 292 */     x &= 0xFFFFFFFFL;
/* 293 */     return (x >> 17L | x << 15L) & 0xFFFFFFFFL;
/*     */   }
/*     */   
/*     */   private int readSize() throws IOException {
/* 297 */     return (int)ByteUtils.fromLittleEndian(this.supplier, 3);
/*     */   }
/*     */   
/*     */   private void skipBlock() throws IOException {
/* 301 */     int size = readSize();
/* 302 */     if (size < 0) {
/* 303 */       throw new IOException("Found illegal chunk with negative size");
/*     */     }
/* 305 */     long read = IOUtils.skip(this.inputStream, size);
/* 306 */     count(read);
/* 307 */     if (read != size) {
/* 308 */       throw new IOException("Premature end of stream");
/*     */     }
/*     */   }
/*     */   
/*     */   private void readStreamIdentifier() throws IOException {
/* 313 */     byte[] b = new byte[10];
/* 314 */     int read = IOUtils.readFully(this.inputStream, b);
/* 315 */     count(read);
/* 316 */     if (10 != read || !matches(b, 10)) {
/* 317 */       throw new IOException("Not a framed Snappy stream");
/*     */     }
/*     */   }
/*     */   
/*     */   private int readOneByte() throws IOException {
/* 322 */     int b = this.inputStream.read();
/* 323 */     if (b != -1) {
/* 324 */       count(1);
/* 325 */       return b & 0xFF;
/*     */     } 
/* 327 */     return -1;
/*     */   }
/*     */   
/*     */   private void verifyLastChecksumAndReset() throws IOException {
/* 331 */     if (this.expectedChecksum >= 0L && this.expectedChecksum != this.checksum.getValue()) {
/* 332 */       throw new IOException("Checksum verification failed");
/*     */     }
/* 334 */     this.expectedChecksum = -1L;
/* 335 */     this.checksum.reset();
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
/* 349 */     if (length < SZ_SIGNATURE.length) {
/* 350 */       return false;
/*     */     }
/*     */     
/* 353 */     byte[] shortenedSig = signature;
/* 354 */     if (signature.length > SZ_SIGNATURE.length) {
/* 355 */       shortenedSig = new byte[SZ_SIGNATURE.length];
/* 356 */       System.arraycopy(signature, 0, shortenedSig, 0, SZ_SIGNATURE.length);
/*     */     } 
/*     */     
/* 359 */     return Arrays.equals(shortenedSig, SZ_SIGNATURE);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\snappy\FramedSnappyCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */