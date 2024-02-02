/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.zip.ZipException;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Zip64ExtendedInformationExtraField
/*     */   implements ZipExtraField
/*     */ {
/*  47 */   static final ZipShort HEADER_ID = new ZipShort(1);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String LFH_MUST_HAVE_BOTH_SIZES_MSG = "Zip64 extended information must contain both size values in the local file header.";
/*     */ 
/*     */ 
/*     */   
/*     */   private ZipEightByteInteger size;
/*     */ 
/*     */ 
/*     */   
/*     */   private ZipEightByteInteger compressedSize;
/*     */ 
/*     */ 
/*     */   
/*     */   private ZipEightByteInteger relativeHeaderOffset;
/*     */ 
/*     */ 
/*     */   
/*     */   private ZipLong diskStart;
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] rawCentralDirectoryData;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Zip64ExtendedInformationExtraField() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize) {
/*  83 */     this(size, compressedSize, null, null);
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
/*     */   public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize, ZipEightByteInteger relativeHeaderOffset, ZipLong diskStart) {
/* 100 */     this.size = size;
/* 101 */     this.compressedSize = compressedSize;
/* 102 */     this.relativeHeaderOffset = relativeHeaderOffset;
/* 103 */     this.diskStart = diskStart;
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/* 108 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/* 113 */     return new ZipShort((this.size != null) ? 16 : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/* 118 */     return new ZipShort(((this.size != null) ? 8 : 0) + ((this.compressedSize != null) ? 8 : 0) + ((this.relativeHeaderOffset != null) ? 8 : 0) + ((this.diskStart != null) ? 4 : 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getLocalFileDataData() {
/* 126 */     if (this.size != null || this.compressedSize != null) {
/* 127 */       if (this.size == null || this.compressedSize == null) {
/* 128 */         throw new IllegalArgumentException("Zip64 extended information must contain both size values in the local file header.");
/*     */       }
/* 130 */       byte[] data = new byte[16];
/* 131 */       addSizes(data);
/* 132 */       return data;
/*     */     } 
/* 134 */     return ByteUtils.EMPTY_BYTE_ARRAY;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 139 */     byte[] data = new byte[getCentralDirectoryLength().getValue()];
/* 140 */     int off = addSizes(data);
/* 141 */     if (this.relativeHeaderOffset != null) {
/* 142 */       System.arraycopy(this.relativeHeaderOffset.getBytes(), 0, data, off, 8);
/* 143 */       off += 8;
/*     */     } 
/* 145 */     if (this.diskStart != null) {
/* 146 */       System.arraycopy(this.diskStart.getBytes(), 0, data, off, 4);
/* 147 */       off += 4;
/*     */     } 
/* 149 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
/* 155 */     if (length == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     if (length < 16) {
/* 163 */       throw new ZipException("Zip64 extended information must contain both size values in the local file header.");
/*     */     }
/* 165 */     this.size = new ZipEightByteInteger(buffer, offset);
/* 166 */     offset += 8;
/* 167 */     this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 168 */     offset += 8;
/* 169 */     int remaining = length - 16;
/* 170 */     if (remaining >= 8) {
/* 171 */       this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 172 */       offset += 8;
/* 173 */       remaining -= 8;
/*     */     } 
/* 175 */     if (remaining >= 4) {
/* 176 */       this.diskStart = new ZipLong(buffer, offset);
/* 177 */       offset += 4;
/* 178 */       remaining -= 4;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
/* 187 */     this.rawCentralDirectoryData = new byte[length];
/* 188 */     System.arraycopy(buffer, offset, this.rawCentralDirectoryData, 0, length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 196 */     if (length >= 28) {
/* 197 */       parseFromLocalFileData(buffer, offset, length);
/* 198 */     } else if (length == 24) {
/* 199 */       this.size = new ZipEightByteInteger(buffer, offset);
/* 200 */       offset += 8;
/* 201 */       this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 202 */       offset += 8;
/* 203 */       this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 204 */     } else if (length % 8 == 4) {
/* 205 */       this.diskStart = new ZipLong(buffer, offset + length - 4);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reparseCentralDirectoryData(boolean hasUncompressedSize, boolean hasCompressedSize, boolean hasRelativeHeaderOffset, boolean hasDiskStart) throws ZipException {
/* 229 */     if (this.rawCentralDirectoryData != null) {
/* 230 */       int expectedLength = (hasUncompressedSize ? 8 : 0) + (hasCompressedSize ? 8 : 0) + (hasRelativeHeaderOffset ? 8 : 0) + (hasDiskStart ? 4 : 0);
/*     */ 
/*     */ 
/*     */       
/* 234 */       if (this.rawCentralDirectoryData.length < expectedLength) {
/* 235 */         throw new ZipException("Central directory zip64 extended information extra field's length doesn't match central directory data.  Expected length " + expectedLength + " but is " + this.rawCentralDirectoryData.length);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 242 */       int offset = 0;
/* 243 */       if (hasUncompressedSize) {
/* 244 */         this.size = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 245 */         offset += 8;
/*     */       } 
/* 247 */       if (hasCompressedSize) {
/* 248 */         this.compressedSize = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/*     */         
/* 250 */         offset += 8;
/*     */       } 
/* 252 */       if (hasRelativeHeaderOffset) {
/* 253 */         this.relativeHeaderOffset = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/*     */         
/* 255 */         offset += 8;
/*     */       } 
/* 257 */       if (hasDiskStart) {
/* 258 */         this.diskStart = new ZipLong(this.rawCentralDirectoryData, offset);
/* 259 */         offset += 4;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEightByteInteger getSize() {
/* 269 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSize(ZipEightByteInteger size) {
/* 277 */     this.size = size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEightByteInteger getCompressedSize() {
/* 285 */     return this.compressedSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCompressedSize(ZipEightByteInteger compressedSize) {
/* 293 */     this.compressedSize = compressedSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEightByteInteger getRelativeHeaderOffset() {
/* 301 */     return this.relativeHeaderOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRelativeHeaderOffset(ZipEightByteInteger rho) {
/* 309 */     this.relativeHeaderOffset = rho;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong getDiskStartNumber() {
/* 317 */     return this.diskStart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDiskStartNumber(ZipLong ds) {
/* 325 */     this.diskStart = ds;
/*     */   }
/*     */   
/*     */   private int addSizes(byte[] data) {
/* 329 */     int off = 0;
/* 330 */     if (this.size != null) {
/* 331 */       System.arraycopy(this.size.getBytes(), 0, data, 0, 8);
/* 332 */       off += 8;
/*     */     } 
/* 334 */     if (this.compressedSize != null) {
/* 335 */       System.arraycopy(this.compressedSize.getBytes(), 0, data, off, 8);
/* 336 */       off += 8;
/*     */     } 
/* 338 */     return off;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\Zip64ExtendedInformationExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */