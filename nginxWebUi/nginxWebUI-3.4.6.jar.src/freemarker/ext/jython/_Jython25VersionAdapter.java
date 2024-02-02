/*    */ package freemarker.ext.jython;
/*    */ 
/*    */ import org.python.core.PyInstance;
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
/*    */ public class _Jython25VersionAdapter
/*    */   extends JythonVersionAdapter
/*    */ {
/*    */   public boolean isPyInstance(Object obj) {
/* 35 */     return obj instanceof PyInstance;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object pyInstanceToJava(Object pyInstance) {
/* 40 */     return ((PyInstance)pyInstance).__tojava__(Object.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPythonClassName(PyObject pyObject) {
/* 45 */     return pyObject.getType().getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\_Jython25VersionAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */