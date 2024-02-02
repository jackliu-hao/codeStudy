/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.joran.spi.Interpreter;
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.Locator;
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
/*    */ public abstract class Action
/*    */   extends ContextAwareBase
/*    */ {
/*    */   public static final String NAME_ATTRIBUTE = "name";
/*    */   public static final String KEY_ATTRIBUTE = "key";
/*    */   public static final String VALUE_ATTRIBUTE = "value";
/*    */   public static final String FILE_ATTRIBUTE = "file";
/*    */   public static final String CLASS_ATTRIBUTE = "class";
/*    */   public static final String PATTERN_ATTRIBUTE = "pattern";
/*    */   public static final String SCOPE_ATTRIBUTE = "scope";
/*    */   public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";
/*    */   
/*    */   public abstract void begin(InterpretationContext paramInterpretationContext, String paramString, Attributes paramAttributes) throws ActionException;
/*    */   
/*    */   public void body(InterpretationContext ic, String body) throws ActionException {}
/*    */   
/*    */   public abstract void end(InterpretationContext paramInterpretationContext, String paramString) throws ActionException;
/*    */   
/*    */   public String toString() {
/* 73 */     return getClass().getName();
/*    */   }
/*    */   
/*    */   protected int getColumnNumber(InterpretationContext ic) {
/* 77 */     Interpreter ji = ic.getJoranInterpreter();
/* 78 */     Locator locator = ji.getLocator();
/* 79 */     if (locator != null) {
/* 80 */       return locator.getColumnNumber();
/*    */     }
/* 82 */     return -1;
/*    */   }
/*    */   
/*    */   protected int getLineNumber(InterpretationContext ic) {
/* 86 */     Interpreter ji = ic.getJoranInterpreter();
/* 87 */     Locator locator = ji.getLocator();
/* 88 */     if (locator != null) {
/* 89 */       return locator.getLineNumber();
/*    */     }
/* 91 */     return -1;
/*    */   }
/*    */   
/*    */   protected String getLineColStr(InterpretationContext ic) {
/* 95 */     return "line: " + getLineNumber(ic) + ", column: " + getColumnNumber(ic);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\Action.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */