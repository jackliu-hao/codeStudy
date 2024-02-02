package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.xml.sax.Attributes;

public abstract class AbstractEventEvaluatorAction extends Action {
   EventEvaluator<?> evaluator;
   boolean inError = false;

   public void begin(InterpretationContext ec, String name, Attributes attributes) {
      this.inError = false;
      this.evaluator = null;
      String className = attributes.getValue("class");
      if (OptionHelper.isEmpty(className)) {
         className = this.defaultClassName();
         this.addInfo("Assuming default evaluator class [" + className + "]");
      }

      if (OptionHelper.isEmpty(className)) {
         className = this.defaultClassName();
         this.inError = true;
         this.addError("Mandatory \"class\" attribute not set for <evaluator>");
      } else {
         String evaluatorName = attributes.getValue("name");
         if (OptionHelper.isEmpty(evaluatorName)) {
            this.inError = true;
            this.addError("Mandatory \"name\" attribute not set for <evaluator>");
         } else {
            try {
               this.evaluator = (EventEvaluator)OptionHelper.instantiateByClassName(className, EventEvaluator.class, this.context);
               this.evaluator.setContext(this.context);
               this.evaluator.setName(evaluatorName);
               ec.pushObject(this.evaluator);
               this.addInfo("Adding evaluator named [" + evaluatorName + "] to the object stack");
            } catch (Exception var7) {
               this.inError = true;
               this.addError("Could not create evaluator of type " + className + "].", var7);
            }

         }
      }
   }

   protected abstract String defaultClassName();

   public void end(InterpretationContext ec, String e) {
      if (!this.inError) {
         if (this.evaluator instanceof LifeCycle) {
            this.evaluator.start();
            this.addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
         }

         Object o = ec.peekObject();
         if (o != this.evaluator) {
            this.addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
         } else {
            ec.popObject();

            try {
               Map<String, EventEvaluator<?>> evaluatorMap = (Map)this.context.getObject("EVALUATOR_MAP");
               if (evaluatorMap == null) {
                  this.addError("Could not find EvaluatorMap");
               } else {
                  evaluatorMap.put(this.evaluator.getName(), this.evaluator);
               }
            } catch (Exception var5) {
               this.addError("Could not set evaluator named [" + this.evaluator + "].", var5);
            }
         }

      }
   }

   public void finish(InterpretationContext ec) {
   }
}
