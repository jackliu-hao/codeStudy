package org.wildfly.common.iteration;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.crypto.Mac;
import org.wildfly.common.Assert;
import org.wildfly.common.bytes.ByteStringBuilder;
import org.wildfly.common.codec.Base32Alphabet;
import org.wildfly.common.codec.Base64Alphabet;

public abstract class ByteIterator implements BiDirIntIterator, IndexIterator {
   private static final int OP_BUFFER_SIZE = 8192;
   private static final ThreadLocal<byte[]> OP_BUFFER = new ThreadLocal<byte[]>() {
      protected byte[] initialValue() {
         return new byte[8192];
      }
   };
   private static final byte[] NO_BYTES = new byte[0];
   public static final ByteIterator EMPTY;

   ByteIterator() {
   }

   public abstract boolean hasNext();

   public abstract boolean hasPrevious();

   public abstract int next() throws NoSuchElementException;

   public abstract int peekNext() throws NoSuchElementException;

   public abstract int previous() throws NoSuchElementException;

   public abstract int peekPrevious() throws NoSuchElementException;

   public abstract long getIndex();

   public int getBE16() throws NoSuchElementException {
      return this.next() << 8 | this.next();
   }

   public int getBE32() throws NoSuchElementException {
      return this.next() << 24 | this.next() << 16 | this.next() << 8 | this.next();
   }

   public long getBE64() throws NoSuchElementException {
      return (long)this.next() << 56 | (long)this.next() << 48 | (long)this.next() << 40 | (long)this.next() << 32 | (long)this.next() << 24 | (long)this.next() << 16 | (long)this.next() << 8 | (long)this.next();
   }

   public int getLE16() throws NoSuchElementException {
      return this.next() | this.next() << 8;
   }

   public int getLE32() throws NoSuchElementException {
      return this.next() | this.next() << 8 | this.next() << 16 | this.next() << 24;
   }

   public long getLE64() throws NoSuchElementException {
      return (long)this.next() | (long)this.next() << 8 | (long)this.next() << 16 | (long)this.next() << 24 | (long)this.next() << 32 | (long)this.next() << 40 | (long)this.next() << 48 | (long)this.next() << 56;
   }

   public int getPackedBE32() throws NoSuchElementException {
      int v = this.next();

      int t;
      for(t = 0; (v & 128) != 0; v = this.next()) {
         t = t << 7 | v & 127;
      }

      t = t << 7 | v;
      return t;
   }

   public long getPackedBE64() throws NoSuchElementException {
      int v = this.next();

      long t;
      for(t = 0L; (v & 128) != 0; v = this.next()) {
         t = t << 7 | (long)(v & 127);
      }

      t = t << 7 | (long)v;
      return t;
   }

   public ByteStringBuilder appendTo(ByteStringBuilder builder) {
      byte[] buffer = (byte[])OP_BUFFER.get();

      for(int cnt = this.drain(buffer); cnt > 0; cnt = this.drain(buffer)) {
         builder.append((byte[])buffer, 0, cnt);
      }

      return builder;
   }

   public void update(MessageDigest digest) {
      byte[] buffer = (byte[])OP_BUFFER.get();

      for(int cnt = this.drain(buffer); cnt > 0; cnt = this.drain(buffer)) {
         digest.update(buffer, 0, cnt);
      }

   }

   public ByteIterator doFinal(MessageDigest digest) {
      this.update(digest);
      return ofBytes(digest.digest());
   }

   public void update(Mac mac) {
      byte[] buffer = (byte[])OP_BUFFER.get();

      for(int cnt = this.drain(buffer); cnt > 0; cnt = this.drain(buffer)) {
         mac.update(buffer, 0, cnt);
      }

   }

   public ByteIterator doFinal(Mac mac) {
      return ofBytes(mac.doFinal(this.drain()));
   }

   public void update(Signature signature) throws SignatureException {
      byte[] buffer = (byte[])OP_BUFFER.get();

      for(int cnt = this.drain(buffer); cnt > 0; cnt = this.drain(buffer)) {
         signature.update(buffer, 0, cnt);
      }

      signature.update(this.drain());
   }

   public ByteIterator sign(Signature signature) throws SignatureException {
      this.update(signature);
      return ofBytes(signature.sign());
   }

   public boolean verify(Signature signature) throws SignatureException {
      byte[] buffer = (byte[])OP_BUFFER.get();

      for(int cnt = this.drain(buffer); cnt > 0; cnt = this.drain(buffer)) {
         signature.update(buffer, 0, cnt);
      }

      return signature.verify(NO_BYTES);
   }

   public CodePointIterator base64Encode(Base64Alphabet alphabet, boolean addPadding) {
      return (CodePointIterator)(alphabet.isLittleEndian() ? new LittleEndianBase64EncodingIterator(this, addPadding, alphabet) : new BigEndianBase64EncodingIterator(this, addPadding, alphabet));
   }

   public CodePointIterator base64Encode(Base64Alphabet alphabet) {
      return this.base64Encode(alphabet, true);
   }

   public CodePointIterator base64Encode() {
      return this.base64Encode(Base64Alphabet.STANDARD, true);
   }

   public CodePointIterator base32Encode(Base32Alphabet alphabet, boolean addPadding) {
      return (CodePointIterator)(alphabet.isLittleEndian() ? new LittleEndianBase32EncodingIterator(this, addPadding, alphabet) : new BigEndianBase32EncodingIterator(this, addPadding, alphabet));
   }

   public CodePointIterator base32Encode(Base32Alphabet alphabet) {
      return this.base32Encode(alphabet, true);
   }

   public CodePointIterator base32Encode() {
      return this.base32Encode(Base32Alphabet.STANDARD, true);
   }

   public CodePointIterator hexEncode(boolean toUpperCase) {
      return new Base16EncodingCodePointIterator(this, toUpperCase);
   }

   public CodePointIterator hexEncode() {
      return this.hexEncode(false);
   }

   public CodePointIterator asUtf8String() {
      return (CodePointIterator)(!this.hasNext() ? CodePointIterator.EMPTY : new Utf8DecodingIterator(this));
   }

   public CodePointIterator asLatin1String() {
      return (CodePointIterator)(!this.hasNext() ? CodePointIterator.EMPTY : new Latin1DecodingIterator(this, this.getIndex()));
   }

   public final boolean contentEquals(ByteIterator other) {
      Assert.checkNotNullParam("other", other);

      do {
         if (!this.hasNext()) {
            return !other.hasNext();
         }

         if (!other.hasNext()) {
            return false;
         }
      } while(this.next() == other.next());

      return false;
   }

   public final ByteIterator limitedTo(int size) {
      return (ByteIterator)(size > 0 && this.hasNext() ? new LimitedByteIterator(this, (long)size) : EMPTY);
   }

   public final ByteIterator delimitedBy(int... delims) {
      if (delims != null && delims.length != 0 && this.hasNext()) {
         int[] var2 = delims;
         int var3 = delims.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int delim = var2[var4];
            if (delim < 0 || delim > 255) {
               return EMPTY;
            }
         }

         return new DelimitedByteIterator(this, delims);
      } else {
         return EMPTY;
      }
   }

   public ByteIterator interleavedWith(byte[] table) {
      return new ByteTableTranslatingByteIterator(this, table);
   }

   public ByteIterator interleavedWith(int[] table) {
      return new IntTableTranslatingByteIterator(this, table);
   }

   public ByteArrayOutputStream drainTo(ByteArrayOutputStream stream) {
      while(this.hasNext()) {
         stream.write(this.next());
      }

      return stream;
   }

   public byte[] drain() {
      return this.drainTo(new ByteArrayOutputStream()).toByteArray();
   }

   public byte[] drain(int count) {
      if (count == 0) {
         return NO_BYTES;
      } else {
         byte[] b = new byte[count];
         int cnt = this.drain(b);
         return cnt == 0 ? NO_BYTES : (cnt < b.length ? Arrays.copyOf(b, cnt) : b);
      }
   }

   public byte[] drainAll(int count) throws NoSuchElementException {
      if (count == 0) {
         return NO_BYTES;
      } else {
         byte[] b = new byte[count];
         int cnt = this.drain(b);
         if (cnt < b.length) {
            throw new NoSuchElementException();
         } else {
            return b;
         }
      }
   }

   public int drain(byte[] dst) {
      return this.drain(dst, 0, dst.length);
   }

   public int drain(byte[] dst, int offs, int len) {
      for(int i = 0; i < len; ++i) {
         if (!this.hasNext()) {
            return i;
         }

         dst[offs + i] = (byte)this.next();
      }

      return len;
   }

   public String drainToUtf8(int count) {
      return new String(this.drain(count), StandardCharsets.UTF_8);
   }

   public String drainToLatin1(int count) {
      return new String(this.drain(count), StandardCharsets.ISO_8859_1);
   }

   public static ByteIterator ofBytes(byte... bytes) {
      Assert.checkNotNullParam("bytes", bytes);
      return ofBytes(bytes, 0, bytes.length);
   }

   public static ByteIterator ofBytes(byte[] bytes, int offs, int len) {
      Assert.checkNotNullParam("bytes", bytes);
      return (ByteIterator)(len <= 0 ? EMPTY : new ByteArrayIterator(len, bytes, offs));
   }

   public static ByteIterator ofBytes(byte[] bytes, int offs, int len, int[] interleave) {
      Assert.checkNotNullParam("bytes", bytes);
      Assert.checkNotNullParam("interleave", interleave);
      return (ByteIterator)(len <= 0 ? EMPTY : new InterleavedByteArrayIterator(len, bytes, offs, interleave));
   }

   public static ByteIterator ofBytes(byte[] bytes, int[] interleave) {
      return ofBytes(bytes, 0, bytes.length, interleave);
   }

   public static ByteIterator ofByteBuffer(ByteBuffer buffer) {
      Assert.checkNotNullParam("buffer", buffer);
      return new ByteBufferIterator(buffer);
   }

   public static ByteIterator ofIterators(ByteIterator... iterators) {
      Assert.checkNotNullParam("iterators", iterators);
      if (iterators.length == 0) {
         return EMPTY;
      } else {
         return (ByteIterator)(iterators.length == 1 ? iterators[0] : new ConcatByteIterator(iterators));
      }
   }

   public final InputStream asInputStream() {
      return new ByteIteratorInputStream(this);
   }

   static {
      EMPTY = new ByteArrayIterator(0, NO_BYTES, 0);
   }
}
