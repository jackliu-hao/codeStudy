/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.nio.charset.UnsupportedCharsetException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ZipEncodingHelper
/*    */ {
/*    */   static final String UTF8 = "UTF8";
/* 40 */   static final ZipEncoding UTF8_ZIP_ENCODING = getZipEncoding("UTF8");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ZipEncoding getZipEncoding(String name) {
/* 55 */     Charset cs = Charset.defaultCharset();
/* 56 */     if (name != null) {
/*    */       try {
/* 58 */         cs = Charset.forName(name);
/* 59 */       } catch (UnsupportedCharsetException unsupportedCharsetException) {}
/*    */     }
/*    */     
/* 62 */     boolean useReplacement = isUTF8(cs.name());
/* 63 */     return new NioZipEncoding(cs, useReplacement);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static boolean isUTF8(String charsetName) {
/* 72 */     if (charsetName == null)
/*    */     {
/* 74 */       charsetName = Charset.defaultCharset().name();
/*    */     }
/* 76 */     if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
/* 77 */       return true;
/*    */     }
/* 79 */     for (String alias : StandardCharsets.UTF_8.aliases()) {
/* 80 */       if (alias.equalsIgnoreCase(charsetName)) {
/* 81 */         return true;
/*    */       }
/*    */     } 
/* 84 */     return false;
/*    */   }
/*    */   
/*    */   static ByteBuffer growBufferBy(ByteBuffer buffer, int increment) {
/* 88 */     buffer.limit(buffer.position());
/* 89 */     buffer.rewind();
/*    */     
/* 91 */     ByteBuffer on = ByteBuffer.allocate(buffer.capacity() + increment);
/*    */     
/* 93 */     on.put(buffer);
/* 94 */     return on;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipEncodingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */