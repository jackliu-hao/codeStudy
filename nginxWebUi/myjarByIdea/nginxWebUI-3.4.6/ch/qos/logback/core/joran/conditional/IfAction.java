package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;

public class IfAction extends Action {
   private static final String CONDITION_ATTR = "condition";
   public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
   public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
   Stack<IfState> stack = new Stack();

   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
      IfState state = new IfState();
      boolean emptyStack = this.stack.isEmpty();
      this.stack.push(state);
      if (emptyStack) {
         ic.pushObject(this);
         if (!EnvUtil.isJaninoAvailable()) {
            this.addError("Could not find Janino library on the class path. Skipping conditional processing.");
            this.addError("See also http://logback.qos.ch/codes.html#ifJanino");
         } else {
            state.active = true;
            Condition condition = null;
            String conditionAttribute = attributes.getValue("condition");
            if (!OptionHelper.isEmpty(conditionAttribute)) {
               conditionAttribute = OptionHelper.substVars(conditionAttribute, ic, this.context);
               PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(ic);
               pesb.setContext(this.context);

               try {
                  condition = pesb.build(conditionAttribute);
               } catch (Exception var10) {
                  this.addError("Failed to parse condition [" + conditionAttribute + "]", var10);
               }

               if (condition != null) {
                  state.boolResult = condition.evaluate();
               }
            }

         }
      }
   }

   public void end(InterpretationContext ic, String name) throws ActionException {
      IfState state = (IfState)this.stack.pop();
      if (state.active) {
         Object o = ic.peekObject();
         if (o == null) {
            throw new IllegalStateException("Unexpected null object on stack");
         } else if (!(o instanceof IfAction)) {
            throw new IllegalStateException("Unexpected object of type [" + o.getClass() + "] on stack");
         } else if (o != this) {
            throw new IllegalStateException("IfAction different then current one on stack");
         } else {
            ic.popObject();
            if (state.boolResult == null) {
               this.addError("Failed to determine \"if then else\" result");
            } else {
               Interpreter interpreter = ic.getJoranInterpreter();
               List<SaxEvent> listToPlay = state.thenSaxEventList;
               if (!state.boolResult) {
                  listToPlay = state.elseSaxEventList;
               }

               if (listToPlay != null) {
                  interpreter.getEventPlayer().addEventsDynamically(listToPlay, 1);
               }

            }
         }
      }
   }

   public void setThenSaxEventList(List<SaxEvent> thenSaxEventList) {
      IfState state = (IfState)this.stack.firstElement();
      if (state.active) {
         state.thenSaxEventList = thenSaxEventList;
      } else {
         throw new IllegalStateException("setThenSaxEventList() invoked on inactive IfAction");
      }
   }

   public void setElseSaxEventList(List<SaxEvent> elseSaxEventList) {
      IfState state = (IfState)this.stack.firstElement();
      if (state.active) {
         state.elseSaxEventList = elseSaxEventList;
      } else {
         throw new IllegalStateException("setElseSaxEventList() invoked on inactive IfAction");
      }
   }

   public boolean isActive() {
      if (this.stack == null) {
         return false;
      } else {
         return this.stack.isEmpty() ? false : ((IfState)this.stack.peek()).active;
      }
   }
}
