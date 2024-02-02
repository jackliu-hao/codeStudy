/*      */ package freemarker.ext.jsp;
/*      */ 
/*      */ import freemarker.core.BugException;
/*      */ import freemarker.core.Environment;
/*      */ import freemarker.ext.beans.BeansWrapper;
/*      */ import freemarker.ext.servlet.FreemarkerServlet;
/*      */ import freemarker.ext.servlet.HttpRequestHashModel;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.TemplateHashModel;
/*      */ import freemarker.template.TemplateMethodModelEx;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.utility.ClassUtil;
/*      */ import freemarker.template.utility.NullArgumentException;
/*      */ import freemarker.template.utility.SecurityUtilities;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import java.beans.IntrospectionException;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FilenameFilter;
/*      */ import java.io.FilterInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.net.JarURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.URLDecoder;
/*      */ import java.net.URLEncoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.TreeSet;
/*      */ import java.util.jar.JarEntry;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipException;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import javax.servlet.ServletContext;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.jsp.tagext.Tag;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.parsers.SAXParserFactory;
/*      */ import org.xml.sax.Attributes;
/*      */ import org.xml.sax.EntityResolver;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.Locator;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.SAXParseException;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.helpers.DefaultHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TaglibFactory
/*      */   implements TemplateHashModel
/*      */ {
/*  103 */   public static final List DEFAULT_CLASSPATH_TLDS = Collections.EMPTY_LIST;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  113 */   public static final List DEFAULT_META_INF_TLD_SOURCES = Collections.singletonList(WebInfPerLibJarMetaInfTldSource.INSTANCE);
/*      */   
/*  115 */   private static final Logger LOG = Logger.getLogger("freemarker.jsp");
/*      */   
/*      */   private static final int URL_TYPE_FULL = 0;
/*      */   
/*      */   private static final int URL_TYPE_ABSOLUTE = 1;
/*      */   
/*      */   private static final int URL_TYPE_RELATIVE = 2;
/*      */   private static final String META_INF_REL_PATH = "META-INF/";
/*      */   private static final String META_INF_ABS_PATH = "/META-INF/";
/*      */   private static final String DEFAULT_TLD_RESOURCE_PATH = "/META-INF/taglib.tld";
/*      */   private static final String JAR_URL_ENTRY_PATH_START = "!/";
/*  126 */   private static final String PLATFORM_FILE_ENCODING = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
/*      */   
/*      */   private final ServletContext servletContext;
/*      */   
/*      */   private ObjectWrapper objectWrapper;
/*  131 */   private List metaInfTldSources = DEFAULT_META_INF_TLD_SOURCES;
/*  132 */   private List classpathTlds = DEFAULT_CLASSPATH_TLDS;
/*      */   
/*      */   boolean test_emulateNoUrlToFileConversions = false;
/*      */   
/*      */   boolean test_emulateNoJarURLConnections = false;
/*      */   boolean test_emulateJarEntryUrlOpenStreamFails = false;
/*  138 */   private final Object lock = new Object();
/*  139 */   private final Map taglibs = new HashMap<>();
/*  140 */   private final Map tldLocations = new HashMap<>();
/*  141 */   private List failedTldLocations = new ArrayList();
/*  142 */   private int nextTldLocationLookupPhase = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TaglibFactory(ServletContext ctx) {
/*  157 */     this.servletContext = ctx;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel get(String taglibUri) throws TemplateModelException {
/*  180 */     synchronized (this.lock) {
/*      */       TldLocation tldLocation; String normalizedTaglibUri;
/*  182 */       Taglib taglib = (Taglib)this.taglibs.get(taglibUri);
/*  183 */       if (taglib != null) {
/*  184 */         return (TemplateModel)taglib;
/*      */       }
/*      */ 
/*      */       
/*  188 */       boolean failedTldListAlreadyIncluded = false;
/*      */ 
/*      */       
/*      */       try {
/*  192 */         if (LOG.isDebugEnabled()) {
/*  193 */           LOG.debug("Locating TLD for taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + ".");
/*      */         }
/*      */         
/*  196 */         TldLocation explicitlyMappedTldLocation = getExplicitlyMappedTldLocation(taglibUri);
/*  197 */         if (explicitlyMappedTldLocation != null) {
/*  198 */           tldLocation = explicitlyMappedTldLocation;
/*  199 */           normalizedTaglibUri = taglibUri;
/*      */         } else {
/*      */           int urlType;
/*      */ 
/*      */           
/*      */           try {
/*  205 */             urlType = getUriType(taglibUri);
/*  206 */           } catch (MalformedURLException e) {
/*  207 */             throw new TaglibGettingException("Malformed taglib URI: " + StringUtil.jQuote(taglibUri), e);
/*      */           } 
/*  209 */           if (urlType == 2)
/*  210 */           { normalizedTaglibUri = resolveRelativeUri(taglibUri); }
/*  211 */           else if (urlType == 1)
/*  212 */           { normalizedTaglibUri = taglibUri; }
/*  213 */           else { if (urlType == 0) {
/*      */               
/*  215 */               String failedTLDsList = getFailedTLDsList();
/*  216 */               failedTldListAlreadyIncluded = true;
/*  217 */               throw new TaglibGettingException("No TLD was found for the " + 
/*  218 */                   StringUtil.jQuoteNoXSS(taglibUri) + " JSP taglib URI. (TLD-s are searched according the JSP 2.2 specification. In development- and embedded-servlet-container setups you may also need the \"" + "MetaInfTldSources" + "\" and \"" + "ClasspathTlds" + "\" " + FreemarkerServlet.class
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/*  223 */                   .getName() + " init-params or the similar system properites." + ((failedTLDsList == null) ? "" : (" Also note these TLD-s were skipped earlier due to errors; see error in the log: " + failedTLDsList)) + ")");
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  231 */             throw new BugException(); }
/*      */ 
/*      */           
/*  234 */           if (!normalizedTaglibUri.equals(taglibUri)) {
/*  235 */             Taglib taglib1 = (Taglib)this.taglibs.get(normalizedTaglibUri);
/*  236 */             if (taglib1 != null) {
/*  237 */               return (TemplateModel)taglib1;
/*      */             }
/*      */           } 
/*      */           
/*  241 */           tldLocation = isJarPath(normalizedTaglibUri) ? new ServletContextJarEntryTldLocation(normalizedTaglibUri, "/META-INF/taglib.tld") : new ServletContextTldLocation(normalizedTaglibUri);
/*      */         
/*      */         }
/*      */       
/*      */       }
/*  246 */       catch (Exception e) {
/*  247 */         String failedTLDsList = failedTldListAlreadyIncluded ? null : getFailedTLDsList();
/*  248 */         throw new TemplateModelException("Error while looking for TLD file for " + 
/*  249 */             StringUtil.jQuoteNoXSS(taglibUri) + "; see cause exception." + ((failedTLDsList == null) ? "" : (" (Note: These TLD-s were skipped earlier due to errors; see errors in the log: " + failedTLDsList + ")")), e);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  259 */         return (TemplateModel)loadTaglib(tldLocation, normalizedTaglibUri);
/*  260 */       } catch (Exception e) {
/*  261 */         throw new TemplateModelException("Error while loading tag library for URI " + 
/*  262 */             StringUtil.jQuoteNoXSS(normalizedTaglibUri) + " from TLD location " + 
/*  263 */             StringUtil.jQuoteNoXSS(tldLocation) + "; see cause exception.", e);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getFailedTLDsList() {
/*  273 */     synchronized (this.failedTldLocations) {
/*  274 */       if (this.failedTldLocations.isEmpty()) {
/*  275 */         return null;
/*      */       }
/*  277 */       StringBuilder sb = new StringBuilder();
/*  278 */       for (int i = 0; i < this.failedTldLocations.size(); i++) {
/*  279 */         if (i != 0) {
/*  280 */           sb.append(", ");
/*      */         }
/*  282 */         sb.append(StringUtil.jQuote(this.failedTldLocations.get(i)));
/*      */       } 
/*  284 */       return sb.toString();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/*  293 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWrapper getObjectWrapper() {
/*  302 */     return this.objectWrapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObjectWrapper(ObjectWrapper objectWrapper) {
/*  314 */     checkNotStarted();
/*  315 */     this.objectWrapper = objectWrapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List getMetaInfTldSources() {
/*  324 */     return this.metaInfTldSources;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMetaInfTldSources(List metaInfTldSources) {
/*  346 */     checkNotStarted();
/*  347 */     NullArgumentException.check("metaInfTldSources", metaInfTldSources);
/*  348 */     this.metaInfTldSources = metaInfTldSources;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List getClasspathTlds() {
/*  357 */     return this.classpathTlds;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClasspathTlds(List classpathTlds) {
/*  377 */     checkNotStarted();
/*  378 */     NullArgumentException.check("classpathTlds", classpathTlds);
/*  379 */     this.classpathTlds = classpathTlds;
/*      */   }
/*      */   
/*      */   private void checkNotStarted() {
/*  383 */     synchronized (this.lock) {
/*  384 */       if (this.nextTldLocationLookupPhase != 0) {
/*  385 */         throw new IllegalStateException(TaglibFactory.class.getName() + " object was already in use.");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private TldLocation getExplicitlyMappedTldLocation(String uri) throws SAXException, IOException, TaglibGettingException {
/*      */     while (true) {
/*  393 */       TldLocation tldLocation = (TldLocation)this.tldLocations.get(uri);
/*  394 */       if (tldLocation != null) {
/*  395 */         return tldLocation;
/*      */       }
/*      */       
/*  398 */       switch (this.nextTldLocationLookupPhase) {
/*      */         
/*      */         case 0:
/*  401 */           addTldLocationsFromClasspathTlds();
/*      */           break;
/*      */         
/*      */         case 1:
/*  405 */           addTldLocationsFromWebXml();
/*      */           break;
/*      */         
/*      */         case 2:
/*  409 */           addTldLocationsFromWebInfTlds();
/*      */           break;
/*      */         
/*      */         case 3:
/*  413 */           addTldLocationsFromMetaInfTlds();
/*      */           break;
/*      */         case 4:
/*  416 */           return null;
/*      */         default:
/*  418 */           throw new BugException();
/*      */       } 
/*  420 */       this.nextTldLocationLookupPhase++;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void addTldLocationsFromWebXml() throws SAXException, IOException {
/*  425 */     LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/web.xml");
/*      */     
/*  427 */     WebXmlParser webXmlParser = new WebXmlParser();
/*  428 */     InputStream in = this.servletContext.getResourceAsStream("/WEB-INF/web.xml");
/*  429 */     if (in == null) {
/*  430 */       LOG.debug("No web.xml was found in servlet context");
/*      */       return;
/*      */     } 
/*      */     try {
/*  434 */       parseXml(in, this.servletContext.getResource("/WEB-INF/web.xml").toExternalForm(), webXmlParser);
/*      */     } finally {
/*  436 */       in.close();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void addTldLocationsFromWebInfTlds() throws IOException, SAXException {
/*  442 */     LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/**/*.tld");
/*  443 */     addTldLocationsFromServletContextResourceTlds("/WEB-INF");
/*      */   }
/*      */ 
/*      */   
/*      */   private void addTldLocationsFromServletContextResourceTlds(String basePath) throws IOException, SAXException {
/*  448 */     Set<?> unsortedResourcePaths = this.servletContext.getResourcePaths(basePath);
/*  449 */     if (unsortedResourcePaths != null) {
/*  450 */       List<Comparable> resourcePaths = new ArrayList(unsortedResourcePaths);
/*  451 */       Collections.sort(resourcePaths);
/*      */       
/*  453 */       for (Iterator<Comparable> iterator1 = resourcePaths.iterator(); iterator1.hasNext(); ) {
/*  454 */         String resourcePath = (String)iterator1.next();
/*  455 */         if (resourcePath.endsWith(".tld")) {
/*  456 */           addTldLocationFromTld(new ServletContextTldLocation(resourcePath));
/*      */         }
/*      */       } 
/*      */       
/*  460 */       for (Iterator<Comparable> it = resourcePaths.iterator(); it.hasNext(); ) {
/*  461 */         String resourcePath = (String)it.next();
/*  462 */         if (resourcePath.endsWith("/")) {
/*  463 */           addTldLocationsFromServletContextResourceTlds(resourcePath);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void addTldLocationsFromMetaInfTlds() throws IOException, SAXException {
/*  470 */     if (this.metaInfTldSources == null || this.metaInfTldSources.isEmpty()) {
/*      */       return;
/*      */     }
/*      */     
/*  474 */     Set cpMetaInfDirUrlsWithEF = null;
/*      */ 
/*      */     
/*  477 */     int srcIdxStart = 0;
/*  478 */     for (int i = this.metaInfTldSources.size() - 1; i >= 0; i--) {
/*  479 */       if (this.metaInfTldSources.get(i) instanceof ClearMetaInfTldSource) {
/*  480 */         srcIdxStart = i + 1;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  485 */     for (int srcIdx = srcIdxStart; srcIdx < this.metaInfTldSources.size(); srcIdx++) {
/*  486 */       MetaInfTldSource miTldSource = this.metaInfTldSources.get(srcIdx);
/*      */       
/*  488 */       if (miTldSource == WebInfPerLibJarMetaInfTldSource.INSTANCE) {
/*  489 */         addTldLocationsFromWebInfPerLibJarMetaInfTlds();
/*  490 */       } else if (miTldSource instanceof ClasspathMetaInfTldSource) {
/*  491 */         ClasspathMetaInfTldSource cpMiTldLocation = (ClasspathMetaInfTldSource)miTldSource;
/*  492 */         if (LOG.isDebugEnabled()) {
/*  493 */           LOG.debug("Looking for TLD-s in classpathRoots[" + cpMiTldLocation
/*  494 */               .getRootContainerPattern() + "]" + "/META-INF/" + "**/*.tld");
/*      */         }
/*      */ 
/*      */         
/*  498 */         if (cpMetaInfDirUrlsWithEF == null) {
/*  499 */           cpMetaInfDirUrlsWithEF = collectMetaInfUrlsFromClassLoaders();
/*      */         }
/*      */         
/*  502 */         for (Iterator<URLWithExternalForm> iterator = cpMetaInfDirUrlsWithEF.iterator(); iterator.hasNext(); ) {
/*  503 */           String rootContainerUrl; URLWithExternalForm urlWithEF = iterator.next();
/*  504 */           URL url = urlWithEF.getUrl();
/*  505 */           boolean isJarUrl = isJarUrl(url);
/*  506 */           String urlEF = urlWithEF.externalForm;
/*      */ 
/*      */           
/*  509 */           if (isJarUrl) {
/*  510 */             int sep = urlEF.indexOf("!/");
/*  511 */             rootContainerUrl = (sep != -1) ? urlEF.substring(0, sep) : urlEF;
/*      */           } else {
/*      */             
/*  514 */             rootContainerUrl = urlEF.endsWith("/META-INF/") ? urlEF.substring(0, urlEF.length() - "META-INF/".length()) : urlEF;
/*      */           } 
/*      */ 
/*      */           
/*  518 */           if (cpMiTldLocation.getRootContainerPattern().matcher(rootContainerUrl).matches()) {
/*  519 */             File urlAsFile = urlToFileOrNull(url);
/*  520 */             if (urlAsFile != null) {
/*  521 */               addTldLocationsFromFileDirectory(urlAsFile); continue;
/*  522 */             }  if (isJarUrl) {
/*  523 */               addTldLocationsFromJarDirectoryEntryURL(url); continue;
/*      */             } 
/*  525 */             if (LOG.isDebugEnabled()) {
/*  526 */               LOG.debug("Can't list entries under this URL; TLD-s won't be discovered here: " + urlWithEF
/*  527 */                   .getExternalForm());
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } else {
/*      */         
/*  533 */         throw new BugException();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void addTldLocationsFromWebInfPerLibJarMetaInfTlds() throws IOException, SAXException {
/*  539 */     if (LOG.isDebugEnabled()) {
/*  540 */       LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/lib/*.{jar,zip}/META-INF/*.tld");
/*      */     }
/*      */ 
/*      */     
/*  544 */     Set libEntPaths = this.servletContext.getResourcePaths("/WEB-INF/lib");
/*  545 */     if (libEntPaths != null) {
/*  546 */       for (Iterator<String> iter = libEntPaths.iterator(); iter.hasNext(); ) {
/*  547 */         String libEntryPath = iter.next();
/*  548 */         if (isJarPath(libEntryPath)) {
/*  549 */           addTldLocationsFromServletContextJar(libEntryPath);
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void addTldLocationsFromClasspathTlds() throws SAXException, IOException, TaglibGettingException {
/*  556 */     if (this.classpathTlds == null || this.classpathTlds.size() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*  560 */     LOG.debug("Looking for TLD locations in TLD-s specified in cfg.classpathTlds");
/*      */     
/*  562 */     for (Iterator<String> it = this.classpathTlds.iterator(); it.hasNext(); ) {
/*  563 */       InputStream in; String tldResourcePath = it.next();
/*  564 */       if (tldResourcePath.trim().length() == 0) {
/*  565 */         throw new TaglibGettingException("classpathTlds can't contain empty item");
/*      */       }
/*      */       
/*  568 */       if (!tldResourcePath.startsWith("/")) {
/*  569 */         tldResourcePath = "/" + tldResourcePath;
/*      */       }
/*  571 */       if (tldResourcePath.endsWith("/")) {
/*  572 */         throw new TaglibGettingException("classpathTlds can't specify a directory: " + tldResourcePath);
/*      */       }
/*      */       
/*  575 */       ClasspathTldLocation tldLocation = new ClasspathTldLocation(tldResourcePath);
/*      */       
/*      */       try {
/*  578 */         in = tldLocation.getInputStream();
/*  579 */       } catch (IOException e) {
/*  580 */         if (LOG.isWarnEnabled()) {
/*  581 */           LOG.warn("Ignored classpath TLD location " + StringUtil.jQuoteNoXSS(tldResourcePath) + " because of error", e);
/*      */         }
/*      */         
/*  584 */         in = null;
/*      */       } 
/*  586 */       if (in != null) {
/*      */         try {
/*  588 */           addTldLocationFromTld(in, tldLocation);
/*      */         } finally {
/*  590 */           in.close();
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addTldLocationsFromServletContextJar(String jarResourcePath) throws IOException, MalformedURLException, SAXException {
/*  602 */     String metaInfEntryPath = normalizeJarEntryPath("/META-INF/", true);
/*      */ 
/*      */     
/*  605 */     JarFile jarFile = servletContextResourceToFileOrNull(jarResourcePath);
/*  606 */     if (jarFile != null) {
/*  607 */       if (LOG.isDebugEnabled()) {
/*  608 */         LOG.debug("Scanning for /META-INF/*.tld-s in JarFile: servletContext:" + jarResourcePath);
/*      */       }
/*      */       
/*  611 */       for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
/*  612 */         JarEntry curEntry = entries.nextElement();
/*  613 */         String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
/*  614 */         if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
/*  615 */           addTldLocationFromTld(new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
/*      */         }
/*      */       } 
/*      */     } else {
/*  619 */       if (LOG.isDebugEnabled()) {
/*  620 */         LOG.debug("Scanning for /META-INF/*.tld-s in ZipInputStream (slow): servletContext:" + jarResourcePath);
/*      */       }
/*      */ 
/*      */       
/*  624 */       InputStream in = this.servletContext.getResourceAsStream(jarResourcePath);
/*  625 */       if (in == null) {
/*  626 */         throw new IOException("ServletContext resource not found: " + jarResourcePath);
/*      */       }
/*      */       
/*  629 */       try (ZipInputStream zipIn = new ZipInputStream(in)) {
/*      */         while (true) {
/*  631 */           ZipEntry curEntry = zipIn.getNextEntry();
/*  632 */           if (curEntry == null)
/*      */             break; 
/*  634 */           String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
/*  635 */           if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
/*  636 */             addTldLocationFromTld(zipIn, new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
/*      */           }
/*      */         }
/*      */       
/*      */       } finally {
/*      */         
/*  642 */         in.close();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addTldLocationsFromJarDirectoryEntryURL(URL jarBaseEntryUrl) throws IOException, MalformedURLException, SAXException {
/*      */     JarFile jarFile;
/*      */     String baseEntryPath, rawJarContentUrlEF;
/*  665 */     URLConnection urlCon = jarBaseEntryUrl.openConnection();
/*  666 */     if (!this.test_emulateNoJarURLConnections && urlCon instanceof JarURLConnection) {
/*  667 */       JarURLConnection jarCon = (JarURLConnection)urlCon;
/*  668 */       jarFile = jarCon.getJarFile();
/*  669 */       rawJarContentUrlEF = null;
/*  670 */       baseEntryPath = normalizeJarEntryPath(jarCon.getEntryName(), true);
/*  671 */       if (baseEntryPath == null) {
/*  672 */         throw newFailedToExtractEntryPathException(jarBaseEntryUrl);
/*      */       }
/*      */     } else {
/*  675 */       String jarBaseEntryUrlEF = jarBaseEntryUrl.toExternalForm();
/*  676 */       int jarEntrySepIdx = jarBaseEntryUrlEF.indexOf("!/");
/*  677 */       if (jarEntrySepIdx == -1) {
/*  678 */         throw newFailedToExtractEntryPathException(jarBaseEntryUrl);
/*      */       }
/*  680 */       rawJarContentUrlEF = jarBaseEntryUrlEF.substring(jarBaseEntryUrlEF.indexOf(':') + 1, jarEntrySepIdx);
/*  681 */       baseEntryPath = normalizeJarEntryPath(jarBaseEntryUrlEF
/*  682 */           .substring(jarEntrySepIdx + "!/".length()), true);
/*      */       
/*  684 */       File rawJarContentAsFile = urlToFileOrNull(new URL(rawJarContentUrlEF));
/*  685 */       jarFile = (rawJarContentAsFile != null) ? new JarFile(rawJarContentAsFile) : null;
/*      */     } 
/*      */     
/*  688 */     if (jarFile != null) {
/*  689 */       if (LOG.isDebugEnabled()) {
/*  690 */         LOG.debug("Scanning for /META-INF/**/*.tld-s in random access mode: " + jarBaseEntryUrl);
/*      */       }
/*      */       
/*  693 */       for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
/*  694 */         JarEntry curEntry = entries.nextElement();
/*  695 */         String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
/*  696 */         if (curEntryPath.startsWith(baseEntryPath) && curEntryPath.endsWith(".tld")) {
/*  697 */           String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
/*  698 */           URL tldUrl = createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
/*  699 */           addTldLocationFromTld(new JarEntryUrlTldLocation(tldUrl, null));
/*      */         } 
/*      */       } 
/*      */     } else {
/*      */       
/*  704 */       if (LOG.isDebugEnabled()) {
/*  705 */         LOG.debug("Scanning for /META-INF/**/*.tld-s in stream mode (slow): " + rawJarContentUrlEF);
/*      */       }
/*      */ 
/*      */       
/*  709 */       try (InputStream in = (new URL(rawJarContentUrlEF)).openStream()) {
/*  710 */         ZipInputStream zipIn = new ZipInputStream(in);
/*      */         try {
/*      */           while (true) {
/*  713 */             ZipEntry curEntry = zipIn.getNextEntry();
/*  714 */             if (curEntry == null)
/*      */               break; 
/*  716 */             String curEntryPath = normalizeJarEntryPath(curEntry.getName(), false);
/*  717 */             if (curEntryPath.startsWith(baseEntryPath) && curEntryPath.endsWith(".tld")) {
/*  718 */               String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
/*  719 */               URL tldUrl = createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
/*  720 */               addTldLocationFromTld(zipIn, new JarEntryUrlTldLocation(tldUrl, null));
/*      */             } 
/*      */           } 
/*      */         } finally {
/*  724 */           zipIn.close();
/*      */         } 
/*  726 */       } catch (ZipException e) {
/*      */         
/*  728 */         IOException ioe = new IOException("Error reading ZIP (see cause excepetion) from: " + rawJarContentUrlEF);
/*      */         
/*      */         try {
/*  731 */           ioe.initCause(e);
/*  732 */         } catch (Exception e2) {
/*  733 */           throw e;
/*      */         } 
/*  735 */         throw ioe;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void addTldLocationsFromFileDirectory(File dir) throws IOException, SAXException {
/*  741 */     if (dir.isDirectory()) {
/*  742 */       if (LOG.isDebugEnabled()) {
/*  743 */         LOG.debug("Scanning for *.tld-s in File directory: " + StringUtil.jQuoteNoXSS(dir));
/*      */       }
/*  745 */       File[] tldFiles = dir.listFiles(new FilenameFilter()
/*      */           {
/*      */             public boolean accept(File urlAsFile, String name)
/*      */             {
/*  749 */               return TaglibFactory.isTldFileNameIgnoreCase(name);
/*      */             }
/*      */           });
/*      */       
/*  753 */       if (tldFiles == null) {
/*  754 */         throw new IOException("Can't list this directory for some reason: " + dir);
/*      */       }
/*  756 */       for (int i = 0; i < tldFiles.length; i++) {
/*  757 */         File file = tldFiles[i];
/*  758 */         addTldLocationFromTld(new FileTldLocation(file));
/*      */       } 
/*      */     } else {
/*  761 */       LOG.warn("Skipped scanning for *.tld for non-existent directory: " + StringUtil.jQuoteNoXSS(dir));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addTldLocationFromTld(TldLocation tldLocation) throws IOException, SAXException {
/*  769 */     try (InputStream in = tldLocation.getInputStream()) {
/*  770 */       addTldLocationFromTld(in, tldLocation);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addTldLocationFromTld(InputStream reusedIn, TldLocation tldLocation) throws SAXException, IOException {
/*      */     String taglibUri;
/*      */     try {
/*  785 */       taglibUri = getTaglibUriFromTld(reusedIn, tldLocation.getXmlSystemId());
/*  786 */     } catch (SAXException e) {
/*  787 */       LOG.error("Error while parsing TLD; skipping: " + tldLocation, e);
/*  788 */       synchronized (this.failedTldLocations) {
/*  789 */         this.failedTldLocations.add(tldLocation.toString());
/*      */       } 
/*  791 */       taglibUri = null;
/*      */     } 
/*  793 */     if (taglibUri != null) {
/*  794 */       addTldLocation(tldLocation, taglibUri);
/*      */     }
/*      */   }
/*      */   
/*      */   private void addTldLocation(TldLocation tldLocation, String taglibUri) {
/*  799 */     if (this.tldLocations.containsKey(taglibUri)) {
/*  800 */       if (LOG.isDebugEnabled()) {
/*  801 */         LOG.debug("Ignored duplicate mapping of taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + 
/*  802 */             StringUtil.jQuoteNoXSS(tldLocation));
/*      */       }
/*      */     } else {
/*  805 */       this.tldLocations.put(taglibUri, tldLocation);
/*  806 */       if (LOG.isDebugEnabled()) {
/*  807 */         LOG.debug("Mapped taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + 
/*  808 */             StringUtil.jQuoteNoXSS(tldLocation));
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private static Set collectMetaInfUrlsFromClassLoaders() throws IOException {
/*  814 */     Set metainfDirUrls = new TreeSet();
/*      */     
/*  816 */     ClassLoader tccl = tryGetThreadContextClassLoader();
/*  817 */     if (tccl != null) {
/*  818 */       collectMetaInfUrlsFromClassLoader(tccl, metainfDirUrls);
/*      */     }
/*      */     
/*  821 */     ClassLoader cccl = TaglibFactory.class.getClassLoader();
/*  822 */     if (!isDescendantOfOrSameAs(tccl, cccl)) {
/*  823 */       collectMetaInfUrlsFromClassLoader(cccl, metainfDirUrls);
/*      */     }
/*  825 */     return metainfDirUrls;
/*      */   }
/*      */ 
/*      */   
/*      */   private static void collectMetaInfUrlsFromClassLoader(ClassLoader cl, Set<URLWithExternalForm> metainfDirUrls) throws IOException {
/*  830 */     Enumeration<URL> urls = cl.getResources("META-INF/");
/*  831 */     if (urls != null) {
/*  832 */       while (urls.hasMoreElements()) {
/*  833 */         metainfDirUrls.add(new URLWithExternalForm(urls.nextElement()));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private String getTaglibUriFromTld(InputStream tldFileIn, String tldFileXmlSystemId) throws SAXException, IOException {
/*  839 */     TldParserForTaglibUriExtraction tldParser = new TldParserForTaglibUriExtraction();
/*  840 */     parseXml(tldFileIn, tldFileXmlSystemId, tldParser);
/*  841 */     return tldParser.getTaglibUri();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TemplateHashModel loadTaglib(TldLocation tldLocation, String taglibUri) throws IOException, SAXException {
/*  851 */     if (LOG.isDebugEnabled()) {
/*  852 */       LOG.debug("Loading taglib for URI " + StringUtil.jQuoteNoXSS(taglibUri) + " from TLD location " + 
/*  853 */           StringUtil.jQuoteNoXSS(tldLocation));
/*      */     }
/*  855 */     Taglib taglib = new Taglib(this.servletContext, tldLocation, this.objectWrapper);
/*  856 */     this.taglibs.put(taglibUri, taglib);
/*  857 */     this.tldLocations.remove(taglibUri);
/*  858 */     return taglib;
/*      */   }
/*      */   
/*      */   private static void parseXml(InputStream in, String systemId, DefaultHandler handler) throws SAXException, IOException {
/*      */     XMLReader reader;
/*  863 */     InputSource inSrc = new InputSource();
/*  864 */     inSrc.setSystemId(systemId);
/*  865 */     inSrc.setByteStream(toCloseIgnoring(in));
/*      */     
/*  867 */     SAXParserFactory factory = SAXParserFactory.newInstance();
/*  868 */     factory.setNamespaceAware(false);
/*  869 */     factory.setValidating(false);
/*      */     
/*      */     try {
/*  872 */       reader = factory.newSAXParser().getXMLReader();
/*  873 */     } catch (ParserConfigurationException e) {
/*      */       
/*  875 */       throw new RuntimeException("XML parser setup failed", e);
/*      */     } 
/*  877 */     reader.setEntityResolver(new EmptyContentEntityResolver());
/*  878 */     reader.setContentHandler(handler);
/*  879 */     reader.setErrorHandler(handler);
/*      */     
/*  881 */     reader.parse(inSrc);
/*      */   }
/*      */   
/*      */   private static String resolveRelativeUri(String uri) throws TaglibGettingException {
/*      */     TemplateModel reqHash;
/*      */     try {
/*  887 */       reqHash = Environment.getCurrentEnvironment().getVariable("__FreeMarkerServlet.Request__");
/*      */     }
/*  889 */     catch (TemplateModelException e) {
/*  890 */       throw new TaglibGettingException("Failed to get FreemarkerServlet request information", e);
/*      */     } 
/*  892 */     if (reqHash instanceof HttpRequestHashModel) {
/*      */       
/*  894 */       HttpServletRequest req = ((HttpRequestHashModel)reqHash).getRequest();
/*  895 */       String pi = req.getPathInfo();
/*  896 */       String reqPath = req.getServletPath();
/*  897 */       if (reqPath == null) {
/*  898 */         reqPath = "";
/*      */       }
/*  900 */       reqPath = reqPath + ((pi == null) ? "" : pi);
/*      */ 
/*      */       
/*  903 */       int lastSlash = reqPath.lastIndexOf('/');
/*  904 */       if (lastSlash != -1) {
/*  905 */         return reqPath.substring(0, lastSlash + 1) + uri;
/*      */       }
/*  907 */       return '/' + uri;
/*      */     } 
/*      */     
/*  910 */     throw new TaglibGettingException("Can't resolve relative URI " + uri + " as request URL information is unavailable.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static FilterInputStream toCloseIgnoring(InputStream in) {
/*  918 */     return new FilterInputStream(in)
/*      */       {
/*      */         public void close() {}
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getUriType(String uri) throws MalformedURLException {
/*  927 */     if (uri == null) {
/*  928 */       throw new IllegalArgumentException("null is not a valid URI");
/*      */     }
/*  930 */     if (uri.length() == 0) {
/*  931 */       throw new MalformedURLException("empty string is not a valid URI");
/*      */     }
/*  933 */     char c0 = uri.charAt(0);
/*  934 */     if (c0 == '/') {
/*  935 */       return 1;
/*      */     }
/*      */     
/*  938 */     if (c0 < 'a' || c0 > 'z') {
/*  939 */       return 2;
/*      */     }
/*  941 */     int colon = uri.indexOf(':');
/*  942 */     if (colon == -1) {
/*  943 */       return 2;
/*      */     }
/*      */     
/*  946 */     for (int i = 1; i < colon; i++) {
/*  947 */       char c = uri.charAt(i);
/*  948 */       if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '+' && c != '-' && c != '.') {
/*  949 */         return 2;
/*      */       }
/*      */     } 
/*  952 */     return 0;
/*      */   }
/*      */   
/*      */   private static boolean isJarPath(String uriPath) {
/*  956 */     return (uriPath.endsWith(".jar") || uriPath.endsWith(".zip"));
/*      */   }
/*      */   
/*      */   private static boolean isJarUrl(URL url) {
/*  960 */     String scheme = url.getProtocol();
/*  961 */     return ("jar".equals(scheme) || "zip".equals(scheme) || "vfszip"
/*  962 */       .equals(scheme) || "wsjar"
/*  963 */       .equals(scheme));
/*      */   }
/*      */ 
/*      */   
/*      */   private static URL createJarEntryUrl(URL jarBaseEntryUrl, String relativeEntryPath) throws MalformedURLException {
/*  968 */     if (relativeEntryPath.startsWith("/")) {
/*  969 */       relativeEntryPath = relativeEntryPath.substring(1);
/*      */     }
/*      */     try {
/*  972 */       return new URL(jarBaseEntryUrl, StringUtil.URLPathEnc(relativeEntryPath, PLATFORM_FILE_ENCODING));
/*  973 */     } catch (UnsupportedEncodingException e) {
/*  974 */       throw new BugException();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String normalizeJarEntryPath(String jarEntryDirPath, boolean directory) {
/*  983 */     if (!jarEntryDirPath.startsWith("/")) {
/*  984 */       jarEntryDirPath = "/" + jarEntryDirPath;
/*      */     }
/*      */ 
/*      */     
/*  988 */     if (directory && !jarEntryDirPath.endsWith("/")) {
/*  989 */       jarEntryDirPath = jarEntryDirPath + "/";
/*      */     }
/*      */     
/*  992 */     return jarEntryDirPath;
/*      */   }
/*      */   
/*      */   private static MalformedURLException newFailedToExtractEntryPathException(URL url) {
/*  996 */     return new MalformedURLException("Failed to extract jar entry path from: " + url);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private File urlToFileOrNull(URL url) {
/*      */     String filePath;
/* 1003 */     if (this.test_emulateNoUrlToFileConversions) {
/* 1004 */       return null;
/*      */     }
/*      */     
/* 1007 */     if (!"file".equals(url.getProtocol())) {
/* 1008 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1014 */       filePath = url.toURI().getSchemeSpecificPart();
/* 1015 */     } catch (URISyntaxException e) {
/*      */ 
/*      */       
/*      */       try {
/* 1019 */         filePath = URLDecoder.decode(url.getFile(), PLATFORM_FILE_ENCODING);
/* 1020 */       } catch (UnsupportedEncodingException e2) {
/* 1021 */         throw new BugException(e2);
/*      */       } 
/*      */     } 
/* 1024 */     return new File(filePath);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JarFile servletContextResourceToFileOrNull(String jarResourcePath) throws MalformedURLException, IOException {
/* 1034 */     URL jarResourceUrl = this.servletContext.getResource(jarResourcePath);
/* 1035 */     if (jarResourceUrl == null) {
/* 1036 */       LOG.error("ServletContext resource URL was null (missing resource?): " + jarResourcePath);
/* 1037 */       return null;
/*      */     } 
/*      */     
/* 1040 */     File jarResourceAsFile = urlToFileOrNull(jarResourceUrl);
/* 1041 */     if (jarResourceAsFile == null)
/*      */     {
/* 1043 */       return null;
/*      */     }
/*      */     
/* 1046 */     if (!jarResourceAsFile.isFile()) {
/* 1047 */       LOG.error("Jar file doesn't exist - falling back to stream mode: " + jarResourceAsFile);
/* 1048 */       return null;
/*      */     } 
/*      */     
/* 1051 */     return new JarFile(jarResourceAsFile);
/*      */   }
/*      */ 
/*      */   
/*      */   private static URL tryCreateServletContextJarEntryUrl(ServletContext servletContext, String servletContextJarFilePath, String entryPath) {
/*      */     try {
/* 1057 */       URL jarFileUrl = servletContext.getResource(servletContextJarFilePath);
/* 1058 */       if (jarFileUrl == null) {
/* 1059 */         throw new IOException("Servlet context resource not found: " + servletContextJarFilePath);
/*      */       }
/* 1061 */       return new URL("jar:" + jarFileUrl
/*      */           
/* 1063 */           .toURI() + "!/" + 
/*      */           
/* 1065 */           URLEncoder.encode(
/* 1066 */             entryPath.startsWith("/") ? entryPath.substring(1) : entryPath, PLATFORM_FILE_ENCODING));
/*      */     }
/* 1068 */     catch (Exception e) {
/* 1069 */       LOG.error("Couldn't get URL for serlvetContext resource " + 
/* 1070 */           StringUtil.jQuoteNoXSS(servletContextJarFilePath) + " / jar entry " + 
/* 1071 */           StringUtil.jQuoteNoXSS(entryPath), e);
/*      */       
/* 1073 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   private static boolean isTldFileNameIgnoreCase(String name) {
/* 1078 */     int dotIdx = name.lastIndexOf('.');
/* 1079 */     if (dotIdx < 0) return false; 
/* 1080 */     String extension = name.substring(dotIdx + 1).toLowerCase();
/* 1081 */     return extension.equalsIgnoreCase("tld");
/*      */   }
/*      */   
/*      */   private static ClassLoader tryGetThreadContextClassLoader() {
/*      */     ClassLoader tccl;
/*      */     try {
/* 1087 */       tccl = Thread.currentThread().getContextClassLoader();
/* 1088 */     } catch (SecurityException e) {
/*      */       
/* 1090 */       tccl = null;
/* 1091 */       LOG.warn("Can't access Thread Context ClassLoader", e);
/*      */     } 
/* 1093 */     return tccl;
/*      */   }
/*      */   
/*      */   private static boolean isDescendantOfOrSameAs(ClassLoader descendant, ClassLoader parent) {
/*      */     while (true) {
/* 1098 */       if (descendant == null) {
/* 1099 */         return false;
/*      */       }
/* 1101 */       if (descendant == parent) {
/* 1102 */         return true;
/*      */       }
/* 1104 */       descendant = descendant.getParent();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class MetaInfTldSource
/*      */   {
/*      */     private MetaInfTldSource() {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class WebInfPerLibJarMetaInfTldSource
/*      */     extends MetaInfTldSource
/*      */   {
/* 1126 */     public static final WebInfPerLibJarMetaInfTldSource INSTANCE = new WebInfPerLibJarMetaInfTldSource();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class ClasspathMetaInfTldSource
/*      */     extends MetaInfTldSource
/*      */   {
/*      */     private final Pattern rootContainerPattern;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ClasspathMetaInfTldSource(Pattern rootContainerPattern) {
/* 1154 */       this.rootContainerPattern = rootContainerPattern;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pattern getRootContainerPattern() {
/* 1161 */       return this.rootContainerPattern;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class ClearMetaInfTldSource
/*      */     extends MetaInfTldSource
/*      */   {
/* 1175 */     public static final ClearMetaInfTldSource INSTANCE = new ClearMetaInfTldSource();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static interface TldLocation
/*      */   {
/*      */     InputStream getInputStream() throws IOException;
/*      */ 
/*      */ 
/*      */     
/*      */     String getXmlSystemId() throws IOException;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static interface InputStreamFactory
/*      */   {
/*      */     InputStream getInputStream();
/*      */   }
/*      */ 
/*      */   
/*      */   private class ServletContextTldLocation
/*      */     implements TldLocation
/*      */   {
/*      */     private final String fileResourcePath;
/*      */ 
/*      */     
/*      */     public ServletContextTldLocation(String fileResourcePath) {
/* 1204 */       this.fileResourcePath = fileResourcePath;
/*      */     }
/*      */ 
/*      */     
/*      */     public InputStream getInputStream() throws IOException {
/* 1209 */       InputStream in = TaglibFactory.this.servletContext.getResourceAsStream(this.fileResourcePath);
/* 1210 */       if (in == null) {
/* 1211 */         throw newResourceNotFoundException();
/*      */       }
/* 1213 */       return in;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getXmlSystemId() throws IOException {
/* 1218 */       URL url = TaglibFactory.this.servletContext.getResource(this.fileResourcePath);
/* 1219 */       return (url != null) ? url.toExternalForm() : null;
/*      */     }
/*      */     
/*      */     private IOException newResourceNotFoundException() {
/* 1223 */       return new IOException("Resource not found: servletContext:" + this.fileResourcePath);
/*      */     }
/*      */ 
/*      */     
/*      */     public final String toString() {
/* 1228 */       return "servletContext:" + this.fileResourcePath;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class ClasspathTldLocation
/*      */     implements TldLocation
/*      */   {
/*      */     private final String resourcePath;
/*      */ 
/*      */ 
/*      */     
/*      */     public ClasspathTldLocation(String resourcePath) {
/* 1242 */       if (!resourcePath.startsWith("/")) {
/* 1243 */         throw new IllegalArgumentException("\"resourcePath\" must start with /");
/*      */       }
/* 1245 */       this.resourcePath = resourcePath;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1250 */       return "classpath:" + this.resourcePath;
/*      */     }
/*      */ 
/*      */     
/*      */     public InputStream getInputStream() throws IOException {
/* 1255 */       ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
/* 1256 */       if (tccl != null) {
/* 1257 */         InputStream ins = ClassUtil.getReasourceAsStream(tccl, this.resourcePath, true);
/* 1258 */         if (ins != null) {
/* 1259 */           return ins;
/*      */         }
/*      */       } 
/*      */       
/* 1263 */       return ClassUtil.getReasourceAsStream(getClass(), this.resourcePath, false);
/*      */     }
/*      */ 
/*      */     
/*      */     public String getXmlSystemId() throws IOException {
/* 1268 */       ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
/* 1269 */       if (tccl != null) {
/* 1270 */         URL uRL = tccl.getResource(this.resourcePath);
/* 1271 */         if (uRL != null) {
/* 1272 */           return uRL.toExternalForm();
/*      */         }
/*      */       } 
/*      */       
/* 1276 */       URL url = getClass().getResource(this.resourcePath);
/* 1277 */       return (url == null) ? null : url.toExternalForm();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private abstract class JarEntryTldLocation
/*      */     implements TldLocation
/*      */   {
/*      */     private final URL entryUrl;
/*      */     
/*      */     private final TaglibFactory.InputStreamFactory fallbackRawJarContentInputStreamFactory;
/*      */     
/*      */     private final String entryPath;
/*      */ 
/*      */     
/*      */     public JarEntryTldLocation(URL entryUrl, TaglibFactory.InputStreamFactory fallbackRawJarContentInputStreamFactory, String entryPath) {
/* 1294 */       if (entryUrl == null) {
/* 1295 */         NullArgumentException.check(fallbackRawJarContentInputStreamFactory);
/* 1296 */         NullArgumentException.check(entryPath);
/*      */       } 
/*      */       
/* 1299 */       this.entryUrl = entryUrl;
/* 1300 */       this.fallbackRawJarContentInputStreamFactory = fallbackRawJarContentInputStreamFactory;
/* 1301 */       this.entryPath = (entryPath != null) ? TaglibFactory.normalizeJarEntryPath(entryPath, false) : null;
/*      */     }
/*      */     
/*      */     public InputStream getInputStream() throws IOException {
/*      */       String entryPath;
/* 1306 */       if (this.entryUrl != null) {
/*      */         try {
/* 1308 */           if (TaglibFactory.this.test_emulateJarEntryUrlOpenStreamFails) {
/* 1309 */             throw new RuntimeException("Test only");
/*      */           }
/* 1311 */           return this.entryUrl.openStream();
/* 1312 */         } catch (Exception e) {
/* 1313 */           if (this.fallbackRawJarContentInputStreamFactory == null) {
/*      */             
/* 1315 */             if (e instanceof IOException) {
/* 1316 */               throw (IOException)e;
/*      */             }
/* 1318 */             if (e instanceof RuntimeException) {
/* 1319 */               throw (RuntimeException)e;
/*      */             }
/* 1321 */             throw new RuntimeException(e);
/*      */           } 
/* 1323 */           TaglibFactory.LOG.error("Failed to open InputStream for URL (will try fallback stream): " + this.entryUrl);
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1329 */       if (this.entryPath != null) {
/* 1330 */         entryPath = this.entryPath;
/*      */       } else {
/* 1332 */         if (this.entryUrl == null) {
/* 1333 */           throw new IOException("Nothing to deduce jar entry path from.");
/*      */         }
/* 1335 */         String urlEF = this.entryUrl.toExternalForm();
/* 1336 */         int sepIdx = urlEF.indexOf("!/");
/* 1337 */         if (sepIdx == -1) {
/* 1338 */           throw new IOException("Couldn't extract jar entry path from: " + urlEF);
/*      */         }
/* 1340 */         entryPath = TaglibFactory.normalizeJarEntryPath(
/* 1341 */             URLDecoder.decode(urlEF
/* 1342 */               .substring(sepIdx + "!/".length()), TaglibFactory
/* 1343 */               .PLATFORM_FILE_ENCODING), false);
/*      */       } 
/*      */ 
/*      */       
/* 1347 */       InputStream rawIn = null;
/* 1348 */       ZipInputStream zipIn = null;
/* 1349 */       boolean returnedZipIn = false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getXmlSystemId() {
/* 1381 */       return (this.entryUrl != null) ? this.entryUrl.toExternalForm() : null;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1386 */       return (this.entryUrl != null) ? this.entryUrl
/* 1387 */         .toExternalForm() : ("jar:{" + this.fallbackRawJarContentInputStreamFactory + "}!" + this.entryPath);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private class JarEntryUrlTldLocation
/*      */     extends JarEntryTldLocation
/*      */   {
/*      */     private JarEntryUrlTldLocation(URL entryUrl, TaglibFactory.InputStreamFactory fallbackRawJarContentInputStreamFactory) {
/* 1396 */       super(entryUrl, fallbackRawJarContentInputStreamFactory, null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class ServletContextJarEntryTldLocation
/*      */     extends JarEntryTldLocation
/*      */   {
/*      */     private ServletContextJarEntryTldLocation(String servletContextJarFilePath, String entryPath) {
/* 1412 */       super(TaglibFactory
/* 1413 */           .tryCreateServletContextJarEntryUrl(TaglibFactory.this.servletContext, servletContextJarFilePath, entryPath), new TaglibFactory.InputStreamFactory(TaglibFactory.this, servletContextJarFilePath)
/*      */           {
/*      */             public InputStream getInputStream()
/*      */             {
/* 1417 */               return TaglibFactory.this.servletContext.getResourceAsStream(servletContextJarFilePath);
/*      */             }
/*      */ 
/*      */             
/*      */             public String toString() {
/* 1422 */               return "servletContext:" + servletContextJarFilePath;
/*      */             }
/*      */           }entryPath);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class FileTldLocation
/*      */     implements TldLocation
/*      */   {
/*      */     private final File file;
/*      */     
/*      */     public FileTldLocation(File file) {
/* 1435 */       this.file = file;
/*      */     }
/*      */ 
/*      */     
/*      */     public InputStream getInputStream() throws IOException {
/* 1440 */       return new FileInputStream(this.file);
/*      */     }
/*      */ 
/*      */     
/*      */     public String getXmlSystemId() throws IOException {
/* 1445 */       return this.file.toURI().toURL().toExternalForm();
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1450 */       return this.file.toString();
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class Taglib
/*      */     implements TemplateHashModel {
/*      */     private final Map tagsAndFunctions;
/*      */     
/*      */     Taglib(ServletContext ctx, TaglibFactory.TldLocation tldPath, ObjectWrapper wrapper) throws IOException, SAXException {
/* 1459 */       this.tagsAndFunctions = parseToTagsAndFunctions(ctx, tldPath, wrapper);
/*      */     }
/*      */ 
/*      */     
/*      */     public TemplateModel get(String key) {
/* 1464 */       return (TemplateModel)this.tagsAndFunctions.get(key);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isEmpty() {
/* 1469 */       return this.tagsAndFunctions.isEmpty();
/*      */     }
/*      */ 
/*      */     
/*      */     private static final Map parseToTagsAndFunctions(ServletContext ctx, TaglibFactory.TldLocation tldLocation, ObjectWrapper objectWrapper) throws IOException, SAXException {
/* 1474 */       TaglibFactory.TldParserForTaglibBuilding tldParser = new TaglibFactory.TldParserForTaglibBuilding(objectWrapper);
/*      */       
/* 1476 */       try (InputStream in = tldLocation.getInputStream()) {
/* 1477 */         TaglibFactory.parseXml(in, tldLocation.getXmlSystemId(), tldParser);
/*      */       } 
/*      */       
/* 1480 */       EventForwarding eventForwarding = EventForwarding.getInstance(ctx);
/* 1481 */       if (eventForwarding != null) {
/* 1482 */         eventForwarding.addListeners(tldParser.getListeners());
/* 1483 */       } else if (tldParser.getListeners().size() > 0) {
/* 1484 */         throw new TaglibFactory.TldParsingSAXException("Event listeners specified in the TLD could not be  registered since the web application doesn't have a listener of class " + EventForwarding.class
/*      */ 
/*      */             
/* 1487 */             .getName() + ". To remedy this, add this element to web.xml:\n| <listener>\n|   <listener-class>" + EventForwarding.class
/*      */ 
/*      */             
/* 1490 */             .getName() + "</listener-class>\n| </listener>", null);
/*      */       } 
/*      */       
/* 1493 */       return tldParser.getTagsAndFunctions();
/*      */     }
/*      */   }
/*      */   
/*      */   private class WebXmlParser extends DefaultHandler {
/*      */     private static final String E_TAGLIB = "taglib";
/*      */     private static final String E_TAGLIB_LOCATION = "taglib-location";
/*      */     private static final String E_TAGLIB_URI = "taglib-uri";
/*      */     private StringBuilder cDataCollector;
/*      */     private String taglibUriCData;
/*      */     private String taglibLocationCData;
/*      */     private Locator locator;
/*      */     
/*      */     private WebXmlParser() {}
/*      */     
/*      */     public void setDocumentLocator(Locator locator) {
/* 1509 */       this.locator = locator;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void startElement(String nsuri, String localName, String qName, Attributes atts) {
/* 1518 */       if ("taglib-uri".equals(qName) || "taglib-location".equals(qName)) {
/* 1519 */         this.cDataCollector = new StringBuilder();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void characters(char[] chars, int off, int len) {
/* 1525 */       if (this.cDataCollector != null) {
/* 1526 */         this.cDataCollector.append(chars, off, len);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void endElement(String nsUri, String localName, String qName) throws TaglibFactory.TldParsingSAXException {
/* 1532 */       if ("taglib-uri".equals(qName)) {
/* 1533 */         this.taglibUriCData = this.cDataCollector.toString().trim();
/* 1534 */         this.cDataCollector = null;
/* 1535 */       } else if ("taglib-location".equals(qName)) {
/* 1536 */         this.taglibLocationCData = this.cDataCollector.toString().trim();
/* 1537 */         if (this.taglibLocationCData.length() == 0) {
/* 1538 */           throw new TaglibFactory.TldParsingSAXException("Required \"taglib-uri\" element was missing or empty", this.locator);
/*      */         }
/*      */         
/*      */         try {
/* 1542 */           if (TaglibFactory.getUriType(this.taglibLocationCData) == 2) {
/* 1543 */             this.taglibLocationCData = "/WEB-INF/" + this.taglibLocationCData;
/*      */           }
/* 1545 */         } catch (MalformedURLException e) {
/* 1546 */           throw new TaglibFactory.TldParsingSAXException("Failed to detect URI type for: " + this.taglibLocationCData, this.locator, e);
/*      */         } 
/* 1548 */         this.cDataCollector = null;
/* 1549 */       } else if ("taglib".equals(qName)) {
/* 1550 */         TaglibFactory.this.addTldLocation(
/* 1551 */             TaglibFactory.isJarPath(this.taglibLocationCData) ? new TaglibFactory.ServletContextJarEntryTldLocation(this.taglibLocationCData, "/META-INF/taglib.tld") : new TaglibFactory.ServletContextTldLocation(this.taglibLocationCData), this.taglibUriCData);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TldParserForTaglibUriExtraction
/*      */     extends DefaultHandler
/*      */   {
/*      */     private static final String E_URI = "uri";
/*      */ 
/*      */     
/*      */     private StringBuilder cDataCollector;
/*      */     
/*      */     private String uri;
/*      */ 
/*      */     
/*      */     String getTaglibUri() {
/* 1570 */       return this.uri;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void startElement(String nsuri, String localName, String qName, Attributes atts) {
/* 1579 */       if ("uri".equals(qName)) {
/* 1580 */         this.cDataCollector = new StringBuilder();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void characters(char[] chars, int off, int len) {
/* 1586 */       if (this.cDataCollector != null) {
/* 1587 */         this.cDataCollector.append(chars, off, len);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void endElement(String nsuri, String localName, String qName) {
/* 1593 */       if ("uri".equals(qName)) {
/* 1594 */         this.uri = this.cDataCollector.toString().trim();
/* 1595 */         this.cDataCollector = null;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static final class TldParserForTaglibBuilding
/*      */     extends DefaultHandler
/*      */   {
/*      */     private static final String E_TAG = "tag";
/*      */     
/*      */     private static final String E_NAME = "name";
/*      */     private static final String E_TAG_CLASS = "tag-class";
/*      */     private static final String E_TAG_CLASS_LEGACY = "tagclass";
/*      */     private static final String E_FUNCTION = "function";
/*      */     private static final String E_FUNCTION_CLASS = "function-class";
/*      */     private static final String E_FUNCTION_SIGNATURE = "function-signature";
/*      */     private static final String E_LISTENER = "listener";
/*      */     private static final String E_LISTENER_CLASS = "listener-class";
/*      */     private final BeansWrapper beansWrapper;
/* 1615 */     private final Map<String, TemplateModel> tagsAndFunctions = new HashMap<>();
/* 1616 */     private final List listeners = new ArrayList();
/*      */     
/*      */     private Locator locator;
/*      */     
/*      */     private StringBuilder cDataCollector;
/* 1621 */     private Stack stack = new Stack();
/*      */     
/*      */     private String tagNameCData;
/*      */     private String tagClassCData;
/*      */     private String functionNameCData;
/*      */     private String functionClassCData;
/*      */     private String functionSignatureCData;
/*      */     private String listenerClassCData;
/*      */     
/*      */     TldParserForTaglibBuilding(ObjectWrapper wrapper) {
/* 1631 */       if (wrapper instanceof BeansWrapper) {
/* 1632 */         this.beansWrapper = (BeansWrapper)wrapper;
/*      */       } else {
/* 1634 */         this.beansWrapper = null;
/* 1635 */         if (TaglibFactory.LOG.isWarnEnabled()) {
/* 1636 */           TaglibFactory.LOG.warn("Custom EL functions won't be loaded because " + ((wrapper == null) ? "no ObjectWrapper was specified for the TaglibFactory (via TaglibFactory.setObjectWrapper(...), exists since 2.3.22)" : ("the ObjectWrapper wasn't instance of " + BeansWrapper.class
/*      */ 
/*      */ 
/*      */               
/* 1640 */               .getName())) + ".");
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     Map<String, TemplateModel> getTagsAndFunctions() {
/* 1647 */       return this.tagsAndFunctions;
/*      */     }
/*      */     
/*      */     List getListeners() {
/* 1651 */       return this.listeners;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setDocumentLocator(Locator locator) {
/* 1656 */       this.locator = locator;
/*      */     }
/*      */ 
/*      */     
/*      */     public void startElement(String nsUri, String localName, String qName, Attributes atts) {
/* 1661 */       this.stack.push(qName);
/* 1662 */       if (this.stack.size() == 3 && (
/* 1663 */         "name".equals(qName) || "tagclass".equals(qName) || "tag-class".equals(qName) || "listener-class"
/* 1664 */         .equals(qName) || "function-class".equals(qName) || "function-signature"
/* 1665 */         .equals(qName))) {
/* 1666 */         this.cDataCollector = new StringBuilder();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void characters(char[] chars, int off, int len) {
/* 1673 */       if (this.cDataCollector != null) {
/* 1674 */         this.cDataCollector.append(chars, off, len);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void endElement(String nsuri, String localName, String qName) throws TaglibFactory.TldParsingSAXException {
/* 1680 */       if (!this.stack.peek().equals(qName)) {
/* 1681 */         throw new TaglibFactory.TldParsingSAXException("Unbalanced tag nesting at \"" + qName + "\" end-tag.", this.locator);
/*      */       }
/*      */       
/* 1684 */       if (this.stack.size() == 3) {
/* 1685 */         if ("name".equals(qName)) {
/* 1686 */           if ("tag".equals(this.stack.get(1))) {
/* 1687 */             this.tagNameCData = pullCData();
/* 1688 */           } else if ("function".equals(this.stack.get(1))) {
/* 1689 */             this.functionNameCData = pullCData();
/*      */           } 
/* 1691 */         } else if ("tagclass".equals(qName) || "tag-class".equals(qName)) {
/* 1692 */           this.tagClassCData = pullCData();
/* 1693 */         } else if ("listener-class".equals(qName)) {
/* 1694 */           this.listenerClassCData = pullCData();
/* 1695 */         } else if ("function-class".equals(qName)) {
/* 1696 */           this.functionClassCData = pullCData();
/* 1697 */         } else if ("function-signature".equals(qName)) {
/* 1698 */           this.functionSignatureCData = pullCData();
/*      */         } 
/* 1700 */       } else if (this.stack.size() == 2) {
/* 1701 */         if ("tag".equals(qName)) {
/* 1702 */           SimpleTagDirectiveModel simpleTagDirectiveModel; checkChildElementNotNull(qName, "name", this.tagNameCData);
/* 1703 */           checkChildElementNotNull(qName, "tag-class", this.tagClassCData);
/*      */           
/* 1705 */           Class<?> tagClass = resoveClassFromTLD(this.tagClassCData, "custom tag", this.tagNameCData);
/*      */ 
/*      */           
/*      */           try {
/* 1709 */             if (Tag.class.isAssignableFrom(tagClass)) {
/* 1710 */               TagTransformModel tagTransformModel = new TagTransformModel(this.tagNameCData, tagClass);
/*      */             } else {
/* 1712 */               simpleTagDirectiveModel = new SimpleTagDirectiveModel(this.tagNameCData, tagClass);
/*      */             } 
/* 1714 */           } catch (IntrospectionException e) {
/* 1715 */             throw new TaglibFactory.TldParsingSAXException("JavaBean introspection failed on custom tag class " + this.tagClassCData, this.locator, e);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1721 */           TemplateModel replacedTagOrFunction = (TemplateModel)this.tagsAndFunctions.put(this.tagNameCData, simpleTagDirectiveModel);
/* 1722 */           if (replacedTagOrFunction != null) {
/* 1723 */             if (CustomTagAndELFunctionCombiner.canBeCombinedAsELFunction(replacedTagOrFunction)) {
/* 1724 */               this.tagsAndFunctions.put(this.tagNameCData, CustomTagAndELFunctionCombiner.combine((TemplateModel)simpleTagDirectiveModel, (TemplateMethodModelEx)replacedTagOrFunction));
/*      */             } else {
/*      */               
/* 1727 */               TaglibFactory.LOG.warn("TLD contains multiple tags with name " + StringUtil.jQuote(this.tagNameCData) + "; keeping only the last one.");
/*      */             } 
/*      */           }
/*      */ 
/*      */           
/* 1732 */           this.tagNameCData = null;
/* 1733 */           this.tagClassCData = null;
/* 1734 */         } else if ("function".equals(qName) && this.beansWrapper != null) {
/* 1735 */           Method functionMethod; TemplateMethodModelEx elFunctionModel; checkChildElementNotNull(qName, "function-class", this.functionClassCData);
/* 1736 */           checkChildElementNotNull(qName, "function-signature", this.functionSignatureCData);
/* 1737 */           checkChildElementNotNull(qName, "name", this.functionNameCData);
/*      */           
/* 1739 */           Class functionClass = resoveClassFromTLD(this.functionClassCData, "custom EL function", this.functionNameCData);
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1744 */             functionMethod = TaglibMethodUtil.getMethodByFunctionSignature(functionClass, this.functionSignatureCData);
/*      */           }
/* 1746 */           catch (Exception e) {
/* 1747 */             throw new TaglibFactory.TldParsingSAXException("Error while trying to resolve signature " + 
/* 1748 */                 StringUtil.jQuote(this.functionSignatureCData) + " on class " + 
/* 1749 */                 StringUtil.jQuote(functionClass.getName()) + " for custom EL function " + 
/* 1750 */                 StringUtil.jQuote(this.functionNameCData) + ".", this.locator, e);
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1755 */           int modifiers = functionMethod.getModifiers();
/* 1756 */           if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
/* 1757 */             throw new TaglibFactory.TldParsingSAXException("The custom EL function method must be public and static: " + functionMethod, this.locator);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1764 */             elFunctionModel = this.beansWrapper.wrap(null, functionMethod);
/* 1765 */           } catch (Exception e) {
/* 1766 */             throw new TaglibFactory.TldParsingSAXException("FreeMarker object wrapping failed on method : " + functionMethod, this.locator);
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1771 */           TemplateModel replacedTagOrFunction = (TemplateModel)this.tagsAndFunctions.put(this.functionNameCData, elFunctionModel);
/* 1772 */           if (replacedTagOrFunction != null) {
/* 1773 */             if (CustomTagAndELFunctionCombiner.canBeCombinedAsCustomTag(replacedTagOrFunction)) {
/* 1774 */               this.tagsAndFunctions.put(this.functionNameCData, CustomTagAndELFunctionCombiner.combine(replacedTagOrFunction, elFunctionModel));
/*      */             } else {
/*      */               
/* 1777 */               TaglibFactory.LOG.warn("TLD contains multiple functions with name " + StringUtil.jQuote(this.functionNameCData) + "; keeping only the last one.");
/*      */             } 
/*      */           }
/*      */ 
/*      */           
/* 1782 */           this.functionNameCData = null;
/* 1783 */           this.functionClassCData = null;
/* 1784 */           this.functionSignatureCData = null;
/* 1785 */         } else if ("listener".equals(qName)) {
/* 1786 */           Object listener; checkChildElementNotNull(qName, "listener-class", this.listenerClassCData);
/*      */           
/* 1788 */           Class listenerClass = resoveClassFromTLD(this.listenerClassCData, "listener", null);
/*      */ 
/*      */           
/*      */           try {
/* 1792 */             listener = listenerClass.newInstance();
/* 1793 */           } catch (Exception e) {
/* 1794 */             throw new TaglibFactory.TldParsingSAXException("Failed to create new instantiate from listener class " + this.listenerClassCData, this.locator, e);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1800 */           this.listeners.add(listener);
/*      */           
/* 1802 */           this.listenerClassCData = null;
/*      */         } 
/*      */       } 
/*      */       
/* 1806 */       this.stack.pop();
/*      */     }
/*      */     
/*      */     private String pullCData() {
/* 1810 */       String r = this.cDataCollector.toString().trim();
/* 1811 */       this.cDataCollector = null;
/* 1812 */       return r;
/*      */     }
/*      */ 
/*      */     
/*      */     private void checkChildElementNotNull(String parentElementName, String childElementName, String value) throws TaglibFactory.TldParsingSAXException {
/* 1817 */       if (value == null) {
/* 1818 */         throw new TaglibFactory.TldParsingSAXException("Missing required \"" + childElementName + "\" element inside the \"" + parentElementName + "\" element.", this.locator);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Class resoveClassFromTLD(String className, String entryType, String entryName) throws TaglibFactory.TldParsingSAXException {
/*      */       try {
/* 1827 */         return ClassUtil.forName(className);
/* 1828 */       } catch (LinkageError|ClassNotFoundException e) {
/* 1829 */         throw newTLDEntryClassLoadingException(e, className, entryType, entryName);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private TaglibFactory.TldParsingSAXException newTLDEntryClassLoadingException(Throwable e, String className, String entryType, String entryName) throws TaglibFactory.TldParsingSAXException {
/* 1836 */       int dotIdx = className.lastIndexOf('.');
/* 1837 */       if (dotIdx != -1) {
/* 1838 */         dotIdx = className.lastIndexOf('.', dotIdx - 1);
/*      */       }
/*      */ 
/*      */       
/* 1842 */       boolean looksLikeNestedClass = (dotIdx != -1 && className.length() > dotIdx + 1 && Character.isUpperCase(className.charAt(dotIdx + 1)));
/* 1843 */       return new TaglibFactory.TldParsingSAXException(((e instanceof ClassNotFoundException) ? "Not found class " : "Can't load class ") + 
/*      */           
/* 1845 */           StringUtil.jQuote(className) + " for " + entryType + ((entryName != null) ? (" " + 
/* 1846 */           StringUtil.jQuote(entryName)) : "") + "." + (looksLikeNestedClass ? " Hint: Before nested classes, use \"$\", not \".\"." : ""), this.locator, e);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class EmptyContentEntityResolver
/*      */     implements EntityResolver
/*      */   {
/*      */     private EmptyContentEntityResolver() {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public InputSource resolveEntity(String publicId, String systemId) {
/* 1863 */       InputSource is = new InputSource(new ByteArrayInputStream(new byte[0]));
/* 1864 */       is.setPublicId(publicId);
/* 1865 */       is.setSystemId(systemId);
/* 1866 */       return is;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class TldParsingSAXException
/*      */     extends SAXParseException
/*      */   {
/*      */     private final Throwable cause;
/*      */ 
/*      */     
/*      */     TldParsingSAXException(String message, Locator locator) {
/* 1879 */       this(message, locator, (Throwable)null);
/*      */     }
/*      */     
/*      */     TldParsingSAXException(String message, Locator locator, Throwable e) {
/* 1883 */       super(message, locator, (e instanceof Exception) ? (Exception)e : new Exception("Unchecked exception; see cause", e));
/*      */       
/* 1885 */       this.cause = e;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1890 */       StringBuilder sb = new StringBuilder(getClass().getName());
/* 1891 */       sb.append(": ");
/* 1892 */       int startLn = sb.length();
/*      */       
/* 1894 */       String systemId = getSystemId();
/* 1895 */       String publicId = getPublicId();
/* 1896 */       if (systemId != null || publicId != null) {
/* 1897 */         sb.append("In ");
/* 1898 */         if (systemId != null) {
/* 1899 */           sb.append(systemId);
/*      */         }
/* 1901 */         if (publicId != null) {
/* 1902 */           if (systemId != null) {
/* 1903 */             sb.append(" (public ID: ");
/*      */           }
/* 1905 */           sb.append(publicId);
/* 1906 */           if (systemId != null) {
/* 1907 */             sb.append(')');
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1912 */       int line = getLineNumber();
/* 1913 */       if (line != -1) {
/* 1914 */         sb.append((sb.length() != startLn) ? ", at " : "At ");
/* 1915 */         sb.append("line ");
/* 1916 */         sb.append(line);
/* 1917 */         int col = getColumnNumber();
/* 1918 */         if (col != -1) {
/* 1919 */           sb.append(", column ");
/* 1920 */           sb.append(col);
/*      */         } 
/*      */       } 
/*      */       
/* 1924 */       String message = getLocalizedMessage();
/* 1925 */       if (message != null) {
/* 1926 */         if (sb.length() != startLn) {
/* 1927 */           sb.append(":\n");
/*      */         }
/* 1929 */         sb.append(message);
/*      */       } 
/*      */       
/* 1932 */       return sb.toString();
/*      */     }
/*      */ 
/*      */     
/*      */     public Throwable getCause() {
/* 1937 */       Throwable superCause = super.getCause();
/* 1938 */       return (superCause == null) ? this.cause : superCause;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class URLWithExternalForm
/*      */     implements Comparable
/*      */   {
/*      */     private final URL url;
/*      */     private final String externalForm;
/*      */     
/*      */     public URLWithExternalForm(URL url) {
/* 1949 */       this.url = url;
/* 1950 */       this.externalForm = url.toExternalForm();
/*      */     }
/*      */     
/*      */     public URL getUrl() {
/* 1954 */       return this.url;
/*      */     }
/*      */     
/*      */     public String getExternalForm() {
/* 1958 */       return this.externalForm;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1963 */       return this.externalForm.hashCode();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object that) {
/* 1968 */       if (this == that) return true; 
/* 1969 */       if (that == null) return false; 
/* 1970 */       if (getClass() != that.getClass()) return false; 
/* 1971 */       return !this.externalForm.equals(((URLWithExternalForm)that).externalForm);
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1976 */       return "URLWithExternalForm(" + this.externalForm + ")";
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(Object that) {
/* 1981 */       return getExternalForm().compareTo(((URLWithExternalForm)that).getExternalForm());
/*      */     }
/*      */   }
/*      */   
/*      */   private static class TaglibGettingException
/*      */     extends Exception
/*      */   {
/*      */     public TaglibGettingException(String message, Throwable cause) {
/* 1989 */       super(message, cause);
/*      */     }
/*      */     
/*      */     public TaglibGettingException(String message) {
/* 1993 */       super(message);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\TaglibFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */