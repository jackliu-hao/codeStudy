/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.PatternLayout;
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.pattern.CompositeConverter;
/*    */ import ch.qos.logback.core.pattern.Converter;
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
/*    */ public class PrefixCompositeConverter
/*    */   extends CompositeConverter<ILoggingEvent>
/*    */ {
/*    */   public String convert(ILoggingEvent event) {
/* 24 */     StringBuilder buf = new StringBuilder();
/* 25 */     Converter<ILoggingEvent> childConverter = getChildConverter();
/*    */     
/* 27 */     for (Converter<ILoggingEvent> c = childConverter; c != null; c = c.getNext()) {
/* 28 */       if (c instanceof MDCConverter) {
/* 29 */         MDCConverter mdcConverter = (MDCConverter)c;
/*    */         
/* 31 */         String key = mdcConverter.getKey();
/* 32 */         if (key != null) {
/* 33 */           buf.append(key).append("=");
/*    */         }
/* 35 */       } else if (c instanceof PropertyConverter) {
/* 36 */         PropertyConverter pc = (PropertyConverter)c;
/* 37 */         String key = pc.getKey();
/* 38 */         if (key != null) {
/* 39 */           buf.append(key).append("=");
/*    */         }
/*    */       } else {
/* 42 */         String classOfConverter = c.getClass().getName();
/*    */         
/* 44 */         String key = (String)PatternLayout.CONVERTER_CLASS_TO_KEY_MAP.get(classOfConverter);
/* 45 */         if (key != null)
/* 46 */           buf.append(key).append("="); 
/*    */       } 
/* 48 */       buf.append(c.convert(event));
/*    */     } 
/* 50 */     return buf.toString();
/*    */   }
/*    */   
/*    */   protected String transform(ILoggingEvent event, String in) {
/* 54 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\PrefixCompositeConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */