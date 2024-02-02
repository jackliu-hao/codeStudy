/*    */ package cn.hutool.extra.template.engine.beetl;
/*    */ 
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import org.beetl.core.Template;
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
/*    */ public class BeetlTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -8157926902932567280L;
/*    */   private final Template rawTemplate;
/*    */   
/*    */   public static BeetlTemplate wrap(Template beetlTemplate) {
/* 27 */     return (null == beetlTemplate) ? null : new BeetlTemplate(beetlTemplate);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeetlTemplate(Template beetlTemplate) {
/* 36 */     this.rawTemplate = beetlTemplate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 41 */     this.rawTemplate.binding(bindingMap);
/* 42 */     this.rawTemplate.renderTo(writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 47 */     this.rawTemplate.binding(bindingMap);
/* 48 */     this.rawTemplate.renderTo(out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\beetl\BeetlTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */