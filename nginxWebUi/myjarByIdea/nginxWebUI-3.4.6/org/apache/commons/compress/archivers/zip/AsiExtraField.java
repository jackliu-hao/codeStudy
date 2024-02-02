package org.apache.commons.compress.archivers.zip;

import java.util.zip.CRC32;
import java.util.zip.ZipException;

public class AsiExtraField implements ZipExtraField, UnixStat, Cloneable {
   private static final ZipShort HEADER_ID = new ZipShort(30062);
   private static final int MIN_SIZE = 14;
   private int mode;
   private int uid;
   private int gid;
   private String link = "";
   private boolean dirFlag;
   private CRC32 crc = new CRC32();

   public ZipShort getHeaderId() {
      return HEADER_ID;
   }

   public ZipShort getLocalFileDataLength() {
      return new ZipShort(14 + this.getLinkedFile().getBytes().length);
   }

   public ZipShort getCentralDirectoryLength() {
      return this.getLocalFileDataLength();
   }

   public byte[] getLocalFileDataData() {
      byte[] data = new byte[this.getLocalFileDataLength().getValue() - 4];
      System.arraycopy(ZipShort.getBytes(this.getMode()), 0, data, 0, 2);
      byte[] linkArray = this.getLinkedFile().getBytes();
      System.arraycopy(ZipLong.getBytes((long)linkArray.length), 0, data, 2, 4);
      System.arraycopy(ZipShort.getBytes(this.getUserId()), 0, data, 6, 2);
      System.arraycopy(ZipShort.getBytes(this.getGroupId()), 0, data, 8, 2);
      System.arraycopy(linkArray, 0, data, 10, linkArray.length);
      this.crc.reset();
      this.crc.update(data);
      long checksum = this.crc.getValue();
      byte[] result = new byte[data.length + 4];
      System.arraycopy(ZipLong.getBytes(checksum), 0, result, 0, 4);
      System.arraycopy(data, 0, result, 4, data.length);
      return result;
   }

   public byte[] getCentralDirectoryData() {
      return this.getLocalFileDataData();
   }

   public void setUserId(int uid) {
      this.uid = uid;
   }

   public int getUserId() {
      return this.uid;
   }

   public void setGroupId(int gid) {
      this.gid = gid;
   }

   public int getGroupId() {
      return this.gid;
   }

   public void setLinkedFile(String name) {
      this.link = name;
      this.mode = this.getMode(this.mode);
   }

   public String getLinkedFile() {
      return this.link;
   }

   public boolean isLink() {
      return !this.getLinkedFile().isEmpty();
   }

   public void setMode(int mode) {
      this.mode = this.getMode(mode);
   }

   public int getMode() {
      return this.mode;
   }

   public void setDirectory(boolean dirFlag) {
      this.dirFlag = dirFlag;
      this.mode = this.getMode(this.mode);
   }

   public boolean isDirectory() {
      return this.dirFlag && !this.isLink();
   }

   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
      if (length < 14) {
         throw new ZipException("The length is too short, only " + length + " bytes, expected at least " + 14);
      } else {
         long givenChecksum = ZipLong.getValue(data, offset);
         byte[] tmp = new byte[length - 4];
         System.arraycopy(data, offset + 4, tmp, 0, length - 4);
         this.crc.reset();
         this.crc.update(tmp);
         long realChecksum = this.crc.getValue();
         if (givenChecksum != realChecksum) {
            throw new ZipException("Bad CRC checksum, expected " + Long.toHexString(givenChecksum) + " instead of " + Long.toHexString(realChecksum));
         } else {
            int newMode = ZipShort.getValue(tmp, 0);
            int linkArrayLength = (int)ZipLong.getValue(tmp, 2);
            if (linkArrayLength >= 0 && linkArrayLength <= tmp.length - 10) {
               this.uid = ZipShort.getValue(tmp, 6);
               this.gid = ZipShort.getValue(tmp, 8);
               if (linkArrayLength == 0) {
                  this.link = "";
               } else {
                  byte[] linkArray = new byte[linkArrayLength];
                  System.arraycopy(tmp, 10, linkArray, 0, linkArrayLength);
                  this.link = new String(linkArray);
               }

               this.setDirectory((newMode & 16384) != 0);
               this.setMode(newMode);
            } else {
               throw new ZipException("Bad symbolic link name length " + linkArrayLength + " in ASI extra field");
            }
         }
      }
   }

   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
      this.parseFromLocalFileData(buffer, offset, length);
   }

   protected int getMode(int mode) {
      int type = '耀';
      if (this.isLink()) {
         type = 'ꀀ';
      } else if (this.isDirectory()) {
         type = 16384;
      }

      return type | mode & 4095;
   }

   public Object clone() {
      try {
         AsiExtraField cloned = (AsiExtraField)super.clone();
         cloned.crc = new CRC32();
         return cloned;
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }
}
