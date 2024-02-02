package org.wildfly.client.config;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

final class BasicXMLStreamReader implements ConfigurationXMLStreamReader {
   private final XMLLocation includedFrom;
   private final XMLStreamReader xmlStreamReader;
   private final URI uri;
   private final XMLInputFactory inputFactory;
   private final Closeable underlying;

   BasicXMLStreamReader(XMLLocation includedFrom, XMLStreamReader xmlStreamReader, URI uri, XMLInputFactory inputFactory, Closeable underlying) {
      this.includedFrom = includedFrom;
      this.xmlStreamReader = xmlStreamReader;
      this.uri = uri;
      this.inputFactory = inputFactory;
      this.underlying = underlying;
   }

   public URI getUri() {
      return this.uri;
   }

   public XMLInputFactory getXmlInputFactory() {
      return this.inputFactory;
   }

   public XMLLocation getIncludedFrom() {
      return this.includedFrom;
   }

   public boolean hasNext() throws ConfigXMLParseException {
      try {
         return this.xmlStreamReader.hasNext();
      } catch (XMLStreamException var2) {
         throw ConfigXMLParseException.from(var2, this.uri, this.includedFrom);
      }
   }

   public int next() throws ConfigXMLParseException {
      try {
         return this.xmlStreamReader.next();
      } catch (XMLStreamException var2) {
         throw ConfigXMLParseException.from(var2, this.uri, this.includedFrom);
      }
   }

   public void require(int type, String namespaceURI, String localName) throws ConfigXMLParseException {
      try {
         this.xmlStreamReader.require(type, namespaceURI, localName);
      } catch (XMLStreamException var5) {
         throw ConfigXMLParseException.from(var5, this.uri, this.includedFrom);
      }
   }

   public String getElementText() throws ConfigXMLParseException {
      try {
         return this.xmlStreamReader.getElementText();
      } catch (XMLStreamException var2) {
         throw ConfigXMLParseException.from(var2, this.uri, this.includedFrom);
      }
   }

   public int nextTag() throws ConfigXMLParseException {
      try {
         return this.xmlStreamReader.nextTag();
      } catch (XMLStreamException var2) {
         throw ConfigXMLParseException.from(var2, this.uri, this.includedFrom);
      }
   }

   public XMLLocation getLocation() {
      return new XMLLocation(this.includedFrom, this.uri, this.xmlStreamReader.getLocation());
   }

   public QName getName() {
      return this.xmlStreamReader.getName();
   }

   public String getLocalName() {
      return this.xmlStreamReader.getLocalName();
   }

   public boolean hasName() {
      return this.xmlStreamReader.hasName();
   }

   public String getNamespaceURI() {
      return this.xmlStreamReader.getNamespaceURI();
   }

   public String getPrefix() {
      return this.xmlStreamReader.getPrefix();
   }

   public String getVersion() {
      return this.xmlStreamReader.getVersion();
   }

   public boolean isStandalone() {
      return this.xmlStreamReader.isStandalone();
   }

   public boolean standaloneSet() {
      return this.xmlStreamReader.standaloneSet();
   }

   public String getCharacterEncodingScheme() {
      return this.xmlStreamReader.getCharacterEncodingScheme();
   }

   public String getPITarget() {
      return this.xmlStreamReader.getPITarget();
   }

   public String getPIData() {
      return this.xmlStreamReader.getPIData();
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws ConfigXMLParseException {
      try {
         return this.xmlStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
      } catch (XMLStreamException var6) {
         throw ConfigXMLParseException.from(var6, this.uri, this.includedFrom);
      }
   }

   public int getTextStart() {
      return this.xmlStreamReader.getTextStart();
   }

   public int getTextLength() {
      return this.xmlStreamReader.getTextLength();
   }

   public String getEncoding() {
      return this.xmlStreamReader.getEncoding();
   }

   public boolean hasText() {
      return this.xmlStreamReader.hasText();
   }

   public void close() throws ConfigXMLParseException {
      try {
         Closeable underlying = this.underlying;
         Throwable var2 = null;

         try {
            try {
               this.xmlStreamReader.close();
            } catch (XMLStreamException var13) {
               throw ConfigXMLParseException.from(var13, this.uri, this.includedFrom);
            }
         } catch (Throwable var14) {
            var2 = var14;
            throw var14;
         } finally {
            if (underlying != null) {
               if (var2 != null) {
                  try {
                     underlying.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  underlying.close();
               }
            }

         }

      } catch (IOException var16) {
         throw ConfigXMLParseException.from((Exception)var16, this.uri, this.includedFrom);
      }
   }

   public String getNamespaceURI(String prefix) {
      return this.xmlStreamReader.getNamespaceURI(prefix);
   }

   public boolean isWhiteSpace() {
      return this.xmlStreamReader.isWhiteSpace();
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      return this.xmlStreamReader.getAttributeValue(namespaceURI, localName);
   }

   public int getAttributeCount() {
      return this.xmlStreamReader.getAttributeCount();
   }

   public QName getAttributeName(int index) {
      return this.xmlStreamReader.getAttributeName(index);
   }

   public String getAttributeNamespace(int index) {
      return this.xmlStreamReader.getAttributeNamespace(index);
   }

   public String getAttributeLocalName(int index) {
      return this.xmlStreamReader.getAttributeLocalName(index);
   }

   public String getAttributePrefix(int index) {
      return this.xmlStreamReader.getAttributePrefix(index);
   }

   public String getAttributeType(int index) {
      return this.xmlStreamReader.getAttributeType(index);
   }

   public String getAttributeValue(int index) {
      return this.xmlStreamReader.getAttributeValue(index);
   }

   public boolean isAttributeSpecified(int index) {
      return this.xmlStreamReader.isAttributeSpecified(index);
   }

   public int getNamespaceCount() {
      return this.xmlStreamReader.getNamespaceCount();
   }

   public String getNamespacePrefix(int index) {
      return this.xmlStreamReader.getNamespacePrefix(index);
   }

   public String getNamespaceURI(int index) {
      return this.xmlStreamReader.getNamespaceURI(index);
   }

   public NamespaceContext getNamespaceContext() {
      return this.xmlStreamReader.getNamespaceContext();
   }

   public int getEventType() {
      return this.xmlStreamReader.getEventType();
   }

   public String getText() {
      return this.xmlStreamReader.getText();
   }

   public char[] getTextCharacters() {
      return this.xmlStreamReader.getTextCharacters();
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      return this.xmlStreamReader.getProperty(name);
   }
}
