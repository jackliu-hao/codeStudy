package freemarker.ext.jython;

import org.python.core.PyObject;

public abstract class JythonVersionAdapter {
   public abstract boolean isPyInstance(Object var1);

   public abstract Object pyInstanceToJava(Object var1);

   public abstract String getPythonClassName(PyObject var1);
}
