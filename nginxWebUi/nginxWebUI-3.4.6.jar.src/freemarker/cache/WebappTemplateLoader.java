/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.CollectionUtils;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import javax.servlet.ServletContext;
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
/*     */ public class WebappTemplateLoader
/*     */   implements TemplateLoader
/*     */ {
/*  45 */   private static final Logger LOG = Logger.getLogger("freemarker.cache");
/*     */ 
/*     */ 
/*     */   
/*     */   private final ServletContext servletContext;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String subdirPath;
/*     */ 
/*     */   
/*     */   private Boolean urlConnectionUsesCaches;
/*     */ 
/*     */   
/*     */   private boolean attemptFileAccess = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public WebappTemplateLoader(ServletContext servletContext) {
/*  64 */     this(servletContext, "/");
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
/*     */   
/*     */   public WebappTemplateLoader(ServletContext servletContext, String subdirPath) {
/*  80 */     NullArgumentException.check("servletContext", servletContext);
/*  81 */     NullArgumentException.check("subdirPath", subdirPath);
/*     */     
/*  83 */     subdirPath = subdirPath.replace('\\', '/');
/*  84 */     if (!subdirPath.endsWith("/")) {
/*  85 */       subdirPath = subdirPath + "/";
/*     */     }
/*  87 */     if (!subdirPath.startsWith("/")) {
/*  88 */       subdirPath = "/" + subdirPath;
/*     */     }
/*  90 */     this.subdirPath = subdirPath;
/*  91 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object findTemplateSource(String name) throws IOException {
/*  96 */     String fullPath = this.subdirPath + name;
/*     */     
/*  98 */     if (this.attemptFileAccess) {
/*     */       
/*     */       try {
/* 101 */         String realPath = this.servletContext.getRealPath(fullPath);
/* 102 */         if (realPath != null) {
/* 103 */           File file = new File(realPath);
/* 104 */           if (file.canRead() && file.isFile()) {
/* 105 */             return file;
/*     */           }
/*     */         } 
/* 108 */       } catch (SecurityException securityException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     URL url = null;
/*     */     try {
/* 116 */       url = this.servletContext.getResource(fullPath);
/* 117 */     } catch (MalformedURLException e) {
/* 118 */       LOG.warn("Could not retrieve resource " + StringUtil.jQuoteNoXSS(fullPath), e);
/*     */       
/* 120 */       return null;
/*     */     } 
/* 122 */     return (url == null) ? null : new URLTemplateSource(url, getURLConnectionUsesCaches());
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModified(Object templateSource) {
/* 127 */     if (templateSource instanceof File) {
/* 128 */       return ((File)templateSource).lastModified();
/*     */     }
/* 130 */     return ((URLTemplateSource)templateSource).lastModified();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getReader(Object templateSource, String encoding) throws IOException {
/* 137 */     if (templateSource instanceof File) {
/* 138 */       return new InputStreamReader(new FileInputStream((File)templateSource), encoding);
/*     */     }
/*     */ 
/*     */     
/* 142 */     return new InputStreamReader(((URLTemplateSource)templateSource)
/* 143 */         .getInputStream(), encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeTemplateSource(Object templateSource) throws IOException {
/* 150 */     if (!(templateSource instanceof File))
/*     */     {
/*     */       
/* 153 */       ((URLTemplateSource)templateSource).close();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean getURLConnectionUsesCaches() {
/* 163 */     return this.urlConnectionUsesCaches;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
/* 172 */     this.urlConnectionUsesCaches = urlConnectionUsesCaches;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 182 */     return TemplateLoaderUtils.getClassNameForToString(this) + "(subdirPath=" + 
/* 183 */       StringUtil.jQuote(this.subdirPath) + ", servletContext={contextPath=" + 
/* 184 */       StringUtil.jQuote(getContextPath()) + ", displayName=" + 
/* 185 */       StringUtil.jQuote(this.servletContext.getServletContextName()) + "})";
/*     */   }
/*     */ 
/*     */   
/*     */   private String getContextPath() {
/*     */     try {
/* 191 */       Method m = this.servletContext.getClass().getMethod("getContextPath", CollectionUtils.EMPTY_CLASS_ARRAY);
/* 192 */       return (String)m.invoke(this.servletContext, CollectionUtils.EMPTY_OBJECT_ARRAY);
/* 193 */     } catch (Throwable e) {
/* 194 */       return "[can't query before Serlvet 2.5]";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAttemptFileAccess() {
/* 204 */     return this.attemptFileAccess;
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
/*     */   public void setAttemptFileAccess(boolean attemptLoadingFromFile) {
/* 219 */     this.attemptFileAccess = attemptLoadingFromFile;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\WebappTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */