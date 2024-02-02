/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public abstract class PKWareExtraHeader
/*     */   implements ZipExtraField
/*     */ {
/*     */   private final ZipShort headerId;
/*     */   private byte[] localData;
/*     */   private byte[] centralData;
/*     */   
/*     */   protected PKWareExtraHeader(ZipShort headerId) {
/*  83 */     this.headerId = headerId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getHeaderId() {
/*  93 */     return this.headerId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocalFileDataData(byte[] data) {
/* 104 */     this.localData = ZipUtil.copy(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/* 114 */     return new ZipShort((this.localData != null) ? this.localData.length : 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getLocalFileDataData() {
/* 124 */     return ZipUtil.copy(this.localData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentralDirectoryData(byte[] data) {
/* 134 */     this.centralData = ZipUtil.copy(data);
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
/* 145 */     if (this.centralData != null) {
/* 146 */       return new ZipShort(this.centralData.length);
/*     */     }
/* 148 */     return getLocalFileDataLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 158 */     if (this.centralData != null) {
/* 159 */       return ZipUtil.copy(this.centralData);
/*     */     }
/* 161 */     return getLocalFileDataData();
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
/* 176 */     setLocalFileDataData(Arrays.copyOfRange(data, offset, offset + length));
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
/*     */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
/* 191 */     byte[] tmp = Arrays.copyOfRange(data, offset, offset + length);
/* 192 */     setCentralDirectoryData(tmp);
/* 193 */     if (this.localData == null) {
/* 194 */       setLocalFileDataData(tmp);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void assertMinimalLength(int minimum, int length) throws ZipException {
/* 200 */     if (length < minimum) {
/* 201 */       throw new ZipException(getClass().getName() + " is too short, only " + length + " bytes, expected at least " + minimum);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum EncryptionAlgorithm
/*     */   {
/* 212 */     DES(26113),
/* 213 */     RC2pre52(26114),
/* 214 */     TripleDES168(26115),
/* 215 */     TripleDES192(26121),
/* 216 */     AES128(26126),
/* 217 */     AES192(26127),
/* 218 */     AES256(26128),
/* 219 */     RC2(26370),
/* 220 */     RC4(26625),
/* 221 */     UNKNOWN(65535);
/*     */     
/*     */     private final int code;
/*     */     
/*     */     private static final Map<Integer, EncryptionAlgorithm> codeToEnum;
/*     */     
/*     */     static {
/* 228 */       Map<Integer, EncryptionAlgorithm> cte = new HashMap<>();
/* 229 */       for (EncryptionAlgorithm method : values()) {
/* 230 */         cte.put(Integer.valueOf(method.getCode()), method);
/*     */       }
/* 232 */       codeToEnum = Collections.unmodifiableMap(cte);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     EncryptionAlgorithm(int code) {
/* 239 */       this.code = code;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getCode() {
/* 248 */       return this.code;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static EncryptionAlgorithm getAlgorithmByCode(int code) {
/* 259 */       return codeToEnum.get(Integer.valueOf(code));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum HashAlgorithm
/*     */   {
/* 269 */     NONE(0),
/* 270 */     CRC32(1),
/* 271 */     MD5(32771),
/* 272 */     SHA1(32772),
/* 273 */     RIPEND160(32775),
/* 274 */     SHA256(32780),
/* 275 */     SHA384(32781),
/* 276 */     SHA512(32782);
/*     */     
/*     */     private final int code;
/*     */     
/*     */     private static final Map<Integer, HashAlgorithm> codeToEnum;
/*     */     
/*     */     static {
/* 283 */       Map<Integer, HashAlgorithm> cte = new HashMap<>();
/* 284 */       for (HashAlgorithm method : values()) {
/* 285 */         cte.put(Integer.valueOf(method.getCode()), method);
/*     */       }
/* 287 */       codeToEnum = Collections.unmodifiableMap(cte);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     HashAlgorithm(int code) {
/* 294 */       this.code = code;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getCode() {
/* 303 */       return this.code;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static HashAlgorithm getAlgorithmByCode(int code) {
/* 314 */       return codeToEnum.get(Integer.valueOf(code));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\PKWareExtraHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */