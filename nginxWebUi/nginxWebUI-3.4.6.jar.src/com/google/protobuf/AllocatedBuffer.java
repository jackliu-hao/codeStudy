/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class AllocatedBuffer
/*     */ {
/*     */   public abstract boolean hasNioBuffer();
/*     */   
/*     */   public abstract boolean hasArray();
/*     */   
/*     */   public abstract ByteBuffer nioBuffer();
/*     */   
/*     */   public abstract byte[] array();
/*     */   
/*     */   public abstract int arrayOffset();
/*     */   
/*     */   public abstract int position();
/*     */   
/*     */   public abstract AllocatedBuffer position(int paramInt);
/*     */   
/*     */   public abstract int limit();
/*     */   
/*     */   public abstract int remaining();
/*     */   
/*     */   public static AllocatedBuffer wrap(byte[] bytes) {
/* 132 */     return wrapNoCheck(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AllocatedBuffer wrap(byte[] bytes, int offset, int length) {
/* 141 */     if (offset < 0 || length < 0 || offset + length > bytes.length) {
/* 142 */       throw new IndexOutOfBoundsException(
/* 143 */           String.format("bytes.length=%d, offset=%d, length=%d", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(offset), Integer.valueOf(length) }));
/*     */     }
/*     */     
/* 146 */     return wrapNoCheck(bytes, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AllocatedBuffer wrap(final ByteBuffer buffer) {
/* 154 */     Internal.checkNotNull(buffer, "buffer");
/*     */     
/* 156 */     return new AllocatedBuffer()
/*     */       {
/*     */         public boolean hasNioBuffer()
/*     */         {
/* 160 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public ByteBuffer nioBuffer() {
/* 165 */           return buffer;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean hasArray() {
/* 170 */           return buffer.hasArray();
/*     */         }
/*     */ 
/*     */         
/*     */         public byte[] array() {
/* 175 */           return buffer.array();
/*     */         }
/*     */ 
/*     */         
/*     */         public int arrayOffset() {
/* 180 */           return buffer.arrayOffset();
/*     */         }
/*     */ 
/*     */         
/*     */         public int position() {
/* 185 */           return buffer.position();
/*     */         }
/*     */ 
/*     */         
/*     */         public AllocatedBuffer position(int position) {
/* 190 */           buffer.position(position);
/* 191 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public int limit() {
/* 196 */           return buffer.limit();
/*     */         }
/*     */ 
/*     */         
/*     */         public int remaining() {
/* 201 */           return buffer.remaining();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private static AllocatedBuffer wrapNoCheck(final byte[] bytes, final int offset, final int length) {
/* 208 */     return new AllocatedBuffer()
/*     */       {
/*     */         private int position;
/*     */ 
/*     */         
/*     */         public boolean hasNioBuffer() {
/* 214 */           return false;
/*     */         }
/*     */ 
/*     */         
/*     */         public ByteBuffer nioBuffer() {
/* 219 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean hasArray() {
/* 224 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public byte[] array() {
/* 229 */           return bytes;
/*     */         }
/*     */ 
/*     */         
/*     */         public int arrayOffset() {
/* 234 */           return offset;
/*     */         }
/*     */ 
/*     */         
/*     */         public int position() {
/* 239 */           return this.position;
/*     */         }
/*     */ 
/*     */         
/*     */         public AllocatedBuffer position(int position) {
/* 244 */           if (position < 0 || position > length) {
/* 245 */             throw new IllegalArgumentException("Invalid position: " + position);
/*     */           }
/* 247 */           this.position = position;
/* 248 */           return this;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public int limit() {
/* 254 */           return length;
/*     */         }
/*     */ 
/*     */         
/*     */         public int remaining() {
/* 259 */           return length - this.position;
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\AllocatedBuffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */