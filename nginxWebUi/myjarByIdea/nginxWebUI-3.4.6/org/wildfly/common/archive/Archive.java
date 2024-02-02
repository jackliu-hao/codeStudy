package org.wildfly.common.archive;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.wildfly.common.Assert;

public final class Archive implements Closeable {
   public static final int GP_ENCRYPTED = 1;
   public static final int GP_IMPLODE_8K_DICTIONARY = 2;
   public static final int GP_IMPLODE_3_TREES = 4;
   public static final int GP_DEFLATE_COMP_OPT_MASK = 6;
   public static final int GP_DEFLATE_COMP_OPT_NORMAL = 0;
   public static final int GP_DEFLATE_COMP_OPT_MAXIMUM = 2;
   public static final int GP_DEFLATE_COMP_OPT_FAST = 4;
   public static final int GP_DEFLATE_COMP_OPT_SUPER_FAST = 6;
   public static final int GP_LZMA_EOS_USED = 2;
   public static final int GP_LATE_SIZES = 8;
   public static final int GP_COMPRESSED_PATCHED = 32;
   public static final int GP_STRONG_ENCRYPTION = 64;
   public static final int GP_UTF_8 = 2048;
   public static final int GP_CD_MASKED = 8192;
   public static final int METHOD_STORED = 0;
   public static final int METHOD_SHRINK = 1;
   public static final int METHOD_REDUCE_1 = 2;
   public static final int METHOD_REDUCE_2 = 3;
   public static final int METHOD_REDUCE_3 = 4;
   public static final int METHOD_REDUCE_4 = 5;
   public static final int METHOD_IMPLODE = 6;
   public static final int METHOD_DEFLATE = 8;
   public static final int METHOD_DEFLATE64 = 9;
   public static final int METHOD_BZIP2 = 12;
   public static final int METHOD_LZMA = 14;
   public static final int MADE_BY_MS_DOS = 0;
   public static final int MADE_BY_UNIX = 3;
   public static final int MADE_BY_NTFS = 10;
   public static final int MADE_BY_OS_X = 19;
   public static final int SIG_LH = 67324752;
   public static final int LH_SIGNATURE = 0;
   public static final int LH_MIN_VERSION = 4;
   public static final int LH_GP_BITS = 6;
   public static final int LH_COMP_METHOD = 8;
   public static final int LH_MOD_TIME = 10;
   public static final int LH_MOD_DATE = 12;
   public static final int LH_CRC_32 = 14;
   public static final int LH_COMPRESSED_SIZE = 18;
   public static final int LH_UNCOMPRESSED_SIZE = 22;
   public static final int LH_FILE_NAME_LENGTH = 26;
   public static final int LH_EXTRA_LENGTH = 28;
   public static final int LH_END = 30;
   public static final int SIG_DD = 134695760;
   public static final int DD_SIGNATURE = 0;
   public static final int DD_CRC_32 = 4;
   public static final int DD_COMPRESSED_SIZE = 8;
   public static final int DD_UNCOMPRESSED_SIZE = 12;
   public static final int DD_END = 16;
   public static final int DD_ZIP64_COMPRESSED_SIZE = 8;
   public static final int DD_ZIP64_UNCOMPRESSED_SIZE = 16;
   public static final int DD_ZIP64_END = 24;
   public static final int SIG_CDE = 33639248;
   public static final int CDE_SIGNATURE = 0;
   public static final int CDE_VERSION_MADE_BY = 4;
   public static final int CDE_VERSION_NEEDED = 6;
   public static final int CDE_GP_BITS = 8;
   public static final int CDE_COMP_METHOD = 10;
   public static final int CDE_MOD_TIME = 12;
   public static final int CDE_MOD_DATE = 14;
   public static final int CDE_CRC_32 = 16;
   public static final int CDE_COMPRESSED_SIZE = 20;
   public static final int CDE_UNCOMPRESSED_SIZE = 24;
   public static final int CDE_FILE_NAME_LENGTH = 28;
   public static final int CDE_EXTRA_LENGTH = 30;
   public static final int CDE_COMMENT_LENGTH = 32;
   public static final int CDE_FIRST_DISK_NUMBER = 34;
   public static final int CDE_INTERNAL_ATTRIBUTES = 36;
   public static final int CDE_EXTERNAL_ATTRIBUTES = 38;
   public static final int CDE_LOCAL_HEADER_OFFSET = 42;
   public static final int CDE_END = 46;
   public static final int SIG_EOCD = 101010256;
   public static final int EOCD_SIGNATURE = 0;
   public static final int EOCD_DISK_NUMBER = 4;
   public static final int EOCD_CD_FIRST_DISK_NUMBER = 6;
   public static final int EOCD_CDE_COUNT_THIS_DISK = 8;
   public static final int EOCD_CDE_COUNT_ALL = 10;
   public static final int EOCD_CD_SIZE = 12;
   public static final int EOCD_CD_START_OFFSET = 16;
   public static final int EOCD_COMMENT_LENGTH = 20;
   public static final int EOCD_END = 22;
   public static final int EXT_ID_ZIP64 = 1;
   public static final int ZIP64_UNCOMPRESSED_SIZE = 0;
   public static final int ZIP64_COMPRESSED_SIZE = 8;
   public static final int ZIP64_LOCAL_HEADER_OFFSET = 16;
   public static final int ZIP64_FIRST_DISK_NUMBER = 24;
   public static final int ZIP64_END = 28;
   public static final int EXT_ID_UNIX = 13;
   public static final int UNIX_ACCESS_TIME = 0;
   public static final int UNIX_MODIFIED_TIME = 4;
   public static final int UNIX_UID = 8;
   public static final int UNIX_GID = 10;
   public static final int UNIX_END = 12;
   public static final int UNIX_DEV_MAJOR = 12;
   public static final int UNIX_DEV_MINOR = 16;
   public static final int UNIX_DEV_END = 20;
   public static final int SIG_EOCD_ZIP64 = 101075792;
   public static final int EOCD_ZIP64_SIGNATURE = 0;
   public static final int EOCD_ZIP64_SIZE = 4;
   public static final int EOCD_ZIP64_VERSION_MADE_BY = 12;
   public static final int EOCD_ZIP64_VERSION_NEEDED = 14;
   public static final int EOCD_ZIP64_DISK_NUMBER = 16;
   public static final int EOCD_ZIP64_CD_FIRST_DISK_NUMBER = 20;
   public static final int EOCD_ZIP64_CDE_COUNT_THIS_DISK = 24;
   public static final int EOCD_ZIP64_CDE_COUNT_ALL = 28;
   public static final int EOCD_ZIP64_CD_SIZE = 36;
   public static final int EOCD_ZIP64_CD_START_OFFSET = 44;
   public static final int EOCD_ZIP64_END = 52;
   public static final int SIG_EOCDL_ZIP64 = 117853008;
   public static final int EOCDL_ZIP64_SIGNATURE = 0;
   public static final int EOCDL_ZIP64_EOCD_DISK_NUMBER = 4;
   public static final int EOCDL_ZIP64_EOCD_OFFSET = 8;
   public static final int EOCDL_ZIP64_DISK_COUNT = 16;
   public static final int EOCDL_ZIP64_END = 20;
   private static final int BUF_SIZE_MAX = 1073741824;
   private static final int BUF_SHIFT = Integer.numberOfTrailingZeros(1073741824);
   private static final int BUF_SIZE_MASK = 1073741823;
   private final ByteBuffer[] bufs;
   private final long offset;
   private final long length;
   private final long cd;
   private final Index index;
   private static final ByteBuffer EMPTY_BUF = ByteBuffer.allocateDirect(0);

   private Archive(ByteBuffer[] bufs, long offset, long length, long cd, Index index) {
      this.bufs = bufs;
      this.offset = offset;
      this.length = length;
      this.cd = cd;
      this.index = index;
   }

   public static Archive open(Path path) throws IOException {
      FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);

      Archive var9;
      try {
         long size = fc.size();
         int bufCnt = Math.toIntExact(size + 1073741823L >> BUF_SHIFT);
         ByteBuffer[] array = new ByteBuffer[bufCnt];
         long offs = 0L;
         int idx = 0;

         while(true) {
            if (size <= 1073741823L) {
               array[idx] = fc.map(MapMode.READ_ONLY, offs, size).order(ByteOrder.LITTLE_ENDIAN);
               var9 = open(array);
               break;
            }

            array[idx++] = fc.map(MapMode.READ_ONLY, offs, 1073741824L).order(ByteOrder.LITTLE_ENDIAN);
            size -= 1073741824L;
            offs += 1073741824L;
         }
      } catch (Throwable var11) {
         if (fc != null) {
            try {
               fc.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (fc != null) {
         fc.close();
      }

      return var9;
   }

   public static Archive open(ByteBuffer buf) throws IOException {
      Assert.checkNotNullParam("buf", buf);
      if (buf.order() == ByteOrder.BIG_ENDIAN) {
         buf = buf.duplicate().order(ByteOrder.LITTLE_ENDIAN);
      }

      return open(new ByteBuffer[]{buf});
   }

   static Archive open(ByteBuffer[] bufs) throws IOException {
      return open(bufs, 0L, capacity(bufs));
   }

   static Archive open(ByteBuffer[] bufs, long offset, long length) throws IOException {
      long eocd;
      for(eocd = length - 22L; getUnsignedInt(bufs, offset + eocd) != 101010256L; --eocd) {
         if (eocd == 0L) {
            throw new IOException("Invalid archive");
         }
      }

      int entries = getUnsignedShort(bufs, offset + eocd + 10L);
      if (getUnsignedShort(bufs, offset + eocd + 6L) == 0 && getUnsignedShort(bufs, offset + eocd + 4L) == 0 && entries == getUnsignedShort(bufs, offset + eocd + 8L)) {
         long eocdLocZip64 = eocd - 20L;
         long eocdZip64 = -1L;
         if (getInt(bufs, offset + eocdLocZip64 + 0L) == 117853008) {
            if (getInt(bufs, offset + eocdLocZip64 + 16L) != 1 || getInt(bufs, offset + eocdLocZip64 + 4L) != 0) {
               throw new IOException("Multi-disk archives are not supported");
            }

            eocdZip64 = getLong(bufs, offset + eocdLocZip64 + 8L);
            if (getUnsignedInt(bufs, offset + eocdZip64 + 0L) != 101075792L) {
               eocdZip64 = -1L;
            }
         }

         long cd = getUnsignedInt(bufs, offset + eocd + 16L);
         if (cd == 4294967295L && eocdZip64 != -1L) {
            cd = getLong(bufs, offset + eocdZip64 + 44L);
         }

         if (entries == 65535 && eocdZip64 != -1L) {
            long cnt = getUnsignedInt(bufs, offset + eocdZip64 + 28L);
            if (cnt > 134217727L) {
               throw new IOException("Archive has too many entries");
            }

            entries = (int)cnt;
         }

         Object index;
         if (length <= 65534L) {
            index = new TinyIndex(entries);
         } else if (length <= 68719476734L) {
            index = new LargeIndex(entries);
         } else {
            index = new HugeIndex(entries);
         }

         int mask = ((Index)index).getMask();
         long cde = cd;

         for(int i = 0; i < entries; ++i) {
            if (getInt(bufs, offset + cde + 0L) != 33639248) {
               throw new IOException("Archive appears to be corrupted");
            }

            int hc = getHashCodeOfEntry(bufs, offset + cde);
            ((Index)index).put(hc & mask, cde);
            cde = cde + 46L + (long)getUnsignedShort(bufs, offset + cde + 28L) + (long)getUnsignedShort(bufs, offset + cde + 30L) + (long)getUnsignedShort(bufs, offset + cde + 32L);
         }

         return new Archive(bufs, offset, length, cd, (Index)index);
      } else {
         throw new IOException("Multi-disk archives are not supported");
      }
   }

   private static String getNameOfEntry(ByteBuffer[] bufs, long cde) {
      long name = cde + 46L;
      int nameLen = getUnsignedShort(bufs, cde + 28L);
      boolean utf8 = (getUnsignedShort(bufs, cde + 8L) & 2048) != 0;
      if (utf8) {
         return new String(getBytes(bufs, name, nameLen), StandardCharsets.UTF_8);
      } else {
         char[] nameChars = new char[nameLen];

         for(int i = 0; i < nameLen; ++i) {
            nameChars[i] = Archive.Cp437.charFor(getUnsignedByte(bufs, name + (long)i));
         }

         return new String(nameChars);
      }
   }

   private static int getHashCodeOfEntry(ByteBuffer[] bufs, long cde) {
      long name = cde + 46L;
      int nameLen = getUnsignedShort(bufs, cde + 28L);
      boolean utf8 = (getUnsignedShort(bufs, cde + 8L) & 2048) != 0;
      int hc = 0;
      int cp;
      if (utf8) {
         for(int i = 0; i < nameLen; i += Archive.Utf8.getByteCount(getUnsignedByte(bufs, name + (long)i))) {
            cp = Archive.Utf8.codePointAt(bufs, name + (long)i);
            if (Character.isSupplementaryCodePoint(cp)) {
               hc = hc * 31 + Character.highSurrogate(cp);
               hc = hc * 31 + Character.lowSurrogate(cp);
            } else {
               hc = hc * 31 + cp;
            }
         }
      } else {
         for(cp = 0; cp < nameLen; ++cp) {
            hc = hc * 31 + Archive.Cp437.charFor(getUnsignedByte(bufs, name + (long)cp));
         }
      }

      return hc;
   }

   public long getFirstEntryHandle() {
      return this.cd;
   }

   public long getNextEntryHandle(long entryHandle) {
      long next = entryHandle + 46L + (long)getUnsignedShort(this.bufs, this.offset + entryHandle + 28L) + (long)getUnsignedShort(this.bufs, this.offset + entryHandle + 30L);
      return next < this.length && getInt(this.bufs, this.offset + next + 0L) == 33639248 ? next : -1L;
   }

   public long getEntryHandle(String fileName) {
      int mask = this.index.getMask();
      int base = fileName.hashCode();

      for(int i = 0; i < mask; ++i) {
         long entryHandle = this.index.get(base + i & mask);
         if (entryHandle == -1L) {
            return -1L;
         }

         if (this.entryNameEquals(entryHandle, fileName)) {
            return entryHandle;
         }
      }

      return -1L;
   }

   public boolean entryNameEquals(long entryHandle, String fileName) {
      long name = entryHandle + 46L;
      int nameLen = getUnsignedShort(this.bufs, this.offset + entryHandle + 28L);
      boolean utf8 = (getUnsignedShort(this.bufs, this.offset + entryHandle + 8L) & 2048) != 0;
      int length = fileName.length();
      if (utf8) {
         long i = 0L;

         int j;
         for(j = 0; i < (long)nameLen && j < length; j = fileName.offsetByCodePoints(j, 1)) {
            if (Archive.Utf8.codePointAt(this.bufs, this.offset + name + i) != fileName.codePointAt(j)) {
               return false;
            }

            i += (long)Archive.Utf8.getByteCount(getUnsignedByte(this.bufs, this.offset + name + i));
         }

         return i == (long)nameLen && j == length;
      } else {
         int i = 0;

         int j;
         for(j = 0; i < nameLen && j < length; j = fileName.offsetByCodePoints(j, 1)) {
            if (Archive.Cp437.charFor(getUnsignedByte(this.bufs, this.offset + (long)i + entryHandle + 46L)) != fileName.codePointAt(j)) {
               return false;
            }

            ++i;
         }

         return i == nameLen && j == length;
      }
   }

   private long getLocalHeader(long entryHandle) {
      long lh = getUnsignedInt(this.bufs, this.offset + entryHandle + 42L);
      if (lh == 4294967295L) {
         long zip64 = this.getExtraRecord(entryHandle, 1);
         if (zip64 != -1L) {
            lh = getLong(this.bufs, this.offset + zip64 + 16L);
         }
      }

      return lh;
   }

   public String getEntryName(long entryHandle) {
      return getNameOfEntry(this.bufs, entryHandle);
   }

   public ByteBuffer getEntryContents(long entryHandle) throws IOException {
      long size = this.getUncompressedSize(entryHandle);
      long compSize = this.getCompressedSize(entryHandle);
      if (size <= 268435456L && compSize <= 268435456L) {
         long localHeader = this.getLocalHeader(entryHandle);
         if ((getUnsignedShort(this.bufs, this.offset + localHeader + 6L) & 65) != 0) {
            throw new IOException("Cannot read encrypted entries");
         } else {
            long offset = this.getDataOffset(localHeader);
            int method = this.getCompressionMethod(entryHandle);
            switch (method) {
               case 0:
                  return bufferOf(this.bufs, this.offset + offset, (int)size);
               case 8:
                  Inflater inflater = new Inflater(true);

                  ByteBuffer var13;
                  try {
                     var13 = JDKSpecific.inflate(inflater, this.bufs, this.offset + offset, (int)compSize, (int)size);
                  } catch (DataFormatException var17) {
                     throw new IOException(var17);
                  } finally {
                     inflater.end();
                  }

                  return var13;
               default:
                  throw new IOException("Unsupported compression scheme");
            }
         }
      } else {
         throw new IOException("Entry is too large to read into RAM");
      }
   }

   private long getDataOffset(long localHeader) {
      return localHeader + 30L + (long)getUnsignedShort(this.bufs, this.offset + localHeader + 26L) + (long)getUnsignedShort(this.bufs, this.offset + localHeader + 28L);
   }

   public InputStream getEntryStream(long entryHandle) throws IOException {
      long size = this.getCompressedSize(entryHandle);
      long localHeader = this.getLocalHeader(entryHandle);
      if ((getUnsignedShort(this.bufs, this.offset + localHeader + 6L) & 65) != 0) {
         throw new IOException("Cannot read encrypted entries");
      } else {
         long offset = this.getDataOffset(localHeader);
         int method = this.getCompressionMethod(entryHandle);
         switch (method) {
            case 0:
               return new ByteBufferInputStream(this.bufs, this.offset + offset, size);
            case 8:
               return new InflaterInputStream(new ByteBufferInputStream(this.bufs, this.offset + offset, size));
            default:
               throw new IOException("Unsupported compression scheme");
         }
      }
   }

   public Archive getNestedArchive(long entryHandle) throws IOException {
      long localHeader = this.getLocalHeader(entryHandle);
      if ((getUnsignedShort(this.bufs, this.offset + localHeader + 6L) & 65) != 0) {
         throw new IOException("Cannot read encrypted entries");
      } else {
         long offset = this.getDataOffset(localHeader);
         int method = this.getCompressionMethod(entryHandle);
         if (method != 0) {
            throw new IOException("Cannot open compressed nested archive");
         } else {
            long size = this.getUncompressedSize(entryHandle);
            if (size < 2147483647L) {
               ByteBuffer slice = sliceOf(this.bufs, this.offset + offset, (int)size);
               if (slice != null) {
                  return open(slice);
               }
            }

            return open(this.bufs, this.offset + offset, size);
         }
      }
   }

   public boolean isCompressed(long entryHandle) {
      return this.getCompressionMethod(entryHandle) != 0;
   }

   private int getCompressionMethod(long entryHandle) {
      return getUnsignedShort(this.bufs, this.offset + entryHandle + 10L);
   }

   private long getExtraRecord(long entryHandle, int headerId) {
      long extra = entryHandle + 46L + (long)getUnsignedShort(this.bufs, this.offset + entryHandle + 28L) + (long)getUnsignedShort(this.bufs, this.offset + entryHandle + 32L);
      int extraLen = getUnsignedShort(this.bufs, this.offset + entryHandle + 30L);

      for(int i = 0; i < extraLen; i += getUnsignedShort(this.bufs, this.offset + extra + (long)i + 2L)) {
         if (getUnsignedShort(this.bufs, this.offset + extra + (long)i) == headerId) {
            return extra + (long)i + 4L;
         }
      }

      return -1L;
   }

   public long getUncompressedSize(long entryHandle) {
      long size = getUnsignedInt(this.bufs, this.offset + entryHandle + 24L);
      if (size == -1L) {
         long zip64 = this.getExtraRecord(entryHandle, 1);
         if (zip64 != -1L) {
            size = getLong(this.bufs, this.offset + zip64 + 0L);
         }
      }

      return size;
   }

   public long getCompressedSize(long entryHandle) {
      long size = getUnsignedInt(this.bufs, this.offset + entryHandle + 20L);
      if (size == -1L) {
         long zip64 = this.getExtraRecord(entryHandle, 1);
         if (zip64 != -1L) {
            size = getLong(this.bufs, this.offset + zip64 + 8L);
         }
      }

      return size;
   }

   public long getModifiedTime(long entryHandle) {
      long unix = this.getExtraRecord(entryHandle, 13);
      if (unix != -1L) {
         long unixTime = getUnsignedInt(this.bufs, this.offset + unix + 4L);
         if (unixTime != 0L) {
            return unixTime * 1000L;
         }
      }

      return dosTimeStamp(getUnsignedShort(this.bufs, this.offset + entryHandle + 12L), getUnsignedShort(this.bufs, this.offset + entryHandle + 14L));
   }

   public void close() {
   }

   private static long dosTimeStamp(int modTime, int modDate) {
      int year = 1980 + (modDate >> 9);
      int month = 1 + (modDate >> 5 & 15);
      int day = modDate & 31;
      int hour = modTime >> 11;
      int minute = modTime >> 5 & 63;
      int second = (modTime & 31) << 1;
      return LocalDateTime.of(year, month, day, hour, minute, second).toInstant(ZoneOffset.UTC).toEpochMilli();
   }

   public boolean isDirectory(long entryHandle) {
      int madeBy = getUnsignedShort(this.bufs, this.offset + entryHandle + 4L);
      int extAttr = getInt(this.bufs, entryHandle + 38L);
      switch (madeBy) {
         case 3:
            return (extAttr & '\uf000') == 16384;
         default:
            return (extAttr & 16) != 0;
      }
   }

   static int bufIdx(long idx) {
      return (int)(idx >>> BUF_SHIFT);
   }

   static int bufOffs(long idx) {
      return (int)idx & 1073741823;
   }

   static byte getByte(ByteBuffer[] bufs, long idx) {
      return bufs[bufIdx(idx)].get(bufOffs(idx));
   }

   static int getUnsignedByte(ByteBuffer[] bufs, long idx) {
      return getByte(bufs, idx) & 255;
   }

   static int getUnsignedByte(ByteBuffer buf, int idx) {
      return buf.get(idx) & 255;
   }

   static short getShort(ByteBuffer[] bufs, long idx) {
      int bi = bufIdx(idx);
      return bi == bufIdx(idx + 1L) ? bufs[bi].getShort(bufOffs(idx)) : (short)(getUnsignedByte(bufs, idx) | getByte(bufs, idx + 1L) << 8);
   }

   static int getUnsignedShort(ByteBuffer[] bufs, long idx) {
      return getShort(bufs, idx) & '\uffff';
   }

   static int getMedium(ByteBuffer[] bufs, long idx) {
      return getUnsignedByte(bufs, idx) | getUnsignedShort(bufs, idx + 1L) << 8;
   }

   static long getUnsignedMedium(ByteBuffer[] bufs, long idx) {
      return (long)(getUnsignedByte(bufs, idx) | getUnsignedShort(bufs, idx + 1L) << 8);
   }

   static int getInt(ByteBuffer[] bufs, long idx) {
      int bi = bufIdx(idx);
      return bi == bufIdx(idx + 3L) ? bufs[bi].getInt(bufOffs(idx)) : getUnsignedShort(bufs, idx) | getShort(bufs, idx + 2L) << 16;
   }

   static long getUnsignedInt(ByteBuffer[] bufs, long idx) {
      return (long)getInt(bufs, idx) & 4294967295L;
   }

   static long getLong(ByteBuffer[] bufs, long idx) {
      int bi = bufIdx(idx);
      return bi == bufIdx(idx + 7L) ? bufs[bi].getLong(bufOffs(idx)) : getUnsignedInt(bufs, idx) | (long)getInt(bufs, idx + 4L) << 32;
   }

   static void readBytes(ByteBuffer[] bufs, long idx, byte[] dest, int off, int len) {
      while(len > 0) {
         int bi = bufIdx(idx);
         int bo = bufOffs(idx);
         ByteBuffer buf = bufs[bi].duplicate();
         buf.position(bo);
         int cnt = Math.min(len, buf.remaining());
         buf.get(dest, 0, cnt);
         len -= cnt;
         off += cnt;
         idx += (long)cnt;
      }

   }

   static byte[] getBytes(ByteBuffer[] bufs, long idx, int len) {
      byte[] bytes = new byte[len];
      readBytes(bufs, idx, bytes, 0, len);
      return bytes;
   }

   static ByteBuffer sliceOf(ByteBuffer[] bufs, long idx, int len) {
      if (len == 0) {
         return EMPTY_BUF;
      } else {
         int biStart = bufIdx(idx);
         int biEnd = bufIdx(idx + (long)len - 1L);
         if (biStart == biEnd) {
            ByteBuffer buf = bufs[biStart].duplicate();
            buf.position(bufOffs(idx));
            buf.limit(buf.position() + len);
            return buf.slice();
         } else {
            return null;
         }
      }
   }

   static ByteBuffer bufferOf(ByteBuffer[] bufs, long idx, int len) {
      ByteBuffer buf = sliceOf(bufs, idx, len);
      if (buf == null) {
         buf = ByteBuffer.wrap(getBytes(bufs, idx, len));
      }

      return buf;
   }

   static long capacity(ByteBuffer[] bufs) {
      int lastIdx = bufs.length - 1;
      return (long)lastIdx * 1073741824L + (long)bufs[lastIdx].capacity();
   }

   static final class Utf8 {
      private Utf8() {
      }

      static int getByteCount(int a) {
         if (a <= 127) {
            return 1;
         } else if (a <= 191) {
            return 1;
         } else if (a <= 223) {
            return 2;
         } else if (a <= 239) {
            return 3;
         } else {
            return a <= 247 ? 4 : 1;
         }
      }

      static int codePointAt(ByteBuffer[] bufs, long i) {
         int a = Archive.getUnsignedByte(bufs, i);
         if (a <= 127) {
            return a;
         } else if (a <= 191) {
            return 65533;
         } else {
            int b = Archive.getUnsignedByte(bufs, i + 1L);
            if ((b & 192) != 128) {
               return 65533;
            } else if (a <= 223) {
               return (a & 31) << 6 | b & 63;
            } else {
               int c = Archive.getUnsignedByte(bufs, i + 2L);
               if ((c & 192) != 128) {
                  return 65533;
               } else if (a <= 239) {
                  return (a & 15) << 12 | (b & 63) << 6 | c & 63;
               } else {
                  int d = Archive.getUnsignedByte(bufs, i + 3L);
                  if ((d & 192) != 128) {
                     return 65533;
                  } else {
                     return a <= 247 ? (a & 7) << 18 | (b & 63) << 12 | (c & 63) << 6 | d & 63 : '�';
                  }
               }
            }
         }
      }
   }

   static final class Cp437 {
      static final char[] codePoints = new char[]{'\u0000', '☺', '☻', '♥', '♦', '♣', '♠', '•', '◘', '○', '◙', '♂', '♀', '♪', '♫', '☼', '►', '◄', '↕', '‼', '¶', '§', '▬', '↨', '↑', '↓', '→', '←', '∟', '↔', '▲', '▼', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '⌂', 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', '¢', '£', '¥', '₧', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', '¬', '½', '¼', '¡', '«', '»', '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐', '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧', '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀', 'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩', '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', ' '};

      private Cp437() {
      }

      static char charFor(int c) {
         return codePoints[c];
      }
   }
}
