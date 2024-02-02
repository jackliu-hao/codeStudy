package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class LoggerContextListenerAction extends Action {
   boolean inError = false;
   LoggerContextListener lcl;

   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
      this.inError = false;
      String className = attributes.getValue("class");
      if (OptionHelper.isEmpty(className)) {
         this.addError("Mandatory \"class\" attribute not set for <loggerContextListener> element");
         this.inError = true;
      } else {
         try {
            this.lcl = (LoggerContextListener)OptionHelper.instantiateByClassName(className, LoggerContextListener.class, this.context);
            if (this.lcl instanceof ContextAware) {
               ((ContextAware)this.lcl).setContext(this.context);
            }

            ec.pushObject(this.lcl);
            this.addInfo("Adding LoggerContextListener of type [" + className + "] to the object stack");
         } catch (Exception var6) {
            this.inError = true;
            this.addError("Could not create LoggerContextListener of type " + className + "].", var6);
         }

      }
   }

   public void end(InterpretationContext ec, String name) throws ActionException {
      if (!this.inError) {
         Object o = ec.peekObject();
         if (o != this.lcl) {
            this.addWarn("The object on the top the of the stack is not the LoggerContextListener pushed earlier.");
         } else {
            if (this.lcl instanceof LifeCycle) {
               ((LifeCycle)this.lcl).start();
               this.addInfo("Starting LoggerContextListener");
            }

            ((LoggerContext)this.context).addListener(this.lcl);
            ec.popObject();
         }

      }
   }
}
