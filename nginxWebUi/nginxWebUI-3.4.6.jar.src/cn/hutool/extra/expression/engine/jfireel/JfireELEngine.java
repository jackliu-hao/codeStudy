/*    */ package cn.hutool.extra.expression.engine.jfireel;
/*    */ 
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import com.jfirer.jfireel.expression.Expression;
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
/*    */ 
/*    */ public class JfireELEngine
/*    */   implements ExpressionEngine
/*    */ {
/*    */   public Object eval(String expression, Map<String, Object> context) {
/* 25 */     return Expression.parse(expression).calculate(context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\jfireel\JfireELEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */