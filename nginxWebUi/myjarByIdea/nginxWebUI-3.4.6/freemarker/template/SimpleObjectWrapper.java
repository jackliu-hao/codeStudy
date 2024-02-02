package freemarker.template;

public class SimpleObjectWrapper extends DefaultObjectWrapper {
   static final SimpleObjectWrapper instance = new SimpleObjectWrapper();

   /** @deprecated */
   @Deprecated
   public SimpleObjectWrapper() {
   }

   public SimpleObjectWrapper(Version incompatibleImprovements) {
      super(incompatibleImprovements);
   }

   protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
      throw new TemplateModelException(this.getClass().getName() + " deliberately won't wrap this type: " + obj.getClass().getName());
   }

   public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
      throw new TemplateModelException(this.getClass().getName() + " deliberately doesn't allow ?api.");
   }
}
