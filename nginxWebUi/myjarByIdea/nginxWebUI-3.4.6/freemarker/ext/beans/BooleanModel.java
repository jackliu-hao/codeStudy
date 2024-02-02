package freemarker.ext.beans;

import freemarker.template.TemplateBooleanModel;

public class BooleanModel extends BeanModel implements TemplateBooleanModel {
   private final boolean value;

   public BooleanModel(Boolean bool, BeansWrapper wrapper) {
      super(bool, wrapper, false);
      this.value = bool;
   }

   public boolean getAsBoolean() {
      return this.value;
   }
}
