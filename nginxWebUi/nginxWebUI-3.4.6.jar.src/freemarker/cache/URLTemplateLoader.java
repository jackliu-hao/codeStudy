/*     */ package freemarker.cache;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.net.URL;
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
/*     */ public abstract class URLTemplateLoader
/*     */   implements TemplateLoader
/*     */ {
/*     */   private Boolean urlConnectionUsesCaches;
/*     */   
/*     */   public Object findTemplateSource(String name) throws IOException {
/*  44 */     URL url = getURL(name);
/*  45 */     return (url == null) ? null : new URLTemplateSource(url, getURLConnectionUsesCaches());
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModified(Object templateSource) {
/*  50 */     return ((URLTemplateSource)templateSource).lastModified();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getReader(Object templateSource, String encoding) throws IOException {
/*  56 */     return new InputStreamReader(((URLTemplateSource)templateSource)
/*  57 */         .getInputStream(), encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeTemplateSource(Object templateSource) throws IOException {
/*  64 */     ((URLTemplateSource)templateSource).close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean getURLConnectionUsesCaches() {
/*  73 */     return this.urlConnectionUsesCaches;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
/*  93 */     this.urlConnectionUsesCaches = urlConnectionUsesCaches;
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
/*     */   protected abstract URL getURL(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String canonicalizePrefix(String prefix) {
/* 114 */     prefix = prefix.replace('\\', '/');
/*     */     
/* 116 */     if (prefix.length() > 0 && !prefix.endsWith("/")) {
/* 117 */       prefix = prefix + "/";
/*     */     }
/* 119 */     return prefix;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\URLTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */