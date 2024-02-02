/*    */ package cn.hutool.extra.expression.engine.rhino;
/*    */ 
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import java.util.Map;
/*    */ import org.mozilla.javascript.Context;
/*    */ import org.mozilla.javascript.Scriptable;
/*    */ import org.mozilla.javascript.ScriptableObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RhinoEngine
/*    */   implements ExpressionEngine
/*    */ {
/*    */   public Object eval(String expression, Map<String, Object> context) {
/* 22 */     Context ctx = Context.enter();
/* 23 */     ScriptableObject scriptableObject = ctx.initStandardObjects();
/* 24 */     if (MapUtil.isNotEmpty(context)) {
/* 25 */       context.forEach((key, value) -> ScriptableObject.putProperty(scope, key, Context.javaToJS(value, scope)));
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 30 */     Object result = ctx.evaluateString((Scriptable)scriptableObject, expression, "rhino.js", 1, null);
/* 31 */     Context.exit();
/* 32 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\rhino\RhinoEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */