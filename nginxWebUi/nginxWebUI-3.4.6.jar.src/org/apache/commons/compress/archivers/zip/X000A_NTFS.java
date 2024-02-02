/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.Date;
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
/*     */ public class X000A_NTFS
/*     */   implements ZipExtraField
/*     */ {
/*  67 */   private static final ZipShort HEADER_ID = new ZipShort(10);
/*  68 */   private static final ZipShort TIME_ATTR_TAG = new ZipShort(1);
/*  69 */   private static final ZipShort TIME_ATTR_SIZE = new ZipShort(24);
/*     */   
/*  71 */   private ZipEightByteInteger modifyTime = ZipEightByteInteger.ZERO;
/*  72 */   private ZipEightByteInteger accessTime = ZipEightByteInteger.ZERO;
/*  73 */   private ZipEightByteInteger createTime = ZipEightByteInteger.ZERO;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long EPOCH_OFFSET = -116444736000000000L;
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/*  82 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/*  93 */     return new ZipShort(32);
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
/*     */   public ZipShort getCentralDirectoryLength() {
/* 111 */     return getLocalFileDataLength();
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
/* 122 */     byte[] data = new byte[getLocalFileDataLength().getValue()];
/* 123 */     int pos = 4;
/* 124 */     System.arraycopy(TIME_ATTR_TAG.getBytes(), 0, data, pos, 2);
/* 125 */     pos += 2;
/* 126 */     System.arraycopy(TIME_ATTR_SIZE.getBytes(), 0, data, pos, 2);
/* 127 */     pos += 2;
/* 128 */     System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 8);
/* 129 */     pos += 8;
/* 130 */     System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 8);
/* 131 */     pos += 8;
/* 132 */     System.arraycopy(this.createTime.getBytes(), 0, data, pos, 8);
/* 133 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 144 */     return getLocalFileDataData();
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
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
/* 159 */     int len = offset + length;
/*     */ 
/*     */     
/* 162 */     offset += 4;
/*     */     
/* 164 */     while (offset + 4 <= len) {
/* 165 */       ZipShort tag = new ZipShort(data, offset);
/* 166 */       offset += 2;
/* 167 */       if (tag.equals(TIME_ATTR_TAG)) {
/* 168 */         readTimeAttr(data, offset, len - offset);
/*     */         break;
/*     */       } 
/* 171 */       ZipShort size = new ZipShort(data, offset);
/* 172 */       offset += 2 + size.getValue();
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
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
/* 184 */     reset();
/* 185 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEightByteInteger getModifyTime() {
/* 196 */     return this.modifyTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEightByteInteger getAccessTime() {
/* 205 */     return this.accessTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipEightByteInteger getCreateTime() {
/* 214 */     return this.createTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getModifyJavaTime() {
/* 223 */     return zipToDate(this.modifyTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getAccessJavaTime() {
/* 233 */     return zipToDate(this.accessTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getCreateJavaTime() {
/* 243 */     return zipToDate(this.createTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setModifyTime(ZipEightByteInteger t) {
/* 253 */     this.modifyTime = (t == null) ? ZipEightByteInteger.ZERO : t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAccessTime(ZipEightByteInteger t) {
/* 263 */     this.accessTime = (t == null) ? ZipEightByteInteger.ZERO : t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreateTime(ZipEightByteInteger t) {
/* 273 */     this.createTime = (t == null) ? ZipEightByteInteger.ZERO : t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setModifyJavaTime(Date d) {
/* 281 */     setModifyTime(dateToZip(d));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAccessJavaTime(Date d) {
/* 289 */     setAccessTime(dateToZip(d));
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
/*     */   public void setCreateJavaTime(Date d) {
/* 304 */     setCreateTime(dateToZip(d));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 315 */     StringBuilder buf = new StringBuilder();
/* 316 */     buf.append("0x000A Zip Extra Field:")
/* 317 */       .append(" Modify:[").append(getModifyJavaTime()).append("] ")
/* 318 */       .append(" Access:[").append(getAccessJavaTime()).append("] ")
/* 319 */       .append(" Create:[").append(getCreateJavaTime()).append("] ");
/* 320 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 325 */     if (o instanceof X000A_NTFS) {
/* 326 */       X000A_NTFS xf = (X000A_NTFS)o;
/*     */       
/* 328 */       return ((this.modifyTime == xf.modifyTime || (this.modifyTime != null && this.modifyTime.equals(xf.modifyTime))) && (this.accessTime == xf.accessTime || (this.accessTime != null && this.accessTime
/* 329 */         .equals(xf.accessTime))) && (this.createTime == xf.createTime || (this.createTime != null && this.createTime
/* 330 */         .equals(xf.createTime))));
/*     */     } 
/* 332 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 337 */     int hc = -123;
/* 338 */     if (this.modifyTime != null) {
/* 339 */       hc ^= this.modifyTime.hashCode();
/*     */     }
/* 341 */     if (this.accessTime != null)
/*     */     {
/*     */       
/* 344 */       hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
/*     */     }
/* 346 */     if (this.createTime != null) {
/* 347 */       hc ^= Integer.rotateLeft(this.createTime.hashCode(), 22);
/*     */     }
/* 349 */     return hc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reset() {
/* 357 */     this.modifyTime = ZipEightByteInteger.ZERO;
/* 358 */     this.accessTime = ZipEightByteInteger.ZERO;
/* 359 */     this.createTime = ZipEightByteInteger.ZERO;
/*     */   }
/*     */   
/*     */   private void readTimeAttr(byte[] data, int offset, int length) {
/* 363 */     if (length >= 26) {
/* 364 */       ZipShort tagValueLength = new ZipShort(data, offset);
/* 365 */       if (TIME_ATTR_SIZE.equals(tagValueLength)) {
/* 366 */         offset += 2;
/* 367 */         this.modifyTime = new ZipEightByteInteger(data, offset);
/* 368 */         offset += 8;
/* 369 */         this.accessTime = new ZipEightByteInteger(data, offset);
/* 370 */         offset += 8;
/* 371 */         this.createTime = new ZipEightByteInteger(data, offset);
/*     */       } 
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
/*     */   private static ZipEightByteInteger dateToZip(Date d) {
/* 384 */     if (d == null) return null; 
/* 385 */     return new ZipEightByteInteger(d.getTime() * 10000L - -116444736000000000L);
/*     */   }
/*     */   
/*     */   private static Date zipToDate(ZipEightByteInteger z) {
/* 389 */     if (z == null || ZipEightByteInteger.ZERO.equals(z)) return null; 
/* 390 */     long l = (z.getLongValue() + -116444736000000000L) / 10000L;
/* 391 */     return new Date(l);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\X000A_NTFS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */