/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.security.SecureClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.script.Compilable;
/*     */ import javax.script.CompiledScript;
/*     */ import javax.script.ScriptEngineManager;
/*     */ import javax.script.ScriptException;
/*     */ import javax.tools.FileObject;
/*     */ import javax.tools.ForwardingJavaFileManager;
/*     */ import javax.tools.JavaCompiler;
/*     */ import javax.tools.JavaFileManager;
/*     */ import javax.tools.JavaFileObject;
/*     */ import javax.tools.SimpleJavaFileObject;
/*     */ import javax.tools.StandardJavaFileManager;
/*     */ import javax.tools.ToolProvider;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
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
/*     */ public class SourceCompiler
/*     */ {
/*     */   static final JavaCompiler JAVA_COMPILER;
/*     */   private static final Class<?> JAVAC_SUN;
/*  61 */   private static final String COMPILE_DIR = Utils.getProperty("java.io.tmpdir", ".");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   final HashMap<String, String> sources = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   final HashMap<String, Class<?>> compiled = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   final Map<String, CompiledScript> compiledScripts = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   boolean useJavaSystemCompiler = SysProperties.JAVA_SYSTEM_COMPILER;
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/*  86 */       javaCompiler = ToolProvider.getSystemJavaCompiler();
/*  87 */     } catch (Exception null) {
/*     */       
/*  89 */       javaCompiler = null;
/*     */     } 
/*  91 */     JAVA_COMPILER = javaCompiler;
/*     */     
/*     */     try {
/*  94 */       clazz = Class.forName("com.sun.tools.javac.Main");
/*  95 */     } catch (Exception exception) {
/*  96 */       clazz = null;
/*     */     } 
/*  98 */     JAVAC_SUN = clazz;
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/*     */     JavaCompiler javaCompiler;
/*     */     Class clazz;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSource(String paramString1, String paramString2) {
/* 109 */     this.sources.put(paramString1, paramString2);
/* 110 */     this.compiled.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setJavaSystemCompiler(boolean paramBoolean) {
/* 119 */     this.useJavaSystemCompiler = paramBoolean;
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
/*     */   public Class<?> getClass(String paramString) throws ClassNotFoundException {
/* 132 */     Class<?> clazz = this.compiled.get(paramString);
/* 133 */     if (clazz != null) {
/* 134 */       return clazz;
/*     */     }
/* 136 */     String str = this.sources.get(paramString);
/* 137 */     if (isGroovySource(str)) {
/* 138 */       Class<?> clazz1 = GroovyCompiler.parseClass(str, paramString);
/* 139 */       this.compiled.put(paramString, clazz1);
/* 140 */       return clazz1;
/*     */     } 
/*     */     
/* 143 */     ClassLoader classLoader = new ClassLoader(getClass().getClassLoader())
/*     */       {
/*     */         public Class<?> findClass(String param1String) throws ClassNotFoundException
/*     */         {
/* 147 */           Class<?> clazz = SourceCompiler.this.compiled.get(param1String);
/* 148 */           if (clazz == null) {
/* 149 */             String str3, str1 = SourceCompiler.this.sources.get(param1String);
/* 150 */             String str2 = null;
/* 151 */             int i = param1String.lastIndexOf('.');
/*     */             
/* 153 */             if (i >= 0) {
/* 154 */               str2 = param1String.substring(0, i);
/* 155 */               str3 = param1String.substring(i + 1);
/*     */             } else {
/* 157 */               str3 = param1String;
/*     */             } 
/* 159 */             String str4 = SourceCompiler.getCompleteSourceCode(str2, str3, str1);
/* 160 */             if (SourceCompiler.JAVA_COMPILER != null && SourceCompiler.this.useJavaSystemCompiler) {
/* 161 */               clazz = SourceCompiler.this.javaxToolsJavac(str2, str3, str4);
/*     */             } else {
/* 163 */               byte[] arrayOfByte = SourceCompiler.this.javacCompile(str2, str3, str4);
/* 164 */               if (arrayOfByte == null) {
/* 165 */                 clazz = findSystemClass(param1String);
/*     */               } else {
/* 167 */                 clazz = defineClass(param1String, arrayOfByte, 0, arrayOfByte.length);
/*     */               } 
/*     */             } 
/* 170 */             SourceCompiler.this.compiled.put(param1String, clazz);
/*     */           } 
/* 172 */           return clazz;
/*     */         }
/*     */       };
/* 175 */     return classLoader.loadClass(paramString);
/*     */   }
/*     */   
/*     */   private static boolean isGroovySource(String paramString) {
/* 179 */     return (paramString.startsWith("//groovy") || paramString.startsWith("@groovy"));
/*     */   }
/*     */   
/*     */   private static boolean isJavascriptSource(String paramString) {
/* 183 */     return paramString.startsWith("//javascript");
/*     */   }
/*     */   
/*     */   private static boolean isRubySource(String paramString) {
/* 187 */     return paramString.startsWith("#ruby");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJavaxScriptSource(String paramString) {
/* 197 */     return (isJavascriptSource(paramString) || isRubySource(paramString));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompiledScript getCompiledScript(String paramString) throws ScriptException {
/* 208 */     CompiledScript compiledScript = this.compiledScripts.get(paramString);
/* 209 */     if (compiledScript == null) {
/* 210 */       String str2, str1 = this.sources.get(paramString);
/*     */       
/* 212 */       if (isJavascriptSource(str1)) {
/* 213 */         str2 = "javascript";
/* 214 */       } else if (isRubySource(str1)) {
/* 215 */         str2 = "ruby";
/*     */       } else {
/* 217 */         throw new IllegalStateException("Unknown language for " + str1);
/*     */       } 
/*     */       
/* 220 */       Compilable compilable = (Compilable)(new ScriptEngineManager()).getEngineByName(str2);
/* 221 */       compiledScript = compilable.compile(str1);
/* 222 */       this.compiledScripts.put(paramString, compiledScript);
/*     */     } 
/* 224 */     return compiledScript;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getMethod(String paramString) throws ClassNotFoundException {
/* 235 */     Class<?> clazz = getClass(paramString);
/* 236 */     Method[] arrayOfMethod = clazz.getDeclaredMethods();
/* 237 */     for (Method method : arrayOfMethod) {
/* 238 */       int i = method.getModifiers();
/* 239 */       if (Modifier.isPublic(i) && Modifier.isStatic(i)) {
/* 240 */         String str = method.getName();
/* 241 */         if (!str.startsWith("_") && !method.getName().equals("main")) {
/* 242 */           return method;
/*     */         }
/*     */       } 
/*     */     } 
/* 246 */     return null;
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
/*     */   byte[] javacCompile(String paramString1, String paramString2, String paramString3) {
/* 260 */     Path path1 = Paths.get(COMPILE_DIR, new String[0]);
/* 261 */     if (paramString1 != null) {
/* 262 */       path1 = path1.resolve(paramString1.replace('.', '/'));
/*     */       try {
/* 264 */         Files.createDirectories(path1, (FileAttribute<?>[])new FileAttribute[0]);
/* 265 */       } catch (Exception exception) {
/* 266 */         throw DbException.convert(exception);
/*     */       } 
/*     */     } 
/* 269 */     Path path2 = path1.resolve(paramString2 + ".java");
/* 270 */     Path path3 = path1.resolve(paramString2 + ".class");
/*     */     try {
/* 272 */       Files.write(path2, paramString3.getBytes(StandardCharsets.UTF_8), new java.nio.file.OpenOption[0]);
/* 273 */       Files.deleteIfExists(path3);
/* 274 */       if (JAVAC_SUN != null) {
/* 275 */         javacSun(path2);
/*     */       } else {
/* 277 */         javacProcess(path2);
/*     */       } 
/* 279 */       return Files.readAllBytes(path3);
/* 280 */     } catch (Exception exception) {
/* 281 */       throw DbException.convert(exception);
/*     */     } finally {
/*     */       try {
/* 284 */         Files.deleteIfExists(path2);
/* 285 */       } catch (IOException iOException) {}
/*     */       
/*     */       try {
/* 288 */         Files.deleteIfExists(path3);
/* 289 */       } catch (IOException iOException) {}
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
/*     */   static String getCompleteSourceCode(String paramString1, String paramString2, String paramString3) {
/* 305 */     if (paramString3.startsWith("package ")) {
/* 306 */       return paramString3;
/*     */     }
/* 308 */     StringBuilder stringBuilder = new StringBuilder();
/* 309 */     if (paramString1 != null) {
/* 310 */       stringBuilder.append("package ").append(paramString1).append(";\n");
/*     */     }
/* 312 */     int i = paramString3.indexOf("@CODE");
/* 313 */     String str = "import java.util.*;\nimport java.math.*;\nimport java.sql.*;\n";
/*     */ 
/*     */ 
/*     */     
/* 317 */     if (i >= 0) {
/* 318 */       str = paramString3.substring(0, i);
/* 319 */       paramString3 = paramString3.substring("@CODE".length() + i);
/*     */     } 
/* 321 */     stringBuilder.append(str);
/* 322 */     stringBuilder.append("public class ").append(paramString2).append(" {\n    public static ")
/*     */       
/* 324 */       .append(paramString3).append("\n}\n");
/*     */     
/* 326 */     return stringBuilder.toString();
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
/*     */   Class<?> javaxToolsJavac(String paramString1, String paramString2, String paramString3) {
/* 338 */     String str = paramString1 + "." + paramString2;
/* 339 */     StringWriter stringWriter = new StringWriter();
/* 340 */     try (ClassFileManager null = new ClassFileManager(JAVA_COMPILER
/*     */           
/* 342 */           .getStandardFileManager(null, null, null))) {
/* 343 */       boolean bool; ArrayList<StringJavaFileObject> arrayList = new ArrayList();
/* 344 */       arrayList.add(new StringJavaFileObject(str, paramString3));
/*     */ 
/*     */       
/* 347 */       synchronized (JAVA_COMPILER) {
/* 348 */         bool = JAVA_COMPILER.getTask(stringWriter, classFileManager, null, null, null, (Iterable)arrayList)
/* 349 */           .call().booleanValue();
/*     */       } 
/* 351 */       String str1 = stringWriter.toString();
/* 352 */       handleSyntaxError(str1, bool ? 0 : 1);
/* 353 */       return classFileManager.getClassLoader(null).loadClass(str);
/* 354 */     } catch (ClassNotFoundException|IOException classNotFoundException) {
/* 355 */       throw DbException.convert(classNotFoundException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void javacProcess(Path paramPath) {
/* 360 */     exec(new String[] { "javac", "-sourcepath", COMPILE_DIR, "-d", COMPILE_DIR, "-encoding", "UTF-8", paramPath
/*     */ 
/*     */ 
/*     */           
/* 364 */           .toAbsolutePath().toString() });
/*     */   }
/*     */   
/*     */   private static int exec(String... paramVarArgs) {
/* 368 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*     */     try {
/* 370 */       ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 375 */       processBuilder.environment().remove("JAVA_TOOL_OPTIONS");
/* 376 */       processBuilder.command(paramVarArgs);
/*     */       
/* 378 */       Process process = processBuilder.start();
/* 379 */       copyInThread(process.getInputStream(), byteArrayOutputStream);
/* 380 */       copyInThread(process.getErrorStream(), byteArrayOutputStream);
/* 381 */       process.waitFor();
/* 382 */       String str = Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8);
/* 383 */       handleSyntaxError(str, process.exitValue());
/* 384 */       return process.exitValue();
/* 385 */     } catch (Exception exception) {
/* 386 */       throw DbException.convert(exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void copyInThread(final InputStream in, final OutputStream out) {
/* 391 */     (new Task()
/*     */       {
/*     */         public void call() throws IOException {
/* 394 */           IOUtils.copy(in, out);
/*     */         }
/* 396 */       }).execute();
/*     */   }
/*     */   
/*     */   private static synchronized void javacSun(Path paramPath) {
/* 400 */     PrintStream printStream = System.err;
/* 401 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*     */     try {
/* 403 */       System.setErr(new PrintStream(byteArrayOutputStream, false, "UTF-8"));
/*     */       
/* 405 */       Method method = JAVAC_SUN.getMethod("compile", new Class[] { String[].class });
/* 406 */       Object object = JAVAC_SUN.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*     */ 
/*     */ 
/*     */       
/* 410 */       Integer integer = (Integer)method.invoke(object, new Object[] { { "-sourcepath", COMPILE_DIR, "-d", COMPILE_DIR, "-encoding", "UTF-8", paramPath
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 415 */               .toAbsolutePath().toString() } });
/* 416 */       String str = Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8);
/* 417 */       handleSyntaxError(str, integer.intValue());
/* 418 */     } catch (Exception exception) {
/* 419 */       throw DbException.convert(exception);
/*     */     } finally {
/* 421 */       System.setErr(printStream);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void handleSyntaxError(String paramString, int paramInt) {
/* 426 */     if (0 == paramInt) {
/*     */       return;
/*     */     }
/* 429 */     boolean bool = false;
/* 430 */     BufferedReader bufferedReader = new BufferedReader(new StringReader(paramString)); try {
/*     */       String str;
/* 432 */       while ((str = bufferedReader.readLine()) != null) {
/* 433 */         if (str.endsWith("warning") || str.endsWith("warnings"))
/*     */           continue; 
/* 435 */         if (str.startsWith("Note:") || str
/* 436 */           .startsWith("warning:")) {
/*     */           continue;
/*     */         }
/* 439 */         bool = true;
/*     */       }
/*     */     
/*     */     }
/* 443 */     catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */     
/* 447 */     if (bool) {
/* 448 */       paramString = StringUtils.replaceAll(paramString, COMPILE_DIR, "");
/* 449 */       throw DbException.get(42000, paramString);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class GroovyCompiler
/*     */   {
/*     */     private static final Object LOADER;
/*     */ 
/*     */     
/*     */     private static final Throwable INIT_FAIL_EXCEPTION;
/*     */ 
/*     */     
/*     */     static {
/* 464 */       Object object = null;
/* 465 */       Exception exception = null;
/*     */       
/*     */       try {
/* 468 */         Class<?> clazz = Class.forName("org.codehaus.groovy.control.customizers.ImportCustomizer");
/*     */         
/* 470 */         Object object1 = Utils.newInstance("org.codehaus.groovy.control.customizers.ImportCustomizer", new Object[0]);
/*     */ 
/*     */         
/* 473 */         String[] arrayOfString = { "java.sql.Connection", "java.sql.Types", "java.sql.ResultSet", "groovy.sql.Sql", "org.h2.tools.SimpleResultSet" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 480 */         Utils.callMethod(object1, "addImports", new Object[] { arrayOfString });
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 485 */         Object object2 = Array.newInstance(clazz, 1);
/* 486 */         Array.set(object2, 0, object1);
/* 487 */         Object object3 = Utils.newInstance("org.codehaus.groovy.control.CompilerConfiguration", new Object[0]);
/*     */         
/* 489 */         Utils.callMethod(object3, "addCompilationCustomizers", new Object[] { object2 });
/*     */ 
/*     */         
/* 492 */         ClassLoader classLoader = GroovyCompiler.class.getClassLoader();
/* 493 */         object = Utils.newInstance("groovy.lang.GroovyClassLoader", new Object[] { classLoader, object3 });
/*     */       }
/* 495 */       catch (Exception exception1) {
/* 496 */         exception = exception1;
/*     */       } 
/* 498 */       LOADER = object;
/* 499 */       INIT_FAIL_EXCEPTION = exception;
/*     */     }
/*     */ 
/*     */     
/*     */     public static Class<?> parseClass(String param1String1, String param1String2) {
/* 504 */       if (LOADER == null) {
/* 505 */         throw new RuntimeException("Compile fail: no Groovy jar in the classpath", INIT_FAIL_EXCEPTION);
/*     */       }
/*     */       
/*     */       try {
/* 509 */         Object object = Utils.newInstance("groovy.lang.GroovyCodeSource", new Object[] { param1String1, param1String2 + ".groovy", "UTF-8" });
/*     */         
/* 511 */         Utils.callMethod(object, "setCachable", new Object[] { Boolean.valueOf(false) });
/* 512 */         return (Class)Utils.callMethod(LOADER, "parseClass", new Object[] { object });
/*     */       }
/* 514 */       catch (Exception exception) {
/* 515 */         throw new RuntimeException(exception);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class StringJavaFileObject
/*     */     extends SimpleJavaFileObject
/*     */   {
/*     */     private final String sourceCode;
/*     */ 
/*     */     
/*     */     public StringJavaFileObject(String param1String1, String param1String2) {
/* 528 */       super(URI.create("string:///" + param1String1.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
/*     */       
/* 530 */       this.sourceCode = param1String2;
/*     */     }
/*     */ 
/*     */     
/*     */     public CharSequence getCharContent(boolean param1Boolean) {
/* 535 */       return this.sourceCode;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class JavaClassObject
/*     */     extends SimpleJavaFileObject
/*     */   {
/* 545 */     private final ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */     
/*     */     public JavaClassObject(String param1String, JavaFileObject.Kind param1Kind) {
/* 548 */       super(URI.create("string:///" + param1String.replace('.', '/') + param1Kind.extension), param1Kind);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] getBytes() {
/* 553 */       return this.out.toByteArray();
/*     */     }
/*     */ 
/*     */     
/*     */     public OutputStream openOutputStream() throws IOException {
/* 558 */       return this.out;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class ClassFileManager
/*     */     extends ForwardingJavaFileManager<StandardJavaFileManager>
/*     */   {
/* 571 */     Map<String, SourceCompiler.JavaClassObject> classObjectsByName = new HashMap<>();
/*     */     
/* 573 */     private SecureClassLoader classLoader = new SecureClassLoader()
/*     */       {
/*     */         
/*     */         protected Class<?> findClass(String param2String) throws ClassNotFoundException
/*     */         {
/* 578 */           byte[] arrayOfByte = ((SourceCompiler.JavaClassObject)SourceCompiler.ClassFileManager.this.classObjectsByName.get(param2String)).getBytes();
/* 579 */           return defineClass(param2String, arrayOfByte, 0, arrayOfByte.length);
/*     */         }
/*     */       };
/*     */ 
/*     */     
/*     */     public ClassFileManager(StandardJavaFileManager param1StandardJavaFileManager) {
/* 585 */       super(param1StandardJavaFileManager);
/*     */     }
/*     */ 
/*     */     
/*     */     public ClassLoader getClassLoader(JavaFileManager.Location param1Location) {
/* 590 */       return this.classLoader;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JavaFileObject getJavaFileForOutput(JavaFileManager.Location param1Location, String param1String, JavaFileObject.Kind param1Kind, FileObject param1FileObject) throws IOException {
/* 596 */       SourceCompiler.JavaClassObject javaClassObject = new SourceCompiler.JavaClassObject(param1String, param1Kind);
/* 597 */       this.classObjectsByName.put(param1String, javaClassObject);
/* 598 */       return javaClassObject;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\SourceCompiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */