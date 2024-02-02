package freemarker.template;

public abstract class WrappingTemplateModel {
   /** @deprecated */
   @Deprecated
   private static ObjectWrapper defaultObjectWrapper;
   private ObjectWrapper objectWrapper;

   /** @deprecated */
   @Deprecated
   public static void setDefaultObjectWrapper(ObjectWrapper objectWrapper) {
      defaultObjectWrapper = objectWrapper;
   }

   /** @deprecated */
   @Deprecated
   public static ObjectWrapper getDefaultObjectWrapper() {
      return defaultObjectWrapper;
   }

   /** @deprecated */
   @Deprecated
   protected WrappingTemplateModel() {
      this(defaultObjectWrapper);
   }

   protected WrappingTemplateModel(ObjectWrapper objectWrapper) {
      this.objectWrapper = objectWrapper != null ? objectWrapper : defaultObjectWrapper;
      if (this.objectWrapper == null) {
         this.objectWrapper = defaultObjectWrapper = new DefaultObjectWrapper();
      }

   }

   public ObjectWrapper getObjectWrapper() {
      return this.objectWrapper;
   }

   public void setObjectWrapper(ObjectWrapper objectWrapper) {
      this.objectWrapper = objectWrapper;
   }

   protected final TemplateModel wrap(Object obj) throws TemplateModelException {
      return this.objectWrapper.wrap(obj);
   }

   static {
      defaultObjectWrapper = DefaultObjectWrapper.instance;
   }
}
