/*     */ package cn.hutool.core.io.resource;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VfsResource
/*     */   implements Resource
/*     */ {
/*     */   private static final String VFS3_PKG = "org.jboss.vfs.";
/*     */   private static final Method VIRTUAL_FILE_METHOD_EXISTS;
/*     */   private static final Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
/*     */   private static final Method VIRTUAL_FILE_METHOD_GET_SIZE;
/*     */   private static final Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
/*     */   private static final Method VIRTUAL_FILE_METHOD_TO_URL;
/*     */   private static final Method VIRTUAL_FILE_METHOD_GET_NAME;
/*     */   private final Object virtualFile;
/*     */   private final long lastModified;
/*     */   
/*     */   static {
/*  30 */     Class<?> virtualFile = ClassLoaderUtil.loadClass("org.jboss.vfs.VirtualFile");
/*     */     try {
/*  32 */       VIRTUAL_FILE_METHOD_EXISTS = virtualFile.getMethod("exists", new Class[0]);
/*  33 */       VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = virtualFile.getMethod("openStream", new Class[0]);
/*  34 */       VIRTUAL_FILE_METHOD_GET_SIZE = virtualFile.getMethod("getSize", new Class[0]);
/*  35 */       VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = virtualFile.getMethod("getLastModified", new Class[0]);
/*  36 */       VIRTUAL_FILE_METHOD_TO_URL = virtualFile.getMethod("toURL", new Class[0]);
/*  37 */       VIRTUAL_FILE_METHOD_GET_NAME = virtualFile.getMethod("getName", new Class[0]);
/*  38 */     } catch (NoSuchMethodException ex) {
/*  39 */       throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
/*     */     } 
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
/*     */   public VfsResource(Object resource) {
/*  55 */     Assert.notNull(resource, "VirtualFile must not be null", new Object[0]);
/*  56 */     this.virtualFile = resource;
/*  57 */     this.lastModified = getLastModified();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean exists() {
/*  66 */     return ((Boolean)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_EXISTS, new Object[0])).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  71 */     return (String)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_NAME, new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/*  76 */     return (URL)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_TO_URL, new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getStream() {
/*  81 */     return (InputStream)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_INPUT_STREAM, new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isModified() {
/*  86 */     return (this.lastModified != getLastModified());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLastModified() {
/*  95 */     return ((Long)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED, new Object[0])).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long size() {
/* 104 */     return ((Long)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_SIZE, new Object[0])).longValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\VfsResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */