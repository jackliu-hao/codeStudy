/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class XmlReaderException
/*     */   extends IOException
/*     */ {
/*     */   private String _bomEncoding;
/*     */   private String _xmlGuessEncoding;
/*     */   private String _xmlEncoding;
/*     */   private String _contentTypeMime;
/*     */   private String _contentTypeEncoding;
/*     */   private InputStream _is;
/*     */   
/*     */   public XmlReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
/*  67 */     this(msg, null, null, bomEnc, xmlGuessEnc, xmlEnc, is);
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
/*     */   public XmlReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
/*  95 */     super(msg);
/*  96 */     this._contentTypeMime = ctMime;
/*  97 */     this._contentTypeEncoding = ctEnc;
/*  98 */     this._bomEncoding = bomEnc;
/*  99 */     this._xmlGuessEncoding = xmlGuessEnc;
/* 100 */     this._xmlEncoding = xmlEnc;
/* 101 */     this._is = is;
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
/*     */   public String getBomEncoding() {
/* 113 */     return this._bomEncoding;
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
/*     */   public String getXmlGuessEncoding() {
/* 125 */     return this._xmlGuessEncoding;
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
/*     */   public String getXmlEncoding() {
/* 137 */     return this._xmlEncoding;
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
/*     */   
/*     */   public String getContentTypeMime() {
/* 150 */     return this._contentTypeMime;
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
/*     */   
/*     */   public String getContentTypeEncoding() {
/* 163 */     return this._contentTypeEncoding;
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
/*     */   
/*     */   public InputStream getInputStream() {
/* 176 */     return this._is;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XmlReaderException.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */