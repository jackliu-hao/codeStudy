/*    */ package cn.hutool.extra.expression.engine.mvel;
/*    */ 
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import java.util.Map;
/*    */ import org.mvel2.MVEL;
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
/*    */ public class MvelEngine
/*    */   implements ExpressionEngine
/*    */ {
/*    */   public Object eval(String expression, Map<String, Object> context) {
/* 25 */     return MVEL.eval(expression, context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\mvel\MvelEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */