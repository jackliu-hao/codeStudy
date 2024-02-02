/*    */ package cn.hutool.extra.expression.engine.aviator;
/*    */ 
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import com.googlecode.aviator.AviatorEvaluator;
/*    */ import com.googlecode.aviator.AviatorEvaluatorInstance;
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
/*    */ public class AviatorEngine
/*    */   implements ExpressionEngine
/*    */ {
/* 24 */   private final AviatorEvaluatorInstance engine = AviatorEvaluator.getInstance();
/*    */ 
/*    */ 
/*    */   
/*    */   public Object eval(String expression, Map<String, Object> context) {
/* 29 */     return this.engine.execute(expression, context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AviatorEvaluatorInstance getEngine() {
/* 38 */     return this.engine;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\aviator\AviatorEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */