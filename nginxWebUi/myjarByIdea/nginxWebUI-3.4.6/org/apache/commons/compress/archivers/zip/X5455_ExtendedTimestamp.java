package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipException;

public class X5455_ExtendedTimestamp implements ZipExtraField, Cloneable, Serializable {
   private static final ZipShort HEADER_ID = new ZipShort(21589);
   private static final long serialVersionUID = 1L;
   public static final byte MODIFY_TIME_BIT = 1;
   public static final byte ACCESS_TIME_BIT = 2;
   public static final byte CREATE_TIME_BIT = 4;
   private byte flags;
   private boolean bit0_modifyTimePresent;
   private boolean bit1_accessTimePresent;
   private boolean bit2_createTimePresent;
   private ZipLong modifyTime;
   private ZipLong accessTime;
   private ZipLong createTime;

   public ZipShort getHeaderId() {
      return HEADER_ID;
   }

   public ZipShort getLocalFileDataLength() {
      return new ZipShort(1 + (this.bit0_modifyTimePresent ? 4 : 0) + (this.bit1_accessTimePresent && this.accessTime != null ? 4 : 0) + (this.bit2_createTimePresent && this.createTime != null ? 4 : 0));
   }

   public ZipShort getCentralDirectoryLength() {
      return new ZipShort(1 + (this.bit0_modifyTimePresent ? 4 : 0));
   }

   public byte[] getLocalFileDataData() {
      byte[] data = new byte[this.getLocalFileDataLength().getValue()];
      int pos = 0;
      data[pos++] = 0;
      if (this.bit0_modifyTimePresent) {
         data[0] = (byte)(data[0] | 1);
         System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 4);
         pos += 4;
      }

      if (this.bit1_accessTimePresent && this.accessTime != null) {
         data[0] = (byte)(data[0] | 2);
         System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 4);
         pos += 4;
      }

      if (this.bit2_createTimePresent && this.createTime != null) {
         data[0] = (byte)(data[0] | 4);
         System.arraycopy(this.createTime.getBytes(), 0, data, pos, 4);
         pos += 4;
      }

      return data;
   }

   public byte[] getCentralDirectoryData() {
      return Arrays.copyOf(this.getLocalFileDataData(), this.getCentralDirectoryLength().getValue());
   }

   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
      this.reset();
      if (length < 1) {
         throw new ZipException("X5455_ExtendedTimestamp too short, only " + length + " bytes");
      } else {
         int len = offset + length;
         this.setFlags(data[offset++]);
         if (this.bit0_modifyTimePresent && offset + 4 <= len) {
            this.modifyTime = new ZipLong(data, offset);
            offset += 4;
         } else {
            this.bit0_modifyTimePresent = false;
         }

         if (this.bit1_accessTimePresent && offset + 4 <= len) {
            this.accessTime = new ZipLong(data, offset);
            offset += 4;
         } else {
            this.bit1_accessTimePresent = false;
         }

         if (this.bit2_createTimePresent && offset + 4 <= len) {
            this.createTime = new ZipLong(data, offset);
            offset += 4;
         } else {
            this.bit2_createTimePresent = false;
         }

      }
   }

   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
      this.reset();
      this.parseFromLocalFileData(buffer, offset, length);
   }

   private void reset() {
      this.setFlags((byte)0);
      this.modifyTime = null;
      this.accessTime = null;
      this.createTime = null;
   }

   public void setFlags(byte flags) {
      this.flags = flags;
      this.bit0_modifyTimePresent = (flags & 1) == 1;
      this.bit1_accessTimePresent = (flags & 2) == 2;
      this.bit2_createTimePresent = (flags & 4) == 4;
   }

   public byte getFlags() {
      return this.flags;
   }

   public boolean isBit0_modifyTimePresent() {
      return this.bit0_modifyTimePresent;
   }

   public boolean isBit1_accessTimePresent() {
      return this.bit1_accessTimePresent;
   }

   public boolean isBit2_createTimePresent() {
      return this.bit2_createTimePresent;
   }

   public ZipLong getModifyTime() {
      return this.modifyTime;
   }

   public ZipLong getAccessTime() {
      return this.accessTime;
   }

   public ZipLong getCreateTime() {
      return this.createTime;
   }

   public Date getModifyJavaTime() {
      return zipLongToDate(this.modifyTime);
   }

   public Date getAccessJavaTime() {
      return zipLongToDate(this.accessTime);
   }

   public Date getCreateJavaTime() {
      return zipLongToDate(this.createTime);
   }

   public void setModifyTime(ZipLong l) {
      this.bit0_modifyTimePresent = l != null;
      this.flags = (byte)(l != null ? this.flags | 1 : this.flags & -2);
      this.modifyTime = l;
   }

   public void setAccessTime(ZipLong l) {
      this.bit1_accessTimePresent = l != null;
      this.flags = (byte)(l != null ? this.flags | 2 : this.flags & -3);
      this.accessTime = l;
   }

   public void setCreateTime(ZipLong l) {
      this.bit2_createTimePresent = l != null;
      this.flags = (byte)(l != null ? this.flags | 4 : this.flags & -5);
      this.createTime = l;
   }

   public void setModifyJavaTime(Date d) {
      this.setModifyTime(dateToZipLong(d));
   }

   public void setAccessJavaTime(Date d) {
      this.setAccessTime(dateToZipLong(d));
   }

   public void setCreateJavaTime(Date d) {
      this.setCreateTime(dateToZipLong(d));
   }

   private static ZipLong dateToZipLong(Date d) {
      return d == null ? null : unixTimeToZipLong(d.getTime() / 1000L);
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("0x5455 Zip Extra Field: Flags=");
      buf.append(Integer.toBinaryString(ZipUtil.unsignedIntToSignedByte(this.flags))).append(" ");
      Date c;
      if (this.bit0_modifyTimePresent && this.modifyTime != null) {
         c = this.getModifyJavaTime();
         buf.append(" Modify:[").append(c).append("] ");
      }

      if (this.bit1_accessTimePresent && this.accessTime != null) {
         c = this.getAccessJavaTime();
         buf.append(" Access:[").append(c).append("] ");
      }

      if (this.bit2_createTimePresent && this.createTime != null) {
         c = this.getCreateJavaTime();
         buf.append(" Create:[").append(c).append("] ");
      }

      return buf.toString();
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public boolean equals(Object o) {
      if (!(o instanceof X5455_ExtendedTimestamp)) {
         return false;
      } else {
         X5455_ExtendedTimestamp xf = (X5455_ExtendedTimestamp)o;
         return (this.flags & 7) == (xf.flags & 7) && (this.modifyTime == xf.modifyTime || this.modifyTime != null && this.modifyTime.equals(xf.modifyTime)) && (this.accessTime == xf.accessTime || this.accessTime != null && this.accessTime.equals(xf.accessTime)) && (this.createTime == xf.createTime || this.createTime != null && this.createTime.equals(xf.createTime));
      }
   }

   public int hashCode() {
      int hc = -123 * (this.flags & 7);
      if (this.modifyTime != null) {
         hc ^= this.modifyTime.hashCode();
      }

      if (this.accessTime != null) {
         hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
      }

      if (this.createTime != null) {
         hc ^= Integer.rotateLeft(this.createTime.hashCode(), 22);
      }

      return hc;
   }

   private static Date zipLongToDate(ZipLong unixTime) {
      return unixTime != null ? new Date((long)unixTime.getIntValue() * 1000L) : null;
   }

   private static ZipLong unixTimeToZipLong(long l) {
      if (l >= -2147483648L && l <= 2147483647L) {
         return new ZipLong(l);
      } else {
         throw new IllegalArgumentException("X5455 timestamps must fit in a signed 32 bit integer: " + l);
      }
   }
}
