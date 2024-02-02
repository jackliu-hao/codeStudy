package cn.hutool.core.compiler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.StringResource;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class JavaSourceCompiler {
   private final List<Resource> sourceList = new ArrayList();
   private final List<File> libraryFileList = new ArrayList();
   private final ClassLoader parentClassLoader;

   public static JavaSourceCompiler create(ClassLoader parent) {
      return new JavaSourceCompiler(parent);
   }

   private JavaSourceCompiler(ClassLoader parent) {
      this.parentClassLoader = (ClassLoader)ObjectUtil.defaultIfNull(parent, (Supplier)(ClassLoaderUtil::getClassLoader));
   }

   public JavaSourceCompiler addSource(Resource... resources) {
      if (ArrayUtil.isNotEmpty((Object[])resources)) {
         this.sourceList.addAll(Arrays.asList(resources));
      }

      return this;
   }

   public JavaSourceCompiler addSource(File... files) {
      if (ArrayUtil.isNotEmpty((Object[])files)) {
         File[] var2 = files;
         int var3 = files.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            this.sourceList.add(new FileResource(file));
         }
      }

      return this;
   }

   public JavaSourceCompiler addSource(Map<String, String> sourceCodeMap) {
      if (MapUtil.isNotEmpty(sourceCodeMap)) {
         sourceCodeMap.forEach(this::addSource);
      }

      return this;
   }

   public JavaSourceCompiler addSource(String className, String sourceCode) {
      if (className != null && sourceCode != null) {
         this.sourceList.add(new StringResource(sourceCode, className));
      }

      return this;
   }

   public JavaSourceCompiler addLibrary(File... files) {
      if (ArrayUtil.isNotEmpty((Object[])files)) {
         this.libraryFileList.addAll(Arrays.asList(files));
      }

      return this;
   }

   public ClassLoader compile() {
      List<File> classPath = this.getClassPath();
      URL[] urLs = URLUtil.getURLs((File[])classPath.toArray(new File[0]));
      URLClassLoader ucl = URLClassLoader.newInstance(urLs, this.parentClassLoader);
      if (this.sourceList.isEmpty()) {
         return ucl;
      } else {
         JavaClassFileManager javaFileManager = new JavaClassFileManager(ucl, CompilerUtil.getFileManager());
         List<String> options = new ArrayList();
         if (!classPath.isEmpty()) {
            List<String> cp = CollUtil.map(classPath, File::getAbsolutePath, true);
            options.add("-cp");
            options.add(CollUtil.join((Iterable)cp, FileUtil.isWindows() ? ";" : ":"));
         }

         DiagnosticCollector<? super JavaFileObject> diagnosticCollector = new DiagnosticCollector();
         List<JavaFileObject> javaFileObjectList = this.getJavaFileObject();
         JavaCompiler.CompilationTask task = CompilerUtil.getTask(javaFileManager, diagnosticCollector, options, javaFileObjectList);

         ClassLoader var9;
         try {
            if (!task.call()) {
               throw new CompilerException(DiagnosticUtil.getMessages(diagnosticCollector));
            }

            var9 = javaFileManager.getClassLoader(StandardLocation.CLASS_OUTPUT);
         } finally {
            IoUtil.close(javaFileManager);
         }

         return var9;
      }
   }

   private List<File> getClassPath() {
      List<File> classPathFileList = new ArrayList();
      Iterator var2 = this.libraryFileList.iterator();

      while(var2.hasNext()) {
         File file = (File)var2.next();
         List<File> jarOrZipFile = FileUtil.loopFiles(file, (subFile) -> {
            return JavaFileObjectUtil.isJarOrZipFile(subFile.getName());
         });
         classPathFileList.addAll(jarOrZipFile);
         if (file.isDirectory()) {
            classPathFileList.add(file);
         }
      }

      return classPathFileList;
   }

   private List<JavaFileObject> getJavaFileObject() {
      List<JavaFileObject> list = new ArrayList();
      Iterator var2 = this.sourceList.iterator();

      while(var2.hasNext()) {
         Resource resource = (Resource)var2.next();
         if (resource instanceof FileResource) {
            File file = ((FileResource)resource).getFile();
            FileUtil.walkFiles(file, (subFile) -> {
               list.addAll(JavaFileObjectUtil.getJavaFileObjects(file));
            });
         } else {
            list.add(new JavaSourceFileObject(resource.getName(), resource.getStream()));
         }
      }

      return list;
   }

   private Collection<JavaFileObject> getJavaFileObjectByMap(Map<String, String> sourceCodeMap) {
      return (Collection)(MapUtil.isNotEmpty(sourceCodeMap) ? (Collection)sourceCodeMap.entrySet().stream().map((entry) -> {
         return new JavaSourceFileObject((String)entry.getKey(), (String)entry.getValue(), CharsetUtil.CHARSET_UTF_8);
      }).collect(Collectors.toList()) : Collections.emptySet());
   }

   private JavaFileObject getJavaFileObjectByJavaFile(File file) {
      return new JavaSourceFileObject(file.toURI());
   }
}
