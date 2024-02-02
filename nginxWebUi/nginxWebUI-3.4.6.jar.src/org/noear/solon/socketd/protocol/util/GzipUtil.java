/*    */ package org.noear.solon.socketd.protocol.util;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.zip.GZIPInputStream;
/*    */ import java.util.zip.GZIPOutputStream;
/*    */ 
/*    */ public final class GzipUtil {
/*    */   public static ByteArrayOutputStream compressDo(byte[] bytes) throws IOException {
/* 11 */     if (bytes == null || bytes.length == 0) {
/* 12 */       return null;
/*    */     }
/*    */     
/* 15 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*    */     
/* 17 */     try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
/* 18 */       gzip.write(bytes);
/*    */     } 
/*    */     
/* 21 */     return out;
/*    */   }
/*    */ 
/*    */   
/*    */   public static byte[] compress(byte[] bytes) throws IOException {
/* 26 */     return compressDo(bytes).toByteArray();
/*    */   }
/*    */ 
/*    */   
/*    */   public static ByteArrayOutputStream uncompressDo(byte[] bytes) throws IOException {
/* 31 */     if (bytes == null || bytes.length == 0) {
/* 32 */       return null;
/*    */     }
/*    */     
/* 35 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 36 */     ByteArrayInputStream in = new ByteArrayInputStream(bytes);
/*    */     
/* 38 */     GZIPInputStream ungzip = new GZIPInputStream(in);
/* 39 */     byte[] buffer = new byte[256];
/*    */     int n;
/* 41 */     while ((n = ungzip.read(buffer)) >= 0) {
/* 42 */       out.write(buffer, 0, n);
/*    */     }
/*    */     
/* 45 */     return out;
/*    */   }
/*    */   
/*    */   public static byte[] uncompress(byte[] bytes) throws IOException {
/* 49 */     if (bytes == null) {
/* 50 */       return null;
/*    */     }
/*    */     
/* 53 */     ByteArrayOutputStream tmp = uncompressDo(bytes);
/* 54 */     if (tmp == null) {
/* 55 */       return null;
/*    */     }
/* 57 */     return tmp.toByteArray();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\protoco\\util\GzipUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */