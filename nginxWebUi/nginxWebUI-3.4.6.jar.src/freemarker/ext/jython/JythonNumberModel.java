/*    */ package freemarker.ext.jython;
/*    */ 
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateNumberModel;
/*    */ import org.python.core.Py;
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
/*    */ public class JythonNumberModel
/*    */   extends JythonModel
/*    */   implements TemplateNumberModel
/*    */ {
/* 41 */   static final ModelFactory FACTORY = new ModelFactory()
/*    */     {
/*    */       
/*    */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*    */       {
/* 46 */         return (TemplateModel)new JythonNumberModel((PyObject)object, (JythonWrapper)wrapper);
/*    */       }
/*    */     };
/*    */   
/*    */   public JythonNumberModel(PyObject object, JythonWrapper wrapper) {
/* 51 */     super(object, wrapper);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Number getAsNumber() throws TemplateModelException {
/*    */     try {
/* 62 */       Object value = this.object.__tojava__(Number.class);
/* 63 */       if (value == null || value == Py.NoConversion) {
/* 64 */         return Double.valueOf(this.object.__float__().getValue());
/*    */       }
/* 66 */       return (Number)value;
/* 67 */     } catch (PyException e) {
/* 68 */       throw new TemplateModelException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonNumberModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */