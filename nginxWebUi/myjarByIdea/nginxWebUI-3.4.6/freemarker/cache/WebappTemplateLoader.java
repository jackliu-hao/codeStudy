package freemarker.cache;

import freemarker.log.Logger;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

public class WebappTemplateLoader implements TemplateLoader {
   private static final Logger LOG = Logger.getLogger("freemarker.cache");
   private final ServletContext servletContext;
   private final String subdirPath;
   private Boolean urlConnectionUsesCaches;
   private boolean attemptFileAccess;

   public WebappTemplateLoader(ServletContext servletContext) {
      this(servletContext, "/");
   }

   public WebappTemplateLoader(ServletContext servletContext, String subdirPath) {
      this.attemptFileAccess = true;
      NullArgumentException.check("servletContext", servletContext);
      NullArgumentException.check("subdirPath", subdirPath);
      subdirPath = subdirPath.replace('\\', '/');
      if (!subdirPath.endsWith("/")) {
         subdirPath = subdirPath + "/";
      }

      if (!subdirPath.startsWith("/")) {
         subdirPath = "/" + subdirPath;
      }

      this.subdirPath = subdirPath;
      this.servletContext = servletContext;
   }

   public Object findTemplateSource(String name) throws IOException {
      String fullPath = this.subdirPath + name;
      String realPath;
      if (this.attemptFileAccess) {
         try {
            realPath = this.servletContext.getRealPath(fullPath);
            if (realPath != null) {
               File file = new File(realPath);
               if (file.canRead() && file.isFile()) {
                  return file;
               }
            }
         } catch (SecurityException var6) {
         }
      }

      realPath = null;

      URL url;
      try {
         url = this.servletContext.getResource(fullPath);
      } catch (MalformedURLException var5) {
         LOG.warn("Could not retrieve resource " + StringUtil.jQuoteNoXSS(fullPath), var5);
         return null;
      }

      return url == null ? null : new URLTemplateSource(url, this.getURLConnectionUsesCaches());
   }

   public long getLastModified(Object templateSource) {
      return templateSource instanceof File ? ((File)templateSource).lastModified() : ((URLTemplateSource)templateSource).lastModified();
   }

   public Reader getReader(Object templateSource, String encoding) throws IOException {
      return templateSource instanceof File ? new InputStreamReader(new FileInputStream((File)templateSource), encoding) : new InputStreamReader(((URLTemplateSource)templateSource).getInputStream(), encoding);
   }

   public void closeTemplateSource(Object templateSource) throws IOException {
      if (!(templateSource instanceof File)) {
         ((URLTemplateSource)templateSource).close();
      }

   }

   public Boolean getURLConnectionUsesCaches() {
      return this.urlConnectionUsesCaches;
   }

   public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
      this.urlConnectionUsesCaches = urlConnectionUsesCaches;
   }

   public String toString() {
      return TemplateLoaderUtils.getClassNameForToString(this) + "(subdirPath=" + StringUtil.jQuote(this.subdirPath) + ", servletContext={contextPath=" + StringUtil.jQuote(this.getContextPath()) + ", displayName=" + StringUtil.jQuote(this.servletContext.getServletContextName()) + "})";
   }

   private String getContextPath() {
      try {
         Method m = this.servletContext.getClass().getMethod("getContextPath", CollectionUtils.EMPTY_CLASS_ARRAY);
         return (String)m.invoke(this.servletContext, CollectionUtils.EMPTY_OBJECT_ARRAY);
      } catch (Throwable var2) {
         return "[can't query before Serlvet 2.5]";
      }
   }

   public boolean getAttemptFileAccess() {
      return this.attemptFileAccess;
   }

   public void setAttemptFileAccess(boolean attemptLoadingFromFile) {
      this.attemptFileAccess = attemptLoadingFromFile;
   }
}
