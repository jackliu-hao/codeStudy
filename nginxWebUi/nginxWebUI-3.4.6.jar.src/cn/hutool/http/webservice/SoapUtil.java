/*    */ package cn.hutool.http.webservice;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.XmlUtil;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.nio.charset.Charset;
/*    */ import javax.xml.soap.SOAPException;
/*    */ import javax.xml.soap.SOAPMessage;
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
/*    */ public class SoapUtil
/*    */ {
/*    */   public static SoapClient createClient(String url) {
/* 30 */     return SoapClient.create(url);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SoapClient createClient(String url, SoapProtocol protocol) {
/* 41 */     return SoapClient.create(url, protocol);
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
/*    */   public static SoapClient createClient(String url, SoapProtocol protocol, String namespaceURI) {
/* 54 */     return SoapClient.create(url, protocol, namespaceURI);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String toString(SOAPMessage message, boolean pretty) {
/* 65 */     return toString(message, pretty, CharsetUtil.CHARSET_UTF_8);
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
/*    */   public static String toString(SOAPMessage message, boolean pretty, Charset charset) {
/*    */     String messageToString;
/* 78 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*    */     try {
/* 80 */       message.writeTo(out);
/* 81 */     } catch (SOAPException|java.io.IOException e) {
/* 82 */       throw new SoapRuntimeException(e);
/*    */     } 
/*    */     
/*    */     try {
/* 86 */       messageToString = out.toString(charset.toString());
/* 87 */     } catch (UnsupportedEncodingException e) {
/* 88 */       throw new UtilException(e);
/*    */     } 
/* 90 */     return pretty ? XmlUtil.format(messageToString) : messageToString;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\webservice\SoapUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */