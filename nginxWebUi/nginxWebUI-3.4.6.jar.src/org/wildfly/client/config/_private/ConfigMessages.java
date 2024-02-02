/*    */ package org.wildfly.client.config._private;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URISyntaxException;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.stream.Location;
/*    */ import org.jboss.logging.Messages;
/*    */ import org.jboss.logging.annotations.Cause;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageBundle;
/*    */ import org.jboss.logging.annotations.Param;
/*    */ import org.wildfly.client.config.ConfigXMLParseException;
/*    */ import org.wildfly.client.config.XMLLocation;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @MessageBundle(projectCode = "CONF", length = 4)
/*    */ public interface ConfigMessages
/*    */ {
/* 42 */   public static final ConfigMessages msg = (ConfigMessages)Messages.getBundle(ConfigMessages.class);
/*    */   
/*    */   @Message(id = 1, value = "An unspecified XML parse error occurred")
/*    */   String parseError();
/*    */   
/*    */   @Message(id = 2, value = "Calling close() on XMLConfigurationReader is not supported")
/*    */   UnsupportedOperationException closeNotSupported();
/*    */   
/*    */   @Message(id = 3, value = "Unexpected end of document")
/*    */   ConfigXMLParseException unexpectedDocumentEnd(@Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 4, value = "Unexpected content of type \"%s\"")
/*    */   ConfigXMLParseException unexpectedContent(String paramString, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 5, value = "Unexpected element \"%s\" in namespace \"%s\" encountered")
/*    */   ConfigXMLParseException unexpectedElement(String paramString1, String paramString2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 5, value = "Unexpected element \"%s\" (no namespace) encountered")
/*    */   ConfigXMLParseException unexpectedElement(String paramString, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 6, value = "Expected start or end element, found \"%s\"")
/*    */   ConfigXMLParseException expectedStartOrEndElement(String paramString, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 7, value = "Expected start element, found \"%s\"")
/*    */   ConfigXMLParseException expectedStartElement(String paramString, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 8, value = "Text content cannot contain elements")
/*    */   ConfigXMLParseException textCannotContainElements(@Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 9, value = "Expected event type \"%s\", found \"%s\"")
/*    */   ConfigXMLParseException expectedEventType(String paramString1, String paramString2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 10, value = "Expected namespace URI \"%s\", found \"%s\"")
/*    */   ConfigXMLParseException expectedNamespace(String paramString1, String paramString2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 11, value = "Expected local name \"%s\", found \"%s\"")
/*    */   ConfigXMLParseException expectedLocalName(String paramString1, String paramString2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 12, value = "Failed to read from input source")
/*    */   ConfigXMLParseException failedToReadInput(@Param(Location.class) XMLLocation paramXMLLocation, @Cause IOException paramIOException);
/*    */   
/*    */   @Message(id = 13, value = "Failed to close input source")
/*    */   ConfigXMLParseException failedToCloseInput(@Param(Location.class) XMLLocation paramXMLLocation, @Cause IOException paramIOException);
/*    */   
/*    */   @Message(id = 14, value = "Invalid configuration file URL")
/*    */   ConfigXMLParseException invalidUrl(@Param(Location.class) XMLLocation paramXMLLocation, @Cause MalformedURLException paramMalformedURLException);
/*    */   
/*    */   @Message(id = 15, value = "Unexpected attribute \"%s\" encountered")
/*    */   ConfigXMLParseException unexpectedAttribute(QName paramQName, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 16, value = "Missing required element \"%2$s\" from namespace \"%s\"")
/*    */   ConfigXMLParseException missingRequiredElement(String paramString1, String paramString2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 17, value = "Missing required attribute \"%2$s\" from namespace \"%s\"")
/*    */   ConfigXMLParseException missingRequiredAttribute(String paramString1, String paramString2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 18, value = "Failed to parse integer value of attribute \"%s\"")
/*    */   ConfigXMLParseException intParseException(@Cause NumberFormatException paramNumberFormatException, QName paramQName, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 19, value = "Failed to parse URI value of attribute \"%s\"")
/*    */   ConfigXMLParseException uriParseException(@Cause URISyntaxException paramURISyntaxException, QName paramQName, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 20, value = "Failed to parse expression value of attribute \"%s\"")
/*    */   ConfigXMLParseException expressionParseException(@Cause IllegalArgumentException paramIllegalArgumentException, QName paramQName, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 21, value = "Failed to parse expression text")
/*    */   ConfigXMLParseException expressionTextParseException(@Cause IllegalArgumentException paramIllegalArgumentException, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 22, value = "Numeric value of attribute \"%s\" is out of range; actual value is \"%s\" but the value must not be less than %d or more than %d")
/*    */   ConfigXMLParseException numericAttributeValueOutOfRange(QName paramQName, String paramString, long paramLong1, long paramLong2, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 23, value = "Failed to parse IP address value of attribute \"%s\": \"%s\" is not a valid IP address")
/*    */   ConfigXMLParseException inetAddressParseException(QName paramQName, String paramString, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */   
/*    */   @Message(id = 24, value = "Failed to parse CIDR address value of attribute \"%s\": \"%s\" is not a valid CIDR address")
/*    */   ConfigXMLParseException cidrAddressParseException(QName paramQName, String paramString, @Param(Location.class) XMLLocation paramXMLLocation);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\_private\ConfigMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */