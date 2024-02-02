/*     */ package org.apache.http.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ByteArrayBuffer
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 4359112959524048036L;
/*     */   private byte[] buffer;
/*     */   private int len;
/*     */   
/*     */   public ByteArrayBuffer(int capacity) {
/*  52 */     Args.notNegative(capacity, "Buffer capacity");
/*  53 */     this.buffer = new byte[capacity];
/*     */   }
/*     */   
/*     */   private void expand(int newlen) {
/*  57 */     byte[] newbuffer = new byte[Math.max(this.buffer.length << 1, newlen)];
/*  58 */     System.arraycopy(this.buffer, 0, newbuffer, 0, this.len);
/*  59 */     this.buffer = newbuffer;
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
/*     */   public void append(byte[] b, int off, int len) {
/*  75 */     if (b == null) {
/*     */       return;
/*     */     }
/*  78 */     if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length)
/*     */     {
/*  80 */       throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
/*     */     }
/*  82 */     if (len == 0) {
/*     */       return;
/*     */     }
/*  85 */     int newlen = this.len + len;
/*  86 */     if (newlen > this.buffer.length) {
/*  87 */       expand(newlen);
/*     */     }
/*  89 */     System.arraycopy(b, off, this.buffer, this.len, len);
/*  90 */     this.len = newlen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(int b) {
/* 100 */     int newlen = this.len + 1;
/* 101 */     if (newlen > this.buffer.length) {
/* 102 */       expand(newlen);
/*     */     }
/* 104 */     this.buffer[this.len] = (byte)b;
/* 105 */     this.len = newlen;
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
/*     */   public void append(char[] b, int off, int len) {
/* 123 */     if (b == null) {
/*     */       return;
/*     */     }
/* 126 */     if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length)
/*     */     {
/* 128 */       throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
/*     */     }
/* 130 */     if (len == 0) {
/*     */       return;
/*     */     }
/* 133 */     int oldlen = this.len;
/* 134 */     int newlen = oldlen + len;
/* 135 */     if (newlen > this.buffer.length) {
/* 136 */       expand(newlen);
/*     */     }
/*     */     
/* 139 */     for (int i1 = off, i2 = oldlen; i2 < newlen; i1++, i2++) {
/* 140 */       int c = b[i1];
/* 141 */       if ((c >= 32 && c <= 126) || (c >= 160 && c <= 255) || c == 9) {
/*     */ 
/*     */         
/* 144 */         this.buffer[i2] = (byte)c;
/*     */       } else {
/* 146 */         this.buffer[i2] = 63;
/*     */       } 
/*     */     } 
/* 149 */     this.len = newlen;
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
/*     */   public void append(CharArrayBuffer b, int off, int len) {
/* 168 */     if (b == null) {
/*     */       return;
/*     */     }
/* 171 */     append(b.buffer(), off, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 178 */     this.len = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] toByteArray() {
/* 187 */     byte[] b = new byte[this.len];
/* 188 */     if (this.len > 0) {
/* 189 */       System.arraycopy(this.buffer, 0, b, 0, this.len);
/*     */     }
/* 191 */     return b;
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
/*     */   public int byteAt(int i) {
/* 205 */     return this.buffer[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 216 */     return this.buffer.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 225 */     return this.len;
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
/*     */   public void ensureCapacity(int required) {
/* 239 */     if (required <= 0) {
/*     */       return;
/*     */     }
/* 242 */     int available = this.buffer.length - this.len;
/* 243 */     if (required > available) {
/* 244 */       expand(this.len + required);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] buffer() {
/* 254 */     return this.buffer;
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
/*     */   public void setLength(int len) {
/* 268 */     if (len < 0 || len > this.buffer.length) {
/* 269 */       throw new IndexOutOfBoundsException("len: " + len + " < 0 or > buffer len: " + this.buffer.length);
/*     */     }
/* 271 */     this.len = len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 281 */     return (this.len == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFull() {
/* 291 */     return (this.len == this.buffer.length);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int indexOf(byte b, int from, int to) {
/* 318 */     int beginIndex = from;
/* 319 */     if (beginIndex < 0) {
/* 320 */       beginIndex = 0;
/*     */     }
/* 322 */     int endIndex = to;
/* 323 */     if (endIndex > this.len) {
/* 324 */       endIndex = this.len;
/*     */     }
/* 326 */     if (beginIndex > endIndex) {
/* 327 */       return -1;
/*     */     }
/* 329 */     for (int i = beginIndex; i < endIndex; i++) {
/* 330 */       if (this.buffer[i] == b) {
/* 331 */         return i;
/*     */       }
/*     */     } 
/* 334 */     return -1;
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
/*     */   public int indexOf(byte b) {
/* 350 */     return indexOf(b, 0, this.len);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\ByteArrayBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */