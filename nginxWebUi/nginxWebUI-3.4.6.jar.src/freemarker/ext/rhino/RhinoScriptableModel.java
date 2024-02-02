/*     */ package freemarker.ext.rhino;
/*     */ 
/*     */ import freemarker.ext.beans.BeansWrapper;
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import org.mozilla.javascript.Context;
/*     */ import org.mozilla.javascript.EvaluatorException;
/*     */ import org.mozilla.javascript.Function;
/*     */ import org.mozilla.javascript.NativeJavaObject;
/*     */ import org.mozilla.javascript.Scriptable;
/*     */ import org.mozilla.javascript.ScriptableObject;
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
/*     */ public class RhinoScriptableModel
/*     */   implements TemplateHashModelEx, TemplateSequenceModel, AdapterTemplateModel, TemplateScalarModel, TemplateBooleanModel, TemplateNumberModel
/*     */ {
/*  47 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper) {
/*  50 */         return (TemplateModel)new RhinoScriptableModel((Scriptable)object, (BeansWrapper)wrapper);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private final Scriptable scriptable;
/*     */   private final BeansWrapper wrapper;
/*     */   
/*     */   public RhinoScriptableModel(Scriptable scriptable, BeansWrapper wrapper) {
/*  59 */     this.scriptable = scriptable;
/*  60 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  65 */     Object retval = ScriptableObject.getProperty(this.scriptable, key);
/*  66 */     if (retval instanceof Function) {
/*  67 */       return (TemplateModel)new RhinoFunctionModel((Function)retval, this.scriptable, this.wrapper);
/*     */     }
/*  69 */     return this.wrapper.wrap(retval);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) throws TemplateModelException {
/*  75 */     Object retval = ScriptableObject.getProperty(this.scriptable, index);
/*  76 */     if (retval instanceof Function) {
/*  77 */       return (TemplateModel)new RhinoFunctionModel((Function)retval, this.scriptable, this.wrapper);
/*     */     }
/*  79 */     return this.wrapper.wrap(retval);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  85 */     return ((this.scriptable.getIds()).length == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() throws TemplateModelException {
/*  90 */     return (TemplateCollectionModel)this.wrapper.wrap(this.scriptable.getIds());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  95 */     return (this.scriptable.getIds()).length;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() throws TemplateModelException {
/* 100 */     Object[] ids = this.scriptable.getIds();
/* 101 */     Object[] values = new Object[ids.length];
/* 102 */     for (int i = 0; i < values.length; i++) {
/* 103 */       Object id = ids[i];
/* 104 */       if (id instanceof Number) {
/* 105 */         values[i] = ScriptableObject.getProperty(this.scriptable, ((Number)id)
/* 106 */             .intValue());
/*     */       } else {
/* 108 */         values[i] = ScriptableObject.getProperty(this.scriptable, 
/* 109 */             String.valueOf(id));
/*     */       } 
/*     */     } 
/* 112 */     return (TemplateCollectionModel)this.wrapper.wrap(values);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAsBoolean() {
/* 117 */     return Context.toBoolean(this.scriptable);
/*     */   }
/*     */ 
/*     */   
/*     */   public Number getAsNumber() {
/* 122 */     return Double.valueOf(Context.toNumber(this.scriptable));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAsString() {
/* 127 */     return Context.toString(this.scriptable);
/*     */   }
/*     */   
/*     */   Scriptable getScriptable() {
/* 131 */     return this.scriptable;
/*     */   }
/*     */   
/*     */   BeansWrapper getWrapper() {
/* 135 */     return this.wrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/*     */     try {
/* 142 */       return NativeJavaObject.coerceType(hint, this.scriptable);
/* 143 */     } catch (EvaluatorException e) {
/* 144 */       return NativeJavaObject.coerceType(Object.class, this.scriptable);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\rhino\RhinoScriptableModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */