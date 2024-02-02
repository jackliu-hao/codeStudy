/*    */ package org.noear.solon.serialization.snack3;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.Bridge;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.handle.ActionExecutor;
/*    */ import org.noear.solon.core.handle.RenderManager;
/*    */ 
/*    */ public class XPluginImp implements Plugin {
/*    */   public static boolean output_meta = false;
/*    */   
/*    */   public void start(AopContext context) {
/* 15 */     output_meta = (Solon.cfg().getInt("solon.output.meta", 0) > 0);
/*    */ 
/*    */     
/* 18 */     EventBus.push(SnackRenderFactory.global);
/*    */     
/* 20 */     RenderManager.mapping("@json", SnackRenderFactory.global.create());
/* 21 */     RenderManager.mapping("@type_json", SnackRenderTypedFactory.global.create());
/*    */ 
/*    */     
/* 24 */     Bridge.actionExecutorAdd((ActionExecutor)new SnackJsonActionExecutor());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\snack3\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */