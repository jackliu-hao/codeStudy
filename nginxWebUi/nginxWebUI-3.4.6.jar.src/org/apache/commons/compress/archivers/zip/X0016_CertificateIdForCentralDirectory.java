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
/*    */ 
/*    */ public class X0016_CertificateIdForCentralDirectory
/*    */   extends PKWareExtraHeader
/*    */ {
/*    */   private int rcount;
/*    */   private PKWareExtraHeader.HashAlgorithm hashAlg;
/*    */   
/*    */   public X0016_CertificateIdForCentralDirectory() {
/* 50 */     super(new ZipShort(22));
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
/* 61 */     return this.rcount;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PKWareExtraHeader.HashAlgorithm getHashAlgorithm() {
/* 69 */     return this.hashAlg;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
/* 75 */     assertMinimalLength(4, length);
/*    */     
/* 77 */     this.rcount = ZipShort.getValue(data, offset);
/* 78 */     this.hashAlg = PKWareExtraHeader.HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\X0016_CertificateIdForCentralDirectory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */