/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.util.Arrays;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.crypto.Mac;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.bytes.ByteStringBuilder;
/*     */ import org.wildfly.common.codec.Base32Alphabet;
/*     */ import org.wildfly.common.codec.Base64Alphabet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ByteIterator
/*     */   implements BiDirIntIterator, IndexIterator
/*     */ {
/*     */   private static final int OP_BUFFER_SIZE = 8192;
/*     */   
/*  45 */   private static final ThreadLocal<byte[]> OP_BUFFER = new ThreadLocal<byte[]>() {
/*     */       protected byte[] initialValue() {
/*  47 */         return new byte[8192];
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean hasNext();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean hasPrevious();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int next() throws NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int peekNext() throws NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int previous() throws NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int peekPrevious() throws NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract long getIndex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBE16() throws NoSuchElementException {
/* 108 */     return next() << 8 | next();
/*     */   }
/*     */   
/*     */   public int getBE32() throws NoSuchElementException {
/* 112 */     return next() << 24 | next() << 16 | next() << 8 | next();
/*     */   }
/*     */   
/*     */   public long getBE64() throws NoSuchElementException {
/* 116 */     return next() << 56L | next() << 48L | next() << 40L | next() << 32L | next() << 24L | next() << 16L | next() << 8L | next();
/*     */   }
/*     */   
/*     */   public int getLE16() throws NoSuchElementException {
/* 120 */     return next() | next() << 8;
/*     */   }
/*     */   
/*     */   public int getLE32() throws NoSuchElementException {
/* 124 */     return next() | next() << 8 | next() << 16 | next() << 24;
/*     */   }
/*     */   
/*     */   public long getLE64() throws NoSuchElementException {
/* 128 */     return next() | next() << 8L | next() << 16L | next() << 24L | next() << 32L | next() << 40L | next() << 48L | next() << 56L;
/*     */   }
/*     */   
/*     */   public int getPackedBE32() throws NoSuchElementException {
/* 132 */     int v = next();
/* 133 */     int t = 0;
/* 134 */     while ((v & 0x80) != 0) {
/* 135 */       t = t << 7 | v & 0x7F;
/* 136 */       v = next();
/*     */     } 
/* 138 */     t = t << 7 | v;
/* 139 */     return t;
/*     */   }
/*     */   
/*     */   public long getPackedBE64() throws NoSuchElementException {
/* 143 */     int v = next();
/* 144 */     long t = 0L;
/* 145 */     while ((v & 0x80) != 0) {
/* 146 */       t = t << 7L | (v & 0x7F);
/* 147 */       v = next();
/*     */     } 
/* 149 */     t = t << 7L | v;
/* 150 */     return t;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendTo(ByteStringBuilder builder) {
/* 154 */     byte[] buffer = OP_BUFFER.get();
/* 155 */     int cnt = drain(buffer);
/* 156 */     while (cnt > 0) {
/* 157 */       builder.append(buffer, 0, cnt);
/* 158 */       cnt = drain(buffer);
/*     */     } 
/* 160 */     return builder;
/*     */   }
/*     */   
/*     */   public void update(MessageDigest digest) {
/* 164 */     byte[] buffer = OP_BUFFER.get();
/* 165 */     int cnt = drain(buffer);
/* 166 */     while (cnt > 0) {
/* 167 */       digest.update(buffer, 0, cnt);
/* 168 */       cnt = drain(buffer);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ByteIterator doFinal(MessageDigest digest) {
/* 173 */     update(digest);
/* 174 */     return ofBytes(digest.digest());
/*     */   }
/*     */   
/*     */   public void update(Mac mac) {
/* 178 */     byte[] buffer = OP_BUFFER.get();
/* 179 */     int cnt = drain(buffer);
/* 180 */     while (cnt > 0) {
/* 181 */       mac.update(buffer, 0, cnt);
/* 182 */       cnt = drain(buffer);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ByteIterator doFinal(Mac mac) {
/* 187 */     return ofBytes(mac.doFinal(drain()));
/*     */   }
/*     */   
/*     */   public void update(Signature signature) throws SignatureException {
/* 191 */     byte[] buffer = OP_BUFFER.get();
/* 192 */     int cnt = drain(buffer);
/* 193 */     while (cnt > 0) {
/* 194 */       signature.update(buffer, 0, cnt);
/* 195 */       cnt = drain(buffer);
/*     */     } 
/* 197 */     signature.update(drain());
/*     */   }
/*     */   
/*     */   public ByteIterator sign(Signature signature) throws SignatureException {
/* 201 */     update(signature);
/* 202 */     return ofBytes(signature.sign());
/*     */   }
/*     */   
/*     */   public boolean verify(Signature signature) throws SignatureException {
/* 206 */     byte[] buffer = OP_BUFFER.get();
/* 207 */     int cnt = drain(buffer);
/* 208 */     while (cnt > 0) {
/* 209 */       signature.update(buffer, 0, cnt);
/* 210 */       cnt = drain(buffer);
/*     */     } 
/* 212 */     return signature.verify(NO_BYTES);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator base64Encode(Base64Alphabet alphabet, boolean addPadding) {
/* 223 */     if (alphabet.isLittleEndian()) {
/* 224 */       return new LittleEndianBase64EncodingIterator(this, addPadding, alphabet);
/*     */     }
/* 226 */     return new BigEndianBase64EncodingIterator(this, addPadding, alphabet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator base64Encode(Base64Alphabet alphabet) {
/* 237 */     return base64Encode(alphabet, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator base64Encode() {
/* 246 */     return base64Encode(Base64Alphabet.STANDARD, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator base32Encode(Base32Alphabet alphabet, boolean addPadding) {
/* 257 */     if (alphabet.isLittleEndian()) {
/* 258 */       return new LittleEndianBase32EncodingIterator(this, addPadding, alphabet);
/*     */     }
/* 260 */     return new BigEndianBase32EncodingIterator(this, addPadding, alphabet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator base32Encode(Base32Alphabet alphabet) {
/* 271 */     return base32Encode(alphabet, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator base32Encode() {
/* 280 */     return base32Encode(Base32Alphabet.STANDARD, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator hexEncode(boolean toUpperCase) {
/* 291 */     return new Base16EncodingCodePointIterator(this, toUpperCase);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator hexEncode() {
/* 300 */     return hexEncode(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator asUtf8String() {
/* 309 */     if (!hasNext()) {
/* 310 */       return CodePointIterator.EMPTY;
/*     */     }
/* 312 */     return new Utf8DecodingIterator(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator asLatin1String() {
/* 321 */     if (!hasNext()) {
/* 322 */       return CodePointIterator.EMPTY;
/*     */     }
/* 324 */     return new Latin1DecodingIterator(this, getIndex());
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
/*     */   public final boolean contentEquals(ByteIterator other) {
/* 336 */     Assert.checkNotNullParam("other", other);
/*     */     
/* 338 */     while (hasNext()) {
/* 339 */       if (!other.hasNext()) {
/* 340 */         return false;
/*     */       }
/* 342 */       if (next() != other.next()) {
/* 343 */         return false;
/*     */       }
/*     */     } 
/* 346 */     return !other.hasNext();
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
/*     */   public final ByteIterator limitedTo(int size) {
/* 359 */     if (size <= 0 || !hasNext()) {
/* 360 */       return EMPTY;
/*     */     }
/* 362 */     return new LimitedByteIterator(this, size);
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
/*     */   public final ByteIterator delimitedBy(int... delims) {
/* 375 */     if (delims == null || delims.length == 0 || !hasNext()) {
/* 376 */       return EMPTY;
/*     */     }
/* 378 */     for (int delim : delims) {
/* 379 */       if (delim < 0 || delim > 255) {
/* 380 */         return EMPTY;
/*     */       }
/*     */     } 
/* 383 */     return new DelimitedByteIterator(this, delims);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator interleavedWith(byte[] table) {
/* 394 */     return new ByteTableTranslatingByteIterator(this, table);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator interleavedWith(int[] table) {
/* 405 */     return new IntTableTranslatingByteIterator(this, table);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArrayOutputStream drainTo(ByteArrayOutputStream stream) {
/* 415 */     while (hasNext()) {
/* 416 */       stream.write(next());
/*     */     }
/* 418 */     return stream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] drain() {
/* 427 */     return drainTo(new ByteArrayOutputStream()).toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] drain(int count) {
/* 437 */     if (count == 0) return NO_BYTES; 
/* 438 */     byte[] b = new byte[count];
/* 439 */     int cnt = drain(b);
/* 440 */     return (cnt == 0) ? NO_BYTES : ((cnt < b.length) ? Arrays.copyOf(b, cnt) : b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] drainAll(int count) throws NoSuchElementException {
/* 451 */     if (count == 0) return NO_BYTES; 
/* 452 */     byte[] b = new byte[count];
/* 453 */     int cnt = drain(b);
/* 454 */     if (cnt < b.length) {
/* 455 */       throw new NoSuchElementException();
/*     */     }
/* 457 */     return b;
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
/*     */   public int drain(byte[] dst) {
/* 472 */     return drain(dst, 0, dst.length);
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
/*     */   public int drain(byte[] dst, int offs, int len) {
/* 489 */     for (int i = 0; i < len; i++) {
/* 490 */       if (!hasNext()) return i; 
/* 491 */       dst[offs + i] = (byte)next();
/*     */     } 
/* 493 */     return len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String drainToUtf8(int count) {
/* 504 */     return new String(drain(count), StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String drainToLatin1(int count) {
/* 515 */     return new String(drain(count), StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteIterator ofBytes(byte... bytes) {
/* 525 */     Assert.checkNotNullParam("bytes", bytes);
/* 526 */     return ofBytes(bytes, 0, bytes.length);
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
/*     */   public static ByteIterator ofBytes(byte[] bytes, int offs, int len) {
/* 538 */     Assert.checkNotNullParam("bytes", bytes);
/* 539 */     if (len <= 0) {
/* 540 */       return EMPTY;
/*     */     }
/* 542 */     return new ByteArrayIterator(len, bytes, offs);
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
/*     */   public static ByteIterator ofBytes(byte[] bytes, int offs, int len, int[] interleave) {
/* 555 */     Assert.checkNotNullParam("bytes", bytes);
/* 556 */     Assert.checkNotNullParam("interleave", interleave);
/* 557 */     if (len <= 0) {
/* 558 */       return EMPTY;
/*     */     }
/* 560 */     return new InterleavedByteArrayIterator(len, bytes, offs, interleave);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteIterator ofBytes(byte[] bytes, int[] interleave) {
/* 571 */     return ofBytes(bytes, 0, bytes.length, interleave);
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
/*     */   public static ByteIterator ofByteBuffer(ByteBuffer buffer) {
/* 583 */     Assert.checkNotNullParam("buffer", buffer);
/* 584 */     return new ByteBufferIterator(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteIterator ofIterators(ByteIterator... iterators) {
/* 595 */     Assert.checkNotNullParam("iterators", iterators);
/* 596 */     if (iterators.length == 0) return EMPTY; 
/* 597 */     if (iterators.length == 1) return iterators[0]; 
/* 598 */     return new ConcatByteIterator(iterators);
/*     */   }
/*     */   
/* 601 */   private static final byte[] NO_BYTES = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 606 */   public static final ByteIterator EMPTY = new ByteArrayIterator(0, NO_BYTES, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final InputStream asInputStream() {
/* 614 */     return new ByteIteratorInputStream(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\ByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */