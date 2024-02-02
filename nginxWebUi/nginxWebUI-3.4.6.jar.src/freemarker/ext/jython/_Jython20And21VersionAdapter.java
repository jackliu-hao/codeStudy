/*    */ package freemarker.ext.jython;
/*    */ 
/*    */ import org.python.core.PyJavaInstance;
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
/*    */ public class _Jython20And21VersionAdapter
/*    */   extends JythonVersionAdapter
/*    */ {
/*    */   public boolean isPyInstance(Object obj) {
/* 35 */     return obj instanceof PyJavaInstance;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object pyInstanceToJava(Object pyInstance) {
/* 40 */     return ((PyJavaInstance)pyInstance).__tojava__(Object.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPythonClassName(PyObject pyObject) {
/* 45 */     return pyObject.__class__.__name__;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\_Jython20And21VersionAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */