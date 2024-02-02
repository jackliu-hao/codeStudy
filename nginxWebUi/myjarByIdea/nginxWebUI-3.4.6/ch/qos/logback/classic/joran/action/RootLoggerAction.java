package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class RootLoggerAction extends Action {
   Logger root;
   boolean inError = false;

   public void begin(InterpretationContext ec, String name, Attributes attributes) {
      this.inError = false;
      LoggerContext loggerContext = (LoggerContext)this.context;
      this.root = loggerContext.getLogger("ROOT");
      String levelStr = ec.subst(attributes.getValue("level"));
      if (!OptionHelper.isEmpty(levelStr)) {
         Level level = Level.toLevel(levelStr);
         this.addInfo("Setting level of ROOT logger to " + level);
         this.root.setLevel(level);
      }

      ec.pushObject(this.root);
   }

   public void end(InterpretationContext ec, String name) {
      if (!this.inError) {
         Object o = ec.peekObject();
         if (o != this.root) {
            this.addWarn("The object on the top the of the stack is not the root logger");
            this.addWarn("It is: " + o);
         } else {
            ec.popObject();
         }

      }
   }

   public void finish(InterpretationContext ec) {
   }
}
