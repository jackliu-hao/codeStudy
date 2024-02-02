package org.wildfly.common.iteration;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.crypto.Mac;
import org.wildfly.common.bytes.ByteStringBuilder;

final class ByteArrayIterator extends ByteIterator {
   private final int len;
   private final byte[] bytes;
   private final int offs;
   private int idx;

   ByteArrayIterator(int len, byte[] bytes, int offs) {
      this.len = len;
      this.bytes = bytes;
      this.offs = offs;
      this.idx = 0;
   }

   public boolean hasNext() {
      return this.idx < this.len;
   }

   public boolean hasPrevious() {
      return this.idx > 0;
   }

   public int next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.idx++] & 255;
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + --this.idx] & 255;
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.idx] & 255;
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.idx - 1] & 255;
      }
   }

   public long getIndex() {
      return (long)this.idx;
   }

   public void update(MessageDigest digest) throws IllegalStateException {
      digest.update(this.bytes, this.offs + this.idx, this.len - this.idx);
      this.idx = this.len;
   }

   public ByteIterator doFinal(MessageDigest digest) throws IllegalStateException {
      this.update(digest);
      return ByteIterator.ofBytes(digest.digest());
   }

   public void update(Mac mac) throws IllegalStateException {
      mac.update(this.bytes, this.offs + this.idx, this.len - this.idx);
      this.idx = this.len;
   }

   public ByteIterator doFinal(Mac mac) throws IllegalStateException {
      this.update(mac);
      return ByteIterator.ofBytes(mac.doFinal());
   }

   public void update(Signature signature) throws SignatureException {
      signature.update(this.bytes, this.offs + this.idx, this.len - this.idx);
      this.idx = this.len;
   }

   public boolean verify(Signature signature) throws SignatureException {
      boolean var2;
      try {
         var2 = signature.verify(this.bytes, this.offs + this.idx, this.len - this.idx);
      } finally {
         this.idx = this.len;
      }

      return var2;
   }

   public ByteArrayOutputStream drainTo(ByteArrayOutputStream stream) {
      stream.write(this.bytes, this.offs + this.idx, this.len - this.idx);
      this.idx = this.len;
      return stream;
   }

   public byte[] drain() {
      byte[] var1;
      try {
         var1 = Arrays.copyOfRange(this.bytes, this.offs + this.idx, this.offs + this.len);
      } finally {
         this.idx = this.len;
      }

      return var1;
   }

   public int drain(byte[] dst, int offs, int dlen) {
      int cnt = Math.min(this.len - this.idx, dlen);
      System.arraycopy(this.bytes, offs + this.idx, dst, offs, cnt);
      this.idx += cnt;
      return cnt;
   }

   public String drainToUtf8(int count) {
      int cnt = Math.min(this.len - this.idx, count);
      String s = new String(this.bytes, this.idx, cnt, StandardCharsets.UTF_8);
      this.idx += cnt;
      return s;
   }

   public String drainToLatin1(int count) {
      int cnt = Math.min(this.len - this.idx, count);
      String s = new String(this.bytes, this.idx, cnt, StandardCharsets.ISO_8859_1);
      this.idx += cnt;
      return s;
   }

   public ByteStringBuilder appendTo(ByteStringBuilder builder) {
      builder.append(this.bytes, this.offs + this.idx, this.len - this.idx);
      this.idx = this.len;
      return builder;
   }
}
