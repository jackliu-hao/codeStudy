package org.wildfly.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import org.wildfly.client.config._private.ConfigMessages;

class TextXMLStreamReader implements ConfigurationXMLStreamReader {
   private final String charsetName;
   private final CountingReader reader;
   private final ConfigurationXMLStreamReader parent;
   private final URI uri;
   private final XMLLocation includedFrom;
   private char[] current;
   private int len;
   private char[] next;
   private int nextLen;

   TextXMLStreamReader(String charsetName, InputStream inputStream, ConfigurationXMLStreamReader parent, URI uri) throws UnsupportedEncodingException {
      this((String)charsetName, (Reader)(new InputStreamReader(inputStream, charsetName)), parent, uri);
   }

   TextXMLStreamReader(Charset charset, InputStream inputStream, ConfigurationXMLStreamReader parent, URI uri) {
      this((String)charset.name(), (Reader)(new InputStreamReader(inputStream, charset)), parent, uri);
   }

   TextXMLStreamReader(String charsetName, Reader reader, ConfigurationXMLStreamReader parent, URI uri) {
      this(charsetName, reader instanceof CountingReader ? (CountingReader)reader : new CountingReader(reader), parent, uri);
   }

   TextXMLStreamReader(String charsetName, CountingReader reader, ConfigurationXMLStreamReader parent, URI uri) {
      this.current = new char[512];
      this.next = new char[512];
      this.charsetName = charsetName;
      this.reader = reader;
      this.parent = parent;
      this.uri = uri;
      this.includedFrom = this.parent.getLocation();
   }

   public XMLLocation getIncludedFrom() {
      return this.includedFrom;
   }

   public int next() throws ConfigXMLParseException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         char[] old = this.current;
         this.current = this.next;
         this.len = this.nextLen;
         this.next = old;
         this.nextLen = 0;
         return 4;
      }
   }

   public URI getUri() {
      return this.uri;
   }

   public XMLInputFactory getXmlInputFactory() {
      throw new UnsupportedOperationException();
   }

   public boolean hasNext() throws ConfigXMLParseException {
      if (this.nextLen == 0) {
         int res;
         try {
            res = this.reader.read(this.next);
         } catch (IOException var3) {
            throw ConfigMessages.msg.failedToReadInput(this.getLocation(), var3);
         }

         if (res == -1) {
            return false;
         }

         this.nextLen = res;
      }

      return true;
   }

   public void close() throws ConfigXMLParseException {
      try {
         this.reader.close();
      } catch (IOException var2) {
         throw ConfigMessages.msg.failedToCloseInput(this.getLocation(), var2);
      }
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
      throw new IllegalStateException();
   }

   public String getNamespacePrefix(int index) {
      throw new IllegalStateException();
   }

   public String getNamespaceURI(int index) {
      throw new IllegalStateException();
   }

   public NamespaceContext getNamespaceContext() {
      throw new IllegalStateException();
   }

   public int getEventType() {
      return this.len == 0 ? 7 : 4;
   }

   public String getText() {
      if (this.len == 0) {
         throw new IllegalStateException();
      } else {
         return new String(this.current, 0, this.len);
      }
   }

   public char[] getTextCharacters() {
      return Arrays.copyOf(this.current, this.len);
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) {
      if (sourceStart <= this.len && targetStart <= length) {
         int realLen = Math.min(this.len - sourceStart, length);
         System.arraycopy(this.current, sourceStart, target, targetStart, realLen);
         return realLen;
      } else {
         return 0;
      }
   }

   public int getTextStart() {
      return 0;
   }

   public int getTextLength() {
      if (this.len == 0) {
         throw new IllegalStateException();
      } else {
         return this.len;
      }
   }

   public String getEncoding() {
      return this.charsetName;
   }

   public XMLLocation getLocation() {
      return new XMLLocation(this.includedFrom, this.uri, this.reader.getLineNumber(), this.reader.getColumnNumber(), this.reader.getCharacterOffset());
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
