package freemarker.ext.ant;

import java.io.File;
import java.util.Map;
import org.apache.tools.ant.BuildException;

interface UnlinkedJythonOperations {
   void execute(String var1, Map var2) throws BuildException;

   void execute(File var1, Map var2) throws BuildException;
}
