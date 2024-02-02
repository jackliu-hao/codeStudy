/*    */ package cn.hutool.extra.expression;
/*    */ 
/*    */ import cn.hutool.extra.expression.engine.ExpressionFactory;
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
/*    */ public class ExpressionUtil
/*    */ {
/*    */   public static ExpressionEngine getEngine() {
/* 21 */     return ExpressionFactory.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Object eval(String expression, Map<String, Object> context) {
/* 32 */     return getEngine().eval(expression, context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\ExpressionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */