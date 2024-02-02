package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class LoggerAction extends Action {
   public static final String LEVEL_ATTRIBUTE = "level";
   boolean inError = false;
   Logger logger;

   public void begin(InterpretationContext ec, String name, Attributes attributes) {
      this.inError = false;
      this.logger = null;
      LoggerContext loggerContext = (LoggerContext)this.context;
      String loggerName = ec.subst(attributes.getValue("name"));
      String levelStr;
      String additivityStr;
      if (OptionHelper.isEmpty(loggerName)) {
         this.inError = true;
         levelStr = this.getLineColStr(ec);
         additivityStr = "No 'name' attribute in element " + name + ", around " + levelStr;
         this.addError(additivityStr);
      } else {
         this.logger = loggerContext.getLogger(loggerName);
         levelStr = ec.subst(attributes.getValue("level"));
         if (!OptionHelper.isEmpty(levelStr)) {
            if (!"INHERITED".equalsIgnoreCase(levelStr) && !"NULL".equalsIgnoreCase(levelStr)) {
               Level level = Level.toLevel(levelStr);
               this.addInfo("Setting level of logger [" + loggerName + "] to " + level);
               this.logger.setLevel(level);
            } else {
               this.addInfo("Setting level of logger [" + loggerName + "] to null, i.e. INHERITED");
               this.logger.setLevel((Level)null);
            }
         }

         additivityStr = ec.subst(attributes.getValue("additivity"));
         if (!OptionHelper.isEmpty(additivityStr)) {
            boolean additive = OptionHelper.toBoolean(additivityStr, true);
            this.addInfo("Setting additivity of logger [" + loggerName + "] to " + additive);
            this.logger.setAdditive(additive);
         }

         ec.pushObject(this.logger);
      }
   }

   public void end(InterpretationContext ec, String e) {
      if (!this.inError) {
         Object o = ec.peekObject();
         if (o != this.logger) {
            this.addWarn("The object on the top the of the stack is not " + this.logger + " pushed earlier");
            this.addWarn("It is: " + o);
         } else {
            ec.popObject();
         }

      }
   }

   public void finish(InterpretationContext ec) {
   }
}
