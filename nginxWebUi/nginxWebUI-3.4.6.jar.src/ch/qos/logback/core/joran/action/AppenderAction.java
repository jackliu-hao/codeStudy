/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.Appender;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import java.util.HashMap;
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
/*    */ 
/*    */ 
/*    */ public class AppenderAction<E>
/*    */   extends Action
/*    */ {
/*    */   Appender<E> appender;
/*    */   private boolean inError = false;
/*    */   
/*    */   public void begin(InterpretationContext ec, String localName, Attributes attributes) throws ActionException {
/* 39 */     this.appender = null;
/* 40 */     this.inError = false;
/*    */     
/* 42 */     String className = attributes.getValue("class");
/* 43 */     if (OptionHelper.isEmpty(className)) {
/* 44 */       addError("Missing class name for appender. Near [" + localName + "] line " + getLineNumber(ec));
/* 45 */       this.inError = true;
/*    */       
/*    */       return;
/*    */     } 
/*    */     try {
/* 50 */       addInfo("About to instantiate appender of type [" + className + "]");
/*    */       
/* 52 */       this.appender = (Appender<E>)OptionHelper.instantiateByClassName(className, Appender.class, this.context);
/*    */       
/* 54 */       this.appender.setContext(this.context);
/*    */       
/* 56 */       String appenderName = ec.subst(attributes.getValue("name"));
/*    */       
/* 58 */       if (OptionHelper.isEmpty(appenderName)) {
/* 59 */         addWarn("No appender name given for appender of type " + className + "].");
/*    */       } else {
/* 61 */         this.appender.setName(appenderName);
/* 62 */         addInfo("Naming appender as [" + appenderName + "]");
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/* 67 */       HashMap<String, Appender<E>> appenderBag = (HashMap<String, Appender<E>>)ec.getObjectMap().get("APPENDER_BAG");
/*    */ 
/*    */       
/* 70 */       appenderBag.put(appenderName, this.appender);
/*    */       
/* 72 */       ec.pushObject(this.appender);
/* 73 */     } catch (Exception oops) {
/* 74 */       this.inError = true;
/* 75 */       addError("Could not create an Appender of type [" + className + "].", oops);
/* 76 */       throw new ActionException(oops);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void end(InterpretationContext ec, String name) {
/* 85 */     if (this.inError) {
/*    */       return;
/*    */     }
/*    */     
/* 89 */     if (this.appender instanceof ch.qos.logback.core.spi.LifeCycle) {
/* 90 */       this.appender.start();
/*    */     }
/*    */     
/* 93 */     Object o = ec.peekObject();
/*    */     
/* 95 */     if (o != this.appender) {
/* 96 */       addWarn("The object at the of the stack is not the appender named [" + this.appender.getName() + "] pushed earlier.");
/*    */     } else {
/* 98 */       ec.popObject();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\AppenderAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */