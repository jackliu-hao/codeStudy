/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import java.io.Writer;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Locale;
/*    */ import javax.tools.DiagnosticListener;
/*    */ import javax.tools.JavaCompiler;
/*    */ import javax.tools.JavaFileManager;
/*    */ import javax.tools.JavaFileObject;
/*    */ import javax.tools.StandardJavaFileManager;
/*    */ import javax.tools.ToolProvider;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompilerUtil
/*    */ {
/* 21 */   public static final JavaCompiler SYSTEM_COMPILER = ToolProvider.getSystemJavaCompiler();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean compile(String... sourceFiles) {
/* 30 */     return (0 == SYSTEM_COMPILER.run(null, null, null, sourceFiles));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static StandardJavaFileManager getFileManager() {
/* 39 */     return getFileManager(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static StandardJavaFileManager getFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener) {
/* 50 */     return SYSTEM_COMPILER.getStandardFileManager(diagnosticListener, (Locale)null, (Charset)null);
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
/*    */ 
/*    */   
/*    */   public static JavaCompiler.CompilationTask getTask(JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> diagnosticListener, Iterable<String> options, Iterable<? extends JavaFileObject> compilationUnits) {
/* 67 */     return SYSTEM_COMPILER.getTask((Writer)null, fileManager, diagnosticListener, options, (Iterable<String>)null, compilationUnits);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static JavaSourceCompiler getCompiler(ClassLoader parent) {
/* 78 */     return JavaSourceCompiler.create(parent);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\CompilerUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */