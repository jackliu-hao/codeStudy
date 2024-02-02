/*    */ package freemarker.ext.ant;
/*    */ 
/*    */ import freemarker.template.utility.ClassUtil;
/*    */ import java.io.File;
/*    */ import java.util.Map;
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.apache.tools.ant.ProjectHelper;
/*    */ import org.apache.tools.ant.Task;
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
/*    */ public class JythonAntTask
/*    */   extends Task
/*    */ {
/*    */   private File scriptFile;
/* 42 */   private String script = "";
/*    */   private UnlinkedJythonOperations jythonOps;
/*    */   
/*    */   public void setFile(File scriptFile) throws BuildException {
/* 46 */     ensureJythonOpsExists();
/* 47 */     this.scriptFile = scriptFile;
/*    */   }
/*    */   
/*    */   public void addText(String text) {
/* 51 */     this.script += text;
/*    */   }
/*    */   
/*    */   public void execute(Map vars) throws BuildException {
/* 55 */     if (this.scriptFile != null) {
/* 56 */       ensureJythonOpsExists();
/* 57 */       this.jythonOps.execute(this.scriptFile, vars);
/*    */     } 
/* 59 */     if (this.script.trim().length() > 0) {
/* 60 */       ensureJythonOpsExists();
/* 61 */       String finalScript = ProjectHelper.replaceProperties(this.project, this.script, this.project
/* 62 */           .getProperties());
/* 63 */       this.jythonOps.execute(finalScript, vars);
/*    */     } 
/*    */   }
/*    */   
/*    */   private void ensureJythonOpsExists() {
/* 68 */     if (this.jythonOps == null) {
/*    */       Class<UnlinkedJythonOperations> clazz;
/*    */       try {
/* 71 */         clazz = ClassUtil.forName("freemarker.ext.ant.UnlinkedJythonOperationsImpl");
/*    */       }
/* 73 */       catch (ClassNotFoundException e) {
/* 74 */         throw new RuntimeException("A ClassNotFoundException has been thrown when trying to get the freemarker.ext.ant.UnlinkedJythonOperationsImpl class. The error message was: " + e
/*    */ 
/*    */ 
/*    */             
/* 78 */             .getMessage());
/*    */       } 
/*    */       try {
/* 81 */         this
/* 82 */           .jythonOps = clazz.newInstance();
/* 83 */       } catch (Exception e) {
/* 84 */         throw new RuntimeException("An exception has been thrown when trying to create a freemarker.ext.ant.JythonAntTask object. The exception was: " + e);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\ant\JythonAntTask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */