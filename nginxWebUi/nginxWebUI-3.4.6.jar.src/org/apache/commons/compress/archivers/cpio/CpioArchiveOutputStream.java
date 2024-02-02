/*     */ package org.apache.commons.compress.archivers.cpio;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.ArchiveUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CpioArchiveOutputStream
/*     */   extends ArchiveOutputStream
/*     */   implements CpioConstants
/*     */ {
/*     */   private CpioArchiveEntry entry;
/*     */   private boolean closed;
/*     */   private boolean finished;
/*     */   private final short entryFormat;
/*  82 */   private final HashMap<String, CpioArchiveEntry> names = new HashMap<>();
/*     */ 
/*     */   
/*     */   private long crc;
/*     */   
/*     */   private long written;
/*     */   
/*     */   private final OutputStream out;
/*     */   
/*     */   private final int blockSize;
/*     */   
/*  93 */   private long nextArtificalDeviceAndInode = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ZipEncoding zipEncoding;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String encoding;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveOutputStream(OutputStream out, short format) {
/* 114 */     this(out, format, 512, "US-ASCII");
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
/*     */   public CpioArchiveOutputStream(OutputStream out, short format, int blockSize) {
/* 132 */     this(out, format, blockSize, "US-ASCII");
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
/*     */   public CpioArchiveOutputStream(OutputStream out, short format, int blockSize, String encoding) {
/* 153 */     this.out = out;
/* 154 */     switch (format) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 4:
/*     */       case 8:
/*     */         break;
/*     */       default:
/* 161 */         throw new IllegalArgumentException("Unknown format: " + format);
/*     */     } 
/*     */     
/* 164 */     this.entryFormat = format;
/* 165 */     this.blockSize = blockSize;
/* 166 */     this.encoding = encoding;
/* 167 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveOutputStream(OutputStream out) {
/* 178 */     this(out, (short)1);
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
/*     */   public CpioArchiveOutputStream(OutputStream out, String encoding) {
/* 193 */     this(out, (short)1, 512, encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureOpen() throws IOException {
/* 203 */     if (this.closed) {
/* 204 */       throw new IOException("Stream closed");
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
/*     */   
/*     */   public void putArchiveEntry(ArchiveEntry entry) throws IOException {
/* 224 */     if (this.finished) {
/* 225 */       throw new IOException("Stream has already been finished");
/*     */     }
/*     */     
/* 228 */     CpioArchiveEntry e = (CpioArchiveEntry)entry;
/* 229 */     ensureOpen();
/* 230 */     if (this.entry != null) {
/* 231 */       closeArchiveEntry();
/*     */     }
/* 233 */     if (e.getTime() == -1L) {
/* 234 */       e.setTime(System.currentTimeMillis() / 1000L);
/*     */     }
/*     */     
/* 237 */     short format = e.getFormat();
/* 238 */     if (format != this.entryFormat) {
/* 239 */       throw new IOException("Header format: " + format + " does not match existing format: " + this.entryFormat);
/*     */     }
/*     */     
/* 242 */     if (this.names.put(e.getName(), e) != null) {
/* 243 */       throw new IOException("Duplicate entry: " + e.getName());
/*     */     }
/*     */     
/* 246 */     writeHeader(e);
/* 247 */     this.entry = e;
/* 248 */     this.written = 0L;
/*     */   }
/*     */   private void writeHeader(CpioArchiveEntry e) throws IOException {
/*     */     boolean swapHalfWord;
/* 252 */     switch (e.getFormat()) {
/*     */       case 1:
/* 254 */         this.out.write(ArchiveUtils.toAsciiBytes("070701"));
/* 255 */         count(6);
/* 256 */         writeNewEntry(e);
/*     */         return;
/*     */       case 2:
/* 259 */         this.out.write(ArchiveUtils.toAsciiBytes("070702"));
/* 260 */         count(6);
/* 261 */         writeNewEntry(e);
/*     */         return;
/*     */       case 4:
/* 264 */         this.out.write(ArchiveUtils.toAsciiBytes("070707"));
/* 265 */         count(6);
/* 266 */         writeOldAsciiEntry(e);
/*     */         return;
/*     */       case 8:
/* 269 */         swapHalfWord = true;
/* 270 */         writeBinaryLong(29127L, 2, true);
/* 271 */         writeOldBinaryEntry(e, true);
/*     */         return;
/*     */     } 
/* 274 */     throw new IOException("Unknown format " + e.getFormat());
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeNewEntry(CpioArchiveEntry entry) throws IOException {
/* 279 */     long inode = entry.getInode();
/* 280 */     long devMin = entry.getDeviceMin();
/*     */     
/* 282 */     inode = devMin = 0L;
/*     */ 
/*     */     
/* 285 */     inode = this.nextArtificalDeviceAndInode & 0xFFFFFFFFFFFFFFFFL;
/* 286 */     devMin = this.nextArtificalDeviceAndInode++ >> 32L & 0xFFFFFFFFFFFFFFFFL;
/*     */     
/* 288 */     this
/* 289 */       .nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 4294967296L * devMin) + 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 294 */     writeAsciiLong(inode, 8, 16);
/* 295 */     writeAsciiLong(entry.getMode(), 8, 16);
/* 296 */     writeAsciiLong(entry.getUID(), 8, 16);
/* 297 */     writeAsciiLong(entry.getGID(), 8, 16);
/* 298 */     writeAsciiLong(entry.getNumberOfLinks(), 8, 16);
/* 299 */     writeAsciiLong(entry.getTime(), 8, 16);
/* 300 */     writeAsciiLong(entry.getSize(), 8, 16);
/* 301 */     writeAsciiLong(entry.getDeviceMaj(), 8, 16);
/* 302 */     writeAsciiLong(devMin, 8, 16);
/* 303 */     writeAsciiLong(entry.getRemoteDeviceMaj(), 8, 16);
/* 304 */     writeAsciiLong(entry.getRemoteDeviceMin(), 8, 16);
/* 305 */     byte[] name = encode(entry.getName());
/* 306 */     writeAsciiLong(name.length + 1L, 8, 16);
/* 307 */     writeAsciiLong(entry.getChksum(), 8, 16);
/* 308 */     writeCString(name);
/* 309 */     pad(entry.getHeaderPadCount(name.length));
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeOldAsciiEntry(CpioArchiveEntry entry) throws IOException {
/* 314 */     long inode = entry.getInode();
/* 315 */     long device = entry.getDevice();
/*     */     
/* 317 */     inode = device = 0L;
/*     */ 
/*     */     
/* 320 */     inode = this.nextArtificalDeviceAndInode & 0x3FFFFL;
/* 321 */     device = this.nextArtificalDeviceAndInode++ >> 18L & 0x3FFFFL;
/*     */     
/* 323 */     this
/* 324 */       .nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 262144L * device) + 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 329 */     writeAsciiLong(device, 6, 8);
/* 330 */     writeAsciiLong(inode, 6, 8);
/* 331 */     writeAsciiLong(entry.getMode(), 6, 8);
/* 332 */     writeAsciiLong(entry.getUID(), 6, 8);
/* 333 */     writeAsciiLong(entry.getGID(), 6, 8);
/* 334 */     writeAsciiLong(entry.getNumberOfLinks(), 6, 8);
/* 335 */     writeAsciiLong(entry.getRemoteDevice(), 6, 8);
/* 336 */     writeAsciiLong(entry.getTime(), 11, 8);
/* 337 */     byte[] name = encode(entry.getName());
/* 338 */     writeAsciiLong(name.length + 1L, 6, 8);
/* 339 */     writeAsciiLong(entry.getSize(), 11, 8);
/* 340 */     writeCString(name);
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeOldBinaryEntry(CpioArchiveEntry entry, boolean swapHalfWord) throws IOException {
/* 345 */     long inode = entry.getInode();
/* 346 */     long device = entry.getDevice();
/*     */     
/* 348 */     inode = device = 0L;
/*     */ 
/*     */     
/* 351 */     inode = this.nextArtificalDeviceAndInode & 0xFFFFL;
/* 352 */     device = this.nextArtificalDeviceAndInode++ >> 16L & 0xFFFFL;
/*     */     
/* 354 */     this
/* 355 */       .nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 65536L * device) + 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 360 */     writeBinaryLong(device, 2, swapHalfWord);
/* 361 */     writeBinaryLong(inode, 2, swapHalfWord);
/* 362 */     writeBinaryLong(entry.getMode(), 2, swapHalfWord);
/* 363 */     writeBinaryLong(entry.getUID(), 2, swapHalfWord);
/* 364 */     writeBinaryLong(entry.getGID(), 2, swapHalfWord);
/* 365 */     writeBinaryLong(entry.getNumberOfLinks(), 2, swapHalfWord);
/* 366 */     writeBinaryLong(entry.getRemoteDevice(), 2, swapHalfWord);
/* 367 */     writeBinaryLong(entry.getTime(), 4, swapHalfWord);
/* 368 */     byte[] name = encode(entry.getName());
/* 369 */     writeBinaryLong(name.length + 1L, 2, swapHalfWord);
/* 370 */     writeBinaryLong(entry.getSize(), 4, swapHalfWord);
/* 371 */     writeCString(name);
/* 372 */     pad(entry.getHeaderPadCount(name.length));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeArchiveEntry() throws IOException {
/* 383 */     if (this.finished) {
/* 384 */       throw new IOException("Stream has already been finished");
/*     */     }
/*     */     
/* 387 */     ensureOpen();
/*     */     
/* 389 */     if (this.entry == null) {
/* 390 */       throw new IOException("Trying to close non-existent entry");
/*     */     }
/*     */     
/* 393 */     if (this.entry.getSize() != this.written) {
/* 394 */       throw new IOException("Invalid entry size (expected " + this.entry
/* 395 */           .getSize() + " but got " + this.written + " bytes)");
/*     */     }
/*     */     
/* 398 */     pad(this.entry.getDataPadCount());
/* 399 */     if (this.entry.getFormat() == 2 && this.crc != this.entry
/* 400 */       .getChksum()) {
/* 401 */       throw new IOException("CRC Error");
/*     */     }
/* 403 */     this.entry = null;
/* 404 */     this.crc = 0L;
/* 405 */     this.written = 0L;
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
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 425 */     ensureOpen();
/* 426 */     if (off < 0 || len < 0 || off > b.length - len) {
/* 427 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 429 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 433 */     if (this.entry == null) {
/* 434 */       throw new IOException("No current CPIO entry");
/*     */     }
/* 436 */     if (this.written + len > this.entry.getSize()) {
/* 437 */       throw new IOException("Attempt to write past end of STORED entry");
/*     */     }
/* 439 */     this.out.write(b, off, len);
/* 440 */     this.written += len;
/* 441 */     if (this.entry.getFormat() == 2) {
/* 442 */       for (int pos = 0; pos < len; pos++) {
/* 443 */         this.crc += (b[pos] & 0xFF);
/* 444 */         this.crc &= 0xFFFFFFFFL;
/*     */       } 
/*     */     }
/* 447 */     count(len);
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
/*     */   public void finish() throws IOException {
/* 461 */     ensureOpen();
/* 462 */     if (this.finished) {
/* 463 */       throw new IOException("This archive has already been finished");
/*     */     }
/*     */     
/* 466 */     if (this.entry != null) {
/* 467 */       throw new IOException("This archive contains unclosed entries.");
/*     */     }
/* 469 */     this.entry = new CpioArchiveEntry(this.entryFormat);
/* 470 */     this.entry.setName("TRAILER!!!");
/* 471 */     this.entry.setNumberOfLinks(1L);
/* 472 */     writeHeader(this.entry);
/* 473 */     closeArchiveEntry();
/*     */     
/* 475 */     int lengthOfLastBlock = (int)(getBytesWritten() % this.blockSize);
/* 476 */     if (lengthOfLastBlock != 0) {
/* 477 */       pad(this.blockSize - lengthOfLastBlock);
/*     */     }
/*     */     
/* 480 */     this.finished = true;
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
/*     */   public void close() throws IOException {
/*     */     try {
/* 493 */       if (!this.finished) {
/* 494 */         finish();
/*     */       }
/*     */     } finally {
/* 497 */       if (!this.closed) {
/* 498 */         this.out.close();
/* 499 */         this.closed = true;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void pad(int count) throws IOException {
/* 505 */     if (count > 0) {
/* 506 */       byte[] buff = new byte[count];
/* 507 */       this.out.write(buff);
/* 508 */       count(count);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeBinaryLong(long number, int length, boolean swapHalfWord) throws IOException {
/* 514 */     byte[] tmp = CpioUtil.long2byteArray(number, length, swapHalfWord);
/* 515 */     this.out.write(tmp);
/* 516 */     count(tmp.length);
/*     */   }
/*     */   
/*     */   private void writeAsciiLong(long number, int length, int radix) throws IOException {
/*     */     String tmpStr;
/* 521 */     StringBuilder tmp = new StringBuilder();
/*     */     
/* 523 */     if (radix == 16) {
/* 524 */       tmp.append(Long.toHexString(number));
/* 525 */     } else if (radix == 8) {
/* 526 */       tmp.append(Long.toOctalString(number));
/*     */     } else {
/* 528 */       tmp.append(Long.toString(number));
/*     */     } 
/*     */     
/* 531 */     if (tmp.length() <= length) {
/* 532 */       int insertLength = length - tmp.length();
/* 533 */       for (int pos = 0; pos < insertLength; pos++) {
/* 534 */         tmp.insert(0, "0");
/*     */       }
/* 536 */       tmpStr = tmp.toString();
/*     */     } else {
/* 538 */       tmpStr = tmp.substring(tmp.length() - length);
/*     */     } 
/* 540 */     byte[] b = ArchiveUtils.toAsciiBytes(tmpStr);
/* 541 */     this.out.write(b);
/* 542 */     count(b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] encode(String str) throws IOException {
/* 553 */     ByteBuffer buf = this.zipEncoding.encode(str);
/* 554 */     int len = buf.limit() - buf.position();
/* 555 */     return Arrays.copyOfRange(buf.array(), buf.arrayOffset(), buf.arrayOffset() + len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeCString(byte[] str) throws IOException {
/* 564 */     this.out.write(str);
/* 565 */     this.out.write(0);
/* 566 */     count(str.length + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 577 */     if (this.finished) {
/* 578 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 580 */     return new CpioArchiveEntry(inputFile, entryName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 591 */     if (this.finished) {
/* 592 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 594 */     return new CpioArchiveEntry(inputPath, entryName, options);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\cpio\CpioArchiveOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */