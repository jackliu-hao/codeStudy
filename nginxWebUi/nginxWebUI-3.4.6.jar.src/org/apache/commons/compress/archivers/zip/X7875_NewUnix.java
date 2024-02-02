/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X7875_NewUnix
/*     */   implements ZipExtraField, Cloneable, Serializable
/*     */ {
/*  60 */   private static final ZipShort HEADER_ID = new ZipShort(30837);
/*  61 */   private static final ZipShort ZERO = new ZipShort(0);
/*  62 */   private static final BigInteger ONE_THOUSAND = BigInteger.valueOf(1000L);
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*  65 */   private int version = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private BigInteger uid;
/*     */ 
/*     */ 
/*     */   
/*     */   private BigInteger gid;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public X7875_NewUnix() {
/*  79 */     reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/*  89 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getUID() {
/* 100 */     return ZipUtil.bigToLong(this.uid);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getGID() {
/* 110 */     return ZipUtil.bigToLong(this.gid);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUID(long l) {
/* 118 */     this.uid = ZipUtil.longToBig(l);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGID(long l) {
/* 127 */     this.gid = ZipUtil.longToBig(l);
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
/* 138 */     byte[] b = trimLeadingZeroesForceMinLength(this.uid.toByteArray());
/* 139 */     int uidSize = (b == null) ? 0 : b.length;
/* 140 */     b = trimLeadingZeroesForceMinLength(this.gid.toByteArray());
/* 141 */     int gidSize = (b == null) ? 0 : b.length;
/*     */ 
/*     */     
/* 144 */     return new ZipShort(3 + uidSize + gidSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/* 155 */     return ZERO;
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
/* 166 */     byte[] uidBytes = this.uid.toByteArray();
/* 167 */     byte[] gidBytes = this.gid.toByteArray();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 172 */     uidBytes = trimLeadingZeroesForceMinLength(uidBytes);
/* 173 */     int uidBytesLen = (uidBytes != null) ? uidBytes.length : 0;
/* 174 */     gidBytes = trimLeadingZeroesForceMinLength(gidBytes);
/* 175 */     int gidBytesLen = (gidBytes != null) ? gidBytes.length : 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 182 */     byte[] data = new byte[3 + uidBytesLen + gidBytesLen];
/*     */ 
/*     */     
/* 185 */     if (uidBytes != null) {
/* 186 */       ZipUtil.reverse(uidBytes);
/*     */     }
/* 188 */     if (gidBytes != null) {
/* 189 */       ZipUtil.reverse(gidBytes);
/*     */     }
/*     */     
/* 192 */     int pos = 0;
/* 193 */     data[pos++] = ZipUtil.unsignedIntToSignedByte(this.version);
/* 194 */     data[pos++] = ZipUtil.unsignedIntToSignedByte(uidBytesLen);
/* 195 */     if (uidBytes != null) {
/* 196 */       System.arraycopy(uidBytes, 0, data, pos, uidBytesLen);
/*     */     }
/* 198 */     pos += uidBytesLen;
/* 199 */     data[pos++] = ZipUtil.unsignedIntToSignedByte(gidBytesLen);
/* 200 */     if (gidBytes != null) {
/* 201 */       System.arraycopy(gidBytes, 0, data, pos, gidBytesLen);
/*     */     }
/* 203 */     return data;
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
/* 214 */     return ByteUtils.EMPTY_BYTE_ARRAY;
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
/* 229 */     reset();
/* 230 */     if (length < 3) {
/* 231 */       throw new ZipException("X7875_NewUnix length is too short, only " + length + " bytes");
/*     */     }
/*     */     
/* 234 */     this.version = ZipUtil.signedByteToUnsignedInt(data[offset++]);
/* 235 */     int uidSize = ZipUtil.signedByteToUnsignedInt(data[offset++]);
/* 236 */     if (uidSize + 3 > length) {
/* 237 */       throw new ZipException("X7875_NewUnix invalid: uidSize " + uidSize + " doesn't fit into " + length + " bytes");
/*     */     }
/*     */     
/* 240 */     byte[] uidBytes = Arrays.copyOfRange(data, offset, offset + uidSize);
/* 241 */     offset += uidSize;
/* 242 */     this.uid = new BigInteger(1, ZipUtil.reverse(uidBytes));
/*     */     
/* 244 */     int gidSize = ZipUtil.signedByteToUnsignedInt(data[offset++]);
/* 245 */     if (uidSize + 3 + gidSize > length) {
/* 246 */       throw new ZipException("X7875_NewUnix invalid: gidSize " + gidSize + " doesn't fit into " + length + " bytes");
/*     */     }
/*     */     
/* 249 */     byte[] gidBytes = Arrays.copyOfRange(data, offset, offset + gidSize);
/* 250 */     this.gid = new BigInteger(1, ZipUtil.reverse(gidBytes));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reset() {
/* 269 */     this.uid = ONE_THOUSAND;
/* 270 */     this.gid = ONE_THOUSAND;
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
/* 282 */     return "0x7875 Zip Extra Field: UID=" + this.uid + " GID=" + this.gid;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 287 */     return super.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 292 */     if (o instanceof X7875_NewUnix) {
/* 293 */       X7875_NewUnix xf = (X7875_NewUnix)o;
/*     */       
/* 295 */       return (this.version == xf.version && this.uid.equals(xf.uid) && this.gid.equals(xf.gid));
/*     */     } 
/* 297 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 302 */     int hc = -1234567 * this.version;
/*     */ 
/*     */ 
/*     */     
/* 306 */     hc ^= Integer.rotateLeft(this.uid.hashCode(), 16);
/* 307 */     hc ^= this.gid.hashCode();
/* 308 */     return hc;
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
/*     */   static byte[] trimLeadingZeroesForceMinLength(byte[] array) {
/* 321 */     if (array == null) {
/* 322 */       return array;
/*     */     }
/*     */     
/* 325 */     int pos = 0;
/* 326 */     for (byte b : array) {
/* 327 */       if (b != 0) {
/*     */         break;
/*     */       }
/* 330 */       pos++;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 370 */     int MIN_LENGTH = 1;
/*     */     
/* 372 */     byte[] trimmedArray = new byte[Math.max(1, array.length - pos)];
/* 373 */     int startPos = trimmedArray.length - array.length - pos;
/* 374 */     System.arraycopy(array, pos, trimmedArray, startPos, trimmedArray.length - startPos);
/* 375 */     return trimmedArray;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\X7875_NewUnix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */