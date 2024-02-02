package freemarker.ext.jython;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.DateModel;
import freemarker.ext.util.ModelCache;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.python.core.PyStringMap;

class JythonModelCache extends ModelCache {
   private final JythonWrapper wrapper;

   JythonModelCache(JythonWrapper wrapper) {
      this.wrapper = wrapper;
   }

   protected boolean isCacheable(Object object) {
      return true;
   }

   protected TemplateModel create(Object obj) {
      boolean asHash = false;
      boolean asSequence = false;
      JythonVersionAdapter versionAdapter = JythonVersionAdapterHolder.INSTANCE;
      if (versionAdapter.isPyInstance(obj)) {
         Object jobj = versionAdapter.pyInstanceToJava(obj);
         if (jobj instanceof TemplateModel) {
            return (TemplateModel)jobj;
         }

         if (jobj instanceof Map) {
            asHash = true;
         }

         if (jobj instanceof Date) {
            return new DateModel((Date)jobj, BeansWrapper.getDefaultInstance());
         }

         if (jobj instanceof Collection) {
            asSequence = true;
            if (!(jobj instanceof List)) {
               obj = new ArrayList((Collection)jobj);
            }
         }
      }

      if (!(obj instanceof PyObject)) {
         obj = Py.java2py(obj);
      }

      if (!asHash && !(obj instanceof PyDictionary) && !(obj instanceof PyStringMap)) {
         if (!asSequence && !(obj instanceof PySequence)) {
            if (!(obj instanceof PyInteger) && !(obj instanceof PyLong) && !(obj instanceof PyFloat)) {
               return obj instanceof PyNone ? null : JythonModel.FACTORY.create(obj, this.wrapper);
            } else {
               return JythonNumberModel.FACTORY.create(obj, this.wrapper);
            }
         } else {
            return JythonSequenceModel.FACTORY.create(obj, this.wrapper);
         }
      } else {
         return JythonHashModel.FACTORY.create(obj, this.wrapper);
      }
   }
}
