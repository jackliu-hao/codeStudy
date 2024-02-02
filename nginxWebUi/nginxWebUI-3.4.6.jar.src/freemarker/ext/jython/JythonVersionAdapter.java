package freemarker.ext.jython;

import org.python.core.PyObject;

public abstract class JythonVersionAdapter {
  public abstract boolean isPyInstance(Object paramObject);
  
  public abstract Object pyInstanceToJava(Object paramObject);
  
  public abstract String getPythonClassName(PyObject paramPyObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jython\JythonVersionAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */