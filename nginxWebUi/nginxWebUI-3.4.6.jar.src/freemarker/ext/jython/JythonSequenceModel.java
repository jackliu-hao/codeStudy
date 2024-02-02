/*    */ package freemarker.ext.jython;
/*    */ 
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import org.python.core.PyException;
/*    */ import org.python.core.PyObject;
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
/*    */ public class JythonSequenceModel
/*    */   extends JythonModel
/*    */   implements TemplateSequenceModel, TemplateCollectionModel
/*    */ {
/* 42 */   static final ModelFactory FACTORY = new ModelFactory()
/*    */     {
/*    */       
/*    */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*    */       {
/* 47 */         return (TemplateModel)new JythonSequenceModel((PyObject)object, (JythonWrapper)wrapper);
/*    */       }
/*    */     };
/*    */   
/*    */   public JythonSequenceModel(PyObject object, JythonWrapper wrapper) {
/* 52 */     super(object, wrapper);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateModel get(int index) throws TemplateModelException {
/*    */     try {
/* 61 */       return this.wrapper.wrap(this.object.__finditem__(index));
/* 62 */     } catch (PyException e) {
/* 63 */       throw new TemplateModelException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/*    */     try {
/* 73 */       return this.object.__len__();
/* 74 */     } catch (PyException e) {
/* 75 */       throw new TemplateModelException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModelIterator iterator() {
/* 81 */     return new TemplateModelIterator()
/*    */       {
/* 83 */         int i = 0;
/*    */ 
/*    */         
/*    */         public boolean hasNext() throws TemplateModelException {
/* 87 */           return (this.i < JythonSequenceModel.this.size());
/*    */         }
/*    */ 
/*    */         
/*    */         public TemplateModel next() throws TemplateModelException {
/* 92 */           return JythonSequenceModel.this.get(this.i++);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonSequenceModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */