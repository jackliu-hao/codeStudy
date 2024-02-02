package org.wildfly.client.config._private;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Param;
import org.wildfly.client.config.ConfigXMLParseException;
import org.wildfly.client.config.XMLLocation;

@MessageBundle(
   projectCode = "CONF",
   length = 4
)
public interface ConfigMessages {
   ConfigMessages msg = (ConfigMessages)Messages.getBundle(ConfigMessages.class);

   @Message(
      id = 1,
      value = "An unspecified XML parse error occurred"
   )
   String parseError();

   @Message(
      id = 2,
      value = "Calling close() on XMLConfigurationReader is not supported"
   )
   UnsupportedOperationException closeNotSupported();

   @Message(
      id = 3,
      value = "Unexpected end of document"
   )
   ConfigXMLParseException unexpectedDocumentEnd(@Param(Location.class) XMLLocation var1);

   @Message(
      id = 4,
      value = "Unexpected content of type \"%s\""
   )
   ConfigXMLParseException unexpectedContent(String var1, @Param(Location.class) XMLLocation var2);

   @Message(
      id = 5,
      value = "Unexpected element \"%s\" in namespace \"%s\" encountered"
   )
   ConfigXMLParseException unexpectedElement(String var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 5,
      value = "Unexpected element \"%s\" (no namespace) encountered"
   )
   ConfigXMLParseException unexpectedElement(String var1, @Param(Location.class) XMLLocation var2);

   @Message(
      id = 6,
      value = "Expected start or end element, found \"%s\""
   )
   ConfigXMLParseException expectedStartOrEndElement(String var1, @Param(Location.class) XMLLocation var2);

   @Message(
      id = 7,
      value = "Expected start element, found \"%s\""
   )
   ConfigXMLParseException expectedStartElement(String var1, @Param(Location.class) XMLLocation var2);

   @Message(
      id = 8,
      value = "Text content cannot contain elements"
   )
   ConfigXMLParseException textCannotContainElements(@Param(Location.class) XMLLocation var1);

   @Message(
      id = 9,
      value = "Expected event type \"%s\", found \"%s\""
   )
   ConfigXMLParseException expectedEventType(String var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 10,
      value = "Expected namespace URI \"%s\", found \"%s\""
   )
   ConfigXMLParseException expectedNamespace(String var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 11,
      value = "Expected local name \"%s\", found \"%s\""
   )
   ConfigXMLParseException expectedLocalName(String var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 12,
      value = "Failed to read from input source"
   )
   ConfigXMLParseException failedToReadInput(@Param(Location.class) XMLLocation var1, @Cause IOException var2);

   @Message(
      id = 13,
      value = "Failed to close input source"
   )
   ConfigXMLParseException failedToCloseInput(@Param(Location.class) XMLLocation var1, @Cause IOException var2);

   @Message(
      id = 14,
      value = "Invalid configuration file URL"
   )
   ConfigXMLParseException invalidUrl(@Param(Location.class) XMLLocation var1, @Cause MalformedURLException var2);

   @Message(
      id = 15,
      value = "Unexpected attribute \"%s\" encountered"
   )
   ConfigXMLParseException unexpectedAttribute(QName var1, @Param(Location.class) XMLLocation var2);

   @Message(
      id = 16,
      value = "Missing required element \"%2$s\" from namespace \"%s\""
   )
   ConfigXMLParseException missingRequiredElement(String var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 17,
      value = "Missing required attribute \"%2$s\" from namespace \"%s\""
   )
   ConfigXMLParseException missingRequiredAttribute(String var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 18,
      value = "Failed to parse integer value of attribute \"%s\""
   )
   ConfigXMLParseException intParseException(@Cause NumberFormatException var1, QName var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 19,
      value = "Failed to parse URI value of attribute \"%s\""
   )
   ConfigXMLParseException uriParseException(@Cause URISyntaxException var1, QName var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 20,
      value = "Failed to parse expression value of attribute \"%s\""
   )
   ConfigXMLParseException expressionParseException(@Cause IllegalArgumentException var1, QName var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 21,
      value = "Failed to parse expression text"
   )
   ConfigXMLParseException expressionTextParseException(@Cause IllegalArgumentException var1, @Param(Location.class) XMLLocation var2);

   @Message(
      id = 22,
      value = "Numeric value of attribute \"%s\" is out of range; actual value is \"%s\" but the value must not be less than %d or more than %d"
   )
   ConfigXMLParseException numericAttributeValueOutOfRange(QName var1, String var2, long var3, long var5, @Param(Location.class) XMLLocation var7);

   @Message(
      id = 23,
      value = "Failed to parse IP address value of attribute \"%s\": \"%s\" is not a valid IP address"
   )
   ConfigXMLParseException inetAddressParseException(QName var1, String var2, @Param(Location.class) XMLLocation var3);

   @Message(
      id = 24,
      value = "Failed to parse CIDR address value of attribute \"%s\": \"%s\" is not a valid CIDR address"
   )
   ConfigXMLParseException cidrAddressParseException(QName var1, String var2, @Param(Location.class) XMLLocation var3);
}
