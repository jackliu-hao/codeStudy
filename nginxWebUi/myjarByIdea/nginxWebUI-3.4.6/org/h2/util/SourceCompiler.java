package org.h2.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject.Kind;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;

public class SourceCompiler {
   static final JavaCompiler JAVA_COMPILER;
   private static final Class<?> JAVAC_SUN;
   private static final String COMPILE_DIR = Utils.getProperty("java.io.tmpdir", ".");
   final HashMap<String, String> sources = new HashMap();
   final HashMap<String, Class<?>> compiled = new HashMap();
   final Map<String, CompiledScript> compiledScripts = new HashMap();
   boolean useJavaSystemCompiler;

   public SourceCompiler() {
      this.useJavaSystemCompiler = SysProperties.JAVA_SYSTEM_COMPILER;
   }

   public void setSource(String var1, String var2) {
      this.sources.put(var1, var2);
      this.compiled.clear();
   }

   public void setJavaSystemCompiler(boolean var1) {
      this.useJavaSystemCompiler = var1;
   }

   public Class<?> getClass(String var1) throws ClassNotFoundException {
      Class var2 = (Class)this.compiled.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = (String)this.sources.get(var1);
         if (isGroovySource(var3)) {
            Class var5 = SourceCompiler.GroovyCompiler.parseClass(var3, var1);
            this.compiled.put(var1, var5);
            return var5;
         } else {
            ClassLoader var4 = new ClassLoader(this.getClass().getClassLoader()) {
               public Class<?> findClass(String var1) throws ClassNotFoundException {
                  Class var2 = (Class)SourceCompiler.this.compiled.get(var1);
                  if (var2 == null) {
                     String var3 = (String)SourceCompiler.this.sources.get(var1);
                     String var4 = null;
                     int var5 = var1.lastIndexOf(46);
                     String var6;
                     if (var5 >= 0) {
                        var4 = var1.substring(0, var5);
                        var6 = var1.substring(var5 + 1);
                     } else {
                        var6 = var1;
                     }

                     String var7 = SourceCompiler.getCompleteSourceCode(var4, var6, var3);
                     if (SourceCompiler.JAVA_COMPILER != null && SourceCompiler.this.useJavaSystemCompiler) {
                        var2 = SourceCompiler.this.javaxToolsJavac(var4, var6, var7);
                     } else {
                        byte[] var8 = SourceCompiler.this.javacCompile(var4, var6, var7);
                        if (var8 == null) {
                           var2 = this.findSystemClass(var1);
                        } else {
                           var2 = this.defineClass(var1, var8, 0, var8.length);
                        }
                     }

                     SourceCompiler.this.compiled.put(var1, var2);
                  }

                  return var2;
               }
            };
            return var4.loadClass(var1);
         }
      }
   }

   private static boolean isGroovySource(String var0) {
      return var0.startsWith("//groovy") || var0.startsWith("@groovy");
   }

   private static boolean isJavascriptSource(String var0) {
      return var0.startsWith("//javascript");
   }

   private static boolean isRubySource(String var0) {
      return var0.startsWith("#ruby");
   }

   public static boolean isJavaxScriptSource(String var0) {
      return isJavascriptSource(var0) || isRubySource(var0);
   }

   public CompiledScript getCompiledScript(String var1) throws ScriptException {
      CompiledScript var2 = (CompiledScript)this.compiledScripts.get(var1);
      if (var2 == null) {
         String var3 = (String)this.sources.get(var1);
         String var4;
         if (isJavascriptSource(var3)) {
            var4 = "javascript";
         } else {
            if (!isRubySource(var3)) {
               throw new IllegalStateException("Unknown language for " + var3);
            }

            var4 = "ruby";
         }

         Compilable var5 = (Compilable)(new ScriptEngineManager()).getEngineByName(var4);
         var2 = var5.compile(var3);
         this.compiledScripts.put(var1, var2);
      }

      return var2;
   }

   public Method getMethod(String var1) throws ClassNotFoundException {
      Class var2 = this.getClass(var1);
      Method[] var3 = var2.getDeclaredMethods();
      Method[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method var7 = var4[var6];
         int var8 = var7.getModifiers();
         if (Modifier.isPublic(var8) && Modifier.isStatic(var8)) {
            String var9 = var7.getName();
            if (!var9.startsWith("_") && !var7.getName().equals("main")) {
               return var7;
            }
         }
      }

      return null;
   }

   byte[] javacCompile(String var1, String var2, String var3) {
      Path var4 = Paths.get(COMPILE_DIR);
      if (var1 != null) {
         var4 = var4.resolve(var1.replace('.', '/'));

         try {
            Files.createDirectories(var4);
         } catch (Exception var22) {
            throw DbException.convert(var22);
         }
      }

      Path var5 = var4.resolve(var2 + ".java");
      Path var6 = var4.resolve(var2 + ".class");

      byte[] var7;
      try {
         Files.write(var5, var3.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
         Files.deleteIfExists(var6);
         if (JAVAC_SUN != null) {
            javacSun(var5);
         } else {
            javacProcess(var5);
         }

         var7 = Files.readAllBytes(var6);
      } catch (Exception var20) {
         throw DbException.convert(var20);
      } finally {
         try {
            Files.deleteIfExists(var5);
         } catch (IOException var19) {
         }

         try {
            Files.deleteIfExists(var6);
         } catch (IOException var18) {
         }

      }

      return var7;
   }

   static String getCompleteSourceCode(String var0, String var1, String var2) {
      if (var2.startsWith("package ")) {
         return var2;
      } else {
         StringBuilder var3 = new StringBuilder();
         if (var0 != null) {
            var3.append("package ").append(var0).append(";\n");
         }

         int var4 = var2.indexOf("@CODE");
         String var5 = "import java.util.*;\nimport java.math.*;\nimport java.sql.*;\n";
         if (var4 >= 0) {
            var5 = var2.substring(0, var4);
            var2 = var2.substring("@CODE".length() + var4);
         }

         var3.append(var5);
         var3.append("public class ").append(var1).append(" {\n    public static ").append(var2).append("\n}\n");
         return var3.toString();
      }
   }

   Class<?> javaxToolsJavac(String var1, String var2, String var3) {
      String var4 = var1 + "." + var2;
      StringWriter var5 = new StringWriter();

      try {
         ClassFileManager var6 = new ClassFileManager(JAVA_COMPILER.getStandardFileManager((DiagnosticListener)null, (Locale)null, (Charset)null));
         Throwable var7 = null;

         Class var11;
         try {
            ArrayList var8 = new ArrayList();
            var8.add(new StringJavaFileObject(var4, var3));
            boolean var9;
            synchronized(JAVA_COMPILER) {
               var9 = JAVA_COMPILER.getTask(var5, var6, (DiagnosticListener)null, (Iterable)null, (Iterable)null, var8).call();
            }

            String var10 = var5.toString();
            handleSyntaxError(var10, var9 ? 0 : 1);
            var11 = var6.getClassLoader((JavaFileManager.Location)null).loadClass(var4);
         } catch (Throwable var23) {
            var7 = var23;
            throw var23;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var21) {
                     var7.addSuppressed(var21);
                  }
               } else {
                  var6.close();
               }
            }

         }

         return var11;
      } catch (IOException | ClassNotFoundException var25) {
         throw DbException.convert(var25);
      }
   }

   private static void javacProcess(Path var0) {
      exec("javac", "-sourcepath", COMPILE_DIR, "-d", COMPILE_DIR, "-encoding", "UTF-8", var0.toAbsolutePath().toString());
   }

   private static int exec(String... var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      try {
         ProcessBuilder var2 = new ProcessBuilder(new String[0]);
         var2.environment().remove("JAVA_TOOL_OPTIONS");
         var2.command(var0);
         Process var3 = var2.start();
         copyInThread(var3.getInputStream(), var1);
         copyInThread(var3.getErrorStream(), var1);
         var3.waitFor();
         String var4 = Utils10.byteArrayOutputStreamToString(var1, StandardCharsets.UTF_8);
         handleSyntaxError(var4, var3.exitValue());
         return var3.exitValue();
      } catch (Exception var5) {
         throw DbException.convert(var5);
      }
   }

   private static void copyInThread(final InputStream var0, final OutputStream var1) {
      (new Task() {
         public void call() throws IOException {
            IOUtils.copy(var0, var1);
         }
      }).execute();
   }

   private static synchronized void javacSun(Path var0) {
      PrintStream var1 = System.err;
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         System.setErr(new PrintStream(var2, false, "UTF-8"));
         Method var3 = JAVAC_SUN.getMethod("compile", String[].class);
         Object var4 = JAVAC_SUN.getDeclaredConstructor().newInstance();
         Integer var5 = (Integer)var3.invoke(var4, new String[]{"-sourcepath", COMPILE_DIR, "-d", COMPILE_DIR, "-encoding", "UTF-8", var0.toAbsolutePath().toString()});
         String var6 = Utils10.byteArrayOutputStreamToString(var2, StandardCharsets.UTF_8);
         handleSyntaxError(var6, var5);
      } catch (Exception var10) {
         throw DbException.convert(var10);
      } finally {
         System.setErr(var1);
      }

   }

   private static void handleSyntaxError(String var0, int var1) {
      if (0 != var1) {
         boolean var2 = false;
         BufferedReader var3 = new BufferedReader(new StringReader(var0));

         String var4;
         try {
            while((var4 = var3.readLine()) != null) {
               if (!var4.endsWith("warning") && !var4.endsWith("warnings") && !var4.startsWith("Note:") && !var4.startsWith("warning:")) {
                  var2 = true;
                  break;
               }
            }
         } catch (IOException var5) {
         }

         if (var2) {
            var0 = StringUtils.replaceAll(var0, COMPILE_DIR, "");
            throw DbException.get(42000, (String)var0);
         }
      }
   }

   static {
      JavaCompiler var0;
      try {
         var0 = ToolProvider.getSystemJavaCompiler();
      } catch (Exception var4) {
         var0 = null;
      }

      JAVA_COMPILER = var0;

      Class var1;
      try {
         var1 = Class.forName("com.sun.tools.javac.Main");
      } catch (Exception var3) {
         var1 = null;
      }

      JAVAC_SUN = var1;
   }

   static class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
      Map<String, JavaClassObject> classObjectsByName = new HashMap();
      private SecureClassLoader classLoader = new SecureClassLoader() {
         protected Class<?> findClass(String var1) throws ClassNotFoundException {
            byte[] var2 = ((JavaClassObject)ClassFileManager.this.classObjectsByName.get(var1)).getBytes();
            return super.defineClass(var1, var2, 0, var2.length);
         }
      };

      public ClassFileManager(StandardJavaFileManager var1) {
         super(var1);
      }

      public ClassLoader getClassLoader(JavaFileManager.Location var1) {
         return this.classLoader;
      }

      public JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException {
         JavaClassObject var5 = new JavaClassObject(var2, var3);
         this.classObjectsByName.put(var2, var5);
         return var5;
      }
   }

   static class JavaClassObject extends SimpleJavaFileObject {
      private final ByteArrayOutputStream out = new ByteArrayOutputStream();

      public JavaClassObject(String var1, JavaFileObject.Kind var2) {
         super(URI.create("string:///" + var1.replace('.', '/') + var2.extension), var2);
      }

      public byte[] getBytes() {
         return this.out.toByteArray();
      }

      public OutputStream openOutputStream() throws IOException {
         return this.out;
      }
   }

   static class StringJavaFileObject extends SimpleJavaFileObject {
      private final String sourceCode;

      public StringJavaFileObject(String var1, String var2) {
         super(URI.create("string:///" + var1.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
         this.sourceCode = var2;
      }

      public CharSequence getCharContent(boolean var1) {
         return this.sourceCode;
      }
   }

   private static final class GroovyCompiler {
      private static final Object LOADER;
      private static final Throwable INIT_FAIL_EXCEPTION;

      public static Class<?> parseClass(String var0, String var1) {
         if (LOADER == null) {
            throw new RuntimeException("Compile fail: no Groovy jar in the classpath", INIT_FAIL_EXCEPTION);
         } else {
            try {
               Object var2 = Utils.newInstance("groovy.lang.GroovyCodeSource", var0, var1 + ".groovy", "UTF-8");
               Utils.callMethod(var2, "setCachable", false);
               return (Class)Utils.callMethod(LOADER, "parseClass", var2);
            } catch (Exception var3) {
               throw new RuntimeException(var3);
            }
         }
      }

      static {
         Object var0 = null;
         Exception var1 = null;

         try {
            Class var2 = Class.forName("org.codehaus.groovy.control.customizers.ImportCustomizer");
            Object var3 = Utils.newInstance("org.codehaus.groovy.control.customizers.ImportCustomizer");
            String[] var4 = new String[]{"java.sql.Connection", "java.sql.Types", "java.sql.ResultSet", "groovy.sql.Sql", "org.h2.tools.SimpleResultSet"};
            Utils.callMethod(var3, "addImports", var4);
            Object var5 = Array.newInstance(var2, 1);
            Array.set(var5, 0, var3);
            Object var6 = Utils.newInstance("org.codehaus.groovy.control.CompilerConfiguration");
            Utils.callMethod(var6, "addCompilationCustomizers", var5);
            ClassLoader var7 = GroovyCompiler.class.getClassLoader();
            var0 = Utils.newInstance("groovy.lang.GroovyClassLoader", var7, var6);
         } catch (Exception var8) {
            var1 = var8;
         }

         LOADER = var0;
         INIT_FAIL_EXCEPTION = var1;
      }
   }
}
