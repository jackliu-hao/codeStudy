package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;

public class NonBooleanException extends UnexpectedTypeException {
   private static final Class[] EXPECTED_TYPES = new Class[]{TemplateBooleanModel.class};

   public NonBooleanException(Environment env) {
      super(env, "Expecting boolean value here");
   }

   public NonBooleanException(String description, Environment env) {
      super(env, description);
   }

   NonBooleanException(Environment env, _ErrorDescriptionBuilder description) {
      super(env, description);
   }

   NonBooleanException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
      super(blamed, model, "boolean", EXPECTED_TYPES, env);
   }

   NonBooleanException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
      super(blamed, model, "boolean", EXPECTED_TYPES, tip, env);
   }

   NonBooleanException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
      super((Expression)blamed, model, "boolean", EXPECTED_TYPES, (Object[])tips, env);
   }
}
