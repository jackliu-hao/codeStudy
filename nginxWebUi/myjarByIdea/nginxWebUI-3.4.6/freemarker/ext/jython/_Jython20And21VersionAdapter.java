package freemarker.ext.jython;

import org.python.core.PyJavaInstance;
import org.python.core.PyObject;

public class _Jython20And21VersionAdapter extends JythonVersionAdapter {
   public boolean isPyInstance(Object obj) {
      return obj instanceof PyJavaInstance;
   }

   public Object pyInstanceToJava(Object pyInstance) {
      return ((PyJavaInstance)pyInstance).__tojava__(Object.class);
   }

   public String getPythonClassName(PyObject pyObject) {
      return pyObject.__class__.__name__;
   }
}
