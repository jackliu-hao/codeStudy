package org.noear.solon.logging.appender;

import java.io.OutputStream;
import java.io.PrintWriter;
import org.noear.snack.ONode;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

public abstract class OutputStreamAppender extends AppenderSimple {
   protected PrintWriter out = null;

   protected void setOutput(OutputStream stream) {
      if (stream != null) {
         this.setOutput(new PrintWriter(stream, true));
      }
   }

   protected void setOutput(PrintWriter writer) {
      if (writer != null) {
         PrintWriter outOld = this.out;
         this.out = writer;
         if (outOld != null) {
            outOld.flush();
            outOld.close();
         }

      }
   }

   public void append(LogEvent logEvent) {
      if (this.out != null) {
         super.append(logEvent);
      }
   }

   protected void appendDo(Level level, String title, Object content) {
      synchronized(this.out) {
         switch (level) {
            case ERROR:
               this.redln(title);
               break;
            case WARN:
               this.yellowln(title);
               break;
            case DEBUG:
               this.blueln(title);
               break;
            case TRACE:
               this.purpleln(title);
               break;
            default:
               this.greenln(title);
         }

         if (content instanceof String) {
            this.out.println(content);
         } else {
            this.out.println(ONode.stringify(content));
         }

      }
   }

   protected void greenln(Object txt) {
      this.colorln("\u001b[32m", txt);
   }

   protected void blueln(Object txt) {
      this.colorln("\u001b[34m", txt);
   }

   protected void redln(String txt) {
      this.colorln("\u001b[31m", txt);
   }

   protected void yellowln(Object txt) {
      this.colorln("\u001b[33m", txt);
   }

   protected void purpleln(Object txt) {
      this.colorln("\u001b[35m", txt);
   }

   protected void colorln(String color, Object s) {
      if (PrintUtil.IS_WINDOWS) {
         this.out.println(s);
      } else {
         this.out.println(color + s);
         this.out.print("\u001b[0m");
      }

   }
}
