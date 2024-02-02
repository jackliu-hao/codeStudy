package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import org.xml.sax.Attributes;

public class AppenderRefAction<E> extends Action {
   boolean inError = false;

   public void begin(InterpretationContext ec, String tagName, Attributes attributes) {
      this.inError = false;
      Object o = ec.peekObject();
      if (!(o instanceof AppenderAttachable)) {
         String errMsg = "Could not find an AppenderAttachable at the top of execution stack. Near [" + tagName + "] line " + this.getLineNumber(ec);
         this.inError = true;
         this.addError(errMsg);
      } else {
         AppenderAttachable<E> appenderAttachable = (AppenderAttachable)o;
         String appenderName = ec.subst(attributes.getValue("ref"));
         if (OptionHelper.isEmpty(appenderName)) {
            String errMsg = "Missing appender ref attribute in <appender-ref> tag.";
            this.inError = true;
            this.addError(errMsg);
         } else {
            HashMap<String, Appender<E>> appenderBag = (HashMap)ec.getObjectMap().get("APPENDER_BAG");
            Appender<E> appender = (Appender)appenderBag.get(appenderName);
            if (appender == null) {
               String msg = "Could not find an appender named [" + appenderName + "]. Did you define it below instead of above in the configuration file?";
               this.inError = true;
               this.addError(msg);
               this.addError("See http://logback.qos.ch/codes.html#appender_order for more details.");
            } else {
               this.addInfo("Attaching appender named [" + appenderName + "] to " + appenderAttachable);
               appenderAttachable.addAppender(appender);
            }
         }
      }
   }

   public void end(InterpretationContext ec, String n) {
   }
}
