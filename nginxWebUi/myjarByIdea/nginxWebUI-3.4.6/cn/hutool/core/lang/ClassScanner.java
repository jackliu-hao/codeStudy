package cn.hutool.core.lang;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String packageName;
   private final String packageNameWithDot;
   private final String packageDirName;
   private final String packagePath;
   private final Filter<Class<?>> classFilter;
   private final Charset charset;
   private ClassLoader classLoader;
   private boolean initialize;
   private final Set<Class<?>> classes;

   public static Set<Class<?>> scanAllPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
      return scanAllPackage(packageName, (clazz) -> {
         return clazz.isAnnotationPresent(annotationClass);
      });
   }

   public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
      return scanPackage(packageName, (clazz) -> {
         return clazz.isAnnotationPresent(annotationClass);
      });
   }

   public static Set<Class<?>> scanAllPackageBySuper(String packageName, Class<?> superClass) {
      return scanAllPackage(packageName, (clazz) -> {
         return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
      });
   }

   public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
      return scanPackage(packageName, (clazz) -> {
         return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
      });
   }

   public static Set<Class<?>> scanAllPackage() {
      return scanAllPackage("", (Filter)null);
   }

   public static Set<Class<?>> scanPackage() {
      return scanPackage("", (Filter)null);
   }

   public static Set<Class<?>> scanPackage(String packageName) {
      return scanPackage(packageName, (Filter)null);
   }

   public static Set<Class<?>> scanAllPackage(String packageName, Filter<Class<?>> classFilter) {
      return (new ClassScanner(packageName, classFilter)).scan(true);
   }

   public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
      return (new ClassScanner(packageName, classFilter)).scan();
   }

   public ClassScanner() {
      this((String)null);
   }

   public ClassScanner(String packageName) {
      this(packageName, (Filter)null);
   }

   public ClassScanner(String packageName, Filter<Class<?>> classFilter) {
      this(packageName, classFilter, CharsetUtil.CHARSET_UTF_8);
   }

   public ClassScanner(String packageName, Filter<Class<?>> classFilter, Charset charset) {
      this.classes = new HashSet();
      packageName = StrUtil.nullToEmpty(packageName);
      this.packageName = packageName;
      this.packageNameWithDot = StrUtil.addSuffixIfNot(packageName, ".");
      this.packageDirName = packageName.replace('.', File.separatorChar);
      this.packagePath = packageName.replace('.', '/');
      this.classFilter = classFilter;
      this.charset = charset;
   }

   public Set<Class<?>> scan() {
      return this.scan(false);
   }

   public Set<Class<?>> scan(boolean forceScanJavaClassPaths) {
      Iterator var2 = ResourceUtil.getResourceIter(this.packagePath).iterator();

      while(var2.hasNext()) {
         URL url = (URL)var2.next();
         switch (url.getProtocol()) {
            case "file":
               this.scanFile(new File(URLUtil.decode(url.getFile(), this.charset.name())), (String)null);
               break;
            case "jar":
               this.scanJar(URLUtil.getJarFile(url));
         }
      }

      if (forceScanJavaClassPaths || CollUtil.isEmpty((Collection)this.classes)) {
         this.scanJavaClassPaths();
      }

      return Collections.unmodifiableSet(this.classes);
   }

   public void setInitialize(boolean initialize) {
      this.initialize = initialize;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   private void scanJavaClassPaths() {
      String[] javaClassPaths = ClassUtil.getJavaClassPaths();
      String[] var2 = javaClassPaths;
      int var3 = javaClassPaths.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String classPath = var2[var4];
         classPath = URLUtil.decode(classPath, CharsetUtil.systemCharsetName());
         this.scanFile(new File(classPath), (String)null);
      }

   }

   private void scanFile(File file, String rootDir) {
      if (file.isFile()) {
         String fileName = file.getAbsolutePath();
         if (fileName.endsWith(".class")) {
            String className = fileName.substring(rootDir.length(), fileName.length() - 6).replace(File.separatorChar, '.');
            this.addIfAccept(className);
         } else if (fileName.endsWith(".jar")) {
            try {
               this.scanJar(new JarFile(file));
            } catch (IOException var8) {
               throw new IORuntimeException(var8);
            }
         }
      } else if (file.isDirectory()) {
         File[] files = file.listFiles();
         if (null != files) {
            File[] var10 = files;
            int var5 = files.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File subFile = var10[var6];
               this.scanFile(subFile, null == rootDir ? this.subPathBeforePackage(file) : rootDir);
            }
         }
      }

   }

   private void scanJar(JarFile jar) {
      Iterator var3 = (new EnumerationIter(jar.entries())).iterator();

      while(true) {
         String name;
         JarEntry entry;
         do {
            if (!var3.hasNext()) {
               return;
            }

            entry = (JarEntry)var3.next();
            name = StrUtil.removePrefix(entry.getName(), "/");
         } while(!StrUtil.isEmpty(this.packagePath) && !name.startsWith(this.packagePath));

         if (name.endsWith(".class") && !entry.isDirectory()) {
            String className = name.substring(0, name.length() - 6).replace('/', '.');
            this.addIfAccept(this.loadClass(className));
         }
      }
   }

   private Class<?> loadClass(String className) {
      ClassLoader loader = this.classLoader;
      if (null == loader) {
         loader = ClassLoaderUtil.getClassLoader();
         this.classLoader = loader;
      }

      Class<?> clazz = null;

      try {
         clazz = Class.forName(className, this.initialize, loader);
      } catch (ClassNotFoundException | NoClassDefFoundError var5) {
      } catch (UnsupportedClassVersionError var6) {
      } catch (Exception var7) {
         throw new RuntimeException(var7);
      }

      return clazz;
   }

   private void addIfAccept(String className) {
      if (!StrUtil.isBlank(className)) {
         int classLen = className.length();
         int packageLen = this.packageName.length();
         if (classLen == packageLen) {
            if (className.equals(this.packageName)) {
               this.addIfAccept(this.loadClass(className));
            }
         } else if (classLen > packageLen && (".".equals(this.packageNameWithDot) || className.startsWith(this.packageNameWithDot))) {
            this.addIfAccept(this.loadClass(className));
         }

      }
   }

   private void addIfAccept(Class<?> clazz) {
      if (null != clazz) {
         Filter<Class<?>> classFilter = this.classFilter;
         if (classFilter == null || classFilter.accept(clazz)) {
            this.classes.add(clazz);
         }
      }

   }

   private String subPathBeforePackage(File file) {
      String filePath = file.getAbsolutePath();
      if (StrUtil.isNotEmpty(this.packageDirName)) {
         filePath = StrUtil.subBefore(filePath, this.packageDirName, true);
      }

      return StrUtil.addSuffixIfNot(filePath, File.separator);
   }
}
