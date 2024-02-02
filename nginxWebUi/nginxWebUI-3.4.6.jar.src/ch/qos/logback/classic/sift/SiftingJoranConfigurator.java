/*    */ package ch.qos.logback.classic.sift;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.util.DefaultNestedComponentRules;
/*    */ import ch.qos.logback.core.Appender;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.action.AppenderAction;
/*    */ import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
/*    */ import ch.qos.logback.core.joran.spi.ElementPath;
/*    */ import ch.qos.logback.core.joran.spi.ElementSelector;
/*    */ import ch.qos.logback.core.joran.spi.RuleStore;
/*    */ import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SiftingJoranConfigurator
/*    */   extends SiftingJoranConfiguratorBase<ILoggingEvent>
/*    */ {
/*    */   SiftingJoranConfigurator(String key, String value, Map<String, String> parentPropertyMap) {
/* 34 */     super(key, value, parentPropertyMap);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ElementPath initialElementPath() {
/* 39 */     return new ElementPath("configuration");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void addInstanceRules(RuleStore rs) {
/* 44 */     super.addInstanceRules(rs);
/* 45 */     rs.addRule(new ElementSelector("configuration/appender"), (Action)new AppenderAction());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
/* 50 */     DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void buildInterpreter() {
/* 55 */     super.buildInterpreter();
/* 56 */     Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
/* 57 */     omap.put("APPENDER_BAG", new HashMap<Object, Object>());
/*    */     
/* 59 */     Map<String, String> propertiesMap = new HashMap<String, String>();
/* 60 */     propertiesMap.putAll(this.parentPropertyMap);
/* 61 */     propertiesMap.put(this.key, this.value);
/* 62 */     this.interpreter.setInterpretationContextPropertiesMap(propertiesMap);
/*    */   }
/*    */ 
/*    */   
/*    */   public Appender<ILoggingEvent> getAppender() {
/* 67 */     Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
/* 68 */     HashMap<String, Appender<?>> appenderMap = (HashMap<String, Appender<?>>)omap.get("APPENDER_BAG");
/* 69 */     oneAndOnlyOneCheck(appenderMap);
/* 70 */     Collection<Appender<?>> values = appenderMap.values();
/* 71 */     if (values.size() == 0) {
/* 72 */       return null;
/*    */     }
/* 74 */     return values.iterator().next();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\sift\SiftingJoranConfigurator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */