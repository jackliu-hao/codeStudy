package freemarker.core;

import freemarker.template.TemplateException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class StopException extends TemplateException {
   StopException(Environment env) {
      super(env);
   }

   StopException(Environment env, String s) {
      super(s, env);
   }

   public void printStackTrace(PrintWriter pw) {
      synchronized(pw) {
         String msg = this.getMessage();
         pw.print("Encountered stop instruction");
         if (msg != null && !msg.equals("")) {
            pw.println("\nCause given: " + msg);
         } else {
            pw.println();
         }

         super.printStackTrace(pw);
      }
   }

   public void printStackTrace(PrintStream ps) {
      synchronized(ps) {
         String msg = this.getMessage();
         ps.print("Encountered stop instruction");
         if (msg != null && !msg.equals("")) {
            ps.println("\nCause given: " + msg);
         } else {
            ps.println();
         }

         super.printStackTrace(ps);
      }
   }
}
