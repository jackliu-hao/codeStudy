/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.util.LinkedHashMap;
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
/*    */ public class _EnumModels
/*    */   extends ClassBasedModelFactory
/*    */ {
/*    */   public _EnumModels(BeansWrapper wrapper) {
/* 34 */     super(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   protected TemplateModel createModel(Class<Object> clazz) {
/* 39 */     Object[] obj = clazz.getEnumConstants();
/* 40 */     if (obj == null)
/*    */     {
/*    */ 
/*    */ 
/*    */       
/* 45 */       return null;
/*    */     }
/* 47 */     Map<Object, Object> map = new LinkedHashMap<>();
/* 48 */     for (int i = 0; i < obj.length; i++) {
/* 49 */       Enum value = (Enum)obj[i];
/* 50 */       map.put(value.name(), value);
/*    */     } 
/* 52 */     return (TemplateModel)new SimpleMapModel(map, getWrapper());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\_EnumModels.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */