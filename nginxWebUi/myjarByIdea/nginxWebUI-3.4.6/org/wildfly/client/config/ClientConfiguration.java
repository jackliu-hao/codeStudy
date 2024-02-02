package org.wildfly.client.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.wildfly.client.config._private.ConfigMessages;
import org.wildfly.common.Assert;
import org.wildfly.common.function.ExceptionSupplier;

public class ClientConfiguration {
   private static final String WILDFLY_CLIENT_1_0 = "urn:wildfly:client:1.0";
   private final XMLInputFactory xmlInputFactory;
   private final URI configurationUri;
   private final ExceptionSupplier<InputStream, IOException> streamSupplier;

   ClientConfiguration(XMLInputFactory xmlInputFactory, URI configurationUri, ExceptionSupplier<InputStream, IOException> streamSupplier) {
      this.xmlInputFactory = xmlInputFactory;
      this.configurationUri = configurationUri;
      this.streamSupplier = streamSupplier;
   }

   ClientConfiguration(XMLInputFactory xmlInputFactory, URI configurationUri) {
      this.xmlInputFactory = xmlInputFactory;
      this.configurationUri = configurationUri;
      this.streamSupplier = this::streamOpener;
   }

   private InputStream streamOpener() throws IOException {
      URL url = this.configurationUri.toURL();
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");
      return connection.getInputStream();
   }

   XMLInputFactory getXmlInputFactory() {
      return this.xmlInputFactory;
   }

   public URI getConfigurationUri() {
      return this.configurationUri;
   }

   static ConfigurationXMLStreamReader openUri(URI uri, XMLInputFactory xmlInputFactory) throws ConfigXMLParseException {
      try {
         URL url = uri.toURL();
         URLConnection connection = url.openConnection();
         connection.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");
         InputStream inputStream = connection.getInputStream();

         try {
            return openUri(uri, xmlInputFactory, inputStream);
         } catch (Throwable var8) {
            try {
               inputStream.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }
      } catch (MalformedURLException var9) {
         throw ConfigMessages.msg.invalidUrl(new XMLLocation(uri), var9);
      } catch (IOException var10) {
         throw ConfigMessages.msg.failedToReadInput(new XMLLocation(uri), var10);
      }
   }

   static ConfigurationXMLStreamReader openUri(URI uri, XMLInputFactory xmlInputFactory, InputStream inputStream) throws ConfigXMLParseException {
      try {
         return new BasicXMLStreamReader((XMLLocation)null, xmlInputFactory.createXMLStreamReader(inputStream), uri, xmlInputFactory, inputStream);
      } catch (XMLStreamException var4) {
         throw ConfigXMLParseException.from((XMLStreamException)var4, uri, (XMLLocation)null);
      }
   }

   public ConfigurationXMLStreamReader readConfiguration(Set<String> recognizedNamespaces) throws ConfigXMLParseException {
      URI uri = this.configurationUri;

      InputStream inputStream;
      try {
         inputStream = (InputStream)this.streamSupplier.get();
      } catch (MalformedURLException var9) {
         throw ConfigMessages.msg.invalidUrl(new XMLLocation(uri), var9);
      } catch (IOException var10) {
         throw ConfigMessages.msg.failedToReadInput(new XMLLocation(uri), var10);
      }

      ConfigurationXMLStreamReader reader = new XIncludeXMLStreamReader(openUri(uri, this.xmlInputFactory, inputStream));

      try {
         if (reader.hasNext()) {
            switch (reader.nextTag()) {
               case 1:
                  String namespaceURI = reader.getNamespaceURI();
                  String localName = reader.getLocalName();
                  if (reader.hasNamespace() && !reader.namespaceURIEquals("urn:wildfly:client:1.0")) {
                     throw ConfigMessages.msg.unexpectedElement(localName, namespaceURI, reader.getLocation());
                  } else if (reader.getAttributeCount() > 0) {
                     throw ConfigMessages.msg.unexpectedAttribute(reader.getAttributeName(0), reader.getLocation());
                  } else {
                     if (!"configuration".equals(localName)) {
                        if (namespaceURI == null) {
                           throw ConfigMessages.msg.unexpectedElement(localName, reader.getLocation());
                        }

                        throw ConfigMessages.msg.unexpectedElement(localName, namespaceURI, reader.getLocation());
                     }

                     return new SelectingXMLStreamReader(true, reader, recognizedNamespaces);
                  }
               default:
                  throw ConfigMessages.msg.unexpectedContent(ConfigurationXMLStreamReader.eventToString(reader.getEventType()), reader.getLocation());
            }
         } else {
            reader.close();
            return null;
         }
      } catch (Throwable var8) {
         try {
            reader.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }
   }

   public static ClientConfiguration getInstance(URI configurationUri) {
      Assert.checkNotNullParam("configurationUri", configurationUri);
      return new ClientConfiguration(createXmlInputFactory(), configurationUri);
   }

   public static ClientConfiguration getInstance(URI configurationUri, ExceptionSupplier<InputStream, IOException> streamSupplier) {
      Assert.checkNotNullParam("configurationUri", configurationUri);
      return new ClientConfiguration(createXmlInputFactory(), configurationUri, streamSupplier);
   }

   private static XMLInputFactory createXmlInputFactory() {
      XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
      xmlInputFactory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
      xmlInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
      xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
      return xmlInputFactory;
   }

   public static ClientConfiguration getInstance() {
      String wildFlyConfig = System.getProperty("wildfly.config.url");
      if (wildFlyConfig != null) {
         return getInstance(propertyUrlToUri(wildFlyConfig));
      } else {
         SecurityManager sm = System.getSecurityManager();
         ClassLoader classLoader;
         if (sm != null) {
            classLoader = (ClassLoader)AccessController.doPrivileged(ClientConfiguration::getContextClassLoader);
         } else {
            classLoader = getContextClassLoader();
         }

         if (classLoader == null) {
            classLoader = ClientConfiguration.class.getClassLoader();
         }

         return getInstance(classLoader);
      }
   }

   public static ClientConfiguration getInstance(ClassLoader classLoader) {
      URL resource = classLoader.getResource("wildfly-config.xml");
      if (resource == null) {
         resource = classLoader.getResource("META-INF/wildfly-config.xml");
         if (resource == null) {
            return null;
         }
      }

      try {
         XMLInputFactory var10002 = XMLInputFactory.newFactory();
         URI var10003 = resource.toURI();
         resource.getClass();
         return new ClientConfiguration(var10002, var10003, resource::openStream);
      } catch (URISyntaxException var3) {
         return null;
      }
   }

   static URI propertyUrlToUri(String wildFlyConfig) {
      if (File.separator.equals("\\") && wildFlyConfig.contains("\\")) {
         File f = new File(wildFlyConfig);
         return f.toPath().toUri();
      } else {
         try {
            URI uri = new URI(wildFlyConfig);
            if (!uri.isAbsolute()) {
               if (uri.getPath().charAt(0) != File.separatorChar && uri.getPath().charAt(0) != '/') {
                  String userDir = System.getProperty("user.dir").replace(File.separatorChar, '/');
                  return Paths.get(userDir, uri.getPath()).toUri();
               } else {
                  return Paths.get(uri.getPath()).toUri();
               }
            } else {
               return uri;
            }
         } catch (URISyntaxException var3) {
            return null;
         }
      }
   }

   private static ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
   }
}
