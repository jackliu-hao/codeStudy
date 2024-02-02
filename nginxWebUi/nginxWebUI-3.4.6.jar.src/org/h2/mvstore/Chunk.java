/*     */ package org.h2.mvstore;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.BitSet;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public final class Chunk
/*     */ {
/*     */   public static final int MAX_ID = 67108863;
/*     */   static final int MAX_HEADER_LENGTH = 1024;
/*     */   static final int FOOTER_LENGTH = 128;
/*     */   private static final String ATTR_CHUNK = "chunk";
/*     */   private static final String ATTR_BLOCK = "block";
/*     */   private static final String ATTR_LEN = "len";
/*     */   private static final String ATTR_MAP = "map";
/*     */   private static final String ATTR_MAX = "max";
/*     */   private static final String ATTR_NEXT = "next";
/*     */   private static final String ATTR_PAGES = "pages";
/*     */   private static final String ATTR_ROOT = "root";
/*     */   private static final String ATTR_TIME = "time";
/*     */   private static final String ATTR_VERSION = "version";
/*     */   private static final String ATTR_LIVE_MAX = "liveMax";
/*     */   private static final String ATTR_LIVE_PAGES = "livePages";
/*     */   private static final String ATTR_UNUSED = "unused";
/*     */   private static final String ATTR_UNUSED_AT_VERSION = "unusedAtVersion";
/*     */   private static final String ATTR_PIN_COUNT = "pinCount";
/*     */   private static final String ATTR_TOC = "toc";
/*     */   private static final String ATTR_OCCUPANCY = "occupancy";
/*     */   private static final String ATTR_FLETCHER = "fletcher";
/*     */   public final int id;
/*     */   public volatile long block;
/*     */   public int len;
/*     */   int pageCount;
/*     */   int pageCountLive;
/*     */   int tocPos;
/*     */   BitSet occupancy;
/*     */   public long maxLen;
/*     */   public long maxLenLive;
/*     */   int collectPriority;
/*     */   long layoutRootPos;
/*     */   public long version;
/*     */   public long time;
/*     */   public long unused;
/*     */   long unusedAtVersion;
/*     */   public int mapId;
/*     */   public long next;
/*     */   private int pinCount;
/*     */   
/*     */   private Chunk(String paramString) {
/* 165 */     this(DataUtils.parseMap(paramString), true);
/*     */   }
/*     */   
/*     */   Chunk(Map<String, String> paramMap) {
/* 169 */     this(paramMap, false);
/*     */   }
/*     */   
/*     */   private Chunk(Map<String, String> paramMap, boolean paramBoolean) {
/* 173 */     this(DataUtils.readHexInt(paramMap, "chunk", 0));
/* 174 */     this.block = DataUtils.readHexLong(paramMap, "block", 0L);
/* 175 */     this.version = DataUtils.readHexLong(paramMap, "version", this.id);
/* 176 */     if (paramBoolean) {
/* 177 */       this.len = DataUtils.readHexInt(paramMap, "len", 0);
/* 178 */       this.pageCount = DataUtils.readHexInt(paramMap, "pages", 0);
/* 179 */       this.pageCountLive = DataUtils.readHexInt(paramMap, "livePages", this.pageCount);
/* 180 */       this.mapId = DataUtils.readHexInt(paramMap, "map", 0);
/* 181 */       this.maxLen = DataUtils.readHexLong(paramMap, "max", 0L);
/* 182 */       this.maxLenLive = DataUtils.readHexLong(paramMap, "liveMax", this.maxLen);
/* 183 */       this.layoutRootPos = DataUtils.readHexLong(paramMap, "root", 0L);
/* 184 */       this.time = DataUtils.readHexLong(paramMap, "time", 0L);
/* 185 */       this.unused = DataUtils.readHexLong(paramMap, "unused", 0L);
/* 186 */       this.unusedAtVersion = DataUtils.readHexLong(paramMap, "unusedAtVersion", 0L);
/* 187 */       this.next = DataUtils.readHexLong(paramMap, "next", 0L);
/* 188 */       this.pinCount = DataUtils.readHexInt(paramMap, "pinCount", 0);
/* 189 */       this.tocPos = DataUtils.readHexInt(paramMap, "toc", 0);
/* 190 */       byte[] arrayOfByte = DataUtils.parseHexBytes(paramMap, "occupancy");
/* 191 */       if (arrayOfByte == null) {
/* 192 */         this.occupancy = new BitSet();
/*     */       } else {
/* 194 */         this.occupancy = BitSet.valueOf(arrayOfByte);
/* 195 */         if (this.pageCount - this.pageCountLive != this.occupancy.cardinality())
/* 196 */           throw DataUtils.newMVStoreException(6, "Inconsistent occupancy info {0} - {1} != {2} {3}", new Object[] {
/*     */                 
/* 198 */                 Integer.valueOf(this.pageCount), Integer.valueOf(this.pageCountLive), Integer.valueOf(this.occupancy.cardinality()), this
/*     */               }); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   Chunk(int paramInt) {
/* 205 */     this.id = paramInt;
/* 206 */     if (paramInt <= 0) {
/* 207 */       throw DataUtils.newMVStoreException(6, "Invalid chunk id {0}", new Object[] {
/* 208 */             Integer.valueOf(paramInt)
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Chunk readChunkHeader(ByteBuffer paramByteBuffer, long paramLong) {
/* 220 */     int i = paramByteBuffer.position();
/* 221 */     byte[] arrayOfByte = new byte[Math.min(paramByteBuffer.remaining(), 1024)];
/* 222 */     paramByteBuffer.get(arrayOfByte);
/*     */     try {
/* 224 */       for (byte b = 0; b < arrayOfByte.length; b++) {
/* 225 */         if (arrayOfByte[b] == 10) {
/*     */           
/* 227 */           paramByteBuffer.position(i + b + 1);
/* 228 */           String str = (new String(arrayOfByte, 0, b, StandardCharsets.ISO_8859_1)).trim();
/* 229 */           return fromString(str);
/*     */         } 
/*     */       } 
/* 232 */     } catch (Exception exception) {
/*     */       
/* 234 */       throw DataUtils.newMVStoreException(6, "File corrupt reading chunk at position {0}", new Object[] {
/*     */             
/* 236 */             Long.valueOf(paramLong), exception });
/*     */     } 
/* 238 */     throw DataUtils.newMVStoreException(6, "File corrupt reading chunk at position {0}", new Object[] {
/*     */           
/* 240 */           Long.valueOf(paramLong)
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void writeChunkHeader(WriteBuffer paramWriteBuffer, int paramInt) {
/* 250 */     long l = (paramWriteBuffer.position() + paramInt - 1);
/* 251 */     paramWriteBuffer.put(asString().getBytes(StandardCharsets.ISO_8859_1));
/* 252 */     while (paramWriteBuffer.position() < l) {
/* 253 */       paramWriteBuffer.put((byte)32);
/*     */     }
/* 255 */     if (paramInt != 0 && paramWriteBuffer.position() > l) {
/* 256 */       throw DataUtils.newMVStoreException(3, "Chunk metadata too long", new Object[0]);
/*     */     }
/*     */ 
/*     */     
/* 260 */     paramWriteBuffer.put((byte)10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String getMetaKey(int paramInt) {
/* 270 */     return "chunk." + Integer.toHexString(paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Chunk fromString(String paramString) {
/* 280 */     return new Chunk(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getFillRate() {
/* 289 */     assert this.maxLenLive <= this.maxLen : this.maxLenLive + " > " + this.maxLen;
/* 290 */     if (this.maxLenLive <= 0L)
/* 291 */       return 0; 
/* 292 */     if (this.maxLenLive == this.maxLen) {
/* 293 */       return 100;
/*     */     }
/* 295 */     return 1 + (int)(98L * this.maxLenLive / this.maxLen);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 300 */     return this.id;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 305 */     return (paramObject instanceof Chunk && ((Chunk)paramObject).id == this.id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String asString() {
/* 314 */     StringBuilder stringBuilder = new StringBuilder(240);
/* 315 */     DataUtils.appendMap(stringBuilder, "chunk", this.id);
/* 316 */     DataUtils.appendMap(stringBuilder, "block", this.block);
/* 317 */     DataUtils.appendMap(stringBuilder, "len", this.len);
/* 318 */     if (this.maxLen != this.maxLenLive) {
/* 319 */       DataUtils.appendMap(stringBuilder, "liveMax", this.maxLenLive);
/*     */     }
/* 321 */     if (this.pageCount != this.pageCountLive) {
/* 322 */       DataUtils.appendMap(stringBuilder, "livePages", this.pageCountLive);
/*     */     }
/* 324 */     DataUtils.appendMap(stringBuilder, "map", this.mapId);
/* 325 */     DataUtils.appendMap(stringBuilder, "max", this.maxLen);
/* 326 */     if (this.next != 0L) {
/* 327 */       DataUtils.appendMap(stringBuilder, "next", this.next);
/*     */     }
/* 329 */     DataUtils.appendMap(stringBuilder, "pages", this.pageCount);
/* 330 */     DataUtils.appendMap(stringBuilder, "root", this.layoutRootPos);
/* 331 */     DataUtils.appendMap(stringBuilder, "time", this.time);
/* 332 */     if (this.unused != 0L) {
/* 333 */       DataUtils.appendMap(stringBuilder, "unused", this.unused);
/*     */     }
/* 335 */     if (this.unusedAtVersion != 0L) {
/* 336 */       DataUtils.appendMap(stringBuilder, "unusedAtVersion", this.unusedAtVersion);
/*     */     }
/* 338 */     DataUtils.appendMap(stringBuilder, "version", this.version);
/* 339 */     if (this.pinCount > 0) {
/* 340 */       DataUtils.appendMap(stringBuilder, "pinCount", this.pinCount);
/*     */     }
/* 342 */     if (this.tocPos > 0) {
/* 343 */       DataUtils.appendMap(stringBuilder, "toc", this.tocPos);
/*     */     }
/* 345 */     if (!this.occupancy.isEmpty()) {
/* 346 */       DataUtils.appendMap(stringBuilder, "occupancy", 
/* 347 */           StringUtils.convertBytesToHex(this.occupancy.toByteArray()));
/*     */     }
/* 349 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   byte[] getFooterBytes() {
/* 353 */     StringBuilder stringBuilder = new StringBuilder(128);
/* 354 */     DataUtils.appendMap(stringBuilder, "chunk", this.id);
/* 355 */     DataUtils.appendMap(stringBuilder, "block", this.block);
/* 356 */     DataUtils.appendMap(stringBuilder, "version", this.version);
/* 357 */     byte[] arrayOfByte = stringBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);
/* 358 */     int i = DataUtils.getFletcher32(arrayOfByte, 0, arrayOfByte.length);
/* 359 */     DataUtils.appendMap(stringBuilder, "fletcher", i);
/* 360 */     while (stringBuilder.length() < 127) {
/* 361 */       stringBuilder.append(' ');
/*     */     }
/* 363 */     stringBuilder.append('\n');
/* 364 */     return stringBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);
/*     */   }
/*     */   
/*     */   boolean isSaved() {
/* 368 */     return (this.block != Long.MAX_VALUE);
/*     */   }
/*     */   
/*     */   boolean isLive() {
/* 372 */     return (this.pageCountLive > 0);
/*     */   }
/*     */   
/*     */   boolean isRewritable() {
/* 376 */     return (isSaved() && 
/* 377 */       isLive() && this.pageCountLive < this.pageCount && 
/*     */       
/* 379 */       isEvacuatable());
/*     */   }
/*     */   
/*     */   private boolean isEvacuatable() {
/* 383 */     return (this.pinCount == 0);
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
/*     */   ByteBuffer readBufferForPage(FileStore paramFileStore, int paramInt, long paramLong) {
/* 395 */     assert isSaved() : this;
/*     */     while (true) {
/* 397 */       long l = this.block;
/*     */       try {
/* 399 */         long l1 = l * 4096L;
/* 400 */         long l2 = l1 + this.len * 4096L;
/* 401 */         l1 += paramInt;
/* 402 */         if (l1 < 0L) {
/* 403 */           throw DataUtils.newMVStoreException(6, "Negative position {0}; p={1}, c={2}", new Object[] {
/*     */                 
/* 405 */                 Long.valueOf(l1), Long.valueOf(paramLong), toString()
/*     */               });
/*     */         }
/* 408 */         int i = DataUtils.getPageMaxLength(paramLong);
/* 409 */         if (i == 2097152) {
/*     */           
/* 411 */           i = paramFileStore.readFully(l1, 128).getInt();
/*     */ 
/*     */           
/* 414 */           i += 4;
/*     */         } 
/* 416 */         i = (int)Math.min(l2 - l1, i);
/* 417 */         if (i < 0) {
/* 418 */           throw DataUtils.newMVStoreException(6, "Illegal page length {0} reading at {1}; max pos {2} ", new Object[] {
/* 419 */                 Integer.valueOf(i), Long.valueOf(l1), Long.valueOf(l2)
/*     */               });
/*     */         }
/* 422 */         ByteBuffer byteBuffer = paramFileStore.readFully(l1, i);
/*     */         
/* 424 */         if (l == this.block) {
/* 425 */           return byteBuffer;
/*     */         }
/* 427 */       } catch (MVStoreException mVStoreException) {
/* 428 */         if (l == this.block) {
/* 429 */           throw mVStoreException;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   long[] readToC(FileStore paramFileStore) {
/* 436 */     assert isSaved() : this;
/* 437 */     assert this.tocPos > 0;
/*     */     while (true) {
/* 439 */       long l = this.block;
/*     */       try {
/* 441 */         long l1 = l * 4096L + this.tocPos;
/* 442 */         int i = this.pageCount * 8;
/* 443 */         long[] arrayOfLong = new long[this.pageCount];
/* 444 */         paramFileStore.readFully(l1, i).asLongBuffer().get(arrayOfLong);
/* 445 */         if (l == this.block) {
/* 446 */           return arrayOfLong;
/*     */         }
/* 448 */       } catch (MVStoreException mVStoreException) {
/* 449 */         if (l == this.block) {
/* 450 */           throw mVStoreException;
/*     */         }
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
/*     */   
/*     */   void accountForWrittenPage(int paramInt, boolean paramBoolean) {
/* 467 */     this.maxLen += paramInt;
/* 468 */     this.pageCount++;
/* 469 */     this.maxLenLive += paramInt;
/* 470 */     this.pageCountLive++;
/* 471 */     if (paramBoolean) {
/* 472 */       this.pinCount++;
/*     */     }
/* 474 */     assert this.pageCount - this.pageCountLive == this.occupancy.cardinality() : this.pageCount + " - " + this.pageCountLive + " <> " + this.occupancy
/* 475 */       .cardinality() + " : " + this.occupancy;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean accountForRemovedPage(int paramInt1, int paramInt2, boolean paramBoolean, long paramLong1, long paramLong2) {
/* 497 */     assert isSaved() : this;
/*     */ 
/*     */     
/* 500 */     if (this.tocPos > 0) {
/* 501 */       assert paramInt1 >= 0 && paramInt1 < this.pageCount : paramInt1 + " // " + this.pageCount;
/* 502 */       assert !this.occupancy.get(paramInt1) : paramInt1 + " " + this + " " + this.occupancy;
/* 503 */       assert this.pageCount - this.pageCountLive == this.occupancy.cardinality() : this.pageCount + " - " + this.pageCountLive + " <> " + this.occupancy
/* 504 */         .cardinality() + " : " + this.occupancy;
/* 505 */       this.occupancy.set(paramInt1);
/*     */     } 
/*     */     
/* 508 */     this.maxLenLive -= paramInt2;
/* 509 */     this.pageCountLive--;
/* 510 */     if (paramBoolean) {
/* 511 */       this.pinCount--;
/*     */     }
/*     */     
/* 514 */     if (this.unusedAtVersion < paramLong2) {
/* 515 */       this.unusedAtVersion = paramLong2;
/*     */     }
/*     */     
/* 518 */     assert this.pinCount >= 0 : this;
/* 519 */     assert this.pageCountLive >= 0 : this;
/* 520 */     assert this.pinCount <= this.pageCountLive : this;
/* 521 */     assert this.maxLenLive >= 0L : this;
/*     */     
/*     */     assert false;
/* 524 */     if (!isLive()) {
/* 525 */       this.unused = paramLong1;
/* 526 */       return true;
/*     */     } 
/* 528 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 533 */     return asString();
/*     */   }
/*     */   
/*     */   public static final class PositionComparator
/*     */     implements Comparator<Chunk> {
/* 538 */     public static final Comparator<Chunk> INSTANCE = new PositionComparator();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int compare(Chunk param1Chunk1, Chunk param1Chunk2) {
/* 544 */       return Long.compare(param1Chunk1.block, param1Chunk2.block);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\Chunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */