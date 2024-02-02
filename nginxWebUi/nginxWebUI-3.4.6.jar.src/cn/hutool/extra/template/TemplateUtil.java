/*    */ package cn.hutool.extra.template;
/*    */ 
/*    */ import cn.hutool.extra.template.engine.TemplateFactory;
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
/*    */ public class TemplateUtil
/*    */ {
/*    */   public static TemplateEngine createEngine() {
/* 21 */     return TemplateFactory.create();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static TemplateEngine createEngine(TemplateConfig config) {
/* 32 */     return TemplateFactory.create(config);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\TemplateUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */