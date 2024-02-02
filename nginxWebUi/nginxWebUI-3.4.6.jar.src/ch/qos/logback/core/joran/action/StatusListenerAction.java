/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.spi.ContextAware;
/*    */ import ch.qos.logback.core.spi.LifeCycle;
/*    */ import ch.qos.logback.core.status.StatusListener;
/*    */ import ch.qos.logback.core.util.OptionHelper;
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
/*    */ public class StatusListenerAction
/*    */   extends Action
/*    */ {
/*    */   boolean inError = false;
/* 28 */   Boolean effectivelyAdded = null;
/* 29 */   StatusListener statusListener = null;
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
/* 32 */     this.inError = false;
/* 33 */     this.effectivelyAdded = null;
/* 34 */     String className = attributes.getValue("class");
/* 35 */     if (OptionHelper.isEmpty(className)) {
/* 36 */       addError("Missing class name for statusListener. Near [" + name + "] line " + getLineNumber(ec));
/* 37 */       this.inError = true;
/*    */       
/*    */       return;
/*    */     } 
/*    */     try {
/* 42 */       this.statusListener = (StatusListener)OptionHelper.instantiateByClassName(className, StatusListener.class, this.context);
/* 43 */       this.effectivelyAdded = Boolean.valueOf(ec.getContext().getStatusManager().add(this.statusListener));
/* 44 */       if (this.statusListener instanceof ContextAware) {
/* 45 */         ((ContextAware)this.statusListener).setContext(this.context);
/*    */       }
/* 47 */       addInfo("Added status listener of type [" + className + "]");
/* 48 */       ec.pushObject(this.statusListener);
/* 49 */     } catch (Exception e) {
/* 50 */       this.inError = true;
/* 51 */       addError("Could not create an StatusListener of type [" + className + "].", e);
/* 52 */       throw new ActionException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void finish(InterpretationContext ec) {}
/*    */ 
/*    */   
/*    */   public void end(InterpretationContext ec, String e) {
/* 61 */     if (this.inError) {
/*    */       return;
/*    */     }
/* 64 */     if (isEffectivelyAdded() && this.statusListener instanceof LifeCycle) {
/* 65 */       ((LifeCycle)this.statusListener).start();
/*    */     }
/* 67 */     Object o = ec.peekObject();
/* 68 */     if (o != this.statusListener) {
/* 69 */       addWarn("The object at the of the stack is not the statusListener pushed earlier.");
/*    */     } else {
/* 71 */       ec.popObject();
/*    */     } 
/*    */   }
/*    */   
/*    */   private boolean isEffectivelyAdded() {
/* 76 */     if (this.effectivelyAdded == null)
/* 77 */       return false; 
/* 78 */     return this.effectivelyAdded.booleanValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\StatusListenerAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */