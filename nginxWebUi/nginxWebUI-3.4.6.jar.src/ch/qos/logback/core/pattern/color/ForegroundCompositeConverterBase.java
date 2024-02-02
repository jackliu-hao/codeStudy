/*    */ package ch.qos.logback.core.pattern.color;
/*    */ 
/*    */ import ch.qos.logback.core.pattern.CompositeConverter;
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
/*    */ public abstract class ForegroundCompositeConverterBase<E>
/*    */   extends CompositeConverter<E>
/*    */ {
/*    */   private static final String SET_DEFAULT_COLOR = "\033[0;39m";
/*    */   
/*    */   protected String transform(E event, String in) {
/* 31 */     StringBuilder sb = new StringBuilder();
/* 32 */     sb.append("\033[");
/* 33 */     sb.append(getForegroundColorCode(event));
/* 34 */     sb.append("m");
/* 35 */     sb.append(in);
/* 36 */     sb.append("\033[0;39m");
/* 37 */     return sb.toString();
/*    */   }
/*    */   
/*    */   protected abstract String getForegroundColorCode(E paramE);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\color\ForegroundCompositeConverterBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */