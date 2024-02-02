/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import freemarker.template.utility.StringUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassTemplateLoader
/*     */   extends URLTemplateLoader
/*     */ {
/*     */   private final Class<?> resourceLoaderClass;
/*     */   private final ClassLoader classLoader;
/*     */   private final String basePackagePath;
/*     */   
/*     */   @Deprecated
/*     */   public ClassTemplateLoader() {
/*  54 */     this(null, true, null, "/");
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
/*     */   @Deprecated
/*     */   public ClassTemplateLoader(Class<?> resourceLoaderClass) {
/*  70 */     this(resourceLoaderClass, "");
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
/*     */   public ClassTemplateLoader(Class<?> resourceLoaderClass, String basePackagePath) {
/* 100 */     this(resourceLoaderClass, false, null, basePackagePath);
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
/*     */   public ClassTemplateLoader(ClassLoader classLoader, String basePackagePath) {
/* 112 */     this(null, true, classLoader, basePackagePath);
/*     */   }
/*     */ 
/*     */   
/*     */   private ClassTemplateLoader(Class<?> resourceLoaderClass, boolean allowNullResourceLoaderClass, ClassLoader classLoader, String basePackagePath) {
/* 117 */     if (!allowNullResourceLoaderClass) {
/* 118 */       NullArgumentException.check("resourceLoaderClass", resourceLoaderClass);
/*     */     }
/* 120 */     NullArgumentException.check("basePackagePath", basePackagePath);
/*     */ 
/*     */     
/* 123 */     this.resourceLoaderClass = (classLoader == null) ? ((resourceLoaderClass == null) ? getClass() : resourceLoaderClass) : null;
/*     */     
/* 125 */     if (this.resourceLoaderClass == null && classLoader == null) {
/* 126 */       throw new NullArgumentException("classLoader");
/*     */     }
/* 128 */     this.classLoader = classLoader;
/*     */     
/* 130 */     String canonBasePackagePath = canonicalizePrefix(basePackagePath);
/* 131 */     if (this.classLoader != null && canonBasePackagePath.startsWith("/")) {
/* 132 */       canonBasePackagePath = canonBasePackagePath.substring(1);
/*     */     }
/* 134 */     this.basePackagePath = canonBasePackagePath;
/*     */   }
/*     */ 
/*     */   
/*     */   protected URL getURL(String name) {
/* 139 */     String fullPath = this.basePackagePath + name;
/*     */ 
/*     */     
/* 142 */     if (this.basePackagePath.equals("/") && !isSchemeless(fullPath)) {
/* 143 */       return null;
/*     */     }
/*     */     
/* 146 */     return (this.resourceLoaderClass != null) ? this.resourceLoaderClass.getResource(fullPath) : this.classLoader
/* 147 */       .getResource(fullPath);
/*     */   }
/*     */   
/*     */   private static boolean isSchemeless(String fullPath) {
/* 151 */     int i = 0;
/* 152 */     int ln = fullPath.length();
/*     */ 
/*     */     
/* 155 */     if (i < ln && fullPath.charAt(i) == '/') i++;
/*     */ 
/*     */ 
/*     */     
/* 159 */     while (i < ln) {
/* 160 */       char c = fullPath.charAt(i);
/* 161 */       if (c == '/') return true; 
/* 162 */       if (c == ':') return false; 
/* 163 */       i++;
/*     */     } 
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 175 */     return TemplateLoaderUtils.getClassNameForToString(this) + "(" + ((this.resourceLoaderClass != null) ? ("resourceLoaderClass=" + this.resourceLoaderClass
/*     */       
/* 177 */       .getName()) : ("classLoader=" + 
/* 178 */       StringUtil.jQuote(this.classLoader))) + ", basePackagePath=" + 
/*     */ 
/*     */       
/* 181 */       StringUtil.jQuote(this.basePackagePath) + ((this.resourceLoaderClass != null) ? (
/*     */       
/* 183 */       this.basePackagePath.startsWith("/") ? "" : " /* relatively to resourceLoaderClass pkg */") : "") + ")";
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
/*     */   public Class getResourceLoaderClass() {
/* 196 */     return this.resourceLoaderClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassLoader getClassLoader() {
/* 206 */     return this.classLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBasePackagePath() {
/* 216 */     return this.basePackagePath;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\ClassTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */