package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

public class StringModel extends BeanModel implements TemplateScalarModel {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new StringModel(object, (BeansWrapper)wrapper);
      }
   };
   static final String TO_STRING_NOT_EXPOSED = "[toString not exposed]";

   public StringModel(Object object, BeansWrapper wrapper) {
      super(object, wrapper);
   }

   public String getAsString() {
      boolean exposeToString = this.wrapper.getMemberAccessPolicy().isToStringAlwaysExposed() || !this.wrapper.getClassIntrospector().get(this.object.getClass()).containsKey(ClassIntrospector.TO_STRING_HIDDEN_FLAG_KEY);
      return exposeToString ? this.object.toString() : "[toString not exposed]";
   }
}
