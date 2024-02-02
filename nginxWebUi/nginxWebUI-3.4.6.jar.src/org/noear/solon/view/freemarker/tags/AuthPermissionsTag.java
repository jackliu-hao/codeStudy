/*    */ package org.noear.solon.view.freemarker.tags;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.template.TemplateDirectiveBody;
/*    */ import freemarker.template.TemplateDirectiveModel;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.auth.AuthUtil;
/*    */ import org.noear.solon.auth.annotation.Logical;
/*    */ import org.noear.solon.core.NvMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthPermissionsTag
/*    */   implements TemplateDirectiveModel
/*    */ {
/*    */   public void execute(Environment env, Map map, TemplateModel[] templateModels, TemplateDirectiveBody body) throws TemplateException, IOException {
/* 24 */     NvMap mapExt = new NvMap(map);
/*    */     
/* 26 */     String nameStr = (String)mapExt.get("name");
/* 27 */     String logicalStr = (String)mapExt.get("logical");
/*    */     
/* 29 */     if (Utils.isEmpty(nameStr)) {
/*    */       return;
/*    */     }
/*    */     
/* 33 */     String[] names = nameStr.split(",");
/*    */     
/* 35 */     if (names.length == 0) {
/*    */       return;
/*    */     }
/*    */     
/* 39 */     if (AuthUtil.verifyPermissions(names, Logical.of(logicalStr)))
/* 40 */       body.render(env.getOut()); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\view\freemarker\tags\AuthPermissionsTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */