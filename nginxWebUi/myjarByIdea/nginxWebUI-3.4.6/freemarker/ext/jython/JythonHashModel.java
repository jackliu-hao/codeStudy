package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonHashModel extends JythonModel implements TemplateHashModelEx {
   private static final String KEYS = "keys";
   private static final String KEYSET = "keySet";
   private static final String VALUES = "values";
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new JythonHashModel((PyObject)object, (JythonWrapper)wrapper);
      }
   };

   public JythonHashModel(PyObject object, JythonWrapper wrapper) {
      super(object, wrapper);
   }

   public int size() throws TemplateModelException {
      try {
         return this.object.__len__();
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }
   }

   public TemplateCollectionModel keys() throws TemplateModelException {
      try {
         PyObject method = this.object.__findattr__("keys");
         if (method == null) {
            method = this.object.__findattr__("keySet");
         }

         if (method != null) {
            return (TemplateCollectionModel)this.wrapper.wrap(method.__call__());
         }
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }

      throw new TemplateModelException("'?keys' is not supported as there is no 'keys' nor 'keySet' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE.getPythonClassName(this.object));
   }

   public TemplateCollectionModel values() throws TemplateModelException {
      try {
         PyObject method = this.object.__findattr__("values");
         if (method != null) {
            return (TemplateCollectionModel)this.wrapper.wrap(method.__call__());
         }
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }

      throw new TemplateModelException("'?values' is not supported as there is no 'values' attribute on an instance of " + JythonVersionAdapterHolder.INSTANCE.getPythonClassName(this.object));
   }
}
