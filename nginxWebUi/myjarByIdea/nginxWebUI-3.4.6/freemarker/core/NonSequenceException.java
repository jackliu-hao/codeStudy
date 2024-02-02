package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.CollectionUtils;

public class NonSequenceException extends UnexpectedTypeException {
   private static final Class[] EXPECTED_TYPES = new Class[]{TemplateSequenceModel.class};

   public NonSequenceException(Environment env) {
      super(env, "Expecting sequence value here");
   }

   public NonSequenceException(String description, Environment env) {
      super(env, description);
   }

   NonSequenceException(Environment env, _ErrorDescriptionBuilder description) {
      super(env, description);
   }

   NonSequenceException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
      this(blamed, model, CollectionUtils.EMPTY_OBJECT_ARRAY, env);
   }

   NonSequenceException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
      this(blamed, model, new Object[]{tip}, env);
   }

   NonSequenceException(Expression blamed, TemplateModel model, Object[] tips, Environment env) throws InvalidReferenceException {
      super(blamed, model, "sequence", EXPECTED_TYPES, tips, env);
   }
}
