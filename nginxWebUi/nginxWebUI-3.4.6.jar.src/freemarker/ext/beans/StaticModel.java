/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ final class StaticModel
/*     */   implements TemplateHashModelEx
/*     */ {
/*  44 */   private static final Logger LOG = Logger.getLogger("freemarker.beans");
/*     */   private final Class<?> clazz;
/*     */   private final BeansWrapper wrapper;
/*  47 */   private final Map<String, Object> map = new HashMap<>();
/*     */   
/*     */   StaticModel(Class<?> clazz, BeansWrapper wrapper) throws TemplateModelException {
/*  50 */     this.clazz = clazz;
/*  51 */     this.wrapper = wrapper;
/*  52 */     populate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  61 */     Object model = this.map.get(key);
/*     */ 
/*     */     
/*  64 */     if (model instanceof TemplateModel) {
/*  65 */       return (TemplateModel)model;
/*     */     }
/*  67 */     if (model instanceof Field) {
/*     */       try {
/*  69 */         return this.wrapper.readField(null, (Field)model);
/*  70 */       } catch (IllegalAccessException e) {
/*  71 */         throw new TemplateModelException("Illegal access for field " + key + " of class " + this.clazz
/*  72 */             .getName());
/*     */       } 
/*     */     }
/*     */     
/*  76 */     throw new TemplateModelException("No such key: " + key + " in class " + this.clazz
/*  77 */         .getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  86 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  91 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() throws TemplateModelException {
/*  96 */     return (TemplateCollectionModel)this.wrapper.getOuterIdentity().wrap(this.map.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() throws TemplateModelException {
/* 101 */     return (TemplateCollectionModel)this.wrapper.getOuterIdentity().wrap(this.map.values());
/*     */   }
/*     */   
/*     */   private void populate() throws TemplateModelException {
/* 105 */     if (!Modifier.isPublic(this.clazz.getModifiers())) {
/* 106 */       throw new TemplateModelException("Can't wrap the non-public class " + this.clazz
/* 107 */           .getName());
/*     */     }
/*     */     
/* 110 */     if (this.wrapper.getExposureLevel() == 3) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 115 */     ClassMemberAccessPolicy effClassMemberAccessPolicy = this.wrapper.getClassIntrospector().getEffectiveMemberAccessPolicy().forClass(this.clazz);
/*     */     
/* 117 */     Field[] fields = this.clazz.getFields();
/* 118 */     for (Field field : fields) {
/* 119 */       int mod = field.getModifiers();
/* 120 */       if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && effClassMemberAccessPolicy.isFieldExposed(field)) {
/* 121 */         if (Modifier.isFinal(mod)) {
/*     */ 
/*     */           
/*     */           try {
/* 125 */             this.map.put(field.getName(), this.wrapper.readField(null, field));
/* 126 */           } catch (IllegalAccessException illegalAccessException) {}
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 133 */           this.map.put(field.getName(), field);
/*     */         } 
/*     */       }
/*     */     } 
/* 137 */     if (this.wrapper.getExposureLevel() < 2) {
/* 138 */       Method[] methods = this.clazz.getMethods();
/* 139 */       for (int i = 0; i < methods.length; i++) {
/* 140 */         Method method = methods[i];
/* 141 */         int mod = method.getModifiers();
/* 142 */         if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && effClassMemberAccessPolicy
/* 143 */           .isMethodExposed(method)) {
/* 144 */           String name = method.getName();
/* 145 */           Object obj = this.map.get(name);
/* 146 */           if (obj instanceof Method) {
/* 147 */             OverloadedMethods overloadedMethods = new OverloadedMethods(this.wrapper.is2321Bugfixed());
/* 148 */             overloadedMethods.addMethod((Method)obj);
/* 149 */             overloadedMethods.addMethod(method);
/* 150 */             this.map.put(name, overloadedMethods);
/* 151 */           } else if (obj instanceof OverloadedMethods) {
/* 152 */             OverloadedMethods overloadedMethods = (OverloadedMethods)obj;
/* 153 */             overloadedMethods.addMethod(method);
/*     */           } else {
/* 155 */             if (obj != null && 
/* 156 */               LOG.isInfoEnabled()) {
/* 157 */               LOG.info("Overwriting value [" + obj + "] for  key '" + name + "' with [" + method + "] in static model for " + this.clazz
/*     */                   
/* 159 */                   .getName());
/*     */             }
/*     */             
/* 162 */             this.map.put(name, method);
/*     */           } 
/*     */         } 
/*     */       } 
/* 166 */       for (Iterator<Map.Entry<String, Object>> entries = this.map.entrySet().iterator(); entries.hasNext(); ) {
/* 167 */         Map.Entry<String, Object> entry = entries.next();
/* 168 */         Object value = entry.getValue();
/* 169 */         if (value instanceof Method) {
/* 170 */           Method method = (Method)value;
/* 171 */           entry.setValue(new SimpleMethodModel(null, method, method
/* 172 */                 .getParameterTypes(), this.wrapper)); continue;
/* 173 */         }  if (value instanceof OverloadedMethods)
/* 174 */           entry.setValue(new OverloadedMethodsModel(null, (OverloadedMethods)value, this.wrapper)); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\StaticModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */