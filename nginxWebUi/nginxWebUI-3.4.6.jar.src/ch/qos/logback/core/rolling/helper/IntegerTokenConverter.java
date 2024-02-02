/*    */ package ch.qos.logback.core.rolling.helper;
/*    */ 
/*    */ import ch.qos.logback.core.pattern.DynamicConverter;
/*    */ import ch.qos.logback.core.pattern.FormatInfo;
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
/*    */ public class IntegerTokenConverter
/*    */   extends DynamicConverter<Object>
/*    */   implements MonoTypedConverter
/*    */ {
/*    */   public static final String CONVERTER_KEY = "i";
/*    */   
/*    */   public String convert(int i) {
/* 30 */     String s = Integer.toString(i);
/* 31 */     FormatInfo formattingInfo = getFormattingInfo();
/* 32 */     if (formattingInfo == null) {
/* 33 */       return s;
/*    */     }
/* 35 */     int min = formattingInfo.getMin();
/* 36 */     StringBuilder sbuf = new StringBuilder();
/* 37 */     for (int j = s.length(); j < min; j++) {
/* 38 */       sbuf.append('0');
/*    */     }
/* 40 */     return sbuf.append(s).toString();
/*    */   }
/*    */   
/*    */   public String convert(Object o) {
/* 44 */     if (o == null) {
/* 45 */       throw new IllegalArgumentException("Null argument forbidden");
/*    */     }
/* 47 */     if (o instanceof Integer) {
/* 48 */       Integer i = (Integer)o;
/* 49 */       return convert(i.intValue());
/*    */     } 
/* 51 */     throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
/*    */   }
/*    */   
/*    */   public boolean isApplicable(Object o) {
/* 55 */     return o instanceof Integer;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\IntegerTokenConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */