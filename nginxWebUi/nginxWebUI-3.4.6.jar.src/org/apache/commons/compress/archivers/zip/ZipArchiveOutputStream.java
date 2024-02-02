/*      */ package org.apache.commons.compress.archivers.zip;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.SeekableByteChannel;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.LinkOption;
/*      */ import java.nio.file.OpenOption;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.StandardOpenOption;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.util.Calendar;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.zip.Deflater;
/*      */ import java.util.zip.ZipException;
/*      */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*      */ import org.apache.commons.compress.utils.ByteUtils;
/*      */ import org.apache.commons.compress.utils.IOUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ZipArchiveOutputStream
/*      */   extends ArchiveOutputStream
/*      */ {
/*      */   static final int BUFFER_SIZE = 512;
/*      */   private static final int LFH_SIG_OFFSET = 0;
/*      */   private static final int LFH_VERSION_NEEDED_OFFSET = 4;
/*      */   private static final int LFH_GPB_OFFSET = 6;
/*      */   private static final int LFH_METHOD_OFFSET = 8;
/*      */   private static final int LFH_TIME_OFFSET = 10;
/*      */   private static final int LFH_CRC_OFFSET = 14;
/*      */   private static final int LFH_COMPRESSED_SIZE_OFFSET = 18;
/*      */   private static final int LFH_ORIGINAL_SIZE_OFFSET = 22;
/*      */   private static final int LFH_FILENAME_LENGTH_OFFSET = 26;
/*      */   private static final int LFH_EXTRA_LENGTH_OFFSET = 28;
/*      */   private static final int LFH_FILENAME_OFFSET = 30;
/*      */   private static final int CFH_SIG_OFFSET = 0;
/*      */   private static final int CFH_VERSION_MADE_BY_OFFSET = 4;
/*      */   private static final int CFH_VERSION_NEEDED_OFFSET = 6;
/*      */   private static final int CFH_GPB_OFFSET = 8;
/*      */   private static final int CFH_METHOD_OFFSET = 10;
/*      */   private static final int CFH_TIME_OFFSET = 12;
/*      */   private static final int CFH_CRC_OFFSET = 16;
/*      */   private static final int CFH_COMPRESSED_SIZE_OFFSET = 20;
/*      */   private static final int CFH_ORIGINAL_SIZE_OFFSET = 24;
/*      */   private static final int CFH_FILENAME_LENGTH_OFFSET = 28;
/*      */   private static final int CFH_EXTRA_LENGTH_OFFSET = 30;
/*      */   private static final int CFH_COMMENT_LENGTH_OFFSET = 32;
/*      */   private static final int CFH_DISK_NUMBER_OFFSET = 34;
/*      */   private static final int CFH_INTERNAL_ATTRIBUTES_OFFSET = 36;
/*      */   private static final int CFH_EXTERNAL_ATTRIBUTES_OFFSET = 38;
/*      */   private static final int CFH_LFH_OFFSET = 42;
/*      */   private static final int CFH_FILENAME_OFFSET = 46;
/*      */   protected boolean finished;
/*      */   public static final int DEFLATED = 8;
/*      */   public static final int DEFAULT_COMPRESSION = -1;
/*      */   public static final int STORED = 0;
/*      */   static final String DEFAULT_ENCODING = "UTF8";
/*      */   @Deprecated
/*      */   public static final int EFS_FLAG = 2048;
/*      */   private CurrentEntry entry;
/*  158 */   private String comment = "";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  163 */   private int level = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasCompressionLevelChanged;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  174 */   private int method = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  179 */   private final List<ZipArchiveEntry> entries = new LinkedList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final StreamCompressor streamCompressor;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long cdOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long cdLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long cdDiskNumberStart;
/*      */ 
/*      */ 
/*      */   
/*      */   private long eocdLength;
/*      */ 
/*      */ 
/*      */   
/*  207 */   private static final byte[] ZERO = new byte[] { 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  212 */   private static final byte[] LZERO = new byte[] { 0, 0, 0, 0 };
/*      */   
/*  214 */   private static final byte[] ONE = ZipLong.getBytes(1L);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  219 */   private final Map<ZipArchiveEntry, EntryMetaData> metaData = new HashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  229 */   private String encoding = "UTF8";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  238 */   private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Deflater def;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final SeekableByteChannel channel;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final OutputStream out;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean useUTF8Flag = true;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean fallbackToUTF8;
/*      */ 
/*      */ 
/*      */   
/*  267 */   private UnicodeExtraFieldPolicy createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasUsedZip64;
/*      */ 
/*      */ 
/*      */   
/*  276 */   private Zip64Mode zip64Mode = Zip64Mode.AsNeeded;
/*      */   
/*  278 */   private final byte[] copyBuffer = new byte[32768];
/*  279 */   private final Calendar calendarInstance = Calendar.getInstance();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean isSplitZip;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  290 */   private final Map<Integer, Integer> numberOfCDInDiskData = new HashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipArchiveOutputStream(OutputStream out) {
/*  297 */     this.out = out;
/*  298 */     this.channel = null;
/*  299 */     this.def = new Deflater(this.level, true);
/*  300 */     this.streamCompressor = StreamCompressor.create(out, this.def);
/*  301 */     this.isSplitZip = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipArchiveOutputStream(File file) throws IOException {
/*  311 */     this(file.toPath(), new OpenOption[0]);
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
/*      */   public ZipArchiveOutputStream(Path file, OpenOption... options) throws IOException {
/*  323 */     this.def = new Deflater(this.level, true);
/*  324 */     OutputStream o = null;
/*  325 */     SeekableByteChannel _channel = null;
/*  326 */     StreamCompressor _streamCompressor = null;
/*      */     try {
/*  328 */       _channel = Files.newByteChannel(file, 
/*  329 */           EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING), (FileAttribute<?>[])new FileAttribute[0]);
/*      */ 
/*      */ 
/*      */       
/*  333 */       _streamCompressor = StreamCompressor.create(_channel, this.def);
/*  334 */     } catch (IOException e) {
/*  335 */       IOUtils.closeQuietly(_channel);
/*  336 */       _channel = null;
/*  337 */       o = Files.newOutputStream(file, options);
/*  338 */       _streamCompressor = StreamCompressor.create(o, this.def);
/*      */     } 
/*  340 */     this.out = o;
/*  341 */     this.channel = _channel;
/*  342 */     this.streamCompressor = _streamCompressor;
/*  343 */     this.isSplitZip = false;
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
/*      */   public ZipArchiveOutputStream(File file, long zipSplitSize) throws IOException {
/*  368 */     this.def = new Deflater(this.level, true);
/*  369 */     this.out = new ZipSplitOutputStream(file, zipSplitSize);
/*  370 */     this.streamCompressor = StreamCompressor.create(this.out, this.def);
/*  371 */     this.channel = null;
/*  372 */     this.isSplitZip = true;
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
/*      */   public ZipArchiveOutputStream(SeekableByteChannel channel) throws IOException {
/*  388 */     this.channel = channel;
/*  389 */     this.def = new Deflater(this.level, true);
/*  390 */     this.streamCompressor = StreamCompressor.create(channel, this.def);
/*  391 */     this.out = null;
/*  392 */     this.isSplitZip = false;
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
/*      */   public boolean isSeekable() {
/*  405 */     return (this.channel != null);
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
/*      */   public void setEncoding(String encoding) {
/*  418 */     this.encoding = encoding;
/*  419 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  420 */     if (this.useUTF8Flag && !ZipEncodingHelper.isUTF8(encoding)) {
/*  421 */       this.useUTF8Flag = false;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getEncoding() {
/*  431 */     return this.encoding;
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
/*      */   public void setUseLanguageEncodingFlag(boolean b) {
/*  444 */     this.useUTF8Flag = (b && ZipEncodingHelper.isUTF8(this.encoding));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b) {
/*  455 */     this.createUnicodeExtraFields = b;
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
/*      */   public void setFallbackToUTF8(boolean b) {
/*  469 */     this.fallbackToUTF8 = b;
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
/*      */   public void setUseZip64(Zip64Mode mode) {
/*  518 */     this.zip64Mode = mode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void finish() throws IOException {
/*  529 */     if (this.finished) {
/*  530 */       throw new IOException("This archive has already been finished");
/*      */     }
/*      */     
/*  533 */     if (this.entry != null) {
/*  534 */       throw new IOException("This archive contains unclosed entries.");
/*      */     }
/*      */     
/*  537 */     long cdOverallOffset = this.streamCompressor.getTotalBytesWritten();
/*  538 */     this.cdOffset = cdOverallOffset;
/*  539 */     if (this.isSplitZip) {
/*      */ 
/*      */       
/*  542 */       ZipSplitOutputStream zipSplitOutputStream = (ZipSplitOutputStream)this.out;
/*  543 */       this.cdOffset = zipSplitOutputStream.getCurrentSplitSegmentBytesWritten();
/*  544 */       this.cdDiskNumberStart = zipSplitOutputStream.getCurrentSplitSegmentIndex();
/*      */     } 
/*  546 */     writeCentralDirectoryInChunks();
/*      */     
/*  548 */     this.cdLength = this.streamCompressor.getTotalBytesWritten() - cdOverallOffset;
/*      */ 
/*      */     
/*  551 */     ByteBuffer commentData = this.zipEncoding.encode(this.comment);
/*  552 */     long commentLength = commentData.limit() - commentData.position();
/*  553 */     this.eocdLength = 22L + commentLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  563 */     writeZip64CentralDirectory();
/*  564 */     writeCentralDirectoryEnd();
/*  565 */     this.metaData.clear();
/*  566 */     this.entries.clear();
/*  567 */     this.streamCompressor.close();
/*  568 */     if (this.isSplitZip)
/*      */     {
/*  570 */       this.out.close();
/*      */     }
/*  572 */     this.finished = true;
/*      */   }
/*      */   
/*      */   private void writeCentralDirectoryInChunks() throws IOException {
/*  576 */     int NUM_PER_WRITE = 1000;
/*  577 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70000);
/*  578 */     int count = 0;
/*  579 */     for (ZipArchiveEntry ze : this.entries) {
/*  580 */       byteArrayOutputStream.write(createCentralFileHeader(ze));
/*  581 */       if (++count > 1000) {
/*  582 */         writeCounted(byteArrayOutputStream.toByteArray());
/*  583 */         byteArrayOutputStream.reset();
/*  584 */         count = 0;
/*      */       } 
/*      */     } 
/*  587 */     writeCounted(byteArrayOutputStream.toByteArray());
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
/*      */   public void closeArchiveEntry() throws IOException {
/*  599 */     preClose();
/*      */     
/*  601 */     flushDeflater();
/*      */     
/*  603 */     long bytesWritten = this.streamCompressor.getTotalBytesWritten() - this.entry.dataStart;
/*  604 */     long realCrc = this.streamCompressor.getCrc32();
/*  605 */     this.entry.bytesRead = this.streamCompressor.getBytesRead();
/*  606 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  607 */     boolean actuallyNeedsZip64 = handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
/*  608 */     closeEntry(actuallyNeedsZip64, false);
/*  609 */     this.streamCompressor.reset();
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
/*      */   private void closeCopiedEntry(boolean phased) throws IOException {
/*  623 */     preClose();
/*  624 */     this.entry.bytesRead = this.entry.entry.getSize();
/*  625 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  626 */     boolean actuallyNeedsZip64 = checkIfNeedsZip64(effectiveMode);
/*  627 */     closeEntry(actuallyNeedsZip64, phased);
/*      */   }
/*      */   
/*      */   private void closeEntry(boolean actuallyNeedsZip64, boolean phased) throws IOException {
/*  631 */     if (!phased && this.channel != null) {
/*  632 */       rewriteSizesAndCrc(actuallyNeedsZip64);
/*      */     }
/*      */     
/*  635 */     if (!phased) {
/*  636 */       writeDataDescriptor(this.entry.entry);
/*      */     }
/*  638 */     this.entry = null;
/*      */   }
/*      */   
/*      */   private void preClose() throws IOException {
/*  642 */     if (this.finished) {
/*  643 */       throw new IOException("Stream has already been finished");
/*      */     }
/*      */     
/*  646 */     if (this.entry == null) {
/*  647 */       throw new IOException("No current entry to close");
/*      */     }
/*      */     
/*  650 */     if (!this.entry.hasWritten) {
/*  651 */       write(ByteUtils.EMPTY_BYTE_ARRAY, 0, 0);
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
/*      */   public void addRawArchiveEntry(ZipArchiveEntry entry, InputStream rawStream) throws IOException {
/*  670 */     ZipArchiveEntry ae = new ZipArchiveEntry(entry);
/*  671 */     if (hasZip64Extra(ae))
/*      */     {
/*      */ 
/*      */       
/*  675 */       ae.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*      */     }
/*      */ 
/*      */     
/*  679 */     boolean is2PhaseSource = (ae.getCrc() != -1L && ae.getSize() != -1L && ae.getCompressedSize() != -1L);
/*  680 */     putArchiveEntry(ae, is2PhaseSource);
/*  681 */     copyFromZipInputStream(rawStream);
/*  682 */     closeCopiedEntry(is2PhaseSource);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void flushDeflater() throws IOException {
/*  689 */     if (this.entry.entry.getMethod() == 8) {
/*  690 */       this.streamCompressor.flushDeflater();
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
/*      */   private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode) throws ZipException {
/*  703 */     if (this.entry.entry.getMethod() == 8) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  708 */       this.entry.entry.setSize(this.entry.bytesRead);
/*  709 */       this.entry.entry.setCompressedSize(bytesWritten);
/*  710 */       this.entry.entry.setCrc(crc);
/*      */     }
/*  712 */     else if (this.channel == null) {
/*  713 */       if (this.entry.entry.getCrc() != crc) {
/*  714 */         throw new ZipException("Bad CRC checksum for entry " + this.entry
/*  715 */             .entry.getName() + ": " + 
/*  716 */             Long.toHexString(this.entry.entry.getCrc()) + " instead of " + 
/*      */             
/*  718 */             Long.toHexString(crc));
/*      */       }
/*      */       
/*  721 */       if (this.entry.entry.getSize() != bytesWritten) {
/*  722 */         throw new ZipException("Bad size for entry " + this.entry
/*  723 */             .entry.getName() + ": " + this.entry
/*  724 */             .entry.getSize() + " instead of " + bytesWritten);
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/*  729 */       this.entry.entry.setSize(bytesWritten);
/*  730 */       this.entry.entry.setCompressedSize(bytesWritten);
/*  731 */       this.entry.entry.setCrc(crc);
/*      */     } 
/*      */     
/*  734 */     return checkIfNeedsZip64(effectiveMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkIfNeedsZip64(Zip64Mode effectiveMode) throws ZipException {
/*  744 */     boolean actuallyNeedsZip64 = isZip64Required(this.entry.entry, effectiveMode);
/*  745 */     if (actuallyNeedsZip64 && effectiveMode == Zip64Mode.Never) {
/*  746 */       throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
/*      */     }
/*  748 */     return actuallyNeedsZip64;
/*      */   }
/*      */   
/*      */   private boolean isZip64Required(ZipArchiveEntry entry1, Zip64Mode requestedMode) {
/*  752 */     return (requestedMode == Zip64Mode.Always || requestedMode == Zip64Mode.AlwaysWithCompatibility || 
/*  753 */       isTooLargeForZip32(entry1));
/*      */   }
/*      */   
/*      */   private boolean isTooLargeForZip32(ZipArchiveEntry zipArchiveEntry) {
/*  757 */     return (zipArchiveEntry.getSize() >= 4294967295L || zipArchiveEntry.getCompressedSize() >= 4294967295L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void rewriteSizesAndCrc(boolean actuallyNeedsZip64) throws IOException {
/*  767 */     long save = this.channel.position();
/*      */     
/*  769 */     this.channel.position(this.entry.localDataStart);
/*  770 */     writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
/*  771 */     if (!hasZip64Extra(this.entry.entry) || !actuallyNeedsZip64) {
/*  772 */       writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
/*  773 */       writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
/*      */     } else {
/*  775 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*  776 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*      */     } 
/*      */     
/*  779 */     if (hasZip64Extra(this.entry.entry)) {
/*  780 */       ByteBuffer name = getName(this.entry.entry);
/*  781 */       int nameLen = name.limit() - name.position();
/*      */       
/*  783 */       this.channel.position(this.entry.localDataStart + 12L + 4L + nameLen + 4L);
/*      */ 
/*      */ 
/*      */       
/*  787 */       writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
/*  788 */       writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
/*      */       
/*  790 */       if (!actuallyNeedsZip64) {
/*      */ 
/*      */         
/*  793 */         this.channel.position(this.entry.localDataStart - 10L);
/*  794 */         writeOut(ZipShort.getBytes(versionNeededToExtract(this.entry.entry.getMethod(), false, false)));
/*      */ 
/*      */ 
/*      */         
/*  798 */         this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*      */         
/*  800 */         this.entry.entry.setExtra();
/*      */ 
/*      */ 
/*      */         
/*  804 */         if (this.entry.causedUseOfZip64) {
/*  805 */           this.hasUsedZip64 = false;
/*      */         }
/*      */       } 
/*      */     } 
/*  809 */     this.channel.position(save);
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
/*      */   public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
/*  821 */     putArchiveEntry(archiveEntry, false);
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
/*      */   private void putArchiveEntry(ArchiveEntry archiveEntry, boolean phased) throws IOException {
/*  837 */     if (this.finished) {
/*  838 */       throw new IOException("Stream has already been finished");
/*      */     }
/*      */     
/*  841 */     if (this.entry != null) {
/*  842 */       closeArchiveEntry();
/*      */     }
/*      */     
/*  845 */     this.entry = new CurrentEntry((ZipArchiveEntry)archiveEntry);
/*  846 */     this.entries.add(this.entry.entry);
/*      */     
/*  848 */     setDefaults(this.entry.entry);
/*      */     
/*  850 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  851 */     validateSizeInformation(effectiveMode);
/*      */     
/*  853 */     if (shouldAddZip64Extra(this.entry.entry, effectiveMode)) {
/*      */       ZipEightByteInteger size, compressedSize;
/*  855 */       Zip64ExtendedInformationExtraField z64 = getZip64Extra(this.entry.entry);
/*      */ 
/*      */ 
/*      */       
/*  859 */       if (phased) {
/*      */         
/*  861 */         size = new ZipEightByteInteger(this.entry.entry.getSize());
/*  862 */         compressedSize = new ZipEightByteInteger(this.entry.entry.getCompressedSize());
/*  863 */       } else if (this.entry.entry.getMethod() == 0 && this.entry
/*  864 */         .entry.getSize() != -1L) {
/*      */         
/*  866 */         compressedSize = size = new ZipEightByteInteger(this.entry.entry.getSize());
/*      */       }
/*      */       else {
/*      */         
/*  870 */         compressedSize = size = ZipEightByteInteger.ZERO;
/*      */       } 
/*  872 */       z64.setSize(size);
/*  873 */       z64.setCompressedSize(compressedSize);
/*  874 */       this.entry.entry.setExtra();
/*      */     } 
/*      */     
/*  877 */     if (this.entry.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
/*  878 */       this.def.setLevel(this.level);
/*  879 */       this.hasCompressionLevelChanged = false;
/*      */     } 
/*  881 */     writeLocalFileHeader((ZipArchiveEntry)archiveEntry, phased);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setDefaults(ZipArchiveEntry entry) {
/*  889 */     if (entry.getMethod() == -1) {
/*  890 */       entry.setMethod(this.method);
/*      */     }
/*      */     
/*  893 */     if (entry.getTime() == -1L) {
/*  894 */       entry.setTime(System.currentTimeMillis());
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
/*      */   private void validateSizeInformation(Zip64Mode effectiveMode) throws ZipException {
/*  907 */     if (this.entry.entry.getMethod() == 0 && this.channel == null) {
/*  908 */       if (this.entry.entry.getSize() == -1L) {
/*  909 */         throw new ZipException("Uncompressed size is required for STORED method when not writing to a file");
/*      */       }
/*      */ 
/*      */       
/*  913 */       if (this.entry.entry.getCrc() == -1L) {
/*  914 */         throw new ZipException("CRC checksum is required for STORED method when not writing to a file");
/*      */       }
/*      */       
/*  917 */       this.entry.entry.setCompressedSize(this.entry.entry.getSize());
/*      */     } 
/*      */     
/*  920 */     if ((this.entry.entry.getSize() >= 4294967295L || this.entry
/*  921 */       .entry.getCompressedSize() >= 4294967295L) && effectiveMode == Zip64Mode.Never)
/*      */     {
/*  923 */       throw new Zip64RequiredException(
/*  924 */           Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
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
/*      */   private boolean shouldAddZip64Extra(ZipArchiveEntry entry, Zip64Mode mode) {
/*  943 */     return (mode == Zip64Mode.Always || mode == Zip64Mode.AlwaysWithCompatibility || entry
/*      */       
/*  945 */       .getSize() >= 4294967295L || entry
/*  946 */       .getCompressedSize() >= 4294967295L || (entry
/*  947 */       .getSize() == -1L && this.channel != null && mode != Zip64Mode.Never));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setComment(String comment) {
/*  956 */     this.comment = comment;
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
/*      */   public void setLevel(int level) {
/*  968 */     if (level < -1 || level > 9)
/*      */     {
/*  970 */       throw new IllegalArgumentException("Invalid compression level: " + level);
/*      */     }
/*      */     
/*  973 */     if (this.level == level) {
/*      */       return;
/*      */     }
/*  976 */     this.hasCompressionLevelChanged = true;
/*  977 */     this.level = level;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMethod(int method) {
/*  987 */     this.method = method;
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
/*      */   public boolean canWriteEntryData(ArchiveEntry ae) {
/*  999 */     if (ae instanceof ZipArchiveEntry) {
/* 1000 */       ZipArchiveEntry zae = (ZipArchiveEntry)ae;
/* 1001 */       return (zae.getMethod() != ZipMethod.IMPLODING.getCode() && zae
/* 1002 */         .getMethod() != ZipMethod.UNSHRINKING.getCode() && 
/* 1003 */         ZipUtil.canHandleEntryData(zae));
/*      */     } 
/* 1005 */     return false;
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
/*      */   public void writePreamble(byte[] preamble) throws IOException {
/* 1017 */     writePreamble(preamble, 0, preamble.length);
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
/*      */   public void writePreamble(byte[] preamble, int offset, int length) throws IOException {
/* 1031 */     if (this.entry != null) {
/* 1032 */       throw new IllegalStateException("Preamble must be written before creating an entry");
/*      */     }
/* 1034 */     this.streamCompressor.writeCounted(preamble, offset, length);
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
/*      */   public void write(byte[] b, int offset, int length) throws IOException {
/* 1046 */     if (this.entry == null) {
/* 1047 */       throw new IllegalStateException("No current entry");
/*      */     }
/* 1049 */     ZipUtil.checkRequestedFeatures(this.entry.entry);
/* 1050 */     long writtenThisTime = this.streamCompressor.write(b, offset, length, this.entry.entry.getMethod());
/* 1051 */     count(writtenThisTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeCounted(byte[] data) throws IOException {
/* 1060 */     this.streamCompressor.writeCounted(data);
/*      */   }
/*      */   
/*      */   private void copyFromZipInputStream(InputStream src) throws IOException {
/* 1064 */     if (this.entry == null) {
/* 1065 */       throw new IllegalStateException("No current entry");
/*      */     }
/* 1067 */     ZipUtil.checkRequestedFeatures(this.entry.entry);
/* 1068 */     this.entry.hasWritten = true;
/*      */     int length;
/* 1070 */     while ((length = src.read(this.copyBuffer)) >= 0) {
/*      */       
/* 1072 */       this.streamCompressor.writeCounted(this.copyBuffer, 0, length);
/* 1073 */       count(length);
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
/*      */   public void close() throws IOException {
/*      */     try {
/* 1089 */       if (!this.finished) {
/* 1090 */         finish();
/*      */       }
/*      */     } finally {
/* 1093 */       destroy();
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
/*      */   public void flush() throws IOException {
/* 1105 */     if (this.out != null) {
/* 1106 */       this.out.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1116 */   static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
/*      */ 
/*      */ 
/*      */   
/* 1120 */   static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
/*      */ 
/*      */ 
/*      */   
/* 1124 */   static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
/*      */ 
/*      */ 
/*      */   
/* 1128 */   static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
/*      */ 
/*      */ 
/*      */   
/* 1132 */   static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
/*      */ 
/*      */ 
/*      */   
/* 1136 */   static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void deflate() throws IOException {
/* 1143 */     this.streamCompressor.deflate();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void writeLocalFileHeader(ZipArchiveEntry ze) throws IOException {
/* 1152 */     writeLocalFileHeader(ze, false);
/*      */   }
/*      */   
/*      */   private void writeLocalFileHeader(ZipArchiveEntry ze, boolean phased) throws IOException {
/* 1156 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 1157 */     ByteBuffer name = getName(ze);
/*      */     
/* 1159 */     if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
/* 1160 */       addUnicodeExtraFields(ze, encodable, name);
/*      */     }
/*      */     
/* 1163 */     long localHeaderStart = this.streamCompressor.getTotalBytesWritten();
/* 1164 */     if (this.isSplitZip) {
/*      */ 
/*      */       
/* 1167 */       ZipSplitOutputStream splitOutputStream = (ZipSplitOutputStream)this.out;
/* 1168 */       ze.setDiskNumberStart(splitOutputStream.getCurrentSplitSegmentIndex());
/* 1169 */       localHeaderStart = splitOutputStream.getCurrentSplitSegmentBytesWritten();
/*      */     } 
/*      */     
/* 1172 */     byte[] localHeader = createLocalFileHeader(ze, name, encodable, phased, localHeaderStart);
/* 1173 */     this.metaData.put(ze, new EntryMetaData(localHeaderStart, usesDataDescriptor(ze.getMethod(), phased)));
/* 1174 */     this.entry.localDataStart = localHeaderStart + 14L;
/* 1175 */     writeCounted(localHeader);
/* 1176 */     this.entry.dataStart = this.streamCompressor.getTotalBytesWritten();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private byte[] createLocalFileHeader(ZipArchiveEntry ze, ByteBuffer name, boolean encodable, boolean phased, long archiveOffset) {
/* 1182 */     ZipExtraField oldEx = ze.getExtraField(ResourceAlignmentExtraField.ID);
/* 1183 */     if (oldEx != null) {
/* 1184 */       ze.removeExtraField(ResourceAlignmentExtraField.ID);
/*      */     }
/* 1186 */     ResourceAlignmentExtraField oldAlignmentEx = (oldEx instanceof ResourceAlignmentExtraField) ? (ResourceAlignmentExtraField)oldEx : null;
/*      */ 
/*      */     
/* 1189 */     int alignment = ze.getAlignment();
/* 1190 */     if (alignment <= 0 && oldAlignmentEx != null) {
/* 1191 */       alignment = oldAlignmentEx.getAlignment();
/*      */     }
/*      */     
/* 1194 */     if (alignment > 1 || (oldAlignmentEx != null && !oldAlignmentEx.allowMethodChange())) {
/*      */ 
/*      */       
/* 1197 */       int oldLength = 30 + name.limit() - name.position() + (ze.getLocalFileDataExtra()).length;
/*      */       
/* 1199 */       int padding = (int)(-archiveOffset - oldLength - 4L - 2L & (alignment - 1));
/*      */ 
/*      */       
/* 1202 */       ze.addExtraField(new ResourceAlignmentExtraField(alignment, (oldAlignmentEx != null && oldAlignmentEx
/* 1203 */             .allowMethodChange()), padding));
/*      */     } 
/*      */     
/* 1206 */     byte[] extra = ze.getLocalFileDataExtra();
/* 1207 */     int nameLen = name.limit() - name.position();
/* 1208 */     int len = 30 + nameLen + extra.length;
/* 1209 */     byte[] buf = new byte[len];
/*      */     
/* 1211 */     System.arraycopy(LFH_SIG, 0, buf, 0, 4);
/*      */ 
/*      */     
/* 1214 */     int zipMethod = ze.getMethod();
/* 1215 */     boolean dataDescriptor = usesDataDescriptor(zipMethod, phased);
/*      */     
/* 1217 */     ZipShort.putShort(versionNeededToExtract(zipMethod, hasZip64Extra(ze), dataDescriptor), buf, 4);
/*      */     
/* 1219 */     GeneralPurposeBit generalPurposeBit = getGeneralPurposeBits((!encodable && this.fallbackToUTF8), dataDescriptor);
/* 1220 */     generalPurposeBit.encode(buf, 6);
/*      */ 
/*      */     
/* 1223 */     ZipShort.putShort(zipMethod, buf, 8);
/*      */     
/* 1225 */     ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 10);
/*      */ 
/*      */     
/* 1228 */     if (phased || (zipMethod != 8 && this.channel == null)) {
/* 1229 */       ZipLong.putLong(ze.getCrc(), buf, 14);
/*      */     } else {
/* 1231 */       System.arraycopy(LZERO, 0, buf, 14, 4);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1236 */     if (hasZip64Extra(this.entry.entry)) {
/*      */ 
/*      */ 
/*      */       
/* 1240 */       ZipLong.ZIP64_MAGIC.putLong(buf, 18);
/* 1241 */       ZipLong.ZIP64_MAGIC.putLong(buf, 22);
/* 1242 */     } else if (phased) {
/* 1243 */       ZipLong.putLong(ze.getCompressedSize(), buf, 18);
/* 1244 */       ZipLong.putLong(ze.getSize(), buf, 22);
/* 1245 */     } else if (zipMethod == 8 || this.channel != null) {
/* 1246 */       System.arraycopy(LZERO, 0, buf, 18, 4);
/* 1247 */       System.arraycopy(LZERO, 0, buf, 22, 4);
/*      */     } else {
/* 1249 */       ZipLong.putLong(ze.getSize(), buf, 18);
/* 1250 */       ZipLong.putLong(ze.getSize(), buf, 22);
/*      */     } 
/*      */     
/* 1253 */     ZipShort.putShort(nameLen, buf, 26);
/*      */ 
/*      */     
/* 1256 */     ZipShort.putShort(extra.length, buf, 28);
/*      */ 
/*      */     
/* 1259 */     System.arraycopy(name.array(), name.arrayOffset(), buf, 30, nameLen);
/*      */ 
/*      */     
/* 1262 */     System.arraycopy(extra, 0, buf, 30 + nameLen, extra.length);
/*      */     
/* 1264 */     return buf;
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
/*      */   private void addUnicodeExtraFields(ZipArchiveEntry ze, boolean encodable, ByteBuffer name) throws IOException {
/* 1276 */     if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !encodable)
/*      */     {
/* 1278 */       ze.addExtraField(new UnicodePathExtraField(ze.getName(), name
/* 1279 */             .array(), name
/* 1280 */             .arrayOffset(), name
/* 1281 */             .limit() - name
/* 1282 */             .position()));
/*      */     }
/*      */     
/* 1285 */     String comm = ze.getComment();
/* 1286 */     if (comm != null && !"".equals(comm)) {
/*      */       
/* 1288 */       boolean commentEncodable = this.zipEncoding.canEncode(comm);
/*      */       
/* 1290 */       if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !commentEncodable) {
/*      */         
/* 1292 */         ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
/* 1293 */         ze.addExtraField(new UnicodeCommentExtraField(comm, commentB
/* 1294 */               .array(), commentB
/* 1295 */               .arrayOffset(), commentB
/* 1296 */               .limit() - commentB
/* 1297 */               .position()));
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
/*      */   protected void writeDataDescriptor(ZipArchiveEntry ze) throws IOException {
/* 1309 */     if (!usesDataDescriptor(ze.getMethod(), false)) {
/*      */       return;
/*      */     }
/* 1312 */     writeCounted(DD_SIG);
/* 1313 */     writeCounted(ZipLong.getBytes(ze.getCrc()));
/* 1314 */     if (!hasZip64Extra(ze)) {
/* 1315 */       writeCounted(ZipLong.getBytes(ze.getCompressedSize()));
/* 1316 */       writeCounted(ZipLong.getBytes(ze.getSize()));
/*      */     } else {
/* 1318 */       writeCounted(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
/* 1319 */       writeCounted(ZipEightByteInteger.getBytes(ze.getSize()));
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
/*      */   protected void writeCentralFileHeader(ZipArchiveEntry ze) throws IOException {
/* 1332 */     byte[] centralFileHeader = createCentralFileHeader(ze);
/* 1333 */     writeCounted(centralFileHeader);
/*      */   }
/*      */ 
/*      */   
/*      */   private byte[] createCentralFileHeader(ZipArchiveEntry ze) throws IOException {
/* 1338 */     EntryMetaData entryMetaData = this.metaData.get(ze);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1343 */     boolean needsZip64Extra = (hasZip64Extra(ze) || ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || entryMetaData.offset >= 4294967295L || ze.getDiskNumberStart() >= 65535L || this.zip64Mode == Zip64Mode.Always || this.zip64Mode == Zip64Mode.AlwaysWithCompatibility);
/*      */ 
/*      */ 
/*      */     
/* 1347 */     if (needsZip64Extra && this.zip64Mode == Zip64Mode.Never)
/*      */     {
/*      */ 
/*      */       
/* 1351 */       throw new Zip64RequiredException("Archive's size exceeds the limit of 4GByte.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1356 */     handleZip64Extra(ze, entryMetaData.offset, needsZip64Extra);
/*      */     
/* 1358 */     return createCentralFileHeader(ze, getName(ze), entryMetaData, needsZip64Extra);
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
/*      */   private byte[] createCentralFileHeader(ZipArchiveEntry ze, ByteBuffer name, EntryMetaData entryMetaData, boolean needsZip64Extra) throws IOException {
/* 1371 */     if (this.isSplitZip) {
/*      */ 
/*      */       
/* 1374 */       int currentSplitSegment = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
/* 1375 */       if (this.numberOfCDInDiskData.get(Integer.valueOf(currentSplitSegment)) == null) {
/* 1376 */         this.numberOfCDInDiskData.put(Integer.valueOf(currentSplitSegment), Integer.valueOf(1));
/*      */       } else {
/* 1378 */         int originalNumberOfCD = ((Integer)this.numberOfCDInDiskData.get(Integer.valueOf(currentSplitSegment))).intValue();
/* 1379 */         this.numberOfCDInDiskData.put(Integer.valueOf(currentSplitSegment), Integer.valueOf(originalNumberOfCD + 1));
/*      */       } 
/*      */     } 
/*      */     
/* 1383 */     byte[] extra = ze.getCentralDirectoryExtra();
/* 1384 */     int extraLength = extra.length;
/*      */ 
/*      */     
/* 1387 */     String comm = ze.getComment();
/* 1388 */     if (comm == null) {
/* 1389 */       comm = "";
/*      */     }
/*      */     
/* 1392 */     ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
/* 1393 */     int nameLen = name.limit() - name.position();
/* 1394 */     int commentLen = commentB.limit() - commentB.position();
/* 1395 */     int len = 46 + nameLen + extraLength + commentLen;
/* 1396 */     byte[] buf = new byte[len];
/*      */     
/* 1398 */     System.arraycopy(CFH_SIG, 0, buf, 0, 4);
/*      */ 
/*      */ 
/*      */     
/* 1402 */     ZipShort.putShort(ze.getPlatform() << 8 | (!this.hasUsedZip64 ? 20 : 45), buf, 4);
/*      */ 
/*      */     
/* 1405 */     int zipMethod = ze.getMethod();
/* 1406 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 1407 */     ZipShort.putShort(versionNeededToExtract(zipMethod, needsZip64Extra, entryMetaData.usesDataDescriptor), buf, 6);
/*      */     
/* 1409 */     getGeneralPurposeBits((!encodable && this.fallbackToUTF8), entryMetaData.usesDataDescriptor).encode(buf, 8);
/*      */ 
/*      */     
/* 1412 */     ZipShort.putShort(zipMethod, buf, 10);
/*      */ 
/*      */ 
/*      */     
/* 1416 */     ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 12);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1421 */     ZipLong.putLong(ze.getCrc(), buf, 16);
/* 1422 */     if (ze.getCompressedSize() >= 4294967295L || ze
/* 1423 */       .getSize() >= 4294967295L || this.zip64Mode == Zip64Mode.Always || this.zip64Mode == Zip64Mode.AlwaysWithCompatibility) {
/*      */ 
/*      */       
/* 1426 */       ZipLong.ZIP64_MAGIC.putLong(buf, 20);
/* 1427 */       ZipLong.ZIP64_MAGIC.putLong(buf, 24);
/*      */     } else {
/* 1429 */       ZipLong.putLong(ze.getCompressedSize(), buf, 20);
/* 1430 */       ZipLong.putLong(ze.getSize(), buf, 24);
/*      */     } 
/*      */     
/* 1433 */     ZipShort.putShort(nameLen, buf, 28);
/*      */ 
/*      */     
/* 1436 */     ZipShort.putShort(extraLength, buf, 30);
/*      */     
/* 1438 */     ZipShort.putShort(commentLen, buf, 32);
/*      */ 
/*      */     
/* 1441 */     if (this.isSplitZip) {
/* 1442 */       if (ze.getDiskNumberStart() >= 65535L || this.zip64Mode == Zip64Mode.Always) {
/* 1443 */         ZipShort.putShort(65535, buf, 34);
/*      */       } else {
/* 1445 */         ZipShort.putShort((int)ze.getDiskNumberStart(), buf, 34);
/*      */       } 
/*      */     } else {
/* 1448 */       System.arraycopy(ZERO, 0, buf, 34, 2);
/*      */     } 
/*      */ 
/*      */     
/* 1452 */     ZipShort.putShort(ze.getInternalAttributes(), buf, 36);
/*      */ 
/*      */     
/* 1455 */     ZipLong.putLong(ze.getExternalAttributes(), buf, 38);
/*      */ 
/*      */     
/* 1458 */     if (entryMetaData.offset >= 4294967295L || this.zip64Mode == Zip64Mode.Always) {
/* 1459 */       ZipLong.putLong(4294967295L, buf, 42);
/*      */     } else {
/* 1461 */       ZipLong.putLong(Math.min(entryMetaData.offset, 4294967295L), buf, 42);
/*      */     } 
/*      */ 
/*      */     
/* 1465 */     System.arraycopy(name.array(), name.arrayOffset(), buf, 46, nameLen);
/*      */     
/* 1467 */     int extraStart = 46 + nameLen;
/* 1468 */     System.arraycopy(extra, 0, buf, extraStart, extraLength);
/*      */     
/* 1470 */     int commentStart = extraStart + extraLength;
/*      */ 
/*      */     
/* 1473 */     System.arraycopy(commentB.array(), commentB.arrayOffset(), buf, commentStart, commentLen);
/* 1474 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleZip64Extra(ZipArchiveEntry ze, long lfhOffset, boolean needsZip64Extra) {
/* 1483 */     if (needsZip64Extra) {
/* 1484 */       Zip64ExtendedInformationExtraField z64 = getZip64Extra(ze);
/* 1485 */       if (ze.getCompressedSize() >= 4294967295L || ze
/* 1486 */         .getSize() >= 4294967295L || this.zip64Mode == Zip64Mode.Always || this.zip64Mode == Zip64Mode.AlwaysWithCompatibility) {
/*      */ 
/*      */         
/* 1489 */         z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
/* 1490 */         z64.setSize(new ZipEightByteInteger(ze.getSize()));
/*      */       } else {
/*      */         
/* 1493 */         z64.setCompressedSize(null);
/* 1494 */         z64.setSize(null);
/*      */       } 
/*      */       
/* 1497 */       boolean needsToEncodeLfhOffset = (lfhOffset >= 4294967295L || this.zip64Mode == Zip64Mode.Always);
/*      */ 
/*      */       
/* 1500 */       boolean needsToEncodeDiskNumberStart = (ze.getDiskNumberStart() >= 65535L || this.zip64Mode == Zip64Mode.Always);
/*      */       
/* 1502 */       if (needsToEncodeLfhOffset || needsToEncodeDiskNumberStart) {
/* 1503 */         z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
/*      */       }
/* 1505 */       if (needsToEncodeDiskNumberStart) {
/* 1506 */         z64.setDiskStartNumber(new ZipLong(ze.getDiskNumberStart()));
/*      */       }
/* 1508 */       ze.setExtra();
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
/*      */   protected void writeCentralDirectoryEnd() throws IOException {
/* 1520 */     if (!this.hasUsedZip64 && this.isSplitZip) {
/* 1521 */       ((ZipSplitOutputStream)this.out).prepareToWriteUnsplittableContent(this.eocdLength);
/*      */     }
/*      */     
/* 1524 */     validateIfZip64IsNeededInEOCD();
/*      */     
/* 1526 */     writeCounted(EOCD_SIG);
/*      */ 
/*      */     
/* 1529 */     int numberOfThisDisk = 0;
/* 1530 */     if (this.isSplitZip) {
/* 1531 */       numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
/*      */     }
/* 1533 */     writeCounted(ZipShort.getBytes(numberOfThisDisk));
/*      */ 
/*      */     
/* 1536 */     writeCounted(ZipShort.getBytes((int)this.cdDiskNumberStart));
/*      */ 
/*      */     
/* 1539 */     int numberOfEntries = this.entries.size();
/*      */ 
/*      */ 
/*      */     
/* 1543 */     int numOfEntriesOnThisDisk = this.isSplitZip ? ((this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk)) == null) ? 0 : ((Integer)this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk))).intValue()) : numberOfEntries;
/*      */ 
/*      */     
/* 1546 */     byte[] numOfEntriesOnThisDiskData = ZipShort.getBytes(Math.min(numOfEntriesOnThisDisk, 65535));
/* 1547 */     writeCounted(numOfEntriesOnThisDiskData);
/*      */ 
/*      */     
/* 1550 */     byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, 65535));
/*      */     
/* 1552 */     writeCounted(num);
/*      */ 
/*      */     
/* 1555 */     writeCounted(ZipLong.getBytes(Math.min(this.cdLength, 4294967295L)));
/* 1556 */     writeCounted(ZipLong.getBytes(Math.min(this.cdOffset, 4294967295L)));
/*      */ 
/*      */     
/* 1559 */     ByteBuffer data = this.zipEncoding.encode(this.comment);
/* 1560 */     int dataLen = data.limit() - data.position();
/* 1561 */     writeCounted(ZipShort.getBytes(dataLen));
/* 1562 */     this.streamCompressor.writeCounted(data.array(), data.arrayOffset(), dataLen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void validateIfZip64IsNeededInEOCD() throws Zip64RequiredException {
/* 1572 */     if (this.zip64Mode != Zip64Mode.Never) {
/*      */       return;
/*      */     }
/*      */     
/* 1576 */     int numberOfThisDisk = 0;
/* 1577 */     if (this.isSplitZip) {
/* 1578 */       numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
/*      */     }
/* 1580 */     if (numberOfThisDisk >= 65535) {
/* 1581 */       throw new Zip64RequiredException("Number of the disk of End Of Central Directory exceeds the limit of 65535.");
/*      */     }
/*      */ 
/*      */     
/* 1585 */     if (this.cdDiskNumberStart >= 65535L) {
/* 1586 */       throw new Zip64RequiredException("Number of the disk with the start of Central Directory exceeds the limit of 65535.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1591 */     int numOfEntriesOnThisDisk = (this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk)) == null) ? 0 : ((Integer)this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk))).intValue();
/* 1592 */     if (numOfEntriesOnThisDisk >= 65535) {
/* 1593 */       throw new Zip64RequiredException("Number of entries on this disk exceeds the limit of 65535.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1598 */     if (this.entries.size() >= 65535) {
/* 1599 */       throw new Zip64RequiredException("Archive contains more than 65535 entries.");
/*      */     }
/*      */ 
/*      */     
/* 1603 */     if (this.cdLength >= 4294967295L) {
/* 1604 */       throw new Zip64RequiredException("The size of the entire central directory exceeds the limit of 4GByte.");
/*      */     }
/*      */ 
/*      */     
/* 1608 */     if (this.cdOffset >= 4294967295L) {
/* 1609 */       throw new Zip64RequiredException("Archive's size exceeds the limit of 4GByte.");
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
/*      */   protected void writeZip64CentralDirectory() throws IOException {
/* 1621 */     if (this.zip64Mode == Zip64Mode.Never) {
/*      */       return;
/*      */     }
/*      */     
/* 1625 */     if (!this.hasUsedZip64 && shouldUseZip64EOCD())
/*      */     {
/* 1627 */       this.hasUsedZip64 = true;
/*      */     }
/*      */     
/* 1630 */     if (!this.hasUsedZip64) {
/*      */       return;
/*      */     }
/*      */     
/* 1634 */     long offset = this.streamCompressor.getTotalBytesWritten();
/* 1635 */     long diskNumberStart = 0L;
/* 1636 */     if (this.isSplitZip) {
/*      */ 
/*      */       
/* 1639 */       ZipSplitOutputStream zipSplitOutputStream = (ZipSplitOutputStream)this.out;
/* 1640 */       offset = zipSplitOutputStream.getCurrentSplitSegmentBytesWritten();
/* 1641 */       diskNumberStart = zipSplitOutputStream.getCurrentSplitSegmentIndex();
/*      */     } 
/*      */ 
/*      */     
/* 1645 */     writeOut(ZIP64_EOCD_SIG);
/*      */ 
/*      */     
/* 1648 */     writeOut(
/* 1649 */         ZipEightByteInteger.getBytes(44L));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1660 */     writeOut(ZipShort.getBytes(45));
/* 1661 */     writeOut(ZipShort.getBytes(45));
/*      */ 
/*      */     
/* 1664 */     int numberOfThisDisk = 0;
/* 1665 */     if (this.isSplitZip) {
/* 1666 */       numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
/*      */     }
/* 1668 */     writeOut(ZipLong.getBytes(numberOfThisDisk));
/*      */ 
/*      */     
/* 1671 */     writeOut(ZipLong.getBytes(this.cdDiskNumberStart));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1676 */     int numOfEntriesOnThisDisk = this.isSplitZip ? ((this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk)) == null) ? 0 : ((Integer)this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk))).intValue()) : this.entries.size();
/* 1677 */     byte[] numOfEntriesOnThisDiskData = ZipEightByteInteger.getBytes(numOfEntriesOnThisDisk);
/* 1678 */     writeOut(numOfEntriesOnThisDiskData);
/*      */ 
/*      */     
/* 1681 */     byte[] num = ZipEightByteInteger.getBytes(this.entries.size());
/* 1682 */     writeOut(num);
/*      */ 
/*      */     
/* 1685 */     writeOut(ZipEightByteInteger.getBytes(this.cdLength));
/* 1686 */     writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
/*      */ 
/*      */ 
/*      */     
/* 1690 */     if (this.isSplitZip) {
/*      */ 
/*      */       
/* 1693 */       int zip64EOCDLOCLength = 20;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1698 */       long unsplittableContentSize = 20L + this.eocdLength;
/* 1699 */       ((ZipSplitOutputStream)this.out).prepareToWriteUnsplittableContent(unsplittableContentSize);
/*      */     } 
/*      */ 
/*      */     
/* 1703 */     writeOut(ZIP64_EOCD_LOC_SIG);
/*      */ 
/*      */     
/* 1706 */     writeOut(ZipLong.getBytes(diskNumberStart));
/*      */     
/* 1708 */     writeOut(ZipEightByteInteger.getBytes(offset));
/*      */     
/* 1710 */     if (this.isSplitZip) {
/*      */ 
/*      */       
/* 1713 */       int totalNumberOfDisks = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex() + 1;
/* 1714 */       writeOut(ZipLong.getBytes(totalNumberOfDisks));
/*      */     } else {
/* 1716 */       writeOut(ONE);
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
/*      */   private boolean shouldUseZip64EOCD() {
/* 1728 */     int numberOfThisDisk = 0;
/* 1729 */     if (this.isSplitZip) {
/* 1730 */       numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
/*      */     }
/* 1732 */     int numOfEntriesOnThisDisk = (this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk)) == null) ? 0 : ((Integer)this.numberOfCDInDiskData.get(Integer.valueOf(numberOfThisDisk))).intValue();
/* 1733 */     return (numberOfThisDisk >= 65535 || this.cdDiskNumberStart >= 65535L || numOfEntriesOnThisDisk >= 65535 || this.entries
/*      */ 
/*      */       
/* 1736 */       .size() >= 65535 || this.cdLength >= 4294967295L || this.cdOffset >= 4294967295L);
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
/*      */   protected final void writeOut(byte[] data) throws IOException {
/* 1748 */     this.streamCompressor.writeOut(data, 0, data.length);
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
/*      */   protected final void writeOut(byte[] data, int offset, int length) throws IOException {
/* 1761 */     this.streamCompressor.writeOut(data, offset, length);
/*      */   }
/*      */ 
/*      */   
/*      */   private GeneralPurposeBit getGeneralPurposeBits(boolean utfFallback, boolean usesDataDescriptor) {
/* 1766 */     GeneralPurposeBit b = new GeneralPurposeBit();
/* 1767 */     b.useUTF8ForNames((this.useUTF8Flag || utfFallback));
/* 1768 */     if (usesDataDescriptor) {
/* 1769 */       b.useDataDescriptor(true);
/*      */     }
/* 1771 */     return b;
/*      */   }
/*      */   
/*      */   private int versionNeededToExtract(int zipMethod, boolean zip64, boolean usedDataDescriptor) {
/* 1775 */     if (zip64) {
/* 1776 */       return 45;
/*      */     }
/* 1778 */     if (usedDataDescriptor) {
/* 1779 */       return 20;
/*      */     }
/* 1781 */     return versionNeededToExtractMethod(zipMethod);
/*      */   }
/*      */   
/*      */   private boolean usesDataDescriptor(int zipMethod, boolean phased) {
/* 1785 */     return (!phased && zipMethod == 8 && this.channel == null);
/*      */   }
/*      */   
/*      */   private int versionNeededToExtractMethod(int zipMethod) {
/* 1789 */     return (zipMethod == 8) ? 20 : 10;
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
/*      */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 1806 */     if (this.finished) {
/* 1807 */       throw new IOException("Stream has already been finished");
/*      */     }
/* 1809 */     return new ZipArchiveEntry(inputFile, entryName);
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
/*      */   public ArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
/* 1832 */     if (this.finished) {
/* 1833 */       throw new IOException("Stream has already been finished");
/*      */     }
/* 1835 */     return new ZipArchiveEntry(inputPath, entryName, new LinkOption[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Zip64ExtendedInformationExtraField getZip64Extra(ZipArchiveEntry ze) {
/* 1846 */     if (this.entry != null) {
/* 1847 */       this.entry.causedUseOfZip64 = !this.hasUsedZip64;
/*      */     }
/* 1849 */     this.hasUsedZip64 = true;
/* 1850 */     ZipExtraField extra = ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/* 1851 */     Zip64ExtendedInformationExtraField z64 = (extra instanceof Zip64ExtendedInformationExtraField) ? (Zip64ExtendedInformationExtraField)extra : null;
/*      */     
/* 1853 */     if (z64 == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1860 */       z64 = new Zip64ExtendedInformationExtraField();
/*      */     }
/*      */ 
/*      */     
/* 1864 */     ze.addAsFirstExtraField(z64);
/*      */     
/* 1866 */     return z64;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasZip64Extra(ZipArchiveEntry ze) {
/* 1876 */     return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) instanceof Zip64ExtendedInformationExtraField;
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
/*      */   private Zip64Mode getEffectiveZip64Mode(ZipArchiveEntry ze) {
/* 1889 */     if (this.zip64Mode != Zip64Mode.AsNeeded || this.channel != null || ze
/*      */       
/* 1891 */       .getMethod() != 8 || ze
/* 1892 */       .getSize() != -1L) {
/* 1893 */       return this.zip64Mode;
/*      */     }
/* 1895 */     return Zip64Mode.Never;
/*      */   }
/*      */   
/*      */   private ZipEncoding getEntryEncoding(ZipArchiveEntry ze) {
/* 1899 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 1900 */     return (!encodable && this.fallbackToUTF8) ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*      */   }
/*      */ 
/*      */   
/*      */   private ByteBuffer getName(ZipArchiveEntry ze) throws IOException {
/* 1905 */     return getEntryEncoding(ze).encode(ze.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void destroy() throws IOException {
/*      */     try {
/* 1917 */       if (this.channel != null) {
/* 1918 */         this.channel.close();
/*      */       }
/*      */     } finally {
/* 1921 */       if (this.out != null) {
/* 1922 */         this.out.close();
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
/*      */   public static final class UnicodeExtraFieldPolicy
/*      */   {
/* 1935 */     public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
/*      */ 
/*      */ 
/*      */     
/* 1939 */     public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1944 */     public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
/*      */     
/*      */     private final String name;
/*      */     
/*      */     private UnicodeExtraFieldPolicy(String n) {
/* 1949 */       this.name = n;
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1953 */       return this.name;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class CurrentEntry {
/*      */     private final ZipArchiveEntry entry;
/*      */     private long localDataStart;
/*      */     private long dataStart;
/*      */     
/*      */     private CurrentEntry(ZipArchiveEntry entry) {
/* 1963 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long bytesRead;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean causedUseOfZip64;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean hasWritten;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class EntryMetaData
/*      */   {
/*      */     private final long offset;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean usesDataDescriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private EntryMetaData(long offset, boolean usesDataDescriptor) {
/* 2002 */       this.offset = offset;
/* 2003 */       this.usesDataDescriptor = usesDataDescriptor;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipArchiveOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */