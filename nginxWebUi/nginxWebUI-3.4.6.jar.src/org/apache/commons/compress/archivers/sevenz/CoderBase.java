/*    */ package org.apache.commons.compress.archivers.sevenz;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.commons.compress.utils.ByteUtils;
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
/*    */ abstract class CoderBase
/*    */ {
/*    */   private final Class<?>[] acceptableOptions;
/*    */   
/*    */   protected CoderBase(Class<?>... acceptableOptions) {
/* 35 */     this.acceptableOptions = acceptableOptions;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean canAcceptOptions(Object opts) {
/* 42 */     for (Class<?> c : this.acceptableOptions) {
/* 43 */       if (c.isInstance(opts)) {
/* 44 */         return true;
/*    */       }
/*    */     } 
/* 47 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   byte[] getOptionsAsProperties(Object options) throws IOException {
/* 54 */     return ByteUtils.EMPTY_BYTE_ARRAY;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
/* 61 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   abstract InputStream decode(String paramString, InputStream paramInputStream, long paramLong, Coder paramCoder, byte[] paramArrayOfbyte, int paramInt) throws IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   OutputStream encode(OutputStream out, Object options) throws IOException {
/* 75 */     throw new UnsupportedOperationException("Method doesn't support writing");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected static int numberOptionOrDefault(Object options, int defaultValue) {
/* 83 */     return (options instanceof Number) ? ((Number)options).intValue() : defaultValue;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\CoderBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */