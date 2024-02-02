/*     */ package ch.qos.logback.core.joran;
/*     */ 
/*     */ import ch.qos.logback.core.joran.action.Action;
/*     */ import ch.qos.logback.core.joran.action.AppenderAction;
/*     */ import ch.qos.logback.core.joran.action.AppenderRefAction;
/*     */ import ch.qos.logback.core.joran.action.ContextPropertyAction;
/*     */ import ch.qos.logback.core.joran.action.ConversionRuleAction;
/*     */ import ch.qos.logback.core.joran.action.DefinePropertyAction;
/*     */ import ch.qos.logback.core.joran.action.ImplicitAction;
/*     */ import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
/*     */ import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
/*     */ import ch.qos.logback.core.joran.action.NewRuleAction;
/*     */ import ch.qos.logback.core.joran.action.ParamAction;
/*     */ import ch.qos.logback.core.joran.action.PropertyAction;
/*     */ import ch.qos.logback.core.joran.action.ShutdownHookAction;
/*     */ import ch.qos.logback.core.joran.action.StatusListenerAction;
/*     */ import ch.qos.logback.core.joran.action.TimestampAction;
/*     */ import ch.qos.logback.core.joran.spi.ElementSelector;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.spi.Interpreter;
/*     */ import ch.qos.logback.core.joran.spi.RuleStore;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JoranConfiguratorBase<E>
/*     */   extends GenericConfigurator
/*     */ {
/*     */   protected void addInstanceRules(RuleStore rs) {
/*  58 */     rs.addRule(new ElementSelector("configuration/variable"), (Action)new PropertyAction());
/*  59 */     rs.addRule(new ElementSelector("configuration/property"), (Action)new PropertyAction());
/*     */     
/*  61 */     rs.addRule(new ElementSelector("configuration/substitutionProperty"), (Action)new PropertyAction());
/*     */     
/*  63 */     rs.addRule(new ElementSelector("configuration/timestamp"), (Action)new TimestampAction());
/*  64 */     rs.addRule(new ElementSelector("configuration/shutdownHook"), (Action)new ShutdownHookAction());
/*  65 */     rs.addRule(new ElementSelector("configuration/define"), (Action)new DefinePropertyAction());
/*     */ 
/*     */ 
/*     */     
/*  69 */     rs.addRule(new ElementSelector("configuration/contextProperty"), (Action)new ContextPropertyAction());
/*     */     
/*  71 */     rs.addRule(new ElementSelector("configuration/conversionRule"), (Action)new ConversionRuleAction());
/*     */     
/*  73 */     rs.addRule(new ElementSelector("configuration/statusListener"), (Action)new StatusListenerAction());
/*     */     
/*  75 */     rs.addRule(new ElementSelector("configuration/appender"), (Action)new AppenderAction());
/*  76 */     rs.addRule(new ElementSelector("configuration/appender/appender-ref"), (Action)new AppenderRefAction());
/*  77 */     rs.addRule(new ElementSelector("configuration/newRule"), (Action)new NewRuleAction());
/*  78 */     rs.addRule(new ElementSelector("*/param"), (Action)new ParamAction(getBeanDescriptionCache()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addImplicitRules(Interpreter interpreter) {
/*  84 */     NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA(getBeanDescriptionCache());
/*  85 */     nestedComplexPropertyIA.setContext(this.context);
/*  86 */     interpreter.addImplicitAction((ImplicitAction)nestedComplexPropertyIA);
/*     */     
/*  88 */     NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA(getBeanDescriptionCache());
/*  89 */     nestedBasicIA.setContext(this.context);
/*  90 */     interpreter.addImplicitAction((ImplicitAction)nestedBasicIA);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void buildInterpreter() {
/*  95 */     super.buildInterpreter();
/*  96 */     Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
/*  97 */     omap.put("APPENDER_BAG", new HashMap<Object, Object>());
/*     */   }
/*     */ 
/*     */   
/*     */   public InterpretationContext getInterpretationContext() {
/* 102 */     return this.interpreter.getInterpretationContext();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\JoranConfiguratorBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */