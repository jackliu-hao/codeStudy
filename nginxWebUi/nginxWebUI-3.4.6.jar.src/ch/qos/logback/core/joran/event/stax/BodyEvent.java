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
/*    */ public class BodyEvent
/*    */   extends StaxEvent
/*    */ {
/*    */   private String text;
/*    */   
/*    */   BodyEvent(String text, Location location) {
/* 23 */     super(null, location);
/* 24 */     this.text = text;
/*    */   }
/*    */   
/*    */   public String getText() {
/* 28 */     return this.text;
/*    */   }
/*    */   
/*    */   void append(String txt) {
/* 32 */     this.text += txt;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 37 */     return "BodyEvent(" + getText() + ")" + this.location.getLineNumber() + "," + this.location.getColumnNumber();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\event\stax\BodyEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */