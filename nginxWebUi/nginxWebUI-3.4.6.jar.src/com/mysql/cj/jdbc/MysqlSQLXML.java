/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLOutputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXResult;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXResult;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ import org.xml.sax.helpers.XMLReaderFactory;
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
/*     */ public class MysqlSQLXML
/*     */   implements SQLXML
/*     */ {
/*     */   private XMLInputFactory inputFactory;
/*     */   private XMLOutputFactory outputFactory;
/*     */   private String stringRep;
/*     */   private ResultSetInternalMethods owningResultSet;
/*     */   private int columnIndexOfXml;
/*     */   private boolean fromResultSet;
/*     */   private boolean isClosed = false;
/*     */   private boolean workingWithResult;
/*     */   private DOMResult asDOMResult;
/*     */   private SAXResult asSAXResult;
/*     */   private SimpleSaxToReader saxToReaderConverter;
/*     */   private StringWriter asStringWriter;
/*     */   private ByteArrayOutputStream asByteArrayOutputStream;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   public MysqlSQLXML(ResultSetInternalMethods owner, int index, ExceptionInterceptor exceptionInterceptor) {
/* 110 */     this.owningResultSet = owner;
/* 111 */     this.columnIndexOfXml = index;
/* 112 */     this.fromResultSet = true;
/* 113 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public MysqlSQLXML(ExceptionInterceptor exceptionInterceptor) {
/* 117 */     this.fromResultSet = false;
/* 118 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public synchronized void free() throws SQLException {
/*     */     try {
/* 123 */       this.stringRep = null;
/* 124 */       this.asDOMResult = null;
/* 125 */       this.asSAXResult = null;
/* 126 */       this.inputFactory = null;
/* 127 */       this.outputFactory = null;
/* 128 */       this.owningResultSet = null;
/* 129 */       this.workingWithResult = false;
/* 130 */       this.isClosed = true; return;
/*     */     } catch (CJException cJException) {
/* 132 */       throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
/*     */     } 
/*     */   } public synchronized String getString() throws SQLException {
/*     */     
/* 136 */     try { checkClosed();
/* 137 */       checkWorkingWithResult();
/*     */       
/* 139 */       if (this.fromResultSet) {
/* 140 */         return this.owningResultSet.getString(this.columnIndexOfXml);
/*     */       }
/*     */       
/* 143 */       return this.stringRep; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private synchronized void checkClosed() throws SQLException {
/* 147 */     if (this.isClosed) {
/* 148 */       throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.0"), this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized void checkWorkingWithResult() throws SQLException {
/* 153 */     if (this.workingWithResult) {
/* 154 */       throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.1"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setString(String str) throws SQLException {
/*     */     
/* 160 */     try { checkClosed();
/* 161 */       checkWorkingWithResult();
/*     */       
/* 163 */       this.stringRep = str;
/* 164 */       this.fromResultSet = false; return; }
/* 165 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public synchronized boolean isEmpty() throws SQLException {
/* 168 */     checkClosed();
/* 169 */     checkWorkingWithResult();
/*     */     
/* 171 */     if (!this.fromResultSet) {
/* 172 */       return (this.stringRep == null || this.stringRep.length() == 0);
/*     */     }
/*     */     
/* 175 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized InputStream getBinaryStream() throws SQLException {
/*     */     
/* 180 */     try { checkClosed();
/* 181 */       checkWorkingWithResult();
/*     */       
/* 183 */       return this.owningResultSet.getBinaryStream(this.columnIndexOfXml); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized Reader getCharacterStream() throws SQLException {
/*     */     
/* 188 */     try { checkClosed();
/* 189 */       checkWorkingWithResult();
/*     */       
/* 191 */       return this.owningResultSet.getCharacterStream(this.columnIndexOfXml); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public <T extends javax.xml.transform.Source> T getSource(Class<T> clazz) throws SQLException {
/*     */     
/* 197 */     try { checkClosed();
/* 198 */       checkWorkingWithResult();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 203 */       if (clazz == null || clazz.equals(SAXSource.class)) {
/*     */         
/*     */         try {
/* 206 */           XMLReader reader = XMLReaderFactory.createXMLReader();
/*     */           
/* 208 */           reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
/* 209 */           setFeature(reader, "http://apache.org/xml/features/disallow-doctype-decl", true);
/* 210 */           setFeature(reader, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
/* 211 */           setFeature(reader, "http://xml.org/sax/features/external-general-entities", false);
/* 212 */           setFeature(reader, "http://xml.org/sax/features/external-parameter-entities", false);
/*     */           
/* 214 */           return (T)new SAXSource(reader, this.fromResultSet ? new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml)) : new InputSource(new StringReader(this.stringRep)));
/*     */         }
/* 216 */         catch (SAXException ex) {
/* 217 */           SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", ex, this.exceptionInterceptor);
/* 218 */           throw sqlEx;
/*     */         } 
/*     */       }
/* 221 */       if (clazz.equals(DOMSource.class)) {
/*     */         try {
/* 223 */           DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
/* 224 */           builderFactory.setNamespaceAware(true);
/*     */ 
/*     */           
/* 227 */           setFeature(builderFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
/* 228 */           setFeature(builderFactory, "http://apache.org/xml/features/disallow-doctype-decl", true);
/* 229 */           setFeature(builderFactory, "http://xml.org/sax/features/external-general-entities", false);
/* 230 */           setFeature(builderFactory, "http://xml.org/sax/features/external-parameter-entities", false);
/* 231 */           setFeature(builderFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
/* 232 */           builderFactory.setXIncludeAware(false);
/* 233 */           builderFactory.setExpandEntityReferences(false);
/*     */           
/* 235 */           builderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
/*     */           
/* 237 */           DocumentBuilder builder = builderFactory.newDocumentBuilder();
/*     */           
/* 239 */           return (T)new DOMSource(builder.parse(this.fromResultSet ? new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml)) : new InputSource(new StringReader(this.stringRep))));
/*     */         }
/* 241 */         catch (Throwable t) {
/* 242 */           SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", t, this.exceptionInterceptor);
/* 243 */           throw sqlEx;
/*     */         } 
/*     */       }
/* 246 */       if (clazz.equals(StreamSource.class)) {
/* 247 */         return (T)new StreamSource(this.fromResultSet ? this.owningResultSet.getCharacterStream(this.columnIndexOfXml) : new StringReader(this.stringRep));
/*     */       }
/* 249 */       if (clazz.equals(StAXSource.class)) {
/*     */         try {
/* 251 */           return (T)new StAXSource(this.inputFactory.createXMLStreamReader(this.fromResultSet ? this.owningResultSet
/* 252 */                 .getCharacterStream(this.columnIndexOfXml) : new StringReader(this.stringRep)));
/* 253 */         } catch (XMLStreamException ex) {
/* 254 */           SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", ex, this.exceptionInterceptor);
/* 255 */           throw sqlEx;
/*     */         } 
/*     */       }
/* 258 */       throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.2", new Object[] { clazz.toString() }), "S1009", this.exceptionInterceptor); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setFeature(Object factory, String name, boolean value) {
/*     */     try {
/* 265 */       if (factory instanceof DocumentBuilderFactory) {
/* 266 */         ((DocumentBuilderFactory)factory).setFeature(name, value);
/* 267 */       } else if (factory instanceof XMLReader) {
/* 268 */         ((XMLReader)factory).setFeature(name, value);
/*     */       } 
/* 270 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized OutputStream setBinaryStream() throws SQLException {
/*     */     
/* 277 */     try { checkClosed();
/* 278 */       checkWorkingWithResult();
/*     */       
/* 280 */       this.workingWithResult = true;
/*     */       
/* 282 */       return setBinaryStreamInternal(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private synchronized OutputStream setBinaryStreamInternal() throws SQLException {
/* 286 */     this.asByteArrayOutputStream = new ByteArrayOutputStream();
/*     */     
/* 288 */     return this.asByteArrayOutputStream;
/*     */   }
/*     */   
/*     */   public synchronized Writer setCharacterStream() throws SQLException {
/*     */     
/* 293 */     try { checkClosed();
/* 294 */       checkWorkingWithResult();
/*     */       
/* 296 */       this.workingWithResult = true;
/*     */       
/* 298 */       return setCharacterStreamInternal(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private synchronized Writer setCharacterStreamInternal() throws SQLException {
/* 302 */     this.asStringWriter = new StringWriter();
/*     */     
/* 304 */     return this.asStringWriter;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized <T extends Result> T setResult(Class<T> clazz) throws SQLException {
/*     */     
/* 310 */     try { checkClosed();
/* 311 */       checkWorkingWithResult();
/*     */       
/* 313 */       this.workingWithResult = true;
/* 314 */       this.asDOMResult = null;
/* 315 */       this.asSAXResult = null;
/* 316 */       this.saxToReaderConverter = null;
/* 317 */       this.stringRep = null;
/* 318 */       this.asStringWriter = null;
/* 319 */       this.asByteArrayOutputStream = null;
/*     */       
/* 321 */       if (clazz == null || clazz.equals(SAXResult.class)) {
/* 322 */         this.saxToReaderConverter = new SimpleSaxToReader();
/*     */         
/* 324 */         this.asSAXResult = new SAXResult(this.saxToReaderConverter);
/*     */         
/* 326 */         return (T)this.asSAXResult;
/* 327 */       }  if (clazz.equals(DOMResult.class)) {
/*     */         
/* 329 */         this.asDOMResult = new DOMResult();
/* 330 */         return (T)this.asDOMResult;
/*     */       } 
/* 332 */       if (clazz.equals(StreamResult.class))
/* 333 */         return (T)new StreamResult(setCharacterStreamInternal()); 
/* 334 */       if (clazz.equals(StAXResult.class)) {
/*     */         try {
/* 336 */           if (this.outputFactory == null) {
/* 337 */             this.outputFactory = XMLOutputFactory.newInstance();
/*     */           }
/*     */           
/* 340 */           return (T)new StAXResult(this.outputFactory.createXMLEventWriter(setCharacterStreamInternal()));
/* 341 */         } catch (XMLStreamException ex) {
/* 342 */           SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", ex, this.exceptionInterceptor);
/* 343 */           throw sqlEx;
/*     */         } 
/*     */       }
/* 346 */       throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.3", new Object[] { clazz.toString() }), "S1009", this.exceptionInterceptor); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out) {
/*     */     try {
/* 357 */       String encoding = "UTF-8";
/*     */       
/*     */       try {
/* 360 */         ByteArrayInputStream bIn = new ByteArrayInputStream(out.toByteArray());
/* 361 */         XMLStreamReader reader = this.inputFactory.createXMLStreamReader(bIn);
/*     */         
/* 363 */         int eventType = 0;
/*     */         
/* 365 */         while ((eventType = reader.next()) != 8) {
/* 366 */           if (eventType == 7) {
/* 367 */             String possibleEncoding = reader.getEncoding();
/*     */             
/* 369 */             if (possibleEncoding != null) {
/* 370 */               encoding = possibleEncoding;
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 376 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */       
/* 380 */       return new StringReader(new String(out.toByteArray(), encoding));
/* 381 */     } catch (UnsupportedEncodingException badEnc) {
/* 382 */       throw new RuntimeException(badEnc);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected String readerToString(Reader reader) throws SQLException {
/* 387 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 389 */     int charsRead = 0;
/*     */     
/* 391 */     char[] charBuf = new char[512];
/*     */     
/*     */     try {
/* 394 */       while ((charsRead = reader.read(charBuf)) != -1) {
/* 395 */         buf.append(charBuf, 0, charsRead);
/*     */       }
/* 397 */     } catch (IOException ioEx) {
/* 398 */       SQLException sqlEx = SQLError.createSQLException(ioEx.getMessage(), "S1009", ioEx, this.exceptionInterceptor);
/* 399 */       throw sqlEx;
/*     */     } 
/*     */     
/* 402 */     return buf.toString();
/*     */   }
/*     */   
/*     */   protected synchronized Reader serializeAsCharacterStream() throws SQLException {
/* 406 */     checkClosed();
/* 407 */     if (this.workingWithResult || this.owningResultSet == null) {
/*     */       
/* 409 */       if (this.stringRep != null) {
/* 410 */         return new StringReader(this.stringRep);
/*     */       }
/*     */       
/* 413 */       if (this.asDOMResult != null) {
/* 414 */         return new StringReader(domSourceToString());
/*     */       }
/*     */       
/* 417 */       if (this.asStringWriter != null) {
/* 418 */         return new StringReader(this.asStringWriter.toString());
/*     */       }
/*     */       
/* 421 */       if (this.asSAXResult != null) {
/* 422 */         return this.saxToReaderConverter.toReader();
/*     */       }
/*     */       
/* 425 */       if (this.asByteArrayOutputStream != null) {
/* 426 */         return binaryInputStreamStreamToReader(this.asByteArrayOutputStream);
/*     */       }
/*     */     } 
/*     */     
/* 430 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */   }
/*     */   
/*     */   protected String domSourceToString() throws SQLException {
/*     */     try {
/* 435 */       DOMSource source = new DOMSource(this.asDOMResult.getNode());
/* 436 */       Transformer identity = TransformerFactory.newInstance().newTransformer();
/* 437 */       StringWriter stringOut = new StringWriter();
/* 438 */       Result result = new StreamResult(stringOut);
/* 439 */       identity.transform(source, result);
/*     */       
/* 441 */       return stringOut.toString();
/* 442 */     } catch (Throwable t) {
/* 443 */       SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", t, this.exceptionInterceptor);
/* 444 */       throw sqlEx;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected synchronized String serializeAsString() throws SQLException {
/* 449 */     checkClosed();
/* 450 */     if (this.workingWithResult) {
/*     */       
/* 452 */       if (this.stringRep != null) {
/* 453 */         return this.stringRep;
/*     */       }
/*     */       
/* 456 */       if (this.asDOMResult != null) {
/* 457 */         return domSourceToString();
/*     */       }
/*     */       
/* 460 */       if (this.asStringWriter != null) {
/* 461 */         return this.asStringWriter.toString();
/*     */       }
/*     */       
/* 464 */       if (this.asSAXResult != null) {
/* 465 */         return readerToString(this.saxToReaderConverter.toReader());
/*     */       }
/*     */       
/* 468 */       if (this.asByteArrayOutputStream != null) {
/* 469 */         return readerToString(binaryInputStreamStreamToReader(this.asByteArrayOutputStream));
/*     */       }
/*     */     } 
/*     */     
/* 473 */     return this.owningResultSet.getString(this.columnIndexOfXml);
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
/*     */   class SimpleSaxToReader
/*     */     extends DefaultHandler
/*     */   {
/* 498 */     StringBuilder buf = new StringBuilder();
/*     */ 
/*     */     
/*     */     public void startDocument() throws SAXException {
/* 502 */       this.buf.append("<?xml version='1.0' encoding='UTF-8'?>");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void endDocument() throws SAXException {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
/* 513 */       this.buf.append("<");
/* 514 */       this.buf.append(qName);
/*     */       
/* 516 */       if (attrs != null) {
/* 517 */         for (int i = 0; i < attrs.getLength(); i++) {
/* 518 */           this.buf.append(" ");
/* 519 */           this.buf.append(attrs.getQName(i)).append("=\"");
/* 520 */           escapeCharsForXml(attrs.getValue(i), true);
/* 521 */           this.buf.append("\"");
/*     */         } 
/*     */       }
/*     */       
/* 525 */       this.buf.append(">");
/*     */     }
/*     */ 
/*     */     
/*     */     public void characters(char[] buffer, int offset, int len) throws SAXException {
/* 530 */       if (!this.inCDATA) {
/* 531 */         escapeCharsForXml(buffer, offset, len, false);
/*     */       } else {
/* 533 */         this.buf.append(buffer, offset, len);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
/* 539 */       characters(ch, start, length);
/*     */     }
/*     */     
/*     */     private boolean inCDATA = false;
/*     */     
/*     */     public void startCDATA() throws SAXException {
/* 545 */       this.buf.append("<![CDATA[");
/* 546 */       this.inCDATA = true;
/*     */     }
/*     */     
/*     */     public void endCDATA() throws SAXException {
/* 550 */       this.inCDATA = false;
/* 551 */       this.buf.append("]]>");
/*     */     }
/*     */ 
/*     */     
/*     */     public void comment(char[] ch, int start, int length) throws SAXException {
/* 556 */       this.buf.append("<!--");
/* 557 */       for (int i = 0; i < length; i++) {
/* 558 */         this.buf.append(ch[start + i]);
/*     */       }
/* 560 */       this.buf.append("-->");
/*     */     }
/*     */ 
/*     */     
/*     */     Reader toReader() {
/* 565 */       return new StringReader(this.buf.toString());
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(String str, boolean isAttributeData) {
/* 569 */       if (str == null) {
/*     */         return;
/*     */       }
/*     */       
/* 573 */       int strLen = str.length();
/*     */       
/* 575 */       for (int i = 0; i < strLen; i++) {
/* 576 */         escapeCharsForXml(str.charAt(i), isAttributeData);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private void escapeCharsForXml(char[] buffer, int offset, int len, boolean isAttributeData) {
/* 582 */       if (buffer == null) {
/*     */         return;
/*     */       }
/*     */       
/* 586 */       for (int i = 0; i < len; i++) {
/* 587 */         escapeCharsForXml(buffer[offset + i], isAttributeData);
/*     */       }
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(char c, boolean isAttributeData) {
/* 592 */       switch (c) {
/*     */         case '<':
/* 594 */           this.buf.append("&lt;");
/*     */           return;
/*     */         
/*     */         case '>':
/* 598 */           this.buf.append("&gt;");
/*     */           return;
/*     */         
/*     */         case '&':
/* 602 */           this.buf.append("&amp;");
/*     */           return;
/*     */ 
/*     */         
/*     */         case '"':
/* 607 */           if (!isAttributeData) {
/* 608 */             this.buf.append("\"");
/*     */           } else {
/* 610 */             this.buf.append("&quot;");
/*     */           } 
/*     */           return;
/*     */ 
/*     */         
/*     */         case '\r':
/* 616 */           this.buf.append("&#xD;");
/*     */           return;
/*     */       } 
/*     */ 
/*     */       
/* 621 */       if ((c >= '\001' && c <= '\037' && c != '\t' && c != '\n') || (c >= '' && c <= '') || c == ' ' || (isAttributeData && (c == '\t' || c == '\n'))) {
/*     */         
/* 623 */         this.buf.append("&#x");
/* 624 */         this.buf.append(Integer.toHexString(c).toUpperCase());
/* 625 */         this.buf.append(";");
/*     */       } else {
/* 627 */         this.buf.append(c);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlSQLXML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */