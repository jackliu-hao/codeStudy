/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLOutputFactory;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.URIResolver;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXResult;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.sax.SAXTransformerFactory;
/*     */ import javax.xml.transform.sax.TransformerHandler;
/*     */ import javax.xml.transform.stax.StAXResult;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.Value;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.DefaultHandler;
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
/*     */ public final class JdbcSQLXML
/*     */   extends JdbcLob
/*     */   implements SQLXML
/*     */ {
/*  58 */   private static final Map<String, Boolean> secureFeatureMap = new HashMap<>();
/*     */   private static final EntityResolver NOOP_ENTITY_RESOLVER = (paramString1, paramString2) -> new InputSource(new StringReader(""));
/*     */   private static final URIResolver NOOP_URI_RESOLVER = (paramString1, paramString2) -> new StreamSource(new StringReader(""));
/*     */   
/*     */   static {
/*  63 */     secureFeatureMap.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.valueOf(true));
/*  64 */     secureFeatureMap.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.valueOf(true));
/*  65 */     secureFeatureMap.put("http://xml.org/sax/features/external-general-entities", Boolean.valueOf(false));
/*  66 */     secureFeatureMap.put("http://xml.org/sax/features/external-parameter-entities", Boolean.valueOf(false));
/*  67 */     secureFeatureMap.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.valueOf(false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DOMResult domResult;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Closeable closable;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdbcSQLXML(JdbcConnection paramJdbcConnection, Value paramValue, JdbcLob.State paramState, int paramInt) {
/*  85 */     super(paramJdbcConnection, paramValue, paramState, 17, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   void checkReadable() throws SQLException, IOException {
/*  90 */     checkClosed();
/*  91 */     if (this.state == JdbcLob.State.SET_CALLED) {
/*  92 */       if (this.domResult != null) {
/*  93 */         Node node = this.domResult.getNode();
/*  94 */         this.domResult = null;
/*  95 */         TransformerFactory transformerFactory = TransformerFactory.newInstance();
/*     */         try {
/*  97 */           Transformer transformer = transformerFactory.newTransformer();
/*  98 */           DOMSource dOMSource = new DOMSource(node);
/*  99 */           StringWriter stringWriter = new StringWriter();
/* 100 */           StreamResult streamResult = new StreamResult(stringWriter);
/* 101 */           transformer.transform(dOMSource, streamResult);
/* 102 */           completeWrite(this.conn.createClob(new StringReader(stringWriter.toString()), -1L));
/* 103 */         } catch (Exception exception) {
/* 104 */           throw logAndConvert(exception);
/*     */         }  return;
/*     */       } 
/* 107 */       if (this.closable != null) {
/* 108 */         this.closable.close();
/* 109 */         this.closable = null;
/*     */         return;
/*     */       } 
/* 112 */       throw DbException.getUnsupportedException("Stream setter is not yet closed.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getBinaryStream() throws SQLException {
/* 118 */     return super.getBinaryStream();
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getCharacterStream() throws SQLException {
/* 123 */     return super.getCharacterStream();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Source> T getSource(Class<T> paramClass) throws SQLException {
/*     */     try {
/* 130 */       if (isDebugEnabled()) {
/* 131 */         debugCode("getSource(" + ((paramClass != null) ? (paramClass
/* 132 */             .getSimpleName() + ".class") : "null") + ')');
/*     */       }
/* 134 */       checkReadable();
/*     */       
/* 136 */       if (paramClass == null || paramClass == DOMSource.class) {
/* 137 */         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 138 */         for (Map.Entry<String, Boolean> entry : secureFeatureMap.entrySet()) {
/*     */           try {
/* 140 */             documentBuilderFactory.setFeature((String)entry.getKey(), ((Boolean)entry.getValue()).booleanValue());
/* 141 */           } catch (Exception exception) {}
/*     */         } 
/* 143 */         documentBuilderFactory.setXIncludeAware(false);
/* 144 */         documentBuilderFactory.setExpandEntityReferences(false);
/* 145 */         documentBuilderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
/* 146 */         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
/* 147 */         documentBuilder.setEntityResolver(NOOP_ENTITY_RESOLVER);
/* 148 */         return (T)new DOMSource(documentBuilder.parse(new InputSource(this.value.getInputStream())));
/* 149 */       }  if (paramClass == SAXSource.class) {
/* 150 */         SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
/* 151 */         for (Map.Entry<String, Boolean> entry : secureFeatureMap.entrySet()) {
/*     */           try {
/* 153 */             sAXParserFactory.setFeature((String)entry.getKey(), ((Boolean)entry.getValue()).booleanValue());
/* 154 */           } catch (Exception exception) {}
/*     */         } 
/* 156 */         XMLReader xMLReader = sAXParserFactory.newSAXParser().getXMLReader();
/* 157 */         xMLReader.setEntityResolver(NOOP_ENTITY_RESOLVER);
/* 158 */         return (T)new SAXSource(xMLReader, new InputSource(this.value.getInputStream()));
/* 159 */       }  if (paramClass == StAXSource.class) {
/* 160 */         XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();
/* 161 */         xMLInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(false));
/* 162 */         xMLInputFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
/* 163 */         xMLInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.valueOf(false));
/* 164 */         return (T)new StAXSource(xMLInputFactory.createXMLStreamReader(this.value.getInputStream()));
/* 165 */       }  if (paramClass == StreamSource.class) {
/* 166 */         TransformerFactory transformerFactory = TransformerFactory.newInstance();
/* 167 */         transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
/* 168 */         transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
/* 169 */         transformerFactory.setURIResolver(NOOP_URI_RESOLVER);
/* 170 */         transformerFactory.newTransformer().transform(new StreamSource(this.value.getInputStream()), new SAXResult(new DefaultHandler()));
/*     */         
/* 172 */         return (T)new StreamSource(this.value.getInputStream());
/*     */       } 
/* 174 */       throw unsupported(paramClass.getName());
/* 175 */     } catch (Exception exception) {
/* 176 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() throws SQLException {
/*     */     try {
/* 183 */       debugCodeCall("getString");
/* 184 */       checkReadable();
/* 185 */       return this.value.getString();
/* 186 */     } catch (Exception exception) {
/* 187 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream setBinaryStream() throws SQLException {
/*     */     try {
/* 194 */       debugCodeCall("setBinaryStream");
/* 195 */       checkEditable();
/* 196 */       this.state = JdbcLob.State.SET_CALLED;
/* 197 */       return new BufferedOutputStream(setClobOutputStreamImpl());
/* 198 */     } catch (Exception exception) {
/* 199 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer setCharacterStream() throws SQLException {
/*     */     try {
/* 206 */       debugCodeCall("setCharacterStream");
/* 207 */       checkEditable();
/* 208 */       this.state = JdbcLob.State.SET_CALLED;
/* 209 */       return setCharacterStreamImpl();
/* 210 */     } catch (Exception exception) {
/* 211 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends javax.xml.transform.Result> T setResult(Class<T> paramClass) throws SQLException {
/*     */     try {
/* 219 */       if (isDebugEnabled()) {
/* 220 */         debugCode("setResult(" + ((paramClass != null) ? (paramClass
/* 221 */             .getSimpleName() + ".class") : "null") + ')');
/*     */       }
/* 223 */       checkEditable();
/* 224 */       if (paramClass == null || paramClass == DOMResult.class) {
/* 225 */         this.domResult = new DOMResult();
/* 226 */         this.state = JdbcLob.State.SET_CALLED;
/* 227 */         return (T)this.domResult;
/* 228 */       }  if (paramClass == SAXResult.class) {
/* 229 */         SAXTransformerFactory sAXTransformerFactory = (SAXTransformerFactory)TransformerFactory.newInstance();
/* 230 */         TransformerHandler transformerHandler = sAXTransformerFactory.newTransformerHandler();
/* 231 */         Writer writer = setCharacterStreamImpl();
/* 232 */         transformerHandler.setResult(new StreamResult(writer));
/* 233 */         SAXResult sAXResult = new SAXResult(transformerHandler);
/* 234 */         this.closable = writer;
/* 235 */         this.state = JdbcLob.State.SET_CALLED;
/* 236 */         return (T)sAXResult;
/* 237 */       }  if (paramClass == StAXResult.class) {
/* 238 */         XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
/* 239 */         Writer writer = setCharacterStreamImpl();
/* 240 */         StAXResult stAXResult = new StAXResult(xMLOutputFactory.createXMLStreamWriter(writer));
/* 241 */         this.closable = writer;
/* 242 */         this.state = JdbcLob.State.SET_CALLED;
/* 243 */         return (T)stAXResult;
/* 244 */       }  if (StreamResult.class.equals(paramClass)) {
/* 245 */         Writer writer = setCharacterStreamImpl();
/* 246 */         StreamResult streamResult = new StreamResult(writer);
/* 247 */         this.closable = writer;
/* 248 */         this.state = JdbcLob.State.SET_CALLED;
/* 249 */         return (T)streamResult;
/*     */       } 
/* 251 */       throw unsupported(paramClass.getName());
/* 252 */     } catch (Exception exception) {
/* 253 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setString(String paramString) throws SQLException {
/*     */     try {
/* 260 */       if (isDebugEnabled()) {
/* 261 */         debugCodeCall("getSource", paramString);
/*     */       }
/* 263 */       checkEditable();
/* 264 */       completeWrite(this.conn.createClob(new StringReader(paramString), -1L));
/* 265 */     } catch (Exception exception) {
/* 266 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcSQLXML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */