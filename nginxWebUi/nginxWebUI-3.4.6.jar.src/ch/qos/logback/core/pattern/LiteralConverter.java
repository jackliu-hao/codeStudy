/*    */ package ch.qos.logback.core.pattern;
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
/*    */ public final class LiteralConverter<E>
/*    */   extends Converter<E>
/*    */ {
/*    */   String literal;
/*    */   
/*    */   public LiteralConverter(String literal) {
/* 21 */     this.literal = literal;
/*    */   }
/*    */   
/*    */   public String convert(E o) {
/* 25 */     return this.literal;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\LiteralConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */