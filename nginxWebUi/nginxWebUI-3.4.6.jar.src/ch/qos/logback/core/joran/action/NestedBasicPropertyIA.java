/*     */ package ch.qos.logback.core.joran.action;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.ElementPath;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.util.PropertySetter;
/*     */ import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
/*     */ import ch.qos.logback.core.util.AggregationType;
/*     */ import java.util.Stack;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NestedBasicPropertyIA
/*     */   extends ImplicitAction
/*     */ {
/*  42 */   Stack<IADataForBasicProperty> actionDataStack = new Stack<IADataForBasicProperty>();
/*     */   
/*     */   private final BeanDescriptionCache beanDescriptionCache;
/*     */   
/*     */   public NestedBasicPropertyIA(BeanDescriptionCache beanDescriptionCache) {
/*  47 */     this.beanDescriptionCache = beanDescriptionCache;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext ec) {
/*     */     IADataForBasicProperty ad;
/*  53 */     String nestedElementTagName = elementPath.peekLast();
/*     */ 
/*     */     
/*  56 */     if (ec.isEmpty()) {
/*  57 */       return false;
/*     */     }
/*     */     
/*  60 */     Object o = ec.peekObject();
/*  61 */     PropertySetter parentBean = new PropertySetter(this.beanDescriptionCache, o);
/*  62 */     parentBean.setContext(this.context);
/*     */     
/*  64 */     AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
/*     */     
/*  66 */     switch (aggregationType) {
/*     */       case NOT_FOUND:
/*     */       case AS_COMPLEX_PROPERTY:
/*     */       case AS_COMPLEX_PROPERTY_COLLECTION:
/*  70 */         return false;
/*     */       
/*     */       case AS_BASIC_PROPERTY:
/*     */       case AS_BASIC_PROPERTY_COLLECTION:
/*  74 */         ad = new IADataForBasicProperty(parentBean, aggregationType, nestedElementTagName);
/*  75 */         this.actionDataStack.push(ad);
/*     */         
/*  77 */         return true;
/*     */     } 
/*  79 */     addError("PropertySetter.canContainComponent returned " + aggregationType);
/*  80 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void begin(InterpretationContext ec, String localName, Attributes attributes) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void body(InterpretationContext ec, String body) {
/*  90 */     String finalBody = ec.subst(body);
/*     */     
/*  92 */     IADataForBasicProperty actionData = this.actionDataStack.peek();
/*  93 */     switch (actionData.aggregationType) {
/*     */       case AS_BASIC_PROPERTY:
/*  95 */         actionData.parentBean.setProperty(actionData.propertyName, finalBody);
/*     */         return;
/*     */       case AS_BASIC_PROPERTY_COLLECTION:
/*  98 */         actionData.parentBean.addBasicProperty(actionData.propertyName, finalBody);
/*     */         return;
/*     */     } 
/* 101 */     addError("Unexpected aggregationType " + actionData.aggregationType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(InterpretationContext ec, String tagName) {
/* 107 */     this.actionDataStack.pop();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\NestedBasicPropertyIA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */