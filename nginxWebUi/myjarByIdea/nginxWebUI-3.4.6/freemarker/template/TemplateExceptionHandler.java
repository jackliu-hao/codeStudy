package freemarker.template;

import freemarker.core.Environment;
import freemarker.template.utility.StringUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public interface TemplateExceptionHandler {
   TemplateExceptionHandler IGNORE_HANDLER = new TemplateExceptionHandler() {
      public void handleTemplateException(TemplateException te, Environment env, Writer out) {
      }
   };
   TemplateExceptionHandler RETHROW_HANDLER = new TemplateExceptionHandler() {
      public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
         throw te;
      }
   };
   TemplateExceptionHandler DEBUG_HANDLER = new TemplateExceptionHandler() {
      public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
         if (!env.isInAttemptBlock()) {
            PrintWriter pw = out instanceof PrintWriter ? (PrintWriter)out : new PrintWriter(out);
            pw.print("FreeMarker template error (DEBUG mode; use RETHROW in production!):\n");
            te.printStackTrace(pw, false, true, true);
            pw.flush();
         }

         throw te;
      }
   };
   TemplateExceptionHandler HTML_DEBUG_HANDLER = new TemplateExceptionHandler() {
      private static final String FONT_RESET_CSS = "color:#A80000; font-size:12px; font-style:normal; font-variant:normal; font-weight:normal; text-decoration:none; text-transform: none";

      public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
         if (!env.isInAttemptBlock()) {
            boolean externalPw = out instanceof PrintWriter;
            PrintWriter pw = externalPw ? (PrintWriter)out : new PrintWriter(out);

            try {
               pw.print("<!-- FREEMARKER ERROR MESSAGE STARTS HERE --><!-- ]]> --><script language=javascript>//\"></script><script language=javascript>//'></script><script language=javascript>//\"></script><script language=javascript>//'></script></title></xmp></script></noscript></style></object></head></pre></table></form></table></table></table></a></u></i></b><div align='left' style='background-color:#FFFF7C; display:block; border-top:double; padding:4px; margin:0; font-family:Arial,sans-serif; ");
               pw.print("color:#A80000; font-size:12px; font-style:normal; font-variant:normal; font-weight:normal; text-decoration:none; text-transform: none");
               pw.print("'><b style='font-size:12px; font-style:normal; font-weight:bold; text-decoration:none; text-transform: none;'>FreeMarker template error  (HTML_DEBUG mode; use RETHROW in production!)</b><pre style='display:block; background: none; border: 0; margin:0; padding: 0;font-family:monospace; ");
               pw.print("color:#A80000; font-size:12px; font-style:normal; font-variant:normal; font-weight:normal; text-decoration:none; text-transform: none");
               pw.println("; white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-wrap: break-word;'>");
               StringWriter stackTraceSW = new StringWriter();
               PrintWriter stackPW = new PrintWriter(stackTraceSW);
               te.printStackTrace(stackPW, false, true, true);
               stackPW.close();
               pw.println();
               pw.println(StringUtil.XMLEncNQG(stackTraceSW.toString()));
               pw.println("</pre></div></html>");
               pw.flush();
            } finally {
               if (!externalPw) {
                  pw.close();
               }

            }
         }

         throw te;
      }
   };

   void handleTemplateException(TemplateException var1, Environment var2, Writer var3) throws TemplateException;
}
