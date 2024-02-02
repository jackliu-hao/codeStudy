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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoggerAction
/*    */   extends Action
/*    */ {
/*    */   public static final String LEVEL_ATTRIBUTE = "level";
/*    */   boolean inError = false;
/*    */   Logger logger;
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) {
/* 39 */     this.inError = false;
/* 40 */     this.logger = null;
/*    */     
/* 42 */     LoggerContext loggerContext = (LoggerContext)this.context;
/*    */     
/* 44 */     String loggerName = ec.subst(attributes.getValue("name"));
/*    */     
/* 46 */     if (OptionHelper.isEmpty(loggerName)) {
/* 47 */       this.inError = true;
/* 48 */       String aroundLine = getLineColStr(ec);
/* 49 */       String errorMsg = "No 'name' attribute in element " + name + ", around " + aroundLine;
/* 50 */       addError(errorMsg);
/*    */       
/*    */       return;
/*    */     } 
/* 54 */     this.logger = loggerContext.getLogger(loggerName);
/*    */     
/* 56 */     String levelStr = ec.subst(attributes.getValue("level"));
/*    */     
/* 58 */     if (!OptionHelper.isEmpty(levelStr)) {
/* 59 */       if ("INHERITED".equalsIgnoreCase(levelStr) || "NULL".equalsIgnoreCase(levelStr)) {
/* 60 */         addInfo("Setting level of logger [" + loggerName + "] to null, i.e. INHERITED");
/* 61 */         this.logger.setLevel(null);
/*    */       } else {
/* 63 */         Level level = Level.toLevel(levelStr);
/* 64 */         addInfo("Setting level of logger [" + loggerName + "] to " + level);
/* 65 */         this.logger.setLevel(level);
/*    */       } 
/*    */     }
/*    */     
/* 69 */     String additivityStr = ec.subst(attributes.getValue("additivity"));
/* 70 */     if (!OptionHelper.isEmpty(additivityStr)) {
/* 71 */       boolean additive = OptionHelper.toBoolean(additivityStr, true);
/* 72 */       addInfo("Setting additivity of logger [" + loggerName + "] to " + additive);
/* 73 */       this.logger.setAdditive(additive);
/*    */     } 
/* 75 */     ec.pushObject(this.logger);
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String e) {
/* 79 */     if (this.inError) {
/*    */       return;
/*    */     }
/* 82 */     Object o = ec.peekObject();
/* 83 */     if (o != this.logger) {
/* 84 */       addWarn("The object on the top the of the stack is not " + this.logger + " pushed earlier");
/* 85 */       addWarn("It is: " + o);
/*    */     } else {
/* 87 */       ec.popObject();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void finish(InterpretationContext ec) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\LoggerAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */