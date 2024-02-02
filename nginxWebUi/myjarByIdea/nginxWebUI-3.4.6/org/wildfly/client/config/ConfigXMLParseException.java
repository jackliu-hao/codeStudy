package org.wildfly.client.config;

import java.net.URI;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.wildfly.client.config._private.ConfigMessages;

public class ConfigXMLParseException extends XMLStreamException {
   private static final long serialVersionUID = -1880381457871462141L;

   public ConfigXMLParseException() {
   }

   public ConfigXMLParseException(String msg) {
      super(msg);
   }

   public ConfigXMLParseException(Throwable cause) {
      super(cause);
   }

   public ConfigXMLParseException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public ConfigXMLParseException(Location location) {
      this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(location), 0);
   }

   public ConfigXMLParseException(String msg, Location location) {
      this(msg, XMLLocation.toXMLLocation(location), 0);
   }

   public ConfigXMLParseException(Throwable cause, Location location) {
      this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(location), cause, 0);
   }

   public ConfigXMLParseException(String msg, Location location, Throwable cause) {
      this(msg, XMLLocation.toXMLLocation(location), cause, 0);
   }

   public ConfigXMLParseException(XMLStreamReader reader) {
      this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(reader.getLocation()), 0);
   }

   public ConfigXMLParseException(String msg, XMLStreamReader reader) {
      this(msg, XMLLocation.toXMLLocation(reader.getLocation()), 0);
   }

   public ConfigXMLParseException(Throwable cause, XMLStreamReader reader) {
      this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(reader.getLocation()), cause, 0);
   }

   public ConfigXMLParseException(String msg, XMLStreamReader reader, Throwable cause) {
      this(msg, XMLLocation.toXMLLocation(reader.getLocation()), cause, 0);
   }

   public XMLLocation getLocation() {
      return XMLLocation.toXMLLocation(super.getLocation());
   }

   protected void setLocation(XMLLocation location) {
      this.location = location;
   }

   static ConfigXMLParseException from(XMLStreamException exception) {
      if (exception instanceof ConfigXMLParseException) {
         return (ConfigXMLParseException)exception;
      } else {
         StackTraceElement[] stackTrace = exception.getStackTrace();
         Throwable cause = exception.getCause();
         ConfigXMLParseException parseException;
         if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), exception.getLocation(), cause);
         } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), exception.getLocation());
         }

         parseException.setStackTrace(stackTrace);
         return parseException;
      }
   }

   static ConfigXMLParseException from(XMLStreamException exception, URI uri) {
      if (exception instanceof ConfigXMLParseException) {
         return (ConfigXMLParseException)exception;
      } else {
         StackTraceElement[] stackTrace = exception.getStackTrace();
         Throwable cause = exception.getCause();
         ConfigXMLParseException parseException;
         if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, exception.getLocation()), cause);
         } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, exception.getLocation()));
         }

         parseException.setStackTrace(stackTrace);
         return parseException;
      }
   }

   static ConfigXMLParseException from(XMLStreamException exception, URI uri, XMLLocation includedFrom) {
      if (exception instanceof ConfigXMLParseException) {
         return (ConfigXMLParseException)exception;
      } else {
         StackTraceElement[] stackTrace = exception.getStackTrace();
         Throwable cause = exception.getCause();
         ConfigXMLParseException parseException;
         if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, exception.getLocation()), cause);
         } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, exception.getLocation()));
         }

         parseException.setStackTrace(stackTrace);
         return parseException;
      }
   }

   static ConfigXMLParseException from(Exception exception) {
      if (exception instanceof XMLStreamException) {
         return from((XMLStreamException)exception);
      } else {
         StackTraceElement[] stackTrace = exception.getStackTrace();
         Throwable cause = exception.getCause();
         ConfigXMLParseException parseException;
         if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.UNKNOWN, cause);
         } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.UNKNOWN);
         }

         parseException.setStackTrace(stackTrace);
         return parseException;
      }
   }

   static ConfigXMLParseException from(Exception exception, URI uri) {
      if (exception instanceof XMLStreamException) {
         return from((XMLStreamException)exception, uri);
      } else {
         StackTraceElement[] stackTrace = exception.getStackTrace();
         Throwable cause = exception.getCause();
         ConfigXMLParseException parseException;
         if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation((URI)uri, new XMLLocation(uri)), cause);
         } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation((URI)uri, new XMLLocation(uri)));
         }

         parseException.setStackTrace(stackTrace);
         return parseException;
      }
   }

   static ConfigXMLParseException from(Exception exception, URI uri, XMLLocation includedFrom) {
      if (exception instanceof XMLStreamException) {
         return from((XMLStreamException)exception, uri, includedFrom);
      } else {
         StackTraceElement[] stackTrace = exception.getStackTrace();
         Throwable cause = exception.getCause();
         ConfigXMLParseException parseException;
         if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, new XMLLocation(uri)), cause);
         } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, new XMLLocation(uri)));
         }

         parseException.setStackTrace(stackTrace);
         return parseException;
      }
   }

   private static String clean(String original) {
      if (original.startsWith("ParseError at [row,col]:[")) {
         int idx = original.indexOf("Message: ");
         return idx == -1 ? original : original.substring(idx + 9);
      } else {
         return original;
      }
   }

   private ConfigXMLParseException(String msg, XMLLocation location, int ignored) {
      super(msg + location);
      this.location = location;
   }

   private ConfigXMLParseException(String msg, XMLLocation location, Throwable cause, int ignored) {
      super(msg + location, cause);
      this.location = location;
   }
}
