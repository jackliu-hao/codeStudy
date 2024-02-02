/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core.CollectionAndSequence;
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.MapKeyValuePairIterator;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelWithAPISupport;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.WrappingTemplateModel;
/*     */ import freemarker.template.utility.RichObjectWrapper;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class SimpleMapModel
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateHashModelEx2, TemplateMethodModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport
/*     */ {
/*  50 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  55 */         return (TemplateModel)new SimpleMapModel((Map)object, (BeansWrapper)wrapper);
/*     */       }
/*     */     };
/*     */   
/*     */   private final Map map;
/*     */   
/*     */   public SimpleMapModel(Map map, BeansWrapper wrapper) {
/*  62 */     super((ObjectWrapper)wrapper);
/*  63 */     this.map = map;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  68 */     Object val = this.map.get(key);
/*  69 */     if (val == null) {
/*  70 */       if (key.length() == 1) {
/*     */         
/*  72 */         Character charKey = Character.valueOf(key.charAt(0));
/*  73 */         val = this.map.get(charKey);
/*  74 */         if (val == null && !this.map.containsKey(key) && !this.map.containsKey(charKey)) {
/*  75 */           return null;
/*     */         }
/*  77 */       } else if (!this.map.containsKey(key)) {
/*  78 */         return null;
/*     */       } 
/*     */     }
/*  81 */     return wrap(val);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*  86 */     Object key = ((BeansWrapper)getObjectWrapper()).unwrap(args.get(0));
/*  87 */     Object value = this.map.get(key);
/*  88 */     if (value == null && !this.map.containsKey(key)) {
/*  89 */       return null;
/*     */     }
/*  91 */     return wrap(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  96 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 101 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() {
/* 106 */     return (TemplateCollectionModel)new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(this.map.keySet(), getObjectWrapper()));
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() {
/* 111 */     return (TemplateCollectionModel)new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(this.map.values(), getObjectWrapper()));
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
/* 116 */     return (TemplateHashModelEx2.KeyValuePairIterator)new MapKeyValuePairIterator(this.map, getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/* 121 */     return this.map;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/* 126 */     return this.map;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/* 131 */     return (TemplateModel)((RichObjectWrapper)getObjectWrapper()).wrapAsAPI(this.map);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\SimpleMapModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */