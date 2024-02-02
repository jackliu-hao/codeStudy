/*    */ package ch.qos.logback.classic.joran;
/*    */ 
/*    */ import ch.qos.logback.classic.joran.action.ConfigurationAction;
/*    */ import ch.qos.logback.classic.joran.action.ConsolePluginAction;
/*    */ import ch.qos.logback.classic.joran.action.ContextNameAction;
/*    */ import ch.qos.logback.classic.joran.action.EvaluatorAction;
/*    */ import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
/*    */ import ch.qos.logback.classic.joran.action.JMXConfiguratorAction;
/*    */ import ch.qos.logback.classic.joran.action.LevelAction;
/*    */ import ch.qos.logback.classic.joran.action.LoggerAction;
/*    */ import ch.qos.logback.classic.joran.action.LoggerContextListenerAction;
/*    */ import ch.qos.logback.classic.joran.action.ReceiverAction;
/*    */ import ch.qos.logback.classic.joran.action.RootLoggerAction;
/*    */ import ch.qos.logback.classic.sift.SiftAction;
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.PlatformInfo;
/*    */ import ch.qos.logback.classic.util.DefaultNestedComponentRules;
/*    */ import ch.qos.logback.core.joran.JoranConfiguratorBase;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.action.AppenderRefAction;
/*    */ import ch.qos.logback.core.joran.action.IncludeAction;
/*    */ import ch.qos.logback.core.joran.action.NOPAction;
/*    */ import ch.qos.logback.core.joran.conditional.ElseAction;
/*    */ import ch.qos.logback.core.joran.conditional.IfAction;
/*    */ import ch.qos.logback.core.joran.conditional.ThenAction;
/*    */ import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
/*    */ import ch.qos.logback.core.joran.spi.ElementSelector;
/*    */ import ch.qos.logback.core.joran.spi.RuleStore;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JoranConfigurator
/*    */   extends JoranConfiguratorBase<ILoggingEvent>
/*    */ {
/*    */   public void addInstanceRules(RuleStore rs) {
/* 42 */     super.addInstanceRules(rs);
/*    */     
/* 44 */     rs.addRule(new ElementSelector("configuration"), (Action)new ConfigurationAction());
/*    */     
/* 46 */     rs.addRule(new ElementSelector("configuration/contextName"), (Action)new ContextNameAction());
/* 47 */     rs.addRule(new ElementSelector("configuration/contextListener"), (Action)new LoggerContextListenerAction());
/* 48 */     rs.addRule(new ElementSelector("configuration/insertFromJNDI"), (Action)new InsertFromJNDIAction());
/* 49 */     rs.addRule(new ElementSelector("configuration/evaluator"), (Action)new EvaluatorAction());
/*    */     
/* 51 */     rs.addRule(new ElementSelector("configuration/appender/sift"), (Action)new SiftAction());
/* 52 */     rs.addRule(new ElementSelector("configuration/appender/sift/*"), (Action)new NOPAction());
/*    */     
/* 54 */     rs.addRule(new ElementSelector("configuration/logger"), (Action)new LoggerAction());
/* 55 */     rs.addRule(new ElementSelector("configuration/logger/level"), (Action)new LevelAction());
/*    */     
/* 57 */     rs.addRule(new ElementSelector("configuration/root"), (Action)new RootLoggerAction());
/* 58 */     rs.addRule(new ElementSelector("configuration/root/level"), (Action)new LevelAction());
/* 59 */     rs.addRule(new ElementSelector("configuration/logger/appender-ref"), (Action)new AppenderRefAction());
/* 60 */     rs.addRule(new ElementSelector("configuration/root/appender-ref"), (Action)new AppenderRefAction());
/*    */ 
/*    */     
/* 63 */     rs.addRule(new ElementSelector("*/if"), (Action)new IfAction());
/* 64 */     rs.addRule(new ElementSelector("*/if/then"), (Action)new ThenAction());
/* 65 */     rs.addRule(new ElementSelector("*/if/then/*"), (Action)new NOPAction());
/* 66 */     rs.addRule(new ElementSelector("*/if/else"), (Action)new ElseAction());
/* 67 */     rs.addRule(new ElementSelector("*/if/else/*"), (Action)new NOPAction());
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 72 */     if (PlatformInfo.hasJMXObjectName()) {
/* 73 */       rs.addRule(new ElementSelector("configuration/jmxConfigurator"), (Action)new JMXConfiguratorAction());
/*    */     }
/* 75 */     rs.addRule(new ElementSelector("configuration/include"), (Action)new IncludeAction());
/*    */     
/* 77 */     rs.addRule(new ElementSelector("configuration/consolePlugin"), (Action)new ConsolePluginAction());
/*    */     
/* 79 */     rs.addRule(new ElementSelector("configuration/receiver"), (Action)new ReceiverAction());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
/* 85 */     DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\JoranConfigurator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */