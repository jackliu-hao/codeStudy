/*    */ package cn.hutool.extra.template.engine.enjoy;
/*    */ 
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import com.jfinal.template.Template;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
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
/*    */ 
/*    */ 
/*    */ public class EnjoyTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Template rawTemplate;
/*    */   
/*    */   public static EnjoyTemplate wrap(Template template) {
/* 28 */     return (null == template) ? null : new EnjoyTemplate(template);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EnjoyTemplate(Template template) {
/* 37 */     this.rawTemplate = template;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 42 */     this.rawTemplate.render(bindingMap, writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 47 */     this.rawTemplate.render(bindingMap, out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\enjoy\EnjoyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */