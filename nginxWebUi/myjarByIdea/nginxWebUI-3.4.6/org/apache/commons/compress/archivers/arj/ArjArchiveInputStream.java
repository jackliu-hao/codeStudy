package org.apache.commons.compress.archivers.arj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ArjArchiveInputStream extends ArchiveInputStream {
   private static final int ARJ_MAGIC_1 = 96;
   private static final int ARJ_MAGIC_2 = 234;
   private final DataInputStream in;
   private final String charsetName;
   private final MainHeader mainHeader;
   private LocalFileHeader currentLocalFileHeader;
   private InputStream currentInputStream;

   public ArjArchiveInputStream(InputStream inputStream, String charsetName) throws ArchiveException {
      this.in = new DataInputStream(inputStream);
      this.charsetName = charsetName;

      try {
         this.mainHeader = this.readMainHeader();
         if ((this.mainHeader.arjFlags & 1) != 0) {
            throw new ArchiveException("Encrypted ARJ files are unsupported");
         } else if ((this.mainHeader.arjFlags & 4) != 0) {
            throw new ArchiveException("Multi-volume ARJ files are unsupported");
         }
      } catch (IOException var4) {
         throw new ArchiveException(var4.getMessage(), var4);
      }
   }

   public ArjArchiveInputStream(InputStream inputStream) throws ArchiveException {
      this(inputStream, "CP437");
   }

   public void close() throws IOException {
      this.in.close();
   }

   private int read8(DataInputStream dataIn) throws IOException {
      int value = dataIn.readUnsignedByte();
      this.count(1);
      return value;
   }

   private int read16(DataInputStream dataIn) throws IOException {
      int value = dataIn.readUnsignedShort();
      this.count(2);
      return Integer.reverseBytes(value) >>> 16;
   }

   private int read32(DataInputStream dataIn) throws IOException {
      int value = dataIn.readInt();
      this.count(4);
      return Integer.reverseBytes(value);
   }

   private String readString(DataInputStream dataIn) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      Throwable var3 = null;

      String var5;
      try {
         int nextByte;
         while((nextByte = dataIn.readUnsignedByte()) != 0) {
            buffer.write(nextByte);
         }

         if (this.charsetName == null) {
            var5 = buffer.toString();
            return var5;
         }

         var5 = buffer.toString(this.charsetName);
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (buffer != null) {
            if (var3 != null) {
               try {
                  buffer.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               buffer.close();
            }
         }

      }

      return var5;
   }

   private byte[] readRange(InputStream in, int len) throws IOException {
      byte[] b = IOUtils.readRange(in, len);
      this.count(b.length);
      if (b.length < len) {
         throw new EOFException();
      } else {
         return b;
      }
   }

   private byte[] readHeader() throws IOException {
      boolean found = false;
      byte[] basicHeaderBytes = null;

      do {
         int first = false;
         int second = this.read8(this.in);

         int first;
         do {
            first = second;
            second = this.read8(this.in);
         } while(first != 96 && second != 234);

         int basicHeaderSize = this.read16(this.in);
         if (basicHeaderSize == 0) {
            return null;
         }

         if (basicHeaderSize <= 2600) {
            basicHeaderBytes = this.readRange(this.in, basicHeaderSize);
            long basicHeaderCrc32 = (long)this.read32(this.in) & 4294967295L;
            CRC32 crc32 = new CRC32();
            crc32.update(basicHeaderBytes);
            if (basicHeaderCrc32 == crc32.getValue()) {
               found = true;
            }
         }
      } while(!found);

      return basicHeaderBytes;
   }

   private MainHeader readMainHeader() throws IOException {
      byte[] basicHeaderBytes = this.readHeader();
      if (basicHeaderBytes == null) {
         throw new IOException("Archive ends without any headers");
      } else {
         DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
         int firstHeaderSize = basicHeader.readUnsignedByte();
         byte[] firstHeaderBytes = this.readRange(basicHeader, firstHeaderSize - 1);
         this.pushedBackBytes((long)firstHeaderBytes.length);
         DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
         MainHeader hdr = new MainHeader();
         hdr.archiverVersionNumber = firstHeader.readUnsignedByte();
         hdr.minVersionToExtract = firstHeader.readUnsignedByte();
         hdr.hostOS = firstHeader.readUnsignedByte();
         hdr.arjFlags = firstHeader.readUnsignedByte();
         hdr.securityVersion = firstHeader.readUnsignedByte();
         hdr.fileType = firstHeader.readUnsignedByte();
         hdr.reserved = firstHeader.readUnsignedByte();
         hdr.dateTimeCreated = this.read32(firstHeader);
         hdr.dateTimeModified = this.read32(firstHeader);
         hdr.archiveSize = 4294967295L & (long)this.read32(firstHeader);
         hdr.securityEnvelopeFilePosition = this.read32(firstHeader);
         hdr.fileSpecPosition = this.read16(firstHeader);
         hdr.securityEnvelopeLength = this.read16(firstHeader);
         this.pushedBackBytes(20L);
         hdr.encryptionVersion = firstHeader.readUnsignedByte();
         hdr.lastChapter = firstHeader.readUnsignedByte();
         if (firstHeaderSize >= 33) {
            hdr.arjProtectionFactor = firstHeader.readUnsignedByte();
            hdr.arjFlags2 = firstHeader.readUnsignedByte();
            firstHeader.readUnsignedByte();
            firstHeader.readUnsignedByte();
         }

         hdr.name = this.readString(basicHeader);
         hdr.comment = this.readString(basicHeader);
         int extendedHeaderSize = this.read16(this.in);
         if (extendedHeaderSize > 0) {
            hdr.extendedHeaderBytes = this.readRange(this.in, extendedHeaderSize);
            long extendedHeaderCrc32 = 4294967295L & (long)this.read32(this.in);
            CRC32 crc32 = new CRC32();
            crc32.update(hdr.extendedHeaderBytes);
            if (extendedHeaderCrc32 != crc32.getValue()) {
               throw new IOException("Extended header CRC32 verification failure");
            }
         }

         return hdr;
      }
   }

   private LocalFileHeader readLocalFileHeader() throws IOException {
      byte[] basicHeaderBytes = this.readHeader();
      if (basicHeaderBytes == null) {
         return null;
      } else {
         DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
         Throwable var3 = null;

         Object extendedHeaderBytes;
         try {
            int firstHeaderSize = basicHeader.readUnsignedByte();
            byte[] firstHeaderBytes = this.readRange(basicHeader, firstHeaderSize - 1);
            this.pushedBackBytes((long)firstHeaderBytes.length);
            DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
            Throwable var7 = null;

            try {
               LocalFileHeader localFileHeader = new LocalFileHeader();
               localFileHeader.archiverVersionNumber = firstHeader.readUnsignedByte();
               localFileHeader.minVersionToExtract = firstHeader.readUnsignedByte();
               localFileHeader.hostOS = firstHeader.readUnsignedByte();
               localFileHeader.arjFlags = firstHeader.readUnsignedByte();
               localFileHeader.method = firstHeader.readUnsignedByte();
               localFileHeader.fileType = firstHeader.readUnsignedByte();
               localFileHeader.reserved = firstHeader.readUnsignedByte();
               localFileHeader.dateTimeModified = this.read32(firstHeader);
               localFileHeader.compressedSize = 4294967295L & (long)this.read32(firstHeader);
               localFileHeader.originalSize = 4294967295L & (long)this.read32(firstHeader);
               localFileHeader.originalCrc32 = 4294967295L & (long)this.read32(firstHeader);
               localFileHeader.fileSpecPosition = this.read16(firstHeader);
               localFileHeader.fileAccessMode = this.read16(firstHeader);
               this.pushedBackBytes(20L);
               localFileHeader.firstChapter = firstHeader.readUnsignedByte();
               localFileHeader.lastChapter = firstHeader.readUnsignedByte();
               this.readExtraData(firstHeaderSize, firstHeader, localFileHeader);
               localFileHeader.name = this.readString(basicHeader);
               localFileHeader.comment = this.readString(basicHeader);
               ArrayList<byte[]> extendedHeaders = new ArrayList();

               int extendedHeaderSize;
               while((extendedHeaderSize = this.read16(this.in)) > 0) {
                  extendedHeaderBytes = this.readRange(this.in, extendedHeaderSize);
                  long extendedHeaderCrc32 = 4294967295L & (long)this.read32(this.in);
                  CRC32 crc32 = new CRC32();
                  crc32.update((byte[])extendedHeaderBytes);
                  if (extendedHeaderCrc32 != crc32.getValue()) {
                     throw new IOException("Extended header CRC32 verification failure");
                  }

                  extendedHeaders.add(extendedHeaderBytes);
               }

               localFileHeader.extendedHeaders = (byte[][])extendedHeaders.toArray(new byte[0][]);
               extendedHeaderBytes = localFileHeader;
            } catch (Throwable var36) {
               var7 = var36;
               throw var36;
            } finally {
               if (firstHeader != null) {
                  if (var7 != null) {
                     try {
                        firstHeader.close();
                     } catch (Throwable var35) {
                        var7.addSuppressed(var35);
                     }
                  } else {
                     firstHeader.close();
                  }
               }

            }
         } catch (Throwable var38) {
            var3 = var38;
            throw var38;
         } finally {
            if (basicHeader != null) {
               if (var3 != null) {
                  try {
                     basicHeader.close();
                  } catch (Throwable var34) {
                     var3.addSuppressed(var34);
                  }
               } else {
                  basicHeader.close();
               }
            }

         }

         return (LocalFileHeader)extendedHeaderBytes;
      }
   }

   private void readExtraData(int firstHeaderSize, DataInputStream firstHeader, LocalFileHeader localFileHeader) throws IOException {
      if (firstHeaderSize >= 33) {
         localFileHeader.extendedFilePosition = this.read32(firstHeader);
         if (firstHeaderSize >= 45) {
            localFileHeader.dateTimeAccessed = this.read32(firstHeader);
            localFileHeader.dateTimeCreated = this.read32(firstHeader);
            localFileHeader.originalSizeEvenForVolumes = this.read32(firstHeader);
            this.pushedBackBytes(12L);
         }

         this.pushedBackBytes(4L);
      }

   }

   public static boolean matches(byte[] signature, int length) {
      return length >= 2 && (255 & signature[0]) == 96 && (255 & signature[1]) == 234;
   }

   public String getArchiveName() {
      return this.mainHeader.name;
   }

   public String getArchiveComment() {
      return this.mainHeader.comment;
   }

   public ArjArchiveEntry getNextEntry() throws IOException {
      if (this.currentInputStream != null) {
         IOUtils.skip(this.currentInputStream, Long.MAX_VALUE);
         this.currentInputStream.close();
         this.currentLocalFileHeader = null;
         this.currentInputStream = null;
      }

      this.currentLocalFileHeader = this.readLocalFileHeader();
      if (this.currentLocalFileHeader != null) {
         this.currentInputStream = new BoundedInputStream(this.in, this.currentLocalFileHeader.compressedSize);
         if (this.currentLocalFileHeader.method == 0) {
            this.currentInputStream = new CRC32VerifyingInputStream(this.currentInputStream, this.currentLocalFileHeader.originalSize, this.currentLocalFileHeader.originalCrc32);
         }

         return new ArjArchiveEntry(this.currentLocalFileHeader);
      } else {
         this.currentInputStream = null;
         return null;
      }
   }

   public boolean canReadEntryData(ArchiveEntry ae) {
      return ae instanceof ArjArchiveEntry && ((ArjArchiveEntry)ae).getMethod() == 0;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else if (this.currentLocalFileHeader == null) {
         throw new IllegalStateException("No current arj entry");
      } else if (this.currentLocalFileHeader.method != 0) {
         throw new IOException("Unsupported compression method " + this.currentLocalFileHeader.method);
      } else {
         return this.currentInputStream.read(b, off, len);
      }
   }
}
