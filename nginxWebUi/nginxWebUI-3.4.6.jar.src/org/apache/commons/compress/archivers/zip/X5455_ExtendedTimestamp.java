/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X5455_ExtendedTimestamp
/*     */   implements ZipExtraField, Cloneable, Serializable
/*     */ {
/*  85 */   private static final ZipShort HEADER_ID = new ZipShort(21589);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final byte MODIFY_TIME_BIT = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final byte ACCESS_TIME_BIT = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final byte CREATE_TIME_BIT = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   private byte flags;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bit0_modifyTimePresent;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bit1_accessTimePresent;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bit2_createTimePresent;
/*     */ 
/*     */ 
/*     */   
/*     */   private ZipLong modifyTime;
/*     */ 
/*     */   
/*     */   private ZipLong accessTime;
/*     */ 
/*     */   
/*     */   private ZipLong createTime;
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/* 132 */     return HEADER_ID;
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
/* 143 */     return new ZipShort(1 + (this.bit0_modifyTimePresent ? 4 : 0) + ((this.bit1_accessTimePresent && this.accessTime != null) ? 4 : 0) + ((this.bit2_createTimePresent && this.createTime != null) ? 4 : 0));
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
/*     */   public ZipShort getCentralDirectoryLength() {
/* 162 */     return new ZipShort(1 + (this.bit0_modifyTimePresent ? 4 : 0));
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
/*     */   public byte[] getLocalFileDataData() {
/* 175 */     byte[] data = new byte[getLocalFileDataLength().getValue()];
/* 176 */     int pos = 0;
/* 177 */     data[pos++] = 0;
/* 178 */     if (this.bit0_modifyTimePresent) {
/* 179 */       data[0] = (byte)(data[0] | 0x1);
/* 180 */       System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 4);
/* 181 */       pos += 4;
/*     */     } 
/* 183 */     if (this.bit1_accessTimePresent && this.accessTime != null) {
/* 184 */       data[0] = (byte)(data[0] | 0x2);
/* 185 */       System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 4);
/* 186 */       pos += 4;
/*     */     } 
/* 188 */     if (this.bit2_createTimePresent && this.createTime != null) {
/* 189 */       data[0] = (byte)(data[0] | 0x4);
/* 190 */       System.arraycopy(this.createTime.getBytes(), 0, data, pos, 4);
/* 191 */       pos += 4;
/*     */     } 
/* 193 */     return data;
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
/*     */   public byte[] getCentralDirectoryData() {
/* 206 */     return Arrays.copyOf(getLocalFileDataData(), getCentralDirectoryLength().getValue());
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
/* 221 */     reset();
/* 222 */     if (length < 1) {
/* 223 */       throw new ZipException("X5455_ExtendedTimestamp too short, only " + length + " bytes");
/*     */     }
/* 225 */     int len = offset + length;
/* 226 */     setFlags(data[offset++]);
/* 227 */     if (this.bit0_modifyTimePresent && offset + 4 <= len) {
/* 228 */       this.modifyTime = new ZipLong(data, offset);
/* 229 */       offset += 4;
/*     */     } else {
/* 231 */       this.bit0_modifyTimePresent = false;
/*     */     } 
/* 233 */     if (this.bit1_accessTimePresent && offset + 4 <= len) {
/* 234 */       this.accessTime = new ZipLong(data, offset);
/* 235 */       offset += 4;
/*     */     } else {
/* 237 */       this.bit1_accessTimePresent = false;
/*     */     } 
/* 239 */     if (this.bit2_createTimePresent && offset + 4 <= len) {
/* 240 */       this.createTime = new ZipLong(data, offset);
/* 241 */       offset += 4;
/*     */     } else {
/* 243 */       this.bit2_createTimePresent = false;
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
/* 255 */     reset();
/* 256 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reset() {
/* 264 */     setFlags((byte)0);
/* 265 */     this.modifyTime = null;
/* 266 */     this.accessTime = null;
/* 267 */     this.createTime = null;
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
/*     */   public void setFlags(byte flags) {
/* 285 */     this.flags = flags;
/* 286 */     this.bit0_modifyTimePresent = ((flags & 0x1) == 1);
/* 287 */     this.bit1_accessTimePresent = ((flags & 0x2) == 2);
/* 288 */     this.bit2_createTimePresent = ((flags & 0x4) == 4);
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
/*     */   public byte getFlags() {
/* 305 */     return this.flags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBit0_modifyTimePresent() {
/* 314 */     return this.bit0_modifyTimePresent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBit1_accessTimePresent() {
/* 323 */     return this.bit1_accessTimePresent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBit2_createTimePresent() {
/* 332 */     return this.bit2_createTimePresent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong getModifyTime() {
/* 341 */     return this.modifyTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipLong getAccessTime() {
/* 350 */     return this.accessTime;
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
/*     */   public ZipLong getCreateTime() {
/* 365 */     return this.createTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getModifyJavaTime() {
/* 376 */     return zipLongToDate(this.modifyTime);
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
/*     */   public Date getAccessJavaTime() {
/* 388 */     return zipLongToDate(this.accessTime);
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
/*     */   public Date getCreateJavaTime() {
/* 406 */     return zipLongToDate(this.createTime);
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
/*     */   public void setModifyTime(ZipLong l) {
/* 422 */     this.bit0_modifyTimePresent = (l != null);
/* 423 */     this.flags = (byte)((l != null) ? (this.flags | 0x1) : (this.flags & 0xFFFFFFFE));
/*     */     
/* 425 */     this.modifyTime = l;
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
/*     */   public void setAccessTime(ZipLong l) {
/* 441 */     this.bit1_accessTimePresent = (l != null);
/* 442 */     this.flags = (byte)((l != null) ? (this.flags | 0x2) : (this.flags & 0xFFFFFFFD));
/*     */     
/* 444 */     this.accessTime = l;
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
/*     */   public void setCreateTime(ZipLong l) {
/* 460 */     this.bit2_createTimePresent = (l != null);
/* 461 */     this.flags = (byte)((l != null) ? (this.flags | 0x4) : (this.flags & 0xFFFFFFFB));
/*     */     
/* 463 */     this.createTime = l;
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
/*     */   public void setModifyJavaTime(Date d) {
/* 479 */     setModifyTime(dateToZipLong(d));
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
/*     */   public void setAccessJavaTime(Date d) {
/* 494 */     setAccessTime(dateToZipLong(d));
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
/* 509 */     setCreateTime(dateToZipLong(d));
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
/*     */   private static ZipLong dateToZipLong(Date d) {
/* 522 */     if (d == null) return null;
/*     */     
/* 524 */     return unixTimeToZipLong(d.getTime() / 1000L);
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
/*     */   public String toString() {
/* 536 */     StringBuilder buf = new StringBuilder();
/* 537 */     buf.append("0x5455 Zip Extra Field: Flags=");
/* 538 */     buf.append(Integer.toBinaryString(ZipUtil.unsignedIntToSignedByte(this.flags))).append(" ");
/* 539 */     if (this.bit0_modifyTimePresent && this.modifyTime != null) {
/* 540 */       Date m = getModifyJavaTime();
/* 541 */       buf.append(" Modify:[").append(m).append("] ");
/*     */     } 
/* 543 */     if (this.bit1_accessTimePresent && this.accessTime != null) {
/* 544 */       Date a = getAccessJavaTime();
/* 545 */       buf.append(" Access:[").append(a).append("] ");
/*     */     } 
/* 547 */     if (this.bit2_createTimePresent && this.createTime != null) {
/* 548 */       Date c = getCreateJavaTime();
/* 549 */       buf.append(" Create:[").append(c).append("] ");
/*     */     } 
/* 551 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 556 */     return super.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 561 */     if (o instanceof X5455_ExtendedTimestamp) {
/* 562 */       X5455_ExtendedTimestamp xf = (X5455_ExtendedTimestamp)o;
/*     */ 
/*     */ 
/*     */       
/* 566 */       return ((this.flags & 0x7) == (xf.flags & 0x7) && (this.modifyTime == xf.modifyTime || (this.modifyTime != null && this.modifyTime
/* 567 */         .equals(xf.modifyTime))) && (this.accessTime == xf.accessTime || (this.accessTime != null && this.accessTime
/* 568 */         .equals(xf.accessTime))) && (this.createTime == xf.createTime || (this.createTime != null && this.createTime
/* 569 */         .equals(xf.createTime))));
/*     */     } 
/* 571 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 576 */     int hc = -123 * (this.flags & 0x7);
/* 577 */     if (this.modifyTime != null) {
/* 578 */       hc ^= this.modifyTime.hashCode();
/*     */     }
/* 580 */     if (this.accessTime != null)
/*     */     {
/*     */       
/* 583 */       hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
/*     */     }
/* 585 */     if (this.createTime != null) {
/* 586 */       hc ^= Integer.rotateLeft(this.createTime.hashCode(), 22);
/*     */     }
/* 588 */     return hc;
/*     */   }
/*     */   
/*     */   private static Date zipLongToDate(ZipLong unixTime) {
/* 592 */     return (unixTime != null) ? new Date(unixTime.getIntValue() * 1000L) : null;
/*     */   }
/*     */   
/*     */   private static ZipLong unixTimeToZipLong(long l) {
/* 596 */     if (l < -2147483648L || l > 2147483647L) {
/* 597 */       throw new IllegalArgumentException("X5455 timestamps must fit in a signed 32 bit integer: " + l);
/*     */     }
/* 599 */     return new ZipLong(l);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\X5455_ExtendedTimestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */