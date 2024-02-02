package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import org.apache.commons.compress.utils.ByteUtils;

public final class ZipLong implements Cloneable, Serializable {
   private static final long serialVersionUID = 1L;
   private final long value;
   public static final ZipLong CFH_SIG = new ZipLong(33639248L);
   public static final ZipLong LFH_SIG = new ZipLong(67324752L);
   public static final ZipLong DD_SIG = new ZipLong(134695760L);
   static final ZipLong ZIP64_MAGIC = new ZipLong(4294967295L);
   public static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
   public static final ZipLong AED_SIG = new ZipLong(134630224L);

   public ZipLong(long value) {
      this.value = value;
   }

   public ZipLong(int value) {
      this.value = (long)value;
   }

   public ZipLong(byte[] bytes) {
      this(bytes, 0);
   }

   public ZipLong(byte[] bytes, int offset) {
      this.value = getValue(bytes, offset);
   }

   public byte[] getBytes() {
      return getBytes(this.value);
   }

   public long getValue() {
      return this.value;
   }

   public int getIntValue() {
      return (int)this.value;
   }

   public static byte[] getBytes(long value) {
      byte[] result = new byte[4];
      putLong(value, result, 0);
      return result;
   }

   public static void putLong(long value, byte[] buf, int offset) {
      ByteUtils.toLittleEndian(buf, value, offset, 4);
   }

   public void putLong(byte[] buf, int offset) {
      putLong(this.value, buf, offset);
   }

   public static long getValue(byte[] bytes, int offset) {
      return ByteUtils.fromLittleEndian(bytes, offset, 4);
   }

   public static long getValue(byte[] bytes) {
      return getValue(bytes, 0);
   }

   public boolean equals(Object o) {
      if (!(o instanceof ZipLong)) {
         return false;
      } else {
         return this.value == ((ZipLong)o).getValue();
      }
   }

   public int hashCode() {
      return (int)this.value;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String toString() {
      return "ZipLong value: " + this.value;
   }
}
