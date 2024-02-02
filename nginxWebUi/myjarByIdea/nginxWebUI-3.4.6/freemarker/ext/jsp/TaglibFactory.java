package freemarker.ext.jsp;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.log.Logger;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TaglibFactory implements TemplateHashModel {
   public static final List DEFAULT_CLASSPATH_TLDS;
   public static final List DEFAULT_META_INF_TLD_SOURCES;
   private static final Logger LOG;
   private static final int URL_TYPE_FULL = 0;
   private static final int URL_TYPE_ABSOLUTE = 1;
   private static final int URL_TYPE_RELATIVE = 2;
   private static final String META_INF_REL_PATH = "META-INF/";
   private static final String META_INF_ABS_PATH = "/META-INF/";
   private static final String DEFAULT_TLD_RESOURCE_PATH = "/META-INF/taglib.tld";
   private static final String JAR_URL_ENTRY_PATH_START = "!/";
   private static final String PLATFORM_FILE_ENCODING;
   private final ServletContext servletContext;
   private ObjectWrapper objectWrapper;
   private List metaInfTldSources;
   private List classpathTlds;
   boolean test_emulateNoUrlToFileConversions;
   boolean test_emulateNoJarURLConnections;
   boolean test_emulateJarEntryUrlOpenStreamFails;
   private final Object lock;
   private final Map taglibs;
   private final Map tldLocations;
   private List failedTldLocations;
   private int nextTldLocationLookupPhase;

   public TaglibFactory(ServletContext ctx) {
      this.metaInfTldSources = DEFAULT_META_INF_TLD_SOURCES;
      this.classpathTlds = DEFAULT_CLASSPATH_TLDS;
      this.test_emulateNoUrlToFileConversions = false;
      this.test_emulateNoJarURLConnections = false;
      this.test_emulateJarEntryUrlOpenStreamFails = false;
      this.lock = new Object();
      this.taglibs = new HashMap();
      this.tldLocations = new HashMap();
      this.failedTldLocations = new ArrayList();
      this.nextTldLocationLookupPhase = 0;
      this.servletContext = ctx;
   }

   public TemplateModel get(String taglibUri) throws TemplateModelException {
      synchronized(this.lock) {
         Taglib taglib = (Taglib)this.taglibs.get(taglibUri);
         if (taglib != null) {
            return taglib;
         } else {
            boolean failedTldListAlreadyIncluded = false;

            Object tldLocation;
            String normalizedTaglibUri;
            try {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Locating TLD for taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + ".");
               }

               TldLocation explicitlyMappedTldLocation = this.getExplicitlyMappedTldLocation(taglibUri);
               if (explicitlyMappedTldLocation != null) {
                  tldLocation = explicitlyMappedTldLocation;
                  normalizedTaglibUri = taglibUri;
               } else {
                  int urlType;
                  try {
                     urlType = getUriType(taglibUri);
                  } catch (MalformedURLException var11) {
                     throw new TaglibGettingException("Malformed taglib URI: " + StringUtil.jQuote(taglibUri), var11);
                  }

                  if (urlType == 2) {
                     normalizedTaglibUri = resolveRelativeUri(taglibUri);
                  } else {
                     if (urlType != 1) {
                        if (urlType == 0) {
                           String failedTLDsList = this.getFailedTLDsList();
                           failedTldListAlreadyIncluded = true;
                           throw new TaglibGettingException("No TLD was found for the " + StringUtil.jQuoteNoXSS(taglibUri) + " JSP taglib URI. (TLD-s are searched according the JSP 2.2 specification. In development- and embedded-servlet-container setups you may also need the \"" + "MetaInfTldSources" + "\" and \"" + "ClasspathTlds" + "\" " + FreemarkerServlet.class.getName() + " init-params or the similar system properites." + (failedTLDsList == null ? "" : " Also note these TLD-s were skipped earlier due to errors; see error in the log: " + failedTLDsList) + ")");
                        }

                        throw new BugException();
                     }

                     normalizedTaglibUri = taglibUri;
                  }

                  if (!normalizedTaglibUri.equals(taglibUri)) {
                     Taglib taglib = (Taglib)this.taglibs.get(normalizedTaglibUri);
                     if (taglib != null) {
                        Taglib var17 = taglib;
                        return var17;
                     }
                  }

                  tldLocation = isJarPath(normalizedTaglibUri) ? new ServletContextJarEntryTldLocation(normalizedTaglibUri, "/META-INF/taglib.tld") : new ServletContextTldLocation(normalizedTaglibUri);
               }
            } catch (Exception var12) {
               String failedTLDsList = failedTldListAlreadyIncluded ? null : this.getFailedTLDsList();
               throw new TemplateModelException("Error while looking for TLD file for " + StringUtil.jQuoteNoXSS(taglibUri) + "; see cause exception." + (failedTLDsList == null ? "" : " (Note: These TLD-s were skipped earlier due to errors; see errors in the log: " + failedTLDsList + ")"), var12);
            }

            TemplateHashModel var10000;
            try {
               var10000 = this.loadTaglib((TldLocation)tldLocation, normalizedTaglibUri);
            } catch (Exception var10) {
               throw new TemplateModelException("Error while loading tag library for URI " + StringUtil.jQuoteNoXSS(normalizedTaglibUri) + " from TLD location " + StringUtil.jQuoteNoXSS(tldLocation) + "; see cause exception.", var10);
            }

            return var10000;
         }
      }
   }

   private String getFailedTLDsList() {
      synchronized(this.failedTldLocations) {
         if (this.failedTldLocations.isEmpty()) {
            return null;
         } else {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < this.failedTldLocations.size(); ++i) {
               if (i != 0) {
                  sb.append(", ");
               }

               sb.append(StringUtil.jQuote(this.failedTldLocations.get(i)));
            }

            return sb.toString();
         }
      }
   }

   public boolean isEmpty() {
      return false;
   }

   public ObjectWrapper getObjectWrapper() {
      return this.objectWrapper;
   }

   public void setObjectWrapper(ObjectWrapper objectWrapper) {
      this.checkNotStarted();
      this.objectWrapper = objectWrapper;
   }

   public List getMetaInfTldSources() {
      return this.metaInfTldSources;
   }

   public void setMetaInfTldSources(List metaInfTldSources) {
      this.checkNotStarted();
      NullArgumentException.check("metaInfTldSources", metaInfTldSources);
      this.metaInfTldSources = metaInfTldSources;
   }

   public List getClasspathTlds() {
      return this.classpathTlds;
   }

   public void setClasspathTlds(List classpathTlds) {
      this.checkNotStarted();
      NullArgumentException.check("classpathTlds", classpathTlds);
      this.classpathTlds = classpathTlds;
   }

   private void checkNotStarted() {
      synchronized(this.lock) {
         if (this.nextTldLocationLookupPhase != 0) {
            throw new IllegalStateException(TaglibFactory.class.getName() + " object was already in use.");
         }
      }
   }

   private TldLocation getExplicitlyMappedTldLocation(String uri) throws SAXException, IOException, TaglibGettingException {
      while(true) {
         TldLocation tldLocation = (TldLocation)this.tldLocations.get(uri);
         if (tldLocation != null) {
            return tldLocation;
         }

         switch (this.nextTldLocationLookupPhase) {
            case 0:
               this.addTldLocationsFromClasspathTlds();
               break;
            case 1:
               this.addTldLocationsFromWebXml();
               break;
            case 2:
               this.addTldLocationsFromWebInfTlds();
               break;
            case 3:
               this.addTldLocationsFromMetaInfTlds();
               break;
            case 4:
               return null;
            default:
               throw new BugException();
         }

         ++this.nextTldLocationLookupPhase;
      }
   }

   private void addTldLocationsFromWebXml() throws SAXException, IOException {
      LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/web.xml");
      WebXmlParser webXmlParser = new WebXmlParser();
      InputStream in = this.servletContext.getResourceAsStream("/WEB-INF/web.xml");
      if (in == null) {
         LOG.debug("No web.xml was found in servlet context");
      } else {
         try {
            parseXml(in, this.servletContext.getResource("/WEB-INF/web.xml").toExternalForm(), webXmlParser);
         } finally {
            in.close();
         }

      }
   }

   private void addTldLocationsFromWebInfTlds() throws IOException, SAXException {
      LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/**/*.tld");
      this.addTldLocationsFromServletContextResourceTlds("/WEB-INF");
   }

   private void addTldLocationsFromServletContextResourceTlds(String basePath) throws IOException, SAXException {
      Set unsortedResourcePaths = this.servletContext.getResourcePaths(basePath);
      if (unsortedResourcePaths != null) {
         List resourcePaths = new ArrayList(unsortedResourcePaths);
         Collections.sort(resourcePaths);
         Iterator it = resourcePaths.iterator();

         String resourcePath;
         while(it.hasNext()) {
            resourcePath = (String)it.next();
            if (resourcePath.endsWith(".tld")) {
               this.addTldLocationFromTld(new ServletContextTldLocation(resourcePath));
            }
         }

         it = resourcePaths.iterator();

         while(it.hasNext()) {
            resourcePath = (String)it.next();
            if (resourcePath.endsWith("/")) {
               this.addTldLocationsFromServletContextResourceTlds(resourcePath);
            }
         }
      }

   }

   private void addTldLocationsFromMetaInfTlds() throws IOException, SAXException {
      if (this.metaInfTldSources != null && !this.metaInfTldSources.isEmpty()) {
         Set cpMetaInfDirUrlsWithEF = null;
         int srcIdxStart = 0;

         int srcIdx;
         for(srcIdx = this.metaInfTldSources.size() - 1; srcIdx >= 0; --srcIdx) {
            if (this.metaInfTldSources.get(srcIdx) instanceof ClearMetaInfTldSource) {
               srcIdxStart = srcIdx + 1;
               break;
            }
         }

         for(srcIdx = srcIdxStart; srcIdx < this.metaInfTldSources.size(); ++srcIdx) {
            MetaInfTldSource miTldSource = (MetaInfTldSource)this.metaInfTldSources.get(srcIdx);
            if (miTldSource == TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE) {
               this.addTldLocationsFromWebInfPerLibJarMetaInfTlds();
            } else {
               if (!(miTldSource instanceof ClasspathMetaInfTldSource)) {
                  throw new BugException();
               }

               ClasspathMetaInfTldSource cpMiTldLocation = (ClasspathMetaInfTldSource)miTldSource;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Looking for TLD-s in classpathRoots[" + cpMiTldLocation.getRootContainerPattern() + "]" + "/META-INF/" + "**/*.tld");
               }

               if (cpMetaInfDirUrlsWithEF == null) {
                  cpMetaInfDirUrlsWithEF = collectMetaInfUrlsFromClassLoaders();
               }

               Iterator iterator = cpMetaInfDirUrlsWithEF.iterator();

               while(iterator.hasNext()) {
                  URLWithExternalForm urlWithEF = (URLWithExternalForm)iterator.next();
                  URL url = urlWithEF.getUrl();
                  boolean isJarUrl = isJarUrl(url);
                  String urlEF = urlWithEF.externalForm;
                  String rootContainerUrl;
                  if (isJarUrl) {
                     int sep = urlEF.indexOf("!/");
                     rootContainerUrl = sep != -1 ? urlEF.substring(0, sep) : urlEF;
                  } else {
                     rootContainerUrl = urlEF.endsWith("/META-INF/") ? urlEF.substring(0, urlEF.length() - "META-INF/".length()) : urlEF;
                  }

                  if (cpMiTldLocation.getRootContainerPattern().matcher(rootContainerUrl).matches()) {
                     File urlAsFile = this.urlToFileOrNull(url);
                     if (urlAsFile != null) {
                        this.addTldLocationsFromFileDirectory(urlAsFile);
                     } else if (isJarUrl) {
                        this.addTldLocationsFromJarDirectoryEntryURL(url);
                     } else if (LOG.isDebugEnabled()) {
                        LOG.debug("Can't list entries under this URL; TLD-s won't be discovered here: " + urlWithEF.getExternalForm());
                     }
                  }
               }
            }
         }

      }
   }

   private void addTldLocationsFromWebInfPerLibJarMetaInfTlds() throws IOException, SAXException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/lib/*.{jar,zip}/META-INF/*.tld");
      }

      Set libEntPaths = this.servletContext.getResourcePaths("/WEB-INF/lib");
      if (libEntPaths != null) {
         Iterator iter = libEntPaths.iterator();

         while(iter.hasNext()) {
            String libEntryPath = (String)iter.next();
            if (isJarPath(libEntryPath)) {
               this.addTldLocationsFromServletContextJar(libEntryPath);
            }
         }
      }

   }

   private void addTldLocationsFromClasspathTlds() throws SAXException, IOException, TaglibGettingException {
      if (this.classpathTlds != null && this.classpathTlds.size() != 0) {
         LOG.debug("Looking for TLD locations in TLD-s specified in cfg.classpathTlds");
         Iterator it = this.classpathTlds.iterator();

         while(it.hasNext()) {
            String tldResourcePath = (String)it.next();
            if (tldResourcePath.trim().length() == 0) {
               throw new TaglibGettingException("classpathTlds can't contain empty item");
            }

            if (!tldResourcePath.startsWith("/")) {
               tldResourcePath = "/" + tldResourcePath;
            }

            if (tldResourcePath.endsWith("/")) {
               throw new TaglibGettingException("classpathTlds can't specify a directory: " + tldResourcePath);
            }

            ClasspathTldLocation tldLocation = new ClasspathTldLocation(tldResourcePath);

            InputStream in;
            try {
               in = tldLocation.getInputStream();
            } catch (IOException var10) {
               if (LOG.isWarnEnabled()) {
                  LOG.warn("Ignored classpath TLD location " + StringUtil.jQuoteNoXSS(tldResourcePath) + " because of error", var10);
               }

               in = null;
            }

            if (in != null) {
               try {
                  this.addTldLocationFromTld(in, tldLocation);
               } finally {
                  in.close();
               }
            }
         }

      }
   }

   private void addTldLocationsFromServletContextJar(String jarResourcePath) throws IOException, MalformedURLException, SAXException {
      String metaInfEntryPath = normalizeJarEntryPath("/META-INF/", true);
      JarFile jarFile = this.servletContextResourceToFileOrNull(jarResourcePath);
      if (jarFile != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for /META-INF/*.tld-s in JarFile: servletContext:" + jarResourcePath);
         }

         Enumeration entries = jarFile.entries();

         while(entries.hasMoreElements()) {
            JarEntry curEntry = (JarEntry)entries.nextElement();
            String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
            if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
               this.addTldLocationFromTld(new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
            }
         }
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for /META-INF/*.tld-s in ZipInputStream (slow): servletContext:" + jarResourcePath);
         }

         InputStream in = this.servletContext.getResourceAsStream(jarResourcePath);
         if (in == null) {
            throw new IOException("ServletContext resource not found: " + jarResourcePath);
         }

         try {
            ZipInputStream zipIn = new ZipInputStream(in);
            Throwable var28 = null;

            try {
               while(true) {
                  ZipEntry curEntry = zipIn.getNextEntry();
                  if (curEntry == null) {
                     break;
                  }

                  String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
                  if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
                     this.addTldLocationFromTld(zipIn, new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
                  }
               }
            } catch (Throwable var23) {
               var28 = var23;
               throw var23;
            } finally {
               if (zipIn != null) {
                  if (var28 != null) {
                     try {
                        zipIn.close();
                     } catch (Throwable var22) {
                        var28.addSuppressed(var22);
                     }
                  } else {
                     zipIn.close();
                  }
               }

            }
         } finally {
            in.close();
         }
      }

   }

   private void addTldLocationsFromJarDirectoryEntryURL(URL jarBaseEntryUrl) throws IOException, MalformedURLException, SAXException {
      URLConnection urlCon = jarBaseEntryUrl.openConnection();
      JarFile jarFile;
      String baseEntryPath;
      String rawJarContentUrlEF;
      if (!this.test_emulateNoJarURLConnections && urlCon instanceof JarURLConnection) {
         JarURLConnection jarCon = (JarURLConnection)urlCon;
         jarFile = jarCon.getJarFile();
         rawJarContentUrlEF = null;
         baseEntryPath = normalizeJarEntryPath(jarCon.getEntryName(), true);
         if (baseEntryPath == null) {
            throw newFailedToExtractEntryPathException(jarBaseEntryUrl);
         }
      } else {
         String jarBaseEntryUrlEF = jarBaseEntryUrl.toExternalForm();
         int jarEntrySepIdx = jarBaseEntryUrlEF.indexOf("!/");
         if (jarEntrySepIdx == -1) {
            throw newFailedToExtractEntryPathException(jarBaseEntryUrl);
         }

         rawJarContentUrlEF = jarBaseEntryUrlEF.substring(jarBaseEntryUrlEF.indexOf(58) + 1, jarEntrySepIdx);
         baseEntryPath = normalizeJarEntryPath(jarBaseEntryUrlEF.substring(jarEntrySepIdx + "!/".length()), true);
         File rawJarContentAsFile = this.urlToFileOrNull(new URL(rawJarContentUrlEF));
         jarFile = rawJarContentAsFile != null ? new JarFile(rawJarContentAsFile) : null;
      }

      if (jarFile != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for /META-INF/**/*.tld-s in random access mode: " + jarBaseEntryUrl);
         }

         Enumeration entries = jarFile.entries();

         while(entries.hasMoreElements()) {
            JarEntry curEntry = (JarEntry)entries.nextElement();
            String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
            if (curEntryPath.startsWith(baseEntryPath) && curEntryPath.endsWith(".tld")) {
               String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
               URL tldUrl = createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
               this.addTldLocationFromTld(new JarEntryUrlTldLocation(tldUrl, (InputStreamFactory)null));
            }
         }
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for /META-INF/**/*.tld-s in stream mode (slow): " + rawJarContentUrlEF);
         }

         try {
            InputStream in = (new URL(rawJarContentUrlEF)).openStream();
            Throwable var40 = null;

            try {
               ZipInputStream zipIn = new ZipInputStream(in);

               try {
                  while(true) {
                     ZipEntry curEntry = zipIn.getNextEntry();
                     if (curEntry == null) {
                        break;
                     }

                     String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
                     if (curEntryPath.startsWith(baseEntryPath) && curEntryPath.endsWith(".tld")) {
                        String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
                        URL tldUrl = createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
                        this.addTldLocationFromTld(zipIn, new JarEntryUrlTldLocation(tldUrl, (InputStreamFactory)null));
                     }
                  }
               } finally {
                  zipIn.close();
               }
            } catch (Throwable var31) {
               var40 = var31;
               throw var31;
            } finally {
               if (in != null) {
                  if (var40 != null) {
                     try {
                        in.close();
                     } catch (Throwable var29) {
                        var40.addSuppressed(var29);
                     }
                  } else {
                     in.close();
                  }
               }

            }
         } catch (ZipException var33) {
            ZipException e = var33;
            IOException ioe = new IOException("Error reading ZIP (see cause excepetion) from: " + rawJarContentUrlEF);

            try {
               ioe.initCause(e);
            } catch (Exception var28) {
               throw var33;
            }

            throw ioe;
         }
      }

   }

   private void addTldLocationsFromFileDirectory(File dir) throws IOException, SAXException {
      if (dir.isDirectory()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for *.tld-s in File directory: " + StringUtil.jQuoteNoXSS((Object)dir));
         }

         File[] tldFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File urlAsFile, String name) {
               return TaglibFactory.isTldFileNameIgnoreCase(name);
            }
         });
         if (tldFiles == null) {
            throw new IOException("Can't list this directory for some reason: " + dir);
         }

         for(int i = 0; i < tldFiles.length; ++i) {
            File file = tldFiles[i];
            this.addTldLocationFromTld(new FileTldLocation(file));
         }
      } else {
         LOG.warn("Skipped scanning for *.tld for non-existent directory: " + StringUtil.jQuoteNoXSS((Object)dir));
      }

   }

   private void addTldLocationFromTld(TldLocation tldLocation) throws IOException, SAXException {
      InputStream in = tldLocation.getInputStream();
      Throwable var3 = null;

      try {
         this.addTldLocationFromTld(in, tldLocation);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (in != null) {
            if (var3 != null) {
               try {
                  in.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               in.close();
            }
         }

      }

   }

   private void addTldLocationFromTld(InputStream reusedIn, TldLocation tldLocation) throws SAXException, IOException {
      String taglibUri;
      try {
         taglibUri = this.getTaglibUriFromTld(reusedIn, tldLocation.getXmlSystemId());
      } catch (SAXException var8) {
         LOG.error("Error while parsing TLD; skipping: " + tldLocation, var8);
         synchronized(this.failedTldLocations) {
            this.failedTldLocations.add(tldLocation.toString());
         }

         taglibUri = null;
      }

      if (taglibUri != null) {
         this.addTldLocation(tldLocation, taglibUri);
      }

   }

   private void addTldLocation(TldLocation tldLocation, String taglibUri) {
      if (this.tldLocations.containsKey(taglibUri)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Ignored duplicate mapping of taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + StringUtil.jQuoteNoXSS((Object)tldLocation));
         }
      } else {
         this.tldLocations.put(taglibUri, tldLocation);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Mapped taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + StringUtil.jQuoteNoXSS((Object)tldLocation));
         }
      }

   }

   private static Set collectMetaInfUrlsFromClassLoaders() throws IOException {
      Set metainfDirUrls = new TreeSet();
      ClassLoader tccl = tryGetThreadContextClassLoader();
      if (tccl != null) {
         collectMetaInfUrlsFromClassLoader(tccl, metainfDirUrls);
      }

      ClassLoader cccl = TaglibFactory.class.getClassLoader();
      if (!isDescendantOfOrSameAs(tccl, cccl)) {
         collectMetaInfUrlsFromClassLoader(cccl, metainfDirUrls);
      }

      return metainfDirUrls;
   }

   private static void collectMetaInfUrlsFromClassLoader(ClassLoader cl, Set metainfDirUrls) throws IOException {
      Enumeration urls = cl.getResources("META-INF/");
      if (urls != null) {
         while(urls.hasMoreElements()) {
            metainfDirUrls.add(new URLWithExternalForm((URL)urls.nextElement()));
         }
      }

   }

   private String getTaglibUriFromTld(InputStream tldFileIn, String tldFileXmlSystemId) throws SAXException, IOException {
      TldParserForTaglibUriExtraction tldParser = new TldParserForTaglibUriExtraction();
      parseXml(tldFileIn, tldFileXmlSystemId, tldParser);
      return tldParser.getTaglibUri();
   }

   private TemplateHashModel loadTaglib(TldLocation tldLocation, String taglibUri) throws IOException, SAXException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Loading taglib for URI " + StringUtil.jQuoteNoXSS(taglibUri) + " from TLD location " + StringUtil.jQuoteNoXSS((Object)tldLocation));
      }

      Taglib taglib = new Taglib(this.servletContext, tldLocation, this.objectWrapper);
      this.taglibs.put(taglibUri, taglib);
      this.tldLocations.remove(taglibUri);
      return taglib;
   }

   private static void parseXml(InputStream in, String systemId, DefaultHandler handler) throws SAXException, IOException {
      InputSource inSrc = new InputSource();
      inSrc.setSystemId(systemId);
      inSrc.setByteStream(toCloseIgnoring(in));
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);

      XMLReader reader;
      try {
         reader = factory.newSAXParser().getXMLReader();
      } catch (ParserConfigurationException var7) {
         throw new RuntimeException("XML parser setup failed", var7);
      }

      reader.setEntityResolver(new EmptyContentEntityResolver());
      reader.setContentHandler(handler);
      reader.setErrorHandler(handler);
      reader.parse(inSrc);
   }

   private static String resolveRelativeUri(String uri) throws TaglibGettingException {
      TemplateModel reqHash;
      try {
         reqHash = Environment.getCurrentEnvironment().getVariable("__FreeMarkerServlet.Request__");
      } catch (TemplateModelException var6) {
         throw new TaglibGettingException("Failed to get FreemarkerServlet request information", var6);
      }

      if (reqHash instanceof HttpRequestHashModel) {
         HttpServletRequest req = ((HttpRequestHashModel)reqHash).getRequest();
         String pi = req.getPathInfo();
         String reqPath = req.getServletPath();
         if (reqPath == null) {
            reqPath = "";
         }

         reqPath = reqPath + (pi == null ? "" : pi);
         int lastSlash = reqPath.lastIndexOf(47);
         return lastSlash != -1 ? reqPath.substring(0, lastSlash + 1) + uri : '/' + uri;
      } else {
         throw new TaglibGettingException("Can't resolve relative URI " + uri + " as request URL information is unavailable.");
      }
   }

   private static FilterInputStream toCloseIgnoring(InputStream in) {
      return new FilterInputStream(in) {
         public void close() {
         }
      };
   }

   private static int getUriType(String uri) throws MalformedURLException {
      if (uri == null) {
         throw new IllegalArgumentException("null is not a valid URI");
      } else if (uri.length() == 0) {
         throw new MalformedURLException("empty string is not a valid URI");
      } else {
         char c0 = uri.charAt(0);
         if (c0 == '/') {
            return 1;
         } else if (c0 >= 'a' && c0 <= 'z') {
            int colon = uri.indexOf(58);
            if (colon == -1) {
               return 2;
            } else {
               for(int i = 1; i < colon; ++i) {
                  char c = uri.charAt(i);
                  if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '+' && c != '-' && c != '.') {
                     return 2;
                  }
               }

               return 0;
            }
         } else {
            return 2;
         }
      }
   }

   private static boolean isJarPath(String uriPath) {
      return uriPath.endsWith(".jar") || uriPath.endsWith(".zip");
   }

   private static boolean isJarUrl(URL url) {
      String scheme = url.getProtocol();
      return "jar".equals(scheme) || "zip".equals(scheme) || "vfszip".equals(scheme) || "wsjar".equals(scheme);
   }

   private static URL createJarEntryUrl(URL jarBaseEntryUrl, String relativeEntryPath) throws MalformedURLException {
      if (relativeEntryPath.startsWith("/")) {
         relativeEntryPath = relativeEntryPath.substring(1);
      }

      try {
         return new URL(jarBaseEntryUrl, StringUtil.URLPathEnc(relativeEntryPath, PLATFORM_FILE_ENCODING));
      } catch (UnsupportedEncodingException var3) {
         throw new BugException();
      }
   }

   private static String normalizeJarEntryPath(String jarEntryDirPath, boolean directory) {
      if (!jarEntryDirPath.startsWith("/")) {
         jarEntryDirPath = "/" + jarEntryDirPath;
      }

      if (directory && !jarEntryDirPath.endsWith("/")) {
         jarEntryDirPath = jarEntryDirPath + "/";
      }

      return jarEntryDirPath;
   }

   private static MalformedURLException newFailedToExtractEntryPathException(URL url) {
      return new MalformedURLException("Failed to extract jar entry path from: " + url);
   }

   private File urlToFileOrNull(URL url) {
      if (this.test_emulateNoUrlToFileConversions) {
         return null;
      } else if (!"file".equals(url.getProtocol())) {
         return null;
      } else {
         String filePath;
         try {
            filePath = url.toURI().getSchemeSpecificPart();
         } catch (URISyntaxException var6) {
            try {
               filePath = URLDecoder.decode(url.getFile(), PLATFORM_FILE_ENCODING);
            } catch (UnsupportedEncodingException var5) {
               throw new BugException(var5);
            }
         }

         return new File(filePath);
      }
   }

   private JarFile servletContextResourceToFileOrNull(String jarResourcePath) throws MalformedURLException, IOException {
      URL jarResourceUrl = this.servletContext.getResource(jarResourcePath);
      if (jarResourceUrl == null) {
         LOG.error("ServletContext resource URL was null (missing resource?): " + jarResourcePath);
         return null;
      } else {
         File jarResourceAsFile = this.urlToFileOrNull(jarResourceUrl);
         if (jarResourceAsFile == null) {
            return null;
         } else if (!jarResourceAsFile.isFile()) {
            LOG.error("Jar file doesn't exist - falling back to stream mode: " + jarResourceAsFile);
            return null;
         } else {
            return new JarFile(jarResourceAsFile);
         }
      }
   }

   private static URL tryCreateServletContextJarEntryUrl(ServletContext servletContext, String servletContextJarFilePath, String entryPath) {
      try {
         URL jarFileUrl = servletContext.getResource(servletContextJarFilePath);
         if (jarFileUrl == null) {
            throw new IOException("Servlet context resource not found: " + servletContextJarFilePath);
         } else {
            return new URL("jar:" + jarFileUrl.toURI() + "!/" + URLEncoder.encode(entryPath.startsWith("/") ? entryPath.substring(1) : entryPath, PLATFORM_FILE_ENCODING));
         }
      } catch (Exception var4) {
         LOG.error("Couldn't get URL for serlvetContext resource " + StringUtil.jQuoteNoXSS(servletContextJarFilePath) + " / jar entry " + StringUtil.jQuoteNoXSS(entryPath), var4);
         return null;
      }
   }

   private static boolean isTldFileNameIgnoreCase(String name) {
      int dotIdx = name.lastIndexOf(46);
      if (dotIdx < 0) {
         return false;
      } else {
         String extension = name.substring(dotIdx + 1).toLowerCase();
         return extension.equalsIgnoreCase("tld");
      }
   }

   private static ClassLoader tryGetThreadContextClassLoader() {
      ClassLoader tccl;
      try {
         tccl = Thread.currentThread().getContextClassLoader();
      } catch (SecurityException var2) {
         tccl = null;
         LOG.warn("Can't access Thread Context ClassLoader", var2);
      }

      return tccl;
   }

   private static boolean isDescendantOfOrSameAs(ClassLoader descendant, ClassLoader parent) {
      while(descendant != null) {
         if (descendant == parent) {
            return true;
         }

         descendant = descendant.getParent();
      }

      return false;
   }

   static {
      DEFAULT_CLASSPATH_TLDS = Collections.EMPTY_LIST;
      DEFAULT_META_INF_TLD_SOURCES = Collections.singletonList(TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE);
      LOG = Logger.getLogger("freemarker.jsp");
      PLATFORM_FILE_ENCODING = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
   }

   private static class TaglibGettingException extends Exception {
      public TaglibGettingException(String message, Throwable cause) {
         super(message, cause);
      }

      public TaglibGettingException(String message) {
         super(message);
      }
   }

   private static class URLWithExternalForm implements Comparable {
      private final URL url;
      private final String externalForm;

      public URLWithExternalForm(URL url) {
         this.url = url;
         this.externalForm = url.toExternalForm();
      }

      public URL getUrl() {
         return this.url;
      }

      public String getExternalForm() {
         return this.externalForm;
      }

      public int hashCode() {
         return this.externalForm.hashCode();
      }

      public boolean equals(Object that) {
         if (this == that) {
            return true;
         } else if (that == null) {
            return false;
         } else if (this.getClass() != that.getClass()) {
            return false;
         } else {
            return !this.externalForm.equals(((URLWithExternalForm)that).externalForm);
         }
      }

      public String toString() {
         return "URLWithExternalForm(" + this.externalForm + ")";
      }

      public int compareTo(Object that) {
         return this.getExternalForm().compareTo(((URLWithExternalForm)that).getExternalForm());
      }
   }

   private static class TldParsingSAXException extends SAXParseException {
      private final Throwable cause;

      TldParsingSAXException(String message, Locator locator) {
         this(message, locator, (Throwable)null);
      }

      TldParsingSAXException(String message, Locator locator, Throwable e) {
         super(message, locator, e instanceof Exception ? (Exception)e : new Exception("Unchecked exception; see cause", e));
         this.cause = e;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder(this.getClass().getName());
         sb.append(": ");
         int startLn = sb.length();
         String systemId = this.getSystemId();
         String publicId = this.getPublicId();
         if (systemId != null || publicId != null) {
            sb.append("In ");
            if (systemId != null) {
               sb.append(systemId);
            }

            if (publicId != null) {
               if (systemId != null) {
                  sb.append(" (public ID: ");
               }

               sb.append(publicId);
               if (systemId != null) {
                  sb.append(')');
               }
            }
         }

         int line = this.getLineNumber();
         if (line != -1) {
            sb.append(sb.length() != startLn ? ", at " : "At ");
            sb.append("line ");
            sb.append(line);
            int col = this.getColumnNumber();
            if (col != -1) {
               sb.append(", column ");
               sb.append(col);
            }
         }

         String message = this.getLocalizedMessage();
         if (message != null) {
            if (sb.length() != startLn) {
               sb.append(":\n");
            }

            sb.append(message);
         }

         return sb.toString();
      }

      public Throwable getCause() {
         Throwable superCause = super.getCause();
         return superCause == null ? this.cause : superCause;
      }
   }

   private static final class EmptyContentEntityResolver implements EntityResolver {
      private EmptyContentEntityResolver() {
      }

      public InputSource resolveEntity(String publicId, String systemId) {
         InputSource is = new InputSource(new ByteArrayInputStream(new byte[0]));
         is.setPublicId(publicId);
         is.setSystemId(systemId);
         return is;
      }

      // $FF: synthetic method
      EmptyContentEntityResolver(Object x0) {
         this();
      }
   }

   static final class TldParserForTaglibBuilding extends DefaultHandler {
      private static final String E_TAG = "tag";
      private static final String E_NAME = "name";
      private static final String E_TAG_CLASS = "tag-class";
      private static final String E_TAG_CLASS_LEGACY = "tagclass";
      private static final String E_FUNCTION = "function";
      private static final String E_FUNCTION_CLASS = "function-class";
      private static final String E_FUNCTION_SIGNATURE = "function-signature";
      private static final String E_LISTENER = "listener";
      private static final String E_LISTENER_CLASS = "listener-class";
      private final BeansWrapper beansWrapper;
      private final Map<String, TemplateModel> tagsAndFunctions = new HashMap();
      private final List listeners = new ArrayList();
      private Locator locator;
      private StringBuilder cDataCollector;
      private Stack stack = new Stack();
      private String tagNameCData;
      private String tagClassCData;
      private String functionNameCData;
      private String functionClassCData;
      private String functionSignatureCData;
      private String listenerClassCData;

      TldParserForTaglibBuilding(ObjectWrapper wrapper) {
         if (wrapper instanceof BeansWrapper) {
            this.beansWrapper = (BeansWrapper)wrapper;
         } else {
            this.beansWrapper = null;
            if (TaglibFactory.LOG.isWarnEnabled()) {
               TaglibFactory.LOG.warn("Custom EL functions won't be loaded because " + (wrapper == null ? "no ObjectWrapper was specified for the TaglibFactory (via TaglibFactory.setObjectWrapper(...), exists since 2.3.22)" : "the ObjectWrapper wasn't instance of " + BeansWrapper.class.getName()) + ".");
            }
         }

      }

      Map<String, TemplateModel> getTagsAndFunctions() {
         return this.tagsAndFunctions;
      }

      List getListeners() {
         return this.listeners;
      }

      public void setDocumentLocator(Locator locator) {
         this.locator = locator;
      }

      public void startElement(String nsUri, String localName, String qName, Attributes atts) {
         this.stack.push(qName);
         if (this.stack.size() == 3 && ("name".equals(qName) || "tagclass".equals(qName) || "tag-class".equals(qName) || "listener-class".equals(qName) || "function-class".equals(qName) || "function-signature".equals(qName))) {
            this.cDataCollector = new StringBuilder();
         }

      }

      public void characters(char[] chars, int off, int len) {
         if (this.cDataCollector != null) {
            this.cDataCollector.append(chars, off, len);
         }

      }

      public void endElement(String nsuri, String localName, String qName) throws TldParsingSAXException {
         if (!this.stack.peek().equals(qName)) {
            throw new TldParsingSAXException("Unbalanced tag nesting at \"" + qName + "\" end-tag.", this.locator);
         } else {
            if (this.stack.size() == 3) {
               if ("name".equals(qName)) {
                  if ("tag".equals(this.stack.get(1))) {
                     this.tagNameCData = this.pullCData();
                  } else if ("function".equals(this.stack.get(1))) {
                     this.functionNameCData = this.pullCData();
                  }
               } else if (!"tagclass".equals(qName) && !"tag-class".equals(qName)) {
                  if ("listener-class".equals(qName)) {
                     this.listenerClassCData = this.pullCData();
                  } else if ("function-class".equals(qName)) {
                     this.functionClassCData = this.pullCData();
                  } else if ("function-signature".equals(qName)) {
                     this.functionSignatureCData = this.pullCData();
                  }
               } else {
                  this.tagClassCData = this.pullCData();
               }
            } else if (this.stack.size() == 2) {
               Class listenerClass;
               Object customTagModel;
               if ("tag".equals(qName)) {
                  this.checkChildElementNotNull(qName, "name", this.tagNameCData);
                  this.checkChildElementNotNull(qName, "tag-class", this.tagClassCData);
                  listenerClass = this.resoveClassFromTLD(this.tagClassCData, "custom tag", this.tagNameCData);

                  try {
                     if (Tag.class.isAssignableFrom(listenerClass)) {
                        customTagModel = new TagTransformModel(this.tagNameCData, listenerClass);
                     } else {
                        customTagModel = new SimpleTagDirectiveModel(this.tagNameCData, listenerClass);
                     }
                  } catch (IntrospectionException var12) {
                     throw new TldParsingSAXException("JavaBean introspection failed on custom tag class " + this.tagClassCData, this.locator, var12);
                  }

                  TemplateModel replacedTagOrFunction = (TemplateModel)this.tagsAndFunctions.put(this.tagNameCData, customTagModel);
                  if (replacedTagOrFunction != null) {
                     if (CustomTagAndELFunctionCombiner.canBeCombinedAsELFunction(replacedTagOrFunction)) {
                        this.tagsAndFunctions.put(this.tagNameCData, CustomTagAndELFunctionCombiner.combine((TemplateModel)customTagModel, (TemplateMethodModelEx)replacedTagOrFunction));
                     } else {
                        TaglibFactory.LOG.warn("TLD contains multiple tags with name " + StringUtil.jQuote(this.tagNameCData) + "; keeping only the last one.");
                     }
                  }

                  this.tagNameCData = null;
                  this.tagClassCData = null;
               } else if ("function".equals(qName) && this.beansWrapper != null) {
                  this.checkChildElementNotNull(qName, "function-class", this.functionClassCData);
                  this.checkChildElementNotNull(qName, "function-signature", this.functionSignatureCData);
                  this.checkChildElementNotNull(qName, "name", this.functionNameCData);
                  listenerClass = this.resoveClassFromTLD(this.functionClassCData, "custom EL function", this.functionNameCData);

                  Method functionMethod;
                  try {
                     functionMethod = TaglibMethodUtil.getMethodByFunctionSignature(listenerClass, this.functionSignatureCData);
                  } catch (Exception var11) {
                     throw new TldParsingSAXException("Error while trying to resolve signature " + StringUtil.jQuote(this.functionSignatureCData) + " on class " + StringUtil.jQuote(listenerClass.getName()) + " for custom EL function " + StringUtil.jQuote(this.functionNameCData) + ".", this.locator, var11);
                  }

                  int modifiers = functionMethod.getModifiers();
                  if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                     throw new TldParsingSAXException("The custom EL function method must be public and static: " + functionMethod, this.locator);
                  }

                  TemplateMethodModelEx elFunctionModel;
                  try {
                     elFunctionModel = this.beansWrapper.wrap((Object)null, functionMethod);
                  } catch (Exception var10) {
                     throw new TldParsingSAXException("FreeMarker object wrapping failed on method : " + functionMethod, this.locator);
                  }

                  TemplateModel replacedTagOrFunction = (TemplateModel)this.tagsAndFunctions.put(this.functionNameCData, elFunctionModel);
                  if (replacedTagOrFunction != null) {
                     if (CustomTagAndELFunctionCombiner.canBeCombinedAsCustomTag(replacedTagOrFunction)) {
                        this.tagsAndFunctions.put(this.functionNameCData, CustomTagAndELFunctionCombiner.combine(replacedTagOrFunction, elFunctionModel));
                     } else {
                        TaglibFactory.LOG.warn("TLD contains multiple functions with name " + StringUtil.jQuote(this.functionNameCData) + "; keeping only the last one.");
                     }
                  }

                  this.functionNameCData = null;
                  this.functionClassCData = null;
                  this.functionSignatureCData = null;
               } else if ("listener".equals(qName)) {
                  this.checkChildElementNotNull(qName, "listener-class", this.listenerClassCData);
                  listenerClass = this.resoveClassFromTLD(this.listenerClassCData, "listener", (String)null);

                  try {
                     customTagModel = listenerClass.newInstance();
                  } catch (Exception var9) {
                     throw new TldParsingSAXException("Failed to create new instantiate from listener class " + this.listenerClassCData, this.locator, var9);
                  }

                  this.listeners.add(customTagModel);
                  this.listenerClassCData = null;
               }
            }

            this.stack.pop();
         }
      }

      private String pullCData() {
         String r = this.cDataCollector.toString().trim();
         this.cDataCollector = null;
         return r;
      }

      private void checkChildElementNotNull(String parentElementName, String childElementName, String value) throws TldParsingSAXException {
         if (value == null) {
            throw new TldParsingSAXException("Missing required \"" + childElementName + "\" element inside the \"" + parentElementName + "\" element.", this.locator);
         }
      }

      private Class resoveClassFromTLD(String className, String entryType, String entryName) throws TldParsingSAXException {
         try {
            return ClassUtil.forName(className);
         } catch (ClassNotFoundException | LinkageError var5) {
            throw this.newTLDEntryClassLoadingException(var5, className, entryType, entryName);
         }
      }

      private TldParsingSAXException newTLDEntryClassLoadingException(Throwable e, String className, String entryType, String entryName) throws TldParsingSAXException {
         int dotIdx = className.lastIndexOf(46);
         if (dotIdx != -1) {
            dotIdx = className.lastIndexOf(46, dotIdx - 1);
         }

         boolean looksLikeNestedClass = dotIdx != -1 && className.length() > dotIdx + 1 && Character.isUpperCase(className.charAt(dotIdx + 1));
         return new TldParsingSAXException((e instanceof ClassNotFoundException ? "Not found class " : "Can't load class ") + StringUtil.jQuote(className) + " for " + entryType + (entryName != null ? " " + StringUtil.jQuote(entryName) : "") + "." + (looksLikeNestedClass ? " Hint: Before nested classes, use \"$\", not \".\"." : ""), this.locator, e);
      }
   }

   private static class TldParserForTaglibUriExtraction extends DefaultHandler {
      private static final String E_URI = "uri";
      private StringBuilder cDataCollector;
      private String uri;

      TldParserForTaglibUriExtraction() {
      }

      String getTaglibUri() {
         return this.uri;
      }

      public void startElement(String nsuri, String localName, String qName, Attributes atts) {
         if ("uri".equals(qName)) {
            this.cDataCollector = new StringBuilder();
         }

      }

      public void characters(char[] chars, int off, int len) {
         if (this.cDataCollector != null) {
            this.cDataCollector.append(chars, off, len);
         }

      }

      public void endElement(String nsuri, String localName, String qName) {
         if ("uri".equals(qName)) {
            this.uri = this.cDataCollector.toString().trim();
            this.cDataCollector = null;
         }

      }
   }

   private class WebXmlParser extends DefaultHandler {
      private static final String E_TAGLIB = "taglib";
      private static final String E_TAGLIB_LOCATION = "taglib-location";
      private static final String E_TAGLIB_URI = "taglib-uri";
      private StringBuilder cDataCollector;
      private String taglibUriCData;
      private String taglibLocationCData;
      private Locator locator;

      private WebXmlParser() {
      }

      public void setDocumentLocator(Locator locator) {
         this.locator = locator;
      }

      public void startElement(String nsuri, String localName, String qName, Attributes atts) {
         if ("taglib-uri".equals(qName) || "taglib-location".equals(qName)) {
            this.cDataCollector = new StringBuilder();
         }

      }

      public void characters(char[] chars, int off, int len) {
         if (this.cDataCollector != null) {
            this.cDataCollector.append(chars, off, len);
         }

      }

      public void endElement(String nsUri, String localName, String qName) throws TldParsingSAXException {
         if ("taglib-uri".equals(qName)) {
            this.taglibUriCData = this.cDataCollector.toString().trim();
            this.cDataCollector = null;
         } else if ("taglib-location".equals(qName)) {
            this.taglibLocationCData = this.cDataCollector.toString().trim();
            if (this.taglibLocationCData.length() == 0) {
               throw new TldParsingSAXException("Required \"taglib-uri\" element was missing or empty", this.locator);
            }

            try {
               if (TaglibFactory.getUriType(this.taglibLocationCData) == 2) {
                  this.taglibLocationCData = "/WEB-INF/" + this.taglibLocationCData;
               }
            } catch (MalformedURLException var5) {
               throw new TldParsingSAXException("Failed to detect URI type for: " + this.taglibLocationCData, this.locator, var5);
            }

            this.cDataCollector = null;
         } else if ("taglib".equals(qName)) {
            TaglibFactory.this.addTldLocation((TldLocation)(TaglibFactory.isJarPath(this.taglibLocationCData) ? TaglibFactory.this.new ServletContextJarEntryTldLocation(this.taglibLocationCData, "/META-INF/taglib.tld") : TaglibFactory.this.new ServletContextTldLocation(this.taglibLocationCData)), this.taglibUriCData);
         }

      }

      // $FF: synthetic method
      WebXmlParser(Object x1) {
         this();
      }
   }

   private static final class Taglib implements TemplateHashModel {
      private final Map tagsAndFunctions;

      Taglib(ServletContext ctx, TldLocation tldPath, ObjectWrapper wrapper) throws IOException, SAXException {
         this.tagsAndFunctions = parseToTagsAndFunctions(ctx, tldPath, wrapper);
      }

      public TemplateModel get(String key) {
         return (TemplateModel)this.tagsAndFunctions.get(key);
      }

      public boolean isEmpty() {
         return this.tagsAndFunctions.isEmpty();
      }

      private static final Map parseToTagsAndFunctions(ServletContext ctx, TldLocation tldLocation, ObjectWrapper objectWrapper) throws IOException, SAXException {
         TldParserForTaglibBuilding tldParser = new TldParserForTaglibBuilding(objectWrapper);
         InputStream in = tldLocation.getInputStream();
         Throwable var5 = null;

         try {
            TaglibFactory.parseXml(in, tldLocation.getXmlSystemId(), tldParser);
         } catch (Throwable var14) {
            var5 = var14;
            throw var14;
         } finally {
            if (in != null) {
               if (var5 != null) {
                  try {
                     in.close();
                  } catch (Throwable var13) {
                     var5.addSuppressed(var13);
                  }
               } else {
                  in.close();
               }
            }

         }

         EventForwarding eventForwarding = EventForwarding.getInstance(ctx);
         if (eventForwarding != null) {
            eventForwarding.addListeners(tldParser.getListeners());
         } else if (tldParser.getListeners().size() > 0) {
            throw new TldParsingSAXException("Event listeners specified in the TLD could not be  registered since the web application doesn't have a listener of class " + EventForwarding.class.getName() + ". To remedy this, add this element to web.xml:\n| <listener>\n|   <listener-class>" + EventForwarding.class.getName() + "</listener-class>\n| </listener>", (Locator)null);
         }

         return tldParser.getTagsAndFunctions();
      }
   }

   private static class FileTldLocation implements TldLocation {
      private final File file;

      public FileTldLocation(File file) {
         this.file = file;
      }

      public InputStream getInputStream() throws IOException {
         return new FileInputStream(this.file);
      }

      public String getXmlSystemId() throws IOException {
         return this.file.toURI().toURL().toExternalForm();
      }

      public String toString() {
         return this.file.toString();
      }
   }

   private class ServletContextJarEntryTldLocation extends JarEntryTldLocation {
      private ServletContextJarEntryTldLocation(final String servletContextJarFilePath, String entryPath) {
         super(TaglibFactory.tryCreateServletContextJarEntryUrl(TaglibFactory.this.servletContext, servletContextJarFilePath, entryPath), new InputStreamFactory() {
            public InputStream getInputStream() {
               return TaglibFactory.this.servletContext.getResourceAsStream(servletContextJarFilePath);
            }

            public String toString() {
               return "servletContext:" + servletContextJarFilePath;
            }
         }, entryPath);
      }

      // $FF: synthetic method
      ServletContextJarEntryTldLocation(String x1, String x2, Object x3) {
         this(x1, x2);
      }
   }

   private class JarEntryUrlTldLocation extends JarEntryTldLocation {
      private JarEntryUrlTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory) {
         super(entryUrl, fallbackRawJarContentInputStreamFactory, (String)null);
      }

      // $FF: synthetic method
      JarEntryUrlTldLocation(URL x1, InputStreamFactory x2, Object x3) {
         this(x1, x2);
      }
   }

   private abstract class JarEntryTldLocation implements TldLocation {
      private final URL entryUrl;
      private final InputStreamFactory fallbackRawJarContentInputStreamFactory;
      private final String entryPath;

      public JarEntryTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory, String entryPath) {
         if (entryUrl == null) {
            NullArgumentException.check(fallbackRawJarContentInputStreamFactory);
            NullArgumentException.check(entryPath);
         }

         this.entryUrl = entryUrl;
         this.fallbackRawJarContentInputStreamFactory = fallbackRawJarContentInputStreamFactory;
         this.entryPath = entryPath != null ? TaglibFactory.normalizeJarEntryPath(entryPath, false) : null;
      }

      public InputStream getInputStream() throws IOException {
         if (this.entryUrl != null) {
            try {
               if (TaglibFactory.this.test_emulateJarEntryUrlOpenStreamFails) {
                  throw new RuntimeException("Test only");
               }

               return this.entryUrl.openStream();
            } catch (Exception var11) {
               if (this.fallbackRawJarContentInputStreamFactory == null) {
                  if (var11 instanceof IOException) {
                     throw (IOException)var11;
                  }

                  if (var11 instanceof RuntimeException) {
                     throw (RuntimeException)var11;
                  }

                  throw new RuntimeException(var11);
               }

               TaglibFactory.LOG.error("Failed to open InputStream for URL (will try fallback stream): " + this.entryUrl);
            }
         }

         String entryPath;
         if (this.entryPath != null) {
            entryPath = this.entryPath;
         } else {
            if (this.entryUrl == null) {
               throw new IOException("Nothing to deduce jar entry path from.");
            }

            String urlEF = this.entryUrl.toExternalForm();
            int sepIdx = urlEF.indexOf("!/");
            if (sepIdx == -1) {
               throw new IOException("Couldn't extract jar entry path from: " + urlEF);
            }

            entryPath = TaglibFactory.normalizeJarEntryPath(URLDecoder.decode(urlEF.substring(sepIdx + "!/".length()), TaglibFactory.PLATFORM_FILE_ENCODING), false);
         }

         InputStream rawIn = null;
         ZipInputStream zipIn = null;
         boolean returnedZipIn = false;

         try {
            rawIn = this.fallbackRawJarContentInputStreamFactory.getInputStream();
            if (rawIn == null) {
               throw new IOException("Jar's InputStreamFactory (" + this.fallbackRawJarContentInputStreamFactory + ") says the resource doesn't exist.");
            } else {
               zipIn = new ZipInputStream(rawIn);

               ZipEntry macthedJarEntry;
               do {
                  macthedJarEntry = zipIn.getNextEntry();
                  if (macthedJarEntry == null) {
                     throw new IOException("Could not find JAR entry " + StringUtil.jQuoteNoXSS(entryPath) + ".");
                  }
               } while(!entryPath.equals(TaglibFactory.normalizeJarEntryPath(macthedJarEntry.getName(), false)));

               returnedZipIn = true;
               ZipInputStream var6 = zipIn;
               return var6;
            }
         } finally {
            if (!returnedZipIn) {
               if (zipIn != null) {
                  zipIn.close();
               }

               if (rawIn != null) {
                  rawIn.close();
               }
            }

         }
      }

      public String getXmlSystemId() {
         return this.entryUrl != null ? this.entryUrl.toExternalForm() : null;
      }

      public String toString() {
         return this.entryUrl != null ? this.entryUrl.toExternalForm() : "jar:{" + this.fallbackRawJarContentInputStreamFactory + "}!" + this.entryPath;
      }
   }

   private static class ClasspathTldLocation implements TldLocation {
      private final String resourcePath;

      public ClasspathTldLocation(String resourcePath) {
         if (!resourcePath.startsWith("/")) {
            throw new IllegalArgumentException("\"resourcePath\" must start with /");
         } else {
            this.resourcePath = resourcePath;
         }
      }

      public String toString() {
         return "classpath:" + this.resourcePath;
      }

      public InputStream getInputStream() throws IOException {
         ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
         if (tccl != null) {
            InputStream ins = ClassUtil.getReasourceAsStream(tccl, this.resourcePath, true);
            if (ins != null) {
               return ins;
            }
         }

         return ClassUtil.getReasourceAsStream(this.getClass(), this.resourcePath, false);
      }

      public String getXmlSystemId() throws IOException {
         ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
         URL url;
         if (tccl != null) {
            url = tccl.getResource(this.resourcePath);
            if (url != null) {
               return url.toExternalForm();
            }
         }

         url = this.getClass().getResource(this.resourcePath);
         return url == null ? null : url.toExternalForm();
      }
   }

   private class ServletContextTldLocation implements TldLocation {
      private final String fileResourcePath;

      public ServletContextTldLocation(String fileResourcePath) {
         this.fileResourcePath = fileResourcePath;
      }

      public InputStream getInputStream() throws IOException {
         InputStream in = TaglibFactory.this.servletContext.getResourceAsStream(this.fileResourcePath);
         if (in == null) {
            throw this.newResourceNotFoundException();
         } else {
            return in;
         }
      }

      public String getXmlSystemId() throws IOException {
         URL url = TaglibFactory.this.servletContext.getResource(this.fileResourcePath);
         return url != null ? url.toExternalForm() : null;
      }

      private IOException newResourceNotFoundException() {
         return new IOException("Resource not found: servletContext:" + this.fileResourcePath);
      }

      public final String toString() {
         return "servletContext:" + this.fileResourcePath;
      }
   }

   private interface InputStreamFactory {
      InputStream getInputStream();
   }

   private interface TldLocation {
      InputStream getInputStream() throws IOException;

      String getXmlSystemId() throws IOException;
   }

   public static final class ClearMetaInfTldSource extends MetaInfTldSource {
      public static final ClearMetaInfTldSource INSTANCE = new ClearMetaInfTldSource();

      private ClearMetaInfTldSource() {
         super(null);
      }
   }

   public static final class ClasspathMetaInfTldSource extends MetaInfTldSource {
      private final Pattern rootContainerPattern;

      public ClasspathMetaInfTldSource(Pattern rootContainerPattern) {
         super(null);
         this.rootContainerPattern = rootContainerPattern;
      }

      public Pattern getRootContainerPattern() {
         return this.rootContainerPattern;
      }
   }

   public static final class WebInfPerLibJarMetaInfTldSource extends MetaInfTldSource {
      public static final WebInfPerLibJarMetaInfTldSource INSTANCE = new WebInfPerLibJarMetaInfTldSource();

      private WebInfPerLibJarMetaInfTldSource() {
         super(null);
      }
   }

   public abstract static class MetaInfTldSource {
      private MetaInfTldSource() {
      }

      // $FF: synthetic method
      MetaInfTldSource(Object x0) {
         this();
      }
   }
}
