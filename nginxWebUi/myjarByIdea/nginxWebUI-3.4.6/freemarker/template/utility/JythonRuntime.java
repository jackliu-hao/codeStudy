package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class JythonRuntime extends PythonInterpreter implements TemplateTransformModel {
   public Writer getWriter(final Writer out, Map args) {
      final StringBuilder buf = new StringBuilder();
      final Environment env = Environment.getCurrentEnvironment();
      return new Writer() {
         public void write(char[] cbuf, int off, int len) {
            buf.append(cbuf, off, len);
         }

         public void flush() throws IOException {
            this.interpretBuffer();
            out.flush();
         }

         public void close() {
            this.interpretBuffer();
         }

         private void interpretBuffer() {
            synchronized(JythonRuntime.this) {
               PyObject prevOut = JythonRuntime.this.systemState.stdout;

               try {
                  JythonRuntime.this.setOut(out);
                  JythonRuntime.this.set("env", env);
                  JythonRuntime.this.exec(buf.toString());
                  buf.setLength(0);
               } finally {
                  JythonRuntime.this.setOut(prevOut);
               }

            }
         }
      };
   }
}
