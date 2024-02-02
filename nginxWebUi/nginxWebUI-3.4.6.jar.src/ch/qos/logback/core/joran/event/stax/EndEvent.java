/*    */ package ch.qos.logback.core.joran.event.stax;
/*    */ 
/*    */ import javax.xml.stream.Location;
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
/*    */ public class EndEvent
/*    */   extends StaxEvent
/*    */ {
/*    */   public EndEvent(String name, Location location) {
/* 28 */     super(name, location);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 33 */     return "EndEvent(" + getName() + ")  [" + this.location.getLineNumber() + "," + this.location.getColumnNumber() + "]";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\event\stax\EndEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */