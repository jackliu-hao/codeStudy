package org.apache.commons.compress.archivers.cpio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;

public class CpioArchiveInputStream extends ArchiveInputStream implements CpioConstants {
   private boolean closed;
   private CpioArchiveEntry entry;
   private long entryBytesRead;
   private boolean entryEOF;
   private final byte[] tmpbuf;
   private long crc;
   private final InputStream in;
   private final byte[] twoBytesBuf;
   private final byte[] fourBytesBuf;
   private final byte[] sixBytesBuf;
   private final int blockSize;
   private final ZipEncoding zipEncoding;
   final String encoding;

   public CpioArchiveInputStream(InputStream in) {
      this(in, 512, "US-ASCII");
   }

   public CpioArchiveInputStream(InputStream in, String encoding) {
      this(in, 512, encoding);
   }

   public CpioArchiveInputStream(InputStream in, int blockSize) {
      this(in, blockSize, "US-ASCII");
   }

   public CpioArchiveInputStream(InputStream in, int blockSize, String encoding) {
      this.tmpbuf = new byte[4096];
      this.twoBytesBuf = new byte[2];
      this.fourBytesBuf = new byte[4];
      this.sixBytesBuf = new byte[6];
      this.in = in;
      if (blockSize <= 0) {
         throw new IllegalArgumentException("blockSize must be bigger than 0");
      } else {
         this.blockSize = blockSize;
         this.encoding = encoding;
         this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      }
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.entryEOF ? 0 : 1;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.in.close();
         this.closed = true;
      }

   }

   private void closeEntry() throws IOException {
      while(this.skip(2147483647L) == 2147483647L) {
      }

   }

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public CpioArchiveEntry getNextCPIOEntry() throws IOException {
      this.ensureOpen();
      if (this.entry != null) {
         this.closeEntry();
      }

      this.readFully(this.twoBytesBuf, 0, this.twoBytesBuf.length);
      if (CpioUtil.byteArray2long(this.twoBytesBuf, false) == 29127L) {
         this.entry = this.readOldBinaryEntry(false);
      } else if (CpioUtil.byteArray2long(this.twoBytesBuf, true) == 29127L) {
         this.entry = this.readOldBinaryEntry(true);
      } else {
         System.arraycopy(this.twoBytesBuf, 0, this.sixBytesBuf, 0, this.twoBytesBuf.length);
         this.readFully(this.sixBytesBuf, this.twoBytesBuf.length, this.fourBytesBuf.length);
         switch (ArchiveUtils.toAsciiString(this.sixBytesBuf)) {
            case "070701":
               this.entry = this.readNewEntry(false);
               break;
            case "070702":
               this.entry = this.readNewEntry(true);
               break;
            case "070707":
               this.entry = this.readOldAsciiEntry();
               break;
            default:
               throw new IOException("Unknown magic [" + magicString + "]. Occurred at byte: " + this.getBytesRead());
         }
      }

      this.entryBytesRead = 0L;
      this.entryEOF = false;
      this.crc = 0L;
      if (this.entry.getName().equals("TRAILER!!!")) {
         this.entryEOF = true;
         this.skipRemainderOfLastBlock();
         return null;
      } else {
         return this.entry;
      }
   }

   private void skip(int bytes) throws IOException {
      if (bytes > 0) {
         this.readFully(this.fourBytesBuf, 0, bytes);
      }

   }

   public int read(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if (off >= 0 && len >= 0 && off <= b.length - len) {
         if (len == 0) {
            return 0;
         } else if (this.entry != null && !this.entryEOF) {
            if (this.entryBytesRead == this.entry.getSize()) {
               this.skip(this.entry.getDataPadCount());
               this.entryEOF = true;
               if (this.entry.getFormat() == 2 && this.crc != this.entry.getChksum()) {
                  throw new IOException("CRC Error. Occurred at byte: " + this.getBytesRead());
               } else {
                  return -1;
               }
            } else {
               int tmplength = (int)Math.min((long)len, this.entry.getSize() - this.entryBytesRead);
               if (tmplength < 0) {
                  return -1;
               } else {
                  int tmpread = this.readFully(b, off, tmplength);
                  if (this.entry.getFormat() == 2) {
                     for(int pos = 0; pos < tmpread; ++pos) {
                        this.crc += (long)(b[pos] & 255);
                        this.crc &= 4294967295L;
                     }
                  }

                  if (tmpread > 0) {
                     this.entryBytesRead += (long)tmpread;
                  }

                  return tmpread;
               }
            }
         } else {
            return -1;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private final int readFully(byte[] b, int off, int len) throws IOException {
      int count = IOUtils.readFully(this.in, b, off, len);
      this.count(count);
      if (count < len) {
         throw new EOFException();
      } else {
         return count;
      }
   }

   private final byte[] readRange(int len) throws IOException {
      byte[] b = IOUtils.readRange(this.in, len);
      this.count(b.length);
      if (b.length < len) {
         throw new EOFException();
      } else {
         return b;
      }
   }

   private long readBinaryLong(int length, boolean swapHalfWord) throws IOException {
      byte[] tmp = this.readRange(length);
      return CpioUtil.byteArray2long(tmp, swapHalfWord);
   }

   private long readAsciiLong(int length, int radix) throws IOException {
      byte[] tmpBuffer = this.readRange(length);
      return Long.parseLong(ArchiveUtils.toAsciiString(tmpBuffer), radix);
   }

   private CpioArchiveEntry readNewEntry(boolean hasCrc) throws IOException {
      CpioArchiveEntry ret;
      if (hasCrc) {
         ret = new CpioArchiveEntry((short)2);
      } else {
         ret = new CpioArchiveEntry((short)1);
      }

      ret.setInode(this.readAsciiLong(8, 16));
      long mode = this.readAsciiLong(8, 16);
      if (CpioUtil.fileType(mode) != 0L) {
         ret.setMode(mode);
      }

      ret.setUID(this.readAsciiLong(8, 16));
      ret.setGID(this.readAsciiLong(8, 16));
      ret.setNumberOfLinks(this.readAsciiLong(8, 16));
      ret.setTime(this.readAsciiLong(8, 16));
      ret.setSize(this.readAsciiLong(8, 16));
      if (ret.getSize() < 0L) {
         throw new IOException("Found illegal entry with negative length");
      } else {
         ret.setDeviceMaj(this.readAsciiLong(8, 16));
         ret.setDeviceMin(this.readAsciiLong(8, 16));
         ret.setRemoteDeviceMaj(this.readAsciiLong(8, 16));
         ret.setRemoteDeviceMin(this.readAsciiLong(8, 16));
         long namesize = this.readAsciiLong(8, 16);
         if (namesize < 0L) {
            throw new IOException("Found illegal entry with negative name length");
         } else {
            ret.setChksum(this.readAsciiLong(8, 16));
            String name = this.readCString((int)namesize);
            ret.setName(name);
            if (CpioUtil.fileType(mode) == 0L && !name.equals("TRAILER!!!")) {
               throw new IOException("Mode 0 only allowed in the trailer. Found entry name: " + ArchiveUtils.sanitize(name) + " Occurred at byte: " + this.getBytesRead());
            } else {
               this.skip(ret.getHeaderPadCount(namesize - 1L));
               return ret;
            }
         }
      }
   }

   private CpioArchiveEntry readOldAsciiEntry() throws IOException {
      CpioArchiveEntry ret = new CpioArchiveEntry((short)4);
      ret.setDevice(this.readAsciiLong(6, 8));
      ret.setInode(this.readAsciiLong(6, 8));
      long mode = this.readAsciiLong(6, 8);
      if (CpioUtil.fileType(mode) != 0L) {
         ret.setMode(mode);
      }

      ret.setUID(this.readAsciiLong(6, 8));
      ret.setGID(this.readAsciiLong(6, 8));
      ret.setNumberOfLinks(this.readAsciiLong(6, 8));
      ret.setRemoteDevice(this.readAsciiLong(6, 8));
      ret.setTime(this.readAsciiLong(11, 8));
      long namesize = this.readAsciiLong(6, 8);
      if (namesize < 0L) {
         throw new IOException("Found illegal entry with negative name length");
      } else {
         ret.setSize(this.readAsciiLong(11, 8));
         if (ret.getSize() < 0L) {
            throw new IOException("Found illegal entry with negative length");
         } else {
            String name = this.readCString((int)namesize);
            ret.setName(name);
            if (CpioUtil.fileType(mode) == 0L && !name.equals("TRAILER!!!")) {
               throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + ArchiveUtils.sanitize(name) + " Occurred at byte: " + this.getBytesRead());
            } else {
               return ret;
            }
         }
      }
   }

   private CpioArchiveEntry readOldBinaryEntry(boolean swapHalfWord) throws IOException {
      CpioArchiveEntry ret = new CpioArchiveEntry((short)8);
      ret.setDevice(this.readBinaryLong(2, swapHalfWord));
      ret.setInode(this.readBinaryLong(2, swapHalfWord));
      long mode = this.readBinaryLong(2, swapHalfWord);
      if (CpioUtil.fileType(mode) != 0L) {
         ret.setMode(mode);
      }

      ret.setUID(this.readBinaryLong(2, swapHalfWord));
      ret.setGID(this.readBinaryLong(2, swapHalfWord));
      ret.setNumberOfLinks(this.readBinaryLong(2, swapHalfWord));
      ret.setRemoteDevice(this.readBinaryLong(2, swapHalfWord));
      ret.setTime(this.readBinaryLong(4, swapHalfWord));
      long namesize = this.readBinaryLong(2, swapHalfWord);
      if (namesize < 0L) {
         throw new IOException("Found illegal entry with negative name length");
      } else {
         ret.setSize(this.readBinaryLong(4, swapHalfWord));
         if (ret.getSize() < 0L) {
            throw new IOException("Found illegal entry with negative length");
         } else {
            String name = this.readCString((int)namesize);
            ret.setName(name);
            if (CpioUtil.fileType(mode) == 0L && !name.equals("TRAILER!!!")) {
               throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + ArchiveUtils.sanitize(name) + "Occurred at byte: " + this.getBytesRead());
            } else {
               this.skip(ret.getHeaderPadCount(namesize - 1L));
               return ret;
            }
         }
      }
   }

   private String readCString(int length) throws IOException {
      byte[] tmpBuffer = this.readRange(length - 1);
      if (this.in.read() == -1) {
         throw new EOFException();
      } else {
         return this.zipEncoding.decode(tmpBuffer);
      }
   }

   public long skip(long n) throws IOException {
      if (n < 0L) {
         throw new IllegalArgumentException("Negative skip length");
      } else {
         this.ensureOpen();
         int max = (int)Math.min(n, 2147483647L);

         int total;
         int len;
         for(total = 0; total < max; total += len) {
            len = max - total;
            if (len > this.tmpbuf.length) {
               len = this.tmpbuf.length;
            }

            len = this.read(this.tmpbuf, 0, len);
            if (len == -1) {
               this.entryEOF = true;
               break;
            }
         }

         return (long)total;
      }
   }

   public ArchiveEntry getNextEntry() throws IOException {
      return this.getNextCPIOEntry();
   }

   private void skipRemainderOfLastBlock() throws IOException {
      long readFromLastBlock = this.getBytesRead() % (long)this.blockSize;

      long skipped;
      for(long remainingBytes = readFromLastBlock == 0L ? 0L : (long)this.blockSize - readFromLastBlock; remainingBytes > 0L; remainingBytes -= skipped) {
         skipped = this.skip((long)this.blockSize - readFromLastBlock);
         if (skipped <= 0L) {
            break;
         }
      }

   }

   public static boolean matches(byte[] signature, int length) {
      if (length < 6) {
         return false;
      } else if (signature[0] == 113 && (signature[1] & 255) == 199) {
         return true;
      } else if (signature[1] == 113 && (signature[0] & 255) == 199) {
         return true;
      } else if (signature[0] != 48) {
         return false;
      } else if (signature[1] != 55) {
         return false;
      } else if (signature[2] != 48) {
         return false;
      } else if (signature[3] != 55) {
         return false;
      } else if (signature[4] != 48) {
         return false;
      } else if (signature[5] == 49) {
         return true;
      } else if (signature[5] == 50) {
         return true;
      } else {
         return signature[5] == 55;
      }
   }
}
