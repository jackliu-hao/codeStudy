package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.net.ReceiverBase;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class ReceiverAction extends Action {
   private ReceiverBase receiver;
   private boolean inError;

   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
      String className = attributes.getValue("class");
      if (OptionHelper.isEmpty(className)) {
         this.addError("Missing class name for receiver. Near [" + name + "] line " + this.getLineNumber(ic));
         this.inError = true;
      } else {
         try {
            this.addInfo("About to instantiate receiver of type [" + className + "]");
            this.receiver = (ReceiverBase)OptionHelper.instantiateByClassName(className, ReceiverBase.class, this.context);
            this.receiver.setContext(this.context);
            ic.pushObject(this.receiver);
         } catch (Exception var6) {
            this.inError = true;
            this.addError("Could not create a receiver of type [" + className + "].", var6);
            throw new ActionException(var6);
         }
      }
   }

   public void end(InterpretationContext ic, String name) throws ActionException {
      if (!this.inError) {
         ic.getContext().register(this.receiver);
         this.receiver.start();
         Object o = ic.peekObject();
         if (o != this.receiver) {
            this.addWarn("The object at the of the stack is not the remote pushed earlier.");
         } else {
            ic.popObject();
         }

      }
   }
}
