package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.action.Action;
import java.util.List;

public interface RuleStore {
   void addRule(ElementSelector var1, String var2) throws ClassNotFoundException;

   void addRule(ElementSelector var1, Action var2);

   List<Action> matchActions(ElementPath var1);
}
