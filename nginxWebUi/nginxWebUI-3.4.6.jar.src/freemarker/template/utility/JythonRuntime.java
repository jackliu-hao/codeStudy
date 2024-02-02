/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.template.TemplateTransformModel;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import org.python.core.PyObject;
/*    */ import org.python.core.PySystemState;
/*    */ import org.python.util.PythonInterpreter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JythonRuntime
/*    */   extends PythonInterpreter
/*    */   implements TemplateTransformModel
/*    */ {
/*    */   public Writer getWriter(final Writer out, Map args) {
/* 41 */     final StringBuilder buf = new StringBuilder();
/* 42 */     final Environment env = Environment.getCurrentEnvironment();
/* 43 */     return new Writer()
/*    */       {
/*    */         public void write(char[] cbuf, int off, int len) {
/* 46 */           buf.append(cbuf, off, len);
/*    */         }
/*    */ 
/*    */         
/*    */         public void flush() throws IOException {
/* 51 */           interpretBuffer();
/* 52 */           out.flush();
/*    */         }
/*    */ 
/*    */         
/*    */         public void close() {
/* 57 */           interpretBuffer();
/*    */         }
/*    */         
/*    */         private void interpretBuffer() {
/* 61 */           synchronized (JythonRuntime.this) {
/* 62 */             PyObject prevOut = JythonRuntime.this.systemState.stdout;
/*    */             try {
/* 64 */               JythonRuntime.this.setOut(out);
/* 65 */               JythonRuntime.this.set("env", env);
/* 66 */               JythonRuntime.this.exec(buf.toString());
/* 67 */               buf.setLength(0);
/*    */             } finally {
/* 69 */               JythonRuntime.this.setOut(prevOut);
/*    */             } 
/*    */           } 
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\JythonRuntime.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */