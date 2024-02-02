package cn.hutool.core.compiler;

import cn.hutool.core.io.resource.FileObjectResource;
import cn.hutool.core.lang.ResourceClassLoader;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

class JavaClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {
   private final Map<String, FileObjectResource> classFileObjectMap = new HashMap();
   private final ClassLoader parent;

   protected JavaClassFileManager(ClassLoader parent, JavaFileManager fileManager) {
      super(fileManager);
      this.parent = (ClassLoader)ObjectUtil.defaultIfNull(parent, (Supplier)(ClassLoaderUtil::getClassLoader));
   }

   public ClassLoader getClassLoader(JavaFileManager.Location location) {
      return new ResourceClassLoader(this.parent, this.classFileObjectMap);
   }

   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
      JavaFileObject javaFileObject = new JavaClassFileObject(className);
      this.classFileObjectMap.put(className, new FileObjectResource(javaFileObject));
      return javaFileObject;
   }
}
