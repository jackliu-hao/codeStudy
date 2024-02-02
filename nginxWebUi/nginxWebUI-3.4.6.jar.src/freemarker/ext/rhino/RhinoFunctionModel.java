/*    */ package freemarker.ext.rhino;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.template.TemplateMethodModelEx;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.util.List;
/*    */ import org.mozilla.javascript.Context;
/*    */ import org.mozilla.javascript.Function;
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
/*    */ public class RhinoFunctionModel
/*    */   extends RhinoScriptableModel
/*    */   implements TemplateMethodModelEx
/*    */ {
/*    */   private final Scriptable fnThis;
/*    */   
/*    */   public RhinoFunctionModel(Function function, Scriptable fnThis, BeansWrapper wrapper) {
/* 43 */     super((Scriptable)function, wrapper);
/* 44 */     this.fnThis = fnThis;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object exec(List arguments) throws TemplateModelException {
/* 49 */     Context cx = Context.getCurrentContext();
/* 50 */     Object[] args = arguments.toArray();
/* 51 */     BeansWrapper wrapper = getWrapper();
/* 52 */     for (int i = 0; i < args.length; i++) {
/* 53 */       args[i] = wrapper.unwrap((TemplateModel)args[i]);
/*    */     }
/* 55 */     return wrapper.wrap(((Function)getScriptable()).call(cx, 
/* 56 */           ScriptableObject.getTopLevelScope(this.fnThis), this.fnThis, args));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\rhino\RhinoFunctionModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */