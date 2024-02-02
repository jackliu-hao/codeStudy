package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import org.xml.sax.Attributes;

public class AppenderAction<E> extends Action {
   Appender<E> appender;
   private boolean inError = false;

   public void begin(InterpretationContext ec, String localName, Attributes attributes) throws ActionException {
      this.appender = null;
      this.inError = false;
      String className = attributes.getValue("class");
      if (OptionHelper.isEmpty(className)) {
         this.addError("Missing class name for appender. Near [" + localName + "] line " + this.getLineNumber(ec));
         this.inError = true;
      } else {
         try {
            this.addInfo("About to instantiate appender of type [" + className + "]");
            this.appender = (Appender)OptionHelper.instantiateByClassName(className, Appender.class, this.context);
            this.appender.setContext(this.context);
            String appenderName = ec.subst(attributes.getValue("name"));
            if (OptionHelper.isEmpty(appenderName)) {
               this.addWarn("No appender name given for appender of type " + className + "].");
            } else {
               this.appender.setName(appenderName);
               this.addInfo("Naming appender as [" + appenderName + "]");
            }

            HashMap<String, Appender<E>> appenderBag = (HashMap)ec.getObjectMap().get("APPENDER_BAG");
            appenderBag.put(appenderName, this.appender);
            ec.pushObject(this.appender);
         } catch (Exception var7) {
            this.inError = true;
            this.addError("Could not create an Appender of type [" + className + "].", var7);
            throw new ActionException(var7);
         }
      }
   }

   public void end(InterpretationContext ec, String name) {
      if (!this.inError) {
         if (this.appender instanceof LifeCycle) {
            this.appender.start();
         }

         Object o = ec.peekObject();
         if (o != this.appender) {
            this.addWarn("The object at the of the stack is not the appender named [" + this.appender.getName() + "] pushed earlier.");
         } else {
            ec.popObject();
         }

      }
   }
}
