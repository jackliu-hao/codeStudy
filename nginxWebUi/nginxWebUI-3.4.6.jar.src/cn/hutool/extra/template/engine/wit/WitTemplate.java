/*    */ package cn.hutool.extra.template.engine.wit;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.lang.TypeReference;
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import org.febit.wit.Template;
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
/*    */ public class WitTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Template rawTemplate;
/*    */   
/*    */   public static WitTemplate wrap(Template witTemplate) {
/* 30 */     return (null == witTemplate) ? null : new WitTemplate(witTemplate);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WitTemplate(Template witTemplate) {
/* 39 */     this.rawTemplate = witTemplate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 44 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 45 */     this.rawTemplate.merge(map, writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 50 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 51 */     this.rawTemplate.merge(map, out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\wit\WitTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */