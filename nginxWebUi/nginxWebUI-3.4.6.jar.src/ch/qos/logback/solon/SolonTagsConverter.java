/*    */ package ch.qos.logback.solon;
/*    */ 
/*    */ import ch.qos.logback.classic.pattern.MessageConverter;
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonTagsConverter
/*    */   extends MessageConverter
/*    */ {
/*    */   public String convert(ILoggingEvent event) {
/* 16 */     Map<String, String> eData = event.getMDCPropertyMap();
/*    */     
/* 18 */     if (eData != null) {
/* 19 */       StringBuilder buf = new StringBuilder();
/* 20 */       eData.forEach((tag, val) -> {
/*    */             if (!"traceId".equals(tag)) {
/*    */               buf.append("[@").append(tag).append(":").append(val).append("]");
/*    */             }
/*    */           });
/* 25 */       return buf.toString();
/*    */     } 
/* 27 */     return "";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\solon\SolonTagsConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */