/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.util.NumberUtil;
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
/*    */ public interface Segment<T extends Number>
/*    */ {
/*    */   T getStartIndex();
/*    */   
/*    */   T getEndIndex();
/*    */   
/*    */   default T length() {
/* 37 */     Number number1 = Assert.<Number>notNull((Number)getStartIndex(), "Start index must be not null!", new Object[0]);
/* 38 */     Number number2 = Assert.<Number>notNull((Number)getEndIndex(), "End index must be not null!", new Object[0]);
/* 39 */     return (T)Convert.convert(number1.getClass(), NumberUtil.sub(number2, number1).abs());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Segment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */