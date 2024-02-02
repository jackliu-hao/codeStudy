package freemarker.ext.servlet;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FreemarkerServlet extends HttpServlet {
   private static final Logger LOG = Logger.getLogger("freemarker.servlet");
   private static final Logger LOG_RT = Logger.getLogger("freemarker.runtime");
   public static final long serialVersionUID = -2440216393145762479L;
   public static final String INIT_PARAM_TEMPLATE_PATH = "TemplatePath";
   public static final String INIT_PARAM_NO_CACHE = "NoCache";
   public static final String INIT_PARAM_CONTENT_TYPE = "ContentType";
   public static final String INIT_PARAM_OVERRIDE_RESPONSE_CONTENT_TYPE = "OverrideResponseContentType";
   public static final String INIT_PARAM_RESPONSE_CHARACTER_ENCODING = "ResponseCharacterEncoding";
   public static final String INIT_PARAM_OVERRIDE_RESPONSE_LOCALE = "OverrideResponseLocale";
   public static final String INIT_PARAM_BUFFER_SIZE = "BufferSize";
   public static final String INIT_PARAM_META_INF_TLD_LOCATIONS = "MetaInfTldSources";
   public static final String INIT_PARAM_EXCEPTION_ON_MISSING_TEMPLATE = "ExceptionOnMissingTemplate";
   public static final String INIT_PARAM_CLASSPATH_TLDS = "ClasspathTlds";
   private static final String INIT_PARAM_DEBUG = "Debug";
   private static final String DEPR_INITPARAM_TEMPLATE_DELAY = "TemplateDelay";
   private static final String DEPR_INITPARAM_ENCODING = "DefaultEncoding";
   private static final String DEPR_INITPARAM_OBJECT_WRAPPER = "ObjectWrapper";
   private static final String DEPR_INITPARAM_WRAPPER_SIMPLE = "simple";
   private static final String DEPR_INITPARAM_WRAPPER_BEANS = "beans";
   private static final String DEPR_INITPARAM_WRAPPER_JYTHON = "jython";
   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER = "TemplateExceptionHandler";
   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW = "rethrow";
   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_DEBUG = "debug";
   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG = "htmlDebug";
   private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE = "ignore";
   private static final String DEPR_INITPARAM_DEBUG = "debug";
   private static final ContentType DEFAULT_CONTENT_TYPE = new ContentType("text/html");
   public static final String INIT_PARAM_VALUE_NEVER = "never";
   public static final String INIT_PARAM_VALUE_ALWAYS = "always";
   public static final String INIT_PARAM_VALUE_WHEN_TEMPLATE_HAS_MIME_TYPE = "whenTemplateHasMimeType";
   public static final String INIT_PARAM_VALUE_FROM_TEMPLATE = "fromTemplate";
   public static final String INIT_PARAM_VALUE_LEGACY = "legacy";
   public static final String INIT_PARAM_VALUE_DO_NOT_SET = "doNotSet";
   public static final String INIT_PARAM_VALUE_FORCE_PREFIX = "force ";
   public static final String SYSTEM_PROPERTY_META_INF_TLD_SOURCES = "org.freemarker.jsp.metaInfTldSources";
   public static final String SYSTEM_PROPERTY_CLASSPATH_TLDS = "org.freemarker.jsp.classpathTlds";
   public static final String META_INF_TLD_LOCATION_WEB_INF_PER_LIB_JARS = "webInfPerLibJars";
   public static final String META_INF_TLD_LOCATION_CLASSPATH = "classpath";
   public static final String META_INF_TLD_LOCATION_CLEAR = "clear";
   public static final String KEY_REQUEST = "Request";
   public static final String KEY_INCLUDE = "include_page";
   public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
   public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
   public static final String KEY_SESSION = "Session";
   public static final String KEY_APPLICATION = "Application";
   public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
   public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
   private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
   private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
   private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
   /** @deprecated */
   @Deprecated
   private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
   /** @deprecated */
   @Deprecated
   private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
   private static final String ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
   private static final String EXPIRATION_DATE;
   private String templatePath;
   private boolean noCache;
   private Integer bufferSize;
   private boolean exceptionOnMissingTemplate;
   /** @deprecated */
   @Deprecated
   protected boolean debug;
   private Configuration config;
   private ObjectWrapper wrapper;
   private ContentType contentType;
   private OverrideResponseContentType overrideResponseContentType = (OverrideResponseContentType)this.initParamValueToEnum(this.getDefaultOverrideResponseContentType(), FreemarkerServlet.OverrideResponseContentType.values());
   private ResponseCharacterEncoding responseCharacterEncoding;
   private Charset forcedResponseCharacterEncoding;
   private OverrideResponseLocale overrideResponseLocale;
   private List metaInfTldSources;
   private List classpathTlds;
   private Object lazyInitFieldsLock;
   private ServletContextHashModel servletContextModel;
   private TaglibFactory taglibFactory;
   private boolean objectWrapperMismatchWarnLogged;

   public FreemarkerServlet() {
      this.responseCharacterEncoding = FreemarkerServlet.ResponseCharacterEncoding.LEGACY;
      this.overrideResponseLocale = FreemarkerServlet.OverrideResponseLocale.ALWAYS;
      this.lazyInitFieldsLock = new Object();
   }

   public void init() throws ServletException {
      try {
         this.initialize();
      } catch (Exception var2) {
         throw new ServletException("Error while initializing " + this.getClass().getName() + " servlet; see cause exception.", var2);
      }
   }

   private void initialize() throws InitParamValueException, MalformedWebXmlException, ConflictingInitParamsException {
      this.config = this.createConfiguration();
      String iciInitParamValue = this.getInitParameter("incompatible_improvements");
      if (iciInitParamValue != null) {
         try {
            this.config.setSetting("incompatible_improvements", iciInitParamValue);
         } catch (Exception var10) {
            throw new InitParamValueException("incompatible_improvements", iciInitParamValue, var10);
         }
      }

      if (!this.config.isTemplateExceptionHandlerExplicitlySet()) {
         this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
      }

      if (!this.config.isLogTemplateExceptionsExplicitlySet()) {
         this.config.setLogTemplateExceptions(false);
      }

      this.contentType = DEFAULT_CONTENT_TYPE;
      this.wrapper = this.createObjectWrapper();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Using object wrapper: " + this.wrapper);
      }

      this.config.setObjectWrapper(this.wrapper);
      this.templatePath = this.getInitParameter("TemplatePath");
      if (this.templatePath == null && !this.config.isTemplateLoaderExplicitlySet()) {
         this.templatePath = "class://";
      }

      if (this.templatePath != null) {
         try {
            this.config.setTemplateLoader(this.createTemplateLoader(this.templatePath));
         } catch (Exception var9) {
            throw new InitParamValueException("TemplatePath", this.templatePath, var9);
         }
      }

      this.metaInfTldSources = this.createDefaultMetaInfTldSources();
      this.classpathTlds = this.createDefaultClassPathTlds();
      Enumeration initpnames = this.getServletConfig().getInitParameterNames();

      while(initpnames.hasMoreElements()) {
         String name = (String)initpnames.nextElement();
         String value = this.getInitParameter(name);
         if (name == null) {
            throw new MalformedWebXmlException("init-param without param-name. Maybe the web.xml is not well-formed?");
         }

         if (value == null) {
            throw new MalformedWebXmlException("init-param " + StringUtil.jQuote(name) + " without param-value. Maybe the web.xml is not well-formed?");
         }

         try {
            if (!name.equals("ObjectWrapper") && !name.equals("object_wrapper") && !name.equals("TemplatePath") && !name.equals("incompatible_improvements")) {
               if (name.equals("DefaultEncoding")) {
                  if (this.getInitParameter("default_encoding") != null) {
                     throw new ConflictingInitParamsException("default_encoding", "DefaultEncoding");
                  }

                  this.config.setDefaultEncoding(value);
               } else if (name.equals("TemplateDelay")) {
                  if (this.getInitParameter("template_update_delay") != null) {
                     throw new ConflictingInitParamsException("template_update_delay", "TemplateDelay");
                  }

                  try {
                     this.config.setTemplateUpdateDelay(Integer.parseInt(value));
                  } catch (NumberFormatException var6) {
                  }
               } else if (name.equals("TemplateExceptionHandler")) {
                  if (this.getInitParameter("template_exception_handler") != null) {
                     throw new ConflictingInitParamsException("template_exception_handler", "TemplateExceptionHandler");
                  }

                  if ("rethrow".equals(value)) {
                     this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                  } else if ("debug".equals(value)) {
                     this.config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                  } else if ("htmlDebug".equals(value)) {
                     this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                  } else {
                     if (!"ignore".equals(value)) {
                        throw new InitParamValueException("TemplateExceptionHandler", value, "Not one of the supported values.");
                     }

                     this.config.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                  }
               } else if (name.equals("NoCache")) {
                  this.noCache = StringUtil.getYesNo(value);
               } else if (name.equals("BufferSize")) {
                  this.bufferSize = this.parseSize(value);
               } else if (name.equals("debug")) {
                  if (this.getInitParameter("Debug") != null) {
                     throw new ConflictingInitParamsException("Debug", "debug");
                  }

                  this.debug = StringUtil.getYesNo(value);
               } else if (name.equals("Debug")) {
                  this.debug = StringUtil.getYesNo(value);
               } else if (name.equals("ContentType")) {
                  this.contentType = new ContentType(value);
               } else if (name.equals("OverrideResponseContentType")) {
                  this.overrideResponseContentType = (OverrideResponseContentType)this.initParamValueToEnum(value, FreemarkerServlet.OverrideResponseContentType.values());
               } else if (name.equals("ResponseCharacterEncoding")) {
                  this.responseCharacterEncoding = (ResponseCharacterEncoding)this.initParamValueToEnum(value, FreemarkerServlet.ResponseCharacterEncoding.values());
                  if (this.responseCharacterEncoding == FreemarkerServlet.ResponseCharacterEncoding.FORCE_CHARSET) {
                     String charsetName = value.substring("force ".length()).trim();
                     this.forcedResponseCharacterEncoding = Charset.forName(charsetName);
                  }
               } else if (name.equals("OverrideResponseLocale")) {
                  this.overrideResponseLocale = (OverrideResponseLocale)this.initParamValueToEnum(value, FreemarkerServlet.OverrideResponseLocale.values());
               } else if (name.equals("ExceptionOnMissingTemplate")) {
                  this.exceptionOnMissingTemplate = StringUtil.getYesNo(value);
               } else if (name.equals("MetaInfTldSources")) {
                  this.metaInfTldSources = this.parseAsMetaInfTldLocations(value);
               } else if (name.equals("ClasspathTlds")) {
                  List newClasspathTlds = new ArrayList();
                  if (this.classpathTlds != null) {
                     newClasspathTlds.addAll(this.classpathTlds);
                  }

                  newClasspathTlds.addAll(InitParamParser.parseCommaSeparatedList(value));
                  this.classpathTlds = newClasspathTlds;
               } else {
                  this.config.setSetting(name, value);
               }
            }
         } catch (ConflictingInitParamsException var7) {
            throw var7;
         } catch (Exception var8) {
            throw new InitParamValueException(name, value, var8);
         }
      }

      if (this.contentType.containsCharset && this.responseCharacterEncoding != FreemarkerServlet.ResponseCharacterEncoding.LEGACY) {
         throw new InitParamValueException("ContentType", this.contentType.httpHeaderValue, new IllegalStateException("You can't specify the charset in the content type, because the \"ResponseCharacterEncoding\" init-param isn't set to \"legacy\"."));
      }
   }

   private List parseAsMetaInfTldLocations(String value) throws ParseException {
      List metaInfTldSources = null;
      List values = InitParamParser.parseCommaSeparatedList(value);

      Object metaInfTldSource;
      for(Iterator it = values.iterator(); it.hasNext(); metaInfTldSources.add(metaInfTldSource)) {
         String itemStr = (String)it.next();
         if (itemStr.equals("webInfPerLibJars")) {
            metaInfTldSource = TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE;
         } else if (itemStr.startsWith("classpath")) {
            String itemRightSide = itemStr.substring("classpath".length()).trim();
            if (itemRightSide.length() == 0) {
               metaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(".*", 32));
            } else {
               if (!itemRightSide.startsWith(":")) {
                  throw new ParseException("Invalid \"classpath\" value syntax: " + value, -1);
               }

               String regexpStr = itemRightSide.substring(1).trim();
               if (regexpStr.length() == 0) {
                  throw new ParseException("Empty regular expression after \"classpath:\"", -1);
               }

               metaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(regexpStr));
            }
         } else {
            if (!itemStr.startsWith("clear")) {
               throw new ParseException("Item has no recognized source type prefix: " + itemStr, -1);
            }

            metaInfTldSource = TaglibFactory.ClearMetaInfTldSource.INSTANCE;
         }

         if (metaInfTldSources == null) {
            metaInfTldSources = new ArrayList();
         }
      }

      return metaInfTldSources;
   }

   protected TemplateLoader createTemplateLoader(String templatePath) throws IOException {
      return InitParamParser.createTemplateLoader(templatePath, this.getConfiguration(), this.getClass(), this.getServletContext());
   }

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.process(request, response);
   }

   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.process(request, response);
   }

   private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (!this.preprocessRequest(request, response)) {
         if (this.bufferSize != null && !response.isCommitted()) {
            try {
               response.setBufferSize(this.bufferSize);
            } catch (IllegalStateException var19) {
               LOG.debug("Can't set buffer size any more,", var19);
            }
         }

         String templatePath = this.requestUrlToTemplatePath(request);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Requested template " + StringUtil.jQuoteNoXSS(templatePath) + ".");
         }

         Locale locale = request.getLocale();
         if (locale == null || this.overrideResponseLocale != FreemarkerServlet.OverrideResponseLocale.NEVER) {
            locale = this.deduceLocale(templatePath, request, response);
         }

         Template template;
         try {
            template = this.config.getTemplate(templatePath, locale);
         } catch (TemplateNotFoundException var21) {
            if (this.exceptionOnMissingTemplate) {
               throw this.newServletExceptionWithFreeMarkerLogging("Template not found for name " + StringUtil.jQuoteNoXSS(templatePath) + ".", var21);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Responding HTTP 404 \"Not found\" for missing template " + StringUtil.jQuoteNoXSS(templatePath) + ".", var21);
            }

            response.sendError(404, "Page template not found");
            return;
         } catch (freemarker.core.ParseException var22) {
            throw this.newServletExceptionWithFreeMarkerLogging("Parsing error with template " + StringUtil.jQuoteNoXSS(templatePath) + ".", var22);
         } catch (Exception var23) {
            throw this.newServletExceptionWithFreeMarkerLogging("Unexpected error when loading template " + StringUtil.jQuoteNoXSS(templatePath) + ".", var23);
         }

         boolean tempSpecContentTypeContainsCharset = false;
         if (response.getContentType() == null || this.overrideResponseContentType != FreemarkerServlet.OverrideResponseContentType.NEVER) {
            ContentType templateSpecificContentType = this.getTemplateSpecificContentType(template);
            if (templateSpecificContentType != null) {
               response.setContentType(this.responseCharacterEncoding != FreemarkerServlet.ResponseCharacterEncoding.DO_NOT_SET ? templateSpecificContentType.httpHeaderValue : templateSpecificContentType.getMimeType());
               tempSpecContentTypeContainsCharset = templateSpecificContentType.containsCharset;
            } else if (response.getContentType() == null || this.overrideResponseContentType == FreemarkerServlet.OverrideResponseContentType.ALWAYS) {
               if (this.responseCharacterEncoding == FreemarkerServlet.ResponseCharacterEncoding.LEGACY && !this.contentType.containsCharset) {
                  response.setContentType(this.contentType.httpHeaderValue + "; charset=" + this.getTemplateSpecificOutputEncoding(template));
               } else {
                  response.setContentType(this.contentType.httpHeaderValue);
               }
            }
         }

         if (this.responseCharacterEncoding != FreemarkerServlet.ResponseCharacterEncoding.LEGACY && this.responseCharacterEncoding != FreemarkerServlet.ResponseCharacterEncoding.DO_NOT_SET) {
            if (this.responseCharacterEncoding != FreemarkerServlet.ResponseCharacterEncoding.FORCE_CHARSET) {
               if (!tempSpecContentTypeContainsCharset) {
                  response.setCharacterEncoding(this.getTemplateSpecificOutputEncoding(template));
               }
            } else {
               response.setCharacterEncoding(this.forcedResponseCharacterEncoding.name());
            }
         }

         this.setBrowserCachingPolicy(response);
         ServletContext servletContext = this.getServletContext();

         try {
            this.logWarnOnObjectWrapperMismatch();
            TemplateModel model = this.createModel(this.wrapper, servletContext, request, response);
            if (this.preTemplateProcess(request, response, template, model)) {
               try {
                  Environment env = template.createProcessingEnvironment(model, response.getWriter());
                  if (this.responseCharacterEncoding != FreemarkerServlet.ResponseCharacterEncoding.LEGACY) {
                     String actualOutputCharset = response.getCharacterEncoding();
                     if (actualOutputCharset != null) {
                        env.setOutputEncoding(actualOutputCharset);
                     }
                  }

                  this.processEnvironment(env, request, response);
               } finally {
                  this.postTemplateProcess(request, response, template, model);
               }
            }

         } catch (TemplateException var20) {
            TemplateExceptionHandler teh = this.config.getTemplateExceptionHandler();
            if (teh == TemplateExceptionHandler.HTML_DEBUG_HANDLER || teh == TemplateExceptionHandler.DEBUG_HANDLER || teh.getClass().getName().indexOf("Debug") != -1) {
               response.flushBuffer();
            }

            throw this.newServletExceptionWithFreeMarkerLogging("Error executing FreeMarker template", var20);
         }
      }
   }

   protected void processEnvironment(Environment env, HttpServletRequest request, HttpServletResponse response) throws TemplateException, IOException {
      env.process();
   }

   private String getTemplateSpecificOutputEncoding(Template template) {
      String outputEncoding = this.responseCharacterEncoding == FreemarkerServlet.ResponseCharacterEncoding.LEGACY ? null : template.getOutputEncoding();
      return outputEncoding != null ? outputEncoding : template.getEncoding();
   }

   private ContentType getTemplateSpecificContentType(Template template) {
      Object contentTypeAttr = template.getCustomAttribute("content_type");
      if (contentTypeAttr != null) {
         return new ContentType(contentTypeAttr.toString());
      } else {
         String outputFormatMimeType = template.getOutputFormat().getMimeType();
         if (outputFormatMimeType != null) {
            return this.responseCharacterEncoding == FreemarkerServlet.ResponseCharacterEncoding.LEGACY ? new ContentType(outputFormatMimeType + "; charset=" + this.getTemplateSpecificOutputEncoding(template), true) : new ContentType(outputFormatMimeType, false);
         } else {
            return null;
         }
      }
   }

   private ServletException newServletExceptionWithFreeMarkerLogging(String message, Throwable cause) throws ServletException {
      if (cause instanceof TemplateException) {
         LOG_RT.error(message, cause);
      } else {
         LOG.error(message, cause);
      }

      ServletException e = new ServletException(message, cause);

      try {
         e.initCause(cause);
      } catch (Exception var5) {
      }

      throw e;
   }

   private void logWarnOnObjectWrapperMismatch() {
      if (this.wrapper != this.config.getObjectWrapper() && !this.objectWrapperMismatchWarnLogged && LOG.isWarnEnabled()) {
         boolean logWarn;
         synchronized(this) {
            logWarn = !this.objectWrapperMismatchWarnLogged;
            if (logWarn) {
               this.objectWrapperMismatchWarnLogged = true;
            }
         }

         if (logWarn) {
            LOG.warn(this.getClass().getName() + ".wrapper != config.getObjectWrapper(); possibly the result of incorrect extension of " + FreemarkerServlet.class.getName() + ".");
         }
      }

   }

   protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) throws ServletException {
      return this.config.getLocale();
   }

   protected TemplateModel createModel(ObjectWrapper objectWrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
      try {
         AllHttpScopesHashModel params = new AllHttpScopesHashModel(objectWrapper, servletContext, request);
         ServletContextHashModel servletContextModel;
         TaglibFactory taglibFactory;
         synchronized(this.lazyInitFieldsLock) {
            if (this.servletContextModel == null) {
               servletContextModel = new ServletContextHashModel(this, objectWrapper);
               taglibFactory = this.createTaglibFactory(objectWrapper, servletContext);
               servletContext.setAttribute(".freemarker.Application", servletContextModel);
               servletContext.setAttribute(".freemarker.JspTaglibs", taglibFactory);
               this.initializeServletContext(request, response);
               this.taglibFactory = taglibFactory;
               this.servletContextModel = servletContextModel;
            } else {
               servletContextModel = this.servletContextModel;
               taglibFactory = this.taglibFactory;
            }
         }

         params.putUnlistedModel("Application", servletContextModel);
         params.putUnlistedModel("__FreeMarkerServlet.Application__", servletContextModel);
         params.putUnlistedModel("JspTaglibs", taglibFactory);
         HttpSession session = request.getSession(false);
         HttpSessionHashModel sessionModel;
         if (session != null) {
            sessionModel = (HttpSessionHashModel)session.getAttribute(".freemarker.Session");
            if (sessionModel == null || sessionModel.isOrphaned(session)) {
               sessionModel = new HttpSessionHashModel(session, objectWrapper);
               this.initializeSessionAndInstallModel(request, response, sessionModel, session);
            }
         } else {
            sessionModel = new HttpSessionHashModel(this, request, response, objectWrapper);
         }

         params.putUnlistedModel("Session", sessionModel);
         HttpRequestHashModel requestModel = (HttpRequestHashModel)request.getAttribute(".freemarker.Request");
         if (requestModel == null || requestModel.getRequest() != request) {
            requestModel = new HttpRequestHashModel(request, response, objectWrapper);
            request.setAttribute(".freemarker.Request", requestModel);
            request.setAttribute(".freemarker.RequestParameters", this.createRequestParametersHashModel(request));
         }

         params.putUnlistedModel("Request", requestModel);
         params.putUnlistedModel("include_page", new IncludePage(request, response));
         params.putUnlistedModel("__FreeMarkerServlet.Request__", requestModel);
         HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel)request.getAttribute(".freemarker.RequestParameters");
         params.putUnlistedModel("RequestParameters", requestParametersModel);
         return params;
      } catch (IOException | ServletException var13) {
         throw new TemplateModelException(var13);
      }
   }

   protected TaglibFactory createTaglibFactory(ObjectWrapper objectWrapper, ServletContext servletContext) throws TemplateModelException {
      TaglibFactory taglibFactory = new TaglibFactory(servletContext);
      taglibFactory.setObjectWrapper(objectWrapper);
      List mergedClassPathTlds = new ArrayList();
      if (this.metaInfTldSources != null) {
         mergedClassPathTlds.addAll(this.metaInfTldSources);
      }

      String sysPropVal = SecurityUtilities.getSystemProperty("org.freemarker.jsp.metaInfTldSources", (String)null);
      List classpathTldsSysProp;
      if (sysPropVal != null) {
         try {
            classpathTldsSysProp = this.parseAsMetaInfTldLocations(sysPropVal);
            if (classpathTldsSysProp != null) {
               mergedClassPathTlds.addAll(classpathTldsSysProp);
            }
         } catch (ParseException var11) {
            throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.metaInfTldSources\"", var11);
         }
      }

      classpathTldsSysProp = null;

      try {
         String attrVal = (String)servletContext.getAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern");
         classpathTldsSysProp = attrVal != null ? InitParamParser.parseCommaSeparatedPatterns(attrVal) : null;
      } catch (Exception var10) {
         LOG.error("Failed to parse application context attribute \"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern\" - it will be ignored", var10);
      }

      if (classpathTldsSysProp != null) {
         Iterator it = classpathTldsSysProp.iterator();

         while(it.hasNext()) {
            Pattern pattern = (Pattern)it.next();
            mergedClassPathTlds.add(new TaglibFactory.ClasspathMetaInfTldSource(pattern));
         }
      }

      taglibFactory.setMetaInfTldSources(mergedClassPathTlds);
      mergedClassPathTlds = new ArrayList();
      if (this.classpathTlds != null) {
         mergedClassPathTlds.addAll(this.classpathTlds);
      }

      sysPropVal = SecurityUtilities.getSystemProperty("org.freemarker.jsp.classpathTlds", (String)null);
      if (sysPropVal != null) {
         try {
            classpathTldsSysProp = InitParamParser.parseCommaSeparatedList(sysPropVal);
            if (classpathTldsSysProp != null) {
               mergedClassPathTlds.addAll(classpathTldsSysProp);
            }
         } catch (ParseException var9) {
            throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.classpathTlds\"", var9);
         }
      }

      taglibFactory.setClasspathTlds(mergedClassPathTlds);
      return taglibFactory;
   }

   protected List createDefaultClassPathTlds() {
      return TaglibFactory.DEFAULT_CLASSPATH_TLDS;
   }

   protected List createDefaultMetaInfTldSources() {
      return TaglibFactory.DEFAULT_META_INF_TLD_SOURCES;
   }

   void initializeSessionAndInstallModel(HttpServletRequest request, HttpServletResponse response, HttpSessionHashModel sessionModel, HttpSession session) throws ServletException, IOException {
      session.setAttribute(".freemarker.Session", sessionModel);
      this.initializeSession(request, response);
   }

   protected String requestUrlToTemplatePath(HttpServletRequest request) throws ServletException {
      String includeServletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
      String path;
      if (includeServletPath != null) {
         path = (String)request.getAttribute("javax.servlet.include.path_info");
         return path == null ? includeServletPath : path;
      } else {
         path = request.getPathInfo();
         if (path != null) {
            return path;
         } else {
            path = request.getServletPath();
            return path != null ? path : "";
         }
      }
   }

   protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      return false;
   }

   protected Configuration createConfiguration() {
      return new Configuration();
   }

   protected void setConfigurationDefaults() {
   }

   protected ObjectWrapper createObjectWrapper() {
      String wrapper = this.getServletConfig().getInitParameter("ObjectWrapper");
      if (wrapper != null) {
         if (this.getInitParameter("object_wrapper") != null) {
            throw new RuntimeException("Conflicting init-params: object_wrapper and ObjectWrapper");
         } else if ("beans".equals(wrapper)) {
            return ObjectWrapper.BEANS_WRAPPER;
         } else if ("simple".equals(wrapper)) {
            return ObjectWrapper.SIMPLE_WRAPPER;
         } else if ("jython".equals(wrapper)) {
            try {
               return (ObjectWrapper)Class.forName("freemarker.ext.jython.JythonWrapper").newInstance();
            } catch (InstantiationException var3) {
               throw new InstantiationError(var3.getMessage());
            } catch (IllegalAccessException var4) {
               throw new IllegalAccessError(var4.getMessage());
            } catch (ClassNotFoundException var5) {
               throw new NoClassDefFoundError(var5.getMessage());
            }
         } else {
            return this.createDefaultObjectWrapper();
         }
      } else {
         wrapper = this.getInitParameter("object_wrapper");
         if (wrapper == null) {
            return !this.config.isObjectWrapperExplicitlySet() ? this.createDefaultObjectWrapper() : this.config.getObjectWrapper();
         } else {
            try {
               this.config.setSetting("object_wrapper", wrapper);
            } catch (TemplateException var6) {
               throw new RuntimeException("Failed to set object_wrapper", var6);
            }

            return this.config.getObjectWrapper();
         }
      }
   }

   protected ObjectWrapper createDefaultObjectWrapper() {
      return Configuration.getDefaultObjectWrapper(this.config.getIncompatibleImprovements());
   }

   protected ObjectWrapper getObjectWrapper() {
      return this.wrapper;
   }

   /** @deprecated */
   @Deprecated
   protected final String getTemplatePath() {
      return this.templatePath;
   }

   protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
      return new HttpRequestParametersHashModel(request);
   }

   protected void initializeServletContext(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   }

   protected void initializeSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   }

   protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel model) throws ServletException, IOException {
      return true;
   }

   protected void postTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {
   }

   protected Configuration getConfiguration() {
      return this.config;
   }

   protected String getDefaultOverrideResponseContentType() {
      return "always";
   }

   private void setBrowserCachingPolicy(HttpServletResponse res) {
      if (this.noCache) {
         res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Expires", EXPIRATION_DATE);
      }

   }

   private int parseSize(String value) throws ParseException {
      int lastDigitIdx;
      int n;
      for(lastDigitIdx = value.length() - 1; lastDigitIdx >= 0; --lastDigitIdx) {
         n = value.charAt(lastDigitIdx);
         if (n >= 48 && n <= 57) {
            break;
         }
      }

      n = Integer.parseInt(value.substring(0, lastDigitIdx + 1).trim());
      String unitStr = value.substring(lastDigitIdx + 1).trim().toUpperCase();
      int unit;
      if (unitStr.length() != 0 && !unitStr.equals("B")) {
         if (!unitStr.equals("K") && !unitStr.equals("KB") && !unitStr.equals("KIB")) {
            if (!unitStr.equals("M") && !unitStr.equals("MB") && !unitStr.equals("MIB")) {
               throw new ParseException("Unknown unit: " + unitStr, lastDigitIdx + 1);
            }

            unit = 1048576;
         } else {
            unit = 1024;
         }
      } else {
         unit = 1;
      }

      long size = (long)n * (long)unit;
      if (size < 0L) {
         throw new IllegalArgumentException("Buffer size can't be negative");
      } else if (size > 2147483647L) {
         throw new IllegalArgumentException("Buffer size can't bigger than 2147483647");
      } else {
         return (int)size;
      }
   }

   private <T extends InitParamValueEnum> T initParamValueToEnum(String initParamValue, T[] enumValues) {
      InitParamValueEnum[] var3 = enumValues;
      int var4 = enumValues.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         T enumValue = var3[var5];
         String enumInitParamValue = enumValue.getInitParamValue();
         if (initParamValue.equals(enumInitParamValue) || enumInitParamValue.endsWith("}") && initParamValue.startsWith(enumInitParamValue.substring(0, enumInitParamValue.indexOf("${")))) {
            return enumValue;
         }
      }

      StringBuilder sb = new StringBuilder();
      sb.append(StringUtil.jQuote(initParamValue));
      sb.append(" is not a one of the enumeration values: ");
      boolean first = true;
      InitParamValueEnum[] var11 = enumValues;
      int var12 = enumValues.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         T value = var11[var13];
         if (!first) {
            sb.append(", ");
         } else {
            first = false;
         }

         sb.append(StringUtil.jQuote(value.getInitParamValue()));
      }

      throw new IllegalArgumentException(sb.toString());
   }

   static {
      GregorianCalendar expiration = new GregorianCalendar();
      expiration.roll(1, -1);
      SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      EXPIRATION_DATE = httpDate.format(expiration.getTime());
   }

   private static enum OverrideResponseLocale implements InitParamValueEnum {
      ALWAYS("always"),
      NEVER("never");

      private final String initParamValue;

      private OverrideResponseLocale(String initParamValue) {
         this.initParamValue = initParamValue;
      }

      public String getInitParamValue() {
         return this.initParamValue;
      }
   }

   private static enum ResponseCharacterEncoding implements InitParamValueEnum {
      LEGACY("legacy"),
      FROM_TEMPLATE("fromTemplate"),
      DO_NOT_SET("doNotSet"),
      FORCE_CHARSET("force ${charsetName}");

      private final String initParamValue;

      private ResponseCharacterEncoding(String initParamValue) {
         this.initParamValue = initParamValue;
      }

      public String getInitParamValue() {
         return this.initParamValue;
      }
   }

   private static enum OverrideResponseContentType implements InitParamValueEnum {
      ALWAYS("always"),
      NEVER("never"),
      WHEN_TEMPLATE_HAS_MIME_TYPE("whenTemplateHasMimeType");

      private final String initParamValue;

      private OverrideResponseContentType(String initParamValue) {
         this.initParamValue = initParamValue;
      }

      public String getInitParamValue() {
         return this.initParamValue;
      }
   }

   private interface InitParamValueEnum {
      String getInitParamValue();
   }

   private static class ContentType {
      private final String httpHeaderValue;
      private final boolean containsCharset;

      public ContentType(String httpHeaderValue) {
         this(httpHeaderValue, contentTypeContainsCharset(httpHeaderValue));
      }

      public ContentType(String httpHeaderValue, boolean containsCharset) {
         this.httpHeaderValue = httpHeaderValue;
         this.containsCharset = containsCharset;
      }

      private static boolean contentTypeContainsCharset(String contentType) {
         int charsetIdx = contentType.toLowerCase().indexOf("charset=");
         if (charsetIdx != -1) {
            char c = 0;
            --charsetIdx;

            while(true) {
               if (charsetIdx >= 0) {
                  c = contentType.charAt(charsetIdx);
                  if (Character.isWhitespace(c)) {
                     --charsetIdx;
                     continue;
                  }
               }

               if (charsetIdx == -1 || c == ';') {
                  return true;
               }
               break;
            }
         }

         return false;
      }

      private String getMimeType() {
         int scIdx = this.httpHeaderValue.indexOf(59);
         return (scIdx == -1 ? this.httpHeaderValue : this.httpHeaderValue.substring(0, scIdx)).trim();
      }
   }

   private static class MalformedWebXmlException extends Exception {
      MalformedWebXmlException(String message) {
         super(message);
      }
   }

   private static class ConflictingInitParamsException extends Exception {
      ConflictingInitParamsException(String recommendedName, String otherName) {
         super("Conflicting servlet init-params: " + StringUtil.jQuote(recommendedName) + " and " + StringUtil.jQuote(otherName) + ". Only use " + StringUtil.jQuote(recommendedName) + ".");
      }
   }

   private static class InitParamValueException extends Exception {
      InitParamValueException(String initParamName, String initParamValue, Throwable casue) {
         super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + StringUtil.jQuote(initParamValue) + "; see cause exception.", casue);
      }

      public InitParamValueException(String initParamName, String initParamValue, String cause) {
         super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + StringUtil.jQuote(initParamValue) + ": " + cause);
      }
   }
}
