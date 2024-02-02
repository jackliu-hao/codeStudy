package org.wildfly.client.config._private;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Locale;
import javax.xml.namespace.QName;
import org.wildfly.client.config.ConfigXMLParseException;
import org.wildfly.client.config.XMLLocation;

public class ConfigMessages_$bundle implements ConfigMessages, Serializable {
   private static final long serialVersionUID = 1L;
   public static final ConfigMessages_$bundle INSTANCE = new ConfigMessages_$bundle();
   private static final Locale LOCALE;
   private static final String parseError = "CONF0001: An unspecified XML parse error occurred";
   private static final String closeNotSupported = "CONF0002: Calling close() on XMLConfigurationReader is not supported";
   private static final String unexpectedDocumentEnd = "CONF0003: Unexpected end of document";
   private static final String unexpectedContent = "CONF0004: Unexpected content of type \"%s\"";
   private static final String unexpectedElement2 = "CONF0005: Unexpected element \"%s\" in namespace \"%s\" encountered";
   private static final String unexpectedElement1 = "CONF0005: Unexpected element \"%s\" (no namespace) encountered";
   private static final String expectedStartOrEndElement = "CONF0006: Expected start or end element, found \"%s\"";
   private static final String expectedStartElement = "CONF0007: Expected start element, found \"%s\"";
   private static final String textCannotContainElements = "CONF0008: Text content cannot contain elements";
   private static final String expectedEventType = "CONF0009: Expected event type \"%s\", found \"%s\"";
   private static final String expectedNamespace = "CONF0010: Expected namespace URI \"%s\", found \"%s\"";
   private static final String expectedLocalName = "CONF0011: Expected local name \"%s\", found \"%s\"";
   private static final String failedToReadInput = "CONF0012: Failed to read from input source";
   private static final String failedToCloseInput = "CONF0013: Failed to close input source";
   private static final String invalidUrl = "CONF0014: Invalid configuration file URL";
   private static final String unexpectedAttribute = "CONF0015: Unexpected attribute \"%s\" encountered";
   private static final String missingRequiredElement = "CONF0016: Missing required element \"%2$s\" from namespace \"%s\"";
   private static final String missingRequiredAttribute = "CONF0017: Missing required attribute \"%2$s\" from namespace \"%s\"";
   private static final String intParseException = "CONF0018: Failed to parse integer value of attribute \"%s\"";
   private static final String uriParseException = "CONF0019: Failed to parse URI value of attribute \"%s\"";
   private static final String expressionParseException = "CONF0020: Failed to parse expression value of attribute \"%s\"";
   private static final String expressionTextParseException = "CONF0021: Failed to parse expression text";
   private static final String numericAttributeValueOutOfRange = "CONF0022: Numeric value of attribute \"%s\" is out of range; actual value is \"%s\" but the value must not be less than %d or more than %d";
   private static final String inetAddressParseException = "CONF0023: Failed to parse IP address value of attribute \"%s\": \"%s\" is not a valid IP address";
   private static final String cidrAddressParseException = "CONF0024: Failed to parse CIDR address value of attribute \"%s\": \"%s\" is not a valid CIDR address";

   protected ConfigMessages_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String parseError$str() {
      return "CONF0001: An unspecified XML parse error occurred";
   }

   public final String parseError() {
      return String.format(this.getLoggingLocale(), this.parseError$str());
   }

   protected String closeNotSupported$str() {
      return "CONF0002: Calling close() on XMLConfigurationReader is not supported";
   }

   public final UnsupportedOperationException closeNotSupported() {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.closeNotSupported$str()));
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String unexpectedDocumentEnd$str() {
      return "CONF0003: Unexpected end of document";
   }

   public final ConfigXMLParseException unexpectedDocumentEnd(XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.unexpectedDocumentEnd$str()), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String unexpectedContent$str() {
      return "CONF0004: Unexpected content of type \"%s\"";
   }

   public final ConfigXMLParseException unexpectedContent(String eventType, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.unexpectedContent$str(), eventType), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String unexpectedElement2$str() {
      return "CONF0005: Unexpected element \"%s\" in namespace \"%s\" encountered";
   }

   public final ConfigXMLParseException unexpectedElement(String localName, String namespaceUri, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.unexpectedElement2$str(), localName, namespaceUri), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String unexpectedElement1$str() {
      return "CONF0005: Unexpected element \"%s\" (no namespace) encountered";
   }

   public final ConfigXMLParseException unexpectedElement(String localName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.unexpectedElement1$str(), localName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expectedStartOrEndElement$str() {
      return "CONF0006: Expected start or end element, found \"%s\"";
   }

   public final ConfigXMLParseException expectedStartOrEndElement(String eventTypeName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expectedStartOrEndElement$str(), eventTypeName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expectedStartElement$str() {
      return "CONF0007: Expected start element, found \"%s\"";
   }

   public final ConfigXMLParseException expectedStartElement(String eventTypeName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expectedStartElement$str(), eventTypeName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String textCannotContainElements$str() {
      return "CONF0008: Text content cannot contain elements";
   }

   public final ConfigXMLParseException textCannotContainElements(XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.textCannotContainElements$str()), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expectedEventType$str() {
      return "CONF0009: Expected event type \"%s\", found \"%s\"";
   }

   public final ConfigXMLParseException expectedEventType(String expectedEventTypeName, String eventTypeName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expectedEventType$str(), expectedEventTypeName, eventTypeName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expectedNamespace$str() {
      return "CONF0010: Expected namespace URI \"%s\", found \"%s\"";
   }

   public final ConfigXMLParseException expectedNamespace(String expectedNamespaceURI, String actualNamespaceURI, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expectedNamespace$str(), expectedNamespaceURI, actualNamespaceURI), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expectedLocalName$str() {
      return "CONF0011: Expected local name \"%s\", found \"%s\"";
   }

   public final ConfigXMLParseException expectedLocalName(String expectedLocalName, String actualLocalName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expectedLocalName$str(), expectedLocalName, actualLocalName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String failedToReadInput$str() {
      return "CONF0012: Failed to read from input source";
   }

   public final ConfigXMLParseException failedToReadInput(XMLLocation location, IOException cause) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.failedToReadInput$str()), location, cause);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String failedToCloseInput$str() {
      return "CONF0013: Failed to close input source";
   }

   public final ConfigXMLParseException failedToCloseInput(XMLLocation location, IOException cause) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.failedToCloseInput$str()), location, cause);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String invalidUrl$str() {
      return "CONF0014: Invalid configuration file URL";
   }

   public final ConfigXMLParseException invalidUrl(XMLLocation location, MalformedURLException e) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.invalidUrl$str()), location, e);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String unexpectedAttribute$str() {
      return "CONF0015: Unexpected attribute \"%s\" encountered";
   }

   public final ConfigXMLParseException unexpectedAttribute(QName name, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.unexpectedAttribute$str(), name), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String missingRequiredElement$str() {
      return "CONF0016: Missing required element \"%2$s\" from namespace \"%s\"";
   }

   public final ConfigXMLParseException missingRequiredElement(String namespaceUri, String localName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.missingRequiredElement$str(), namespaceUri, localName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String missingRequiredAttribute$str() {
      return "CONF0017: Missing required attribute \"%2$s\" from namespace \"%s\"";
   }

   public final ConfigXMLParseException missingRequiredAttribute(String namespaceUri, String localName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.missingRequiredAttribute$str(), namespaceUri, localName), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String intParseException$str() {
      return "CONF0018: Failed to parse integer value of attribute \"%s\"";
   }

   public final ConfigXMLParseException intParseException(NumberFormatException e, QName attributeName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.intParseException$str(), attributeName), location, e);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String uriParseException$str() {
      return "CONF0019: Failed to parse URI value of attribute \"%s\"";
   }

   public final ConfigXMLParseException uriParseException(URISyntaxException e, QName attributeName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.uriParseException$str(), attributeName), location, e);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expressionParseException$str() {
      return "CONF0020: Failed to parse expression value of attribute \"%s\"";
   }

   public final ConfigXMLParseException expressionParseException(IllegalArgumentException ex, QName attributeName, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expressionParseException$str(), attributeName), location, ex);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String expressionTextParseException$str() {
      return "CONF0021: Failed to parse expression text";
   }

   public final ConfigXMLParseException expressionTextParseException(IllegalArgumentException ex, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.expressionTextParseException$str()), location, ex);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String numericAttributeValueOutOfRange$str() {
      return "CONF0022: Numeric value of attribute \"%s\" is out of range; actual value is \"%s\" but the value must not be less than %d or more than %d";
   }

   public final ConfigXMLParseException numericAttributeValueOutOfRange(QName attributeName, String actualValue, long minValue, long maxValue, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.numericAttributeValueOutOfRange$str(), attributeName, actualValue, minValue, maxValue), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String inetAddressParseException$str() {
      return "CONF0023: Failed to parse IP address value of attribute \"%s\": \"%s\" is not a valid IP address";
   }

   public final ConfigXMLParseException inetAddressParseException(QName attributeName, String address, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.inetAddressParseException$str(), attributeName, address), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String cidrAddressParseException$str() {
      return "CONF0024: Failed to parse CIDR address value of attribute \"%s\": \"%s\" is not a valid CIDR address";
   }

   public final ConfigXMLParseException cidrAddressParseException(QName attributeName, String address, XMLLocation location) {
      ConfigXMLParseException result = new ConfigXMLParseException(String.format(this.getLoggingLocale(), this.cidrAddressParseException$str(), attributeName, address), location);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
