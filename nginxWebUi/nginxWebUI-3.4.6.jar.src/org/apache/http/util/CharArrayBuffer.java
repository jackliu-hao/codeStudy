/*     */ package org.apache.http.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.nio.CharBuffer;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CharArrayBuffer
/*     */   implements CharSequence, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6208952725094867135L;
/*     */   private char[] buffer;
/*     */   private int len;
/*     */   
/*     */   public CharArrayBuffer(int capacity) {
/*  55 */     Args.notNegative(capacity, "Buffer capacity");
/*  56 */     this.buffer = new char[capacity];
/*     */   }
/*     */   
/*     */   private void expand(int newlen) {
/*  60 */     char[] newbuffer = new char[Math.max(this.buffer.length << 1, newlen)];
/*  61 */     System.arraycopy(this.buffer, 0, newbuffer, 0, this.len);
/*  62 */     this.buffer = newbuffer;
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
/*     */   public void append(char[] b, int off, int len) {
/*  78 */     if (b == null) {
/*     */       return;
/*     */     }
/*  81 */     if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length)
/*     */     {
/*  83 */       throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
/*     */     }
/*  85 */     if (len == 0) {
/*     */       return;
/*     */     }
/*  88 */     int newlen = this.len + len;
/*  89 */     if (newlen > this.buffer.length) {
/*  90 */       expand(newlen);
/*     */     }
/*  92 */     System.arraycopy(b, off, this.buffer, this.len, len);
/*  93 */     this.len = newlen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String str) {
/* 103 */     String s = (str != null) ? str : "null";
/* 104 */     int strlen = s.length();
/* 105 */     int newlen = this.len + strlen;
/* 106 */     if (newlen > this.buffer.length) {
/* 107 */       expand(newlen);
/*     */     }
/* 109 */     s.getChars(0, strlen, this.buffer, this.len);
/* 110 */     this.len = newlen;
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
/*     */   public void append(CharArrayBuffer b, int off, int len) {
/* 127 */     if (b == null) {
/*     */       return;
/*     */     }
/* 130 */     append(b.buffer, off, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(CharArrayBuffer b) {
/* 141 */     if (b == null) {
/*     */       return;
/*     */     }
/* 144 */     append(b.buffer, 0, b.len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(char ch) {
/* 154 */     int newlen = this.len + 1;
/* 155 */     if (newlen > this.buffer.length) {
/* 156 */       expand(newlen);
/*     */     }
/* 158 */     this.buffer[this.len] = ch;
/* 159 */     this.len = newlen;
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
/*     */   public void append(byte[] b, int off, int len) {
/* 177 */     if (b == null) {
/*     */       return;
/*     */     }
/* 180 */     if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length)
/*     */     {
/* 182 */       throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
/*     */     }
/* 184 */     if (len == 0) {
/*     */       return;
/*     */     }
/* 187 */     int oldlen = this.len;
/* 188 */     int newlen = oldlen + len;
/* 189 */     if (newlen > this.buffer.length) {
/* 190 */       expand(newlen);
/*     */     }
/* 192 */     for (int i1 = off, i2 = oldlen; i2 < newlen; i1++, i2++) {
/* 193 */       this.buffer[i2] = (char)(b[i1] & 0xFF);
/*     */     }
/* 195 */     this.len = newlen;
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
/*     */   public void append(ByteArrayBuffer b, int off, int len) {
/* 213 */     if (b == null) {
/*     */       return;
/*     */     }
/* 216 */     append(b.buffer(), off, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(Object obj) {
/* 227 */     append(String.valueOf(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 234 */     this.len = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] toCharArray() {
/* 243 */     char[] b = new char[this.len];
/* 244 */     if (this.len > 0) {
/* 245 */       System.arraycopy(this.buffer, 0, b, 0, this.len);
/*     */     }
/* 247 */     return b;
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
/*     */   public char charAt(int i) {
/* 262 */     return this.buffer[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] buffer() {
/* 271 */     return this.buffer;
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
/* 282 */     return this.buffer.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 292 */     return this.len;
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
/*     */   public void ensureCapacity(int required) {
/* 304 */     if (required <= 0) {
/*     */       return;
/*     */     }
/* 307 */     int available = this.buffer.length - this.len;
/* 308 */     if (required > available) {
/* 309 */       expand(this.len + required);
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
/*     */   public void setLength(int len) {
/* 324 */     if (len < 0 || len > this.buffer.length) {
/* 325 */       throw new IndexOutOfBoundsException("len: " + len + " < 0 or > buffer len: " + this.buffer.length);
/*     */     }
/* 327 */     this.len = len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 337 */     return (this.len == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFull() {
/* 347 */     return (this.len == this.buffer.length);
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
/*     */   public int indexOf(int ch, int from, int to) {
/* 372 */     int beginIndex = from;
/* 373 */     if (beginIndex < 0) {
/* 374 */       beginIndex = 0;
/*     */     }
/* 376 */     int endIndex = to;
/* 377 */     if (endIndex > this.len) {
/* 378 */       endIndex = this.len;
/*     */     }
/* 380 */     if (beginIndex > endIndex) {
/* 381 */       return -1;
/*     */     }
/* 383 */     for (int i = beginIndex; i < endIndex; i++) {
/* 384 */       if (this.buffer[i] == ch) {
/* 385 */         return i;
/*     */       }
/*     */     } 
/* 388 */     return -1;
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
/*     */   public int indexOf(int ch) {
/* 402 */     return indexOf(ch, 0, this.len);
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
/*     */   public String substring(int beginIndex, int endIndex) {
/* 420 */     if (beginIndex < 0) {
/* 421 */       throw new IndexOutOfBoundsException("Negative beginIndex: " + beginIndex);
/*     */     }
/* 423 */     if (endIndex > this.len) {
/* 424 */       throw new IndexOutOfBoundsException("endIndex: " + endIndex + " > length: " + this.len);
/*     */     }
/* 426 */     if (beginIndex > endIndex) {
/* 427 */       throw new IndexOutOfBoundsException("beginIndex: " + beginIndex + " > endIndex: " + endIndex);
/*     */     }
/* 429 */     return new String(this.buffer, beginIndex, endIndex - beginIndex);
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
/*     */   public String substringTrimmed(int beginIndex, int endIndex) {
/* 449 */     if (beginIndex < 0) {
/* 450 */       throw new IndexOutOfBoundsException("Negative beginIndex: " + beginIndex);
/*     */     }
/* 452 */     if (endIndex > this.len) {
/* 453 */       throw new IndexOutOfBoundsException("endIndex: " + endIndex + " > length: " + this.len);
/*     */     }
/* 455 */     if (beginIndex > endIndex) {
/* 456 */       throw new IndexOutOfBoundsException("beginIndex: " + beginIndex + " > endIndex: " + endIndex);
/*     */     }
/* 458 */     int beginIndex0 = beginIndex;
/* 459 */     int endIndex0 = endIndex;
/* 460 */     while (beginIndex0 < endIndex && HTTP.isWhitespace(this.buffer[beginIndex0])) {
/* 461 */       beginIndex0++;
/*     */     }
/* 463 */     while (endIndex0 > beginIndex0 && HTTP.isWhitespace(this.buffer[endIndex0 - 1])) {
/* 464 */       endIndex0--;
/*     */     }
/* 466 */     return new String(this.buffer, beginIndex0, endIndex0 - beginIndex0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharSequence subSequence(int beginIndex, int endIndex) {
/* 475 */     if (beginIndex < 0) {
/* 476 */       throw new IndexOutOfBoundsException("Negative beginIndex: " + beginIndex);
/*     */     }
/* 478 */     if (endIndex > this.len) {
/* 479 */       throw new IndexOutOfBoundsException("endIndex: " + endIndex + " > length: " + this.len);
/*     */     }
/* 481 */     if (beginIndex > endIndex) {
/* 482 */       throw new IndexOutOfBoundsException("beginIndex: " + beginIndex + " > endIndex: " + endIndex);
/*     */     }
/* 484 */     return CharBuffer.wrap(this.buffer, beginIndex, endIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 489 */     return new String(this.buffer, 0, this.len);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\CharArrayBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */