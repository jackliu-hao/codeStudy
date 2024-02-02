/*    */ package freemarker.ext.jython;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.ext.beans.DateModel;
/*    */ import freemarker.ext.util.ModelCache;
/*    */ import freemarker.template.TemplateModel;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Date;
/*    */ import org.python.core.Py;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class JythonModelCache
/*    */   extends ModelCache
/*    */ {
/*    */   private final JythonWrapper wrapper;
/*    */   
/*    */   JythonModelCache(JythonWrapper wrapper) {
/* 47 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isCacheable(Object object) {
/* 52 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   protected TemplateModel create(Object obj) {
/* 57 */     boolean asHash = false;
/* 58 */     boolean asSequence = false;
/* 59 */     JythonVersionAdapter versionAdapter = JythonVersionAdapterHolder.INSTANCE;
/* 60 */     if (versionAdapter.isPyInstance(obj)) {
/* 61 */       Object jobj = versionAdapter.pyInstanceToJava(obj);
/*    */       
/* 63 */       if (jobj instanceof TemplateModel) {
/* 64 */         return (TemplateModel)jobj;
/*    */       }
/* 66 */       if (jobj instanceof java.util.Map) {
/* 67 */         asHash = true;
/*    */       }
/* 69 */       if (jobj instanceof Date)
/* 70 */         return (TemplateModel)new DateModel((Date)jobj, BeansWrapper.getDefaultInstance()); 
/* 71 */       if (jobj instanceof Collection) {
/* 72 */         asSequence = true;
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 77 */         if (!(jobj instanceof java.util.List)) {
/* 78 */           obj = new ArrayList((Collection)jobj);
/*    */         }
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 84 */     if (!(obj instanceof org.python.core.PyObject)) {
/* 85 */       obj = Py.java2py(obj);
/*    */     }
/* 87 */     if (asHash || obj instanceof org.python.core.PyDictionary || obj instanceof org.python.core.PyStringMap) {
/* 88 */       return JythonHashModel.FACTORY.create(obj, this.wrapper);
/*    */     }
/* 90 */     if (asSequence || obj instanceof org.python.core.PySequence) {
/* 91 */       return JythonSequenceModel.FACTORY.create(obj, this.wrapper);
/*    */     }
/* 93 */     if (obj instanceof org.python.core.PyInteger || obj instanceof org.python.core.PyLong || obj instanceof org.python.core.PyFloat) {
/* 94 */       return JythonNumberModel.FACTORY.create(obj, this.wrapper);
/*    */     }
/* 96 */     if (obj instanceof org.python.core.PyNone) {
/* 97 */       return null;
/*    */     }
/* 99 */     return JythonModel.FACTORY.create(obj, this.wrapper);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonModelCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */