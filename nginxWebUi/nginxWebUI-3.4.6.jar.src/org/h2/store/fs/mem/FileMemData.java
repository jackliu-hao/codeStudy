/*     */ package org.h2.store.fs.mem;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.h2.compress.CompressLZF;
/*     */ import org.h2.util.MathUtils;
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
/*     */ class FileMemData
/*     */ {
/*     */   private static final int CACHE_SIZE = 8;
/*     */   private static final int BLOCK_SIZE_SHIFT = 10;
/*     */   private static final int BLOCK_SIZE = 1024;
/*     */   private static final int BLOCK_SIZE_MASK = 1023;
/*  27 */   private static final CompressLZF LZF = new CompressLZF();
/*  28 */   private static final byte[] BUFFER = new byte[2048];
/*     */   
/*     */   private static final byte[] COMPRESSED_EMPTY_BLOCK;
/*  31 */   private static final Cache<CompressItem, CompressItem> COMPRESS_LATER = new Cache<>(8);
/*     */   
/*     */   private String name;
/*     */   
/*     */   private final int id;
/*     */   private final boolean compress;
/*     */   private volatile long length;
/*     */   private AtomicReference<byte[]>[] data;
/*     */   private long lastModified;
/*     */   private boolean isReadOnly;
/*     */   private boolean isLockedExclusive;
/*     */   private int sharedLockCount;
/*     */   
/*     */   static {
/*  45 */     byte[] arrayOfByte = new byte[1024];
/*  46 */     int i = LZF.compress(arrayOfByte, 0, 1024, BUFFER, 0);
/*  47 */     COMPRESSED_EMPTY_BLOCK = Arrays.copyOf(BUFFER, i);
/*     */   }
/*     */ 
/*     */   
/*     */   FileMemData(String paramString, boolean paramBoolean) {
/*  52 */     this.name = paramString;
/*  53 */     this.id = paramString.hashCode();
/*  54 */     this.compress = paramBoolean;
/*  55 */     this.data = (AtomicReference<byte[]>[])new AtomicReference[0];
/*  56 */     this.lastModified = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] getPage(int paramInt) {
/*  66 */     AtomicReference<byte[]>[] arrayOfAtomicReference = this.data;
/*  67 */     if (paramInt >= arrayOfAtomicReference.length) {
/*  68 */       return null;
/*     */     }
/*  70 */     return arrayOfAtomicReference[paramInt].get();
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
/*     */   private void setPage(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, boolean paramBoolean) {
/*  83 */     AtomicReference<byte[]>[] arrayOfAtomicReference = this.data;
/*  84 */     if (paramInt >= arrayOfAtomicReference.length) {
/*     */       return;
/*     */     }
/*  87 */     if (paramBoolean) {
/*  88 */       arrayOfAtomicReference[paramInt].set(paramArrayOfbyte2);
/*     */     } else {
/*  90 */       arrayOfAtomicReference[paramInt].compareAndSet(paramArrayOfbyte1, paramArrayOfbyte2);
/*     */     } 
/*     */   }
/*     */   
/*     */   int getId() {
/*  95 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean lockExclusive() {
/* 104 */     if (this.sharedLockCount > 0 || this.isLockedExclusive) {
/* 105 */       return false;
/*     */     }
/* 107 */     this.isLockedExclusive = true;
/* 108 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean lockShared() {
/* 117 */     if (this.isLockedExclusive) {
/* 118 */       return false;
/*     */     }
/* 120 */     this.sharedLockCount++;
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void unlock() throws IOException {
/* 128 */     if (this.isLockedExclusive) {
/* 129 */       this.isLockedExclusive = false;
/* 130 */     } else if (this.sharedLockCount > 0) {
/* 131 */       this.sharedLockCount--;
/*     */     } else {
/* 133 */       throw new IOException("not locked");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static class Cache<K, V>
/*     */     extends LinkedHashMap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     private final int size;
/*     */     
/*     */     Cache(int param1Int) {
/* 146 */       super(param1Int, 0.75F, true);
/* 147 */       this.size = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized V put(K param1K, V param1V) {
/* 152 */       return super.put(param1K, param1V);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean removeEldestEntry(Map.Entry<K, V> param1Entry) {
/* 157 */       if (size() < this.size) {
/* 158 */         return false;
/*     */       }
/* 160 */       FileMemData.CompressItem compressItem = (FileMemData.CompressItem)param1Entry.getKey();
/* 161 */       compressItem.file.compress(compressItem.page);
/* 162 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class CompressItem
/*     */   {
/*     */     FileMemData file;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int page;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 183 */       return this.page ^ this.file.getId();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/* 188 */       if (param1Object instanceof CompressItem) {
/* 189 */         CompressItem compressItem = (CompressItem)param1Object;
/* 190 */         return (compressItem.page == this.page && compressItem.file == this.file);
/*     */       } 
/* 192 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void compressLater(int paramInt) {
/* 198 */     CompressItem compressItem = new CompressItem();
/* 199 */     compressItem.file = this;
/* 200 */     compressItem.page = paramInt;
/* 201 */     synchronized (LZF) {
/* 202 */       COMPRESS_LATER.put(compressItem, compressItem);
/*     */     } 
/*     */   }
/*     */   
/*     */   private byte[] expand(int paramInt) {
/* 207 */     byte[] arrayOfByte1 = getPage(paramInt);
/* 208 */     if (arrayOfByte1.length == 1024) {
/* 209 */       return arrayOfByte1;
/*     */     }
/* 211 */     byte[] arrayOfByte2 = new byte[1024];
/* 212 */     if (arrayOfByte1 != COMPRESSED_EMPTY_BLOCK) {
/* 213 */       synchronized (LZF) {
/* 214 */         LZF.expand(arrayOfByte1, 0, arrayOfByte1.length, arrayOfByte2, 0, 1024);
/*     */       } 
/*     */     }
/* 217 */     setPage(paramInt, arrayOfByte1, arrayOfByte2, false);
/* 218 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void compress(int paramInt) {
/* 227 */     byte[] arrayOfByte = getPage(paramInt);
/* 228 */     if (arrayOfByte == null || arrayOfByte.length != 1024) {
/*     */       return;
/*     */     }
/*     */     
/* 232 */     synchronized (LZF) {
/* 233 */       int i = LZF.compress(arrayOfByte, 0, 1024, BUFFER, 0);
/* 234 */       if (i <= 1024) {
/* 235 */         byte[] arrayOfByte1 = Arrays.copyOf(BUFFER, i);
/*     */         
/* 237 */         setPage(paramInt, arrayOfByte, arrayOfByte1, false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void touch(boolean paramBoolean) {
/* 248 */     if (this.isReadOnly || paramBoolean) {
/* 249 */       throw new NonWritableChannelException();
/*     */     }
/* 251 */     this.lastModified = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long length() {
/* 260 */     return this.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void truncate(long paramLong) {
/* 269 */     changeLength(paramLong);
/* 270 */     long l = MathUtils.roundUpLong(paramLong, 1024L);
/* 271 */     if (l != paramLong) {
/* 272 */       int i = (int)(paramLong >>> 10L);
/* 273 */       byte[] arrayOfByte1 = expand(i);
/* 274 */       byte[] arrayOfByte2 = Arrays.copyOf(arrayOfByte1, arrayOfByte1.length);
/* 275 */       for (int j = (int)(paramLong & 0x3FFL); j < 1024; j++) {
/* 276 */         arrayOfByte2[j] = 0;
/*     */       }
/* 278 */       setPage(i, arrayOfByte1, arrayOfByte2, true);
/* 279 */       if (this.compress) {
/* 280 */         compressLater(i);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void changeLength(long paramLong) {
/* 286 */     this.length = paramLong;
/* 287 */     paramLong = MathUtils.roundUpLong(paramLong, 1024L);
/* 288 */     int i = (int)(paramLong >>> 10L);
/* 289 */     if (i != this.data.length) {
/* 290 */       AtomicReference[] arrayOfAtomicReference = Arrays.<AtomicReference>copyOf((AtomicReference[])this.data, i);
/* 291 */       for (int j = this.data.length; j < i; j++) {
/* 292 */         arrayOfAtomicReference[j] = new AtomicReference<>(COMPRESSED_EMPTY_BLOCK);
/*     */       }
/* 294 */       this.data = (AtomicReference<byte[]>[])arrayOfAtomicReference;
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
/*     */   long readWrite(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 309 */     long l = paramLong + paramInt2;
/* 310 */     if (l > this.length) {
/* 311 */       if (paramBoolean) {
/* 312 */         changeLength(l);
/*     */       } else {
/* 314 */         paramInt2 = (int)(this.length - paramLong);
/*     */       } 
/*     */     }
/* 317 */     while (paramInt2 > 0) {
/* 318 */       int i = (int)Math.min(paramInt2, 1024L - (paramLong & 0x3FFL));
/* 319 */       int j = (int)(paramLong >>> 10L);
/* 320 */       byte[] arrayOfByte = expand(j);
/* 321 */       int k = (int)(paramLong & 0x3FFL);
/* 322 */       if (paramBoolean) {
/* 323 */         byte[] arrayOfByte1 = Arrays.copyOf(arrayOfByte, arrayOfByte.length);
/* 324 */         System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, k, i);
/* 325 */         setPage(j, arrayOfByte, arrayOfByte1, true);
/*     */       } else {
/* 327 */         System.arraycopy(arrayOfByte, k, paramArrayOfbyte, paramInt1, i);
/*     */       } 
/* 329 */       if (this.compress) {
/* 330 */         compressLater(j);
/*     */       }
/* 332 */       paramInt1 += i;
/* 333 */       paramLong += i;
/* 334 */       paramInt2 -= i;
/*     */     } 
/* 336 */     return paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setName(String paramString) {
/* 345 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getName() {
/* 354 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getLastModified() {
/* 363 */     return this.lastModified;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean canWrite() {
/* 372 */     return !this.isReadOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean setReadOnly() {
/* 381 */     this.isReadOnly = true;
/* 382 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\mem\FileMemData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */