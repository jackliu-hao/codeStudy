/*    */ package cn.hutool.extra.template.engine.freemarker;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import cn.hutool.extra.template.TemplateException;
/*    */ import freemarker.template.Template;
/*    */ import freemarker.template.TemplateException;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class FreemarkerTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -8157926902932567280L;
/*    */   Template rawTemplate;
/*    */   
/*    */   public static FreemarkerTemplate wrap(Template beetlTemplate) {
/* 32 */     return (null == beetlTemplate) ? null : new FreemarkerTemplate(beetlTemplate);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FreemarkerTemplate(Template freemarkerTemplate) {
/* 41 */     this.rawTemplate = freemarkerTemplate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/*    */     try {
/* 47 */       this.rawTemplate.process(bindingMap, writer);
/* 48 */     } catch (TemplateException e) {
/* 49 */       throw new TemplateException(e);
/* 50 */     } catch (IOException e) {
/* 51 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 57 */     render(bindingMap, IoUtil.getWriter(out, Charset.forName(this.rawTemplate.getEncoding())));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\freemarker\FreemarkerTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */