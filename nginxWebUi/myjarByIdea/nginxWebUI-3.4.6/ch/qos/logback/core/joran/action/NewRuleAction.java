package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class NewRuleAction extends Action {
   boolean inError = false;

   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
      this.inError = false;
      String pattern = attributes.getValue("pattern");
      String actionClass = attributes.getValue("actionClass");
      String errorMsg;
      if (OptionHelper.isEmpty(pattern)) {
         this.inError = true;
         errorMsg = "No 'pattern' attribute in <newRule>";
         this.addError(errorMsg);
      } else if (OptionHelper.isEmpty(actionClass)) {
         this.inError = true;
         errorMsg = "No 'actionClass' attribute in <newRule>";
         this.addError(errorMsg);
      } else {
         try {
            this.addInfo("About to add new Joran parsing rule [" + pattern + "," + actionClass + "].");
            ec.getJoranInterpreter().getRuleStore().addRule(new ElementSelector(pattern), actionClass);
         } catch (Exception var8) {
            this.inError = true;
            errorMsg = "Could not add new Joran parsing rule [" + pattern + "," + actionClass + "]";
            this.addError(errorMsg);
         }

      }
   }

   public void end(InterpretationContext ec, String n) {
   }

   public void finish(InterpretationContext ec) {
   }
}
