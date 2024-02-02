/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.zip.CRC32;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.utils.CountingOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SevenZOutputFile
/*     */   implements Closeable
/*     */ {
/*     */   private final SeekableByteChannel channel;
/*  58 */   private final List<SevenZArchiveEntry> files = new ArrayList<>();
/*     */   private int numNonEmptyStreams;
/*  60 */   private final CRC32 crc32 = new CRC32();
/*  61 */   private final CRC32 compressedCrc32 = new CRC32();
/*     */   
/*     */   private long fileBytesWritten;
/*     */   private boolean finished;
/*     */   private CountingOutputStream currentOutputStream;
/*     */   private CountingOutputStream[] additionalCountingStreams;
/*  67 */   private Iterable<? extends SevenZMethodConfiguration> contentMethods = Collections.singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
/*  68 */   private final Map<SevenZArchiveEntry, long[]> additionalSizes = (Map)new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZOutputFile(File fileName) throws IOException {
/*  77 */     this(Files.newByteChannel(fileName.toPath(), 
/*  78 */           EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), (FileAttribute<?>[])new FileAttribute[0]));
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
/*     */   public SevenZOutputFile(SeekableByteChannel channel) throws IOException {
/*  94 */     this.channel = channel;
/*  95 */     channel.position(32L);
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
/*     */   public void setContentCompression(SevenZMethod method) {
/* 111 */     setContentMethods(Collections.singletonList(new SevenZMethodConfiguration(method)));
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
/*     */   public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods) {
/* 129 */     this.contentMethods = reverse(methods);
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
/* 140 */       if (!this.finished) {
/* 141 */         finish();
/*     */       }
/*     */     } finally {
/* 144 */       this.channel.close();
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
/*     */   public SevenZArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 159 */     SevenZArchiveEntry entry = new SevenZArchiveEntry();
/* 160 */     entry.setDirectory(inputFile.isDirectory());
/* 161 */     entry.setName(entryName);
/* 162 */     entry.setLastModifiedDate(new Date(inputFile.lastModified()));
/* 163 */     return entry;
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
/*     */   public SevenZArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 179 */     SevenZArchiveEntry entry = new SevenZArchiveEntry();
/* 180 */     entry.setDirectory(Files.isDirectory(inputPath, options));
/* 181 */     entry.setName(entryName);
/* 182 */     entry.setLastModifiedDate(new Date(Files.getLastModifiedTime(inputPath, options).toMillis()));
/* 183 */     return entry;
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
/*     */   public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
/* 196 */     SevenZArchiveEntry entry = (SevenZArchiveEntry)archiveEntry;
/* 197 */     this.files.add(entry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeArchiveEntry() throws IOException {
/* 205 */     if (this.currentOutputStream != null) {
/* 206 */       this.currentOutputStream.flush();
/* 207 */       this.currentOutputStream.close();
/*     */     } 
/*     */     
/* 210 */     SevenZArchiveEntry entry = this.files.get(this.files.size() - 1);
/* 211 */     if (this.fileBytesWritten > 0L) {
/* 212 */       entry.setHasStream(true);
/* 213 */       this.numNonEmptyStreams++;
/* 214 */       entry.setSize(this.currentOutputStream.getBytesWritten());
/* 215 */       entry.setCompressedSize(this.fileBytesWritten);
/* 216 */       entry.setCrcValue(this.crc32.getValue());
/* 217 */       entry.setCompressedCrcValue(this.compressedCrc32.getValue());
/* 218 */       entry.setHasCrc(true);
/* 219 */       if (this.additionalCountingStreams != null) {
/* 220 */         long[] sizes = new long[this.additionalCountingStreams.length];
/* 221 */         for (int i = 0; i < this.additionalCountingStreams.length; i++) {
/* 222 */           sizes[i] = this.additionalCountingStreams[i].getBytesWritten();
/*     */         }
/* 224 */         this.additionalSizes.put(entry, sizes);
/*     */       } 
/*     */     } else {
/* 227 */       entry.setHasStream(false);
/* 228 */       entry.setSize(0L);
/* 229 */       entry.setCompressedSize(0L);
/* 230 */       entry.setHasCrc(false);
/*     */     } 
/* 232 */     this.currentOutputStream = null;
/* 233 */     this.additionalCountingStreams = null;
/* 234 */     this.crc32.reset();
/* 235 */     this.compressedCrc32.reset();
/* 236 */     this.fileBytesWritten = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 245 */     getCurrentOutputStream().write(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 254 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 265 */     if (len > 0) {
/* 266 */       getCurrentOutputStream().write(b, off, len);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(InputStream inputStream) throws IOException {
/* 277 */     byte[] buffer = new byte[8024];
/* 278 */     int n = 0;
/* 279 */     while (-1 != (n = inputStream.read(buffer))) {
/* 280 */       write(buffer, 0, n);
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
/*     */   public void write(Path path, OpenOption... options) throws IOException {
/* 292 */     try (InputStream in = new BufferedInputStream(Files.newInputStream(path, options))) {
/* 293 */       write(in);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 303 */     if (this.finished) {
/* 304 */       throw new IOException("This archive has already been finished");
/*     */     }
/* 306 */     this.finished = true;
/*     */     
/* 308 */     long headerPosition = this.channel.position();
/*     */     
/* 310 */     ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
/* 311 */     DataOutputStream header = new DataOutputStream(headerBaos);
/*     */     
/* 313 */     writeHeader(header);
/* 314 */     header.flush();
/* 315 */     byte[] headerBytes = headerBaos.toByteArray();
/* 316 */     this.channel.write(ByteBuffer.wrap(headerBytes));
/*     */     
/* 318 */     CRC32 crc32 = new CRC32();
/* 319 */     crc32.update(headerBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 327 */     ByteBuffer bb = ByteBuffer.allocate(SevenZFile.sevenZSignature.length + 2 + 4 + 8 + 8 + 4).order(ByteOrder.LITTLE_ENDIAN);
/*     */     
/* 329 */     this.channel.position(0L);
/* 330 */     bb.put(SevenZFile.sevenZSignature);
/*     */     
/* 332 */     bb.put((byte)0).put((byte)2);
/*     */ 
/*     */     
/* 335 */     bb.putInt(0);
/*     */ 
/*     */     
/* 338 */     bb.putLong(headerPosition - 32L)
/* 339 */       .putLong(0xFFFFFFFFL & headerBytes.length)
/* 340 */       .putInt((int)crc32.getValue());
/* 341 */     crc32.reset();
/* 342 */     crc32.update(bb.array(), SevenZFile.sevenZSignature.length + 6, 20);
/* 343 */     bb.putInt(SevenZFile.sevenZSignature.length + 2, (int)crc32.getValue());
/* 344 */     bb.flip();
/* 345 */     this.channel.write(bb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private OutputStream getCurrentOutputStream() throws IOException {
/* 354 */     if (this.currentOutputStream == null) {
/* 355 */       this.currentOutputStream = setupFileOutputStream();
/*     */     }
/* 357 */     return (OutputStream)this.currentOutputStream;
/*     */   }
/*     */   
/*     */   private CountingOutputStream setupFileOutputStream() throws IOException {
/* 361 */     if (this.files.isEmpty()) {
/* 362 */       throw new IllegalStateException("No current 7z entry");
/*     */     }
/*     */ 
/*     */     
/* 366 */     OutputStream outputStream = new OutputStreamWrapper();
/* 367 */     ArrayList<CountingOutputStream> moreStreams = new ArrayList<>();
/* 368 */     boolean first = true;
/* 369 */     for (SevenZMethodConfiguration m : getContentMethods(this.files.get(this.files.size() - 1))) {
/* 370 */       CountingOutputStream countingOutputStream; if (!first) {
/* 371 */         CountingOutputStream cos = new CountingOutputStream(outputStream);
/* 372 */         moreStreams.add(cos);
/* 373 */         countingOutputStream = cos;
/*     */       } 
/* 375 */       outputStream = Coders.addEncoder((OutputStream)countingOutputStream, m.getMethod(), m.getOptions());
/* 376 */       first = false;
/*     */     } 
/* 378 */     if (!moreStreams.isEmpty()) {
/* 379 */       this.additionalCountingStreams = moreStreams.<CountingOutputStream>toArray(new CountingOutputStream[0]);
/*     */     }
/* 381 */     return new CountingOutputStream(outputStream)
/*     */       {
/*     */         public void write(int b) throws IOException {
/* 384 */           super.write(b);
/* 385 */           SevenZOutputFile.this.crc32.update(b);
/*     */         }
/*     */ 
/*     */         
/*     */         public void write(byte[] b) throws IOException {
/* 390 */           super.write(b);
/* 391 */           SevenZOutputFile.this.crc32.update(b);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public void write(byte[] b, int off, int len) throws IOException {
/* 397 */           super.write(b, off, len);
/* 398 */           SevenZOutputFile.this.crc32.update(b, off, len);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private Iterable<? extends SevenZMethodConfiguration> getContentMethods(SevenZArchiveEntry entry) {
/* 404 */     Iterable<? extends SevenZMethodConfiguration> ms = entry.getContentMethods();
/* 405 */     return (ms == null) ? this.contentMethods : ms;
/*     */   }
/*     */   
/*     */   private void writeHeader(DataOutput header) throws IOException {
/* 409 */     header.write(1);
/*     */     
/* 411 */     header.write(4);
/* 412 */     writeStreamsInfo(header);
/* 413 */     writeFilesInfo(header);
/* 414 */     header.write(0);
/*     */   }
/*     */   
/*     */   private void writeStreamsInfo(DataOutput header) throws IOException {
/* 418 */     if (this.numNonEmptyStreams > 0) {
/* 419 */       writePackInfo(header);
/* 420 */       writeUnpackInfo(header);
/*     */     } 
/*     */     
/* 423 */     writeSubStreamsInfo(header);
/*     */     
/* 425 */     header.write(0);
/*     */   }
/*     */   
/*     */   private void writePackInfo(DataOutput header) throws IOException {
/* 429 */     header.write(6);
/*     */     
/* 431 */     writeUint64(header, 0L);
/* 432 */     writeUint64(header, 0xFFFFFFFFL & this.numNonEmptyStreams);
/*     */     
/* 434 */     header.write(9);
/* 435 */     for (SevenZArchiveEntry entry : this.files) {
/* 436 */       if (entry.hasStream()) {
/* 437 */         writeUint64(header, entry.getCompressedSize());
/*     */       }
/*     */     } 
/*     */     
/* 441 */     header.write(10);
/* 442 */     header.write(1);
/* 443 */     for (SevenZArchiveEntry entry : this.files) {
/* 444 */       if (entry.hasStream()) {
/* 445 */         header.writeInt(Integer.reverseBytes((int)entry.getCompressedCrcValue()));
/*     */       }
/*     */     } 
/*     */     
/* 449 */     header.write(0);
/*     */   }
/*     */   
/*     */   private void writeUnpackInfo(DataOutput header) throws IOException {
/* 453 */     header.write(7);
/*     */     
/* 455 */     header.write(11);
/* 456 */     writeUint64(header, this.numNonEmptyStreams);
/* 457 */     header.write(0);
/* 458 */     for (SevenZArchiveEntry entry : this.files) {
/* 459 */       if (entry.hasStream()) {
/* 460 */         writeFolder(header, entry);
/*     */       }
/*     */     } 
/*     */     
/* 464 */     header.write(12);
/* 465 */     for (SevenZArchiveEntry entry : this.files) {
/* 466 */       if (entry.hasStream()) {
/* 467 */         long[] moreSizes = this.additionalSizes.get(entry);
/* 468 */         if (moreSizes != null) {
/* 469 */           for (long s : moreSizes) {
/* 470 */             writeUint64(header, s);
/*     */           }
/*     */         }
/* 473 */         writeUint64(header, entry.getSize());
/*     */       } 
/*     */     } 
/*     */     
/* 477 */     header.write(10);
/* 478 */     header.write(1);
/* 479 */     for (SevenZArchiveEntry entry : this.files) {
/* 480 */       if (entry.hasStream()) {
/* 481 */         header.writeInt(Integer.reverseBytes((int)entry.getCrcValue()));
/*     */       }
/*     */     } 
/*     */     
/* 485 */     header.write(0);
/*     */   }
/*     */   
/*     */   private void writeFolder(DataOutput header, SevenZArchiveEntry entry) throws IOException {
/* 489 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 490 */     int numCoders = 0;
/* 491 */     for (SevenZMethodConfiguration m : getContentMethods(entry)) {
/* 492 */       numCoders++;
/* 493 */       writeSingleCodec(m, bos);
/*     */     } 
/*     */     
/* 496 */     writeUint64(header, numCoders);
/* 497 */     header.write(bos.toByteArray()); long i;
/* 498 */     for (i = 0L; i < (numCoders - 1); i++) {
/* 499 */       writeUint64(header, i + 1L);
/* 500 */       writeUint64(header, i);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeSingleCodec(SevenZMethodConfiguration m, OutputStream bos) throws IOException {
/* 505 */     byte[] id = m.getMethod().getId();
/*     */     
/* 507 */     byte[] properties = Coders.findByMethod(m.getMethod()).getOptionsAsProperties(m.getOptions());
/*     */     
/* 509 */     int codecFlags = id.length;
/* 510 */     if (properties.length > 0) {
/* 511 */       codecFlags |= 0x20;
/*     */     }
/* 513 */     bos.write(codecFlags);
/* 514 */     bos.write(id);
/*     */     
/* 516 */     if (properties.length > 0) {
/* 517 */       bos.write(properties.length);
/* 518 */       bos.write(properties);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeSubStreamsInfo(DataOutput header) throws IOException {
/* 523 */     header.write(8);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 533 */     header.write(0);
/*     */   }
/*     */   
/*     */   private void writeFilesInfo(DataOutput header) throws IOException {
/* 537 */     header.write(5);
/*     */     
/* 539 */     writeUint64(header, this.files.size());
/*     */     
/* 541 */     writeFileEmptyStreams(header);
/* 542 */     writeFileEmptyFiles(header);
/* 543 */     writeFileAntiItems(header);
/* 544 */     writeFileNames(header);
/* 545 */     writeFileCTimes(header);
/* 546 */     writeFileATimes(header);
/* 547 */     writeFileMTimes(header);
/* 548 */     writeFileWindowsAttributes(header);
/* 549 */     header.write(0);
/*     */   }
/*     */   
/*     */   private void writeFileEmptyStreams(DataOutput header) throws IOException {
/* 553 */     boolean hasEmptyStreams = false;
/* 554 */     for (SevenZArchiveEntry entry : this.files) {
/* 555 */       if (!entry.hasStream()) {
/* 556 */         hasEmptyStreams = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 560 */     if (hasEmptyStreams) {
/* 561 */       header.write(14);
/* 562 */       BitSet emptyStreams = new BitSet(this.files.size());
/* 563 */       for (int i = 0; i < this.files.size(); i++) {
/* 564 */         emptyStreams.set(i, !((SevenZArchiveEntry)this.files.get(i)).hasStream());
/*     */       }
/* 566 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 567 */       DataOutputStream out = new DataOutputStream(baos);
/* 568 */       writeBits(out, emptyStreams, this.files.size());
/* 569 */       out.flush();
/* 570 */       byte[] contents = baos.toByteArray();
/* 571 */       writeUint64(header, contents.length);
/* 572 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   private void writeFileEmptyFiles(DataOutput header) throws IOException {
/*     */     int i;
/* 577 */     boolean hasEmptyFiles = false;
/* 578 */     int emptyStreamCounter = 0;
/* 579 */     BitSet emptyFiles = new BitSet(0);
/* 580 */     for (SevenZArchiveEntry file1 : this.files) {
/* 581 */       if (!file1.hasStream()) {
/* 582 */         boolean isDir = file1.isDirectory();
/* 583 */         emptyFiles.set(emptyStreamCounter++, !isDir);
/* 584 */         i = hasEmptyFiles | (!isDir ? 1 : 0);
/*     */       } 
/*     */     } 
/* 587 */     if (i != 0) {
/* 588 */       header.write(15);
/* 589 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 590 */       DataOutputStream out = new DataOutputStream(baos);
/* 591 */       writeBits(out, emptyFiles, emptyStreamCounter);
/* 592 */       out.flush();
/* 593 */       byte[] contents = baos.toByteArray();
/* 594 */       writeUint64(header, contents.length);
/* 595 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFileAntiItems(DataOutput header) throws IOException {
/* 600 */     boolean hasAntiItems = false;
/* 601 */     BitSet antiItems = new BitSet(0);
/* 602 */     int antiItemCounter = 0;
/* 603 */     for (SevenZArchiveEntry file1 : this.files) {
/* 604 */       if (!file1.hasStream()) {
/* 605 */         boolean isAnti = file1.isAntiItem();
/* 606 */         antiItems.set(antiItemCounter++, isAnti);
/* 607 */         hasAntiItems |= isAnti;
/*     */       } 
/*     */     } 
/* 610 */     if (hasAntiItems) {
/* 611 */       header.write(16);
/* 612 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 613 */       DataOutputStream out = new DataOutputStream(baos);
/* 614 */       writeBits(out, antiItems, antiItemCounter);
/* 615 */       out.flush();
/* 616 */       byte[] contents = baos.toByteArray();
/* 617 */       writeUint64(header, contents.length);
/* 618 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFileNames(DataOutput header) throws IOException {
/* 623 */     header.write(17);
/*     */     
/* 625 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 626 */     DataOutputStream out = new DataOutputStream(baos);
/* 627 */     out.write(0);
/* 628 */     for (SevenZArchiveEntry entry : this.files) {
/* 629 */       out.write(entry.getName().getBytes(StandardCharsets.UTF_16LE));
/* 630 */       out.writeShort(0);
/*     */     } 
/* 632 */     out.flush();
/* 633 */     byte[] contents = baos.toByteArray();
/* 634 */     writeUint64(header, contents.length);
/* 635 */     header.write(contents);
/*     */   }
/*     */   
/*     */   private void writeFileCTimes(DataOutput header) throws IOException {
/* 639 */     int numCreationDates = 0;
/* 640 */     for (SevenZArchiveEntry entry : this.files) {
/* 641 */       if (entry.getHasCreationDate()) {
/* 642 */         numCreationDates++;
/*     */       }
/*     */     } 
/* 645 */     if (numCreationDates > 0) {
/* 646 */       header.write(18);
/*     */       
/* 648 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 649 */       DataOutputStream out = new DataOutputStream(baos);
/* 650 */       if (numCreationDates != this.files.size()) {
/* 651 */         out.write(0);
/* 652 */         BitSet cTimes = new BitSet(this.files.size());
/* 653 */         for (int i = 0; i < this.files.size(); i++) {
/* 654 */           cTimes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasCreationDate());
/*     */         }
/* 656 */         writeBits(out, cTimes, this.files.size());
/*     */       } else {
/* 658 */         out.write(1);
/*     */       } 
/* 660 */       out.write(0);
/* 661 */       for (SevenZArchiveEntry entry : this.files) {
/* 662 */         if (entry.getHasCreationDate()) {
/* 663 */           out.writeLong(Long.reverseBytes(
/* 664 */                 SevenZArchiveEntry.javaTimeToNtfsTime(entry.getCreationDate())));
/*     */         }
/*     */       } 
/* 667 */       out.flush();
/* 668 */       byte[] contents = baos.toByteArray();
/* 669 */       writeUint64(header, contents.length);
/* 670 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFileATimes(DataOutput header) throws IOException {
/* 675 */     int numAccessDates = 0;
/* 676 */     for (SevenZArchiveEntry entry : this.files) {
/* 677 */       if (entry.getHasAccessDate()) {
/* 678 */         numAccessDates++;
/*     */       }
/*     */     } 
/* 681 */     if (numAccessDates > 0) {
/* 682 */       header.write(19);
/*     */       
/* 684 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 685 */       DataOutputStream out = new DataOutputStream(baos);
/* 686 */       if (numAccessDates != this.files.size()) {
/* 687 */         out.write(0);
/* 688 */         BitSet aTimes = new BitSet(this.files.size());
/* 689 */         for (int i = 0; i < this.files.size(); i++) {
/* 690 */           aTimes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasAccessDate());
/*     */         }
/* 692 */         writeBits(out, aTimes, this.files.size());
/*     */       } else {
/* 694 */         out.write(1);
/*     */       } 
/* 696 */       out.write(0);
/* 697 */       for (SevenZArchiveEntry entry : this.files) {
/* 698 */         if (entry.getHasAccessDate()) {
/* 699 */           out.writeLong(Long.reverseBytes(
/* 700 */                 SevenZArchiveEntry.javaTimeToNtfsTime(entry.getAccessDate())));
/*     */         }
/*     */       } 
/* 703 */       out.flush();
/* 704 */       byte[] contents = baos.toByteArray();
/* 705 */       writeUint64(header, contents.length);
/* 706 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFileMTimes(DataOutput header) throws IOException {
/* 711 */     int numLastModifiedDates = 0;
/* 712 */     for (SevenZArchiveEntry entry : this.files) {
/* 713 */       if (entry.getHasLastModifiedDate()) {
/* 714 */         numLastModifiedDates++;
/*     */       }
/*     */     } 
/* 717 */     if (numLastModifiedDates > 0) {
/* 718 */       header.write(20);
/*     */       
/* 720 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 721 */       DataOutputStream out = new DataOutputStream(baos);
/* 722 */       if (numLastModifiedDates != this.files.size()) {
/* 723 */         out.write(0);
/* 724 */         BitSet mTimes = new BitSet(this.files.size());
/* 725 */         for (int i = 0; i < this.files.size(); i++) {
/* 726 */           mTimes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasLastModifiedDate());
/*     */         }
/* 728 */         writeBits(out, mTimes, this.files.size());
/*     */       } else {
/* 730 */         out.write(1);
/*     */       } 
/* 732 */       out.write(0);
/* 733 */       for (SevenZArchiveEntry entry : this.files) {
/* 734 */         if (entry.getHasLastModifiedDate()) {
/* 735 */           out.writeLong(Long.reverseBytes(
/* 736 */                 SevenZArchiveEntry.javaTimeToNtfsTime(entry.getLastModifiedDate())));
/*     */         }
/*     */       } 
/* 739 */       out.flush();
/* 740 */       byte[] contents = baos.toByteArray();
/* 741 */       writeUint64(header, contents.length);
/* 742 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeFileWindowsAttributes(DataOutput header) throws IOException {
/* 747 */     int numWindowsAttributes = 0;
/* 748 */     for (SevenZArchiveEntry entry : this.files) {
/* 749 */       if (entry.getHasWindowsAttributes()) {
/* 750 */         numWindowsAttributes++;
/*     */       }
/*     */     } 
/* 753 */     if (numWindowsAttributes > 0) {
/* 754 */       header.write(21);
/*     */       
/* 756 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 757 */       DataOutputStream out = new DataOutputStream(baos);
/* 758 */       if (numWindowsAttributes != this.files.size()) {
/* 759 */         out.write(0);
/* 760 */         BitSet attributes = new BitSet(this.files.size());
/* 761 */         for (int i = 0; i < this.files.size(); i++) {
/* 762 */           attributes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasWindowsAttributes());
/*     */         }
/* 764 */         writeBits(out, attributes, this.files.size());
/*     */       } else {
/* 766 */         out.write(1);
/*     */       } 
/* 768 */       out.write(0);
/* 769 */       for (SevenZArchiveEntry entry : this.files) {
/* 770 */         if (entry.getHasWindowsAttributes()) {
/* 771 */           out.writeInt(Integer.reverseBytes(entry.getWindowsAttributes()));
/*     */         }
/*     */       } 
/* 774 */       out.flush();
/* 775 */       byte[] contents = baos.toByteArray();
/* 776 */       writeUint64(header, contents.length);
/* 777 */       header.write(contents);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeUint64(DataOutput header, long value) throws IOException {
/* 782 */     int firstByte = 0;
/* 783 */     int mask = 128;
/*     */     int i;
/* 785 */     for (i = 0; i < 8; i++) {
/* 786 */       if (value < 1L << 7 * (i + 1)) {
/* 787 */         firstByte = (int)(firstByte | value >>> 8 * i);
/*     */         break;
/*     */       } 
/* 790 */       firstByte |= mask;
/* 791 */       mask >>>= 1;
/*     */     } 
/* 793 */     header.write(firstByte);
/* 794 */     for (; i > 0; i--) {
/* 795 */       header.write((int)(0xFFL & value));
/* 796 */       value >>>= 8L;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeBits(DataOutput header, BitSet bits, int length) throws IOException {
/* 801 */     int cache = 0;
/* 802 */     int shift = 7;
/* 803 */     for (int i = 0; i < length; i++) {
/* 804 */       cache |= (bits.get(i) ? 1 : 0) << shift;
/* 805 */       if (--shift < 0) {
/* 806 */         header.write(cache);
/* 807 */         shift = 7;
/* 808 */         cache = 0;
/*     */       } 
/*     */     } 
/* 811 */     if (shift != 7) {
/* 812 */       header.write(cache);
/*     */     }
/*     */   }
/*     */   
/*     */   private static <T> Iterable<T> reverse(Iterable<T> i) {
/* 817 */     LinkedList<T> l = new LinkedList<>();
/* 818 */     for (T t : i) {
/* 819 */       l.addFirst(t);
/*     */     }
/* 821 */     return l;
/*     */   }
/*     */   
/*     */   private class OutputStreamWrapper extends OutputStream {
/*     */     private static final int BUF_SIZE = 8192;
/* 826 */     private final ByteBuffer buffer = ByteBuffer.allocate(8192);
/*     */     
/*     */     public void write(int b) throws IOException {
/* 829 */       this.buffer.clear();
/* 830 */       this.buffer.put((byte)b).flip();
/* 831 */       SevenZOutputFile.this.channel.write(this.buffer);
/* 832 */       SevenZOutputFile.this.compressedCrc32.update(b);
/* 833 */       SevenZOutputFile.this.fileBytesWritten++;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(byte[] b) throws IOException {
/* 838 */       write(b, 0, b.length);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 844 */       if (len > 8192) {
/* 845 */         SevenZOutputFile.this.channel.write(ByteBuffer.wrap(b, off, len));
/*     */       } else {
/* 847 */         this.buffer.clear();
/* 848 */         this.buffer.put(b, off, len).flip();
/* 849 */         SevenZOutputFile.this.channel.write(this.buffer);
/*     */       } 
/* 851 */       SevenZOutputFile.this.compressedCrc32.update(b, off, len);
/* 852 */       SevenZOutputFile.this.fileBytesWritten = SevenZOutputFile.this.fileBytesWritten + len;
/*     */     }
/*     */     
/*     */     public void flush() throws IOException {}
/*     */     
/*     */     public void close() throws IOException {}
/*     */     
/*     */     private OutputStreamWrapper() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\SevenZOutputFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */