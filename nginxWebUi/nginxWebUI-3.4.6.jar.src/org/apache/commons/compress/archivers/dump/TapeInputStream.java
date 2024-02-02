/*     */ package org.apache.commons.compress.archivers.dump;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TapeInputStream
/*     */   extends FilterInputStream
/*     */ {
/*  38 */   private byte[] blockBuffer = new byte[1024];
/*  39 */   private int currBlkIdx = -1;
/*  40 */   private int blockSize = 1024;
/*     */   private static final int RECORD_SIZE = 1024;
/*  42 */   private int readOffset = 1024;
/*     */   
/*     */   private boolean isCompressed;
/*     */   
/*     */   private long bytesRead;
/*     */ 
/*     */   
/*     */   public TapeInputStream(InputStream in) {
/*  50 */     super(in);
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
/*     */   public void resetBlockSize(int recsPerBlock, boolean isCompressed) throws IOException {
/*  71 */     this.isCompressed = isCompressed;
/*     */     
/*  73 */     if (recsPerBlock < 1) {
/*  74 */       throw new IOException("Block with " + recsPerBlock + " records found, must be at least 1");
/*     */     }
/*     */     
/*  77 */     this.blockSize = 1024 * recsPerBlock;
/*     */ 
/*     */     
/*  80 */     byte[] oldBuffer = this.blockBuffer;
/*     */ 
/*     */     
/*  83 */     this.blockBuffer = new byte[this.blockSize];
/*  84 */     System.arraycopy(oldBuffer, 0, this.blockBuffer, 0, 1024);
/*  85 */     readFully(this.blockBuffer, 1024, this.blockSize - 1024);
/*     */     
/*  87 */     this.currBlkIdx = 0;
/*  88 */     this.readOffset = 1024;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  96 */     if (this.readOffset < this.blockSize) {
/*  97 */       return this.blockSize - this.readOffset;
/*     */     }
/*     */     
/* 100 */     return this.in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 108 */     throw new IllegalArgumentException("All reads must be multiple of record size (1024 bytes.");
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
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 123 */     if (len == 0) {
/* 124 */       return 0;
/*     */     }
/* 126 */     if (len % 1024 != 0) {
/* 127 */       throw new IllegalArgumentException("All reads must be multiple of record size (1024 bytes.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 132 */     int bytes = 0;
/*     */     
/* 134 */     while (bytes < len) {
/*     */ 
/*     */ 
/*     */       
/* 138 */       if (this.readOffset == this.blockSize) {
/*     */         try {
/* 140 */           readBlock(true);
/* 141 */         } catch (ShortFileException sfe) {
/* 142 */           return -1;
/*     */         } 
/*     */       }
/*     */       
/* 146 */       int n = 0;
/*     */       
/* 148 */       if (this.readOffset + len - bytes <= this.blockSize) {
/*     */         
/* 150 */         n = len - bytes;
/*     */       } else {
/*     */         
/* 153 */         n = this.blockSize - this.readOffset;
/*     */       } 
/*     */ 
/*     */       
/* 157 */       System.arraycopy(this.blockBuffer, this.readOffset, b, off, n);
/* 158 */       this.readOffset += n;
/* 159 */       bytes += n;
/* 160 */       off += n;
/*     */     } 
/*     */     
/* 163 */     return bytes;
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
/*     */   public long skip(long len) throws IOException {
/* 176 */     if (len % 1024L != 0L) {
/* 177 */       throw new IllegalArgumentException("All reads must be multiple of record size (1024 bytes.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 182 */     long bytes = 0L;
/*     */     
/* 184 */     while (bytes < len) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 189 */       if (this.readOffset == this.blockSize) {
/*     */         try {
/* 191 */           readBlock((len - bytes < this.blockSize));
/* 192 */         } catch (ShortFileException sfe) {
/* 193 */           return -1L;
/*     */         } 
/*     */       }
/*     */       
/* 197 */       long n = 0L;
/*     */       
/* 199 */       if (this.readOffset + len - bytes <= this.blockSize) {
/*     */         
/* 201 */         n = len - bytes;
/*     */       } else {
/*     */         
/* 204 */         n = this.blockSize - this.readOffset;
/*     */       } 
/*     */ 
/*     */       
/* 208 */       this.readOffset = (int)(this.readOffset + n);
/* 209 */       bytes += n;
/*     */     } 
/*     */     
/* 212 */     return bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 222 */     if (this.in != null && this.in != System.in) {
/* 223 */       this.in.close();
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
/*     */   public byte[] peek() throws IOException {
/* 237 */     if (this.readOffset == this.blockSize) {
/*     */       try {
/* 239 */         readBlock(true);
/* 240 */       } catch (ShortFileException sfe) {
/* 241 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 246 */     byte[] b = new byte[1024];
/* 247 */     System.arraycopy(this.blockBuffer, this.readOffset, b, 0, b.length);
/*     */     
/* 249 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readRecord() throws IOException {
/* 259 */     byte[] result = new byte[1024];
/*     */ 
/*     */ 
/*     */     
/* 263 */     if (-1 == read(result, 0, result.length)) {
/* 264 */       throw new ShortFileException();
/*     */     }
/*     */     
/* 267 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readBlock(boolean decompress) throws IOException {
/* 277 */     if (this.in == null) {
/* 278 */       throw new IOException("Input buffer is closed");
/*     */     }
/*     */     
/* 281 */     if (!this.isCompressed || this.currBlkIdx == -1) {
/*     */       
/* 283 */       readFully(this.blockBuffer, 0, this.blockSize);
/* 284 */       this.bytesRead += this.blockSize;
/*     */     } else {
/* 286 */       readFully(this.blockBuffer, 0, 4);
/* 287 */       this.bytesRead += 4L;
/*     */       
/* 289 */       int h = DumpArchiveUtil.convert32(this.blockBuffer, 0);
/* 290 */       boolean compressed = ((h & 0x1) == 1);
/*     */       
/* 292 */       if (!compressed) {
/*     */         
/* 294 */         readFully(this.blockBuffer, 0, this.blockSize);
/* 295 */         this.bytesRead += this.blockSize;
/*     */       } else {
/*     */         
/* 298 */         int flags = h >> 1 & 0x7;
/* 299 */         int length = h >> 4 & 0xFFFFFFF;
/* 300 */         byte[] compBuffer = readRange(length);
/* 301 */         this.bytesRead += length;
/*     */         
/* 303 */         if (!decompress) {
/*     */           
/* 305 */           Arrays.fill(this.blockBuffer, (byte)0);
/*     */         } else {
/* 307 */           Inflater inflator; switch (DumpArchiveConstants.COMPRESSION_TYPE.find(flags & 0x3)) {
/*     */ 
/*     */             
/*     */             case ZLIB:
/* 311 */               inflator = new Inflater();
/*     */               try {
/* 313 */                 inflator.setInput(compBuffer, 0, compBuffer.length);
/* 314 */                 length = inflator.inflate(this.blockBuffer);
/*     */                 
/* 316 */                 if (length != this.blockSize) {
/* 317 */                   throw new ShortFileException();
/*     */                 }
/* 319 */               } catch (DataFormatException e) {
/* 320 */                 throw new DumpArchiveException("Bad data", e);
/*     */               } finally {
/* 322 */                 inflator.end();
/*     */               } 
/*     */               break;
/*     */ 
/*     */             
/*     */             case BZLIB:
/* 328 */               throw new UnsupportedCompressionAlgorithmException("BZLIB2");
/*     */ 
/*     */             
/*     */             case LZO:
/* 332 */               throw new UnsupportedCompressionAlgorithmException("LZO");
/*     */ 
/*     */             
/*     */             default:
/* 336 */               throw new UnsupportedCompressionAlgorithmException();
/*     */           } 
/*     */         
/*     */         } 
/*     */       } 
/*     */     } 
/* 342 */     this.currBlkIdx++;
/* 343 */     this.readOffset = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readFully(byte[] b, int off, int len) throws IOException {
/* 351 */     int count = IOUtils.readFully(this.in, b, off, len);
/* 352 */     if (count < len) {
/* 353 */       throw new ShortFileException();
/*     */     }
/*     */   }
/*     */   
/*     */   private byte[] readRange(int len) throws IOException {
/* 358 */     byte[] ret = IOUtils.readRange(this.in, len);
/* 359 */     if (ret.length < len) {
/* 360 */       throw new ShortFileException();
/*     */     }
/* 362 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 369 */     return this.bytesRead;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\TapeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */