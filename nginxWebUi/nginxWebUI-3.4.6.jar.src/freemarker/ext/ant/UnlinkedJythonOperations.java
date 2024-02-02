package freemarker.ext.ant;

import java.io.File;
import java.util.Map;
import org.apache.tools.ant.BuildException;

interface UnlinkedJythonOperations {
  void execute(String paramString, Map paramMap) throws BuildException;
  
  void execute(File paramFile, Map paramMap) throws BuildException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\ant\UnlinkedJythonOperations.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */