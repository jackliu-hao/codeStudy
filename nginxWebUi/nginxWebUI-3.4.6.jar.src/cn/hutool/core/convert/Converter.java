/*    */ package cn.hutool.core.convert;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Converter<T>
/*    */ {
/*    */   T convert(Object paramObject, T paramT) throws IllegalArgumentException;
/*    */   
/*    */   default T convertWithCheck(Object value, T defaultValue, boolean quietly) {
/*    */     try {
/* 35 */       return convert(value, defaultValue);
/* 36 */     } catch (Exception e) {
/* 37 */       if (quietly) {
/* 38 */         return defaultValue;
/*    */       }
/* 40 */       throw e;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */