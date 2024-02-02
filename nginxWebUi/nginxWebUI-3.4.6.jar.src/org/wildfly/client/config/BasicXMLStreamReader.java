/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import javax.xml.namespace.NamespaceContext;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
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
/*     */ final class BasicXMLStreamReader
/*     */   implements ConfigurationXMLStreamReader
/*     */ {
/*     */   private final XMLLocation includedFrom;
/*     */   private final XMLStreamReader xmlStreamReader;
/*     */   private final URI uri;
/*     */   private final XMLInputFactory inputFactory;
/*     */   private final Closeable underlying;
/*     */   
/*     */   BasicXMLStreamReader(XMLLocation includedFrom, XMLStreamReader xmlStreamReader, URI uri, XMLInputFactory inputFactory, Closeable underlying) {
/*  42 */     this.includedFrom = includedFrom;
/*  43 */     this.xmlStreamReader = xmlStreamReader;
/*  44 */     this.uri = uri;
/*  45 */     this.inputFactory = inputFactory;
/*  46 */     this.underlying = underlying;
/*     */   }
/*     */   
/*     */   public URI getUri() {
/*  50 */     return this.uri;
/*     */   }
/*     */   
/*     */   public XMLInputFactory getXmlInputFactory() {
/*  54 */     return this.inputFactory;
/*     */   }
/*     */   
/*     */   public XMLLocation getIncludedFrom() {
/*  58 */     return this.includedFrom;
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws ConfigXMLParseException {
/*     */     try {
/*  63 */       return this.xmlStreamReader.hasNext();
/*  64 */     } catch (XMLStreamException e) {
/*  65 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int next() throws ConfigXMLParseException {
/*     */     try {
/*  71 */       return this.xmlStreamReader.next();
/*  72 */     } catch (XMLStreamException e) {
/*  73 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void require(int type, String namespaceURI, String localName) throws ConfigXMLParseException {
/*     */     try {
/*  79 */       this.xmlStreamReader.require(type, namespaceURI, localName);
/*  80 */     } catch (XMLStreamException e) {
/*  81 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getElementText() throws ConfigXMLParseException {
/*     */     try {
/*  87 */       return this.xmlStreamReader.getElementText();
/*  88 */     } catch (XMLStreamException e) {
/*  89 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int nextTag() throws ConfigXMLParseException {
/*     */     try {
/*  95 */       return this.xmlStreamReader.nextTag();
/*  96 */     } catch (XMLStreamException e) {
/*  97 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public XMLLocation getLocation() {
/* 102 */     return new XMLLocation(this.includedFrom, this.uri, this.xmlStreamReader.getLocation());
/*     */   }
/*     */   
/*     */   public QName getName() {
/* 106 */     return this.xmlStreamReader.getName();
/*     */   }
/*     */   
/*     */   public String getLocalName() {
/* 110 */     return this.xmlStreamReader.getLocalName();
/*     */   }
/*     */   
/*     */   public boolean hasName() {
/* 114 */     return this.xmlStreamReader.hasName();
/*     */   }
/*     */   
/*     */   public String getNamespaceURI() {
/* 118 */     return this.xmlStreamReader.getNamespaceURI();
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/* 122 */     return this.xmlStreamReader.getPrefix();
/*     */   }
/*     */   
/*     */   public String getVersion() {
/* 126 */     return this.xmlStreamReader.getVersion();
/*     */   }
/*     */   
/*     */   public boolean isStandalone() {
/* 130 */     return this.xmlStreamReader.isStandalone();
/*     */   }
/*     */   
/*     */   public boolean standaloneSet() {
/* 134 */     return this.xmlStreamReader.standaloneSet();
/*     */   }
/*     */   
/*     */   public String getCharacterEncodingScheme() {
/* 138 */     return this.xmlStreamReader.getCharacterEncodingScheme();
/*     */   }
/*     */   
/*     */   public String getPITarget() {
/* 142 */     return this.xmlStreamReader.getPITarget();
/*     */   }
/*     */   
/*     */   public String getPIData() {
/* 146 */     return this.xmlStreamReader.getPIData();
/*     */   }
/*     */   
/*     */   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws ConfigXMLParseException {
/*     */     try {
/* 151 */       return this.xmlStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
/* 152 */     } catch (XMLStreamException e) {
/* 153 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getTextStart() {
/* 158 */     return this.xmlStreamReader.getTextStart();
/*     */   }
/*     */   
/*     */   public int getTextLength() {
/* 162 */     return this.xmlStreamReader.getTextLength();
/*     */   }
/*     */   
/*     */   public String getEncoding() {
/* 166 */     return this.xmlStreamReader.getEncoding();
/*     */   }
/*     */   
/*     */   public boolean hasText() {
/* 170 */     return this.xmlStreamReader.hasText();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws ConfigXMLParseException {
/* 175 */     try (Closeable underlying = this.underlying) {
/*     */       try {
/* 177 */         this.xmlStreamReader.close();
/* 178 */       } catch (XMLStreamException e) {
/* 179 */         throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */       } 
/* 181 */     } catch (IOException e) {
/* 182 */       throw ConfigXMLParseException.from(e, this.uri, this.includedFrom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(String prefix) {
/* 187 */     return this.xmlStreamReader.getNamespaceURI(prefix);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWhiteSpace() {
/* 192 */     return this.xmlStreamReader.isWhiteSpace();
/*     */   }
/*     */   
/*     */   public String getAttributeValue(String namespaceURI, String localName) {
/* 196 */     return this.xmlStreamReader.getAttributeValue(namespaceURI, localName);
/*     */   }
/*     */   
/*     */   public int getAttributeCount() {
/* 200 */     return this.xmlStreamReader.getAttributeCount();
/*     */   }
/*     */   
/*     */   public QName getAttributeName(int index) {
/* 204 */     return this.xmlStreamReader.getAttributeName(index);
/*     */   }
/*     */   
/*     */   public String getAttributeNamespace(int index) {
/* 208 */     return this.xmlStreamReader.getAttributeNamespace(index);
/*     */   }
/*     */   
/*     */   public String getAttributeLocalName(int index) {
/* 212 */     return this.xmlStreamReader.getAttributeLocalName(index);
/*     */   }
/*     */   
/*     */   public String getAttributePrefix(int index) {
/* 216 */     return this.xmlStreamReader.getAttributePrefix(index);
/*     */   }
/*     */   
/*     */   public String getAttributeType(int index) {
/* 220 */     return this.xmlStreamReader.getAttributeType(index);
/*     */   }
/*     */   
/*     */   public String getAttributeValue(int index) {
/* 224 */     return this.xmlStreamReader.getAttributeValue(index);
/*     */   }
/*     */   
/*     */   public boolean isAttributeSpecified(int index) {
/* 228 */     return this.xmlStreamReader.isAttributeSpecified(index);
/*     */   }
/*     */   
/*     */   public int getNamespaceCount() {
/* 232 */     return this.xmlStreamReader.getNamespaceCount();
/*     */   }
/*     */   
/*     */   public String getNamespacePrefix(int index) {
/* 236 */     return this.xmlStreamReader.getNamespacePrefix(index);
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(int index) {
/* 240 */     return this.xmlStreamReader.getNamespaceURI(index);
/*     */   }
/*     */   
/*     */   public NamespaceContext getNamespaceContext() {
/* 244 */     return this.xmlStreamReader.getNamespaceContext();
/*     */   }
/*     */   
/*     */   public int getEventType() {
/* 248 */     return this.xmlStreamReader.getEventType();
/*     */   }
/*     */   
/*     */   public String getText() {
/* 252 */     return this.xmlStreamReader.getText();
/*     */   }
/*     */   
/*     */   public char[] getTextCharacters() {
/* 256 */     return this.xmlStreamReader.getTextCharacters();
/*     */   }
/*     */   
/*     */   public Object getProperty(String name) throws IllegalArgumentException {
/* 260 */     return this.xmlStreamReader.getProperty(name);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\BasicXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */