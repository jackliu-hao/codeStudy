/*     */ package org.apache.commons.compress.archivers.cpio;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.ArchiveUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CpioArchiveInputStream
/*     */   extends ArchiveInputStream
/*     */   implements CpioConstants
/*     */ {
/*     */   private boolean closed;
/*     */   private CpioArchiveEntry entry;
/*     */   private long entryBytesRead;
/*     */   private boolean entryEOF;
/*  78 */   private final byte[] tmpbuf = new byte[4096];
/*     */ 
/*     */   
/*     */   private long crc;
/*     */   
/*     */   private final InputStream in;
/*     */   
/*  85 */   private final byte[] twoBytesBuf = new byte[2];
/*  86 */   private final byte[] fourBytesBuf = new byte[4];
/*  87 */   private final byte[] sixBytesBuf = new byte[6];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int blockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ZipEncoding zipEncoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String encoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpioArchiveInputStream(InputStream in) {
/* 108 */     this(in, 512, "US-ASCII");
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
/*     */   public CpioArchiveInputStream(InputStream in, String encoding) {
/* 123 */     this(in, 512, encoding);
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
/*     */   public CpioArchiveInputStream(InputStream in, int blockSize) {
/* 138 */     this(in, blockSize, "US-ASCII");
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
/*     */   public CpioArchiveInputStream(InputStream in, int blockSize, String encoding) {
/* 155 */     this.in = in;
/* 156 */     if (blockSize <= 0) {
/* 157 */       throw new IllegalArgumentException("blockSize must be bigger than 0");
/*     */     }
/* 159 */     this.blockSize = blockSize;
/* 160 */     this.encoding = encoding;
/* 161 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
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
/*     */   public int available() throws IOException {
/* 178 */     ensureOpen();
/* 179 */     if (this.entryEOF) {
/* 180 */       return 0;
/*     */     }
/* 182 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 193 */     if (!this.closed) {
/* 194 */       this.in.close();
/* 195 */       this.closed = true;
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
/*     */   private void closeEntry() throws IOException {
/* 210 */     while (skip(2147483647L) == 2147483647L);
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
/*     */   private void ensureOpen() throws IOException {
/* 222 */     if (this.closed) {
/* 223 */       throw new IOException("Stream closed");
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
/*     */   public CpioArchiveEntry getNextCPIOEntry() throws IOException {
/* 237 */     ensureOpen();
/* 238 */     if (this.entry != null) {
/* 239 */       closeEntry();
/*     */     }
/* 241 */     readFully(this.twoBytesBuf, 0, this.twoBytesBuf.length);
/* 242 */     if (CpioUtil.byteArray2long(this.twoBytesBuf, false) == 29127L) {
/* 243 */       this.entry = readOldBinaryEntry(false);
/* 244 */     } else if (CpioUtil.byteArray2long(this.twoBytesBuf, true) == 29127L) {
/*     */       
/* 246 */       this.entry = readOldBinaryEntry(true);
/*     */     } else {
/* 248 */       System.arraycopy(this.twoBytesBuf, 0, this.sixBytesBuf, 0, this.twoBytesBuf.length);
/*     */       
/* 250 */       readFully(this.sixBytesBuf, this.twoBytesBuf.length, this.fourBytesBuf.length);
/*     */       
/* 252 */       String magicString = ArchiveUtils.toAsciiString(this.sixBytesBuf);
/* 253 */       switch (magicString) {
/*     */         case "070701":
/* 255 */           this.entry = readNewEntry(false);
/*     */           break;
/*     */         case "070702":
/* 258 */           this.entry = readNewEntry(true);
/*     */           break;
/*     */         case "070707":
/* 261 */           this.entry = readOldAsciiEntry();
/*     */           break;
/*     */         default:
/* 264 */           throw new IOException("Unknown magic [" + magicString + "]. Occurred at byte: " + getBytesRead());
/*     */       } 
/*     */     
/*     */     } 
/* 268 */     this.entryBytesRead = 0L;
/* 269 */     this.entryEOF = false;
/* 270 */     this.crc = 0L;
/*     */     
/* 272 */     if (this.entry.getName().equals("TRAILER!!!")) {
/* 273 */       this.entryEOF = true;
/* 274 */       skipRemainderOfLastBlock();
/* 275 */       return null;
/*     */     } 
/* 277 */     return this.entry;
/*     */   }
/*     */ 
/*     */   
/*     */   private void skip(int bytes) throws IOException {
/* 282 */     if (bytes > 0) {
/* 283 */       readFully(this.fourBytesBuf, 0, bytes);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 306 */     ensureOpen();
/* 307 */     if (off < 0 || len < 0 || off > b.length - len) {
/* 308 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 310 */     if (len == 0) {
/* 311 */       return 0;
/*     */     }
/*     */     
/* 314 */     if (this.entry == null || this.entryEOF) {
/* 315 */       return -1;
/*     */     }
/* 317 */     if (this.entryBytesRead == this.entry.getSize()) {
/* 318 */       skip(this.entry.getDataPadCount());
/* 319 */       this.entryEOF = true;
/* 320 */       if (this.entry.getFormat() == 2 && this.crc != this.entry
/* 321 */         .getChksum()) {
/* 322 */         throw new IOException("CRC Error. Occurred at byte: " + 
/* 323 */             getBytesRead());
/*     */       }
/* 325 */       return -1;
/*     */     } 
/* 327 */     int tmplength = (int)Math.min(len, this.entry.getSize() - this.entryBytesRead);
/*     */     
/* 329 */     if (tmplength < 0) {
/* 330 */       return -1;
/*     */     }
/*     */     
/* 333 */     int tmpread = readFully(b, off, tmplength);
/* 334 */     if (this.entry.getFormat() == 2) {
/* 335 */       for (int pos = 0; pos < tmpread; pos++) {
/* 336 */         this.crc += (b[pos] & 0xFF);
/* 337 */         this.crc &= 0xFFFFFFFFL;
/*     */       } 
/*     */     }
/* 340 */     if (tmpread > 0) {
/* 341 */       this.entryBytesRead += tmpread;
/*     */     }
/*     */     
/* 344 */     return tmpread;
/*     */   }
/*     */ 
/*     */   
/*     */   private final int readFully(byte[] b, int off, int len) throws IOException {
/* 349 */     int count = IOUtils.readFully(this.in, b, off, len);
/* 350 */     count(count);
/* 351 */     if (count < len) {
/* 352 */       throw new EOFException();
/*     */     }
/* 354 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   private final byte[] readRange(int len) throws IOException {
/* 359 */     byte[] b = IOUtils.readRange(this.in, len);
/* 360 */     count(b.length);
/* 361 */     if (b.length < len) {
/* 362 */       throw new EOFException();
/*     */     }
/* 364 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   private long readBinaryLong(int length, boolean swapHalfWord) throws IOException {
/* 369 */     byte[] tmp = readRange(length);
/* 370 */     return CpioUtil.byteArray2long(tmp, swapHalfWord);
/*     */   }
/*     */ 
/*     */   
/*     */   private long readAsciiLong(int length, int radix) throws IOException {
/* 375 */     byte[] tmpBuffer = readRange(length);
/* 376 */     return Long.parseLong(ArchiveUtils.toAsciiString(tmpBuffer), radix);
/*     */   }
/*     */ 
/*     */   
/*     */   private CpioArchiveEntry readNewEntry(boolean hasCrc) throws IOException {
/*     */     CpioArchiveEntry ret;
/* 382 */     if (hasCrc) {
/* 383 */       ret = new CpioArchiveEntry((short)2);
/*     */     } else {
/* 385 */       ret = new CpioArchiveEntry((short)1);
/*     */     } 
/*     */     
/* 388 */     ret.setInode(readAsciiLong(8, 16));
/* 389 */     long mode = readAsciiLong(8, 16);
/* 390 */     if (CpioUtil.fileType(mode) != 0L) {
/* 391 */       ret.setMode(mode);
/*     */     }
/* 393 */     ret.setUID(readAsciiLong(8, 16));
/* 394 */     ret.setGID(readAsciiLong(8, 16));
/* 395 */     ret.setNumberOfLinks(readAsciiLong(8, 16));
/* 396 */     ret.setTime(readAsciiLong(8, 16));
/* 397 */     ret.setSize(readAsciiLong(8, 16));
/* 398 */     if (ret.getSize() < 0L) {
/* 399 */       throw new IOException("Found illegal entry with negative length");
/*     */     }
/* 401 */     ret.setDeviceMaj(readAsciiLong(8, 16));
/* 402 */     ret.setDeviceMin(readAsciiLong(8, 16));
/* 403 */     ret.setRemoteDeviceMaj(readAsciiLong(8, 16));
/* 404 */     ret.setRemoteDeviceMin(readAsciiLong(8, 16));
/* 405 */     long namesize = readAsciiLong(8, 16);
/* 406 */     if (namesize < 0L) {
/* 407 */       throw new IOException("Found illegal entry with negative name length");
/*     */     }
/* 409 */     ret.setChksum(readAsciiLong(8, 16));
/* 410 */     String name = readCString((int)namesize);
/* 411 */     ret.setName(name);
/* 412 */     if (CpioUtil.fileType(mode) == 0L && !name.equals("TRAILER!!!")) {
/* 413 */       throw new IOException("Mode 0 only allowed in the trailer. Found entry name: " + 
/* 414 */           ArchiveUtils.sanitize(name) + " Occurred at byte: " + 
/* 415 */           getBytesRead());
/*     */     }
/* 417 */     skip(ret.getHeaderPadCount(namesize - 1L));
/*     */     
/* 419 */     return ret;
/*     */   }
/*     */   
/*     */   private CpioArchiveEntry readOldAsciiEntry() throws IOException {
/* 423 */     CpioArchiveEntry ret = new CpioArchiveEntry((short)4);
/*     */     
/* 425 */     ret.setDevice(readAsciiLong(6, 8));
/* 426 */     ret.setInode(readAsciiLong(6, 8));
/* 427 */     long mode = readAsciiLong(6, 8);
/* 428 */     if (CpioUtil.fileType(mode) != 0L) {
/* 429 */       ret.setMode(mode);
/*     */     }
/* 431 */     ret.setUID(readAsciiLong(6, 8));
/* 432 */     ret.setGID(readAsciiLong(6, 8));
/* 433 */     ret.setNumberOfLinks(readAsciiLong(6, 8));
/* 434 */     ret.setRemoteDevice(readAsciiLong(6, 8));
/* 435 */     ret.setTime(readAsciiLong(11, 8));
/* 436 */     long namesize = readAsciiLong(6, 8);
/* 437 */     if (namesize < 0L) {
/* 438 */       throw new IOException("Found illegal entry with negative name length");
/*     */     }
/* 440 */     ret.setSize(readAsciiLong(11, 8));
/* 441 */     if (ret.getSize() < 0L) {
/* 442 */       throw new IOException("Found illegal entry with negative length");
/*     */     }
/* 444 */     String name = readCString((int)namesize);
/* 445 */     ret.setName(name);
/* 446 */     if (CpioUtil.fileType(mode) == 0L && !name.equals("TRAILER!!!")) {
/* 447 */       throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + 
/* 448 */           ArchiveUtils.sanitize(name) + " Occurred at byte: " + 
/* 449 */           getBytesRead());
/*     */     }
/*     */     
/* 452 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   private CpioArchiveEntry readOldBinaryEntry(boolean swapHalfWord) throws IOException {
/* 457 */     CpioArchiveEntry ret = new CpioArchiveEntry((short)8);
/*     */     
/* 459 */     ret.setDevice(readBinaryLong(2, swapHalfWord));
/* 460 */     ret.setInode(readBinaryLong(2, swapHalfWord));
/* 461 */     long mode = readBinaryLong(2, swapHalfWord);
/* 462 */     if (CpioUtil.fileType(mode) != 0L) {
/* 463 */       ret.setMode(mode);
/*     */     }
/* 465 */     ret.setUID(readBinaryLong(2, swapHalfWord));
/* 466 */     ret.setGID(readBinaryLong(2, swapHalfWord));
/* 467 */     ret.setNumberOfLinks(readBinaryLong(2, swapHalfWord));
/* 468 */     ret.setRemoteDevice(readBinaryLong(2, swapHalfWord));
/* 469 */     ret.setTime(readBinaryLong(4, swapHalfWord));
/* 470 */     long namesize = readBinaryLong(2, swapHalfWord);
/* 471 */     if (namesize < 0L) {
/* 472 */       throw new IOException("Found illegal entry with negative name length");
/*     */     }
/* 474 */     ret.setSize(readBinaryLong(4, swapHalfWord));
/* 475 */     if (ret.getSize() < 0L) {
/* 476 */       throw new IOException("Found illegal entry with negative length");
/*     */     }
/* 478 */     String name = readCString((int)namesize);
/* 479 */     ret.setName(name);
/* 480 */     if (CpioUtil.fileType(mode) == 0L && !name.equals("TRAILER!!!")) {
/* 481 */       throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + 
/* 482 */           ArchiveUtils.sanitize(name) + "Occurred at byte: " + 
/* 483 */           getBytesRead());
/*     */     }
/* 485 */     skip(ret.getHeaderPadCount(namesize - 1L));
/*     */     
/* 487 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   private String readCString(int length) throws IOException {
/* 492 */     byte[] tmpBuffer = readRange(length - 1);
/* 493 */     if (this.in.read() == -1) {
/* 494 */       throw new EOFException();
/*     */     }
/* 496 */     return this.zipEncoding.decode(tmpBuffer);
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
/*     */   public long skip(long n) throws IOException {
/* 512 */     if (n < 0L) {
/* 513 */       throw new IllegalArgumentException("Negative skip length");
/*     */     }
/* 515 */     ensureOpen();
/* 516 */     int max = (int)Math.min(n, 2147483647L);
/* 517 */     int total = 0;
/*     */     
/* 519 */     while (total < max) {
/* 520 */       int len = max - total;
/* 521 */       if (len > this.tmpbuf.length) {
/* 522 */         len = this.tmpbuf.length;
/*     */       }
/* 524 */       len = read(this.tmpbuf, 0, len);
/* 525 */       if (len == -1) {
/* 526 */         this.entryEOF = true;
/*     */         break;
/*     */       } 
/* 529 */       total += len;
/*     */     } 
/* 531 */     return total;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArchiveEntry getNextEntry() throws IOException {
/* 536 */     return getNextCPIOEntry();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void skipRemainderOfLastBlock() throws IOException {
/* 543 */     long readFromLastBlock = getBytesRead() % this.blockSize;
/* 544 */     long remainingBytes = (readFromLastBlock == 0L) ? 0L : (this.blockSize - readFromLastBlock);
/*     */     
/* 546 */     while (remainingBytes > 0L) {
/* 547 */       long skipped = skip(this.blockSize - readFromLastBlock);
/* 548 */       if (skipped <= 0L) {
/*     */         break;
/*     */       }
/* 551 */       remainingBytes -= skipped;
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
/*     */   
/*     */   public static boolean matches(byte[] signature, int length) {
/* 572 */     if (length < 6) {
/* 573 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 577 */     if (signature[0] == 113 && (signature[1] & 0xFF) == 199) {
/* 578 */       return true;
/*     */     }
/* 580 */     if (signature[1] == 113 && (signature[0] & 0xFF) == 199) {
/* 581 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 586 */     if (signature[0] != 48) {
/* 587 */       return false;
/*     */     }
/* 589 */     if (signature[1] != 55) {
/* 590 */       return false;
/*     */     }
/* 592 */     if (signature[2] != 48) {
/* 593 */       return false;
/*     */     }
/* 595 */     if (signature[3] != 55) {
/* 596 */       return false;
/*     */     }
/* 598 */     if (signature[4] != 48) {
/* 599 */       return false;
/*     */     }
/*     */     
/* 602 */     if (signature[5] == 49) {
/* 603 */       return true;
/*     */     }
/* 605 */     if (signature[5] == 50) {
/* 606 */       return true;
/*     */     }
/* 608 */     if (signature[5] == 55) {
/* 609 */       return true;
/*     */     }
/*     */     
/* 612 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\cpio\CpioArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */