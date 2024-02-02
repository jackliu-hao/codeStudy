/*    */ package ch.qos.logback.core.joran.conditional;
/*    */ 
/*    */ import ch.qos.logback.core.joran.event.SaxEvent;
/*    */ import java.util.List;
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
/*    */ public class ElseAction
/*    */   extends ThenOrElseActionBase
/*    */ {
/*    */   void registerEventList(IfAction ifAction, List<SaxEvent> eventList) {
/* 24 */     ifAction.setElseSaxEventList(eventList);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\conditional\ElseAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */