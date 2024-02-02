/*    */ package ch.qos.logback.core.joran.conditional;
/*    */ 
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.event.SaxEvent;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import java.util.List;
/*    */ import java.util.Stack;
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
/*    */ public abstract class ThenOrElseActionBase
/*    */   extends Action
/*    */ {
/* 30 */   Stack<ThenActionState> stateStack = new Stack<ThenActionState>();
/*    */ 
/*    */ 
/*    */   
/*    */   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
/* 35 */     if (!weAreActive(ic)) {
/*    */       return;
/*    */     }
/* 38 */     ThenActionState state = new ThenActionState();
/* 39 */     if (ic.isListenerListEmpty()) {
/* 40 */       ic.addInPlayListener(state);
/* 41 */       state.isRegistered = true;
/*    */     } 
/* 43 */     this.stateStack.push(state);
/*    */   }
/*    */   
/*    */   boolean weAreActive(InterpretationContext ic) {
/* 47 */     Object o = ic.peekObject();
/* 48 */     if (!(o instanceof IfAction))
/* 49 */       return false; 
/* 50 */     IfAction ifAction = (IfAction)o;
/* 51 */     return ifAction.isActive();
/*    */   }
/*    */ 
/*    */   
/*    */   public void end(InterpretationContext ic, String name) throws ActionException {
/* 56 */     if (!weAreActive(ic)) {
/*    */       return;
/*    */     }
/* 59 */     ThenActionState state = this.stateStack.pop();
/* 60 */     if (state.isRegistered) {
/* 61 */       ic.removeInPlayListener(state);
/* 62 */       Object o = ic.peekObject();
/* 63 */       if (o instanceof IfAction) {
/* 64 */         IfAction ifAction = (IfAction)o;
/* 65 */         removeFirstAndLastFromList(state.eventList);
/* 66 */         registerEventList(ifAction, state.eventList);
/*    */       } else {
/* 68 */         throw new IllegalStateException("Missing IfAction on top of stack");
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   abstract void registerEventList(IfAction paramIfAction, List<SaxEvent> paramList);
/*    */   
/*    */   void removeFirstAndLastFromList(List<SaxEvent> eventList) {
/* 76 */     eventList.remove(0);
/* 77 */     eventList.remove(eventList.size() - 1);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\conditional\ThenOrElseActionBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */