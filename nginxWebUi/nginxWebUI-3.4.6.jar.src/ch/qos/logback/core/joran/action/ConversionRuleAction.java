/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.xml.sax.Attributes;
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
/*    */ public class ConversionRuleAction
/*    */   extends Action
/*    */ {
/*    */   boolean inError = false;
/*    */   
/*    */   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
/* 35 */     this.inError = false;
/*    */ 
/*    */     
/* 38 */     String conversionWord = attributes.getValue("conversionWord");
/* 39 */     String converterClass = attributes.getValue("converterClass");
/*    */     
/* 41 */     if (OptionHelper.isEmpty(conversionWord)) {
/* 42 */       this.inError = true;
/* 43 */       String errorMsg = "No 'conversionWord' attribute in <conversionRule>";
/* 44 */       addError(errorMsg);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 49 */     if (OptionHelper.isEmpty(converterClass)) {
/* 50 */       this.inError = true;
/* 51 */       String errorMsg = "No 'converterClass' attribute in <conversionRule>";
/* 52 */       ec.addError(errorMsg);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/*    */     try {
/* 58 */       Map<String, String> ruleRegistry = (Map<String, String>)this.context.getObject("PATTERN_RULE_REGISTRY");
/* 59 */       if (ruleRegistry == null) {
/* 60 */         ruleRegistry = new HashMap<String, String>();
/* 61 */         this.context.putObject("PATTERN_RULE_REGISTRY", ruleRegistry);
/*    */       } 
/*    */       
/* 64 */       addInfo("registering conversion word " + conversionWord + " with class [" + converterClass + "]");
/* 65 */       ruleRegistry.put(conversionWord, converterClass);
/* 66 */     } catch (Exception oops) {
/* 67 */       this.inError = true;
/* 68 */       String errorMsg = "Could not add conversion rule to PatternLayout.";
/* 69 */       addError(errorMsg);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String n) {}
/*    */   
/*    */   public void finish(InterpretationContext ec) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\ConversionRuleAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */