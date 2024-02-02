/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import cn.hutool.core.io.resource.FileObjectResource;
/*    */ import cn.hutool.core.lang.ResourceClassLoader;
/*    */ import cn.hutool.core.util.ClassLoaderUtil;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.tools.FileObject;
/*    */ import javax.tools.ForwardingJavaFileManager;
/*    */ import javax.tools.JavaFileManager;
/*    */ import javax.tools.JavaFileObject;
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
/*    */ class JavaClassFileManager
/*    */   extends ForwardingJavaFileManager<JavaFileManager>
/*    */ {
/* 32 */   private final Map<String, FileObjectResource> classFileObjectMap = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final ClassLoader parent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected JavaClassFileManager(ClassLoader parent, JavaFileManager fileManager) {
/* 46 */     super(fileManager);
/* 47 */     this.parent = (ClassLoader)ObjectUtil.defaultIfNull(parent, ClassLoaderUtil::getClassLoader);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClassLoader getClassLoader(JavaFileManager.Location location) {
/* 58 */     return (ClassLoader)new ResourceClassLoader(this.parent, this.classFileObjectMap);
/*    */   }
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
/*    */   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
/* 73 */     JavaFileObject javaFileObject = new JavaClassFileObject(className);
/* 74 */     this.classFileObjectMap.put(className, new FileObjectResource(javaFileObject));
/* 75 */     return javaFileObject;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\JavaClassFileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */