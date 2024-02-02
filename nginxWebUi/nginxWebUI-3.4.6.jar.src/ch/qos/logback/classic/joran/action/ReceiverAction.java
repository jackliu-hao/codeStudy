/*    */ package ch.qos.logback.classic.joran.action;
/*    */ 
/*    */ import ch.qos.logback.classic.net.ReceiverBase;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ReceiverAction
/*    */   extends Action
/*    */ {
/*    */   private ReceiverBase receiver;
/*    */   private boolean inError;
/*    */   
/*    */   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
/* 38 */     String className = attributes.getValue("class");
/* 39 */     if (OptionHelper.isEmpty(className)) {
/* 40 */       addError("Missing class name for receiver. Near [" + name + "] line " + getLineNumber(ic));
/* 41 */       this.inError = true;
/*    */       
/*    */       return;
/*    */     } 
/*    */     try {
/* 46 */       addInfo("About to instantiate receiver of type [" + className + "]");
/*    */       
/* 48 */       this.receiver = (ReceiverBase)OptionHelper.instantiateByClassName(className, ReceiverBase.class, this.context);
/* 49 */       this.receiver.setContext(this.context);
/*    */       
/* 51 */       ic.pushObject(this.receiver);
/* 52 */     } catch (Exception ex) {
/* 53 */       this.inError = true;
/* 54 */       addError("Could not create a receiver of type [" + className + "].", ex);
/* 55 */       throw new ActionException(ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void end(InterpretationContext ic, String name) throws ActionException {
/* 62 */     if (this.inError) {
/*    */       return;
/*    */     }
/* 65 */     ic.getContext().register((LifeCycle)this.receiver);
/* 66 */     this.receiver.start();
/*    */     
/* 68 */     Object o = ic.peekObject();
/* 69 */     if (o != this.receiver) {
/* 70 */       addWarn("The object at the of the stack is not the remote pushed earlier.");
/*    */     } else {
/* 72 */       ic.popObject();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\ReceiverAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */