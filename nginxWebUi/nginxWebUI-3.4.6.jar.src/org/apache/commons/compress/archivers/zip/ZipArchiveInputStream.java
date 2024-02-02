/*      */ package org.apache.commons.compress.archivers.zip;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PushbackInputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Arrays;
/*      */ import java.util.zip.CRC32;
/*      */ import java.util.zip.DataFormatException;
/*      */ import java.util.zip.Inflater;
/*      */ import java.util.zip.ZipException;
/*      */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*      */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*      */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
/*      */ import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
/*      */ import org.apache.commons.compress.utils.ArchiveUtils;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ZipArchiveInputStream
/*      */   extends ArchiveInputStream
/*      */   implements InputStreamStatistics
/*      */ {
/*      */   private final ZipEncoding zipEncoding;
/*      */   final String encoding;
/*      */   private final boolean useUnicodeExtraFields;
/*      */   private final InputStream in;
/*   96 */   private final Inflater inf = new Inflater(true);
/*      */ 
/*      */   
/*   99 */   private final ByteBuffer buf = ByteBuffer.allocate(512);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CurrentEntry current;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean closed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hitCentralDirectory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteArrayInputStream lastStoredEntry;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean allowStoredEntriesWithDataDescriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long uncompressedCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean skipSplitSig;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int LFH_LEN = 30;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int CFH_LEN = 46;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long TWO_EXP_32 = 4294967296L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  175 */   private final byte[] lfhBuf = new byte[30];
/*  176 */   private final byte[] skipBuf = new byte[1024];
/*  177 */   private final byte[] shortBuf = new byte[2];
/*  178 */   private final byte[] wordBuf = new byte[4];
/*  179 */   private final byte[] twoDwordBuf = new byte[16];
/*      */ 
/*      */   
/*      */   private int entriesRead;
/*      */   
/*      */   private static final String USE_ZIPFILE_INSTEAD_OF_STREAM_DISCLAIMER = " while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile";
/*      */ 
/*      */   
/*      */   public ZipArchiveInputStream(InputStream inputStream) {
/*  188 */     this(inputStream, "UTF8");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZipArchiveInputStream(InputStream inputStream, String encoding) {
/*  199 */     this(inputStream, encoding, true);
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
/*      */   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields) {
/*  211 */     this(inputStream, encoding, useUnicodeExtraFields, false);
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
/*      */   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor) {
/*  229 */     this(inputStream, encoding, useUnicodeExtraFields, allowStoredEntriesWithDataDescriptor, false);
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
/*      */   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor, boolean skipSplitSig) {
/*  251 */     this.encoding = encoding;
/*  252 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  253 */     this.useUnicodeExtraFields = useUnicodeExtraFields;
/*  254 */     this.in = new PushbackInputStream(inputStream, this.buf.capacity());
/*  255 */     this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor;
/*      */     
/*  257 */     this.skipSplitSig = skipSplitSig;
/*      */     
/*  259 */     this.buf.limit(0);
/*      */   }
/*      */   
/*      */   public ZipArchiveEntry getNextZipEntry() throws IOException {
/*  263 */     this.uncompressedCount = 0L;
/*      */     
/*  265 */     boolean firstEntry = true;
/*  266 */     if (this.closed || this.hitCentralDirectory) {
/*  267 */       return null;
/*      */     }
/*  269 */     if (this.current != null) {
/*  270 */       closeEntry();
/*  271 */       firstEntry = false;
/*      */     } 
/*      */     
/*  274 */     long currentHeaderOffset = getBytesRead();
/*      */     try {
/*  276 */       if (firstEntry) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  281 */         readFirstLocalFileHeader();
/*      */       } else {
/*  283 */         readFully(this.lfhBuf);
/*      */       } 
/*  285 */     } catch (EOFException e) {
/*  286 */       return null;
/*      */     } 
/*      */     
/*  289 */     ZipLong sig = new ZipLong(this.lfhBuf);
/*  290 */     if (!sig.equals(ZipLong.LFH_SIG)) {
/*  291 */       if (sig.equals(ZipLong.CFH_SIG) || sig.equals(ZipLong.AED_SIG) || isApkSigningBlock(this.lfhBuf)) {
/*  292 */         this.hitCentralDirectory = true;
/*  293 */         skipRemainderOfArchive();
/*  294 */         return null;
/*      */       } 
/*  296 */       throw new ZipException(String.format("Unexpected record signature: 0X%X", new Object[] { Long.valueOf(sig.getValue()) }));
/*      */     } 
/*      */     
/*  299 */     int off = 4;
/*  300 */     this.current = new CurrentEntry();
/*      */     
/*  302 */     int versionMadeBy = ZipShort.getValue(this.lfhBuf, off);
/*  303 */     off += 2;
/*  304 */     this.current.entry.setPlatform(versionMadeBy >> 8 & 0xF);
/*      */     
/*  306 */     GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.lfhBuf, off);
/*  307 */     boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
/*  308 */     ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*  309 */     this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
/*  310 */     this.current.entry.setGeneralPurposeBit(gpFlag);
/*      */     
/*  312 */     off += 2;
/*      */     
/*  314 */     this.current.entry.setMethod(ZipShort.getValue(this.lfhBuf, off));
/*  315 */     off += 2;
/*      */     
/*  317 */     long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.lfhBuf, off));
/*  318 */     this.current.entry.setTime(time);
/*  319 */     off += 4;
/*      */     
/*  321 */     ZipLong size = null, cSize = null;
/*  322 */     if (!this.current.hasDataDescriptor) {
/*  323 */       this.current.entry.setCrc(ZipLong.getValue(this.lfhBuf, off));
/*  324 */       off += 4;
/*      */       
/*  326 */       cSize = new ZipLong(this.lfhBuf, off);
/*  327 */       off += 4;
/*      */       
/*  329 */       size = new ZipLong(this.lfhBuf, off);
/*  330 */       off += 4;
/*      */     } else {
/*  332 */       off += 12;
/*      */     } 
/*      */     
/*  335 */     int fileNameLen = ZipShort.getValue(this.lfhBuf, off);
/*      */     
/*  337 */     off += 2;
/*      */     
/*  339 */     int extraLen = ZipShort.getValue(this.lfhBuf, off);
/*  340 */     off += 2;
/*      */     
/*  342 */     byte[] fileName = readRange(fileNameLen);
/*  343 */     this.current.entry.setName(entryEncoding.decode(fileName), fileName);
/*  344 */     if (hasUTF8Flag) {
/*  345 */       this.current.entry.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
/*      */     }
/*      */     
/*  348 */     byte[] extraData = readRange(extraLen);
/*      */     try {
/*  350 */       this.current.entry.setExtra(extraData);
/*  351 */     } catch (RuntimeException ex) {
/*  352 */       ZipException z = new ZipException("Invalid extra data in entry " + this.current.entry.getName());
/*  353 */       z.initCause(ex);
/*  354 */       throw z;
/*      */     } 
/*      */     
/*  357 */     if (!hasUTF8Flag && this.useUnicodeExtraFields) {
/*  358 */       ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, null);
/*      */     }
/*      */     
/*  361 */     processZip64Extra(size, cSize);
/*      */     
/*  363 */     this.current.entry.setLocalHeaderOffset(currentHeaderOffset);
/*  364 */     this.current.entry.setDataOffset(getBytesRead());
/*  365 */     this.current.entry.setStreamContiguous(true);
/*      */     
/*  367 */     ZipMethod m = ZipMethod.getMethodByCode(this.current.entry.getMethod());
/*  368 */     if (this.current.entry.getCompressedSize() != -1L) {
/*  369 */       if (ZipUtil.canHandleEntryData(this.current.entry) && m != ZipMethod.STORED && m != ZipMethod.DEFLATED) {
/*  370 */         InputStream bis = new BoundedInputStream(this.in, this.current.entry.getCompressedSize());
/*  371 */         switch (m) {
/*      */           case UNSHRINKING:
/*  373 */             this.current.in = (InputStream)new UnshrinkingInputStream(bis);
/*      */             break;
/*      */           case IMPLODING:
/*      */             try {
/*  377 */               this.current.in = new ExplodingInputStream(this.current
/*  378 */                   .entry.getGeneralPurposeBit().getSlidingDictionarySize(), this.current
/*  379 */                   .entry.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), bis);
/*      */             }
/*  381 */             catch (IllegalArgumentException ex) {
/*  382 */               throw new IOException("bad IMPLODE data", ex);
/*      */             } 
/*      */             break;
/*      */           case BZIP2:
/*  386 */             this.current.in = (InputStream)new BZip2CompressorInputStream(bis);
/*      */             break;
/*      */           case ENHANCED_DEFLATED:
/*  389 */             this.current.in = (InputStream)new Deflate64CompressorInputStream(bis);
/*      */             break;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       } 
/*  398 */     } else if (m == ZipMethod.ENHANCED_DEFLATED) {
/*  399 */       this.current.in = (InputStream)new Deflate64CompressorInputStream(this.in);
/*      */     } 
/*      */     
/*  402 */     this.entriesRead++;
/*  403 */     return this.current.entry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void readFirstLocalFileHeader() throws IOException {
/*  412 */     readFully(this.lfhBuf);
/*  413 */     ZipLong sig = new ZipLong(this.lfhBuf);
/*      */     
/*  415 */     if (!this.skipSplitSig && sig.equals(ZipLong.DD_SIG)) {
/*  416 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.SPLITTING);
/*      */     }
/*      */ 
/*      */     
/*  420 */     if (sig.equals(ZipLong.SINGLE_SEGMENT_SPLIT_MARKER) || sig.equals(ZipLong.DD_SIG)) {
/*      */       
/*  422 */       byte[] missedLfhBytes = new byte[4];
/*  423 */       readFully(missedLfhBytes);
/*  424 */       System.arraycopy(this.lfhBuf, 4, this.lfhBuf, 0, 26);
/*  425 */       System.arraycopy(missedLfhBytes, 0, this.lfhBuf, 26, 4);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void processZip64Extra(ZipLong size, ZipLong cSize) throws ZipException {
/*  436 */     ZipExtraField extra = this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  437 */     if (extra != null && !(extra instanceof Zip64ExtendedInformationExtraField)) {
/*  438 */       throw new ZipException("archive contains unparseable zip64 extra field");
/*      */     }
/*  440 */     Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)extra;
/*      */     
/*  442 */     this.current.usesZip64 = (z64 != null);
/*  443 */     if (!this.current.hasDataDescriptor) {
/*  444 */       if (z64 != null && (ZipLong.ZIP64_MAGIC
/*  445 */         .equals(cSize) || ZipLong.ZIP64_MAGIC.equals(size))) {
/*  446 */         if (z64.getCompressedSize() == null || z64.getSize() == null)
/*      */         {
/*  448 */           throw new ZipException("archive contains corrupted zip64 extra field");
/*      */         }
/*  450 */         long s = z64.getCompressedSize().getLongValue();
/*  451 */         if (s < 0L) {
/*  452 */           throw new ZipException("broken archive, entry with negative compressed size");
/*      */         }
/*  454 */         this.current.entry.setCompressedSize(s);
/*  455 */         s = z64.getSize().getLongValue();
/*  456 */         if (s < 0L) {
/*  457 */           throw new ZipException("broken archive, entry with negative size");
/*      */         }
/*  459 */         this.current.entry.setSize(s);
/*  460 */       } else if (cSize != null && size != null) {
/*  461 */         if (cSize.getValue() < 0L) {
/*  462 */           throw new ZipException("broken archive, entry with negative compressed size");
/*      */         }
/*  464 */         this.current.entry.setCompressedSize(cSize.getValue());
/*  465 */         if (size.getValue() < 0L) {
/*  466 */           throw new ZipException("broken archive, entry with negative size");
/*      */         }
/*  468 */         this.current.entry.setSize(size.getValue());
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public ArchiveEntry getNextEntry() throws IOException {
/*  475 */     return getNextZipEntry();
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
/*      */   public boolean canReadEntryData(ArchiveEntry ae) {
/*  487 */     if (ae instanceof ZipArchiveEntry) {
/*  488 */       ZipArchiveEntry ze = (ZipArchiveEntry)ae;
/*  489 */       return (ZipUtil.canHandleEntryData(ze) && 
/*  490 */         supportsDataDescriptorFor(ze) && 
/*  491 */         supportsCompressedSizeFor(ze));
/*      */     } 
/*  493 */     return false;
/*      */   }
/*      */   
/*      */   public int read(byte[] buffer, int offset, int length) throws IOException {
/*      */     int read;
/*  498 */     if (length == 0) {
/*  499 */       return 0;
/*      */     }
/*  501 */     if (this.closed) {
/*  502 */       throw new IOException("The stream is closed");
/*      */     }
/*      */     
/*  505 */     if (this.current == null) {
/*  506 */       return -1;
/*      */     }
/*      */ 
/*      */     
/*  510 */     if (offset > buffer.length || length < 0 || offset < 0 || buffer.length - offset < length) {
/*  511 */       throw new ArrayIndexOutOfBoundsException();
/*      */     }
/*      */     
/*  514 */     ZipUtil.checkRequestedFeatures(this.current.entry);
/*  515 */     if (!supportsDataDescriptorFor(this.current.entry)) {
/*  516 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.DATA_DESCRIPTOR, this.current
/*  517 */           .entry);
/*      */     }
/*  519 */     if (!supportsCompressedSizeFor(this.current.entry)) {
/*  520 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.UNKNOWN_COMPRESSED_SIZE, this.current
/*  521 */           .entry);
/*      */     }
/*      */ 
/*      */     
/*  525 */     if (this.current.entry.getMethod() == 0) {
/*  526 */       read = readStored(buffer, offset, length);
/*  527 */     } else if (this.current.entry.getMethod() == 8) {
/*  528 */       read = readDeflated(buffer, offset, length);
/*  529 */     } else if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || this.current
/*  530 */       .entry.getMethod() == ZipMethod.IMPLODING.getCode() || this.current
/*  531 */       .entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || this.current
/*  532 */       .entry.getMethod() == ZipMethod.BZIP2.getCode()) {
/*  533 */       read = this.current.in.read(buffer, offset, length);
/*      */     } else {
/*  535 */       throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(this.current.entry.getMethod()), this.current
/*  536 */           .entry);
/*      */     } 
/*      */     
/*  539 */     if (read >= 0) {
/*  540 */       this.current.crc.update(buffer, offset, read);
/*  541 */       this.uncompressedCount += read;
/*      */     } 
/*      */     
/*  544 */     return read;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getCompressedCount() {
/*  552 */     if (this.current.entry.getMethod() == 0) {
/*  553 */       return this.current.bytesRead;
/*      */     }
/*  555 */     if (this.current.entry.getMethod() == 8) {
/*  556 */       return getBytesInflated();
/*      */     }
/*  558 */     if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) {
/*  559 */       return ((UnshrinkingInputStream)this.current.in).getCompressedCount();
/*      */     }
/*  561 */     if (this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode()) {
/*  562 */       return ((ExplodingInputStream)this.current.in).getCompressedCount();
/*      */     }
/*  564 */     if (this.current.entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode()) {
/*  565 */       return ((Deflate64CompressorInputStream)this.current.in).getCompressedCount();
/*      */     }
/*  567 */     if (this.current.entry.getMethod() == ZipMethod.BZIP2.getCode()) {
/*  568 */       return ((BZip2CompressorInputStream)this.current.in).getCompressedCount();
/*      */     }
/*  570 */     return -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getUncompressedCount() {
/*  578 */     return this.uncompressedCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readStored(byte[] buffer, int offset, int length) throws IOException {
/*  586 */     if (this.current.hasDataDescriptor) {
/*  587 */       if (this.lastStoredEntry == null) {
/*  588 */         readStoredEntry();
/*      */       }
/*  590 */       return this.lastStoredEntry.read(buffer, offset, length);
/*      */     } 
/*      */     
/*  593 */     long csize = this.current.entry.getSize();
/*  594 */     if (this.current.bytesRead >= csize) {
/*  595 */       return -1;
/*      */     }
/*      */     
/*  598 */     if (this.buf.position() >= this.buf.limit()) {
/*  599 */       this.buf.position(0);
/*  600 */       int l = this.in.read(this.buf.array());
/*  601 */       if (l == -1) {
/*  602 */         this.buf.limit(0);
/*  603 */         throw new IOException("Truncated ZIP file");
/*      */       } 
/*  605 */       this.buf.limit(l);
/*      */       
/*  607 */       count(l);
/*  608 */       CurrentEntry currentEntry1 = this.current; currentEntry1.bytesReadFromStream = currentEntry1.bytesReadFromStream + l;
/*      */     } 
/*      */     
/*  611 */     int toRead = Math.min(this.buf.remaining(), length);
/*  612 */     if (csize - this.current.bytesRead < toRead)
/*      */     {
/*  614 */       toRead = (int)(csize - this.current.bytesRead);
/*      */     }
/*  616 */     this.buf.get(buffer, offset, toRead);
/*  617 */     CurrentEntry currentEntry = this.current; currentEntry.bytesRead = currentEntry.bytesRead + toRead;
/*  618 */     return toRead;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readDeflated(byte[] buffer, int offset, int length) throws IOException {
/*  625 */     int read = readFromInflater(buffer, offset, length);
/*  626 */     if (read <= 0) {
/*  627 */       if (this.inf.finished()) {
/*  628 */         return -1;
/*      */       }
/*  630 */       if (this.inf.needsDictionary()) {
/*  631 */         throw new ZipException("This archive needs a preset dictionary which is not supported by Commons Compress.");
/*      */       }
/*      */ 
/*      */       
/*  635 */       if (read == -1) {
/*  636 */         throw new IOException("Truncated ZIP file");
/*      */       }
/*      */     } 
/*  639 */     return read;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readFromInflater(byte[] buffer, int offset, int length) throws IOException {
/*  647 */     int read = 0;
/*      */     do {
/*  649 */       if (this.inf.needsInput()) {
/*  650 */         int l = fill();
/*  651 */         if (l > 0)
/*  652 */         { CurrentEntry currentEntry = this.current; currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream + this.buf.limit(); }
/*  653 */         else { if (l == -1) {
/*  654 */             return -1;
/*      */           }
/*      */           break; }
/*      */       
/*      */       } 
/*      */       try {
/*  660 */         read = this.inf.inflate(buffer, offset, length);
/*  661 */       } catch (DataFormatException e) {
/*  662 */         throw (IOException)(new ZipException(e.getMessage())).initCause(e);
/*      */       } 
/*  664 */     } while (read == 0 && this.inf.needsInput());
/*  665 */     return read;
/*      */   }
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  670 */     if (!this.closed) {
/*  671 */       this.closed = true;
/*      */       try {
/*  673 */         this.in.close();
/*      */       } finally {
/*  675 */         this.inf.end();
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
/*      */   public long skip(long value) throws IOException {
/*  697 */     if (value >= 0L) {
/*  698 */       long skipped = 0L;
/*  699 */       while (skipped < value) {
/*  700 */         long rem = value - skipped;
/*  701 */         int x = read(this.skipBuf, 0, (int)((this.skipBuf.length > rem) ? rem : this.skipBuf.length));
/*  702 */         if (x == -1) {
/*  703 */           return skipped;
/*      */         }
/*  705 */         skipped += x;
/*      */       } 
/*  707 */       return skipped;
/*      */     } 
/*  709 */     throw new IllegalArgumentException();
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
/*      */   public static boolean matches(byte[] signature, int length) {
/*  722 */     if (length < ZipArchiveOutputStream.LFH_SIG.length) {
/*  723 */       return false;
/*      */     }
/*      */     
/*  726 */     return (checksig(signature, ZipArchiveOutputStream.LFH_SIG) || 
/*  727 */       checksig(signature, ZipArchiveOutputStream.EOCD_SIG) || 
/*  728 */       checksig(signature, ZipArchiveOutputStream.DD_SIG) || 
/*  729 */       checksig(signature, ZipLong.SINGLE_SEGMENT_SPLIT_MARKER.getBytes()));
/*      */   }
/*      */   
/*      */   private static boolean checksig(byte[] signature, byte[] expected) {
/*  733 */     for (int i = 0; i < expected.length; i++) {
/*  734 */       if (signature[i] != expected[i]) {
/*  735 */         return false;
/*      */       }
/*      */     } 
/*  738 */     return true;
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
/*      */   private void closeEntry() throws IOException {
/*  760 */     if (this.closed) {
/*  761 */       throw new IOException("The stream is closed");
/*      */     }
/*  763 */     if (this.current == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  768 */     if (currentEntryHasOutstandingBytes()) {
/*  769 */       drainCurrentEntryData();
/*      */     } else {
/*      */       
/*  772 */       skip(Long.MAX_VALUE);
/*      */ 
/*      */       
/*  775 */       long inB = (this.current.entry.getMethod() == 8) ? getBytesInflated() : this.current.bytesRead;
/*      */ 
/*      */ 
/*      */       
/*  779 */       int diff = (int)(this.current.bytesReadFromStream - inB);
/*      */ 
/*      */       
/*  782 */       if (diff > 0) {
/*  783 */         pushback(this.buf.array(), this.buf.limit() - diff, diff);
/*  784 */         CurrentEntry currentEntry = this.current; currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream - diff;
/*      */       } 
/*      */ 
/*      */       
/*  788 */       if (currentEntryHasOutstandingBytes()) {
/*  789 */         drainCurrentEntryData();
/*      */       }
/*      */     } 
/*      */     
/*  793 */     if (this.lastStoredEntry == null && this.current.hasDataDescriptor) {
/*  794 */       readDataDescriptor();
/*      */     }
/*      */     
/*  797 */     this.inf.reset();
/*  798 */     this.buf.clear().flip();
/*  799 */     this.current = null;
/*  800 */     this.lastStoredEntry = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean currentEntryHasOutstandingBytes() {
/*  811 */     return (this.current.bytesReadFromStream <= this.current.entry.getCompressedSize() && 
/*  812 */       !this.current.hasDataDescriptor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drainCurrentEntryData() throws IOException {
/*  820 */     long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream;
/*  821 */     while (remaining > 0L) {
/*  822 */       long n = this.in.read(this.buf.array(), 0, (int)Math.min(this.buf.capacity(), remaining));
/*  823 */       if (n < 0L) {
/*  824 */         throw new EOFException("Truncated ZIP entry: " + 
/*  825 */             ArchiveUtils.sanitize(this.current.entry.getName()));
/*      */       }
/*  827 */       count(n);
/*  828 */       remaining -= n;
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
/*      */   private long getBytesInflated() {
/*  848 */     long inB = this.inf.getBytesRead();
/*  849 */     if (this.current.bytesReadFromStream >= 4294967296L) {
/*  850 */       while (inB + 4294967296L <= this.current.bytesReadFromStream) {
/*  851 */         inB += 4294967296L;
/*      */       }
/*      */     }
/*  854 */     return inB;
/*      */   }
/*      */   
/*      */   private int fill() throws IOException {
/*  858 */     if (this.closed) {
/*  859 */       throw new IOException("The stream is closed");
/*      */     }
/*  861 */     int length = this.in.read(this.buf.array());
/*  862 */     if (length > 0) {
/*  863 */       this.buf.limit(length);
/*  864 */       count(this.buf.limit());
/*  865 */       this.inf.setInput(this.buf.array(), 0, this.buf.limit());
/*      */     } 
/*  867 */     return length;
/*      */   }
/*      */   
/*      */   private void readFully(byte[] b) throws IOException {
/*  871 */     readFully(b, 0);
/*      */   }
/*      */   
/*      */   private void readFully(byte[] b, int off) throws IOException {
/*  875 */     int len = b.length - off;
/*  876 */     int count = IOUtils.readFully(this.in, b, off, len);
/*  877 */     count(count);
/*  878 */     if (count < len) {
/*  879 */       throw new EOFException();
/*      */     }
/*      */   }
/*      */   
/*      */   private byte[] readRange(int len) throws IOException {
/*  884 */     byte[] ret = IOUtils.readRange(this.in, len);
/*  885 */     count(ret.length);
/*  886 */     if (ret.length < len) {
/*  887 */       throw new EOFException();
/*      */     }
/*  889 */     return ret;
/*      */   }
/*      */   
/*      */   private void readDataDescriptor() throws IOException {
/*  893 */     readFully(this.wordBuf);
/*  894 */     ZipLong val = new ZipLong(this.wordBuf);
/*  895 */     if (ZipLong.DD_SIG.equals(val)) {
/*      */       
/*  897 */       readFully(this.wordBuf);
/*  898 */       val = new ZipLong(this.wordBuf);
/*      */     } 
/*  900 */     this.current.entry.setCrc(val.getValue());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  913 */     readFully(this.twoDwordBuf);
/*  914 */     ZipLong potentialSig = new ZipLong(this.twoDwordBuf, 8);
/*  915 */     if (potentialSig.equals(ZipLong.CFH_SIG) || potentialSig.equals(ZipLong.LFH_SIG)) {
/*  916 */       pushback(this.twoDwordBuf, 8, 8);
/*  917 */       long size = ZipLong.getValue(this.twoDwordBuf);
/*  918 */       if (size < 0L) {
/*  919 */         throw new ZipException("broken archive, entry with negative compressed size");
/*      */       }
/*  921 */       this.current.entry.setCompressedSize(size);
/*  922 */       size = ZipLong.getValue(this.twoDwordBuf, 4);
/*  923 */       if (size < 0L) {
/*  924 */         throw new ZipException("broken archive, entry with negative size");
/*      */       }
/*  926 */       this.current.entry.setSize(size);
/*      */     } else {
/*  928 */       long size = ZipEightByteInteger.getLongValue(this.twoDwordBuf);
/*  929 */       if (size < 0L) {
/*  930 */         throw new ZipException("broken archive, entry with negative compressed size");
/*      */       }
/*  932 */       this.current.entry.setCompressedSize(size);
/*  933 */       size = ZipEightByteInteger.getLongValue(this.twoDwordBuf, 8);
/*  934 */       if (size < 0L) {
/*  935 */         throw new ZipException("broken archive, entry with negative size");
/*      */       }
/*  937 */       this.current.entry.setSize(size);
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
/*      */   private boolean supportsDataDescriptorFor(ZipArchiveEntry entry) {
/*  949 */     return (!entry.getGeneralPurposeBit().usesDataDescriptor() || (this.allowStoredEntriesWithDataDescriptor && entry
/*      */       
/*  951 */       .getMethod() == 0) || entry
/*  952 */       .getMethod() == 8 || entry
/*  953 */       .getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean supportsCompressedSizeFor(ZipArchiveEntry entry) {
/*  961 */     return (entry.getCompressedSize() != -1L || entry
/*  962 */       .getMethod() == 8 || entry
/*  963 */       .getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || (entry
/*  964 */       .getGeneralPurposeBit().usesDataDescriptor() && this.allowStoredEntriesWithDataDescriptor && entry
/*      */       
/*  966 */       .getMethod() == 0));
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
/*      */   private void readStoredEntry() throws IOException {
/*  993 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  994 */     int off = 0;
/*  995 */     boolean done = false;
/*      */ 
/*      */     
/*  998 */     int ddLen = this.current.usesZip64 ? 20 : 12;
/*      */     
/* 1000 */     while (!done) {
/* 1001 */       int r = this.in.read(this.buf.array(), off, 512 - off);
/* 1002 */       if (r <= 0)
/*      */       {
/*      */         
/* 1005 */         throw new IOException("Truncated ZIP file");
/*      */       }
/* 1007 */       if (r + off < 4) {
/*      */         
/* 1009 */         off += r;
/*      */         
/*      */         continue;
/*      */       } 
/* 1013 */       done = bufferContainsSignature(bos, off, r, ddLen);
/* 1014 */       if (!done) {
/* 1015 */         off = cacheBytesRead(bos, off, r, ddLen);
/*      */       }
/*      */     } 
/* 1018 */     if (this.current.entry.getCompressedSize() != this.current.entry.getSize()) {
/* 1019 */       throw new ZipException("compressed and uncompressed size don't match while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile");
/*      */     }
/*      */     
/* 1022 */     byte[] b = bos.toByteArray();
/* 1023 */     if (b.length != this.current.entry.getSize()) {
/* 1024 */       throw new ZipException("actual and claimed size don't match while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile");
/*      */     }
/*      */     
/* 1027 */     this.lastStoredEntry = new ByteArrayInputStream(b);
/*      */   }
/*      */   
/* 1030 */   private static final byte[] LFH = ZipLong.LFH_SIG.getBytes();
/* 1031 */   private static final byte[] CFH = ZipLong.CFH_SIG.getBytes();
/* 1032 */   private static final byte[] DD = ZipLong.DD_SIG.getBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean bufferContainsSignature(ByteArrayOutputStream bos, int offset, int lastRead, int expectedDDLen) throws IOException {
/* 1045 */     boolean done = false;
/* 1046 */     for (int i = 0; !done && i < offset + lastRead - 4; i++) {
/* 1047 */       if (this.buf.array()[i] == LFH[0] && this.buf.array()[i + 1] == LFH[1]) {
/* 1048 */         int expectDDPos = i;
/* 1049 */         if ((i >= expectedDDLen && this.buf
/* 1050 */           .array()[i + 2] == LFH[2] && this.buf.array()[i + 3] == LFH[3]) || (this.buf
/* 1051 */           .array()[i + 2] == CFH[2] && this.buf.array()[i + 3] == CFH[3])) {
/*      */           
/* 1053 */           expectDDPos = i - expectedDDLen;
/* 1054 */           done = true;
/*      */         }
/* 1056 */         else if (this.buf.array()[i + 2] == DD[2] && this.buf.array()[i + 3] == DD[3]) {
/*      */           
/* 1058 */           done = true;
/*      */         } 
/* 1060 */         if (done) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1065 */           pushback(this.buf.array(), expectDDPos, offset + lastRead - expectDDPos);
/* 1066 */           bos.write(this.buf.array(), 0, expectDDPos);
/* 1067 */           readDataDescriptor();
/*      */         } 
/*      */       } 
/*      */     } 
/* 1071 */     return done;
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
/*      */   private int cacheBytesRead(ByteArrayOutputStream bos, int offset, int lastRead, int expecteDDLen) {
/* 1084 */     int cacheable = offset + lastRead - expecteDDLen - 3;
/* 1085 */     if (cacheable > 0) {
/* 1086 */       bos.write(this.buf.array(), 0, cacheable);
/* 1087 */       System.arraycopy(this.buf.array(), cacheable, this.buf.array(), 0, expecteDDLen + 3);
/* 1088 */       offset = expecteDDLen + 3;
/*      */     } else {
/* 1090 */       offset += lastRead;
/*      */     } 
/* 1092 */     return offset;
/*      */   }
/*      */   
/*      */   private void pushback(byte[] buf, int offset, int length) throws IOException {
/* 1096 */     ((PushbackInputStream)this.in).unread(buf, offset, length);
/* 1097 */     pushedBackBytes(length);
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
/*      */   private void skipRemainderOfArchive() throws IOException {
/* 1125 */     if (this.entriesRead > 0) {
/* 1126 */       realSkip(this.entriesRead * 46L - 30L);
/* 1127 */       boolean foundEocd = findEocdRecord();
/* 1128 */       if (foundEocd) {
/* 1129 */         realSkip(16L);
/* 1130 */         readFully(this.shortBuf);
/*      */         
/* 1132 */         int commentLen = ZipShort.getValue(this.shortBuf);
/* 1133 */         if (commentLen >= 0) {
/* 1134 */           realSkip(commentLen);
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1139 */     throw new IOException("Truncated ZIP file");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean findEocdRecord() throws IOException {
/* 1147 */     int currentByte = -1;
/* 1148 */     boolean skipReadCall = false;
/* 1149 */     while (skipReadCall || (currentByte = readOneByte()) > -1) {
/* 1150 */       skipReadCall = false;
/* 1151 */       if (!isFirstByteOfEocdSig(currentByte)) {
/*      */         continue;
/*      */       }
/* 1154 */       currentByte = readOneByte();
/* 1155 */       if (currentByte != ZipArchiveOutputStream.EOCD_SIG[1]) {
/* 1156 */         if (currentByte == -1) {
/*      */           break;
/*      */         }
/* 1159 */         skipReadCall = isFirstByteOfEocdSig(currentByte);
/*      */         continue;
/*      */       } 
/* 1162 */       currentByte = readOneByte();
/* 1163 */       if (currentByte != ZipArchiveOutputStream.EOCD_SIG[2]) {
/* 1164 */         if (currentByte == -1) {
/*      */           break;
/*      */         }
/* 1167 */         skipReadCall = isFirstByteOfEocdSig(currentByte);
/*      */         continue;
/*      */       } 
/* 1170 */       currentByte = readOneByte();
/* 1171 */       if (currentByte == -1) {
/*      */         break;
/*      */       }
/* 1174 */       if (currentByte == ZipArchiveOutputStream.EOCD_SIG[3]) {
/* 1175 */         return true;
/*      */       }
/* 1177 */       skipReadCall = isFirstByteOfEocdSig(currentByte);
/*      */     } 
/* 1179 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void realSkip(long value) throws IOException {
/* 1190 */     if (value >= 0L) {
/* 1191 */       long skipped = 0L;
/* 1192 */       while (skipped < value) {
/* 1193 */         long rem = value - skipped;
/* 1194 */         int x = this.in.read(this.skipBuf, 0, (int)((this.skipBuf.length > rem) ? rem : this.skipBuf.length));
/* 1195 */         if (x == -1) {
/*      */           return;
/*      */         }
/* 1198 */         count(x);
/* 1199 */         skipped += x;
/*      */       } 
/*      */       return;
/*      */     } 
/* 1203 */     throw new IllegalArgumentException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readOneByte() throws IOException {
/* 1213 */     int b = this.in.read();
/* 1214 */     if (b != -1) {
/* 1215 */       count(1);
/*      */     }
/* 1217 */     return b;
/*      */   }
/*      */   
/*      */   private boolean isFirstByteOfEocdSig(int b) {
/* 1221 */     return (b == ZipArchiveOutputStream.EOCD_SIG[0]);
/*      */   }
/*      */   
/* 1224 */   private static final byte[] APK_SIGNING_BLOCK_MAGIC = new byte[] { 65, 80, 75, 32, 83, 105, 103, 32, 66, 108, 111, 99, 107, 32, 52, 50 };
/*      */ 
/*      */   
/* 1227 */   private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isApkSigningBlock(byte[] suspectLocalFileHeader) throws IOException {
/* 1245 */     BigInteger len = ZipEightByteInteger.getValue(suspectLocalFileHeader);
/*      */ 
/*      */     
/* 1248 */     BigInteger toSkip = len.add(BigInteger.valueOf((8 - suspectLocalFileHeader.length) - APK_SIGNING_BLOCK_MAGIC.length));
/*      */     
/* 1250 */     byte[] magic = new byte[APK_SIGNING_BLOCK_MAGIC.length];
/*      */     
/*      */     try {
/* 1253 */       if (toSkip.signum() < 0) {
/*      */         
/* 1255 */         int off = suspectLocalFileHeader.length + toSkip.intValue();
/*      */         
/* 1257 */         if (off < 8) {
/* 1258 */           return false;
/*      */         }
/* 1260 */         int bytesInBuffer = Math.abs(toSkip.intValue());
/* 1261 */         System.arraycopy(suspectLocalFileHeader, off, magic, 0, Math.min(bytesInBuffer, magic.length));
/* 1262 */         if (bytesInBuffer < magic.length) {
/* 1263 */           readFully(magic, bytesInBuffer);
/*      */         }
/*      */       } else {
/* 1266 */         while (toSkip.compareTo(LONG_MAX) > 0) {
/* 1267 */           realSkip(Long.MAX_VALUE);
/* 1268 */           toSkip = toSkip.add(LONG_MAX.negate());
/*      */         } 
/* 1270 */         realSkip(toSkip.longValue());
/* 1271 */         readFully(magic);
/*      */       } 
/* 1273 */     } catch (EOFException ex) {
/*      */       
/* 1275 */       return false;
/*      */     } 
/* 1277 */     return Arrays.equals(magic, APK_SIGNING_BLOCK_MAGIC);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class CurrentEntry
/*      */   {
/*      */     private CurrentEntry() {}
/*      */ 
/*      */ 
/*      */     
/* 1289 */     private final ZipArchiveEntry entry = new ZipArchiveEntry();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean hasDataDescriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean usesZip64;
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
/*      */     
/*      */     private long bytesReadFromStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1319 */     private final CRC32 crc = new CRC32();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private InputStream in;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class BoundedInputStream
/*      */     extends InputStream
/*      */   {
/*      */     private final InputStream in;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final long max;
/*      */ 
/*      */ 
/*      */     
/*      */     private long pos;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BoundedInputStream(InputStream in, long size) {
/* 1349 */       this.max = size;
/* 1350 */       this.in = in;
/*      */     }
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/* 1355 */       if (this.max >= 0L && this.pos >= this.max) {
/* 1356 */         return -1;
/*      */       }
/* 1358 */       int result = this.in.read();
/* 1359 */       this.pos++;
/* 1360 */       ZipArchiveInputStream.this.count(1);
/* 1361 */       ZipArchiveInputStream.this.current.bytesReadFromStream++;
/* 1362 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(byte[] b) throws IOException {
/* 1367 */       return read(b, 0, b.length);
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/* 1372 */       if (len == 0) {
/* 1373 */         return 0;
/*      */       }
/* 1375 */       if (this.max >= 0L && this.pos >= this.max) {
/* 1376 */         return -1;
/*      */       }
/* 1378 */       long maxRead = (this.max >= 0L) ? Math.min(len, this.max - this.pos) : len;
/* 1379 */       int bytesRead = this.in.read(b, off, (int)maxRead);
/*      */       
/* 1381 */       if (bytesRead == -1) {
/* 1382 */         return -1;
/*      */       }
/*      */       
/* 1385 */       this.pos += bytesRead;
/* 1386 */       ZipArchiveInputStream.this.count(bytesRead);
/* 1387 */       ZipArchiveInputStream.CurrentEntry currentEntry = ZipArchiveInputStream.this.current; currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream + bytesRead;
/* 1388 */       return bytesRead;
/*      */     }
/*      */ 
/*      */     
/*      */     public long skip(long n) throws IOException {
/* 1393 */       long toSkip = (this.max >= 0L) ? Math.min(n, this.max - this.pos) : n;
/* 1394 */       long skippedBytes = IOUtils.skip(this.in, toSkip);
/* 1395 */       this.pos += skippedBytes;
/* 1396 */       return skippedBytes;
/*      */     }
/*      */ 
/*      */     
/*      */     public int available() throws IOException {
/* 1401 */       if (this.max >= 0L && this.pos >= this.max) {
/* 1402 */         return 0;
/*      */       }
/* 1404 */       return this.in.available();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */