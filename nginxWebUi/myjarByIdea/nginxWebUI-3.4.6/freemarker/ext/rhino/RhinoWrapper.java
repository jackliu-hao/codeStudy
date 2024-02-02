package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.Wrapper;

public class RhinoWrapper extends BeansWrapper {
   private static final Object UNDEFINED_INSTANCE;

   public TemplateModel wrap(Object obj) throws TemplateModelException {
      if (obj != UNDEFINED_INSTANCE && obj != UniqueTag.NOT_FOUND) {
         if (obj == UniqueTag.NULL_VALUE) {
            return super.wrap((Object)null);
         } else {
            if (obj instanceof Wrapper) {
               obj = ((Wrapper)obj).unwrap();
            }

            return super.wrap(obj);
         }
      } else {
         return null;
      }
   }

   protected ModelFactory getModelFactory(Class clazz) {
      return Scriptable.class.isAssignableFrom(clazz) ? RhinoScriptableModel.FACTORY : super.getModelFactory(clazz);
   }

   static {
      try {
         UNDEFINED_INSTANCE = AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws Exception {
               return Undefined.class.getField("instance").get((Object)null);
            }
         });
      } catch (RuntimeException var1) {
         throw var1;
      } catch (Exception var2) {
         throw new UndeclaredThrowableException(var2);
      }
   }
}
