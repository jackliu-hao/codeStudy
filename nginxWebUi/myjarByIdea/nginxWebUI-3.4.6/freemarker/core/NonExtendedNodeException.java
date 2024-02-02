package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModelEx;

public class NonExtendedNodeException extends UnexpectedTypeException {
   private static final Class<?>[] EXPECTED_TYPES = new Class[]{TemplateNodeModelEx.class};

   public NonExtendedNodeException(Environment env) {
      super(env, "Expecting extended node value here");
   }

   public NonExtendedNodeException(String description, Environment env) {
      super(env, description);
   }

   NonExtendedNodeException(Environment env, _ErrorDescriptionBuilder description) {
      super(env, description);
   }

   NonExtendedNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
      super(blamed, model, "extended node", EXPECTED_TYPES, env);
   }

   NonExtendedNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
      super(blamed, model, "extended node", EXPECTED_TYPES, tip, env);
   }

   NonExtendedNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
      super((Expression)blamed, model, "extended node", EXPECTED_TYPES, (Object[])tips, env);
   }
}
