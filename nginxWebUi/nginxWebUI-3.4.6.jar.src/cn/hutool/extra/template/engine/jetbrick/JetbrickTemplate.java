/*    */ package cn.hutool.extra.template.engine.jetbrick;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.lang.TypeReference;
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import jetbrick.template.JetTemplate;
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
/*    */ public class JetbrickTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final JetTemplate rawTemplate;
/*    */   
/*    */   public static JetbrickTemplate wrap(JetTemplate jetTemplate) {
/* 32 */     return (null == jetTemplate) ? null : new JetbrickTemplate(jetTemplate);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JetbrickTemplate(JetTemplate jetTemplate) {
/* 41 */     this.rawTemplate = jetTemplate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 46 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 47 */     this.rawTemplate.render(map, writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 52 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 53 */     this.rawTemplate.render(map, out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\jetbrick\JetbrickTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */