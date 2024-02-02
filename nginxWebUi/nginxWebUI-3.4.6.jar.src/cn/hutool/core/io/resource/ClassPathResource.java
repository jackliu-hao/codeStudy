/*     */ package cn.hutool.core.io.resource;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
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
/*     */ public class ClassPathResource
/*     */   extends UrlResource
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final String path;
/*     */   private final ClassLoader classLoader;
/*     */   private final Class<?> clazz;
/*     */   
/*     */   public ClassPathResource(String path) {
/*  34 */     this(path, (ClassLoader)null, (Class<?>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassPathResource(String path, ClassLoader classLoader) {
/*  44 */     this(path, classLoader, (Class<?>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassPathResource(String path, Class<?> clazz) {
/*  54 */     this(path, (ClassLoader)null, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassPathResource(String pathBaseClassLoader, ClassLoader classLoader, Class<?> clazz) {
/*  65 */     super((URL)null);
/*  66 */     Assert.notNull(pathBaseClassLoader, "Path must not be null", new Object[0]);
/*     */     
/*  68 */     String path = normalizePath(pathBaseClassLoader);
/*  69 */     this.path = path;
/*  70 */     this.name = StrUtil.isBlank(path) ? null : FileUtil.getName(path);
/*     */     
/*  72 */     this.classLoader = (ClassLoader)ObjectUtil.defaultIfNull(classLoader, ClassUtil::getClassLoader);
/*  73 */     this.clazz = clazz;
/*  74 */     initUrl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getPath() {
/*  84 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getAbsolutePath() {
/*  94 */     if (FileUtil.isAbsolutePath(this.path)) {
/*  95 */       return this.path;
/*     */     }
/*     */     
/*  98 */     return FileUtil.normalize(URLUtil.getDecodedPath(this.url));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClassLoader getClassLoader() {
/* 107 */     return this.classLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initUrl() {
/* 114 */     if (null != this.clazz) {
/* 115 */       this.url = this.clazz.getResource(this.path);
/* 116 */     } else if (null != this.classLoader) {
/* 117 */       this.url = this.classLoader.getResource(this.path);
/*     */     } else {
/* 119 */       this.url = ClassLoader.getSystemResource(this.path);
/*     */     } 
/* 121 */     if (null == this.url) {
/* 122 */       throw new NoResourceException("Resource of path [{}] not exist!", new Object[] { this.path });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return (null == this.path) ? super.toString() : ("classpath:" + this.path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String normalizePath(String path) {
/* 139 */     path = FileUtil.normalize(path);
/* 140 */     path = StrUtil.removePrefix(path, "/");
/*     */     
/* 142 */     Assert.isFalse(FileUtil.isAbsolutePath(path), "Path [{}] must be a relative path !", new Object[] { path });
/* 143 */     return path;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\ClassPathResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */