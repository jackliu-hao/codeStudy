/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.function.IntPredicate;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CodePointIterator
/*     */   implements BiDirIntIterator, IndexIterator
/*     */ {
/*     */   public final boolean contentEquals(CodePointIterator other) {
/*  99 */     while (hasNext()) {
/* 100 */       if (!other.hasNext()) {
/* 101 */         return false;
/*     */       }
/* 103 */       if (peekNext() != other.peekNext()) {
/* 104 */         return false;
/*     */       }
/* 106 */       next();
/* 107 */       other.next();
/*     */     } 
/* 109 */     return !other.hasNext();
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
/*     */   public boolean contentEquals(String other) {
/* 124 */     return contentEquals(ofString(other));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CodePointIterator limitedTo(long size) {
/* 135 */     if (size <= 0L || !hasNext()) {
/* 136 */       return EMPTY;
/*     */     }
/* 138 */     return new LimitedCodePointIterator(this, size);
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
/*     */   public final CodePointIterator delimitedBy(int... delims) {
/* 151 */     if (delims == null || delims.length == 0 || !hasNext()) {
/* 152 */       return EMPTY;
/*     */     }
/* 154 */     return new DelimitedCodePointIterator(this, delims);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder drainTo(StringBuilder b) {
/* 164 */     while (hasNext()) {
/* 165 */       b.appendCodePoint(next());
/*     */     }
/* 167 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator skipAll() {
/* 177 */     for (; hasNext(); next());
/* 178 */     return this;
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
/*     */   public StringBuilder drainTo(StringBuilder b, String prefix, int delim, int n) {
/* 193 */     int i = 0;
/* 194 */     boolean insertPrefix = (prefix != null);
/* 195 */     boolean insertDelim = Character.isValidCodePoint(delim);
/* 196 */     if (hasNext()) {
/* 197 */       if (insertPrefix) {
/* 198 */         b.append(prefix);
/*     */       }
/* 200 */       b.appendCodePoint(next());
/* 201 */       i++;
/* 202 */       while (hasNext()) {
/* 203 */         if (i == n) {
/* 204 */           if (insertDelim) {
/* 205 */             b.appendCodePoint(delim);
/*     */           }
/* 207 */           if (insertPrefix) {
/* 208 */             b.append(prefix);
/*     */           }
/* 210 */           b.appendCodePoint(next());
/* 211 */           i = 1; continue;
/*     */         } 
/* 213 */         b.appendCodePoint(next());
/* 214 */         i++;
/*     */       } 
/*     */     } 
/*     */     
/* 218 */     return b;
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
/*     */   public StringBuilder drainTo(StringBuilder b, int delim, int n) {
/* 231 */     return drainTo(b, null, delim, n);
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
/*     */   public StringBuilder drainTo(StringBuilder b, String prefix, int n) {
/* 244 */     return drainTo(b, prefix, -1, n);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String drainToString() {
/* 253 */     return hasNext() ? drainTo(new StringBuilder()).toString() : "";
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
/*     */   public String drainToString(String prefix, int delim, int n) {
/* 267 */     return hasNext() ? drainTo(new StringBuilder(), prefix, delim, n).toString() : "";
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
/*     */   public String drainToString(int delim, int n) {
/* 279 */     return hasNext() ? drainTo(new StringBuilder(), null, delim, n).toString() : "";
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
/*     */   public String drainToString(String prefix, int n) {
/* 291 */     return hasNext() ? drainTo(new StringBuilder(), prefix, -1, n).toString() : "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator base64Decode(Base64Alphabet alphabet, boolean requirePadding) {
/* 302 */     if (!hasNext()) return ByteIterator.EMPTY; 
/* 303 */     if (alphabet.isLittleEndian()) {
/* 304 */       return new LittleEndianBase64DecodingByteIterator(this, requirePadding, alphabet);
/*     */     }
/* 306 */     return new BigEndianBase64DecodingByteIterator(this, requirePadding, alphabet);
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
/*     */   public ByteIterator base32Decode(Base32Alphabet alphabet, boolean requirePadding) {
/* 318 */     if (!hasNext()) return ByteIterator.EMPTY; 
/* 319 */     if (alphabet.isLittleEndian()) {
/* 320 */       return new LittleEndianBase32DecodingByteIterator(this, requirePadding, alphabet);
/*     */     }
/* 322 */     return new BigEndianBase32DecodingByteIterator(this, requirePadding, alphabet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator hexDecode() {
/* 332 */     if (!hasNext()) return ByteIterator.EMPTY; 
/* 333 */     return new Base16DecodingByteIterator(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator base64Decode(Base64Alphabet alphabet) {
/* 343 */     return base64Decode(alphabet, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator base64Decode() {
/* 352 */     return base64Decode(Base64Alphabet.STANDARD, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator base32Decode(Base32Alphabet alphabet) {
/* 362 */     return base32Decode(alphabet, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator base32Decode() {
/* 371 */     return base32Decode(Base32Alphabet.STANDARD, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator asLatin1() {
/* 380 */     return new Latin1EncodingByteIterator(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator asUtf8() {
/* 389 */     return asUtf8(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteIterator asUtf8(boolean escapeNul) {
/* 399 */     return new Utf8EncodingByteIterator(this, escapeNul);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodePointIterator ofString(String string) {
/* 409 */     return ofString(string, 0, string.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodePointIterator ofString(String string, int offs, int len) {
/* 419 */     if (len == 0) {
/* 420 */       return EMPTY;
/*     */     }
/* 422 */     return new StringIterator(len, string, offs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodePointIterator ofChars(char[] chars) {
/* 432 */     return ofChars(chars, 0, chars.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodePointIterator ofChars(char[] chars, int offs) {
/* 443 */     return ofChars(chars, offs, chars.length - offs);
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
/*     */   public static CodePointIterator ofChars(char[] chars, int offs, int len) {
/* 455 */     if (len <= 0) {
/* 456 */       return EMPTY;
/*     */     }
/* 458 */     return new CharArrayIterator(len, chars, offs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodePointIterator ofUtf8Bytes(byte[] bytes) {
/* 468 */     return ofUtf8Bytes(bytes, 0, bytes.length);
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
/*     */   public static CodePointIterator ofUtf8Bytes(byte[] bytes, int offs, int len) {
/* 480 */     if (len <= 0) {
/* 481 */       return EMPTY;
/*     */     }
/* 483 */     return ByteIterator.ofBytes(bytes, offs, len).asUtf8String();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodePointIterator ofLatin1Bytes(byte[] bytes) {
/* 493 */     return ofLatin1Bytes(bytes, 0, bytes.length);
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
/*     */   public static CodePointIterator ofLatin1Bytes(byte[] bytes, int offs, int len) {
/* 505 */     if (len <= 0) {
/* 506 */       return EMPTY;
/*     */     }
/* 508 */     return ByteIterator.ofBytes(bytes, offs, len).asLatin1String();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator skipCrLf() {
/* 517 */     return skip(value -> (value == 10 || value == 13));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodePointIterator skip(IntPredicate predicate) {
/* 528 */     if (!hasNext()) {
/* 529 */       return EMPTY;
/*     */     }
/* 531 */     return new SkippingCodePointIterator(this, predicate);
/*     */   }
/*     */   
/* 534 */   private static final char[] NO_CHARS = new char[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 539 */   public static final CodePointIterator EMPTY = new CharArrayIterator(0, NO_CHARS, 0);
/*     */   
/*     */   public abstract boolean hasNext();
/*     */   
/*     */   public abstract boolean hasPrevious();
/*     */   
/*     */   public abstract int next() throws NoSuchElementException;
/*     */   
/*     */   public abstract int peekNext() throws NoSuchElementException;
/*     */   
/*     */   public abstract int previous() throws NoSuchElementException;
/*     */   
/*     */   public abstract int peekPrevious() throws NoSuchElementException;
/*     */   
/*     */   public abstract long getIndex();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\CodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */