/*     */ package cn.hutool.core.compiler;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.FileResource;
/*     */ import cn.hutool.core.io.resource.Resource;
/*     */ import cn.hutool.core.io.resource.StringResource;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.tools.DiagnosticCollector;
/*     */ import javax.tools.JavaCompiler;
/*     */ import javax.tools.JavaFileObject;
/*     */ import javax.tools.StandardLocation;
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
/*     */ public class JavaSourceCompiler
/*     */ {
/*  64 */   private final List<Resource> sourceList = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   private final List<File> libraryFileList = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ClassLoader parentClassLoader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaSourceCompiler create(ClassLoader parent) {
/*  83 */     return new JavaSourceCompiler(parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JavaSourceCompiler(ClassLoader parent) {
/*  92 */     this.parentClassLoader = (ClassLoader)ObjectUtil.defaultIfNull(parent, ClassLoaderUtil::getClassLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaSourceCompiler addSource(Resource... resources) {
/* 103 */     if (ArrayUtil.isNotEmpty((Object[])resources)) {
/* 104 */       this.sourceList.addAll(Arrays.asList(resources));
/*     */     }
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaSourceCompiler addSource(File... files) {
/* 117 */     if (ArrayUtil.isNotEmpty((Object[])files)) {
/* 118 */       for (File file : files) {
/* 119 */         this.sourceList.add(new FileResource(file));
/*     */       }
/*     */     }
/* 122 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaSourceCompiler addSource(Map<String, String> sourceCodeMap) {
/* 132 */     if (MapUtil.isNotEmpty(sourceCodeMap)) {
/* 133 */       sourceCodeMap.forEach(this::addSource);
/*     */     }
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaSourceCompiler addSource(String className, String sourceCode) {
/* 146 */     if (className != null && sourceCode != null) {
/* 147 */       this.sourceList.add(new StringResource(sourceCode, className));
/*     */     }
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaSourceCompiler addLibrary(File... files) {
/* 159 */     if (ArrayUtil.isNotEmpty((Object[])files)) {
/* 160 */       this.libraryFileList.addAll(Arrays.asList(files));
/*     */     }
/* 162 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassLoader compile() {
/* 172 */     List<File> classPath = getClassPath();
/* 173 */     URL[] urLs = URLUtil.getURLs(classPath.<File>toArray(new File[0]));
/* 174 */     URLClassLoader ucl = URLClassLoader.newInstance(urLs, this.parentClassLoader);
/* 175 */     if (this.sourceList.isEmpty())
/*     */     {
/* 177 */       return ucl;
/*     */     }
/*     */ 
/*     */     
/* 181 */     JavaClassFileManager javaFileManager = new JavaClassFileManager(ucl, CompilerUtil.getFileManager());
/*     */ 
/*     */     
/* 184 */     List<String> options = new ArrayList<>();
/* 185 */     if (false == classPath.isEmpty()) {
/* 186 */       List<String> cp = CollUtil.map(classPath, File::getAbsolutePath, true);
/* 187 */       options.add("-cp");
/* 188 */       options.add(CollUtil.join(cp, FileUtil.isWindows() ? ";" : ":"));
/*     */     } 
/*     */ 
/*     */     
/* 192 */     DiagnosticCollector<? super JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
/* 193 */     List<JavaFileObject> javaFileObjectList = getJavaFileObject();
/* 194 */     JavaCompiler.CompilationTask task = CompilerUtil.getTask(javaFileManager, diagnosticCollector, options, javaFileObjectList);
/*     */     try {
/* 196 */       if (task.call().booleanValue())
/*     */       {
/* 198 */         return javaFileManager.getClassLoader(StandardLocation.CLASS_OUTPUT);
/*     */       }
/*     */     } finally {
/* 201 */       IoUtil.close(javaFileManager);
/*     */     } 
/*     */     
/* 204 */     throw new CompilerException(DiagnosticUtil.getMessages(diagnosticCollector));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<File> getClassPath() {
/* 213 */     List<File> classPathFileList = new ArrayList<>();
/* 214 */     for (File file : this.libraryFileList) {
/* 215 */       List<File> jarOrZipFile = FileUtil.loopFiles(file, subFile -> JavaFileObjectUtil.isJarOrZipFile(subFile.getName()));
/* 216 */       classPathFileList.addAll(jarOrZipFile);
/* 217 */       if (file.isDirectory()) {
/* 218 */         classPathFileList.add(file);
/*     */       }
/*     */     } 
/* 221 */     return classPathFileList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<JavaFileObject> getJavaFileObject() {
/* 230 */     List<JavaFileObject> list = new ArrayList<>();
/*     */     
/* 232 */     for (Resource resource : this.sourceList) {
/* 233 */       if (resource instanceof FileResource) {
/* 234 */         File file = ((FileResource)resource).getFile();
/* 235 */         FileUtil.walkFiles(file, subFile -> list.addAll(JavaFileObjectUtil.getJavaFileObjects(file))); continue;
/*     */       } 
/* 237 */       list.add(new JavaSourceFileObject(resource.getName(), resource.getStream()));
/*     */     } 
/*     */ 
/*     */     
/* 241 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Collection<JavaFileObject> getJavaFileObjectByMap(Map<String, String> sourceCodeMap) {
/* 251 */     if (MapUtil.isNotEmpty(sourceCodeMap)) {
/* 252 */       return (Collection<JavaFileObject>)sourceCodeMap.entrySet().stream()
/* 253 */         .map(entry -> new JavaSourceFileObject((String)entry.getKey(), (String)entry.getValue(), CharsetUtil.CHARSET_UTF_8))
/* 254 */         .collect(Collectors.toList());
/*     */     }
/* 256 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JavaFileObject getJavaFileObjectByJavaFile(File file) {
/* 266 */     return new JavaSourceFileObject(file.toURI());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\JavaSourceCompiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */