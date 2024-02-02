/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import cn.hutool.core.io.resource.Resource;
/*    */ import cn.hutool.core.util.ClassLoaderUtil;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import java.security.SecureClassLoader;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResourceClassLoader<T extends Resource>
/*    */   extends SecureClassLoader
/*    */ {
/*    */   private final Map<String, T> resourceMap;
/*    */   private final Map<String, Class<?>> cacheClassMap;
/*    */   
/*    */   public ResourceClassLoader(ClassLoader parentClassLoader, Map<String, T> resourceMap) {
/* 33 */     super((ClassLoader)ObjectUtil.defaultIfNull(parentClassLoader, ClassLoaderUtil::getClassLoader));
/* 34 */     this.resourceMap = (Map<String, T>)ObjectUtil.defaultIfNull(resourceMap, HashMap::new);
/* 35 */     this.cacheClassMap = new HashMap<>();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResourceClassLoader<T> addResource(T resource) {
/* 45 */     this.resourceMap.put(resource.getName(), resource);
/* 46 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Class<?> findClass(String name) throws ClassNotFoundException {
/* 51 */     Class<?> clazz = this.cacheClassMap.computeIfAbsent(name, this::defineByName);
/* 52 */     if (clazz == null) {
/* 53 */       return super.findClass(name);
/*    */     }
/* 55 */     return clazz;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private Class<?> defineByName(String name) {
/* 66 */     Resource resource = (Resource)this.resourceMap.get(name);
/* 67 */     if (null != resource) {
/* 68 */       byte[] bytes = resource.readBytes();
/* 69 */       return defineClass(name, bytes, 0, bytes.length);
/*    */     } 
/* 71 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ResourceClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */