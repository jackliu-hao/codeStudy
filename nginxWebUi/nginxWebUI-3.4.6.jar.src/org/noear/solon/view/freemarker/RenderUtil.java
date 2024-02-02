/*    */ package org.noear.solon.view.freemarker;
/*    */ 
/*    */ import freemarker.cache.StringTemplateLoader;
/*    */ import freemarker.cache.TemplateLoader;
/*    */ import freemarker.template.Configuration;
/*    */ import freemarker.template.Template;
/*    */ import java.io.StringWriter;
/*    */ import org.noear.solon.Solon;
/*    */ 
/*    */ public class RenderUtil {
/*    */   public static String render(String template, Object model) throws Exception {
/* 12 */     StringWriter writer = new StringWriter();
/* 13 */     Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
/* 14 */     cfg.setNumberFormat("#");
/* 15 */     cfg.setDefaultEncoding("utf-8");
/*    */     
/* 17 */     StringTemplateLoader stringLoader = new StringTemplateLoader();
/* 18 */     stringLoader.putTemplate("template", template);
/* 19 */     cfg.setTemplateLoader((TemplateLoader)stringLoader);
/*    */     
/* 21 */     Template tmpl = cfg.getTemplate("template", Solon.encoding());
/*    */     
/* 23 */     tmpl.process(model, writer);
/* 24 */     return writer.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\view\freemarker\RenderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */