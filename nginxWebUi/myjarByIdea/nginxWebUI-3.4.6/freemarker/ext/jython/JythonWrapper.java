package freemarker.ext.jython;

import freemarker.ext.util.ModelCache;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.OptimizerUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.python.core.Py;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;

public class JythonWrapper implements ObjectWrapper {
   private static final Class PYOBJECT_CLASS = PyObject.class;
   public static final JythonWrapper INSTANCE = new JythonWrapper();
   private final ModelCache modelCache = new JythonModelCache(this);
   private boolean attributesShadowItems = true;

   public void setUseCache(boolean useCache) {
      this.modelCache.setUseCache(useCache);
   }

   public synchronized void setAttributesShadowItems(boolean attributesShadowItems) {
      this.attributesShadowItems = attributesShadowItems;
   }

   boolean isAttributesShadowItems() {
      return this.attributesShadowItems;
   }

   public TemplateModel wrap(Object obj) {
      return obj == null ? null : this.modelCache.getInstance(obj);
   }

   public PyObject unwrap(TemplateModel model) throws TemplateModelException {
      if (model instanceof AdapterTemplateModel) {
         return Py.java2py(((AdapterTemplateModel)model).getAdaptedObject(PYOBJECT_CLASS));
      } else if (model instanceof WrapperTemplateModel) {
         return Py.java2py(((WrapperTemplateModel)model).getWrappedObject());
      } else if (model instanceof TemplateScalarModel) {
         return new PyString(((TemplateScalarModel)model).getAsString());
      } else if (model instanceof TemplateNumberModel) {
         Number number = ((TemplateNumberModel)model).getAsNumber();
         if (number instanceof BigDecimal) {
            number = OptimizerUtil.optimizeNumberRepresentation(number);
         }

         return (PyObject)(number instanceof BigInteger ? new PyLong((BigInteger)number) : Py.java2py(number));
      } else {
         return new TemplateModelToJythonAdapter(model);
      }
   }

   private class TemplateModelToJythonAdapter extends PyObject implements TemplateModelAdapter {
      private final TemplateModel model;

      TemplateModelToJythonAdapter(TemplateModel model) {
         this.model = model;
      }

      public TemplateModel getTemplateModel() {
         return this.model;
      }

      public PyObject __finditem__(PyObject key) {
         return key instanceof PyInteger ? this.__finditem__(((PyInteger)key).getValue()) : this.__finditem__(key.toString());
      }

      public PyObject __finditem__(String key) {
         if (this.model instanceof TemplateHashModel) {
            try {
               return JythonWrapper.this.unwrap(((TemplateHashModel)this.model).get(key));
            } catch (TemplateModelException var3) {
               throw Py.JavaError(var3);
            }
         } else {
            throw Py.TypeError("item lookup on non-hash model (" + this.getModelClass() + ")");
         }
      }

      public PyObject __finditem__(int index) {
         if (this.model instanceof TemplateSequenceModel) {
            try {
               return JythonWrapper.this.unwrap(((TemplateSequenceModel)this.model).get(index));
            } catch (TemplateModelException var3) {
               throw Py.JavaError(var3);
            }
         } else {
            throw Py.TypeError("item lookup on non-sequence model (" + this.getModelClass() + ")");
         }
      }

      public PyObject __call__(PyObject[] args, String[] keywords) {
         if (this.model instanceof TemplateMethodModel) {
            boolean isEx = this.model instanceof TemplateMethodModelEx;
            List list = new ArrayList(args.length);

            try {
               for(int i = 0; i < args.length; ++i) {
                  list.add(isEx ? JythonWrapper.this.wrap(args[i]) : (args[i] == null ? null : args[i].toString()));
               }

               return JythonWrapper.this.unwrap((TemplateModel)((TemplateMethodModelEx)this.model).exec(list));
            } catch (TemplateModelException var6) {
               throw Py.JavaError(var6);
            }
         } else {
            throw Py.TypeError("call of non-method model (" + this.getModelClass() + ")");
         }
      }

      public int __len__() {
         try {
            if (this.model instanceof TemplateSequenceModel) {
               return ((TemplateSequenceModel)this.model).size();
            } else {
               return this.model instanceof TemplateHashModelEx ? ((TemplateHashModelEx)this.model).size() : 0;
            }
         } catch (TemplateModelException var2) {
            throw Py.JavaError(var2);
         }
      }

      public boolean __nonzero__() {
         try {
            if (this.model instanceof TemplateBooleanModel) {
               return ((TemplateBooleanModel)this.model).getAsBoolean();
            } else if (this.model instanceof TemplateSequenceModel) {
               return ((TemplateSequenceModel)this.model).size() > 0;
            } else if (this.model instanceof TemplateHashModel) {
               return !((TemplateHashModelEx)this.model).isEmpty();
            } else {
               return false;
            }
         } catch (TemplateModelException var2) {
            throw Py.JavaError(var2);
         }
      }

      private String getModelClass() {
         return this.model == null ? "null" : this.model.getClass().getName();
      }
   }
}
