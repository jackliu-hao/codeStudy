package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

class StaticModels extends ClassBasedModelFactory {
   StaticModels(BeansWrapper wrapper) {
      super(wrapper);
   }

   protected TemplateModel createModel(Class clazz) throws TemplateModelException {
      return new StaticModel(clazz, this.getWrapper());
   }
}
