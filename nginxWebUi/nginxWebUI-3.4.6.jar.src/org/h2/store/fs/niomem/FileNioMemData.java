/*     */ package org.h2.store.fs.niomem;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.function.Supplier;
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
/*     */ class FileNioMemData
/*     */ {
/*     */   private static final int CACHE_MIN_SIZE = 8;
/*     */   private static final int BLOCK_SIZE_SHIFT = 16;
/*     */   private static final int BLOCK_SIZE = 65536;
/*     */   private static final int BLOCK_SIZE_MASK = 65535;
/*     */   private static final ByteBuffer COMPRESSED_EMPTY_BLOCK;
/*  30 */   private static final ThreadLocal<CompressLZF> LZF_THREAD_LOCAL = ThreadLocal.withInitial(CompressLZF::new);
/*     */ 
/*     */ 
/*     */   
/*  34 */   private static final ThreadLocal<byte[]> COMPRESS_OUT_BUF_THREAD_LOCAL = (ThreadLocal)ThreadLocal.withInitial(() -> new byte[131072]);
/*     */ 
/*     */ 
/*     */   
/*     */   final int nameHashCode;
/*     */ 
/*     */   
/*  41 */   private final CompressLaterCache<CompressItem, CompressItem> compressLaterCache = new CompressLaterCache<>(8);
/*     */   
/*     */   private String name;
/*     */   
/*     */   private final boolean compress;
/*     */   private final float compressLaterCachePercent;
/*     */   private volatile long length;
/*     */   private AtomicReference<ByteBuffer>[] buffers;
/*     */   private long lastModified;
/*     */   private boolean isReadOnly;
/*     */   private boolean isLockedExclusive;
/*     */   private int sharedLockCount;
/*  53 */   private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
/*     */   
/*     */   static {
/*  56 */     byte[] arrayOfByte1 = new byte[65536];
/*  57 */     byte[] arrayOfByte2 = new byte[131072];
/*  58 */     int i = (new CompressLZF()).compress(arrayOfByte1, 0, 65536, arrayOfByte2, 0);
/*  59 */     COMPRESSED_EMPTY_BLOCK = ByteBuffer.allocateDirect(i);
/*  60 */     COMPRESSED_EMPTY_BLOCK.put(arrayOfByte2, 0, i);
/*     */   }
/*     */ 
/*     */   
/*     */   FileNioMemData(String paramString, boolean paramBoolean, float paramFloat) {
/*  65 */     this.name = paramString;
/*  66 */     this.nameHashCode = paramString.hashCode();
/*  67 */     this.compress = paramBoolean;
/*  68 */     this.compressLaterCachePercent = paramFloat;
/*  69 */     this.buffers = (AtomicReference<ByteBuffer>[])new AtomicReference[0];
/*  70 */     this.lastModified = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean lockExclusive() {
/*  79 */     if (this.sharedLockCount > 0 || this.isLockedExclusive) {
/*  80 */       return false;
/*     */     }
/*  82 */     this.isLockedExclusive = true;
/*  83 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean lockShared() {
/*  92 */     if (this.isLockedExclusive) {
/*  93 */       return false;
/*     */     }
/*  95 */     this.sharedLockCount++;
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void unlock() {
/* 103 */     if (this.isLockedExclusive) {
/* 104 */       this.isLockedExclusive = false;
/*     */     } else {
/* 106 */       this.sharedLockCount = Math.max(0, this.sharedLockCount - 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static class CompressLaterCache<K, V>
/*     */     extends LinkedHashMap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     private int size;
/*     */     
/*     */     CompressLaterCache(int param1Int) {
/* 119 */       super(param1Int, 0.75F, true);
/* 120 */       this.size = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized V put(K param1K, V param1V) {
/* 125 */       return super.put(param1K, param1V);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean removeEldestEntry(Map.Entry<K, V> param1Entry) {
/* 130 */       if (size() < this.size) {
/* 131 */         return false;
/*     */       }
/* 133 */       FileNioMemData.CompressItem compressItem = (FileNioMemData.CompressItem)param1Entry.getKey();
/* 134 */       compressItem.data.compressPage(compressItem.page);
/* 135 */       return true;
/*     */     }
/*     */     
/*     */     public void setCacheSize(int param1Int) {
/* 139 */       this.size = param1Int;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class CompressItem
/*     */   {
/*     */     public final FileNioMemData data;
/*     */ 
/*     */ 
/*     */     
/*     */     public final int page;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompressItem(FileNioMemData param1FileNioMemData, int param1Int) {
/* 159 */       this.data = param1FileNioMemData;
/* 160 */       this.page = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 165 */       return this.page ^ this.data.nameHashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/* 170 */       if (param1Object instanceof CompressItem) {
/* 171 */         CompressItem compressItem = (CompressItem)param1Object;
/* 172 */         return (compressItem.data == this.data && compressItem.page == this.page);
/*     */       } 
/* 174 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void addToCompressLaterCache(int paramInt) {
/* 180 */     CompressItem compressItem = new CompressItem(this, paramInt);
/* 181 */     this.compressLaterCache.put(compressItem, compressItem);
/*     */   }
/*     */   
/*     */   private ByteBuffer expandPage(int paramInt) {
/* 185 */     ByteBuffer byteBuffer = this.buffers[paramInt].get();
/* 186 */     if (byteBuffer.capacity() == 65536)
/*     */     {
/* 188 */       return byteBuffer;
/*     */     }
/* 190 */     synchronized (byteBuffer) {
/* 191 */       if (byteBuffer.capacity() == 65536) {
/* 192 */         return byteBuffer;
/*     */       }
/* 194 */       ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(65536);
/* 195 */       if (byteBuffer != COMPRESSED_EMPTY_BLOCK) {
/* 196 */         byteBuffer.position(0);
/* 197 */         CompressLZF.expand(byteBuffer, byteBuffer1);
/*     */       } 
/* 199 */       this.buffers[paramInt].compareAndSet(byteBuffer, byteBuffer1);
/* 200 */       return byteBuffer1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void compressPage(int paramInt) {
/* 210 */     ByteBuffer byteBuffer = this.buffers[paramInt].get();
/* 211 */     synchronized (byteBuffer) {
/* 212 */       if (byteBuffer.capacity() != 65536) {
/*     */         return;
/*     */       }
/*     */       
/* 216 */       byte[] arrayOfByte = COMPRESS_OUT_BUF_THREAD_LOCAL.get();
/* 217 */       int i = ((CompressLZF)LZF_THREAD_LOCAL.get()).compress(byteBuffer, 0, arrayOfByte, 0);
/* 218 */       ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(i);
/* 219 */       byteBuffer1.put(arrayOfByte, 0, i);
/* 220 */       this.buffers[paramInt].compareAndSet(byteBuffer, byteBuffer1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void touch(boolean paramBoolean) {
/* 230 */     if (this.isReadOnly || paramBoolean) {
/* 231 */       throw new NonWritableChannelException();
/*     */     }
/* 233 */     this.lastModified = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long length() {
/* 242 */     return this.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void truncate(long paramLong) {
/* 251 */     this.rwLock.writeLock().lock();
/*     */     try {
/* 253 */       changeLength(paramLong);
/* 254 */       long l = MathUtils.roundUpLong(paramLong, 65536L);
/* 255 */       if (l != paramLong) {
/* 256 */         int i = (int)(paramLong >>> 16L);
/* 257 */         ByteBuffer byteBuffer = expandPage(i);
/* 258 */         for (int j = (int)(paramLong & 0xFFFFL); j < 65536; j++) {
/* 259 */           byteBuffer.put(j, (byte)0);
/*     */         }
/* 261 */         if (this.compress) {
/* 262 */           addToCompressLaterCache(i);
/*     */         }
/*     */       } 
/*     */     } finally {
/* 266 */       this.rwLock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void changeLength(long paramLong) {
/* 272 */     this.length = paramLong;
/* 273 */     paramLong = MathUtils.roundUpLong(paramLong, 65536L);
/* 274 */     int i = (int)(paramLong >>> 16L);
/* 275 */     if (i != this.buffers.length) {
/* 276 */       AtomicReference[] arrayOfAtomicReference = new AtomicReference[i];
/* 277 */       System.arraycopy(this.buffers, 0, arrayOfAtomicReference, 0, 
/* 278 */           Math.min(this.buffers.length, arrayOfAtomicReference.length));
/* 279 */       for (int j = this.buffers.length; j < i; j++) {
/* 280 */         arrayOfAtomicReference[j] = new AtomicReference<>(COMPRESSED_EMPTY_BLOCK);
/*     */       }
/* 282 */       this.buffers = (AtomicReference<ByteBuffer>[])arrayOfAtomicReference;
/*     */     } 
/* 284 */     this.compressLaterCache.setCacheSize(Math.max(8, (int)(i * this.compressLaterCachePercent / 100.0F)));
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
/*     */   long readWrite(long paramLong, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 300 */     Lock lock = (Lock)(paramBoolean ? this.rwLock.writeLock() : this.rwLock.readLock());
/* 301 */     lock.lock();
/*     */     
/*     */     try {
/* 304 */       long l = paramLong + paramInt2;
/* 305 */       if (l > this.length) {
/* 306 */         if (paramBoolean) {
/* 307 */           changeLength(l);
/*     */         } else {
/* 309 */           paramInt2 = (int)(this.length - paramLong);
/*     */         } 
/*     */       }
/* 312 */       while (paramInt2 > 0) {
/* 313 */         int i = (int)Math.min(paramInt2, 65536L - (paramLong & 0xFFFFL));
/* 314 */         int j = (int)(paramLong >>> 16L);
/* 315 */         ByteBuffer byteBuffer = expandPage(j);
/* 316 */         int k = (int)(paramLong & 0xFFFFL);
/* 317 */         if (paramBoolean) {
/* 318 */           ByteBuffer byteBuffer1 = paramByteBuffer.slice();
/* 319 */           ByteBuffer byteBuffer2 = byteBuffer.duplicate();
/* 320 */           byteBuffer1.position(paramInt1);
/* 321 */           byteBuffer1.limit(paramInt1 + i);
/* 322 */           byteBuffer2.position(k);
/* 323 */           byteBuffer2.put(byteBuffer1);
/*     */         } else {
/*     */           
/* 326 */           ByteBuffer byteBuffer1 = byteBuffer.duplicate();
/* 327 */           byteBuffer1.position(k);
/* 328 */           byteBuffer1.limit(i + k);
/* 329 */           int m = paramByteBuffer.position();
/* 330 */           paramByteBuffer.position(paramInt1);
/* 331 */           paramByteBuffer.put(byteBuffer1);
/*     */           
/* 333 */           paramByteBuffer.position(m);
/*     */         } 
/* 335 */         if (this.compress) {
/* 336 */           addToCompressLaterCache(j);
/*     */         }
/* 338 */         paramInt1 += i;
/* 339 */         paramLong += i;
/* 340 */         paramInt2 -= i;
/*     */       } 
/* 342 */       return paramLong;
/*     */     } finally {
/* 344 */       lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setName(String paramString) {
/* 354 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getName() {
/* 363 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getLastModified() {
/* 372 */     return this.lastModified;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean canWrite() {
/* 381 */     return !this.isReadOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean setReadOnly() {
/* 390 */     this.isReadOnly = true;
/* 391 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\niomem\FileNioMemData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */