package org.apache.commons.compress.archivers.tar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.CountingOutputStream;
import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;

public class TarArchiveOutputStream extends ArchiveOutputStream {
   public static final int LONGFILE_ERROR = 0;
   public static final int LONGFILE_TRUNCATE = 1;
   public static final int LONGFILE_GNU = 2;
   public static final int LONGFILE_POSIX = 3;
   public static final int BIGNUMBER_ERROR = 0;
   public static final int BIGNUMBER_STAR = 1;
   public static final int BIGNUMBER_POSIX = 2;
   private static final int RECORD_SIZE = 512;
   private long currSize;
   private String currName;
   private long currBytes;
   private final byte[] recordBuf;
   private int longFileMode;
   private int bigNumberMode;
   private int recordsWritten;
   private final int recordsPerBlock;
   private boolean closed;
   private boolean haveUnclosedEntry;
   private boolean finished;
   private final FixedLengthBlockOutputStream out;
   private final CountingOutputStream countingOut;
   private final ZipEncoding zipEncoding;
   final String encoding;
   private boolean addPaxHeadersForNonAsciiNames;
   private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
   private static final int BLOCK_SIZE_UNSPECIFIED = -511;

   public TarArchiveOutputStream(OutputStream os) {
      this(os, -511);
   }

   public TarArchiveOutputStream(OutputStream os, String encoding) {
      this(os, -511, encoding);
   }

   public TarArchiveOutputStream(OutputStream os, int blockSize) {
      this(os, blockSize, (String)null);
   }

   /** @deprecated */
   @Deprecated
   public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize) {
      this(os, blockSize, recordSize, (String)null);
   }

   /** @deprecated */
   @Deprecated
   public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize, String encoding) {
      this(os, blockSize, encoding);
      if (recordSize != 512) {
         throw new IllegalArgumentException("Tar record size must always be 512 bytes. Attempt to set size of " + recordSize);
      }
   }

   public TarArchiveOutputStream(OutputStream os, int blockSize, String encoding) {
      this.longFileMode = 0;
      this.bigNumberMode = 0;
      int realBlockSize;
      if (-511 == blockSize) {
         realBlockSize = 512;
      } else {
         realBlockSize = blockSize;
      }

      if (realBlockSize > 0 && realBlockSize % 512 == 0) {
         this.out = new FixedLengthBlockOutputStream(this.countingOut = new CountingOutputStream(os), 512);
         this.encoding = encoding;
         this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
         this.recordBuf = new byte[512];
         this.recordsPerBlock = realBlockSize / 512;
      } else {
         throw new IllegalArgumentException("Block size must be a multiple of 512 bytes. Attempt to use set size of " + blockSize);
      }
   }

   public void setLongFileMode(int longFileMode) {
      this.longFileMode = longFileMode;
   }

   public void setBigNumberMode(int bigNumberMode) {
      this.bigNumberMode = bigNumberMode;
   }

   public void setAddPaxHeadersForNonAsciiNames(boolean b) {
      this.addPaxHeadersForNonAsciiNames = b;
   }

   /** @deprecated */
   @Deprecated
   public int getCount() {
      return (int)this.getBytesWritten();
   }

   public long getBytesWritten() {
      return this.countingOut.getBytesWritten();
   }

   public void finish() throws IOException {
      if (this.finished) {
         throw new IOException("This archive has already been finished");
      } else if (this.haveUnclosedEntry) {
         throw new IOException("This archive contains unclosed entries.");
      } else {
         this.writeEOFRecord();
         this.writeEOFRecord();
         this.padAsNeeded();
         this.out.flush();
         this.finished = true;
      }
   }

   public void close() throws IOException {
      try {
         if (!this.finished) {
            this.finish();
         }
      } finally {
         if (!this.closed) {
            this.out.close();
            this.closed = true;
         }

      }

   }

   /** @deprecated */
   @Deprecated
   public int getRecordSize() {
      return 512;
   }

   public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
      if (this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         TarArchiveEntry entry = (TarArchiveEntry)archiveEntry;
         if (entry.isGlobalPaxHeader()) {
            byte[] data = this.encodeExtendedPaxHeadersContents(entry.getExtraPaxHeaders());
            entry.setSize((long)data.length);
            entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode == 1);
            this.writeRecord(this.recordBuf);
            this.currSize = entry.getSize();
            this.currBytes = 0L;
            this.haveUnclosedEntry = true;
            this.write(data);
            this.closeArchiveEntry();
         } else {
            Map<String, String> paxHeaders = new HashMap();
            String entryName = entry.getName();
            boolean paxHeaderContainsPath = this.handleLongName(entry, entryName, paxHeaders, "path", (byte)76, "file name");
            String linkName = entry.getLinkName();
            boolean paxHeaderContainsLinkPath = linkName != null && !linkName.isEmpty() && this.handleLongName(entry, linkName, paxHeaders, "linkpath", (byte)75, "link name");
            if (this.bigNumberMode == 2) {
               this.addPaxHeadersForBigNumbers(paxHeaders, entry);
            } else if (this.bigNumberMode != 1) {
               this.failForBigNumbers(entry);
            }

            if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsPath && !ASCII.canEncode(entryName)) {
               paxHeaders.put("path", entryName);
            }

            if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsLinkPath && (entry.isLink() || entry.isSymbolicLink()) && !ASCII.canEncode(linkName)) {
               paxHeaders.put("linkpath", linkName);
            }

            paxHeaders.putAll(entry.getExtraPaxHeaders());
            if (!paxHeaders.isEmpty()) {
               this.writePaxHeaders(entry, entryName, paxHeaders);
            }

            entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode == 1);
            this.writeRecord(this.recordBuf);
            this.currBytes = 0L;
            if (entry.isDirectory()) {
               this.currSize = 0L;
            } else {
               this.currSize = entry.getSize();
            }

            this.currName = entryName;
            this.haveUnclosedEntry = true;
         }

      }
   }

   public void closeArchiveEntry() throws IOException {
      if (this.finished) {
         throw new IOException("Stream has already been finished");
      } else if (!this.haveUnclosedEntry) {
         throw new IOException("No current entry to close");
      } else {
         this.out.flushBlock();
         if (this.currBytes < this.currSize) {
            throw new IOException("Entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
         } else {
            this.recordsWritten = (int)((long)this.recordsWritten + this.currSize / 512L);
            if (0L != this.currSize % 512L) {
               ++this.recordsWritten;
            }

            this.haveUnclosedEntry = false;
         }
      }
   }

   public void write(byte[] wBuf, int wOffset, int numToWrite) throws IOException {
      if (!this.haveUnclosedEntry) {
         throw new IllegalStateException("No current tar entry");
      } else if (this.currBytes + (long)numToWrite > this.currSize) {
         throw new IOException("Request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
      } else {
         this.out.write(wBuf, wOffset, numToWrite);
         this.currBytes += (long)numToWrite;
      }
   }

   void writePaxHeaders(TarArchiveEntry entry, String entryName, Map<String, String> headers) throws IOException {
      String name = "./PaxHeaders.X/" + this.stripTo7Bits(entryName);
      if (name.length() >= 100) {
         name = name.substring(0, 99);
      }

      TarArchiveEntry pex = new TarArchiveEntry(name, (byte)120);
      this.transferModTime(entry, pex);
      byte[] data = this.encodeExtendedPaxHeadersContents(headers);
      pex.setSize((long)data.length);
      this.putArchiveEntry(pex);
      this.write(data);
      this.closeArchiveEntry();
   }

   private byte[] encodeExtendedPaxHeadersContents(Map<String, String> headers) {
      StringWriter w = new StringWriter();
      Iterator var3 = headers.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, String> h = (Map.Entry)var3.next();
         String key = (String)h.getKey();
         String value = (String)h.getValue();
         int len = key.length() + value.length() + 3 + 2;
         String line = len + " " + key + "=" + value + "\n";

         for(int actualLength = line.getBytes(StandardCharsets.UTF_8).length; len != actualLength; actualLength = line.getBytes(StandardCharsets.UTF_8).length) {
            len = actualLength;
            line = actualLength + " " + key + "=" + value + "\n";
         }

         w.write(line);
      }

      return w.toString().getBytes(StandardCharsets.UTF_8);
   }

   private String stripTo7Bits(String name) {
      int length = name.length();
      StringBuilder result = new StringBuilder(length);

      for(int i = 0; i < length; ++i) {
         char stripped = (char)(name.charAt(i) & 127);
         if (this.shouldBeReplaced(stripped)) {
            result.append("_");
         } else {
            result.append(stripped);
         }
      }

      return result.toString();
   }

   private boolean shouldBeReplaced(char c) {
      return c == 0 || c == '/' || c == '\\';
   }

   private void writeEOFRecord() throws IOException {
      Arrays.fill(this.recordBuf, (byte)0);
      this.writeRecord(this.recordBuf);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
      if (this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         return new TarArchiveEntry(inputFile, entryName);
      }
   }

   public ArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
      if (this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         return new TarArchiveEntry(inputPath, entryName, options);
      }
   }

   private void writeRecord(byte[] record) throws IOException {
      if (record.length != 512) {
         throw new IOException("Record to write has length '" + record.length + "' which is not the record size of '" + 512 + "'");
      } else {
         this.out.write(record);
         ++this.recordsWritten;
      }
   }

   private void padAsNeeded() throws IOException {
      int start = this.recordsWritten % this.recordsPerBlock;
      if (start != 0) {
         for(int i = start; i < this.recordsPerBlock; ++i) {
            this.writeEOFRecord();
         }
      }

   }

   private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarArchiveEntry entry) {
      this.addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 8589934591L);
      this.addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), 2097151L);
      this.addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000L, 8589934591L);
      this.addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), 2097151L);
      this.addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", (long)entry.getDevMajor(), 2097151L);
      this.addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", (long)entry.getDevMinor(), 2097151L);
      this.failForBigNumber("mode", (long)entry.getMode(), 2097151L);
   }

   private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue) {
      if (value < 0L || value > maxValue) {
         paxHeaders.put(header, String.valueOf(value));
      }

   }

   private void failForBigNumbers(TarArchiveEntry entry) {
      this.failForBigNumber("entry size", entry.getSize(), 8589934591L);
      this.failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), 2097151L);
      this.failForBigNumber("last modification time", entry.getModTime().getTime() / 1000L, 8589934591L);
      this.failForBigNumber("user id", entry.getLongUserId(), 2097151L);
      this.failForBigNumber("mode", (long)entry.getMode(), 2097151L);
      this.failForBigNumber("major device number", (long)entry.getDevMajor(), 2097151L);
      this.failForBigNumber("minor device number", (long)entry.getDevMinor(), 2097151L);
   }

   private void failForBigNumber(String field, long value, long maxValue) {
      this.failForBigNumber(field, value, maxValue, "");
   }

   private void failForBigNumberWithPosixMessage(String field, long value, long maxValue) {
      this.failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
   }

   private void failForBigNumber(String field, long value, long maxValue, String additionalMsg) {
      if (value < 0L || value > maxValue) {
         throw new IllegalArgumentException(field + " '" + value + "' is too big ( > " + maxValue + " )." + additionalMsg);
      }
   }

   private boolean handleLongName(TarArchiveEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName) throws IOException {
      ByteBuffer encodedName = this.zipEncoding.encode(name);
      int len = encodedName.limit() - encodedName.position();
      if (len >= 100) {
         if (this.longFileMode == 3) {
            paxHeaders.put(paxHeaderName, name);
            return true;
         }

         if (this.longFileMode == 2) {
            TarArchiveEntry longLinkEntry = new TarArchiveEntry("././@LongLink", linkType);
            longLinkEntry.setSize((long)len + 1L);
            this.transferModTime(entry, longLinkEntry);
            this.putArchiveEntry(longLinkEntry);
            this.write(encodedName.array(), encodedName.arrayOffset(), len);
            this.write(0);
            this.closeArchiveEntry();
         } else if (this.longFileMode != 1) {
            throw new IllegalArgumentException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
         }
      }

      return false;
   }

   private void transferModTime(TarArchiveEntry from, TarArchiveEntry to) {
      Date fromModTime = from.getModTime();
      long fromModTimeSeconds = fromModTime.getTime() / 1000L;
      if (fromModTimeSeconds < 0L || fromModTimeSeconds > 8589934591L) {
         fromModTime = new Date(0L);
      }

      to.setModTime(fromModTime);
   }
}
