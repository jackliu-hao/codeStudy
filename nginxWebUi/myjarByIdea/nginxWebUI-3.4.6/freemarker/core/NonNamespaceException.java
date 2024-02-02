package freemarker.core;

import freemarker.template.TemplateModel;

class NonNamespaceException extends UnexpectedTypeException {
   private static final Class[] EXPECTED_TYPES = new Class[]{Environment.Namespace.class};

   public NonNamespaceException(Environment env) {
      super(env, "Expecting namespace value here");
   }

   public NonNamespaceException(String description, Environment env) {
      super(env, description);
   }

   NonNamespaceException(Environment env, _ErrorDescriptionBuilder description) {
      super(env, description);
   }

   NonNamespaceException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
      super(blamed, model, "namespace", EXPECTED_TYPES, env);
   }

   NonNamespaceException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
      super(blamed, model, "namespace", EXPECTED_TYPES, tip, env);
   }

   NonNamespaceException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
      super((Expression)blamed, model, "namespace", EXPECTED_TYPES, (Object[])tips, env);
   }
}
