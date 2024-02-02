package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class ZipArchiveInputStream extends ArchiveInputStream implements InputStreamStatistics {
   private final ZipEncoding zipEncoding;
   final String encoding;
   private final boolean useUnicodeExtraFields;
   private final InputStream in;
   private final Inflater inf;
   private final ByteBuffer buf;
   private CurrentEntry current;
   private boolean closed;
   private boolean hitCentralDirectory;
   private ByteArrayInputStream lastStoredEntry;
   private boolean allowStoredEntriesWithDataDescriptor;
   private long uncompressedCount;
   private final boolean skipSplitSig;
   private static final int LFH_LEN = 30;
   private static final int CFH_LEN = 46;
   private static final long TWO_EXP_32 = 4294967296L;
   private final byte[] lfhBuf;
   private final byte[] skipBuf;
   private final byte[] shortBuf;
   private final byte[] wordBuf;
   private final byte[] twoDwordBuf;
   private int entriesRead;
   private static final String USE_ZIPFILE_INSTEAD_OF_STREAM_DISCLAIMER = " while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile";
   private static final byte[] LFH;
   private static final byte[] CFH;
   private static final byte[] DD;
   private static final byte[] APK_SIGNING_BLOCK_MAGIC;
   private static final BigInteger LONG_MAX;

   public ZipArchiveInputStream(InputStream inputStream) {
      this(inputStream, "UTF8");
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding) {
      this(inputStream, encoding, true);
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields) {
      this(inputStream, encoding, useUnicodeExtraFields, false);
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor) {
      this(inputStream, encoding, useUnicodeExtraFields, allowStoredEntriesWithDataDescriptor, false);
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor, boolean skipSplitSig) {
      this.inf = new Inflater(true);
      this.buf = ByteBuffer.allocate(512);
      this.lfhBuf = new byte[30];
      this.skipBuf = new byte[1024];
      this.shortBuf = new byte[2];
      this.wordBuf = new byte[4];
      this.twoDwordBuf = new byte[16];
      this.encoding = encoding;
      this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      this.useUnicodeExtraFields = useUnicodeExtraFields;
      this.in = new PushbackInputStream(inputStream, this.buf.capacity());
      this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor;
      this.skipSplitSig = skipSplitSig;
      this.buf.limit(0);
   }

   public ZipArchiveEntry getNextZipEntry() throws IOException {
      this.uncompressedCount = 0L;
      boolean firstEntry = true;
      if (!this.closed && !this.hitCentralDirectory) {
         if (this.current != null) {
            this.closeEntry();
            firstEntry = false;
         }

         long currentHeaderOffset = this.getBytesRead();

         try {
            if (firstEntry) {
               this.readFirstLocalFileHeader();
            } else {
               this.readFully(this.lfhBuf);
            }
         } catch (EOFException var23) {
            return null;
         }

         ZipLong sig = new ZipLong(this.lfhBuf);
         if (!sig.equals(ZipLong.LFH_SIG)) {
            if (!sig.equals(ZipLong.CFH_SIG) && !sig.equals(ZipLong.AED_SIG) && !this.isApkSigningBlock(this.lfhBuf)) {
               throw new ZipException(String.format("Unexpected record signature: 0X%X", sig.getValue()));
            } else {
               this.hitCentralDirectory = true;
               this.skipRemainderOfArchive();
               return null;
            }
         } else {
            int off = 4;
            this.current = new CurrentEntry();
            int versionMadeBy = ZipShort.getValue(this.lfhBuf, off);
            off += 2;
            this.current.entry.setPlatform(versionMadeBy >> 8 & 15);
            GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.lfhBuf, off);
            boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
            ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
            this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
            this.current.entry.setGeneralPurposeBit(gpFlag);
            off += 2;
            this.current.entry.setMethod(ZipShort.getValue(this.lfhBuf, off));
            off += 2;
            long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.lfhBuf, off));
            this.current.entry.setTime(time);
            off += 4;
            ZipLong size = null;
            ZipLong cSize = null;
            if (!this.current.hasDataDescriptor) {
               this.current.entry.setCrc(ZipLong.getValue(this.lfhBuf, off));
               off += 4;
               cSize = new ZipLong(this.lfhBuf, off);
               off += 4;
               size = new ZipLong(this.lfhBuf, off);
               off += 4;
            } else {
               off += 12;
            }

            int fileNameLen = ZipShort.getValue(this.lfhBuf, off);
            off += 2;
            int extraLen = ZipShort.getValue(this.lfhBuf, off);
            off += 2;
            byte[] fileName = this.readRange(fileNameLen);
            this.current.entry.setName(entryEncoding.decode(fileName), fileName);
            if (hasUTF8Flag) {
               this.current.entry.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
            }

            byte[] extraData = this.readRange(extraLen);

            try {
               this.current.entry.setExtra(extraData);
            } catch (RuntimeException var22) {
               ZipException z = new ZipException("Invalid extra data in entry " + this.current.entry.getName());
               z.initCause(var22);
               throw z;
            }

            if (!hasUTF8Flag && this.useUnicodeExtraFields) {
               ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, (byte[])null);
            }

            this.processZip64Extra(size, cSize);
            this.current.entry.setLocalHeaderOffset(currentHeaderOffset);
            this.current.entry.setDataOffset(this.getBytesRead());
            this.current.entry.setStreamContiguous(true);
            ZipMethod m = ZipMethod.getMethodByCode(this.current.entry.getMethod());
            if (this.current.entry.getCompressedSize() != -1L) {
               if (ZipUtil.canHandleEntryData(this.current.entry) && m != ZipMethod.STORED && m != ZipMethod.DEFLATED) {
                  InputStream bis = new BoundedInputStream(this.in, this.current.entry.getCompressedSize());
                  switch (m) {
                     case UNSHRINKING:
                        this.current.in = new UnshrinkingInputStream(bis);
                        break;
                     case IMPLODING:
                        try {
                           this.current.in = new ExplodingInputStream(this.current.entry.getGeneralPurposeBit().getSlidingDictionarySize(), this.current.entry.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), bis);
                           break;
                        } catch (IllegalArgumentException var21) {
                           throw new IOException("bad IMPLODE data", var21);
                        }
                     case BZIP2:
                        this.current.in = new BZip2CompressorInputStream(bis);
                        break;
                     case ENHANCED_DEFLATED:
                        this.current.in = new Deflate64CompressorInputStream(bis);
                  }
               }
            } else if (m == ZipMethod.ENHANCED_DEFLATED) {
               this.current.in = new Deflate64CompressorInputStream(this.in);
            }

            ++this.entriesRead;
            return this.current.entry;
         }
      } else {
         return null;
      }
   }

   private void readFirstLocalFileHeader() throws IOException {
      this.readFully(this.lfhBuf);
      ZipLong sig = new ZipLong(this.lfhBuf);
      if (!this.skipSplitSig && sig.equals(ZipLong.DD_SIG)) {
         throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.SPLITTING);
      } else {
         if (sig.equals(ZipLong.SINGLE_SEGMENT_SPLIT_MARKER) || sig.equals(ZipLong.DD_SIG)) {
            byte[] missedLfhBytes = new byte[4];
            this.readFully(missedLfhBytes);
            System.arraycopy(this.lfhBuf, 4, this.lfhBuf, 0, 26);
            System.arraycopy(missedLfhBytes, 0, this.lfhBuf, 26, 4);
         }

      }
   }

   private void processZip64Extra(ZipLong size, ZipLong cSize) throws ZipException {
      ZipExtraField extra = this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
      if (extra != null && !(extra instanceof Zip64ExtendedInformationExtraField)) {
         throw new ZipException("archive contains unparseable zip64 extra field");
      } else {
         Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)extra;
         this.current.usesZip64 = z64 != null;
         if (!this.current.hasDataDescriptor) {
            if (z64 != null && (ZipLong.ZIP64_MAGIC.equals(cSize) || ZipLong.ZIP64_MAGIC.equals(size))) {
               if (z64.getCompressedSize() == null || z64.getSize() == null) {
                  throw new ZipException("archive contains corrupted zip64 extra field");
               }

               long s = z64.getCompressedSize().getLongValue();
               if (s < 0L) {
                  throw new ZipException("broken archive, entry with negative compressed size");
               }

               this.current.entry.setCompressedSize(s);
               s = z64.getSize().getLongValue();
               if (s < 0L) {
                  throw new ZipException("broken archive, entry with negative size");
               }

               this.current.entry.setSize(s);
            } else if (cSize != null && size != null) {
               if (cSize.getValue() < 0L) {
                  throw new ZipException("broken archive, entry with negative compressed size");
               }

               this.current.entry.setCompressedSize(cSize.getValue());
               if (size.getValue() < 0L) {
                  throw new ZipException("broken archive, entry with negative size");
               }

               this.current.entry.setSize(size.getValue());
            }
         }

      }
   }

   public ArchiveEntry getNextEntry() throws IOException {
      return this.getNextZipEntry();
   }

   public boolean canReadEntryData(ArchiveEntry ae) {
      if (!(ae instanceof ZipArchiveEntry)) {
         return false;
      } else {
         ZipArchiveEntry ze = (ZipArchiveEntry)ae;
         return ZipUtil.canHandleEntryData(ze) && this.supportsDataDescriptorFor(ze) && this.supportsCompressedSizeFor(ze);
      }
   }

   public int read(byte[] buffer, int offset, int length) throws IOException {
      if (length == 0) {
         return 0;
      } else if (this.closed) {
         throw new IOException("The stream is closed");
      } else if (this.current == null) {
         return -1;
      } else if (offset <= buffer.length && length >= 0 && offset >= 0 && buffer.length - offset >= length) {
         ZipUtil.checkRequestedFeatures(this.current.entry);
         if (!this.supportsDataDescriptorFor(this.current.entry)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.DATA_DESCRIPTOR, this.current.entry);
         } else if (!this.supportsCompressedSizeFor(this.current.entry)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.UNKNOWN_COMPRESSED_SIZE, this.current.entry);
         } else {
            int read;
            if (this.current.entry.getMethod() == 0) {
               read = this.readStored(buffer, offset, length);
            } else if (this.current.entry.getMethod() == 8) {
               read = this.readDeflated(buffer, offset, length);
            } else {
               if (this.current.entry.getMethod() != ZipMethod.UNSHRINKING.getCode() && this.current.entry.getMethod() != ZipMethod.IMPLODING.getCode() && this.current.entry.getMethod() != ZipMethod.ENHANCED_DEFLATED.getCode() && this.current.entry.getMethod() != ZipMethod.BZIP2.getCode()) {
                  throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(this.current.entry.getMethod()), this.current.entry);
               }

               read = this.current.in.read(buffer, offset, length);
            }

            if (read >= 0) {
               this.current.crc.update(buffer, offset, read);
               this.uncompressedCount += (long)read;
            }

            return read;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public long getCompressedCount() {
      if (this.current.entry.getMethod() == 0) {
         return this.current.bytesRead;
      } else if (this.current.entry.getMethod() == 8) {
         return this.getBytesInflated();
      } else if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) {
         return ((UnshrinkingInputStream)this.current.in).getCompressedCount();
      } else if (this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode()) {
         return ((ExplodingInputStream)this.current.in).getCompressedCount();
      } else if (this.current.entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode()) {
         return ((Deflate64CompressorInputStream)this.current.in).getCompressedCount();
      } else {
         return this.current.entry.getMethod() == ZipMethod.BZIP2.getCode() ? ((BZip2CompressorInputStream)this.current.in).getCompressedCount() : -1L;
      }
   }

   public long getUncompressedCount() {
      return this.uncompressedCount;
   }

   private int readStored(byte[] buffer, int offset, int length) throws IOException {
      if (this.current.hasDataDescriptor) {
         if (this.lastStoredEntry == null) {
            this.readStoredEntry();
         }

         return this.lastStoredEntry.read(buffer, offset, length);
      } else {
         long csize = this.current.entry.getSize();
         if (this.current.bytesRead >= csize) {
            return -1;
         } else {
            int toRead;
            CurrentEntry var7;
            if (this.buf.position() >= this.buf.limit()) {
               this.buf.position(0);
               toRead = this.in.read(this.buf.array());
               if (toRead == -1) {
                  this.buf.limit(0);
                  throw new IOException("Truncated ZIP file");
               }

               this.buf.limit(toRead);
               this.count(toRead);
               var7 = this.current;
               var7.bytesReadFromStream = var7.bytesReadFromStream + (long)toRead;
            }

            toRead = Math.min(this.buf.remaining(), length);
            if (csize - this.current.bytesRead < (long)toRead) {
               toRead = (int)(csize - this.current.bytesRead);
            }

            this.buf.get(buffer, offset, toRead);
            var7 = this.current;
            var7.bytesRead = var7.bytesRead + (long)toRead;
            return toRead;
         }
      }
   }

   private int readDeflated(byte[] buffer, int offset, int length) throws IOException {
      int read = this.readFromInflater(buffer, offset, length);
      if (read <= 0) {
         if (this.inf.finished()) {
            return -1;
         }

         if (this.inf.needsDictionary()) {
            throw new ZipException("This archive needs a preset dictionary which is not supported by Commons Compress.");
         }

         if (read == -1) {
            throw new IOException("Truncated ZIP file");
         }
      }

      return read;
   }

   private int readFromInflater(byte[] buffer, int offset, int length) throws IOException {
      int read = 0;

      do {
         if (this.inf.needsInput()) {
            int l = this.fill();
            if (l <= 0) {
               if (l == -1) {
                  return -1;
               }
               break;
            }

            CurrentEntry var6 = this.current;
            var6.bytesReadFromStream = var6.bytesReadFromStream + (long)this.buf.limit();
         }

         try {
            read = this.inf.inflate(buffer, offset, length);
         } catch (DataFormatException var7) {
            throw (IOException)(new ZipException(var7.getMessage())).initCause(var7);
         }
      } while(read == 0 && this.inf.needsInput());

      return read;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;

         try {
            this.in.close();
         } finally {
            this.inf.end();
         }
      }

   }

   public long skip(long value) throws IOException {
      if (value >= 0L) {
         long skipped;
         int x;
         for(skipped = 0L; skipped < value; skipped += (long)x) {
            long rem = value - skipped;
            x = this.read(this.skipBuf, 0, (int)((long)this.skipBuf.length > rem ? rem : (long)this.skipBuf.length));
            if (x == -1) {
               return skipped;
            }
         }

         return skipped;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean matches(byte[] signature, int length) {
      if (length < ZipArchiveOutputStream.LFH_SIG.length) {
         return false;
      } else {
         return checksig(signature, ZipArchiveOutputStream.LFH_SIG) || checksig(signature, ZipArchiveOutputStream.EOCD_SIG) || checksig(signature, ZipArchiveOutputStream.DD_SIG) || checksig(signature, ZipLong.SINGLE_SEGMENT_SPLIT_MARKER.getBytes());
      }
   }

   private static boolean checksig(byte[] signature, byte[] expected) {
      for(int i = 0; i < expected.length; ++i) {
         if (signature[i] != expected[i]) {
            return false;
         }
      }

      return true;
   }

   private void closeEntry() throws IOException {
      if (this.closed) {
         throw new IOException("The stream is closed");
      } else if (this.current != null) {
         if (this.currentEntryHasOutstandingBytes()) {
            this.drainCurrentEntryData();
         } else {
            this.skip(Long.MAX_VALUE);
            long inB = this.current.entry.getMethod() == 8 ? this.getBytesInflated() : this.current.bytesRead;
            int diff = (int)(this.current.bytesReadFromStream - inB);
            if (diff > 0) {
               this.pushback(this.buf.array(), this.buf.limit() - diff, diff);
               CurrentEntry var4 = this.current;
               var4.bytesReadFromStream = var4.bytesReadFromStream - (long)diff;
            }

            if (this.currentEntryHasOutstandingBytes()) {
               this.drainCurrentEntryData();
            }
         }

         if (this.lastStoredEntry == null && this.current.hasDataDescriptor) {
            this.readDataDescriptor();
         }

         this.inf.reset();
         this.buf.clear().flip();
         this.current = null;
         this.lastStoredEntry = null;
      }
   }

   private boolean currentEntryHasOutstandingBytes() {
      return this.current.bytesReadFromStream <= this.current.entry.getCompressedSize() && !this.current.hasDataDescriptor;
   }

   private void drainCurrentEntryData() throws IOException {
      long n;
      for(long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream; remaining > 0L; remaining -= n) {
         n = (long)this.in.read(this.buf.array(), 0, (int)Math.min((long)this.buf.capacity(), remaining));
         if (n < 0L) {
            throw new EOFException("Truncated ZIP entry: " + ArchiveUtils.sanitize(this.current.entry.getName()));
         }

         this.count(n);
      }

   }

   private long getBytesInflated() {
      long inB = this.inf.getBytesRead();
      if (this.current.bytesReadFromStream >= 4294967296L) {
         while(inB + 4294967296L <= this.current.bytesReadFromStream) {
            inB += 4294967296L;
         }
      }

      return inB;
   }

   private int fill() throws IOException {
      if (this.closed) {
         throw new IOException("The stream is closed");
      } else {
         int length = this.in.read(this.buf.array());
         if (length > 0) {
            this.buf.limit(length);
            this.count(this.buf.limit());
            this.inf.setInput(this.buf.array(), 0, this.buf.limit());
         }

         return length;
      }
   }

   private void readFully(byte[] b) throws IOException {
      this.readFully(b, 0);
   }

   private void readFully(byte[] b, int off) throws IOException {
      int len = b.length - off;
      int count = IOUtils.readFully(this.in, b, off, len);
      this.count(count);
      if (count < len) {
         throw new EOFException();
      }
   }

   private byte[] readRange(int len) throws IOException {
      byte[] ret = IOUtils.readRange(this.in, len);
      this.count(ret.length);
      if (ret.length < len) {
         throw new EOFException();
      } else {
         return ret;
      }
   }

   private void readDataDescriptor() throws IOException {
      this.readFully(this.wordBuf);
      ZipLong val = new ZipLong(this.wordBuf);
      if (ZipLong.DD_SIG.equals(val)) {
         this.readFully(this.wordBuf);
         val = new ZipLong(this.wordBuf);
      }

      this.current.entry.setCrc(val.getValue());
      this.readFully(this.twoDwordBuf);
      ZipLong potentialSig = new ZipLong(this.twoDwordBuf, 8);
      long size;
      if (!potentialSig.equals(ZipLong.CFH_SIG) && !potentialSig.equals(ZipLong.LFH_SIG)) {
         size = ZipEightByteInteger.getLongValue(this.twoDwordBuf);
         if (size < 0L) {
            throw new ZipException("broken archive, entry with negative compressed size");
         }

         this.current.entry.setCompressedSize(size);
         size = ZipEightByteInteger.getLongValue(this.twoDwordBuf, 8);
         if (size < 0L) {
            throw new ZipException("broken archive, entry with negative size");
         }

         this.current.entry.setSize(size);
      } else {
         this.pushback(this.twoDwordBuf, 8, 8);
         size = ZipLong.getValue(this.twoDwordBuf);
         if (size < 0L) {
            throw new ZipException("broken archive, entry with negative compressed size");
         }

         this.current.entry.setCompressedSize(size);
         size = ZipLong.getValue(this.twoDwordBuf, 4);
         if (size < 0L) {
            throw new ZipException("broken archive, entry with negative size");
         }

         this.current.entry.setSize(size);
      }

   }

   private boolean supportsDataDescriptorFor(ZipArchiveEntry entry) {
      return !entry.getGeneralPurposeBit().usesDataDescriptor() || this.allowStoredEntriesWithDataDescriptor && entry.getMethod() == 0 || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode();
   }

   private boolean supportsCompressedSizeFor(ZipArchiveEntry entry) {
      return entry.getCompressedSize() != -1L || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || entry.getGeneralPurposeBit().usesDataDescriptor() && this.allowStoredEntriesWithDataDescriptor && entry.getMethod() == 0;
   }

   private void readStoredEntry() throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int off = 0;
      boolean done = false;
      int ddLen = this.current.usesZip64 ? 20 : 12;

      while(!done) {
         int r = this.in.read(this.buf.array(), off, 512 - off);
         if (r <= 0) {
            throw new IOException("Truncated ZIP file");
         }

         if (r + off < 4) {
            off += r;
         } else {
            done = this.bufferContainsSignature(bos, off, r, ddLen);
            if (!done) {
               off = this.cacheBytesRead(bos, off, r, ddLen);
            }
         }
      }

      if (this.current.entry.getCompressedSize() != this.current.entry.getSize()) {
         throw new ZipException("compressed and uncompressed size don't match while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile");
      } else {
         byte[] b = bos.toByteArray();
         if ((long)b.length != this.current.entry.getSize()) {
            throw new ZipException("actual and claimed size don't match while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile");
         } else {
            this.lastStoredEntry = new ByteArrayInputStream(b);
         }
      }
   }

   private boolean bufferContainsSignature(ByteArrayOutputStream bos, int offset, int lastRead, int expectedDDLen) throws IOException {
      boolean done = false;

      for(int i = 0; !done && i < offset + lastRead - 4; ++i) {
         if (this.buf.array()[i] == LFH[0] && this.buf.array()[i + 1] == LFH[1]) {
            int expectDDPos = i;
            if (i >= expectedDDLen && this.buf.array()[i + 2] == LFH[2] && this.buf.array()[i + 3] == LFH[3] || this.buf.array()[i + 2] == CFH[2] && this.buf.array()[i + 3] == CFH[3]) {
               expectDDPos = i - expectedDDLen;
               done = true;
            } else if (this.buf.array()[i + 2] == DD[2] && this.buf.array()[i + 3] == DD[3]) {
               done = true;
            }

            if (done) {
               this.pushback(this.buf.array(), expectDDPos, offset + lastRead - expectDDPos);
               bos.write(this.buf.array(), 0, expectDDPos);
               this.readDataDescriptor();
            }
         }
      }

      return done;
   }

   private int cacheBytesRead(ByteArrayOutputStream bos, int offset, int lastRead, int expecteDDLen) {
      int cacheable = offset + lastRead - expecteDDLen - 3;
      if (cacheable > 0) {
         bos.write(this.buf.array(), 0, cacheable);
         System.arraycopy(this.buf.array(), cacheable, this.buf.array(), 0, expecteDDLen + 3);
         offset = expecteDDLen + 3;
      } else {
         offset += lastRead;
      }

      return offset;
   }

   private void pushback(byte[] buf, int offset, int length) throws IOException {
      ((PushbackInputStream)this.in).unread(buf, offset, length);
      this.pushedBackBytes((long)length);
   }

   private void skipRemainderOfArchive() throws IOException {
      if (this.entriesRead > 0) {
         this.realSkip((long)this.entriesRead * 46L - 30L);
         boolean foundEocd = this.findEocdRecord();
         if (foundEocd) {
            this.realSkip(16L);
            this.readFully(this.shortBuf);
            int commentLen = ZipShort.getValue(this.shortBuf);
            if (commentLen >= 0) {
               this.realSkip((long)commentLen);
               return;
            }
         }
      }

      throw new IOException("Truncated ZIP file");
   }

   private boolean findEocdRecord() throws IOException {
      int currentByte = -1;
      boolean skipReadCall = false;

      while(skipReadCall || (currentByte = this.readOneByte()) > -1) {
         skipReadCall = false;
         if (this.isFirstByteOfEocdSig(currentByte)) {
            currentByte = this.readOneByte();
            if (currentByte != ZipArchiveOutputStream.EOCD_SIG[1]) {
               if (currentByte == -1) {
                  break;
               }

               skipReadCall = this.isFirstByteOfEocdSig(currentByte);
            } else {
               currentByte = this.readOneByte();
               if (currentByte != ZipArchiveOutputStream.EOCD_SIG[2]) {
                  if (currentByte == -1) {
                     break;
                  }

                  skipReadCall = this.isFirstByteOfEocdSig(currentByte);
               } else {
                  currentByte = this.readOneByte();
                  if (currentByte == -1) {
                     break;
                  }

                  if (currentByte == ZipArchiveOutputStream.EOCD_SIG[3]) {
                     return true;
                  }

                  skipReadCall = this.isFirstByteOfEocdSig(currentByte);
               }
            }
         }
      }

      return false;
   }

   private void realSkip(long value) throws IOException {
      if (value >= 0L) {
         int x;
         for(long skipped = 0L; skipped < value; skipped += (long)x) {
            long rem = value - skipped;
            x = this.in.read(this.skipBuf, 0, (int)((long)this.skipBuf.length > rem ? rem : (long)this.skipBuf.length));
            if (x == -1) {
               return;
            }

            this.count(x);
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   private int readOneByte() throws IOException {
      int b = this.in.read();
      if (b != -1) {
         this.count(1);
      }

      return b;
   }

   private boolean isFirstByteOfEocdSig(int b) {
      return b == ZipArchiveOutputStream.EOCD_SIG[0];
   }

   private boolean isApkSigningBlock(byte[] suspectLocalFileHeader) throws IOException {
      BigInteger len = ZipEightByteInteger.getValue(suspectLocalFileHeader);
      BigInteger toSkip = len.add(BigInteger.valueOf((long)(8 - suspectLocalFileHeader.length) - (long)APK_SIGNING_BLOCK_MAGIC.length));
      byte[] magic = new byte[APK_SIGNING_BLOCK_MAGIC.length];

      try {
         if (toSkip.signum() < 0) {
            int off = suspectLocalFileHeader.length + toSkip.intValue();
            if (off < 8) {
               return false;
            }

            int bytesInBuffer = Math.abs(toSkip.intValue());
            System.arraycopy(suspectLocalFileHeader, off, magic, 0, Math.min(bytesInBuffer, magic.length));
            if (bytesInBuffer < magic.length) {
               this.readFully(magic, bytesInBuffer);
            }
         } else {
            while(toSkip.compareTo(LONG_MAX) > 0) {
               this.realSkip(Long.MAX_VALUE);
               toSkip = toSkip.add(LONG_MAX.negate());
            }

            this.realSkip(toSkip.longValue());
            this.readFully(magic);
         }
      } catch (EOFException var7) {
         return false;
      }

      return Arrays.equals(magic, APK_SIGNING_BLOCK_MAGIC);
   }

   static {
      LFH = ZipLong.LFH_SIG.getBytes();
      CFH = ZipLong.CFH_SIG.getBytes();
      DD = ZipLong.DD_SIG.getBytes();
      APK_SIGNING_BLOCK_MAGIC = new byte[]{65, 80, 75, 32, 83, 105, 103, 32, 66, 108, 111, 99, 107, 32, 52, 50};
      LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
   }

   private class BoundedInputStream extends InputStream {
      private final InputStream in;
      private final long max;
      private long pos;

      public BoundedInputStream(InputStream in, long size) {
         this.max = size;
         this.in = in;
      }

      public int read() throws IOException {
         if (this.max >= 0L && this.pos >= this.max) {
            return -1;
         } else {
            int result = this.in.read();
            ++this.pos;
            ZipArchiveInputStream.this.count(1);
            ZipArchiveInputStream.this.current.bytesReadFromStream++;
            return result;
         }
      }

      public int read(byte[] b) throws IOException {
         return this.read(b, 0, b.length);
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (len == 0) {
            return 0;
         } else if (this.max >= 0L && this.pos >= this.max) {
            return -1;
         } else {
            long maxRead = this.max >= 0L ? Math.min((long)len, this.max - this.pos) : (long)len;
            int bytesRead = this.in.read(b, off, (int)maxRead);
            if (bytesRead == -1) {
               return -1;
            } else {
               this.pos += (long)bytesRead;
               ZipArchiveInputStream.this.count(bytesRead);
               CurrentEntry var7 = ZipArchiveInputStream.this.current;
               var7.bytesReadFromStream = var7.bytesReadFromStream + (long)bytesRead;
               return bytesRead;
            }
         }
      }

      public long skip(long n) throws IOException {
         long toSkip = this.max >= 0L ? Math.min(n, this.max - this.pos) : n;
         long skippedBytes = IOUtils.skip(this.in, toSkip);
         this.pos += skippedBytes;
         return skippedBytes;
      }

      public int available() throws IOException {
         return this.max >= 0L && this.pos >= this.max ? 0 : this.in.available();
      }
   }

   private static final class CurrentEntry {
      private final ZipArchiveEntry entry;
      private boolean hasDataDescriptor;
      private boolean usesZip64;
      private long bytesRead;
      private long bytesReadFromStream;
      private final CRC32 crc;
      private InputStream in;

      private CurrentEntry() {
         this.entry = new ZipArchiveEntry();
         this.crc = new CRC32();
      }

      // $FF: synthetic method
      CurrentEntry(Object x0) {
         this();
      }
   }
}
