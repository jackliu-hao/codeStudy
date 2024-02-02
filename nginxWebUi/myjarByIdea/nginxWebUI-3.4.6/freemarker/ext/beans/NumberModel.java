package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

public class NumberModel extends BeanModel implements TemplateNumberModel {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new NumberModel((Number)object, (BeansWrapper)wrapper);
      }
   };

   public NumberModel(Number number, BeansWrapper wrapper) {
      super(number, wrapper);
   }

   public Number getAsNumber() {
      return (Number)this.object;
   }
}
