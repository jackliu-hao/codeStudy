/*    */ package ch.qos.logback.core.joran.event;
/*    */ 
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
/*    */ public class BodyEvent
/*    */   extends SaxEvent
/*    */ {
/*    */   private String text;
/*    */   
/*    */   BodyEvent(String text, Locator locator) {
/* 23 */     super(null, null, null, locator);
/* 24 */     this.text = text;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getText() {
/* 33 */     if (this.text != null) {
/* 34 */       return this.text.trim();
/*    */     }
/* 36 */     return this.text;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "BodyEvent(" + getText() + ")" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber();
/*    */   }
/*    */   
/*    */   public void append(String str) {
/* 45 */     this.text += str;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\event\BodyEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */