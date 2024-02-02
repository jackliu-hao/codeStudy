package org.wildfly.client.config;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.wildfly.client.config._private.ConfigMessages;
import org.wildfly.common.expression.Expression;
import org.wildfly.common.net.CidrAddress;
import org.wildfly.common.net.Inet;

public interface ConfigurationXMLStreamReader extends XMLStreamReader, AutoCloseable {
   char[] EMPTY_CHARS = new char[0];

   URI getUri();

   XMLInputFactory getXmlInputFactory();

   XMLLocation getIncludedFrom();

   boolean hasNext() throws ConfigXMLParseException;

   int next() throws ConfigXMLParseException;

   default boolean hasNamespace() {
      String namespaceURI = this.getNamespaceURI();
      return namespaceURI != null && !namespaceURI.isEmpty();
   }

   default boolean namespaceURIEquals(String uri) {
      return uri != null && !uri.isEmpty() ? uri.equals(this.getNamespaceURI()) : !this.hasNamespace();
   }

   default boolean hasAttributeNamespace(int idx) {
      String attributeNamespace = this.getAttributeNamespace(idx);
      return attributeNamespace != null && !attributeNamespace.isEmpty();
   }

   default boolean attributeNamespaceEquals(int idx, String uri) {
      return uri != null && !uri.isEmpty() ? uri.equals(this.getAttributeNamespace(idx)) : !this.hasAttributeNamespace(idx);
   }

   default int nextTag() throws ConfigXMLParseException {
      while(true) {
         int eventType = this.next();
         switch (eventType) {
            case 1:
            case 2:
               return eventType;
            case 3:
            case 5:
            case 6:
               break;
            case 4:
            case 12:
               if (this.isWhiteSpace()) {
                  break;
               }
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               throw ConfigMessages.msg.expectedStartOrEndElement(eventToString(eventType), this.getLocation());
         }
      }
   }

   default String getElementText() throws ConfigXMLParseException {
      int eventType = this.getEventType();
      if (eventType != 1) {
         throw ConfigMessages.msg.expectedStartElement(eventToString(eventType), this.getLocation());
      } else {
         StringBuilder sb = new StringBuilder();

         while(true) {
            eventType = this.next();
            switch (eventType) {
               case 1:
                  throw ConfigMessages.msg.textCannotContainElements(this.getLocation());
               case 2:
                  return sb.toString();
               case 3:
               case 5:
                  break;
               case 4:
               case 6:
               case 9:
               case 12:
                  sb.append(this.getText());
                  break;
               case 7:
               case 10:
               case 11:
               default:
                  throw ConfigMessages.msg.unexpectedContent(eventToString(eventType), this.getLocation());
               case 8:
                  throw ConfigMessages.msg.unexpectedDocumentEnd(this.getLocation());
            }
         }
      }
   }

   default Expression getElementExpression(Expression.Flag... flags) throws ConfigXMLParseException {
      try {
         return Expression.compile(this.getElementText(), flags);
      } catch (IllegalArgumentException var3) {
         throw ConfigMessages.msg.expressionTextParseException(var3, this.getLocation());
      }
   }

   default void require(int type, String namespaceURI, String localName) throws ConfigXMLParseException {
      if (this.getEventType() != type) {
         throw ConfigMessages.msg.expectedEventType(eventToString(type), eventToString(this.getEventType()), this.getLocation());
      } else if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
         throw ConfigMessages.msg.expectedNamespace(namespaceURI, this.getNamespaceURI(), this.getLocation());
      } else if (localName != null && !localName.equals(this.getLocalName())) {
         throw ConfigMessages.msg.expectedLocalName(localName, this.getLocalName(), this.getLocation());
      }
   }

   default boolean isStartElement() {
      return this.getEventType() == 1;
   }

   default boolean isEndElement() {
      return this.getEventType() == 2;
   }

   default boolean isCharacters() {
      return this.getEventType() == 4;
   }

   default boolean isWhiteSpace() {
      return this.getEventType() == 6 || this.getEventType() == 4 && this.getText().trim().isEmpty();
   }

   static String eventToString(int type) {
      switch (type) {
         case 1:
            return "start element";
         case 2:
            return "end element";
         case 3:
            return "processing instruction";
         case 4:
            return "characters";
         case 5:
         default:
            return "unknown";
         case 6:
            return "white space";
         case 7:
            return "document start";
         case 8:
            return "document end";
         case 9:
            return "entity reference";
         case 10:
            return "attribute";
         case 11:
            return "dtd";
         case 12:
            return "cdata";
         case 13:
            return "namespace";
         case 14:
            return "notation declaration";
         case 15:
            return "entity declaration";
      }
   }

   default Object getProperty(String name) throws IllegalArgumentException {
      return null;
   }

   default boolean hasText() {
      switch (this.getEventType()) {
         case 4:
         case 5:
         case 6:
         case 9:
         case 11:
            return true;
         case 7:
         case 8:
         case 10:
         default:
            return false;
      }
   }

   default boolean isStandalone() {
      return false;
   }

   default boolean standaloneSet() {
      return false;
   }

   default boolean hasName() {
      int eventType = this.getEventType();
      return eventType == 1 || eventType == 2;
   }

   XMLLocation getLocation();

   int getTextCharacters(int var1, char[] var2, int var3, int var4) throws ConfigXMLParseException;

   void close() throws ConfigXMLParseException;

   default void skipContent() throws ConfigXMLParseException {
      while(this.hasNext()) {
         switch (this.next()) {
            case 1:
               this.skipContent();
               break;
            case 2:
               return;
         }
      }

   }

   default ConfigXMLParseException unexpectedElement() {
      String namespaceURI = this.getNamespaceURI();
      String localName = this.getLocalName();
      return namespaceURI == null ? ConfigMessages.msg.unexpectedElement(localName, this.getLocation()) : ConfigMessages.msg.unexpectedElement(localName, namespaceURI, this.getLocation());
   }

   default ConfigXMLParseException unexpectedAttribute(int i) {
      return ConfigMessages.msg.unexpectedAttribute(this.getAttributeName(i), this.getLocation());
   }

   default ConfigXMLParseException unexpectedDocumentEnd() {
      return ConfigMessages.msg.unexpectedDocumentEnd(this.getLocation());
   }

   default ConfigXMLParseException unexpectedContent() {
      return ConfigMessages.msg.unexpectedContent(eventToString(this.getEventType()), this.getLocation());
   }

   default ConfigXMLParseException missingRequiredElement(String namespaceUri, String localName) {
      return ConfigMessages.msg.missingRequiredElement(namespaceUri, localName, this.getLocation());
   }

   default ConfigXMLParseException missingRequiredAttribute(String namespaceUri, String localName) {
      return ConfigMessages.msg.missingRequiredAttribute(namespaceUri, localName, this.getLocation());
   }

   default ConfigXMLParseException numericAttributeValueOutOfRange(int index, long minValue, long maxValue) {
      return ConfigMessages.msg.numericAttributeValueOutOfRange(this.getAttributeName(index), this.getAttributeValue(index), minValue, maxValue, this.getLocation());
   }

   String getAttributeValue(int var1);

   default String getAttributeValueResolved(int index) throws ConfigXMLParseException {
      Expression expression = this.getExpressionAttributeValue(index, Expression.Flag.ESCAPES);
      return expression.evaluateWithPropertiesAndEnvironment(false);
   }

   default int getIntAttributeValue(int index) throws ConfigXMLParseException {
      try {
         return Integer.parseInt(this.getAttributeValue(index));
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default int getIntAttributeValueResolved(int index) throws ConfigXMLParseException {
      try {
         return Integer.parseInt(this.getAttributeValueResolved(index));
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default int getIntAttributeValue(int index, int minValue, int maxValue) throws ConfigXMLParseException {
      int value = this.getIntAttributeValue(index);
      if (value >= minValue && value <= maxValue) {
         return value;
      } else {
         throw this.numericAttributeValueOutOfRange(index, (long)minValue, (long)maxValue);
      }
   }

   default int getIntAttributeValueResolved(int index, int minValue, int maxValue) throws ConfigXMLParseException {
      int value = this.getIntAttributeValueResolved(index);
      if (value >= minValue && value <= maxValue) {
         return value;
      } else {
         throw this.numericAttributeValueOutOfRange(index, (long)minValue, (long)maxValue);
      }
   }

   default int[] getIntListAttributeValue(int index) throws ConfigXMLParseException {
      try {
         return (new Delimiterator(this.getAttributeValue(index), ' ')).toIntArray();
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default int[] getIntListAttributeValueResolved(int index) throws ConfigXMLParseException {
      try {
         return (new Delimiterator(this.getAttributeValueResolved(index), ' ')).toIntArray();
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default Iterator<String> getListAttributeValueAsIterator(int index) throws ConfigXMLParseException {
      return new Delimiterator(this.getAttributeValue(index), ' ');
   }

   default Iterator<String> getListAttributeValueAsIteratorResolved(int index) throws ConfigXMLParseException {
      return new Delimiterator(this.getAttributeValueResolved(index), ' ');
   }

   default List<String> getListAttributeValue(int index) throws ConfigXMLParseException {
      return Arrays.asList(this.getListAttributeValueAsArray(index));
   }

   default List<String> getListAttributeValueResolved(int index) throws ConfigXMLParseException {
      return Arrays.asList(this.getListAttributeValueAsArrayResolved(index));
   }

   default String[] getListAttributeValueAsArray(int index) throws ConfigXMLParseException {
      return (new Delimiterator(this.getAttributeValue(index), ' ')).toStringArray();
   }

   default String[] getListAttributeValueAsArrayResolved(int index) throws ConfigXMLParseException {
      return (new Delimiterator(this.getAttributeValueResolved(index), ' ')).toStringArray();
   }

   default long getLongAttributeValue(int index) throws ConfigXMLParseException {
      try {
         return Long.parseLong(this.getAttributeValue(index));
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default long getLongAttributeValueResolved(int index) throws ConfigXMLParseException {
      try {
         return Long.parseLong(this.getAttributeValueResolved(index));
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default long getLongAttributeValue(int index, long minValue, long maxValue) throws ConfigXMLParseException {
      long value = this.getLongAttributeValue(index);
      if (value >= minValue && value <= maxValue) {
         return value;
      } else {
         throw this.numericAttributeValueOutOfRange(index, minValue, maxValue);
      }
   }

   default long getLongAttributeValueResolved(int index, long minValue, long maxValue) throws ConfigXMLParseException {
      long value = this.getLongAttributeValueResolved(index);
      if (value >= minValue && value <= maxValue) {
         return value;
      } else {
         throw this.numericAttributeValueOutOfRange(index, minValue, maxValue);
      }
   }

   default long[] getLongListAttributeValue(int index) throws ConfigXMLParseException {
      try {
         return (new Delimiterator(this.getAttributeValue(index), ' ')).toLongArray();
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default long[] getLongListAttributeValueResolved(int index) throws ConfigXMLParseException {
      try {
         return (new Delimiterator(this.getAttributeValueResolved(index), ' ')).toLongArray();
      } catch (NumberFormatException var3) {
         throw ConfigMessages.msg.intParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default boolean getBooleanAttributeValue(int index) {
      return Boolean.parseBoolean(this.getAttributeValue(index));
   }

   default boolean getBooleanAttributeValueResolved(int index) throws ConfigXMLParseException {
      return Boolean.parseBoolean(this.getAttributeValueResolved(index));
   }

   default URI getURIAttributeValue(int index) throws ConfigXMLParseException {
      try {
         return new URI(this.getAttributeValue(index));
      } catch (URISyntaxException var3) {
         throw ConfigMessages.msg.uriParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default URI getURIAttributeValueResolved(int index) throws ConfigXMLParseException {
      try {
         return new URI(this.getAttributeValueResolved(index));
      } catch (URISyntaxException var3) {
         throw ConfigMessages.msg.uriParseException(var3, this.getAttributeName(index), this.getLocation());
      }
   }

   default Expression getExpressionAttributeValue(int index, Expression.Flag... flags) throws ConfigXMLParseException {
      String attributeValue = this.getAttributeValue(index);
      if (attributeValue == null) {
         return null;
      } else {
         try {
            return Expression.compile(attributeValue, flags);
         } catch (IllegalArgumentException var5) {
            throw ConfigMessages.msg.expressionParseException(var5, this.getAttributeName(index), this.getLocation());
         }
      }
   }

   default InetAddress getInetAddressAttributeValue(int index) throws ConfigXMLParseException {
      String attributeValue = this.getAttributeValue(index);
      if (attributeValue == null) {
         return null;
      } else {
         InetAddress inetAddress = Inet.parseInetAddress(attributeValue);
         if (inetAddress == null) {
            throw ConfigMessages.msg.inetAddressParseException(this.getAttributeName(index), attributeValue, this.getLocation());
         } else {
            return inetAddress;
         }
      }
   }

   default InetAddress getInetAddressAttributeValueResolved(int index) throws ConfigXMLParseException {
      String attributeValue = this.getAttributeValueResolved(index);
      if (attributeValue == null) {
         return null;
      } else {
         InetAddress inetAddress = Inet.parseInetAddress(attributeValue);
         if (inetAddress == null) {
            throw ConfigMessages.msg.inetAddressParseException(this.getAttributeName(index), attributeValue, this.getLocation());
         } else {
            return inetAddress;
         }
      }
   }

   default CidrAddress getCidrAddressAttributeValue(int index) throws ConfigXMLParseException {
      String attributeValue = this.getAttributeValue(index);
      if (attributeValue == null) {
         return null;
      } else {
         CidrAddress cidrAddress = Inet.parseCidrAddress(attributeValue);
         if (cidrAddress == null) {
            throw ConfigMessages.msg.cidrAddressParseException(this.getAttributeName(index), attributeValue, this.getLocation());
         } else {
            return cidrAddress;
         }
      }
   }

   default CidrAddress getCidrAddressAttributeValueResolved(int index) throws ConfigXMLParseException {
      String attributeValue = this.getAttributeValueResolved(index);
      if (attributeValue == null) {
         return null;
      } else {
         CidrAddress cidrAddress = Inet.parseCidrAddress(attributeValue);
         if (cidrAddress == null) {
            throw ConfigMessages.msg.cidrAddressParseException(this.getAttributeName(index), attributeValue, this.getLocation());
         } else {
            return cidrAddress;
         }
      }
   }
}
