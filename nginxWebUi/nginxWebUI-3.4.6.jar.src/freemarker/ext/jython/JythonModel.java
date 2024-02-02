/*     */ package freemarker.ext.jython;
/*     */ 
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.python.core.Py;
/*     */ import org.python.core.PyException;
/*     */ import org.python.core.PyObject;
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
/*     */ public class JythonModel
/*     */   implements TemplateBooleanModel, TemplateScalarModel, TemplateHashModel, TemplateMethodModelEx, AdapterTemplateModel, WrapperTemplateModel
/*     */ {
/*     */   protected final PyObject object;
/*     */   protected final JythonWrapper wrapper;
/*     */   
/*  49 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  54 */         return (TemplateModel)new JythonModel((PyObject)object, (JythonWrapper)wrapper);
/*     */       }
/*     */     };
/*     */   
/*     */   public JythonModel(PyObject object, JythonWrapper wrapper) {
/*  59 */     this.object = object;
/*  60 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAsBoolean() throws TemplateModelException {
/*     */     try {
/*  69 */       return this.object.__nonzero__();
/*  70 */     } catch (PyException e) {
/*  71 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAsString() throws TemplateModelException {
/*     */     try {
/*  81 */       return this.object.toString();
/*  82 */     } catch (PyException e) {
/*  83 */       throw new TemplateModelException(e);
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
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  97 */     if (key != null) {
/*  98 */       key = key.intern();
/*     */     }
/*     */     
/* 101 */     PyObject obj = null;
/*     */     
/*     */     try {
/* 104 */       if (this.wrapper.isAttributesShadowItems()) {
/* 105 */         obj = this.object.__findattr__(key);
/* 106 */         if (obj == null) {
/* 107 */           obj = this.object.__finditem__(key);
/*     */         }
/*     */       } else {
/* 110 */         obj = this.object.__finditem__(key);
/* 111 */         if (obj == null) {
/* 112 */           obj = this.object.__findattr__(key);
/*     */         }
/*     */       } 
/* 115 */     } catch (PyException e) {
/* 116 */       throw new TemplateModelException(e);
/*     */     } 
/*     */     
/* 119 */     return this.wrapper.wrap(obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() throws TemplateModelException {
/*     */     try {
/* 128 */       return (this.object.__len__() == 0);
/* 129 */     } catch (PyException e) {
/* 130 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object exec(List<TemplateModel> arguments) throws TemplateModelException {
/* 139 */     int size = arguments.size();
/*     */     try {
/* 141 */       switch (size) {
/*     */ 
/*     */         
/*     */         case 0:
/* 145 */           return this.wrapper.wrap(this.object.__call__());
/*     */ 
/*     */         
/*     */         case 1:
/* 149 */           return this.wrapper.wrap(this.object.__call__(this.wrapper.unwrap(arguments
/* 150 */                   .get(0))));
/*     */       } 
/*     */ 
/*     */       
/* 154 */       PyObject[] pyargs = new PyObject[size];
/* 155 */       int i = 0;
/* 156 */       for (Iterator<TemplateModel> arg = arguments.iterator(); arg.hasNext();) {
/* 157 */         pyargs[i++] = this.wrapper.unwrap(arg
/* 158 */             .next());
/*     */       }
/* 160 */       return this.wrapper.wrap(this.object.__call__(pyargs));
/*     */     
/*     */     }
/* 163 */     catch (PyException e) {
/* 164 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/* 170 */     if (this.object == null) {
/* 171 */       return null;
/*     */     }
/* 173 */     Object view = this.object.__tojava__(hint);
/* 174 */     if (view == Py.NoConversion) {
/* 175 */       view = this.object.__tojava__(Object.class);
/*     */     }
/* 177 */     return view;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/* 182 */     return (this.object == null) ? null : this.object.__tojava__(Object.class);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */