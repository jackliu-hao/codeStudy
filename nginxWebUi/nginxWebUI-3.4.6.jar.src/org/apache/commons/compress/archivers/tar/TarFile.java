/*     */ package org.apache.commons.compress.archivers.tar;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.ArchiveUtils;
/*     */ import org.apache.commons.compress.utils.BoundedArchiveInputStream;
/*     */ import org.apache.commons.compress.utils.BoundedInputStream;
/*     */ import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
/*     */ import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TarFile
/*     */   implements Closeable
/*     */ {
/*     */   private static final int SMALL_BUFFER_SIZE = 256;
/*  51 */   private final byte[] smallBuf = new byte[256];
/*     */ 
/*     */   
/*     */   private final SeekableByteChannel archive;
/*     */ 
/*     */   
/*     */   private final ZipEncoding zipEncoding;
/*     */ 
/*     */   
/*  60 */   private final LinkedList<TarArchiveEntry> entries = new LinkedList<>();
/*     */ 
/*     */   
/*     */   private final int blockSize;
/*     */   
/*     */   private final boolean lenient;
/*     */   
/*     */   private final int recordSize;
/*     */   
/*     */   private final ByteBuffer recordBuffer;
/*     */   
/*  71 */   private final List<TarArchiveStructSparse> globalSparseHeaders = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasHitEOF;
/*     */ 
/*     */   
/*     */   private TarArchiveEntry currEntry;
/*     */ 
/*     */   
/*  81 */   private Map<String, String> globalPaxHeaders = new HashMap<>();
/*     */   
/*  83 */   private final Map<String, List<InputStream>> sparseInputStreams = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(byte[] content) throws IOException {
/*  92 */     this((SeekableByteChannel)new SeekableInMemoryByteChannel(content));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(byte[] content, String encoding) throws IOException {
/* 103 */     this((SeekableByteChannel)new SeekableInMemoryByteChannel(content), 10240, 512, encoding, false);
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
/*     */   public TarFile(byte[] content, boolean lenient) throws IOException {
/* 116 */     this((SeekableByteChannel)new SeekableInMemoryByteChannel(content), 10240, 512, null, lenient);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(File archive) throws IOException {
/* 126 */     this(archive.toPath());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(File archive, String encoding) throws IOException {
/* 137 */     this(archive.toPath(), encoding);
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
/*     */   public TarFile(File archive, boolean lenient) throws IOException {
/* 150 */     this(archive.toPath(), lenient);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(Path archivePath) throws IOException {
/* 160 */     this(Files.newByteChannel(archivePath, new java.nio.file.OpenOption[0]), 10240, 512, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(Path archivePath, String encoding) throws IOException {
/* 171 */     this(Files.newByteChannel(archivePath, new java.nio.file.OpenOption[0]), 10240, 512, encoding, false);
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
/*     */   public TarFile(Path archivePath, boolean lenient) throws IOException {
/* 184 */     this(Files.newByteChannel(archivePath, new java.nio.file.OpenOption[0]), 10240, 512, null, lenient);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TarFile(SeekableByteChannel content) throws IOException {
/* 194 */     this(content, 10240, 512, null, false);
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
/*     */   public TarFile(SeekableByteChannel archive, int blockSize, int recordSize, String encoding, boolean lenient) throws IOException {
/* 210 */     this.archive = archive;
/* 211 */     this.hasHitEOF = false;
/* 212 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/* 213 */     this.recordSize = recordSize;
/* 214 */     this.recordBuffer = ByteBuffer.allocate(this.recordSize);
/* 215 */     this.blockSize = blockSize;
/* 216 */     this.lenient = lenient;
/*     */     
/*     */     TarArchiveEntry entry;
/* 219 */     while ((entry = getNextTarEntry()) != null) {
/* 220 */       this.entries.add(entry);
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
/*     */   private TarArchiveEntry getNextTarEntry() throws IOException {
/* 238 */     if (isAtEOF()) {
/* 239 */       return null;
/*     */     }
/*     */     
/* 242 */     if (this.currEntry != null) {
/*     */       
/* 244 */       repositionForwardTo(this.currEntry.getDataOffset() + this.currEntry.getSize());
/* 245 */       throwExceptionIfPositionIsNotInArchive();
/* 246 */       skipRecordPadding();
/*     */     } 
/*     */     
/* 249 */     ByteBuffer headerBuf = getRecord();
/* 250 */     if (null == headerBuf) {
/*     */       
/* 252 */       this.currEntry = null;
/* 253 */       return null;
/*     */     } 
/*     */     
/*     */     try {
/* 257 */       this.currEntry = new TarArchiveEntry(headerBuf.array(), this.zipEncoding, this.lenient, this.archive.position());
/* 258 */     } catch (IllegalArgumentException e) {
/* 259 */       throw new IOException("Error detected parsing the header", e);
/*     */     } 
/*     */     
/* 262 */     if (this.currEntry.isGNULongLinkEntry()) {
/* 263 */       byte[] longLinkData = getLongNameData();
/* 264 */       if (longLinkData == null)
/*     */       {
/*     */ 
/*     */         
/* 268 */         return null;
/*     */       }
/* 270 */       this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
/*     */     } 
/*     */     
/* 273 */     if (this.currEntry.isGNULongNameEntry()) {
/* 274 */       byte[] longNameData = getLongNameData();
/* 275 */       if (longNameData == null)
/*     */       {
/*     */ 
/*     */         
/* 279 */         return null;
/*     */       }
/*     */ 
/*     */       
/* 283 */       String name = this.zipEncoding.decode(longNameData);
/* 284 */       this.currEntry.setName(name);
/* 285 */       if (this.currEntry.isDirectory() && !name.endsWith("/")) {
/* 286 */         this.currEntry.setName(name + "/");
/*     */       }
/*     */     } 
/*     */     
/* 290 */     if (this.currEntry.isGlobalPaxHeader()) {
/* 291 */       readGlobalPaxHeaders();
/*     */     }
/*     */     
/*     */     try {
/* 295 */       if (this.currEntry.isPaxHeader()) {
/* 296 */         paxHeaders();
/* 297 */       } else if (!this.globalPaxHeaders.isEmpty()) {
/* 298 */         applyPaxHeadersToCurrentEntry(this.globalPaxHeaders, this.globalSparseHeaders);
/*     */       } 
/* 300 */     } catch (NumberFormatException e) {
/* 301 */       throw new IOException("Error detected parsing the pax header", e);
/*     */     } 
/*     */     
/* 304 */     if (this.currEntry.isOldGNUSparse()) {
/* 305 */       readOldGNUSparse();
/*     */     }
/*     */     
/* 308 */     return this.currEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readOldGNUSparse() throws IOException {
/* 318 */     if (this.currEntry.isExtended()) {
/*     */       TarArchiveSparseEntry entry;
/*     */       do {
/* 321 */         ByteBuffer headerBuf = getRecord();
/* 322 */         if (headerBuf == null) {
/* 323 */           throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
/*     */         }
/* 325 */         entry = new TarArchiveSparseEntry(headerBuf.array());
/* 326 */         this.currEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
/* 327 */         this.currEntry.setDataOffset(this.currEntry.getDataOffset() + this.recordSize);
/* 328 */       } while (entry.isExtended());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 333 */     buildSparseInputStreams();
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
/*     */   private void buildSparseInputStreams() throws IOException {
/* 345 */     List<InputStream> streams = new ArrayList<>();
/*     */     
/* 347 */     List<TarArchiveStructSparse> sparseHeaders = this.currEntry.getOrderedSparseHeaders();
/*     */ 
/*     */     
/* 350 */     InputStream zeroInputStream = new TarArchiveSparseZeroInputStream();
/*     */     
/* 352 */     long offset = 0L;
/* 353 */     long numberOfZeroBytesInSparseEntry = 0L;
/* 354 */     for (TarArchiveStructSparse sparseHeader : sparseHeaders) {
/* 355 */       long zeroBlockSize = sparseHeader.getOffset() - offset;
/* 356 */       if (zeroBlockSize < 0L)
/*     */       {
/* 358 */         throw new IOException("Corrupted struct sparse detected");
/*     */       }
/*     */ 
/*     */       
/* 362 */       if (zeroBlockSize > 0L) {
/* 363 */         streams.add(new BoundedInputStream(zeroInputStream, zeroBlockSize));
/* 364 */         numberOfZeroBytesInSparseEntry += zeroBlockSize;
/*     */       } 
/*     */ 
/*     */       
/* 368 */       if (sparseHeader.getNumbytes() > 0L) {
/*     */         
/* 370 */         long start = this.currEntry.getDataOffset() + sparseHeader.getOffset() - numberOfZeroBytesInSparseEntry;
/* 371 */         if (start + sparseHeader.getNumbytes() < start)
/*     */         {
/* 373 */           throw new IOException("Unreadable TAR archive, sparse block offset or length too big");
/*     */         }
/* 375 */         streams.add(new BoundedSeekableByteChannelInputStream(start, sparseHeader.getNumbytes(), this.archive));
/*     */       } 
/*     */       
/* 378 */       offset = sparseHeader.getOffset() + sparseHeader.getNumbytes();
/*     */     } 
/*     */     
/* 381 */     this.sparseInputStreams.put(this.currEntry.getName(), streams);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void applyPaxHeadersToCurrentEntry(Map<String, String> headers, List<TarArchiveStructSparse> sparseHeaders) throws IOException {
/* 391 */     this.currEntry.updateEntryFromPaxHeaders(headers);
/* 392 */     this.currEntry.setSparseHeaders(sparseHeaders);
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
/*     */ 
/*     */ 
/*     */   
/*     */   private void paxHeaders() throws IOException {
/*     */     Map<String, String> headers;
/* 425 */     List<TarArchiveStructSparse> sparseHeaders = new ArrayList<>();
/*     */     
/* 427 */     try (InputStream input = getInputStream(this.currEntry)) {
/* 428 */       headers = TarUtils.parsePaxHeaders(input, sparseHeaders, this.globalPaxHeaders, this.currEntry.getSize());
/*     */     } 
/*     */ 
/*     */     
/* 432 */     if (headers.containsKey("GNU.sparse.map")) {
/* 433 */       sparseHeaders = new ArrayList<>(TarUtils.parseFromPAX01SparseHeaders(headers.get("GNU.sparse.map")));
/*     */     }
/* 435 */     getNextTarEntry();
/* 436 */     if (this.currEntry == null) {
/* 437 */       throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
/*     */     }
/* 439 */     applyPaxHeadersToCurrentEntry(headers, sparseHeaders);
/*     */ 
/*     */     
/* 442 */     if (this.currEntry.isPaxGNU1XSparse()) {
/* 443 */       try (InputStream input = getInputStream(this.currEntry)) {
/* 444 */         sparseHeaders = TarUtils.parsePAX1XSparseHeaders(input, this.recordSize);
/*     */       } 
/* 446 */       this.currEntry.setSparseHeaders(sparseHeaders);
/*     */       
/* 448 */       this.currEntry.setDataOffset(this.currEntry.getDataOffset() + this.recordSize);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 453 */     buildSparseInputStreams();
/*     */   }
/*     */   
/*     */   private void readGlobalPaxHeaders() throws IOException {
/* 457 */     try (InputStream input = getInputStream(this.currEntry)) {
/* 458 */       this.globalPaxHeaders = TarUtils.parsePaxHeaders(input, this.globalSparseHeaders, this.globalPaxHeaders, this.currEntry
/* 459 */           .getSize());
/*     */     } 
/* 461 */     getNextTarEntry();
/*     */     
/* 463 */     if (this.currEntry == null) {
/* 464 */       throw new IOException("Error detected parsing the pax header");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] getLongNameData() throws IOException {
/* 475 */     ByteArrayOutputStream longName = new ByteArrayOutputStream();
/*     */     
/* 477 */     try (InputStream in = getInputStream(this.currEntry)) {
/* 478 */       int i; while ((i = in.read(this.smallBuf)) >= 0) {
/* 479 */         longName.write(this.smallBuf, 0, i);
/*     */       }
/*     */     } 
/* 482 */     getNextTarEntry();
/* 483 */     if (this.currEntry == null)
/*     */     {
/*     */       
/* 486 */       return null;
/*     */     }
/* 488 */     byte[] longNameData = longName.toByteArray();
/*     */     
/* 490 */     int length = longNameData.length;
/* 491 */     while (length > 0 && longNameData[length - 1] == 0) {
/* 492 */       length--;
/*     */     }
/* 494 */     if (length != longNameData.length) {
/* 495 */       byte[] l = new byte[length];
/* 496 */       System.arraycopy(longNameData, 0, l, 0, length);
/* 497 */       longNameData = l;
/*     */     } 
/* 499 */     return longNameData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void skipRecordPadding() throws IOException {
/* 509 */     if (!isDirectory() && this.currEntry.getSize() > 0L && this.currEntry.getSize() % this.recordSize != 0L) {
/* 510 */       long numRecords = this.currEntry.getSize() / this.recordSize + 1L;
/* 511 */       long padding = numRecords * this.recordSize - this.currEntry.getSize();
/* 512 */       repositionForwardBy(padding);
/* 513 */       throwExceptionIfPositionIsNotInArchive();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void repositionForwardTo(long newPosition) throws IOException {
/* 518 */     long currPosition = this.archive.position();
/* 519 */     if (newPosition < currPosition) {
/* 520 */       throw new IOException("trying to move backwards inside of the archive");
/*     */     }
/* 522 */     this.archive.position(newPosition);
/*     */   }
/*     */   
/*     */   private void repositionForwardBy(long offset) throws IOException {
/* 526 */     repositionForwardTo(this.archive.position() + offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void throwExceptionIfPositionIsNotInArchive() throws IOException {
/* 534 */     if (this.archive.size() < this.archive.position()) {
/* 535 */       throw new IOException("Truncated TAR archive");
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
/*     */   private ByteBuffer getRecord() throws IOException {
/* 554 */     ByteBuffer headerBuf = readRecord();
/* 555 */     setAtEOF(isEOFRecord(headerBuf));
/* 556 */     if (isAtEOF() && headerBuf != null) {
/*     */       
/* 558 */       tryToConsumeSecondEOFRecord();
/* 559 */       consumeRemainderOfLastBlock();
/* 560 */       headerBuf = null;
/*     */     } 
/* 562 */     return headerBuf;
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
/*     */   private void tryToConsumeSecondEOFRecord() throws IOException {
/* 579 */     boolean shouldReset = true;
/*     */     try {
/* 581 */       shouldReset = !isEOFRecord(readRecord());
/*     */     } finally {
/* 583 */       if (shouldReset) {
/* 584 */         this.archive.position(this.archive.position() - this.recordSize);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void consumeRemainderOfLastBlock() throws IOException {
/* 595 */     long bytesReadOfLastBlock = this.archive.position() % this.blockSize;
/* 596 */     if (bytesReadOfLastBlock > 0L) {
/* 597 */       repositionForwardBy(this.blockSize - bytesReadOfLastBlock);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBuffer readRecord() throws IOException {
/* 608 */     this.recordBuffer.rewind();
/* 609 */     int readNow = this.archive.read(this.recordBuffer);
/* 610 */     if (readNow != this.recordSize) {
/* 611 */       return null;
/*     */     }
/* 613 */     return this.recordBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<TarArchiveEntry> getEntries() {
/* 622 */     return new ArrayList<>(this.entries);
/*     */   }
/*     */   
/*     */   private boolean isEOFRecord(ByteBuffer headerBuf) {
/* 626 */     return (headerBuf == null || ArchiveUtils.isArrayZero(headerBuf.array(), this.recordSize));
/*     */   }
/*     */   
/*     */   protected final boolean isAtEOF() {
/* 630 */     return this.hasHitEOF;
/*     */   }
/*     */   
/*     */   protected final void setAtEOF(boolean b) {
/* 634 */     this.hasHitEOF = b;
/*     */   }
/*     */   
/*     */   private boolean isDirectory() {
/* 638 */     return (this.currEntry != null && this.currEntry.isDirectory());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream(TarArchiveEntry entry) throws IOException {
/*     */     try {
/* 649 */       return (InputStream)new BoundedTarEntryInputStream(entry, this.archive);
/* 650 */     } catch (RuntimeException ex) {
/* 651 */       throw new IOException("Corrupted TAR archive. Can't read entry", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 657 */     this.archive.close();
/*     */   }
/*     */ 
/*     */   
/*     */   private final class BoundedTarEntryInputStream
/*     */     extends BoundedArchiveInputStream
/*     */   {
/*     */     private final SeekableByteChannel channel;
/*     */     
/*     */     private final TarArchiveEntry entry;
/*     */     private long entryOffset;
/*     */     private int currentSparseInputStreamIndex;
/*     */     
/*     */     BoundedTarEntryInputStream(TarArchiveEntry entry, SeekableByteChannel channel) throws IOException {
/* 671 */       super(entry.getDataOffset(), entry.getRealSize());
/* 672 */       if (channel.size() - entry.getSize() < entry.getDataOffset()) {
/* 673 */         throw new IOException("entry size exceeds archive size");
/*     */       }
/* 675 */       this.entry = entry;
/* 676 */       this.channel = channel;
/*     */     }
/*     */     
/*     */     protected int read(long pos, ByteBuffer buf) throws IOException {
/*     */       int totalRead;
/* 681 */       if (this.entryOffset >= this.entry.getRealSize()) {
/* 682 */         return -1;
/*     */       }
/*     */ 
/*     */       
/* 686 */       if (this.entry.isSparse()) {
/* 687 */         totalRead = readSparse(this.entryOffset, buf, buf.limit());
/*     */       } else {
/* 689 */         totalRead = readArchive(pos, buf);
/*     */       } 
/*     */       
/* 692 */       if (totalRead == -1) {
/* 693 */         if ((buf.array()).length > 0) {
/* 694 */           throw new IOException("Truncated TAR archive");
/*     */         }
/* 696 */         TarFile.this.setAtEOF(true);
/*     */       } else {
/* 698 */         this.entryOffset += totalRead;
/* 699 */         buf.flip();
/*     */       } 
/* 701 */       return totalRead;
/*     */     }
/*     */ 
/*     */     
/*     */     private int readSparse(long pos, ByteBuffer buf, int numToRead) throws IOException {
/* 706 */       List<InputStream> entrySparseInputStreams = (List<InputStream>)TarFile.this.sparseInputStreams.get(this.entry.getName());
/* 707 */       if (entrySparseInputStreams == null || entrySparseInputStreams.isEmpty()) {
/* 708 */         return readArchive(this.entry.getDataOffset() + pos, buf);
/*     */       }
/*     */       
/* 711 */       if (this.currentSparseInputStreamIndex >= entrySparseInputStreams.size()) {
/* 712 */         return -1;
/*     */       }
/*     */       
/* 715 */       InputStream currentInputStream = entrySparseInputStreams.get(this.currentSparseInputStreamIndex);
/* 716 */       byte[] bufArray = new byte[numToRead];
/* 717 */       int readLen = currentInputStream.read(bufArray);
/* 718 */       if (readLen != -1) {
/* 719 */         buf.put(bufArray, 0, readLen);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 724 */       if (this.currentSparseInputStreamIndex == entrySparseInputStreams.size() - 1) {
/* 725 */         return readLen;
/*     */       }
/*     */ 
/*     */       
/* 729 */       if (readLen == -1) {
/* 730 */         this.currentSparseInputStreamIndex++;
/* 731 */         return readSparse(pos, buf, numToRead);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 736 */       if (readLen < numToRead) {
/* 737 */         this.currentSparseInputStreamIndex++;
/* 738 */         int readLenOfNext = readSparse(pos + readLen, buf, numToRead - readLen);
/* 739 */         if (readLenOfNext == -1) {
/* 740 */           return readLen;
/*     */         }
/*     */         
/* 743 */         return readLen + readLenOfNext;
/*     */       } 
/*     */ 
/*     */       
/* 747 */       return readLen;
/*     */     }
/*     */     
/*     */     private int readArchive(long pos, ByteBuffer buf) throws IOException {
/* 751 */       this.channel.position(pos);
/* 752 */       return this.channel.read(buf);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */