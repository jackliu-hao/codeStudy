/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import org.xnio.BufferAllocator;
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
/*     */ public final class LimitedBufferSlicePool
/*     */ {
/*  41 */   private static final AtomicIntegerFieldUpdater regionUpdater = AtomicIntegerFieldUpdater.newUpdater(LimitedBufferSlicePool.class, "regionsUsed");
/*  42 */   private final Queue<Slice> sliceQueue = new ConcurrentLinkedQueue<>();
/*     */ 
/*     */   
/*     */   private final BufferAllocator<ByteBuffer> allocator;
/*     */ 
/*     */   
/*     */   private final int bufferSize;
/*     */ 
/*     */   
/*     */   private final int buffersPerRegion;
/*     */   
/*     */   private final int maxRegions;
/*     */   
/*     */   private volatile int regionsUsed;
/*     */ 
/*     */   
/*     */   public LimitedBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize, int maxRegions) {
/*  59 */     if (bufferSize <= 0) {
/*  60 */       throw new IllegalArgumentException("Buffer size must be greater than zero");
/*     */     }
/*  62 */     if (maxRegionSize < bufferSize) {
/*  63 */       throw new IllegalArgumentException("Maximum region size must be greater than or equal to the buffer size");
/*     */     }
/*  65 */     this.buffersPerRegion = maxRegionSize / bufferSize;
/*  66 */     this.bufferSize = bufferSize;
/*  67 */     this.allocator = allocator;
/*  68 */     this.maxRegions = maxRegions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LimitedBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize) {
/*  79 */     this(allocator, bufferSize, maxRegionSize, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LimitedBufferSlicePool(int bufferSize, int maxRegionSize) {
/*  90 */     this(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, bufferSize, maxRegionSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledByteBuffer allocate() {
/*  99 */     Queue<Slice> sliceQueue = this.sliceQueue;
/* 100 */     Slice slice = sliceQueue.poll();
/* 101 */     if (slice == null && (this.maxRegions <= 0 || regionUpdater.getAndIncrement((T)this) < this.maxRegions)) {
/* 102 */       int bufferSize = this.bufferSize;
/* 103 */       int buffersPerRegion = this.buffersPerRegion;
/* 104 */       ByteBuffer region = (ByteBuffer)this.allocator.allocate(buffersPerRegion * bufferSize);
/* 105 */       int idx = bufferSize;
/* 106 */       for (int i = 1; i < buffersPerRegion; i++) {
/* 107 */         sliceQueue.add(new Slice(region, idx, bufferSize));
/* 108 */         idx += bufferSize;
/*     */       } 
/* 110 */       Slice newSlice = new Slice(region, 0, bufferSize);
/* 111 */       return new PooledByteBuffer(newSlice, newSlice.slice(), sliceQueue);
/*     */     } 
/* 113 */     if (slice == null) {
/* 114 */       return null;
/*     */     }
/* 116 */     return new PooledByteBuffer(slice, slice.slice(), sliceQueue);
/*     */   }
/*     */   
/*     */   public boolean canAllocate(int slices) {
/* 120 */     if (this.regionsUsed < this.maxRegions) {
/* 121 */       return true;
/*     */     }
/* 123 */     if (this.sliceQueue.isEmpty()) {
/* 124 */       return false;
/*     */     }
/* 126 */     Iterator<Slice> iterator = this.sliceQueue.iterator();
/* 127 */     for (int i = 0; i < slices; i++) {
/* 128 */       if (!iterator.hasNext()) {
/* 129 */         return false;
/*     */       }
/*     */       try {
/* 132 */         iterator.next();
/* 133 */       } catch (NoSuchElementException e) {
/* 134 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 138 */     return true;
/*     */   }
/*     */   
/*     */   public static final class PooledByteBuffer
/*     */   {
/*     */     private final LimitedBufferSlicePool.Slice region;
/*     */     private final Queue<LimitedBufferSlicePool.Slice> slices;
/*     */     volatile ByteBuffer buffer;
/* 146 */     private static final AtomicReferenceFieldUpdater<PooledByteBuffer, ByteBuffer> bufferUpdater = AtomicReferenceFieldUpdater.newUpdater(PooledByteBuffer.class, ByteBuffer.class, "buffer");
/*     */     
/*     */     private PooledByteBuffer(LimitedBufferSlicePool.Slice region, ByteBuffer buffer, Queue<LimitedBufferSlicePool.Slice> slices) {
/* 149 */       this.region = region;
/* 150 */       this.buffer = buffer;
/* 151 */       this.slices = slices;
/*     */     }
/*     */     
/*     */     public void free() {
/* 155 */       if (bufferUpdater.getAndSet(this, null) != null)
/*     */       {
/* 157 */         this.slices.add(this.region);
/*     */       }
/*     */     }
/*     */     
/*     */     public ByteBuffer getBuffer() {
/* 162 */       ByteBuffer buffer = this.buffer;
/* 163 */       if (buffer == null) {
/* 164 */         throw new IllegalStateException();
/*     */       }
/* 166 */       return buffer;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 170 */       return "Pooled buffer " + this.buffer;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Slice {
/*     */     private final ByteBuffer parent;
/*     */     private final int start;
/*     */     private final int size;
/*     */     
/*     */     private Slice(ByteBuffer parent, int start, int size) {
/* 180 */       this.parent = parent;
/* 181 */       this.start = start;
/* 182 */       this.size = size;
/*     */     }
/*     */     
/*     */     ByteBuffer slice() {
/* 186 */       return ((ByteBuffer)this.parent.duplicate().position(this.start).limit(this.start + this.size)).slice();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\LimitedBufferSlicePool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */