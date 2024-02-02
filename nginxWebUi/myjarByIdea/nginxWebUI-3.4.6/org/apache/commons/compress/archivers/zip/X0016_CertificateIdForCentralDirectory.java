package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public class X0016_CertificateIdForCentralDirectory extends PKWareExtraHeader {
   private int rcount;
   private PKWareExtraHeader.HashAlgorithm hashAlg;

   public X0016_CertificateIdForCentralDirectory() {
      super(new ZipShort(22));
   }

   public int getRecordCount() {
      return this.rcount;
   }

   public PKWareExtraHeader.HashAlgorithm getHashAlgorithm() {
      return this.hashAlg;
   }

   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
      this.assertMinimalLength(4, length);
      this.rcount = ZipShort.getValue(data, offset);
      this.hashAlg = PKWareExtraHeader.HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
   }
}
