/*     */ package freemarker.ext.jython;
/*     */ 
/*     */ import freemarker.ext.util.ModelCache;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelAdapter;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.OptimizerUtil;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.python.core.Py;
/*     */ import org.python.core.PyInteger;
/*     */ import org.python.core.PyLong;
/*     */ import org.python.core.PyObject;
/*     */ import org.python.core.PyString;
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
/*     */ public class JythonWrapper
/*     */   implements ObjectWrapper
/*     */ {
/*  59 */   private static final Class PYOBJECT_CLASS = PyObject.class;
/*  60 */   public static final JythonWrapper INSTANCE = new JythonWrapper();
/*     */   
/*  62 */   private final ModelCache modelCache = new JythonModelCache(this);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean attributesShadowItems = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUseCache(boolean useCache) {
/*  75 */     this.modelCache.setUseCache(useCache);
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
/*     */   public synchronized void setAttributesShadowItems(boolean attributesShadowItems) {
/*  89 */     this.attributesShadowItems = attributesShadowItems;
/*     */   }
/*     */   
/*     */   boolean isAttributesShadowItems() {
/*  93 */     return this.attributesShadowItems;
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
/*     */   public TemplateModel wrap(Object obj) {
/* 111 */     if (obj == null) {
/* 112 */       return null;
/*     */     }
/* 114 */     return this.modelCache.getInstance(obj);
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
/*     */   public PyObject unwrap(TemplateModel model) throws TemplateModelException {
/* 140 */     if (model instanceof AdapterTemplateModel) {
/* 141 */       return Py.java2py(((AdapterTemplateModel)model).getAdaptedObject(PYOBJECT_CLASS));
/*     */     }
/*     */     
/* 144 */     if (model instanceof WrapperTemplateModel) {
/* 145 */       return Py.java2py(((WrapperTemplateModel)model).getWrappedObject());
/*     */     }
/*     */ 
/*     */     
/* 149 */     if (model instanceof TemplateScalarModel) {
/* 150 */       return (PyObject)new PyString(((TemplateScalarModel)model).getAsString());
/*     */     }
/*     */ 
/*     */     
/* 154 */     if (model instanceof TemplateNumberModel) {
/* 155 */       Number number = ((TemplateNumberModel)model).getAsNumber();
/* 156 */       if (number instanceof java.math.BigDecimal) {
/* 157 */         number = OptimizerUtil.optimizeNumberRepresentation(number);
/*     */       }
/* 159 */       if (number instanceof BigInteger)
/*     */       {
/*     */ 
/*     */         
/* 163 */         return (PyObject)new PyLong((BigInteger)number);
/*     */       }
/* 165 */       return Py.java2py(number);
/*     */     } 
/*     */ 
/*     */     
/* 169 */     return new TemplateModelToJythonAdapter(model);
/*     */   }
/*     */   
/*     */   private class TemplateModelToJythonAdapter
/*     */     extends PyObject implements TemplateModelAdapter {
/*     */     private final TemplateModel model;
/*     */     
/*     */     TemplateModelToJythonAdapter(TemplateModel model) {
/* 177 */       this.model = model;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel getTemplateModel() {
/* 182 */       return this.model;
/*     */     }
/*     */ 
/*     */     
/*     */     public PyObject __finditem__(PyObject key) {
/* 187 */       if (key instanceof PyInteger) {
/* 188 */         return __finditem__(((PyInteger)key).getValue());
/*     */       }
/* 190 */       return __finditem__(key.toString());
/*     */     }
/*     */ 
/*     */     
/*     */     public PyObject __finditem__(String key) {
/* 195 */       if (this.model instanceof TemplateHashModel) {
/*     */         try {
/* 197 */           return JythonWrapper.this.unwrap(((TemplateHashModel)this.model).get(key));
/* 198 */         } catch (TemplateModelException e) {
/* 199 */           throw Py.JavaError(e);
/*     */         } 
/*     */       }
/* 202 */       throw Py.TypeError("item lookup on non-hash model (" + getModelClass() + ")");
/*     */     }
/*     */ 
/*     */     
/*     */     public PyObject __finditem__(int index) {
/* 207 */       if (this.model instanceof TemplateSequenceModel) {
/*     */         try {
/* 209 */           return JythonWrapper.this.unwrap(((TemplateSequenceModel)this.model).get(index));
/* 210 */         } catch (TemplateModelException e) {
/* 211 */           throw Py.JavaError(e);
/*     */         } 
/*     */       }
/* 214 */       throw Py.TypeError("item lookup on non-sequence model (" + getModelClass() + ")");
/*     */     }
/*     */ 
/*     */     
/*     */     public PyObject __call__(PyObject[] args, String[] keywords) {
/* 219 */       if (this.model instanceof freemarker.template.TemplateMethodModel) {
/* 220 */         boolean isEx = this.model instanceof TemplateMethodModelEx;
/* 221 */         List list = new ArrayList(args.length);
/*     */         try {
/* 223 */           for (int i = 0; i < args.length; i++) {
/* 224 */             list.add(isEx ? JythonWrapper.this
/*     */                 
/* 226 */                 .wrap(args[i]) : ((args[i] == null) ? null : args[i]
/*     */ 
/*     */                 
/* 229 */                 .toString()));
/*     */           }
/* 231 */           return JythonWrapper.this.unwrap((TemplateModel)((TemplateMethodModelEx)this.model).exec(list));
/* 232 */         } catch (TemplateModelException e) {
/* 233 */           throw Py.JavaError(e);
/*     */         } 
/*     */       } 
/* 236 */       throw Py.TypeError("call of non-method model (" + getModelClass() + ")");
/*     */     }
/*     */ 
/*     */     
/*     */     public int __len__() {
/*     */       try {
/* 242 */         if (this.model instanceof TemplateSequenceModel) {
/* 243 */           return ((TemplateSequenceModel)this.model).size();
/*     */         }
/* 245 */         if (this.model instanceof TemplateHashModelEx) {
/* 246 */           return ((TemplateHashModelEx)this.model).size();
/*     */         }
/* 248 */       } catch (TemplateModelException e) {
/* 249 */         throw Py.JavaError(e);
/*     */       } 
/*     */       
/* 252 */       return 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean __nonzero__() {
/*     */       try {
/* 258 */         if (this.model instanceof TemplateBooleanModel) {
/* 259 */           return ((TemplateBooleanModel)this.model).getAsBoolean();
/*     */         }
/* 261 */         if (this.model instanceof TemplateSequenceModel) {
/* 262 */           return (((TemplateSequenceModel)this.model).size() > 0);
/*     */         }
/* 264 */         if (this.model instanceof TemplateHashModel) {
/* 265 */           return !((TemplateHashModelEx)this.model).isEmpty();
/*     */         }
/* 267 */       } catch (TemplateModelException e) {
/* 268 */         throw Py.JavaError(e);
/*     */       } 
/* 270 */       return false;
/*     */     }
/*     */     
/*     */     private String getModelClass() {
/* 274 */       return (this.model == null) ? "null" : this.model.getClass().getName();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonWrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */