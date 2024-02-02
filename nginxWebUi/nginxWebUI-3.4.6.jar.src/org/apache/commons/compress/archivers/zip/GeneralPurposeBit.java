/*     */ package org.apache.commons.compress.archivers.zip;
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
/*     */ public final class GeneralPurposeBit
/*     */   implements Cloneable
/*     */ {
/*     */   private static final int ENCRYPTION_FLAG = 1;
/*     */   private static final int SLIDING_DICTIONARY_SIZE_FLAG = 2;
/*     */   private static final int NUMBER_OF_SHANNON_FANO_TREES_FLAG = 4;
/*     */   private static final int DATA_DESCRIPTOR_FLAG = 8;
/*     */   private static final int STRONG_ENCRYPTION_FLAG = 64;
/*     */   public static final int UFT8_NAMES_FLAG = 2048;
/*     */   private boolean languageEncodingFlag;
/*     */   private boolean dataDescriptorFlag;
/*     */   private boolean encryptionFlag;
/*     */   private boolean strongEncryptionFlag;
/*     */   private int slidingDictionarySize;
/*     */   private int numberOfShannonFanoTrees;
/*     */   
/*     */   public boolean usesUTF8ForNames() {
/*  87 */     return this.languageEncodingFlag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void useUTF8ForNames(boolean b) {
/*  95 */     this.languageEncodingFlag = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesDataDescriptor() {
/* 105 */     return this.dataDescriptorFlag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void useDataDescriptor(boolean b) {
/* 115 */     this.dataDescriptorFlag = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesEncryption() {
/* 123 */     return this.encryptionFlag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void useEncryption(boolean b) {
/* 131 */     this.encryptionFlag = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesStrongEncryption() {
/* 139 */     return (this.encryptionFlag && this.strongEncryptionFlag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void useStrongEncryption(boolean b) {
/* 147 */     this.strongEncryptionFlag = b;
/* 148 */     if (b) {
/* 149 */       useEncryption(true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getSlidingDictionarySize() {
/* 157 */     return this.slidingDictionarySize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getNumberOfShannonFanoTrees() {
/* 164 */     return this.numberOfShannonFanoTrees;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode() {
/* 172 */     byte[] result = new byte[2];
/* 173 */     encode(result, 0);
/* 174 */     return result;
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
/*     */   public void encode(byte[] buf, int offset) {
/* 187 */     ZipShort.putShort((this.dataDescriptorFlag ? 8 : 0) | (this.languageEncodingFlag ? 2048 : 0) | (this.encryptionFlag ? 1 : 0) | (this.strongEncryptionFlag ? 64 : 0), buf, offset);
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
/*     */   public static GeneralPurposeBit parse(byte[] data, int offset) {
/* 205 */     int generalPurposeFlag = ZipShort.getValue(data, offset);
/* 206 */     GeneralPurposeBit b = new GeneralPurposeBit();
/* 207 */     b.useDataDescriptor(((generalPurposeFlag & 0x8) != 0));
/* 208 */     b.useUTF8ForNames(((generalPurposeFlag & 0x800) != 0));
/* 209 */     b.useStrongEncryption(((generalPurposeFlag & 0x40) != 0));
/* 210 */     b.useEncryption(((generalPurposeFlag & 0x1) != 0));
/* 211 */     b.slidingDictionarySize = ((generalPurposeFlag & 0x2) != 0) ? 8192 : 4096;
/* 212 */     b.numberOfShannonFanoTrees = ((generalPurposeFlag & 0x4) != 0) ? 3 : 2;
/* 213 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 218 */     return 3 * (7 * (13 * (17 * (this.encryptionFlag ? 1 : 0) + (this.strongEncryptionFlag ? 1 : 0)) + (this.languageEncodingFlag ? 1 : 0)) + (this.dataDescriptorFlag ? 1 : 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 226 */     if (!(o instanceof GeneralPurposeBit)) {
/* 227 */       return false;
/*     */     }
/* 229 */     GeneralPurposeBit g = (GeneralPurposeBit)o;
/* 230 */     return (g.encryptionFlag == this.encryptionFlag && g.strongEncryptionFlag == this.strongEncryptionFlag && g.languageEncodingFlag == this.languageEncodingFlag && g.dataDescriptorFlag == this.dataDescriptorFlag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 239 */       return super.clone();
/* 240 */     } catch (CloneNotSupportedException ex) {
/*     */       
/* 242 */       throw new RuntimeException("GeneralPurposeBit is not Cloneable?", ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\GeneralPurposeBit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */