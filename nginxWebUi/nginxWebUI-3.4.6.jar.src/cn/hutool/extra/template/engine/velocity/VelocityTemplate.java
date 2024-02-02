/*    */ package cn.hutool.extra.template.engine.velocity;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.lang.TypeReference;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import org.apache.velocity.Template;
/*    */ import org.apache.velocity.VelocityContext;
/*    */ import org.apache.velocity.app.Velocity;
/*    */ import org.apache.velocity.context.Context;
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
/*    */ public class VelocityTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -132774960373894911L;
/*    */   private final Template rawTemplate;
/*    */   private String charset;
/*    */   
/*    */   public static VelocityTemplate wrap(Template template) {
/* 36 */     return (null == template) ? null : new VelocityTemplate(template);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public VelocityTemplate(Template rawTemplate) {
/* 45 */     this.rawTemplate = rawTemplate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 50 */     this.rawTemplate.merge((Context)toContext(bindingMap), writer);
/* 51 */     IoUtil.flush(writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 56 */     if (null == this.charset) {
/* 57 */       loadEncoding();
/*    */     }
/* 59 */     render(bindingMap, IoUtil.getWriter(out, CharsetUtil.charset(this.charset)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private VelocityContext toContext(Map<?, ?> bindingMap) {
/* 69 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 70 */     return new VelocityContext(map);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void loadEncoding() {
/* 77 */     String charset = (String)Velocity.getProperty("resource.default_encoding");
/* 78 */     this.charset = StrUtil.isEmpty(charset) ? "UTF-8" : charset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\velocity\VelocityTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */