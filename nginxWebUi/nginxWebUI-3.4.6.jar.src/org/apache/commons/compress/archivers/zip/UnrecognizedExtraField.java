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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnrecognizedExtraField
/*     */   implements ZipExtraField
/*     */ {
/*     */   private ZipShort headerId;
/*     */   private byte[] localData;
/*     */   private byte[] centralData;
/*     */   
/*     */   public void setHeaderId(ZipShort headerId) {
/*  43 */     this.headerId = headerId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/*  52 */     return this.headerId;
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
/*     */   public void setLocalFileDataData(byte[] data) {
/*  67 */     this.localData = ZipUtil.copy(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/*  76 */     return new ZipShort((this.localData != null) ? this.localData.length : 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getLocalFileDataData() {
/*  85 */     return ZipUtil.copy(this.localData);
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
/*     */   public void setCentralDirectoryData(byte[] data) {
/*  99 */     this.centralData = ZipUtil.copy(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/* 109 */     if (this.centralData != null) {
/* 110 */       return new ZipShort(this.centralData.length);
/*     */     }
/* 112 */     return getLocalFileDataLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 121 */     if (this.centralData != null) {
/* 122 */       return ZipUtil.copy(this.centralData);
/*     */     }
/* 124 */     return getLocalFileDataData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length) {
/* 135 */     setLocalFileDataData(Arrays.copyOfRange(data, offset, offset + length));
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
/*     */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) {
/* 147 */     byte[] tmp = Arrays.copyOfRange(data, offset, offset + length);
/* 148 */     setCentralDirectoryData(tmp);
/* 149 */     if (this.localData == null)
/* 150 */       setLocalFileDataData(tmp); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnrecognizedExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */