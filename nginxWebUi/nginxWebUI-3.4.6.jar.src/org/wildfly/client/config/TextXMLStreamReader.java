/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.xml.namespace.NamespaceContext;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import org.wildfly.client.config._private.ConfigMessages;
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
/*     */ class TextXMLStreamReader
/*     */   implements ConfigurationXMLStreamReader
/*     */ {
/*     */   private final String charsetName;
/*     */   private final CountingReader reader;
/*     */   private final ConfigurationXMLStreamReader parent;
/*     */   private final URI uri;
/*     */   private final XMLLocation includedFrom;
/*  46 */   private char[] current = new char[512];
/*     */   private int len;
/*  48 */   private char[] next = new char[512];
/*     */   private int nextLen;
/*     */   
/*     */   TextXMLStreamReader(String charsetName, InputStream inputStream, ConfigurationXMLStreamReader parent, URI uri) throws UnsupportedEncodingException {
/*  52 */     this(charsetName, new InputStreamReader(inputStream, charsetName), parent, uri);
/*     */   }
/*     */   
/*     */   TextXMLStreamReader(Charset charset, InputStream inputStream, ConfigurationXMLStreamReader parent, URI uri) {
/*  56 */     this(charset.name(), new InputStreamReader(inputStream, charset), parent, uri);
/*     */   }
/*     */   
/*     */   TextXMLStreamReader(String charsetName, Reader reader, ConfigurationXMLStreamReader parent, URI uri) {
/*  60 */     this(charsetName, (reader instanceof CountingReader) ? (CountingReader)reader : new CountingReader(reader), parent, uri);
/*     */   }
/*     */   
/*     */   TextXMLStreamReader(String charsetName, CountingReader reader, ConfigurationXMLStreamReader parent, URI uri) {
/*  64 */     this.charsetName = charsetName;
/*  65 */     this.reader = reader;
/*  66 */     this.parent = parent;
/*  67 */     this.uri = uri;
/*  68 */     this.includedFrom = this.parent.getLocation();
/*     */   }
/*     */   
/*     */   public XMLLocation getIncludedFrom() {
/*  72 */     return this.includedFrom;
/*     */   }
/*     */   
/*     */   public int next() throws ConfigXMLParseException {
/*  76 */     if (!hasNext()) {
/*  77 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*  80 */     char[] old = this.current;
/*  81 */     this.current = this.next;
/*  82 */     this.len = this.nextLen;
/*  83 */     this.next = old;
/*  84 */     this.nextLen = 0;
/*  85 */     return 4;
/*     */   }
/*     */   
/*     */   public URI getUri() {
/*  89 */     return this.uri;
/*     */   }
/*     */   
/*     */   public XMLInputFactory getXmlInputFactory() {
/*  93 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws ConfigXMLParseException {
/*  97 */     if (this.nextLen == 0) {
/*     */       int res;
/*     */       try {
/* 100 */         res = this.reader.read(this.next);
/* 101 */       } catch (IOException e) {
/* 102 */         throw ConfigMessages.msg.failedToReadInput(getLocation(), e);
/*     */       } 
/* 104 */       if (res == -1) {
/* 105 */         return false;
/*     */       }
/* 107 */       this.nextLen = res;
/*     */     } 
/* 109 */     return true;
/*     */   }
/*     */   
/*     */   public void close() throws ConfigXMLParseException {
/*     */     try {
/* 114 */       this.reader.close();
/* 115 */     } catch (IOException e) {
/* 116 */       throw ConfigMessages.msg.failedToCloseInput(getLocation(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(String prefix) {
/* 121 */     return null;
/*     */   }
/*     */   
/*     */   public String getAttributeValue(String namespaceURI, String localName) {
/* 125 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getAttributeCount() {
/* 129 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public QName getAttributeName(int index) {
/* 133 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeNamespace(int index) {
/* 137 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeLocalName(int index) {
/* 141 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributePrefix(int index) {
/* 145 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeType(int index) {
/* 149 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeValue(int index) {
/* 153 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public boolean isAttributeSpecified(int index) {
/* 157 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getNamespaceCount() {
/* 161 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getNamespacePrefix(int index) {
/* 165 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(int index) {
/* 169 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public NamespaceContext getNamespaceContext() {
/* 173 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getEventType() {
/* 177 */     return (this.len == 0) ? 7 : 4;
/*     */   }
/*     */   
/*     */   public String getText() {
/* 181 */     if (this.len == 0) throw new IllegalStateException(); 
/* 182 */     return new String(this.current, 0, this.len);
/*     */   }
/*     */   
/*     */   public char[] getTextCharacters() {
/* 186 */     return Arrays.copyOf(this.current, this.len);
/*     */   }
/*     */   
/*     */   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) {
/* 190 */     if (sourceStart > this.len || targetStart > length) return 0; 
/* 191 */     int realLen = Math.min(this.len - sourceStart, length);
/* 192 */     System.arraycopy(this.current, sourceStart, target, targetStart, realLen);
/* 193 */     return realLen;
/*     */   }
/*     */   
/*     */   public int getTextStart() {
/* 197 */     return 0;
/*     */   }
/*     */   
/*     */   public int getTextLength() {
/* 201 */     if (this.len == 0) throw new IllegalStateException(); 
/* 202 */     return this.len;
/*     */   }
/*     */   
/*     */   public String getEncoding() {
/* 206 */     return this.charsetName;
/*     */   }
/*     */   
/*     */   public XMLLocation getLocation() {
/* 210 */     return new XMLLocation(this.includedFrom, this.uri, this.reader.getLineNumber(), this.reader.getColumnNumber(), this.reader.getCharacterOffset());
/*     */   }
/*     */   
/*     */   public QName getName() {
/* 214 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getLocalName() {
/* 218 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getNamespaceURI() {
/* 222 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/* 226 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getVersion() {
/* 230 */     return null;
/*     */   }
/*     */   
/*     */   public String getCharacterEncodingScheme() {
/* 234 */     return null;
/*     */   }
/*     */   
/*     */   public String getPITarget() {
/* 238 */     return null;
/*     */   }
/*     */   
/*     */   public String getPIData() {
/* 242 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\TextXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */