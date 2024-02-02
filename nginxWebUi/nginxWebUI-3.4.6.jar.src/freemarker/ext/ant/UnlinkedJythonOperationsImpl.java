/*    */ package freemarker.ext.ant;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.python.util.PythonInterpreter;
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
/*    */ public class UnlinkedJythonOperationsImpl
/*    */   implements UnlinkedJythonOperations
/*    */ {
/*    */   public void execute(String script, Map vars) throws BuildException {
/* 37 */     PythonInterpreter pi = createInterpreter(vars);
/* 38 */     pi.exec(script);
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(File file, Map vars) throws BuildException {
/* 43 */     PythonInterpreter pi = createInterpreter(vars);
/*    */     try {
/* 45 */       pi.execfile(file.getCanonicalPath());
/* 46 */     } catch (IOException e) {
/* 47 */       throw new BuildException(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   private PythonInterpreter createInterpreter(Map vars) {
/* 52 */     PythonInterpreter pi = new PythonInterpreter();
/* 53 */     Iterator<Map.Entry> it = vars.entrySet().iterator();
/* 54 */     while (it.hasNext()) {
/* 55 */       Map.Entry ent = it.next();
/* 56 */       pi.set((String)ent.getKey(), ent.getValue());
/*    */     } 
/* 58 */     return pi;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\ant\UnlinkedJythonOperationsImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */