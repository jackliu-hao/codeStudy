/*    */ package org.xnio.streams;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.UnsupportedCharsetException;
/*    */ import org.xnio.IoUtils;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Streams
/*    */ {
/*    */   public static void copyStream(InputStream input, OutputStream output, boolean close, int bufferSize) throws IOException {
/* 50 */     byte[] buffer = new byte[bufferSize];
/*    */     
/*    */     try {
/*    */       while (true) {
/* 54 */         int res = input.read(buffer);
/* 55 */         if (res == -1) {
/* 56 */           if (close) {
/* 57 */             input.close();
/* 58 */             output.close();
/*    */           } 
/*    */           return;
/*    */         } 
/* 62 */         output.write(buffer, 0, res);
/*    */       } 
/*    */     } finally {
/* 65 */       if (close) {
/* 66 */         IoUtils.safeClose(input);
/* 67 */         IoUtils.safeClose(output);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void copyStream(InputStream input, OutputStream output, boolean close) throws IOException {
/* 81 */     copyStream(input, output, close, 8192);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void copyStream(InputStream input, OutputStream output) throws IOException {
/* 92 */     copyStream(input, output, true, 8192);
/*    */   }
/*    */   
/*    */   static Charset getCharset(String charsetName) throws UnsupportedEncodingException {
/*    */     try {
/* 97 */       return Charset.forName(charsetName);
/* 98 */     } catch (UnsupportedCharsetException e) {
/* 99 */       throw new UnsupportedEncodingException(e.getMessage());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\Streams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */