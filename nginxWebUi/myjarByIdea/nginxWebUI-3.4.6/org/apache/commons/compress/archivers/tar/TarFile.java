package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.BoundedArchiveInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

public class TarFile implements Closeable {
   private static final int SMALL_BUFFER_SIZE = 256;
   private final byte[] smallBuf;
   private final SeekableByteChannel archive;
   private final ZipEncoding zipEncoding;
   private final LinkedList<TarArchiveEntry> entries;
   private final int blockSize;
   private final boolean lenient;
   private final int recordSize;
   private final ByteBuffer recordBuffer;
   private final List<TarArchiveStructSparse> globalSparseHeaders;
   private boolean hasHitEOF;
   private TarArchiveEntry currEntry;
   private Map<String, String> globalPaxHeaders;
   private final Map<String, List<InputStream>> sparseInputStreams;

   public TarFile(byte[] content) throws IOException {
      this((SeekableByteChannel)(new SeekableInMemoryByteChannel(content)));
   }

   public TarFile(byte[] content, String encoding) throws IOException {
      this(new SeekableInMemoryByteChannel(content), 10240, 512, encoding, false);
   }

   public TarFile(byte[] content, boolean lenient) throws IOException {
      this(new SeekableInMemoryByteChannel(content), 10240, 512, (String)null, lenient);
   }

   public TarFile(File archive) throws IOException {
      this(archive.toPath());
   }

   public TarFile(File archive, String encoding) throws IOException {
      this(archive.toPath(), encoding);
   }

   public TarFile(File archive, boolean lenient) throws IOException {
      this(archive.toPath(), lenient);
   }

   public TarFile(Path archivePath) throws IOException {
      this(Files.newByteChannel(archivePath), 10240, 512, (String)null, false);
   }

   public TarFile(Path archivePath, String encoding) throws IOException {
      this(Files.newByteChannel(archivePath), 10240, 512, encoding, false);
   }

   public TarFile(Path archivePath, boolean lenient) throws IOException {
      this(Files.newByteChannel(archivePath), 10240, 512, (String)null, lenient);
   }

   public TarFile(SeekableByteChannel content) throws IOException {
      this(content, 10240, 512, (String)null, false);
   }

   public TarFile(SeekableByteChannel archive, int blockSize, int recordSize, String encoding, boolean lenient) throws IOException {
      this.smallBuf = new byte[256];
      this.entries = new LinkedList();
      this.globalSparseHeaders = new ArrayList();
      this.globalPaxHeaders = new HashMap();
      this.sparseInputStreams = new HashMap();
      this.archive = archive;
      this.hasHitEOF = false;
      this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      this.recordSize = recordSize;
      this.recordBuffer = ByteBuffer.allocate(this.recordSize);
      this.blockSize = blockSize;
      this.lenient = lenient;

      TarArchiveEntry entry;
      while((entry = this.getNextTarEntry()) != null) {
         this.entries.add(entry);
      }

   }

   private TarArchiveEntry getNextTarEntry() throws IOException {
      if (this.isAtEOF()) {
         return null;
      } else {
         if (this.currEntry != null) {
            this.repositionForwardTo(this.currEntry.getDataOffset() + this.currEntry.getSize());
            this.throwExceptionIfPositionIsNotInArchive();
            this.skipRecordPadding();
         }

         ByteBuffer headerBuf = this.getRecord();
         if (null == headerBuf) {
            this.currEntry = null;
            return null;
         } else {
            try {
               this.currEntry = new TarArchiveEntry(headerBuf.array(), this.zipEncoding, this.lenient, this.archive.position());
            } catch (IllegalArgumentException var5) {
               throw new IOException("Error detected parsing the header", var5);
            }

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

            return this.currEntry;
         }
      }
   }

   private void readOldGNUSparse() throws IOException {
      TarArchiveSparseEntry entry;
      if (this.currEntry.isExtended()) {
         do {
            ByteBuffer headerBuf = this.getRecord();
            if (headerBuf == null) {
               throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
            }

            entry = new TarArchiveSparseEntry(headerBuf.array());
            this.currEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
            this.currEntry.setDataOffset(this.currEntry.getDataOffset() + (long)this.recordSize);
         } while(entry.isExtended());
      }

      this.buildSparseInputStreams();
   }

   private void buildSparseInputStreams() throws IOException {
      List<InputStream> streams = new ArrayList();
      List<TarArchiveStructSparse> sparseHeaders = this.currEntry.getOrderedSparseHeaders();
      InputStream zeroInputStream = new TarArchiveSparseZeroInputStream();
      long offset = 0L;
      long numberOfZeroBytesInSparseEntry = 0L;

      TarArchiveStructSparse sparseHeader;
      for(Iterator var8 = sparseHeaders.iterator(); var8.hasNext(); offset = sparseHeader.getOffset() + sparseHeader.getNumbytes()) {
         sparseHeader = (TarArchiveStructSparse)var8.next();
         long zeroBlockSize = sparseHeader.getOffset() - offset;
         if (zeroBlockSize < 0L) {
            throw new IOException("Corrupted struct sparse detected");
         }

         if (zeroBlockSize > 0L) {
            streams.add(new BoundedInputStream(zeroInputStream, zeroBlockSize));
            numberOfZeroBytesInSparseEntry += zeroBlockSize;
         }

         if (sparseHeader.getNumbytes() > 0L) {
            long start = this.currEntry.getDataOffset() + sparseHeader.getOffset() - numberOfZeroBytesInSparseEntry;
            if (start + sparseHeader.getNumbytes() < start) {
               throw new IOException("Unreadable TAR archive, sparse block offset or length too big");
            }

            streams.add(new BoundedSeekableByteChannelInputStream(start, sparseHeader.getNumbytes(), this.archive));
         }
      }

      this.sparseInputStreams.put(this.currEntry.getName(), streams);
   }

   private void applyPaxHeadersToCurrentEntry(Map<String, String> headers, List<TarArchiveStructSparse> sparseHeaders) throws IOException {
      this.currEntry.updateEntryFromPaxHeaders(headers);
      this.currEntry.setSparseHeaders(sparseHeaders);
   }

   private void paxHeaders() throws IOException {
      List<TarArchiveStructSparse> sparseHeaders = new ArrayList();
      InputStream input = this.getInputStream(this.currEntry);
      Throwable var4 = null;

      Map headers;
      try {
         headers = TarUtils.parsePaxHeaders(input, sparseHeaders, this.globalPaxHeaders, this.currEntry.getSize());
      } catch (Throwable var28) {
         var4 = var28;
         throw var28;
      } finally {
         if (input != null) {
            if (var4 != null) {
               try {
                  input.close();
               } catch (Throwable var25) {
                  var4.addSuppressed(var25);
               }
            } else {
               input.close();
            }
         }

      }

      if (headers.containsKey("GNU.sparse.map")) {
         sparseHeaders = new ArrayList(TarUtils.parseFromPAX01SparseHeaders((String)headers.get("GNU.sparse.map")));
      }

      this.getNextTarEntry();
      if (this.currEntry == null) {
         throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
      } else {
         this.applyPaxHeadersToCurrentEntry(headers, sparseHeaders);
         if (this.currEntry.isPaxGNU1XSparse()) {
            input = this.getInputStream(this.currEntry);
            var4 = null;

            List sparseHeaders;
            try {
               sparseHeaders = TarUtils.parsePAX1XSparseHeaders(input, this.recordSize);
            } catch (Throwable var27) {
               var4 = var27;
               throw var27;
            } finally {
               if (input != null) {
                  if (var4 != null) {
                     try {
                        input.close();
                     } catch (Throwable var26) {
                        var4.addSuppressed(var26);
                     }
                  } else {
                     input.close();
                  }
               }

            }

            this.currEntry.setSparseHeaders(sparseHeaders);
            this.currEntry.setDataOffset(this.currEntry.getDataOffset() + (long)this.recordSize);
         }

         this.buildSparseInputStreams();
      }
   }

   private void readGlobalPaxHeaders() throws IOException {
      InputStream input = this.getInputStream(this.currEntry);
      Throwable var2 = null;

      try {
         this.globalPaxHeaders = TarUtils.parsePaxHeaders(input, this.globalSparseHeaders, this.globalPaxHeaders, this.currEntry.getSize());
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (input != null) {
            if (var2 != null) {
               try {
                  input.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               input.close();
            }
         }

      }

      this.getNextTarEntry();
      if (this.currEntry == null) {
         throw new IOException("Error detected parsing the pax header");
      }
   }

   private byte[] getLongNameData() throws IOException {
      ByteArrayOutputStream longName = new ByteArrayOutputStream();
      InputStream in = this.getInputStream(this.currEntry);
      Throwable var4 = null;

      int length;
      try {
         while((length = in.read(this.smallBuf)) >= 0) {
            longName.write(this.smallBuf, 0, length);
         }
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (in != null) {
            if (var4 != null) {
               try {
                  in.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               in.close();
            }
         }

      }

      this.getNextTarEntry();
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

   private void skipRecordPadding() throws IOException {
      if (!this.isDirectory() && this.currEntry.getSize() > 0L && this.currEntry.getSize() % (long)this.recordSize != 0L) {
         long numRecords = this.currEntry.getSize() / (long)this.recordSize + 1L;
         long padding = numRecords * (long)this.recordSize - this.currEntry.getSize();
         this.repositionForwardBy(padding);
         this.throwExceptionIfPositionIsNotInArchive();
      }

   }

   private void repositionForwardTo(long newPosition) throws IOException {
      long currPosition = this.archive.position();
      if (newPosition < currPosition) {
         throw new IOException("trying to move backwards inside of the archive");
      } else {
         this.archive.position(newPosition);
      }
   }

   private void repositionForwardBy(long offset) throws IOException {
      this.repositionForwardTo(this.archive.position() + offset);
   }

   private void throwExceptionIfPositionIsNotInArchive() throws IOException {
      if (this.archive.size() < this.archive.position()) {
         throw new IOException("Truncated TAR archive");
      }
   }

   private ByteBuffer getRecord() throws IOException {
      ByteBuffer headerBuf = this.readRecord();
      this.setAtEOF(this.isEOFRecord(headerBuf));
      if (this.isAtEOF() && headerBuf != null) {
         this.tryToConsumeSecondEOFRecord();
         this.consumeRemainderOfLastBlock();
         headerBuf = null;
      }

      return headerBuf;
   }

   private void tryToConsumeSecondEOFRecord() throws IOException {
      boolean shouldReset = true;

      try {
         shouldReset = !this.isEOFRecord(this.readRecord());
      } finally {
         if (shouldReset) {
            this.archive.position(this.archive.position() - (long)this.recordSize);
         }

      }

   }

   private void consumeRemainderOfLastBlock() throws IOException {
      long bytesReadOfLastBlock = this.archive.position() % (long)this.blockSize;
      if (bytesReadOfLastBlock > 0L) {
         this.repositionForwardBy((long)this.blockSize - bytesReadOfLastBlock);
      }

   }

   private ByteBuffer readRecord() throws IOException {
      this.recordBuffer.rewind();
      int readNow = this.archive.read(this.recordBuffer);
      return readNow != this.recordSize ? null : this.recordBuffer;
   }

   public List<TarArchiveEntry> getEntries() {
      return new ArrayList(this.entries);
   }

   private boolean isEOFRecord(ByteBuffer headerBuf) {
      return headerBuf == null || ArchiveUtils.isArrayZero(headerBuf.array(), this.recordSize);
   }

   protected final boolean isAtEOF() {
      return this.hasHitEOF;
   }

   protected final void setAtEOF(boolean b) {
      this.hasHitEOF = b;
   }

   private boolean isDirectory() {
      return this.currEntry != null && this.currEntry.isDirectory();
   }

   public InputStream getInputStream(TarArchiveEntry entry) throws IOException {
      try {
         return new BoundedTarEntryInputStream(entry, this.archive);
      } catch (RuntimeException var3) {
         throw new IOException("Corrupted TAR archive. Can't read entry", var3);
      }
   }

   public void close() throws IOException {
      this.archive.close();
   }

   private final class BoundedTarEntryInputStream extends BoundedArchiveInputStream {
      private final SeekableByteChannel channel;
      private final TarArchiveEntry entry;
      private long entryOffset;
      private int currentSparseInputStreamIndex;

      BoundedTarEntryInputStream(TarArchiveEntry entry, SeekableByteChannel channel) throws IOException {
         super(entry.getDataOffset(), entry.getRealSize());
         if (channel.size() - entry.getSize() < entry.getDataOffset()) {
            throw new IOException("entry size exceeds archive size");
         } else {
            this.entry = entry;
            this.channel = channel;
         }
      }

      protected int read(long pos, ByteBuffer buf) throws IOException {
         if (this.entryOffset >= this.entry.getRealSize()) {
            return -1;
         } else {
            int totalRead;
            if (this.entry.isSparse()) {
               totalRead = this.readSparse(this.entryOffset, buf, buf.limit());
            } else {
               totalRead = this.readArchive(pos, buf);
            }

            if (totalRead == -1) {
               if (buf.array().length > 0) {
                  throw new IOException("Truncated TAR archive");
               }

               TarFile.this.setAtEOF(true);
            } else {
               this.entryOffset += (long)totalRead;
               buf.flip();
            }

            return totalRead;
         }
      }

      private int readSparse(long pos, ByteBuffer buf, int numToRead) throws IOException {
         List<InputStream> entrySparseInputStreams = (List)TarFile.this.sparseInputStreams.get(this.entry.getName());
         if (entrySparseInputStreams != null && !entrySparseInputStreams.isEmpty()) {
            if (this.currentSparseInputStreamIndex >= entrySparseInputStreams.size()) {
               return -1;
            } else {
               InputStream currentInputStream = (InputStream)entrySparseInputStreams.get(this.currentSparseInputStreamIndex);
               byte[] bufArray = new byte[numToRead];
               int readLen = currentInputStream.read(bufArray);
               if (readLen != -1) {
                  buf.put(bufArray, 0, readLen);
               }

               if (this.currentSparseInputStreamIndex == entrySparseInputStreams.size() - 1) {
                  return readLen;
               } else if (readLen == -1) {
                  ++this.currentSparseInputStreamIndex;
                  return this.readSparse(pos, buf, numToRead);
               } else if (readLen < numToRead) {
                  ++this.currentSparseInputStreamIndex;
                  int readLenOfNext = this.readSparse(pos + (long)readLen, buf, numToRead - readLen);
                  return readLenOfNext == -1 ? readLen : readLen + readLenOfNext;
               } else {
                  return readLen;
               }
            }
         } else {
            return this.readArchive(this.entry.getDataOffset() + pos, buf);
         }
      }

      private int readArchive(long pos, ByteBuffer buf) throws IOException {
         this.channel.position(pos);
         return this.channel.read(buf);
      }
   }
}
