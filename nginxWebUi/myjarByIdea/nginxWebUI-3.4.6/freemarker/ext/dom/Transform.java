package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.StringTokenizer;

/** @deprecated */
@Deprecated
public class Transform {
   private File inputFile;
   private File ftlFile;
   private File outputFile;
   private String encoding;
   private Locale locale;
   private Configuration cfg;

   /** @deprecated */
   @Deprecated
   public static void main(String[] args) {
      try {
         Transform proc = transformFromArgs(args);
         proc.transform();
      } catch (IllegalArgumentException var2) {
         System.err.println(var2.getMessage());
         usage();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   Transform(File inputFile, File ftlFile, File outputFile, Locale locale, String encoding) throws IOException {
      if (encoding == null) {
         encoding = System.getProperty("file.encoding");
      }

      if (locale == null) {
         locale = Locale.getDefault();
      }

      this.encoding = encoding;
      this.locale = locale;
      this.inputFile = inputFile;
      this.ftlFile = ftlFile;
      this.outputFile = outputFile;
      File ftlDirectory = ftlFile.getAbsoluteFile().getParentFile();
      this.cfg = new Configuration();
      this.cfg.setDirectoryForTemplateLoading(ftlDirectory);
   }

   void transform() throws Exception {
      String templateName = this.ftlFile.getName();
      Template template = this.cfg.getTemplate(templateName, this.locale);
      NodeModel rootNode = NodeModel.parse(this.inputFile);
      OutputStream outputStream = System.out;
      if (this.outputFile != null) {
         outputStream = new FileOutputStream(this.outputFile);
      }

      Writer outputWriter = new OutputStreamWriter((OutputStream)outputStream, this.encoding);

      try {
         template.process((Object)null, outputWriter, (ObjectWrapper)null, rootNode);
      } finally {
         if (this.outputFile != null) {
            outputWriter.close();
         }

      }

   }

   static Transform transformFromArgs(String[] args) throws IOException {
      int i = 0;
      String input = null;
      String output = null;
      String ftl = null;
      String loc = null;
      String enc = null;

      while(i < args.length) {
         String dashArg = args[i++];
         if (i >= args.length) {
            throw new IllegalArgumentException("");
         }

         String arg = args[i++];
         if (dashArg.equals("-in")) {
            if (input != null) {
               throw new IllegalArgumentException("The input file should only be specified once");
            }

            input = arg;
         } else if (dashArg.equals("-ftl")) {
            if (ftl != null) {
               throw new IllegalArgumentException("The ftl file should only be specified once");
            }

            ftl = arg;
         } else if (dashArg.equals("-out")) {
            if (output != null) {
               throw new IllegalArgumentException("The output file should only be specified once");
            }

            output = arg;
         } else if (dashArg.equals("-locale")) {
            if (loc != null) {
               throw new IllegalArgumentException("The locale should only be specified once");
            }

            loc = arg;
         } else {
            if (!dashArg.equals("-encoding")) {
               throw new IllegalArgumentException("Unknown input argument: " + dashArg);
            }

            if (enc != null) {
               throw new IllegalArgumentException("The encoding should only be specified once");
            }

            enc = arg;
         }
      }

      if (input == null) {
         throw new IllegalArgumentException("No input file specified.");
      } else if (ftl == null) {
         throw new IllegalArgumentException("No ftl file specified.");
      } else {
         File inputFile = (new File(input)).getAbsoluteFile();
         File ftlFile = (new File(ftl)).getAbsoluteFile();
         if (!inputFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + input);
         } else if (!ftlFile.exists()) {
            throw new IllegalArgumentException("FTL file does not exist: " + ftl);
         } else if (inputFile.isFile() && inputFile.canRead()) {
            if (ftlFile.isFile() && ftlFile.canRead()) {
               File outputFile = null;
               if (output != null) {
                  outputFile = (new File(output)).getAbsoluteFile();
                  File outputDirectory = outputFile.getParentFile();
                  if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
                     throw new IllegalArgumentException("The output directory must exist and be writable: " + outputDirectory);
                  }
               }

               Locale locale = Locale.getDefault();
               if (loc != null) {
                  locale = localeFromString(loc);
               }

               return new Transform(inputFile, ftlFile, outputFile, locale, enc);
            } else {
               throw new IllegalArgumentException("FTL file must be a readable file: " + ftl);
            }
         } else {
            throw new IllegalArgumentException("Input file must be a readable file: " + input);
         }
      }
   }

   static Locale localeFromString(String ls) {
      if (ls == null) {
         ls = "";
      }

      String lang = "";
      String country = "";
      String variant = "";
      StringTokenizer st = new StringTokenizer(ls, "_-,");
      if (st.hasMoreTokens()) {
         lang = st.nextToken();
         if (st.hasMoreTokens()) {
            country = st.nextToken();
            if (st.hasMoreTokens()) {
               variant = st.nextToken();
            }
         }

         return new Locale(lang, country, variant);
      } else {
         return Locale.getDefault();
      }
   }

   static void usage() {
      System.err.println("Usage: java freemarker.ext.dom.Transform -in <xmlfile> -ftl <ftlfile> [-out <outfile>] [-locale <locale>] [-encoding <encoding>]");
      if (Environment.getCurrentEnvironment() == null) {
         System.exit(-1);
      }

   }
}
