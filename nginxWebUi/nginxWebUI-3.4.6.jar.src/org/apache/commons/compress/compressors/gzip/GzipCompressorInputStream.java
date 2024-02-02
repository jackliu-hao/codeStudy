/*     */ package org.apache.commons.compress.compressors.gzip;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GzipCompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private static final int FHCRC = 2;
/*     */   private static final int FEXTRA = 4;
/*     */   private static final int FNAME = 8;
/*     */   private static final int FCOMMENT = 16;
/*     */   private static final int FRESERVED = 224;
/*     */   private final CountingInputStream countingStream;
/*     */   private final InputStream in;
/*     */   private final boolean decompressConcatenated;
/*  97 */   private final byte[] buf = new byte[8192];
/*     */ 
/*     */   
/*     */   private int bufUsed;
/*     */ 
/*     */   
/* 103 */   private Inflater inf = new Inflater(true);
/*     */ 
/*     */   
/* 106 */   private final CRC32 crc = new CRC32();
/*     */ 
/*     */   
/*     */   private boolean endReached;
/*     */ 
/*     */   
/* 112 */   private final byte[] oneByte = new byte[1];
/*     */   
/* 114 */   private final GzipParameters parameters = new GzipParameters();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GzipCompressorInputStream(InputStream inputStream) throws IOException {
/* 131 */     this(inputStream, false);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public GzipCompressorInputStream(InputStream inputStream, boolean decompressConcatenated) throws IOException {
/* 157 */     this.countingStream = new CountingInputStream(inputStream);
/*     */ 
/*     */     
/* 160 */     if (this.countingStream.markSupported()) {
/* 161 */       this.in = (InputStream)this.countingStream;
/*     */     } else {
/* 163 */       this.in = new BufferedInputStream((InputStream)this.countingStream);
/*     */     } 
/*     */     
/* 166 */     this.decompressConcatenated = decompressConcatenated;
/* 167 */     init(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GzipParameters getMetaData() {
/* 177 */     return this.parameters;
/*     */   }
/*     */   
/*     */   private boolean init(boolean isFirstMember) throws IOException {
/* 181 */     assert isFirstMember || this.decompressConcatenated;
/*     */ 
/*     */     
/* 184 */     int magic0 = this.in.read();
/*     */ 
/*     */ 
/*     */     
/* 188 */     if (magic0 == -1 && !isFirstMember) {
/* 189 */       return false;
/*     */     }
/*     */     
/* 192 */     if (magic0 != 31 || this.in.read() != 139) {
/* 193 */       throw new IOException(isFirstMember ? "Input is not in the .gz format" : "Garbage after a valid .gz stream");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 199 */     DataInput inData = new DataInputStream(this.in);
/* 200 */     int method = inData.readUnsignedByte();
/* 201 */     if (method != 8) {
/* 202 */       throw new IOException("Unsupported compression method " + method + " in the .gz header");
/*     */     }
/*     */ 
/*     */     
/* 206 */     int flg = inData.readUnsignedByte();
/* 207 */     if ((flg & 0xE0) != 0) {
/* 208 */       throw new IOException("Reserved flags are set in the .gz header");
/*     */     }
/*     */ 
/*     */     
/* 212 */     this.parameters.setModificationTime(ByteUtils.fromLittleEndian(inData, 4) * 1000L);
/* 213 */     switch (inData.readUnsignedByte()) {
/*     */       case 2:
/* 215 */         this.parameters.setCompressionLevel(9);
/*     */         break;
/*     */       case 4:
/* 218 */         this.parameters.setCompressionLevel(1);
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 224 */     this.parameters.setOperatingSystem(inData.readUnsignedByte());
/*     */ 
/*     */     
/* 227 */     if ((flg & 0x4) != 0) {
/* 228 */       int xlen = inData.readUnsignedByte();
/* 229 */       xlen |= inData.readUnsignedByte() << 8;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 234 */       while (xlen-- > 0) {
/* 235 */         inData.readUnsignedByte();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 240 */     if ((flg & 0x8) != 0) {
/* 241 */       this.parameters.setFilename(new String(readToNull(inData), StandardCharsets.ISO_8859_1));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 246 */     if ((flg & 0x10) != 0) {
/* 247 */       this.parameters.setComment(new String(readToNull(inData), StandardCharsets.ISO_8859_1));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 256 */     if ((flg & 0x2) != 0) {
/* 257 */       inData.readShort();
/*     */     }
/*     */ 
/*     */     
/* 261 */     this.inf.reset();
/* 262 */     this.crc.reset();
/*     */     
/* 264 */     return true;
/*     */   }
/*     */   
/*     */   private static byte[] readToNull(DataInput inData) throws IOException {
/* 268 */     try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
/* 269 */       int b = 0;
/* 270 */       while ((b = inData.readUnsignedByte()) != 0) {
/* 271 */         bos.write(b);
/*     */       }
/* 273 */       return bos.toByteArray();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 279 */     return (read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 289 */     if (len == 0) {
/* 290 */       return 0;
/*     */     }
/* 292 */     if (this.endReached) {
/* 293 */       return -1;
/*     */     }
/*     */     
/* 296 */     int size = 0;
/*     */     
/* 298 */     while (len > 0) {
/* 299 */       int ret; if (this.inf.needsInput()) {
/*     */ 
/*     */         
/* 302 */         this.in.mark(this.buf.length);
/*     */         
/* 304 */         this.bufUsed = this.in.read(this.buf);
/* 305 */         if (this.bufUsed == -1) {
/* 306 */           throw new EOFException();
/*     */         }
/*     */         
/* 309 */         this.inf.setInput(this.buf, 0, this.bufUsed);
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 314 */         ret = this.inf.inflate(b, off, len);
/* 315 */       } catch (DataFormatException e) {
/* 316 */         throw new IOException("Gzip-compressed data is corrupt");
/*     */       } 
/*     */       
/* 319 */       this.crc.update(b, off, ret);
/* 320 */       off += ret;
/* 321 */       len -= ret;
/* 322 */       size += ret;
/* 323 */       count(ret);
/*     */       
/* 325 */       if (this.inf.finished()) {
/*     */ 
/*     */         
/* 328 */         this.in.reset();
/*     */         
/* 330 */         int skipAmount = this.bufUsed - this.inf.getRemaining();
/* 331 */         if (IOUtils.skip(this.in, skipAmount) != skipAmount) {
/* 332 */           throw new IOException();
/*     */         }
/*     */         
/* 335 */         this.bufUsed = 0;
/*     */         
/* 337 */         DataInput inData = new DataInputStream(this.in);
/*     */ 
/*     */         
/* 340 */         long crcStored = ByteUtils.fromLittleEndian(inData, 4);
/*     */         
/* 342 */         if (crcStored != this.crc.getValue()) {
/* 343 */           throw new IOException("Gzip-compressed data is corrupt (CRC32 error)");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 348 */         long isize = ByteUtils.fromLittleEndian(inData, 4);
/*     */         
/* 350 */         if (isize != (this.inf.getBytesWritten() & 0xFFFFFFFFL)) {
/* 351 */           throw new IOException("Gzip-compressed data is corrupt(uncompressed size mismatch)");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 356 */         if (!this.decompressConcatenated || !init(false)) {
/* 357 */           this.inf.end();
/* 358 */           this.inf = null;
/* 359 */           this.endReached = true;
/* 360 */           return (size == 0) ? -1 : size;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 365 */     return size;
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 378 */     return (length >= 2 && signature[0] == 31 && signature[1] == -117);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 388 */     if (this.inf != null) {
/* 389 */       this.inf.end();
/* 390 */       this.inf = null;
/*     */     } 
/*     */     
/* 393 */     if (this.in != System.in) {
/* 394 */       this.in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 403 */     return this.countingStream.getBytesRead();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\gzip\GzipCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */