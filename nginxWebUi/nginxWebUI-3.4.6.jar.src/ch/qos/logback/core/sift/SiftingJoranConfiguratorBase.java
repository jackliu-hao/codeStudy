/*    */ package ch.qos.logback.core.sift;
/*    */ 
/*    */ import ch.qos.logback.core.Appender;
/*    */ import ch.qos.logback.core.joran.GenericConfigurator;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.action.DefinePropertyAction;
/*    */ import ch.qos.logback.core.joran.action.ImplicitAction;
/*    */ import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
/*    */ import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
/*    */ import ch.qos.logback.core.joran.action.PropertyAction;
/*    */ import ch.qos.logback.core.joran.action.TimestampAction;
/*    */ import ch.qos.logback.core.joran.event.SaxEvent;
/*    */ import ch.qos.logback.core.joran.spi.ElementSelector;
/*    */ import ch.qos.logback.core.joran.spi.Interpreter;
/*    */ import ch.qos.logback.core.joran.spi.JoranException;
/*    */ import ch.qos.logback.core.joran.spi.RuleStore;
/*    */ import java.util.List;
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
/*    */ public abstract class SiftingJoranConfiguratorBase<E>
/*    */   extends GenericConfigurator
/*    */ {
/*    */   protected final String key;
/*    */   protected final String value;
/*    */   protected final Map<String, String> parentPropertyMap;
/*    */   static final String ONE_AND_ONLY_ONE_URL = "http://logback.qos.ch/codes.html#1andOnly1";
/*    */   int errorEmmissionCount;
/*    */   
/*    */   protected void addImplicitRules(Interpreter interpreter) {
/*    */     NestedComplexPropertyIA nestedComplexIA = new NestedComplexPropertyIA(getBeanDescriptionCache());
/*    */     nestedComplexIA.setContext(this.context);
/*    */     interpreter.addImplicitAction((ImplicitAction)nestedComplexIA);
/*    */     NestedBasicPropertyIA nestedSimpleIA = new NestedBasicPropertyIA(getBeanDescriptionCache());
/*    */     nestedSimpleIA.setContext(this.context);
/*    */     interpreter.addImplicitAction((ImplicitAction)nestedSimpleIA);
/*    */   }
/*    */   
/*    */   protected SiftingJoranConfiguratorBase(String key, String value, Map<String, String> parentPropertyMap) {
/* 64 */     this.errorEmmissionCount = 0;
/*    */     this.key = key;
/*    */     this.value = value;
/* 67 */     this.parentPropertyMap = parentPropertyMap; } protected void oneAndOnlyOneCheck(Map<?, ?> appenderMap) { String errMsg = null;
/* 68 */     if (appenderMap.size() == 0) {
/* 69 */       this.errorEmmissionCount++;
/* 70 */       errMsg = "No nested appenders found within the <sift> element in SiftingAppender.";
/* 71 */     } else if (appenderMap.size() > 1) {
/* 72 */       this.errorEmmissionCount++;
/* 73 */       errMsg = "Only and only one appender can be nested the <sift> element in SiftingAppender. See also http://logback.qos.ch/codes.html#1andOnly1";
/*    */     } 
/*    */     
/* 76 */     if (errMsg != null && this.errorEmmissionCount < 4) {
/* 77 */       addError(errMsg);
/*    */     } }
/*    */ 
/*    */   
/*    */   public void doConfigure(List<SaxEvent> eventList) throws JoranException {
/* 82 */     super.doConfigure(eventList); } protected void addInstanceRules(RuleStore rs) {
/*    */     rs.addRule(new ElementSelector("configuration/property"), (Action)new PropertyAction());
/*    */     rs.addRule(new ElementSelector("configuration/timestamp"), (Action)new TimestampAction());
/*    */     rs.addRule(new ElementSelector("configuration/define"), (Action)new DefinePropertyAction());
/*    */   } public abstract Appender<E> getAppender(); public String toString() {
/* 87 */     return getClass().getName() + "{" + this.key + "=" + this.value + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\sift\SiftingJoranConfiguratorBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */