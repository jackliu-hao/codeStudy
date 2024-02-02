/*    */ package cn.hutool.extra.template.engine.rythm;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.lang.TypeReference;
/*    */ import cn.hutool.extra.template.AbstractTemplate;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import org.rythmengine.template.ITemplate;
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
/*    */ public class RythmTemplate
/*    */   extends AbstractTemplate
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -132774960373894911L;
/*    */   private final ITemplate rawTemplate;
/*    */   
/*    */   public static RythmTemplate wrap(ITemplate template) {
/* 30 */     return (null == template) ? null : new RythmTemplate(template);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RythmTemplate(ITemplate rawTemplate) {
/* 39 */     this.rawTemplate = rawTemplate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, Writer writer) {
/* 44 */     Map<String, Object> map = (Map<String, Object>)Convert.convert(new TypeReference<Map<String, Object>>() {  }, bindingMap);
/* 45 */     this.rawTemplate.__setRenderArgs(map);
/* 46 */     this.rawTemplate.render(writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(Map<?, ?> bindingMap, OutputStream out) {
/* 51 */     this.rawTemplate.__setRenderArgs(new Object[] { bindingMap });
/* 52 */     this.rawTemplate.render(out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\rythm\RythmTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */