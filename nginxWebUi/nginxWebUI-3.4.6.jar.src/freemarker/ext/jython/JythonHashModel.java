/*     */ package freemarker.ext.jython;
/*     */ 
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JythonHashModel
/*     */   extends JythonModel
/*     */   implements TemplateHashModelEx
/*     */ {
/*     */   private static final String KEYS = "keys";
/*     */   private static final String KEYSET = "keySet";
/*     */   private static final String VALUES = "values";
/*     */   
/*  53 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  58 */         return (TemplateModel)new JythonHashModel((PyObject)object, (JythonWrapper)wrapper);
/*     */       }
/*     */     };
/*     */   
/*     */   public JythonHashModel(PyObject object, JythonWrapper wrapper) {
/*  63 */     super(object, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() throws TemplateModelException {
/*     */     try {
/*  72 */       return this.object.__len__();
/*  73 */     } catch (PyException e) {
/*  74 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() throws TemplateModelException {
/*     */     try {
/*  85 */       PyObject method = this.object.__findattr__("keys");
/*  86 */       if (method == null) {
/*  87 */         method = this.object.__findattr__("keySet");
/*     */       }
/*  89 */       if (method != null) {
/*  90 */         return (TemplateCollectionModel)this.wrapper.wrap(method.__call__());
/*     */       }
/*  92 */     } catch (PyException e) {
/*  93 */       throw new TemplateModelException(e);
/*     */     } 
/*  95 */     throw new TemplateModelException("'?keys' is not supported as there is no 'keys' nor 'keySet' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE
/*     */         
/*  97 */         .getPythonClassName(this.object));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() throws TemplateModelException {
/*     */     try {
/* 106 */       PyObject method = this.object.__findattr__("values");
/* 107 */       if (method != null) {
/* 108 */         return (TemplateCollectionModel)this.wrapper.wrap(method.__call__());
/*     */       }
/* 110 */     } catch (PyException e) {
/* 111 */       throw new TemplateModelException(e);
/*     */     } 
/* 113 */     throw new TemplateModelException("'?values' is not supported as there is no 'values' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE
/*     */         
/* 115 */         .getPythonClassName(this.object));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */