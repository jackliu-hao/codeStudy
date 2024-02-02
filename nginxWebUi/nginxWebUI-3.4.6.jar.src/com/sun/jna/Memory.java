/*     */ package com.sun.jna;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
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
/*     */ public class Memory
/*     */   extends Pointer
/*     */ {
/*  58 */   private static final Map<Memory, Reference<Memory>> allocatedMemory = Collections.synchronizedMap(new WeakHashMap<Memory, Reference<Memory>>());
/*     */   
/*  60 */   private static final WeakMemoryHolder buffers = new WeakMemoryHolder();
/*     */   
/*     */   protected long size;
/*     */ 
/*     */   
/*     */   public static void purge() {
/*  66 */     buffers.clean();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void disposeAll() {
/*  72 */     Collection<Memory> refs = new LinkedList<Memory>(allocatedMemory.keySet());
/*  73 */     for (Memory r : refs) {
/*  74 */       r.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class SharedMemory
/*     */     extends Memory
/*     */   {
/*     */     public SharedMemory(long offset, long size) {
/*  85 */       this.size = size;
/*  86 */       this.peer = Memory.this.peer + offset;
/*     */     }
/*     */ 
/*     */     
/*     */     protected synchronized void dispose() {
/*  91 */       this.peer = 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void boundsCheck(long off, long sz) {
/*  96 */       Memory.this.boundsCheck(this.peer - Memory.this.peer + off, sz);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 100 */       return super.toString() + " (shared from " + Memory.this.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Memory(long size) {
/* 110 */     this.size = size;
/* 111 */     if (size <= 0L) {
/* 112 */       throw new IllegalArgumentException("Allocation size must be greater than zero");
/*     */     }
/* 114 */     this.peer = malloc(size);
/* 115 */     if (this.peer == 0L) {
/* 116 */       throw new OutOfMemoryError("Cannot allocate " + size + " bytes");
/*     */     }
/* 118 */     allocatedMemory.put(this, new WeakReference<Memory>(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Memory() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pointer share(long offset) {
/* 133 */     return share(offset, size() - offset);
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
/*     */   public Pointer share(long offset, long sz) {
/* 145 */     boundsCheck(offset, sz);
/* 146 */     return new SharedMemory(offset, sz);
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
/*     */   public Memory align(int byteBoundary) {
/* 158 */     if (byteBoundary <= 0) {
/* 159 */       throw new IllegalArgumentException("Byte boundary must be positive: " + byteBoundary);
/*     */     }
/* 161 */     for (int i = 0; i < 32; i++) {
/* 162 */       if (byteBoundary == 1 << i) {
/* 163 */         long mask = byteBoundary - 1L ^ 0xFFFFFFFFFFFFFFFFL;
/*     */         
/* 165 */         if ((this.peer & mask) != this.peer) {
/* 166 */           long newPeer = this.peer + byteBoundary - 1L & mask;
/* 167 */           long newSize = this.peer + this.size - newPeer;
/* 168 */           if (newSize <= 0L) {
/* 169 */             throw new IllegalArgumentException("Insufficient memory to align to the requested boundary");
/*     */           }
/* 171 */           return (Memory)share(newPeer - this.peer, newSize);
/*     */         } 
/* 173 */         return this;
/*     */       } 
/*     */     } 
/* 176 */     throw new IllegalArgumentException("Byte boundary must be a power of two");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() {
/* 182 */     dispose();
/*     */   }
/*     */ 
/*     */   
/*     */   protected synchronized void dispose() {
/*     */     try {
/* 188 */       free(this.peer);
/*     */     } finally {
/* 190 */       allocatedMemory.remove(this);
/* 191 */       this.peer = 0L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 197 */     clear(this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean valid() {
/* 202 */     return (this.peer != 0L);
/*     */   }
/*     */   
/*     */   public long size() {
/* 206 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void boundsCheck(long off, long sz) {
/* 215 */     if (off < 0L) {
/* 216 */       throw new IndexOutOfBoundsException("Invalid offset: " + off);
/*     */     }
/* 218 */     if (off + sz > this.size) {
/* 219 */       String msg = "Bounds exceeds available space : size=" + this.size + ", offset=" + (off + sz);
/*     */       
/* 221 */       throw new IndexOutOfBoundsException(msg);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(long bOff, byte[] buf, int index, int length) {
/* 239 */     boundsCheck(bOff, length * 1L);
/* 240 */     super.read(bOff, buf, index, length);
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
/*     */   public void read(long bOff, short[] buf, int index, int length) {
/* 253 */     boundsCheck(bOff, length * 2L);
/* 254 */     super.read(bOff, buf, index, length);
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
/*     */   public void read(long bOff, char[] buf, int index, int length) {
/* 267 */     boundsCheck(bOff, length * 2L);
/* 268 */     super.read(bOff, buf, index, length);
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
/*     */   public void read(long bOff, int[] buf, int index, int length) {
/* 281 */     boundsCheck(bOff, length * 4L);
/* 282 */     super.read(bOff, buf, index, length);
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
/*     */   public void read(long bOff, long[] buf, int index, int length) {
/* 295 */     boundsCheck(bOff, length * 8L);
/* 296 */     super.read(bOff, buf, index, length);
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
/*     */   public void read(long bOff, float[] buf, int index, int length) {
/* 309 */     boundsCheck(bOff, length * 4L);
/* 310 */     super.read(bOff, buf, index, length);
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
/*     */   public void read(long bOff, double[] buf, int index, int length) {
/* 323 */     boundsCheck(bOff, length * 8L);
/* 324 */     super.read(bOff, buf, index, length);
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
/*     */   public void write(long bOff, byte[] buf, int index, int length) {
/* 341 */     boundsCheck(bOff, length * 1L);
/* 342 */     super.write(bOff, buf, index, length);
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
/*     */   public void write(long bOff, short[] buf, int index, int length) {
/* 355 */     boundsCheck(bOff, length * 2L);
/* 356 */     super.write(bOff, buf, index, length);
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
/*     */   public void write(long bOff, char[] buf, int index, int length) {
/* 369 */     boundsCheck(bOff, length * 2L);
/* 370 */     super.write(bOff, buf, index, length);
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
/*     */   public void write(long bOff, int[] buf, int index, int length) {
/* 383 */     boundsCheck(bOff, length * 4L);
/* 384 */     super.write(bOff, buf, index, length);
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
/*     */   public void write(long bOff, long[] buf, int index, int length) {
/* 397 */     boundsCheck(bOff, length * 8L);
/* 398 */     super.write(bOff, buf, index, length);
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
/*     */   public void write(long bOff, float[] buf, int index, int length) {
/* 411 */     boundsCheck(bOff, length * 4L);
/* 412 */     super.write(bOff, buf, index, length);
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
/*     */   public void write(long bOff, double[] buf, int index, int length) {
/* 425 */     boundsCheck(bOff, length * 8L);
/* 426 */     super.write(bOff, buf, index, length);
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
/*     */   public byte getByte(long offset) {
/* 443 */     boundsCheck(offset, 1L);
/* 444 */     return super.getByte(offset);
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
/*     */   public char getChar(long offset) {
/* 457 */     boundsCheck(offset, 1L);
/* 458 */     return super.getChar(offset);
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
/*     */   public short getShort(long offset) {
/* 471 */     boundsCheck(offset, 2L);
/* 472 */     return super.getShort(offset);
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
/*     */   public int getInt(long offset) {
/* 485 */     boundsCheck(offset, 4L);
/* 486 */     return super.getInt(offset);
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
/*     */   public long getLong(long offset) {
/* 499 */     boundsCheck(offset, 8L);
/* 500 */     return super.getLong(offset);
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
/*     */   public float getFloat(long offset) {
/* 513 */     boundsCheck(offset, 4L);
/* 514 */     return super.getFloat(offset);
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
/*     */   public double getDouble(long offset) {
/* 527 */     boundsCheck(offset, 8L);
/* 528 */     return super.getDouble(offset);
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
/*     */   public Pointer getPointer(long offset) {
/* 541 */     boundsCheck(offset, Native.POINTER_SIZE);
/* 542 */     return super.getPointer(offset);
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
/*     */   public ByteBuffer getByteBuffer(long offset, long length) {
/* 559 */     boundsCheck(offset, length);
/* 560 */     ByteBuffer b = super.getByteBuffer(offset, length);
/*     */ 
/*     */     
/* 563 */     buffers.put(b, this);
/* 564 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getString(long offset, String encoding) {
/* 570 */     boundsCheck(offset, 0L);
/* 571 */     return super.getString(offset, encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getWideString(long offset) {
/* 577 */     boundsCheck(offset, 0L);
/* 578 */     return super.getWideString(offset);
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
/*     */   public void setByte(long offset, byte value) {
/* 595 */     boundsCheck(offset, 1L);
/* 596 */     super.setByte(offset, value);
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
/*     */   public void setChar(long offset, char value) {
/* 609 */     boundsCheck(offset, Native.WCHAR_SIZE);
/* 610 */     super.setChar(offset, value);
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
/*     */   public void setShort(long offset, short value) {
/* 623 */     boundsCheck(offset, 2L);
/* 624 */     super.setShort(offset, value);
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
/*     */   public void setInt(long offset, int value) {
/* 637 */     boundsCheck(offset, 4L);
/* 638 */     super.setInt(offset, value);
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
/*     */   public void setLong(long offset, long value) {
/* 651 */     boundsCheck(offset, 8L);
/* 652 */     super.setLong(offset, value);
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
/*     */   public void setFloat(long offset, float value) {
/* 665 */     boundsCheck(offset, 4L);
/* 666 */     super.setFloat(offset, value);
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
/*     */   public void setDouble(long offset, double value) {
/* 679 */     boundsCheck(offset, 8L);
/* 680 */     super.setDouble(offset, value);
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
/*     */   public void setPointer(long offset, Pointer value) {
/* 693 */     boundsCheck(offset, Native.POINTER_SIZE);
/* 694 */     super.setPointer(offset, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setString(long offset, String value, String encoding) {
/* 699 */     boundsCheck(offset, (Native.getBytes(value, encoding)).length + 1L);
/* 700 */     super.setString(offset, value, encoding);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWideString(long offset, String value) {
/* 705 */     boundsCheck(offset, (value.length() + 1L) * Native.WCHAR_SIZE);
/* 706 */     super.setWideString(offset, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 711 */     return "allocated@0x" + Long.toHexString(this.peer) + " (" + this.size + " bytes)";
/*     */   }
/*     */ 
/*     */   
/*     */   protected static void free(long p) {
/* 716 */     if (p != 0L) {
/* 717 */       Native.free(p);
/*     */     }
/*     */   }
/*     */   
/*     */   protected static long malloc(long size) {
/* 722 */     return Native.malloc(size);
/*     */   }
/*     */ 
/*     */   
/*     */   public String dump() {
/* 727 */     return dump(0L, (int)size());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Memory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */