/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public abstract class AbstractUnicodeExtraField
/*     */   implements ZipExtraField
/*     */ {
/*     */   private long nameCRC32;
/*     */   private byte[] unicodeName;
/*     */   private byte[] data;
/*     */   
/*     */   protected AbstractUnicodeExtraField() {}
/*     */   
/*     */   protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len) {
/*  50 */     CRC32 crc32 = new CRC32();
/*  51 */     crc32.update(bytes, off, len);
/*  52 */     this.nameCRC32 = crc32.getValue();
/*     */     
/*  54 */     this.unicodeName = text.getBytes(StandardCharsets.UTF_8);
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
/*     */   protected AbstractUnicodeExtraField(String text, byte[] bytes) {
/*  66 */     this(text, bytes, 0, bytes.length);
/*     */   }
/*     */   
/*     */   private void assembleData() {
/*  70 */     if (this.unicodeName == null) {
/*     */       return;
/*     */     }
/*     */     
/*  74 */     this.data = new byte[5 + this.unicodeName.length];
/*     */     
/*  76 */     this.data[0] = 1;
/*  77 */     System.arraycopy(ZipLong.getBytes(this.nameCRC32), 0, this.data, 1, 4);
/*  78 */     System.arraycopy(this.unicodeName, 0, this.data, 5, this.unicodeName.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNameCRC32() {
/*  86 */     return this.nameCRC32;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNameCRC32(long nameCRC32) {
/*  94 */     this.nameCRC32 = nameCRC32;
/*  95 */     this.data = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getUnicodeName() {
/* 102 */     byte[] b = null;
/* 103 */     if (this.unicodeName != null) {
/* 104 */       b = new byte[this.unicodeName.length];
/* 105 */       System.arraycopy(this.unicodeName, 0, b, 0, b.length);
/*     */     } 
/* 107 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUnicodeName(byte[] unicodeName) {
/* 114 */     if (unicodeName != null) {
/* 115 */       this.unicodeName = new byte[unicodeName.length];
/* 116 */       System.arraycopy(unicodeName, 0, this.unicodeName, 0, unicodeName.length);
/*     */     } else {
/*     */       
/* 119 */       this.unicodeName = null;
/*     */     } 
/* 121 */     this.data = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getCentralDirectoryData() {
/* 126 */     if (this.data == null) {
/* 127 */       assembleData();
/*     */     }
/* 129 */     byte[] b = null;
/* 130 */     if (this.data != null) {
/* 131 */       b = new byte[this.data.length];
/* 132 */       System.arraycopy(this.data, 0, b, 0, b.length);
/*     */     } 
/* 134 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getCentralDirectoryLength() {
/* 139 */     if (this.data == null) {
/* 140 */       assembleData();
/*     */     }
/* 142 */     return new ZipShort((this.data != null) ? this.data.length : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getLocalFileDataData() {
/* 147 */     return getCentralDirectoryData();
/*     */   }
/*     */ 
/*     */   
/*     */   public ZipShort getLocalFileDataLength() {
/* 152 */     return getCentralDirectoryLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
/* 159 */     if (length < 5) {
/* 160 */       throw new ZipException("UniCode path extra data must have at least 5 bytes.");
/*     */     }
/*     */     
/* 163 */     int version = buffer[offset];
/*     */     
/* 165 */     if (version != 1) {
/* 166 */       throw new ZipException("Unsupported version [" + version + "] for UniCode path extra data.");
/*     */     }
/*     */ 
/*     */     
/* 170 */     this.nameCRC32 = ZipLong.getValue(buffer, offset + 1);
/* 171 */     this.unicodeName = new byte[length - 5];
/* 172 */     System.arraycopy(buffer, offset + 5, this.unicodeName, 0, length - 5);
/* 173 */     this.data = null;
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
/* 184 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\AbstractUnicodeExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */