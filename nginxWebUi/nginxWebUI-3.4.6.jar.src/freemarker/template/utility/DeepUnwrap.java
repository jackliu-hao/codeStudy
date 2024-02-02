/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public class DeepUnwrap
/*     */ {
/*  48 */   private static final Class OBJECT_CLASS = Object.class;
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
/*     */   public static Object unwrap(TemplateModel model) throws TemplateModelException {
/*  79 */     return unwrap(model, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object permissiveUnwrap(TemplateModel model) throws TemplateModelException {
/*  88 */     return unwrap(model, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Object premissiveUnwrap(TemplateModel model) throws TemplateModelException {
/*  97 */     return unwrap(model, true);
/*     */   }
/*     */   
/*     */   private static Object unwrap(TemplateModel model, boolean permissive) throws TemplateModelException {
/* 101 */     Environment env = Environment.getCurrentEnvironment();
/* 102 */     TemplateModel nullModel = null;
/* 103 */     if (env != null) {
/* 104 */       ObjectWrapper wrapper = env.getObjectWrapper();
/* 105 */       if (wrapper != null) {
/* 106 */         nullModel = wrapper.wrap(null);
/*     */       }
/*     */     } 
/* 109 */     return unwrap(model, nullModel, permissive);
/*     */   }
/*     */   
/*     */   private static Object unwrap(TemplateModel model, TemplateModel nullModel, boolean permissive) throws TemplateModelException {
/* 113 */     if (model instanceof AdapterTemplateModel) {
/* 114 */       return ((AdapterTemplateModel)model).getAdaptedObject(OBJECT_CLASS);
/*     */     }
/* 116 */     if (model instanceof WrapperTemplateModel) {
/* 117 */       return ((WrapperTemplateModel)model).getWrappedObject();
/*     */     }
/* 119 */     if (model == nullModel) {
/* 120 */       return null;
/*     */     }
/* 122 */     if (model instanceof TemplateScalarModel) {
/* 123 */       return ((TemplateScalarModel)model).getAsString();
/*     */     }
/* 125 */     if (model instanceof TemplateNumberModel) {
/* 126 */       return ((TemplateNumberModel)model).getAsNumber();
/*     */     }
/* 128 */     if (model instanceof TemplateDateModel) {
/* 129 */       return ((TemplateDateModel)model).getAsDate();
/*     */     }
/* 131 */     if (model instanceof TemplateBooleanModel) {
/* 132 */       return Boolean.valueOf(((TemplateBooleanModel)model).getAsBoolean());
/*     */     }
/* 134 */     if (model instanceof TemplateSequenceModel) {
/* 135 */       TemplateSequenceModel seq = (TemplateSequenceModel)model;
/* 136 */       int size = seq.size();
/* 137 */       ArrayList<Object> list = new ArrayList(size);
/* 138 */       for (int i = 0; i < size; i++) {
/* 139 */         list.add(unwrap(seq.get(i), nullModel, permissive));
/*     */       }
/* 141 */       return list;
/*     */     } 
/* 143 */     if (model instanceof TemplateCollectionModel) {
/* 144 */       TemplateCollectionModel coll = (TemplateCollectionModel)model;
/* 145 */       ArrayList<Object> list = new ArrayList();
/* 146 */       TemplateModelIterator it = coll.iterator();
/* 147 */       while (it.hasNext()) {
/* 148 */         list.add(unwrap(it.next(), nullModel, permissive));
/*     */       }
/* 150 */       return list;
/*     */     } 
/* 152 */     if (model instanceof TemplateHashModelEx) {
/* 153 */       TemplateHashModelEx hash = (TemplateHashModelEx)model;
/* 154 */       Map<Object, Object> map = new LinkedHashMap<>();
/* 155 */       if (model instanceof TemplateHashModelEx2) {
/* 156 */         TemplateHashModelEx2.KeyValuePairIterator kvps = ((TemplateHashModelEx2)model).keyValuePairIterator();
/* 157 */         while (kvps.hasNext()) {
/* 158 */           TemplateHashModelEx2.KeyValuePair kvp = kvps.next();
/* 159 */           map.put(
/* 160 */               unwrap(kvp.getKey(), nullModel, permissive), 
/* 161 */               unwrap(kvp.getValue(), nullModel, permissive));
/*     */         } 
/*     */       } else {
/* 164 */         TemplateModelIterator keys = hash.keys().iterator();
/* 165 */         while (keys.hasNext()) {
/* 166 */           String key = (String)unwrap(keys.next(), nullModel, permissive);
/* 167 */           map.put(key, unwrap(hash.get(key), nullModel, permissive));
/*     */         } 
/*     */       } 
/* 170 */       return map;
/*     */     } 
/* 172 */     if (permissive) {
/* 173 */       return model;
/*     */     }
/* 175 */     throw new TemplateModelException("Cannot deep-unwrap model of type " + model.getClass().getName());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\DeepUnwrap.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */