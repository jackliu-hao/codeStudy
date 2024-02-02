package freemarker.ext.ant;

import freemarker.template.utility.ClassUtil;
import java.io.File;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;

public class JythonAntTask extends Task {
   private File scriptFile;
   private String script = "";
   private UnlinkedJythonOperations jythonOps;

   public void setFile(File scriptFile) throws BuildException {
      this.ensureJythonOpsExists();
      this.scriptFile = scriptFile;
   }

   public void addText(String text) {
      this.script = this.script + text;
   }

   public void execute(Map vars) throws BuildException {
      if (this.scriptFile != null) {
         this.ensureJythonOpsExists();
         this.jythonOps.execute(this.scriptFile, vars);
      }

      if (this.script.trim().length() > 0) {
         this.ensureJythonOpsExists();
         String finalScript = ProjectHelper.replaceProperties(this.project, this.script, this.project.getProperties());
         this.jythonOps.execute(finalScript, vars);
      }

   }

   private void ensureJythonOpsExists() {
      if (this.jythonOps == null) {
         Class clazz;
         try {
            clazz = ClassUtil.forName("freemarker.ext.ant.UnlinkedJythonOperationsImpl");
         } catch (ClassNotFoundException var4) {
            throw new RuntimeException("A ClassNotFoundException has been thrown when trying to get the freemarker.ext.ant.UnlinkedJythonOperationsImpl class. The error message was: " + var4.getMessage());
         }

         try {
            this.jythonOps = (UnlinkedJythonOperations)clazz.newInstance();
         } catch (Exception var3) {
            throw new RuntimeException("An exception has been thrown when trying to create a freemarker.ext.ant.JythonAntTask object. The exception was: " + var3);
         }
      }

   }
}
