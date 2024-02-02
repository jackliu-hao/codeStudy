/*     */ package cn.hutool.core.io.resource;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UrlResource
/*     */   implements Resource, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected URL url;
/*  22 */   private long lastModified = 0L;
/*     */ 
/*     */ 
/*     */   
/*     */   protected String name;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlResource(URI uri) {
/*  32 */     this(URLUtil.url(uri), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlResource(URL url) {
/*  40 */     this(url, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlResource(URL url, String name) {
/*  49 */     this.url = url;
/*  50 */     if (null != url && "file".equals(url.getProtocol())) {
/*  51 */       this.lastModified = FileUtil.file(url).lastModified();
/*     */     }
/*  53 */     this.name = (String)ObjectUtil.defaultIfNull(name, () -> (null != url) ? FileUtil.getName(url.getPath()) : null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public UrlResource(File file) {
/*  63 */     this.url = URLUtil.getURL(file);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  69 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/*  74 */     return this.url;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getStream() throws NoResourceException {
/*  79 */     if (null == this.url) {
/*  80 */       throw new NoResourceException("Resource URL is null!");
/*     */     }
/*  82 */     return URLUtil.getStream(this.url);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isModified() {
/*  88 */     return (0L != this.lastModified && this.lastModified != getFile().lastModified());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File getFile() {
/*  96 */     return FileUtil.file(this.url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 105 */     return (null == this.url) ? "null" : this.url.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\UrlResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */