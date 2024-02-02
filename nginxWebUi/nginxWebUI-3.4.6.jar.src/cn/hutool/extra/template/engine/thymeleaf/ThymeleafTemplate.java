/*    */ package cn.hutool.extra.template.engine.thymeleaf;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.lang.TypeReference;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
/*    */ import org.thymeleaf.TemplateEngine;
/*    */ import org.thymeleaf.context.Context;
/*    */ import org.thymeleaf.context.IContext;
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
/*    */ public class ThymeleafTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 781284916568562509L;
/*    */   private final TemplateEngine engine;
/*    */   private final String template;
/*    */   private final Charset charset;
/*    */   
/*    */   public static ThymeleafTemplate wrap(TemplateEngine engine, String template, Charset charset) {
/* 41 */     return (null == engine) ? null : new ThymeleafTemplate(engine, template, charset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ThymeleafTemplate(TemplateEngine engine, String template, Charset charset) {
/* 52 */     this.engine = engine;
/* 53 */     this.template = template;
/* 54 */     this.charset = (Charset)ObjectUtil.defaultIfNull(charset, CharsetUtil.CHARSET_UTF_8);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 59 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 60 */     Context context = new Context(Locale.getDefault(), map);
/* 61 */     this.engine.process(this.template, (IContext)context, writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 66 */     render(bindingMap, IoUtil.getWriter(out, this.charset));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\thymeleaf\ThymeleafTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */