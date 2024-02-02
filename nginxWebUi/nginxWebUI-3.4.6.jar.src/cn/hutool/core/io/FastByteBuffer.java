/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FastByteBuffer
/*     */ {
/*  15 */   private byte[][] buffers = new byte[16][];
/*     */ 
/*     */ 
/*     */   
/*     */   private int buffersCount;
/*     */ 
/*     */ 
/*     */   
/*  23 */   private int currentBufferIndex = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] currentBuffer;
/*     */ 
/*     */ 
/*     */   
/*     */   private int offset;
/*     */ 
/*     */ 
/*     */   
/*     */   private int size;
/*     */ 
/*     */   
/*     */   private final int minChunkLen;
/*     */ 
/*     */ 
/*     */   
/*     */   public FastByteBuffer() {
/*  43 */     this(1024);
/*     */   }
/*     */   
/*     */   public FastByteBuffer(int size) {
/*  47 */     if (size <= 0) {
/*  48 */       size = 1024;
/*     */     }
/*  50 */     this.minChunkLen = Math.abs(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void needNewBuffer(int newSize) {
/*  59 */     int delta = newSize - this.size;
/*  60 */     int newBufferSize = Math.max(this.minChunkLen, delta);
/*     */     
/*  62 */     this.currentBufferIndex++;
/*  63 */     this.currentBuffer = new byte[newBufferSize];
/*  64 */     this.offset = 0;
/*     */ 
/*     */     
/*  67 */     if (this.currentBufferIndex >= this.buffers.length) {
/*  68 */       int newLen = this.buffers.length << 1;
/*  69 */       byte[][] newBuffers = new byte[newLen][];
/*  70 */       System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
/*  71 */       this.buffers = newBuffers;
/*     */     } 
/*  73 */     this.buffers[this.currentBufferIndex] = this.currentBuffer;
/*  74 */     this.buffersCount++;
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
/*     */   public FastByteBuffer append(byte[] array, int off, int len) {
/*  86 */     int end = off + len;
/*  87 */     if (off < 0 || len < 0 || end > array.length) {
/*  88 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  90 */     if (len == 0) {
/*  91 */       return this;
/*     */     }
/*  93 */     int newSize = this.size + len;
/*  94 */     int remaining = len;
/*     */     
/*  96 */     if (this.currentBuffer != null) {
/*     */       
/*  98 */       int part = Math.min(remaining, this.currentBuffer.length - this.offset);
/*  99 */       System.arraycopy(array, end - remaining, this.currentBuffer, this.offset, part);
/* 100 */       remaining -= part;
/* 101 */       this.offset += part;
/* 102 */       this.size += part;
/*     */     } 
/*     */     
/* 105 */     if (remaining > 0) {
/*     */ 
/*     */       
/* 108 */       needNewBuffer(newSize);
/*     */ 
/*     */ 
/*     */       
/* 112 */       int part = Math.min(remaining, this.currentBuffer.length - this.offset);
/* 113 */       System.arraycopy(array, end - remaining, this.currentBuffer, this.offset, part);
/* 114 */       this.offset += part;
/* 115 */       this.size += part;
/*     */     } 
/*     */     
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastByteBuffer append(byte[] array) {
/* 129 */     return append(array, 0, array.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastByteBuffer append(byte element) {
/* 139 */     if (this.currentBuffer == null || this.offset == this.currentBuffer.length) {
/* 140 */       needNewBuffer(this.size + 1);
/*     */     }
/*     */     
/* 143 */     this.currentBuffer[this.offset] = element;
/* 144 */     this.offset++;
/* 145 */     this.size++;
/*     */     
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastByteBuffer append(FastByteBuffer buff) {
/* 157 */     if (buff.size == 0) {
/* 158 */       return this;
/*     */     }
/* 160 */     for (int i = 0; i < buff.currentBufferIndex; i++) {
/* 161 */       append(buff.buffers[i]);
/*     */     }
/* 163 */     append(buff.currentBuffer, 0, buff.offset);
/* 164 */     return this;
/*     */   }
/*     */   
/*     */   public int size() {
/* 168 */     return this.size;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 172 */     return (this.size == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int index() {
/* 181 */     return this.currentBufferIndex;
/*     */   }
/*     */   
/*     */   public int offset() {
/* 185 */     return this.offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] array(int index) {
/* 195 */     return this.buffers[index];
/*     */   }
/*     */   
/*     */   public void reset() {
/* 199 */     this.size = 0;
/* 200 */     this.offset = 0;
/* 201 */     this.currentBufferIndex = -1;
/* 202 */     this.currentBuffer = null;
/* 203 */     this.buffersCount = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] toArray() {
/* 212 */     int pos = 0;
/* 213 */     byte[] array = new byte[this.size];
/*     */     
/* 215 */     if (this.currentBufferIndex == -1) {
/* 216 */       return array;
/*     */     }
/*     */     
/* 219 */     for (int i = 0; i < this.currentBufferIndex; i++) {
/* 220 */       int len = (this.buffers[i]).length;
/* 221 */       System.arraycopy(this.buffers[i], 0, array, pos, len);
/* 222 */       pos += len;
/*     */     } 
/*     */     
/* 225 */     System.arraycopy(this.buffers[this.currentBufferIndex], 0, array, pos, this.offset);
/*     */     
/* 227 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] toArray(int start, int len) {
/* 238 */     int remaining = len;
/* 239 */     int pos = 0;
/* 240 */     byte[] array = new byte[len];
/*     */     
/* 242 */     if (len == 0) {
/* 243 */       return array;
/*     */     }
/*     */     
/* 246 */     int i = 0;
/* 247 */     while (start >= (this.buffers[i]).length) {
/* 248 */       start -= (this.buffers[i]).length;
/* 249 */       i++;
/*     */     } 
/*     */     
/* 252 */     while (i < this.buffersCount) {
/* 253 */       byte[] buf = this.buffers[i];
/* 254 */       int c = Math.min(buf.length - start, remaining);
/* 255 */       System.arraycopy(buf, start, array, pos, c);
/* 256 */       pos += c;
/* 257 */       remaining -= c;
/* 258 */       if (remaining == 0) {
/*     */         break;
/*     */       }
/* 261 */       start = 0;
/* 262 */       i++;
/*     */     } 
/* 264 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte get(int index) {
/* 274 */     if (index >= this.size || index < 0) {
/* 275 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 277 */     int ndx = 0;
/*     */     while (true) {
/* 279 */       byte[] b = this.buffers[ndx];
/* 280 */       if (index < b.length) {
/* 281 */         return b[index];
/*     */       }
/* 283 */       ndx++;
/* 284 */       index -= b.length;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\FastByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */