package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarArchiveInputStream extends ArchiveInputStream {
   private static final int SMALL_BUFFER_SIZE = 256;
   private final byte[] smallBuf;
   private final int recordSize;
   private final byte[] recordBuffer;
   private final int blockSize;
   private boolean hasHitEOF;
   private long entrySize;
   private long entryOffset;
   private final InputStream inputStream;
   private List<InputStream> sparseInputStreams;
   private int currentSparseInputStreamIndex;
   private TarArchiveEntry currEntry;
   private final ZipEncoding zipEncoding;
   final String encoding;
   private Map<String, String> globalPaxHeaders;
   private final List<TarArchiveStructSparse> globalSparseHeaders;
   private final boolean lenient;

   public TarArchiveInputStream(InputStream is) {
      this(is, 10240, 512);
   }

   public TarArchiveInputStream(InputStream is, boolean lenient) {
      this(is, 10240, 512, (String)null, lenient);
   }

   public TarArchiveInputStream(InputStream is, String encoding) {
      this(is, 10240, 512, encoding);
   }

   public TarArchiveInputStream(InputStream is, int blockSize) {
      this(is, blockSize, 512);
   }

   public TarArchiveInputStream(InputStream is, int blockSize, String encoding) {
      this(is, blockSize, 512, encoding);
   }

   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize) {
      this(is, blockSize, recordSize, (String)null);
   }

   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding) {
      this(is, blockSize, recordSize, encoding, false);
   }

   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding, boolean lenient) {
      this.smallBuf = new byte[256];
      this.globalPaxHeaders = new HashMap();
      this.globalSparseHeaders = new ArrayList();
      this.inputStream = is;
      this.hasHitEOF = false;
      this.encoding = encoding;
      this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      this.recordSize = recordSize;
      this.recordBuffer = new byte[recordSize];
      this.blockSize = blockSize;
      this.lenient = lenient;
   }

   public void close() throws IOException {
      if (this.sparseInputStreams != null) {
         Iterator var1 = this.sparseInputStreams.iterator();

         while(var1.hasNext()) {
            InputStream inputStream = (InputStream)var1.next();
            inputStream.close();
         }
      }

      this.inputStream.close();
   }

   public int getRecordSize() {
      return this.recordSize;
   }

   public int available() throws IOException {
      if (this.isDirectory()) {
         return 0;
      } else {
         return this.currEntry.getRealSize() - this.entryOffset > 2147483647L ? Integer.MAX_VALUE : (int)(this.currEntry.getRealSize() - this.entryOffset);
      }
   }

   public long skip(long n) throws IOException {
      if (n > 0L && !this.isDirectory()) {
         long availableOfInputStream = (long)this.inputStream.available();
         long available = this.currEntry.getRealSize() - this.entryOffset;
         long numToSkip = Math.min(n, available);
         long skipped;
         if (!this.currEntry.isSparse()) {
            skipped = IOUtils.skip(this.inputStream, numToSkip);
            skipped = this.getActuallySkipped(availableOfInputStream, skipped, numToSkip);
         } else {
            skipped = this.skipSparse(numToSkip);
         }

         this.count(skipped);
         this.entryOffset += skipped;
         return skipped;
      } else {
         return 0L;
      }
   }

   private long skipSparse(long n) throws IOException {
      if (this.sparseInputStreams != null && !this.sparseInputStreams.isEmpty()) {
         long bytesSkipped = 0L;

         while(bytesSkipped < n && this.currentSparseInputStreamIndex < this.sparseInputStreams.size()) {
            InputStream currentInputStream = (InputStream)this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
            bytesSkipped += currentInputStream.skip(n - bytesSkipped);
            if (bytesSkipped < n) {
               ++this.currentSparseInputStreamIndex;
            }
         }

         return bytesSkipped;
      } else {
         return this.inputStream.skip(n);
      }
   }

   public boolean markSupported() {
      return false;
   }

   public synchronized void mark(int markLimit) {
   }

   public synchronized void reset() {
   }

   public TarArchiveEntry getNextTarEntry() throws IOException {
      if (this.isAtEOF()) {
         return null;
      } else {
         if (this.currEntry != null) {
            IOUtils.skip(this, Long.MAX_VALUE);
            this.skipRecordPadding();
         }

         byte[] headerBuf = this.getRecord();
         if (headerBuf == null) {
            this.currEntry = null;
            return null;
         } else {
            try {
               this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding, this.lenient);
            } catch (IllegalArgumentException var5) {
               throw new IOException("Error detected parsing the header", var5);
            }

            this.entryOffset = 0L;
            this.entrySize = this.currEntry.getSize();
            byte[] longNameData;
            if (this.currEntry.isGNULongLinkEntry()) {
               longNameData = this.getLongNameData();
               if (longNameData == null) {
                  return null;
               }

               this.currEntry.setLinkName(this.zipEncoding.decode(longNameData));
            }

            if (this.currEntry.isGNULongNameEntry()) {
               longNameData = this.getLongNameData();
               if (longNameData == null) {
                  return null;
               }

               String name = this.zipEncoding.decode(longNameData);
               this.currEntry.setName(name);
               if (this.currEntry.isDirectory() && !name.endsWith("/")) {
                  this.currEntry.setName(name + "/");
               }
            }

            if (this.currEntry.isGlobalPaxHeader()) {
               this.readGlobalPaxHeaders();
            }

            try {
               if (this.currEntry.isPaxHeader()) {
                  this.paxHeaders();
               } else if (!this.globalPaxHeaders.isEmpty()) {
                  this.applyPaxHeadersToCurrentEntry(this.globalPaxHeaders, this.globalSparseHeaders);
               }
            } catch (NumberFormatException var4) {
               throw new IOException("Error detected parsing the pax header", var4);
            }

            if (this.currEntry.isOldGNUSparse()) {
               this.readOldGNUSparse();
            }

            this.entrySize = this.currEntry.getSize();
            return this.currEntry;
         }
      }
   }

   private void skipRecordPadding() throws IOException {
      if (!this.isDirectory() && this.entrySize > 0L && this.entrySize % (long)this.recordSize != 0L) {
         long available = (long)this.inputStream.available();
         long numRecords = this.entrySize / (long)this.recordSize + 1L;
         long padding = numRecords * (long)this.recordSize - this.entrySize;
         long skipped = IOUtils.skip(this.inputStream, padding);
         skipped = this.getActuallySkipped(available, skipped, padding);
         this.count(skipped);
      }

   }

   private long getActuallySkipped(long available, long skipped, long expected) throws IOException {
      long actuallySkipped = skipped;
      if (this.inputStream instanceof FileInputStream) {
         actuallySkipped = Math.min(skipped, available);
      }

      if (actuallySkipped != expected) {
         throw new IOException("Truncated TAR archive");
      } else {
         return actuallySkipped;
      }
   }

   protected byte[] getLongNameData() throws IOException {
      ByteArrayOutputStream longName = new ByteArrayOutputStream();
      int length = false;

      int length;
      while((length = this.read(this.smallBuf)) >= 0) {
         longName.write(this.smallBuf, 0, length);
      }

      this.getNextEntry();
      if (this.currEntry == null) {
         return null;
      } else {
         byte[] longNameData = longName.toByteArray();

         for(length = longNameData.length; length > 0 && longNameData[length - 1] == 0; --length) {
         }

         if (length != longNameData.length) {
            byte[] l = new byte[length];
            System.arraycopy(longNameData, 0, l, 0, length);
            longNameData = l;
         }

         return longNameData;
      }
   }

   private byte[] getRecord() throws IOException {
      byte[] headerBuf = this.readRecord();
      this.setAtEOF(this.isEOFRecord(headerBuf));
      if (this.isAtEOF() && headerBuf != null) {
         this.tryToConsumeSecondEOFRecord();
         this.consumeRemainderOfLastBlock();
         headerBuf = null;
      }

      return headerBuf;
   }

   protected boolean isEOFRecord(byte[] record) {
      return record == null || ArchiveUtils.isArrayZero(record, this.recordSize);
   }

   protected byte[] readRecord() throws IOException {
      int readNow = IOUtils.readFully(this.inputStream, this.recordBuffer);
      this.count(readNow);
      return readNow != this.recordSize ? null : this.recordBuffer;
   }

   private void readGlobalPaxHeaders() throws IOException {
      this.globalPaxHeaders = TarUtils.parsePaxHeaders(this, this.globalSparseHeaders, this.globalPaxHeaders, this.entrySize);
      this.getNextEntry();
      if (this.currEntry == null) {
         throw new IOException("Error detected parsing the pax header");
      }
   }

   private void paxHeaders() throws IOException {
      List<TarArchiveStructSparse> sparseHeaders = new ArrayList();
      Map<String, String> headers = TarUtils.parsePaxHeaders(this, sparseHeaders, this.globalPaxHeaders, this.entrySize);
      if (headers.containsKey("GNU.sparse.map")) {
         sparseHeaders = new ArrayList(TarUtils.parseFromPAX01SparseHeaders((String)headers.get("GNU.sparse.map")));
      }

      this.getNextEntry();
      if (this.currEntry == null) {
         throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
      } else {
         this.applyPaxHeadersToCurrentEntry(headers, sparseHeaders);
         if (this.currEntry.isPaxGNU1XSparse()) {
            List<TarArchiveStructSparse> sparseHeaders = TarUtils.parsePAX1XSparseHeaders(this.inputStream, this.recordSize);
            this.currEntry.setSparseHeaders(sparseHeaders);
         }

         this.buildSparseInputStreams();
      }
   }

   private void applyPaxHeadersToCurrentEntry(Map<String, String> headers, List<TarArchiveStructSparse> sparseHeaders) throws IOException {
      this.currEntry.updateEntryFromPaxHeaders(headers);
      this.currEntry.setSparseHeaders(sparseHeaders);
   }

   private void readOldGNUSparse() throws IOException {
      TarArchiveSparseEntry entry;
      if (this.currEntry.isExtended()) {
         do {
            byte[] headerBuf = this.getRecord();
            if (headerBuf == null) {
               throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
            }

            entry = new TarArchiveSparseEntry(headerBuf);
            this.currEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
         } while(entry.isExtended());
      }

      this.buildSparseInputStreams();
   }

   private boolean isDirectory() {
      return this.currEntry != null && this.currEntry.isDirectory();
   }

   public ArchiveEntry getNextEntry() throws IOException {
      return this.getNextTarEntry();
   }

   private void tryToConsumeSecondEOFRecord() throws IOException {
      boolean shouldReset = true;
      boolean marked = this.inputStream.markSupported();
      if (marked) {
         this.inputStream.mark(this.recordSize);
      }

      try {
         shouldReset = !this.isEOFRecord(this.readRecord());
      } finally {
         if (shouldReset && marked) {
            this.pushedBackBytes((long)this.recordSize);
            this.inputStream.reset();
         }

      }

   }

   public int read(byte[] buf, int offset, int numToRead) throws IOException {
      if (numToRead == 0) {
         return 0;
      } else {
         int totalRead = false;
         if (!this.isAtEOF() && !this.isDirectory()) {
            if (this.currEntry == null) {
               throw new IllegalStateException("No current tar entry");
            } else if (this.entryOffset >= this.currEntry.getRealSize()) {
               return -1;
            } else {
               numToRead = Math.min(numToRead, this.available());
               int totalRead;
               if (this.currEntry.isSparse()) {
                  totalRead = this.readSparse(buf, offset, numToRead);
               } else {
                  totalRead = this.inputStream.read(buf, offset, numToRead);
               }

               if (totalRead == -1) {
                  if (numToRead > 0) {
                     throw new IOException("Truncated TAR archive");
                  }

                  this.setAtEOF(true);
               } else {
                  this.count(totalRead);
                  this.entryOffset += (long)totalRead;
               }

               return totalRead;
            }
         } else {
            return -1;
         }
      }
   }

   private int readSparse(byte[] buf, int offset, int numToRead) throws IOException {
      if (this.sparseInputStreams != null && !this.sparseInputStreams.isEmpty()) {
         if (this.currentSparseInputStreamIndex >= this.sparseInputStreams.size()) {
            return -1;
         } else {
            InputStream currentInputStream = (InputStream)this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
            int readLen = currentInputStream.read(buf, offset, numToRead);
            if (this.currentSparseInputStreamIndex == this.sparseInputStreams.size() - 1) {
               return readLen;
            } else if (readLen == -1) {
               ++this.currentSparseInputStreamIndex;
               return this.readSparse(buf, offset, numToRead);
            } else if (readLen < numToRead) {
               ++this.currentSparseInputStreamIndex;
               int readLenOfNext = this.readSparse(buf, offset + readLen, numToRead - readLen);
               return readLenOfNext == -1 ? readLen : readLen + readLenOfNext;
            } else {
               return readLen;
            }
         }
      } else {
         return this.inputStream.read(buf, offset, numToRead);
      }
   }

   public boolean canReadEntryData(ArchiveEntry ae) {
      return ae instanceof TarArchiveEntry;
   }

   public TarArchiveEntry getCurrentEntry() {
      return this.currEntry;
   }

   protected final void setCurrentEntry(TarArchiveEntry e) {
      this.currEntry = e;
   }

   protected final boolean isAtEOF() {
      return this.hasHitEOF;
   }

   protected final void setAtEOF(boolean b) {
      this.hasHitEOF = b;
   }

   private void consumeRemainderOfLastBlock() throws IOException {
      long bytesReadOfLastBlock = this.getBytesRead() % (long)this.blockSize;
      if (bytesReadOfLastBlock > 0L) {
         long skipped = IOUtils.skip(this.inputStream, (long)this.blockSize - bytesReadOfLastBlock);
         this.count(skipped);
      }

   }

   public static boolean matches(byte[] signature, int length) {
      if (length < 265) {
         return false;
      } else if (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2)) {
         return true;
      } else if (ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(" \u0000", signature, 263, 2) || ArchiveUtils.matchAsciiBuffer("0\u0000", signature, 263, 2))) {
         return true;
      } else {
         return ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("\u0000\u0000", signature, 263, 2);
      }
   }

   private void buildSparseInputStreams() throws IOException {
      this.currentSparseInputStreamIndex = -1;
      this.sparseInputStreams = new ArrayList();
      List<TarArchiveStructSparse> sparseHeaders = this.currEntry.getOrderedSparseHeaders();
      InputStream zeroInputStream = new TarArchiveSparseZeroInputStream();
      long offset = 0L;

      TarArchiveStructSparse sparseHeader;
      for(Iterator var5 = sparseHeaders.iterator(); var5.hasNext(); offset = sparseHeader.getOffset() + sparseHeader.getNumbytes()) {
         sparseHeader = (TarArchiveStructSparse)var5.next();
         long zeroBlockSize = sparseHeader.getOffset() - offset;
         if (zeroBlockSize < 0L) {
            throw new IOException("Corrupted struct sparse detected");
         }

         if (zeroBlockSize > 0L) {
            this.sparseInputStreams.add(new BoundedInputStream(zeroInputStream, sparseHeader.getOffset() - offset));
         }

         if (sparseHeader.getNumbytes() > 0L) {
            this.sparseInputStreams.add(new BoundedInputStream(this.inputStream, sparseHeader.getNumbytes()));
         }
      }

      if (!this.sparseInputStreams.isEmpty()) {
         this.currentSparseInputStreamIndex = 0;
      }

   }
}
