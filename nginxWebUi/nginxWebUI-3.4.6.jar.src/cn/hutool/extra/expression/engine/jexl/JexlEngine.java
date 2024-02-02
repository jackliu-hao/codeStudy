/*    */ package cn.hutool.extra.expression.engine.jexl;
/*    */ 
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.jexl3.JexlBuilder;
/*    */ import org.apache.commons.jexl3.JexlContext;
/*    */ import org.apache.commons.jexl3.MapContext;
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
/*    */ public class JexlEngine
/*    */   implements ExpressionEngine
/*    */ {
/* 21 */   private final org.apache.commons.jexl3.JexlEngine engine = (new JexlBuilder()).cache(512).strict(true).silent(false).create();
/*    */ 
/*    */ 
/*    */   
/*    */   public Object eval(String expression, Map<String, Object> context) {
/* 26 */     MapContext mapContext = new MapContext(context);
/*    */     
/*    */     try {
/* 29 */       return this.engine.createExpression(expression).evaluate((JexlContext)mapContext);
/* 30 */     } catch (Exception ignore) {
/*    */ 
/*    */       
/* 33 */       return this.engine.createScript(expression).execute((JexlContext)mapContext);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public org.apache.commons.jexl3.JexlEngine getEngine() {
/* 43 */     return this.engine;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\jexl\JexlEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */