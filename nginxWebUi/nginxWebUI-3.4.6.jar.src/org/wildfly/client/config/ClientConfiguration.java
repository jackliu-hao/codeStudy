/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.file.Paths;
/*     */ import java.security.AccessController;
/*     */ import java.util.Set;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import org.wildfly.client.config._private.ConfigMessages;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.function.ExceptionSupplier;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClientConfiguration
/*     */ {
/*     */   private static final String WILDFLY_CLIENT_1_0 = "urn:wildfly:client:1.0";
/*     */   private final XMLInputFactory xmlInputFactory;
/*     */   private final URI configurationUri;
/*     */   private final ExceptionSupplier<InputStream, IOException> streamSupplier;
/*     */   
/*     */   ClientConfiguration(XMLInputFactory xmlInputFactory, URI configurationUri, ExceptionSupplier<InputStream, IOException> streamSupplier) {
/*  59 */     this.xmlInputFactory = xmlInputFactory;
/*  60 */     this.configurationUri = configurationUri;
/*  61 */     this.streamSupplier = streamSupplier;
/*     */   }
/*     */   
/*     */   ClientConfiguration(XMLInputFactory xmlInputFactory, URI configurationUri) {
/*  65 */     this.xmlInputFactory = xmlInputFactory;
/*  66 */     this.configurationUri = configurationUri;
/*  67 */     this.streamSupplier = this::streamOpener;
/*     */   }
/*     */   
/*     */   private InputStream streamOpener() throws IOException {
/*  71 */     URL url = this.configurationUri.toURL();
/*  72 */     URLConnection connection = url.openConnection();
/*  73 */     connection.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");
/*  74 */     return connection.getInputStream();
/*     */   }
/*     */   
/*     */   XMLInputFactory getXmlInputFactory() {
/*  78 */     return this.xmlInputFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getConfigurationUri() {
/*  87 */     return this.configurationUri;
/*     */   }
/*     */   
/*     */   static ConfigurationXMLStreamReader openUri(URI uri, XMLInputFactory xmlInputFactory) throws ConfigXMLParseException {
/*     */     try {
/*  92 */       URL url = uri.toURL();
/*  93 */       URLConnection connection = url.openConnection();
/*  94 */       connection.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");
/*  95 */       InputStream inputStream = connection.getInputStream();
/*     */       try {
/*  97 */         return openUri(uri, xmlInputFactory, inputStream);
/*  98 */       } catch (Throwable t) {
/*     */         try {
/* 100 */           inputStream.close();
/* 101 */         } catch (Throwable t2) {
/* 102 */           t.addSuppressed(t2);
/*     */         } 
/* 104 */         throw t;
/*     */       } 
/* 106 */     } catch (MalformedURLException e) {
/* 107 */       throw ConfigMessages.msg.invalidUrl(new XMLLocation(uri), e);
/* 108 */     } catch (IOException e) {
/* 109 */       throw ConfigMessages.msg.failedToReadInput(new XMLLocation(uri), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   static ConfigurationXMLStreamReader openUri(URI uri, XMLInputFactory xmlInputFactory, InputStream inputStream) throws ConfigXMLParseException {
/*     */     try {
/* 115 */       return new BasicXMLStreamReader(null, xmlInputFactory.createXMLStreamReader(inputStream), uri, xmlInputFactory, inputStream);
/* 116 */     } catch (XMLStreamException e) {
/* 117 */       throw ConfigXMLParseException.from(e, uri, null);
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
/*     */   public ConfigurationXMLStreamReader readConfiguration(Set<String> recognizedNamespaces) throws ConfigXMLParseException {
/*     */     InputStream inputStream;
/* 131 */     URI uri = this.configurationUri;
/*     */     
/*     */     try {
/* 134 */       inputStream = (InputStream)this.streamSupplier.get();
/* 135 */     } catch (MalformedURLException e) {
/* 136 */       throw ConfigMessages.msg.invalidUrl(new XMLLocation(uri), e);
/* 137 */     } catch (IOException e) {
/* 138 */       throw ConfigMessages.msg.failedToReadInput(new XMLLocation(uri), e);
/*     */     } 
/* 140 */     ConfigurationXMLStreamReader reader = new XIncludeXMLStreamReader(openUri(uri, this.xmlInputFactory, inputStream));
/*     */     try {
/* 142 */       if (reader.hasNext()) {
/* 143 */         String namespaceURI; String localName; switch (reader.nextTag()) {
/*     */           case 1:
/* 145 */             namespaceURI = reader.getNamespaceURI();
/* 146 */             localName = reader.getLocalName();
/* 147 */             if (reader.hasNamespace() && !reader.namespaceURIEquals("urn:wildfly:client:1.0")) {
/* 148 */               throw ConfigMessages.msg.unexpectedElement(localName, namespaceURI, reader.getLocation());
/*     */             }
/* 150 */             if (reader.getAttributeCount() > 0) {
/* 151 */               throw ConfigMessages.msg.unexpectedAttribute(reader.getAttributeName(0), reader.getLocation());
/*     */             }
/* 153 */             if (!"configuration".equals(localName)) {
/* 154 */               if (namespaceURI == null) {
/* 155 */                 throw ConfigMessages.msg.unexpectedElement(localName, reader.getLocation());
/*     */               }
/* 157 */               throw ConfigMessages.msg.unexpectedElement(localName, namespaceURI, reader.getLocation());
/*     */             } 
/*     */             
/* 160 */             return new SelectingXMLStreamReader(true, reader, recognizedNamespaces);
/*     */         } 
/*     */         
/* 163 */         throw ConfigMessages.msg.unexpectedContent(ConfigurationXMLStreamReader.eventToString(reader.getEventType()), reader.getLocation());
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 168 */       reader.close();
/* 169 */       return null;
/* 170 */     } catch (Throwable t) {
/*     */       try {
/* 172 */         reader.close();
/* 173 */       } catch (Throwable t2) {
/* 174 */         t.addSuppressed(t2);
/*     */       } 
/* 176 */       throw t;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClientConfiguration getInstance(URI configurationUri) {
/* 187 */     Assert.checkNotNullParam("configurationUri", configurationUri);
/*     */     
/* 189 */     return new ClientConfiguration(createXmlInputFactory(), configurationUri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClientConfiguration getInstance(URI configurationUri, ExceptionSupplier<InputStream, IOException> streamSupplier) {
/* 199 */     Assert.checkNotNullParam("configurationUri", configurationUri);
/*     */     
/* 201 */     return new ClientConfiguration(createXmlInputFactory(), configurationUri, streamSupplier);
/*     */   }
/*     */   
/*     */   private static XMLInputFactory createXmlInputFactory() {
/* 205 */     XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
/* 206 */     xmlInputFactory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
/* 207 */     xmlInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
/* 208 */     xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
/* 209 */     return xmlInputFactory;
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
/*     */   public static ClientConfiguration getInstance() {
/*     */     ClassLoader classLoader;
/* 225 */     String wildFlyConfig = System.getProperty("wildfly.config.url");
/* 226 */     if (wildFlyConfig != null) {
/* 227 */       return getInstance(propertyUrlToUri(wildFlyConfig));
/*     */     }
/*     */ 
/*     */     
/* 231 */     SecurityManager sm = System.getSecurityManager();
/* 232 */     if (sm != null) {
/* 233 */       classLoader = AccessController.<ClassLoader>doPrivileged(ClientConfiguration::getContextClassLoader);
/*     */     } else {
/* 235 */       classLoader = getContextClassLoader();
/*     */     } 
/* 237 */     if (classLoader == null)
/*     */     {
/* 239 */       classLoader = ClientConfiguration.class.getClassLoader();
/*     */     }
/* 241 */     return getInstance(classLoader);
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
/*     */   public static ClientConfiguration getInstance(ClassLoader classLoader) {
/* 253 */     URL resource = classLoader.getResource("wildfly-config.xml");
/* 254 */     if (resource == null) {
/* 255 */       resource = classLoader.getResource("META-INF/wildfly-config.xml");
/* 256 */       if (resource == null) {
/* 257 */         return null;
/*     */       }
/*     */     } 
/*     */     
/*     */     try {
/* 262 */       return new ClientConfiguration(XMLInputFactory.newFactory(), resource.toURI(), resource::openStream);
/* 263 */     } catch (URISyntaxException e) {
/* 264 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static URI propertyUrlToUri(String wildFlyConfig) {
/* 269 */     if (File.separator.equals("\\") && wildFlyConfig.contains("\\")) {
/* 270 */       File f = new File(wildFlyConfig);
/* 271 */       return f.toPath().toUri();
/*     */     } 
/*     */     try {
/* 274 */       URI uri = new URI(wildFlyConfig);
/* 275 */       if (!uri.isAbsolute()) {
/* 276 */         if (uri.getPath().charAt(0) != File.separatorChar && uri.getPath().charAt(0) != '/') {
/* 277 */           String userDir = System.getProperty("user.dir").replace(File.separatorChar, '/');
/* 278 */           return Paths.get(userDir, new String[] { uri.getPath() }).toUri();
/*     */         } 
/* 280 */         return Paths.get(uri.getPath(), new String[0]).toUri();
/*     */       } 
/*     */       
/* 283 */       return uri;
/* 284 */     } catch (URISyntaxException e) {
/*     */       
/* 286 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static ClassLoader getContextClassLoader() {
/* 292 */     return Thread.currentThread().getContextClassLoader();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\ClientConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */