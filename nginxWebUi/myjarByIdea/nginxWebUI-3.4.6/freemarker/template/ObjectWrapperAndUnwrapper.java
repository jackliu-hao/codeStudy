package freemarker.template;

public interface ObjectWrapperAndUnwrapper extends ObjectWrapper {
   Object CANT_UNWRAP_TO_TARGET_CLASS = new Object();

   Object unwrap(TemplateModel var1) throws TemplateModelException;

   Object tryUnwrapTo(TemplateModel var1, Class<?> var2) throws TemplateModelException;
}
