/*     */ package org.apache.commons.compress.archivers.tar;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.ArchiveUtils;
/*     */ import org.apache.commons.compress.utils.BoundedInputStream;
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
/*     */ public class TarArchiveInputStream
/*     */   extends ArchiveInputStream
/*     */ {
/*     */   private static final int SMALL_BUFFER_SIZE = 256;
/*  54 */   private final byte[] smallBuf = new byte[256];
/*     */ 
/*     */   
/*     */   private final int recordSize;
/*     */ 
/*     */   
/*     */   private final byte[] recordBuffer;
/*     */ 
/*     */   
/*     */   private final int blockSize;
/*     */ 
/*     */   
/*     */   private boolean hasHitEOF;
/*     */ 
/*     */   
/*     */   private long entrySize;
/*     */ 
/*     */   
/*     */   private long entryOffset;
/*     */ 
/*     */   
/*     */   private final InputStream inputStream;
/*     */ 
/*     */   
/*     */   private List<InputStream> sparseInputStreams;
/*     */ 
/*     */   
/*     */   private int currentSparseInputStreamIndex;
/*     */ 
/*     */   
/*     */   private TarArchiveEntry currEntry;
/*     */ 
/*     */   
/*     */   private final ZipEncoding zipEncoding;
/*     */ 
/*     */   
/*     */   final String encoding;
/*     */ 
/*     */   
/*  93 */   private Map<String, String> globalPaxHeaders = new HashMap<>();
/*     */ 
/*     */   
/*  96 */   private final List<TarArchiveStructSparse> globalSparseHeaders = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean lenient;
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveInputStream(InputStream is) {
/* 105 */     this(is, 10240, 512);
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
/*     */   public TarArchiveInputStream(InputStream is, boolean lenient) {
/* 117 */     this(is, 10240, 512, (String)null, lenient);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveInputStream(InputStream is, String encoding) {
/* 127 */     this(is, 10240, 512, encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveInputStream(InputStream is, int blockSize) {
/* 137 */     this(is, blockSize, 512);
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
/*     */   public TarArchiveInputStream(InputStream is, int blockSize, String encoding) {
/* 149 */     this(is, blockSize, 512, encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize) {
/* 159 */     this(is, blockSize, recordSize, (String)null);
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
/*     */   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding) {
/* 172 */     this(is, blockSize, recordSize, encoding, false);
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
/*     */   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding, boolean lenient) {
/* 188 */     this.inputStream = is;
/* 189 */     this.hasHitEOF = false;
/* 190 */     this.encoding = encoding;
/* 191 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/* 192 */     this.recordSize = recordSize;
/* 193 */     this.recordBuffer = new byte[recordSize];
/* 194 */     this.blockSize = blockSize;
/* 195 */     this.lenient = lenient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 205 */     if (this.sparseInputStreams != null) {
/* 206 */       for (InputStream inputStream : this.sparseInputStreams) {
/* 207 */         inputStream.close();
/*     */       }
/*     */     }
/*     */     
/* 211 */     this.inputStream.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRecordSize() {
/* 220 */     return this.recordSize;
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
/* 237 */     if (isDirectory()) {
/* 238 */       return 0;
/*     */     }
/*     */     
/* 241 */     if (this.currEntry.getRealSize() - this.entryOffset > 2147483647L) {
/* 242 */       return Integer.MAX_VALUE;
/*     */     }
/* 244 */     return (int)(this.currEntry.getRealSize() - this.entryOffset);
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
/*     */   public long skip(long n) throws IOException {
/*     */     long skipped;
/* 266 */     if (n <= 0L || isDirectory()) {
/* 267 */       return 0L;
/*     */     }
/*     */     
/* 270 */     long availableOfInputStream = this.inputStream.available();
/* 271 */     long available = this.currEntry.getRealSize() - this.entryOffset;
/* 272 */     long numToSkip = Math.min(n, available);
/*     */ 
/*     */     
/* 275 */     if (!this.currEntry.isSparse()) {
/* 276 */       skipped = IOUtils.skip(this.inputStream, numToSkip);
/*     */ 
/*     */       
/* 279 */       skipped = getActuallySkipped(availableOfInputStream, skipped, numToSkip);
/*     */     } else {
/* 281 */       skipped = skipSparse(numToSkip);
/*     */     } 
/*     */ 
/*     */     
/* 285 */     count(skipped);
/* 286 */     this.entryOffset += skipped;
/* 287 */     return skipped;
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
/*     */   private long skipSparse(long n) throws IOException {
/* 300 */     if (this.sparseInputStreams == null || this.sparseInputStreams.isEmpty()) {
/* 301 */       return this.inputStream.skip(n);
/*     */     }
/*     */     
/* 304 */     long bytesSkipped = 0L;
/*     */     
/* 306 */     while (bytesSkipped < n && this.currentSparseInputStreamIndex < this.sparseInputStreams.size()) {
/* 307 */       InputStream currentInputStream = this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
/* 308 */       bytesSkipped += currentInputStream.skip(n - bytesSkipped);
/*     */       
/* 310 */       if (bytesSkipped < n) {
/* 311 */         this.currentSparseInputStreamIndex++;
/*     */       }
/*     */     } 
/*     */     
/* 315 */     return bytesSkipped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 325 */     return false;
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
/*     */   public synchronized void mark(int markLimit) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void reset() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveEntry getNextTarEntry() throws IOException {
/* 358 */     if (isAtEOF()) {
/* 359 */       return null;
/*     */     }
/*     */     
/* 362 */     if (this.currEntry != null) {
/*     */       
/* 364 */       IOUtils.skip((InputStream)this, Long.MAX_VALUE);
/*     */ 
/*     */       
/* 367 */       skipRecordPadding();
/*     */     } 
/*     */     
/* 370 */     byte[] headerBuf = getRecord();
/*     */     
/* 372 */     if (headerBuf == null) {
/*     */       
/* 374 */       this.currEntry = null;
/* 375 */       return null;
/*     */     } 
/*     */     
/*     */     try {
/* 379 */       this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding, this.lenient);
/* 380 */     } catch (IllegalArgumentException e) {
/* 381 */       throw new IOException("Error detected parsing the header", e);
/*     */     } 
/*     */     
/* 384 */     this.entryOffset = 0L;
/* 385 */     this.entrySize = this.currEntry.getSize();
/*     */     
/* 387 */     if (this.currEntry.isGNULongLinkEntry()) {
/* 388 */       byte[] longLinkData = getLongNameData();
/* 389 */       if (longLinkData == null)
/*     */       {
/*     */ 
/*     */         
/* 393 */         return null;
/*     */       }
/* 395 */       this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
/*     */     } 
/*     */     
/* 398 */     if (this.currEntry.isGNULongNameEntry()) {
/* 399 */       byte[] longNameData = getLongNameData();
/* 400 */       if (longNameData == null)
/*     */       {
/*     */ 
/*     */         
/* 404 */         return null;
/*     */       }
/*     */ 
/*     */       
/* 408 */       String name = this.zipEncoding.decode(longNameData);
/* 409 */       this.currEntry.setName(name);
/* 410 */       if (this.currEntry.isDirectory() && !name.endsWith("/")) {
/* 411 */         this.currEntry.setName(name + "/");
/*     */       }
/*     */     } 
/*     */     
/* 415 */     if (this.currEntry.isGlobalPaxHeader()) {
/* 416 */       readGlobalPaxHeaders();
/*     */     }
/*     */     
/*     */     try {
/* 420 */       if (this.currEntry.isPaxHeader()) {
/* 421 */         paxHeaders();
/* 422 */       } else if (!this.globalPaxHeaders.isEmpty()) {
/* 423 */         applyPaxHeadersToCurrentEntry(this.globalPaxHeaders, this.globalSparseHeaders);
/*     */       } 
/* 425 */     } catch (NumberFormatException e) {
/* 426 */       throw new IOException("Error detected parsing the pax header", e);
/*     */     } 
/*     */     
/* 429 */     if (this.currEntry.isOldGNUSparse()) {
/* 430 */       readOldGNUSparse();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 437 */     this.entrySize = this.currEntry.getSize();
/*     */     
/* 439 */     return this.currEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void skipRecordPadding() throws IOException {
/* 449 */     if (!isDirectory() && this.entrySize > 0L && this.entrySize % this.recordSize != 0L) {
/* 450 */       long available = this.inputStream.available();
/* 451 */       long numRecords = this.entrySize / this.recordSize + 1L;
/* 452 */       long padding = numRecords * this.recordSize - this.entrySize;
/* 453 */       long skipped = IOUtils.skip(this.inputStream, padding);
/*     */       
/* 455 */       skipped = getActuallySkipped(available, skipped, padding);
/*     */       
/* 457 */       count(skipped);
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
/*     */   private long getActuallySkipped(long available, long skipped, long expected) throws IOException {
/* 472 */     long actuallySkipped = skipped;
/* 473 */     if (this.inputStream instanceof java.io.FileInputStream) {
/* 474 */       actuallySkipped = Math.min(skipped, available);
/*     */     }
/*     */     
/* 477 */     if (actuallySkipped != expected) {
/* 478 */       throw new IOException("Truncated TAR archive");
/*     */     }
/*     */     
/* 481 */     return actuallySkipped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] getLongNameData() throws IOException {
/* 492 */     ByteArrayOutputStream longName = new ByteArrayOutputStream();
/* 493 */     int length = 0;
/* 494 */     while ((length = read(this.smallBuf)) >= 0) {
/* 495 */       longName.write(this.smallBuf, 0, length);
/*     */     }
/* 497 */     getNextEntry();
/* 498 */     if (this.currEntry == null)
/*     */     {
/*     */       
/* 501 */       return null;
/*     */     }
/* 503 */     byte[] longNameData = longName.toByteArray();
/*     */     
/* 505 */     length = longNameData.length;
/* 506 */     while (length > 0 && longNameData[length - 1] == 0) {
/* 507 */       length--;
/*     */     }
/* 509 */     if (length != longNameData.length) {
/* 510 */       byte[] l = new byte[length];
/* 511 */       System.arraycopy(longNameData, 0, l, 0, length);
/* 512 */       longNameData = l;
/*     */     } 
/* 514 */     return longNameData;
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
/*     */   private byte[] getRecord() throws IOException {
/* 532 */     byte[] headerBuf = readRecord();
/* 533 */     setAtEOF(isEOFRecord(headerBuf));
/* 534 */     if (isAtEOF() && headerBuf != null) {
/* 535 */       tryToConsumeSecondEOFRecord();
/* 536 */       consumeRemainderOfLastBlock();
/* 537 */       headerBuf = null;
/*     */     } 
/* 539 */     return headerBuf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isEOFRecord(byte[] record) {
/* 550 */     return (record == null || ArchiveUtils.isArrayZero(record, this.recordSize));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] readRecord() throws IOException {
/* 560 */     int readNow = IOUtils.readFully(this.inputStream, this.recordBuffer);
/* 561 */     count(readNow);
/* 562 */     if (readNow != this.recordSize) {
/* 563 */       return null;
/*     */     }
/*     */     
/* 566 */     return this.recordBuffer;
/*     */   }
/*     */   
/*     */   private void readGlobalPaxHeaders() throws IOException {
/* 570 */     this.globalPaxHeaders = TarUtils.parsePaxHeaders((InputStream)this, this.globalSparseHeaders, this.globalPaxHeaders, this.entrySize);
/* 571 */     getNextEntry();
/*     */     
/* 573 */     if (this.currEntry == null) {
/* 574 */       throw new IOException("Error detected parsing the pax header");
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
/*     */   private void paxHeaders() throws IOException {
/* 604 */     List<TarArchiveStructSparse> sparseHeaders = new ArrayList<>();
/* 605 */     Map<String, String> headers = TarUtils.parsePaxHeaders((InputStream)this, sparseHeaders, this.globalPaxHeaders, this.entrySize);
/*     */ 
/*     */     
/* 608 */     if (headers.containsKey("GNU.sparse.map")) {
/* 609 */       sparseHeaders = new ArrayList<>(TarUtils.parseFromPAX01SparseHeaders(headers.get("GNU.sparse.map")));
/*     */     }
/* 611 */     getNextEntry();
/* 612 */     if (this.currEntry == null) {
/* 613 */       throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
/*     */     }
/* 615 */     applyPaxHeadersToCurrentEntry(headers, sparseHeaders);
/*     */ 
/*     */     
/* 618 */     if (this.currEntry.isPaxGNU1XSparse()) {
/* 619 */       sparseHeaders = TarUtils.parsePAX1XSparseHeaders(this.inputStream, this.recordSize);
/* 620 */       this.currEntry.setSparseHeaders(sparseHeaders);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 625 */     buildSparseInputStreams();
/*     */   }
/*     */ 
/*     */   
/*     */   private void applyPaxHeadersToCurrentEntry(Map<String, String> headers, List<TarArchiveStructSparse> sparseHeaders) throws IOException {
/* 630 */     this.currEntry.updateEntryFromPaxHeaders(headers);
/* 631 */     this.currEntry.setSparseHeaders(sparseHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readOldGNUSparse() throws IOException {
/* 641 */     if (this.currEntry.isExtended()) {
/*     */       TarArchiveSparseEntry entry;
/*     */       do {
/* 644 */         byte[] headerBuf = getRecord();
/* 645 */         if (headerBuf == null) {
/* 646 */           throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
/*     */         }
/* 648 */         entry = new TarArchiveSparseEntry(headerBuf);
/* 649 */         this.currEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
/* 650 */       } while (entry.isExtended());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 655 */     buildSparseInputStreams();
/*     */   }
/*     */   
/*     */   private boolean isDirectory() {
/* 659 */     return (this.currEntry != null && this.currEntry.isDirectory());
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
/*     */   public ArchiveEntry getNextEntry() throws IOException {
/* 671 */     return getNextTarEntry();
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
/*     */   private void tryToConsumeSecondEOFRecord() throws IOException {
/* 685 */     boolean shouldReset = true;
/* 686 */     boolean marked = this.inputStream.markSupported();
/* 687 */     if (marked) {
/* 688 */       this.inputStream.mark(this.recordSize);
/*     */     }
/*     */     try {
/* 691 */       shouldReset = !isEOFRecord(readRecord());
/*     */     } finally {
/* 693 */       if (shouldReset && marked) {
/* 694 */         pushedBackBytes(this.recordSize);
/* 695 */         this.inputStream.reset();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int offset, int numToRead) throws IOException {
/* 715 */     if (numToRead == 0) {
/* 716 */       return 0;
/*     */     }
/* 718 */     int totalRead = 0;
/*     */     
/* 720 */     if (isAtEOF() || isDirectory()) {
/* 721 */       return -1;
/*     */     }
/*     */     
/* 724 */     if (this.currEntry == null) {
/* 725 */       throw new IllegalStateException("No current tar entry");
/*     */     }
/*     */     
/* 728 */     if (this.entryOffset >= this.currEntry.getRealSize()) {
/* 729 */       return -1;
/*     */     }
/*     */     
/* 732 */     numToRead = Math.min(numToRead, available());
/*     */     
/* 734 */     if (this.currEntry.isSparse()) {
/*     */       
/* 736 */       totalRead = readSparse(buf, offset, numToRead);
/*     */     } else {
/* 738 */       totalRead = this.inputStream.read(buf, offset, numToRead);
/*     */     } 
/*     */     
/* 741 */     if (totalRead == -1) {
/* 742 */       if (numToRead > 0) {
/* 743 */         throw new IOException("Truncated TAR archive");
/*     */       }
/* 745 */       setAtEOF(true);
/*     */     } else {
/* 747 */       count(totalRead);
/* 748 */       this.entryOffset += totalRead;
/*     */     } 
/*     */     
/* 751 */     return totalRead;
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
/*     */   private int readSparse(byte[] buf, int offset, int numToRead) throws IOException {
/* 770 */     if (this.sparseInputStreams == null || this.sparseInputStreams.isEmpty()) {
/* 771 */       return this.inputStream.read(buf, offset, numToRead);
/*     */     }
/*     */     
/* 774 */     if (this.currentSparseInputStreamIndex >= this.sparseInputStreams.size()) {
/* 775 */       return -1;
/*     */     }
/*     */     
/* 778 */     InputStream currentInputStream = this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
/* 779 */     int readLen = currentInputStream.read(buf, offset, numToRead);
/*     */ 
/*     */ 
/*     */     
/* 783 */     if (this.currentSparseInputStreamIndex == this.sparseInputStreams.size() - 1) {
/* 784 */       return readLen;
/*     */     }
/*     */ 
/*     */     
/* 788 */     if (readLen == -1) {
/* 789 */       this.currentSparseInputStreamIndex++;
/* 790 */       return readSparse(buf, offset, numToRead);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 795 */     if (readLen < numToRead) {
/* 796 */       this.currentSparseInputStreamIndex++;
/* 797 */       int readLenOfNext = readSparse(buf, offset + readLen, numToRead - readLen);
/* 798 */       if (readLenOfNext == -1) {
/* 799 */         return readLen;
/*     */       }
/*     */       
/* 802 */       return readLen + readLenOfNext;
/*     */     } 
/*     */ 
/*     */     
/* 806 */     return readLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canReadEntryData(ArchiveEntry ae) {
/* 816 */     return ae instanceof TarArchiveEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarArchiveEntry getCurrentEntry() {
/* 825 */     return this.currEntry;
/*     */   }
/*     */   
/*     */   protected final void setCurrentEntry(TarArchiveEntry e) {
/* 829 */     this.currEntry = e;
/*     */   }
/*     */   
/*     */   protected final boolean isAtEOF() {
/* 833 */     return this.hasHitEOF;
/*     */   }
/*     */   
/*     */   protected final void setAtEOF(boolean b) {
/* 837 */     this.hasHitEOF = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void consumeRemainderOfLastBlock() throws IOException {
/* 846 */     long bytesReadOfLastBlock = getBytesRead() % this.blockSize;
/* 847 */     if (bytesReadOfLastBlock > 0L) {
/* 848 */       long skipped = IOUtils.skip(this.inputStream, this.blockSize - bytesReadOfLastBlock);
/* 849 */       count(skipped);
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 863 */     if (length < 265) {
/* 864 */       return false;
/*     */     }
/*     */     
/* 867 */     if (ArchiveUtils.matchAsciiBuffer("ustar\000", signature, 257, 6) && 
/*     */ 
/*     */       
/* 870 */       ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2))
/*     */     {
/*     */       
/* 873 */       return true;
/*     */     }
/* 875 */     if (ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6) && (
/*     */ 
/*     */ 
/*     */       
/* 879 */       ArchiveUtils.matchAsciiBuffer(" \000", signature, 263, 2) || 
/*     */ 
/*     */       
/* 882 */       ArchiveUtils.matchAsciiBuffer("0\000", signature, 263, 2)))
/*     */     {
/*     */ 
/*     */       
/* 886 */       return true;
/*     */     }
/*     */     
/* 889 */     return (ArchiveUtils.matchAsciiBuffer("ustar\000", signature, 257, 6) && 
/*     */ 
/*     */       
/* 892 */       ArchiveUtils.matchAsciiBuffer("\000\000", signature, 263, 2));
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
/*     */   private void buildSparseInputStreams() throws IOException {
/* 905 */     this.currentSparseInputStreamIndex = -1;
/* 906 */     this.sparseInputStreams = new ArrayList<>();
/*     */     
/* 908 */     List<TarArchiveStructSparse> sparseHeaders = this.currEntry.getOrderedSparseHeaders();
/*     */ 
/*     */     
/* 911 */     InputStream zeroInputStream = new TarArchiveSparseZeroInputStream();
/*     */     
/* 913 */     long offset = 0L;
/* 914 */     for (TarArchiveStructSparse sparseHeader : sparseHeaders) {
/* 915 */       long zeroBlockSize = sparseHeader.getOffset() - offset;
/* 916 */       if (zeroBlockSize < 0L)
/*     */       {
/* 918 */         throw new IOException("Corrupted struct sparse detected");
/*     */       }
/*     */ 
/*     */       
/* 922 */       if (zeroBlockSize > 0L) {
/* 923 */         this.sparseInputStreams.add(new BoundedInputStream(zeroInputStream, sparseHeader.getOffset() - offset));
/*     */       }
/*     */ 
/*     */       
/* 927 */       if (sparseHeader.getNumbytes() > 0L) {
/* 928 */         this.sparseInputStreams.add(new BoundedInputStream(this.inputStream, sparseHeader.getNumbytes()));
/*     */       }
/*     */       
/* 931 */       offset = sparseHeader.getOffset() + sparseHeader.getNumbytes();
/*     */     } 
/*     */     
/* 934 */     if (!this.sparseInputStreams.isEmpty())
/* 935 */       this.currentSparseInputStreamIndex = 0; 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */