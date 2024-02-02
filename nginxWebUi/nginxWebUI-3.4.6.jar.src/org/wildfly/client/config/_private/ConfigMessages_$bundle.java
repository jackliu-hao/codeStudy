/*     */ package org.wildfly.client.config._private;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.Location;
/*     */ import org.wildfly.client.config.ConfigXMLParseException;
/*     */ import org.wildfly.client.config.XMLLocation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigMessages_$bundle
/*     */   implements ConfigMessages, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  25 */   public static final ConfigMessages_$bundle INSTANCE = new ConfigMessages_$bundle();
/*     */   protected Object readResolve() {
/*  27 */     return INSTANCE;
/*     */   }
/*  29 */   private static final Locale LOCALE = Locale.ROOT; private static final String parseError = "CONF0001: An unspecified XML parse error occurred"; private static final String closeNotSupported = "CONF0002: Calling close() on XMLConfigurationReader is not supported"; private static final String unexpectedDocumentEnd = "CONF0003: Unexpected end of document"; private static final String unexpectedContent = "CONF0004: Unexpected content of type \"%s\""; private static final String unexpectedElement2 = "CONF0005: Unexpected element \"%s\" in namespace \"%s\" encountered"; private static final String unexpectedElement1 = "CONF0005: Unexpected element \"%s\" (no namespace) encountered"; private static final String expectedStartOrEndElement = "CONF0006: Expected start or end element, found \"%s\""; private static final String expectedStartElement = "CONF0007: Expected start element, found \"%s\""; private static final String textCannotContainElements = "CONF0008: Text content cannot contain elements"; private static final String expectedEventType = "CONF0009: Expected event type \"%s\", found \"%s\""; private static final String expectedNamespace = "CONF0010: Expected namespace URI \"%s\", found \"%s\""; private static final String expectedLocalName = "CONF0011: Expected local name \"%s\", found \"%s\"";
/*     */   protected Locale getLoggingLocale() {
/*  31 */     return LOCALE;
/*     */   }
/*     */   private static final String failedToReadInput = "CONF0012: Failed to read from input source"; private static final String failedToCloseInput = "CONF0013: Failed to close input source"; private static final String invalidUrl = "CONF0014: Invalid configuration file URL"; private static final String unexpectedAttribute = "CONF0015: Unexpected attribute \"%s\" encountered"; private static final String missingRequiredElement = "CONF0016: Missing required element \"%2$s\" from namespace \"%s\""; private static final String missingRequiredAttribute = "CONF0017: Missing required attribute \"%2$s\" from namespace \"%s\""; private static final String intParseException = "CONF0018: Failed to parse integer value of attribute \"%s\""; private static final String uriParseException = "CONF0019: Failed to parse URI value of attribute \"%s\""; private static final String expressionParseException = "CONF0020: Failed to parse expression value of attribute \"%s\""; private static final String expressionTextParseException = "CONF0021: Failed to parse expression text"; private static final String numericAttributeValueOutOfRange = "CONF0022: Numeric value of attribute \"%s\" is out of range; actual value is \"%s\" but the value must not be less than %d or more than %d"; private static final String inetAddressParseException = "CONF0023: Failed to parse IP address value of attribute \"%s\": \"%s\" is not a valid IP address"; private static final String cidrAddressParseException = "CONF0024: Failed to parse CIDR address value of attribute \"%s\": \"%s\" is not a valid CIDR address";
/*     */   protected String parseError$str() {
/*  35 */     return "CONF0001: An unspecified XML parse error occurred";
/*     */   }
/*     */   
/*     */   public final String parseError() {
/*  39 */     return String.format(getLoggingLocale(), parseError$str(), new Object[0]);
/*     */   }
/*     */   
/*     */   protected String closeNotSupported$str() {
/*  43 */     return "CONF0002: Calling close() on XMLConfigurationReader is not supported";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException closeNotSupported() {
/*  47 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), closeNotSupported$str(), new Object[0]));
/*  48 */     StackTraceElement[] st = result.getStackTrace();
/*  49 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*  50 */     return result;
/*     */   }
/*     */   
/*     */   protected String unexpectedDocumentEnd$str() {
/*  54 */     return "CONF0003: Unexpected end of document";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException unexpectedDocumentEnd(XMLLocation location) {
/*  58 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), unexpectedDocumentEnd$str(), new Object[0]), (Location)location);
/*  59 */     StackTraceElement[] st = result.getStackTrace();
/*  60 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*  61 */     return result;
/*     */   }
/*     */   
/*     */   protected String unexpectedContent$str() {
/*  65 */     return "CONF0004: Unexpected content of type \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException unexpectedContent(String eventType, XMLLocation location) {
/*  69 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), unexpectedContent$str(), new Object[] { eventType }), (Location)location);
/*  70 */     StackTraceElement[] st = result.getStackTrace();
/*  71 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*  72 */     return result;
/*     */   }
/*     */   
/*     */   protected String unexpectedElement2$str() {
/*  76 */     return "CONF0005: Unexpected element \"%s\" in namespace \"%s\" encountered";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException unexpectedElement(String localName, String namespaceUri, XMLLocation location) {
/*  80 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), unexpectedElement2$str(), new Object[] { localName, namespaceUri }), (Location)location);
/*  81 */     StackTraceElement[] st = result.getStackTrace();
/*  82 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*  83 */     return result;
/*     */   }
/*     */   
/*     */   protected String unexpectedElement1$str() {
/*  87 */     return "CONF0005: Unexpected element \"%s\" (no namespace) encountered";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException unexpectedElement(String localName, XMLLocation location) {
/*  91 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), unexpectedElement1$str(), new Object[] { localName }), (Location)location);
/*  92 */     StackTraceElement[] st = result.getStackTrace();
/*  93 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*  94 */     return result;
/*     */   }
/*     */   
/*     */   protected String expectedStartOrEndElement$str() {
/*  98 */     return "CONF0006: Expected start or end element, found \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expectedStartOrEndElement(String eventTypeName, XMLLocation location) {
/* 102 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expectedStartOrEndElement$str(), new Object[] { eventTypeName }), (Location)location);
/* 103 */     StackTraceElement[] st = result.getStackTrace();
/* 104 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 105 */     return result;
/*     */   }
/*     */   
/*     */   protected String expectedStartElement$str() {
/* 109 */     return "CONF0007: Expected start element, found \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expectedStartElement(String eventTypeName, XMLLocation location) {
/* 113 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expectedStartElement$str(), new Object[] { eventTypeName }), (Location)location);
/* 114 */     StackTraceElement[] st = result.getStackTrace();
/* 115 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 116 */     return result;
/*     */   }
/*     */   
/*     */   protected String textCannotContainElements$str() {
/* 120 */     return "CONF0008: Text content cannot contain elements";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException textCannotContainElements(XMLLocation location) {
/* 124 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), textCannotContainElements$str(), new Object[0]), (Location)location);
/* 125 */     StackTraceElement[] st = result.getStackTrace();
/* 126 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 127 */     return result;
/*     */   }
/*     */   
/*     */   protected String expectedEventType$str() {
/* 131 */     return "CONF0009: Expected event type \"%s\", found \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expectedEventType(String expectedEventTypeName, String eventTypeName, XMLLocation location) {
/* 135 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expectedEventType$str(), new Object[] { expectedEventTypeName, eventTypeName }), (Location)location);
/* 136 */     StackTraceElement[] st = result.getStackTrace();
/* 137 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 138 */     return result;
/*     */   }
/*     */   
/*     */   protected String expectedNamespace$str() {
/* 142 */     return "CONF0010: Expected namespace URI \"%s\", found \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expectedNamespace(String expectedNamespaceURI, String actualNamespaceURI, XMLLocation location) {
/* 146 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expectedNamespace$str(), new Object[] { expectedNamespaceURI, actualNamespaceURI }), (Location)location);
/* 147 */     StackTraceElement[] st = result.getStackTrace();
/* 148 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 149 */     return result;
/*     */   }
/*     */   
/*     */   protected String expectedLocalName$str() {
/* 153 */     return "CONF0011: Expected local name \"%s\", found \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expectedLocalName(String expectedLocalName, String actualLocalName, XMLLocation location) {
/* 157 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expectedLocalName$str(), new Object[] { expectedLocalName, actualLocalName }), (Location)location);
/* 158 */     StackTraceElement[] st = result.getStackTrace();
/* 159 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 160 */     return result;
/*     */   }
/*     */   
/*     */   protected String failedToReadInput$str() {
/* 164 */     return "CONF0012: Failed to read from input source";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException failedToReadInput(XMLLocation location, IOException cause) {
/* 168 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), failedToReadInput$str(), new Object[0]), (Location)location, cause);
/* 169 */     StackTraceElement[] st = result.getStackTrace();
/* 170 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 171 */     return result;
/*     */   }
/*     */   
/*     */   protected String failedToCloseInput$str() {
/* 175 */     return "CONF0013: Failed to close input source";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException failedToCloseInput(XMLLocation location, IOException cause) {
/* 179 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), failedToCloseInput$str(), new Object[0]), (Location)location, cause);
/* 180 */     StackTraceElement[] st = result.getStackTrace();
/* 181 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 182 */     return result;
/*     */   }
/*     */   
/*     */   protected String invalidUrl$str() {
/* 186 */     return "CONF0014: Invalid configuration file URL";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException invalidUrl(XMLLocation location, MalformedURLException e) {
/* 190 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), invalidUrl$str(), new Object[0]), (Location)location, e);
/* 191 */     StackTraceElement[] st = result.getStackTrace();
/* 192 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 193 */     return result;
/*     */   }
/*     */   
/*     */   protected String unexpectedAttribute$str() {
/* 197 */     return "CONF0015: Unexpected attribute \"%s\" encountered";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException unexpectedAttribute(QName name, XMLLocation location) {
/* 201 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), unexpectedAttribute$str(), new Object[] { name }), (Location)location);
/* 202 */     StackTraceElement[] st = result.getStackTrace();
/* 203 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 204 */     return result;
/*     */   }
/*     */   
/*     */   protected String missingRequiredElement$str() {
/* 208 */     return "CONF0016: Missing required element \"%2$s\" from namespace \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException missingRequiredElement(String namespaceUri, String localName, XMLLocation location) {
/* 212 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), missingRequiredElement$str(), new Object[] { namespaceUri, localName }), (Location)location);
/* 213 */     StackTraceElement[] st = result.getStackTrace();
/* 214 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 215 */     return result;
/*     */   }
/*     */   
/*     */   protected String missingRequiredAttribute$str() {
/* 219 */     return "CONF0017: Missing required attribute \"%2$s\" from namespace \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException missingRequiredAttribute(String namespaceUri, String localName, XMLLocation location) {
/* 223 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), missingRequiredAttribute$str(), new Object[] { namespaceUri, localName }), (Location)location);
/* 224 */     StackTraceElement[] st = result.getStackTrace();
/* 225 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 226 */     return result;
/*     */   }
/*     */   
/*     */   protected String intParseException$str() {
/* 230 */     return "CONF0018: Failed to parse integer value of attribute \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException intParseException(NumberFormatException e, QName attributeName, XMLLocation location) {
/* 234 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), intParseException$str(), new Object[] { attributeName }), (Location)location, e);
/* 235 */     StackTraceElement[] st = result.getStackTrace();
/* 236 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 237 */     return result;
/*     */   }
/*     */   
/*     */   protected String uriParseException$str() {
/* 241 */     return "CONF0019: Failed to parse URI value of attribute \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException uriParseException(URISyntaxException e, QName attributeName, XMLLocation location) {
/* 245 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), uriParseException$str(), new Object[] { attributeName }), (Location)location, e);
/* 246 */     StackTraceElement[] st = result.getStackTrace();
/* 247 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 248 */     return result;
/*     */   }
/*     */   
/*     */   protected String expressionParseException$str() {
/* 252 */     return "CONF0020: Failed to parse expression value of attribute \"%s\"";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expressionParseException(IllegalArgumentException ex, QName attributeName, XMLLocation location) {
/* 256 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expressionParseException$str(), new Object[] { attributeName }), (Location)location, ex);
/* 257 */     StackTraceElement[] st = result.getStackTrace();
/* 258 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 259 */     return result;
/*     */   }
/*     */   
/*     */   protected String expressionTextParseException$str() {
/* 263 */     return "CONF0021: Failed to parse expression text";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException expressionTextParseException(IllegalArgumentException ex, XMLLocation location) {
/* 267 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), expressionTextParseException$str(), new Object[0]), (Location)location, ex);
/* 268 */     StackTraceElement[] st = result.getStackTrace();
/* 269 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 270 */     return result;
/*     */   }
/*     */   
/*     */   protected String numericAttributeValueOutOfRange$str() {
/* 274 */     return "CONF0022: Numeric value of attribute \"%s\" is out of range; actual value is \"%s\" but the value must not be less than %d or more than %d";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException numericAttributeValueOutOfRange(QName attributeName, String actualValue, long minValue, long maxValue, XMLLocation location) {
/* 278 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), numericAttributeValueOutOfRange$str(), new Object[] { attributeName, actualValue, Long.valueOf(minValue), Long.valueOf(maxValue) }), (Location)location);
/* 279 */     StackTraceElement[] st = result.getStackTrace();
/* 280 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 281 */     return result;
/*     */   }
/*     */   
/*     */   protected String inetAddressParseException$str() {
/* 285 */     return "CONF0023: Failed to parse IP address value of attribute \"%s\": \"%s\" is not a valid IP address";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException inetAddressParseException(QName attributeName, String address, XMLLocation location) {
/* 289 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), inetAddressParseException$str(), new Object[] { attributeName, address }), (Location)location);
/* 290 */     StackTraceElement[] st = result.getStackTrace();
/* 291 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 292 */     return result;
/*     */   }
/*     */   
/*     */   protected String cidrAddressParseException$str() {
/* 296 */     return "CONF0024: Failed to parse CIDR address value of attribute \"%s\": \"%s\" is not a valid CIDR address";
/*     */   }
/*     */   
/*     */   public final ConfigXMLParseException cidrAddressParseException(QName attributeName, String address, XMLLocation location) {
/* 300 */     ConfigXMLParseException result = new ConfigXMLParseException(String.format(getLoggingLocale(), cidrAddressParseException$str(), new Object[] { attributeName, address }), (Location)location);
/* 301 */     StackTraceElement[] st = result.getStackTrace();
/* 302 */     result.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/* 303 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\_private\ConfigMessages_$bundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */