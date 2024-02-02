/*      */ package org.apache.commons.compress.archivers.zip;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.EOFException;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.SequenceInputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.SeekableByteChannel;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.StandardOpenOption;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.EnumSet;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.zip.Inflater;
/*      */ import java.util.zip.ZipException;
/*      */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
/*      */ import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
/*      */ import org.apache.commons.compress.utils.BoundedArchiveInputStream;
/*      */ import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
/*      */ import org.apache.commons.compress.utils.CountingInputStream;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ZipFile
/*      */   implements Closeable
/*      */ {
/*      */   private static final int HASH_SIZE = 509;
/*      */   static final int NIBLET_MASK = 15;
/*      */   static final int BYTE_SHIFT = 8;
/*      */   private static final int POS_0 = 0;
/*      */   private static final int POS_1 = 1;
/*      */   private static final int POS_2 = 2;
/*      */   private static final int POS_3 = 3;
/*   99 */   private static final byte[] ONE_ZERO_BYTE = new byte[1];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  105 */   private final List<ZipArchiveEntry> entries = new LinkedList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  111 */   private final Map<String, LinkedList<ZipArchiveEntry>> nameMap = new HashMap<>(509);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String encoding;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ZipEncoding zipEncoding;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String archiveName;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final SeekableByteChannel archive;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean useUnicodeExtraFields;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile boolean closed = true;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean isSplitZipArchive;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  154 */   private final byte[] dwordBuf = new byte[8];
/*  155 */   private final byte[] wordBuf = new byte[4];
/*  156 */   private final byte[] cfhBuf = new byte[42];
/*  157 */   private final byte[] shortBuf = new byte[2];
/*  158 */   private final ByteBuffer dwordBbuf = ByteBuffer.wrap(this.dwordBuf);
/*  159 */   private final ByteBuffer wordBbuf = ByteBuffer.wrap(this.wordBuf);
/*  160 */   private final ByteBuffer cfhBbuf = ByteBuffer.wrap(this.cfhBuf);
/*  161 */   private final ByteBuffer shortBbuf = ByteBuffer.wrap(this.shortBuf);
/*      */ 
/*      */   
/*      */   private long centralDirectoryStartDiskNumber;
/*      */   
/*      */   private long centralDirectoryStartRelativeOffset;
/*      */   
/*      */   private long centralDirectoryStartOffset;
/*      */   
/*      */   private static final int CFH_LEN = 42;
/*      */ 
/*      */   
/*      */   public ZipFile(File f) throws IOException {
/*  174 */     this(f, "UTF8");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipFile(String name) throws IOException {
/*  185 */     this(new File(name), "UTF8");
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
/*      */   public ZipFile(String name, String encoding) throws IOException {
/*  199 */     this(new File(name), encoding, true);
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
/*      */   public ZipFile(File f, String encoding) throws IOException {
/*  213 */     this(f, encoding, true);
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
/*      */   public ZipFile(File f, String encoding, boolean useUnicodeExtraFields) throws IOException {
/*  230 */     this(f, encoding, useUnicodeExtraFields, false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipFile(File f, String encoding, boolean useUnicodeExtraFields, boolean ignoreLocalFileHeader) throws IOException {
/*  261 */     this(Files.newByteChannel(f.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), f
/*  262 */         .getAbsolutePath(), encoding, useUnicodeExtraFields, true, ignoreLocalFileHeader);
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
/*      */   public ZipFile(SeekableByteChannel channel) throws IOException {
/*  279 */     this(channel, "unknown archive", "UTF8", true);
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
/*      */   public ZipFile(SeekableByteChannel channel, String encoding) throws IOException {
/*  299 */     this(channel, "unknown archive", encoding, true);
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
/*      */   public ZipFile(SeekableByteChannel channel, String archiveName, String encoding, boolean useUnicodeExtraFields) throws IOException {
/*  323 */     this(channel, archiveName, encoding, useUnicodeExtraFields, false, false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipFile(SeekableByteChannel channel, String archiveName, String encoding, boolean useUnicodeExtraFields, boolean ignoreLocalFileHeader) throws IOException {
/*  359 */     this(channel, archiveName, encoding, useUnicodeExtraFields, false, ignoreLocalFileHeader);
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
/*      */   public String getEncoding() {
/*  398 */     return this.encoding;
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
/*      */   public void close() throws IOException {
/*  410 */     this.closed = true;
/*      */     
/*  412 */     this.archive.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void closeQuietly(ZipFile zipfile) {
/*  421 */     IOUtils.closeQuietly(zipfile);
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
/*      */   public Enumeration<ZipArchiveEntry> getEntries() {
/*  433 */     return Collections.enumeration(this.entries);
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
/*      */   public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder() {
/*  447 */     ZipArchiveEntry[] allEntries = this.entries.<ZipArchiveEntry>toArray(ZipArchiveEntry.EMPTY_ZIP_ARCHIVE_ENTRY_ARRAY);
/*  448 */     Arrays.sort(allEntries, this.offsetComparator);
/*  449 */     return Collections.enumeration(Arrays.asList(allEntries));
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
/*      */   public ZipArchiveEntry getEntry(String name) {
/*  465 */     LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
/*  466 */     return (entriesOfThatName != null) ? entriesOfThatName.getFirst() : null;
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
/*      */   public Iterable<ZipArchiveEntry> getEntries(String name) {
/*  479 */     List<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
/*  480 */     return (entriesOfThatName != null) ? entriesOfThatName : 
/*  481 */       Collections.<ZipArchiveEntry>emptyList();
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
/*      */   public Iterable<ZipArchiveEntry> getEntriesInPhysicalOrder(String name) {
/*  494 */     ZipArchiveEntry[] entriesOfThatName = ZipArchiveEntry.EMPTY_ZIP_ARCHIVE_ENTRY_ARRAY;
/*  495 */     if (this.nameMap.containsKey(name)) {
/*  496 */       entriesOfThatName = (ZipArchiveEntry[])((LinkedList)this.nameMap.get(name)).toArray((Object[])entriesOfThatName);
/*  497 */       Arrays.sort(entriesOfThatName, this.offsetComparator);
/*      */     } 
/*  499 */     return Arrays.asList(entriesOfThatName);
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
/*      */   public boolean canReadEntryData(ZipArchiveEntry ze) {
/*  512 */     return ZipUtil.canHandleEntryData(ze);
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
/*      */   public InputStream getRawInputStream(ZipArchiveEntry ze) {
/*  526 */     if (!(ze instanceof Entry)) {
/*  527 */       return null;
/*      */     }
/*  529 */     long start = ze.getDataOffset();
/*  530 */     if (start == -1L) {
/*  531 */       return null;
/*      */     }
/*  533 */     return (InputStream)createBoundedInputStream(start, ze.getCompressedSize());
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
/*      */   public void copyRawEntries(ZipArchiveOutputStream target, ZipArchiveEntryPredicate predicate) throws IOException {
/*  548 */     Enumeration<ZipArchiveEntry> src = getEntriesInPhysicalOrder();
/*  549 */     while (src.hasMoreElements()) {
/*  550 */       ZipArchiveEntry entry = src.nextElement();
/*  551 */       if (predicate.test(entry)) {
/*  552 */         target.addRawArchiveEntry(entry, getRawInputStream(entry));
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
/*      */   
/*      */   public InputStream getInputStream(ZipArchiveEntry ze) throws IOException {
/*      */     final Inflater inflater;
/*  567 */     if (!(ze instanceof Entry)) {
/*  568 */       return null;
/*      */     }
/*      */     
/*  571 */     ZipUtil.checkRequestedFeatures(ze);
/*  572 */     long start = getDataOffset(ze);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  578 */     InputStream is = new BufferedInputStream((InputStream)createBoundedInputStream(start, ze.getCompressedSize()));
/*  579 */     switch (ZipMethod.getMethodByCode(ze.getMethod())) {
/*      */       case STORED:
/*  581 */         return (InputStream)new StoredStatisticsStream(is);
/*      */       case UNSHRINKING:
/*  583 */         return (InputStream)new UnshrinkingInputStream(is);
/*      */       case IMPLODING:
/*      */         try {
/*  586 */           return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze
/*  587 */               .getGeneralPurposeBit().getNumberOfShannonFanoTrees(), is);
/*  588 */         } catch (IllegalArgumentException ex) {
/*  589 */           throw new IOException("bad IMPLODE data", ex);
/*      */         } 
/*      */       case DEFLATED:
/*  592 */         inflater = new Inflater(true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  598 */         return new InflaterInputStreamWithStatistics(new SequenceInputStream(is, new ByteArrayInputStream(ONE_ZERO_BYTE)), inflater)
/*      */           {
/*      */             public void close() throws IOException
/*      */             {
/*      */               try {
/*  603 */                 super.close();
/*      */               } finally {
/*  605 */                 inflater.end();
/*      */               } 
/*      */             }
/*      */           };
/*      */       case BZIP2:
/*  610 */         return (InputStream)new BZip2CompressorInputStream(is);
/*      */       case ENHANCED_DEFLATED:
/*  612 */         return (InputStream)new Deflate64CompressorInputStream(is);
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
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  627 */     throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(ze.getMethod()), ze);
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
/*      */   public String getUnixSymlink(ZipArchiveEntry entry) throws IOException {
/*  646 */     if (entry != null && entry.isUnixSymlink()) {
/*  647 */       try (InputStream in = getInputStream(entry)) {
/*  648 */         return this.zipEncoding.decode(IOUtils.toByteArray(in));
/*      */       } 
/*      */     }
/*  651 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void finalize() throws Throwable {
/*      */     try {
/*  662 */       if (!this.closed) {
/*  663 */         System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
/*      */         
/*  665 */         close();
/*      */       } 
/*      */     } finally {
/*  668 */       super.finalize();
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
/*  695 */   private static final long CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
/*      */   static final int MIN_EOCD_SIZE = 22;
/*      */   private static final int MAX_EOCD_SIZE = 65557;
/*      */   private static final int CFD_LOCATOR_OFFSET = 16;
/*      */   private static final int CFD_DISK_OFFSET = 6;
/*      */   private static final int CFD_LOCATOR_RELATIVE_OFFSET = 8;
/*      */   private static final int ZIP64_EOCDL_LENGTH = 20;
/*      */   private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
/*      */   private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
/*      */   private static final int ZIP64_EOCD_CFD_DISK_OFFSET = 20;
/*      */   private static final int ZIP64_EOCD_CFD_LOCATOR_RELATIVE_OFFSET = 24;
/*      */   private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
/*      */   private final Comparator<ZipArchiveEntry> offsetComparator;
/*      */   
/*      */   private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
/*  710 */     HashMap<ZipArchiveEntry, NameAndComment> noUTF8Flag = new HashMap<>();
/*      */ 
/*      */     
/*  713 */     positionAtCentralDirectory();
/*  714 */     this.centralDirectoryStartOffset = this.archive.position();
/*      */     
/*  716 */     this.wordBbuf.rewind();
/*  717 */     IOUtils.readFully(this.archive, this.wordBbuf);
/*  718 */     long sig = ZipLong.getValue(this.wordBuf);
/*      */     
/*  720 */     if (sig != CFH_SIG && startsWithLocalFileHeader()) {
/*  721 */       throw new IOException("Central directory is empty, can't expand corrupt archive.");
/*      */     }
/*      */ 
/*      */     
/*  725 */     while (sig == CFH_SIG) {
/*  726 */       readCentralDirectoryEntry(noUTF8Flag);
/*  727 */       this.wordBbuf.rewind();
/*  728 */       IOUtils.readFully(this.archive, this.wordBbuf);
/*  729 */       sig = ZipLong.getValue(this.wordBuf);
/*      */     } 
/*  731 */     return noUTF8Flag;
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
/*      */   private void readCentralDirectoryEntry(Map<ZipArchiveEntry, NameAndComment> noUTF8Flag) throws IOException {
/*  746 */     this.cfhBbuf.rewind();
/*  747 */     IOUtils.readFully(this.archive, this.cfhBbuf);
/*  748 */     int off = 0;
/*  749 */     Entry ze = new Entry();
/*      */     
/*  751 */     int versionMadeBy = ZipShort.getValue(this.cfhBuf, off);
/*  752 */     off += 2;
/*  753 */     ze.setVersionMadeBy(versionMadeBy);
/*  754 */     ze.setPlatform(versionMadeBy >> 8 & 0xF);
/*      */     
/*  756 */     ze.setVersionRequired(ZipShort.getValue(this.cfhBuf, off));
/*  757 */     off += 2;
/*      */     
/*  759 */     GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.cfhBuf, off);
/*  760 */     boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
/*  761 */     ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*      */     
/*  763 */     if (hasUTF8Flag) {
/*  764 */       ze.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
/*      */     }
/*  766 */     ze.setGeneralPurposeBit(gpFlag);
/*  767 */     ze.setRawFlag(ZipShort.getValue(this.cfhBuf, off));
/*      */     
/*  769 */     off += 2;
/*      */ 
/*      */     
/*  772 */     ze.setMethod(ZipShort.getValue(this.cfhBuf, off));
/*  773 */     off += 2;
/*      */     
/*  775 */     long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.cfhBuf, off));
/*  776 */     ze.setTime(time);
/*  777 */     off += 4;
/*      */     
/*  779 */     ze.setCrc(ZipLong.getValue(this.cfhBuf, off));
/*  780 */     off += 4;
/*      */     
/*  782 */     long size = ZipLong.getValue(this.cfhBuf, off);
/*  783 */     if (size < 0L) {
/*  784 */       throw new IOException("broken archive, entry with negative compressed size");
/*      */     }
/*  786 */     ze.setCompressedSize(size);
/*  787 */     off += 4;
/*      */     
/*  789 */     size = ZipLong.getValue(this.cfhBuf, off);
/*  790 */     if (size < 0L) {
/*  791 */       throw new IOException("broken archive, entry with negative size");
/*      */     }
/*  793 */     ze.setSize(size);
/*  794 */     off += 4;
/*      */     
/*  796 */     int fileNameLen = ZipShort.getValue(this.cfhBuf, off);
/*  797 */     off += 2;
/*  798 */     if (fileNameLen < 0) {
/*  799 */       throw new IOException("broken archive, entry with negative fileNameLen");
/*      */     }
/*      */     
/*  802 */     int extraLen = ZipShort.getValue(this.cfhBuf, off);
/*  803 */     off += 2;
/*  804 */     if (extraLen < 0) {
/*  805 */       throw new IOException("broken archive, entry with negative extraLen");
/*      */     }
/*      */     
/*  808 */     int commentLen = ZipShort.getValue(this.cfhBuf, off);
/*  809 */     off += 2;
/*  810 */     if (commentLen < 0) {
/*  811 */       throw new IOException("broken archive, entry with negative commentLen");
/*      */     }
/*      */     
/*  814 */     ze.setDiskNumberStart(ZipShort.getValue(this.cfhBuf, off));
/*  815 */     off += 2;
/*      */     
/*  817 */     ze.setInternalAttributes(ZipShort.getValue(this.cfhBuf, off));
/*  818 */     off += 2;
/*      */     
/*  820 */     ze.setExternalAttributes(ZipLong.getValue(this.cfhBuf, off));
/*  821 */     off += 4;
/*      */     
/*  823 */     byte[] fileName = IOUtils.readRange(this.archive, fileNameLen);
/*  824 */     if (fileName.length < fileNameLen) {
/*  825 */       throw new EOFException();
/*      */     }
/*  827 */     ze.setName(entryEncoding.decode(fileName), fileName);
/*      */ 
/*      */     
/*  830 */     ze.setLocalHeaderOffset(ZipLong.getValue(this.cfhBuf, off));
/*      */     
/*  832 */     this.entries.add(ze);
/*      */     
/*  834 */     byte[] cdExtraData = IOUtils.readRange(this.archive, extraLen);
/*  835 */     if (cdExtraData.length < extraLen) {
/*  836 */       throw new EOFException();
/*      */     }
/*      */     try {
/*  839 */       ze.setCentralDirectoryExtra(cdExtraData);
/*  840 */     } catch (RuntimeException ex) {
/*  841 */       ZipException z = new ZipException("Invalid extra data in entry " + ze.getName());
/*  842 */       z.initCause(ex);
/*  843 */       throw z;
/*      */     } 
/*      */     
/*  846 */     setSizesAndOffsetFromZip64Extra(ze);
/*  847 */     sanityCheckLFHOffset(ze);
/*      */     
/*  849 */     byte[] comment = IOUtils.readRange(this.archive, commentLen);
/*  850 */     if (comment.length < commentLen) {
/*  851 */       throw new EOFException();
/*      */     }
/*  853 */     ze.setComment(entryEncoding.decode(comment));
/*      */     
/*  855 */     if (!hasUTF8Flag && this.useUnicodeExtraFields) {
/*  856 */       noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
/*      */     }
/*      */     
/*  859 */     ze.setStreamContiguous(true);
/*      */   }
/*      */   
/*      */   private void sanityCheckLFHOffset(ZipArchiveEntry ze) throws IOException {
/*  863 */     if (ze.getDiskNumberStart() < 0L) {
/*  864 */       throw new IOException("broken archive, entry with negative disk number");
/*      */     }
/*  866 */     if (ze.getLocalHeaderOffset() < 0L) {
/*  867 */       throw new IOException("broken archive, entry with negative local file header offset");
/*      */     }
/*  869 */     if (this.isSplitZipArchive) {
/*  870 */       if (ze.getDiskNumberStart() > this.centralDirectoryStartDiskNumber) {
/*  871 */         throw new IOException("local file header for " + ze.getName() + " starts on a later disk than central directory");
/*      */       }
/*  873 */       if (ze.getDiskNumberStart() == this.centralDirectoryStartDiskNumber && ze
/*  874 */         .getLocalHeaderOffset() > this.centralDirectoryStartRelativeOffset) {
/*  875 */         throw new IOException("local file header for " + ze.getName() + " starts after central directory");
/*      */       }
/*      */     }
/*  878 */     else if (ze.getLocalHeaderOffset() > this.centralDirectoryStartOffset) {
/*  879 */       throw new IOException("local file header for " + ze.getName() + " starts after central directory");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setSizesAndOffsetFromZip64Extra(ZipArchiveEntry ze) throws IOException {
/*  899 */     ZipExtraField extra = ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  900 */     if (extra != null && !(extra instanceof Zip64ExtendedInformationExtraField)) {
/*  901 */       throw new ZipException("archive contains unparseable zip64 extra field");
/*      */     }
/*  903 */     Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)extra;
/*      */     
/*  905 */     if (z64 != null) {
/*  906 */       boolean hasUncompressedSize = (ze.getSize() == 4294967295L);
/*  907 */       boolean hasCompressedSize = (ze.getCompressedSize() == 4294967295L);
/*      */       
/*  909 */       boolean hasRelativeHeaderOffset = (ze.getLocalHeaderOffset() == 4294967295L);
/*  910 */       boolean hasDiskStart = (ze.getDiskNumberStart() == 65535L);
/*  911 */       z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, hasDiskStart);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  916 */       if (hasUncompressedSize) {
/*  917 */         long size = z64.getSize().getLongValue();
/*  918 */         if (size < 0L) {
/*  919 */           throw new IOException("broken archive, entry with negative size");
/*      */         }
/*  921 */         ze.setSize(size);
/*  922 */       } else if (hasCompressedSize) {
/*  923 */         z64.setSize(new ZipEightByteInteger(ze.getSize()));
/*      */       } 
/*      */       
/*  926 */       if (hasCompressedSize) {
/*  927 */         long size = z64.getCompressedSize().getLongValue();
/*  928 */         if (size < 0L) {
/*  929 */           throw new IOException("broken archive, entry with negative compressed size");
/*      */         }
/*  931 */         ze.setCompressedSize(size);
/*  932 */       } else if (hasUncompressedSize) {
/*  933 */         z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
/*      */       } 
/*      */       
/*  936 */       if (hasRelativeHeaderOffset) {
/*  937 */         ze.setLocalHeaderOffset(z64.getRelativeHeaderOffset().getLongValue());
/*      */       }
/*      */       
/*  940 */       if (hasDiskStart) {
/*  941 */         ze.setDiskNumberStart(z64.getDiskStartNumber().getValue());
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
/*      */   private void positionAtCentralDirectory() throws IOException {
/* 1097 */     positionAtEndOfCentralDirectoryRecord();
/* 1098 */     boolean found = false;
/*      */     
/* 1100 */     boolean searchedForZip64EOCD = (this.archive.position() > 20L);
/* 1101 */     if (searchedForZip64EOCD) {
/* 1102 */       this.archive.position(this.archive.position() - 20L);
/* 1103 */       this.wordBbuf.rewind();
/* 1104 */       IOUtils.readFully(this.archive, this.wordBbuf);
/* 1105 */       found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.wordBuf);
/*      */     } 
/*      */     
/* 1108 */     if (!found) {
/*      */       
/* 1110 */       if (searchedForZip64EOCD) {
/* 1111 */         skipBytes(16);
/*      */       }
/* 1113 */       positionAtCentralDirectory32();
/*      */     } else {
/* 1115 */       positionAtCentralDirectory64();
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
/*      */ 
/*      */ 
/*      */   
/*      */   private void positionAtCentralDirectory64() throws IOException {
/* 1130 */     if (this.isSplitZipArchive) {
/* 1131 */       this.wordBbuf.rewind();
/* 1132 */       IOUtils.readFully(this.archive, this.wordBbuf);
/* 1133 */       long diskNumberOfEOCD = ZipLong.getValue(this.wordBuf);
/*      */       
/* 1135 */       this.dwordBbuf.rewind();
/* 1136 */       IOUtils.readFully(this.archive, this.dwordBbuf);
/* 1137 */       long relativeOffsetOfEOCD = ZipEightByteInteger.getLongValue(this.dwordBuf);
/* 1138 */       ((ZipSplitReadOnlySeekableByteChannel)this.archive)
/* 1139 */         .position(diskNumberOfEOCD, relativeOffsetOfEOCD);
/*      */     } else {
/* 1141 */       skipBytes(4);
/*      */       
/* 1143 */       this.dwordBbuf.rewind();
/* 1144 */       IOUtils.readFully(this.archive, this.dwordBbuf);
/* 1145 */       this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
/*      */     } 
/*      */     
/* 1148 */     this.wordBbuf.rewind();
/* 1149 */     IOUtils.readFully(this.archive, this.wordBbuf);
/* 1150 */     if (!Arrays.equals(this.wordBuf, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
/* 1151 */       throw new ZipException("Archive's ZIP64 end of central directory locator is corrupt.");
/*      */     }
/*      */ 
/*      */     
/* 1155 */     if (this.isSplitZipArchive) {
/* 1156 */       skipBytes(16);
/*      */       
/* 1158 */       this.wordBbuf.rewind();
/* 1159 */       IOUtils.readFully(this.archive, this.wordBbuf);
/* 1160 */       this.centralDirectoryStartDiskNumber = ZipLong.getValue(this.wordBuf);
/*      */       
/* 1162 */       skipBytes(24);
/*      */       
/* 1164 */       this.dwordBbuf.rewind();
/* 1165 */       IOUtils.readFully(this.archive, this.dwordBbuf);
/* 1166 */       this.centralDirectoryStartRelativeOffset = ZipEightByteInteger.getLongValue(this.dwordBuf);
/* 1167 */       ((ZipSplitReadOnlySeekableByteChannel)this.archive)
/* 1168 */         .position(this.centralDirectoryStartDiskNumber, this.centralDirectoryStartRelativeOffset);
/*      */     } else {
/* 1170 */       skipBytes(44);
/*      */       
/* 1172 */       this.dwordBbuf.rewind();
/* 1173 */       IOUtils.readFully(this.archive, this.dwordBbuf);
/* 1174 */       this.centralDirectoryStartDiskNumber = 0L;
/* 1175 */       this.centralDirectoryStartRelativeOffset = ZipEightByteInteger.getLongValue(this.dwordBuf);
/* 1176 */       this.archive.position(this.centralDirectoryStartRelativeOffset);
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
/*      */   
/*      */   private void positionAtCentralDirectory32() throws IOException {
/* 1189 */     if (this.isSplitZipArchive) {
/* 1190 */       skipBytes(6);
/* 1191 */       this.shortBbuf.rewind();
/* 1192 */       IOUtils.readFully(this.archive, this.shortBbuf);
/* 1193 */       this.centralDirectoryStartDiskNumber = ZipShort.getValue(this.shortBuf);
/*      */       
/* 1195 */       skipBytes(8);
/*      */       
/* 1197 */       this.wordBbuf.rewind();
/* 1198 */       IOUtils.readFully(this.archive, this.wordBbuf);
/* 1199 */       this.centralDirectoryStartRelativeOffset = ZipLong.getValue(this.wordBuf);
/* 1200 */       ((ZipSplitReadOnlySeekableByteChannel)this.archive)
/* 1201 */         .position(this.centralDirectoryStartDiskNumber, this.centralDirectoryStartRelativeOffset);
/*      */     } else {
/* 1203 */       skipBytes(16);
/* 1204 */       this.wordBbuf.rewind();
/* 1205 */       IOUtils.readFully(this.archive, this.wordBbuf);
/* 1206 */       this.centralDirectoryStartDiskNumber = 0L;
/* 1207 */       this.centralDirectoryStartRelativeOffset = ZipLong.getValue(this.wordBuf);
/* 1208 */       this.archive.position(this.centralDirectoryStartRelativeOffset);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void positionAtEndOfCentralDirectoryRecord() throws IOException {
/* 1218 */     boolean found = tryToLocateSignature(22L, 65557L, ZipArchiveOutputStream.EOCD_SIG);
/*      */     
/* 1220 */     if (!found) {
/* 1221 */       throw new ZipException("Archive is not a ZIP archive");
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
/*      */   private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig) throws IOException {
/* 1233 */     boolean found = false;
/* 1234 */     long off = this.archive.size() - minDistanceFromEnd;
/*      */     
/* 1236 */     long stopSearching = Math.max(0L, this.archive.size() - maxDistanceFromEnd);
/* 1237 */     if (off >= 0L) {
/* 1238 */       for (; off >= stopSearching; off--) {
/* 1239 */         this.archive.position(off);
/*      */         try {
/* 1241 */           this.wordBbuf.rewind();
/* 1242 */           IOUtils.readFully(this.archive, this.wordBbuf);
/* 1243 */           this.wordBbuf.flip();
/* 1244 */         } catch (EOFException ex) {
/*      */           break;
/*      */         } 
/* 1247 */         int curr = this.wordBbuf.get();
/* 1248 */         if (curr == sig[0]) {
/* 1249 */           curr = this.wordBbuf.get();
/* 1250 */           if (curr == sig[1]) {
/* 1251 */             curr = this.wordBbuf.get();
/* 1252 */             if (curr == sig[2]) {
/* 1253 */               curr = this.wordBbuf.get();
/* 1254 */               if (curr == sig[3]) {
/* 1255 */                 found = true;
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/* 1263 */     if (found) {
/* 1264 */       this.archive.position(off);
/*      */     }
/* 1266 */     return found;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void skipBytes(int count) throws IOException {
/* 1274 */     long currentPosition = this.archive.position();
/* 1275 */     long newPosition = currentPosition + count;
/* 1276 */     if (newPosition > this.archive.size()) {
/* 1277 */       throw new EOFException();
/*      */     }
/* 1279 */     this.archive.position(newPosition);
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
/*      */   
/*      */   private void resolveLocalFileHeaderData(Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag) throws IOException {
/* 1307 */     for (ZipArchiveEntry zipArchiveEntry : this.entries) {
/*      */ 
/*      */       
/* 1310 */       Entry ze = (Entry)zipArchiveEntry;
/* 1311 */       int[] lens = setDataOffset(ze);
/* 1312 */       int fileNameLen = lens[0];
/* 1313 */       int extraFieldLen = lens[1];
/* 1314 */       skipBytes(fileNameLen);
/* 1315 */       byte[] localExtraData = IOUtils.readRange(this.archive, extraFieldLen);
/* 1316 */       if (localExtraData.length < extraFieldLen) {
/* 1317 */         throw new EOFException();
/*      */       }
/*      */       try {
/* 1320 */         ze.setExtra(localExtraData);
/* 1321 */       } catch (RuntimeException ex) {
/* 1322 */         ZipException z = new ZipException("Invalid extra data in entry " + ze.getName());
/* 1323 */         z.initCause(ex);
/* 1324 */         throw z;
/*      */       } 
/*      */       
/* 1327 */       if (entriesWithoutUTF8Flag.containsKey(ze)) {
/* 1328 */         NameAndComment nc = entriesWithoutUTF8Flag.get(ze);
/* 1329 */         ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc
/* 1330 */             .comment);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void fillNameMap() {
/* 1336 */     for (ZipArchiveEntry ze : this.entries) {
/*      */ 
/*      */       
/* 1339 */       String name = ze.getName();
/* 1340 */       LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.computeIfAbsent(name, k -> new LinkedList());
/* 1341 */       entriesOfThatName.addLast(ze);
/*      */     } 
/*      */   }
/*      */   
/*      */   private int[] setDataOffset(ZipArchiveEntry ze) throws IOException {
/* 1346 */     long offset = ze.getLocalHeaderOffset();
/* 1347 */     if (this.isSplitZipArchive) {
/* 1348 */       ((ZipSplitReadOnlySeekableByteChannel)this.archive)
/* 1349 */         .position(ze.getDiskNumberStart(), offset + 26L);
/*      */       
/* 1351 */       offset = this.archive.position() - 26L;
/*      */     } else {
/* 1353 */       this.archive.position(offset + 26L);
/*      */     } 
/* 1355 */     this.wordBbuf.rewind();
/* 1356 */     IOUtils.readFully(this.archive, this.wordBbuf);
/* 1357 */     this.wordBbuf.flip();
/* 1358 */     this.wordBbuf.get(this.shortBuf);
/* 1359 */     int fileNameLen = ZipShort.getValue(this.shortBuf);
/* 1360 */     this.wordBbuf.get(this.shortBuf);
/* 1361 */     int extraFieldLen = ZipShort.getValue(this.shortBuf);
/* 1362 */     ze.setDataOffset(offset + 26L + 2L + 2L + fileNameLen + extraFieldLen);
/*      */     
/* 1364 */     if (ze.getDataOffset() + ze.getCompressedSize() > this.centralDirectoryStartOffset) {
/* 1365 */       throw new IOException("data for " + ze.getName() + " overlaps with central directory.");
/*      */     }
/* 1367 */     return new int[] { fileNameLen, extraFieldLen };
/*      */   }
/*      */   
/*      */   private long getDataOffset(ZipArchiveEntry ze) throws IOException {
/* 1371 */     long s = ze.getDataOffset();
/* 1372 */     if (s == -1L) {
/* 1373 */       setDataOffset(ze);
/* 1374 */       return ze.getDataOffset();
/*      */     } 
/* 1376 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean startsWithLocalFileHeader() throws IOException {
/* 1384 */     this.archive.position(0L);
/* 1385 */     this.wordBbuf.rewind();
/* 1386 */     IOUtils.readFully(this.archive, this.wordBbuf);
/* 1387 */     return Arrays.equals(this.wordBuf, ZipArchiveOutputStream.LFH_SIG);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BoundedArchiveInputStream createBoundedInputStream(long start, long remaining) {
/* 1395 */     if (start < 0L || remaining < 0L || start + remaining < start) {
/* 1396 */       throw new IllegalArgumentException("Corrupted archive, stream boundaries are out of range");
/*      */     }
/*      */     
/* 1399 */     return (this.archive instanceof FileChannel) ? new BoundedFileChannelInputStream(start, remaining) : (BoundedArchiveInputStream)new BoundedSeekableByteChannelInputStream(start, remaining, this.archive);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class BoundedFileChannelInputStream
/*      */     extends BoundedArchiveInputStream
/*      */   {
/*      */     private final FileChannel archive;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     BoundedFileChannelInputStream(long start, long remaining) {
/* 1414 */       super(start, remaining);
/* 1415 */       this.archive = (FileChannel)ZipFile.this.archive;
/*      */     }
/*      */ 
/*      */     
/*      */     protected int read(long pos, ByteBuffer buf) throws IOException {
/* 1420 */       int read = this.archive.read(buf, pos);
/* 1421 */       buf.flip();
/* 1422 */       return read;
/*      */     } }
/*      */   
/*      */   private static final class NameAndComment {
/*      */     private final byte[] name;
/*      */     private final byte[] comment;
/*      */     
/*      */     private NameAndComment(byte[] name, byte[] comment) {
/* 1430 */       this.name = name;
/* 1431 */       this.comment = comment;
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
/*      */   private ZipFile(SeekableByteChannel channel, String archiveName, String encoding, boolean useUnicodeExtraFields, boolean closeOnError, boolean ignoreLocalFileHeader) throws IOException {
/* 1443 */     this
/*      */       
/* 1445 */       .offsetComparator = Comparator.<ZipArchiveEntry>comparingLong(ZipArchiveEntry::getDiskNumberStart).thenComparingLong(ZipArchiveEntry::getLocalHeaderOffset); this.isSplitZipArchive = channel instanceof ZipSplitReadOnlySeekableByteChannel; this.archiveName = archiveName; this.encoding = encoding; this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding); this.useUnicodeExtraFields = useUnicodeExtraFields; this.archive = channel; boolean success = false; try {
/*      */       Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag = populateFromCentralDirectory();
/*      */       if (!ignoreLocalFileHeader)
/*      */         resolveLocalFileHeaderData(entriesWithoutUTF8Flag); 
/*      */       fillNameMap();
/*      */       success = true;
/*      */     } catch (IOException e) {
/*      */       throw new IOException("Error on ZipFile " + archiveName, e);
/*      */     } finally {
/*      */       this.closed = !success;
/*      */       if (!success && closeOnError)
/*      */         IOUtils.closeQuietly(this.archive); 
/* 1457 */     }  } private static class Entry extends ZipArchiveEntry { public int hashCode() { return 3 * super.hashCode() + 
/* 1458 */         (int)getLocalHeaderOffset() + (int)(getLocalHeaderOffset() >> 32L); }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean equals(Object other) {
/* 1463 */       if (super.equals(other)) {
/*      */         
/* 1465 */         Entry otherEntry = (Entry)other;
/* 1466 */         return (getLocalHeaderOffset() == otherEntry
/* 1467 */           .getLocalHeaderOffset() && 
/* 1468 */           getDataOffset() == otherEntry
/* 1469 */           .getDataOffset() && 
/* 1470 */           getDiskNumberStart() == otherEntry
/* 1471 */           .getDiskNumberStart());
/*      */       } 
/* 1473 */       return false;
/*      */     } }
/*      */ 
/*      */   
/*      */   private static class StoredStatisticsStream extends CountingInputStream implements InputStreamStatistics {
/*      */     StoredStatisticsStream(InputStream in) {
/* 1479 */       super(in);
/*      */     }
/*      */ 
/*      */     
/*      */     public long getCompressedCount() {
/* 1484 */       return getBytesRead();
/*      */     }
/*      */ 
/*      */     
/*      */     public long getUncompressedCount() {
/* 1489 */       return getCompressedCount();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */