/*    */ package org.codehaus.plexus.util.xml;
/*    */ 
/*    */ import java.io.InputStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XmlStreamReaderException
/*    */   extends XmlReaderException
/*    */ {
/*    */   public XmlStreamReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
/* 54 */     super(msg, bomEnc, xmlGuessEnc, xmlEnc, is);
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
/*    */   public XmlStreamReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
/* 82 */     super(msg, ctMime, ctEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XmlStreamReaderException.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */