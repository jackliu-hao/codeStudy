package ch.qos.logback.solon;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

public class SolonConfigurator extends JoranConfigurator {
   public void addInstanceRules(RuleStore rs) {
      super.addInstanceRules(rs);
      rs.addRule(new ElementSelector("configuration/solonProperty"), (Action)(new SolonPropertyAction()));
   }
}
