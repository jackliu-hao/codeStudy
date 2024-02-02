package org.apache.commons.compress.archivers.zip;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

public abstract class PKWareExtraHeader implements ZipExtraField {
   private final ZipShort headerId;
   private byte[] localData;
   private byte[] centralData;

   protected PKWareExtraHeader(ZipShort headerId) {
      this.headerId = headerId;
   }

   public ZipShort getHeaderId() {
      return this.headerId;
   }

   public void setLocalFileDataData(byte[] data) {
      this.localData = ZipUtil.copy(data);
   }

   public ZipShort getLocalFileDataLength() {
      return new ZipShort(this.localData != null ? this.localData.length : 0);
   }

   public byte[] getLocalFileDataData() {
      return ZipUtil.copy(this.localData);
   }

   public void setCentralDirectoryData(byte[] data) {
      this.centralData = ZipUtil.copy(data);
   }

   public ZipShort getCentralDirectoryLength() {
      return this.centralData != null ? new ZipShort(this.centralData.length) : this.getLocalFileDataLength();
   }

   public byte[] getCentralDirectoryData() {
      return this.centralData != null ? ZipUtil.copy(this.centralData) : this.getLocalFileDataData();
   }

   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
      this.setLocalFileDataData(Arrays.copyOfRange(data, offset, offset + length));
   }

   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
      byte[] tmp = Arrays.copyOfRange(data, offset, offset + length);
      this.setCentralDirectoryData(tmp);
      if (this.localData == null) {
         this.setLocalFileDataData(tmp);
      }

   }

   protected final void assertMinimalLength(int minimum, int length) throws ZipException {
      if (length < minimum) {
         throw new ZipException(this.getClass().getName() + " is too short, only " + length + " bytes, expected at least " + minimum);
      }
   }

   public static enum HashAlgorithm {
      NONE(0),
      CRC32(1),
      MD5(32771),
      SHA1(32772),
      RIPEND160(32775),
      SHA256(32780),
      SHA384(32781),
      SHA512(32782);

      private final int code;
      private static final Map<Integer, HashAlgorithm> codeToEnum;

      private HashAlgorithm(int code) {
         this.code = code;
      }

      public int getCode() {
         return this.code;
      }

      public static HashAlgorithm getAlgorithmByCode(int code) {
         return (HashAlgorithm)codeToEnum.get(code);
      }

      static {
         Map<Integer, HashAlgorithm> cte = new HashMap();
         HashAlgorithm[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            HashAlgorithm method = var1[var3];
            cte.put(method.getCode(), method);
         }

         codeToEnum = Collections.unmodifiableMap(cte);
      }
   }

   public static enum EncryptionAlgorithm {
      DES(26113),
      RC2pre52(26114),
      TripleDES168(26115),
      TripleDES192(26121),
      AES128(26126),
      AES192(26127),
      AES256(26128),
      RC2(26370),
      RC4(26625),
      UNKNOWN(65535);

      private final int code;
      private static final Map<Integer, EncryptionAlgorithm> codeToEnum;

      private EncryptionAlgorithm(int code) {
         this.code = code;
      }

      public int getCode() {
         return this.code;
      }

      public static EncryptionAlgorithm getAlgorithmByCode(int code) {
         return (EncryptionAlgorithm)codeToEnum.get(code);
      }

      static {
         Map<Integer, EncryptionAlgorithm> cte = new HashMap();
         EncryptionAlgorithm[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EncryptionAlgorithm method = var1[var3];
            cte.put(method.getCode(), method);
         }

         codeToEnum = Collections.unmodifiableMap(cte);
      }
   }
}
