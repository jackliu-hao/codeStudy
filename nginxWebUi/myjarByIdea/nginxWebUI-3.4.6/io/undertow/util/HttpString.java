package io.undertow.util;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class HttpString implements Comparable<HttpString>, Serializable {
   private static final long serialVersionUID = 1L;
   private final byte[] bytes;
   private final transient int hashCode;
   private final int orderInt;
   private transient String string;
   private static final Field hashCodeField;
   public static final HttpString EMPTY;

   public HttpString(byte[] bytes) {
      this((byte[])bytes.clone(), (String)null);
   }

   public HttpString(byte[] bytes, int offset, int length) {
      this(Arrays.copyOfRange(bytes, offset, offset + length), (String)null);
   }

   public HttpString(ByteBuffer buffer) {
      this(take(buffer), (String)null);
   }

   public HttpString(String string) {
      this(string, 0);
   }

   HttpString(String string, int orderInt) {
      this.orderInt = orderInt;
      this.bytes = toByteArray(string);
      this.hashCode = calcHashCode(this.bytes);
      this.string = string;
      this.checkForNewlines();
   }

   private static byte[] toByteArray(String string) {
      int len = string.length();
      byte[] bytes = new byte[len];

      for(int i = 0; i < len; ++i) {
         char c = string.charAt(i);
         if (c > 255) {
            throw new IllegalArgumentException("Invalid string contents " + string);
         }

         bytes[i] = (byte)c;
      }

      return bytes;
   }

   private void checkForNewlines() {
      byte[] var1 = this.bytes;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         byte b = var1[var3];
         if (b == 13 || b == 10) {
            throw UndertowMessages.MESSAGES.newlineNotSupportedInHttpString(this.string);
         }
      }

   }

   private HttpString(byte[] bytes, String string) {
      this.bytes = bytes;
      this.hashCode = calcHashCode(bytes);
      this.string = string;
      this.orderInt = 0;
      this.checkForNewlines();
   }

   public static HttpString tryFromString(String string) {
      HttpString cached = Headers.fromCache(string);
      if (cached != null) {
         return cached;
      } else {
         int len = string.length();
         byte[] bytes = new byte[len];

         for(int i = 0; i < len; ++i) {
            char c = string.charAt(i);
            if (c > 255) {
               return null;
            }

            bytes[i] = (byte)c;
         }

         return new HttpString(bytes, string);
      }
   }

   public int length() {
      return this.bytes.length;
   }

   public byte byteAt(int idx) {
      return this.bytes[idx];
   }

   public void copyTo(int srcOffs, byte[] dst, int offs, int len) {
      System.arraycopy(this.bytes, srcOffs, dst, offs, len);
   }

   public void copyTo(byte[] dst, int offs, int len) {
      this.copyTo(0, dst, offs, len);
   }

   public void copyTo(byte[] dst, int offs) {
      this.copyTo(dst, offs, this.bytes.length);
   }

   public void appendTo(ByteBuffer buffer) {
      buffer.put(this.bytes);
   }

   public void writeTo(OutputStream output) throws IOException {
      output.write(this.bytes);
   }

   private static byte[] take(ByteBuffer buffer) {
      byte[] bytes;
      if (buffer.hasArray()) {
         try {
            bytes = Arrays.copyOfRange(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
         } finally {
            buffer.position(buffer.limit());
         }

         return bytes;
      } else {
         bytes = new byte[buffer.remaining()];
         buffer.get(bytes);
         return bytes;
      }
   }

   public int compareTo(HttpString other) {
      if (this.orderInt != 0 && other.orderInt != 0) {
         return Integer.signum(this.orderInt - other.orderInt);
      } else {
         int len = Math.min(this.bytes.length, other.bytes.length);

         for(int i = 0; i < len; ++i) {
            int res = Integer.signum(higher(this.bytes[i]) - higher(other.bytes[i]));
            if (res != 0) {
               return res;
            }
         }

         return Integer.signum(this.bytes.length - other.bytes.length);
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!(other instanceof HttpString)) {
         return false;
      } else {
         HttpString otherString = (HttpString)other;
         return this.orderInt > 0 && otherString.orderInt > 0 ? false : bytesAreEqual(this.bytes, otherString.bytes);
      }
   }

   public boolean equals(HttpString other) {
      return other == this || other != null && bytesAreEqual(this.bytes, other.bytes);
   }

   private static int calcHashCode(byte[] bytes) {
      int hc = 17;
      byte[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         hc = (hc << 4) + hc + higher(b);
      }

      return hc;
   }

   private static int higher(byte b) {
      return b & (b >= 97 && b <= 122 ? 223 : 255);
   }

   private static boolean bytesAreEqual(byte[] a, byte[] b) {
      return a.length == b.length && bytesAreEquivalent(a, b);
   }

   private static boolean bytesAreEquivalent(byte[] a, byte[] b) {
      assert a.length == b.length;

      int len = a.length;

      for(int i = 0; i < len; ++i) {
         if (higher(a[i]) != higher(b[i])) {
            return false;
         }
      }

      return true;
   }

   public String toString() {
      if (this.string == null) {
         this.string = new String(this.bytes, StandardCharsets.US_ASCII);
      }

      return this.string;
   }

   private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
      ois.defaultReadObject();

      try {
         hashCodeField.setInt(this, calcHashCode(this.bytes));
      } catch (IllegalAccessException var3) {
         throw new IllegalAccessError(var3.getMessage());
      }
   }

   static int hashCodeOf(String headerName) {
      byte[] bytes = toByteArray(headerName);
      return calcHashCode(bytes);
   }

   public boolean equalToString(String headerName) {
      if (headerName.length() != this.bytes.length) {
         return false;
      } else {
         int len = this.bytes.length;

         for(int i = 0; i < len; ++i) {
            if (higher(this.bytes[i]) != higher((byte)headerName.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   static {
      try {
         hashCodeField = HttpString.class.getDeclaredField("hashCode");
         hashCodeField.setAccessible(true);
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }

      EMPTY = new HttpString("");
   }
}
