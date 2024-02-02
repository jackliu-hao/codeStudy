package freemarker.template.utility;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/** @deprecated */
@Deprecated
public class ToCanonical {
   static Configuration config = Configuration.getDefaultConfiguration();

   /** @deprecated */
   @Deprecated
   public static void main(String[] args) {
      config.setWhitespaceStripping(false);
      if (args.length == 0) {
         usage();
      }

      for(int i = 0; i < args.length; ++i) {
         File f = new File(args[i]);
         if (!f.exists()) {
            System.err.println("File " + f + " doesn't exist.");
         }

         try {
            convertFile(f);
         } catch (Exception var4) {
            System.err.println("Error converting file: " + f);
            var4.printStackTrace();
         }
      }

   }

   static void convertFile(File f) throws IOException {
      File fullPath = f.getAbsoluteFile();
      File dir = fullPath.getParentFile();
      String filename = fullPath.getName();
      File convertedFile = new File(dir, filename + ".canonical");
      config.setDirectoryForTemplateLoading(dir);
      Template template = config.getTemplate(filename);
      FileWriter output = new FileWriter(convertedFile);
      Throwable var7 = null;

      try {
         template.dump((Writer)output);
      } catch (Throwable var16) {
         var7 = var16;
         throw var16;
      } finally {
         if (output != null) {
            if (var7 != null) {
               try {
                  output.close();
               } catch (Throwable var15) {
                  var7.addSuppressed(var15);
               }
            } else {
               output.close();
            }
         }

      }

   }

   static void usage() {
      System.err.println("Usage: java freemarker.template.utility.ToCanonical <filename(s)>");
   }
}
