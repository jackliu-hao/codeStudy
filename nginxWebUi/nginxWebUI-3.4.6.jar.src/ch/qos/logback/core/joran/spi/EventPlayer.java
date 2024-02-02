/*    */ package ch.qos.logback.core.joran.spi;
/*    */ 
/*    */ import ch.qos.logback.core.joran.event.BodyEvent;
/*    */ import ch.qos.logback.core.joran.event.EndEvent;
/*    */ import ch.qos.logback.core.joran.event.SaxEvent;
/*    */ import ch.qos.logback.core.joran.event.StartEvent;
/*    */ import java.util.ArrayList;
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
/*    */ public class EventPlayer
/*    */ {
/*    */   final Interpreter interpreter;
/*    */   List<SaxEvent> eventList;
/*    */   int currentIndex;
/*    */   
/*    */   public EventPlayer(Interpreter interpreter) {
/* 31 */     this.interpreter = interpreter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<SaxEvent> getCopyOfPlayerEventList() {
/* 40 */     return new ArrayList<SaxEvent>(this.eventList);
/*    */   }
/*    */   
/*    */   public void play(List<SaxEvent> aSaxEventList) {
/* 44 */     this.eventList = aSaxEventList;
/*    */     
/* 46 */     for (this.currentIndex = 0; this.currentIndex < this.eventList.size(); this.currentIndex++) {
/* 47 */       SaxEvent se = this.eventList.get(this.currentIndex);
/*    */       
/* 49 */       if (se instanceof StartEvent) {
/* 50 */         this.interpreter.startElement((StartEvent)se);
/*    */         
/* 52 */         this.interpreter.getInterpretationContext().fireInPlay(se);
/*    */       } 
/* 54 */       if (se instanceof BodyEvent) {
/*    */         
/* 56 */         this.interpreter.getInterpretationContext().fireInPlay(se);
/* 57 */         this.interpreter.characters((BodyEvent)se);
/*    */       } 
/* 59 */       if (se instanceof EndEvent) {
/*    */         
/* 61 */         this.interpreter.getInterpretationContext().fireInPlay(se);
/* 62 */         this.interpreter.endElement((EndEvent)se);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void addEventsDynamically(List<SaxEvent> eventList, int offset) {
/* 69 */     this.eventList.addAll(this.currentIndex + offset, eventList);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\EventPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */