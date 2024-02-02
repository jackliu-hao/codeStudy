package ch.qos.logback.classic.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class ContextNameAction extends Action {
   public void begin(InterpretationContext ec, String name, Attributes attributes) {
   }

   public void body(InterpretationContext ec, String body) {
      String finalBody = ec.subst(body);
      this.addInfo("Setting logger context name as [" + finalBody + "]");

      try {
         this.context.setName(finalBody);
      } catch (IllegalStateException var5) {
         this.addError("Failed to rename context [" + this.context.getName() + "] as [" + finalBody + "]", var5);
      }

   }

   public void end(InterpretationContext ec, String name) {
   }
}
