/*      */ package freemarker.ext.servlet;
/*      */ 
/*      */ import freemarker.cache.TemplateLoader;
/*      */ import freemarker.core.Environment;
/*      */ import freemarker.core.ParseException;
/*      */ import freemarker.ext.jsp.TaglibFactory;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.Template;
/*      */ import freemarker.template.TemplateException;
/*      */ import freemarker.template.TemplateExceptionHandler;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.TemplateNotFoundException;
/*      */ import freemarker.template.utility.SecurityUtilities;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import java.io.IOException;
/*      */ import java.nio.charset.Charset;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.servlet.GenericServlet;
/*      */ import javax.servlet.ServletContext;
/*      */ import javax.servlet.ServletException;
/*      */ import javax.servlet.http.HttpServlet;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ import javax.servlet.http.HttpSession;
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
/*      */ 
/*      */ 
/*      */ public class FreemarkerServlet
/*      */   extends HttpServlet
/*      */ {
/*  321 */   private static final Logger LOG = Logger.getLogger("freemarker.servlet");
/*  322 */   private static final Logger LOG_RT = Logger.getLogger("freemarker.runtime");
/*      */ 
/*      */ 
/*      */   
/*      */   public static final long serialVersionUID = -2440216393145762479L;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_TEMPLATE_PATH = "TemplatePath";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_NO_CACHE = "NoCache";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_CONTENT_TYPE = "ContentType";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_OVERRIDE_RESPONSE_CONTENT_TYPE = "OverrideResponseContentType";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_RESPONSE_CHARACTER_ENCODING = "ResponseCharacterEncoding";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_OVERRIDE_RESPONSE_LOCALE = "OverrideResponseLocale";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_BUFFER_SIZE = "BufferSize";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_META_INF_TLD_LOCATIONS = "MetaInfTldSources";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_EXCEPTION_ON_MISSING_TEMPLATE = "ExceptionOnMissingTemplate";
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_CLASSPATH_TLDS = "ClasspathTlds";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String INIT_PARAM_DEBUG = "Debug";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_TEMPLATE_DELAY = "TemplateDelay";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_ENCODING = "DefaultEncoding";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_OBJECT_WRAPPER = "ObjectWrapper";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_WRAPPER_SIMPLE = "simple";
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_WRAPPER_BEANS = "beans";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_WRAPPER_JYTHON = "jython";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER = "TemplateExceptionHandler";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW = "rethrow";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_DEBUG = "debug";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG = "htmlDebug";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE = "ignore";
/*      */ 
/*      */   
/*      */   private static final String DEPR_INITPARAM_DEBUG = "debug";
/*      */ 
/*      */   
/*  414 */   private static final ContentType DEFAULT_CONTENT_TYPE = new ContentType("text/html");
/*      */ 
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_NEVER = "never";
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_ALWAYS = "always";
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_WHEN_TEMPLATE_HAS_MIME_TYPE = "whenTemplateHasMimeType";
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_FROM_TEMPLATE = "fromTemplate";
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_LEGACY = "legacy";
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_DO_NOT_SET = "doNotSet";
/*      */   
/*      */   public static final String INIT_PARAM_VALUE_FORCE_PREFIX = "force ";
/*      */   
/*      */   public static final String SYSTEM_PROPERTY_META_INF_TLD_SOURCES = "org.freemarker.jsp.metaInfTldSources";
/*      */   
/*      */   public static final String SYSTEM_PROPERTY_CLASSPATH_TLDS = "org.freemarker.jsp.classpathTlds";
/*      */   
/*      */   public static final String META_INF_TLD_LOCATION_WEB_INF_PER_LIB_JARS = "webInfPerLibJars";
/*      */   
/*      */   public static final String META_INF_TLD_LOCATION_CLASSPATH = "classpath";
/*      */   
/*      */   public static final String META_INF_TLD_LOCATION_CLEAR = "clear";
/*      */   
/*      */   public static final String KEY_REQUEST = "Request";
/*      */   
/*      */   public static final String KEY_INCLUDE = "include_page";
/*      */   
/*      */   public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
/*      */   
/*      */   public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
/*      */   
/*      */   public static final String KEY_SESSION = "Session";
/*      */   
/*      */   public static final String KEY_APPLICATION = "Application";
/*      */   
/*      */   public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
/*      */   
/*      */   public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
/*      */   
/*      */   private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
/*      */   
/*      */   private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
/*      */   
/*      */   private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
/*      */   
/*      */   @Deprecated
/*      */   private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
/*      */   
/*      */   @Deprecated
/*      */   private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
/*      */   
/*      */   private static final String ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
/*      */   
/*      */   private static final String EXPIRATION_DATE;
/*      */   
/*      */   private String templatePath;
/*      */   
/*      */   private boolean noCache;
/*      */   
/*      */   private Integer bufferSize;
/*      */   
/*      */   private boolean exceptionOnMissingTemplate;
/*      */   
/*      */   @Deprecated
/*      */   protected boolean debug;
/*      */   
/*      */   private Configuration config;
/*      */   
/*      */   private ObjectWrapper wrapper;
/*      */   
/*      */   private ContentType contentType;
/*      */ 
/*      */   
/*      */   static {
/*  492 */     GregorianCalendar expiration = new GregorianCalendar();
/*  493 */     expiration.roll(1, -1);
/*  494 */     SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
/*      */ 
/*      */ 
/*      */     
/*  498 */     EXPIRATION_DATE = httpDate.format(expiration.getTime());
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
/*  519 */   private OverrideResponseContentType overrideResponseContentType = initParamValueToEnum(
/*  520 */       getDefaultOverrideResponseContentType(), OverrideResponseContentType.values());
/*  521 */   private ResponseCharacterEncoding responseCharacterEncoding = ResponseCharacterEncoding.LEGACY;
/*      */   
/*      */   private Charset forcedResponseCharacterEncoding;
/*  524 */   private OverrideResponseLocale overrideResponseLocale = OverrideResponseLocale.ALWAYS;
/*      */   
/*      */   private List metaInfTldSources;
/*      */   private List classpathTlds;
/*  528 */   private Object lazyInitFieldsLock = new Object();
/*      */ 
/*      */ 
/*      */   
/*      */   private ServletContextHashModel servletContextModel;
/*      */ 
/*      */ 
/*      */   
/*      */   private TaglibFactory taglibFactory;
/*      */ 
/*      */   
/*      */   private boolean objectWrapperMismatchWarnLogged;
/*      */ 
/*      */ 
/*      */   
/*      */   public void init() throws ServletException {
/*      */     try {
/*  545 */       initialize();
/*  546 */     } catch (Exception e) {
/*      */ 
/*      */       
/*  549 */       throw new ServletException("Error while initializing " + getClass().getName() + " servlet; see cause exception.", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void initialize() throws InitParamValueException, MalformedWebXmlException, ConflictingInitParamsException {
/*  555 */     this.config = createConfiguration();
/*      */ 
/*      */     
/*  558 */     String iciInitParamValue = getInitParameter("incompatible_improvements");
/*  559 */     if (iciInitParamValue != null) {
/*      */       try {
/*  561 */         this.config.setSetting("incompatible_improvements", iciInitParamValue);
/*  562 */       } catch (Exception e) {
/*  563 */         throw new InitParamValueException("incompatible_improvements", iciInitParamValue, e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  568 */     if (!this.config.isTemplateExceptionHandlerExplicitlySet()) {
/*  569 */       this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
/*      */     }
/*  571 */     if (!this.config.isLogTemplateExceptionsExplicitlySet()) {
/*  572 */       this.config.setLogTemplateExceptions(false);
/*      */     }
/*      */     
/*  575 */     this.contentType = DEFAULT_CONTENT_TYPE;
/*      */ 
/*      */     
/*  578 */     this.wrapper = createObjectWrapper();
/*  579 */     if (LOG.isDebugEnabled()) {
/*  580 */       LOG.debug("Using object wrapper: " + this.wrapper);
/*      */     }
/*  582 */     this.config.setObjectWrapper(this.wrapper);
/*      */ 
/*      */     
/*  585 */     this.templatePath = getInitParameter("TemplatePath");
/*  586 */     if (this.templatePath == null && !this.config.isTemplateLoaderExplicitlySet()) {
/*  587 */       this.templatePath = "class://";
/*      */     }
/*  589 */     if (this.templatePath != null) {
/*      */       try {
/*  591 */         this.config.setTemplateLoader(createTemplateLoader(this.templatePath));
/*  592 */       } catch (Exception e) {
/*  593 */         throw new InitParamValueException("TemplatePath", this.templatePath, e);
/*      */       } 
/*      */     }
/*      */     
/*  597 */     this.metaInfTldSources = createDefaultMetaInfTldSources();
/*  598 */     this.classpathTlds = createDefaultClassPathTlds();
/*      */ 
/*      */     
/*  601 */     Enumeration<String> initpnames = getServletConfig().getInitParameterNames();
/*  602 */     while (initpnames.hasMoreElements()) {
/*  603 */       String name = initpnames.nextElement();
/*  604 */       String value = getInitParameter(name);
/*  605 */       if (name == null) {
/*  606 */         throw new MalformedWebXmlException("init-param without param-name. Maybe the web.xml is not well-formed?");
/*      */       }
/*      */ 
/*      */       
/*  610 */       if (value == null) {
/*  611 */         throw new MalformedWebXmlException("init-param " + 
/*  612 */             StringUtil.jQuote(name) + " without param-value. Maybe the web.xml is not well-formed?");
/*      */       }
/*      */ 
/*      */       
/*      */       try {
/*  617 */         if (name.equals("ObjectWrapper") || name
/*  618 */           .equals("object_wrapper") || name
/*  619 */           .equals("TemplatePath") || name
/*  620 */           .equals("incompatible_improvements"))
/*      */           continue; 
/*  622 */         if (name.equals("DefaultEncoding")) {
/*  623 */           if (getInitParameter("default_encoding") != null) {
/*  624 */             throw new ConflictingInitParamsException("default_encoding", "DefaultEncoding");
/*      */           }
/*      */           
/*  627 */           this.config.setDefaultEncoding(value); continue;
/*  628 */         }  if (name.equals("TemplateDelay")) {
/*  629 */           if (getInitParameter("template_update_delay") != null) {
/*  630 */             throw new ConflictingInitParamsException("template_update_delay", "TemplateDelay");
/*      */           }
/*      */           
/*      */           try {
/*  634 */             this.config.setTemplateUpdateDelay(Integer.parseInt(value));
/*  635 */           } catch (NumberFormatException numberFormatException) {}
/*      */           continue;
/*      */         } 
/*  638 */         if (name.equals("TemplateExceptionHandler")) {
/*  639 */           if (getInitParameter("template_exception_handler") != null) {
/*  640 */             throw new ConflictingInitParamsException("template_exception_handler", "TemplateExceptionHandler");
/*      */           }
/*      */ 
/*      */           
/*  644 */           if ("rethrow".equals(value)) {
/*  645 */             this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER); continue;
/*  646 */           }  if ("debug".equals(value)) {
/*  647 */             this.config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER); continue;
/*  648 */           }  if ("htmlDebug".equals(value)) {
/*  649 */             this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER); continue;
/*  650 */           }  if ("ignore".equals(value)) {
/*  651 */             this.config.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER); continue;
/*      */           } 
/*  653 */           throw new InitParamValueException("TemplateExceptionHandler", value, "Not one of the supported values.");
/*      */         } 
/*      */         
/*  656 */         if (name.equals("NoCache")) {
/*  657 */           this.noCache = StringUtil.getYesNo(value); continue;
/*  658 */         }  if (name.equals("BufferSize")) {
/*  659 */           this.bufferSize = Integer.valueOf(parseSize(value)); continue;
/*  660 */         }  if (name.equals("debug")) {
/*  661 */           if (getInitParameter("Debug") != null) {
/*  662 */             throw new ConflictingInitParamsException("Debug", "debug");
/*      */           }
/*  664 */           this.debug = StringUtil.getYesNo(value); continue;
/*  665 */         }  if (name.equals("Debug")) {
/*  666 */           this.debug = StringUtil.getYesNo(value); continue;
/*  667 */         }  if (name.equals("ContentType")) {
/*  668 */           this.contentType = new ContentType(value); continue;
/*  669 */         }  if (name.equals("OverrideResponseContentType")) {
/*  670 */           this.overrideResponseContentType = initParamValueToEnum(value, OverrideResponseContentType.values()); continue;
/*  671 */         }  if (name.equals("ResponseCharacterEncoding")) {
/*  672 */           this.responseCharacterEncoding = initParamValueToEnum(value, ResponseCharacterEncoding.values());
/*  673 */           if (this.responseCharacterEncoding == ResponseCharacterEncoding.FORCE_CHARSET) {
/*  674 */             String charsetName = value.substring("force ".length()).trim();
/*  675 */             this.forcedResponseCharacterEncoding = Charset.forName(charsetName);
/*      */           }  continue;
/*  677 */         }  if (name.equals("OverrideResponseLocale")) {
/*  678 */           this.overrideResponseLocale = initParamValueToEnum(value, OverrideResponseLocale.values()); continue;
/*  679 */         }  if (name.equals("ExceptionOnMissingTemplate")) {
/*  680 */           this.exceptionOnMissingTemplate = StringUtil.getYesNo(value); continue;
/*  681 */         }  if (name.equals("MetaInfTldSources")) {
/*  682 */           this.metaInfTldSources = parseAsMetaInfTldLocations(value); continue;
/*  683 */         }  if (name.equals("ClasspathTlds")) {
/*  684 */           List newClasspathTlds = new ArrayList();
/*  685 */           if (this.classpathTlds != null) {
/*  686 */             newClasspathTlds.addAll(this.classpathTlds);
/*      */           }
/*  688 */           newClasspathTlds.addAll(InitParamParser.parseCommaSeparatedList(value));
/*  689 */           this.classpathTlds = newClasspathTlds; continue;
/*      */         } 
/*  691 */         this.config.setSetting(name, value);
/*      */       }
/*  693 */       catch (ConflictingInitParamsException e) {
/*  694 */         throw e;
/*  695 */       } catch (Exception e) {
/*  696 */         throw new InitParamValueException(name, value, e);
/*      */       } 
/*      */     } 
/*      */     
/*  700 */     if (this.contentType.containsCharset && this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY) {
/*  701 */       throw new InitParamValueException("ContentType", this.contentType.httpHeaderValue, new IllegalStateException("You can't specify the charset in the content type, because the \"ResponseCharacterEncoding\" init-param isn't set to \"legacy\"."));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List parseAsMetaInfTldLocations(String value) throws ParseException {
/*  709 */     List<TaglibFactory.ClearMetaInfTldSource> metaInfTldSources = null;
/*      */     
/*  711 */     List values = InitParamParser.parseCommaSeparatedList(value);
/*  712 */     for (Iterator<String> it = values.iterator(); it.hasNext(); ) {
/*  713 */       TaglibFactory.ClearMetaInfTldSource clearMetaInfTldSource; String itemStr = it.next();
/*      */       
/*  715 */       if (itemStr.equals("webInfPerLibJars")) {
/*  716 */         TaglibFactory.WebInfPerLibJarMetaInfTldSource webInfPerLibJarMetaInfTldSource = TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE;
/*  717 */       } else if (itemStr.startsWith("classpath")) {
/*  718 */         String itemRightSide = itemStr.substring("classpath".length()).trim();
/*  719 */         if (itemRightSide.length() == 0) {
/*  720 */           TaglibFactory.ClasspathMetaInfTldSource classpathMetaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(".*", 32));
/*  721 */         } else if (itemRightSide.startsWith(":")) {
/*  722 */           String regexpStr = itemRightSide.substring(1).trim();
/*  723 */           if (regexpStr.length() == 0) {
/*  724 */             throw new ParseException("Empty regular expression after \"classpath:\"", -1);
/*      */           }
/*      */           
/*  727 */           TaglibFactory.ClasspathMetaInfTldSource classpathMetaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(regexpStr));
/*      */         } else {
/*  729 */           throw new ParseException("Invalid \"classpath\" value syntax: " + value, -1);
/*      */         }
/*      */       
/*  732 */       } else if (itemStr.startsWith("clear")) {
/*  733 */         clearMetaInfTldSource = TaglibFactory.ClearMetaInfTldSource.INSTANCE;
/*      */       } else {
/*  735 */         throw new ParseException("Item has no recognized source type prefix: " + itemStr, -1);
/*      */       } 
/*  737 */       if (metaInfTldSources == null) {
/*  738 */         metaInfTldSources = new ArrayList();
/*      */       }
/*  740 */       metaInfTldSources.add(clearMetaInfTldSource);
/*      */     } 
/*      */     
/*  743 */     return metaInfTldSources;
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
/*      */   protected TemplateLoader createTemplateLoader(String templatePath) throws IOException {
/*  758 */     return InitParamParser.createTemplateLoader(templatePath, getConfiguration(), getClass(), getServletContext());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*  764 */     process(request, response);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*  772 */     process(request, response);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*      */     Template template;
/*  780 */     if (preprocessRequest(request, response)) {
/*      */       return;
/*      */     }
/*      */     
/*  784 */     if (this.bufferSize != null && !response.isCommitted()) {
/*      */       try {
/*  786 */         response.setBufferSize(this.bufferSize.intValue());
/*  787 */       } catch (IllegalStateException e) {
/*  788 */         LOG.debug("Can't set buffer size any more,", e);
/*      */       } 
/*      */     }
/*      */     
/*  792 */     String templatePath = requestUrlToTemplatePath(request);
/*      */     
/*  794 */     if (LOG.isDebugEnabled()) {
/*  795 */       LOG.debug("Requested template " + StringUtil.jQuoteNoXSS(templatePath) + ".");
/*      */     }
/*      */     
/*  798 */     Locale locale = request.getLocale();
/*  799 */     if (locale == null || this.overrideResponseLocale != OverrideResponseLocale.NEVER) {
/*  800 */       locale = deduceLocale(templatePath, request, response);
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/*  805 */       template = this.config.getTemplate(templatePath, locale);
/*  806 */     } catch (TemplateNotFoundException e) {
/*  807 */       if (this.exceptionOnMissingTemplate) {
/*  808 */         throw newServletExceptionWithFreeMarkerLogging("Template not found for name " + 
/*  809 */             StringUtil.jQuoteNoXSS(templatePath) + ".", e);
/*      */       }
/*  811 */       if (LOG.isDebugEnabled()) {
/*  812 */         LOG.debug("Responding HTTP 404 \"Not found\" for missing template " + 
/*  813 */             StringUtil.jQuoteNoXSS(templatePath) + ".", (Throwable)e);
/*      */       }
/*  815 */       response.sendError(404, "Page template not found");
/*      */       
/*      */       return;
/*  818 */     } catch (ParseException e) {
/*  819 */       throw newServletExceptionWithFreeMarkerLogging("Parsing error with template " + 
/*  820 */           StringUtil.jQuoteNoXSS(templatePath) + ".", e);
/*  821 */     } catch (Exception e) {
/*  822 */       throw newServletExceptionWithFreeMarkerLogging("Unexpected error when loading template " + 
/*  823 */           StringUtil.jQuoteNoXSS(templatePath) + ".", e);
/*      */     } 
/*      */     
/*  826 */     boolean tempSpecContentTypeContainsCharset = false;
/*  827 */     if (response.getContentType() == null || this.overrideResponseContentType != OverrideResponseContentType.NEVER) {
/*  828 */       ContentType templateSpecificContentType = getTemplateSpecificContentType(template);
/*  829 */       if (templateSpecificContentType != null) {
/*      */         
/*  831 */         response.setContentType((this.responseCharacterEncoding != ResponseCharacterEncoding.DO_NOT_SET) ? templateSpecificContentType
/*      */             
/*  833 */             .httpHeaderValue : templateSpecificContentType
/*  834 */             .getMimeType());
/*  835 */         tempSpecContentTypeContainsCharset = templateSpecificContentType.containsCharset;
/*  836 */       } else if (response.getContentType() == null || this.overrideResponseContentType == OverrideResponseContentType.ALWAYS) {
/*      */         
/*  838 */         if (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY && !this.contentType.containsCharset) {
/*      */           
/*  840 */           response.setContentType(this.contentType
/*  841 */               .httpHeaderValue + "; charset=" + getTemplateSpecificOutputEncoding(template));
/*      */         } else {
/*  843 */           response.setContentType(this.contentType.httpHeaderValue);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  848 */     if (this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY && this.responseCharacterEncoding != ResponseCharacterEncoding.DO_NOT_SET)
/*      */     {
/*      */       
/*  851 */       if (this.responseCharacterEncoding != ResponseCharacterEncoding.FORCE_CHARSET) {
/*  852 */         if (!tempSpecContentTypeContainsCharset) {
/*  853 */           response.setCharacterEncoding(getTemplateSpecificOutputEncoding(template));
/*      */         }
/*      */       } else {
/*  856 */         response.setCharacterEncoding(this.forcedResponseCharacterEncoding.name());
/*      */       } 
/*      */     }
/*      */     
/*  860 */     setBrowserCachingPolicy(response);
/*      */     
/*  862 */     ServletContext servletContext = getServletContext();
/*      */     try {
/*  864 */       logWarnOnObjectWrapperMismatch();
/*      */       
/*  866 */       TemplateModel model = createModel(this.wrapper, servletContext, request, response);
/*      */ 
/*      */       
/*  869 */       if (preTemplateProcess(request, response, template, model)) {
/*      */         
/*      */         try {
/*  872 */           Environment env = template.createProcessingEnvironment(model, response.getWriter());
/*  873 */           if (this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY) {
/*  874 */             String actualOutputCharset = response.getCharacterEncoding();
/*  875 */             if (actualOutputCharset != null) {
/*  876 */               env.setOutputEncoding(actualOutputCharset);
/*      */             }
/*      */           } 
/*  879 */           processEnvironment(env, request, response);
/*      */         } finally {
/*      */           
/*  882 */           postTemplateProcess(request, response, template, model);
/*      */         } 
/*      */       }
/*  885 */     } catch (TemplateException e) {
/*  886 */       TemplateExceptionHandler teh = this.config.getTemplateExceptionHandler();
/*      */       
/*  888 */       if (teh == TemplateExceptionHandler.HTML_DEBUG_HANDLER || teh == TemplateExceptionHandler.DEBUG_HANDLER || teh
/*  889 */         .getClass().getName().indexOf("Debug") != -1) {
/*  890 */         response.flushBuffer();
/*      */       }
/*  892 */       throw newServletExceptionWithFreeMarkerLogging("Error executing FreeMarker template", e);
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
/*      */   protected void processEnvironment(Environment env, HttpServletRequest request, HttpServletResponse response) throws TemplateException, IOException {
/*  910 */     env.process();
/*      */   }
/*      */ 
/*      */   
/*      */   private String getTemplateSpecificOutputEncoding(Template template) {
/*  915 */     String outputEncoding = (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY) ? null : template.getOutputEncoding();
/*  916 */     return (outputEncoding != null) ? outputEncoding : template.getEncoding();
/*      */   }
/*      */   
/*      */   private ContentType getTemplateSpecificContentType(Template template) {
/*  920 */     Object contentTypeAttr = template.getCustomAttribute("content_type");
/*  921 */     if (contentTypeAttr != null)
/*      */     {
/*  923 */       return new ContentType(contentTypeAttr.toString());
/*      */     }
/*      */     
/*  926 */     String outputFormatMimeType = template.getOutputFormat().getMimeType();
/*  927 */     if (outputFormatMimeType != null) {
/*  928 */       if (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY)
/*      */       {
/*  930 */         return new ContentType(outputFormatMimeType + "; charset=" + getTemplateSpecificOutputEncoding(template), true);
/*      */       }
/*  932 */       return new ContentType(outputFormatMimeType, false);
/*      */     } 
/*      */ 
/*      */     
/*  936 */     return null;
/*      */   }
/*      */   
/*      */   private ServletException newServletExceptionWithFreeMarkerLogging(String message, Throwable cause) throws ServletException {
/*  940 */     if (cause instanceof TemplateException) {
/*      */ 
/*      */       
/*  943 */       LOG_RT.error(message, cause);
/*      */     } else {
/*  945 */       LOG.error(message, cause);
/*      */     } 
/*      */     
/*  948 */     ServletException e = new ServletException(message, cause);
/*      */ 
/*      */     
/*      */     try {
/*  952 */       e.initCause(cause);
/*  953 */     } catch (Exception exception) {}
/*      */ 
/*      */     
/*  956 */     throw e;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void logWarnOnObjectWrapperMismatch() {
/*  962 */     if (this.wrapper != this.config.getObjectWrapper() && !this.objectWrapperMismatchWarnLogged && LOG.isWarnEnabled()) {
/*      */       boolean logWarn;
/*  964 */       synchronized (this) {
/*  965 */         logWarn = !this.objectWrapperMismatchWarnLogged;
/*  966 */         if (logWarn) {
/*  967 */           this.objectWrapperMismatchWarnLogged = true;
/*      */         }
/*      */       } 
/*  970 */       if (logWarn) {
/*  971 */         LOG.warn(
/*  972 */             getClass().getName() + ".wrapper != config.getObjectWrapper(); possibly the result of incorrect extension of " + FreemarkerServlet.class
/*      */             
/*  974 */             .getName() + ".");
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
/*      */   protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) throws ServletException {
/*  994 */     return this.config.getLocale();
/*      */   }
/*      */   
/*      */   protected TemplateModel createModel(ObjectWrapper objectWrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
/*      */     try {
/*      */       ServletContextHashModel servletContextModel;
/*      */       TaglibFactory taglibFactory;
/*      */       HttpSessionHashModel sessionModel;
/* 1002 */       AllHttpScopesHashModel params = new AllHttpScopesHashModel(objectWrapper, servletContext, request);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1007 */       synchronized (this.lazyInitFieldsLock) {
/* 1008 */         if (this.servletContextModel == null) {
/* 1009 */           servletContextModel = new ServletContextHashModel((GenericServlet)this, objectWrapper);
/* 1010 */           taglibFactory = createTaglibFactory(objectWrapper, servletContext);
/*      */ 
/*      */           
/* 1013 */           servletContext.setAttribute(".freemarker.Application", servletContextModel);
/* 1014 */           servletContext.setAttribute(".freemarker.JspTaglibs", taglibFactory);
/*      */           
/* 1016 */           initializeServletContext(request, response);
/*      */           
/* 1018 */           this.taglibFactory = taglibFactory;
/* 1019 */           this.servletContextModel = servletContextModel;
/*      */         } else {
/* 1021 */           servletContextModel = this.servletContextModel;
/* 1022 */           taglibFactory = this.taglibFactory;
/*      */         } 
/*      */       } 
/*      */       
/* 1026 */       params.putUnlistedModel("Application", (TemplateModel)servletContextModel);
/* 1027 */       params.putUnlistedModel("__FreeMarkerServlet.Application__", (TemplateModel)servletContextModel);
/* 1028 */       params.putUnlistedModel("JspTaglibs", (TemplateModel)taglibFactory);
/*      */ 
/*      */       
/* 1031 */       HttpSession session = request.getSession(false);
/* 1032 */       if (session != null) {
/* 1033 */         sessionModel = (HttpSessionHashModel)session.getAttribute(".freemarker.Session");
/* 1034 */         if (sessionModel == null || sessionModel.isOrphaned(session)) {
/* 1035 */           sessionModel = new HttpSessionHashModel(session, objectWrapper);
/* 1036 */           initializeSessionAndInstallModel(request, response, sessionModel, session);
/*      */         } 
/*      */       } else {
/*      */         
/* 1040 */         sessionModel = new HttpSessionHashModel(this, request, response, objectWrapper);
/*      */       } 
/* 1042 */       params.putUnlistedModel("Session", (TemplateModel)sessionModel);
/*      */ 
/*      */ 
/*      */       
/* 1046 */       HttpRequestHashModel requestModel = (HttpRequestHashModel)request.getAttribute(".freemarker.Request");
/* 1047 */       if (requestModel == null || requestModel.getRequest() != request) {
/* 1048 */         requestModel = new HttpRequestHashModel(request, response, objectWrapper);
/* 1049 */         request.setAttribute(".freemarker.Request", requestModel);
/* 1050 */         request.setAttribute(".freemarker.RequestParameters", 
/*      */             
/* 1052 */             createRequestParametersHashModel(request));
/*      */       } 
/* 1054 */       params.putUnlistedModel("Request", (TemplateModel)requestModel);
/* 1055 */       params.putUnlistedModel("include_page", (TemplateModel)new IncludePage(request, response));
/* 1056 */       params.putUnlistedModel("__FreeMarkerServlet.Request__", (TemplateModel)requestModel);
/*      */ 
/*      */ 
/*      */       
/* 1060 */       HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel)request.getAttribute(".freemarker.RequestParameters");
/*      */       
/* 1062 */       params.putUnlistedModel("RequestParameters", (TemplateModel)requestParametersModel);
/* 1063 */       return (TemplateModel)params;
/* 1064 */     } catch (ServletException|IOException e) {
/* 1065 */       throw new TemplateModelException(e);
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
/*      */   protected TaglibFactory createTaglibFactory(ObjectWrapper objectWrapper, ServletContext servletContext) throws TemplateModelException {
/* 1078 */     TaglibFactory taglibFactory = new TaglibFactory(servletContext);
/*      */     
/* 1080 */     taglibFactory.setObjectWrapper(objectWrapper);
/*      */ 
/*      */     
/* 1083 */     List<TaglibFactory.ClasspathMetaInfTldSource> mergedMetaInfTldSources = new ArrayList();
/*      */     
/* 1085 */     if (this.metaInfTldSources != null) {
/* 1086 */       mergedMetaInfTldSources.addAll(this.metaInfTldSources);
/*      */     }
/*      */     
/* 1089 */     String sysPropVal = SecurityUtilities.getSystemProperty("org.freemarker.jsp.metaInfTldSources", null);
/* 1090 */     if (sysPropVal != null) {
/*      */       try {
/* 1092 */         List metaInfTldSourcesSysProp = parseAsMetaInfTldLocations(sysPropVal);
/* 1093 */         if (metaInfTldSourcesSysProp != null) {
/* 1094 */           mergedMetaInfTldSources.addAll(metaInfTldSourcesSysProp);
/*      */         }
/* 1096 */       } catch (ParseException e) {
/* 1097 */         throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.metaInfTldSources\"", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1102 */     List jettyTaglibJarPatterns = null;
/*      */     try {
/* 1104 */       String attrVal = (String)servletContext.getAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern");
/* 1105 */       jettyTaglibJarPatterns = (attrVal != null) ? InitParamParser.parseCommaSeparatedPatterns(attrVal) : null;
/* 1106 */     } catch (Exception e) {
/* 1107 */       LOG.error("Failed to parse application context attribute \"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern\" - it will be ignored", e);
/*      */     } 
/*      */     
/* 1110 */     if (jettyTaglibJarPatterns != null) {
/* 1111 */       for (Iterator<Pattern> it = jettyTaglibJarPatterns.iterator(); it.hasNext(); ) {
/* 1112 */         Pattern pattern = it.next();
/* 1113 */         mergedMetaInfTldSources.add(new TaglibFactory.ClasspathMetaInfTldSource(pattern));
/*      */       } 
/*      */     }
/*      */     
/* 1117 */     taglibFactory.setMetaInfTldSources(mergedMetaInfTldSources);
/*      */ 
/*      */ 
/*      */     
/* 1121 */     List mergedClassPathTlds = new ArrayList();
/* 1122 */     if (this.classpathTlds != null) {
/* 1123 */       mergedClassPathTlds.addAll(this.classpathTlds);
/*      */     }
/*      */     
/* 1126 */     sysPropVal = SecurityUtilities.getSystemProperty("org.freemarker.jsp.classpathTlds", null);
/* 1127 */     if (sysPropVal != null) {
/*      */       try {
/* 1129 */         List classpathTldsSysProp = InitParamParser.parseCommaSeparatedList(sysPropVal);
/* 1130 */         if (classpathTldsSysProp != null) {
/* 1131 */           mergedClassPathTlds.addAll(classpathTldsSysProp);
/*      */         }
/* 1133 */       } catch (ParseException e) {
/* 1134 */         throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.classpathTlds\"", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1139 */     taglibFactory.setClasspathTlds(mergedClassPathTlds);
/*      */ 
/*      */     
/* 1142 */     return taglibFactory;
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
/*      */   protected List createDefaultClassPathTlds() {
/* 1157 */     return TaglibFactory.DEFAULT_CLASSPATH_TLDS;
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
/*      */   protected List createDefaultMetaInfTldSources() {
/* 1172 */     return TaglibFactory.DEFAULT_META_INF_TLD_SOURCES;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void initializeSessionAndInstallModel(HttpServletRequest request, HttpServletResponse response, HttpSessionHashModel sessionModel, HttpSession session) throws ServletException, IOException {
/* 1179 */     session.setAttribute(".freemarker.Session", sessionModel);
/* 1180 */     initializeSession(request, response);
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
/*      */   protected String requestUrlToTemplatePath(HttpServletRequest request) throws ServletException {
/* 1201 */     String includeServletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
/* 1202 */     if (includeServletPath != null) {
/*      */ 
/*      */       
/* 1205 */       String includePathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
/* 1206 */       return (includePathInfo == null) ? includeServletPath : includePathInfo;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1211 */     String path = request.getPathInfo();
/* 1212 */     if (path != null) return path; 
/* 1213 */     path = request.getServletPath();
/* 1214 */     if (path != null) return path;
/*      */     
/* 1216 */     return "";
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
/*      */   protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 1233 */     return false;
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
/*      */   protected Configuration createConfiguration() {
/* 1252 */     return new Configuration();
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
/*      */   protected void setConfigurationDefaults() {}
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
/*      */   protected ObjectWrapper createObjectWrapper() {
/* 1281 */     String wrapper = getServletConfig().getInitParameter("ObjectWrapper");
/* 1282 */     if (wrapper != null) {
/* 1283 */       if (getInitParameter("object_wrapper") != null) {
/* 1284 */         throw new RuntimeException("Conflicting init-params: object_wrapper and ObjectWrapper");
/*      */       }
/*      */ 
/*      */       
/* 1288 */       if ("beans".equals(wrapper)) {
/* 1289 */         return ObjectWrapper.BEANS_WRAPPER;
/*      */       }
/* 1291 */       if ("simple".equals(wrapper)) {
/* 1292 */         return ObjectWrapper.SIMPLE_WRAPPER;
/*      */       }
/* 1294 */       if ("jython".equals(wrapper)) {
/*      */         
/*      */         try {
/* 1297 */           return (ObjectWrapper)Class.forName("freemarker.ext.jython.JythonWrapper")
/* 1298 */             .newInstance();
/* 1299 */         } catch (InstantiationException e) {
/* 1300 */           throw new InstantiationError(e.getMessage());
/* 1301 */         } catch (IllegalAccessException e) {
/* 1302 */           throw new IllegalAccessError(e.getMessage());
/* 1303 */         } catch (ClassNotFoundException e) {
/* 1304 */           throw new NoClassDefFoundError(e.getMessage());
/*      */         } 
/*      */       }
/* 1307 */       return createDefaultObjectWrapper();
/*      */     } 
/* 1309 */     wrapper = getInitParameter("object_wrapper");
/* 1310 */     if (wrapper == null) {
/* 1311 */       if (!this.config.isObjectWrapperExplicitlySet()) {
/* 1312 */         return createDefaultObjectWrapper();
/*      */       }
/* 1314 */       return this.config.getObjectWrapper();
/*      */     } 
/*      */     
/*      */     try {
/* 1318 */       this.config.setSetting("object_wrapper", wrapper);
/* 1319 */     } catch (TemplateException e) {
/* 1320 */       throw new RuntimeException("Failed to set object_wrapper", e);
/*      */     } 
/* 1322 */     return this.config.getObjectWrapper();
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
/*      */   protected ObjectWrapper createDefaultObjectWrapper() {
/* 1342 */     return Configuration.getDefaultObjectWrapper(this.config.getIncompatibleImprovements());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWrapper getObjectWrapper() {
/* 1350 */     return this.wrapper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected final String getTemplatePath() {
/* 1361 */     return this.templatePath;
/*      */   }
/*      */   
/*      */   protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
/* 1365 */     return new HttpRequestParametersHashModel(request);
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
/*      */   protected void initializeServletContext(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
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
/*      */   protected void initializeSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
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
/*      */   protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel model) throws ServletException, IOException {
/* 1429 */     return true;
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
/*      */   protected void postTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {}
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
/*      */   protected Configuration getConfiguration() {
/* 1456 */     return this.config;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getDefaultOverrideResponseContentType() {
/* 1467 */     return "always";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setBrowserCachingPolicy(HttpServletResponse res) {
/* 1475 */     if (this.noCache) {
/*      */       
/* 1477 */       res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
/*      */ 
/*      */       
/* 1480 */       res.setHeader("Pragma", "no-cache");
/*      */       
/* 1482 */       res.setHeader("Expires", EXPIRATION_DATE);
/*      */     } 
/*      */   }
/*      */   
/*      */   private int parseSize(String value) throws ParseException {
/*      */     int unit, lastDigitIdx;
/* 1488 */     for (lastDigitIdx = value.length() - 1; lastDigitIdx >= 0; lastDigitIdx--) {
/* 1489 */       char c = value.charAt(lastDigitIdx);
/* 1490 */       if (c >= '0' && c <= '9') {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/* 1495 */     int n = Integer.parseInt(value.substring(0, lastDigitIdx + 1).trim());
/*      */     
/* 1497 */     String unitStr = value.substring(lastDigitIdx + 1).trim().toUpperCase();
/*      */     
/* 1499 */     if (unitStr.length() == 0 || unitStr.equals("B")) {
/* 1500 */       unit = 1;
/* 1501 */     } else if (unitStr.equals("K") || unitStr.equals("KB") || unitStr.equals("KIB")) {
/* 1502 */       unit = 1024;
/* 1503 */     } else if (unitStr.equals("M") || unitStr.equals("MB") || unitStr.equals("MIB")) {
/* 1504 */       unit = 1048576;
/*      */     } else {
/* 1506 */       throw new ParseException("Unknown unit: " + unitStr, lastDigitIdx + 1);
/*      */     } 
/*      */     
/* 1509 */     long size = n * unit;
/* 1510 */     if (size < 0L) {
/* 1511 */       throw new IllegalArgumentException("Buffer size can't be negative");
/*      */     }
/* 1513 */     if (size > 2147483647L) {
/* 1514 */       throw new IllegalArgumentException("Buffer size can't bigger than 2147483647");
/*      */     }
/* 1516 */     return (int)size;
/*      */   }
/*      */   
/*      */   private static class InitParamValueException
/*      */     extends Exception {
/*      */     InitParamValueException(String initParamName, String initParamValue, Throwable casue) {
/* 1522 */       super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + 
/* 1523 */           StringUtil.jQuote(initParamValue) + "; see cause exception.", casue);
/*      */     }
/*      */ 
/*      */     
/*      */     public InitParamValueException(String initParamName, String initParamValue, String cause) {
/* 1528 */       super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + 
/* 1529 */           StringUtil.jQuote(initParamValue) + ": " + cause);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ConflictingInitParamsException
/*      */     extends Exception
/*      */   {
/*      */     ConflictingInitParamsException(String recommendedName, String otherName) {
/* 1537 */       super("Conflicting servlet init-params: " + 
/* 1538 */           StringUtil.jQuote(recommendedName) + " and " + StringUtil.jQuote(otherName) + ". Only use " + 
/* 1539 */           StringUtil.jQuote(recommendedName) + ".");
/*      */     }
/*      */   }
/*      */   
/*      */   private static class MalformedWebXmlException
/*      */     extends Exception {
/*      */     MalformedWebXmlException(String message) {
/* 1546 */       super(message);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ContentType
/*      */   {
/*      */     private final String httpHeaderValue;
/*      */     private final boolean containsCharset;
/*      */     
/*      */     public ContentType(String httpHeaderValue) {
/* 1556 */       this(httpHeaderValue, contentTypeContainsCharset(httpHeaderValue));
/*      */     }
/*      */     
/*      */     public ContentType(String httpHeaderValue, boolean containsCharset) {
/* 1560 */       this.httpHeaderValue = httpHeaderValue;
/* 1561 */       this.containsCharset = containsCharset;
/*      */     }
/*      */     
/*      */     private static boolean contentTypeContainsCharset(String contentType) {
/* 1565 */       int charsetIdx = contentType.toLowerCase().indexOf("charset=");
/* 1566 */       if (charsetIdx != -1) {
/* 1567 */         char c = Character.MIN_VALUE;
/* 1568 */         charsetIdx--;
/* 1569 */         while (charsetIdx >= 0) {
/* 1570 */           c = contentType.charAt(charsetIdx);
/* 1571 */           if (!Character.isWhitespace(c))
/* 1572 */             break;  charsetIdx--;
/*      */         } 
/* 1574 */         if (charsetIdx == -1 || c == ';') {
/* 1575 */           return true;
/*      */         }
/*      */       } 
/* 1578 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private String getMimeType() {
/* 1585 */       int scIdx = this.httpHeaderValue.indexOf(';');
/* 1586 */       return ((scIdx == -1) ? this.httpHeaderValue : this.httpHeaderValue.substring(0, scIdx)).trim();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private <T extends InitParamValueEnum> T initParamValueToEnum(String initParamValue, T[] enumValues) {
/* 1592 */     for (T enumValue : enumValues) {
/* 1593 */       String enumInitParamValue = enumValue.getInitParamValue();
/* 1594 */       if (initParamValue.equals(enumInitParamValue) || (enumInitParamValue
/* 1595 */         .endsWith("}") && initParamValue.startsWith(enumInitParamValue
/* 1596 */           .substring(0, enumInitParamValue.indexOf("${"))))) {
/* 1597 */         return enumValue;
/*      */       }
/*      */     } 
/*      */     
/* 1601 */     StringBuilder sb = new StringBuilder();
/* 1602 */     sb.append(StringUtil.jQuote(initParamValue));
/* 1603 */     sb.append(" is not a one of the enumeration values: ");
/* 1604 */     boolean first = true;
/* 1605 */     for (T value : enumValues) {
/* 1606 */       if (!first) {
/* 1607 */         sb.append(", ");
/*      */       } else {
/* 1609 */         first = false;
/*      */       } 
/* 1611 */       sb.append(StringUtil.jQuote(value.getInitParamValue()));
/*      */     } 
/* 1613 */     throw new IllegalArgumentException(sb.toString());
/*      */   }
/*      */ 
/*      */   
/*      */   private static interface InitParamValueEnum
/*      */   {
/*      */     String getInitParamValue();
/*      */   }
/*      */ 
/*      */   
/*      */   private enum OverrideResponseContentType
/*      */     implements InitParamValueEnum
/*      */   {
/* 1626 */     ALWAYS("always"),
/* 1627 */     NEVER("never"),
/* 1628 */     WHEN_TEMPLATE_HAS_MIME_TYPE("whenTemplateHasMimeType");
/*      */     
/*      */     private final String initParamValue;
/*      */     
/*      */     OverrideResponseContentType(String initParamValue) {
/* 1633 */       this.initParamValue = initParamValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getInitParamValue() {
/* 1638 */       return this.initParamValue;
/*      */     }
/*      */   }
/*      */   
/*      */   private enum ResponseCharacterEncoding implements InitParamValueEnum {
/* 1643 */     LEGACY("legacy"),
/* 1644 */     FROM_TEMPLATE("fromTemplate"),
/* 1645 */     DO_NOT_SET("doNotSet"),
/* 1646 */     FORCE_CHARSET("force ${charsetName}");
/*      */     
/*      */     private final String initParamValue;
/*      */     
/*      */     ResponseCharacterEncoding(String initParamValue) {
/* 1651 */       this.initParamValue = initParamValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getInitParamValue() {
/* 1656 */       return this.initParamValue;
/*      */     }
/*      */   }
/*      */   
/*      */   private enum OverrideResponseLocale implements InitParamValueEnum {
/* 1661 */     ALWAYS("always"),
/* 1662 */     NEVER("never");
/*      */     
/*      */     private final String initParamValue;
/*      */     
/*      */     OverrideResponseLocale(String initParamValue) {
/* 1667 */       this.initParamValue = initParamValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getInitParamValue() {
/* 1672 */       return this.initParamValue;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\FreemarkerServlet.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */