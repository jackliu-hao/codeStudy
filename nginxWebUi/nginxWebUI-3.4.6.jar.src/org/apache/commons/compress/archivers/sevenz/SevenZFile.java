/*      */ package org.apache.commons.compress.archivers.sevenz;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.File;
/*      */ import java.io.FilterInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.channels.SeekableByteChannel;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.StandardOpenOption;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.stream.Collectors;
/*      */ import java.util.zip.CRC32;
/*      */ import org.apache.commons.compress.MemoryLimitException;
/*      */ import org.apache.commons.compress.utils.BoundedInputStream;
/*      */ import org.apache.commons.compress.utils.ByteUtils;
/*      */ import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
/*      */ import org.apache.commons.compress.utils.IOUtils;
/*      */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SevenZFile
/*      */   implements Closeable
/*      */ {
/*      */   static final int SIGNATURE_HEADER_SIZE = 32;
/*      */   private static final String DEFAULT_FILE_NAME = "unknown archive";
/*      */   private final String fileName;
/*      */   private SeekableByteChannel channel;
/*      */   private final Archive archive;
/*   97 */   private int currentEntryIndex = -1;
/*   98 */   private int currentFolderIndex = -1;
/*      */   
/*      */   private InputStream currentFolderInputStream;
/*      */   
/*      */   private byte[] password;
/*      */   private final SevenZFileOptions options;
/*      */   private long compressedBytesReadFromCurrentEntry;
/*      */   private long uncompressedBytesReadFromCurrentEntry;
/*  106 */   private final ArrayList<InputStream> deferredBlockStreams = new ArrayList<>();
/*      */ 
/*      */   
/*  109 */   static final byte[] sevenZSignature = new byte[] { 55, 122, -68, -81, 39, 28 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(File fileName, char[] password) throws IOException {
/*  122 */     this(fileName, password, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(File fileName, char[] password, SevenZFileOptions options) throws IOException {
/*  135 */     this(Files.newByteChannel(fileName.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), fileName
/*  136 */         .getAbsolutePath(), utf16Decode(password), true, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public SevenZFile(File fileName, byte[] password) throws IOException {
/*  151 */     this(Files.newByteChannel(fileName.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), fileName
/*  152 */         .getAbsolutePath(), password, true, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel) throws IOException {
/*  167 */     this(channel, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, SevenZFileOptions options) throws IOException {
/*  183 */     this(channel, "unknown archive", null, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, char[] password) throws IOException {
/*  200 */     this(channel, password, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, char[] password, SevenZFileOptions options) throws IOException {
/*  218 */     this(channel, "unknown archive", password, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, String fileName, char[] password) throws IOException {
/*  236 */     this(channel, fileName, password, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, String fileName, char[] password, SevenZFileOptions options) throws IOException {
/*  255 */     this(channel, fileName, utf16Decode(password), false, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, String fileName) throws IOException {
/*  272 */     this(channel, fileName, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(SeekableByteChannel channel, String fileName, SevenZFileOptions options) throws IOException {
/*  290 */     this(channel, fileName, null, false, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public SevenZFile(SeekableByteChannel channel, byte[] password) throws IOException {
/*  311 */     this(channel, "unknown archive", password);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public SevenZFile(SeekableByteChannel channel, String fileName, byte[] password) throws IOException {
/*  333 */     this(channel, fileName, password, false, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */   
/*      */   private SevenZFile(SeekableByteChannel channel, String filename, byte[] password, boolean closeOnError, SevenZFileOptions options) throws IOException {
/*  338 */     boolean succeeded = false;
/*  339 */     this.channel = channel;
/*  340 */     this.fileName = filename;
/*  341 */     this.options = options;
/*      */     try {
/*  343 */       this.archive = readHeaders(password);
/*  344 */       if (password != null) {
/*  345 */         this.password = Arrays.copyOf(password, password.length);
/*      */       } else {
/*  347 */         this.password = null;
/*      */       } 
/*  349 */       succeeded = true;
/*      */     } finally {
/*  351 */       if (!succeeded && closeOnError) {
/*  352 */         this.channel.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(File fileName) throws IOException {
/*  364 */     this(fileName, SevenZFileOptions.DEFAULT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZFile(File fileName, SevenZFileOptions options) throws IOException {
/*  376 */     this(fileName, (char[])null, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  385 */     if (this.channel != null) {
/*      */       try {
/*  387 */         this.channel.close();
/*      */       } finally {
/*  389 */         this.channel = null;
/*  390 */         if (this.password != null) {
/*  391 */           Arrays.fill(this.password, (byte)0);
/*      */         }
/*  393 */         this.password = null;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SevenZArchiveEntry getNextEntry() throws IOException {
/*  406 */     if (this.currentEntryIndex >= this.archive.files.length - 1) {
/*  407 */       return null;
/*      */     }
/*  409 */     this.currentEntryIndex++;
/*  410 */     SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
/*  411 */     if (entry.getName() == null && this.options.getUseDefaultNameForUnnamedEntries()) {
/*  412 */       entry.setName(getDefaultName());
/*      */     }
/*  414 */     buildDecodingStream(this.currentEntryIndex, false);
/*  415 */     this.uncompressedBytesReadFromCurrentEntry = this.compressedBytesReadFromCurrentEntry = 0L;
/*  416 */     return entry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterable<SevenZArchiveEntry> getEntries() {
/*  433 */     return new ArrayList<>(Arrays.asList(this.archive.files));
/*      */   }
/*      */ 
/*      */   
/*      */   private Archive readHeaders(byte[] password) throws IOException {
/*  438 */     ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
/*  439 */     readFully(buf);
/*  440 */     byte[] signature = new byte[6];
/*  441 */     buf.get(signature);
/*  442 */     if (!Arrays.equals(signature, sevenZSignature)) {
/*  443 */       throw new IOException("Bad 7z signature");
/*      */     }
/*      */     
/*  446 */     byte archiveVersionMajor = buf.get();
/*  447 */     byte archiveVersionMinor = buf.get();
/*  448 */     if (archiveVersionMajor != 0) {
/*  449 */       throw new IOException(String.format("Unsupported 7z version (%d,%d)", new Object[] {
/*  450 */               Byte.valueOf(archiveVersionMajor), Byte.valueOf(archiveVersionMinor)
/*      */             }));
/*      */     }
/*  453 */     boolean headerLooksValid = false;
/*  454 */     long startHeaderCrc = 0xFFFFFFFFL & buf.getInt();
/*  455 */     if (startHeaderCrc == 0L) {
/*      */       
/*  457 */       long currentPosition = this.channel.position();
/*  458 */       ByteBuffer peekBuf = ByteBuffer.allocate(20);
/*  459 */       readFully(peekBuf);
/*  460 */       this.channel.position(currentPosition);
/*      */       
/*  462 */       while (peekBuf.hasRemaining()) {
/*  463 */         if (peekBuf.get() != 0) {
/*  464 */           headerLooksValid = true;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  469 */       headerLooksValid = true;
/*      */     } 
/*      */     
/*  472 */     if (headerLooksValid) {
/*  473 */       StartHeader startHeader = readStartHeader(startHeaderCrc);
/*  474 */       return initializeArchive(startHeader, password, true);
/*      */     } 
/*      */     
/*  477 */     if (this.options.getTryToRecoverBrokenArchives()) {
/*  478 */       return tryToLocateEndHeader(password);
/*      */     }
/*  480 */     throw new IOException("archive seems to be invalid.\nYou may want to retry and enable the tryToRecoverBrokenArchives if the archive could be a multi volume archive that has been closed prematurely.");
/*      */   }
/*      */ 
/*      */   
/*      */   private Archive tryToLocateEndHeader(byte[] password) throws IOException {
/*      */     long minPos;
/*  486 */     ByteBuffer nidBuf = ByteBuffer.allocate(1);
/*  487 */     long searchLimit = 1048576L;
/*      */     
/*  489 */     long previousDataSize = this.channel.position() + 20L;
/*      */ 
/*      */     
/*  492 */     if (this.channel.position() + 1048576L > this.channel.size()) {
/*  493 */       minPos = this.channel.position();
/*      */     } else {
/*  495 */       minPos = this.channel.size() - 1048576L;
/*      */     } 
/*  497 */     long pos = this.channel.size() - 1L;
/*      */     
/*  499 */     while (pos > minPos) {
/*  500 */       pos--;
/*  501 */       this.channel.position(pos);
/*  502 */       nidBuf.rewind();
/*  503 */       if (this.channel.read(nidBuf) < 1) {
/*  504 */         throw new EOFException();
/*      */       }
/*  506 */       int nid = nidBuf.array()[0];
/*      */       
/*  508 */       if (nid == 23 || nid == 1) {
/*      */         
/*      */         try {
/*  511 */           StartHeader startHeader = new StartHeader();
/*  512 */           startHeader.nextHeaderOffset = pos - previousDataSize;
/*  513 */           startHeader.nextHeaderSize = this.channel.size() - pos;
/*  514 */           Archive result = initializeArchive(startHeader, password, false);
/*      */           
/*  516 */           if (result.packSizes.length > 0 && result.files.length > 0) {
/*  517 */             return result;
/*      */           }
/*  519 */         } catch (Exception exception) {}
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  524 */     throw new IOException("Start header corrupt and unable to guess end header");
/*      */   }
/*      */   
/*      */   private Archive initializeArchive(StartHeader startHeader, byte[] password, boolean verifyCrc) throws IOException {
/*  528 */     assertFitsIntoNonNegativeInt("nextHeaderSize", startHeader.nextHeaderSize);
/*  529 */     int nextHeaderSizeInt = (int)startHeader.nextHeaderSize;
/*  530 */     this.channel.position(32L + startHeader.nextHeaderOffset);
/*  531 */     ByteBuffer buf = ByteBuffer.allocate(nextHeaderSizeInt).order(ByteOrder.LITTLE_ENDIAN);
/*  532 */     readFully(buf);
/*  533 */     if (verifyCrc) {
/*  534 */       CRC32 crc = new CRC32();
/*  535 */       crc.update(buf.array());
/*  536 */       if (startHeader.nextHeaderCrc != crc.getValue()) {
/*  537 */         throw new IOException("NextHeader CRC mismatch");
/*      */       }
/*      */     } 
/*      */     
/*  541 */     Archive archive = new Archive();
/*  542 */     int nid = getUnsignedByte(buf);
/*  543 */     if (nid == 23) {
/*  544 */       buf = readEncodedHeader(buf, archive, password);
/*      */       
/*  546 */       archive = new Archive();
/*  547 */       nid = getUnsignedByte(buf);
/*      */     } 
/*  549 */     if (nid != 1) {
/*  550 */       throw new IOException("Broken or unsupported archive: no Header");
/*      */     }
/*  552 */     readHeader(buf, archive);
/*  553 */     archive.subStreamsInfo = null;
/*  554 */     return archive;
/*      */   }
/*      */   
/*      */   private StartHeader readStartHeader(long startHeaderCrc) throws IOException {
/*  558 */     StartHeader startHeader = new StartHeader();
/*      */ 
/*      */     
/*  561 */     try (DataInputStream dataInputStream = new DataInputStream((InputStream)new CRC32VerifyingInputStream(new BoundedSeekableByteChannelInputStream(this.channel, 20L), 20L, startHeaderCrc))) {
/*      */       
/*  563 */       startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
/*  564 */       if (startHeader.nextHeaderOffset < 0L || startHeader.nextHeaderOffset + 32L > this.channel
/*  565 */         .size()) {
/*  566 */         throw new IOException("nextHeaderOffset is out of bounds");
/*      */       }
/*      */       
/*  569 */       startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
/*  570 */       long nextHeaderEnd = startHeader.nextHeaderOffset + startHeader.nextHeaderSize;
/*  571 */       if (nextHeaderEnd < startHeader.nextHeaderOffset || nextHeaderEnd + 32L > this.channel
/*  572 */         .size()) {
/*  573 */         throw new IOException("nextHeaderSize is out of bounds");
/*      */       }
/*      */       
/*  576 */       startHeader.nextHeaderCrc = 0xFFFFFFFFL & Integer.reverseBytes(dataInputStream.readInt());
/*      */       
/*  578 */       return startHeader;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void readHeader(ByteBuffer header, Archive archive) throws IOException {
/*  583 */     int pos = header.position();
/*  584 */     ArchiveStatistics stats = sanityCheckAndCollectStatistics(header);
/*  585 */     stats.assertValidity(this.options.getMaxMemoryLimitInKb());
/*  586 */     header.position(pos);
/*      */     
/*  588 */     int nid = getUnsignedByte(header);
/*      */     
/*  590 */     if (nid == 2) {
/*  591 */       readArchiveProperties(header);
/*  592 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  595 */     if (nid == 3) {
/*  596 */       throw new IOException("Additional streams unsupported");
/*      */     }
/*      */ 
/*      */     
/*  600 */     if (nid == 4) {
/*  601 */       readStreamsInfo(header, archive);
/*  602 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  605 */     if (nid == 5) {
/*  606 */       readFilesInfo(header, archive);
/*  607 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private ArchiveStatistics sanityCheckAndCollectStatistics(ByteBuffer header) throws IOException {
/*  613 */     ArchiveStatistics stats = new ArchiveStatistics();
/*      */     
/*  615 */     int nid = getUnsignedByte(header);
/*      */     
/*  617 */     if (nid == 2) {
/*  618 */       sanityCheckArchiveProperties(header);
/*  619 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  622 */     if (nid == 3) {
/*  623 */       throw new IOException("Additional streams unsupported");
/*      */     }
/*      */ 
/*      */     
/*  627 */     if (nid == 4) {
/*  628 */       sanityCheckStreamsInfo(header, stats);
/*  629 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  632 */     if (nid == 5) {
/*  633 */       sanityCheckFilesInfo(header, stats);
/*  634 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  637 */     if (nid != 0) {
/*  638 */       throw new IOException("Badly terminated header, found " + nid);
/*      */     }
/*      */     
/*  641 */     return stats;
/*      */   }
/*      */ 
/*      */   
/*      */   private void readArchiveProperties(ByteBuffer input) throws IOException {
/*  646 */     int nid = getUnsignedByte(input);
/*  647 */     while (nid != 0) {
/*  648 */       long propertySize = readUint64(input);
/*  649 */       byte[] property = new byte[(int)propertySize];
/*  650 */       get(input, property);
/*  651 */       nid = getUnsignedByte(input);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void sanityCheckArchiveProperties(ByteBuffer header) throws IOException {
/*  657 */     int nid = getUnsignedByte(header);
/*  658 */     while (nid != 0) {
/*      */       
/*  660 */       int propertySize = assertFitsIntoNonNegativeInt("propertySize", readUint64(header));
/*  661 */       if (skipBytesFully(header, propertySize) < propertySize) {
/*  662 */         throw new IOException("invalid property size");
/*      */       }
/*  664 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */   }
/*      */   
/*      */   private ByteBuffer readEncodedHeader(ByteBuffer header, Archive archive, byte[] password) throws IOException {
/*      */     CRC32VerifyingInputStream cRC32VerifyingInputStream;
/*  670 */     int pos = header.position();
/*  671 */     ArchiveStatistics stats = new ArchiveStatistics();
/*  672 */     sanityCheckStreamsInfo(header, stats);
/*  673 */     stats.assertValidity(this.options.getMaxMemoryLimitInKb());
/*  674 */     header.position(pos);
/*      */     
/*  676 */     readStreamsInfo(header, archive);
/*      */     
/*  678 */     if (archive.folders == null || archive.folders.length == 0) {
/*  679 */       throw new IOException("no folders, can't read encoded header");
/*      */     }
/*  681 */     if (archive.packSizes == null || archive.packSizes.length == 0) {
/*  682 */       throw new IOException("no packed streams, can't read encoded header");
/*      */     }
/*      */ 
/*      */     
/*  686 */     Folder folder = archive.folders[0];
/*  687 */     int firstPackStreamIndex = 0;
/*  688 */     long folderOffset = 32L + archive.packPos + 0L;
/*      */ 
/*      */     
/*  691 */     this.channel.position(folderOffset);
/*  692 */     InputStream inputStreamStack = new BoundedSeekableByteChannelInputStream(this.channel, archive.packSizes[0]);
/*      */     
/*  694 */     for (Coder coder : folder.getOrderedCoders()) {
/*  695 */       if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
/*  696 */         throw new IOException("Multi input/output stream coders are not yet supported");
/*      */       }
/*  698 */       inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder
/*  699 */           .getUnpackSizeForCoder(coder), coder, password, this.options.getMaxMemoryLimitInKb());
/*      */     } 
/*  701 */     if (folder.hasCrc)
/*      */     {
/*  703 */       cRC32VerifyingInputStream = new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
/*      */     }
/*  705 */     int unpackSize = assertFitsIntoNonNegativeInt("unpackSize", folder.getUnpackSize());
/*  706 */     byte[] nextHeader = IOUtils.readRange((InputStream)cRC32VerifyingInputStream, unpackSize);
/*  707 */     if (nextHeader.length < unpackSize) {
/*  708 */       throw new IOException("premature end of stream");
/*      */     }
/*  710 */     cRC32VerifyingInputStream.close();
/*  711 */     return ByteBuffer.wrap(nextHeader).order(ByteOrder.LITTLE_ENDIAN);
/*      */   }
/*      */ 
/*      */   
/*      */   private void sanityCheckStreamsInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
/*  716 */     int nid = getUnsignedByte(header);
/*      */     
/*  718 */     if (nid == 6) {
/*  719 */       sanityCheckPackInfo(header, stats);
/*  720 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  723 */     if (nid == 7) {
/*  724 */       sanityCheckUnpackInfo(header, stats);
/*  725 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  728 */     if (nid == 8) {
/*  729 */       sanityCheckSubStreamsInfo(header, stats);
/*  730 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  733 */     if (nid != 0) {
/*  734 */       throw new IOException("Badly terminated StreamsInfo");
/*      */     }
/*      */   }
/*      */   
/*      */   private void readStreamsInfo(ByteBuffer header, Archive archive) throws IOException {
/*  739 */     int nid = getUnsignedByte(header);
/*      */     
/*  741 */     if (nid == 6) {
/*  742 */       readPackInfo(header, archive);
/*  743 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  746 */     if (nid == 7) {
/*  747 */       readUnpackInfo(header, archive);
/*  748 */       nid = getUnsignedByte(header);
/*      */     } else {
/*      */       
/*  751 */       archive.folders = Folder.EMPTY_FOLDER_ARRAY;
/*      */     } 
/*      */     
/*  754 */     if (nid == 8) {
/*  755 */       readSubStreamsInfo(header, archive);
/*  756 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sanityCheckPackInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
/*  761 */     long packPos = readUint64(header);
/*  762 */     if (packPos < 0L || 32L + packPos > this.channel.size() || 32L + packPos < 0L)
/*      */     {
/*  764 */       throw new IOException("packPos (" + packPos + ") is out of range");
/*      */     }
/*  766 */     long numPackStreams = readUint64(header);
/*  767 */     stats.numberOfPackedStreams = assertFitsIntoNonNegativeInt("numPackStreams", numPackStreams);
/*  768 */     int nid = getUnsignedByte(header);
/*  769 */     if (nid == 9) {
/*  770 */       long totalPackSizes = 0L;
/*  771 */       for (int i = 0; i < stats.numberOfPackedStreams; i++) {
/*  772 */         long packSize = readUint64(header);
/*  773 */         totalPackSizes += packSize;
/*  774 */         long endOfPackStreams = 32L + packPos + totalPackSizes;
/*  775 */         if (packSize < 0L || endOfPackStreams > this.channel
/*  776 */           .size() || endOfPackStreams < packPos)
/*      */         {
/*  778 */           throw new IOException("packSize (" + packSize + ") is out of range");
/*      */         }
/*      */       } 
/*  781 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  784 */     if (nid == 10) {
/*      */       
/*  786 */       int crcsDefined = readAllOrBits(header, stats.numberOfPackedStreams).cardinality();
/*  787 */       if (skipBytesFully(header, (4 * crcsDefined)) < (4 * crcsDefined)) {
/*  788 */         throw new IOException("invalid number of CRCs in PackInfo");
/*      */       }
/*  790 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  793 */     if (nid != 0) {
/*  794 */       throw new IOException("Badly terminated PackInfo (" + nid + ")");
/*      */     }
/*      */   }
/*      */   
/*      */   private void readPackInfo(ByteBuffer header, Archive archive) throws IOException {
/*  799 */     archive.packPos = readUint64(header);
/*  800 */     int numPackStreamsInt = (int)readUint64(header);
/*  801 */     int nid = getUnsignedByte(header);
/*  802 */     if (nid == 9) {
/*  803 */       archive.packSizes = new long[numPackStreamsInt];
/*  804 */       for (int i = 0; i < archive.packSizes.length; i++) {
/*  805 */         archive.packSizes[i] = readUint64(header);
/*      */       }
/*  807 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  810 */     if (nid == 10) {
/*  811 */       archive.packCrcsDefined = readAllOrBits(header, numPackStreamsInt);
/*  812 */       archive.packCrcs = new long[numPackStreamsInt];
/*  813 */       for (int i = 0; i < numPackStreamsInt; i++) {
/*  814 */         if (archive.packCrcsDefined.get(i)) {
/*  815 */           archive.packCrcs[i] = 0xFFFFFFFFL & getInt(header);
/*      */         }
/*      */       } 
/*      */       
/*  819 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void sanityCheckUnpackInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
/*  825 */     int nid = getUnsignedByte(header);
/*  826 */     if (nid != 11) {
/*  827 */       throw new IOException("Expected kFolder, got " + nid);
/*      */     }
/*  829 */     long numFolders = readUint64(header);
/*  830 */     stats.numberOfFolders = assertFitsIntoNonNegativeInt("numFolders", numFolders);
/*  831 */     int external = getUnsignedByte(header);
/*  832 */     if (external != 0) {
/*  833 */       throw new IOException("External unsupported");
/*      */     }
/*      */     
/*  836 */     List<Integer> numberOfOutputStreamsPerFolder = new LinkedList<>();
/*  837 */     for (int i = 0; i < stats.numberOfFolders; i++) {
/*  838 */       numberOfOutputStreamsPerFolder.add(Integer.valueOf(sanityCheckFolder(header, stats)));
/*      */     }
/*      */     
/*  841 */     long totalNumberOfBindPairs = stats.numberOfOutStreams - stats.numberOfFolders;
/*  842 */     long packedStreamsRequiredByFolders = stats.numberOfInStreams - totalNumberOfBindPairs;
/*  843 */     if (packedStreamsRequiredByFolders < stats.numberOfPackedStreams) {
/*  844 */       throw new IOException("archive doesn't contain enough packed streams");
/*      */     }
/*      */     
/*  847 */     nid = getUnsignedByte(header);
/*  848 */     if (nid != 12) {
/*  849 */       throw new IOException("Expected kCodersUnpackSize, got " + nid);
/*      */     }
/*      */     
/*  852 */     for (Iterator<Integer> iterator = numberOfOutputStreamsPerFolder.iterator(); iterator.hasNext(); ) { int numberOfOutputStreams = ((Integer)iterator.next()).intValue();
/*  853 */       for (int j = 0; j < numberOfOutputStreams; j++) {
/*  854 */         long unpackSize = readUint64(header);
/*  855 */         if (unpackSize < 0L) {
/*  856 */           throw new IllegalArgumentException("negative unpackSize");
/*      */         }
/*      */       }  }
/*      */ 
/*      */     
/*  861 */     nid = getUnsignedByte(header);
/*  862 */     if (nid == 10) {
/*  863 */       stats.folderHasCrc = readAllOrBits(header, stats.numberOfFolders);
/*  864 */       int crcsDefined = stats.folderHasCrc.cardinality();
/*  865 */       if (skipBytesFully(header, (4 * crcsDefined)) < (4 * crcsDefined)) {
/*  866 */         throw new IOException("invalid number of CRCs in UnpackInfo");
/*      */       }
/*  868 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  871 */     if (nid != 0) {
/*  872 */       throw new IOException("Badly terminated UnpackInfo");
/*      */     }
/*      */   }
/*      */   
/*      */   private void readUnpackInfo(ByteBuffer header, Archive archive) throws IOException {
/*  877 */     int nid = getUnsignedByte(header);
/*  878 */     int numFoldersInt = (int)readUint64(header);
/*  879 */     Folder[] folders = new Folder[numFoldersInt];
/*  880 */     archive.folders = folders;
/*  881 */     getUnsignedByte(header);
/*  882 */     for (int i = 0; i < numFoldersInt; i++) {
/*  883 */       folders[i] = readFolder(header);
/*      */     }
/*      */     
/*  886 */     nid = getUnsignedByte(header);
/*  887 */     for (Folder folder : folders) {
/*  888 */       assertFitsIntoNonNegativeInt("totalOutputStreams", folder.totalOutputStreams);
/*  889 */       folder.unpackSizes = new long[(int)folder.totalOutputStreams];
/*  890 */       for (int j = 0; j < folder.totalOutputStreams; j++) {
/*  891 */         folder.unpackSizes[j] = readUint64(header);
/*      */       }
/*      */     } 
/*      */     
/*  895 */     nid = getUnsignedByte(header);
/*  896 */     if (nid == 10) {
/*  897 */       BitSet crcsDefined = readAllOrBits(header, numFoldersInt);
/*  898 */       for (int j = 0; j < numFoldersInt; j++) {
/*  899 */         if (crcsDefined.get(j)) {
/*  900 */           (folders[j]).hasCrc = true;
/*  901 */           (folders[j]).crc = 0xFFFFFFFFL & getInt(header);
/*      */         } else {
/*  903 */           (folders[j]).hasCrc = false;
/*      */         } 
/*      */       } 
/*      */       
/*  907 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void sanityCheckSubStreamsInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
/*  913 */     int nid = getUnsignedByte(header);
/*  914 */     List<Integer> numUnpackSubStreamsPerFolder = new LinkedList<>();
/*  915 */     if (nid == 13) {
/*  916 */       for (int i = 0; i < stats.numberOfFolders; i++) {
/*  917 */         numUnpackSubStreamsPerFolder.add(Integer.valueOf(assertFitsIntoNonNegativeInt("numStreams", readUint64(header))));
/*      */       }
/*  919 */       stats.numberOfUnpackSubStreams = ((Long)numUnpackSubStreamsPerFolder.stream().collect(Collectors.summingLong(Integer::longValue))).longValue();
/*  920 */       nid = getUnsignedByte(header);
/*      */     } else {
/*  922 */       stats.numberOfUnpackSubStreams = stats.numberOfFolders;
/*      */     } 
/*      */     
/*  925 */     assertFitsIntoNonNegativeInt("totalUnpackStreams", stats.numberOfUnpackSubStreams);
/*      */     
/*  927 */     if (nid == 9) {
/*  928 */       for (Iterator<Integer> iterator = numUnpackSubStreamsPerFolder.iterator(); iterator.hasNext(); ) { int numUnpackSubStreams = ((Integer)iterator.next()).intValue();
/*  929 */         if (numUnpackSubStreams == 0) {
/*      */           continue;
/*      */         }
/*  932 */         for (int i = 0; i < numUnpackSubStreams - 1; i++) {
/*  933 */           long size = readUint64(header);
/*  934 */           if (size < 0L) {
/*  935 */             throw new IOException("negative unpackSize");
/*      */           }
/*      */         }  }
/*      */       
/*  939 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  942 */     int numDigests = 0;
/*  943 */     if (numUnpackSubStreamsPerFolder.isEmpty()) {
/*      */       
/*  945 */       numDigests = (stats.folderHasCrc == null) ? stats.numberOfFolders : (stats.numberOfFolders - stats.folderHasCrc.cardinality());
/*      */     } else {
/*  947 */       int folderIdx = 0;
/*  948 */       for (Iterator<Integer> iterator = numUnpackSubStreamsPerFolder.iterator(); iterator.hasNext(); ) { int numUnpackSubStreams = ((Integer)iterator.next()).intValue();
/*  949 */         if (numUnpackSubStreams != 1 || stats.folderHasCrc == null || 
/*  950 */           !stats.folderHasCrc.get(folderIdx++)) {
/*  951 */           numDigests += numUnpackSubStreams;
/*      */         } }
/*      */     
/*      */     } 
/*      */     
/*  956 */     if (nid == 10) {
/*  957 */       assertFitsIntoNonNegativeInt("numDigests", numDigests);
/*      */       
/*  959 */       int missingCrcs = readAllOrBits(header, numDigests).cardinality();
/*  960 */       if (skipBytesFully(header, (4 * missingCrcs)) < (4 * missingCrcs)) {
/*  961 */         throw new IOException("invalid number of missing CRCs in SubStreamInfo");
/*      */       }
/*  963 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  966 */     if (nid != 0) {
/*  967 */       throw new IOException("Badly terminated SubStreamsInfo");
/*      */     }
/*      */   }
/*      */   
/*      */   private void readSubStreamsInfo(ByteBuffer header, Archive archive) throws IOException {
/*  972 */     for (Folder folder : archive.folders) {
/*  973 */       folder.numUnpackSubStreams = 1;
/*      */     }
/*  975 */     long unpackStreamsCount = archive.folders.length;
/*      */     
/*  977 */     int nid = getUnsignedByte(header);
/*  978 */     if (nid == 13) {
/*  979 */       unpackStreamsCount = 0L;
/*  980 */       for (Folder folder : archive.folders) {
/*  981 */         long numStreams = readUint64(header);
/*  982 */         folder.numUnpackSubStreams = (int)numStreams;
/*  983 */         unpackStreamsCount += numStreams;
/*      */       } 
/*  985 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/*  988 */     int totalUnpackStreams = (int)unpackStreamsCount;
/*  989 */     SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
/*  990 */     subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
/*  991 */     subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
/*  992 */     subStreamsInfo.crcs = new long[totalUnpackStreams];
/*      */     
/*  994 */     int nextUnpackStream = 0;
/*  995 */     for (Folder folder : archive.folders) {
/*  996 */       if (folder.numUnpackSubStreams != 0) {
/*      */ 
/*      */         
/*  999 */         long sum = 0L;
/* 1000 */         if (nid == 9) {
/* 1001 */           for (int i = 0; i < folder.numUnpackSubStreams - 1; i++) {
/* 1002 */             long size = readUint64(header);
/* 1003 */             subStreamsInfo.unpackSizes[nextUnpackStream++] = size;
/* 1004 */             sum += size;
/*      */           } 
/*      */         }
/* 1007 */         if (sum > folder.getUnpackSize()) {
/* 1008 */           throw new IOException("sum of unpack sizes of folder exceeds total unpack size");
/*      */         }
/* 1010 */         subStreamsInfo.unpackSizes[nextUnpackStream++] = folder.getUnpackSize() - sum;
/*      */       } 
/* 1012 */     }  if (nid == 9) {
/* 1013 */       nid = getUnsignedByte(header);
/*      */     }
/*      */     
/* 1016 */     int numDigests = 0;
/* 1017 */     for (Folder folder : archive.folders) {
/* 1018 */       if (folder.numUnpackSubStreams != 1 || !folder.hasCrc) {
/* 1019 */         numDigests += folder.numUnpackSubStreams;
/*      */       }
/*      */     } 
/*      */     
/* 1023 */     if (nid == 10) {
/* 1024 */       BitSet hasMissingCrc = readAllOrBits(header, numDigests);
/* 1025 */       long[] missingCrcs = new long[numDigests];
/* 1026 */       for (int i = 0; i < numDigests; i++) {
/* 1027 */         if (hasMissingCrc.get(i)) {
/* 1028 */           missingCrcs[i] = 0xFFFFFFFFL & getInt(header);
/*      */         }
/*      */       } 
/* 1031 */       int nextCrc = 0;
/* 1032 */       int nextMissingCrc = 0;
/* 1033 */       for (Folder folder : archive.folders) {
/* 1034 */         if (folder.numUnpackSubStreams == 1 && folder.hasCrc) {
/* 1035 */           subStreamsInfo.hasCrc.set(nextCrc, true);
/* 1036 */           subStreamsInfo.crcs[nextCrc] = folder.crc;
/* 1037 */           nextCrc++;
/*      */         } else {
/* 1039 */           for (int j = 0; j < folder.numUnpackSubStreams; j++) {
/* 1040 */             subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
/* 1041 */             subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
/* 1042 */             nextCrc++;
/* 1043 */             nextMissingCrc++;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1048 */       nid = getUnsignedByte(header);
/*      */     } 
/*      */     
/* 1051 */     archive.subStreamsInfo = subStreamsInfo;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int sanityCheckFolder(ByteBuffer header, ArchiveStatistics stats) throws IOException {
/* 1057 */     int numCoders = assertFitsIntoNonNegativeInt("numCoders", readUint64(header));
/* 1058 */     if (numCoders == 0) {
/* 1059 */       throw new IOException("Folder without coders");
/*      */     }
/* 1061 */     stats.numberOfCoders = stats.numberOfCoders + numCoders;
/*      */     
/* 1063 */     long totalOutStreams = 0L;
/* 1064 */     long totalInStreams = 0L;
/* 1065 */     for (int i = 0; i < numCoders; i++) {
/* 1066 */       int bits = getUnsignedByte(header);
/* 1067 */       int idSize = bits & 0xF;
/* 1068 */       get(header, new byte[idSize]);
/*      */       
/* 1070 */       boolean isSimple = ((bits & 0x10) == 0);
/* 1071 */       boolean hasAttributes = ((bits & 0x20) != 0);
/* 1072 */       boolean moreAlternativeMethods = ((bits & 0x80) != 0);
/* 1073 */       if (moreAlternativeMethods) {
/* 1074 */         throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
/*      */       }
/*      */ 
/*      */       
/* 1078 */       if (isSimple) {
/* 1079 */         totalInStreams++;
/* 1080 */         totalOutStreams++;
/*      */       } else {
/* 1082 */         totalInStreams += 
/* 1083 */           assertFitsIntoNonNegativeInt("numInStreams", readUint64(header));
/* 1084 */         totalOutStreams += 
/* 1085 */           assertFitsIntoNonNegativeInt("numOutStreams", readUint64(header));
/*      */       } 
/*      */       
/* 1088 */       if (hasAttributes) {
/*      */         
/* 1090 */         int propertiesSize = assertFitsIntoNonNegativeInt("propertiesSize", readUint64(header));
/* 1091 */         if (skipBytesFully(header, propertiesSize) < propertiesSize) {
/* 1092 */           throw new IOException("invalid propertiesSize in folder");
/*      */         }
/*      */       } 
/*      */     } 
/* 1096 */     assertFitsIntoNonNegativeInt("totalInStreams", totalInStreams);
/* 1097 */     assertFitsIntoNonNegativeInt("totalOutStreams", totalOutStreams);
/* 1098 */     stats.numberOfOutStreams = stats.numberOfOutStreams + totalOutStreams;
/* 1099 */     stats.numberOfInStreams = stats.numberOfInStreams + totalInStreams;
/*      */     
/* 1101 */     if (totalOutStreams == 0L) {
/* 1102 */       throw new IOException("Total output streams can't be 0");
/*      */     }
/*      */ 
/*      */     
/* 1106 */     int numBindPairs = assertFitsIntoNonNegativeInt("numBindPairs", totalOutStreams - 1L);
/* 1107 */     if (totalInStreams < numBindPairs) {
/* 1108 */       throw new IOException("Total input streams can't be less than the number of bind pairs");
/*      */     }
/* 1110 */     BitSet inStreamsBound = new BitSet((int)totalInStreams);
/* 1111 */     for (int j = 0; j < numBindPairs; j++) {
/* 1112 */       int inIndex = assertFitsIntoNonNegativeInt("inIndex", readUint64(header));
/* 1113 */       if (totalInStreams <= inIndex) {
/* 1114 */         throw new IOException("inIndex is bigger than number of inStreams");
/*      */       }
/* 1116 */       inStreamsBound.set(inIndex);
/* 1117 */       int outIndex = assertFitsIntoNonNegativeInt("outIndex", readUint64(header));
/* 1118 */       if (totalOutStreams <= outIndex) {
/* 1119 */         throw new IOException("outIndex is bigger than number of outStreams");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1124 */     int numPackedStreams = assertFitsIntoNonNegativeInt("numPackedStreams", totalInStreams - numBindPairs);
/*      */     
/* 1126 */     if (numPackedStreams == 1) {
/* 1127 */       if (inStreamsBound.nextClearBit(0) == -1) {
/* 1128 */         throw new IOException("Couldn't find stream's bind pair index");
/*      */       }
/*      */     } else {
/* 1131 */       for (int k = 0; k < numPackedStreams; k++) {
/*      */         
/* 1133 */         int packedStreamIndex = assertFitsIntoNonNegativeInt("packedStreamIndex", readUint64(header));
/* 1134 */         if (packedStreamIndex >= totalInStreams) {
/* 1135 */           throw new IOException("packedStreamIndex is bigger than number of totalInStreams");
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1140 */     return (int)totalOutStreams;
/*      */   }
/*      */   
/*      */   private Folder readFolder(ByteBuffer header) throws IOException {
/* 1144 */     Folder folder = new Folder();
/*      */     
/* 1146 */     long numCoders = readUint64(header);
/* 1147 */     Coder[] coders = new Coder[(int)numCoders];
/* 1148 */     long totalInStreams = 0L;
/* 1149 */     long totalOutStreams = 0L;
/* 1150 */     for (int i = 0; i < coders.length; i++) {
/* 1151 */       coders[i] = new Coder();
/* 1152 */       int bits = getUnsignedByte(header);
/* 1153 */       int idSize = bits & 0xF;
/* 1154 */       boolean isSimple = ((bits & 0x10) == 0);
/* 1155 */       boolean hasAttributes = ((bits & 0x20) != 0);
/* 1156 */       boolean moreAlternativeMethods = ((bits & 0x80) != 0);
/*      */       
/* 1158 */       (coders[i]).decompressionMethodId = new byte[idSize];
/* 1159 */       get(header, (coders[i]).decompressionMethodId);
/* 1160 */       if (isSimple) {
/* 1161 */         (coders[i]).numInStreams = 1L;
/* 1162 */         (coders[i]).numOutStreams = 1L;
/*      */       } else {
/* 1164 */         (coders[i]).numInStreams = readUint64(header);
/* 1165 */         (coders[i]).numOutStreams = readUint64(header);
/*      */       } 
/* 1167 */       totalInStreams += (coders[i]).numInStreams;
/* 1168 */       totalOutStreams += (coders[i]).numOutStreams;
/* 1169 */       if (hasAttributes) {
/* 1170 */         long propertiesSize = readUint64(header);
/* 1171 */         (coders[i]).properties = new byte[(int)propertiesSize];
/* 1172 */         get(header, (coders[i]).properties);
/*      */       } 
/*      */       
/* 1175 */       if (moreAlternativeMethods) {
/* 1176 */         throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
/*      */       }
/*      */     } 
/*      */     
/* 1180 */     folder.coders = coders;
/* 1181 */     folder.totalInputStreams = totalInStreams;
/* 1182 */     folder.totalOutputStreams = totalOutStreams;
/*      */     
/* 1184 */     long numBindPairs = totalOutStreams - 1L;
/* 1185 */     BindPair[] bindPairs = new BindPair[(int)numBindPairs];
/* 1186 */     for (int j = 0; j < bindPairs.length; j++) {
/* 1187 */       bindPairs[j] = new BindPair();
/* 1188 */       (bindPairs[j]).inIndex = readUint64(header);
/* 1189 */       (bindPairs[j]).outIndex = readUint64(header);
/*      */     } 
/* 1191 */     folder.bindPairs = bindPairs;
/*      */     
/* 1193 */     long numPackedStreams = totalInStreams - numBindPairs;
/* 1194 */     long[] packedStreams = new long[(int)numPackedStreams];
/* 1195 */     if (numPackedStreams == 1L) {
/*      */       int k;
/* 1197 */       for (k = 0; k < (int)totalInStreams && 
/* 1198 */         folder.findBindPairForInStream(k) >= 0; k++);
/*      */ 
/*      */ 
/*      */       
/* 1202 */       packedStreams[0] = k;
/*      */     } else {
/* 1204 */       for (int k = 0; k < (int)numPackedStreams; k++) {
/* 1205 */         packedStreams[k] = readUint64(header);
/*      */       }
/*      */     } 
/* 1208 */     folder.packedStreams = packedStreams;
/*      */     
/* 1210 */     return folder;
/*      */   }
/*      */   private BitSet readAllOrBits(ByteBuffer header, int size) throws IOException {
/*      */     BitSet bits;
/* 1214 */     int areAllDefined = getUnsignedByte(header);
/*      */     
/* 1216 */     if (areAllDefined != 0) {
/* 1217 */       bits = new BitSet(size);
/* 1218 */       for (int i = 0; i < size; i++) {
/* 1219 */         bits.set(i, true);
/*      */       }
/*      */     } else {
/* 1222 */       bits = readBits(header, size);
/*      */     } 
/* 1224 */     return bits;
/*      */   }
/*      */   
/*      */   private BitSet readBits(ByteBuffer header, int size) throws IOException {
/* 1228 */     BitSet bits = new BitSet(size);
/* 1229 */     int mask = 0;
/* 1230 */     int cache = 0;
/* 1231 */     for (int i = 0; i < size; i++) {
/* 1232 */       if (mask == 0) {
/* 1233 */         mask = 128;
/* 1234 */         cache = getUnsignedByte(header);
/*      */       } 
/* 1236 */       bits.set(i, ((cache & mask) != 0));
/* 1237 */       mask >>>= 1;
/*      */     } 
/* 1239 */     return bits;
/*      */   }
/*      */   
/*      */   private void sanityCheckFilesInfo(ByteBuffer header, ArchiveStatistics stats) throws IOException {
/* 1243 */     stats.numberOfEntries = assertFitsIntoNonNegativeInt("numFiles", readUint64(header));
/*      */     
/* 1245 */     int emptyStreams = -1;
/*      */     while (true) {
/* 1247 */       int external, timesDefined, attributesDefined, namesLength, j, filesSeen, i, propertyType = getUnsignedByte(header);
/* 1248 */       if (propertyType == 0) {
/*      */         break;
/*      */       }
/* 1251 */       long size = readUint64(header);
/* 1252 */       switch (propertyType) {
/*      */         case 14:
/* 1254 */           emptyStreams = readBits(header, stats.numberOfEntries).cardinality();
/*      */           continue;
/*      */         
/*      */         case 15:
/* 1258 */           if (emptyStreams == -1) {
/* 1259 */             throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
/*      */           }
/* 1261 */           readBits(header, emptyStreams);
/*      */           continue;
/*      */         
/*      */         case 16:
/* 1265 */           if (emptyStreams == -1) {
/* 1266 */             throw new IOException("Header format error: kEmptyStream must appear before kAnti");
/*      */           }
/* 1268 */           readBits(header, emptyStreams);
/*      */           continue;
/*      */         
/*      */         case 17:
/* 1272 */           external = getUnsignedByte(header);
/* 1273 */           if (external != 0) {
/* 1274 */             throw new IOException("Not implemented");
/*      */           }
/*      */           
/* 1277 */           namesLength = assertFitsIntoNonNegativeInt("file names length", size - 1L);
/* 1278 */           if ((namesLength & 0x1) != 0) {
/* 1279 */             throw new IOException("File names length invalid");
/*      */           }
/*      */           
/* 1282 */           filesSeen = 0;
/* 1283 */           for (i = 0; i < namesLength; i += 2) {
/* 1284 */             char c = getChar(header);
/* 1285 */             if (c == '\000') {
/* 1286 */               filesSeen++;
/*      */             }
/*      */           } 
/* 1289 */           if (filesSeen != stats.numberOfEntries) {
/* 1290 */             throw new IOException("Invalid number of file names (" + filesSeen + " instead of " + stats
/* 1291 */                 .numberOfEntries + ")");
/*      */           }
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 18:
/* 1297 */           timesDefined = readAllOrBits(header, stats.numberOfEntries).cardinality();
/* 1298 */           j = getUnsignedByte(header);
/* 1299 */           if (j != 0) {
/* 1300 */             throw new IOException("Not implemented");
/*      */           }
/* 1302 */           if (skipBytesFully(header, (8 * timesDefined)) < (8 * timesDefined)) {
/* 1303 */             throw new IOException("invalid creation dates size");
/*      */           }
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 19:
/* 1309 */           timesDefined = readAllOrBits(header, stats.numberOfEntries).cardinality();
/* 1310 */           j = getUnsignedByte(header);
/* 1311 */           if (j != 0) {
/* 1312 */             throw new IOException("Not implemented");
/*      */           }
/* 1314 */           if (skipBytesFully(header, (8 * timesDefined)) < (8 * timesDefined)) {
/* 1315 */             throw new IOException("invalid access dates size");
/*      */           }
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 20:
/* 1321 */           timesDefined = readAllOrBits(header, stats.numberOfEntries).cardinality();
/* 1322 */           j = getUnsignedByte(header);
/* 1323 */           if (j != 0) {
/* 1324 */             throw new IOException("Not implemented");
/*      */           }
/* 1326 */           if (skipBytesFully(header, (8 * timesDefined)) < (8 * timesDefined)) {
/* 1327 */             throw new IOException("invalid modification dates size");
/*      */           }
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 21:
/* 1333 */           attributesDefined = readAllOrBits(header, stats.numberOfEntries).cardinality();
/* 1334 */           j = getUnsignedByte(header);
/* 1335 */           if (j != 0) {
/* 1336 */             throw new IOException("Not implemented");
/*      */           }
/* 1338 */           if (skipBytesFully(header, (4 * attributesDefined)) < (4 * attributesDefined)) {
/* 1339 */             throw new IOException("invalid windows attributes size");
/*      */           }
/*      */           continue;
/*      */         
/*      */         case 24:
/* 1344 */           throw new IOException("kStartPos is unsupported, please report");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 25:
/* 1350 */           if (skipBytesFully(header, size) < size) {
/* 1351 */             throw new IOException("Incomplete kDummy property");
/*      */           }
/*      */           continue;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1358 */       if (skipBytesFully(header, size) < size) {
/* 1359 */         throw new IOException("Incomplete property of type " + propertyType);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1365 */     stats.numberOfEntriesWithStream = stats.numberOfEntries - ((emptyStreams > 0) ? emptyStreams : 0);
/*      */   }
/*      */   
/*      */   private void readFilesInfo(ByteBuffer header, Archive archive) throws IOException {
/* 1369 */     int numFilesInt = (int)readUint64(header);
/* 1370 */     Map<Integer, SevenZArchiveEntry> fileMap = new HashMap<>();
/* 1371 */     BitSet isEmptyStream = null;
/* 1372 */     BitSet isEmptyFile = null;
/* 1373 */     BitSet isAnti = null; while (true) {
/*      */       byte[] names; BitSet timesDefined, attributesDefined;
/* 1375 */       int namesLength, j, nextFile, nextName, k, propertyType = getUnsignedByte(header);
/* 1376 */       if (propertyType == 0) {
/*      */         break;
/*      */       }
/* 1379 */       long size = readUint64(header);
/* 1380 */       switch (propertyType) {
/*      */         case 14:
/* 1382 */           isEmptyStream = readBits(header, numFilesInt);
/*      */           continue;
/*      */         
/*      */         case 15:
/* 1386 */           isEmptyFile = readBits(header, isEmptyStream.cardinality());
/*      */           continue;
/*      */         
/*      */         case 16:
/* 1390 */           isAnti = readBits(header, isEmptyStream.cardinality());
/*      */           continue;
/*      */         
/*      */         case 17:
/* 1394 */           getUnsignedByte(header);
/* 1395 */           names = new byte[(int)(size - 1L)];
/* 1396 */           namesLength = names.length;
/* 1397 */           get(header, names);
/* 1398 */           nextFile = 0;
/* 1399 */           nextName = 0;
/* 1400 */           for (k = 0; k < namesLength; k += 2) {
/* 1401 */             if (names[k] == 0 && names[k + 1] == 0) {
/* 1402 */               checkEntryIsInitialized(fileMap, nextFile);
/* 1403 */               ((SevenZArchiveEntry)fileMap.get(Integer.valueOf(nextFile))).setName(new String(names, nextName, k - nextName, StandardCharsets.UTF_16LE));
/* 1404 */               nextName = k + 2;
/* 1405 */               nextFile++;
/*      */             } 
/*      */           } 
/* 1408 */           if (nextName != namesLength || nextFile != numFilesInt) {
/* 1409 */             throw new IOException("Error parsing file names");
/*      */           }
/*      */           continue;
/*      */         
/*      */         case 18:
/* 1414 */           timesDefined = readAllOrBits(header, numFilesInt);
/* 1415 */           getUnsignedByte(header);
/* 1416 */           for (j = 0; j < numFilesInt; j++) {
/* 1417 */             checkEntryIsInitialized(fileMap, j);
/* 1418 */             SevenZArchiveEntry entryAtIndex = fileMap.get(Integer.valueOf(j));
/* 1419 */             entryAtIndex.setHasCreationDate(timesDefined.get(j));
/* 1420 */             if (entryAtIndex.getHasCreationDate()) {
/* 1421 */               entryAtIndex.setCreationDate(getLong(header));
/*      */             }
/*      */           } 
/*      */           continue;
/*      */         
/*      */         case 19:
/* 1427 */           timesDefined = readAllOrBits(header, numFilesInt);
/* 1428 */           getUnsignedByte(header);
/* 1429 */           for (j = 0; j < numFilesInt; j++) {
/* 1430 */             checkEntryIsInitialized(fileMap, j);
/* 1431 */             SevenZArchiveEntry entryAtIndex = fileMap.get(Integer.valueOf(j));
/* 1432 */             entryAtIndex.setHasAccessDate(timesDefined.get(j));
/* 1433 */             if (entryAtIndex.getHasAccessDate()) {
/* 1434 */               entryAtIndex.setAccessDate(getLong(header));
/*      */             }
/*      */           } 
/*      */           continue;
/*      */         
/*      */         case 20:
/* 1440 */           timesDefined = readAllOrBits(header, numFilesInt);
/* 1441 */           getUnsignedByte(header);
/* 1442 */           for (j = 0; j < numFilesInt; j++) {
/* 1443 */             checkEntryIsInitialized(fileMap, j);
/* 1444 */             SevenZArchiveEntry entryAtIndex = fileMap.get(Integer.valueOf(j));
/* 1445 */             entryAtIndex.setHasLastModifiedDate(timesDefined.get(j));
/* 1446 */             if (entryAtIndex.getHasLastModifiedDate()) {
/* 1447 */               entryAtIndex.setLastModifiedDate(getLong(header));
/*      */             }
/*      */           } 
/*      */           continue;
/*      */         
/*      */         case 21:
/* 1453 */           attributesDefined = readAllOrBits(header, numFilesInt);
/* 1454 */           getUnsignedByte(header);
/* 1455 */           for (j = 0; j < numFilesInt; j++) {
/* 1456 */             checkEntryIsInitialized(fileMap, j);
/* 1457 */             SevenZArchiveEntry entryAtIndex = fileMap.get(Integer.valueOf(j));
/* 1458 */             entryAtIndex.setHasWindowsAttributes(attributesDefined.get(j));
/* 1459 */             if (entryAtIndex.getHasWindowsAttributes()) {
/* 1460 */               entryAtIndex.setWindowsAttributes(getInt(header));
/*      */             }
/*      */           } 
/*      */           continue;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 25:
/* 1469 */           skipBytesFully(header, size);
/*      */           continue;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1475 */       skipBytesFully(header, size);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1480 */     int nonEmptyFileCounter = 0;
/* 1481 */     int emptyFileCounter = 0;
/* 1482 */     for (int i = 0; i < numFilesInt; i++) {
/* 1483 */       SevenZArchiveEntry entryAtIndex = fileMap.get(Integer.valueOf(i));
/* 1484 */       if (entryAtIndex != null) {
/*      */ 
/*      */         
/* 1487 */         entryAtIndex.setHasStream((isEmptyStream == null || !isEmptyStream.get(i)));
/* 1488 */         if (entryAtIndex.hasStream()) {
/* 1489 */           if (archive.subStreamsInfo == null) {
/* 1490 */             throw new IOException("Archive contains file with streams but no subStreamsInfo");
/*      */           }
/* 1492 */           entryAtIndex.setDirectory(false);
/* 1493 */           entryAtIndex.setAntiItem(false);
/* 1494 */           entryAtIndex.setHasCrc(archive.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
/* 1495 */           entryAtIndex.setCrcValue(archive.subStreamsInfo.crcs[nonEmptyFileCounter]);
/* 1496 */           entryAtIndex.setSize(archive.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
/* 1497 */           if (entryAtIndex.getSize() < 0L) {
/* 1498 */             throw new IOException("broken archive, entry with negative size");
/*      */           }
/* 1500 */           nonEmptyFileCounter++;
/*      */         } else {
/* 1502 */           entryAtIndex.setDirectory((isEmptyFile == null || !isEmptyFile.get(emptyFileCounter)));
/* 1503 */           entryAtIndex.setAntiItem((isAnti != null && isAnti.get(emptyFileCounter)));
/* 1504 */           entryAtIndex.setHasCrc(false);
/* 1505 */           entryAtIndex.setSize(0L);
/* 1506 */           emptyFileCounter++;
/*      */         } 
/*      */       } 
/* 1509 */     }  List<SevenZArchiveEntry> entries = new ArrayList<>();
/* 1510 */     for (SevenZArchiveEntry e : fileMap.values()) {
/* 1511 */       if (e != null) {
/* 1512 */         entries.add(e);
/*      */       }
/*      */     } 
/* 1515 */     archive.files = entries.<SevenZArchiveEntry>toArray(SevenZArchiveEntry.EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY);
/* 1516 */     calculateStreamMap(archive);
/*      */   }
/*      */   
/*      */   private void checkEntryIsInitialized(Map<Integer, SevenZArchiveEntry> archiveEntries, int index) {
/* 1520 */     if (archiveEntries.get(Integer.valueOf(index)) == null) {
/* 1521 */       archiveEntries.put(Integer.valueOf(index), new SevenZArchiveEntry());
/*      */     }
/*      */   }
/*      */   
/*      */   private void calculateStreamMap(Archive archive) throws IOException {
/* 1526 */     StreamMap streamMap = new StreamMap();
/*      */     
/* 1528 */     int nextFolderPackStreamIndex = 0;
/* 1529 */     int numFolders = (archive.folders != null) ? archive.folders.length : 0;
/* 1530 */     streamMap.folderFirstPackStreamIndex = new int[numFolders];
/* 1531 */     for (int i = 0; i < numFolders; i++) {
/* 1532 */       streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
/* 1533 */       nextFolderPackStreamIndex += (archive.folders[i]).packedStreams.length;
/*      */     } 
/*      */     
/* 1536 */     long nextPackStreamOffset = 0L;
/* 1537 */     int numPackSizes = archive.packSizes.length;
/* 1538 */     streamMap.packStreamOffsets = new long[numPackSizes];
/* 1539 */     for (int j = 0; j < numPackSizes; j++) {
/* 1540 */       streamMap.packStreamOffsets[j] = nextPackStreamOffset;
/* 1541 */       nextPackStreamOffset += archive.packSizes[j];
/*      */     } 
/*      */     
/* 1544 */     streamMap.folderFirstFileIndex = new int[numFolders];
/* 1545 */     streamMap.fileFolderIndex = new int[archive.files.length];
/* 1546 */     int nextFolderIndex = 0;
/* 1547 */     int nextFolderUnpackStreamIndex = 0;
/* 1548 */     for (int k = 0; k < archive.files.length; k++) {
/* 1549 */       if (!archive.files[k].hasStream() && nextFolderUnpackStreamIndex == 0) {
/* 1550 */         streamMap.fileFolderIndex[k] = -1;
/*      */       } else {
/*      */         
/* 1553 */         if (nextFolderUnpackStreamIndex == 0) {
/* 1554 */           for (; nextFolderIndex < archive.folders.length; nextFolderIndex++) {
/* 1555 */             streamMap.folderFirstFileIndex[nextFolderIndex] = k;
/* 1556 */             if ((archive.folders[nextFolderIndex]).numUnpackSubStreams > 0) {
/*      */               break;
/*      */             }
/*      */           } 
/* 1560 */           if (nextFolderIndex >= archive.folders.length) {
/* 1561 */             throw new IOException("Too few folders in archive");
/*      */           }
/*      */         } 
/* 1564 */         streamMap.fileFolderIndex[k] = nextFolderIndex;
/* 1565 */         if (archive.files[k].hasStream()) {
/*      */ 
/*      */           
/* 1568 */           nextFolderUnpackStreamIndex++;
/* 1569 */           if (nextFolderUnpackStreamIndex >= (archive.folders[nextFolderIndex]).numUnpackSubStreams) {
/* 1570 */             nextFolderIndex++;
/* 1571 */             nextFolderUnpackStreamIndex = 0;
/*      */           } 
/*      */         } 
/*      */       } 
/* 1575 */     }  archive.streamMap = streamMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void buildDecodingStream(int entryIndex, boolean isRandomAccess) throws IOException {
/*      */     CRC32VerifyingInputStream cRC32VerifyingInputStream;
/* 1591 */     if (this.archive.streamMap == null) {
/* 1592 */       throw new IOException("Archive doesn't contain stream information to read entries");
/*      */     }
/* 1594 */     int folderIndex = this.archive.streamMap.fileFolderIndex[entryIndex];
/* 1595 */     if (folderIndex < 0) {
/* 1596 */       this.deferredBlockStreams.clear();
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1601 */     SevenZArchiveEntry file = this.archive.files[entryIndex];
/* 1602 */     boolean isInSameFolder = false;
/* 1603 */     if (this.currentFolderIndex == folderIndex) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1609 */       if (entryIndex > 0) {
/* 1610 */         file.setContentMethods(this.archive.files[entryIndex - 1].getContentMethods());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1616 */       if (isRandomAccess && file.getContentMethods() == null) {
/* 1617 */         int folderFirstFileIndex = this.archive.streamMap.folderFirstFileIndex[folderIndex];
/* 1618 */         SevenZArchiveEntry folderFirstFile = this.archive.files[folderFirstFileIndex];
/* 1619 */         file.setContentMethods(folderFirstFile.getContentMethods());
/*      */       } 
/* 1621 */       isInSameFolder = true;
/*      */     } else {
/* 1623 */       this.currentFolderIndex = folderIndex;
/*      */       
/* 1625 */       reopenFolderInputStream(folderIndex, file);
/*      */     } 
/*      */     
/* 1628 */     boolean haveSkippedEntries = false;
/* 1629 */     if (isRandomAccess)
/*      */     {
/* 1631 */       haveSkippedEntries = skipEntriesWhenNeeded(entryIndex, isInSameFolder, folderIndex);
/*      */     }
/*      */     
/* 1634 */     if (isRandomAccess && this.currentEntryIndex == entryIndex && !haveSkippedEntries) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1643 */     BoundedInputStream boundedInputStream = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
/* 1644 */     if (file.getHasCrc()) {
/* 1645 */       cRC32VerifyingInputStream = new CRC32VerifyingInputStream((InputStream)boundedInputStream, file.getSize(), file.getCrcValue());
/*      */     }
/*      */     
/* 1648 */     this.deferredBlockStreams.add(cRC32VerifyingInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void reopenFolderInputStream(int folderIndex, SevenZArchiveEntry file) throws IOException {
/* 1659 */     this.deferredBlockStreams.clear();
/* 1660 */     if (this.currentFolderInputStream != null) {
/* 1661 */       this.currentFolderInputStream.close();
/* 1662 */       this.currentFolderInputStream = null;
/*      */     } 
/* 1664 */     Folder folder = this.archive.folders[folderIndex];
/* 1665 */     int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
/* 1666 */     long folderOffset = 32L + this.archive.packPos + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex];
/*      */ 
/*      */     
/* 1669 */     this.currentFolderInputStream = buildDecoderStack(folder, folderOffset, firstPackStreamIndex, file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean skipEntriesWhenNeeded(int entryIndex, boolean isInSameFolder, int folderIndex) throws IOException {
/* 1696 */     SevenZArchiveEntry file = this.archive.files[entryIndex];
/*      */ 
/*      */     
/* 1699 */     if (this.currentEntryIndex == entryIndex && !hasCurrentEntryBeenRead()) {
/* 1700 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1708 */     int filesToSkipStartIndex = this.archive.streamMap.folderFirstFileIndex[this.currentFolderIndex];
/* 1709 */     if (isInSameFolder) {
/* 1710 */       if (this.currentEntryIndex < entryIndex) {
/*      */         
/* 1712 */         filesToSkipStartIndex = this.currentEntryIndex + 1;
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1717 */         reopenFolderInputStream(folderIndex, file);
/*      */       } 
/*      */     }
/*      */     
/* 1721 */     for (int i = filesToSkipStartIndex; i < entryIndex; i++) {
/* 1722 */       CRC32VerifyingInputStream cRC32VerifyingInputStream; SevenZArchiveEntry fileToSkip = this.archive.files[i];
/* 1723 */       BoundedInputStream boundedInputStream = new BoundedInputStream(this.currentFolderInputStream, fileToSkip.getSize());
/* 1724 */       if (fileToSkip.getHasCrc()) {
/* 1725 */         cRC32VerifyingInputStream = new CRC32VerifyingInputStream((InputStream)boundedInputStream, fileToSkip.getSize(), fileToSkip.getCrcValue());
/*      */       }
/* 1727 */       this.deferredBlockStreams.add(cRC32VerifyingInputStream);
/*      */ 
/*      */       
/* 1730 */       fileToSkip.setContentMethods(file.getContentMethods());
/*      */     } 
/* 1732 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasCurrentEntryBeenRead() {
/* 1744 */     boolean hasCurrentEntryBeenRead = false;
/* 1745 */     if (!this.deferredBlockStreams.isEmpty()) {
/* 1746 */       InputStream currentEntryInputStream = this.deferredBlockStreams.get(this.deferredBlockStreams.size() - 1);
/*      */ 
/*      */       
/* 1749 */       if (currentEntryInputStream instanceof CRC32VerifyingInputStream) {
/* 1750 */         hasCurrentEntryBeenRead = (((CRC32VerifyingInputStream)currentEntryInputStream).getBytesRemaining() != this.archive.files[this.currentEntryIndex].getSize());
/*      */       }
/*      */       
/* 1753 */       if (currentEntryInputStream instanceof BoundedInputStream) {
/* 1754 */         hasCurrentEntryBeenRead = (((BoundedInputStream)currentEntryInputStream).getBytesRemaining() != this.archive.files[this.currentEntryIndex].getSize());
/*      */       }
/*      */     } 
/* 1757 */     return hasCurrentEntryBeenRead;
/*      */   }
/*      */ 
/*      */   
/*      */   private InputStream buildDecoderStack(Folder folder, long folderOffset, int firstPackStreamIndex, SevenZArchiveEntry entry) throws IOException {
/* 1762 */     this.channel.position(folderOffset);
/* 1763 */     InputStream inputStreamStack = new FilterInputStream(new BufferedInputStream(new BoundedSeekableByteChannelInputStream(this.channel, this.archive.packSizes[firstPackStreamIndex])))
/*      */       {
/*      */         
/*      */         public int read() throws IOException
/*      */         {
/* 1768 */           int r = this.in.read();
/* 1769 */           if (r >= 0) {
/* 1770 */             count(1);
/*      */           }
/* 1772 */           return r;
/*      */         }
/*      */         
/*      */         public int read(byte[] b) throws IOException {
/* 1776 */           return read(b, 0, b.length);
/*      */         }
/*      */         
/*      */         public int read(byte[] b, int off, int len) throws IOException {
/* 1780 */           if (len == 0) {
/* 1781 */             return 0;
/*      */           }
/* 1783 */           int r = this.in.read(b, off, len);
/* 1784 */           if (r >= 0) {
/* 1785 */             count(r);
/*      */           }
/* 1787 */           return r;
/*      */         }
/*      */         private void count(int c) {
/* 1790 */           SevenZFile.this.compressedBytesReadFromCurrentEntry = SevenZFile.this.compressedBytesReadFromCurrentEntry + c;
/*      */         }
/*      */       };
/* 1793 */     LinkedList<SevenZMethodConfiguration> methods = new LinkedList<>();
/* 1794 */     for (Coder coder : folder.getOrderedCoders()) {
/* 1795 */       if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
/* 1796 */         throw new IOException("Multi input/output stream coders are not yet supported");
/*      */       }
/* 1798 */       SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
/* 1799 */       inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder
/* 1800 */           .getUnpackSizeForCoder(coder), coder, this.password, this.options.getMaxMemoryLimitInKb());
/* 1801 */       methods.addFirst(new SevenZMethodConfiguration(method, 
/* 1802 */             Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
/*      */     } 
/* 1804 */     entry.setContentMethods(methods);
/* 1805 */     if (folder.hasCrc) {
/* 1806 */       return (InputStream)new CRC32VerifyingInputStream(inputStreamStack, folder
/* 1807 */           .getUnpackSize(), folder.crc);
/*      */     }
/* 1809 */     return inputStreamStack;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int read() throws IOException {
/* 1820 */     int b = getCurrentStream().read();
/* 1821 */     if (b >= 0) {
/* 1822 */       this.uncompressedBytesReadFromCurrentEntry++;
/*      */     }
/* 1824 */     return b;
/*      */   }
/*      */   
/*      */   private InputStream getCurrentStream() throws IOException {
/* 1828 */     if (this.archive.files[this.currentEntryIndex].getSize() == 0L) {
/* 1829 */       return new ByteArrayInputStream(ByteUtils.EMPTY_BYTE_ARRAY);
/*      */     }
/* 1831 */     if (this.deferredBlockStreams.isEmpty()) {
/* 1832 */       throw new IllegalStateException("No current 7z entry (call getNextEntry() first).");
/*      */     }
/*      */     
/* 1835 */     while (this.deferredBlockStreams.size() > 1) {
/*      */ 
/*      */ 
/*      */       
/* 1839 */       try (InputStream stream = (InputStream)this.deferredBlockStreams.remove(0)) {
/* 1840 */         IOUtils.skip(stream, Long.MAX_VALUE);
/*      */       } 
/* 1842 */       this.compressedBytesReadFromCurrentEntry = 0L;
/*      */     } 
/*      */     
/* 1845 */     return this.deferredBlockStreams.get(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getInputStream(SevenZArchiveEntry entry) throws IOException {
/* 1861 */     int entryIndex = -1;
/* 1862 */     for (int i = 0; i < this.archive.files.length; i++) {
/* 1863 */       if (entry == this.archive.files[i]) {
/* 1864 */         entryIndex = i;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1869 */     if (entryIndex < 0) {
/* 1870 */       throw new IllegalArgumentException("Can not find " + entry.getName() + " in " + this.fileName);
/*      */     }
/*      */     
/* 1873 */     buildDecodingStream(entryIndex, true);
/* 1874 */     this.currentEntryIndex = entryIndex;
/* 1875 */     this.currentFolderIndex = this.archive.streamMap.fileFolderIndex[entryIndex];
/* 1876 */     return getCurrentStream();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int read(byte[] b) throws IOException {
/* 1888 */     return read(b, 0, b.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int read(byte[] b, int off, int len) throws IOException {
/* 1902 */     if (len == 0) {
/* 1903 */       return 0;
/*      */     }
/* 1905 */     int cnt = getCurrentStream().read(b, off, len);
/* 1906 */     if (cnt > 0) {
/* 1907 */       this.uncompressedBytesReadFromCurrentEntry += cnt;
/*      */     }
/* 1909 */     return cnt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStreamStatistics getStatisticsForCurrentEntry() {
/* 1919 */     return new InputStreamStatistics()
/*      */       {
/*      */         public long getCompressedCount() {
/* 1922 */           return SevenZFile.this.compressedBytesReadFromCurrentEntry;
/*      */         }
/*      */         
/*      */         public long getUncompressedCount() {
/* 1926 */           return SevenZFile.this.uncompressedBytesReadFromCurrentEntry;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */   
/*      */   private static long readUint64(ByteBuffer in) throws IOException {
/* 1933 */     long firstByte = getUnsignedByte(in);
/* 1934 */     int mask = 128;
/* 1935 */     long value = 0L;
/* 1936 */     for (int i = 0; i < 8; i++) {
/* 1937 */       if ((firstByte & mask) == 0L) {
/* 1938 */         return value | (firstByte & (mask - 1)) << 8 * i;
/*      */       }
/* 1940 */       long nextByte = getUnsignedByte(in);
/* 1941 */       value |= nextByte << 8 * i;
/* 1942 */       mask >>>= 1;
/*      */     } 
/* 1944 */     return value;
/*      */   }
/*      */   
/*      */   private static char getChar(ByteBuffer buf) throws IOException {
/* 1948 */     if (buf.remaining() < 2) {
/* 1949 */       throw new EOFException();
/*      */     }
/* 1951 */     return buf.getChar();
/*      */   }
/*      */   
/*      */   private static int getInt(ByteBuffer buf) throws IOException {
/* 1955 */     if (buf.remaining() < 4) {
/* 1956 */       throw new EOFException();
/*      */     }
/* 1958 */     return buf.getInt();
/*      */   }
/*      */   
/*      */   private static long getLong(ByteBuffer buf) throws IOException {
/* 1962 */     if (buf.remaining() < 8) {
/* 1963 */       throw new EOFException();
/*      */     }
/* 1965 */     return buf.getLong();
/*      */   }
/*      */   
/*      */   private static void get(ByteBuffer buf, byte[] to) throws IOException {
/* 1969 */     if (buf.remaining() < to.length) {
/* 1970 */       throw new EOFException();
/*      */     }
/* 1972 */     buf.get(to);
/*      */   }
/*      */   
/*      */   private static int getUnsignedByte(ByteBuffer buf) throws IOException {
/* 1976 */     if (!buf.hasRemaining()) {
/* 1977 */       throw new EOFException();
/*      */     }
/* 1979 */     return buf.get() & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean matches(byte[] signature, int length) {
/* 1993 */     if (length < sevenZSignature.length) {
/* 1994 */       return false;
/*      */     }
/*      */     
/* 1997 */     for (int i = 0; i < sevenZSignature.length; i++) {
/* 1998 */       if (signature[i] != sevenZSignature[i]) {
/* 1999 */         return false;
/*      */       }
/*      */     } 
/* 2002 */     return true;
/*      */   }
/*      */   
/*      */   private static long skipBytesFully(ByteBuffer input, long bytesToSkip) throws IOException {
/* 2006 */     if (bytesToSkip < 1L) {
/* 2007 */       return 0L;
/*      */     }
/* 2009 */     int current = input.position();
/* 2010 */     int maxSkip = input.remaining();
/* 2011 */     if (maxSkip < bytesToSkip) {
/* 2012 */       bytesToSkip = maxSkip;
/*      */     }
/* 2014 */     input.position(current + (int)bytesToSkip);
/* 2015 */     return bytesToSkip;
/*      */   }
/*      */   
/*      */   private void readFully(ByteBuffer buf) throws IOException {
/* 2019 */     buf.rewind();
/* 2020 */     IOUtils.readFully(this.channel, buf);
/* 2021 */     buf.flip();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2026 */     return this.archive.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDefaultName() {
/* 2047 */     if ("unknown archive".equals(this.fileName) || this.fileName == null) {
/* 2048 */       return null;
/*      */     }
/*      */     
/* 2051 */     String lastSegment = (new File(this.fileName)).getName();
/* 2052 */     int dotPos = lastSegment.lastIndexOf(".");
/* 2053 */     if (dotPos > 0) {
/* 2054 */       return lastSegment.substring(0, dotPos);
/*      */     }
/* 2056 */     return lastSegment + "~";
/*      */   }
/*      */   
/* 2059 */   private static final CharsetEncoder PASSWORD_ENCODER = StandardCharsets.UTF_16LE.newEncoder();
/*      */   
/*      */   private static byte[] utf16Decode(char[] chars) throws IOException {
/* 2062 */     if (chars == null) {
/* 2063 */       return null;
/*      */     }
/* 2065 */     ByteBuffer encoded = PASSWORD_ENCODER.encode(CharBuffer.wrap(chars));
/* 2066 */     if (encoded.hasArray()) {
/* 2067 */       return encoded.array();
/*      */     }
/* 2069 */     byte[] e = new byte[encoded.remaining()];
/* 2070 */     encoded.get(e);
/* 2071 */     return e;
/*      */   }
/*      */   
/*      */   private static int assertFitsIntoNonNegativeInt(String what, long value) throws IOException {
/* 2075 */     if (value > 2147483647L || value < 0L) {
/* 2076 */       throw new IOException("Cannot handle " + what + " " + value);
/*      */     }
/* 2078 */     return (int)value;
/*      */   }
/*      */   
/*      */   private static class ArchiveStatistics { private int numberOfPackedStreams;
/*      */     private long numberOfCoders;
/*      */     private long numberOfOutStreams;
/*      */     private long numberOfInStreams;
/*      */     private long numberOfUnpackSubStreams;
/*      */     private int numberOfFolders;
/*      */     private BitSet folderHasCrc;
/*      */     private int numberOfEntries;
/*      */     private int numberOfEntriesWithStream;
/*      */     
/*      */     private ArchiveStatistics() {}
/*      */     
/*      */     public String toString() {
/* 2094 */       return "Archive with " + this.numberOfEntries + " entries in " + this.numberOfFolders + " folders. Estimated size " + (
/* 2095 */         estimateSize() / 1024L) + " kB.";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long estimateSize() {
/* 2107 */       long lowerBound = 16L * this.numberOfPackedStreams + (this.numberOfPackedStreams / 8) + this.numberOfFolders * folderSize() + this.numberOfCoders * coderSize() + (this.numberOfOutStreams - this.numberOfFolders) * bindPairSize() + 8L * (this.numberOfInStreams - this.numberOfOutStreams + this.numberOfFolders) + 8L * this.numberOfOutStreams + this.numberOfEntries * entrySize() + streamMapSize();
/*      */       
/* 2109 */       return 2L * lowerBound;
/*      */     }
/*      */     
/*      */     void assertValidity(int maxMemoryLimitInKb) throws IOException {
/* 2113 */       if (this.numberOfEntriesWithStream > 0 && this.numberOfFolders == 0) {
/* 2114 */         throw new IOException("archive with entries but no folders");
/*      */       }
/* 2116 */       if (this.numberOfEntriesWithStream > this.numberOfUnpackSubStreams) {
/* 2117 */         throw new IOException("archive doesn't contain enough substreams for entries");
/*      */       }
/*      */       
/* 2120 */       long memoryNeededInKb = estimateSize() / 1024L;
/* 2121 */       if (maxMemoryLimitInKb < memoryNeededInKb) {
/* 2122 */         throw new MemoryLimitException(memoryNeededInKb, maxMemoryLimitInKb);
/*      */       }
/*      */     }
/*      */     
/*      */     private long folderSize() {
/* 2127 */       return 30L;
/*      */     }
/*      */     
/*      */     private long coderSize() {
/* 2131 */       return 22L;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long bindPairSize() {
/* 2138 */       return 16L;
/*      */     }
/*      */     
/*      */     private long entrySize() {
/* 2142 */       return 100L;
/*      */     }
/*      */     
/*      */     private long streamMapSize() {
/* 2146 */       return (8 * this.numberOfFolders + 8 * this.numberOfPackedStreams + 4 * this.numberOfEntries);
/*      */     } }
/*      */ 
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\SevenZFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */