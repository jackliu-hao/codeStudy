package org.wildfly.client.config;

import java.net.URI;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

final class EmptyXMLStreamReader implements ConfigurationXMLStreamReader {
   private final URI uri;
   private final XMLLocation includedFrom;

   EmptyXMLStreamReader(URI uri, XMLLocation includedFrom) {
      this.uri = uri;
      this.includedFrom = includedFrom;
   }

   public URI getUri() {
      return this.uri;
   }

   public XMLInputFactory getXmlInputFactory() {
      throw new UnsupportedOperationException();
   }

   public XMLLocation getIncludedFrom() {
      return this.includedFrom;
   }

   public boolean hasNext() throws ConfigXMLParseException {
      return false;
   }

   public int next() throws ConfigXMLParseException {
      throw new NoSuchElementException();
   }

   public XMLLocation getLocation() {
      return XMLLocation.UNKNOWN;
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws ConfigXMLParseException {
      throw new UnsupportedOperationException();
   }

   public void close() {
   }

   public String getNamespaceURI(String prefix) {
      return null;
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      throw new IllegalStateException();
   }

   public int getAttributeCount() {
      throw new IllegalStateException();
   }

   public QName getAttributeName(int index) {
      throw new IllegalStateException();
   }

   public String getAttributeNamespace(int index) {
      throw new IllegalStateException();
   }

   public String getAttributeLocalName(int index) {
      throw new IllegalStateException();
   }

   public String getAttributePrefix(int index) {
      throw new IllegalStateException();
   }

   public String getAttributeType(int index) {
      throw new IllegalStateException();
   }

   public String getAttributeValue(int index) {
      throw new IllegalStateException();
   }

   public boolean isAttributeSpecified(int index) {
      throw new IllegalStateException();
   }

   public int getNamespaceCount() {
      return 0;
   }

   public String getNamespacePrefix(int index) {
      return null;
   }

   public String getNamespaceURI(int index) {
      return null;
   }

   public NamespaceContext getNamespaceContext() {
      throw new UnsupportedOperationException();
   }

   public int getEventType() {
      return 8;
   }

   public String getText() {
      throw new IllegalStateException();
   }

   public char[] getTextCharacters() {
      throw new IllegalStateException();
   }

   public int getTextStart() {
      throw new IllegalStateException();
   }

   public int getTextLength() {
      throw new IllegalStateException();
   }

   public String getEncoding() {
      return null;
   }

   public QName getName() {
      throw new IllegalStateException();
   }

   public String getLocalName() {
      throw new IllegalStateException();
   }

   public String getNamespaceURI() {
      throw new IllegalStateException();
   }

   public String getPrefix() {
      throw new IllegalStateException();
   }

   public String getVersion() {
      return null;
   }

   public String getCharacterEncodingScheme() {
      return null;
   }

   public String getPITarget() {
      return null;
   }

   public String getPIData() {
      return null;
   }
}
