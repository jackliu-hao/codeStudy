/*    */ package freemarker.ext.jython;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
/*    */ import org.python.core.PySystemState;
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
/*    */ class JythonVersionAdapterHolder
/*    */ {
/*    */   static final JythonVersionAdapter INSTANCE;
/*    */   
/*    */   static {
/*    */     int version;
/*    */     try {
/* 41 */       version = StringUtil.versionStringToInt(PySystemState.class
/* 42 */           .getField("version").get(null).toString());
/* 43 */     } catch (Exception e) {
/* 44 */       throw new RuntimeException("Failed to get Jython version: " + e);
/*    */     } 
/* 46 */     ClassLoader cl = JythonVersionAdapter.class.getClassLoader();
/*    */     try {
/* 48 */       if (version >= 2005000) {
/*    */ 
/*    */         
/* 51 */         INSTANCE = (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython25VersionAdapter").newInstance();
/* 52 */       } else if (version >= 2002000) {
/*    */ 
/*    */         
/* 55 */         INSTANCE = (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython22VersionAdapter").newInstance();
/*    */       }
/*    */       else {
/*    */         
/* 59 */         INSTANCE = (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython20And21VersionAdapter").newInstance();
/*    */       } 
/* 61 */     } catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {
/* 62 */       throw adapterCreationException(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   private static RuntimeException adapterCreationException(Exception e) {
/* 67 */     return new RuntimeException("Unexpected exception when creating JythonVersionAdapter", e);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonVersionAdapterHolder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */