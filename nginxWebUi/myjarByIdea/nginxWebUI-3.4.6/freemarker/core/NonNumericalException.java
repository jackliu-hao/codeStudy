package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

public class NonNumericalException extends UnexpectedTypeException {
   private static final Class[] EXPECTED_TYPES = new Class[]{TemplateNumberModel.class};

   public NonNumericalException(Environment env) {
      super(env, "Expecting numerical value here");
   }

   public NonNumericalException(String description, Environment env) {
      super(env, description);
   }

   NonNumericalException(_ErrorDescriptionBuilder description, Environment env) {
      super(env, description);
   }

   NonNumericalException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
      super(blamed, model, "number", EXPECTED_TYPES, env);
   }

   NonNumericalException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
      super(blamed, model, "number", EXPECTED_TYPES, tip, env);
   }

   NonNumericalException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
      super((Expression)blamed, model, "number", EXPECTED_TYPES, (Object[])tips, env);
   }

   NonNumericalException(String assignmentTargetVarName, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
      super((String)assignmentTargetVarName, model, "number", EXPECTED_TYPES, (Object[])tips, env);
   }

   static NonNumericalException newMalformedNumberException(Expression blamed, String text, Environment env) {
      return new NonNumericalException((new _ErrorDescriptionBuilder(new Object[]{"Can't convert this string to number: ", new _DelayedJQuote(text)})).blame(blamed), env);
   }
}
