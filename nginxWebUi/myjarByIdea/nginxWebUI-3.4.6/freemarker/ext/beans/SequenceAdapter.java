package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractList;

class SequenceAdapter extends AbstractList implements TemplateModelAdapter {
   private final BeansWrapper wrapper;
   private final TemplateSequenceModel model;

   SequenceAdapter(TemplateSequenceModel model, BeansWrapper wrapper) {
      this.model = model;
      this.wrapper = wrapper;
   }

   public TemplateModel getTemplateModel() {
      return this.model;
   }

   public int size() {
      try {
         return this.model.size();
      } catch (TemplateModelException var2) {
         throw new UndeclaredThrowableException(var2);
      }
   }

   public Object get(int index) {
      try {
         return this.wrapper.unwrap(this.model.get(index));
      } catch (TemplateModelException var3) {
         throw new UndeclaredThrowableException(var3);
      }
   }

   public TemplateSequenceModel getTemplateSequenceModel() {
      return this.model;
   }
}
