package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.DefaultNestedComponentRules;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.AppenderAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SiftingJoranConfigurator extends SiftingJoranConfiguratorBase<ILoggingEvent> {
   SiftingJoranConfigurator(String key, String value, Map<String, String> parentPropertyMap) {
      super(key, value, parentPropertyMap);
   }

   protected ElementPath initialElementPath() {
      return new ElementPath("configuration");
   }

   protected void addInstanceRules(RuleStore rs) {
      super.addInstanceRules(rs);
      rs.addRule(new ElementSelector("configuration/appender"), (Action)(new AppenderAction()));
   }

   protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
      DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
   }

   protected void buildInterpreter() {
      super.buildInterpreter();
      Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
      omap.put("APPENDER_BAG", new HashMap());
      Map<String, String> propertiesMap = new HashMap();
      propertiesMap.putAll(this.parentPropertyMap);
      propertiesMap.put(this.key, this.value);
      this.interpreter.setInterpretationContextPropertiesMap(propertiesMap);
   }

   public Appender<ILoggingEvent> getAppender() {
      Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
      HashMap<String, Appender<?>> appenderMap = (HashMap)omap.get("APPENDER_BAG");
      this.oneAndOnlyOneCheck(appenderMap);
      Collection<Appender<?>> values = appenderMap.values();
      return values.size() == 0 ? null : (Appender)values.iterator().next();
   }
}
