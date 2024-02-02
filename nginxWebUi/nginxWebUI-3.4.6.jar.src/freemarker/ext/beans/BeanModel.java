/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core.CollectionAndSequence;
/*     */ import freemarker.core._DelayedFTLTypeDescription;
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateModelWithAPISupport;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ public class BeanModel
/*     */   implements TemplateHashModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport
/*     */ {
/*  65 */   private static final Logger LOG = Logger.getLogger("freemarker.beans");
/*     */   
/*     */   protected final Object object;
/*     */   
/*     */   protected final BeansWrapper wrapper;
/*  70 */   static final TemplateModel UNKNOWN = (TemplateModel)new SimpleScalar("UNKNOWN");
/*     */   
/*  72 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  77 */         return (TemplateModel)new BeanModel(object, (BeansWrapper)wrapper);
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
/*     */   
/*     */   private HashMap<Object, TemplateModel> memberCache;
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
/*     */   public BeanModel(Object object, BeansWrapper wrapper) {
/* 102 */     this(object, wrapper, true);
/*     */   }
/*     */ 
/*     */   
/*     */   BeanModel(Object object, BeansWrapper wrapper, boolean inrospectNow) {
/* 107 */     this.object = object;
/* 108 */     this.wrapper = wrapper;
/* 109 */     if (inrospectNow && object != null)
/*     */     {
/* 111 */       wrapper.getClassIntrospector().get(object.getClass());
/*     */     }
/*     */   }
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
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/* 144 */     Class<?> clazz = this.object.getClass();
/* 145 */     Map<Object, Object> classInfo = this.wrapper.getClassIntrospector().get(clazz);
/* 146 */     TemplateModel retval = null;
/*     */     
/*     */     try {
/* 149 */       if (this.wrapper.isMethodsShadowItems()) {
/* 150 */         Object fd = classInfo.get(key);
/* 151 */         if (fd != null) {
/* 152 */           retval = invokeThroughDescriptor(fd, classInfo);
/*     */         } else {
/* 154 */           retval = invokeGenericGet(classInfo, clazz, key);
/*     */         } 
/*     */       } else {
/* 157 */         TemplateModel model = invokeGenericGet(classInfo, clazz, key);
/* 158 */         TemplateModel nullModel = this.wrapper.wrap(null);
/* 159 */         if (model != nullModel && model != UNKNOWN) {
/* 160 */           return model;
/*     */         }
/* 162 */         Object fd = classInfo.get(key);
/* 163 */         if (fd != null) {
/* 164 */           retval = invokeThroughDescriptor(fd, classInfo);
/* 165 */           if (retval == UNKNOWN && model == nullModel)
/*     */           {
/*     */ 
/*     */             
/* 169 */             retval = nullModel;
/*     */           }
/*     */         } 
/*     */       } 
/* 173 */       if (retval == UNKNOWN) {
/* 174 */         if (this.wrapper.isStrict())
/* 175 */           throw new InvalidPropertyException("No such bean property: " + key); 
/* 176 */         if (LOG.isDebugEnabled()) {
/* 177 */           logNoSuchKey(key, classInfo);
/*     */         }
/* 179 */         retval = this.wrapper.wrap(null);
/*     */       } 
/* 181 */       return retval;
/* 182 */     } catch (TemplateModelException e) {
/* 183 */       throw e;
/* 184 */     } catch (Exception e) {
/* 185 */       throw new _TemplateModelException(e, new Object[] { "An error has occurred when reading existing sub-variable ", new _DelayedJQuote(key), "; see cause exception! The type of the containing value was: ", new _DelayedFTLTypeDescription(this) });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void logNoSuchKey(String key, Map<?, ?> keyMap) {
/* 194 */     LOG.debug("Key " + StringUtil.jQuoteNoXSS(key) + " was not found on instance of " + this.object
/* 195 */         .getClass().getName() + ". Introspection information for the class is: " + keyMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean hasPlainGetMethod() {
/* 204 */     return (this.wrapper.getClassIntrospector().get(this.object.getClass()).get(ClassIntrospector.GENERIC_GET_KEY) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   private TemplateModel invokeThroughDescriptor(Object desc, Map<Object, Object> classInfo) throws IllegalAccessException, InvocationTargetException, TemplateModelException {
/*     */     TemplateModel cachedModel;
/*     */     OverloadedMethodsModel overloadedMethodsModel1, overloadedMethodsModel2;
/* 211 */     synchronized (this) {
/* 212 */       cachedModel = (this.memberCache != null) ? this.memberCache.get(desc) : null;
/*     */     } 
/*     */     
/* 215 */     if (cachedModel != null) {
/* 216 */       return cachedModel;
/*     */     }
/*     */     
/* 219 */     TemplateModel resultModel = UNKNOWN;
/* 220 */     if (desc instanceof FastPropertyDescriptor) {
/* 221 */       FastPropertyDescriptor pd = (FastPropertyDescriptor)desc;
/* 222 */       Method indexedReadMethod = pd.getIndexedReadMethod();
/* 223 */       if (indexedReadMethod != null) {
/* 224 */         if (!this.wrapper.getPreferIndexedReadMethod() && pd.getReadMethod() != null) {
/* 225 */           resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null);
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 230 */           SimpleMethodModel simpleMethodModel1 = new SimpleMethodModel(this.object, indexedReadMethod, ClassIntrospector.getArgTypes(classInfo, indexedReadMethod), this.wrapper), simpleMethodModel2 = simpleMethodModel1;
/*     */         } 
/*     */       } else {
/* 233 */         resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), null);
/*     */       }
/*     */     
/* 236 */     } else if (desc instanceof Field) {
/* 237 */       resultModel = this.wrapper.readField(this.object, (Field)desc);
/*     */     }
/* 239 */     else if (desc instanceof Method) {
/* 240 */       Method method = (Method)desc;
/*     */       
/* 242 */       SimpleMethodModel simpleMethodModel1 = new SimpleMethodModel(this.object, method, ClassIntrospector.getArgTypes(classInfo, method), this.wrapper), simpleMethodModel2 = simpleMethodModel1;
/* 243 */     } else if (desc instanceof OverloadedMethods) {
/* 244 */       overloadedMethodsModel2 = overloadedMethodsModel1 = new OverloadedMethodsModel(this.object, (OverloadedMethods)desc, this.wrapper);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 249 */     if (overloadedMethodsModel1 != null) {
/* 250 */       synchronized (this) {
/* 251 */         if (this.memberCache == null) {
/* 252 */           this.memberCache = new HashMap<>();
/*     */         }
/* 254 */         this.memberCache.put(desc, overloadedMethodsModel1);
/*     */       } 
/*     */     }
/* 257 */     return (TemplateModel)overloadedMethodsModel2;
/*     */   }
/*     */   
/*     */   void clearMemberCache() {
/* 261 */     synchronized (this) {
/* 262 */       this.memberCache = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected TemplateModel invokeGenericGet(Map classInfo, Class<?> clazz, String key) throws IllegalAccessException, InvocationTargetException, TemplateModelException {
/* 269 */     Method genericGet = (Method)classInfo.get(ClassIntrospector.GENERIC_GET_KEY);
/* 270 */     if (genericGet == null) {
/* 271 */       return UNKNOWN;
/*     */     }
/*     */     
/* 274 */     return this.wrapper.invokeMethod(this.object, genericGet, new Object[] { key });
/*     */   }
/*     */ 
/*     */   
/*     */   protected TemplateModel wrap(Object obj) throws TemplateModelException {
/* 279 */     return this.wrapper.getOuterIdentity().wrap(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object unwrap(TemplateModel model) throws TemplateModelException {
/* 284 */     return this.wrapper.unwrap(model);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 293 */     if (this.object instanceof String) {
/* 294 */       return (((String)this.object).length() == 0);
/*     */     }
/* 296 */     if (this.object instanceof Collection) {
/* 297 */       return ((Collection)this.object).isEmpty();
/*     */     }
/* 299 */     if (this.object instanceof Iterator && this.wrapper.is2324Bugfixed()) {
/* 300 */       return !((Iterator)this.object).hasNext();
/*     */     }
/* 302 */     if (this.object instanceof Map) {
/* 303 */       return ((Map)this.object).isEmpty();
/*     */     }
/* 305 */     return (this.object == null || Boolean.FALSE.equals(this.object));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class<?> hint) {
/* 314 */     return this.object;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/* 319 */     return this.object;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 324 */     return this.wrapper.getClassIntrospector().keyCount(this.object.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() {
/* 329 */     return (TemplateCollectionModel)new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(keySet(), (ObjectWrapper)this.wrapper));
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() throws TemplateModelException {
/* 334 */     List<Object> values = new ArrayList(size());
/* 335 */     TemplateModelIterator it = keys().iterator();
/* 336 */     while (it.hasNext()) {
/* 337 */       String key = ((TemplateScalarModel)it.next()).getAsString();
/* 338 */       values.add(get(key));
/*     */     } 
/* 340 */     return (TemplateCollectionModel)new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(values, (ObjectWrapper)this.wrapper));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getAsClassicCompatibleString() {
/* 352 */     if (this.object == null) {
/* 353 */       return "null";
/*     */     }
/* 355 */     String s = this.object.toString();
/* 356 */     return (s != null) ? s : "null";
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 361 */     return this.object.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Set keySet() {
/* 371 */     return this.wrapper.getClassIntrospector().keySet(this.object.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/* 376 */     return (TemplateModel)this.wrapper.wrapAsAPI(this.object);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BeanModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */