/*     */ package org.apache.commons.compress.archivers.tar;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StringWriter;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.CountingOutputStream;
/*     */ import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TarArchiveOutputStream
/*     */   extends ArchiveOutputStream
/*     */ {
/*     */   public static final int LONGFILE_ERROR = 0;
/*     */   public static final int LONGFILE_TRUNCATE = 1;
/*     */   public static final int LONGFILE_GNU = 2;
/*     */   public static final int LONGFILE_POSIX = 3;
/*     */   public static final int BIGNUMBER_ERROR = 0;
/*     */   public static final int BIGNUMBER_STAR = 1;
/*     */   public static final int BIGNUMBER_POSIX = 2;
/*     */   private static final int RECORD_SIZE = 512;
/*     */   private long currSize;
/*     */   private String currName;
/*     */   private long currBytes;
/*     */   private final byte[] recordBuf;
/*     */   private int longFileMode;
/*     */   private int bigNumberMode;
/*     */   private int recordsWritten;
/*     */   private final int recordsPerBlock;
/*     */   private boolean closed;
/*     */   private boolean haveUnclosedEntry;
/*     */   private boolean finished;
/*     */   private final FixedLengthBlockOutputStream out;
/*     */   private final CountingOutputStream countingOut;
/*     */   private final ZipEncoding zipEncoding;
/*     */   final String encoding;
/*     */   private boolean addPaxHeadersForNonAsciiNames;
/* 125 */   private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int BLOCK_SIZE_UNSPECIFIED = -511;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveOutputStream(OutputStream os) {
/* 137 */     this(os, -511);
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
/*     */   public TarArchiveOutputStream(OutputStream os, String encoding) {
/* 150 */     this(os, -511, encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveOutputStream(OutputStream os, int blockSize) {
/* 160 */     this(os, blockSize, (String)null);
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
/*     */   @Deprecated
/*     */   public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize) {
/* 176 */     this(os, blockSize, recordSize, (String)null);
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
/*     */   @Deprecated
/*     */   public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize, String encoding) {
/* 193 */     this(os, blockSize, encoding);
/* 194 */     if (recordSize != 512) {
/* 195 */       throw new IllegalArgumentException("Tar record size must always be 512 bytes. Attempt to set size of " + recordSize);
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
/*     */   public TarArchiveOutputStream(OutputStream os, int blockSize, String encoding) {
/*     */     int realBlockSize;
/*     */     this.longFileMode = 0;
/*     */     this.bigNumberMode = 0;
/* 212 */     if (-511 == blockSize) {
/* 213 */       realBlockSize = 512;
/*     */     } else {
/* 215 */       realBlockSize = blockSize;
/*     */     } 
/*     */     
/* 218 */     if (realBlockSize <= 0 || realBlockSize % 512 != 0) {
/* 219 */       throw new IllegalArgumentException("Block size must be a multiple of 512 bytes. Attempt to use set size of " + blockSize);
/*     */     }
/* 221 */     this.out = new FixedLengthBlockOutputStream((OutputStream)(this.countingOut = new CountingOutputStream(os)), 512);
/*     */     
/* 223 */     this.encoding = encoding;
/* 224 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*     */     
/* 226 */     this.recordBuf = new byte[512];
/* 227 */     this.recordsPerBlock = realBlockSize / 512;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLongFileMode(int longFileMode) {
/* 238 */     this.longFileMode = longFileMode;
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
/*     */   public void setBigNumberMode(int bigNumberMode) {
/* 251 */     this.bigNumberMode = bigNumberMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAddPaxHeadersForNonAsciiNames(boolean b) {
/* 261 */     this.addPaxHeadersForNonAsciiNames = b;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getCount() {
/* 267 */     return (int)getBytesWritten();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 272 */     return this.countingOut.getBytesWritten();
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
/* 286 */     if (this.finished) {
/* 287 */       throw new IOException("This archive has already been finished");
/*     */     }
/*     */     
/* 290 */     if (this.haveUnclosedEntry) {
/* 291 */       throw new IOException("This archive contains unclosed entries.");
/*     */     }
/* 293 */     writeEOFRecord();
/* 294 */     writeEOFRecord();
/* 295 */     padAsNeeded();
/* 296 */     this.out.flush();
/* 297 */     this.finished = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 308 */       if (!this.finished) {
/* 309 */         finish();
/*     */       }
/*     */     } finally {
/* 312 */       if (!this.closed) {
/* 313 */         this.out.close();
/* 314 */         this.closed = true;
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
/*     */   @Deprecated
/*     */   public int getRecordSize() {
/* 327 */     return 512;
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
/*     */   public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
/* 349 */     if (this.finished) {
/* 350 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 352 */     TarArchiveEntry entry = (TarArchiveEntry)archiveEntry;
/* 353 */     if (entry.isGlobalPaxHeader()) {
/* 354 */       byte[] data = encodeExtendedPaxHeadersContents(entry.getExtraPaxHeaders());
/* 355 */       entry.setSize(data.length);
/* 356 */       entry.writeEntryHeader(this.recordBuf, this.zipEncoding, (this.bigNumberMode == 1));
/* 357 */       writeRecord(this.recordBuf);
/* 358 */       this.currSize = entry.getSize();
/* 359 */       this.currBytes = 0L;
/* 360 */       this.haveUnclosedEntry = true;
/* 361 */       write(data);
/* 362 */       closeArchiveEntry();
/*     */     } else {
/* 364 */       Map<String, String> paxHeaders = new HashMap<>();
/* 365 */       String entryName = entry.getName();
/* 366 */       boolean paxHeaderContainsPath = handleLongName(entry, entryName, paxHeaders, "path", (byte)76, "file name");
/*     */ 
/*     */       
/* 369 */       String linkName = entry.getLinkName();
/*     */       
/* 371 */       boolean paxHeaderContainsLinkPath = (linkName != null && !linkName.isEmpty() && handleLongName(entry, linkName, paxHeaders, "linkpath", (byte)75, "link name"));
/*     */ 
/*     */       
/* 374 */       if (this.bigNumberMode == 2) {
/* 375 */         addPaxHeadersForBigNumbers(paxHeaders, entry);
/* 376 */       } else if (this.bigNumberMode != 1) {
/* 377 */         failForBigNumbers(entry);
/*     */       } 
/*     */       
/* 380 */       if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsPath && 
/* 381 */         !ASCII.canEncode(entryName)) {
/* 382 */         paxHeaders.put("path", entryName);
/*     */       }
/*     */       
/* 385 */       if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsLinkPath && (entry
/* 386 */         .isLink() || entry.isSymbolicLink()) && 
/* 387 */         !ASCII.canEncode(linkName)) {
/* 388 */         paxHeaders.put("linkpath", linkName);
/*     */       }
/* 390 */       paxHeaders.putAll(entry.getExtraPaxHeaders());
/*     */       
/* 392 */       if (!paxHeaders.isEmpty()) {
/* 393 */         writePaxHeaders(entry, entryName, paxHeaders);
/*     */       }
/*     */       
/* 396 */       entry.writeEntryHeader(this.recordBuf, this.zipEncoding, (this.bigNumberMode == 1));
/* 397 */       writeRecord(this.recordBuf);
/*     */       
/* 399 */       this.currBytes = 0L;
/*     */       
/* 401 */       if (entry.isDirectory()) {
/* 402 */         this.currSize = 0L;
/*     */       } else {
/* 404 */         this.currSize = entry.getSize();
/*     */       } 
/* 406 */       this.currName = entryName;
/* 407 */       this.haveUnclosedEntry = true;
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
/*     */   public void closeArchiveEntry() throws IOException {
/* 421 */     if (this.finished) {
/* 422 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 424 */     if (!this.haveUnclosedEntry) {
/* 425 */       throw new IOException("No current entry to close");
/*     */     }
/* 427 */     this.out.flushBlock();
/* 428 */     if (this.currBytes < this.currSize) {
/* 429 */       throw new IOException("Entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 434 */     this.recordsWritten = (int)(this.recordsWritten + this.currSize / 512L);
/* 435 */     if (0L != this.currSize % 512L) {
/* 436 */       this.recordsWritten++;
/*     */     }
/* 438 */     this.haveUnclosedEntry = false;
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
/*     */   public void write(byte[] wBuf, int wOffset, int numToWrite) throws IOException {
/* 453 */     if (!this.haveUnclosedEntry) {
/* 454 */       throw new IllegalStateException("No current tar entry");
/*     */     }
/* 456 */     if (this.currBytes + numToWrite > this.currSize) {
/* 457 */       throw new IOException("Request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 462 */     this.out.write(wBuf, wOffset, numToWrite);
/* 463 */     this.currBytes += numToWrite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void writePaxHeaders(TarArchiveEntry entry, String entryName, Map<String, String> headers) throws IOException {
/* 474 */     String name = "./PaxHeaders.X/" + stripTo7Bits(entryName);
/* 475 */     if (name.length() >= 100) {
/* 476 */       name = name.substring(0, 99);
/*     */     }
/* 478 */     TarArchiveEntry pex = new TarArchiveEntry(name, (byte)120);
/*     */     
/* 480 */     transferModTime(entry, pex);
/*     */     
/* 482 */     byte[] data = encodeExtendedPaxHeadersContents(headers);
/* 483 */     pex.setSize(data.length);
/* 484 */     putArchiveEntry(pex);
/* 485 */     write(data);
/* 486 */     closeArchiveEntry();
/*     */   }
/*     */   
/*     */   private byte[] encodeExtendedPaxHeadersContents(Map<String, String> headers) {
/* 490 */     StringWriter w = new StringWriter();
/* 491 */     for (Map.Entry<String, String> h : headers.entrySet()) {
/* 492 */       String key = h.getKey();
/* 493 */       String value = h.getValue();
/* 494 */       int len = key.length() + value.length() + 3 + 2;
/*     */ 
/*     */       
/* 497 */       String line = len + " " + key + "=" + value + "\n";
/* 498 */       int actualLength = (line.getBytes(StandardCharsets.UTF_8)).length;
/* 499 */       while (len != actualLength) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 505 */         len = actualLength;
/* 506 */         line = len + " " + key + "=" + value + "\n";
/* 507 */         actualLength = (line.getBytes(StandardCharsets.UTF_8)).length;
/*     */       } 
/* 509 */       w.write(line);
/*     */     } 
/* 511 */     return w.toString().getBytes(StandardCharsets.UTF_8);
/*     */   }
/*     */   
/*     */   private String stripTo7Bits(String name) {
/* 515 */     int length = name.length();
/* 516 */     StringBuilder result = new StringBuilder(length);
/* 517 */     for (int i = 0; i < length; i++) {
/* 518 */       char stripped = (char)(name.charAt(i) & 0x7F);
/* 519 */       if (shouldBeReplaced(stripped)) {
/* 520 */         result.append("_");
/*     */       } else {
/* 522 */         result.append(stripped);
/*     */       } 
/*     */     } 
/* 525 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldBeReplaced(char c) {
/* 533 */     return (c == '\000' || c == '/' || c == '\\');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeEOFRecord() throws IOException {
/* 543 */     Arrays.fill(this.recordBuf, (byte)0);
/* 544 */     writeRecord(this.recordBuf);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 549 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 555 */     if (this.finished) {
/* 556 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 558 */     return new TarArchiveEntry(inputFile, entryName);
/*     */   }
/*     */ 
/*     */   
/*     */   public ArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 563 */     if (this.finished) {
/* 564 */       throw new IOException("Stream has already been finished");
/*     */     }
/* 566 */     return new TarArchiveEntry(inputPath, entryName, options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeRecord(byte[] record) throws IOException {
/* 576 */     if (record.length != 512) {
/* 577 */       throw new IOException("Record to write has length '" + record.length + "' which is not the record size of '" + 'Ȁ' + "'");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 583 */     this.out.write(record);
/* 584 */     this.recordsWritten++;
/*     */   }
/*     */   
/*     */   private void padAsNeeded() throws IOException {
/* 588 */     int start = this.recordsWritten % this.recordsPerBlock;
/* 589 */     if (start != 0) {
/* 590 */       for (int i = start; i < this.recordsPerBlock; i++) {
/* 591 */         writeEOFRecord();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarArchiveEntry entry) {
/* 598 */     addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 8589934591L);
/*     */     
/* 600 */     addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), 2097151L);
/*     */     
/* 602 */     addPaxHeaderForBigNumber(paxHeaders, "mtime", entry
/* 603 */         .getModTime().getTime() / 1000L, 8589934591L);
/*     */     
/* 605 */     addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), 2097151L);
/*     */ 
/*     */     
/* 608 */     addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", entry
/* 609 */         .getDevMajor(), 2097151L);
/* 610 */     addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", entry
/* 611 */         .getDevMinor(), 2097151L);
/*     */     
/* 613 */     failForBigNumber("mode", entry.getMode(), 2097151L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue) {
/* 619 */     if (value < 0L || value > maxValue) {
/* 620 */       paxHeaders.put(header, String.valueOf(value));
/*     */     }
/*     */   }
/*     */   
/*     */   private void failForBigNumbers(TarArchiveEntry entry) {
/* 625 */     failForBigNumber("entry size", entry.getSize(), 8589934591L);
/* 626 */     failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), 2097151L);
/* 627 */     failForBigNumber("last modification time", entry
/* 628 */         .getModTime().getTime() / 1000L, 8589934591L);
/*     */     
/* 630 */     failForBigNumber("user id", entry.getLongUserId(), 2097151L);
/* 631 */     failForBigNumber("mode", entry.getMode(), 2097151L);
/* 632 */     failForBigNumber("major device number", entry.getDevMajor(), 2097151L);
/*     */     
/* 634 */     failForBigNumber("minor device number", entry.getDevMinor(), 2097151L);
/*     */   }
/*     */ 
/*     */   
/*     */   private void failForBigNumber(String field, long value, long maxValue) {
/* 639 */     failForBigNumber(field, value, maxValue, "");
/*     */   }
/*     */ 
/*     */   
/*     */   private void failForBigNumberWithPosixMessage(String field, long value, long maxValue) {
/* 644 */     failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void failForBigNumber(String field, long value, long maxValue, String additionalMsg) {
/* 650 */     if (value < 0L || value > maxValue) {
/* 651 */       throw new IllegalArgumentException(field + " '" + value + "' is too big ( > " + maxValue + " )." + additionalMsg);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean handleLongName(TarArchiveEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName) throws IOException {
/* 681 */     ByteBuffer encodedName = this.zipEncoding.encode(name);
/* 682 */     int len = encodedName.limit() - encodedName.position();
/* 683 */     if (len >= 100) {
/*     */       
/* 685 */       if (this.longFileMode == 3) {
/* 686 */         paxHeaders.put(paxHeaderName, name);
/* 687 */         return true;
/*     */       } 
/* 689 */       if (this.longFileMode == 2) {
/*     */ 
/*     */         
/* 692 */         TarArchiveEntry longLinkEntry = new TarArchiveEntry("././@LongLink", linkType);
/*     */ 
/*     */         
/* 695 */         longLinkEntry.setSize(len + 1L);
/* 696 */         transferModTime(entry, longLinkEntry);
/* 697 */         putArchiveEntry(longLinkEntry);
/* 698 */         write(encodedName.array(), encodedName.arrayOffset(), len);
/* 699 */         write(0);
/* 700 */         closeArchiveEntry();
/* 701 */       } else if (this.longFileMode != 1) {
/* 702 */         throw new IllegalArgumentException(fieldName + " '" + name + "' is too long ( > " + 'd' + " bytes)");
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 707 */     return false;
/*     */   }
/*     */   
/*     */   private void transferModTime(TarArchiveEntry from, TarArchiveEntry to) {
/* 711 */     Date fromModTime = from.getModTime();
/* 712 */     long fromModTimeSeconds = fromModTime.getTime() / 1000L;
/* 713 */     if (fromModTimeSeconds < 0L || fromModTimeSeconds > 8589934591L) {
/* 714 */       fromModTime = new Date(0L);
/*     */     }
/* 716 */     to.setModTime(fromModTime);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarArchiveOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */