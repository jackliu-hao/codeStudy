/*     */ package org.wildfly.common.archive;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneOffset;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ import org.wildfly.common.Assert;
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
/*     */ public final class Archive
/*     */   implements Closeable
/*     */ {
/*     */   public static final int GP_ENCRYPTED = 1;
/*     */   public static final int GP_IMPLODE_8K_DICTIONARY = 2;
/*     */   public static final int GP_IMPLODE_3_TREES = 4;
/*     */   public static final int GP_DEFLATE_COMP_OPT_MASK = 6;
/*     */   public static final int GP_DEFLATE_COMP_OPT_NORMAL = 0;
/*     */   public static final int GP_DEFLATE_COMP_OPT_MAXIMUM = 2;
/*     */   public static final int GP_DEFLATE_COMP_OPT_FAST = 4;
/*     */   public static final int GP_DEFLATE_COMP_OPT_SUPER_FAST = 6;
/*     */   public static final int GP_LZMA_EOS_USED = 2;
/*     */   public static final int GP_LATE_SIZES = 8;
/*     */   public static final int GP_COMPRESSED_PATCHED = 32;
/*     */   public static final int GP_STRONG_ENCRYPTION = 64;
/*     */   public static final int GP_UTF_8 = 2048;
/*     */   public static final int GP_CD_MASKED = 8192;
/*     */   public static final int METHOD_STORED = 0;
/*     */   public static final int METHOD_SHRINK = 1;
/*     */   public static final int METHOD_REDUCE_1 = 2;
/*     */   public static final int METHOD_REDUCE_2 = 3;
/*     */   public static final int METHOD_REDUCE_3 = 4;
/*     */   public static final int METHOD_REDUCE_4 = 5;
/*     */   public static final int METHOD_IMPLODE = 6;
/*     */   public static final int METHOD_DEFLATE = 8;
/*     */   public static final int METHOD_DEFLATE64 = 9;
/*     */   public static final int METHOD_BZIP2 = 12;
/*     */   public static final int METHOD_LZMA = 14;
/*     */   public static final int MADE_BY_MS_DOS = 0;
/*     */   public static final int MADE_BY_UNIX = 3;
/*     */   public static final int MADE_BY_NTFS = 10;
/*     */   public static final int MADE_BY_OS_X = 19;
/*     */   public static final int SIG_LH = 67324752;
/*     */   public static final int LH_SIGNATURE = 0;
/*     */   public static final int LH_MIN_VERSION = 4;
/*     */   public static final int LH_GP_BITS = 6;
/*     */   public static final int LH_COMP_METHOD = 8;
/*     */   public static final int LH_MOD_TIME = 10;
/*     */   public static final int LH_MOD_DATE = 12;
/*     */   public static final int LH_CRC_32 = 14;
/*     */   public static final int LH_COMPRESSED_SIZE = 18;
/*     */   public static final int LH_UNCOMPRESSED_SIZE = 22;
/*     */   public static final int LH_FILE_NAME_LENGTH = 26;
/*     */   public static final int LH_EXTRA_LENGTH = 28;
/*     */   public static final int LH_END = 30;
/*     */   public static final int SIG_DD = 134695760;
/*     */   public static final int DD_SIGNATURE = 0;
/*     */   public static final int DD_CRC_32 = 4;
/*     */   public static final int DD_COMPRESSED_SIZE = 8;
/*     */   public static final int DD_UNCOMPRESSED_SIZE = 12;
/*     */   public static final int DD_END = 16;
/*     */   public static final int DD_ZIP64_COMPRESSED_SIZE = 8;
/*     */   public static final int DD_ZIP64_UNCOMPRESSED_SIZE = 16;
/*     */   public static final int DD_ZIP64_END = 24;
/*     */   public static final int SIG_CDE = 33639248;
/*     */   public static final int CDE_SIGNATURE = 0;
/*     */   public static final int CDE_VERSION_MADE_BY = 4;
/*     */   public static final int CDE_VERSION_NEEDED = 6;
/*     */   public static final int CDE_GP_BITS = 8;
/*     */   public static final int CDE_COMP_METHOD = 10;
/*     */   public static final int CDE_MOD_TIME = 12;
/*     */   public static final int CDE_MOD_DATE = 14;
/*     */   public static final int CDE_CRC_32 = 16;
/*     */   public static final int CDE_COMPRESSED_SIZE = 20;
/*     */   public static final int CDE_UNCOMPRESSED_SIZE = 24;
/*     */   public static final int CDE_FILE_NAME_LENGTH = 28;
/*     */   public static final int CDE_EXTRA_LENGTH = 30;
/*     */   public static final int CDE_COMMENT_LENGTH = 32;
/*     */   public static final int CDE_FIRST_DISK_NUMBER = 34;
/*     */   public static final int CDE_INTERNAL_ATTRIBUTES = 36;
/*     */   public static final int CDE_EXTERNAL_ATTRIBUTES = 38;
/*     */   public static final int CDE_LOCAL_HEADER_OFFSET = 42;
/*     */   public static final int CDE_END = 46;
/*     */   public static final int SIG_EOCD = 101010256;
/*     */   public static final int EOCD_SIGNATURE = 0;
/*     */   public static final int EOCD_DISK_NUMBER = 4;
/*     */   public static final int EOCD_CD_FIRST_DISK_NUMBER = 6;
/*     */   public static final int EOCD_CDE_COUNT_THIS_DISK = 8;
/*     */   public static final int EOCD_CDE_COUNT_ALL = 10;
/*     */   public static final int EOCD_CD_SIZE = 12;
/*     */   public static final int EOCD_CD_START_OFFSET = 16;
/*     */   public static final int EOCD_COMMENT_LENGTH = 20;
/*     */   public static final int EOCD_END = 22;
/*     */   public static final int EXT_ID_ZIP64 = 1;
/*     */   public static final int ZIP64_UNCOMPRESSED_SIZE = 0;
/*     */   public static final int ZIP64_COMPRESSED_SIZE = 8;
/*     */   public static final int ZIP64_LOCAL_HEADER_OFFSET = 16;
/*     */   public static final int ZIP64_FIRST_DISK_NUMBER = 24;
/*     */   public static final int ZIP64_END = 28;
/*     */   public static final int EXT_ID_UNIX = 13;
/*     */   public static final int UNIX_ACCESS_TIME = 0;
/*     */   public static final int UNIX_MODIFIED_TIME = 4;
/*     */   public static final int UNIX_UID = 8;
/*     */   public static final int UNIX_GID = 10;
/*     */   public static final int UNIX_END = 12;
/*     */   public static final int UNIX_DEV_MAJOR = 12;
/*     */   public static final int UNIX_DEV_MINOR = 16;
/*     */   public static final int UNIX_DEV_END = 20;
/*     */   public static final int SIG_EOCD_ZIP64 = 101075792;
/*     */   public static final int EOCD_ZIP64_SIGNATURE = 0;
/*     */   public static final int EOCD_ZIP64_SIZE = 4;
/*     */   public static final int EOCD_ZIP64_VERSION_MADE_BY = 12;
/*     */   public static final int EOCD_ZIP64_VERSION_NEEDED = 14;
/*     */   public static final int EOCD_ZIP64_DISK_NUMBER = 16;
/*     */   public static final int EOCD_ZIP64_CD_FIRST_DISK_NUMBER = 20;
/*     */   public static final int EOCD_ZIP64_CDE_COUNT_THIS_DISK = 24;
/*     */   public static final int EOCD_ZIP64_CDE_COUNT_ALL = 28;
/*     */   public static final int EOCD_ZIP64_CD_SIZE = 36;
/*     */   public static final int EOCD_ZIP64_CD_START_OFFSET = 44;
/*     */   public static final int EOCD_ZIP64_END = 52;
/*     */   public static final int SIG_EOCDL_ZIP64 = 117853008;
/*     */   public static final int EOCDL_ZIP64_SIGNATURE = 0;
/*     */   public static final int EOCDL_ZIP64_EOCD_DISK_NUMBER = 4;
/*     */   public static final int EOCDL_ZIP64_EOCD_OFFSET = 8;
/*     */   public static final int EOCDL_ZIP64_DISK_COUNT = 16;
/*     */   public static final int EOCDL_ZIP64_END = 20;
/*     */   private static final int BUF_SIZE_MAX = 1073741824;
/* 179 */   private static final int BUF_SHIFT = Integer.numberOfTrailingZeros(1073741824);
/*     */   
/*     */   private static final int BUF_SIZE_MASK = 1073741823;
/*     */   private final ByteBuffer[] bufs;
/*     */   private final long offset;
/*     */   private final long length;
/*     */   private final long cd;
/*     */   private final Index index;
/*     */   
/*     */   private Archive(ByteBuffer[] bufs, long offset, long length, long cd, Index index) {
/* 189 */     this.bufs = bufs;
/* 190 */     this.offset = offset;
/* 191 */     this.length = length;
/* 192 */     this.cd = cd;
/* 193 */     this.index = index;
/*     */   }
/*     */   
/*     */   public static Archive open(Path path) throws IOException {
/* 197 */     FileChannel fc = FileChannel.open(path, new OpenOption[] { StandardOpenOption.READ }); 
/* 198 */     try { long size = fc.size();
/* 199 */       int bufCnt = Math.toIntExact(size + 1073741823L >> BUF_SHIFT);
/* 200 */       ByteBuffer[] array = new ByteBuffer[bufCnt];
/* 201 */       long offs = 0L;
/* 202 */       int idx = 0;
/* 203 */       while (size > 1073741823L) {
/* 204 */         array[idx++] = fc.map(FileChannel.MapMode.READ_ONLY, offs, 1073741824L).order(ByteOrder.LITTLE_ENDIAN);
/* 205 */         size -= 1073741824L;
/* 206 */         offs += 1073741824L;
/*     */       } 
/* 208 */       array[idx] = fc.map(FileChannel.MapMode.READ_ONLY, offs, size).order(ByteOrder.LITTLE_ENDIAN);
/* 209 */       Archive archive = open(array);
/* 210 */       if (fc != null) fc.close();  return archive; } catch (Throwable throwable) { if (fc != null)
/*     */         try { fc.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/* 214 */      } public static Archive open(ByteBuffer buf) throws IOException { Assert.checkNotNullParam("buf", buf);
/* 215 */     if (buf.order() == ByteOrder.BIG_ENDIAN) {
/* 216 */       buf = buf.duplicate().order(ByteOrder.LITTLE_ENDIAN);
/*     */     }
/* 218 */     return open(new ByteBuffer[] { buf }); }
/*     */ 
/*     */   
/*     */   static Archive open(ByteBuffer[] bufs) throws IOException {
/* 222 */     return open(bufs, 0L, capacity(bufs));
/*     */   }
/*     */   
/*     */   static Archive open(ByteBuffer[] bufs, long offset, long length) throws IOException {
/*     */     Index index;
/* 227 */     long eocd = length - 22L;
/*     */     
/* 229 */     while (getUnsignedInt(bufs, offset + eocd) != 101010256L) {
/* 230 */       if (eocd == 0L) {
/* 231 */         throw new IOException("Invalid archive");
/*     */       }
/* 233 */       eocd--;
/*     */     } 
/* 235 */     int entries = getUnsignedShort(bufs, offset + eocd + 10L);
/*     */     
/* 237 */     if (getUnsignedShort(bufs, offset + eocd + 6L) != 0 || getUnsignedShort(bufs, offset + eocd + 4L) != 0 || entries != getUnsignedShort(bufs, offset + eocd + 8L)) {
/* 238 */       throw new IOException("Multi-disk archives are not supported");
/*     */     }
/*     */     
/* 241 */     long eocdLocZip64 = eocd - 20L;
/* 242 */     long eocdZip64 = -1L;
/* 243 */     if (getInt(bufs, offset + eocdLocZip64 + 0L) == 117853008) {
/*     */ 
/*     */       
/* 246 */       if (getInt(bufs, offset + eocdLocZip64 + 16L) != 1 || getInt(bufs, offset + eocdLocZip64 + 4L) != 0) {
/* 247 */         throw new IOException("Multi-disk archives are not supported");
/*     */       }
/* 249 */       eocdZip64 = getLong(bufs, offset + eocdLocZip64 + 8L);
/* 250 */       if (getUnsignedInt(bufs, offset + eocdZip64 + 0L) != 101075792L)
/*     */       {
/* 252 */         eocdZip64 = -1L;
/*     */       }
/*     */     } 
/*     */     
/* 256 */     long cd = getUnsignedInt(bufs, offset + eocd + 16L);
/* 257 */     if (cd == 4294967295L && eocdZip64 != -1L) {
/* 258 */       cd = getLong(bufs, offset + eocdZip64 + 44L);
/*     */     }
/* 260 */     if (entries == 65535 && eocdZip64 != -1L) {
/* 261 */       long cnt = getUnsignedInt(bufs, offset + eocdZip64 + 28L);
/* 262 */       if (cnt > 134217727L) {
/* 263 */         throw new IOException("Archive has too many entries");
/*     */       }
/* 265 */       entries = (int)cnt;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 271 */     if (length <= 65534L) {
/* 272 */       index = new TinyIndex(entries);
/* 273 */     } else if (length <= 68719476734L) {
/* 274 */       index = new LargeIndex(entries);
/*     */     } else {
/* 276 */       index = new HugeIndex(entries);
/*     */     } 
/*     */     
/* 279 */     int mask = index.getMask();
/* 280 */     long cde = cd;
/* 281 */     for (int i = 0; i < entries; i++) {
/* 282 */       if (getInt(bufs, offset + cde + 0L) != 33639248) {
/* 283 */         throw new IOException("Archive appears to be corrupted");
/*     */       }
/* 285 */       int hc = getHashCodeOfEntry(bufs, offset + cde);
/* 286 */       index.put(hc & mask, cde);
/* 287 */       cde = cde + 46L + getUnsignedShort(bufs, offset + cde + 28L) + getUnsignedShort(bufs, offset + cde + 30L) + getUnsignedShort(bufs, offset + cde + 32L);
/*     */     } 
/* 289 */     return new Archive(bufs, offset, length, cd, index);
/*     */   }
/*     */   
/*     */   private static String getNameOfEntry(ByteBuffer[] bufs, long cde) {
/* 293 */     long name = cde + 46L;
/* 294 */     int nameLen = getUnsignedShort(bufs, cde + 28L);
/* 295 */     boolean utf8 = ((getUnsignedShort(bufs, cde + 8L) & 0x800) != 0);
/* 296 */     if (utf8) {
/* 297 */       return new String(getBytes(bufs, name, nameLen), StandardCharsets.UTF_8);
/*     */     }
/* 299 */     char[] nameChars = new char[nameLen];
/* 300 */     for (int i = 0; i < nameLen; i++) {
/* 301 */       nameChars[i] = Cp437.charFor(getUnsignedByte(bufs, name + i));
/*     */     }
/* 303 */     return new String(nameChars);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getHashCodeOfEntry(ByteBuffer[] bufs, long cde) {
/* 308 */     long name = cde + 46L;
/* 309 */     int nameLen = getUnsignedShort(bufs, cde + 28L);
/* 310 */     boolean utf8 = ((getUnsignedShort(bufs, cde + 8L) & 0x800) != 0);
/* 311 */     int hc = 0;
/* 312 */     if (utf8) {
/*     */       int i;
/* 314 */       for (i = 0; i < nameLen; i += Utf8.getByteCount(getUnsignedByte(bufs, name + i))) {
/* 315 */         int cp = Utf8.codePointAt(bufs, name + i);
/* 316 */         if (Character.isSupplementaryCodePoint(cp)) {
/* 317 */           hc = hc * 31 + Character.highSurrogate(cp);
/* 318 */           hc = hc * 31 + Character.lowSurrogate(cp);
/*     */         } else {
/* 320 */           hc = hc * 31 + cp;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 324 */       for (int i = 0; i < nameLen; i++) {
/* 325 */         hc = hc * 31 + Cp437.charFor(getUnsignedByte(bufs, name + i));
/*     */       }
/*     */     } 
/* 328 */     return hc;
/*     */   }
/*     */   
/*     */   public long getFirstEntryHandle() {
/* 332 */     return this.cd;
/*     */   }
/*     */   
/*     */   public long getNextEntryHandle(long entryHandle) {
/* 336 */     long next = entryHandle + 46L + getUnsignedShort(this.bufs, this.offset + entryHandle + 28L) + getUnsignedShort(this.bufs, this.offset + entryHandle + 30L);
/* 337 */     if (next >= this.length || getInt(this.bufs, this.offset + next + 0L) != 33639248) {
/* 338 */       return -1L;
/*     */     }
/* 340 */     return next;
/*     */   }
/*     */   
/*     */   public long getEntryHandle(String fileName) {
/* 344 */     int mask = this.index.getMask();
/* 345 */     int base = fileName.hashCode();
/*     */     
/* 347 */     for (int i = 0; i < mask; i++) {
/* 348 */       long entryHandle = this.index.get(base + i & mask);
/* 349 */       if (entryHandle == -1L) {
/* 350 */         return -1L;
/*     */       }
/* 352 */       if (entryNameEquals(entryHandle, fileName)) {
/* 353 */         return entryHandle;
/*     */       }
/*     */     } 
/* 356 */     return -1L;
/*     */   }
/*     */   
/*     */   public boolean entryNameEquals(long entryHandle, String fileName) {
/* 360 */     long name = entryHandle + 46L;
/* 361 */     int nameLen = getUnsignedShort(this.bufs, this.offset + entryHandle + 28L);
/* 362 */     boolean utf8 = ((getUnsignedShort(this.bufs, this.offset + entryHandle + 8L) & 0x800) != 0);
/* 363 */     int length = fileName.length();
/* 364 */     if (utf8) {
/*     */       long l;
/*     */       int k;
/* 367 */       for (l = 0L, k = 0; l < nameLen && k < length; l += Utf8.getByteCount(getUnsignedByte(this.bufs, this.offset + name + l)), k = fileName.offsetByCodePoints(k, 1)) {
/* 368 */         if (Utf8.codePointAt(this.bufs, this.offset + name + l) != fileName.codePointAt(k)) {
/* 369 */           return false;
/*     */         }
/*     */       } 
/* 372 */       return (l == nameLen && k == length);
/*     */     } 
/*     */     int i, j;
/* 375 */     for (i = 0, j = 0; i < nameLen && j < length; i++, j = fileName.offsetByCodePoints(j, 1)) {
/* 376 */       if (Cp437.charFor(getUnsignedByte(this.bufs, this.offset + i + entryHandle + 46L)) != fileName.codePointAt(j)) {
/* 377 */         return false;
/*     */       }
/*     */     } 
/* 380 */     return (i == nameLen && j == length);
/*     */   }
/*     */ 
/*     */   
/*     */   private long getLocalHeader(long entryHandle) {
/* 385 */     long lh = getUnsignedInt(this.bufs, this.offset + entryHandle + 42L);
/* 386 */     if (lh == 4294967295L) {
/* 387 */       long zip64 = getExtraRecord(entryHandle, 1);
/* 388 */       if (zip64 != -1L) {
/* 389 */         lh = getLong(this.bufs, this.offset + zip64 + 16L);
/*     */       }
/*     */     } 
/* 392 */     return lh;
/*     */   }
/*     */   
/*     */   public String getEntryName(long entryHandle) {
/* 396 */     return getNameOfEntry(this.bufs, entryHandle);
/*     */   }
/*     */   public ByteBuffer getEntryContents(long entryHandle) throws IOException {
/*     */     Inflater inflater;
/* 400 */     long size = getUncompressedSize(entryHandle);
/* 401 */     long compSize = getCompressedSize(entryHandle);
/* 402 */     if (size > 268435456L || compSize > 268435456L) {
/* 403 */       throw new IOException("Entry is too large to read into RAM");
/*     */     }
/* 405 */     long localHeader = getLocalHeader(entryHandle);
/* 406 */     if ((getUnsignedShort(this.bufs, this.offset + localHeader + 6L) & 0x41) != 0) {
/* 407 */       throw new IOException("Cannot read encrypted entries");
/*     */     }
/* 409 */     long offset = getDataOffset(localHeader);
/* 410 */     int method = getCompressionMethod(entryHandle);
/* 411 */     switch (method) {
/*     */       case 0:
/* 413 */         return bufferOf(this.bufs, this.offset + offset, (int)size);
/*     */       
/*     */       case 8:
/* 416 */         inflater = new Inflater(true);
/*     */         try {
/* 418 */           return JDKSpecific.inflate(inflater, this.bufs, this.offset + offset, (int)compSize, (int)size);
/* 419 */         } catch (DataFormatException e) {
/* 420 */           throw new IOException(e);
/*     */         } finally {
/* 422 */           inflater.end();
/*     */         } 
/*     */     } 
/*     */     
/* 426 */     throw new IOException("Unsupported compression scheme");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private long getDataOffset(long localHeader) {
/* 432 */     return localHeader + 30L + getUnsignedShort(this.bufs, this.offset + localHeader + 26L) + getUnsignedShort(this.bufs, this.offset + localHeader + 28L);
/*     */   }
/*     */   
/*     */   public InputStream getEntryStream(long entryHandle) throws IOException {
/* 436 */     long size = getCompressedSize(entryHandle);
/* 437 */     long localHeader = getLocalHeader(entryHandle);
/* 438 */     if ((getUnsignedShort(this.bufs, this.offset + localHeader + 6L) & 0x41) != 0) {
/* 439 */       throw new IOException("Cannot read encrypted entries");
/*     */     }
/* 441 */     long offset = getDataOffset(localHeader);
/* 442 */     int method = getCompressionMethod(entryHandle);
/* 443 */     switch (method) {
/*     */       case 0:
/* 445 */         return new ByteBufferInputStream(this.bufs, this.offset + offset, size);
/*     */       
/*     */       case 8:
/* 448 */         return new InflaterInputStream(new ByteBufferInputStream(this.bufs, this.offset + offset, size));
/*     */     } 
/*     */     
/* 451 */     throw new IOException("Unsupported compression scheme");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Archive getNestedArchive(long entryHandle) throws IOException {
/* 457 */     long localHeader = getLocalHeader(entryHandle);
/* 458 */     if ((getUnsignedShort(this.bufs, this.offset + localHeader + 6L) & 0x41) != 0) {
/* 459 */       throw new IOException("Cannot read encrypted entries");
/*     */     }
/* 461 */     long offset = getDataOffset(localHeader);
/* 462 */     int method = getCompressionMethod(entryHandle);
/* 463 */     if (method != 0) {
/* 464 */       throw new IOException("Cannot open compressed nested archive");
/*     */     }
/* 466 */     long size = getUncompressedSize(entryHandle);
/* 467 */     if (size < 2147483647L) {
/* 468 */       ByteBuffer slice = sliceOf(this.bufs, this.offset + offset, (int)size);
/* 469 */       if (slice != null) {
/* 470 */         return open(slice);
/*     */       }
/*     */     } 
/* 473 */     return open(this.bufs, this.offset + offset, size);
/*     */   }
/*     */   
/*     */   public boolean isCompressed(long entryHandle) {
/* 477 */     return (getCompressionMethod(entryHandle) != 0);
/*     */   }
/*     */   
/*     */   private int getCompressionMethod(long entryHandle) {
/* 481 */     return getUnsignedShort(this.bufs, this.offset + entryHandle + 10L);
/*     */   }
/*     */   
/*     */   private long getExtraRecord(long entryHandle, int headerId) {
/* 485 */     long extra = entryHandle + 46L + getUnsignedShort(this.bufs, this.offset + entryHandle + 28L) + getUnsignedShort(this.bufs, this.offset + entryHandle + 32L);
/* 486 */     int extraLen = getUnsignedShort(this.bufs, this.offset + entryHandle + 30L); int i;
/* 487 */     for (i = 0; i < extraLen; i += getUnsignedShort(this.bufs, this.offset + extra + i + 2L)) {
/* 488 */       if (getUnsignedShort(this.bufs, this.offset + extra + i) == headerId) {
/* 489 */         return extra + i + 4L;
/*     */       }
/*     */     } 
/* 492 */     return -1L;
/*     */   }
/*     */   
/*     */   public long getUncompressedSize(long entryHandle) {
/* 496 */     long size = getUnsignedInt(this.bufs, this.offset + entryHandle + 24L);
/* 497 */     if (size == -1L) {
/* 498 */       long zip64 = getExtraRecord(entryHandle, 1);
/* 499 */       if (zip64 != -1L) {
/* 500 */         size = getLong(this.bufs, this.offset + zip64 + 0L);
/*     */       }
/*     */     } 
/* 503 */     return size;
/*     */   }
/*     */   
/*     */   public long getCompressedSize(long entryHandle) {
/* 507 */     long size = getUnsignedInt(this.bufs, this.offset + entryHandle + 20L);
/* 508 */     if (size == -1L) {
/* 509 */       long zip64 = getExtraRecord(entryHandle, 1);
/* 510 */       if (zip64 != -1L) {
/* 511 */         size = getLong(this.bufs, this.offset + zip64 + 8L);
/*     */       }
/*     */     } 
/* 514 */     return size;
/*     */   }
/*     */   
/*     */   public long getModifiedTime(long entryHandle) {
/* 518 */     long unix = getExtraRecord(entryHandle, 13);
/* 519 */     if (unix != -1L) {
/* 520 */       long unixTime = getUnsignedInt(this.bufs, this.offset + unix + 4L);
/* 521 */       if (unixTime != 0L) {
/* 522 */         return unixTime * 1000L;
/*     */       }
/*     */     } 
/*     */     
/* 526 */     return dosTimeStamp(getUnsignedShort(this.bufs, this.offset + entryHandle + 12L), getUnsignedShort(this.bufs, this.offset + entryHandle + 14L));
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */   
/*     */   private static long dosTimeStamp(int modTime, int modDate) {
/* 534 */     int year = 1980 + (modDate >> 9);
/* 535 */     int month = 1 + (modDate >> 5 & 0xF);
/* 536 */     int day = modDate & 0x1F;
/* 537 */     int hour = modTime >> 11;
/* 538 */     int minute = modTime >> 5 & 0x3F;
/* 539 */     int second = (modTime & 0x1F) << 1;
/* 540 */     return LocalDateTime.of(year, month, day, hour, minute, second).toInstant(ZoneOffset.UTC).toEpochMilli();
/*     */   }
/*     */   
/*     */   public boolean isDirectory(long entryHandle) {
/* 544 */     int madeBy = getUnsignedShort(this.bufs, this.offset + entryHandle + 4L);
/* 545 */     int extAttr = getInt(this.bufs, entryHandle + 38L);
/* 546 */     switch (madeBy) {
/*     */       
/*     */       case 3:
/* 549 */         return ((extAttr & 0xF000) == 16384);
/*     */     } 
/*     */     
/* 552 */     return ((extAttr & 0x10) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Cp437
/*     */   {
/* 562 */     static final char[] codePoints = new char[] { Character.MIN_VALUE, '☺', '☻', '♥', '♦', '♣', '♠', '•', '◘', '○', '◙', '♂', '♀', '♪', '♫', '☼', '►', '◄', '↕', '‼', '¶', '§', '▬', '↨', '↑', '↓', '→', '←', '∟', '↔', '▲', '▼', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '⌂', 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', '¢', '£', '¥', '₧', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', '¬', '½', '¼', '¡', '«', '»', '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐', '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧', '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀', 'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩', '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', ' ' };
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
/*     */     static char charFor(int c) {
/* 584 */       return codePoints[c];
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Utf8
/*     */   {
/*     */     static int getByteCount(int a) {
/* 592 */       if (a <= 127)
/* 593 */         return 1; 
/* 594 */       if (a <= 191)
/*     */       {
/* 596 */         return 1; } 
/* 597 */       if (a <= 223)
/* 598 */         return 2; 
/* 599 */       if (a <= 239)
/* 600 */         return 3; 
/* 601 */       if (a <= 247) {
/* 602 */         return 4;
/*     */       }
/*     */       
/* 605 */       return 1;
/*     */     }
/*     */ 
/*     */     
/*     */     static int codePointAt(ByteBuffer[] bufs, long i) {
/* 610 */       int a = Archive.getUnsignedByte(bufs, i);
/* 611 */       if (a <= 127)
/* 612 */         return a; 
/* 613 */       if (a <= 191)
/*     */       {
/* 615 */         return 65533;
/*     */       }
/* 617 */       int b = Archive.getUnsignedByte(bufs, i + 1L);
/* 618 */       if ((b & 0xC0) != 128)
/*     */       {
/* 620 */         return 65533;
/*     */       }
/* 622 */       if (a <= 223)
/*     */       {
/* 624 */         return (a & 0x1F) << 6 | b & 0x3F;
/*     */       }
/* 626 */       int c = Archive.getUnsignedByte(bufs, i + 2L);
/* 627 */       if ((c & 0xC0) != 128)
/*     */       {
/* 629 */         return 65533;
/*     */       }
/* 631 */       if (a <= 239)
/*     */       {
/* 633 */         return (a & 0xF) << 12 | (b & 0x3F) << 6 | c & 0x3F;
/*     */       }
/* 635 */       int d = Archive.getUnsignedByte(bufs, i + 3L);
/* 636 */       if ((d & 0xC0) != 128)
/*     */       {
/* 638 */         return 65533;
/*     */       }
/* 640 */       if (a <= 247)
/*     */       {
/* 642 */         return (a & 0x7) << 18 | (b & 0x3F) << 12 | (c & 0x3F) << 6 | d & 0x3F;
/*     */       }
/*     */       
/* 645 */       return 65533;
/*     */     }
/*     */   }
/*     */   
/*     */   static int bufIdx(long idx) {
/* 650 */     return (int)(idx >>> BUF_SHIFT);
/*     */   }
/*     */   
/*     */   static int bufOffs(long idx) {
/* 654 */     return (int)idx & 0x3FFFFFFF;
/*     */   }
/*     */   
/*     */   static byte getByte(ByteBuffer[] bufs, long idx) {
/* 658 */     return bufs[bufIdx(idx)].get(bufOffs(idx));
/*     */   }
/*     */   
/*     */   static int getUnsignedByte(ByteBuffer[] bufs, long idx) {
/* 662 */     return getByte(bufs, idx) & 0xFF;
/*     */   }
/*     */   
/*     */   static int getUnsignedByte(ByteBuffer buf, int idx) {
/* 666 */     return buf.get(idx) & 0xFF;
/*     */   }
/*     */   
/*     */   static short getShort(ByteBuffer[] bufs, long idx) {
/* 670 */     int bi = bufIdx(idx);
/* 671 */     return (bi == bufIdx(idx + 1L)) ? bufs[bi].getShort(bufOffs(idx)) : (short)(getUnsignedByte(bufs, idx) | getByte(bufs, idx + 1L) << 8);
/*     */   }
/*     */   
/*     */   static int getUnsignedShort(ByteBuffer[] bufs, long idx) {
/* 675 */     return getShort(bufs, idx) & 0xFFFF;
/*     */   }
/*     */   
/*     */   static int getMedium(ByteBuffer[] bufs, long idx) {
/* 679 */     return getUnsignedByte(bufs, idx) | getUnsignedShort(bufs, idx + 1L) << 8;
/*     */   }
/*     */   
/*     */   static long getUnsignedMedium(ByteBuffer[] bufs, long idx) {
/* 683 */     return (getUnsignedByte(bufs, idx) | getUnsignedShort(bufs, idx + 1L) << 8);
/*     */   }
/*     */   
/*     */   static int getInt(ByteBuffer[] bufs, long idx) {
/* 687 */     int bi = bufIdx(idx);
/* 688 */     return (bi == bufIdx(idx + 3L)) ? bufs[bi].getInt(bufOffs(idx)) : (getUnsignedShort(bufs, idx) | getShort(bufs, idx + 2L) << 16);
/*     */   }
/*     */   
/*     */   static long getUnsignedInt(ByteBuffer[] bufs, long idx) {
/* 692 */     return getInt(bufs, idx) & 0xFFFFFFFFL;
/*     */   }
/*     */   
/*     */   static long getLong(ByteBuffer[] bufs, long idx) {
/* 696 */     int bi = bufIdx(idx);
/* 697 */     return (bi == bufIdx(idx + 7L)) ? bufs[bi].getLong(bufOffs(idx)) : (getUnsignedInt(bufs, idx) | getInt(bufs, idx + 4L) << 32L);
/*     */   }
/*     */   
/*     */   static void readBytes(ByteBuffer[] bufs, long idx, byte[] dest, int off, int len) {
/* 701 */     while (len > 0) {
/* 702 */       int bi = bufIdx(idx);
/* 703 */       int bo = bufOffs(idx);
/* 704 */       ByteBuffer buf = bufs[bi].duplicate();
/* 705 */       buf.position(bo);
/* 706 */       int cnt = Math.min(len, buf.remaining());
/* 707 */       buf.get(dest, 0, cnt);
/* 708 */       len -= cnt;
/* 709 */       off += cnt;
/* 710 */       idx += cnt;
/*     */     } 
/*     */   }
/*     */   
/*     */   static byte[] getBytes(ByteBuffer[] bufs, long idx, int len) {
/* 715 */     byte[] bytes = new byte[len];
/* 716 */     readBytes(bufs, idx, bytes, 0, len);
/* 717 */     return bytes;
/*     */   }
/*     */   
/* 720 */   private static final ByteBuffer EMPTY_BUF = ByteBuffer.allocateDirect(0);
/*     */   
/*     */   static ByteBuffer sliceOf(ByteBuffer[] bufs, long idx, int len) {
/* 723 */     if (len == 0) return EMPTY_BUF; 
/* 724 */     int biStart = bufIdx(idx);
/* 725 */     int biEnd = bufIdx(idx + len - 1L);
/* 726 */     if (biStart == biEnd) {
/* 727 */       ByteBuffer buf = bufs[biStart].duplicate();
/* 728 */       buf.position(bufOffs(idx));
/* 729 */       buf.limit(buf.position() + len);
/* 730 */       return buf.slice();
/*     */     } 
/* 732 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   static ByteBuffer bufferOf(ByteBuffer[] bufs, long idx, int len) {
/* 737 */     ByteBuffer buf = sliceOf(bufs, idx, len);
/* 738 */     if (buf == null) {
/* 739 */       buf = ByteBuffer.wrap(getBytes(bufs, idx, len));
/*     */     }
/* 741 */     return buf;
/*     */   }
/*     */   
/*     */   static long capacity(ByteBuffer[] bufs) {
/* 745 */     int lastIdx = bufs.length - 1;
/* 746 */     return lastIdx * 1073741824L + bufs[lastIdx].capacity();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\Archive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */