/*    */ package ch.qos.logback.classic.boolex;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.boolex.EvaluationException;
/*    */ import ch.qos.logback.core.boolex.EventEvaluatorBase;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.slf4j.Marker;
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
/*    */ public class OnMarkerEvaluator
/*    */   extends EventEvaluatorBase<ILoggingEvent>
/*    */ {
/* 33 */   List<String> markerList = new ArrayList<String>();
/*    */   
/*    */   public void addMarker(String markerStr) {
/* 36 */     this.markerList.add(markerStr);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
/* 45 */     Marker eventsMarker = event.getMarker();
/* 46 */     if (eventsMarker == null) {
/* 47 */       return false;
/*    */     }
/*    */     
/* 50 */     for (String markerStr : this.markerList) {
/* 51 */       if (eventsMarker.contains(markerStr)) {
/* 52 */         return true;
/*    */       }
/*    */     } 
/* 55 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\boolex\OnMarkerEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */