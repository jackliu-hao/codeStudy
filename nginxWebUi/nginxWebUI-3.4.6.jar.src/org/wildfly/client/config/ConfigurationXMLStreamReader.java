/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.wildfly.client.config._private.ConfigMessages;
/*     */ import org.wildfly.common.expression.Expression;
/*     */ import org.wildfly.common.net.CidrAddress;
/*     */ import org.wildfly.common.net.Inet;
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
/*     */ 
/*     */ 
/*     */ public interface ConfigurationXMLStreamReader
/*     */   extends XMLStreamReader, AutoCloseable
/*     */ {
/*  42 */   public static final char[] EMPTY_CHARS = new char[0];
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
/*     */   default boolean hasNamespace() {
/*  60 */     String namespaceURI = getNamespaceURI();
/*  61 */     return (namespaceURI != null && !namespaceURI.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean namespaceURIEquals(String uri) {
/*  73 */     return (uri == null || uri.isEmpty()) ? (!hasNamespace()) : uri.equals(getNamespaceURI());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean hasAttributeNamespace(int idx) {
/*  83 */     String attributeNamespace = getAttributeNamespace(idx);
/*  84 */     return (attributeNamespace != null && !attributeNamespace.isEmpty());
/*     */   }
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
/*     */   default boolean attributeNamespaceEquals(int idx, String uri) {
/*  97 */     return (uri == null || uri.isEmpty()) ? (!hasAttributeNamespace(idx)) : uri.equals(getAttributeNamespace(idx));
/*     */   }
/*     */   
/*     */   default int nextTag() throws ConfigXMLParseException {
/*     */     int eventType;
/*     */     while (true) {
/* 103 */       eventType = next();
/* 104 */       switch (eventType) {
/*     */         case 3:
/*     */         case 5:
/*     */         case 6:
/*     */           continue;
/*     */         
/*     */         case 1:
/*     */         case 2:
/* 112 */           return eventType;
/*     */         
/*     */         case 4:
/*     */         case 12:
/* 116 */           if (isWhiteSpace())
/*     */             continue; 
/*     */           break;
/*     */       } 
/*     */       break;
/*     */     } 
/* 122 */     throw ConfigMessages.msg.expectedStartOrEndElement(eventToString(eventType), getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String getElementText() throws ConfigXMLParseException {
/* 129 */     int eventType = getEventType();
/* 130 */     if (eventType != 1) {
/* 131 */       throw ConfigMessages.msg.expectedStartElement(eventToString(eventType), getLocation());
/*     */     }
/* 133 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/* 135 */       eventType = next();
/* 136 */       switch (eventType) {
/*     */         case 2:
/* 138 */           return sb.toString();
/*     */         
/*     */         case 4:
/*     */         case 6:
/*     */         case 9:
/*     */         case 12:
/* 144 */           sb.append(getText());
/*     */           continue;
/*     */ 
/*     */         
/*     */         case 3:
/*     */         case 5:
/*     */           continue;
/*     */         
/*     */         case 8:
/* 153 */           throw ConfigMessages.msg.unexpectedDocumentEnd(getLocation());
/*     */         
/*     */         case 1:
/* 156 */           throw ConfigMessages.msg.textCannotContainElements(getLocation());
/*     */       }  break;
/*     */     } 
/* 159 */     throw ConfigMessages.msg.unexpectedContent(eventToString(eventType), getLocation());
/*     */   }
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
/*     */   Expression getElementExpression(Expression.Flag... flags) throws ConfigXMLParseException {
/*     */     try {
/* 174 */       return Expression.compile(getElementText(), flags);
/* 175 */     } catch (IllegalArgumentException ex) {
/* 176 */       throw ConfigMessages.msg.expressionTextParseException(ex, getLocation());
/*     */     } 
/*     */   }
/*     */   
/*     */   default void require(int type, String namespaceURI, String localName) throws ConfigXMLParseException {
/* 181 */     if (getEventType() != type)
/* 182 */       throw ConfigMessages.msg.expectedEventType(eventToString(type), eventToString(getEventType()), getLocation()); 
/* 183 */     if (namespaceURI != null && !namespaceURI.equals(getNamespaceURI()))
/* 184 */       throw ConfigMessages.msg.expectedNamespace(namespaceURI, getNamespaceURI(), getLocation()); 
/* 185 */     if (localName != null && !localName.equals(getLocalName())) {
/* 186 */       throw ConfigMessages.msg.expectedLocalName(localName, getLocalName(), getLocation());
/*     */     }
/*     */   }
/*     */   
/*     */   default boolean isStartElement() {
/* 191 */     return (getEventType() == 1);
/*     */   }
/*     */   
/*     */   default boolean isEndElement() {
/* 195 */     return (getEventType() == 2);
/*     */   }
/*     */   
/*     */   default boolean isCharacters() {
/* 199 */     return (getEventType() == 4);
/*     */   }
/*     */   
/*     */   default boolean isWhiteSpace() {
/* 203 */     return (getEventType() == 6 || (getEventType() == 4 && getText().trim().isEmpty()));
/*     */   }
/*     */   
/*     */   static String eventToString(int type) {
/* 207 */     switch (type) { case 7:
/* 208 */         return "document start";
/* 209 */       case 8: return "document end";
/* 210 */       case 1: return "start element";
/* 211 */       case 2: return "end element";
/* 212 */       case 12: return "cdata";
/* 213 */       case 4: return "characters";
/* 214 */       case 10: return "attribute";
/* 215 */       case 11: return "dtd";
/* 216 */       case 15: return "entity declaration";
/* 217 */       case 9: return "entity reference";
/* 218 */       case 13: return "namespace";
/* 219 */       case 14: return "notation declaration";
/* 220 */       case 3: return "processing instruction";
/* 221 */       case 6: return "white space"; }
/* 222 */      return "unknown";
/*     */   }
/*     */ 
/*     */   
/*     */   default Object getProperty(String name) throws IllegalArgumentException {
/* 227 */     return null;
/*     */   }
/*     */   
/*     */   default boolean hasText() {
/* 231 */     switch (getEventType()) {
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 9:
/*     */       case 11:
/* 237 */         return true;
/*     */     } 
/*     */     
/* 240 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean isStandalone() {
/* 246 */     return false;
/*     */   }
/*     */   
/*     */   default boolean standaloneSet() {
/* 250 */     return false;
/*     */   }
/*     */   
/*     */   default boolean hasName() {
/* 254 */     int eventType = getEventType();
/* 255 */     return (eventType == 1 || eventType == 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void skipContent() throws ConfigXMLParseException {
/* 265 */     while (hasNext()) {
/* 266 */       switch (next()) {
/*     */         case 1:
/* 268 */           skipContent();
/*     */         case 2:
/*     */           return;
/*     */       } 
/*     */     } 
/*     */   }
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
/*     */   default ConfigXMLParseException unexpectedElement() {
/* 286 */     String namespaceURI = getNamespaceURI();
/* 287 */     String localName = getLocalName();
/* 288 */     if (namespaceURI == null) {
/* 289 */       return ConfigMessages.msg.unexpectedElement(localName, getLocation());
/*     */     }
/* 291 */     return ConfigMessages.msg.unexpectedElement(localName, namespaceURI, getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ConfigXMLParseException unexpectedAttribute(int i) {
/* 303 */     return ConfigMessages.msg.unexpectedAttribute(getAttributeName(i), getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ConfigXMLParseException unexpectedDocumentEnd() {
/* 312 */     return ConfigMessages.msg.unexpectedDocumentEnd(getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ConfigXMLParseException unexpectedContent() {
/* 321 */     return ConfigMessages.msg.unexpectedContent(eventToString(getEventType()), getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ConfigXMLParseException missingRequiredElement(String namespaceUri, String localName) {
/* 332 */     return ConfigMessages.msg.missingRequiredElement(namespaceUri, localName, getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ConfigXMLParseException missingRequiredAttribute(String namespaceUri, String localName) {
/* 343 */     return ConfigMessages.msg.missingRequiredAttribute(namespaceUri, localName, getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default ConfigXMLParseException numericAttributeValueOutOfRange(int index, long minValue, long maxValue) {
/* 355 */     return ConfigMessages.msg.numericAttributeValueOutOfRange(getAttributeName(index), getAttributeValue(index), minValue, maxValue, getLocation());
/*     */   }
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
/*     */   default String getAttributeValueResolved(int index) throws ConfigXMLParseException {
/* 376 */     Expression expression = getExpressionAttributeValue(index, new Expression.Flag[] { Expression.Flag.ESCAPES });
/* 377 */     return expression.evaluateWithPropertiesAndEnvironment(false);
/*     */   }
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
/*     */   default int getIntAttributeValue(int index) throws ConfigXMLParseException {
/*     */     try {
/* 391 */       return Integer.parseInt(getAttributeValue(index));
/* 392 */     } catch (NumberFormatException e) {
/* 393 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
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
/*     */   default int getIntAttributeValueResolved(int index) throws ConfigXMLParseException {
/*     */     try {
/* 408 */       return Integer.parseInt(getAttributeValueResolved(index));
/* 409 */     } catch (NumberFormatException e) {
/* 410 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
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
/*     */   default int getIntAttributeValue(int index, int minValue, int maxValue) throws ConfigXMLParseException {
/* 426 */     int value = getIntAttributeValue(index);
/* 427 */     if (value < minValue || value > maxValue) {
/* 428 */       throw numericAttributeValueOutOfRange(index, minValue, maxValue);
/*     */     }
/* 430 */     return value;
/*     */   }
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
/*     */   default int getIntAttributeValueResolved(int index, int minValue, int maxValue) throws ConfigXMLParseException {
/* 445 */     int value = getIntAttributeValueResolved(index);
/* 446 */     if (value < minValue || value > maxValue) {
/* 447 */       throw numericAttributeValueOutOfRange(index, minValue, maxValue);
/*     */     }
/* 449 */     return value;
/*     */   }
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
/*     */   default int[] getIntListAttributeValue(int index) throws ConfigXMLParseException {
/*     */     try {
/* 463 */       return (new Delimiterator(getAttributeValue(index), ' ')).toIntArray();
/* 464 */     } catch (NumberFormatException e) {
/* 465 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
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
/*     */   default int[] getIntListAttributeValueResolved(int index) throws ConfigXMLParseException {
/*     */     try {
/* 480 */       return (new Delimiterator(getAttributeValueResolved(index), ' ')).toIntArray();
/* 481 */     } catch (NumberFormatException e) {
/* 482 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Iterator<String> getListAttributeValueAsIterator(int index) throws ConfigXMLParseException {
/* 494 */     return new Delimiterator(getAttributeValue(index), ' ');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Iterator<String> getListAttributeValueAsIteratorResolved(int index) throws ConfigXMLParseException {
/* 506 */     return new Delimiterator(getAttributeValueResolved(index), ' ');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default List<String> getListAttributeValue(int index) throws ConfigXMLParseException {
/* 517 */     return Arrays.asList(getListAttributeValueAsArray(index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default List<String> getListAttributeValueResolved(int index) throws ConfigXMLParseException {
/* 528 */     return Arrays.asList(getListAttributeValueAsArrayResolved(index));
/*     */   }
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
/*     */   default String[] getListAttributeValueAsArray(int index) throws ConfigXMLParseException {
/* 541 */     return (new Delimiterator(getAttributeValue(index), ' ')).toStringArray();
/*     */   }
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
/*     */   default String[] getListAttributeValueAsArrayResolved(int index) throws ConfigXMLParseException {
/* 554 */     return (new Delimiterator(getAttributeValueResolved(index), ' ')).toStringArray();
/*     */   }
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
/*     */   default long getLongAttributeValue(int index) throws ConfigXMLParseException {
/*     */     try {
/* 568 */       return Long.parseLong(getAttributeValue(index));
/* 569 */     } catch (NumberFormatException e) {
/* 570 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
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
/*     */   default long getLongAttributeValueResolved(int index) throws ConfigXMLParseException {
/*     */     try {
/* 585 */       return Long.parseLong(getAttributeValueResolved(index));
/* 586 */     } catch (NumberFormatException e) {
/* 587 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
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
/*     */   default long getLongAttributeValue(int index, long minValue, long maxValue) throws ConfigXMLParseException {
/* 603 */     long value = getLongAttributeValue(index);
/* 604 */     if (value < minValue || value > maxValue) {
/* 605 */       throw numericAttributeValueOutOfRange(index, minValue, maxValue);
/*     */     }
/* 607 */     return value;
/*     */   }
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
/*     */   default long getLongAttributeValueResolved(int index, long minValue, long maxValue) throws ConfigXMLParseException {
/* 622 */     long value = getLongAttributeValueResolved(index);
/* 623 */     if (value < minValue || value > maxValue) {
/* 624 */       throw numericAttributeValueOutOfRange(index, minValue, maxValue);
/*     */     }
/* 626 */     return value;
/*     */   }
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
/*     */   default long[] getLongListAttributeValue(int index) throws ConfigXMLParseException {
/*     */     try {
/* 640 */       return (new Delimiterator(getAttributeValue(index), ' ')).toLongArray();
/* 641 */     } catch (NumberFormatException e) {
/* 642 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
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
/*     */   default long[] getLongListAttributeValueResolved(int index) throws ConfigXMLParseException {
/*     */     try {
/* 657 */       return (new Delimiterator(getAttributeValueResolved(index), ' ')).toLongArray();
/* 658 */     } catch (NumberFormatException e) {
/* 659 */       throw ConfigMessages.msg.intParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean getBooleanAttributeValue(int index) {
/* 671 */     return Boolean.parseBoolean(getAttributeValue(index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean getBooleanAttributeValueResolved(int index) throws ConfigXMLParseException {
/* 683 */     return Boolean.parseBoolean(getAttributeValueResolved(index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default URI getURIAttributeValue(int index) throws ConfigXMLParseException {
/*     */     try {
/* 695 */       return new URI(getAttributeValue(index));
/* 696 */     } catch (URISyntaxException e) {
/* 697 */       throw ConfigMessages.msg.uriParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default URI getURIAttributeValueResolved(int index) throws ConfigXMLParseException {
/*     */     try {
/* 710 */       return new URI(getAttributeValueResolved(index));
/* 711 */     } catch (URISyntaxException e) {
/* 712 */       throw ConfigMessages.msg.uriParseException(e, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Expression getExpressionAttributeValue(int index, Expression.Flag... flags) throws ConfigXMLParseException {
/* 725 */     String attributeValue = getAttributeValue(index);
/* 726 */     if (attributeValue == null)
/* 727 */       return null; 
/*     */     try {
/* 729 */       return Expression.compile(attributeValue, flags);
/* 730 */     } catch (IllegalArgumentException ex) {
/* 731 */       throw ConfigMessages.msg.expressionParseException(ex, getAttributeName(index), getLocation());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default InetAddress getInetAddressAttributeValue(int index) throws ConfigXMLParseException {
/* 743 */     String attributeValue = getAttributeValue(index);
/* 744 */     if (attributeValue == null) {
/* 745 */       return null;
/*     */     }
/* 747 */     InetAddress inetAddress = Inet.parseInetAddress(attributeValue);
/* 748 */     if (inetAddress == null) {
/* 749 */       throw ConfigMessages.msg.inetAddressParseException(getAttributeName(index), attributeValue, getLocation());
/*     */     }
/* 751 */     return inetAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default InetAddress getInetAddressAttributeValueResolved(int index) throws ConfigXMLParseException {
/* 763 */     String attributeValue = getAttributeValueResolved(index);
/* 764 */     if (attributeValue == null) {
/* 765 */       return null;
/*     */     }
/* 767 */     InetAddress inetAddress = Inet.parseInetAddress(attributeValue);
/* 768 */     if (inetAddress == null) {
/* 769 */       throw ConfigMessages.msg.inetAddressParseException(getAttributeName(index), attributeValue, getLocation());
/*     */     }
/* 771 */     return inetAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default CidrAddress getCidrAddressAttributeValue(int index) throws ConfigXMLParseException {
/* 783 */     String attributeValue = getAttributeValue(index);
/* 784 */     if (attributeValue == null) {
/* 785 */       return null;
/*     */     }
/* 787 */     CidrAddress cidrAddress = Inet.parseCidrAddress(attributeValue);
/* 788 */     if (cidrAddress == null) {
/* 789 */       throw ConfigMessages.msg.cidrAddressParseException(getAttributeName(index), attributeValue, getLocation());
/*     */     }
/* 791 */     return cidrAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default CidrAddress getCidrAddressAttributeValueResolved(int index) throws ConfigXMLParseException {
/* 803 */     String attributeValue = getAttributeValueResolved(index);
/* 804 */     if (attributeValue == null) {
/* 805 */       return null;
/*     */     }
/* 807 */     CidrAddress cidrAddress = Inet.parseCidrAddress(attributeValue);
/* 808 */     if (cidrAddress == null) {
/* 809 */       throw ConfigMessages.msg.cidrAddressParseException(getAttributeName(index), attributeValue, getLocation());
/*     */     }
/* 811 */     return cidrAddress;
/*     */   }
/*     */   
/*     */   URI getUri();
/*     */   
/*     */   XMLInputFactory getXmlInputFactory();
/*     */   
/*     */   XMLLocation getIncludedFrom();
/*     */   
/*     */   boolean hasNext() throws ConfigXMLParseException;
/*     */   
/*     */   int next() throws ConfigXMLParseException;
/*     */   
/*     */   XMLLocation getLocation();
/*     */   
/*     */   int getTextCharacters(int paramInt1, char[] paramArrayOfchar, int paramInt2, int paramInt3) throws ConfigXMLParseException;
/*     */   
/*     */   void close() throws ConfigXMLParseException;
/*     */   
/*     */   String getAttributeValue(int paramInt);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\ConfigurationXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */