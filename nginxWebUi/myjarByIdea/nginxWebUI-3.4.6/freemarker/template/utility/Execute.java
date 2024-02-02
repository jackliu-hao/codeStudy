package freemarker.template.utility;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class Execute implements TemplateMethodModel {
   private static final int OUTPUT_BUFFER_SIZE = 1024;

   public Object exec(List arguments) throws TemplateModelException {
      StringBuilder aOutputBuffer = new StringBuilder();
      if (arguments.size() < 1) {
         throw new TemplateModelException("Need an argument to execute");
      } else {
         String aExecute = (String)((String)arguments.get(0));

         try {
            Process exec = Runtime.getRuntime().exec(aExecute);
            InputStream execOut = exec.getInputStream();
            Throwable var6 = null;

            try {
               Reader execReader = new InputStreamReader(execOut);
               char[] buffer = new char[1024];

               for(int bytes_read = execReader.read(buffer); bytes_read > 0; bytes_read = execReader.read(buffer)) {
                  aOutputBuffer.append(buffer, 0, bytes_read);
               }
            } catch (Throwable var18) {
               var6 = var18;
               throw var18;
            } finally {
               if (execOut != null) {
                  if (var6 != null) {
                     try {
                        execOut.close();
                     } catch (Throwable var17) {
                        var6.addSuppressed(var17);
                     }
                  } else {
                     execOut.close();
                  }
               }

            }
         } catch (IOException var20) {
            throw new TemplateModelException(var20.getMessage());
         }

         return aOutputBuffer.toString();
      }
   }
}
