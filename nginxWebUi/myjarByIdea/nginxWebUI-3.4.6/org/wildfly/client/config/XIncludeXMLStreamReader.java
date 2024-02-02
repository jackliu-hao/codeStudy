package org.wildfly.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import org.wildfly.client.config._private.ConfigMessages;

final class XIncludeXMLStreamReader extends AbstractDelegatingXMLStreamReader {
   static final String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";
   private ConfigurationXMLStreamReader child;

   XIncludeXMLStreamReader(ConfigurationXMLStreamReader delegate) {
      super(true, delegate);
   }

   private ConfigurationXMLStreamReader getRawDelegate() {
      return super.getDelegate();
   }

   protected ConfigurationXMLStreamReader getDelegate() {
      ConfigurationXMLStreamReader child = this.child;
      return child != null ? child : this.getRawDelegate();
   }

   public void skipContent() throws ConfigXMLParseException {
      while(this.getDelegate().hasNext()) {
         switch (this.getDelegate().next()) {
            case 1:
               this.skipContent();
               break;
            case 2:
               return;
         }
      }

   }

   public int next() throws ConfigXMLParseException {
      ConfigurationXMLStreamReader child = this.child;
      if (child != null) {
         if (child.hasNext()) {
            int next = child.next();
            if (next != 8) {
               return next;
            }

            child.close();
         }

         this.child = null;
      }

      ConfigurationXMLStreamReader delegate = this.getDelegate();
      if (!delegate.hasNext()) {
         throw new NoSuchElementException();
      } else {
         while(true) {
            int res = delegate.next();
            if (res != 1) {
               return res;
            }

            String namespaceURI = delegate.getNamespaceURI();
            if (!"http://www.w3.org/2001/XInclude".equals(namespaceURI)) {
               return res;
            }

            switch (delegate.getLocalName()) {
               case "include":
                  ConfigurationXMLStreamReader nested = this.processInclude();
                  boolean ok = false;

                  int var10;
                  try {
                     if (nested == null || !nested.hasNext()) {
                        ok = true;
                        break;
                     }

                     int eventType = nested.next();
                     if (eventType == 7) {
                        if (!nested.hasNext()) {
                           break;
                        }

                        eventType = nested.next();
                     }

                     this.child = new XIncludeXMLStreamReader(nested);
                     ok = true;
                     var10 = eventType;
                  } finally {
                     if (!ok) {
                        try {
                           nested.close();
                        } catch (ConfigXMLParseException var19) {
                        }
                     }

                  }

                  return var10;
               default:
                  throw ConfigMessages.msg.unexpectedElement(delegate.getLocalName(), namespaceURI, this.getLocation());
            }
         }
      }
   }

   private ConfigurationXMLStreamReader processInclude() throws ConfigXMLParseException {
      ScopedXMLStreamReader includeElement = new ScopedXMLStreamReader(false, this.getRawDelegate());
      ConfigurationXMLStreamReader delegate = this.getDelegate();
      int attributeCount = delegate.getAttributeCount();
      URI href = null;
      Charset textCharset = StandardCharsets.UTF_8;
      boolean fallback = false;
      String accept = null;
      String acceptLanguage = null;
      boolean parseAsText = false;

      int level;
      for(level = 0; level < attributeCount; ++level) {
         if (delegate.getAttributeNamespace(level) == null) {
            switch (delegate.getAttributeLocalName(level)) {
               case "href":
                  try {
                     href = new URI(delegate.getAttributeValue(level));
                  } catch (URISyntaxException var23) {
                     throw new ConfigXMLParseException("Invalid include URI", this.getLocation(), var23);
                  }

                  if (href.getFragment() != null) {
                     throw new ConfigXMLParseException("Invalid include URI: must not contain fragment identifier", this.getLocation());
                  }

                  fallback |= href.isOpaque();
                  break;
               case "parse":
                  switch (delegate.getAttributeValue(level)) {
                     case "xml":
                        parseAsText = false;
                        continue;
                     case "text":
                        parseAsText = true;
                        continue;
                     default:
                        throw new ConfigXMLParseException("Invalid include directive: unknown parse type (must be \"text\" or \"xml\")", this.getLocation());
                  }
               case "xpointer":
                  fallback = true;
                  break;
               case "encoding":
                  try {
                     textCharset = Charset.forName(delegate.getAttributeValue(level));
                  } catch (UnsupportedCharsetException | IllegalCharsetNameException var22) {
                     fallback = true;
                  }
                  break;
               case "accept":
                  accept = delegate.getAttributeValue(level);
                  break;
               case "accept-language":
                  acceptLanguage = delegate.getAttributeValue(level);
            }
         }
      }

      if (!fallback) {
         if (href == null) {
            throw delegate.missingRequiredAttribute((String)null, "href");
         } else {
            Object child;
            try {
               if (!href.isAbsolute()) {
                  href = this.getRawDelegate().getUri().resolve(href);
               }

               URL url = href.toURL();
               URLConnection connection = url.openConnection();
               connection.addRequestProperty("Accept", accept != null ? accept : (parseAsText ? "text/plain,text/*" : "application/xml,text/xml,application/*+xml,text/*+xml"));
               if (acceptLanguage != null) {
                  connection.addRequestProperty("Accept-Language", acceptLanguage);
               }

               InputStream inputStream = connection.getInputStream();

               try {
                  if (parseAsText) {
                     child = new TextXMLStreamReader(textCharset, inputStream, this, href);
                  } else {
                     child = new XIncludeXMLStreamReader(new BasicXMLStreamReader(this.getLocation(), this.getXmlInputFactory().createXMLStreamReader(inputStream), href, this.getXmlInputFactory(), inputStream));
                  }
               } catch (XMLStreamException var20) {
                  try {
                     inputStream.close();
                  } catch (Throwable var18) {
                     var20.addSuppressed(var18);
                  }

                  throw ConfigXMLParseException.from(var20, this.getUri(), this.getIncludedFrom());
               } catch (Throwable var21) {
                  try {
                     inputStream.close();
                  } catch (Throwable var17) {
                     var21.addSuppressed(var17);
                  }

                  throw var21;
               }
            } catch (IOException var24) {
               throw ConfigXMLParseException.from((Exception)var24, this.getUri(), this.getIncludedFrom());
            }

            try {
               this.getRawDelegate().skipContent();
               return (ConfigurationXMLStreamReader)child;
            } catch (Throwable var19) {
               try {
                  ((ConfigurationXMLStreamReader)child).close();
               } catch (Throwable var16) {
                  var19.addSuppressed(var16);
               }

               throw var19;
            }
         }
      } else {
         label180:
         while(super.hasNext()) {
            switch (super.next()) {
               case 1:
                  if ("http://www.w3.org/2001/XInclude".equals(super.getNamespaceURI()) && "fallback".equals(super.getLocalName())) {
                     return this.child = new ScopedXMLStreamReader(true, new DrainingXMLStreamReader(false, includeElement));
                  }

                  level = 0;

                  while(true) {
                     if (!super.hasNext()) {
                        continue label180;
                     }

                     switch (super.next()) {
                        case 1:
                           ++level;
                           break;
                        case 2:
                           if (level-- == 0) {
                              continue label180;
                           }
                     }
                  }
               case 2:
                  return null;
            }
         }

         return null;
      }
   }
}
