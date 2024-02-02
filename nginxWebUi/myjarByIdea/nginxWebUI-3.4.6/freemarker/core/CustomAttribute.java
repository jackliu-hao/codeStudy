package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class CustomAttribute {
   public static final int SCOPE_ENVIRONMENT = 0;
   public static final int SCOPE_TEMPLATE = 1;
   public static final int SCOPE_CONFIGURATION = 2;
   private final Object key = new Object();
   private final int scope;

   public CustomAttribute(int scope) {
      if (scope != 0 && scope != 1 && scope != 2) {
         throw new IllegalArgumentException();
      } else {
         this.scope = scope;
      }
   }

   protected Object create() {
      return null;
   }

   public final Object get(Environment env) {
      return this.getScopeConfigurable(env).getCustomAttribute(this.key, this);
   }

   public final Object get() {
      return this.getScopeConfigurable(this.getRequiredCurrentEnvironment()).getCustomAttribute(this.key, this);
   }

   public final Object get(Template template) {
      if (this.scope != 1) {
         throw new UnsupportedOperationException("This is not a template-scope attribute");
      } else {
         return template.getCustomAttribute(this.key, this);
      }
   }

   public Object get(TemplateConfiguration templateConfiguration) {
      if (this.scope != 1) {
         throw new UnsupportedOperationException("This is not a template-scope attribute");
      } else {
         return templateConfiguration.getCustomAttribute(this.key, this);
      }
   }

   public final Object get(Configuration cfg) {
      if (this.scope != 2) {
         throw new UnsupportedOperationException("This is not a template-scope attribute");
      } else {
         return cfg.getCustomAttribute(this.key, this);
      }
   }

   public final void set(Object value, Environment env) {
      this.getScopeConfigurable(env).setCustomAttribute(this.key, value);
   }

   public final void set(Object value) {
      this.getScopeConfigurable(this.getRequiredCurrentEnvironment()).setCustomAttribute(this.key, value);
   }

   public final void set(Object value, Template template) {
      if (this.scope != 1) {
         throw new UnsupportedOperationException("This is not a template-scope attribute");
      } else {
         template.setCustomAttribute(this.key, value);
      }
   }

   public final void set(Object value, TemplateConfiguration templateConfiguration) {
      if (this.scope != 1) {
         throw new UnsupportedOperationException("This is not a template-scope attribute");
      } else {
         templateConfiguration.setCustomAttribute(this.key, value);
      }
   }

   public final void set(Object value, Configuration cfg) {
      if (this.scope != 2) {
         throw new UnsupportedOperationException("This is not a configuration-scope attribute");
      } else {
         cfg.setCustomAttribute(this.key, value);
      }
   }

   private Environment getRequiredCurrentEnvironment() {
      Environment c = Environment.getCurrentEnvironment();
      if (c == null) {
         throw new IllegalStateException("No current environment");
      } else {
         return c;
      }
   }

   private Configurable getScopeConfigurable(Environment env) throws Error {
      switch (this.scope) {
         case 0:
            return env;
         case 1:
            return env.getParent();
         case 2:
            return env.getParent().getParent();
         default:
            throw new BugException();
      }
   }
}
