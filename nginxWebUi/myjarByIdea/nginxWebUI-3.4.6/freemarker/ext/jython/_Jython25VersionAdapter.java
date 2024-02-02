package freemarker.ext.jython;

import org.python.core.PyInstance;
import org.python.core.PyObject;

public class _Jython25VersionAdapter extends JythonVersionAdapter {
   public boolean isPyInstance(Object obj) {
      return obj instanceof PyInstance;
   }

   public Object pyInstanceToJava(Object pyInstance) {
      return ((PyInstance)pyInstance).__tojava__(Object.class);
   }

   public String getPythonClassName(PyObject pyObject) {
      return pyObject.getType().getName();
   }
}
