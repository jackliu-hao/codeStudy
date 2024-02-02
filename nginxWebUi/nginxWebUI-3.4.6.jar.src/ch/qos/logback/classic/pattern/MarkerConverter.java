/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
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
/*    */ public class MarkerConverter
/*    */   extends ClassicConverter
/*    */ {
/* 27 */   private static String EMPTY = "";
/*    */   
/*    */   public String convert(ILoggingEvent le) {
/* 30 */     Marker marker = le.getMarker();
/* 31 */     if (marker == null) {
/* 32 */       return EMPTY;
/*    */     }
/* 34 */     return marker.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\MarkerConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */