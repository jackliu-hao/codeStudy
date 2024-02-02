/*     */ package cn.hutool.core.io.resource;
/*     */ 
/*     */ import cn.hutool.core.collection.EnumerationIter;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class ResourceUtil
/*     */ {
/*     */   public static String readUtf8Str(String resource) {
/*  36 */     return getResourceObj(resource).readUtf8Str();
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
/*     */   public static String readStr(String resource, Charset charset) {
/*  48 */     return getResourceObj(resource).readStr(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] readBytes(String resource) {
/*  59 */     return getResourceObj(resource).readBytes();
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
/*     */   public static InputStream getStream(String resource) throws NoResourceException {
/*  71 */     return getResourceObj(resource).getStream();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InputStream getStreamSafe(String resource) {
/*     */     try {
/*  83 */       return getResourceObj(resource).getStream();
/*  84 */     } catch (NoResourceException noResourceException) {
/*     */ 
/*     */       
/*  87 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedReader getUtf8Reader(String resource) {
/*  98 */     return getReader(resource, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public static BufferedReader getReader(String resource, Charset charset) {
/* 110 */     return getResourceObj(resource).getReader(charset);
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
/*     */   public static URL getResource(String resource) throws IORuntimeException {
/* 126 */     return getResource(resource, null);
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
/*     */   public static List<URL> getResources(String resource) {
/* 142 */     return getResources(resource, null);
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
/*     */   public static List<URL> getResources(String resource, Filter<URL> filter) {
/* 159 */     return IterUtil.filterToList((Iterator)getResourceIter(resource), filter);
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
/*     */   public static EnumerationIter<URL> getResourceIter(String resource) {
/*     */     Enumeration<URL> resources;
/*     */     try {
/* 178 */       resources = ClassLoaderUtil.getClassLoader().getResources(resource);
/* 179 */     } catch (IOException e) {
/* 180 */       throw new IORuntimeException(e);
/*     */     } 
/* 182 */     return new EnumerationIter(resources);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getResource(String resource, Class<?> baseClass) {
/* 193 */     resource = StrUtil.nullToEmpty(resource);
/* 194 */     return (null != baseClass) ? baseClass.getResource(resource) : ClassLoaderUtil.getClassLoader().getResource(resource);
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
/*     */   public static Resource getResourceObj(String path) {
/* 206 */     if (StrUtil.isNotBlank(path) && (
/* 207 */       path.startsWith("file:") || FileUtil.isAbsolutePath(path))) {
/* 208 */       return new FileResource(path);
/*     */     }
/*     */     
/* 211 */     return new ClassPathResource(path);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\ResourceUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */