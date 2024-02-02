/*    */ package ch.qos.logback.classic.joran.action;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
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
/*    */ public class RootLoggerAction
/*    */   extends Action
/*    */ {
/*    */   Logger root;
/*    */   boolean inError = false;
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) {
/* 32 */     this.inError = false;
/*    */     
/* 34 */     LoggerContext loggerContext = (LoggerContext)this.context;
/* 35 */     this.root = loggerContext.getLogger("ROOT");
/*    */     
/* 37 */     String levelStr = ec.subst(attributes.getValue("level"));
/* 38 */     if (!OptionHelper.isEmpty(levelStr)) {
/* 39 */       Level level = Level.toLevel(levelStr);
/* 40 */       addInfo("Setting level of ROOT logger to " + level);
/* 41 */       this.root.setLevel(level);
/*    */     } 
/* 43 */     ec.pushObject(this.root);
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String name) {
/* 47 */     if (this.inError) {
/*    */       return;
/*    */     }
/* 50 */     Object o = ec.peekObject();
/* 51 */     if (o != this.root) {
/* 52 */       addWarn("The object on the top the of the stack is not the root logger");
/* 53 */       addWarn("It is: " + o);
/*    */     } else {
/* 55 */       ec.popObject();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void finish(InterpretationContext ec) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\RootLoggerAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */