package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import java.util.function.IntPredicate;
import org.wildfly.common.codec.Base32Alphabet;
import org.wildfly.common.codec.Base64Alphabet;

public abstract class CodePointIterator implements BiDirIntIterator, IndexIterator {
   private static final char[] NO_CHARS = new char[0];
   public static final CodePointIterator EMPTY;

   CodePointIterator() {
   }

   public abstract boolean hasNext();

   public abstract boolean hasPrevious();

   public abstract int next() throws NoSuchElementException;

   public abstract int peekNext() throws NoSuchElementException;

   public abstract int previous() throws NoSuchElementException;

   public abstract int peekPrevious() throws NoSuchElementException;

   public abstract long getIndex();

   public final boolean contentEquals(CodePointIterator other) {
      while(this.hasNext()) {
         if (!other.hasNext()) {
            return false;
         }

         if (this.peekNext() != other.peekNext()) {
            return false;
         }

         this.next();
         other.next();
      }

      return !other.hasNext();
   }

   public boolean contentEquals(String other) {
      return this.contentEquals(ofString(other));
   }

   public final CodePointIterator limitedTo(long size) {
      return (CodePointIterator)(size > 0L && this.hasNext() ? new LimitedCodePointIterator(this, size) : EMPTY);
   }

   public final CodePointIterator delimitedBy(int... delims) {
      return (CodePointIterator)(delims != null && delims.length != 0 && this.hasNext() ? new DelimitedCodePointIterator(this, delims) : EMPTY);
   }

   public StringBuilder drainTo(StringBuilder b) {
      while(this.hasNext()) {
         b.appendCodePoint(this.next());
      }

      return b;
   }

   public CodePointIterator skipAll() {
      while(this.hasNext()) {
         this.next();
      }

      return this;
   }

   public StringBuilder drainTo(StringBuilder b, String prefix, int delim, int n) {
      int i = 0;
      boolean insertPrefix = prefix != null;
      boolean insertDelim = Character.isValidCodePoint(delim);
      if (this.hasNext()) {
         if (insertPrefix) {
            b.append(prefix);
         }

         b.appendCodePoint(this.next());
         ++i;

         while(this.hasNext()) {
            if (i == n) {
               if (insertDelim) {
                  b.appendCodePoint(delim);
               }

               if (insertPrefix) {
                  b.append(prefix);
               }

               b.appendCodePoint(this.next());
               i = 1;
            } else {
               b.appendCodePoint(this.next());
               ++i;
            }
         }
      }

      return b;
   }

   public StringBuilder drainTo(StringBuilder b, int delim, int n) {
      return this.drainTo(b, (String)null, delim, n);
   }

   public StringBuilder drainTo(StringBuilder b, String prefix, int n) {
      return this.drainTo(b, prefix, -1, n);
   }

   public String drainToString() {
      return this.hasNext() ? this.drainTo(new StringBuilder()).toString() : "";
   }

   public String drainToString(String prefix, int delim, int n) {
      return this.hasNext() ? this.drainTo(new StringBuilder(), prefix, delim, n).toString() : "";
   }

   public String drainToString(int delim, int n) {
      return this.hasNext() ? this.drainTo(new StringBuilder(), (String)null, delim, n).toString() : "";
   }

   public String drainToString(String prefix, int n) {
      return this.hasNext() ? this.drainTo(new StringBuilder(), prefix, -1, n).toString() : "";
   }

   public ByteIterator base64Decode(Base64Alphabet alphabet, boolean requirePadding) {
      if (!this.hasNext()) {
         return ByteIterator.EMPTY;
      } else {
         return (ByteIterator)(alphabet.isLittleEndian() ? new LittleEndianBase64DecodingByteIterator(this, requirePadding, alphabet) : new BigEndianBase64DecodingByteIterator(this, requirePadding, alphabet));
      }
   }

   public ByteIterator base32Decode(Base32Alphabet alphabet, boolean requirePadding) {
      if (!this.hasNext()) {
         return ByteIterator.EMPTY;
      } else {
         return (ByteIterator)(alphabet.isLittleEndian() ? new LittleEndianBase32DecodingByteIterator(this, requirePadding, alphabet) : new BigEndianBase32DecodingByteIterator(this, requirePadding, alphabet));
      }
   }

   public ByteIterator hexDecode() {
      return (ByteIterator)(!this.hasNext() ? ByteIterator.EMPTY : new Base16DecodingByteIterator(this));
   }

   public ByteIterator base64Decode(Base64Alphabet alphabet) {
      return this.base64Decode(alphabet, true);
   }

   public ByteIterator base64Decode() {
      return this.base64Decode(Base64Alphabet.STANDARD, true);
   }

   public ByteIterator base32Decode(Base32Alphabet alphabet) {
      return this.base32Decode(alphabet, true);
   }

   public ByteIterator base32Decode() {
      return this.base32Decode(Base32Alphabet.STANDARD, true);
   }

   public ByteIterator asLatin1() {
      return new Latin1EncodingByteIterator(this);
   }

   public ByteIterator asUtf8() {
      return this.asUtf8(false);
   }

   public ByteIterator asUtf8(boolean escapeNul) {
      return new Utf8EncodingByteIterator(this, escapeNul);
   }

   public static CodePointIterator ofString(String string) {
      return ofString(string, 0, string.length());
   }

   public static CodePointIterator ofString(String string, int offs, int len) {
      return (CodePointIterator)(len == 0 ? EMPTY : new StringIterator(len, string, offs));
   }

   public static CodePointIterator ofChars(char[] chars) {
      return ofChars(chars, 0, chars.length);
   }

   public static CodePointIterator ofChars(char[] chars, int offs) {
      return ofChars(chars, offs, chars.length - offs);
   }

   public static CodePointIterator ofChars(char[] chars, int offs, int len) {
      return (CodePointIterator)(len <= 0 ? EMPTY : new CharArrayIterator(len, chars, offs));
   }

   public static CodePointIterator ofUtf8Bytes(byte[] bytes) {
      return ofUtf8Bytes(bytes, 0, bytes.length);
   }

   public static CodePointIterator ofUtf8Bytes(byte[] bytes, int offs, int len) {
      return len <= 0 ? EMPTY : ByteIterator.ofBytes(bytes, offs, len).asUtf8String();
   }

   public static CodePointIterator ofLatin1Bytes(byte[] bytes) {
      return ofLatin1Bytes(bytes, 0, bytes.length);
   }

   public static CodePointIterator ofLatin1Bytes(byte[] bytes, int offs, int len) {
      return len <= 0 ? EMPTY : ByteIterator.ofBytes(bytes, offs, len).asLatin1String();
   }

   public CodePointIterator skipCrLf() {
      return this.skip((value) -> {
         return value == 10 || value == 13;
      });
   }

   public CodePointIterator skip(IntPredicate predicate) {
      return (CodePointIterator)(!this.hasNext() ? EMPTY : new SkippingCodePointIterator(this, predicate));
   }

   static {
      EMPTY = new CharArrayIterator(0, NO_CHARS, 0);
   }
}
