/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.net.URI;
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
/*     */ 
/*     */ abstract class AbstractDelegatingXMLStreamReader
/*     */   implements ConfigurationXMLStreamReader
/*     */ {
/*     */   private final boolean closeDelegate;
/*     */   private final ConfigurationXMLStreamReader delegate;
/*     */   
/*     */   AbstractDelegatingXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate) {
/*  36 */     this.closeDelegate = closeDelegate;
/*  37 */     this.delegate = delegate;
/*     */   }
/*     */   
/*     */   protected ConfigurationXMLStreamReader getDelegate() {
/*  41 */     return this.delegate;
/*     */   }
/*     */   
/*     */   public URI getUri() {
/*  45 */     return getDelegate().getUri();
/*     */   }
/*     */   
/*     */   public XMLInputFactory getXmlInputFactory() {
/*  49 */     return getDelegate().getXmlInputFactory();
/*     */   }
/*     */   
/*     */   public XMLLocation getIncludedFrom() {
/*  53 */     return getDelegate().getIncludedFrom();
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws ConfigXMLParseException {
/*  57 */     return getDelegate().hasNext();
/*     */   }
/*     */   
/*     */   public int next() throws ConfigXMLParseException {
/*  61 */     return getDelegate().next();
/*     */   }
/*     */   
/*     */   public int nextTag() throws ConfigXMLParseException {
/*  65 */     int eventType = next();
/*  66 */     while ((eventType == 4 && isWhiteSpace()) || (eventType == 12 && 
/*  67 */       isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5)
/*     */     {
/*     */ 
/*     */       
/*  71 */       eventType = next();
/*     */     }
/*  73 */     if (eventType != 1 && eventType != 2) {
/*  74 */       throw unexpectedContent();
/*     */     }
/*  76 */     return eventType;
/*     */   }
/*     */   
/*     */   public String getElementText() throws ConfigXMLParseException {
/*  80 */     return getDelegate().getElementText();
/*     */   }
/*     */   
/*     */   public void require(int type, String namespaceURI, String localName) throws ConfigXMLParseException {
/*  84 */     getDelegate().require(type, namespaceURI, localName);
/*     */   }
/*     */   
/*     */   public boolean isStartElement() {
/*  88 */     return getDelegate().isStartElement();
/*     */   }
/*     */   
/*     */   public boolean isEndElement() {
/*  92 */     return getDelegate().isEndElement();
/*     */   }
/*     */   
/*     */   public boolean isCharacters() {
/*  96 */     return getDelegate().isCharacters();
/*     */   }
/*     */   
/*     */   public boolean isWhiteSpace() {
/* 100 */     return getDelegate().isWhiteSpace();
/*     */   }
/*     */   
/*     */   public static String eventToString(int type) {
/* 104 */     return ConfigurationXMLStreamReader.eventToString(type);
/*     */   }
/*     */   
/*     */   public Object getProperty(String name) throws IllegalArgumentException {
/* 108 */     return getDelegate().getProperty(name);
/*     */   }
/*     */   
/*     */   public boolean hasText() {
/* 112 */     return getDelegate().hasText();
/*     */   }
/*     */   
/*     */   public boolean isStandalone() {
/* 116 */     return getDelegate().isStandalone();
/*     */   }
/*     */   
/*     */   public boolean standaloneSet() {
/* 120 */     return getDelegate().standaloneSet();
/*     */   }
/*     */   
/*     */   public boolean hasName() {
/* 124 */     return getDelegate().hasName();
/*     */   }
/*     */   
/*     */   public XMLLocation getLocation() {
/* 128 */     return getDelegate().getLocation();
/*     */   }
/*     */   
/*     */   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws ConfigXMLParseException {
/* 132 */     return getDelegate().getTextCharacters(sourceStart, target, targetStart, length);
/*     */   }
/*     */   
/*     */   public void close() throws ConfigXMLParseException {
/* 136 */     if (this.closeDelegate) getDelegate().close(); 
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(String prefix) {
/* 140 */     return getDelegate().getNamespaceURI(prefix);
/*     */   }
/*     */   
/*     */   public String getAttributeValue(String namespaceURI, String localName) {
/* 144 */     return getDelegate().getAttributeValue(namespaceURI, localName);
/*     */   }
/*     */   
/*     */   public int getAttributeCount() {
/* 148 */     return getDelegate().getAttributeCount();
/*     */   }
/*     */   
/*     */   public QName getAttributeName(int index) {
/* 152 */     return getDelegate().getAttributeName(index);
/*     */   }
/*     */   
/*     */   public String getAttributeNamespace(int index) {
/* 156 */     return getDelegate().getAttributeNamespace(index);
/*     */   }
/*     */   
/*     */   public String getAttributeLocalName(int index) {
/* 160 */     return getDelegate().getAttributeLocalName(index);
/*     */   }
/*     */   
/*     */   public String getAttributePrefix(int index) {
/* 164 */     return getDelegate().getAttributePrefix(index);
/*     */   }
/*     */   
/*     */   public String getAttributeType(int index) {
/* 168 */     return getDelegate().getAttributeType(index);
/*     */   }
/*     */   
/*     */   public String getAttributeValue(int index) {
/* 172 */     return getDelegate().getAttributeValue(index);
/*     */   }
/*     */   
/*     */   public boolean isAttributeSpecified(int index) {
/* 176 */     return getDelegate().isAttributeSpecified(index);
/*     */   }
/*     */   
/*     */   public int getNamespaceCount() {
/* 180 */     return getDelegate().getNamespaceCount();
/*     */   }
/*     */   
/*     */   public String getNamespacePrefix(int index) {
/* 184 */     return getDelegate().getNamespacePrefix(index);
/*     */   }
/*     */   
/*     */   public String getNamespaceURI(int index) {
/* 188 */     return getDelegate().getNamespaceURI(index);
/*     */   }
/*     */   
/*     */   public NamespaceContext getNamespaceContext() {
/* 192 */     return getDelegate().getNamespaceContext();
/*     */   }
/*     */   
/*     */   public int getEventType() {
/* 196 */     return getDelegate().getEventType();
/*     */   }
/*     */   
/*     */   public String getText() {
/* 200 */     return getDelegate().getText();
/*     */   }
/*     */   
/*     */   public char[] getTextCharacters() {
/* 204 */     return getDelegate().getTextCharacters();
/*     */   }
/*     */   
/*     */   public int getTextStart() {
/* 208 */     return getDelegate().getTextStart();
/*     */   }
/*     */   
/*     */   public int getTextLength() {
/* 212 */     return getDelegate().getTextLength();
/*     */   }
/*     */   
/*     */   public String getEncoding() {
/* 216 */     return getDelegate().getEncoding();
/*     */   }
/*     */   
/*     */   public QName getName() {
/* 220 */     return getDelegate().getName();
/*     */   }
/*     */   
/*     */   public String getLocalName() {
/* 224 */     return getDelegate().getLocalName();
/*     */   }
/*     */   
/*     */   public String getNamespaceURI() {
/* 228 */     return getDelegate().getNamespaceURI();
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/* 232 */     return getDelegate().getPrefix();
/*     */   }
/*     */   
/*     */   public String getVersion() {
/* 236 */     return getDelegate().getVersion();
/*     */   }
/*     */   
/*     */   public String getCharacterEncodingScheme() {
/* 240 */     return getDelegate().getCharacterEncodingScheme();
/*     */   }
/*     */   
/*     */   public String getPITarget() {
/* 244 */     return getDelegate().getPITarget();
/*     */   }
/*     */   
/*     */   public String getPIData() {
/* 248 */     return getDelegate().getPIData();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\AbstractDelegatingXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */