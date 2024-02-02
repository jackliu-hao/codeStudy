package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

public abstract class ZipUtil {
   private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448L);

   public static ZipLong toDosTime(Date time) {
      return new ZipLong(toDosTime(time.getTime()));
   }

   public static byte[] toDosTime(long t) {
      byte[] result = new byte[4];
      toDosTime(t, result, 0);
      return result;
   }

   public static void toDosTime(long t, byte[] buf, int offset) {
      toDosTime(Calendar.getInstance(), t, buf, offset);
   }

   static void toDosTime(Calendar c, long t, byte[] buf, int offset) {
      c.setTimeInMillis(t);
      int year = c.get(1);
      if (year < 1980) {
         copy(DOS_TIME_MIN, buf, offset);
      } else {
         int month = c.get(2) + 1;
         long value = (long)(year - 1980 << 25 | month << 21 | c.get(5) << 16 | c.get(11) << 11 | c.get(12) << 5 | c.get(13) >> 1);
         ZipLong.putLong(value, buf, offset);
      }
   }

   public static long adjustToLong(int i) {
      return i < 0 ? 4294967296L + (long)i : (long)i;
   }

   public static byte[] reverse(byte[] array) {
      int z = array.length - 1;

      for(int i = 0; i < array.length / 2; ++i) {
         byte x = array[i];
         array[i] = array[z - i];
         array[z - i] = x;
      }

      return array;
   }

   static long bigToLong(BigInteger big) {
      if (big.bitLength() <= 63) {
         return big.longValue();
      } else {
         throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
      }
   }

   static BigInteger longToBig(long l) {
      if (l < -2147483648L) {
         throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
      } else {
         if (l < 0L && l >= -2147483648L) {
            l = adjustToLong((int)l);
         }

         return BigInteger.valueOf(l);
      }
   }

   public static int signedByteToUnsignedInt(byte b) {
      return b >= 0 ? b : 256 + b;
   }

   public static byte unsignedIntToSignedByte(int i) {
      if (i <= 255 && i >= 0) {
         return i < 128 ? (byte)i : (byte)(i - 256);
      } else {
         throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
      }
   }

   public static Date fromDosTime(ZipLong zipDosTime) {
      long dosTime = zipDosTime.getValue();
      return new Date(dosToJavaTime(dosTime));
   }

   public static long dosToJavaTime(long dosTime) {
      Calendar cal = Calendar.getInstance();
      cal.set(1, (int)(dosTime >> 25 & 127L) + 1980);
      cal.set(2, (int)(dosTime >> 21 & 15L) - 1);
      cal.set(5, (int)(dosTime >> 16) & 31);
      cal.set(11, (int)(dosTime >> 11) & 31);
      cal.set(12, (int)(dosTime >> 5) & 63);
      cal.set(13, (int)(dosTime << 1) & 62);
      cal.set(14, 0);
      return cal.getTime().getTime();
   }

   static void setNameAndCommentFromExtraFields(ZipArchiveEntry ze, byte[] originalNameBytes, byte[] commentBytes) {
      ZipExtraField nameCandidate = ze.getExtraField(UnicodePathExtraField.UPATH_ID);
      UnicodePathExtraField name = nameCandidate instanceof UnicodePathExtraField ? (UnicodePathExtraField)nameCandidate : null;
      String newName = getUnicodeStringIfOriginalMatches(name, originalNameBytes);
      if (newName != null) {
         ze.setName(newName);
         ze.setNameSource(ZipArchiveEntry.NameSource.UNICODE_EXTRA_FIELD);
      }

      if (commentBytes != null && commentBytes.length > 0) {
         ZipExtraField cmtCandidate = ze.getExtraField(UnicodeCommentExtraField.UCOM_ID);
         UnicodeCommentExtraField cmt = cmtCandidate instanceof UnicodeCommentExtraField ? (UnicodeCommentExtraField)cmtCandidate : null;
         String newComment = getUnicodeStringIfOriginalMatches(cmt, commentBytes);
         if (newComment != null) {
            ze.setComment(newComment);
            ze.setCommentSource(ZipArchiveEntry.CommentSource.UNICODE_EXTRA_FIELD);
         }
      }

   }

   private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f, byte[] orig) {
      if (f != null) {
         CRC32 crc32 = new CRC32();
         crc32.update(orig);
         long origCRC32 = crc32.getValue();
         if (origCRC32 == f.getNameCRC32()) {
            try {
               return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
            } catch (IOException var6) {
            }
         }
      }

      return null;
   }

   static byte[] copy(byte[] from) {
      return from != null ? Arrays.copyOf(from, from.length) : null;
   }

   static void copy(byte[] from, byte[] to, int offset) {
      if (from != null) {
         System.arraycopy(from, 0, to, offset, from.length);
      }

   }

   static boolean canHandleEntryData(ZipArchiveEntry entry) {
      return supportsEncryptionOf(entry) && supportsMethodOf(entry);
   }

   private static boolean supportsEncryptionOf(ZipArchiveEntry entry) {
      return !entry.getGeneralPurposeBit().usesEncryption();
   }

   private static boolean supportsMethodOf(ZipArchiveEntry entry) {
      return entry.getMethod() == 0 || entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || entry.getMethod() == ZipMethod.IMPLODING.getCode() || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || entry.getMethod() == ZipMethod.BZIP2.getCode();
   }

   static void checkRequestedFeatures(ZipArchiveEntry ze) throws UnsupportedZipFeatureException {
      if (!supportsEncryptionOf(ze)) {
         throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze);
      } else if (!supportsMethodOf(ze)) {
         ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
         if (m == null) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze);
         } else {
            throw new UnsupportedZipFeatureException(m, ze);
         }
      }
   }
}
