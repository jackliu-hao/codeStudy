/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.zip.CRC32;
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
/*     */ public class AsiExtraField
/*     */   implements ZipExtraField, UnixStat, Cloneable
/*     */ {
/*  57 */   private static final ZipShort HEADER_ID = new ZipShort(30062);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MIN_SIZE = 14;
/*     */ 
/*     */ 
/*     */   
/*     */   private int mode;
/*     */ 
/*     */ 
/*     */   
/*     */   private int uid;
/*     */ 
/*     */ 
/*     */   
/*     */   private int gid;
/*     */ 
/*     */   
/*  76 */   private String link = "";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean dirFlag;
/*     */ 
/*     */ 
/*     */   
/*  85 */   private CRC32 crc = new CRC32();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/*  97 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/* 107 */     return new ZipShort(14 + (
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 112 */         getLinkedFile().getBytes()).length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/* 122 */     return getLocalFileDataLength();
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
/* 133 */     byte[] data = new byte[getLocalFileDataLength().getValue() - 4];
/* 134 */     System.arraycopy(ZipShort.getBytes(getMode()), 0, data, 0, 2);
/*     */     
/* 136 */     byte[] linkArray = getLinkedFile().getBytes();
/*     */     
/* 138 */     System.arraycopy(ZipLong.getBytes(linkArray.length), 0, data, 2, 4);
/*     */ 
/*     */     
/* 141 */     System.arraycopy(ZipShort.getBytes(getUserId()), 0, data, 6, 2);
/*     */     
/* 143 */     System.arraycopy(ZipShort.getBytes(getGroupId()), 0, data, 8, 2);
/*     */ 
/*     */     
/* 146 */     System.arraycopy(linkArray, 0, data, 10, linkArray.length);
/*     */ 
/*     */     
/* 149 */     this.crc.reset();
/* 150 */     this.crc.update(data);
/* 151 */     long checksum = this.crc.getValue();
/*     */     
/* 153 */     byte[] result = new byte[data.length + 4];
/* 154 */     System.arraycopy(ZipLong.getBytes(checksum), 0, result, 0, 4);
/* 155 */     System.arraycopy(data, 0, result, 4, data.length);
/* 156 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 165 */     return getLocalFileDataData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserId(int uid) {
/* 173 */     this.uid = uid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUserId() {
/* 181 */     return this.uid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroupId(int gid) {
/* 189 */     this.gid = gid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGroupId() {
/* 197 */     return this.gid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLinkedFile(String name) {
/* 207 */     this.link = name;
/* 208 */     this.mode = getMode(this.mode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLinkedFile() {
/* 218 */     return this.link;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLink() {
/* 226 */     return !getLinkedFile().isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMode(int mode) {
/* 234 */     this.mode = getMode(mode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode() {
/* 242 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDirectory(boolean dirFlag) {
/* 250 */     this.dirFlag = dirFlag;
/* 251 */     this.mode = getMode(this.mode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 259 */     return (this.dirFlag && !isLink());
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
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
/* 272 */     if (length < 14) {
/* 273 */       throw new ZipException("The length is too short, only " + length + " bytes, expected at least " + '\016');
/*     */     }
/*     */ 
/*     */     
/* 277 */     long givenChecksum = ZipLong.getValue(data, offset);
/* 278 */     byte[] tmp = new byte[length - 4];
/* 279 */     System.arraycopy(data, offset + 4, tmp, 0, length - 4);
/* 280 */     this.crc.reset();
/* 281 */     this.crc.update(tmp);
/* 282 */     long realChecksum = this.crc.getValue();
/* 283 */     if (givenChecksum != realChecksum) {
/* 284 */       throw new ZipException("Bad CRC checksum, expected " + 
/* 285 */           Long.toHexString(givenChecksum) + " instead of " + 
/*     */           
/* 287 */           Long.toHexString(realChecksum));
/*     */     }
/*     */     
/* 290 */     int newMode = ZipShort.getValue(tmp, 0);
/*     */     
/* 292 */     int linkArrayLength = (int)ZipLong.getValue(tmp, 2);
/* 293 */     if (linkArrayLength < 0 || linkArrayLength > tmp.length - 10) {
/* 294 */       throw new ZipException("Bad symbolic link name length " + linkArrayLength + " in ASI extra field");
/*     */     }
/*     */     
/* 297 */     this.uid = ZipShort.getValue(tmp, 6);
/* 298 */     this.gid = ZipShort.getValue(tmp, 8);
/* 299 */     if (linkArrayLength == 0) {
/* 300 */       this.link = "";
/*     */     } else {
/* 302 */       byte[] linkArray = new byte[linkArrayLength];
/* 303 */       System.arraycopy(tmp, 10, linkArray, 0, linkArrayLength);
/* 304 */       this.link = new String(linkArray);
/*     */     } 
/*     */     
/* 307 */     setDirectory(((newMode & 0x4000) != 0));
/* 308 */     setMode(newMode);
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
/* 319 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getMode(int mode) {
/* 328 */     int type = 32768;
/* 329 */     if (isLink()) {
/* 330 */       type = 40960;
/* 331 */     } else if (isDirectory()) {
/* 332 */       type = 16384;
/*     */     } 
/* 334 */     return type | mode & 0xFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 340 */       AsiExtraField cloned = (AsiExtraField)super.clone();
/* 341 */       cloned.crc = new CRC32();
/* 342 */       return cloned;
/* 343 */     } catch (CloneNotSupportedException cnfe) {
/*     */       
/* 345 */       throw new RuntimeException(cnfe);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\AsiExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */