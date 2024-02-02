package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonNumberModel extends JythonModel implements TemplateNumberModel {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new JythonNumberModel((PyObject)object, (JythonWrapper)wrapper);
      }
   };

   public JythonNumberModel(PyObject object, JythonWrapper wrapper) {
      super(object, wrapper);
   }

   public Number getAsNumber() throws TemplateModelException {
      try {
         Object value = this.object.__tojava__(Number.class);
         return (Number)(value != null && value != Py.NoConversion ? (Number)value : this.object.__float__().getValue());
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }
   }
}
