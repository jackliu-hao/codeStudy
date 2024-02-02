/*     */ package ch.qos.logback.core.joran.conditional;
/*     */ 
/*     */ import ch.qos.logback.core.joran.action.Action;
/*     */ import ch.qos.logback.core.joran.event.SaxEvent;
/*     */ import ch.qos.logback.core.joran.spi.ActionException;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.spi.Interpreter;
/*     */ import ch.qos.logback.core.spi.PropertyContainer;
/*     */ import ch.qos.logback.core.util.EnvUtil;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IfAction
/*     */   extends Action
/*     */ {
/*     */   private static final String CONDITION_ATTR = "condition";
/*     */   public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
/*     */   public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
/*  36 */   Stack<IfState> stack = new Stack<IfState>();
/*     */ 
/*     */ 
/*     */   
/*     */   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
/*  41 */     IfState state = new IfState();
/*  42 */     boolean emptyStack = this.stack.isEmpty();
/*  43 */     this.stack.push(state);
/*     */     
/*  45 */     if (!emptyStack) {
/*     */       return;
/*     */     }
/*     */     
/*  49 */     ic.pushObject(this);
/*  50 */     if (!EnvUtil.isJaninoAvailable()) {
/*  51 */       addError("Could not find Janino library on the class path. Skipping conditional processing.");
/*  52 */       addError("See also http://logback.qos.ch/codes.html#ifJanino");
/*     */       
/*     */       return;
/*     */     } 
/*  56 */     state.active = true;
/*  57 */     Condition condition = null;
/*  58 */     String conditionAttribute = attributes.getValue("condition");
/*     */     
/*  60 */     if (!OptionHelper.isEmpty(conditionAttribute)) {
/*  61 */       conditionAttribute = OptionHelper.substVars(conditionAttribute, (PropertyContainer)ic, (PropertyContainer)this.context);
/*  62 */       PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder((PropertyContainer)ic);
/*  63 */       pesb.setContext(this.context);
/*     */       try {
/*  65 */         condition = pesb.build(conditionAttribute);
/*  66 */       } catch (Exception e) {
/*  67 */         addError("Failed to parse condition [" + conditionAttribute + "]", e);
/*     */       } 
/*     */       
/*  70 */       if (condition != null) {
/*  71 */         state.boolResult = Boolean.valueOf(condition.evaluate());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void end(InterpretationContext ic, String name) throws ActionException {
/*  80 */     IfState state = this.stack.pop();
/*  81 */     if (!state.active) {
/*     */       return;
/*     */     }
/*     */     
/*  85 */     Object o = ic.peekObject();
/*  86 */     if (o == null) {
/*  87 */       throw new IllegalStateException("Unexpected null object on stack");
/*     */     }
/*  89 */     if (!(o instanceof IfAction)) {
/*  90 */       throw new IllegalStateException("Unexpected object of type [" + o.getClass() + "] on stack");
/*     */     }
/*     */     
/*  93 */     if (o != this) {
/*  94 */       throw new IllegalStateException("IfAction different then current one on stack");
/*     */     }
/*  96 */     ic.popObject();
/*     */     
/*  98 */     if (state.boolResult == null) {
/*  99 */       addError("Failed to determine \"if then else\" result");
/*     */       
/*     */       return;
/*     */     } 
/* 103 */     Interpreter interpreter = ic.getJoranInterpreter();
/* 104 */     List<SaxEvent> listToPlay = state.thenSaxEventList;
/* 105 */     if (!state.boolResult.booleanValue()) {
/* 106 */       listToPlay = state.elseSaxEventList;
/*     */     }
/*     */ 
/*     */     
/* 110 */     if (listToPlay != null)
/*     */     {
/* 112 */       interpreter.getEventPlayer().addEventsDynamically(listToPlay, 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setThenSaxEventList(List<SaxEvent> thenSaxEventList) {
/* 118 */     IfState state = this.stack.firstElement();
/* 119 */     if (state.active) {
/* 120 */       state.thenSaxEventList = thenSaxEventList;
/*     */     } else {
/* 122 */       throw new IllegalStateException("setThenSaxEventList() invoked on inactive IfAction");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setElseSaxEventList(List<SaxEvent> elseSaxEventList) {
/* 127 */     IfState state = this.stack.firstElement();
/* 128 */     if (state.active) {
/* 129 */       state.elseSaxEventList = elseSaxEventList;
/*     */     } else {
/* 131 */       throw new IllegalStateException("setElseSaxEventList() invoked on inactive IfAction");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 137 */     if (this.stack == null)
/* 138 */       return false; 
/* 139 */     if (this.stack.isEmpty())
/* 140 */       return false; 
/* 141 */     return ((IfState)this.stack.peek()).active;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\conditional\IfAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */