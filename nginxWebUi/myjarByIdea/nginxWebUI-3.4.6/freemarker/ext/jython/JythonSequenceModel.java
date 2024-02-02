package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonSequenceModel extends JythonModel implements TemplateSequenceModel, TemplateCollectionModel {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new JythonSequenceModel((PyObject)object, (JythonWrapper)wrapper);
      }
   };

   public JythonSequenceModel(PyObject object, JythonWrapper wrapper) {
      super(object, wrapper);
   }

   public TemplateModel get(int index) throws TemplateModelException {
      try {
         return this.wrapper.wrap(this.object.__finditem__(index));
      } catch (PyException var3) {
         throw new TemplateModelException(var3);
      }
   }

   public int size() throws TemplateModelException {
      try {
         return this.object.__len__();
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }
   }

   public TemplateModelIterator iterator() {
      return new TemplateModelIterator() {
         int i = 0;

         public boolean hasNext() throws TemplateModelException {
            return this.i < JythonSequenceModel.this.size();
         }

         public TemplateModel next() throws TemplateModelException {
            return JythonSequenceModel.this.get(this.i++);
         }
      };
   }
}
