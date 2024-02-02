/*     */ package org.wildfly.common.bytes;
/*     */ 
/*     */ import java.security.DigestException;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Arrays;
/*     */ import javax.crypto.Mac;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ import org.wildfly.common.iteration.ByteIterator;
/*     */ import org.wildfly.common.iteration.CodePointIterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ByteStringBuilder
/*     */ {
/*     */   private byte[] content;
/*     */   private int length;
/*     */   
/*     */   public ByteStringBuilder() {
/*  40 */     this.content = new byte[16];
/*     */   }
/*     */   
/*     */   public ByteStringBuilder(byte[] content) {
/*  44 */     if (content != null && content.length != 0) {
/*  45 */       this.content = (byte[])content.clone();
/*  46 */       this.length = this.content.length;
/*     */     } else {
/*  48 */       this.content = new byte[16];
/*     */     } 
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(boolean b) {
/*  53 */     appendLatin1(Boolean.toString(b));
/*  54 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(byte b) {
/*  58 */     doAppend(b);
/*  59 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(char c) {
/*  63 */     return appendUtf8Raw(c);
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendUtf8Raw(int codePoint) {
/*  67 */     if (codePoint < 0)
/*  68 */       throw new IllegalArgumentException(); 
/*  69 */     if (codePoint < 128) {
/*  70 */       doAppend((byte)codePoint);
/*  71 */     } else if (codePoint < 2048) {
/*  72 */       doAppend((byte)(0xC0 | 0x1F & codePoint >>> 6));
/*  73 */       doAppend((byte)(0x80 | 0x3F & codePoint));
/*  74 */     } else if (codePoint < 65536) {
/*  75 */       doAppend((byte)(0xE0 | 0xF & codePoint >>> 12));
/*  76 */       doAppend((byte)(0x80 | 0x3F & codePoint >>> 6));
/*  77 */       doAppend((byte)(0x80 | 0x3F & codePoint));
/*  78 */     } else if (codePoint < 1114112) {
/*  79 */       doAppend((byte)(0xF0 | 0x7 & codePoint >>> 18));
/*  80 */       doAppend((byte)(0x80 | 0x3F & codePoint >>> 12));
/*  81 */       doAppend((byte)(0x80 | 0x3F & codePoint >>> 6));
/*  82 */       doAppend((byte)(0x80 | 0x3F & codePoint));
/*     */     } else {
/*  84 */       throw new IllegalArgumentException();
/*     */     } 
/*  86 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendUtf8(CodePointIterator iterator) {
/*  90 */     while (iterator.hasNext()) {
/*  91 */       appendUtf8Raw(iterator.next());
/*     */     }
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteStringBuilder appendLatin1(CodePointIterator iterator) {
/*  98 */     while (iterator.hasNext()) {
/*  99 */       int cp = iterator.next();
/* 100 */       if (cp > 255) throw new IllegalArgumentException(); 
/* 101 */       append((byte)cp);
/*     */     } 
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteStringBuilder appendAscii(CodePointIterator iterator) {
/* 108 */     while (iterator.hasNext()) {
/* 109 */       int cp = iterator.next();
/* 110 */       if (cp > 127) throw new IllegalArgumentException(); 
/* 111 */       append((byte)cp);
/*     */     } 
/* 113 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(ByteIterator iterator) {
/* 117 */     return iterator.appendTo(this);
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(byte[] bytes) {
/* 121 */     int length = this.length;
/* 122 */     int bl = bytes.length;
/* 123 */     reserve(bl, false);
/* 124 */     System.arraycopy(bytes, 0, this.content, length, bl);
/* 125 */     this.length = length + bl;
/* 126 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(byte[] bytes, int offs, int len) {
/* 130 */     reserve(len, false);
/* 131 */     int length = this.length;
/* 132 */     System.arraycopy(bytes, offs, this.content, length, len);
/* 133 */     this.length = length + len;
/* 134 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendLatin1(CharSequence s) {
/* 138 */     int len = s.length();
/* 139 */     reserve(len, false);
/*     */     
/* 141 */     for (int i = 0; i < len; i++) {
/* 142 */       char c = s.charAt(i);
/* 143 */       if (c > 'ÿ') throw new IllegalArgumentException(); 
/* 144 */       doAppendNoCheck((byte)c);
/*     */     } 
/* 146 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendLatin1(CharSequence s, int offs, int len) {
/* 150 */     reserve(len, false);
/*     */     
/* 152 */     for (int i = 0; i < len; i++) {
/* 153 */       char c = s.charAt(i + offs);
/* 154 */       if (c > 'ÿ') throw new IllegalArgumentException(); 
/* 155 */       doAppendNoCheck((byte)c);
/*     */     } 
/* 157 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendLatin1(String s) {
/* 161 */     int len = s.length();
/* 162 */     reserve(len, false);
/*     */     
/* 164 */     for (int i = 0; i < len; i++) {
/* 165 */       char c = s.charAt(i);
/* 166 */       if (c > 'ÿ') throw new IllegalArgumentException(); 
/* 167 */       doAppendNoCheck((byte)c);
/*     */     } 
/* 169 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendLatin1(String s, int offs, int len) {
/* 173 */     reserve(len, false);
/*     */     
/* 175 */     for (int i = 0; i < len; i++) {
/* 176 */       char c = s.charAt(i + offs);
/* 177 */       if (c > 'ÿ') throw new IllegalArgumentException(); 
/* 178 */       doAppendNoCheck((byte)c);
/*     */     } 
/* 180 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(CharSequence s) {
/* 184 */     return append(s, 0, s.length());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteStringBuilder append(CharSequence s, int offs, int len) {
/* 189 */     int i = 0;
/* 190 */     while (i < len) {
/* 191 */       int c = s.charAt(offs + i++);
/* 192 */       if (Character.isHighSurrogate((char)c)) {
/* 193 */         if (i < len) {
/* 194 */           char t = s.charAt(offs + i++);
/* 195 */           if (!Character.isLowSurrogate(t)) {
/* 196 */             throw new IllegalArgumentException();
/*     */           }
/* 198 */           c = Character.toCodePoint((char)c, t);
/*     */         } else {
/* 200 */           throw new IllegalArgumentException();
/*     */         } 
/*     */       }
/* 203 */       appendUtf8Raw(c);
/*     */     } 
/* 205 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(String s) {
/* 209 */     return append(s, 0, s.length());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteStringBuilder append(String s, int offs, int len) {
/* 214 */     int i = 0;
/* 215 */     while (i < len) {
/* 216 */       int c = s.charAt(offs + i++);
/* 217 */       if (Character.isHighSurrogate((char)c)) {
/* 218 */         if (i < len) {
/* 219 */           char t = s.charAt(offs + i++);
/* 220 */           if (!Character.isLowSurrogate(t)) {
/* 221 */             throw new IllegalArgumentException();
/*     */           }
/* 223 */           c = Character.toCodePoint((char)c, t);
/*     */         } else {
/* 225 */           throw new IllegalArgumentException();
/*     */         } 
/*     */       }
/* 228 */       appendUtf8Raw(c);
/*     */     } 
/* 230 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendPackedUnsignedBE(int v) {
/* 234 */     if (v > 0) {
/* 235 */       int bits = Integer.numberOfTrailingZeros(Integer.highestOneBit(v)) + 1;
/* 236 */       int size = (bits + 7) / 7;
/* 237 */       for (int x = 0, b = (size - 1) * 7; x < size - 1; x++, b -= 7) {
/* 238 */         doAppend((byte)(0x80 | v >>> b));
/*     */       }
/*     */     } 
/* 241 */     doAppend((byte)(0xFFFFFF7F & v));
/* 242 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendPackedUnsignedBE(long v) {
/* 246 */     if (v > 0L) {
/* 247 */       int bits = Long.numberOfTrailingZeros(Long.highestOneBit(v)) + 1;
/* 248 */       int size = (bits + 7) / 7;
/* 249 */       for (int x = 0, b = (size - 1) * 7; x < size - 1; x++, b -= 7) {
/* 250 */         doAppend((byte)(int)(0x80L | v >>> b));
/*     */       }
/*     */     } 
/* 253 */     doAppend((byte)(int)(0xFFFFFFFFFFFFFF7FL & v));
/* 254 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendBE(short s) {
/* 258 */     doAppend((byte)(s >>> 8));
/* 259 */     doAppend((byte)s);
/* 260 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendNumber(int i) {
/* 264 */     appendLatin1(Integer.toString(i));
/* 265 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendBE(int i) {
/* 269 */     doAppend((byte)(i >>> 24));
/* 270 */     doAppend((byte)(i >>> 16));
/* 271 */     doAppend((byte)(i >>> 8));
/* 272 */     doAppend((byte)i);
/* 273 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendNumber(long l) {
/* 277 */     appendLatin1(Long.toString(l));
/* 278 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendBE(long l) {
/* 282 */     doAppend((byte)(int)(l >>> 56L));
/* 283 */     doAppend((byte)(int)(l >>> 48L));
/* 284 */     doAppend((byte)(int)(l >>> 40L));
/* 285 */     doAppend((byte)(int)(l >>> 32L));
/* 286 */     doAppend((byte)(int)(l >>> 24L));
/* 287 */     doAppend((byte)(int)(l >>> 16L));
/* 288 */     doAppend((byte)(int)(l >>> 8L));
/* 289 */     doAppend((byte)(int)l);
/* 290 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendObject(Object o) {
/* 294 */     appendLatin1(String.valueOf(o));
/* 295 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder append(ByteStringBuilder other) {
/* 299 */     append(other.content, 0, other.length);
/* 300 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder updateDigest(MessageDigest messageDigest) {
/* 304 */     messageDigest.update(this.content, 0, this.length);
/* 305 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendDigestResult(MessageDigest messageDigest) throws DigestException {
/* 309 */     reserve(messageDigest.getDigestLength(), false);
/* 310 */     int length = this.length;
/* 311 */     byte[] content = this.content;
/* 312 */     this.length = length + messageDigest.digest(content, length, content.length - length);
/* 313 */     return this;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder updateMac(Mac mac) {
/* 317 */     mac.update(this.content, 0, this.length);
/* 318 */     return this;
/*     */   }
/*     */   
/*     */   public byte[] toArray() {
/* 322 */     return Arrays.copyOf(this.content, this.length);
/*     */   }
/*     */   
/*     */   public byte byteAt(int index) {
/* 326 */     if (index < 0 || index > this.length) throw new IndexOutOfBoundsException(); 
/* 327 */     return this.content[index];
/*     */   }
/*     */   
/*     */   public int capacity() {
/* 331 */     return this.content.length;
/*     */   }
/*     */   
/*     */   public int length() {
/* 335 */     return this.length;
/*     */   }
/*     */   
/*     */   public void setLength(int newLength) {
/* 339 */     if (newLength > this.length)
/*     */     {
/* 341 */       reserve(newLength - this.length, true);
/*     */     }
/* 343 */     this.length = newLength;
/*     */   }
/*     */   
/*     */   public boolean contentEquals(byte[] other) {
/* 347 */     return contentEquals(other, 0, other.length);
/*     */   }
/*     */   
/*     */   public boolean contentEquals(byte[] other, int offs, int length) {
/* 351 */     if (length != this.length) return false; 
/* 352 */     for (int i = 0; i < length; i++) {
/* 353 */       if (this.content[i] != other[offs + i]) {
/* 354 */         return false;
/*     */       }
/*     */     } 
/* 357 */     return true;
/*     */   }
/*     */   
/*     */   private void reserve(int count, boolean clear) {
/* 361 */     int length = this.length;
/* 362 */     byte[] content = this.content;
/* 363 */     int cl = content.length;
/* 364 */     if (cl - length >= count) {
/* 365 */       if (clear) Arrays.fill(content, length, length + count, (byte)0);
/*     */       
/*     */       return;
/*     */     } 
/* 369 */     if (clear) Arrays.fill(content, length, cl, (byte)0);
/*     */     
/*     */     while (true) {
/* 372 */       cl += cl + 1 >> 1;
/* 373 */       if (cl < 0) throw CommonMessages.msg.tooLarge(); 
/* 374 */       if (cl - length >= count) {
/* 375 */         this.content = Arrays.copyOf(content, cl);
/*     */         return;
/*     */       } 
/*     */     }  } private void doAppend(byte b) {
/* 379 */     byte[] content = this.content;
/* 380 */     int cl = content.length;
/* 381 */     int length = this.length;
/* 382 */     if (length == cl) {
/* 383 */       content = this.content = Arrays.copyOf(content, cl + (cl + 1 >> 1));
/*     */     }
/* 385 */     content[length] = b;
/* 386 */     this.length = length + 1;
/*     */   }
/*     */   
/*     */   private void doAppendNoCheck(byte b) {
/* 390 */     this.content[this.length++] = b;
/*     */   }
/*     */   
/*     */   public ByteIterator iterate() {
/* 394 */     return ByteIterator.ofBytes(this.content, 0, this.length);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\bytes\ByteStringBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */