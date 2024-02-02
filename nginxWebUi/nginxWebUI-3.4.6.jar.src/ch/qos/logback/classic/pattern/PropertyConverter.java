/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.LoggerContextVO;
/*    */ import java.util.Map;
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
/*    */ public final class PropertyConverter
/*    */   extends ClassicConverter
/*    */ {
/*    */   String key;
/*    */   
/*    */   public void start() {
/* 26 */     String optStr = getFirstOption();
/* 27 */     if (optStr != null) {
/* 28 */       this.key = optStr;
/* 29 */       super.start();
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getKey() {
/* 34 */     return this.key;
/*    */   }
/*    */   
/*    */   public String convert(ILoggingEvent event) {
/* 38 */     if (this.key == null) {
/* 39 */       return "Property_HAS_NO_KEY";
/*    */     }
/* 41 */     LoggerContextVO lcvo = event.getLoggerContextVO();
/* 42 */     Map<String, String> map = lcvo.getPropertyMap();
/* 43 */     String val = map.get(this.key);
/* 44 */     if (val != null) {
/* 45 */       return val;
/*    */     }
/* 47 */     return System.getProperty(this.key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\PropertyConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */