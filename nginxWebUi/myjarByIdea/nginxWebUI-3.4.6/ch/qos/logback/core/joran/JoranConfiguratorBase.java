package ch.qos.logback.core.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.AppenderAction;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.ContextPropertyAction;
import ch.qos.logback.core.joran.action.ConversionRuleAction;
import ch.qos.logback.core.joran.action.DefinePropertyAction;
import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import ch.qos.logback.core.joran.action.NewRuleAction;
import ch.qos.logback.core.joran.action.ParamAction;
import ch.qos.logback.core.joran.action.PropertyAction;
import ch.qos.logback.core.joran.action.ShutdownHookAction;
import ch.qos.logback.core.joran.action.StatusListenerAction;
import ch.qos.logback.core.joran.action.TimestampAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.RuleStore;
import java.util.HashMap;
import java.util.Map;

public abstract class JoranConfiguratorBase<E> extends GenericConfigurator {
   protected void addInstanceRules(RuleStore rs) {
      rs.addRule(new ElementSelector("configuration/variable"), (Action)(new PropertyAction()));
      rs.addRule(new ElementSelector("configuration/property"), (Action)(new PropertyAction()));
      rs.addRule(new ElementSelector("configuration/substitutionProperty"), (Action)(new PropertyAction()));
      rs.addRule(new ElementSelector("configuration/timestamp"), (Action)(new TimestampAction()));
      rs.addRule(new ElementSelector("configuration/shutdownHook"), (Action)(new ShutdownHookAction()));
      rs.addRule(new ElementSelector("configuration/define"), (Action)(new DefinePropertyAction()));
      rs.addRule(new ElementSelector("configuration/contextProperty"), (Action)(new ContextPropertyAction()));
      rs.addRule(new ElementSelector("configuration/conversionRule"), (Action)(new ConversionRuleAction()));
      rs.addRule(new ElementSelector("configuration/statusListener"), (Action)(new StatusListenerAction()));
      rs.addRule(new ElementSelector("configuration/appender"), (Action)(new AppenderAction()));
      rs.addRule(new ElementSelector("configuration/appender/appender-ref"), (Action)(new AppenderRefAction()));
      rs.addRule(new ElementSelector("configuration/newRule"), (Action)(new NewRuleAction()));
      rs.addRule(new ElementSelector("*/param"), (Action)(new ParamAction(this.getBeanDescriptionCache())));
   }

   protected void addImplicitRules(Interpreter interpreter) {
      NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA(this.getBeanDescriptionCache());
      nestedComplexPropertyIA.setContext(this.context);
      interpreter.addImplicitAction(nestedComplexPropertyIA);
      NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA(this.getBeanDescriptionCache());
      nestedBasicIA.setContext(this.context);
      interpreter.addImplicitAction(nestedBasicIA);
   }

   protected void buildInterpreter() {
      super.buildInterpreter();
      Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
      omap.put("APPENDER_BAG", new HashMap());
   }

   public InterpretationContext getInterpretationContext() {
      return this.interpreter.getInterpretationContext();
   }
}
