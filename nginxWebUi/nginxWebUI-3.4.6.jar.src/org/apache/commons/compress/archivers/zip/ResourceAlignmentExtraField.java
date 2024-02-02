/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
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
/*     */ public class ResourceAlignmentExtraField
/*     */   implements ZipExtraField
/*     */ {
/*  43 */   public static final ZipShort ID = new ZipShort(41246);
/*     */   
/*     */   public static final int BASE_SIZE = 2;
/*     */   
/*     */   private static final int ALLOW_METHOD_MESSAGE_CHANGE_FLAG = 32768;
/*     */   
/*     */   private short alignment;
/*     */   
/*     */   private boolean allowMethodChange;
/*     */   
/*     */   private int padding;
/*     */ 
/*     */   
/*     */   public ResourceAlignmentExtraField() {}
/*     */   
/*     */   public ResourceAlignmentExtraField(int alignment) {
/*  59 */     this(alignment, false);
/*     */   }
/*     */   
/*     */   public ResourceAlignmentExtraField(int alignment, boolean allowMethodChange) {
/*  63 */     this(alignment, allowMethodChange, 0);
/*     */   }
/*     */   
/*     */   public ResourceAlignmentExtraField(int alignment, boolean allowMethodChange, int padding) {
/*  67 */     if (alignment < 0 || alignment > 32767) {
/*  68 */       throw new IllegalArgumentException("Alignment must be between 0 and 0x7fff, was: " + alignment);
/*     */     }
/*  70 */     if (padding < 0) {
/*  71 */       throw new IllegalArgumentException("Padding must not be negative, was: " + padding);
/*     */     }
/*  73 */     this.alignment = (short)alignment;
/*  74 */     this.allowMethodChange = allowMethodChange;
/*  75 */     this.padding = padding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short getAlignment() {
/*  85 */     return this.alignment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowMethodChange() {
/*  95 */     return this.allowMethodChange;
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/* 100 */     return ID;
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/* 105 */     return new ZipShort(2 + this.padding);
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/* 110 */     return new ZipShort(2);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getLocalFileDataData() {
/* 115 */     byte[] content = new byte[2 + this.padding];
/* 116 */     ZipShort.putShort(this.alignment | (this.allowMethodChange ? 32768 : 0), content, 0);
/*     */     
/* 118 */     return content;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 123 */     return ZipShort.getBytes(this.alignment | (this.allowMethodChange ? 32768 : 0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
/* 128 */     parseFromCentralDirectoryData(buffer, offset, length);
/* 129 */     this.padding = length - 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
/* 134 */     if (length < 2) {
/* 135 */       throw new ZipException("Too short content for ResourceAlignmentExtraField (0xa11e): " + length);
/*     */     }
/* 137 */     int alignmentValue = ZipShort.getValue(buffer, offset);
/* 138 */     this.alignment = (short)(alignmentValue & 0x7FFF);
/* 139 */     this.allowMethodChange = ((alignmentValue & 0x8000) != 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ResourceAlignmentExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */