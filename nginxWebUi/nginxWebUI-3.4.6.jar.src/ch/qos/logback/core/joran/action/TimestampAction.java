/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.util.CachingDateFormatter;
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
/*    */ 
/*    */ public class TimestampAction
/*    */   extends Action
/*    */ {
/* 33 */   static String DATE_PATTERN_ATTRIBUTE = "datePattern";
/* 34 */   static String TIME_REFERENCE_ATTRIBUTE = "timeReference";
/* 35 */   static String CONTEXT_BIRTH = "contextBirth";
/*    */   
/*    */   boolean inError = false;
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
/*    */     long timeReference;
/* 41 */     String keyStr = attributes.getValue("key");
/* 42 */     if (OptionHelper.isEmpty(keyStr)) {
/* 43 */       addError("Attribute named [key] cannot be empty");
/* 44 */       this.inError = true;
/*    */     } 
/* 46 */     String datePatternStr = attributes.getValue(DATE_PATTERN_ATTRIBUTE);
/* 47 */     if (OptionHelper.isEmpty(datePatternStr)) {
/* 48 */       addError("Attribute named [" + DATE_PATTERN_ATTRIBUTE + "] cannot be empty");
/* 49 */       this.inError = true;
/*    */     } 
/*    */     
/* 52 */     String timeReferenceStr = attributes.getValue(TIME_REFERENCE_ATTRIBUTE);
/*    */     
/* 54 */     if (CONTEXT_BIRTH.equalsIgnoreCase(timeReferenceStr)) {
/* 55 */       addInfo("Using context birth as time reference.");
/* 56 */       timeReference = this.context.getBirthTime();
/*    */     } else {
/* 58 */       timeReference = System.currentTimeMillis();
/* 59 */       addInfo("Using current interpretation time, i.e. now, as time reference.");
/*    */     } 
/*    */     
/* 62 */     if (this.inError) {
/*    */       return;
/*    */     }
/* 65 */     String scopeStr = attributes.getValue("scope");
/* 66 */     ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
/*    */     
/* 68 */     CachingDateFormatter sdf = new CachingDateFormatter(datePatternStr);
/* 69 */     String val = sdf.format(timeReference);
/*    */     
/* 71 */     addInfo("Adding property to the context with key=\"" + keyStr + "\" and value=\"" + val + "\" to the " + scope + " scope");
/* 72 */     ActionUtil.setProperty(ec, keyStr, val, scope);
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String name) throws ActionException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\TimestampAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */