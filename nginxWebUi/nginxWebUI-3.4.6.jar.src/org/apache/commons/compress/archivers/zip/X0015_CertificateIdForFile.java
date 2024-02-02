/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.util.zip.ZipException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X0015_CertificateIdForFile
/*    */   extends PKWareExtraHeader
/*    */ {
/*    */   private int rcount;
/*    */   private PKWareExtraHeader.HashAlgorithm hashAlg;
/*    */   
/*    */   public X0015_CertificateIdForFile() {
/* 49 */     super(new ZipShort(21));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getRecordCount() {
/* 60 */     return this.rcount;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PKWareExtraHeader.HashAlgorithm getHashAlgorithm() {
/* 68 */     return this.hashAlg;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
/* 74 */     assertMinimalLength(4, length);
/* 75 */     super.parseFromCentralDirectoryData(data, offset, length);
/* 76 */     this.rcount = ZipShort.getValue(data, offset);
/* 77 */     this.hashAlg = PKWareExtraHeader.HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\X0015_CertificateIdForFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */