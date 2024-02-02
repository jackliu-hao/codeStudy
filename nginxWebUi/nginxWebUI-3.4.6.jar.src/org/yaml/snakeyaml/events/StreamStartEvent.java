/*    */ package org.yaml.snakeyaml.events;
/*    */ 
/*    */ import org.yaml.snakeyaml.error.Mark;
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
/*    */ public final class StreamStartEvent
/*    */   extends Event
/*    */ {
/*    */   public StreamStartEvent(Mark startMark, Mark endMark) {
/* 34 */     super(startMark, endMark);
/*    */   }
/*    */ 
/*    */   
/*    */   public Event.ID getEventId() {
/* 39 */     return Event.ID.StreamStart;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\events\StreamStartEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */