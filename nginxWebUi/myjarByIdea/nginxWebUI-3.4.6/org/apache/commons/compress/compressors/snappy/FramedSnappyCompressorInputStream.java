package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class FramedSnappyCompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
   static final long MASK_OFFSET = 2726488792L;
   private static final int STREAM_IDENTIFIER_TYPE = 255;
   static final int COMPRESSED_CHUNK_TYPE = 0;
   private static final int UNCOMPRESSED_CHUNK_TYPE = 1;
   private static final int PADDING_CHUNK_TYPE = 254;
   private static final int MIN_UNSKIPPABLE_TYPE = 2;
   private static final int MAX_UNSKIPPABLE_TYPE = 127;
   private static final int MAX_SKIPPABLE_TYPE = 253;
   static final byte[] SZ_SIGNATURE = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
   private long unreadBytes;
   private final CountingInputStream countingStream;
   private final PushbackInputStream inputStream;
   private final FramedSnappyDialect dialect;
   private SnappyCompressorInputStream currentCompressedChunk;
   private final byte[] oneByte;
   private boolean endReached;
   private boolean inUncompressedChunk;
   private int uncompressedBytesRemaining;
   private long expectedChecksum;
   private final int blockSize;
   private final PureJavaCrc32C checksum;
   private final ByteUtils.ByteSupplier supplier;

   public FramedSnappyCompressorInputStream(InputStream in) throws IOException {
      this(in, FramedSnappyDialect.STANDARD);
   }

   public FramedSnappyCompressorInputStream(InputStream in, FramedSnappyDialect dialect) throws IOException {
      this(in, 32768, dialect);
   }

   public FramedSnappyCompressorInputStream(InputStream in, int blockSize, FramedSnappyDialect dialect) throws IOException {
      this.oneByte = new byte[1];
      this.expectedChecksum = -1L;
      this.checksum = new PureJavaCrc32C();
      this.supplier = this::readOneByte;
      if (blockSize <= 0) {
         throw new IllegalArgumentException("blockSize must be bigger than 0");
      } else {
         this.countingStream = new CountingInputStream(in);
         this.inputStream = new PushbackInputStream(this.countingStream, 1);
         this.blockSize = blockSize;
         this.dialect = dialect;
         if (dialect.hasStreamIdentifier()) {
            this.readStreamIdentifier();
         }

      }
   }

   public int read() throws IOException {
      return this.read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 255;
   }

   public void close() throws IOException {
      try {
         if (this.currentCompressedChunk != null) {
            this.currentCompressedChunk.close();
            this.currentCompressedChunk = null;
         }
      } finally {
         this.inputStream.close();
      }

   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         int read = this.readOnce(b, off, len);
         if (read == -1) {
            this.readNextBlock();
            if (this.endReached) {
               return -1;
            }

            read = this.readOnce(b, off, len);
         }

         return read;
      }
   }

   public int available() throws IOException {
      if (this.inUncompressedChunk) {
         return Math.min(this.uncompressedBytesRemaining, this.inputStream.available());
      } else {
         return this.currentCompressedChunk != null ? this.currentCompressedChunk.available() : 0;
      }
   }

   public long getCompressedCount() {
      return this.countingStream.getBytesRead() - this.unreadBytes;
   }

   private int readOnce(byte[] b, int off, int len) throws IOException {
      int read = -1;
      if (this.inUncompressedChunk) {
         int amount = Math.min(this.uncompressedBytesRemaining, len);
         if (amount == 0) {
            return -1;
         }

         read = this.inputStream.read(b, off, amount);
         if (read != -1) {
            this.uncompressedBytesRemaining -= read;
            this.count(read);
         }
      } else if (this.currentCompressedChunk != null) {
         long before = this.currentCompressedChunk.getBytesRead();
         read = this.currentCompressedChunk.read(b, off, len);
         if (read == -1) {
            this.currentCompressedChunk.close();
            this.currentCompressedChunk = null;
         } else {
            this.count(this.currentCompressedChunk.getBytesRead() - before);
         }
      }

      if (read > 0) {
         this.checksum.update(b, off, read);
      }

      return read;
   }

   private void readNextBlock() throws IOException {
      this.verifyLastChecksumAndReset();
      this.inUncompressedChunk = false;
      int type = this.readOneByte();
      if (type == -1) {
         this.endReached = true;
      } else if (type == 255) {
         this.inputStream.unread(type);
         ++this.unreadBytes;
         this.pushedBackBytes(1L);
         this.readStreamIdentifier();
         this.readNextBlock();
      } else if (type != 254 && (type <= 127 || type > 253)) {
         if (type >= 2 && type <= 127) {
            throw new IOException("Unskippable chunk with type " + type + " (hex " + Integer.toHexString(type) + ") detected.");
         }

         if (type == 1) {
            this.inUncompressedChunk = true;
            this.uncompressedBytesRemaining = this.readSize() - 4;
            if (this.uncompressedBytesRemaining < 0) {
               throw new IOException("Found illegal chunk with negative size");
            }

            this.expectedChecksum = unmask(this.readCrc());
         } else {
            if (type != 0) {
               throw new IOException("Unknown chunk type " + type + " detected.");
            }

            boolean expectChecksum = this.dialect.usesChecksumWithCompressedChunks();
            long size = (long)this.readSize() - (expectChecksum ? 4L : 0L);
            if (size < 0L) {
               throw new IOException("Found illegal chunk with negative size");
            }

            if (expectChecksum) {
               this.expectedChecksum = unmask(this.readCrc());
            } else {
               this.expectedChecksum = -1L;
            }

            this.currentCompressedChunk = new SnappyCompressorInputStream(new BoundedInputStream(this.inputStream, size), this.blockSize);
            this.count(this.currentCompressedChunk.getBytesRead());
         }
      } else {
         this.skipBlock();
         this.readNextBlock();
      }

   }

   private long readCrc() throws IOException {
      byte[] b = new byte[4];
      int read = IOUtils.readFully((InputStream)this.inputStream, (byte[])b);
      this.count(read);
      if (read != 4) {
         throw new IOException("Premature end of stream");
      } else {
         return ByteUtils.fromLittleEndian(b);
      }
   }

   static long unmask(long x) {
      x -= 2726488792L;
      x &= 4294967295L;
      return (x >> 17 | x << 15) & 4294967295L;
   }

   private int readSize() throws IOException {
      return (int)ByteUtils.fromLittleEndian((ByteUtils.ByteSupplier)this.supplier, 3);
   }

   private void skipBlock() throws IOException {
      int size = this.readSize();
      if (size < 0) {
         throw new IOException("Found illegal chunk with negative size");
      } else {
         long read = IOUtils.skip(this.inputStream, (long)size);
         this.count(read);
         if (read != (long)size) {
            throw new IOException("Premature end of stream");
         }
      }
   }

   private void readStreamIdentifier() throws IOException {
      byte[] b = new byte[10];
      int read = IOUtils.readFully((InputStream)this.inputStream, (byte[])b);
      this.count(read);
      if (10 != read || !matches(b, 10)) {
         throw new IOException("Not a framed Snappy stream");
      }
   }

   private int readOneByte() throws IOException {
      int b = this.inputStream.read();
      if (b != -1) {
         this.count(1);
         return b & 255;
      } else {
         return -1;
      }
   }

   private void verifyLastChecksumAndReset() throws IOException {
      if (this.expectedChecksum >= 0L && this.expectedChecksum != this.checksum.getValue()) {
         throw new IOException("Checksum verification failed");
      } else {
         this.expectedChecksum = -1L;
         this.checksum.reset();
      }
   }

   public static boolean matches(byte[] signature, int length) {
      if (length < SZ_SIGNATURE.length) {
         return false;
      } else {
         byte[] shortenedSig = signature;
         if (signature.length > SZ_SIGNATURE.length) {
            shortenedSig = new byte[SZ_SIGNATURE.length];
            System.arraycopy(signature, 0, shortenedSig, 0, SZ_SIGNATURE.length);
         }

         return Arrays.equals(shortenedSig, SZ_SIGNATURE);
      }
   }
}
