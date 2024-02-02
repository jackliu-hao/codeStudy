/*    */ package META-INF.versions.10.org.h2.util;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.Socket;
/*    */ import java.nio.charset.Charset;
/*    */ import jdk.net.ExtendedSocketOptions;
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
/* 33 */     return paramByteArrayOutputStream.toString(paramCharset);
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
/* 48 */     return ((Boolean)paramSocket.<Boolean>getOption(ExtendedSocketOptions.TCP_QUICKACK)).booleanValue();
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
/*    */   public static boolean setTcpQuickack(Socket paramSocket, boolean paramBoolean) {
/*    */     try {
/* 62 */       paramSocket.setOption(ExtendedSocketOptions.TCP_QUICKACK, Boolean.valueOf(paramBoolean));
/* 63 */       return true;
/* 64 */     } catch (Throwable throwable) {
/* 65 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\10\org\h\\util\Utils10.class
 * Java compiler version: 10 (54.0)
 * JD-Core Version:       1.1.3
 */