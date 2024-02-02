/*    */ package cn.hutool.extra.expression.engine.spel;
/*    */ 
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import java.util.Map;
/*    */ import org.springframework.expression.EvaluationContext;
/*    */ import org.springframework.expression.ExpressionParser;
/*    */ import org.springframework.expression.spel.standard.SpelExpressionParser;
/*    */ import org.springframework.expression.spel.support.StandardEvaluationContext;
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
/*    */ public class SpELEngine
/*    */   implements ExpressionEngine
/*    */ {
/* 26 */   private final ExpressionParser parser = (ExpressionParser)new SpelExpressionParser();
/*    */ 
/*    */ 
/*    */   
/*    */   public Object eval(String expression, Map<String, Object> context) {
/* 31 */     StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
/* 32 */     context.forEach(standardEvaluationContext::setVariable);
/* 33 */     return this.parser.parseExpression(expression).getValue((EvaluationContext)standardEvaluationContext);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\spel\SpELEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */