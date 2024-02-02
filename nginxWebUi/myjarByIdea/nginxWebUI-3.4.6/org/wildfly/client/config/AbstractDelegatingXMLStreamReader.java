package org.wildfly.client.config;

import java.net.URI;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

abstract class AbstractDelegatingXMLStreamReader implements ConfigurationXMLStreamReader {
   private final boolean closeDelegate;
   private final ConfigurationXMLStreamReader delegate;

   AbstractDelegatingXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate) {
      this.closeDelegate = closeDelegate;
      this.delegate = delegate;
   }

   protected ConfigurationXMLStreamReader getDelegate() {
      return this.delegate;
   }

   public URI getUri() {
      return this.getDelegate().getUri();
   }

   public XMLInputFactory getXmlInputFactory() {
      return this.getDelegate().getXmlInputFactory();
   }

   public XMLLocation getIncludedFrom() {
      return this.getDelegate().getIncludedFrom();
   }

   public boolean hasNext() throws ConfigXMLParseException {
      return this.getDelegate().hasNext();
   }

   public int next() throws ConfigXMLParseException {
      return this.getDelegate().next();
   }

   public int nextTag() throws ConfigXMLParseException {
      int eventType;
      for(eventType = this.next(); eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {
      }

      if (eventType != 1 && eventType != 2) {
         throw this.unexpectedContent();
      } else {
         return eventType;
      }
   }

   public String getElementText() throws ConfigXMLParseException {
      return this.getDelegate().getElementText();
   }

   public void require(int type, String namespaceURI, String localName) throws ConfigXMLParseException {
      this.getDelegate().require(type, namespaceURI, localName);
   }

   public boolean isStartElement() {
      return this.getDelegate().isStartElement();
   }

   public boolean isEndElement() {
      return this.getDelegate().isEndElement();
   }

   public boolean isCharacters() {
      return this.getDelegate().isCharacters();
   }

   public boolean isWhiteSpace() {
      return this.getDelegate().isWhiteSpace();
   }

   public static String eventToString(int type) {
      return ConfigurationXMLStreamReader.eventToString(type);
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      return this.getDelegate().getProperty(name);
   }

   public boolean hasText() {
      return this.getDelegate().hasText();
   }

   public boolean isStandalone() {
      return this.getDelegate().isStandalone();
   }

   public boolean standaloneSet() {
      return this.getDelegate().standaloneSet();
   }

   public boolean hasName() {
      return this.getDelegate().hasName();
   }

   public XMLLocation getLocation() {
      return this.getDelegate().getLocation();
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws ConfigXMLParseException {
      return this.getDelegate().getTextCharacters(sourceStart, target, targetStart, length);
   }

   public void close() throws ConfigXMLParseException {
      if (this.closeDelegate) {
         this.getDelegate().close();
      }

   }

   public String getNamespaceURI(String prefix) {
      return this.getDelegate().getNamespaceURI(prefix);
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      return this.getDelegate().getAttributeValue(namespaceURI, localName);
   }

   public int getAttributeCount() {
      return this.getDelegate().getAttributeCount();
   }

   public QName getAttributeName(int index) {
      return this.getDelegate().getAttributeName(index);
   }

   public String getAttributeNamespace(int index) {
      return this.getDelegate().getAttributeNamespace(index);
   }

   public String getAttributeLocalName(int index) {
      return this.getDelegate().getAttributeLocalName(index);
   }

   public String getAttributePrefix(int index) {
      return this.getDelegate().getAttributePrefix(index);
   }

   public String getAttributeType(int index) {
      return this.getDelegate().getAttributeType(index);
   }

   public String getAttributeValue(int index) {
      return this.getDelegate().getAttributeValue(index);
   }

   public boolean isAttributeSpecified(int index) {
      return this.getDelegate().isAttributeSpecified(index);
   }

   public int getNamespaceCount() {
      return this.getDelegate().getNamespaceCount();
   }

   public String getNamespacePrefix(int index) {
      return this.getDelegate().getNamespacePrefix(index);
   }

   public String getNamespaceURI(int index) {
      return this.getDelegate().getNamespaceURI(index);
   }

   public NamespaceContext getNamespaceContext() {
      return this.getDelegate().getNamespaceContext();
   }

   public int getEventType() {
      return this.getDelegate().getEventType();
   }

   public String getText() {
      return this.getDelegate().getText();
   }

   public char[] getTextCharacters() {
      return this.getDelegate().getTextCharacters();
   }

   public int getTextStart() {
      return this.getDelegate().getTextStart();
   }

   public int getTextLength() {
      return this.getDelegate().getTextLength();
   }

   public String getEncoding() {
      return this.getDelegate().getEncoding();
   }

   public QName getName() {
      return this.getDelegate().getName();
   }

   public String getLocalName() {
      return this.getDelegate().getLocalName();
   }

   public String getNamespaceURI() {
      return this.getDelegate().getNamespaceURI();
   }

   public String getPrefix() {
      return this.getDelegate().getPrefix();
   }

   public String getVersion() {
      return this.getDelegate().getVersion();
   }

   public String getCharacterEncodingScheme() {
      return this.getDelegate().getCharacterEncodingScheme();
   }

   public String getPITarget() {
      return this.getDelegate().getPITarget();
   }

   public String getPIData() {
      return this.getDelegate().getPIData();
   }
}
