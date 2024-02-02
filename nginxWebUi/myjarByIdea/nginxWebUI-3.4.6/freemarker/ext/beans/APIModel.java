package freemarker.ext.beans;

final class APIModel extends BeanModel {
   APIModel(Object object, BeansWrapper wrapper) {
      super(object, wrapper, false);
   }

   protected boolean isMethodsShadowItems() {
      return true;
   }
}
