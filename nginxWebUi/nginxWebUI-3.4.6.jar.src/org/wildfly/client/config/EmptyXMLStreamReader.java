/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.xml.namespace.NamespaceContext;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLInputFactory;
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
/*     */ final class EmptyXMLStreamReader
/*     */   implements ConfigurationXMLStreamReader
/*     */ {
/*     */   private final URI uri;
/*     */   private final XMLLocation includedFrom;
/*     */   
/*     */   EmptyXMLStreamReader(URI uri, XMLLocation includedFrom) {
/*  36 */     this.uri = uri;
/*  37 */     this.includedFrom = includedFrom;
/*     */   }
/*     */   
/*     */   public URI getUri() {
/*  41 */     return this.uri;
/*     */   }
/*     */   
/*     */   public XMLInputFactory getXmlInputFactory() {
/*  45 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public XMLLocation getIncludedFrom() {
/*  49 */     return this.includedFrom;
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws ConfigXMLParseException {
/*  53 */     return false;
/*     */   }
/*     */   
/*     */   public int next() throws ConfigXMLParseException {
/*  57 */     throw new NoSuchElementException();
/*     */   }
/*     */   
/*     */   public XMLLocation getLocation() {
/*  61 */     return XMLLocation.UNKNOWN;
/*     */   }
/*     */   
/*     */   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws ConfigXMLParseException {
/*  65 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */   
/*     */   public String getNamespaceURI(String prefix) {
/*  72 */     return null;
/*     */   }
/*     */   
/*     */   public String getAttributeValue(String namespaceURI, String localName) {
/*  76 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getAttributeCount() {
/*  80 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public QName getAttributeName(int index) {
/*  84 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeNamespace(int index) {
/*  88 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeLocalName(int index) {
/*  92 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributePrefix(int index) {
/*  96 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeType(int index) {
/* 100 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getAttributeValue(int index) {
/* 104 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public boolean isAttributeSpecified(int index) {
/* 108 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getNamespaceCount() {
/* 112 */     return 0;
/*     */   }
/*     */   
/*     */   public String getNamespacePrefix(int index) {
/* 116 */     return null;
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(int index) {
/* 120 */     return null;
/*     */   }
/*     */   
/*     */   public NamespaceContext getNamespaceContext() {
/* 124 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public int getEventType() {
/* 128 */     return 8;
/*     */   }
/*     */   
/*     */   public String getText() {
/* 132 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public char[] getTextCharacters() {
/* 136 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getTextStart() {
/* 140 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public int getTextLength() {
/* 144 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getEncoding() {
/* 148 */     return null;
/*     */   }
/*     */   
/*     */   public QName getName() {
/* 152 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getLocalName() {
/* 156 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getNamespaceURI() {
/* 160 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/* 164 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   public String getVersion() {
/* 168 */     return null;
/*     */   }
/*     */   
/*     */   public String getCharacterEncodingScheme() {
/* 172 */     return null;
/*     */   }
/*     */   
/*     */   public String getPITarget() {
/* 176 */     return null;
/*     */   }
/*     */   
/*     */   public String getPIData() {
/* 180 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\EmptyXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */