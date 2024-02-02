package org.xnio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.xnio._private.Messages;

public final class ByteString implements Comparable<ByteString>, Serializable, CharSequence {
   private static final long serialVersionUID = -5998895518404718196L;
   private final byte[] bytes;
   private final int offs;
   private final int len;
   private transient int hashCode;
   private transient int hashCodeIgnoreCase;
   private static final ByteString ZERO = new ByteString(new byte[]{48});

   private ByteString(byte[] bytes, int offs, int len) {
      this.bytes = bytes;
      this.offs = offs;
      this.len = len;
      if (offs < 0) {
         throw Messages.msg.parameterOutOfRange("offs");
      } else if (len < 0) {
         throw Messages.msg.parameterOutOfRange("len");
      } else if (offs + len > bytes.length) {
         throw Messages.msg.parameterOutOfRange("offs");
      }
   }

   private static int calcHashCode(byte[] bytes, int offs, int len) {
      int hc = 31;
      int end = offs + len;

      for(int i = offs; i < end; ++i) {
         hc = (hc << 5) - hc + (bytes[i] & 255);
      }

      return hc == 0 ? Integer.MAX_VALUE : hc;
   }

   private static int calcHashCodeIgnoreCase(byte[] bytes, int offs, int len) {
      int hc = 31;
      int end = offs + len;

      for(int i = offs; i < end; ++i) {
         hc = (hc << 5) - hc + (upperCase(bytes[i]) & 255);
      }

      return hc == 0 ? Integer.MAX_VALUE : hc;
   }

   private ByteString(byte[] bytes) {
      this(bytes, 0, bytes.length);
   }

   public static ByteString of(byte... bytes) {
      return new ByteString((byte[])bytes.clone());
   }

   public static ByteString copyOf(byte[] b, int offs, int len) {
      return new ByteString(Arrays.copyOfRange(b, offs, offs + len));
   }

   public static ByteString getBytes(String str, String charset) throws UnsupportedEncodingException {
      return new ByteString(str.getBytes(charset));
   }

   public static ByteString getBytes(String str, Charset charset) {
      return new ByteString(str.getBytes(charset));
   }

   public static ByteString getBytes(String str) {
      int length = str.length();
      return new ByteString(getStringBytes(false, new byte[length], 0, str, 0, length), 0, length);
   }

   public static ByteString getBytes(ByteBuffer buffer) {
      return new ByteString(Buffers.take(buffer));
   }

   public static ByteString getBytes(ByteBuffer buffer, int length) {
      return new ByteString(Buffers.take(buffer, length));
   }

   public byte[] getBytes() {
      return Arrays.copyOfRange(this.bytes, this.offs, this.len);
   }

   /** @deprecated */
   public void getBytes(byte[] dest) {
      this.copyTo(dest);
   }

   /** @deprecated */
   public void getBytes(byte[] dest, int offs) {
      this.copyTo(dest, offs);
   }

   /** @deprecated */
   public void getBytes(byte[] dest, int offs, int len) {
      this.copyTo(dest, offs, len);
   }

   public void copyTo(int srcOffs, byte[] dst, int offs, int len) {
      System.arraycopy(this.bytes, srcOffs + this.offs, dst, offs, Math.min(this.len, len));
   }

   public void copyTo(byte[] dst, int offs, int len) {
      this.copyTo(0, dst, offs, len);
   }

   public void copyTo(byte[] dst, int offs) {
      this.copyTo(dst, offs, dst.length - offs);
   }

   public void copyTo(byte[] dst) {
      this.copyTo(dst, 0, dst.length);
   }

   public void appendTo(ByteBuffer dest) {
      dest.put(this.bytes, this.offs, this.len);
   }

   public int tryAppendTo(int offs, ByteBuffer buffer) {
      byte[] b = this.bytes;
      int len = Math.min(buffer.remaining(), b.length - offs);
      buffer.put(b, offs + this.offs, len);
      return len;
   }

   public void writeTo(OutputStream output) throws IOException {
      output.write(this.bytes, this.offs, this.len);
   }

   public int compareTo(ByteString other) {
      if (other == this) {
         return 0;
      } else {
         int length = this.len;
         int otherLength = other.len;
         int len1 = Math.min(length, otherLength);
         byte[] bytes = this.bytes;
         byte[] otherBytes = other.bytes;
         int offs = this.offs;
         int otherOffs = other.offs;

         for(int i = 0; i < len1; ++i) {
            int res = Integer.signum(bytes[i + offs] - otherBytes[i + otherOffs]);
            if (res != 0) {
               return res;
            }
         }

         return Integer.signum(length - otherLength);
      }
   }

   public int compareToIgnoreCase(ByteString other) {
      if (other == this) {
         return 0;
      } else if (other == this) {
         return 0;
      } else {
         int length = this.len;
         int otherLength = other.len;
         int len1 = Math.min(length, otherLength);
         byte[] bytes = this.bytes;
         byte[] otherBytes = other.bytes;
         int offs = this.offs;
         int otherOffs = other.offs;

         for(int i = 0; i < len1; ++i) {
            int res = Integer.signum(upperCase(bytes[i + offs]) - upperCase(otherBytes[i + otherOffs]));
            if (res != 0) {
               return res;
            }
         }

         return Integer.signum(length - otherLength);
      }
   }

   private static int upperCase(byte b) {
      return b >= 97 && b <= 122 ? b & 223 : b;
   }

   public String toString(String charset) throws UnsupportedEncodingException {
      return !"ISO-8859-1".equalsIgnoreCase(charset) && !"Latin-1".equalsIgnoreCase(charset) && !"ISO-Latin-1".equals(charset) ? new String(this.bytes, this.offs, this.len, charset) : this.toString();
   }

   public int length() {
      return this.len;
   }

   public String toString() {
      return new String(this.bytes, 0, this.offs, this.len);
   }

   public String toUtf8String() {
      return new String(this.bytes, this.offs, this.len, StandardCharsets.UTF_8);
   }

   public byte byteAt(int idx) {
      if (idx >= 0 && idx <= this.len) {
         return this.bytes[idx + this.offs];
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public ByteString substring(int offs) {
      return this.substring(offs, this.len - offs);
   }

   public ByteString substring(int offs, int len) {
      if (this.len - offs < len) {
         throw new IndexOutOfBoundsException();
      } else {
         return new ByteString(this.bytes, this.offs + offs, len);
      }
   }

   public int hashCode() {
      int hashCode = this.hashCode;
      if (hashCode == 0) {
         this.hashCode = hashCode = calcHashCode(this.bytes, this.offs, this.len);
      }

      return hashCode;
   }

   public int hashCodeIgnoreCase() {
      int hashCode = this.hashCodeIgnoreCase;
      if (hashCode == 0) {
         this.hashCodeIgnoreCase = hashCode = calcHashCodeIgnoreCase(this.bytes, this.offs, this.len);
      }

      return hashCode;
   }

   private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
      ois.defaultReadObject();
   }

   private static boolean equals(byte[] a, int aoff, byte[] b, int boff, int len) {
      for(int i = 0; i < len; ++i) {
         if (a[i + aoff] != b[i + boff]) {
            return false;
         }
      }

      return true;
   }

   private static boolean equalsIgnoreCase(byte[] a, int aoff, byte[] b, int boff, int len) {
      for(int i = 0; i < len; ++i) {
         if (upperCase(a[i + aoff]) != upperCase(b[i + boff])) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(Object obj) {
      return obj instanceof ByteString && this.equals((ByteString)obj);
   }

   public boolean equals(ByteString other) {
      int len = this.len;
      return this == other || other != null && len == other.len && equals(this.bytes, this.offs, other.bytes, other.offs, len);
   }

   public boolean equalsIgnoreCase(ByteString other) {
      int len = this.len;
      return this == other || other != null && len == other.len && equalsIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, len);
   }

   public int toInt(int start) {
      int len = this.len;
      if (start >= len) {
         return 0;
      } else {
         byte[] bytes = this.bytes;
         int v = 0;

         for(int i = start + this.offs; i < len; ++i) {
            byte b = bytes[i];
            if (b < 48 || b > 57) {
               return v;
            }

            v = (v << 3) + (v << 1) + (b - 48);
         }

         return v;
      }
   }

   public int toInt() {
      return this.toInt(0);
   }

   public long toLong(int start) {
      int len = this.len;
      if (start >= len) {
         return 0L;
      } else {
         byte[] bytes = this.bytes;
         long v = 0L;

         for(int i = start; i < len; ++i) {
            byte b = bytes[i];
            if (b < 48 || b > 57) {
               return v;
            }

            v = (v << 3) + (v << 1) + (long)(b - 48);
         }

         return v;
      }
   }

   public long toLong() {
      return this.toLong(0);
   }

   private static int decimalCount(int val) {
      assert val >= 0;

      if (val < 10) {
         return 1;
      } else if (val < 100) {
         return 2;
      } else if (val < 1000) {
         return 3;
      } else if (val < 10000) {
         return 4;
      } else if (val < 100000) {
         return 5;
      } else if (val < 1000000) {
         return 6;
      } else if (val < 10000000) {
         return 7;
      } else if (val < 100000000) {
         return 8;
      } else {
         return val < 1000000000 ? 9 : 10;
      }
   }

   private static int decimalCount(long val) {
      assert val >= 0L;

      if (val < 10L) {
         return 1;
      } else if (val < 100L) {
         return 2;
      } else if (val < 1000L) {
         return 3;
      } else if (val < 10000L) {
         return 4;
      } else if (val < 100000L) {
         return 5;
      } else if (val < 1000000L) {
         return 6;
      } else if (val < 10000000L) {
         return 7;
      } else if (val < 100000000L) {
         return 8;
      } else if (val < 1000000000L) {
         return 9;
      } else if (val < 10000000000L) {
         return 10;
      } else if (val < 100000000000L) {
         return 11;
      } else if (val < 1000000000000L) {
         return 12;
      } else if (val < 10000000000000L) {
         return 13;
      } else if (val < 100000000000000L) {
         return 14;
      } else if (val < 1000000000000000L) {
         return 15;
      } else if (val < 10000000000000000L) {
         return 16;
      } else if (val < 100000000000000000L) {
         return 17;
      } else {
         return val < 1000000000000000000L ? 18 : 19;
      }
   }

   public static ByteString fromLong(long val) {
      if (val == 0L) {
         return ZERO;
      } else {
         int i = decimalCount(Math.abs(val));
         byte[] b;
         if (val < 0L) {
            ++i;
            b = new byte[i];
            b[0] = 45;
         } else {
            b = new byte[i];
         }

         do {
            long quo = val / 10L;
            int mod = (int)(val - ((quo << 3) + (quo << 1)));
            --i;
            b[i] = (byte)(mod + 48);
            val = quo;
         } while(i > 0);

         return new ByteString(b);
      }
   }

   public static ByteString fromInt(int val) {
      if (val == 0) {
         return ZERO;
      } else {
         int i = decimalCount(Math.abs(val));
         byte[] b;
         if (val < 0) {
            ++i;
            b = new byte[i];
            b[0] = 45;
         } else {
            b = new byte[i];
         }

         do {
            int quo = val / 10;
            int mod = val - ((quo << 3) + (quo << 1));
            --i;
            b[i] = (byte)(mod + 48);
            val = quo;
         } while(i > 0);

         return new ByteString(b);
      }
   }

   public boolean equalToString(String str) {
      if (str == null) {
         return false;
      } else {
         byte[] bytes = this.bytes;
         int length = bytes.length;
         if (str.length() != length) {
            return false;
         } else {
            int end = this.offs + this.len;

            for(int i = this.offs; i < end; ++i) {
               char ch = str.charAt(i);
               if (ch > 255 || bytes[i] != (byte)str.charAt(i)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean equalToStringIgnoreCase(String str) {
      if (str == null) {
         return false;
      } else {
         byte[] bytes = this.bytes;
         int length = bytes.length;
         if (str.length() != length) {
            return false;
         } else {
            int end = this.offs + this.len;

            for(int i = this.offs; i < end; ++i) {
               char ch = str.charAt(i);
               if (ch > 255 || upperCase(bytes[i]) != upperCase((byte)ch)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int indexOf(char c) {
      return this.indexOf(c, 0);
   }

   public int indexOf(char c, int start) {
      if (c > 255) {
         return -1;
      } else {
         int len = this.len;
         if (start > len) {
            return -1;
         } else {
            start = Math.max(0, start) + this.offs;
            byte[] bytes = this.bytes;
            byte bc = (byte)c;
            int end = start + len;

            for(int i = start; i < end; ++i) {
               if (bytes[i] == bc) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   public int lastIndexOf(char c) {
      return this.lastIndexOf(c, this.length() - 1);
   }

   public int lastIndexOf(char c, int start) {
      if (c > 255) {
         return -1;
      } else {
         byte[] bytes = this.bytes;
         int offs = this.offs;
         start = Math.min(start, this.len - 1) + offs;
         byte bc = (byte)c;

         for(int i = start; i >= offs; --i) {
            if (bytes[i] == bc) {
               return i;
            }
         }

         return -1;
      }
   }

   private static int arrayIndexOf(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
      int aLen = a.length - aOffs;
      if (bLen <= aLen && aLen >= 0) {
         aOffs = Math.max(0, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            byte startByte = b[bOffs];
            int limit = aLen - bLen;

            label34:
            for(int i = aOffs; i < limit; ++i) {
               if (a[i] == startByte) {
                  for(int j = 1; j < bLen; ++j) {
                     if (a[i + j] != b[j + bOffs]) {
                        continue label34;
                     }
                  }

                  return i;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int arrayIndexOf(byte[] a, int aOffs, String string) {
      int aLen = a.length - aOffs;
      int bLen = string.length();
      if (bLen <= aLen && aLen >= 0) {
         aOffs = Math.max(0, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            char startChar = string.charAt(0);
            if (startChar > 255) {
               return -1;
            } else {
               int limit = aLen - bLen;

               label42:
               for(int i = aOffs; i < limit; ++i) {
                  if (a[i] == startChar) {
                     for(int j = 1; j < bLen; ++j) {
                        char ch = string.charAt(j);
                        if (ch > 255) {
                           return -1;
                        }

                        if (a[i + j] != ch) {
                           continue label42;
                        }
                     }

                     return i;
                  }
               }

               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   private static int arrayIndexOfIgnoreCase(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
      int aLen = a.length - aOffs;
      if (bLen <= aLen && aLen >= 0) {
         aOffs = Math.max(0, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            int startChar = upperCase(b[bOffs]);
            int limit = aLen - bLen;

            label34:
            for(int i = aOffs; i < limit; ++i) {
               if (upperCase(a[i]) == startChar) {
                  for(int j = 1; j < bLen; ++j) {
                     if (upperCase(a[i + j]) != upperCase(b[j + bOffs])) {
                        continue label34;
                     }
                  }

                  return i;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int arrayIndexOfIgnoreCase(byte[] a, int aOffs, String string) {
      int aLen = a.length - aOffs;
      int bLen = string.length();
      if (bLen <= aLen && aLen >= 0) {
         aOffs = Math.max(0, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            char startChar = string.charAt(0);
            if (startChar > 255) {
               return -1;
            } else {
               int startCP = upperCase((byte)startChar);
               int limit = aLen - bLen;

               label42:
               for(int i = aOffs; i < limit; ++i) {
                  if (upperCase(a[i]) == startCP) {
                     for(int j = 1; j < bLen; ++j) {
                        char ch = string.charAt(j);
                        if (ch > 255) {
                           return -1;
                        }

                        if (upperCase(a[i + j]) != upperCase((byte)ch)) {
                           continue label42;
                        }
                     }

                     return i;
                  }
               }

               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   private static int arrayLastIndexOf(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
      int aLen = a.length - aOffs;
      if (bLen <= aLen && aLen >= 0 && aOffs >= 0) {
         aOffs = Math.min(aLen - bLen, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            byte startByte = b[0];

            for(int i = aOffs - 1; i >= 0; --i) {
               if (a[i] == startByte) {
                  int j = 1;
                  if (j < bLen && a[i + j] == b[bOffs + j]) {
                     return i;
                  }
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int arrayLastIndexOf(byte[] a, int aOffs, String string) {
      int aLen = a.length - aOffs;
      int bLen = string.length();
      if (bLen <= aLen && aLen >= 0 && aOffs >= 0) {
         aOffs = Math.min(aLen - bLen, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            char startChar = string.charAt(0);
            if (startChar > 255) {
               return -1;
            } else {
               byte startByte = (byte)startChar;

               for(int i = aOffs - 1; i >= 0; --i) {
                  if (a[i] == startByte) {
                     int j = 1;
                     if (j < bLen) {
                        char ch = string.charAt(j);
                        if (ch > 255) {
                           return -1;
                        }

                        if (a[i + j] == (byte)ch) {
                           return i;
                        }
                     }
                  }
               }

               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   private static int arrayLastIndexOfIgnoreCase(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
      int aLen = a.length - aOffs;
      if (bLen <= aLen && aLen >= 0 && aOffs >= 0) {
         aOffs = Math.min(aLen - bLen, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            int startCP = upperCase(b[bOffs]);

            for(int i = aOffs - 1; i >= 0; --i) {
               if (upperCase(a[i]) == startCP) {
                  int j = 1;
                  if (j < bLen && upperCase(a[i + j]) == upperCase(b[j + bOffs])) {
                     return i;
                  }
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int arrayLastIndexOfIgnoreCase(byte[] a, int aOffs, String string) {
      int aLen = a.length - aOffs;
      int bLen = string.length();
      if (bLen <= aLen && aLen >= 0 && aOffs >= 0) {
         aOffs = Math.min(aLen - bLen, aOffs);
         if (bLen == 0) {
            return aOffs;
         } else {
            char startChar = string.charAt(0);
            if (startChar > 255) {
               return -1;
            } else {
               int startCP = upperCase((byte)startChar);

               for(int i = aOffs - 1; i >= 0; --i) {
                  if (upperCase(a[i]) == startCP) {
                     int j = 1;
                     if (j < bLen) {
                        char ch = string.charAt(j);
                        if (ch > 255) {
                           return -1;
                        }

                        if (upperCase(a[i + j]) == upperCase((byte)ch)) {
                           return i;
                        }
                     }
                  }
               }

               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   public boolean contains(ByteString other) {
      if (other == this) {
         return true;
      } else if (other == null) {
         return false;
      } else {
         byte[] otherBytes = other.bytes;
         return arrayIndexOf(this.bytes, this.offs, otherBytes, other.offs, other.len) != -1;
      }
   }

   public boolean contains(String other) {
      return other != null && this.toString().contains(other);
   }

   public boolean containsIgnoreCase(ByteString other) {
      return other == this || other != null && arrayIndexOfIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, other.len) != -1;
   }

   public boolean containsIgnoreCase(String other) {
      return arrayIndexOfIgnoreCase(this.bytes, this.offs, other) != -1;
   }

   public int indexOf(ByteString other) {
      return arrayIndexOf(this.bytes, this.offs, other.bytes, other.offs, other.len);
   }

   public int indexOf(ByteString other, int start) {
      if (start > this.len) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         return arrayIndexOf(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
      }
   }

   public int indexOf(String other) {
      return arrayIndexOf(this.bytes, this.offs, other);
   }

   public int indexOf(String other, int start) {
      if (start > this.len) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         return arrayIndexOf(this.bytes, this.offs + start, other);
      }
   }

   public int indexOfIgnoreCase(ByteString other) {
      return arrayIndexOfIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, other.len);
   }

   public int indexOfIgnoreCase(ByteString other, int start) {
      if (start > this.len) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         return arrayIndexOfIgnoreCase(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
      }
   }

   public int indexOfIgnoreCase(String other) {
      return arrayIndexOfIgnoreCase(this.bytes, this.offs, other);
   }

   public int indexOfIgnoreCase(String other, int start) {
      if (start > this.len) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         return arrayIndexOfIgnoreCase(this.bytes, this.offs + start, other);
      }
   }

   public int lastIndexOf(ByteString other) {
      return arrayLastIndexOf(this.bytes, this.offs, other.bytes, other.offs, other.len);
   }

   public int lastIndexOf(ByteString other, int start) {
      if (start > this.len) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         return arrayLastIndexOf(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
      }
   }

   public int lastIndexOf(String other) {
      return arrayLastIndexOf(this.bytes, this.offs, other);
   }

   public int lastIndexOf(String other, int start) {
      return arrayLastIndexOf(this.bytes, this.offs + start, other);
   }

   public int lastIndexOfIgnoreCase(ByteString other) {
      return arrayLastIndexOfIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, other.len);
   }

   public int lastIndexOfIgnoreCase(ByteString other, int start) {
      if (start > this.len) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         return arrayLastIndexOfIgnoreCase(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
      }
   }

   public int lastIndexOfIgnoreCase(String other) {
      return arrayLastIndexOfIgnoreCase(this.bytes, this.offs, other);
   }

   public int lastIndexOfIgnoreCase(String other, int start) {
      return arrayLastIndexOfIgnoreCase(this.bytes, this.offs + start, other);
   }

   public boolean regionMatches(boolean ignoreCase, int offset, byte[] other, int otherOffset, int len) {
      if (offset >= 0 && otherOffset >= 0 && offset + len <= this.len && otherOffset + len <= other.length) {
         return ignoreCase ? equalsIgnoreCase(this.bytes, offset + this.offs, other, otherOffset, len) : equals(this.bytes, offset + this.offs, other, otherOffset, len);
      } else {
         return false;
      }
   }

   public boolean regionMatches(boolean ignoreCase, int offset, ByteString other, int otherOffset, int len) {
      if (offset >= 0 && otherOffset >= 0 && offset + len <= this.len && otherOffset + len <= other.len) {
         return ignoreCase ? equalsIgnoreCase(this.bytes, offset + this.offs, other.bytes, otherOffset, len) : equals(this.bytes, offset + this.offs, other.bytes, otherOffset, len);
      } else {
         return false;
      }
   }

   public boolean regionMatches(boolean ignoreCase, int offset, String other, int otherOffset, int len) {
      if (offset >= 0 && otherOffset >= 0 && offset + len <= this.len && otherOffset + len <= other.length()) {
         return ignoreCase ? equalsIgnoreCase(this.bytes, offset + this.offs, other, otherOffset, len) : equals(this.bytes, offset + this.offs, other, otherOffset, len);
      } else {
         return false;
      }
   }

   private static boolean equalsIgnoreCase(byte[] a, int aOffs, String string, int stringOffset, int length) {
      for(int i = 0; i < length; ++i) {
         char ch = string.charAt(i + stringOffset);
         if (ch > 255) {
            return false;
         }

         if (a[i + aOffs] != (byte)ch) {
            return false;
         }
      }

      return true;
   }

   private static boolean equals(byte[] a, int aOffs, String string, int stringOffset, int length) {
      for(int i = 0; i < length; ++i) {
         char ch = string.charAt(i + stringOffset);
         if (ch > 255) {
            return false;
         }

         if (upperCase(a[i + aOffs]) != upperCase((byte)ch)) {
            return false;
         }
      }

      return true;
   }

   public boolean startsWith(ByteString prefix) {
      return this.regionMatches(false, 0, (ByteString)prefix, 0, prefix.length());
   }

   public boolean startsWith(String prefix) {
      return this.regionMatches(false, 0, (String)prefix, 0, prefix.length());
   }

   public boolean startsWith(char prefix) {
      return prefix <= 255 && this.len > 0 && this.bytes[this.offs] == (byte)prefix;
   }

   public boolean startsWithIgnoreCase(ByteString prefix) {
      return this.regionMatches(true, 0, (ByteString)prefix, 0, prefix.length());
   }

   public boolean startsWithIgnoreCase(String prefix) {
      return this.regionMatches(true, 0, (String)prefix, 0, prefix.length());
   }

   public boolean startsWithIgnoreCase(char prefix) {
      return prefix <= 255 && this.len > 0 && upperCase(this.bytes[this.offs]) == upperCase((byte)prefix);
   }

   public boolean endsWith(ByteString suffix) {
      int suffixLength = suffix.len;
      return this.regionMatches(false, this.len - suffixLength, (ByteString)suffix, 0, suffixLength);
   }

   public boolean endsWith(String suffix) {
      int suffixLength = suffix.length();
      return this.regionMatches(false, this.len - suffixLength, (String)suffix, 0, suffixLength);
   }

   public boolean endsWith(char suffix) {
      int len = this.len;
      return suffix <= 255 && len > 0 && this.bytes[this.offs + len - 1] == (byte)suffix;
   }

   public boolean endsWithIgnoreCase(ByteString suffix) {
      int suffixLength = suffix.length();
      return this.regionMatches(true, this.len - suffixLength, (ByteString)suffix, 0, suffixLength);
   }

   public boolean endsWithIgnoreCase(String suffix) {
      int suffixLength = suffix.length();
      return this.regionMatches(true, this.len - suffixLength, (String)suffix, 0, suffixLength);
   }

   public boolean endsWithIgnoreCase(char suffix) {
      int len = this.len;
      return suffix <= 255 && len > 0 && upperCase(this.bytes[this.offs + len - 1]) == upperCase((byte)suffix);
   }

   public ByteString concat(byte[] suffixBytes) {
      return this.concat((byte[])suffixBytes, 0, suffixBytes.length);
   }

   public ByteString concat(byte[] suffixBytes, int offs, int len) {
      if (len <= 0) {
         return this;
      } else {
         int length = this.len;
         byte[] newBytes = Arrays.copyOfRange(this.bytes, this.offs, length + len);
         System.arraycopy(suffixBytes, offs, newBytes, length, len);
         return new ByteString(newBytes);
      }
   }

   public ByteString concat(ByteString suffix) {
      return this.concat(suffix.bytes, suffix.offs, suffix.len);
   }

   public ByteString concat(ByteString suffix, int offs, int len) {
      return this.concat(suffix.bytes, offs + suffix.offs, Math.min(len, suffix.len));
   }

   public ByteString concat(String suffix) {
      return this.concat((String)suffix, 0, suffix.length());
   }

   private static byte[] getStringBytes(boolean trust, byte[] dst, int dstOffs, String src, int srcOffs, int len) {
      if (trust) {
         src.getBytes(srcOffs, srcOffs + len, dst, dstOffs);
      } else {
         for(int i = srcOffs; i < len; ++i) {
            char c = src.charAt(i);
            if (c > 255) {
               throw new IllegalArgumentException("Invalid string contents");
            }

            dst[i + dstOffs] = (byte)c;
         }
      }

      return dst;
   }

   public ByteString concat(String suffix, int offs, int len) {
      if (len <= 0) {
         return this;
      } else {
         byte[] bytes = this.bytes;
         int length = this.len;
         byte[] newBytes = Arrays.copyOfRange(bytes, offs, offs + length + len);
         getStringBytes(false, newBytes, length, suffix, offs, len);
         return new ByteString(newBytes);
      }
   }

   public static ByteString concat(String prefix, ByteString suffix) {
      int prefixLength = prefix.length();
      byte[] suffixBytes = suffix.bytes;
      int suffixLength = suffixBytes.length;
      byte[] newBytes = new byte[prefixLength + suffixLength];
      getStringBytes(false, newBytes, 0, prefix, 0, prefixLength);
      System.arraycopy(suffixBytes, suffix.offs, newBytes, prefixLength, suffixLength);
      return new ByteString(newBytes);
   }

   public static ByteString concat(String prefix, String suffix) {
      int prefixLength = prefix.length();
      int suffixLength = suffix.length();
      byte[] newBytes = new byte[prefixLength + suffixLength];
      getStringBytes(false, newBytes, 0, prefix, 0, prefixLength);
      getStringBytes(false, newBytes, prefixLength, suffix, 0, suffixLength);
      return new ByteString(newBytes);
   }

   public char charAt(int index) {
      if (index >= 0 && index <= this.len) {
         return (char)(this.bytes[index + this.offs] & 255);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public ByteString subSequence(int start, int end) {
      return this.substring(start, end);
   }
}
