package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.joran.action.ConfigurationAction;
import ch.qos.logback.classic.joran.action.ConsolePluginAction;
import ch.qos.logback.classic.joran.action.ContextNameAction;
import ch.qos.logback.classic.joran.action.EvaluatorAction;
import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
import ch.qos.logback.classic.joran.action.JMXConfiguratorAction;
import ch.qos.logback.classic.joran.action.LevelAction;
import ch.qos.logback.classic.joran.action.LoggerAction;
import ch.qos.logback.classic.joran.action.LoggerContextListenerAction;
import ch.qos.logback.classic.joran.action.ReceiverAction;
import ch.qos.logback.classic.joran.action.RootLoggerAction;
import ch.qos.logback.classic.sift.SiftAction;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.PlatformInfo;
import ch.qos.logback.classic.util.DefaultNestedComponentRules;
import ch.qos.logback.core.joran.JoranConfiguratorBase;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.IncludeAction;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.conditional.ElseAction;
import ch.qos.logback.core.joran.conditional.IfAction;
import ch.qos.logback.core.joran.conditional.ThenAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

public class JoranConfigurator extends JoranConfiguratorBase<ILoggingEvent> {
   public void addInstanceRules(RuleStore rs) {
      super.addInstanceRules(rs);
      rs.addRule(new ElementSelector("configuration"), (Action)(new ConfigurationAction()));
      rs.addRule(new ElementSelector("configuration/contextName"), (Action)(new ContextNameAction()));
      rs.addRule(new ElementSelector("configuration/contextListener"), (Action)(new LoggerContextListenerAction()));
      rs.addRule(new ElementSelector("configuration/insertFromJNDI"), (Action)(new InsertFromJNDIAction()));
      rs.addRule(new ElementSelector("configuration/evaluator"), (Action)(new EvaluatorAction()));
      rs.addRule(new ElementSelector("configuration/appender/sift"), (Action)(new SiftAction()));
      rs.addRule(new ElementSelector("configuration/appender/sift/*"), (Action)(new NOPAction()));
      rs.addRule(new ElementSelector("configuration/logger"), (Action)(new LoggerAction()));
      rs.addRule(new ElementSelector("configuration/logger/level"), (Action)(new LevelAction()));
      rs.addRule(new ElementSelector("configuration/root"), (Action)(new RootLoggerAction()));
      rs.addRule(new ElementSelector("configuration/root/level"), (Action)(new LevelAction()));
      rs.addRule(new ElementSelector("configuration/logger/appender-ref"), (Action)(new AppenderRefAction()));
      rs.addRule(new ElementSelector("configuration/root/appender-ref"), (Action)(new AppenderRefAction()));
      rs.addRule(new ElementSelector("*/if"), (Action)(new IfAction()));
      rs.addRule(new ElementSelector("*/if/then"), (Action)(new ThenAction()));
      rs.addRule(new ElementSelector("*/if/then/*"), (Action)(new NOPAction()));
      rs.addRule(new ElementSelector("*/if/else"), (Action)(new ElseAction()));
      rs.addRule(new ElementSelector("*/if/else/*"), (Action)(new NOPAction()));
      if (PlatformInfo.hasJMXObjectName()) {
         rs.addRule(new ElementSelector("configuration/jmxConfigurator"), (Action)(new JMXConfiguratorAction()));
      }

      rs.addRule(new ElementSelector("configuration/include"), (Action)(new IncludeAction()));
      rs.addRule(new ElementSelector("configuration/consolePlugin"), (Action)(new ConsolePluginAction()));
      rs.addRule(new ElementSelector("configuration/receiver"), (Action)(new ReceiverAction()));
   }

   protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
      DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
   }
}
