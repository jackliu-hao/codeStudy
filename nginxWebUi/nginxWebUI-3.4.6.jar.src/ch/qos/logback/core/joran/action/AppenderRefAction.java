/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.Appender;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.spi.AppenderAttachable;
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
/*    */ public class AppenderRefAction<E>
/*    */   extends Action
/*    */ {
/*    */   boolean inError = false;
/*    */   
/*    */   public void begin(InterpretationContext ec, String tagName, Attributes attributes) {
/* 32 */     this.inError = false;
/*    */ 
/*    */ 
/*    */     
/* 36 */     Object o = ec.peekObject();
/*    */     
/* 38 */     if (!(o instanceof AppenderAttachable)) {
/* 39 */       String errMsg = "Could not find an AppenderAttachable at the top of execution stack. Near [" + tagName + "] line " + getLineNumber(ec);
/* 40 */       this.inError = true;
/* 41 */       addError(errMsg);
/*    */       
/*    */       return;
/*    */     } 
/* 45 */     AppenderAttachable<E> appenderAttachable = (AppenderAttachable<E>)o;
/*    */     
/* 47 */     String appenderName = ec.subst(attributes.getValue("ref"));
/*    */     
/* 49 */     if (OptionHelper.isEmpty(appenderName)) {
/*    */       
/* 51 */       String errMsg = "Missing appender ref attribute in <appender-ref> tag.";
/* 52 */       this.inError = true;
/* 53 */       addError(errMsg);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 58 */     HashMap<String, Appender<E>> appenderBag = (HashMap<String, Appender<E>>)ec.getObjectMap().get("APPENDER_BAG");
/* 59 */     Appender<E> appender = appenderBag.get(appenderName);
/*    */     
/* 61 */     if (appender == null) {
/* 62 */       String msg = "Could not find an appender named [" + appenderName + "]. Did you define it below instead of above in the configuration file?";
/* 63 */       this.inError = true;
/* 64 */       addError(msg);
/* 65 */       addError("See http://logback.qos.ch/codes.html#appender_order for more details.");
/*    */       
/*    */       return;
/*    */     } 
/* 69 */     addInfo("Attaching appender named [" + appenderName + "] to " + appenderAttachable);
/* 70 */     appenderAttachable.addAppender(appender);
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String n) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\AppenderRefAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */