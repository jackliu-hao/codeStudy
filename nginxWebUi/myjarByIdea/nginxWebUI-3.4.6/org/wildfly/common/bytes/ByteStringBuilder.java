package org.wildfly.common.bytes;

import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Mac;
import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.iteration.ByteIterator;
import org.wildfly.common.iteration.CodePointIterator;

public final class ByteStringBuilder {
   private byte[] content;
   private int length;

   public ByteStringBuilder() {
      this.content = new byte[16];
   }

   public ByteStringBuilder(byte[] content) {
      if (content != null && content.length != 0) {
         this.content = (byte[])content.clone();
         this.length = this.content.length;
      } else {
         this.content = new byte[16];
      }

   }

   public ByteStringBuilder append(boolean b) {
      this.appendLatin1(Boolean.toString(b));
      return this;
   }

   public ByteStringBuilder append(byte b) {
      this.doAppend(b);
      return this;
   }

   public ByteStringBuilder append(char c) {
      return this.appendUtf8Raw(c);
   }

   public ByteStringBuilder appendUtf8Raw(int codePoint) {
      if (codePoint < 0) {
         throw new IllegalArgumentException();
      } else {
         if (codePoint < 128) {
            this.doAppend((byte)codePoint);
         } else if (codePoint < 2048) {
            this.doAppend((byte)(192 | 31 & codePoint >>> 6));
            this.doAppend((byte)(128 | 63 & codePoint));
         } else if (codePoint < 65536) {
            this.doAppend((byte)(224 | 15 & codePoint >>> 12));
            this.doAppend((byte)(128 | 63 & codePoint >>> 6));
            this.doAppend((byte)(128 | 63 & codePoint));
         } else {
            if (codePoint >= 1114112) {
               throw new IllegalArgumentException();
            }

            this.doAppend((byte)(240 | 7 & codePoint >>> 18));
            this.doAppend((byte)(128 | 63 & codePoint >>> 12));
            this.doAppend((byte)(128 | 63 & codePoint >>> 6));
            this.doAppend((byte)(128 | 63 & codePoint));
         }

         return this;
      }
   }

   public ByteStringBuilder appendUtf8(CodePointIterator iterator) {
      while(iterator.hasNext()) {
         this.appendUtf8Raw(iterator.next());
      }

      return this;
   }

   public ByteStringBuilder appendLatin1(CodePointIterator iterator) {
      while(iterator.hasNext()) {
         int cp = iterator.next();
         if (cp > 255) {
            throw new IllegalArgumentException();
         }

         this.append((byte)cp);
      }

      return this;
   }

   public ByteStringBuilder appendAscii(CodePointIterator iterator) {
      while(iterator.hasNext()) {
         int cp = iterator.next();
         if (cp > 127) {
            throw new IllegalArgumentException();
         }

         this.append((byte)cp);
      }

      return this;
   }

   public ByteStringBuilder append(ByteIterator iterator) {
      return iterator.appendTo(this);
   }

   public ByteStringBuilder append(byte[] bytes) {
      int length = this.length;
      int bl = bytes.length;
      this.reserve(bl, false);
      System.arraycopy(bytes, 0, this.content, length, bl);
      this.length = length + bl;
      return this;
   }

   public ByteStringBuilder append(byte[] bytes, int offs, int len) {
      this.reserve(len, false);
      int length = this.length;
      System.arraycopy(bytes, offs, this.content, length, len);
      this.length = length + len;
      return this;
   }

   public ByteStringBuilder appendLatin1(CharSequence s) {
      int len = s.length();
      this.reserve(len, false);

      for(int i = 0; i < len; ++i) {
         char c = s.charAt(i);
         if (c > 255) {
            throw new IllegalArgumentException();
         }

         this.doAppendNoCheck((byte)c);
      }

      return this;
   }

   public ByteStringBuilder appendLatin1(CharSequence s, int offs, int len) {
      this.reserve(len, false);

      for(int i = 0; i < len; ++i) {
         char c = s.charAt(i + offs);
         if (c > 255) {
            throw new IllegalArgumentException();
         }

         this.doAppendNoCheck((byte)c);
      }

      return this;
   }

   public ByteStringBuilder appendLatin1(String s) {
      int len = s.length();
      this.reserve(len, false);

      for(int i = 0; i < len; ++i) {
         char c = s.charAt(i);
         if (c > 255) {
            throw new IllegalArgumentException();
         }

         this.doAppendNoCheck((byte)c);
      }

      return this;
   }

   public ByteStringBuilder appendLatin1(String s, int offs, int len) {
      this.reserve(len, false);

      for(int i = 0; i < len; ++i) {
         char c = s.charAt(i + offs);
         if (c > 255) {
            throw new IllegalArgumentException();
         }

         this.doAppendNoCheck((byte)c);
      }

      return this;
   }

   public ByteStringBuilder append(CharSequence s) {
      return this.append((CharSequence)s, 0, s.length());
   }

   public ByteStringBuilder append(CharSequence s, int offs, int len) {
      int c;
      for(int i = 0; i < len; this.appendUtf8Raw(c)) {
         c = s.charAt(offs + i++);
         if (Character.isHighSurrogate((char)c)) {
            if (i >= len) {
               throw new IllegalArgumentException();
            }

            char t = s.charAt(offs + i++);
            if (!Character.isLowSurrogate(t)) {
               throw new IllegalArgumentException();
            }

            c = Character.toCodePoint((char)c, t);
         }
      }

      return this;
   }

   public ByteStringBuilder append(String s) {
      return this.append((String)s, 0, s.length());
   }

   public ByteStringBuilder append(String s, int offs, int len) {
      int c;
      for(int i = 0; i < len; this.appendUtf8Raw(c)) {
         c = s.charAt(offs + i++);
         if (Character.isHighSurrogate((char)c)) {
            if (i >= len) {
               throw new IllegalArgumentException();
            }

            char t = s.charAt(offs + i++);
            if (!Character.isLowSurrogate(t)) {
               throw new IllegalArgumentException();
            }

            c = Character.toCodePoint((char)c, t);
         }
      }

      return this;
   }

   public ByteStringBuilder appendPackedUnsignedBE(int v) {
      if (v > 0) {
         int bits = Integer.numberOfTrailingZeros(Integer.highestOneBit(v)) + 1;
         int size = (bits + 7) / 7;
         int x = 0;

         for(int b = (size - 1) * 7; x < size - 1; b -= 7) {
            this.doAppend((byte)(128 | v >>> b));
            ++x;
         }
      }

      this.doAppend((byte)(-129 & v));
      return this;
   }

   public ByteStringBuilder appendPackedUnsignedBE(long v) {
      if (v > 0L) {
         int bits = Long.numberOfTrailingZeros(Long.highestOneBit(v)) + 1;
         int size = (bits + 7) / 7;
         int x = 0;

         for(int b = (size - 1) * 7; x < size - 1; b -= 7) {
            this.doAppend((byte)((int)(128L | v >>> b)));
            ++x;
         }
      }

      this.doAppend((byte)((int)(-129L & v)));
      return this;
   }

   public ByteStringBuilder appendBE(short s) {
      this.doAppend((byte)(s >>> 8));
      this.doAppend((byte)s);
      return this;
   }

   public ByteStringBuilder appendNumber(int i) {
      this.appendLatin1(Integer.toString(i));
      return this;
   }

   public ByteStringBuilder appendBE(int i) {
      this.doAppend((byte)(i >>> 24));
      this.doAppend((byte)(i >>> 16));
      this.doAppend((byte)(i >>> 8));
      this.doAppend((byte)i);
      return this;
   }

   public ByteStringBuilder appendNumber(long l) {
      this.appendLatin1(Long.toString(l));
      return this;
   }

   public ByteStringBuilder appendBE(long l) {
      this.doAppend((byte)((int)(l >>> 56)));
      this.doAppend((byte)((int)(l >>> 48)));
      this.doAppend((byte)((int)(l >>> 40)));
      this.doAppend((byte)((int)(l >>> 32)));
      this.doAppend((byte)((int)(l >>> 24)));
      this.doAppend((byte)((int)(l >>> 16)));
      this.doAppend((byte)((int)(l >>> 8)));
      this.doAppend((byte)((int)l));
      return this;
   }

   public ByteStringBuilder appendObject(Object o) {
      this.appendLatin1(String.valueOf(o));
      return this;
   }

   public ByteStringBuilder append(ByteStringBuilder other) {
      this.append((byte[])other.content, 0, other.length);
      return this;
   }

   public ByteStringBuilder updateDigest(MessageDigest messageDigest) {
      messageDigest.update(this.content, 0, this.length);
      return this;
   }

   public ByteStringBuilder appendDigestResult(MessageDigest messageDigest) throws DigestException {
      this.reserve(messageDigest.getDigestLength(), false);
      int length = this.length;
      byte[] content = this.content;
      this.length = length + messageDigest.digest(content, length, content.length - length);
      return this;
   }

   public ByteStringBuilder updateMac(Mac mac) {
      mac.update(this.content, 0, this.length);
      return this;
   }

   public byte[] toArray() {
      return Arrays.copyOf(this.content, this.length);
   }

   public byte byteAt(int index) {
      if (index >= 0 && index <= this.length) {
         return this.content[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int capacity() {
      return this.content.length;
   }

   public int length() {
      return this.length;
   }

   public void setLength(int newLength) {
      if (newLength > this.length) {
         this.reserve(newLength - this.length, true);
      }

      this.length = newLength;
   }

   public boolean contentEquals(byte[] other) {
      return this.contentEquals(other, 0, other.length);
   }

   public boolean contentEquals(byte[] other, int offs, int length) {
      if (length != this.length) {
         return false;
      } else {
         for(int i = 0; i < length; ++i) {
            if (this.content[i] != other[offs + i]) {
               return false;
            }
         }

         return true;
      }
   }

   private void reserve(int count, boolean clear) {
      int length = this.length;
      byte[] content = this.content;
      int cl = content.length;
      if (cl - length >= count) {
         if (clear) {
            Arrays.fill(content, length, length + count, (byte)0);
         }

      } else {
         if (clear) {
            Arrays.fill(content, length, cl, (byte)0);
         }

         do {
            cl += cl + 1 >> 1;
            if (cl < 0) {
               throw CommonMessages.msg.tooLarge();
            }
         } while(cl - length < count);

         this.content = Arrays.copyOf(content, cl);
      }
   }

   private void doAppend(byte b) {
      byte[] content = this.content;
      int cl = content.length;
      int length = this.length;
      if (length == cl) {
         content = this.content = Arrays.copyOf(content, cl + (cl + 1 >> 1));
      }

      content[length] = b;
      this.length = length + 1;
   }

   private void doAppendNoCheck(byte b) {
      this.content[this.length++] = b;
   }

   public ByteIterator iterate() {
      return ByteIterator.ofBytes(this.content, 0, this.length);
   }
}
