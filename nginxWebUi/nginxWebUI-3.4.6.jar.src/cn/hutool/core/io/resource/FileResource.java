/*     */ package cn.hutool.core.io.resource;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Path;
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
/*     */ public class FileResource
/*     */   implements Resource, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final File file;
/*     */   private final long lastModified;
/*     */   private final String name;
/*     */   
/*     */   public FileResource(String path) {
/*  33 */     this(FileUtil.file(path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileResource(Path path) {
/*  43 */     this(path.toFile());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileResource(File file) {
/*  52 */     this(file, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileResource(File file, String fileName) {
/*  62 */     Assert.notNull(file, "File must be not null !", new Object[0]);
/*  63 */     this.file = file;
/*  64 */     this.lastModified = file.lastModified();
/*  65 */     this.name = (String)ObjectUtil.defaultIfNull(fileName, file::getName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  72 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/*  77 */     return URLUtil.getURL(this.file);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getStream() throws NoResourceException {
/*  82 */     return FileUtil.getInputStream(this.file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File getFile() {
/*  91 */     return this.file;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isModified() {
/*  96 */     return (this.lastModified != this.file.lastModified());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 105 */     return this.file.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\FileResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */