/*    */ package org.noear.solon.view.freemarker;
/*    */ 
/*    */ import freemarker.template.TemplateDirectiveModel;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.handle.RenderManager;
/*    */ import org.noear.solon.view.freemarker.tags.AuthPermissionsTag;
/*    */ import org.noear.solon.view.freemarker.tags.AuthRolesTag;
/*    */ 
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public static boolean output_meta = false;
/*    */   
/*    */   public void start(AopContext context) {
/* 20 */     output_meta = (Solon.cfg().getInt("solon.output.meta", 0) > 0);
/*    */     
/* 22 */     FreemarkerRender render = FreemarkerRender.global();
/*    */     
/* 24 */     context.beanOnloaded(ctx -> ctx.beanForeach(()));
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
/* 42 */     RenderManager.register(render);
/* 43 */     RenderManager.mapping(".ftl", render);
/*    */     
/* 45 */     if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
/* 46 */       render.putDirective("authPermissions", new AuthPermissionsTag());
/* 47 */       render.putDirective("authRoles", new AuthRolesTag());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\view\freemarker\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */