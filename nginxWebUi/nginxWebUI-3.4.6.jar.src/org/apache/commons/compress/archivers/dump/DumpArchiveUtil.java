/*     */ package org.apache.commons.compress.archivers.dump;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
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
/*     */ class DumpArchiveUtil
/*     */ {
/*     */   public static int calculateChecksum(byte[] buffer) {
/*  43 */     int calc = 0;
/*     */     
/*  45 */     for (int i = 0; i < 256; i++) {
/*  46 */       calc += convert32(buffer, 4 * i);
/*     */     }
/*     */     
/*  49 */     return 84446 - calc - 
/*  50 */       convert32(buffer, 28);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean verify(byte[] buffer) {
/*  60 */     int magic = convert32(buffer, 24);
/*     */     
/*  62 */     if (magic != 60012) {
/*  63 */       return false;
/*     */     }
/*     */ 
/*     */     
/*  67 */     int checksum = convert32(buffer, 28);
/*     */     
/*  69 */     return (checksum == calculateChecksum(buffer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int getIno(byte[] buffer) {
/*  78 */     return convert32(buffer, 20);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final long convert64(byte[] buffer, int offset) {
/*  89 */     return ByteUtils.fromLittleEndian(buffer, offset, 8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int convert32(byte[] buffer, int offset) {
/* 100 */     return (int)ByteUtils.fromLittleEndian(buffer, offset, 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int convert16(byte[] buffer, int offset) {
/* 111 */     return (int)ByteUtils.fromLittleEndian(buffer, offset, 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String decode(ZipEncoding encoding, byte[] b, int offset, int len) throws IOException {
/* 119 */     return encoding.decode(Arrays.copyOfRange(b, offset, offset + len));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\DumpArchiveUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */