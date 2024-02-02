package freemarker.core;

import freemarker.template.TemplateException;

public class _MiscTemplateException extends TemplateException {
   public _MiscTemplateException(String description) {
      super((String)description, (Environment)null);
   }

   public _MiscTemplateException(Environment env, String description) {
      super(description, env);
   }

   public _MiscTemplateException(Throwable cause, String description) {
      this((Throwable)cause, (Environment)null, (String)description);
   }

   public _MiscTemplateException(Throwable cause, Environment env) {
      this(cause, env, (String)null);
   }

   public _MiscTemplateException(Throwable cause) {
      this((Throwable)cause, (Environment)null, (String)((String)null));
   }

   public _MiscTemplateException(Throwable cause, Environment env, String description) {
      super(description, cause, env);
   }

   public _MiscTemplateException(_ErrorDescriptionBuilder description) {
      this((Environment)null, (_ErrorDescriptionBuilder)description);
   }

   public _MiscTemplateException(Environment env, _ErrorDescriptionBuilder description) {
      this((Throwable)null, env, (_ErrorDescriptionBuilder)description);
   }

   public _MiscTemplateException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
      super(cause, env, (Expression)null, description);
   }

   public _MiscTemplateException(Object... descriptionParts) {
      this((Environment)null, descriptionParts);
   }

   public _MiscTemplateException(Environment env, Object... descriptionParts) {
      this((Throwable)null, env, descriptionParts);
   }

   public _MiscTemplateException(Throwable cause, Object... descriptionParts) {
      this((Throwable)cause, (Environment)null, (Object[])descriptionParts);
   }

   public _MiscTemplateException(Throwable cause, Environment env, Object... descriptionParts) {
      super(cause, env, (Expression)null, new _ErrorDescriptionBuilder(descriptionParts));
   }

   public _MiscTemplateException(Expression blamed, Object... descriptionParts) {
      this((Expression)blamed, (Environment)null, (Object[])descriptionParts);
   }

   public _MiscTemplateException(Expression blamed, Environment env, Object... descriptionParts) {
      this(blamed, (Throwable)null, env, (Object[])descriptionParts);
   }

   public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, Object... descriptionParts) {
      super(cause, env, blamed, (new _ErrorDescriptionBuilder(descriptionParts)).blame(blamed));
   }

   public _MiscTemplateException(Expression blamed, String description) {
      this((Expression)blamed, (Environment)null, (String)description);
   }

   public _MiscTemplateException(Expression blamed, Environment env, String description) {
      this(blamed, (Throwable)null, env, (String)description);
   }

   public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, String description) {
      super(cause, env, blamed, (new _ErrorDescriptionBuilder(description)).blame(blamed));
   }
}
