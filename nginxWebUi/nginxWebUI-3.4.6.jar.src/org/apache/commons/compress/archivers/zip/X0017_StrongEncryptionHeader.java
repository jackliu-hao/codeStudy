/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X0017_StrongEncryptionHeader
/*     */   extends PKWareExtraHeader
/*     */ {
/*     */   private int format;
/*     */   private PKWareExtraHeader.EncryptionAlgorithm algId;
/*     */   private int bitlen;
/*     */   private int flags;
/*     */   private long rcount;
/*     */   private PKWareExtraHeader.HashAlgorithm hashAlg;
/*     */   private int hashSize;
/*     */   private byte[] ivData;
/*     */   private byte[] erdData;
/*     */   private byte[] recipientKeyHash;
/*     */   private byte[] keyBlob;
/*     */   private byte[] vData;
/*     */   private byte[] vCRC32;
/*     */   
/*     */   public X0017_StrongEncryptionHeader() {
/* 251 */     super(new ZipShort(23));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRecordCount() {
/* 279 */     return this.rcount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PKWareExtraHeader.HashAlgorithm getHashAlgorithm() {
/* 287 */     return this.hashAlg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PKWareExtraHeader.EncryptionAlgorithm getEncryptionAlgorithm() {
/* 295 */     return this.algId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseCentralDirectoryFormat(byte[] data, int offset, int length) throws ZipException {
/* 308 */     assertMinimalLength(12, length);
/*     */     
/* 310 */     this.format = ZipShort.getValue(data, offset);
/* 311 */     this.algId = PKWareExtraHeader.EncryptionAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
/* 312 */     this.bitlen = ZipShort.getValue(data, offset + 4);
/* 313 */     this.flags = ZipShort.getValue(data, offset + 6);
/* 314 */     this.rcount = ZipLong.getValue(data, offset + 8);
/*     */     
/* 316 */     if (this.rcount > 0L) {
/* 317 */       assertMinimalLength(16, length);
/* 318 */       this.hashAlg = PKWareExtraHeader.HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 12));
/* 319 */       this.hashSize = ZipShort.getValue(data, offset + 14);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFileFormat(byte[] data, int offset, int length) throws ZipException {
/* 335 */     assertMinimalLength(4, length);
/* 336 */     int ivSize = ZipShort.getValue(data, offset);
/* 337 */     assertDynamicLengthFits("ivSize", ivSize, 4, length);
/* 338 */     assertMinimalLength(offset + 4, ivSize);
/*     */     
/* 340 */     this.ivData = Arrays.copyOfRange(data, offset + 4, ivSize);
/*     */     
/* 342 */     assertMinimalLength(16 + ivSize, length);
/*     */     
/* 344 */     this.format = ZipShort.getValue(data, offset + ivSize + 6);
/* 345 */     this.algId = PKWareExtraHeader.EncryptionAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + ivSize + 8));
/* 346 */     this.bitlen = ZipShort.getValue(data, offset + ivSize + 10);
/* 347 */     this.flags = ZipShort.getValue(data, offset + ivSize + 12);
/*     */     
/* 349 */     int erdSize = ZipShort.getValue(data, offset + ivSize + 14);
/* 350 */     assertDynamicLengthFits("erdSize", erdSize, ivSize + 16, length);
/* 351 */     assertMinimalLength(offset + ivSize + 16, erdSize);
/* 352 */     this.erdData = Arrays.copyOfRange(data, offset + ivSize + 16, erdSize);
/*     */     
/* 354 */     assertMinimalLength(20 + ivSize + erdSize, length);
/* 355 */     this.rcount = ZipLong.getValue(data, offset + ivSize + 16 + erdSize);
/* 356 */     if (this.rcount == 0L) {
/* 357 */       assertMinimalLength(ivSize + 20 + erdSize + 2, length);
/* 358 */       int vSize = ZipShort.getValue(data, offset + ivSize + 20 + erdSize);
/* 359 */       assertDynamicLengthFits("vSize", vSize, ivSize + 22 + erdSize, length);
/* 360 */       if (vSize < 4) {
/* 361 */         throw new ZipException("Invalid X0017_StrongEncryptionHeader: vSize " + vSize + " is too small to hold CRC");
/*     */       }
/*     */       
/* 364 */       assertMinimalLength(offset + ivSize + 22 + erdSize, vSize - 4);
/* 365 */       this.vData = Arrays.copyOfRange(data, offset + ivSize + 22 + erdSize, vSize - 4);
/* 366 */       assertMinimalLength(offset + ivSize + 22 + erdSize + vSize - 4, 4);
/* 367 */       this.vCRC32 = Arrays.copyOfRange(data, offset + ivSize + 22 + erdSize + vSize - 4, 4);
/*     */     } else {
/* 369 */       assertMinimalLength(ivSize + 20 + erdSize + 6, length);
/* 370 */       this.hashAlg = PKWareExtraHeader.HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + ivSize + 20 + erdSize));
/* 371 */       this.hashSize = ZipShort.getValue(data, offset + ivSize + 22 + erdSize);
/* 372 */       int resize = ZipShort.getValue(data, offset + ivSize + 24 + erdSize);
/*     */       
/* 374 */       if (resize < this.hashSize) {
/* 375 */         throw new ZipException("Invalid X0017_StrongEncryptionHeader: resize " + resize + " is too small to hold hashSize" + this.hashSize);
/*     */       }
/*     */       
/* 378 */       this.recipientKeyHash = new byte[this.hashSize];
/* 379 */       this.keyBlob = new byte[resize - this.hashSize];
/*     */       
/* 381 */       assertDynamicLengthFits("resize", resize, ivSize + 24 + erdSize, length);
/*     */       
/* 383 */       System.arraycopy(data, offset + ivSize + 24 + erdSize, this.recipientKeyHash, 0, this.hashSize);
/* 384 */       System.arraycopy(data, offset + ivSize + 24 + erdSize + this.hashSize, this.keyBlob, 0, resize - this.hashSize);
/*     */       
/* 386 */       assertMinimalLength(ivSize + 26 + erdSize + resize + 2, length);
/* 387 */       int vSize = ZipShort.getValue(data, offset + ivSize + 26 + erdSize + resize);
/* 388 */       if (vSize < 4) {
/* 389 */         throw new ZipException("Invalid X0017_StrongEncryptionHeader: vSize " + vSize + " is too small to hold CRC");
/*     */       }
/*     */ 
/*     */       
/* 393 */       assertDynamicLengthFits("vSize", vSize, ivSize + 22 + erdSize + resize, length);
/*     */       
/* 395 */       this.vData = new byte[vSize - 4];
/* 396 */       this.vCRC32 = new byte[4];
/* 397 */       System.arraycopy(data, offset + ivSize + 22 + erdSize + resize, this.vData, 0, vSize - 4);
/* 398 */       System.arraycopy(data, offset + ivSize + 22 + erdSize + resize + vSize - 4, this.vCRC32, 0, 4);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
/* 407 */     super.parseFromLocalFileData(data, offset, length);
/* 408 */     parseFileFormat(data, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
/* 414 */     super.parseFromCentralDirectoryData(data, offset, length);
/* 415 */     parseCentralDirectoryFormat(data, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   private void assertDynamicLengthFits(String what, int dynamicLength, int prefixLength, int length) throws ZipException {
/* 420 */     if (prefixLength + dynamicLength > length)
/* 421 */       throw new ZipException("Invalid X0017_StrongEncryptionHeader: " + what + " " + dynamicLength + " doesn't fit into " + length + " bytes of data at position " + prefixLength); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\X0017_StrongEncryptionHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */