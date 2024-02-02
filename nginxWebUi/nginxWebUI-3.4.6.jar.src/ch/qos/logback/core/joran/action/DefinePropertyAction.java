/*     */ package ch.qos.logback.core.joran.action;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.ActionException;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.spi.LifeCycle;
/*     */ import ch.qos.logback.core.spi.PropertyDefiner;
/*     */ import ch.qos.logback.core.util.OptionHelper;
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
/*     */ public class DefinePropertyAction
/*     */   extends Action
/*     */ {
/*     */   String scopeStr;
/*     */   ActionUtil.Scope scope;
/*     */   String propertyName;
/*     */   PropertyDefiner definer;
/*     */   boolean inError;
/*     */   
/*     */   public void begin(InterpretationContext ec, String localName, Attributes attributes) throws ActionException {
/*  41 */     this.scopeStr = null;
/*  42 */     this.scope = null;
/*  43 */     this.propertyName = null;
/*  44 */     this.definer = null;
/*  45 */     this.inError = false;
/*     */ 
/*     */     
/*  48 */     this.propertyName = attributes.getValue("name");
/*  49 */     this.scopeStr = attributes.getValue("scope");
/*     */     
/*  51 */     this.scope = ActionUtil.stringToScope(this.scopeStr);
/*  52 */     if (OptionHelper.isEmpty(this.propertyName)) {
/*  53 */       addError("Missing property name for property definer. Near [" + localName + "] line " + getLineNumber(ec));
/*  54 */       this.inError = true;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  59 */     String className = attributes.getValue("class");
/*  60 */     if (OptionHelper.isEmpty(className)) {
/*  61 */       addError("Missing class name for property definer. Near [" + localName + "] line " + getLineNumber(ec));
/*  62 */       this.inError = true;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*     */     try {
/*  68 */       addInfo("About to instantiate property definer of type [" + className + "]");
/*  69 */       this.definer = (PropertyDefiner)OptionHelper.instantiateByClassName(className, PropertyDefiner.class, this.context);
/*  70 */       this.definer.setContext(this.context);
/*  71 */       if (this.definer instanceof LifeCycle) {
/*  72 */         ((LifeCycle)this.definer).start();
/*     */       }
/*  74 */       ec.pushObject(this.definer);
/*  75 */     } catch (Exception oops) {
/*  76 */       this.inError = true;
/*  77 */       addError("Could not create an PropertyDefiner of type [" + className + "].", oops);
/*  78 */       throw new ActionException(oops);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(InterpretationContext ec, String name) {
/*  87 */     if (this.inError) {
/*     */       return;
/*     */     }
/*     */     
/*  91 */     Object o = ec.peekObject();
/*     */     
/*  93 */     if (o != this.definer) {
/*  94 */       addWarn("The object at the of the stack is not the property definer for property named [" + this.propertyName + "] pushed earlier.");
/*     */     } else {
/*  96 */       addInfo("Popping property definer for property named [" + this.propertyName + "] from the object stack");
/*  97 */       ec.popObject();
/*     */ 
/*     */       
/* 100 */       String propertyValue = this.definer.getPropertyValue();
/* 101 */       if (propertyValue != null)
/* 102 */         ActionUtil.setProperty(ec, this.propertyName, propertyValue, this.scope); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\DefinePropertyAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */