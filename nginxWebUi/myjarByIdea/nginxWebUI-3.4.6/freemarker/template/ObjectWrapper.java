package freemarker.template;

import freemarker.ext.beans.BeansWrapper;

public interface ObjectWrapper {
   /** @deprecated */
   @Deprecated
   ObjectWrapper BEANS_WRAPPER = BeansWrapper.getDefaultInstance();
   /** @deprecated */
   @Deprecated
   ObjectWrapper DEFAULT_WRAPPER = DefaultObjectWrapper.instance;
   /** @deprecated */
   @Deprecated
   ObjectWrapper SIMPLE_WRAPPER = SimpleObjectWrapper.instance;

   TemplateModel wrap(Object var1) throws TemplateModelException;
}
