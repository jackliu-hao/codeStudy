/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MapModel
/*     */   extends StringModel
/*     */   implements TemplateMethodModelEx
/*     */ {
/*  50 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  55 */         return (TemplateModel)new MapModel((Map)object, (BeansWrapper)wrapper);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapModel(Map map, BeansWrapper wrapper) {
/*  68 */     super(map, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object exec(List<TemplateModel> arguments) throws TemplateModelException {
/*  77 */     Object key = unwrap(arguments.get(0));
/*  78 */     return wrap(((Map)this.object).get(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
/*  88 */     Map map = (Map)this.object;
/*  89 */     Object val = map.get(key);
/*  90 */     if (val == null) {
/*  91 */       if (key.length() == 1) {
/*     */         
/*  93 */         Character charKey = Character.valueOf(key.charAt(0));
/*  94 */         val = map.get(charKey);
/*  95 */         if (val == null && !map.containsKey(key) && !map.containsKey(charKey)) {
/*  96 */           return UNKNOWN;
/*     */         }
/*  98 */       } else if (!map.containsKey(key)) {
/*  99 */         return UNKNOWN;
/*     */       } 
/*     */     }
/* 102 */     return wrap(val);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 107 */     return (((Map)this.object).isEmpty() && super.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 112 */     return keySet().size();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Set keySet() {
/* 117 */     Set set = super.keySet();
/* 118 */     set.addAll(((Map)this.object).keySet());
/* 119 */     return set;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\MapModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */