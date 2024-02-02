/*     */ package ch.qos.logback.core.joran.action;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.ElementPath;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
/*     */ import ch.qos.logback.core.joran.util.PropertySetter;
/*     */ import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
/*     */ import ch.qos.logback.core.spi.ContextAware;
/*     */ import ch.qos.logback.core.spi.LifeCycle;
/*     */ import ch.qos.logback.core.util.AggregationType;
/*     */ import ch.qos.logback.core.util.Loader;
/*     */ import ch.qos.logback.core.util.OptionHelper;
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
/*     */ public class NestedComplexPropertyIA
/*     */   extends ImplicitAction
/*     */ {
/*  47 */   Stack<IADataForComplexProperty> actionDataStack = new Stack<IADataForComplexProperty>();
/*     */   
/*     */   private final BeanDescriptionCache beanDescriptionCache;
/*     */   
/*     */   public NestedComplexPropertyIA(BeanDescriptionCache beanDescriptionCache) {
/*  52 */     this.beanDescriptionCache = beanDescriptionCache;
/*     */   }
/*     */   
/*     */   public boolean isApplicable(ElementPath elementPath, Attributes attributes, InterpretationContext ic) {
/*     */     IADataForComplexProperty ad;
/*  57 */     String nestedElementTagName = elementPath.peekLast();
/*     */ 
/*     */     
/*  60 */     if (ic.isEmpty()) {
/*  61 */       return false;
/*     */     }
/*     */     
/*  64 */     Object o = ic.peekObject();
/*  65 */     PropertySetter parentBean = new PropertySetter(this.beanDescriptionCache, o);
/*  66 */     parentBean.setContext(this.context);
/*     */     
/*  68 */     AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
/*     */     
/*  70 */     switch (aggregationType) {
/*     */       case NOT_FOUND:
/*     */       case AS_BASIC_PROPERTY:
/*     */       case AS_BASIC_PROPERTY_COLLECTION:
/*  74 */         return false;
/*     */ 
/*     */       
/*     */       case AS_COMPLEX_PROPERTY_COLLECTION:
/*     */       case AS_COMPLEX_PROPERTY:
/*  79 */         ad = new IADataForComplexProperty(parentBean, aggregationType, nestedElementTagName);
/*  80 */         this.actionDataStack.push(ad);
/*     */         
/*  82 */         return true;
/*     */     } 
/*  84 */     addError("PropertySetter.computeAggregationType returned " + aggregationType);
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
/*  92 */     IADataForComplexProperty actionData = this.actionDataStack.peek();
/*     */     
/*  94 */     String className = attributes.getValue("class");
/*     */     
/*  96 */     className = ec.subst(className);
/*     */     
/*  98 */     Class<?> componentClass = null;
/*     */     
/*     */     try {
/* 101 */       if (!OptionHelper.isEmpty(className)) {
/* 102 */         componentClass = Loader.loadClass(className, this.context);
/*     */       } else {
/*     */         
/* 105 */         PropertySetter parentBean = actionData.parentBean;
/* 106 */         componentClass = parentBean.getClassNameViaImplicitRules(actionData.getComplexPropertyName(), actionData.getAggregationType(), ec
/* 107 */             .getDefaultNestedComponentRegistry());
/*     */       } 
/*     */       
/* 110 */       if (componentClass == null) {
/* 111 */         actionData.inError = true;
/* 112 */         String errMsg = "Could not find an appropriate class for property [" + localName + "]";
/* 113 */         addError(errMsg);
/*     */         
/*     */         return;
/*     */       } 
/* 117 */       if (OptionHelper.isEmpty(className)) {
/* 118 */         addInfo("Assuming default type [" + componentClass.getName() + "] for [" + localName + "] property");
/*     */       }
/*     */       
/* 121 */       actionData.setNestedComplexProperty(componentClass.newInstance());
/*     */ 
/*     */       
/* 124 */       if (actionData.getNestedComplexProperty() instanceof ContextAware) {
/* 125 */         ((ContextAware)actionData.getNestedComplexProperty()).setContext(this.context);
/*     */       }
/*     */ 
/*     */       
/* 129 */       ec.pushObject(actionData.getNestedComplexProperty());
/*     */     }
/* 131 */     catch (Exception oops) {
/* 132 */       actionData.inError = true;
/* 133 */       String msg = "Could not create component [" + localName + "] of type [" + className + "]";
/* 134 */       addError(msg, oops);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(InterpretationContext ec, String tagName) {
/* 143 */     IADataForComplexProperty actionData = this.actionDataStack.pop();
/*     */     
/* 145 */     if (actionData.inError) {
/*     */       return;
/*     */     }
/*     */     
/* 149 */     PropertySetter nestedBean = new PropertySetter(this.beanDescriptionCache, actionData.getNestedComplexProperty());
/* 150 */     nestedBean.setContext(this.context);
/*     */ 
/*     */     
/* 153 */     if (nestedBean.computeAggregationType("parent") == AggregationType.AS_COMPLEX_PROPERTY) {
/* 154 */       nestedBean.setComplexProperty("parent", actionData.parentBean.getObj());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 159 */     Object nestedComplexProperty = actionData.getNestedComplexProperty();
/* 160 */     if (nestedComplexProperty instanceof LifeCycle && NoAutoStartUtil.notMarkedWithNoAutoStart(nestedComplexProperty)) {
/* 161 */       ((LifeCycle)nestedComplexProperty).start();
/*     */     }
/*     */     
/* 164 */     Object o = ec.peekObject();
/*     */     
/* 166 */     if (o != actionData.getNestedComplexProperty()) {
/* 167 */       addError("The object on the top the of the stack is not the component pushed earlier.");
/*     */     } else {
/* 169 */       ec.popObject();
/*     */       
/* 171 */       switch (actionData.aggregationType) {
/*     */         case AS_COMPLEX_PROPERTY:
/* 173 */           actionData.parentBean.setComplexProperty(tagName, actionData.getNestedComplexProperty());
/*     */           return;
/*     */         
/*     */         case AS_COMPLEX_PROPERTY_COLLECTION:
/* 177 */           actionData.parentBean.addComplexProperty(tagName, actionData.getNestedComplexProperty());
/*     */           return;
/*     */       } 
/* 180 */       addError("Unexpected aggregationType " + actionData.aggregationType);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\NestedComplexPropertyIA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */