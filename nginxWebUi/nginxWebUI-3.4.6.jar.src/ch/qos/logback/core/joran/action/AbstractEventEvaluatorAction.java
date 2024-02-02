/*     */ package ch.qos.logback.core.joran.action;
/*     */ 
/*     */ import ch.qos.logback.core.boolex.EventEvaluator;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.util.Map;
/*     */ import org.xml.sax.Attributes;
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
/*     */ public abstract class AbstractEventEvaluatorAction
/*     */   extends Action
/*     */ {
/*     */   EventEvaluator<?> evaluator;
/*     */   boolean inError = false;
/*     */   
/*     */   public void begin(InterpretationContext ec, String name, Attributes attributes) {
/*  36 */     this.inError = false;
/*  37 */     this.evaluator = null;
/*     */     
/*  39 */     String className = attributes.getValue("class");
/*  40 */     if (OptionHelper.isEmpty(className)) {
/*  41 */       className = defaultClassName();
/*  42 */       addInfo("Assuming default evaluator class [" + className + "]");
/*     */     } 
/*     */     
/*  45 */     if (OptionHelper.isEmpty(className)) {
/*  46 */       className = defaultClassName();
/*  47 */       this.inError = true;
/*  48 */       addError("Mandatory \"class\" attribute not set for <evaluator>");
/*     */       
/*     */       return;
/*     */     } 
/*  52 */     String evaluatorName = attributes.getValue("name");
/*  53 */     if (OptionHelper.isEmpty(evaluatorName)) {
/*  54 */       this.inError = true;
/*  55 */       addError("Mandatory \"name\" attribute not set for <evaluator>");
/*     */       return;
/*     */     } 
/*     */     try {
/*  59 */       this.evaluator = (EventEvaluator)OptionHelper.instantiateByClassName(className, EventEvaluator.class, this.context);
/*     */       
/*  61 */       this.evaluator.setContext(this.context);
/*  62 */       this.evaluator.setName(evaluatorName);
/*     */       
/*  64 */       ec.pushObject(this.evaluator);
/*  65 */       addInfo("Adding evaluator named [" + evaluatorName + "] to the object stack");
/*     */     }
/*  67 */     catch (Exception oops) {
/*  68 */       this.inError = true;
/*  69 */       addError("Could not create evaluator of type " + className + "].", oops);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract String defaultClassName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(InterpretationContext ec, String e) {
/*  86 */     if (this.inError) {
/*     */       return;
/*     */     }
/*     */     
/*  90 */     if (this.evaluator instanceof ch.qos.logback.core.spi.LifeCycle) {
/*  91 */       this.evaluator.start();
/*  92 */       addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
/*     */     } 
/*     */     
/*  95 */     Object o = ec.peekObject();
/*     */     
/*  97 */     if (o != this.evaluator) {
/*  98 */       addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
/*     */     } else {
/* 100 */       ec.popObject();
/*     */       
/*     */       try {
/* 103 */         Map<String, EventEvaluator<?>> evaluatorMap = (Map<String, EventEvaluator<?>>)this.context.getObject("EVALUATOR_MAP");
/* 104 */         if (evaluatorMap == null) {
/* 105 */           addError("Could not find EvaluatorMap");
/*     */         } else {
/* 107 */           evaluatorMap.put(this.evaluator.getName(), this.evaluator);
/*     */         } 
/* 109 */       } catch (Exception ex) {
/* 110 */         addError("Could not set evaluator named [" + this.evaluator + "].", ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void finish(InterpretationContext ec) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\AbstractEventEvaluatorAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */