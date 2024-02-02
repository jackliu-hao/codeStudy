/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UnparseableExtraFieldData
/*     */   implements ZipExtraField
/*     */ {
/*  34 */   private static final ZipShort HEADER_ID = new ZipShort(44225);
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] localFileData;
/*     */ 
/*     */   
/*     */   private byte[] centralDirectoryData;
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/*  46 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/*  56 */     return new ZipShort((this.localFileData == null) ? 0 : this.localFileData.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/*  66 */     return (this.centralDirectoryData == null) ? 
/*  67 */       getLocalFileDataLength() : new ZipShort(this.centralDirectoryData.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getLocalFileDataData() {
/*  78 */     return ZipUtil.copy(this.localFileData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/*  88 */     return (this.centralDirectoryData == null) ? 
/*  89 */       getLocalFileDataData() : ZipUtil.copy(this.centralDirectoryData);
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
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length) {
/* 101 */     this.localFileData = Arrays.copyOfRange(buffer, offset, offset + length);
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
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) {
/* 114 */     this.centralDirectoryData = Arrays.copyOfRange(buffer, offset, offset + length);
/* 115 */     if (this.localFileData == null)
/* 116 */       parseFromLocalFileData(buffer, offset, length); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnparseableExtraFieldData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */