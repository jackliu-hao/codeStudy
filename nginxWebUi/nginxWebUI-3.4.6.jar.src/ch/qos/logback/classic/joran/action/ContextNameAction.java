/*    */ package ch.qos.logback.classic.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
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
/*    */ public class ContextNameAction
/*    */   extends Action
/*    */ {
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) {}
/*    */   
/*    */   public void body(InterpretationContext ec, String body) {
/* 28 */     String finalBody = ec.subst(body);
/* 29 */     addInfo("Setting logger context name as [" + finalBody + "]");
/*    */     try {
/* 31 */       this.context.setName(finalBody);
/* 32 */     } catch (IllegalStateException e) {
/* 33 */       addError("Failed to rename context [" + this.context.getName() + "] as [" + finalBody + "]", e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String name) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\ContextNameAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */