package freemarker.template.utility;

import freemarker.template.TemplateTransformModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class NormalizeNewlines implements TemplateTransformModel {
   public Writer getWriter(final Writer out, Map args) {
      final StringBuilder buf = new StringBuilder();
      return new Writer() {
         public void write(char[] cbuf, int off, int len) {
            buf.append(cbuf, off, len);
         }

         public void flush() throws IOException {
            out.flush();
         }

         public void close() throws IOException {
            StringReader sr = new StringReader(buf.toString());
            StringWriter sw = new StringWriter();
            NormalizeNewlines.this.transform(sr, sw);
            out.write(sw.toString());
         }
      };
   }

   public void transform(Reader in, Writer out) throws IOException {
      BufferedReader br = in instanceof BufferedReader ? (BufferedReader)in : new BufferedReader(in);
      PrintWriter pw = out instanceof PrintWriter ? (PrintWriter)out : new PrintWriter(out);
      String line = br.readLine();
      if (line != null && line.length() > 0) {
         pw.println(line);
      }

      while((line = br.readLine()) != null) {
         pw.println(line);
      }

   }
}
