/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.zip.CRC32;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ZipUtil
/*     */ {
/*  36 */   private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ZipLong toDosTime(Date time) {
/*  44 */     return new ZipLong(toDosTime(time.getTime()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toDosTime(long t) {
/*  55 */     byte[] result = new byte[4];
/*  56 */     toDosTime(t, result, 0);
/*  57 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void toDosTime(long t, byte[] buf, int offset) {
/*  71 */     toDosTime(Calendar.getInstance(), t, buf, offset);
/*     */   }
/*     */   
/*     */   static void toDosTime(Calendar c, long t, byte[] buf, int offset) {
/*  75 */     c.setTimeInMillis(t);
/*     */     
/*  77 */     int year = c.get(1);
/*  78 */     if (year < 1980) {
/*  79 */       copy(DOS_TIME_MIN, buf, offset);
/*     */       return;
/*     */     } 
/*  82 */     int month = c.get(2) + 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  88 */     long value = (year - 1980 << 25 | month << 21 | c.get(5) << 16 | c.get(11) << 11 | c.get(12) << 5 | c.get(13) >> 1);
/*  89 */     ZipLong.putLong(value, buf, offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long adjustToLong(int i) {
/* 101 */     if (i < 0) {
/* 102 */       return 4294967296L + i;
/*     */     }
/* 104 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] reverse(byte[] array) {
/* 119 */     int z = array.length - 1;
/* 120 */     for (int i = 0; i < array.length / 2; i++) {
/* 121 */       byte x = array[i];
/* 122 */       array[i] = array[z - i];
/* 123 */       array[z - i] = x;
/*     */     } 
/* 125 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long bigToLong(BigInteger big) {
/* 136 */     if (big.bitLength() <= 63) {
/* 137 */       return big.longValue();
/*     */     }
/* 139 */     throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static BigInteger longToBig(long l) {
/* 154 */     if (l < -2147483648L) {
/* 155 */       throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
/*     */     }
/* 157 */     if (l < 0L && l >= -2147483648L)
/*     */     {
/*     */       
/* 160 */       l = adjustToLong((int)l);
/*     */     }
/* 162 */     return BigInteger.valueOf(l);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int signedByteToUnsignedInt(byte b) {
/* 174 */     if (b >= 0) {
/* 175 */       return b;
/*     */     }
/* 177 */     return 256 + b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte unsignedIntToSignedByte(int i) {
/* 189 */     if (i > 255 || i < 0) {
/* 190 */       throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
/*     */     }
/* 192 */     if (i < 128) {
/* 193 */       return (byte)i;
/*     */     }
/* 195 */     return (byte)(i - 256);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date fromDosTime(ZipLong zipDosTime) {
/* 205 */     long dosTime = zipDosTime.getValue();
/* 206 */     return new Date(dosToJavaTime(dosTime));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long dosToJavaTime(long dosTime) {
/* 216 */     Calendar cal = Calendar.getInstance();
/*     */     
/* 218 */     cal.set(1, (int)(dosTime >> 25L & 0x7FL) + 1980);
/* 219 */     cal.set(2, (int)(dosTime >> 21L & 0xFL) - 1);
/* 220 */     cal.set(5, (int)(dosTime >> 16L) & 0x1F);
/* 221 */     cal.set(11, (int)(dosTime >> 11L) & 0x1F);
/* 222 */     cal.set(12, (int)(dosTime >> 5L) & 0x3F);
/* 223 */     cal.set(13, (int)(dosTime << 1L) & 0x3E);
/* 224 */     cal.set(14, 0);
/*     */     
/* 226 */     return cal.getTime().getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void setNameAndCommentFromExtraFields(ZipArchiveEntry ze, byte[] originalNameBytes, byte[] commentBytes) {
/* 237 */     ZipExtraField nameCandidate = ze.getExtraField(UnicodePathExtraField.UPATH_ID);
/* 238 */     UnicodePathExtraField name = (nameCandidate instanceof UnicodePathExtraField) ? (UnicodePathExtraField)nameCandidate : null;
/*     */     
/* 240 */     String newName = getUnicodeStringIfOriginalMatches(name, originalNameBytes);
/*     */     
/* 242 */     if (newName != null) {
/* 243 */       ze.setName(newName);
/* 244 */       ze.setNameSource(ZipArchiveEntry.NameSource.UNICODE_EXTRA_FIELD);
/*     */     } 
/*     */     
/* 247 */     if (commentBytes != null && commentBytes.length > 0) {
/* 248 */       ZipExtraField cmtCandidate = ze.getExtraField(UnicodeCommentExtraField.UCOM_ID);
/* 249 */       UnicodeCommentExtraField cmt = (cmtCandidate instanceof UnicodeCommentExtraField) ? (UnicodeCommentExtraField)cmtCandidate : null;
/*     */ 
/*     */       
/* 252 */       String newComment = getUnicodeStringIfOriginalMatches(cmt, commentBytes);
/* 253 */       if (newComment != null) {
/* 254 */         ze.setComment(newComment);
/* 255 */         ze.setCommentSource(ZipArchiveEntry.CommentSource.UNICODE_EXTRA_FIELD);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f, byte[] orig) {
/* 270 */     if (f != null) {
/* 271 */       CRC32 crc32 = new CRC32();
/* 272 */       crc32.update(orig);
/* 273 */       long origCRC32 = crc32.getValue();
/*     */       
/* 275 */       if (origCRC32 == f.getNameCRC32()) {
/*     */         try {
/* 277 */           return ZipEncodingHelper.UTF8_ZIP_ENCODING
/* 278 */             .decode(f.getUnicodeName());
/* 279 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 286 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] copy(byte[] from) {
/* 294 */     if (from != null) {
/* 295 */       return Arrays.copyOf(from, from.length);
/*     */     }
/* 297 */     return null;
/*     */   }
/*     */   
/*     */   static void copy(byte[] from, byte[] to, int offset) {
/* 301 */     if (from != null) {
/* 302 */       System.arraycopy(from, 0, to, offset, from.length);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean canHandleEntryData(ZipArchiveEntry entry) {
/* 311 */     return (supportsEncryptionOf(entry) && supportsMethodOf(entry));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean supportsEncryptionOf(ZipArchiveEntry entry) {
/* 321 */     return !entry.getGeneralPurposeBit().usesEncryption();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean supportsMethodOf(ZipArchiveEntry entry) {
/* 331 */     return (entry.getMethod() == 0 || entry
/* 332 */       .getMethod() == ZipMethod.UNSHRINKING.getCode() || entry
/* 333 */       .getMethod() == ZipMethod.IMPLODING.getCode() || entry
/* 334 */       .getMethod() == 8 || entry
/* 335 */       .getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || entry
/* 336 */       .getMethod() == ZipMethod.BZIP2.getCode());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void checkRequestedFeatures(ZipArchiveEntry ze) throws UnsupportedZipFeatureException {
/* 345 */     if (!supportsEncryptionOf(ze)) {
/* 346 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze);
/*     */     }
/*     */ 
/*     */     
/* 350 */     if (!supportsMethodOf(ze)) {
/* 351 */       ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
/* 352 */       if (m == null) {
/* 353 */         throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze);
/*     */       }
/*     */ 
/*     */       
/* 357 */       throw new UnsupportedZipFeatureException(m, ze);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */