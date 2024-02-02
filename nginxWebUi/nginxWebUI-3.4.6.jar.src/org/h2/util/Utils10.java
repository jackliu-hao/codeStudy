/*    */ package org.h2.util;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.net.Socket;
/*    */ import java.nio.charset.Charset;
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
/*    */ public final class Utils10
/*    */ {
/*    */   public static String byteArrayOutputStreamToString(ByteArrayOutputStream paramByteArrayOutputStream, Charset paramCharset) {
/*    */     try {
/* 40 */       return paramByteArrayOutputStream.toString(paramCharset.name());
/* 41 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 42 */       throw new RuntimeException(unsupportedEncodingException);
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
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean getTcpQuickack(Socket paramSocket) throws IOException {
/* 58 */     throw new UnsupportedOperationException();
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
/*    */ 
/*    */   
/*    */   public static boolean setTcpQuickack(Socket paramSocket, boolean paramBoolean) {
/* 72 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\Utils10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */