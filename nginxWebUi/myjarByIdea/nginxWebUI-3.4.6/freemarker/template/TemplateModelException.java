package freemarker.template;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core._ErrorDescriptionBuilder;

public class TemplateModelException extends TemplateException {
   private final boolean replaceWithCause;

   public TemplateModelException() {
      this((String)null, (Exception)null);
   }

   public TemplateModelException(String description) {
      this(description, (Exception)null);
   }

   public TemplateModelException(Exception cause) {
      this((String)null, (Exception)cause);
   }

   public TemplateModelException(Throwable cause) {
      this((String)null, (Throwable)cause);
   }

   public TemplateModelException(String description, Exception cause) {
      this(description, (Throwable)cause);
   }

   public TemplateModelException(String description, Throwable cause) {
      this(description, false, cause);
   }

   public TemplateModelException(String description, boolean replaceWithCause, Throwable cause) {
      super(description, (Throwable)cause, (Environment)null);
      this.replaceWithCause = replaceWithCause;
   }

   protected TemplateModelException(Throwable cause, Environment env, String description, boolean preventAmbiguity) {
      super(description, cause, env);
      this.replaceWithCause = false;
   }

   protected TemplateModelException(Throwable cause, Environment env, _ErrorDescriptionBuilder descriptionBuilder, boolean preventAmbiguity) {
      super(cause, env, (Expression)null, descriptionBuilder);
      this.replaceWithCause = false;
   }

   public boolean getReplaceWithCause() {
      return this.replaceWithCause;
   }
}
