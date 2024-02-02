package freemarker.ext.jython;

import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import java.util.Iterator;
import java.util.List;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

public class JythonModel implements TemplateBooleanModel, TemplateScalarModel, TemplateHashModel, TemplateMethodModelEx, AdapterTemplateModel, WrapperTemplateModel {
   protected final PyObject object;
   protected final JythonWrapper wrapper;
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new JythonModel((PyObject)object, (JythonWrapper)wrapper);
      }
   };

   public JythonModel(PyObject object, JythonWrapper wrapper) {
      this.object = object;
      this.wrapper = wrapper;
   }

   public boolean getAsBoolean() throws TemplateModelException {
      try {
         return this.object.__nonzero__();
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }
   }

   public String getAsString() throws TemplateModelException {
      try {
         return this.object.toString();
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }
   }

   public TemplateModel get(String key) throws TemplateModelException {
      if (key != null) {
         key = key.intern();
      }

      PyObject obj = null;

      try {
         if (this.wrapper.isAttributesShadowItems()) {
            obj = this.object.__findattr__(key);
            if (obj == null) {
               obj = this.object.__finditem__(key);
            }
         } else {
            obj = this.object.__finditem__(key);
            if (obj == null) {
               obj = this.object.__findattr__(key);
            }
         }
      } catch (PyException var4) {
         throw new TemplateModelException(var4);
      }

      return this.wrapper.wrap(obj);
   }

   public boolean isEmpty() throws TemplateModelException {
      try {
         return this.object.__len__() == 0;
      } catch (PyException var2) {
         throw new TemplateModelException(var2);
      }
   }

   public Object exec(List arguments) throws TemplateModelException {
      int size = arguments.size();

      try {
         switch (size) {
            case 0:
               return this.wrapper.wrap(this.object.__call__());
            case 1:
               return this.wrapper.wrap(this.object.__call__(this.wrapper.unwrap((TemplateModel)arguments.get(0))));
            default:
               PyObject[] pyargs = new PyObject[size];
               int i = 0;

               for(Iterator arg = arguments.iterator(); arg.hasNext(); pyargs[i++] = this.wrapper.unwrap((TemplateModel)arg.next())) {
               }

               return this.wrapper.wrap(this.object.__call__(pyargs));
         }
      } catch (PyException var6) {
         throw new TemplateModelException(var6);
      }
   }

   public Object getAdaptedObject(Class hint) {
      if (this.object == null) {
         return null;
      } else {
         Object view = this.object.__tojava__(hint);
         if (view == Py.NoConversion) {
            view = this.object.__tojava__(Object.class);
         }

         return view;
      }
   }

   public Object getWrappedObject() {
      return this.object == null ? null : this.object.__tojava__(Object.class);
   }
}
