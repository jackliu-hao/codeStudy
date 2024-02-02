/*    */ package ch.qos.logback.classic.joran.action;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.spi.LoggerContextListener;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.spi.ContextAware;
/*    */ import ch.qos.logback.core.spi.LifeCycle;
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
/*    */ 
/*    */ public class LoggerContextListenerAction
/*    */   extends Action
/*    */ {
/*    */   boolean inError = false;
/*    */   LoggerContextListener lcl;
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
/* 34 */     this.inError = false;
/*    */     
/* 36 */     String className = attributes.getValue("class");
/* 37 */     if (OptionHelper.isEmpty(className)) {
/* 38 */       addError("Mandatory \"class\" attribute not set for <loggerContextListener> element");
/* 39 */       this.inError = true;
/*    */       
/*    */       return;
/*    */     } 
/*    */     try {
/* 44 */       this.lcl = (LoggerContextListener)OptionHelper.instantiateByClassName(className, LoggerContextListener.class, this.context);
/*    */       
/* 46 */       if (this.lcl instanceof ContextAware) {
/* 47 */         ((ContextAware)this.lcl).setContext(this.context);
/*    */       }
/*    */       
/* 50 */       ec.pushObject(this.lcl);
/* 51 */       addInfo("Adding LoggerContextListener of type [" + className + "] to the object stack");
/*    */     }
/* 53 */     catch (Exception oops) {
/* 54 */       this.inError = true;
/* 55 */       addError("Could not create LoggerContextListener of type " + className + "].", oops);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void end(InterpretationContext ec, String name) throws ActionException {
/* 61 */     if (this.inError) {
/*    */       return;
/*    */     }
/* 64 */     Object o = ec.peekObject();
/*    */     
/* 66 */     if (o != this.lcl) {
/* 67 */       addWarn("The object on the top the of the stack is not the LoggerContextListener pushed earlier.");
/*    */     } else {
/* 69 */       if (this.lcl instanceof LifeCycle) {
/* 70 */         ((LifeCycle)this.lcl).start();
/* 71 */         addInfo("Starting LoggerContextListener");
/*    */       } 
/* 73 */       ((LoggerContext)this.context).addListener(this.lcl);
/* 74 */       ec.popObject();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\LoggerContextListenerAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */